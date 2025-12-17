package com.manage.dto.company;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 公司核心团队请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyTeamRequest {

    /**
     * 姓名（中文）
     */
    @NotBlank(message = "姓名（中文）不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String nameCn;

    /**
     * 姓名（英文）
     */
    @Size(max = 100, message = "英文姓名长度不能超过100个字符")
    private String nameEn;

    /**
     * 职位（中文）
     */
    @NotBlank(message = "职位（中文）不能为空")
    @Size(max = 200, message = "职位长度不能超过200个字符")
    private String positionCn;

    /**
     * 职位（英文）
     */
    @Size(max = 200, message = "英文职位长度不能超过200个字符")
    private String positionEn;

    /**
     * 头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatarUrl;

    /**
     * 个人简介（中文）
     */
    @Size(max = 2000, message = "个人简介长度不能超过2000个字符")
    private String profileCn;

    /**
     * 个人简介（英文）
     */
    @Size(max = 2000, message = "英文个人简介长度不能超过2000个字符")
    private String profileEn;

    /**
     * 所属部门
     */
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    private String department;

    /**
     * 是否核心成员：0-否 1-是
     */
    @NotNull(message = "是否核心成员不能为空")
    private Boolean isCore;

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