package com.imooc.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @author xubr 2020/12/6
 * @Description: 在Zuul 按照规则路由到下级服务之前执行,如果需要请求进行预处理,比如鉴权,限流等,都应该考虑在此类Filter实现
 */
public abstract class AbstractPreZuulFilter extends AbstractZuulFilter{


    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

}
