package com.manage.dto.company;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * 公司联系方式请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyContactRequest {

    /**
     * 地址（中文）
     */
    @Size(max = 500, message = "中文地址长度不能超过500个字符")
    private String addressCn;

    /**
     * 地址（英文）
     */
    @Size(max = 500, message = "英文地址长度不能超过500个字符")
    private String addressEn;

    /**
     * 电话
     */
    @Size(max = 50, message = "电话长度不能超过50个字符")
    private String phone;

    /**
     * 传真
     */
    @Size(max = 50, message = "传真长度不能超过50个字符")
    private String fax;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 官网地址
     */
    @Size(max = 200, message = "官网地址长度不能超过200个字符")
    private String websiteUrl;

    /**
     * 联系人（中文）
     */
    @Size(max = 100, message = "中文联系人姓名长度不能超过100个字符")
    private String contactPersonCn;

    /**
     * 联系人（英文）
     */
    @Size(max = 100, message = "英文联系人姓名长度不能超过100个字符")
    private String contactPersonEn;

    /**
     * 微信号
     */
    @Size(max = 100, message = "微信号长度不能超过100个字符")
    private String wechat;

    /**
     * WhatsApp账号
     */
    @Size(max = 50, message = "WhatsApp账号长度不能超过50个字符")
    private String whatsapp;

    /**
     * QQ号
     */
    @Size(max = 50, message = "QQ号长度不能超过50个字符")
    private String qq;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}