package com.manage.dto.company;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公司核心团队响应DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyTeamResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 姓名（中文）
     */
    private String nameCn;

    /**
     * 姓名（英文）
     */
    private String nameEn;

    /**
     * 职位（中文）
     */
    private String positionCn;

    /**
     * 职位（英文）
     */
    private String positionEn;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 个人简介（中文）
     */
    private String profileCn;

    /**
     * 个人简介（英文）
     */
    private String profileEn;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 是否核心成员：0-否 1-是
     */
    private Boolean isCore;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}