package com.manage.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公司基本信息响应DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyInfoResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 公司名称
     */
    @JsonProperty("companyName")
    private String companyName;

    /**
     * 公司简称
     */
    @JsonProperty("companyShortName")
    private String companyShortName;

    /**
     * 公司描述
     */
    @JsonProperty("companyDescription")
    private String companyDescription;

    /**
     * 公司Logo
     */
    @JsonProperty("companyLogo")
    private String companyLogo;

    /**
     * 企业视频
     */
    @JsonProperty("companyVideo")
    private String companyVideo;

    /**
     * 成立日期
     */
    @JsonProperty("establishDate")
    private String establishDate;

    /**
     * 注册资本
     */
    @JsonProperty("registeredCapital")
    private String registeredCapital;

    /**
     * 法定代表人
     */
    @JsonProperty("legalRepresentative")
    private String legalRepresentative;

    /**
     * 经营范围
     */
    @JsonProperty("businessScope")
    private String businessScope;

    /**
     * 公司地址
     */
    @JsonProperty("companyAddress")
    private String companyAddress;

    /**
     * 公司网站
     */
    @JsonProperty("companyWebsite")
    private String companyWebsite;

    /**
     * 公司电话
     */
    @JsonProperty("companyPhone")
    private String companyPhone;

    /**
     * 公司邮箱
     */
    @JsonProperty("companyEmail")
    private String companyEmail;

    /**
     * 状态：0-禁用 1-启用
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;
}