package com.imooc.coupon.annotation;

/**
 * @author xubr
 * @title: ImoocCouponPermission
 * @projectName imooc-coupon
 * @description: <h1></h1>
 * @date 2021/3/109:46
 */
public @interface ImoocCouponPermission {


    /**
     * <h2>接口描述信息<h2/>
     * @return
     */
    String description() default "";

    /**
     * <h2>此接口是否只读,默认是 true<h2/>
     * @return
     */
    boolean readOnly() default true;

    /**
     * <h2>扩展属性<h2/>
     * 最好以JSON 格式存取
     * @return
     */
    String extra() default "";


}
