package com.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 公司部门架构实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "company_department")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDepartment {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 部门名称（中文）
     */
    @NotBlank(message = "部门名称（中文）不能为空")
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    @Column(name = "department_name_cn", nullable = false, length = 100)
    private String departmentNameCn;

    /**
     * 部门名称（英文）
     */
    @Size(max = 100, message = "英文部门名称长度不能超过100个字符")
    @Column(name = "department_name_en", length = 100)
    private String departmentNameEn;

    /**
     * 父部门ID（0表示顶级部门）
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 部门层级
     */
    @Column(name = "department_level", nullable = false)
    private Integer departmentLevel;

    /**
     * 部门描述（中文）
     */
    @Column(name = "description_cn", columnDefinition = "TEXT")
    private String descriptionCn;

    /**
     * 部门描述（英文）
     */
    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 创建人ID
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 更新人ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * JPA生命周期回调 - 更新前自动设置更新时间
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}