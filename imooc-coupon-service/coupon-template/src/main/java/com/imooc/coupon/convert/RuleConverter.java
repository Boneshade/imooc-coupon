package com.imooc.coupon.convert;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.vo.TemplateRule;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * @author xubr 2020/12/17
 */
@Convert
public class RuleConverter implements AttributeConverter<TemplateRule, String> {
    @Override
    public String convertToDatabaseColumn(TemplateRule rule) {
        return JSON.toJSONString(rule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String rule) {
        return JSON.parseObject(rule, TemplateRule.class);
    }
}
