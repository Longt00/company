package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.company.CompanyContactRequest;
import com.manage.dto.company.CompanyContactResponse;
import com.manage.service.CompanyContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * WhatsApp功能测试控制器（仅用于开发和测试）
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/public/test/whatsapp")
@RequiredArgsConstructor
public class WhatsAppTestController {

    private final CompanyContactService companyContactService;

    /**
     * 测试WhatsApp更新功能
     */
    @PostMapping("/update")
    public Result<CompanyContactResponse> testUpdateWhatsApp(@RequestParam String whatsapp) {
        try {
            log.info("测试WhatsApp更新功能，新值：{}", whatsapp);

            // 首先获取当前联系方式数据
            CompanyContactResponse currentContact = companyContactService.getContact();

            // 创建更新请求，保留现有数据，只更新WhatsApp字段
            CompanyContactRequest request = new CompanyContactRequest();
            request.setAddressCn(currentContact.getAddressCn());
            request.setAddressEn(currentContact.getAddressEn());
            request.setPhone(currentContact.getPhone());
            request.setFax(currentContact.getFax());
            request.setEmail(currentContact.getEmail());
            request.setWebsiteUrl(currentContact.getWebsiteUrl());
            request.setContactPersonCn(currentContact.getContactPersonCn());
            request.setContactPersonEn(currentContact.getContactPersonEn());
            request.setWechat(currentContact.getWechat());
            request.setQq(currentContact.getQq());
            request.setWhatsapp(whatsapp); // 只更新WhatsApp
            request.setStatus(currentContact.getStatus());

            // 使用默认用户ID进行更新（仅用于测试）
            Long testUserId = 1L;
            CompanyContactResponse response = companyContactService.updateContact(request, testUserId);

            log.info("WhatsApp测试更新成功，新值：{}", whatsapp);
            return Result.success("WhatsApp更新测试成功", response);
        } catch (Exception e) {
            log.error("WhatsApp测试更新失败：{}", e.getMessage(), e);
            return Result.error("WhatsApp测试更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前WhatsApp值
     */
    @GetMapping("/current")
    public Result<String> getCurrentWhatsApp() {
        try {
            CompanyContactResponse contact = companyContactService.getContact();
            String whatsapp = contact.getWhatsapp();
            log.info("获取当前WhatsApp值：{}", whatsapp);
            return Result.success("获取当前WhatsApp值成功", whatsapp);
        } catch (Exception e) {
            log.error("获取当前WhatsApp值失败：{}", e.getMessage(), e);
            return Result.error("获取当前WhatsApp值失败：" + e.getMessage());
        }
    }

    /**
     * 测试WhatsApp链接生成
     */
    @GetMapping("/link")
    public Result<String> getWhatsAppLink() {
        try {
            CompanyContactResponse contact = companyContactService.getContact();
            String whatsappNumber = contact.getWhatsapp();

            if (whatsappNumber == null || whatsappNumber.trim().isEmpty()) {
                return Result.success("WhatsApp号码为空", "");
            }

            // 清理号码，移除+86、空格、横线等字符，只保留数字
            String cleanNumber = whatsappNumber.replaceAll("[^0-9]", "");

            // 生成WhatsApp聊天链接
            String whatsappUrl = "https://wa.me/" + cleanNumber;

            log.info("WhatsApp号码：{}，生成的链接：{}", whatsappNumber, whatsappUrl);
            return Result.success("WhatsApp链接生成成功", whatsappUrl);
        } catch (Exception e) {
            log.error("生成WhatsApp链接失败：{}", e.getMessage(), e);
            return Result.error("生成WhatsApp链接失败：" + e.getMessage());
        }
    }
}