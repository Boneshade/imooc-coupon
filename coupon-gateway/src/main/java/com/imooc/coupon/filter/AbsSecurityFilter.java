package com.imooc.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xubr
 * @title: AbsSecurityFilter
 * @projectName imooc-coupon
 * @description: <h1>抽象权限过滤器,可以有多种实现</h1>
 * @date 2021/3/1210:18
 */
@Slf4j
public abstract class AbsSecurityFilter extends ZuulFilter {

    /**
     * 在Zuul按照规则路由到下级服务之前执行。如果需要对请求进行预处理,比如鉴权,限流等,都应该考虑在此类Filter实现
     * 也就是前置过滤器
     * @return
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //优先级,数字越大,优先级越低
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        //是否执行该过滤器,true代表需要过滤

        //获取当前的上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取上下文中的响应
        HttpServletResponse response = context.getResponse();

        //如果前一个filter 执行失败,不会调用后面的filter
        return response.getStatus() == 0
                || response.getStatus() == HttpStatus.SC_OK;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();


        log.info("filter {} begin check request {}.", this.getClass().getSimpleName(),
                request.getRequestURI());


        Boolean result = null;

        try {
            result = interceptCheck(request, response);
        } catch (Exception ex) {
            log.error("filter {} check request {}, throws exception {}.",
                    this.getClass().getSimpleName(),
                    request.getRequestURI(), ex.getMessage());
        }


        log.debug("filter {} finish check,result is null.",
                this.getClass().getSimpleName());


        if (result == null) {
            log.debug("filter {} finish check,result is null.",
                    this.getClass().getSimpleName());

            //对当前的请求不进行路由
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(getHttpStatus());
            return null;
        }

        if (!result) {

            try {
                //对当前的请求不进行路由
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(getHttpStatus());
                response.setHeader("Content-type",
                        "application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(getErrorMsg());
                context.setResponse(response);
            } catch (IOException e) {
                log.error("filter {} check request {}, result is false," +
                                "setResponse throws Exception {}",
                        this.getClass().getSimpleName(),
                        request.getRequestURI(), e.getMessage());
            }
        }
        return null;
    }

    /**
     * <h2>子Filter 实现该方法,填充校验逻辑<h2/>
     * @param request
     * @param response
     * @return true:通过校验；false:校验未通过
     * @throws Exception
     */
    protected abstract Boolean interceptCheck(HttpServletRequest request,
                                              HttpServletResponse response) throws Exception;

    /**
     * <h2>获得http响应状态<h2/>
     * @return
     */
    protected abstract int getHttpStatus();

    /**
     * <h2>获得错误消息<h2/>
     * @return
     */
    protected abstract String getErrorMsg();

}
