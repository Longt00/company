package com.manage.controller;

import com.manage.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页图标配置控制器
 * 提供无需认证的公开API接口，用于获取首页图标配置
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/public/config")
@RequiredArgsConstructor
public class HomePageIconController {

    private static final String HOME_ICONS_CONFIG_FILE = "static/config/home-icons.json";
    private static final String DEFAULT_HOME_ICONS_CONFIG_FILE = "static/config/default-home-icons.json";

    /**
     * 获取首页图标配置
     *
     * @return 首页图标配置JSON
     */
    @GetMapping(value = "/home-icons.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> getHomeIconsConfig() {
        try {
            Resource resource = new ClassPathResource(HOME_ICONS_CONFIG_FILE);

            // 如果配置文件不存在，返回默认配置
            if (!resource.exists()) {
                log.info("自定义图标配置文件不存在，使用默认配置");
                resource = new ClassPathResource(DEFAULT_HOME_ICONS_CONFIG_FILE);
            }

            if (!resource.exists()) {
                // 如果默认配置也不存在，返回基础默认配置
                log.warn("默认图标配置文件不存在，返回基础配置");
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createDefaultConfigResource());
            }

            log.info("成功加载首页图标配置文件");
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(resource);

        } catch (Exception e) {
            log.error("获取首页图标配置失败：{}", e.getMessage(), e);
            // 发生异常时返回基础配置
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createDefaultConfigResource());
        }
    }

    /**
     * 获取图标配置信息（非JSON格式，返回信息）
     *
     * @return 图标配置信息
     */
    @GetMapping("/home-icons/info")
    public Result<Map<String, Object>> getHomeIconsInfo() {
        try {
            Resource resource = new ClassPathResource(HOME_ICONS_CONFIG_FILE);

            Map<String, Object> info = new HashMap<>();

            if (resource.exists()) {
                info.put("exists", true);
                info.put("lastModified", resource.lastModified());
                info.put("configType", "custom");
                info.put("message", "使用自定义图标配置");
            } else {
                resource = new ClassPathResource(DEFAULT_HOME_ICONS_CONFIG_FILE);
                if (resource.exists()) {
                    info.put("exists", true);
                    info.put("lastModified", resource.lastModified());
                    info.put("configType", "default");
                    info.put("message", "使用默认图标配置");
                } else {
                    info.put("exists", false);
                    info.put("configType", "basic");
                    info.put("message", "使用基础内置配置");
                }
            }

            info.put("apiEndpoint", "/api/public/config/home-icons.json");
            info.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            return Result.success(info);

        } catch (Exception e) {
            log.error("获取图标配置信息失败：{}", e.getMessage(), e);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("exists", false);
            errorInfo.put("configType", "error");
            errorInfo.put("message", "获取配置信息失败，将使用基础配置");
            errorInfo.put("apiEndpoint", "/api/public/config/home-icons.json");
            errorInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            return Result.success(errorInfo);
        }
    }

    /**
     * 创建基础默认配置资源
     *
     * @return 默认配置资源
     */
    private Resource createDefaultConfigResource() {
        String defaultConfig = createBasicHomeIconsConfig();
        return new org.springframework.core.io.ByteArrayResource(
            defaultConfig.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 创建基础首页图标配置JSON
     *
     * @return 基础配置JSON字符串
     */
    private String createBasicHomeIconsConfig() {
        return "{\n" +
               "  \"title\": \"首页图标配置\",\n" +
               "  \"description\": \"用于网站首页展示的4个特色图标\",\n" +
               "  \"icons\": [\n" +
               "    {\n" +
               "      \"position\": 1,\n" +
               "      \"title\": \"COMPANY\",\n" +
               "      \"description\": \"展示公司或团队相关图标\",\n" +
               "      \"iconType\": \"icon-company\",\n" +
               "      \"defaultIcon\": \"/images/default-icons/company.png\",\n" +
               "      \"customIcon\": \"/uploads/icons/custom-company-icon.png\",\n" +
               "      \"enabled\": true\n" +
               "    },\n" +
               "    {\n" +
               "      \"position\": 2,\n" +
               "      \"title\": \"TEAM\",\n" +
               "      \"description\": \"展示团队或人员相关图标\",\n" +
               "      \"iconType\": \"icon-team\",\n" +
               "      \"defaultIcon\": \"/images/default-icons/team.png\",\n" +
               "      \"customIcon\": \"/uploads/icons/custom-team-icon.png\",\n" +
               "      \"enabled\": true\n" +
               "    },\n" +
               "    {\n" +
               "      \"position\": 3,\n" +
               "      \"title\": \"PRICES\",\n" +
               "      \"description\": \"展示价格或成本相关图标\",\n" +
               "      \"iconType\": \"icon-prices\",\n" +
               "      \"defaultIcon\": \"/images/default-icons/prices.png\",\n" +
               "      \"customIcon\": \"/uploads/icons/custom-prices-icon.png\",\n" +
               "      \"enabled\": true\n" +
               "    },\n" +
               "    {\n" +
               "      \"position\": 4,\n" +
               "      \"title\": \"QUALITY\",\n" +
               "      \"description\": \"展示质量或认证相关图标\",\n" +
               "      \"iconType\": \"icon-quality\",\n" +
               "      \"defaultIcon\": \"/images/default-icons/quality.png\",\n" +
               "      \"customIcon\": \"/uploads/icons/custom-quality-icon.png\",\n" +
               "      \"enabled\": true\n" +
               "    }\n" +
               "  ],\n" +
               "  \"displayOptions\": {\n" +
               "    \"gridColumns\": 4,\n" +
               "    \"showTitles\": true,\n" +
               "    \"showDescriptions\": false,\n" +
               "    \"borderRadius\": 8,\n" +
               "    \"padding\": 16,\n" +
               "    \"gap\": 24\n" +
               "  },\n" +
               "  \"version\": \"1.0.0\",\n" +
               "  \"lastUpdated\": \"" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\"\n" +
               "}";
    }
}