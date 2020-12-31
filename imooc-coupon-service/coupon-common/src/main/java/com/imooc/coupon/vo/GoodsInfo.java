package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xubr
 * @title: GoodsInfo
 * @projectName imooc-coupon
 * @description: <h1>fake 商品信息<h1/>
 * @date 2020/12/3110:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfo {

    /**
     * 商品类型(GoodsType对象)
     */
    private Integer type;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品数量
     */
    private Integer count;

    //TODO 名称,使用信息

}
