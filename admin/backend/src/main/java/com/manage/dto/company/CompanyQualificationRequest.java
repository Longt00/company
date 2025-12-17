package com.manage.dto.company;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 公司资质文件请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyQualificationRequest {

    /**
     * 证书名称（中文）
     */
    @NotBlank(message = "证书名称（中文）不能为空")
    @Size(max = 200, message = "证书名称长度不能超过200个字符")
    private String certificateNameCn;

    /**
     * 证书名称（英文）
     */
    @Size(max = 200, message = "英文证书名称长度不能超过200个字符")
    private String certificateNameEn;

    /**
     * 证书类型：business_license-营业执照，iso-ISO认证，honor-荣誉证书，other-其他
     */
    @NotBlank(message = "证书类型不能为空")
    @Size(max = 50, message = "证书类型长度不能超过50个字符")
    private String certificateType;

    /**
     * 证书图片URL
     */
    @NotBlank(message = "证书图片不能为空")
    @Size(max = 500, message = "证书图片URL长度不能超过500个字符")
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
    @Size(max = 200, message = "发证机构名称长度不能超过200个字符")
    private String issuingAuthorityCn;

    /**
     * 发证机构（英文）
     */
    @Size(max = 200, message = "发证机构英文名称长度不能超过200个字符")
    private String issuingAuthorityEn;

    /**
     * 证书编号
     */
    @Size(max = 100, message = "证书编号长度不能超过100个字符")
    private String certificateNumber;

    /**
     * 状态：0-过期 1-有效 2-禁用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 排序顺序
     */
    @NotNull(message = "排序顺序不能为空")
    private Integer sortOrder;
}