package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>优惠券分类<h1/>
 *
 * @author xubr 2020/12/15
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("all")
public enum CouponCategory {

    MANJIAN("满减卷", "001"),
    ZHEKOU("折扣卷", "002"),
    LIJIAN("立减劵", "003");

    /**
     * 优惠券分类描述信息
     **/
    private String description;

    /**
     * 优惠券分类编码
     **/
    private String code;

    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exist!"));
    }


}
