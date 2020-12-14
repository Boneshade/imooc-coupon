package com.imooc.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @author xubr 2020/12/6
 */
public abstract class AbstractPreZuulFilter extends AbstractZuulFilter{


    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

}
