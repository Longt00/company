package com.manage.controller;

import com.manage.common.Result;
import com.manage.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试删除功能的控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestDeleteController {

    private final FileUploadService fileUploadService;

    /**
     * 测试删除功能
     *
     * @param fileUrl 文件URL
     * @return 删除结果和调试信息
     */
    @PostMapping("/delete")
    public Result<Map<String, Object>> testDelete(@RequestParam("fileUrl") String fileUrl) {
        Map<String, Object> debugInfo = new HashMap<>();

        try {
            log.info("开始测试删除功能，文件URL: {}", fileUrl);
            debugInfo.put("fileUrl", fileUrl);
            debugInfo.put("timestamp", System.currentTimeMillis());

            // 1. 检查文件是否存在
            boolean fileExists = fileUploadService.fileExists(fileUrl);
            debugInfo.put("fileExists", fileExists);
            log.info("文件存在检查结果: {}", fileExists);

            // 2. 尝试删除文件
            boolean deleteResult = fileUploadService.deleteFile(fileUrl);
            debugInfo.put("deleteResult", deleteResult);
            log.info("文件删除结果: {}", deleteResult);

            // 3. 再次检查文件是否存在
            boolean fileExistsAfterDelete = fileUploadService.fileExists(fileUrl);
            debugInfo.put("fileExistsAfterDelete", fileExistsAfterDelete);
            log.info("删除后文件存在检查结果: {}", fileExistsAfterDelete);

            debugInfo.put("success", deleteResult && !fileExistsAfterDelete);

            return Result.success("测试完成", debugInfo);

        } catch (Exception e) {
            log.error("测试删除功能失败", e);
            debugInfo.put("error", e.getMessage());
            debugInfo.put("stackTrace", e.getStackTrace());
            return Result.error("测试失败: " + e.getMessage(), debugInfo);
        }
    }

    /**
     * 检查文件状态
     *
     * @param fileUrl 文件URL
     * @return 文件状态信息
     */
    @GetMapping("/file-status")
    public Result<Map<String, Object>> checkFileStatus(@RequestParam("fileUrl") String fileUrl) {
        Map<String, Object> statusInfo = new HashMap<>();

        try {
            log.info("检查文件状态，文件URL: {}", fileUrl);
            statusInfo.put("fileUrl", fileUrl);

            // 检查物理文件是否存在
            boolean fileExists = fileUploadService.fileExists(fileUrl);
            statusInfo.put("physicalFileExists", fileExists);

            // 获取文件信息（这会查询数据库）
            try {
                Map<String, Object> fileInfo = fileUploadService.getFileInfo(fileUrl);
                statusInfo.put("databaseRecordExists", true);
                statusInfo.put("fileInfo", fileInfo);
            } catch (Exception e) {
                statusInfo.put("databaseRecordExists", false);
                statusInfo.put("databaseError", e.getMessage());
            }

            return Result.success("状态检查完成", statusInfo);

        } catch (Exception e) {
            log.error("检查文件状态失败", e);
            statusInfo.put("error", e.getMessage());
            return Result.error("状态检查失败: " + e.getMessage(), statusInfo);
        }
    }
}