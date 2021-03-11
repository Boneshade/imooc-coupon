package com.imooc.coupon.vo;

import lombok.Data;

/**
 * @author xubr
 * @title: PermissionInfo
 * @projectName imooc-coupon
 * @description: <h1>接口权限信息组装类定义</h1>
 * @date 2021/3/1114:38
 */
@Data
public class PermissionInfo {

    /** controller 的URL**/
    private String url;

    /** 方法类型 **/
    private String method;

    /** 方法只读**/
    private Boolean isRead;

    /**方法描述**/
    private String description;

    /** 扩展属性 */
    private String extra;


    @Override
    public String toString() {

        return "url = " + url
                + ", method = " + method
                + ", isRead = " + isRead
                + ", description = " + description;
    }

}
