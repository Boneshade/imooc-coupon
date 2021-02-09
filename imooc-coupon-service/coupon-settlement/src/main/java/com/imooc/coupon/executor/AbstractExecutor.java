package com.imooc.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>规则执行器抽象类，定义通用方法<h1/>
 * @author xubr 2021/2/8
 */
public abstract class AbstractExecutor {

    /**
     * <h2>校验商品类型与优惠券是否匹配<h2/>
     * 需要注意:
     * 1.这里实现的单品类优惠券的校验,多品类优惠券重载此方法
     * 2.商品只需要有一个优惠券要求的商品类型去匹配就可以
     * @param settlementInfo
     * @return
     */
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        List<Integer> goodsType = settlementInfo.getGoodsInfos()
                .stream().map(GoodsInfo::getType)
                .collect(Collectors.toList());

        List<Integer> templateGoodsType = JSON.parseObject(settlementInfo.getCouponAndTemplateInfos().get(0)
                .getTemplate().getRule().getUsage().getGoodsType(), List.class);

        //存在交集即可
        return CollectionUtils.isNotEmpty(
                //若有交集会打印出如:[2, 3]
                CollectionUtils.intersection(goodsType, templateGoodsType));
    }

    /**
     * <h2>处理商品类型与优惠券限制不匹配的情况<h2/>
     * @param settlementInfo {@link SettlementInfo} 用户传递的结算信息
     * @param goodsSum       商品总价
     * @return {@link SettlementInfo} 已经修改过的结算信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(
            SettlementInfo settlementInfo, double goodsSum
    ) {

        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlementInfo);

        //当商品类型不满足时,直接返回总价,并清空优惠券
        if (!isGoodsTypeSatisfy) {
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }
        return null;
    }

    /**
     * <h2>商品总价<h2/>
     * @param goodsInfos
     * @return
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {
        return goodsInfos.stream().mapToDouble(
                g -> g.getPrice() * g.getCount()
        ).sum();
    }

    /**
     * <h2>保留两位小数<h2/>
     * @param value
     * @return
     */
    protected double retain2Decimals(double value) {
        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }


    /**
     * <h2>最小支付费用</h2>
     */
    protected double minCost() {
        return 0.1;
    }


}
