package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.company.CompanyContactRequest;
import com.manage.dto.company.CompanyContactResponse;
import com.manage.entity.User;
import com.manage.service.CompanyContactService;
import com.manage.service.OperationLogService;
import com.manage.service.UserService;
import com.manage.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 公司联系方式管理控制器
 * 提供联系方式的管理功能，包括WhatsApp等社交联系方式
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/company/admin/contact")
@Validated
public class CompanyContactController {

    private final CompanyContactService companyContactService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final OperationLogService operationLogService;

    public CompanyContactController(CompanyContactService companyContactService,
                                  JwtUtil jwtUtil,
                                  UserService userService,
                                  OperationLogService operationLogService) {
        this.companyContactService = companyContactService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.operationLogService = operationLogService;
    }

    /**
     * 获取联系方式（管理接口）
     */
    @GetMapping("/info")
    public Result<CompanyContactResponse> getContactInfo() {
        try {
            CompanyContactResponse response = companyContactService.getContactForAdmin();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取联系方式失败：{}", e.getMessage());
            return Result.error("获取联系方式失败：" + e.getMessage());
        }
    }

    /**
     * 更新联系方式
     */
    @PutMapping("/info")
    public Result<CompanyContactResponse> updateContactInfo(@Valid @RequestBody CompanyContactRequest request,
                                                          HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromAuthentication(httpRequest);
            log.info("用户 {} 更新联系方式", userId);

            CompanyContactResponse response = companyContactService.updateContact(request, userId);

            // 记录操作日志
            operationLogService.log(
                "UPDATE",
                "CONTACT",
                "更新了公司联系方式，WhatsApp: " + request.getWhatsapp(),
                "CompanyContact",
                response.getId(),
                "联系方式更新"
            );

            return Result.success("联系方式更新成功", response);
        } catch (Exception e) {
            log.error("更新联系方式失败：{}", e.getMessage(), e);
            return Result.error("更新联系方式失败：" + e.getMessage());
        }
    }

    /**
     * 获取WhatsApp联系方式（专门用于前端跳转）
     */
    @GetMapping("/whatsapp")
    public Result<Map<String, Object>> getWhatsAppInfo() {
        try {
            CompanyContactResponse contact = companyContactService.getContactForAdmin();
            String whatsappNumber = contact.getWhatsapp();

            Map<String, Object> result = new HashMap<>();
            result.put("whatsapp", whatsappNumber);

            // 生成WhatsApp聊天链接
            if (whatsappNumber != null && !whatsappNumber.trim().isEmpty()) {
                // 清理号码，移除+86、空格、横线等字符
                String cleanNumber = whatsappNumber.replaceAll("[^0-9]", "");
                String whatsappUrl = "https://wa.me/" + cleanNumber;
                result.put("whatsappUrl", whatsappUrl);
            } else {
                result.put("whatsappUrl", "");
            }

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取WhatsApp信息失败：{}", e.getMessage());
            return Result.error("获取WhatsApp信息失败：" + e.getMessage());
        }
    }

    /**
     * 检查联系方式是否存在
     */
    @GetMapping("/info/check")
    public Result<Map<String, Object>> checkContactExists() {
        try {
            boolean exists = companyContactService.existsContact();
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            return Result.success(result);
        } catch (Exception e) {
            log.error("检查联系方式是否存在失败：{}", e.getMessage());
            return Result.error("检查失败：" + e.getMessage());
        }
    }

    /**
     * 从认证信息中获取用户ID
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getUserIdFromAuthentication(HttpServletRequest request) {
        // 1. 尝试从JWT Token获取
        try {
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);
                Long userIdFromToken = jwtUtil.getUserIdFromToken(token);
                if (userIdFromToken != null) {
                    return userIdFromToken;
                }
            }
        } catch (Exception e) {
            log.warn("无法从token中解析用户ID: {}", e.getMessage());
        }

        // 2. 尝试从SecurityContextHolder获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String username = null;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                username = (String) principal;
            }

            if (username != null) {
                try {
                    User user = userService.findByUsername(username);
                    return user.getId();
                } catch (Exception e) {
                    log.error("无法根据用户名 '{}' 从 SecurityContext 获取用户ID: {}", username, e.getMessage());
                    throw new RuntimeException("无法确定操作用户ID", e);
                }
            }
        }

        log.error("无法从请求或SecurityContext中获取用户ID");
        throw new RuntimeException("无法确定操作用户ID");
    }
}