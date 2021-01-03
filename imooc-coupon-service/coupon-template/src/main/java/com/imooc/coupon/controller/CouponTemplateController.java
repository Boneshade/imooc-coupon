package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板相关的功能控制器<h1/>
 *
 * @author xubr 2020/12/27
 */
@RestController
@Slf4j
public class CouponTemplateController {

    /**
     * 构建优惠券模板服务
     **/
    private final IBuildTemplateService templateService;

    /**
     * 优惠券模板基础服务
     **/
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService templateService, ITemplateBaseService templateBaseService) {
        this.templateService = templateService;
        this.templateBaseService = templateBaseService;
    }

    /***
     * <h2>构建优惠券模板<h2/>
     * 127.0.0.1:7001/coupon-template/template/build
     * 127.0.0.1:9000/imooc/coupon-template/template/build
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return templateService.buildTemplate(request);
    }

    /***
     * <h2>构造优惠券模板详情<h2/>
     * 127..0.0.1:7001/coupon-template/template/info?id=1
     * 127.0.0.1:9000/imooc/coupon-template/template/build
     * @param id
     * @return
     * @throws CouponException
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("ID") Integer id) throws CouponException {
        log.info("Build Template Info For: {}", JSON.toJSONString(id));
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * <h2>查找所有可用的优惠券模板<h2/>
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     * 127.0.0.1:9000/imooc/coupon-template/template/build
     *
     * @return
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template");
        return templateBaseService.findAllUsableTemplate();
    }

    /****
     * <h2>获取ids 到couponTemplateSDK 的映射<h2/>
     * @param ids
     * @return Map<key:模板 id, value:couponTemplateSDK></>
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids) {
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }


}
