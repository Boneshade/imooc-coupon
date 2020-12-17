package com.imooc.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.imooc.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * <h1>优惠券模板实体类自定义序列化器<h1/>
 * 用来返回给前段你想让他展示的对应的数据
 *
 * @author xubr 2020/12/17
 */
public class CouponTemplateSerialize
        extends JsonSerializer<CouponTemplate> {


    @Override
    public void serialize(CouponTemplate couponTemplate,
                          JsonGenerator generator,
                          SerializerProvider serializerProvider) throws IOException {

        // 开始序列化对象
        generator.writeStartObject();
        generator.writeStringField("id", couponTemplate.getId().toString());
        generator.writeStringField("name", couponTemplate.getName());
        generator.writeStringField("logo", couponTemplate.getLogo());
        generator.writeStringField("desc", couponTemplate.getDesc());
        generator.writeStringField("category", couponTemplate.getCategory().getDescription());
        generator.writeStringField("productLine", couponTemplate.getProductLine().getDescription());
        generator.writeStringField("count", couponTemplate.getCount().toString());
        generator.writeStringField("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(couponTemplate.getCreateTime().toString()));
        generator.writeStringField("userId", couponTemplate.getUserId().toString());
        generator.writeStringField("key", couponTemplate.getKey() + String.format("%04d", couponTemplate.getId()));
        generator.writeStringField("target", couponTemplate.getTarget().getDescription());
        generator.writeStringField("rule", JSON.toJSONString(couponTemplate.getRule()));
        //结束序列化对象
        generator.writeEndObject();

    }
}
