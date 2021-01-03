package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>优惠券kafka消息对象定义<h1/>
 * @author xubr 2021/1/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponKafkaMessage {

    /**优惠券状态*/
    private Integer status;

    /**coupon主键**/
    private List<Integer> ids;




}
