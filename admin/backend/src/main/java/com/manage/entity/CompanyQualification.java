package com.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公司资质文件实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "company_qualification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyQualification {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 证书名称（中文）
     */
    @NotBlank(message = "证书名称（中文）不能为空")
    @Size(max = 200, message = "证书名称长度不能超过200个字符")
    @Column(name = "certificate_name_cn", nullable = false, length = 200)
    private String certificateNameCn;

    /**
     * 证书名称（英文）
     */
    @Size(max = 200, message = "英文证书名称长度不能超过200个字符")
    @Column(name = "certificate_name_en", length = 200)
    private String certificateNameEn;

    /**
     * 证书类型：business_license-营业执照，iso-ISO认证，honor-荣誉证书，other-其他
     */
    @NotBlank(message = "证书类型不能为空")
    @Column(name = "certificate_type", nullable = false, length = 50)
    private String certificateType;

    /**
     * 证书图片URL
     */
    @NotBlank(message = "证书图片不能为空")
    @Size(max = 500, message = "证书图片URL长度不能超过500个字符")
    @Column(name = "certificate_image_url", nullable = false, length = 500)
    private String certificateImageUrl;

    /**
     * 发证日期
     */
    @Column(name = "issue_date")
    private LocalDate issueDate;

    /**
     * 有效期至
     */
    @Column(name = "expire_date")
    private LocalDate expireDate;

    /**
     * 发证机构（中文）
     */
    @Size(max = 200, message = "发证机构名称长度不能超过200个字符")
    @Column(name = "issuing_authority_cn", length = 200)
    private String issuingAuthorityCn;

    /**
     * 发证机构（英文）
     */
    @Size(max = 200, message = "发证机构英文名称长度不能超过200个字符")
    @Column(name = "issuing_authority_en", length = 200)
    private String issuingAuthorityEn;

    /**
     * 证书编号
     */
    @Size(max = 100, message = "证书编号长度不能超过100个字符")
    @Column(name = "certificate_number", length = 100)
    private String certificateNumber;

    /**
     * 状态：0-过期 1-有效 2-禁用
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

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