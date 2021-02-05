package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IUserService;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h2>用户服务controller<h2/>
 * @author xubr 2021/2/5
 */
@Slf4j
@RestController
public class UserServiceController {

    private IUserService userService;

    @Autowired
    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * <h2>根据用户id和优惠券状态查找用户优惠券记录<h2/>
     * @param userId
     * @param status
     * @return
     * @throws CouponException
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(@RequestParam("userId") Long userId,
                                            @RequestParam("status") Integer status) throws CouponException {
        log.info("Find Coupons By status:{},{}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }

    /**
     * <h2>根据用户id 查找当前可以领取的优惠券模板<h2/>
     * @param userId
     * @return
     * @throws CouponException
     */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(@RequestParam("userId") Long userId) throws CouponException {
        log.info("Find Available Template:{}", userId);
        return userService.findAvailableTemplate(userId);
    }

    /**
     * <h2>用户领取优惠券<h2/>
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request) throws CouponException {
        log.info("Acquire Template:{}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }


    /**
     * <h2>结算核销优惠券<h2/>
     * @param info
     * @throws CouponException
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException {
        log.info("Acquire Template:{}", JSON.toJSONString(info));
        return userService.settlement(info);
    }


}
