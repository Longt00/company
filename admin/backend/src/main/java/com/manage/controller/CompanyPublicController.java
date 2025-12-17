package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.company.*;
import com.manage.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公司信息公共接口控制器（无需认证）
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/public/company")
@RequiredArgsConstructor
public class CompanyPublicController {

    private final CompanyInfoService companyInfoService;
    private final CompanyContactService companyContactService;
    private final CompanyQualificationService companyQualificationService;
    private final CompanyTeamService companyTeamService;

    /**
     * 获取公司基本信息
     *
     * @return 公司基本信息
     */
    @GetMapping("/info")
    public Result<CompanyInfoResponse> getCompanyInfo() {
        try {
            CompanyInfoResponse response = companyInfoService.getCompanyInfo();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取公司基本信息失败：{}", e.getMessage());
            return Result.error("获取公司信息失败：" + e.getMessage());
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
     * 获取所有有效的资质文件
     *
     * @return 资质文件列表
     */
    @GetMapping("/qualifications")
    public Result<List<CompanyQualificationResponse>> getQualifications() {
        try {
            List<CompanyQualificationResponse> response = companyQualificationService.getActiveQualifications();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取资质文件失败：{}", e.getMessage());
            return Result.error("获取资质文件失败：" + e.getMessage());
        }
    }

    /**
     * 根据类型获取资质文件
     *
     * @param type 资质类型
     * @return 资质文件列表
     */
    @GetMapping("/qualifications/type/{type}")
    public Result<List<CompanyQualificationResponse>> getQualificationsByType(@PathVariable String type) {
        try {
            List<CompanyQualificationResponse> response = companyQualificationService.getQualificationsByType(type);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据类型获取资质文件失败：{}", e.getMessage());
            return Result.error("获取资质文件失败：" + e.getMessage());
        }
    }

    /**
     * 获取核心团队成员
     *
     * @return 核心成员列表
     */
    @GetMapping("/team/core")
    public Result<List<CompanyTeamResponse>> getCoreTeamMembers() {
        try {
            List<CompanyTeamResponse> response = companyTeamService.getCoreMembers();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取核心团队成员失败：{}", e.getMessage());
            return Result.error("获取团队成员失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有团队成员
     *
     * @return 团队成员列表
     */
    @GetMapping("/team")
    public Result<List<CompanyTeamResponse>> getTeamMembers() {
        try {
            List<CompanyTeamResponse> response = companyTeamService.getActiveTeamMembers();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取团队成员失败：{}", e.getMessage());
            return Result.error("获取团队成员失败：" + e.getMessage());
        }
    }

    /**
     * 根据部门获取团队成员
     *
     * @param department 部门名称
     * @return 团队成员列表
     */
    @GetMapping("/team/department/{department}")
    public Result<List<CompanyTeamResponse>> getTeamMembersByDepartment(@PathVariable String department) {
        try {
            List<CompanyTeamResponse> response = companyTeamService.getTeamMembersByDepartment(department);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据部门获取团队成员失败：{}", e.getMessage());
            return Result.error("获取团队成员失败：" + e.getMessage());
        }
    }

    /**
     * 获取营业执照
     *
     * @return 营业执照
     */
    @GetMapping("/qualification/business-license")
    public Result<List<CompanyQualificationResponse>> getBusinessLicense() {
        try {
            List<CompanyQualificationResponse> response = companyQualificationService
                    .getQualificationsByType("business_license");
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取营业执照失败：{}", e.getMessage());
            return Result.error("获取营业执照失败：" + e.getMessage());
        }
    }

    /**
     * 获取认证证书
     *
     * @return 认证证书列表
     */
    @GetMapping("/qualification/certificates")
    public Result<List<CompanyQualificationResponse>> getCertificates() {
        try {
            List<CompanyQualificationResponse> response = companyQualificationService
                    .getQualificationsByType("iso");
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取认证证书失败：{}", e.getMessage());
            return Result.error("获取认证证书失败：" + e.getMessage());
        }
    }

    /**
     * 获取荣誉证书
     *
     * @return 荣誉证书列表
     */
    @GetMapping("/qualification/honors")
    public Result<List<CompanyQualificationResponse>> getHonors() {
        try {
            List<CompanyQualificationResponse> response = companyQualificationService
                    .getQualificationsByType("honor");
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取荣誉证书失败：{}", e.getMessage());
            return Result.error("获取荣誉证书失败：" + e.getMessage());
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
}