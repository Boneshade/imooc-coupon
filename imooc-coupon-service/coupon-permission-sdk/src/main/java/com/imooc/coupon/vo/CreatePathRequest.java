package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xubr
 * @title: CreatePathRequest
 * @projectName imooc-coupon
 * @description: <h1>路径创建请求对象定义</h1>
 * @date 2021/3/916:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePathRequest {

    private List<PathInfo> pathInfos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PathInfo {

        /** 路径模式 */
        private String pathPattern;

        /** HTTP 方法类型 */
        private String httpMethod;

        /** 路径名称 */
        private String pathName;

        /** 服务名称 */
        private String serviceName;

        /** 操作模式: READ, WRITE */
        private String opMode;

    }


}
