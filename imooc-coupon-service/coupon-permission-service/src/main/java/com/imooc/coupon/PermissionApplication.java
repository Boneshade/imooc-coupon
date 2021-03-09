package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author xubr
 * @title: PermissionApplication
 * @projectName imooc-coupon
 * @description: <h1>权限启动服务</h1>
 * @date 2021/3/914:50
 */
@EnableEurekaClient
@SpringBootApplication
public class PermissionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PermissionApplication.class, args);
    }
}
