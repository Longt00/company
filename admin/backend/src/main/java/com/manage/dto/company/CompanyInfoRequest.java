package com.manage.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 公司基本信息请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyInfoRequest {

    /**
     * 公司名称
     */
    @NotBlank(message = "公司名称不能为空")
    @Size(max = 200, message = "公司名称长度不能超过200个字符")
    @JsonProperty("companyName")
    private String companyName;

    /**
     * 公司简称
     */
    @Size(max = 100, message = "公司简称长度不能超过100个字符")
    @JsonProperty("companyShortName")
    private String companyShortName;

    /**
     * 公司描述
     */
    @Size(max = 2000, message = "公司描述长度不能超过2000个字符")
    @JsonProperty("companyDescription")
    private String companyDescription;

    /**
     * 公司Logo
     */
    @Size(max = 500, message = "Logo URL长度不能超过500个字符")
    @JsonProperty("companyLogo")
    private String companyLogo;

    /**
     * 企业视频
     */
    @Size(max = 500, message = "视频URL长度不能超过500个字符")
    @JsonProperty("companyVideo")
    private String companyVideo;

    /**
     * 成立日期
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式不正确，应为YYYY-MM-DD")
    @JsonProperty("establishDate")
    private String establishDate;

    /**
     * 注册资本
     */
    @Size(max = 50, message = "注册资本长度不能超过50个字符")
    @JsonProperty("registeredCapital")
    private String registeredCapital;

    /**
     * 法定代表人
     */
    @Size(max = 100, message = "法定代表人长度不能超过100个字符")
    @JsonProperty("legalRepresentative")
    private String legalRepresentative;

    /**
     * 经营范围
     */
    @Size(max = 2000, message = "经营范围长度不能超过2000个字符")
    @JsonProperty("businessScope")
    private String businessScope;

    /**
     * 公司地址
     */
    @Size(max = 500, message = "公司地址长度不能超过500个字符")
    @JsonProperty("companyAddress")
    private String companyAddress;

    /**
     * 公司网站
     */
    @Size(max = 200, message = "网站长度不能超过200个字符")
    @JsonProperty("companyWebsite")
    private String companyWebsite;

    /**
     * 公司电话
     */
    @Size(max = 50, message = "电话长度不能超过50个字符")
    @JsonProperty("companyPhone")
    private String companyPhone;

    /**
     * 公司邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @JsonProperty("companyEmail")
    private String companyEmail;

    /**
     * 状态：0-禁用 1-启用
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    @JsonProperty("status")
    private Integer status = 1;
}