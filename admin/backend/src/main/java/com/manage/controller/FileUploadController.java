package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.company.CompanyInfoResponse;
import com.manage.entity.MediaFile;
import com.manage.entity.User;
import com.manage.service.CompanyInfoService;
import com.manage.service.FileUploadService;
import com.manage.service.OperationLogService;
import com.manage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件上传控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final UserService userService;
    private final OperationLogService operationLogService;
    private final CompanyInfoService companyInfoService;

    /**
     * 上传公司Logo并更新数据库
     *
     * @param file Logo文件
     * @return 上传结果
     */
    @PostMapping("/logo")
    public Result<Map<String, Object>> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 上传文件
            String fileUrl = fileUploadService.uploadFile(file, "logo");

            // 更新公司信息中的Logo URL
            Long userId = getCurrentUserId();
            CompanyInfoResponse companyInfoResponse = companyInfoService.updateCompanyLogo(fileUrl, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("companyInfo", companyInfoResponse);

            // 记录操作日志
            operationLogService.log(
                "UPLOAD",
                "IMAGE",
                "上传并更新了公司Logo: " + file.getOriginalFilename(),
                "CompanyInfo",
                companyInfoResponse.getId(),
                file.getOriginalFilename()
            );

            return Result.success("Logo上传并更新成功", result);
        } catch (Exception e) {
            log.error("Logo上传失败：{}", e.getMessage());
            return Result.error("Logo上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传企业视频
     *
     * @param file 视频文件
     * @return 上传结果
     */
    @PostMapping("/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            String fileUrl = fileUploadService.uploadFile(file, "video");

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());

            // 记录操作日志
            operationLogService.log(
                "UPLOAD",
                "VIDEO",
                "上传了企业视频: " + file.getOriginalFilename(),
                "MediaFile",
                null,
                file.getOriginalFilename()
            );

            return Result.success("视频上传成功", result);
        } catch (Exception e) {
            log.error("视频上传失败：{}", e.getMessage());
            return Result.error("视频上传失败：" + e.getMessage());
        }
    }

    /**
     * 增强图片上传（支持压缩、进度回调等）
     *
     * @param file     图片文件
     * @param category 图片分类（必填）
     * @param description 图片描述（可选）
     * @param tags     图片标签（可选）
     * @param relatedId 关联ID（可选）
     * @param relatedType 关联类型（可选）
     * @param width    目标宽度（可选）
     * @param height   目标高度（可选）
     * @param maxSize  最大文件大小（可选）
     * @param watermark 是否添加水印（可选）
     * @param thumbnail 是否生成缩略图（可选）
     * @return 上传结果
     */
    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "relatedId", required = false) Long relatedId,
            @RequestParam(value = "relatedType", required = false) String relatedType,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "maxSize", required = false) Long maxSize,
            @RequestParam(value = "watermark", defaultValue = "false") Boolean watermark,
            @RequestParam(value = "thumbnail", defaultValue = "true") Boolean thumbnail) {

        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 设置默认分类
            if (category == null || category.trim().isEmpty()) {
                category = "general";
            }

            // 构建上传选项
            Map<String, Object> options = new HashMap<>();
            options.put("category", category);

            if (description != null && !description.trim().isEmpty()) {
                options.put("description", description);
            }
            if (tags != null && !tags.trim().isEmpty()) {
                options.put("tags", tags);
            }
            if (relatedId != null) {
                options.put("relatedId", relatedId);
            }
            if (relatedType != null && !relatedType.trim().isEmpty()) {
                options.put("relatedType", relatedType);
            }
            if (width != null) {
                options.put("width", width);
            }
            if (height != null) {
                options.put("height", height);
            }
            if (maxSize != null) {
                options.put("maxSize", maxSize);
            }
            if (watermark) {
                options.put("watermark", true);
            }
            options.put("thumbnail", thumbnail);

            Map<String, Object> result = fileUploadService.uploadImage(file, category, options);

            // 记录操作日志
            operationLogService.log(
                "UPLOAD",
                "IMAGE",
                "上传了图片: " + file.getOriginalFilename(),
                "MediaFile",
                result.containsKey("id") ? ((Number) result.get("id")).longValue() : null,
                file.getOriginalFilename()
            );

            return Result.success("图片上传成功", result);

        } catch (Exception e) {
            log.error("图片上传失败：{}", e.getMessage(), e);
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 增强图片上传（支持压缩、水印等）
     *
     * @param file     图片文件
     * @param category 图片分类
     * @param maxSize  最大文件大小（可选）
     * @param width    目标宽度（可选）
     * @param height   目标高度（可选）
     * @param watermark 是否添加水印（可选）
     * @param description 图片描述（可选）
     * @param tags     图片标签（可选）
     * @param relatedId 关联ID（可选）
     * @param relatedType 关联类型（可选）
     * @return 上传结果
     */
    @PostMapping("/image/enhanced")
    public Result<Map<String, Object>> uploadImageEnhanced(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "maxSize", required = false) Long maxSize,
            Authentication authentication,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "watermark", defaultValue = "false") Boolean watermark,
            @RequestParam(value = "thumbnail", defaultValue = "true") Boolean thumbnail,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "relatedId", required = false) Long relatedId,
            @RequestParam(value = "relatedType", required = false) String relatedType) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 验证category参数
            if (category == null || category.trim().isEmpty()) {
                return Result.error("文件分类不能为空");
            }

            // 构建上传选项
            Map<String, Object> options = new HashMap<>();
            if (maxSize != null) {
                options.put("maxSize", maxSize);
            }
            if (width != null) {
                options.put("width", width);
            }
            if (height != null) {
                options.put("height", height);
            }
            if (watermark) {
                options.put("watermark", true);
            }
            options.put("thumbnail", thumbnail); // 添加缩略图参数
            if (description != null && !description.trim().isEmpty()) {
                options.put("description", description);
            }
            if (tags != null && !tags.trim().isEmpty()) {
                options.put("tags", tags);
            }
            if (relatedId != null) {
                options.put("relatedId", relatedId);
            }
            if (relatedType != null && !relatedType.trim().isEmpty()) {
                options.put("relatedType", relatedType);
            }

            // 获取当前用户ID
            Long userId = getCurrentUserId(authentication);
            if (userId != null) {
                options.put("uploadedBy", userId);
            }

            Map<String, Object> result = fileUploadService.uploadImage(file, category, options);
            return Result.success("图片上传成功", result);

        } catch (Exception e) {
            log.error("增强图片上传失败：{}", e.getMessage());
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 增强视频上传（支持转码、切片等）
     *
     * @param file       视频文件
     * @param category   视频分类
     * @param maxSize    最大文件大小（可选）
     * @param thumbnail  是否生成缩略图（可选）
     * @param description 视频描述（可选）
     * @param tags       视频标签（可选）
     * @param relatedId  关联ID（可选）
     * @param relatedType 关联类型（可选）
     * @return 上传结果
     */
    @PostMapping("/video/enhanced")
    public Result<Map<String, Object>> uploadVideoEnhanced(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "maxSize", required = false) Long maxSize,
            Authentication authentication,
            @RequestParam(value = "thumbnail", defaultValue = "true") Boolean thumbnail,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "relatedId", required = false) Long relatedId,
            @RequestParam(value = "relatedType", required = false) String relatedType) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 验证category参数
            if (category == null || category.trim().isEmpty()) {
                return Result.error("文件分类不能为空");
            }

            // 构建上传选项
            Map<String, Object> options = new HashMap<>();
            if (maxSize != null) {
                options.put("maxSize", maxSize);
            }
            if (thumbnail) {
                options.put("thumbnail", true);
            }
            if (description != null && !description.trim().isEmpty()) {
                options.put("description", description);
            }
            if (tags != null && !tags.trim().isEmpty()) {
                options.put("tags", tags);
            }
            if (relatedId != null) {
                options.put("relatedId", relatedId);
            }
            if (relatedType != null && !relatedType.trim().isEmpty()) {
                options.put("relatedType", relatedType);
            }

            // 获取当前用户ID
            Long userId = getCurrentUserId(authentication);
            if (userId != null) {
                options.put("uploadedBy", userId);
            }

            Map<String, Object> result = fileUploadService.uploadVideo(file, category, options);
            
            // 记录操作日志
            String categoryDisplay = getCategoryDisplay(category);
            operationLogService.log(
                "UPLOAD",
                "VIDEO",
                "上传了" + categoryDisplay + ": " + file.getOriginalFilename(),
                "MediaFile",
                null,
                file.getOriginalFilename()
            );
            
            return Result.success("视频上传成功", result);

        } catch (Exception e) {
            log.error("增强视频上传失败：{}", e.getMessage());
            return Result.error("视频上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传图片
     *
     * @param files    图片文件数组
     * @param category 图片分类
     * @return 上传结果
     */
    @PostMapping("/images/batch")
    public Result<Map<String, Object>> batchUploadImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "category", required = false) String category) {
        try {
            if (files == null || files.length == 0) {
                return Result.error("请选择要上传的文件");
            }

            if (files.length > 20) {
                return Result.error("一次最多只能上传20张图片");
            }

            // 验证category参数
            if (category == null || category.trim().isEmpty()) {
                return Result.error("文件分类不能为空");
            }

            List<MultipartFile> fileList = Arrays.asList(files);
            Map<String, Object> result = fileUploadService.uploadFiles(fileList, category);

            int successCount = (Integer) result.get("successCount");
            int errorCount = (Integer) result.get("errorCount");

            if (successCount == 0) {
                return Result.error("所有图片上传失败", result);
            } else if (errorCount == 0) {
                return Result.success("所有图片上传成功", result);
            } else {
                return Result.success("部分图片上传成功", result);
            }

        } catch (Exception e) {
            log.error("批量上传图片失败：{}", e.getMessage());
            return Result.error("批量上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传视频
     *
     * @param files    视频文件数组
     * @param category 视频分类
     * @return 上传结果
     */
    @PostMapping("/videos/batch")
    public Result<Map<String, Object>> batchUploadVideos(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "category", required = false) String category) {
        try {
            if (files == null || files.length == 0) {
                return Result.error("请选择要上传的文件");
            }

            if (files.length > 10) {
                return Result.error("一次最多只能上传10个视频");
            }

            // 验证category参数
            if (category == null || category.trim().isEmpty()) {
                return Result.error("文件分类不能为空");
            }

            List<MultipartFile> fileList = Arrays.asList(files);
            Map<String, Object> result = fileUploadService.uploadFiles(fileList, category);

            int successCount = (Integer) result.get("successCount");
            int errorCount = (Integer) result.get("errorCount");

            if (successCount == 0) {
                return Result.error("所有视频上传失败", result);
            } else if (errorCount == 0) {
                return Result.success("所有视频上传成功", result);
            } else {
                return Result.success("部分视频上传成功", result);
            }

        } catch (Exception e) {
            log.error("批量上传视频失败：{}", e.getMessage());
            return Result.error("批量上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件信息
     *
     * @param fileUrl 文件URL
     * @return 文件信息
     */
    @GetMapping("/file/info")
    public Result<Map<String, Object>> getFileInfo(@RequestParam("fileUrl") String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.trim().isEmpty()) {
                return Result.error("文件URL不能为空");
            }

            Map<String, Object> fileInfo = fileUploadService.getFileInfo(fileUrl);
            return Result.success("获取文件信息成功", fileInfo);

        } catch (Exception e) {
            log.error("获取文件信息失败：{}", e.getMessage());
            return Result.error("获取文件信息失败：" + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param fileUrl 文件URL
     * @return 检查结果
     */
    @GetMapping("/file/exists")
    public Result<Boolean> checkFileExists(@RequestParam("fileUrl") String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.trim().isEmpty()) {
                return Result.error("文件URL不能为空");
            }

            boolean exists = fileUploadService.fileExists(fileUrl);
            return Result.success(exists);

        } catch (Exception e) {
            log.error("检查文件是否存在失败：{}", e.getMessage());
            return Result.error("检查文件失败：" + e.getMessage());
        }
    }

    /**
     * 获取预签名上传URL（用于前端直接上传）
     *
     * @param fileName 文件名
     * @param category 文件分类
     * @param expires  过期时间（秒）
     * @return 预签名URL
     */
    @GetMapping("/upload/presigned-url")
    public Result<String> getPresignedUploadUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "expires", defaultValue = "3600") Integer expires) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                return Result.error("文件名不能为空");
            }
            if (category == null || category.trim().isEmpty()) {
                return Result.error("文件分类不能为空");
            }

            String presignedUrl = fileUploadService.getPresignedUrl(fileName, category, expires);
            return Result.success("获取预签名URL成功", presignedUrl);

        } catch (Exception e) {
            log.error("获取预签名URL失败：{}", e.getMessage());
            return Result.error("获取预签名URL失败：" + e.getMessage());
        }
    }

    /**
     * 根据分类查询文件列表
     *
     * @param category 文件分类
     * @return 文件列表
     */
    @GetMapping("/files/category/{category}")
    public Result<Map<String, Object>> getFilesByCategory(@PathVariable String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                return Result.error("文件分类不能为空");
            }

            log.info("开始查询分类文件，分类：{}", category);

            // 只查询状态为1（已完成）的文件
            List<MediaFile> files = fileUploadService.getFilesByCategoryAndStatus(category, 1);

            // 过滤掉可能存在的已删除文件（双重保险）
            List<MediaFile> activeFiles = files.stream()
                    .filter(file -> file.getStatus() != null && file.getStatus() != 3)
                    .collect(Collectors.toList());

            log.info("查询分类文件成功，分类：{}，原始数量：{}，过滤后数量：{}",
                    category, files.size(), activeFiles.size());

            Map<String, Object> result = new HashMap<>();
            result.put("files", activeFiles);
            result.put("category", category);
            result.put("totalCount", activeFiles.size());
            result.put("timestamp", System.currentTimeMillis());

            return Result.success("查询成功", result);

        } catch (Exception e) {
            log.error("查询分类文件失败，分类：{}，错误：{}", category, e.getMessage(), e);
            return Result.error("查询文件失败：" + e.getMessage());
        }
    }

    /**
     * 根据分类查询单个最新文件（已废弃，建议使用 /files/category/{category}）
     *
     * @param category 文件分类
     * @return 文件信息
     */
    @GetMapping("/file/category/{category}/latest")
    public Result<MediaFile> getLatestFileByCategory(@PathVariable String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                return Result.error("文件分类不能为空");
            }

            List<MediaFile> files = fileUploadService.getFilesByCategoryAndStatus(category, 1);

            // 过滤掉已删除的文件
            List<MediaFile> activeFiles = files.stream()
                    .filter(file -> file.getStatus() != null && file.getStatus() != 3)
                    .collect(Collectors.toList());

            if (activeFiles.isEmpty()) {
                return Result.error("未找到相关文件");
            }

            // 返回最新的一个文件
            MediaFile latestFile = activeFiles.get(0);
            log.info("查询最新文件成功，分类：{}，文件：{}", category, latestFile.getOriginalName());
            return Result.success("查询成功", latestFile);

        } catch (Exception e) {
            log.error("查询最新文件失败，分类：{}，错误：{}", category, e.getMessage());
            return Result.error("查询文件失败：" + e.getMessage());
        }
    }

    /**
     * 刷新文件列表（用于清除前端缓存）
     *
     * @param category 文件分类（可选）
     * @return 最新的文件列表
     */
    @GetMapping("/files/refresh")
    public Result<Map<String, Object>> refreshFiles(@RequestParam(required = false) String category) {
        try {
            log.info("刷新文件列表请求，分类：{}", category);

            Map<String, Object> result = new HashMap<>();
            result.put("refreshed", true);
            result.put("timestamp", System.currentTimeMillis());

            if (category != null && !category.trim().isEmpty()) {
                // 刷新指定分类的文件
                List<MediaFile> files = fileUploadService.getFilesByCategoryAndStatus(category, 1);
                List<MediaFile> activeFiles = files.stream()
                        .filter(file -> file.getStatus() != null && file.getStatus() != 3)
                        .collect(Collectors.toList());

                result.put("category", category);
                result.put("files", activeFiles);
                result.put("totalCount", activeFiles.size());

                log.info("刷新分类文件列表成功，分类：{}，数量：{}", category, activeFiles.size());
            } else {
                // 刷新所有分类的文件统计
                result.put("message", "所有文件列表已刷新");
                log.info("刷新所有文件列表成功");
            }

            return Result.success("文件列表刷新成功", result);

        } catch (Exception e) {
            log.error("刷新文件列表失败：{}", e.getMessage(), e);
            return Result.error("刷新文件列表失败：" + e.getMessage());
        }
    }

    /**
     * 复制文件
     *
     * @param fileUrl        源文件URL
     * @param targetCategory 目标分类
     * @return 新文件URL
     */
    @PostMapping("/file/copy")
    public Result<String> copyFile(
            @RequestParam("fileUrl") String fileUrl,
            @RequestParam("targetCategory") String targetCategory) {
        try {
            if (fileUrl == null || fileUrl.trim().isEmpty()) {
                return Result.error("源文件URL不能为空");
            }
            if (targetCategory == null || targetCategory.trim().isEmpty()) {
                return Result.error("目标分类不能为空");
            }

            String newFileUrl = fileUploadService.copyFile(fileUrl, targetCategory);
            return Result.success("文件复制成功", newFileUrl);

        } catch (Exception e) {
            log.error("复制文件失败：{}", e.getMessage());
            return Result.error("复制文件失败：" + e.getMessage());
        }
    }

    /**
     * 移动文件
     *
     * @param fileUrl        源文件URL
     * @param targetCategory 目标分类
     * @return 新文件URL
     */
    @PostMapping("/file/move")
    public Result<String> moveFile(
            @RequestParam("fileUrl") String fileUrl,
            @RequestParam("targetCategory") String targetCategory) {
        try {
            if (fileUrl == null || fileUrl.trim().isEmpty()) {
                return Result.error("源文件URL不能为空");
            }
            if (targetCategory == null || targetCategory.trim().isEmpty()) {
                return Result.error("目标分类不能为空");
            }

            String newFileUrl = fileUploadService.moveFile(fileUrl, targetCategory);
            return Result.success("文件移动成功", newFileUrl);

        } catch (Exception e) {
            log.error("移动文件失败：{}", e.getMessage());
            return Result.error("移动文件失败：" + e.getMessage());
        }
    }

    /**
     * 上传资质证书
     *
     * @param file 证书文件
     * @return 上传结果
     */
    @PostMapping("/qualification")
    public Result<Map<String, String>> uploadQualification(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            String fileUrl = fileUploadService.uploadFile(file, "qualification");

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());

            return Result.success("资质证书上传成功", result);
        } catch (Exception e) {
            log.error("资质证书上传失败：{}", e.getMessage());
            return Result.error("资质证书上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传团队头像
     *
     * @param file 头像文件
     * @return 上传结果
     */
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            String fileUrl = fileUploadService.uploadFile(file, "avatar");

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());

            return Result.success("头像上传成功", result);
        } catch (Exception e) {
            log.error("头像上传失败：{}", e.getMessage());
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 通用文件上传
     *
     * @param file     文件
     * @param category 文件分类
     * @return 上传结果
     */
    @PostMapping("/file")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("category") String category) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            if (category == null || category.trim().isEmpty()) {
                return Result.error("文件分类不能为空");
            }

            String fileUrl = fileUploadService.uploadFile(file, category);

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("category", category);

            return Result.success("文件上传成功", result);
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage());
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传产品主图（带压缩）
     *
     * @param file 图片文件
     * @return 上传结果
     */
    @PostMapping("/product/main-image")
    public Result<Map<String, Object>> uploadProductMainImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 验证文件类型
            if (!isValidImageFile(file)) {
                return Result.error("只支持jpg、jpeg、png、gif格式的图片文件");
            }

            // 验证文件大小（10MB，压缩前可以更大）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("图片文件大小不能超过10MB");
            }

            // 构建上传选项，使用与普通图片相同的压缩参数
            Map<String, Object> options = new HashMap<>();
            options.put("width", 1200);          // 产品图片宽度限制
            options.put("height", 1200);         // 产品图片高度限制
            options.put("maxSize", 10 * 1024 * 1024);  // 最大10MB
            // 移除quality参数，让系统使用智能压缩质量（60-85%，基于文件大小自动调整）
            options.put("thumbnail", true);      // 生成缩略图
            options.put("description", "产品主图");

            log.info("开始上传产品主图：{}，大小：{}MB", file.getOriginalFilename(),
                    String.format("%.2f", file.getSize() / 1024.0 / 1024.0));

            // 使用带压缩的图片上传方法
            Map<String, Object> result = fileUploadService.uploadImage(file, "product/images", options);

            log.info("产品主图上传完成：{}，压缩后大小：{}MB", file.getOriginalFilename(),
                    result.containsKey("compressedSize") ?
                        String.format("%.2f", ((Number) result.get("compressedSize")).doubleValue() / 1024.0 / 1024.0) :
                        "未知");

            return Result.success("产品主图上传成功", result);
        } catch (Exception e) {
            log.error("产品主图上传失败：{}", e.getMessage());
            return Result.error("产品主图上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传产品图片
     *
     * @param files 图片文件列表
     * @return 上传结果
     */
    @PostMapping("/product/images/batch")
    public Result<Map<String, Object>> batchUploadProductImages(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return Result.error("请选择要上传的文件");
            }

            if (files.length > 10) {
                return Result.error("一次最多只能上传10张图片");
            }

            List<Map<String, Object>> successResults = new ArrayList<>();
            List<Map<String, Object>> errorResults = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    // 验证文件
                    if (file.isEmpty()) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("fileName", "空文件");
                        error.put("error", "文件为空");
                        errorResults.add(error);
                        continue;
                    }

                    if (!isValidImageFile(file)) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("fileName", file.getOriginalFilename());
                        error.put("error", "只支持jpg、jpeg、png、gif格式的图片文件");
                        errorResults.add(error);
                        continue;
                    }

                    // 验证文件大小（10MB，压缩前可以更大）
                    if (file.getSize() > 10 * 1024 * 1024) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("fileName", file.getOriginalFilename());
                        error.put("error", "图片文件大小不能超过10MB");
                        errorResults.add(error);
                        continue;
                    }

                    // 构建上传选项，使用与普通图片相同的压缩参数
                    Map<String, Object> options = new HashMap<>();
                    options.put("width", 1200);          // 产品图片宽度限制
                    options.put("height", 1200);         // 产品图片高度限制
                    options.put("maxSize", 10 * 1024 * 1024);  // 最大10MB
                    // 移除quality参数，让系统使用智能压缩质量（60-85%，基于文件大小自动调整）
                    options.put("thumbnail", true);      // 生成缩略图
                    options.put("description", "产品图片");

                    log.info("开始压缩上传产品图片：{}，大小：{}MB", file.getOriginalFilename(),
                            String.format("%.2f", file.getSize() / 1024.0 / 1024.0));

                    // 使用带压缩的图片上传方法
                    Map<String, Object> uploadResult = fileUploadService.uploadImage(file, "product/images", options);

                    Map<String, Object> success = new HashMap<>();
                    success.put("fileName", file.getOriginalFilename());
                    success.put("url", uploadResult.get("url"));
                    success.put("fileSize", uploadResult.get("compressedSize") != null ? uploadResult.get("compressedSize") : file.getSize());
                    success.put("originalSize", file.getSize());
                    success.put("compressed", uploadResult.get("compressed"));
                    if (uploadResult.containsKey("compressionRatio")) {
                        success.put("compressionRatio", uploadResult.get("compressionRatio"));
                    }
                    successResults.add(success);

                    log.info("产品图片压缩上传完成：{}，压缩后大小：{}MB", file.getOriginalFilename(),
                            uploadResult.containsKey("compressedSize") ?
                                String.format("%.2f", ((Number) uploadResult.get("compressedSize")).doubleValue() / 1024.0 / 1024.0) :
                                "未知");

                } catch (Exception e) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("fileName", file.getOriginalFilename());
                    error.put("error", e.getMessage());
                    errorResults.add(error);
                    log.error("产品图片上传失败：{}，错误：{}", file.getOriginalFilename(), e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", successResults);
            result.put("errors", errorResults);
            result.put("totalCount", files.length);
            result.put("successCount", successResults.size());
            result.put("errorCount", errorResults.size());

            if (successResults.isEmpty()) {
                return Result.error("所有图片上传失败", result);
            } else if (errorResults.isEmpty()) {
                return Result.success("所有图片上传成功", result);
            } else {
                return Result.success("部分图片上传成功", result);
            }

        } catch (Exception e) {
            log.error("批量上传产品图片失败：{}", e.getMessage());
            return Result.error("批量上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传单个产品图片
     *
     * @param file 图片文件
     * @return 上传结果
     */
    @PostMapping("/product/image")
    public Result<Map<String, String>> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 验证文件类型
            if (!isValidImageFile(file)) {
                return Result.error("只支持jpg、jpeg、png、gif格式的图片文件");
            }

            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("图片文件大小不能超过5MB");
            }

            String fileUrl = fileUploadService.uploadFile(file, "product/images");

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", String.valueOf(file.getSize()));

            return Result.success("产品图片上传成功", result);
        } catch (Exception e) {
            log.error("产品图片上传失败：{}", e.getMessage());
            return Result.error("产品图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除产品图片
     *
     * @param imageUrl 图片URL
     * @return 删除结果
     */
    @DeleteMapping("/product/image")
    public Result<Void> deleteProductImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return Result.error("图片URL不能为空");
            }

            boolean success = fileUploadService.deleteFile(imageUrl);
            if (success) {
                return Result.success("产品图片删除成功");
            } else {
                return Result.error("产品图片删除失败");
            }
        } catch (Exception e) {
            log.error("产品图片删除失败：{}", e.getMessage());
            return Result.error("产品图片删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除产品图片
     *
     * @param imageUrls 图片URL列表
     * @return 删除结果
     */
    @DeleteMapping("/product/images/batch")
    public Result<Map<String, Object>> batchDeleteProductImages(@RequestBody List<String> imageUrls) {
        try {
            if (imageUrls == null || imageUrls.isEmpty()) {
                return Result.error("请选择要删除的图片");
            }

            if (imageUrls.size() > 50) {
                return Result.error("一次最多只能删除50张图片");
            }

            List<String> successUrls = new ArrayList<>();
            List<Map<String, String>> errorUrls = new ArrayList<>();

            for (String imageUrl : imageUrls) {
                try {
                    if (imageUrl == null || imageUrl.trim().isEmpty()) {
                        Map<String, String> error = new HashMap<>();
                        error.put("url", "空URL");
                        error.put("error", "图片URL为空");
                        errorUrls.add(error);
                        continue;
                    }

                    boolean success = fileUploadService.deleteFile(imageUrl);
                    if (success) {
                        successUrls.add(imageUrl);
                        log.info("产品图片删除成功：{}", imageUrl);
                    } else {
                        Map<String, String> error = new HashMap<>();
                        error.put("url", imageUrl);
                        error.put("error", "删除失败");
                        errorUrls.add(error);
                    }
                } catch (Exception e) {
                    Map<String, String> error = new HashMap<>();
                    error.put("url", imageUrl);
                    error.put("error", e.getMessage());
                    errorUrls.add(error);
                    log.error("产品图片删除失败：{}，错误：{}", imageUrl, e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successUrls", successUrls);
            result.put("errors", errorUrls);
            result.put("totalCount", imageUrls.size());
            result.put("successCount", successUrls.size());
            result.put("errorCount", errorUrls.size());

            if (successUrls.isEmpty()) {
                return Result.error("所有图片删除失败", result);
            } else if (errorUrls.isEmpty()) {
                return Result.success("所有图片删除成功", result);
            } else {
                return Result.success("部分图片删除成功", result);
            }

        } catch (Exception e) {
            log.error("批量删除产品图片失败：{}", e.getMessage());
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 删除结果
     */
    @DeleteMapping("/file")
    public Result<Map<String, Object>> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.trim().isEmpty()) {
                return Result.error("文件URL不能为空");
            }

            // 从数据库获取原始文件名
            String originalFileName = fileUploadService.getOriginalFileName(fileUrl);
            // 如果数据库中没有，则使用URL中提取的文件名
            String displayName = originalFileName != null ? originalFileName : getFileNameFromUrl(fileUrl);

            boolean success = fileUploadService.deleteFile(fileUrl);

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("fileUrl", fileUrl);
            resultData.put("fileName", displayName);
            resultData.put("deleted", success);
            resultData.put("timestamp", System.currentTimeMillis());

            if (success) {
                // 记录操作日志（使用原始文件名）
                operationLogService.log(
                    "DELETE",
                    "FILE",
                    "删除了文件: " + displayName,
                    "MediaFile",
                    null,
                    displayName
                );

                log.info("文件删除成功: {}", displayName);
                resultData.put("message", "文件删除成功");
                return Result.success("文件删除成功", resultData);
            } else {
                log.warn("文件删除失败: {}", displayName);
                resultData.put("message", "文件删除失败");
                return Result.error("文件删除失败", resultData);
            }
        } catch (Exception e) {
            log.error("文件删除失败：{}", e.getMessage());
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("fileUrl", fileUrl);
            errorData.put("deleted", false);
            errorData.put("error", e.getMessage());
            errorData.put("timestamp", System.currentTimeMillis());
            return Result.error("文件删除失败：" + e.getMessage(), errorData);
        }
    }

    /**
     * 上传富文本编辑器图片
     *
     * @param file 图片文件
     * @return 上传结果（适配富文本编辑器格式）
     */
    @PostMapping("/editor/image")
    public Result<Map<String, Object>> uploadEditorImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 验证文件类型
            if (!isValidImageFile(file)) {
                return Result.error("只支持jpg、jpeg、png、gif格式的图片文件");
            }

            // 验证文件大小（10MB，富文本编辑器可能需要上传更大的图片）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("图片文件大小不能超过10MB");
            }

            String fileUrl = fileUploadService.uploadFile(file, "editor/images");

            // 返回富文本编辑器需要的格式
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("alt", file.getOriginalFilename());
            result.put("href", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());

            log.info("富文本编辑器图片上传成功：{}", file.getOriginalFilename());
            return Result.success("图片上传成功", result);

        } catch (Exception e) {
            log.error("富文本编辑器图片上传失败：{}", e.getMessage());
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传富文本编辑器文件（支持更多文件类型）
     *
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping("/editor/file")
    public Result<Map<String, Object>> uploadEditorFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 验证文件类型（支持更多类型）
            if (!isValidEditorFile(file)) {
                return Result.error("不支持的文件类型");
            }

            // 验证文件大小（20MB）
            if (file.getSize() > 20 * 1024 * 1024) {
                return Result.error("文件大小不能超过20MB");
            }

            String fileUrl = fileUploadService.uploadFile(file, "editor/files");

            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("fileType", getFileType(file.getOriginalFilename()));

            log.info("富文本编辑器文件上传成功：{}", file.getOriginalFilename());
            return Result.success("文件上传成功", result);

        } catch (Exception e) {
            log.error("富文本编辑器文件上传失败：{}", e.getMessage());
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传富文本编辑器图片
     *
     * @param files 图片文件数组
     * @return 上传结果
     */
    @PostMapping("/editor/images/batch")
    public Result<Map<String, Object>> batchUploadEditorImages(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return Result.error("请选择要上传的文件");
            }

            if (files.length > 20) {
                return Result.error("一次最多只能上传20张图片");
            }

            List<Map<String, Object>> successResults = new ArrayList<>();
            List<Map<String, Object>> errorResults = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    // 验证文件
                    if (file.isEmpty()) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("fileName", "空文件");
                        error.put("error", "文件为空");
                        errorResults.add(error);
                        continue;
                    }

                    if (!isValidImageFile(file)) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("fileName", file.getOriginalFilename());
                        error.put("error", "只支持jpg、jpeg、png、gif格式的图片文件");
                        errorResults.add(error);
                        continue;
                    }

                    // 验证文件大小（10MB）
                    if (file.getSize() > 10 * 1024 * 1024) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("fileName", file.getOriginalFilename());
                        error.put("error", "图片文件大小不能超过10MB");
                        errorResults.add(error);
                        continue;
                    }

                    // 上传文件
                    String fileUrl = fileUploadService.uploadFile(file, "editor/images");

                    Map<String, Object> success = new HashMap<>();
                    success.put("fileName", file.getOriginalFilename());
                    success.put("url", fileUrl);
                    success.put("fileSize", file.getSize());
                    successResults.add(success);

                    log.info("富文本编辑器图片上传成功：{}", file.getOriginalFilename());

                } catch (Exception e) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("fileName", file.getOriginalFilename());
                    error.put("error", e.getMessage());
                    errorResults.add(error);
                    log.error("富文本编辑器图片上传失败：{}，错误：{}", file.getOriginalFilename(), e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", successResults);
            result.put("errors", errorResults);
            result.put("totalCount", files.length);
            result.put("successCount", successResults.size());
            result.put("errorCount", errorResults.size());

            if (successResults.isEmpty()) {
                return Result.error("所有图片上传失败", result);
            } else if (errorResults.isEmpty()) {
                return Result.success("所有图片上传成功", result);
            } else {
                return Result.success("部分图片上传成功", result);
            }

        } catch (Exception e) {
            log.error("批量上传富文本编辑器图片失败：{}", e.getMessage());
            return Result.error("批量上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取富文本编辑器图片信息
     *
     * @param imageUrl 图片URL
     * @return 图片信息
     */
    @GetMapping("/editor/image/info")
    public Result<Map<String, Object>> getEditorImageInfo(@RequestParam("imageUrl") String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return Result.error("图片URL不能为空");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("url", imageUrl);
            result.put("fileName", getFileNameFromUrl(imageUrl));
            result.put("fileType", getFileType(getFileNameFromUrl(imageUrl)));

            return Result.success("获取图片信息成功", result);

        } catch (Exception e) {
            log.error("获取富文本编辑器图片信息失败：{}", e.getMessage());
            return Result.error("获取图片信息失败：" + e.getMessage());
        }
    }

    /**
     * 验证是否为有效的图片文件
     *
     * @param file 文件
     * @return 是否为有效图片
     */
    private boolean isValidImageFile(MultipartFile file) {
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
     * 验证是否为有效的富文本编辑器文件
     *
     * @param file 文件
     * @return 是否为有效文件
     */
    private boolean isValidEditorFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String extension = originalFilename.toLowerCase();

        // 支持的图片格式
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg") ||
            extension.endsWith(".png") || extension.endsWith(".gif") ||
            extension.endsWith(".webp") || extension.endsWith(".svg")) {
            return true;
        }

        // 支持的文档格式
        if (extension.endsWith(".pdf") || extension.endsWith(".doc") ||
            extension.endsWith(".docx") || extension.endsWith(".xls") ||
            extension.endsWith(".xlsx") || extension.endsWith(".ppt") ||
            extension.endsWith(".pptx")) {
            return true;
        }

        // 支持的压缩格式
        if (extension.endsWith(".zip") || extension.endsWith(".rar") ||
            extension.endsWith(".7z")) {
            return true;
        }

        // 支持的文本格式
        if (extension.endsWith(".txt") || extension.endsWith(".md")) {
            return true;
        }

        return false;
    }

    /**
     * 获取文件类型
     *
     * @param fileName 文件名
     * @return 文件类型
     */
    private String getFileType(String fileName) {
        if (fileName == null) {
            return "unknown";
        }

        String extension = fileName.toLowerCase();
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return "image";
        } else if (extension.endsWith(".png")) {
            return "image";
        } else if (extension.endsWith(".gif")) {
            return "image";
        } else if (extension.endsWith(".webp")) {
            return "image";
        } else if (extension.endsWith(".svg")) {
            return "image";
        } else if (extension.endsWith(".pdf")) {
            return "pdf";
        } else if (extension.endsWith(".doc") || extension.endsWith(".docx")) {
            return "word";
        } else if (extension.endsWith(".xls") || extension.endsWith(".xlsx")) {
            return "excel";
        } else if (extension.endsWith(".ppt") || extension.endsWith(".pptx")) {
            return "powerpoint";
        } else if (extension.endsWith(".zip") || extension.endsWith(".rar") || extension.endsWith(".7z")) {
            return "archive";
        } else if (extension.endsWith(".txt") || extension.endsWith(".md")) {
            return "text";
        } else {
            return "unknown";
        }
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

    /**
     * 获取当前登录用户ID
     *
     * @param authentication 认证信息
     * @return 用户ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            try {
                User user = userService.findByUsername(username);
                return user.getId();
            } catch (Exception e) {
                log.warn("获取用户信息失败，用户名: {}", username, e);
                return null;
            }
        }
        return null;
    }

    /**
     * 获取当前认证用户的ID
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        try {
            // 从安全上下文获取认证信息
            org.springframework.security.core.context.SecurityContext context =
                org.springframework.security.core.context.SecurityContextHolder.getContext();

            if (context != null && context.getAuthentication() != null) {
                org.springframework.security.core.Authentication authentication = context.getAuthentication();

                if (authentication != null && authentication.isAuthenticated()) {
                    Object principal = authentication.getPrincipal();

                    if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                        String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                        User user = userService.findByUsername(username);
                        return user.getId();
                    } else if (principal instanceof String) {
                        // 如果principal直接是用户名
                        String username = (String) principal;
                        User user = userService.findByUsername(username);
                        return user.getId();
                    }
                }
            }

            // 如果无法从安全上下文获取用户ID，返回默认值1（管理员ID）
            log.warn("无法从安全上下文获取用户ID，使用默认管理员ID: 1");
            return 1L;

        } catch (Exception e) {
            log.error("获取当前用户ID失败：{}", e.getMessage(), e);
            return 1L; // 返回默认管理员ID
        }
    }

    /**
     * 上传首页图标文件
     *
     * @param file 图标文件
     * @param iconType 图标类型 (company/team/prices/quality)
     * @return 上传结果
     */
    @PostMapping("/home-icon")
    public Result<Map<String, Object>> uploadHomeIcon(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("iconType") String iconType) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 验证文件类型
            if (!isValidImageFile(file)) {
                return Result.error("只支持jpg、jpeg、png、gif、webp格式的图片文件");
            }

            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("图标文件大小不能超过5MB");
            }

            // 验证图标类型
            if (!isValidIconType(iconType)) {
                return Result.error("无效的图标类型，支持的类型：company, team, prices, quality");
            }

            // 构建文件名：custom-{iconType}-icon.{ext}
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = "custom-" + iconType + "-icon." + extension;

            // 上传到icons分类
            Map<String, Object> options = new HashMap<>();
            options.put("width", 128);
            options.put("height", 128);
            options.put("category", "icons");
            options.put("description", "首页图标 - " + iconType);

            Map<String, Object> result = fileUploadService.uploadImage(file, "icons", options);

            // 添加图标相关信息
            result.put("iconType", iconType);
            result.put("customIconPath", "/uploads/icons/" + fileName);
            result.put("accessUrl", result.get("url"));

            log.info("首页图标上传成功：{}，类型：{}", file.getOriginalFilename(), iconType);
            return Result.success("首页图标上传成功", result);

        } catch (Exception e) {
            log.error("首页图标上传失败：{}", e.getMessage());
            return Result.error("首页图标上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传首页图标
     *
     * @param files 图标文件映射，key为iconType，value为文件
     * @return 上传结果
     */
    @PostMapping("/home-icons/batch")
    public Result<Map<String, Object>> uploadHomeIconsBatch(@RequestParam Map<String, MultipartFile> files) {
        try {
            if (files == null || files.isEmpty()) {
                return Result.error("请选择要上传的图标文件");
            }

            Map<String, Object> successResults = new HashMap<>();
            Map<String, String> errorResults = new HashMap<>();

            for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
                String iconType = entry.getKey();
                MultipartFile file = entry.getValue();

                try {
                    if (file == null || file.isEmpty()) {
                        errorResults.put(iconType, "文件为空");
                        continue;
                    }

                    if (!isValidIconType(iconType)) {
                        errorResults.put(iconType, "无效的图标类型");
                        continue;
                    }

                    if (!isValidImageFile(file)) {
                        errorResults.put(iconType, "不支持的图片格式");
                        continue;
                    }

                    // 构建文件名
                    String originalFilename = file.getOriginalFilename();
                    String extension = getFileExtension(originalFilename);
                    String fileName = "custom-" + iconType + "-icon." + extension;

                    // 上传文件
                    Map<String, Object> options = new HashMap<>();
                    options.put("width", 128);
                    options.put("height", 128);
                    options.put("category", "icons");
                    options.put("description", "首页图标 - " + iconType);

                    Map<String, Object> uploadResult = fileUploadService.uploadImage(file, "icons", options);
                    uploadResult.put("iconType", iconType);
                    uploadResult.put("customIconPath", "/uploads/icons/" + fileName);

                    successResults.put(iconType, uploadResult);
                    log.info("首页图标批量上传成功：{}，类型：{}", file.getOriginalFilename(), iconType);

                } catch (Exception e) {
                    errorResults.put(iconType, e.getMessage());
                    log.error("首页图标批量上传失败：{}，类型：{}，错误：{}",
                             file != null ? file.getOriginalFilename() : "null", iconType, e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", successResults);
            result.put("errors", errorResults);
            result.put("totalCount", files.size());
            result.put("successCount", successResults.size());
            result.put("errorCount", errorResults.size());

            if (successResults.isEmpty()) {
                return Result.error("所有图标上传失败", result);
            } else if (errorResults.isEmpty()) {
                return Result.success("所有图标上传成功", result);
            } else {
                return Result.success("部分图标上传成功", result);
            }

        } catch (Exception e) {
            log.error("批量上传首页图标失败：{}", e.getMessage());
            return Result.error("批量上传失败：" + e.getMessage());
        }
    }

    /**
     * 验证图标类型是否有效
     *
     * @param iconType 图标类型
     * @return 是否有效
     */
    private boolean isValidIconType(String iconType) {
        if (iconType == null || iconType.trim().isEmpty()) {
            return false;
        }
        return "company".equals(iconType) || "team".equals(iconType) ||
               "prices".equals(iconType) || "quality".equals(iconType);
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "png"; // 默认扩展名
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }

        return "png"; // 默认扩展名
    }

    /**
     * 获取分类显示文本
     *
     * @param category 分类
     * @return 显示文本
     */
    private String getCategoryDisplay(String category) {
        if (category == null) {
            return "文件";
        }

        switch (category) {
            case "company-intro":
                return "企业宣传视频";
            case "product-video":
                return "产品展示视频";
            case "logo":
                return "公司Logo";
            case "video":
                return "企业视频";
            case "product-image":
                return "产品图片";
            case "editor/images":
                return "富文本图片";
            case "editor/files":
                return "富文本文件";
            case "icons":
                return "首页图标";
            default:
                return category;
        }
    }
}
