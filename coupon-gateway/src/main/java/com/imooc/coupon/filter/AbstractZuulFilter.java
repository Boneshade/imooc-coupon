package com.imooc.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;


/**
 * @author 26586
 */
public abstract class AbstractZuulFilter extends ZuulFilter {

    /**
     * 用于在过滤器之间传递消息,数据保存在每个请求的ThreadLocal中
     * 扩展了Map,后台传参一直是一个比较重要的地方,如果涉及Web,我们可以用RequestContext来帮我们传递参数
     **/
    RequestContext context;

    private final static String NEXT = "next";


    /**
     * a "true" return from this method means that the run() method should be invoked
     *
     * @return true if the run() method should be invoked. false will not invoke the run() method
     */
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (boolean) ctx.getOrDefault(NEXT, true);
    }

    /**
     * if shouldFilter() is true, this method will be invoked. this method is the core method of a ZuulFilter
     *
     * @return Some arbitrary artifact may be returned. Current implementation ignores it.
     * @throws ZuulException if an error occurs during execution.
     */
    @Override
    public Object run() throws ZuulException {

        context = RequestContext.getCurrentContext();
        return cRun();
    }


    /**
     * 有抽象方法的类一定是抽象类,但是抽象类不一定有抽象方法
     * 抽象方法不能被private修饰,因为抽象方法必须被子类重写,而private权限对于子类来说是不能访问的,所以会产生矛盾.
     * 抽象方法不能用static修饰,如果用static修饰了,那么我们就可以直接通过类名调用了,而抽象方法压根没有主体,没有任何业务逻辑,这样就毫无意义了
     *
     * @return
     */
    protected abstract Object cRun();

    Object fail(int code, String msg) {
        context.set(NEXT, false);
        context.setSendZuulResponse(false);
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);
        context.setResponseBody(String.format("{\"result\":\"%s!\"}", msg));
        return null;
    }

    Object success() {
        context.set(NEXT, true);
        return null;
    }


}
