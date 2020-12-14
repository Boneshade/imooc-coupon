package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xubr 2020/12/6
 */
@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {

    @Override
    protected Object cRun() {

        HttpServletRequest request = context.getRequest();
        //从PreRequestFilter 中获取设置的请求时间戳

        Long startTime = (Long) context.get("startTime");
        String uri = request.getRequestURI();
        long duration = System.currentTimeMillis() - startTime;

        //从网关通过的请求都会打印日志记录,uri+duration
        log.info("uri: {},duration {}", uri, duration);

        return success();
    }

    /**
     * 这个是需要等待所有的都相应完成,才走的方法
     * @return
     */
    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}
