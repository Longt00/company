package com.manage.service;

import com.manage.dto.homepage.HomePageBackgroundRequest;
import com.manage.dto.homepage.HomePageBackgroundResponse;
import com.manage.entity.HomePageBackground;

import java.util.List;

/**
 * 主页背景管理服务接口
 *
 * @author System
 * @version 1.0
 */
public interface HomePageBackgroundService {

    /**
     * 创建新的主页背景
     *
     * @param request 背景请求信息
     * @param userId 操作用户ID
     * @return 创建的背景信息
     */
    HomePageBackgroundResponse createBackground(HomePageBackgroundRequest request, Long userId);

    /**
     * 更新主页背景
     *
     * @param id 背景ID
     * @param request 背景请求信息
     * @param userId 操作用户ID
     * @return 更新后的背景信息
     */
    HomePageBackgroundResponse updateBackground(Long id, HomePageBackgroundRequest request, Long userId);

    /**
     * 删除主页背景
     *
     * @param id 背景ID
     */
    void deleteBackground(Long id);

    /**
     * 启用/禁用主页背景
     *
     * @param id 背景ID
     * @param isActive 是否启用
     * @param userId 操作用户ID
     */
    void toggleBackgroundStatus(Long id, Boolean isActive, Long userId);

    /**
     * 获取当前主页背景（公开接口）
     *
     * @return 当前启用的背景信息
     */
    HomePageBackgroundResponse getCurrentBackground();

    /**
     * 获取背景详情
     *
     * @param id 背景ID
     * @return 背景详情
     */
    HomePageBackgroundResponse getBackgroundById(Long id);

    /**
     * 获取所有背景列表
     *
     * @return 背景列表
     */
    List<HomePageBackgroundResponse> getAllBackgrounds();

    /**
     * 获取所有启用的背景列表
     *
     * @return 启用的背景列表
     */
    List<HomePageBackgroundResponse> getActiveBackgrounds();

    /**
     * 根据类型获取背景列表
     *
     * @param backgroundType 背景类型
     * @return 指定类型的背景列表
     */
    List<HomePageBackgroundResponse> getBackgroundsByType(String backgroundType);

    /**
     * 设置指定背景为当前活跃背景（禁用其他同类型背景）
     *
     * @param id 背景ID
     * @param userId 操作用户ID
     */
    void setActiveBackground(Long id, Long userId);
}