package com.imooc.coupon.service.Impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xubr
 * @title: RedisServiceImpl
 * @projectName imooc-coupon
 * @description: <h1>redis相关的服务接口实现<h1/>
 * @date 2020/12/3114:07
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class RedisServiceImpl implements IRedisService {

    /**
     * redis客户端
     */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * @param userId
     * @param status
     * @return
     */
    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {

        log.info("Get Coupons From Cache:{},{}", userId, status);
        String redisKey = status2RedisKey(status, userId);

        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey).stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(couponStrs)) {
            saveEmptyCouponListToCache(userId,
                    Collections.singletonList(status));
            return Collections.emptyList();
        }

        return couponStrs.stream()
                .map(cs -> JSON.parseObject(cs, Coupon.class)).collect(Collectors.toList());
    }

    /**
     * <h2>避免缓存穿透<h2/>
     * 保存无效的优惠卷近redis
     *
     * @param userId 用户id
     * @param status 优惠券状态列表
     */
    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {

        log.info("Save Empty List To Cache For User:{},Status:{}",
                userId, JSON.toJSONString(status));

        //key是 coupon_id,value是序列化的Coupon
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        //用户优惠卷缓存信息
        //kv
        //k:status->rediskey
        //v:{coupon_id: 序列化的 Coupon}
        //使用 SessionCallback 把数据命令放入 Redis 的pipeline
        //pipeline不仅减少了,它允许客户端可以一次发送多条命令 ,同时也减少了IO调用次数
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    operations.opsForHash().putAll(redisKey, invalidCouponMap);
                });

                return null;
            }
        };

        log.info("Pipeline Exe Result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));


    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {

        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, templateId);
        //因为优惠卷码不存在顺序关系,左边 pop右边pop,没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon Code:{},{},{}", templateId, redisKey, couponCode);
        return couponCode;
    }

    /**
     * 将用户的优惠卷保存到缓存中
     *
     * @param userId
     * @param coupons {@link Coupon}s
     * @param status  优惠券状态
     * @return
     * @throws CouponException
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon to Cache:{},{},{}", userId, JSON.toJSONString(coupons), status);

        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);

                break;
            case USED:

                result = addCouponToCacheForUsed(userId, coupons);
                break;

            case EXPIRED:
                break;
        }

        return result;
    }

    /**
     * 新增加优惠卷到cache中
     *
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {

        //如果 status 是usable,代表是新增加的优惠卷
        //只会影响一个cache：USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable");
        Map<String, String> needCachedObject = new HashMap<>();
        coupons.forEach(c -> needCachedObject.put(
                c.getId().toString(),
                JSON.toJSONString(c)));

        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} Coupons To Cache:{},{}", needCachedObject.size(), userId, redisKey);

        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

        return needCachedObject.size();
    }


    /**
     * <h2>将已使用的优惠卷加入到cache中<h2/>
     *
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {

        //如果是status 是used，代表用户操作是使用当前的优惠卷,影响到两个Cache
        //usebale,used
        log.debug("Add {} Coupons To Cache For Used,");

        Map<String, String> needCachedForUsed = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyForUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);

        List<Coupon> curUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());

        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> needCachedForUsed.put(c.getId().toString(), JSON.toJSONString(c)));


        //校验当前的优惠卷参数是否与Cached中匹配

        List<Integer> curUsableIds = curUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());

        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());

        //如果paramIds不是curUsableIds的子集
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons Is Not Equal ToCache:", userId, JSON.toJSONString(curUsableCoupons), JSON.toJSONString(paramIds));

            throw new CouponException("CurCoupons Is Not Equal To Cache !");
        }


        List<String> needCleanKey = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                //1.已使用的优惠卷 Cache缓存添加
                redisOperations.opsForHash().putAll(redisKeyForUsed, needCachedForUsed);
                //2.可使用的优惠卷Cache需要清理
                redisOperations.opsForHash().delete(redisKeyForUsable, needCleanKey.toArray());

                //3.重置过期时间
                redisOperations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

                redisOperations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

                return null;
            }
        };

        log.info("Pipeline Exe Result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }


    /**
     * <h2>根据status 获取到对应的RedisKey<h2/>
     *
     * @param status
     * @param userId
     * @return
     */
    private String status2RedisKey(Integer status, Long userId) {

        String redisKey = null;

        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;

            case USED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
        }
        return redisKey;
    }

    /**
     * <h2>获取一个随机的过期时间<h2/>
     * 缓存雪崩:key 在同一时间失效
     *
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回[min, max] 之间的随机秒数
     */
    private Long getRandomExpirationTime(Integer min, Integer max) {

        return RandomUtils.nextLong(
                min * 60 * 60,
                max * 60 * 60
        );


    }

}
