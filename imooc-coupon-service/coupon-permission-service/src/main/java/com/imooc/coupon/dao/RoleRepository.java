package com.imooc.coupon.dao;

import com.imooc.coupon.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author xubr
 * @title: RoleRepository
 * @projectName imooc-coupon
 * @description: <h1></h1>
 * @date 2021/3/915:48
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
