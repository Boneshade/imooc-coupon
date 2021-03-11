package com.imooc.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xubr
 * @title: IgnorePermission
 * @projectName imooc-coupon
 * @description: <h1>权限忽略注解: 忽略当前标识的 Controller 接口,不注册权限</h1>
 * @date 2021/3/109:44
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnorePermission {
}
