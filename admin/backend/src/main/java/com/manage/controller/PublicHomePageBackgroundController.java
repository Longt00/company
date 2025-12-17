package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.homepage.HomePageBackgroundResponse;
import com.manage.service.HomePageBackgroundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公开主页背景控制器
 * 提供无需认证的主页背景访问接口
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/public/homepage/background")
@RequiredArgsConstructor
public class PublicHomePageBackgroundController {

    private final HomePageBackgroundService homePageBackgroundService;

    /**
     * 获取当前主页背景（主要接口）
     */
    @GetMapping("/current")
    public Result<HomePageBackgroundResponse> getCurrentBackground() {
        try {
            HomePageBackgroundResponse response = homePageBackgroundService.getCurrentBackground();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取当前主页背景失败：{}", e.getMessage(), e);
            return Result.error("获取背景失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前主页背景CSS样式
     */
    @GetMapping("/css")
    public Result<Map<String, Object>> getCurrentBackgroundCss() {
        try {
            HomePageBackgroundResponse background = homePageBackgroundService.getCurrentBackground();

            Map<String, Object> result = new HashMap<>();
            result.put("cssStyle", background.getCssStyle());
            result.put("backgroundType", background.getBackgroundType());
            result.put("backgroundImageUrl", background.getBackgroundImageUrl());
            result.put("backgroundColor", background.getBackgroundColor());
            result.put("gradientStartColor", background.getGradientStartColor());
            result.put("gradientEndColor", background.getGradientEndColor());
            result.put("gradientDirection", background.getGradientDirection());
            result.put("backgroundRepeat", background.getBackgroundRepeat());
            result.put("backgroundPosition", background.getBackgroundPosition());
            result.put("backgroundSize", background.getBackgroundSize());
            result.put("backgroundOpacity", background.getBackgroundOpacity());
            result.put("lastUpdated", background.getUpdateTime());
            result.put("timestamp", System.currentTimeMillis());

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取当前主页背景CSS失败：{}", e.getMessage(), e);
            return Result.error("获取背景CSS失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有启用的背景列表（公开访问）
     */
    @GetMapping("/active")
    public Result<List<HomePageBackgroundResponse>> getActiveBackgrounds() {
        try {
            List<HomePageBackgroundResponse> backgrounds = homePageBackgroundService.getActiveBackgrounds();
            return Result.success(backgrounds);
        } catch (Exception e) {
            log.error("获取启用背景列表失败：{}", e.getMessage(), e);
            return Result.error("获取背景列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据类型获取启用的背景列表（公开访问）
     */
    @GetMapping("/type/{backgroundType}")
    public Result<List<HomePageBackgroundResponse>> getActiveBackgroundsByType(@PathVariable String backgroundType) {
        try {
            List<HomePageBackgroundResponse> backgrounds = homePageBackgroundService.getBackgroundsByType(backgroundType);
            return Result.success(backgrounds);
        } catch (Exception e) {
            log.error("根据类型获取启用背景列表失败，类型：{}，错误：{}", backgroundType, e.getMessage(), e);
            return Result.error("获取背景列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取背景配置信息（用于前端配置）
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getBackgroundConfigInfo() {
        try {
            HomePageBackgroundResponse currentBackground = homePageBackgroundService.getCurrentBackground();
            List<HomePageBackgroundResponse> activeBackgrounds = homePageBackgroundService.getActiveBackgrounds();

            Map<String, Object> info = new HashMap<>();
            info.put("hasCustomBackground", currentBackground.getId() != 0);
            info.put("currentBackgroundType", currentBackground.getBackgroundType());
            info.put("totalActiveBackgrounds", activeBackgrounds.size());
            info.put("currentBackgroundId", currentBackground.getId());
            info.put("lastUpdated", currentBackground.getUpdateTime());
            Map<String, String> apiEndpoints = new HashMap<>();
            apiEndpoints.put("currentBackground", "/api/public/homepage/background/current");
            apiEndpoints.put("cssStyle", "/api/public/homepage/background/css");
            apiEndpoints.put("activeBackgrounds", "/api/public/homepage/background/active");
            info.put("apiEndpoints", apiEndpoints);
            info.put("timestamp", System.currentTimeMillis());

            return Result.success(info);
        } catch (Exception e) {
            log.error("获取背景配置信息失败：{}", e.getMessage(), e);
            return Result.error("获取配置信息失败：" + e.getMessage());
        }
    }
}