package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author xubr
 * @title: RolePathMapping
 * @projectName imooc-coupon
 * @description: <h1>Role 与 path的映射关系</h1>
 * @date 2021/3/915:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coupon_role_path_mapping")
public class RolePathMapping {

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /** Role 表的主键 */
    @Basic
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    /** Path 表的主键 */
    @Basic
    @Column(name = "path_id", nullable = false)
    private Integer pathId;

}
