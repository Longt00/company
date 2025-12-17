package com.manage.controller;

import com.manage.entity.OperationLog;
import com.manage.service.CompanyInfoService;
import com.manage.service.FileUploadService;
import com.manage.service.OperationLogService;
import com.manage.dto.company.CompanyInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test/upload")
public class TestUploadController {

    private static final Logger log = LoggerFactory.getLogger(TestUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件为空");
                return ResponseEntity.badRequest().body(result);
            }

            String originalFilename = file.getOriginalFilename();
            log.info("开始上传测试图片：{}", originalFilename);

            // 使用文件上传服务上传到logo分类
            String fileUrl = fileUploadService.uploadFile(file, "test-images");

            result.put("success", true);
            result.put("message", "图片上传成功");
            result.put("fileUrl", fileUrl);
            result.put("originalFilename", originalFilename);
            result.put("size", file.getSize());

            // 记录操作日志
            operationLogService.log(
                "UPLOAD",
                "TEST_UPLOAD",
                "测试图片上传",
                "FILE",
                0L,
                "上传文件: " + originalFilename
            );

            log.info("测试图片上传成功：{}", fileUrl);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("测试图片上传失败", e);
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @PostMapping("/logo")
    public ResponseEntity<Map<String, Object>> uploadLogo(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件为空");
                return ResponseEntity.badRequest().body(result);
            }

            String originalFilename = file.getOriginalFilename();
            log.info("开始测试Logo上传：{}", originalFilename);

            // 上传文件
            String fileUrl = fileUploadService.uploadFile(file, "logo");

            // 更新公司信息中的Logo URL (使用测试用户ID 37)
            CompanyInfoResponse companyInfoResponse = companyInfoService.updateCompanyLogo(fileUrl, 37L);

            result.put("success", true);
            result.put("message", "Logo上传并更新成功");
            result.put("fileUrl", fileUrl);
            result.put("originalFilename", originalFilename);
            result.put("size", file.getSize());
            result.put("companyInfo", companyInfoResponse);

            // 记录操作日志
            operationLogService.log(
                "UPLOAD",
                "TEST_LOGO",
                "测试Logo上传并更新公司信息",
                "CompanyInfo",
                companyInfoResponse.getId(),
                "上传文件: " + originalFilename
            );

            log.info("测试Logo上传并更新成功：{}", fileUrl);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("测试Logo上传失败", e);
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @PostMapping("/video")
    public ResponseEntity<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件为空");
                return ResponseEntity.badRequest().body(result);
            }

            String originalFilename = file.getOriginalFilename();
            log.info("开始上传测试视频：{}", originalFilename);

            // 使用文件上传服务上传到video分类
            String fileUrl = fileUploadService.uploadFile(file, "video");

            result.put("success", true);
            result.put("message", "视频上传成功");
            result.put("fileUrl", fileUrl);
            result.put("originalFilename", originalFilename);
            result.put("size", file.getSize());

            // 记录操作日志
            operationLogService.log(
                "UPLOAD",
                "TEST_UPLOAD",
                "测试视频上传",
                "FILE",
                0L,
                "上传文件: " + originalFilename
            );

            log.info("测试视频上传成功：{}", fileUrl);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("测试视频上传失败", e);
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/check-file")
    public ResponseEntity<Map<String, Object>> checkFile(@RequestParam("fileName") String fileName) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查文件是否存在于本地存储中
            boolean exists = fileUploadService.fileExists(fileName);

            result.put("success", true);
            result.put("fileName", fileName);
            result.put("exists", exists);

            if (exists) {
                result.put("message", "文件存在");
            } else {
                result.put("message", "文件不存在");
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("检查文件失败", e);
            result.put("success", false);
            result.put("message", "检查失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/list-uploads")
    public ResponseEntity<Map<String, Object>> listUploads() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取上传目录
            String uploadPath = System.getProperty("user.dir") + "/uploads";
            Path uploadDir = Paths.get(uploadPath);

            if (!Files.exists(uploadDir)) {
                result.put("success", true);
                result.put("message", "上传目录不存在");
                result.put("uploadPath", uploadPath);
                result.put("files", new String[0]);
                return ResponseEntity.ok(result);
            }

            File[] files = uploadDir.toFile().listFiles();
            String[] fileNames = new String[0];

            if (files != null) {
                fileNames = new String[files.length];
                for (int i = 0; i < files.length; i++) {
                    fileNames[i] = files[i].getName();
                }
            }

            result.put("success", true);
            result.put("message", "获取文件列表成功");
            result.put("uploadPath", uploadPath);
            result.put("fileCount", fileNames.length);
            result.put("files", fileNames);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("获取文件列表失败", e);
            result.put("success", false);
            result.put("message", "获取文件列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}