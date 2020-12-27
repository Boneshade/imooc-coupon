package com.imooc.coupon.service;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * <h1>优惠券模板基础服务的测试<h1/>
 *
 * @author xubr 2020/12/27
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TemplateBaseTest {

    @Autowired
    private ITemplateBaseService baseService;
    @Test
    public void testBuildTemplate() throws CouponException{
        System.out.println(JSON.toJSONString(baseService.buildTemplateInfo(1)));
    }

    @Test
    public void testFindAllUsableTemplate() throws CouponException{
        System.out.println(JSON.toJSONString(baseService.findAllUsableTemplate()));
    }


    @Test
    public void testFindId2TemplateSDK() throws CouponException{
        System.out.println(JSON.toJSONString(baseService.findIds2TemplateSDK(Arrays.asList(1,2,3))));
    }


}
