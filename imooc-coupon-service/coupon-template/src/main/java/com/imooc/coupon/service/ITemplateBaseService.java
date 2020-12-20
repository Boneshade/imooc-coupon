package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板信息(view,delete...)服务定义<h1/>
 *
 * @author xubr 2020/12/20
 */
@SuppressWarnings("all")
public interface ITemplateBaseService {


    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     *
     * @return {@link CouponTemplate} 模板实体
     * @Param id 模板id
     **/
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * <h2>查找所有可用的优惠券模板<h2/>
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /****
     * <h2>获取ids 到couponTemplateSDK 的映射<h2/>
     * @param ids
     * @return Map<key:模板 id, value:couponTemplateSDK></>
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);


}
