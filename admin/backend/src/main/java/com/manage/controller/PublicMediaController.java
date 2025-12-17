package com.manage.controller;

import com.manage.common.Result;
import com.manage.entity.MediaFile;
import com.manage.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公开媒体文件查询接口控制器（无需认证）
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/public/media")
@RequiredArgsConstructor
public class PublicMediaController {

    private final FileUploadService fileUploadService;

    /**
     * 根据分类查询媒体文件列表
     */
    @GetMapping("/category/{category}")
    public Result<Map<String, Object>> getMediaFilesByCategory(@PathVariable String category) {
        try {
            if (!StringUtils.hasText(category)) {
                return Result.error("分类参数不能为空");
            }

            log.info("开始查询公开媒体文件，分类：{}", category);

            // 只查询状态为1（已完成）且公开的文件
            List<MediaFile> files = fileUploadService.getPublicFilesByCategoryAndStatus(category, 1);

            // 过滤掉可能存在的已删除文件（双重保险）
            List<MediaFile> activeFiles = files.stream()
                    .filter(file -> file.getStatus() != null && file.getStatus() != 3)
                    .filter(file -> file.getIsPublic() != null && file.getIsPublic())
                    .collect(java.util.stream.Collectors.toList());

            log.info("查询公开媒体文件成功，分类：{}，原始数量：{}，过滤后数量：{}",
                    category, files.size(), activeFiles.size());

            Map<String, Object> result = new HashMap<>();
            result.put("files", activeFiles);
            result.put("category", category);
            result.put("totalCount", activeFiles.size());
            result.put("timestamp", System.currentTimeMillis());

            return Result.success("查询成功", result);

        } catch (Exception e) {
            log.error("查询分类媒体文件失败，分类：{}，错误：{}", category, e.getMessage(), e);
            return Result.error("查询媒体文件失败：" + e.getMessage());
        }
    }

    /**
     * 获取支持的媒体分类列表
     */
    @GetMapping("/categories")
    public Result<Map<String, Object>> getSupportedCategories() {
        try {
            // 从数据库动态获取所有有效的分类
            Map<String, Integer> categories = fileUploadService.getActiveCategories();

            Map<String, Object> result = new HashMap<>();
            result.put("categories", categories);
            result.put("totalCount", categories.size());
            result.put("timestamp", System.currentTimeMillis());

            log.info("获取媒体分类列表成功，分类数量：{}", categories.size());
            return Result.success("查询成功", result);

        } catch (Exception e) {
            log.error("获取支持的媒体分类失败：{}", e.getMessage());
            return Result.error("获取分类列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/file/info")
    public Result<Map<String, Object>> getFileInfo(@RequestParam String fileUrl) {
        try {
            if (!StringUtils.hasText(fileUrl)) {
                return Result.error("文件URL不能为空");
            }

            log.info("获取公开文件信息，URL：{}", fileUrl);

            Map<String, Object> fileInfo = fileUploadService.getPublicFileInfo(fileUrl);
            
            Map<String, Object> result = new HashMap<>();
            result.putAll(fileInfo);
            result.put("timestamp", System.currentTimeMillis());

            return Result.success("查询成功", result);

        } catch (Exception e) {
            log.error("获取文件信息失败，URL：{}，错误：{}", fileUrl, e.getMessage());
            return Result.error("获取文件信息失败：" + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     */
    @GetMapping("/file/exists")
    public Result<Boolean> checkFileExists(@RequestParam String fileUrl) {
        try {
            if (!StringUtils.hasText(fileUrl)) {
                return Result.error("文件URL不能为空");
            }

            log.debug("检查文件是否存在，URL：{}", fileUrl);

            boolean exists = fileUploadService.checkPublicFileExists(fileUrl);
            return Result.success(exists);

        } catch (Exception e) {
            log.error("检查文件是否存在失败，URL：{}，错误：{}", fileUrl, e.getMessage());
            return Result.error("检查文件失败：" + e.getMessage());
        }
    }
}