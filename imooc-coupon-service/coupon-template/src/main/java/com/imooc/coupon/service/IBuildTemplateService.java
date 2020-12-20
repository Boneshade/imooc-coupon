package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.TemplateRequest;

/**
 * <构建优惠券模板接口定义></>
 *
 * @author xubr 2020/12/20
 */
public interface IBuildTemplateService {

    /**
     * <h2>创建优惠券模板</h2>
     *
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     * @throws CouponException
     */
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;


}
