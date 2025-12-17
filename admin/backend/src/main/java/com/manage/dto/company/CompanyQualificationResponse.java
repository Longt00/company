package com.manage.dto.company;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公司资质文件响应DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyQualificationResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 证书名称（中文）
     */
    private String certificateNameCn;

    /**
     * 证书名称（英文）
     */
    private String certificateNameEn;

    /**
     * 证书类型：business_license-营业执照，iso-ISO认证，honor-荣誉证书，other-其他
     */
    private String certificateType;

    /**
     * 证书类型描述
     */
    private String certificateTypeDesc;

    /**
     * 证书图片URL
     */
    private String certificateImageUrl;

    /**
     * 发证日期
     */
    private LocalDate issueDate;

    /**
     * 有效期至
     */
    private LocalDate expireDate;

    /**
     * 发证机构（中文）
     */
    private String issuingAuthorityCn;

    /**
     * 发证机构（英文）
     */
    private String issuingAuthorityEn;

    /**
     * 证书编号
     */
    private String certificateNumber;

    /**
     * 状态：0-过期 1-有效 2-禁用
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 是否即将过期（30天内）
     */
    private Boolean isExpiringSoon;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}