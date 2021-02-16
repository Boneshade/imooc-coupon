package com.imooc.coupon.executor;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券结算规则执行管理器<h1/>
 * 即根据用户的请求(SettlementInfo)找到对应的Executor,去做结算
 * BeanPostProcessor: Bean 后置处理器
 * @author xubr 2021/2/12
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ExecuteManager implements BeanPostProcessor {

    /**
     * 规则执行器映射
     **/
    private static Map<RuleFlag, RuleExecutor> executorIndex =
            new HashMap<>(RuleFlag.values().length);


    /**
     * <h2>优惠券结算规则入口<h2/>
     * 注意:一定要保证传递进来的优惠券个数>=1
     * @param settlementInfo
     * @return
     * @throws CouponException
     */
    public SettlementInfo computeRule(SettlementInfo settlementInfo) throws CouponException {
        SettlementInfo result = null;

        //单类优惠券
        if (settlementInfo.getCouponAndTemplateInfos().size() == 1) {

            //获取优惠券类别
            CouponCategory category = CouponCategory.of(
                    settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate()
                            .getCategory()
            );
            switch (category) {
                case MANJIAN:
                    result = executorIndex.get(RuleFlag.MANJIAN).computeRule(settlementInfo);
                    break;
                case ZHEKOU:
                    result = executorIndex.get(RuleFlag.ZHEKOU).computeRule(settlementInfo);
                    break;
                case LIJIAN:
                    result = executorIndex.get(RuleFlag.LIJIAN).computeRule(settlementInfo);
                    break;
            }
        } else {
            //多类优惠券
            List<CouponCategory> categories = new ArrayList<>(
                    settlementInfo.getCouponAndTemplateInfos().size()
            );

            settlementInfo.getCouponAndTemplateInfos().forEach(
                    ct ->
                            categories.add(CouponCategory.of(
                                    ct.getTemplate().getCategory()
                            )));


            if (categories.size() != 2) {
                throw new CouponException("Not Support For More" +
                        "Template Category");
            } else {
                if (categories.contains(CouponCategory.MANJIAN) &&
                        categories.contains(CouponCategory.ZHEKOU)) {
                    result = executorIndex.get(RuleFlag.MANJIAN_ZHEKOU).computeRule(settlementInfo);
                } else {
                    throw new CouponException("Not Support For Other" + "Template Category");
                }
            }
        }
        return result;

    }


    /**
     * <h2>在 bean初始化之前去执行(before)<h2/>
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {

        if (!(bean instanceof RuleExecutor)) {
            return bean;
        }

        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();

        if (executorIndex.containsKey(ruleFlag)) {
            throw new IllegalStateException("There is already an executor" +
                    "for rule flag" + ruleFlag);
        }


        log.info("Load executor {} for rule flag {}", executor.getClass(), ruleFlag);
        executorIndex.put(ruleFlag, executor);
        return null;

    }

    /**
     * <h2>在bean初始化之后去执行(after)<h2/>
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
