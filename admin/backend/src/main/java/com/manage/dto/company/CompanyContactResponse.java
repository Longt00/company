package com.manage.dto.company;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公司联系方式响应DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyContactResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 地址（中文）
     */
    private String addressCn;

    /**
     * 地址（英文）
     */
    private String addressEn;

    /**
     * 电话
     */
    private String phone;

    /**
     * 传真
     */
    private String fax;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 官网地址
     */
    private String websiteUrl;

    /**
     * 联系人（中文）
     */
    private String contactPersonCn;

    /**
     * 联系人（英文）
     */
    private String contactPersonEn;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * WhatsApp账号
     */
    private String whatsapp;

    /**
     * QQ号
     */
    private String qq;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}