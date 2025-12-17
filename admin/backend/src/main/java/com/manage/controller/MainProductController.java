package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.product.ProductQueryRequest;
import com.manage.dto.product.ProductResponse;
import com.manage.entity.User;
import com.manage.service.OperationLogService;
import com.manage.service.ProductService;
import com.manage.service.UserService;
import com.manage.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主要产品管理控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/main-products")
@Validated
@RequiredArgsConstructor
public class MainProductController {

    private final ProductService productService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final OperationLogService operationLogService;

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userService.findByUsername(username);
            return user.getId();
        }
        throw new RuntimeException("未找到当前用户信息");
    }

    /**
     * 分页查询主要产品列表
     */
    @PostMapping
    public Result<Page<ProductResponse>> getMainProducts(@Valid @RequestBody ProductQueryRequest queryRequest) {
        try {
            // 查询主要产品
            List<ProductResponse> mainProducts = productService.getMainProducts();

            // 手动分页处理
            int start = queryRequest.getPage() * queryRequest.getSize();
            int end = Math.min(start + queryRequest.getSize(), mainProducts.size());

            List<ProductResponse> pageContent;
            if (start < mainProducts.size()) {
                pageContent = mainProducts.subList(start, end);
            } else {
                pageContent = new ArrayList<>();
            }

            // 模拟分页结果
            Page<ProductResponse> response = new org.springframework.data.domain.PageImpl<>(
                pageContent,
                org.springframework.data.domain.PageRequest.of(queryRequest.getPage(), queryRequest.getSize()),
                mainProducts.size()
            );

            return Result.success(response);
        } catch (Exception e) {
            log.error("分页查询主要产品列表失败，错误：{}", e.getMessage());
            return Result.error("查询主要产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取上架主要产品列表
     */
    @GetMapping("/active")
    public Result<List<ProductResponse>> getActiveMainProducts() {
        try {
            List<ProductResponse> response = productService.getActiveMainProducts();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取上架主要产品列表失败，错误：{}", e.getMessage());
            return Result.error("获取上架主要产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 设置产品为主要产品
     */
    @PutMapping("/{id}")
    public Result<ProductResponse> setAsMain(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.setAsMain(id, userId);

            // 记录操作日志
            operationLogService.log(
                "SET_MAIN",
                "MAIN_PRODUCT",
                "在主要产品管理模块设置产品为主要产品",
                "Product",
                id,
                response.getProductName()
            );

            log.info("在主要产品管理模块设置产品为主要产品成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("在主要产品管理模块设置产品主要产品失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("设置产品主要产品失败：" + e.getMessage());
        }
    }

    /**
     * 取消产品主要产品标识
     */
    @DeleteMapping("/{id}")
    public Result<ProductResponse> unsetAsMain(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.unsetAsMain(id, userId);

            // 记录操作日志
            operationLogService.log(
                "UNSET_MAIN",
                "MAIN_PRODUCT",
                "在主要产品管理模块取消产品主要产品标识",
                "Product",
                id,
                response.getProductName()
            );

            log.info("在主要产品管理模块取消产品主要产品成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("在主要产品管理模块取消产品主要产品失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("取消产品主要产品失败：" + e.getMessage());
        }
    }

    /**
     * 批量设置产品为主要产品
     */
    @PutMapping("/batch")
    public Result<Void> batchSetAsMain(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            productService.batchSetAsMain(ids, userId);

            // 记录操作日志
            operationLogService.log(
                "BATCH_SET_MAIN",
                "MAIN_PRODUCT",
                "在主要产品管理模块批量设置产品为主要产品，数量：" + ids.size(),
                "Product",
                null,
                null
            );

            log.info("在主要产品管理模块批量设置产品为主要产品成功，设置数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("在主要产品管理模块批量设置产品主要产品失败，错误：{}", e.getMessage());
            return Result.error("批量设置产品主要产品失败：" + e.getMessage());
        }
    }

    /**
     * 批量取消产品主要产品标识
     */
    @DeleteMapping("/batch")
    public Result<Void> batchUnsetAsMain(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            productService.batchUnsetAsMain(ids, userId);

            // 记录操作日志
            operationLogService.log(
                "BATCH_UNSET_MAIN",
                "MAIN_PRODUCT",
                "在主要产品管理模块批量取消产品主要产品标识，数量：" + ids.size(),
                "Product",
                null,
                null
            );

            log.info("在主要产品管理模块批量取消产品主要产品成功，取消数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("在主要产品管理模块批量取消产品主要产品失败，错误：{}", e.getMessage());
            return Result.error("批量取消产品主要产品失败：" + e.getMessage());
        }
    }

    /**
     * 获取主要产品统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getMainProductStatistics() {
        try {
            // 获取主要产品统计信息
            List<ProductResponse> allMainProducts = productService.getMainProducts();
            List<ProductResponse> activeMainProducts = productService.getActiveMainProducts();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalMainProducts", allMainProducts.size());
            statistics.put("activeMainProducts", activeMainProducts.size());
            statistics.put("inactiveMainProducts", allMainProducts.size() - activeMainProducts.size());

            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取主要产品统计信息失败，错误：{}", e.getMessage());
            return Result.error("获取主要产品统计信息失败：" + e.getMessage());
        }
    }
}