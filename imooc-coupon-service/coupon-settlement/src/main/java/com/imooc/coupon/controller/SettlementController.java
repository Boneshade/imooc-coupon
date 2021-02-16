package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.executor.ExecuteManager;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>结算服务的 Controller<h1/>
 * @author xubr 2021/2/12
 */
@Slf4j
@RestController
public class SettlementController {

    /**
     * 结算规则执行管理器
     **/
    private final ExecuteManager executeManager;

    @Autowired
    public SettlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * <h2>优惠券结算<h2/>
     * @param settlementInfo
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlementInfo) throws CouponException {

        log.info("settlement :{}", JSON.toJSONString(settlementInfo));
        return executeManager.computeRule(settlementInfo);
    }





}
