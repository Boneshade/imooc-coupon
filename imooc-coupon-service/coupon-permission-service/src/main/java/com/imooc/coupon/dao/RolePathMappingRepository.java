package com.imooc.coupon.dao;

import com.imooc.coupon.entity.RolePathMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author xubr
 * @title: RolePathMappingRepository
 * @projectName imooc-coupon
 * @description: <h1>RolePathMapping Dao</h1>
 * @date 2021/3/915:45
 */
public interface RolePathMappingRepository extends
        JpaRepository<RolePathMapping, Integer> {

    /**
     * <h2>通过 角色id+ 路径id寻找数据记录<h2/>
     * @param roleId
     * @param pathId
     * @return
     */
    RolePathMapping findByRoleIdAndPathId(Integer roleId, Integer pathId);

}
