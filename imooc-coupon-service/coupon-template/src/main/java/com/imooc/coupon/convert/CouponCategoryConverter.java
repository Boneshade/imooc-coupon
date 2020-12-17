package com.imooc.coupon.convert;

import com.imooc.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * @author xubr 2020/12/17
 * AttributeConverter<x,y>
 * x:是实体属性的类型
 * Y:是数据库字段的类型
 */
@Convert
public class CouponCategoryConverter
        implements AttributeConverter<CouponCategory,String> {


    /**
     * <H2>将实体属性x转化为HY存储到数据库中<H2/>
     * @param couponCategory
     * @return
     */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     * 将数据库中的字段y转化为实体属性x,查询操作时执行的操作
     * @param code
     * @return
     */
    @Override
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
