package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1>优惠券模板基础服务接口实现<h1/>
 *
 * @author xubr 2020/12/20
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    private final CouponTemplateDao templateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }


    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     *
     * @return {@link CouponTemplate} 模板实体
     * @Param id 模板id
     **/
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = templateDao.findById(id);
        if (!template.isPresent()) {
            throw new CouponException("Template Is Not Exist:" + id);
        }
        return template.get();
    }

    /**
     * <h2>查找所有可用的优惠券模板<h2/>
     *
     * @return {@link CouponTemplate} 模板实体
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {

        List<CouponTemplate> templates = templateDao.findAllByAvailableAndExpired(
                true, false);

        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    /****
     * <h2>获取ids 到couponTemplateSDK 的映射<h2/>
     * @param ids
     * @return Map<key:模板 id, value:couponTemplateSDK></>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {

        List<CouponTemplate> templates = templateDao.findAllById(ids);
        //Function.identity()返回的是CouponTemplateSDK对象
        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toMap(
                CouponTemplateSDK::getId, Function.identity()
        ));
    }

    private CouponTemplateSDK template2TemplateSDK(CouponTemplate couponTemplate) {
        return new CouponTemplateSDK(
                couponTemplate.getId(),
                couponTemplate.getName(),
                couponTemplate.getLogo(),
                couponTemplate.getDesc(),
                couponTemplate.getCategory().getCode(),
                couponTemplate.getProductLine().getCode(),
                couponTemplate.getKey(),//并不是拼装好的key
                couponTemplate.getTarget().getCode(),
                couponTemplate.getRule()
        );
    }

}
