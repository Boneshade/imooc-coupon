package com.imooc.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xubr
 * @title: OpModeEnum
 * @projectName imooc-coupon
 * @description: <h1>操作模式的枚举定义</h1>
 * @date 2021/3/914:45
 */
@Getter
@AllArgsConstructor
public enum OpModeEnum {

    READ("读"),
    WRITE("写");

    private String mode;


}
