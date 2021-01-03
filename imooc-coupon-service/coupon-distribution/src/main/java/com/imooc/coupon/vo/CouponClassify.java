package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>用户优惠券的分类,根据优惠券状态<h1/>
 *
 * @author xubr 2021/1/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponClassify {

    /**
     * 可以使用的
     **/
    private List<Coupon> usable;

    /**
     * 已使用的
     **/
    private List<Coupon> used;

    /**
     * 已过期的
     **/
    private List<Coupon> expired;

    /**
     * <h2>对当前的优惠券进行分类<h2/>
     *
     * @param coupons
     * @return
     */
    public static CouponClassify classify(List<Coupon> coupons) {

        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(coupon -> {
            boolean isTimeExpire;
            long curTime = System.currentTimeMillis();
            //Period:有效期规则 REGULAR:固定的
            if (coupon.getTemplateSDK().getRule().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode())) {

                isTimeExpire = coupon.getTemplateSDK().getRule()
                        .getExpiration().getDeadline() <= curTime;
            } else {
                //Gap:变动有效期 AssignTime领取优惠券时间+有效间隔
                isTimeExpire = DateUtils.addDays(coupon.getAssignTime(), coupon.getTemplateSDK()
                        .getRule().getExpiration().getGap()).getTime() <= curTime;
            }

            if (coupon.getStatus() == CouponStatus.USED) {
                used.add(coupon);
            } else if (coupon.getStatus() == CouponStatus.EXPIRED || isTimeExpire) {
                expired.add(coupon);
            } else {
                usable.add(coupon);
            }
        });

        return new CouponClassify(usable, used, expired);

    }


}
