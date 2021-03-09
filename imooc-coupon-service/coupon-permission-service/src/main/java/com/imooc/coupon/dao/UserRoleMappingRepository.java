package com.imooc.coupon.dao;

import com.imooc.coupon.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author xubr
 * @title: UserRoleMappingRepository
 * @projectName imooc-coupon
 * @description: <h1>UserRoleMapping dao</h1>
 * @date 2021/3/915:50
 */
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, Long> {

    /**
     * <h2>通过 userId 寻找数据记录<h2/>
     * @param userId
     * @return
     */
    UserRoleMapping findByUserId(Long userId);

}
