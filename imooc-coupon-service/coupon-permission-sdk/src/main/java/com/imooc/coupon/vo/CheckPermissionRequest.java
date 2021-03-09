package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xubr
 * @title: CheckServicePermission
 * @projectName imooc-coupon
 * @description: <h1>权限校验请求定义</h1>
 * @date 2021/3/917:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPermissionRequest {

    private Long userId;

    private String uri;

    private String httpMethod;

}
