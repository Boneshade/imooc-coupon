package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xubr
 * @title: SettlementInfo
 * @projectName imooc-coupon
 * @description: <h1>结算信息对象定义<h1/>
 * 包含:
 * 1.userId
 * 2.商品信息
 * 3.优惠卷列表
 * 4.结算结果金额
 * @date 2020/12/3110:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementInfo {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商品信息
     */
    private List<GoodsInfo> goodsInfos;

    /**
     * 优惠卷列表
     */
    private List<CouponAndTemplateInfo> couponAndTemplateInfos;

    /**
     * 是否使结算生效,即核销(就是使用了优惠卷)
     */
    private Boolean employ;

    /**
     * 结果结算金额
     */
    private Double cost;


    /**
     * 优惠卷和模板信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CouponAndTemplateInfo {
        /**
         * coupon 的主键
         */
        private Integer id;
        /**
         * 优惠卷对应的模板对象
         */
        private CouponTemplateSDK template;

    }


}
