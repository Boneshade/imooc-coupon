package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IAsyncService;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <h1>构建优惠券模板接口实现<h1/>
 *
 * @author xubr 2020/12/20
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {


    private final IAsyncService iAsyncService;

    private final CouponTemplateDao templateDao;

    @Autowired
    public BuildTemplateServiceImpl(IAsyncService iAsyncService, CouponTemplateDao templateDao) {
        this.iAsyncService = iAsyncService;
        this.templateDao = templateDao;
    }


    /**
     * <h2>创建优惠券模板</h2>
     *
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {

        //参数合法性校验
        if (!request.validate()) {

            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }

        if (null != templateDao.findByName(request.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }

        //构造CouponTemplate 并保存到数据库中
        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);

        //根据优惠券模板异步生成优惠券码
        iAsyncService.asyncConstructCouponByTemplate(template);
        return template;
    }

    /****
     * <h2>将TemplateRequest 转换为 CouponTemplate<h2/>
     * @param request
     * @return
     */
    private CouponTemplate requestToTemplate(TemplateRequest request) {

        return new CouponTemplate(request.getName(),
                request.getLogo(), request.getDesc(), request.getCategory(), request.getProductionLine(),
                request.getCount(), request.getUserId(), request.getTarget(), request.getRule());
    }

}
