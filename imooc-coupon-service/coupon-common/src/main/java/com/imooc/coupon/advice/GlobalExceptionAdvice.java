package com.imooc.coupon.advice;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>全局异常处理<h1/>
 *
 * @author xubr 2020/12/14
 */
@RestController
public class GlobalExceptionAdvice {

    /**
     * <h2>对 couponException 进行统一处理<h2/>
     *
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest req, CouponException ex) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business  error");
        response.setData(ex.getMessage());
        return response;

    }


}
