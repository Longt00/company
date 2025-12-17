package com.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "operation_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {

    /**
     * 日志ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作类型：UPLOAD/UPDATE/DELETE/CREATE
     */
    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;

    /**
     * 操作模块：IMAGE/VIDEO/PRODUCT/COMPANY
     */
    @Column(name = "operation_module", nullable = false, length = 50)
    private String operationModule;

    /**
     * 操作描述
     */
    @Column(name = "operation_desc", nullable = false, length = 500)
    private String operationDesc;

    /**
     * 目标类型
     */
    @Column(name = "target_type", length = 50)
    private String targetType;

    /**
     * 目标ID
     */
    @Column(name = "target_id")
    private Long targetId;

    /**
     * 目标名称
     */
    @Column(name = "target_name", length = 255)
    private String targetName;

    /**
     * 操作用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 操作用户名
     */
    @Column(name = "username", length = 50)
    private String username;

    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 浏览器信息
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 操作时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
}