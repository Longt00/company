package com.manage.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * 启动配置验证器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Configuration
public class StartupConfigValidator implements ApplicationRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始验证系统配置...");

        // 验证数据库连接
        validateDatabaseConnection();

        // 验证本地存储配置
        validateLocalStorageConfiguration();

        // 验证JWT配置
        validateJwtConfiguration();

        log.info("系统配置验证完成！");
    }

    /**
     * 验证数据库连接
     */
    private void validateDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("数据库连接验证成功！");
            log.info("数据库产品名称: {}", metaData.getDatabaseProductName());
            log.info("数据库版本: {}", metaData.getDatabaseProductVersion());
            log.info("驱动名称: {}", metaData.getDriverName());
            log.info("驱动版本: {}", metaData.getDriverVersion());
        } catch (Exception e) {
            log.error("数据库连接验证失败: {}", e.getMessage());
            throw new RuntimeException("数据库连接配置错误", e);
        }
    }

    /**
     * 验证本地存储配置
     */
    private void validateLocalStorageConfiguration() {
        String uploadPath = environment.getProperty("file.upload.path");
        String accessUrl = environment.getProperty("file.access.url");

        if (uploadPath == null || uploadPath.trim().isEmpty()) {
            log.error("文件上传路径未配置！");
            throw new RuntimeException("本地存储配置错误");
        } else {
            java.io.File uploadDir = new java.io.File(uploadPath);
            if (!uploadDir.exists()) {
                if (uploadDir.mkdirs()) {
                    log.info("创建上传目录成功: {}", uploadPath);
                } else {
                    log.error("创建上传目录失败: {}", uploadPath);
                    throw new RuntimeException("无法创建上传目录");
                }
            }
            log.info("文件上传路径: {}", uploadPath);
        }

        if (accessUrl == null || accessUrl.trim().isEmpty()) {
            log.warn("文件访问URL未配置，使用默认值");
        } else {
            log.info("文件访问URL: {}", accessUrl);
        }

        log.info("本地存储配置验证完成");
    }

    /**
     * 验证JWT配置
     */
    private void validateJwtConfiguration() {
        String secret = environment.getProperty("jwt.secret");
        String expiration = environment.getProperty("jwt.expiration");

        if (secret == null || secret.trim().isEmpty()) {
            log.error("JWT密钥未配置！");
            throw new RuntimeException("JWT密钥配置错误");
        } else {
            log.info("JWT密钥已配置 (长度: {})", secret.length());
        }

        if (expiration == null || expiration.trim().isEmpty()) {
            log.warn("JWT过期时间未配置，使用默认值24小时");
        } else {
            log.info("JWT过期时间: {} 毫秒", expiration);
        }

        log.info("JWT配置验证完成");
    }
}