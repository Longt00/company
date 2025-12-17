package com.manage.controller;

import com.manage.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment environment;

    /**
     * 健康检查接口
     *
     * @return 系统健康状态
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();

        try {
            // 检查数据库连接
            try (Connection connection = dataSource.getConnection()) {
                healthInfo.put("database", "UP");
                healthInfo.put("databaseUrl", connection.getMetaData().getURL());
            } catch (Exception e) {
                healthInfo.put("database", "DOWN");
                healthInfo.put("databaseError", e.getMessage());
            }

            // 检查应用状态
            healthInfo.put("application", "UP");
            healthInfo.put("timestamp", LocalDateTime.now());
            healthInfo.put("version", "1.0.0");
            healthInfo.put("profile", environment.getActiveProfiles());

            // 检查本地存储配置
            String uploadPath = environment.getProperty("file.upload.path");
            String accessUrl = environment.getProperty("file.access.url");
            if (uploadPath != null && accessUrl != null) {
                java.io.File uploadDir = new java.io.File(uploadPath);
                if (uploadDir.exists() && uploadDir.isDirectory()) {
                    healthInfo.put("localStorage", "UP");
                    healthInfo.put("uploadPath", uploadPath);
                    healthInfo.put("accessUrl", accessUrl);
                } else {
                    healthInfo.put("localStorage", "DOWN");
                    healthInfo.put("localStorageError", "Upload directory does not exist");
                }
            } else {
                healthInfo.put("localStorage", "NOT_CONFIGURED");
            }

            // 检查JWT配置
            String jwtSecret = environment.getProperty("jwt.secret");
            if (jwtSecret != null && !jwtSecret.trim().isEmpty()) {
                healthInfo.put("jwt", "CONFIGURED");
            } else {
                healthInfo.put("jwt", "NOT_CONFIGURED");
            }

            return Result.success("系统运行正常", healthInfo);

        } catch (Exception e) {
            log.error("健康检查失败", e);
            healthInfo.put("application", "DOWN");
            healthInfo.put("error", e.getMessage());
            return Result.error("系统异常", healthInfo);
        }
    }

    /**
     * 简单的存活检查接口
     *
     * @return 存活状态
     */
    @GetMapping("/alive")
    public Result<String> alive() {
        return Result.success("应用存活", "OK");
    }
}