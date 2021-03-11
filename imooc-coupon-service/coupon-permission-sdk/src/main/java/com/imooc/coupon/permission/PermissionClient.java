package com.imooc.coupon.permission;

import com.imooc.coupon.vo.CheckPermissionRequest;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.CreatePathRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author xubr
 * @title: PermissionClient
 * @projectName imooc-coupon
 * @description: <h1>路径创建于权限校验功能 Feign 接口实现</h1>
 * @date 2021/3/109:39
 */
@FeignClient("eureka-client-coupon-permission")
public interface PermissionClient {

    @RequestMapping(value = "/coupon-permission/create/path",method = RequestMethod.POST)
    CommonResponse<List<Integer>> createPath(@RequestBody CreatePathRequest request);

    @RequestMapping(value = "/coupon-permission/check/permission",
            method = RequestMethod.POST)
    Boolean checkPermission(@RequestBody CheckPermissionRequest request);

}
