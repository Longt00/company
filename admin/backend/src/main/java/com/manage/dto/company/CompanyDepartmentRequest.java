package com.manage.dto.company;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 公司部门架构请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyDepartmentRequest {

    /**
     * 部门名称（中文）
     */
    @NotBlank(message = "部门名称（中文）不能为空")
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    private String departmentNameCn;

    /**
     * 部门名称（英文）
     */
    @Size(max = 100, message = "英文部门名称长度不能超过100个字符")
    private String departmentNameEn;

    /**
     * 父部门ID（0表示顶级部门）
     */
    private Long parentId;

    /**
     * 部门层级
     */
    @NotNull(message = "部门层级不能为空")
    private Integer departmentLevel;

    /**
     * 部门描述（中文）
     */
    @Size(max = 1000, message = "部门描述长度不能超过1000个字符")
    private String descriptionCn;

    /**
     * 部门描述（英文）
     */
    @Size(max = 1000, message = "英文部门描述长度不能超过1000个字符")
    private String descriptionEn;

    /**
     * 排序顺序
     */
    @NotNull(message = "排序顺序不能为空")
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}