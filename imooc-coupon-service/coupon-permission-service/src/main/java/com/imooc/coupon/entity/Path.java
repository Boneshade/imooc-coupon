package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author xubr
 * @title: Path
 * @projectName imooc-coupon
 * @description: <h1>URL 路径信息实体</h1>
 * @date 2021/3/915:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_path")
public class Path {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 路径模式 如/coupon-template/template/build
     */
    @Basic
    @Column(name = "path_pattern", nullable = false)
    private String pathPattern;


    /**
     * HTTP 方法类型 (GET,POST)
     */
    @Basic
    @Column(name = "http_method", nullable = false)
    private String httpMethod;

    /**
     * 路径名称
     */
    @Basic
    @Column(name = "path_name", nullable = false)
    private String pathName;


    /**
     * 服务名称
     */
    @Basic
    @Column(name = "service_name", nullable = false)
    private String serviceName;


    /**
     * 操作模式: READ, WRITE
     */
    @Basic
    @Column(name = "op_mode", nullable = false)
    private String opMode;


    public Path(String pathPattern, String httpMethod, String pathName,
                String serviceName, String opMode) {

        this.pathPattern = pathPattern;
        this.httpMethod = httpMethod;
        this.pathName = pathName;
        this.serviceName = serviceName;
        this.opMode = opMode;
    }

}
