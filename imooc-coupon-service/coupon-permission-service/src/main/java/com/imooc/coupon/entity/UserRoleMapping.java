package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author xubr
 * @title: UserRoleMapping
 * @projectName imooc-coupon
 * @description: <h1>用户与 Role的映射实体关系</h1>
 * @date 2021/3/915:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_user_role_mapping")
public class UserRoleMapping {

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /** User 的主键 */
    @Basic
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Role 表的主键*/
    @Basic
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

}
