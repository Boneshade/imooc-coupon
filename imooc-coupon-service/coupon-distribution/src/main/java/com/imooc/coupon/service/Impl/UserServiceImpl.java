package com.imooc.coupon.service.Impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.feign.TemplateClient;
import com.imooc.coupon.service.IRedisService;
import com.imooc.coupon.service.IUserService;
import com.imooc.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1>用户服务相关的接口实现<h1/>
 * 所有的操作过程,状态都保存在Redis中,并通过kafka 把消息传递到Mysql中
 * 为什么使用kafka,而不是直接使用Springboot 中的异步处理？
 * 安全性:异步任务可能会失败,kafka可以回溯消息,
 * @author xubr 2021/1/5
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    /**
     * Coupon Dao
     */
    private final CouponDao couponDao;

    /**
     * Redis
     */
    private final IRedisService redisService;

    /**
     * 模板微服务客户端
     */
    private final TemplateClient templateClient;

    /**
     * 结算微服务客户端
     */
    private final SettlementClient settlementClient;

    /**
     * kafka客户端
     */
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    public UserServiceImpl(CouponDao couponDao, IRedisService redisService,
                           TemplateClient templateClient,
                           SettlementClient settlementClient,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * <h2>根据用户id 和状态查询优惠券记录<h2/>
     * @param userId 用户id
     * @param status 优惠卷状态
     * @return
     * @throws CouponException
     */
    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {

        //此时已经有一张无效的优惠券信息,不必担心缓存穿透问题
        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;

        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("coupon cache is not empty: {},{}", userId, status);
            preTarget = curCached;
        } else {
            log.debug("coupon cache is empty,get coupon from db: {},{}", userId, status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));
            //如果数据库中没有记录,直接返回就可以,Cache中已经加入一张无效的优惠券
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not have coupon:{},{}", userId, status);
                return dbCoupons;
            }

            //填充dbCoupons 的TemplateSDK 字段
            Map<Integer, CouponTemplateSDK> id2TemplateSDK =
                    templateClient.findIds2TemplateSDK(dbCoupons.stream()
                            .map(Coupon::getTemplateId).collect(Collectors.toList())).getData();
            dbCoupons.forEach(dc -> dc.setTemplateSDK(
                    id2TemplateSDK.get(dc.getTemplateId())
            ));

            //数据库中存在记录
            preTarget = dbCoupons;
            //将记录写入Cache
            redisService.addCouponToCache(userId, preTarget, status);
        }

        //将无效优惠卷剔除
        preTarget = preTarget.stream().filter(c -> c.getId() != -1)
                .collect(Collectors.toList());

        //如果当前获取的是可用优惠卷,还需要做对已过期优惠卷的延迟处理
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify couponClassify = CouponClassify.classify(preTarget);
            if (CollectionUtils.isNotEmpty(couponClassify.getExpired())) {
                //如果已过期状态不为空,需要做延迟处理
                log.info("Add Expired Coupons To Cache From FindCouponByStatus: {},{}", userId, status);

                redisService.addCouponToCache(
                        userId, couponClassify.getExpired(),
                        CouponStatus.EXPIRED.getCode()
                );

                //发送到 Kafka 中做异步处理
                kafkaTemplate.send(
                        Constant.TOPIC,
                        JSON.toJSONString(new CouponKafkaMessage(
                                CouponStatus.EXPIRED.getCode(),
                                couponClassify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList()
                                ))
                        ));
            }

            return couponClassify.getUsable();
        }
        //如果不是可用状态则直接返回优惠卷即可
        return preTarget;
    }

    /**
     * <h2>根据用户的id查找当前可以领取的优惠券模板<h2/>
     * @param userId
     * @return
     * @throws CouponException
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {

        long curTime = System.currentTimeMillis();
        List<CouponTemplateSDK> templateSDKS =
                templateClient.findAllUsableTemplate().getData();

        log.debug("Find All Template(From TemplateClient) Count :{}",
                templateSDKS.size());

        //过滤过期的优惠卷模板
        templateSDKS = templateSDKS.stream().filter(
                t -> t.getRule().getExpiration().getDeadline() > curTime).collect(Collectors.toList());

        log.info("Find Usable Template Count:{}", templateSDKS.size());

        //key 是TemplateId
        //value 中的 left  是Template limitation,right 是优惠卷模板
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template =
                new HashMap<>(templateSDKS.size());

        templateSDKS.forEach(t -> limit2Template.put(
                t.getId(),
                Pair.of(t.getRule().getLimitation(), t)
        ));

        List<CouponTemplateSDK> result =
                new ArrayList<>(limit2Template.size());

        //查找当前用户可用的优惠券模板
        List<Coupon> userUsableCoupons = findCouponsByStatus(userId, CouponStatus.USABLE.getCode());

        log.debug("Current User Has Usable Coupons:{},{}", userId, userUsableCoupons.size());

        //key 是TemplateId
        Map<Integer, List<Coupon>> templateIdsCoupons = userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        //根据Template 的Rule 判断是否可以领取优惠券模板
        limit2Template.forEach((k, v) -> {

            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();
            if (templateIdsCoupons.containsKey(k)
                    && templateIdsCoupons.get(k).size() >= limitation) {
                return;
            }

            result.add(templateSDK);

        });
        return result;
    }

    /**
     * <h2>用户领取优惠券模板<h2/>
     * 1.从TemplateClient 拿到对应的优惠券,并检查是否过期
     * 2.save to db
     * 3.填充 CouponTemplateSDK
     * 5.save to Cache
     * @param request {@link AcquireTemplateRequest}
     * @return
     * @throws CouponException
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {

        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(Collections.singletonList(
                request.getTemplateSDK().getId()
        )).getData();


        //优惠券模板是需要存在的
        if (id2Template.size() <= 0) {
            log.error("Can Not Acquire Template From TemplateClient:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Template From TemplateClient");
        }

        //用户是否可以领取这张优惠券
        List<Coupon> userUsableCoupon = findCouponsByStatus(
                request.getUserId(), CouponStatus.USABLE.getCode()
        );

        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupon.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));

        if (templateId2Coupons.containsKey(request.getTemplateSDK().getId()) &&
                templateId2Coupons.get(request.getTemplateSDK().getId()).size() >=
                        request.getTemplateSDK().getRule().getLimitation()) {
            log.error("Exceed Template Assign Limitation: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign limitation");
        }


        //尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(
                request.getTemplateSDK().getId()
        );

        if (StringUtils.isEmpty(couponCode)) {
            log.error("Can Not Acquire Coupon Code:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Coupon Code");
        }

        Coupon newCoupon = new Coupon(
                request.getTemplateSDK().getId(),
                request.getUserId(),
                couponCode,
                CouponStatus.USABLE
        );

        newCoupon = couponDao.save(newCoupon);


        //填充 Coupon 对象的 CouponTemplateSDK，一定要放在缓存之前去填充
        newCoupon.setTemplateSDK(request.getTemplateSDK());

        //放入缓存
        redisService.addCouponToCache(
                request.getUserId(),
                Collections.singletonList(newCoupon),
                CouponStatus.USABLE.getCode()
        );

        return newCoupon;
    }

    /**
     * <h2>结算(核销)优惠券<h2/>
     * 这里需要注意,规则相关处理需要由 Settlement 系统去做,当前系统仅仅做业务处理过程
     * @param info {@link SettlementInfo}
     * @return
     * @throws CouponException
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {

        // 当没有传递优惠券时,直接返回商品总价
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos =
                info.getCouponAndTemplateInfos();

        if (CollectionUtils.isEmpty(ctInfos)) {

            log.info("Empty Coupons For Settle");
            double goodsSum = 0.0;

            for (GoodsInfo gi : info.getGoodsInfos()) {
                goodsSum += gi.getPrice() * gi.getCount();
            }

            //没有优惠券也就不存在优惠券的核销,SettlementInfo 其他的字段不需要修改

            info.setCost(retain2Decimal(goodsSum));
        }

        //校验传递的优惠券是否是用户自己的
        List<Coupon> coupons = findCouponsByStatus(
                info.getUserId(), CouponStatus.USABLE.getCode()
        );

        Map<Integer, Coupon> id2Coupon = coupons.stream()
                .collect(Collectors.toMap(
                        Coupon::getId,
                        Function.identity()
                ));
        if (MapUtils.isEmpty(id2Coupon) || !CollectionUtils.isSubCollection(
                ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                        .collect(Collectors.toList()), id2Coupon.keySet()
        )) {
            log.info("{}", id2Coupon.keySet());
            log.error("User Coupon Has Some Problem,It Is Not SubCollection" +
                    "Of Coupons!");
            throw new CouponException("User Coupon Has Some Problem," + "It is Not SubCollection of Coupons!");
        }
        log.debug("Current Settlement Coupos Is User's:{}", ctInfos.size());

        List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());

        ctInfos.forEach(ci -> settleCoupons.add(id2Coupon.get(ci.getId())));

        //通过结算服务获取结算信息
        SettlementInfo processedInfo = settlementClient.computeRule(info).getData();

        if (processedInfo.getEmploy() &&
                CollectionUtils.isNotEmpty(processedInfo.getCouponAndTemplateInfos())) {

            log.info("Settle User Coupon:{},{}", info.getUserId(),
                    JSON.toJSONString(settleCoupons));

            redisService.addCouponToCache(
                    info.getUserId(),
                    settleCoupons,
                    CouponStatus.USED.getCode()
            );
            //更新 db
            kafkaTemplate.send(Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                            CouponStatus.USED.getCode(),
                            settleCoupons.stream().map(Coupon::getId)
                                    .collect(Collectors.toList())
                    )));

        }

        return processedInfo;
    }


    /**
     * <h2>保留2位小数<h2/>
     * @param value
     * @return
     */
    private double retain2Decimal(double value) {
        //BigDecimal.ROUND_HALF_UP 代表四舍五入
        return new BigDecimal(value).
                setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
