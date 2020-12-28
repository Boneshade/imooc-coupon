package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>用户优惠券的状态<h1/>
 *
 * @author xubr 2020/12/28
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("all")
public enum CouponStatus {
    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期的(未被使用的)", 3);

    /**
     * 优惠券状态描述信息
     **/
    private String description;
    /**
     * 优惠券状态编码
     **/
    private Integer code;


    /**
     * <h2>根据code 获取到CouponStatus<h2/>
     **/
    public static CouponStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> bean.code.equals(code))
                .findAny().orElseThrow(() -> new IllegalArgumentException(code + "not exists"));
    }
}