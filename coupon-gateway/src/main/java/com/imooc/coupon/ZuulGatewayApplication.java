package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * <h1>网关应用启动入口<h1/>
 * EnableZuulProxy 标识当前的应用是 zuul Server
 * SpringCloudApplication 组合了Springboot 应用 +服务发现 +熔断
 *
 * @author xubr
 */
@EnableZuulProxy
@SpringCloudApplication
public class ZuulGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }

}
