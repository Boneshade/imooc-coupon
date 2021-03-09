package com.imooc.coupon.dao;

import com.imooc.coupon.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author xubr
 * @title: PathRepository
 * @projectName imooc-coupon
 * @description: <h1>Path DAO</h1>
 * @date 2021/3/915:39
 */
public interface PathRepository extends JpaRepository<Path, Integer> {


    /**
     * <h2>根据服务名称查找 path 记录<h2/>
     * @param serviceName
     * @return
     */
    List<Path> findAllByServiceName(String serviceName);


    /**
     * <h2>根据 路径模式 + 请求类型 查找数据记录<h2/>
     * @param pathPattern
     * @param httpMethod
     * @return
     */
    Path findByPathPatternAndHttpMethod(String pathPattern, String httpMethod);


}
