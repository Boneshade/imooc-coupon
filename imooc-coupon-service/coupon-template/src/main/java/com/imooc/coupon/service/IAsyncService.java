package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;

/**
 * <h1>异步服务接口定义<h1/>
 *
 * @author xubr 2020/12/20
 */
public interface IAsyncService {

    /***
     * 根据模板异步的创建优惠券码
     * @param template {@link CouponTemplate}优惠券模板实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);

}
