package com.imooc.coupon.filter;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.permission.PermissionClient;
import com.imooc.coupon.vo.CheckPermissionRequest;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xubr
 * @title: PremissionFilter
 * @projectName imooc-coupon
 * @description: <h1>权限过滤器实现</h1>
 * @date 2021/3/1210:59
 */
public class PermissionFilter extends AbsSecurityFilter {


    private final PermissionClient permissionClient;

    @Autowired
    public PermissionFilter(PermissionClient permissionClient) {
        this.permissionClient = permissionClient;
    }


    @Override
    protected Boolean interceptCheck(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {

        Long userId = Long.valueOf(request.getHeader("userId"));
        String uri = request.getRequestURI().substring("/imooc".length());
        String httpMethod = request.getMethod();

        return permissionClient.checkPermission(
                new CheckPermissionRequest(userId, uri, httpMethod)
        );
    }

    @Override
    protected int getHttpStatus() {
        return HttpStatus.OK.value();
    }

    @Override
    protected String getErrorMsg() {
        CommonResponse<Object> response = new CommonResponse<>();
        response.setCode(-2);
        response.setMessage("您没有操作权限");

        return JSON.toJSONString(response);
    }
}
