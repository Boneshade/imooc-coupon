package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>立减优惠券结算规则执行器<h1/>
 * @author xubr 2021/2/9
 */
@Slf4j
@Component
public class LijianExecutor extends AbstractExecutor implements RuleExecutor {

    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.LIJIAN;
    }

    /**
     * <h2>优惠券规则的计算<h2/>
     * @param settlementInfo {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {

        double goodsSum = retain2Decimals(goodsCostSum(
                settlementInfo.getGoodsInfos()
        ));

        //校验商品类型与优惠券是否匹配
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlementInfo, goodsSum
        );

        //当商品类型不满足时会直接返回总价
        if (null != probability) {
            log.debug("LIJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 立减优惠券直接使用,没有门槛
        CouponTemplateSDK templateSDK = settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate();

        //获取额度
        double quota = templateSDK.getRule().getDiscount().getQuota();

        //计算使用优惠券之后的价格 - 结算
        settlementInfo.setCost(retain2Decimals(goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost());
        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}", goodsSum, settlementInfo.getCost());
        return settlementInfo;
    }
}
