package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * @author xubr
 * @title: IUserService
 * @projectName imooc-coupon
 * @description: <h1>用户服务相关的接口定义<h1/>
 * 1.用户的三类状态优惠卷信息展示服务
 * 2.查看用户当前可以领取的优惠卷模板 - coupon-template 微服务配合实现
 * 3.用户领取优惠卷服务
 * 4.用户消费优惠卷服务 - coupon-settlement 微服务配合实现
 * @date 2020/12/3110:01
 */
public interface IUserService {

    /**
     * 根据用户id和状态查询优惠卷记录
     * @param userId 用户id
     * @param status 优惠卷状态
     * @return  {@link Coupon}s
     * @throws CouponException
     */
    List<Coupon> findCouponsByStatus(Long userId,Integer status) throws CouponException;

    /**
     *<h2>根据用户id 查找当前可以领取的优惠卷模板<h2/>
     * @param userId
     * @return {@link CouponTemplateSDK}s
     * @throws CouponException
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId)throws CouponException;

    /**
     * <h2>用户领取优惠卷<h2/>
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     * @throws CouponException
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;


    /**
     * <h2>结算(核销)优惠卷<h2/>
     * 讲解:一开始SettlementInfo的入参只有前3个并没有核销,最后返回回去的时候有核销字段
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;



}
