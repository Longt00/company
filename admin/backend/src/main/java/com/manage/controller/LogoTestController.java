package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.company.CompanyInfoResponse;
import com.manage.entity.CompanyInfo;
import com.manage.repository.CompanyInfoRepository;
import com.manage.service.CompanyInfoService;
import com.manage.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Logo功能测试控制器（仅用于开发和测试）
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/public/test/logo")
@RequiredArgsConstructor
public class LogoTestController {

    private final FileUploadService fileUploadService;
    private final CompanyInfoService companyInfoService;
    private final CompanyInfoRepository companyInfoRepository;

    /**
     * 获取当前Logo URL
     */
    @GetMapping("/current")
    public Result<String> getCurrentLogo() {
        try {
            CompanyInfoResponse companyInfo = companyInfoService.getCompanyInfo();
            String logoUrl = companyInfo.getCompanyLogo();
            log.info("获取当前Logo URL：{}", logoUrl);
            return Result.success("获取当前Logo成功", logoUrl);
        } catch (Exception e) {
            log.error("获取当前Logo失败：{}", e.getMessage(), e);
            return Result.error("获取当前Logo失败：" + e.getMessage());
        }
    }

    /**
     * 直接设置Logo URL（模拟文件上传后的数据库更新）
     */
    @PostMapping("/set-url")
    public Result<CompanyInfoResponse> setLogoUrl(@RequestParam String logoUrl) {
        try {
            log.info("直接设置Logo URL：{}", logoUrl);

            // 获取当前公司信息
            CompanyInfo companyInfo = companyInfoRepository.findFirstByOrderByIdDesc()
                    .orElseThrow(() -> new RuntimeException("公司信息不存在"));

            // 更新Logo URL
            companyInfo.setCompanyLogo(logoUrl);
            companyInfo.setUpdatedBy(1L); // 使用测试用户ID

            // 保存更新
            CompanyInfo savedCompanyInfo = companyInfoRepository.save(companyInfo);

            log.info("Logo URL设置成功：{}", logoUrl);
            return Result.success("Logo URL设置成功", convertToResponse(savedCompanyInfo));
        } catch (Exception e) {
            log.error("设置Logo URL失败：{}", e.getMessage(), e);
            return Result.error("设置Logo URL失败：" + e.getMessage());
        }
    }

    /**
     * 测试Logo文件上传（仅用于测试，不实际上传文件）
     */
    @PostMapping("/upload-test")
    public Result<Map<String, Object>> testLogoUpload() {
        try {
            log.info("测试Logo上传流程");

            // 模拟一个测试Logo URL
            String testLogoUrl = "http://localhost:33380/api/files/logo/test/test-logo-" + System.currentTimeMillis() + ".png";

            // 获取当前公司信息并更新Logo
            CompanyInfo companyInfo = companyInfoRepository.findFirstByOrderByIdDesc()
                    .orElseThrow(() -> new RuntimeException("公司信息不存在"));

            String oldLogoUrl = companyInfo.getCompanyLogo();
            companyInfo.setCompanyLogo(testLogoUrl);
            companyInfo.setUpdatedBy(1L);

            // 保存更新
            CompanyInfo savedCompanyInfo = companyInfoRepository.save(companyInfo);

            Map<String, Object> result = new HashMap<>();
            result.put("oldLogoUrl", oldLogoUrl);
            result.put("newLogoUrl", testLogoUrl);
            result.put("success", true);
            result.put("companyInfo", convertToResponse(savedCompanyInfo));

            log.info("Logo上传测试成功，新URL：{}", testLogoUrl);
            return Result.success("Logo上传测试成功", result);
        } catch (Exception e) {
            log.error("Logo上传测试失败：{}", e.getMessage(), e);
            return Result.error("Logo上传测试失败：" + e.getMessage());
        }
    }

    /**
     * 恢复原始Logo URL
     */
    @PostMapping("/restore")
    public Result<CompanyInfoResponse> restoreOriginalLogo() {
        try {
            log.info("恢复原始Logo URL");

            // 恢复原始Logo URL
            String originalLogoUrl = "http://39.97.60.191:33380/api/files/logo/2025/11/29/dc085dc26dd646d6bab8370d6ce698a4.png";

            CompanyInfo companyInfo = companyInfoRepository.findFirstByOrderByIdDesc()
                    .orElseThrow(() -> new RuntimeException("公司信息不存在"));

            companyInfo.setCompanyLogo(originalLogoUrl);
            companyInfo.setUpdatedBy(1L);

            CompanyInfo savedCompanyInfo = companyInfoRepository.save(companyInfo);

            log.info("原始Logo URL恢复成功");
            return Result.success("原始Logo URL恢复成功", convertToResponse(savedCompanyInfo));
        } catch (Exception e) {
            log.error("恢复原始Logo URL失败：{}", e.getMessage(), e);
            return Result.error("恢复原始Logo URL失败：" + e.getMessage());
        }
    }

    /**
     * 验证Logo数据库字段状态
     */
    @GetMapping("/verify-db")
    public Result<Map<String, Object>> verifyDatabaseField() {
        try {
            CompanyInfo companyInfo = companyInfoRepository.findFirstByOrderByIdDesc()
                    .orElseThrow(() -> new RuntimeException("公司信息不存在"));

            Map<String, Object> result = new HashMap<>();
            result.put("id", companyInfo.getId());
            result.put("companyName", companyInfo.getCompanyName());
            result.put("logoUrl", companyInfo.getCompanyLogo());
            result.put("logoUrlLength", companyInfo.getCompanyLogo() != null ? companyInfo.getCompanyLogo().length() : 0);
            result.put("updateTime", companyInfo.getUpdateTime());
            result.put("updatedBy", companyInfo.getUpdatedBy());

            log.info("Logo数据库字段验证完成");
            return Result.success("数据库字段验证完成", result);
        } catch (Exception e) {
            log.error("数据库字段验证失败：{}", e.getMessage(), e);
            return Result.error("数据库字段验证失败：" + e.getMessage());
        }
    }

    /**
     * 将CompanyInfo实体转换为响应DTO
     */
    private com.manage.dto.company.CompanyInfoResponse convertToResponse(CompanyInfo companyInfo) {
        com.manage.dto.company.CompanyInfoResponse response = new com.manage.dto.company.CompanyInfoResponse();
        BeanUtils.copyProperties(companyInfo, response);
        return response;
    }
}