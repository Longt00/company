package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.homepage.HomePageBackgroundRequest;
import com.manage.dto.homepage.HomePageBackgroundResponse;
import com.manage.dto.homepage.TextColorRequest;
import com.manage.dto.homepage.TextColorResponse;
import com.manage.service.HomePageBackgroundService;
import com.manage.util.JwtUtil;
import com.manage.util.ColorContrastUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 主页背景管理控制器
 * 提供主页背景的增删改查和状态管理功能
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/homepage/background")
@RequiredArgsConstructor
public class HomePageBackgroundController {

    private final HomePageBackgroundService homePageBackgroundService;
    private final JwtUtil jwtUtil;

    /**
     * 创建新的主页背景
     */
    @PostMapping
    public Result<HomePageBackgroundResponse> createBackground(
            @Valid @RequestBody HomePageBackgroundRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            HomePageBackgroundResponse response = homePageBackgroundService.createBackground(request, userId);
            return Result.success("背景创建成功", response);
        } catch (Exception e) {
            log.error("创建主页背景失败：{}", e.getMessage(), e);
            return Result.error("创建背景失败：" + e.getMessage());
        }
    }

    /**
     * 更新主页背景
     */
    @PutMapping("/{id}")
    public Result<HomePageBackgroundResponse> updateBackground(
            @PathVariable Long id,
            @Valid @RequestBody HomePageBackgroundRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            HomePageBackgroundResponse response = homePageBackgroundService.updateBackground(id, request, userId);
            return Result.success("背景更新成功", response);
        } catch (Exception e) {
            log.error("更新主页背景失败，ID：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("更新背景失败：" + e.getMessage());
        }
    }

    /**
     * 删除主页背景
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBackground(@PathVariable Long id) {
        try {
            homePageBackgroundService.deleteBackground(id);
            return Result.success("背景删除成功");
        } catch (Exception e) {
            log.error("删除主页背景失败，ID：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("删除背景失败：" + e.getMessage());
        }
    }

    /**
     * 启用/禁用主页背景
     */
    @PutMapping("/{id}/status")
    public Result<Void> toggleBackgroundStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            homePageBackgroundService.toggleBackgroundStatus(id, isActive, userId);
            return Result.success("背景状态切换成功");
        } catch (Exception e) {
            log.error("切换背景状态失败，ID：{}，状态：{}，错误：{}", id, isActive, e.getMessage(), e);
            return Result.error("状态切换失败：" + e.getMessage());
        }
    }

    /**
     * 设置指定背景为当前活跃背景
     */
    @PutMapping("/{id}/active")
    public Result<Void> setActiveBackground(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            homePageBackgroundService.setActiveBackground(id, userId);
            return Result.success("活跃背景设置成功");
        } catch (Exception e) {
            log.error("设置活跃背景失败，ID：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("设置活跃背景失败：" + e.getMessage());
        }
    }

    /**
     * 获取背景详情
     */
    @GetMapping("/{id}")
    public Result<HomePageBackgroundResponse> getBackgroundById(@PathVariable Long id) {
        try {
            HomePageBackgroundResponse response = homePageBackgroundService.getBackgroundById(id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取背景详情失败，ID：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("获取背景详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有背景列表
     */
    @GetMapping
    public Result<List<HomePageBackgroundResponse>> getAllBackgrounds() {
        try {
            List<HomePageBackgroundResponse> backgrounds = homePageBackgroundService.getAllBackgrounds();
            return Result.success(backgrounds);
        } catch (Exception e) {
            log.error("获取背景列表失败：{}", e.getMessage(), e);
            return Result.error("获取背景列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有启用的背景列表
     */
    @GetMapping("/active")
    public Result<List<HomePageBackgroundResponse>> getActiveBackgrounds() {
        try {
            List<HomePageBackgroundResponse> backgrounds = homePageBackgroundService.getActiveBackgrounds();
            return Result.success(backgrounds);
        } catch (Exception e) {
            log.error("获取启用背景列表失败：{}", e.getMessage(), e);
            return Result.error("获取启用背景列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据类型获取背景列表
     */
    @GetMapping("/type/{backgroundType}")
    public Result<List<HomePageBackgroundResponse>> getBackgroundsByType(@PathVariable String backgroundType) {
        try {
            List<HomePageBackgroundResponse> backgrounds = homePageBackgroundService.getBackgroundsByType(backgroundType);
            return Result.success(backgrounds);
        } catch (Exception e) {
            log.error("根据类型获取背景列表失败，类型：{}，错误：{}", backgroundType, e.getMessage(), e);
            return Result.error("获取背景列表失败：" + e.getMessage());
        }
    }

    // ========== 字体颜色管理接口 ==========

    /**
     * 设置字体颜色配置
     */
    @PutMapping("/{id}/text-color")
    public Result<TextColorResponse> setTextColor(
            @PathVariable Long id,
            @Valid @RequestBody TextColorRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            TextColorResponse response = homePageBackgroundService.setTextColor(id, request, userId);
            return Result.success("字体颜色设置成功", response);
        } catch (Exception e) {
            log.error("设置字体颜色失败，背景ID：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("设置字体颜色失败：" + e.getMessage());
        }
    }

    /**
     * 获取字体颜色配置
     */
    @GetMapping("/{id}/text-color")
    public Result<TextColorResponse> getTextColor(@PathVariable Long id) {
        try {
            TextColorResponse response = homePageBackgroundService.getTextColor(id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取字体颜色配置失败，背景ID：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("获取字体颜色配置失败：" + e.getMessage());
        }
    }

    /**
     * 自动计算字体颜色
     */
    @PostMapping("/{id}/auto-text-color")
    public Result<TextColorResponse> calculateAutoTextColor(@PathVariable Long id) {
        try {
            TextColorResponse response = homePageBackgroundService.calculateAutoTextColor(id);
            return Result.success("自动计算字体颜色成功", response);
        } catch (Exception e) {
            log.error("自动计算字体颜色失败，背景ID：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("自动计算字体颜色失败：" + e.getMessage());
        }
    }

    /**
     * 批量自动计算字体颜色
     */
    @PostMapping("/batch/auto-text-color")
    public Result<List<TextColorResponse>> batchCalculateAutoTextColor() {
        try {
            List<TextColorResponse> responses = homePageBackgroundService.batchCalculateAutoTextColor();
            return Result.success("批量自动计算字体颜色成功", responses);
        } catch (Exception e) {
            log.error("批量自动计算字体颜色失败，错误：{}", e.getMessage(), e);
            return Result.error("批量自动计算字体颜色失败：" + e.getMessage());
        }
    }

    /**
     * 验证颜色对比度
     */
    @PostMapping("/validate-contrast")
    public Result<Object> validateColorContrast(
            @RequestParam String textColor,
            @RequestParam String backgroundColor,
            @RequestParam(defaultValue = "AA") String wcagLevel,
            @RequestParam(defaultValue = "normal") String textSize) {
        try {
            boolean isValid = ColorContrastUtil.meetsWCAGStandards(textColor, backgroundColor, wcagLevel, textSize);
            double contrast = ColorContrastUtil.getContrast(textColor, backgroundColor);

            Object result = new Object() {
                public final boolean isValid = isValid;
                public final double contrast = contrast;
                public final String wcagLevel = wcagLevel;
                public final String textSize = textSize;
                public final String recommendation = ColorContrastUtil.calculateOptimalTextColor(backgroundColor);
            };

            return Result.success(result);
        } catch (Exception e) {
            log.error("验证颜色对比度失败，错误：{}", e.getMessage(), e);
            return Result.error("验证颜色对比度失败：" + e.getMessage());
        }
    }

    /**
     * 获取推荐字体颜色
     */
    @GetMapping("/recommended-colors/{backgroundColor}")
    public Result<Object> getRecommendedTextColors(@PathVariable String backgroundColor) {
        try {
            if (!ColorContrastUtil.isValidHexColor(backgroundColor)) {
                return Result.error("无效的背景颜色格式，请使用十六进制格式（如 #ffffff）");
            }

            ColorContrastUtil.TextColorScheme scheme = ColorContrastUtil.generateColorScheme(backgroundColor);

            Object result = new Object() {
                public final String backgroundColor = backgroundColor;
                public final String primaryTextColor = scheme.getPrimary();
                public final String secondaryTextColor = scheme.getSecondary();
                public final String linkColor = scheme.getLink();
                public final String accentColor = scheme.getAccent();
                public final Double primaryContrast = ColorContrastUtil.getContrast(scheme.getPrimary(), backgroundColor);
                public final Double secondaryContrast = ColorContrastUtil.getContrast(scheme.getSecondary(), backgroundColor);
                public final Double linkContrast = ColorContrastUtil.getContrast(scheme.getLink(), backgroundColor);
            };

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取推荐字体颜色失败，背景颜色：{}，错误：{}", backgroundColor, e.getMessage(), e);
            return Result.error("获取推荐字体颜色失败：" + e.getMessage());
        }
    }

    /**
     * 从JWT Token中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("用户认证失败");
    }
}