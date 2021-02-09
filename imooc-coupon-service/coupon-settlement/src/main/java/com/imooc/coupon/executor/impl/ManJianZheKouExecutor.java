package com.imooc.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>满减+折扣优惠券结算规则执行器<h1/>
 * @author xubr 2021/2/9
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * <h2>规则类型标记<h2/>
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * <h2>校验商品类型与优惠券是否匹配<h2/>
     * 需要注意:
     * 1.这里实现的满减+折扣优惠券的校验,多品类优惠券重载此方法
     * 2.如果想要使用多类优惠券,则必须要所有的商品类型都包含在内,即差集为空
     * @param settlementInfo {@link SettlementInfo} 用户传递的计算信息
     * @return
     */
    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        log.debug("Check ManJian And ZheKou Is Match Or Not!");

        List<Integer> goodsType = settlementInfo.getGoodsInfos().stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());

        List<Integer> templateGoodsType = new ArrayList<>();
        settlementInfo.getCouponAndTemplateInfos().forEach(ct -> {
            templateGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));
        });

        //如果想要使用多类优惠券,则必须要所有的商品类型都包含在内,即差集为空
        return CollectionUtils.isEmpty(
                CollectionUtils.subtract(goodsType, templateGoodsType)
        );

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

        //校验商品类型与优惠券是否匹配(此处调用的是子类中的isGoodsTypeSatisfy方法)
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlementInfo, goodsSum
        );

        //当商品类型不满足时会直接返回总价
        if (null != probability) {
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct : settlementInfo.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }

        assert null != manJian;
        assert null != zheKou;

        //当前的折扣优惠券和满减卷如果不能(一起使用),清空优惠券,返回商品原价
        if (!isTemplateCanShared(manJian, zheKou)) {
            log.debug("Current ManJian And Zhekou Can Not Shared!");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }

        //实际结算完成的优惠券信息
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = (double) manJian.getTemplate().getRule()
                .getDiscount().getBase();
        double manJianQuota = (double) manJian.getTemplate().getRule()
                .getDiscount().getQuota();


        // 最终的价格
        double targetSum = goodsSum;

        // 先计算满减
        if (targetSum >= manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        // 再计算折扣
        double zheKouQuota = (double) zheKou.getTemplate().getRule()
                .getDiscount().getQuota();
        targetSum *= zheKouQuota * 1.0 / 100;
        ctInfos.add(zheKou);

        settlementInfo.setCouponAndTemplateInfos(ctInfos);
        settlementInfo.setCost(retain2Decimals(
                targetSum > minCost() ? targetSum : minCost()
        ));

        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settlementInfo.getCost());

        return settlementInfo;
    }

    /**
     * <h2>当前的两张优惠券是否可以共用<h2/>
     * 即校验 TemplateRule 中的 weight 是否满足条件
     * @param manJian
     * @param zheKou
     * @return
     */
    private boolean isTemplateCanShared(
            SettlementInfo.CouponAndTemplateInfo manJian,
            SettlementInfo.CouponAndTemplateInfo zheKou) {

        String manjianKey = manJian.getTemplate().getKey()
                + String.format("%04d", manJian.getTemplate().getId());

        String zhekouKey = zheKou.getTemplate().getKey()
                + String.format("04d", zheKou.getTemplate().getId());


        List<String> allSharedKeysForManjian = new ArrayList<>();
        allSharedKeysForManjian.add(manjianKey);
        //其他可以共用的优惠券
        allSharedKeysForManjian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(),
                List.class
        ));


        List<String> allSharedKeysForZhekou = new ArrayList<>();
        allSharedKeysForZhekou.add(zhekouKey);
        allSharedKeysForZhekou.addAll(JSON.parseObject(
                zheKou.getTemplate().getRule().getWeight(),
                List.class
        ));

        //判断manjianKey和zhekouKey是否allSharedKeysForManjian或allSharedKeysForZhekou可以共用的子集
        return CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey, zhekouKey),
                allSharedKeysForManjian)
                || CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey, zhekouKey),
                allSharedKeysForZhekou
        );

    }


}
