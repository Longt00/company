package com.manage.dto.company;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * WhatsApp联系方式修改请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class CompanyWhatsAppUpdateRequest {

    /**
     * WhatsApp账号
     */
    @Size(max = 50, message = "WhatsApp账号长度不能超过50个字符")
    private String whatsapp;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}