package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author xubr
 * @title: Role
 * @projectName imooc-coupon
 * @description: <h1>用户角色实体类</h1>
 * @date 2021/3/915:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_role")
public class Role {

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /** 角色名称 */
    @Basic
    @Column(name = "role_name", nullable = false)
    private String roleName;

    /** 角色标签 如ADMIN*/
    @Basic
    @Column(name = "role_tag", nullable = false)
    private String roleTag;
}
