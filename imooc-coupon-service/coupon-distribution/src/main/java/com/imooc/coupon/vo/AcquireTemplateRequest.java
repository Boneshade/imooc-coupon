package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xubr
 * @title: AcquireTemplateRequest
 * @projectName imooc-coupon
 * @description: <h1>获取优惠卷请求对象定义<h1/>
 * @date 2020/12/3110:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquireTemplateRequest {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 优惠卷模板信息
     */
    private CouponTemplateSDK templateSDK;




}
