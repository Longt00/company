package com.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 公司联系方式实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "company_contact")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyContact {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 地址（中文）
     */
    @Size(max = 500, message = "中文地址长度不能超过500个字符")
    @Column(name = "address_cn", length = 500)
    private String addressCn;

    /**
     * 地址（英文）
     */
    @Size(max = 500, message = "英文地址长度不能超过500个字符")
    @Column(name = "address_en", length = 500)
    private String addressEn;

    /**
     * 电话
     */
    @Size(max = 50, message = "电话长度不能超过50个字符")
    @Column(name = "phone", length = 50)
    private String phone;

    /**
     * 传真
     */
    @Size(max = 50, message = "传真长度不能超过50个字符")
    @Column(name = "fax", length = 50)
    private String fax;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 官网地址
     */
    @Size(max = 200, message = "官网地址长度不能超过200个字符")
    @Column(name = "website_url", length = 200)
    private String websiteUrl;

    /**
     * 联系人（中文）
     */
    @Size(max = 100, message = "中文联系人姓名长度不能超过100个字符")
    @Column(name = "contact_person_cn", length = 100)
    private String contactPersonCn;

    /**
     * 联系人（英文）
     */
    @Size(max = 100, message = "英文联系人姓名长度不能超过100个字符")
    @Column(name = "contact_person_en", length = 100)
    private String contactPersonEn;

    /**
     * 微信号
     */
    @Size(max = 100, message = "微信号长度不能超过100个字符")
    @Column(name = "wechat", length = 100)
    private String wechat;

    /**
     * WhatsApp账号
     */
    @Size(max = 50, message = "WhatsApp账号长度不能超过50个字符")
    @Column(name = "whatsapp", length = 50)
    private String whatsapp;

    /**
     * QQ号
     */
    @Size(max = 50, message = "QQ号长度不能超过50个字符")
    @Column(name = "qq", length = 50)
    private String qq;

    /**
     * 状态：0-禁用 1-启用
     */
    @Column(name = "status", nullable = false)
    private Integer status;

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