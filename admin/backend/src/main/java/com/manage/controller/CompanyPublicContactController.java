package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.company.CompanyContactResponse;
import com.manage.dto.company.CompanyInfoResponse;
import com.manage.service.CompanyContactService;
import com.manage.service.CompanyInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 公司信息公共接口控制器（无需认证）- 匹配前端调用路径
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/company/public")
@RequiredArgsConstructor
public class CompanyPublicContactController {

    private final CompanyContactService companyContactService;
    private final CompanyInfoService companyInfoService;

    /**
     * 获取WhatsApp联系方式
     *
     * @return WhatsApp联系方式
     */
    @GetMapping("/contact/whatsapp")
    public Result<String> getWhatsAppContact() {
        try {
            CompanyContactResponse contact = companyContactService.getContact();
            return Result.success(contact.getWhatsapp());
        } catch (Exception e) {
            log.error("获取WhatsApp联系方式失败：{}", e.getMessage());
            return Result.error("获取WhatsApp联系方式失败：" + e.getMessage());
        }
    }

    /**
     * 获取WhatsApp聊天链接
     *
     * @return WhatsApp聊天链接
     */
    @GetMapping("/contact/whatsapp/link")
    public Result<String> getWhatsAppLink() {
        try {
            CompanyContactResponse contact = companyContactService.getContact();
            String whatsappNumber = contact.getWhatsapp();

            if (whatsappNumber == null || whatsappNumber.trim().isEmpty()) {
                return Result.success("");
            }

            // 清理号码，移除+86、空格、横线等字符，只保留数字
            String cleanNumber = whatsappNumber.replaceAll("[^0-9]", "");

            // 生成WhatsApp聊天链接
            String whatsappUrl = "https://wa.me/" + cleanNumber;

            return Result.success(whatsappUrl);
        } catch (Exception e) {
            log.error("获取WhatsApp聊天链接失败：{}", e.getMessage());
            return Result.error("获取WhatsApp聊天链接失败：" + e.getMessage());
        }
    }

    /**
     * 获取联系方式
     *
     * @return 联系方式
     */
    @GetMapping("/contact")
    public Result<CompanyContactResponse> getContact() {
        try {
            CompanyContactResponse response = companyContactService.getContact();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取联系方式失败：{}", e.getMessage());
            return Result.error("获取联系方式失败：" + e.getMessage());
        }
    }

    /**
     * 获取公司Logo
     *
     * @return 公司Logo URL
     */
    @GetMapping("/logo")
    public Result<String> getCompanyLogo() {
        try {
            CompanyInfoResponse companyInfo = companyInfoService.getCompanyInfo();
            return Result.success(companyInfo.getCompanyLogo());
        } catch (Exception e) {
            log.error("获取公司Logo失败：{}", e.getMessage());
            return Result.error("获取公司Logo失败：" + e.getMessage());
        }
    }

    /**
     * 获取公司Logo图片（直接返回文件内容）
     *
     * @return Logo图片文件
     */
    @GetMapping("/logo/image")
    public ResponseEntity<org.springframework.core.io.Resource> getCompanyLogoImage() {
        try {
            CompanyInfoResponse companyInfo = companyInfoService.getCompanyInfo();
            String logoUrl = companyInfo.getCompanyLogo();

            if (logoUrl == null || logoUrl.trim().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // 从URL中提取文件路径
            String fileName = extractFileNameFromUrl(logoUrl);
            if (fileName == null) {
                return ResponseEntity.notFound().build();
            }

            // 构建文件完整路径
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads", fileName);
            java.io.File file = uploadPath.toFile();

            if (!file.exists() || !file.isFile()) {
                log.warn("Logo文件不存在：{}", uploadPath);
                return ResponseEntity.notFound().build();
            }

            // 获取文件MIME类型
            String contentType = java.nio.file.Files.probeContentType(uploadPath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 创建文件资源
            org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(file);

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .header(org.springframework.http.HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                    .body(resource);

        } catch (Exception e) {
            log.error("获取公司Logo图片失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 从URL中提取文件名
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        try {
            // 移除查询参数和锚点
            String normalizedUrl = fileUrl;
            int queryIndex = normalizedUrl.indexOf('?');
            if (queryIndex != -1) {
                normalizedUrl = normalizedUrl.substring(0, queryIndex);
            }
            int anchorIndex = normalizedUrl.indexOf('#');
            if (anchorIndex != -1) {
                normalizedUrl = normalizedUrl.substring(0, anchorIndex);
            }

            // 移除域名部分，只保留路径
            String basePath = "/api/files/";
            if (normalizedUrl.contains(basePath)) {
                int startIndex = normalizedUrl.indexOf(basePath);
                return normalizedUrl.substring(startIndex + basePath.length());
            }

            return null;
        } catch (Exception e) {
            log.warn("从URL提取文件名失败：{}", fileUrl, e);
            return null;
        }
    }
}