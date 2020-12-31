package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;

import java.util.List;

/**
 * <h1>Redis相关的操作服务接口定义<h1/>
 * 1.用户的三类状态优惠券Cache 相关操作
 * 2.优惠券模板生成的优惠券码Cache操作
 *
 * @author xubr 2020/12/30
 */
public interface IRedisService {

    /**
     * <h2>根据userId 和状态找到缓存的优惠券列表数据<h2/>
     *
     * @param userId
     * @param status
     * @return {@link Coupon}s,注意,可能会为null,代表从没有记录
     */
    List<Coupon> getCachedCoupons(Long userId, Integer status);


    /**
     * <h2>保存空的优惠券列表到缓存中<h2/>
     * 防止缓存穿透
     * @param userId 用户id
     * @param status 优惠券状态列表
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);


    /**
     * <h2>尝试从Cache中获取优惠券码<h2/>
     * @param templateId
     * @return 优惠券码
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * <h2>将优惠券保存到Cache中<h2/>
     * @param userId
     * @param coupons {@link Coupon}s
     * @param status 优惠券状态
     * @return 保存成功的个数
     * @throws CouponException
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException;
}
