package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xubr
 * @title: RoleEnum
 * @projectName imooc-coupon
 * @description: <h1>角色枚举</h1>
 * @date 2021/3/915:26
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN("管理员"),
    SUPER_ADMIN("超级管理员"),
    CUSTOMER("普通用户");

    private String roleName;
}
