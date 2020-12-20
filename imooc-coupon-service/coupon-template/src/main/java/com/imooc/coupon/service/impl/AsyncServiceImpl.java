package com.imooc.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xubr 2020/12/20
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    /**
     * CouponTemplate Dao
     **/
    private final CouponTemplateDao templateDao;

    /**
     * 注入 Redis 模板类
     **/
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    /***
     * 根据模板异步的创建优惠券码
     * @param template {@link CouponTemplate}优惠券模板实体
     */
    @Async("getAsyncExecutor")
    @Override
    @SuppressWarnings("all")
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(template);

        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push CouponCode To Redis:{}", redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));

        template.setAvailable(true);
        templateDao.save(template);
        watch.stop();
        log.info("Construct Coupon By Template Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));

        //TODO 发送短信或者邮件通知优惠券模板已经可用
        log.info("CouponTemplate({}) IS Available!", template.getId());

    }

    /**
     * 构造优惠券码
     * 优惠券码(对应于每一张优惠券,18位)
     * 前4位:产品线+类型
     * 中间六位:日期随机(190101)
     *
     * @param template {@link CouponTemplate} 实体类
     * @return Set<String> 与template.count 相同个数的优惠券码
     */
    @SuppressWarnings("all")
    private Set<String> buildCouponCode(CouponTemplate template) {

        //监视生成时间
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> result = new HashSet<>(template.getCount());

        //前4位getProductLine1位+getCategory3位
        String prefix4 = template.getProductLine().getCode().toString() +
                template.getCategory().getCode();

        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());

        for (int i = 0; i < template.getCount(); i++) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        //while的效率比for低下
        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        assert result.size() == template.getCount();
        log.info("Build Coupon Code Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));

        return result;

    }

    /****
     * <h2>构造优惠券码的后14位<h2/>
     * @param date
     * @return
     */
    private String buildCouponCodeSuffix14(String date) {

        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};


        //中间6位
        List<Character> chars = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        //Collectors.joining()将字符串重新组合成6位
        String mid6 = chars.stream().map(Objects::toString).collect(Collectors.joining());

        //后8位
        String suffix8 = RandomStringUtils.random(1, bases) + RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;


    }


}
