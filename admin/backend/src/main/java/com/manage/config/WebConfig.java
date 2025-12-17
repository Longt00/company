package com.manage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC 配置
 * 
 * @author System
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    /**
     * 配置全局 CORS 规则
     * 确保静态资源路径 /api/files/** 支持跨域访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 配置静态资源映射
     * 将 /api/files/** 映射到本地 uploads 目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取绝对路径
        File uploadDir = new File(uploadPath);
        String absolutePath = uploadDir.getAbsolutePath();
        
        // 映射 /api/files/** 到本地文件目录
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations("file:" + absolutePath + File.separator)
                .setCachePeriod(3600);
    }
}
