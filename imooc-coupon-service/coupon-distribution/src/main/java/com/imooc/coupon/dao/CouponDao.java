package com.imooc.coupon.dao;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>coupon Dao 接口定义<h1/>
 * @author xubr 2020/12/30
 */
public interface CouponDao extends JpaRepository<Coupon,Integer> {

    /**
     * <h2>根据userId + 状态寻找优惠券记录<h2/>
     * @param userId
     * @param status
     * @return
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);





}
