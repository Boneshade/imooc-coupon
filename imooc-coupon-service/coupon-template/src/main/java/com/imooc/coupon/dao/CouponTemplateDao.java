package com.imooc.coupon.dao;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author xubr 2020/12/20
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {


    /***
     * <h2>根据模板名称查询模板<h2/>
     * where name=...
     * @param name
     * @return
     */
    CouponTemplate findByName(String name);


    /****
     * <h2>根据Available和expired 标记查找模板记录<h2/>
     * @param available
     * @param expired
     * @return
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);


    /****
     * <h2>根据expired标记查找模板标记记录<h2/>
     * @param b
     */
    List<CouponTemplate> findAllByExpired(boolean b);


}
