package com.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 公司核心团队实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "company_team")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyTeam {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 姓名（中文）
     */
    @NotBlank(message = "姓名（中文）不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    @Column(name = "name_cn", nullable = false, length = 100)
    private String nameCn;

    /**
     * 姓名（英文）
     */
    @Size(max = 100, message = "英文姓名长度不能超过100个字符")
    @Column(name = "name_en", length = 100)
    private String nameEn;

    /**
     * 职位（中文）
     */
    @NotBlank(message = "职位（中文）不能为空")
    @Size(max = 200, message = "职位长度不能超过200个字符")
    @Column(name = "position_cn", nullable = false, length = 200)
    private String positionCn;

    /**
     * 职位（英文）
     */
    @Size(max = 200, message = "英文职位长度不能超过200个字符")
    @Column(name = "position_en", length = 200)
    private String positionEn;

    /**
     * 头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /**
     * 个人简介（中文）
     */
    @Column(name = "profile_cn", columnDefinition = "TEXT")
    private String profileCn;

    /**
     * 个人简介（英文）
     */
    @Column(name = "profile_en", columnDefinition = "TEXT")
    private String profileEn;

    /**
     * 所属部门
     */
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    @Column(name = "department", length = 100)
    private String department;

    /**
     * 是否核心成员：0-否 1-是
     */
    @Column(name = "is_core", nullable = false)
    private Boolean isCore;

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