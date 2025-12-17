package com.manage.service.impl;

import com.manage.dto.homepage.HomePageBackgroundRequest;
import com.manage.dto.homepage.HomePageBackgroundResponse;
import com.manage.entity.HomePageBackground;
import com.manage.repository.HomePageBackgroundRepository;
import com.manage.service.HomePageBackgroundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 主页背景管理服务实现类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomePageBackgroundServiceImpl implements HomePageBackgroundService {

    private final HomePageBackgroundRepository homePageBackgroundRepository;

    @Override
    @Transactional
    public HomePageBackgroundResponse createBackground(HomePageBackgroundRequest request, Long userId) {
        log.info("创建主页背景，类型：{}，操作用户：{}", request.getBackgroundType(), userId);

        // 验证背景类型
        validateBackgroundType(request.getBackgroundType());

        // 创建背景实体
        HomePageBackground background = new HomePageBackground();
        BeanUtils.copyProperties(request, background);
        background.setCreatedBy(userId);
        background.setUpdatedBy(userId);

        // 如果设置为启用，先禁用其他同类型的背景
        if (Boolean.TRUE.equals(request.getIsActive())) {
            disableOtherBackgroundsOfSameType(request.getBackgroundType());
        }

        // 保存背景
        HomePageBackground savedBackground = homePageBackgroundRepository.save(background);
        log.info("主页背景创建成功，ID：{}", savedBackground.getId());

        return convertToResponse(savedBackground);
    }

    @Override
    @Transactional
    public HomePageBackgroundResponse updateBackground(Long id, HomePageBackgroundRequest request, Long userId) {
        log.info("更新主页背景，ID：{}，操作用户：{}", id, userId);

        // 验证背景类型
        validateBackgroundType(request.getBackgroundType());

        // 查找背景
        HomePageBackground background = homePageBackgroundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("背景不存在，ID：" + id));

        // 更新背景信息
        BeanUtils.copyProperties(request, background, "id", "createTime");
        background.setUpdatedBy(userId);

        // 如果设置为启用，先禁用其他同类型的背景
        if (Boolean.TRUE.equals(request.getIsActive()) && !Boolean.TRUE.equals(background.getIsActive())) {
            disableOtherBackgroundsOfSameType(request.getBackgroundType());
        }

        // 保存更新
        HomePageBackground savedBackground = homePageBackgroundRepository.save(background);
        log.info("主页背景更新成功，ID：{}", savedBackground.getId());

        return convertToResponse(savedBackground);
    }

    @Override
    @Transactional
    public void deleteBackground(Long id) {
        log.info("删除主页背景，ID：{}", id);

        HomePageBackground background = homePageBackgroundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("背景不存在，ID：" + id));

        homePageBackgroundRepository.delete(background);
        log.info("主页背景删除成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void toggleBackgroundStatus(Long id, Boolean isActive, Long userId) {
        log.info("切换背景状态，ID：{}，状态：{}，操作用户：{}", id, isActive, userId);

        HomePageBackground background = homePageBackgroundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("背景不存在，ID：" + id));

        if (Boolean.TRUE.equals(isActive) && !Boolean.TRUE.equals(background.getIsActive())) {
            // 如果要启用，先禁用其他同类型的背景
            disableOtherBackgroundsOfSameType(background.getBackgroundType());
        }

        background.setIsActive(isActive);
        background.setUpdatedBy(userId);
        homePageBackgroundRepository.save(background);

        log.info("背景状态切换成功，ID：{}，新状态：{}", id, isActive);
    }

    @Override
    public HomePageBackgroundResponse getCurrentBackground() {
        Optional<HomePageBackground> activeBackground = homePageBackgroundRepository.findActiveBackground();

        if (activeBackground.isPresent()) {
            HomePageBackgroundResponse response = convertToResponse(activeBackground.get());
            response.generateCssStyle();
            return response;
        }

        // 返回默认背景配置
        return getDefaultBackgroundResponse();
    }

    @Override
    public HomePageBackgroundResponse getBackgroundById(Long id) {
        HomePageBackground background = homePageBackgroundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("背景不存在，ID：" + id));

        HomePageBackgroundResponse response = convertToResponse(background);
        response.generateCssStyle();
        return response;
    }

    @Override
    public List<HomePageBackgroundResponse> getAllBackgrounds() {
        List<HomePageBackground> backgrounds = homePageBackgroundRepository.findAll();
        return backgrounds.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HomePageBackgroundResponse> getActiveBackgrounds() {
        List<HomePageBackground> backgrounds = homePageBackgroundRepository.findAllActiveBackgrounds();
        return backgrounds.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HomePageBackgroundResponse> getBackgroundsByType(String backgroundType) {
        validateBackgroundType(backgroundType);
        List<HomePageBackground> backgrounds = homePageBackgroundRepository.findActiveBackgroundsByType(backgroundType);
        return backgrounds.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void setActiveBackground(Long id, Long userId) {
        log.info("设置活跃背景，ID：{}，操作用户：{}", id, userId);

        HomePageBackground background = homePageBackgroundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("背景不存在，ID：" + id));

        // 禁用其他同类型的背景
        disableOtherBackgroundsOfSameType(background.getBackgroundType());

        // 启用当前背景
        background.setIsActive(true);
        background.setUpdatedBy(userId);
        homePageBackgroundRepository.save(background);

        log.info("活跃背景设置成功，ID：{}", id);
    }

    /**
     * 禁用其他同类型的背景
     */
    private void disableOtherBackgroundsOfSameType(String backgroundType) {
        List<HomePageBackground> sameTypeBackgrounds = homePageBackgroundRepository.findActiveBackgroundsByType(backgroundType);
        sameTypeBackgrounds.forEach(bg -> {
            bg.setIsActive(false);
            homePageBackgroundRepository.save(bg);
        });
    }

    /**
     * 验证背景类型
     */
    private void validateBackgroundType(String backgroundType) {
        if (!"image".equals(backgroundType) && !"color".equals(backgroundType) && !"gradient".equals(backgroundType)) {
            throw new IllegalArgumentException("无效的背景类型，必须是：image、color、gradient");
        }
    }

    /**
     * 转换为响应DTO
     */
    private HomePageBackgroundResponse convertToResponse(HomePageBackground background) {
        HomePageBackgroundResponse response = new HomePageBackgroundResponse();
        BeanUtils.copyProperties(background, response);
        return response;
    }

    /**
     * 获取默认背景响应
     */
    private HomePageBackgroundResponse getDefaultBackgroundResponse() {
        HomePageBackgroundResponse defaultBackground = new HomePageBackgroundResponse();
        defaultBackground.setId(0L);
        defaultBackground.setBackgroundType("color");
        defaultBackground.setBackgroundColor("#f5f5f5");
        defaultBackground.setBackgroundRepeat("no-repeat");
        defaultBackground.setBackgroundPosition("center");
        defaultBackground.setBackgroundSize("cover");
        defaultBackground.setBackgroundOpacity(1.0);
        defaultBackground.setIsActive(true);
        defaultBackground.setDescription("默认背景");
        defaultBackground.generateCssStyle();
        return defaultBackground;
    }
}