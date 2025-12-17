package com.manage.dto.company;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公司部门架构响应DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyDepartmentResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 部门名称（中文）
     */
    private String departmentNameCn;

    /**
     * 部门名称（英文）
     */
    private String departmentNameEn;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门层级
     */
    private Integer departmentLevel;

    /**
     * 部门描述（中文）
     */
    private String descriptionCn;

    /**
     * 部门描述（英文）
     */
    private String descriptionEn;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 子部门列表
     */
    private List<CompanyDepartmentResponse> children;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}