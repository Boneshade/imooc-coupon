package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>微服务之间用的优惠券模板信息定义<h1/>
 *
 * @author xubr 2020/12/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplateSDK {

    /**
     * 优惠券模板主键
     **/
    private Integer id;

    /**
     * 优惠券模板名称
     **/
    private String name;

    /**
     * 优惠券logo
     **/
    private String logo;

    /**
     * 优惠券描述
     **/
    private String desc;

    /**
     * 产品线
     **/
    private Integer productLine;

    /**
     * 优惠券模板的编码
     **/
    private String key;

    /****
     * 目标用户
     */
    private Integer target;

    /**
     * 优惠券规则
     **/
    private TemplateRule rule;


    public CouponTemplateSDK(Integer id, String name, String logo, String desc, String code, Integer code1, String key, Integer code2, TemplateRule rule) {
    }
}
