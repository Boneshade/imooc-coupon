package com.imooc.coupon.advice;

import com.imooc.coupon.annotation.IgnoreResponseAdvice;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * <h1>统一响应<h1/>
 *
 * @author xubr 2020/12/14
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     * <h2>判断是否需要对响应进行处理<h2/>
     *
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {


        //如果当前方法所在的类标识了 @IgnoreResponseAdvice 注解,则不需要处理
        if (methodParameter.getDeclaringClass().isAnnotationPresent(
                IgnoreResponseAdvice.class
        )) {
            return false;
        }

        //如果当前方法所在的类标识了 @IgnoreResponseAdvice 注解,则不需要处理
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        //对响应进行处理,执行beforeBodyWrite方法
        return true;
    }

    /**
     * 在body写入响应流之前进行处理
     * 也就是响应返回之前的处理
     * @param o
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType
            , Class<? extends HttpMessageConverter<?>> aClass
            , ServerHttpRequest serverHttpRequest
            , ServerHttpResponse serverHttpResponse) {

        //定义最终的返回对象
        CommonResponse<Object> response = new CommonResponse<>(0, "");

        //如果o是null,response 不需要设置 data
        if (null == o) {
            return response;
        } else if (o instanceof CommonResponse) {
            //如果o 是CommonResponse,不需要再次处理
            response = (CommonResponse<Object>) o;
            //否则,把响应对象作为 是CommonResponse 的data部分
        } else {
            response.setData(o);
        }
        return response;
    }
}
