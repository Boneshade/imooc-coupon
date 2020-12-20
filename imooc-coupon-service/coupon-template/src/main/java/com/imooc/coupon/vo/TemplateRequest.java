package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.lang.ref.PhantomReference;
import java.security.KeyStore;

/**
 * <h1>优惠券模板创建请求对象<h1/>
 *
 * @author xubr 2020/12/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {

    private String name;

    private String logo;

    private String desc;

    private String category;

    private Integer productionLine;

    private Integer count;

    private Long userId;

    /**
     * 目标用户
     **/
    private Integer target;

    /****
     * 优惠券规则
     */
    private TemplateRule rule;

    /****
     * <h2>校验对象的合法性<h2/>
     * @return
     */
    public boolean validate() {

        boolean stringValid = StringUtils.isNoneEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);
        boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productionLine)
                && null != DistributeTarget.of(target);

        boolean numValid = count > 0 && userId > 0;
        return stringValid && enumValid && numValid && rule.validate();
    }

}
