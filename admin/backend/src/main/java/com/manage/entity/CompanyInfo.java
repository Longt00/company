package com.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * 公司基本信息实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "company_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfo {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 公司名称
     */
    @NotBlank(message = "公司名称不能为空")
    @Size(max = 200, message = "公司名称长度不能超过200个字符")
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    /**
     * 公司简称
     */
    @Size(max = 100, message = "公司简称长度不能超过100个字符")
    @Column(name = "company_short_name", length = 100)
    private String companyShortName;

    /**
     * 公司描述
     */
    @Size(max = 2000, message = "公司描述长度不能超过2000个字符")
    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;

    /**
     * 公司Logo
     */
    @Size(max = 500, message = "Logo URL长度不能超过500个字符")
    @Column(name = "company_logo", length = 500)
    private String companyLogo;

    /**
     * 企业视频
     */
    @Size(max = 500, message = "视频URL长度不能超过500个字符")
    @Column(name = "company_video", length = 500)
    private String companyVideo;

    /**
     * 成立日期
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式不正确，应为YYYY-MM-DD")
    @Column(name = "establish_date", length = 10)
    private String establishDate;

    /**
     * 注册资本
     */
    @Size(max = 50, message = "注册资本长度不能超过50个字符")
    @Column(name = "registered_capital", length = 50)
    private String registeredCapital;

    /**
     * 法定代表人
     */
    @Size(max = 100, message = "法定代表人长度不能超过100个字符")
    @Column(name = "legal_representative", length = 100)
    private String legalRepresentative;

    /**
     * 经营范围
     */
    @Size(max = 2000, message = "经营范围长度不能超过2000个字符")
    @Column(name = "business_scope", columnDefinition = "TEXT")
    private String businessScope;

    /**
     * 公司地址
     */
    @Size(max = 500, message = "公司地址长度不能超过500个字符")
    @Column(name = "company_address", length = 500)
    private String companyAddress;

    /**
     * 公司网站
     */
    @Size(max = 200, message = "网站长度不能超过200个字符")
    @Column(name = "company_website", length = 200)
    private String companyWebsite;

    /**
     * 公司电话
     */
    @Size(max = 50, message = "电话长度不能超过50个字符")
    @Column(name = "company_phone", length = 50)
    private String companyPhone;

    /**
     * 公司邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Column(name = "company_email", length = 100)
    private String companyEmail;

    /**
     * 状态：0-禁用 1-启用
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    @Column(name = "status", nullable = false)
    private Integer status = 1;

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
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}