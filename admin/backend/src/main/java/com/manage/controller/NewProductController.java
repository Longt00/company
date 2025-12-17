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
 * 新品管理控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/new-products")
@Validated
@RequiredArgsConstructor
public class NewProductController {

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
     * 分页查询新品产品列表
     */
    @PostMapping
    public Result<Page<ProductResponse>> getNewProducts(@Valid @RequestBody ProductQueryRequest queryRequest) {
        try {
            // 查询新品产品
            List<ProductResponse> newProducts = productService.getNewProducts();

            // 手动分页处理
            int start = queryRequest.getPage() * queryRequest.getSize();
            int end = Math.min(start + queryRequest.getSize(), newProducts.size());

            List<ProductResponse> pageContent;
            if (start < newProducts.size()) {
                pageContent = newProducts.subList(start, end);
            } else {
                pageContent = new ArrayList<>();
            }

            // 模拟分页结果
            Page<ProductResponse> response = new org.springframework.data.domain.PageImpl<>(
                pageContent,
                org.springframework.data.domain.PageRequest.of(queryRequest.getPage(), queryRequest.getSize()),
                newProducts.size()
            );

            return Result.success(response);
        } catch (Exception e) {
            log.error("分页查询新品产品列表失败，错误：{}", e.getMessage());
            return Result.error("查询新品产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取上架新品产品列表
     */
    @GetMapping("/active")
    public Result<List<ProductResponse>> getActiveNewProducts() {
        try {
            List<ProductResponse> response = productService.getActiveNewProducts();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取上架新品产品列表失败，错误：{}", e.getMessage());
            return Result.error("获取上架新品产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 设置产品为新品
     */
    @PutMapping("/{id}")
    public Result<ProductResponse> setAsNew(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.setAsNew(id, userId);

            // 记录操作日志
            operationLogService.log(
                "SET_NEW",
                "NEW_PRODUCT",
                "在新品管理模块设置产品为新品",
                "Product",
                id,
                response.getProductName()
            );

            log.info("在新品管理模块设置产品为新品成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("在新品管理模块设置产品新品失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("设置产品新品失败：" + e.getMessage());
        }
    }

    /**
     * 取消产品新品标识
     */
    @DeleteMapping("/{id}")
    public Result<ProductResponse> unsetAsNew(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.unsetAsNew(id, userId);

            // 记录操作日志
            operationLogService.log(
                "UNSET_NEW",
                "NEW_PRODUCT",
                "在新品管理模块取消产品新品标识",
                "Product",
                id,
                response.getProductName()
            );

            log.info("在新品管理模块取消产品新品成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("在新品管理模块取消产品新品失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("取消产品新品失败：" + e.getMessage());
        }
    }

    /**
     * 批量设置产品为新品
     */
    @PutMapping("/batch")
    public Result<Void> batchSetAsNew(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            productService.batchSetAsNew(ids, userId);

            // 记录操作日志
            operationLogService.log(
                "BATCH_SET_NEW",
                "NEW_PRODUCT",
                "在新品管理模块批量设置产品为新品，数量：" + ids.size(),
                "Product",
                null,
                null
            );

            log.info("在新品管理模块批量设置产品为新品成功，设置数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("在新品管理模块批量设置产品新品失败，错误：{}", e.getMessage());
            return Result.error("批量设置产品新品失败：" + e.getMessage());
        }
    }

    /**
     * 批量取消产品新品标识
     */
    @DeleteMapping("/batch")
    public Result<Void> batchUnsetAsNew(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            productService.batchUnsetAsNew(ids, userId);

            // 记录操作日志
            operationLogService.log(
                "BATCH_UNSET_NEW",
                "NEW_PRODUCT",
                "在新品管理模块批量取消产品新品标识，数量：" + ids.size(),
                "Product",
                null,
                null
            );

            log.info("在新品管理模块批量取消产品新品成功，取消数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("在新品管理模块批量取消产品新品失败，错误：{}", e.getMessage());
            return Result.error("批量取消产品新品失败：" + e.getMessage());
        }
    }

    /**
     * 获取新品统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getNewProductStatistics() {
        try {
            // 获取新品统计信息
            List<ProductResponse> allNewProducts = productService.getNewProducts();
            List<ProductResponse> activeNewProducts = productService.getActiveNewProducts();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalNewProducts", allNewProducts.size());
            statistics.put("activeNewProducts", activeNewProducts.size());
            statistics.put("inactiveNewProducts", allNewProducts.size() - activeNewProducts.size());

            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取新品统计信息失败，错误：{}", e.getMessage());
            return Result.error("获取新品统计信息失败：" + e.getMessage());
        }
    }
}