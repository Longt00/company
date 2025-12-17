package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.company.CompanyInfoRequest;
import com.manage.dto.company.CompanyInfoResponse;
import com.manage.dto.company.CompanyInfoPartialUpdateRequest;
import com.manage.dto.company.CompanyWhatsAppUpdateRequest;
import com.manage.entity.User;
import com.manage.service.CompanyInfoService;
import com.manage.service.CompanyContactService;
import com.manage.service.OperationLogService;
import com.manage.service.UserService;
import com.manage.service.FileUploadService;
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
 * 公司信息管理控制器
 * 提供单一公司模式的信息管理，包括初始化、获取、更新等功能
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/company")
@Validated
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;
    private final CompanyContactService companyContactService;
    private final FileUploadService fileUploadService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final OperationLogService operationLogService;

    // 使用构造函数注入所有依赖
    public CompanyInfoController(CompanyInfoService companyInfoService, CompanyContactService companyContactService, FileUploadService fileUploadService, JwtUtil jwtUtil, UserService userService, OperationLogService operationLogService) {
        this.companyInfoService = companyInfoService;
        this.companyContactService = companyContactService;
        this.fileUploadService = fileUploadService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.operationLogService = operationLogService;
    }

    /**
     * 获取公司基本信息（公开接口）
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
     * 获取公司基本信息（管理接口）
     */
    @GetMapping("/admin/info")
    public Result<CompanyInfoResponse> getCompanyInfoForAdmin() {
        try {
            CompanyInfoResponse response = companyInfoService.getCompanyInfoForAdmin();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取公司基本信息失败：{}", e.getMessage());
            return Result.error("获取公司信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新公司基本信息（更新最新记录）
     */
    @PutMapping("/admin/info")
    public Result<CompanyInfoResponse> updateCompanyInfo(@Valid @RequestBody CompanyInfoRequest request,
                                                        HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromAuthentication(httpRequest); // 使用更新后的方法获取UserID

            CompanyInfoResponse response = companyInfoService.updateCompanyInfo(request, userId);

            // 记录操作日志
            operationLogService.log(
                "UPDATE",
                "COMPANY",
                "更新了公司信息: " + request.getCompanyName(),
                "CompanyInfo",
                response.getId(),
                request.getCompanyName()
            );

            return Result.success("公司信息更新成功", response);
        } catch (Exception e) {
            log.error("更新公司基本信息失败：{}", e.getMessage(), e); // 建议打印堆栈信息
            return Result.error("更新公司信息失败：" + e.getMessage());
        }
    }

    /**
     * 修改自身公司信息（简化接口）
     * 用于公司管理员修改自己的公司信息，更加用户友好
     */
    @PutMapping("/admin/update")
    public Result<CompanyInfoResponse> updateSelfCompanyInfo(@Valid @RequestBody CompanyInfoRequest request,
                                                          HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromAuthentication(httpRequest);
            log.info("用户 {} 修改公司信息", userId);

            CompanyInfoResponse response = companyInfoService.updateCompanyInfo(request, userId);

            // 记录操作日志
            operationLogService.log(
                "UPDATE",
                "COMPANY",
                "修改了公司信息: " + request.getCompanyName(),
                "CompanyInfo",
                response.getId(),
                request.getCompanyName()
            );

            return Result.success("公司信息修改成功", response);
        } catch (Exception e) {
            log.error("修改公司信息失败：{}", e.getMessage(), e);
            return Result.error("修改公司信息失败：" + e.getMessage());
        }
    }

    /**
     * 部分更新公司信息
     * 用于只更新部分字段，不要求提供完整信息
     */
    @PatchMapping("/admin/info")
    public Result<CompanyInfoResponse> partialUpdateCompanyInfo(@RequestBody CompanyInfoPartialUpdateRequest request,
                                                           HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromAuthentication(httpRequest);
            log.info("用户 {} 部分更新公司信息", userId);

            CompanyInfoResponse response = companyInfoService.partialUpdateCompanyInfo(request, userId);
            return Result.success("公司信息部分更新成功", response);
        } catch (Exception e) {
            log.error("部分更新公司信息失败：{}", e.getMessage(), e);
            return Result.error("部分更新公司信息失败：" + e.getMessage());
        }
    }

    /**
     * 初始化公司信息
     */
    @PostMapping("/admin/info/init")
    public Result<CompanyInfoResponse> initCompanyInfo(@Valid @RequestBody CompanyInfoRequest request,
                                                       HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromAuthentication(httpRequest); // 使用更新后的方法获取UserID

            CompanyInfoResponse response = companyInfoService.initCompanyInfo(request, userId);
            return Result.success("公司信息初始化成功", response);
        } catch (Exception e) {
            log.error("初始化公司信息失败：{}", e.getMessage(), e); // 建议打印堆栈信息
            return Result.error("初始化公司信息失败：" + e.getMessage());
        }
    }

    /**
     * 检查公司信息是否存在
     */
    @GetMapping("/admin/info/check")
    public Result<Map<String, Object>> checkCompanyInfoExists() {
        try {
            boolean exists = companyInfoService.existsCompanyInfo();
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            return Result.success(result);
        } catch (Exception e) {
            log.error("检查公司信息是否存在失败：{}", e.getMessage());
            return Result.error("检查失败：" + e.getMessage());
        }
    }

    /**
     * 修改WhatsApp联系方式
     */
    @PutMapping("/admin/contact/whatsapp")
    public Result<Map<String, Object>> updateWhatsApp(@Valid @RequestBody CompanyWhatsAppUpdateRequest request,
                                                      HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromAuthentication(httpRequest);
            log.info("用户 {} 修改WhatsApp联系方式", userId);

            // 验证输入
            if (request.getWhatsapp() != null && request.getWhatsapp().trim().isEmpty()) {
                return Result.error("WhatsApp账号不能为空字符串");
            }

            // 获取现有联系方式
            com.manage.dto.company.CompanyContactResponse existingContact = companyContactService.getContactForAdmin();

            // 构建更新请求
            com.manage.dto.company.CompanyContactRequest updateRequest = new com.manage.dto.company.CompanyContactRequest();
            updateRequest.setWhatsapp(request.getWhatsapp());

            // 如果提供了状态参数，则更新状态；否则保持原有状态
            if (request.getStatus() != null) {
                updateRequest.setStatus(request.getStatus());
            } else {
                updateRequest.setStatus(existingContact.getStatus());
            }

            // 其他字段保持不变
            updateRequest.setAddressCn(existingContact.getAddressCn());
            updateRequest.setAddressEn(existingContact.getAddressEn());
            updateRequest.setPhone(existingContact.getPhone());
            updateRequest.setFax(existingContact.getFax());
            updateRequest.setEmail(existingContact.getEmail());
            updateRequest.setWebsiteUrl(existingContact.getWebsiteUrl());
            updateRequest.setContactPersonCn(existingContact.getContactPersonCn());
            updateRequest.setContactPersonEn(existingContact.getContactPersonEn());
            updateRequest.setWechat(existingContact.getWechat());
            updateRequest.setQq(existingContact.getQq());

            // 更新联系方式
            com.manage.dto.company.CompanyContactResponse response = companyContactService.updateContact(updateRequest, userId);

            // 记录操作日志
            String operationDetail = "修改了WhatsApp联系方式: " + (request.getWhatsapp() != null ? request.getWhatsapp() : "清空WhatsApp");
            operationLogService.log(
                "UPDATE",
                "COMPANY_CONTACT",
                operationDetail,
                "CompanyContact",
                response.getId(),
                "WhatsApp联系方式修改"
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("whatsapp", response.getWhatsapp());
            result.put("message", "WhatsApp联系方式修改成功");
            result.put("updateTime", response.getUpdateTime());

            return Result.success("WhatsApp联系方式修改成功", result);
        } catch (Exception e) {
            log.error("修改WhatsApp联系方式失败：{}", e.getMessage(), e);
            return Result.error("修改WhatsApp联系方式失败：" + e.getMessage());
        }
    }

    /**
     * 获取WhatsApp联系方式（公共接口）
     */
    @GetMapping("/public/contact/whatsapp")
    public Result<Map<String, Object>> getPublicWhatsApp() {
        try {
            com.manage.dto.company.CompanyContactResponse contact = companyContactService.getContact();

            Map<String, Object> result = new HashMap<>();
            result.put("whatsapp", contact.getWhatsapp());
            result.put("hasWhatsApp", contact.getWhatsapp() != null && !contact.getWhatsapp().trim().isEmpty());

            return Result.success("获取WhatsApp联系方式成功", result);
        } catch (Exception e) {
            log.error("获取WhatsApp联系方式失败：{}", e.getMessage());
            return Result.error("获取WhatsApp联系方式失败：" + e.getMessage());
        }
    }

    /**
     * 获取公司Logo（公共接口）
     */
    @GetMapping("/public/logo")
    public Result<Map<String, Object>> getPublicLogo() {
        try {
            CompanyInfoResponse companyInfo = companyInfoService.getCompanyInfo();

            Map<String, Object> result = new HashMap<>();
            result.put("logoUrl", companyInfo.getCompanyLogo());
            result.put("hasLogo", companyInfo.getCompanyLogo() != null && !companyInfo.getCompanyLogo().trim().isEmpty());

            // 如果有Logo，提供额外信息
            if (result.get("hasLogo").equals(true)) {
                Map<String, Object> logoInfo = new HashMap<>();
                logoInfo.put("fileName", getFileNameFromUrl(companyInfo.getCompanyLogo()));
                logoInfo.put("uploadTime", companyInfo.getUpdateTime());
                result.put("logoInfo", logoInfo);
            } else {
                result.put("logoInfo", null);
            }

            return Result.success("获取Logo成功", result);
        } catch (Exception e) {
            log.error("获取公司Logo失败：{}", e.getMessage());
            return Result.error("获取公司Logo失败：" + e.getMessage());
        }
    }

    /**
     * 更换公司Logo
     */
    @PutMapping("/admin/logo")
    public Result<Map<String, Object>> updateCompanyLogo(@RequestParam("logoUrl") String logoUrl,
                                                         HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromAuthentication(httpRequest);
            log.info("用户 {} 更换公司Logo，Logo URL：{}", userId, logoUrl);

            if (logoUrl == null || logoUrl.trim().isEmpty()) {
                return Result.error("Logo URL不能为空");
            }

            // 更新Logo
            CompanyInfoResponse response = companyInfoService.updateCompanyLogo(logoUrl, userId);

            // 记录操作日志
            operationLogService.log(
                "UPDATE",
                "COMPANY",
                "更换了公司Logo: " + logoUrl,
                "CompanyInfo",
                response.getId(),
                "公司Logo更换"
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("logoUrl", response.getCompanyLogo());
            result.put("message", "公司Logo更换成功");
            result.put("updateTime", response.getUpdateTime());

            return Result.success("公司Logo更换成功", result);
        } catch (Exception e) {
            log.error("更换公司Logo失败：{}", e.getMessage(), e);
            return Result.error("更换公司Logo失败：" + e.getMessage());
        }
    }

    /**
     * 上传并更换公司Logo（一体化接口）
     */
    @PostMapping("/admin/logo/upload")
    public Result<Map<String, Object>> uploadAndUpdateCompanyLogo(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                                                   HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromAuthentication(httpRequest);
            log.info("用户 {} 上传并更换公司Logo，文件：{}", userId, file.getOriginalFilename());

            if (file.isEmpty()) {
                return Result.error("请选择要上传的Logo文件");
            }

            // 验证文件类型
            if (!isValidImageFile(file)) {
                return Result.error("只支持jpg、jpeg、png、gif格式的图片文件");
            }

            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("Logo文件大小不能超过5MB");
            }

            // 上传Logo文件
            String logoUrl = fileUploadService.uploadFile(file, "logo");

            // 更新公司信息中的Logo
            CompanyInfoResponse response = companyInfoService.updateCompanyLogo(logoUrl, userId);

            // 记录操作日志
            operationLogService.log(
                "UPLOAD",
                "IMAGE",
                "上传并更换了公司Logo: " + file.getOriginalFilename(),
                "CompanyInfo",
                response.getId(),
                file.getOriginalFilename()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("logoUrl", response.getCompanyLogo());
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("message", "Logo上传并更换成功");
            result.put("updateTime", response.getUpdateTime());

            return Result.success("Logo上传并更换成功", result);
        } catch (Exception e) {
            log.error("上传并更换公司Logo失败：{}", e.getMessage(), e);
            return Result.error("上传并更换Logo失败：" + e.getMessage());
        }
    }

    /**
     * 验证是否为有效的图片文件
     *
     * @param file 文件
     * @return 是否为有效图片
     */
    private boolean isValidImageFile(org.springframework.web.multipart.MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String extension = originalFilename.toLowerCase();
        return extension.endsWith(".jpg") ||
               extension.endsWith(".jpeg") ||
               extension.endsWith(".png") ||
               extension.endsWith(".gif") ||
               extension.endsWith(".webp") ||
               extension.endsWith(".svg");
    }

    
    
    /**
     * 从认证信息中获取用户ID (已更新）
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
                // 有时 principal 直接是 username 字符串
                username = (String) principal;
            }

            if (username != null) {
                try {
                    // 使用 UserService 查找用户并获取 ID
                    User user = userService.findByUsername(username);
                    return user.getId();
                } catch (Exception e) {
                    log.error("无法根据用户名 '{}' 从 SecurityContext 获取用户ID: {}", username, e.getMessage());
                    // 抛出运行时异常，因为对于管理接口，用户ID是必需的
                    throw new RuntimeException("无法确定操作用户ID", e);
                }
            }
        }

        log.error("无法从请求或SecurityContext中获取用户ID");
        // 抛出运行时异常
        throw new RuntimeException("无法确定操作用户ID");
    }

    /**
     * 从URL中提取文件名
     *
     * @param url URL
     * @return 文件名
     */
    private String getFileNameFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }

        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        }

        return url;
    }
}