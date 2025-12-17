package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.product.ProductImageUploadRequest;
import com.manage.dto.product.ProductQueryRequest;
import com.manage.dto.product.ProductRequest;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 产品信息管理控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@Validated
@RequiredArgsConstructor
public class ProductController {

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
     * 创建新产品
     */
    @PostMapping
    public Result<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.createProduct(request, userId);

            // 记录操作日志
            operationLogService.log(
                "CREATE",
                "PRODUCT",
                "创建了新产品: " + request.getProductName(),
                "Product",
                response.getId(),
                request.getProductName()
            );

            log.info("产品创建成功，产品ID：{}", response.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建产品失败：{}", e.getMessage());
            return Result.error("创建产品失败：" + e.getMessage());
        }
    }

    /**
     * 更新产品信息
     */
    @PutMapping("/{id}")
    public Result<ProductResponse> updateProduct(@PathVariable Long id,
                                               @Valid @RequestBody ProductRequest request) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.updateProduct(id, request, userId);

            // 记录操作日志
            operationLogService.log(
                "UPDATE",
                "PRODUCT",
                "更新了产品信息: " + request.getProductName(),
                "Product",
                id,
                request.getProductName()
            );

            log.info("产品更新成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新产品失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("更新产品失败：" + e.getMessage());
        }
    }

    /**
     * 根据产品编码获取产品详情
     */
    @GetMapping("/code/{productCode}")
    public Result<ProductResponse> getProductByCode(@PathVariable String productCode) {
        try {
            ProductResponse response = productService.getProductByCode(productCode);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据编码获取产品详情失败，产品编码：{}，错误：{}", productCode, e.getMessage());
            return Result.error("获取产品详情失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取产品详情
     */
    @GetMapping("/{id}")
    public Result<ProductResponse> getProductById(@PathVariable Long id) {
        try {
            ProductResponse response = productService.getProductById(id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取产品详情失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("获取产品详情失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询产品列表
     */
    @GetMapping
    public Result<Page<ProductResponse>> getProducts(@Valid ProductQueryRequest queryRequest) {
        try {
            Page<ProductResponse> response = productService.getProducts(queryRequest);
            return Result.success(response);
        } catch (Exception e) {
            log.error("分页查询产品列表失败，错误：{}", e.getMessage());
            return Result.error("查询产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取上架产品列表
     */
    @GetMapping("/active")
    public Result<List<ProductResponse>> getActiveProducts() {
        try {
            List<ProductResponse> response = productService.getActiveProducts();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取上架产品列表失败，错误：{}", e.getMessage());
            return Result.error("获取上架产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取推荐产品列表
     */
    @GetMapping("/featured")
    public Result<List<ProductResponse>> getFeaturedProducts() {
        try {
            List<ProductResponse> response = productService.getFeaturedProducts();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取推荐产品列表失败，错误：{}", e.getMessage());
            return Result.error("获取推荐产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 搜索产品
     */
    @GetMapping("/search")
    public Result<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        try {
            List<ProductResponse> response = productService.searchProducts(keyword);
            return Result.success(response);
        } catch (Exception e) {
            log.error("搜索产品失败，关键词：{}，错误：{}", keyword, e.getMessage());
            return Result.error("搜索产品失败：" + e.getMessage());
        }
    }

    /**
     * 根据分类获取产品列表
     */
    @GetMapping("/category/{category}")
    public Result<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        try {
            List<ProductResponse> response = productService.getProductsByCategory(category);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据分类获取产品列表失败，分类：{}，错误：{}", category, e.getMessage());
            return Result.error("获取产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 上架产品
     */
    @PutMapping("/{id}/activate")
    public Result<ProductResponse> activateProduct(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.activateProduct(id, userId);
            log.info("产品上架成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("产品上架失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("产品上架失败：" + e.getMessage());
        }
    }

    /**
     * 下架产品
     */
    @PutMapping("/{id}/deactivate")
    public Result<ProductResponse> deactivateProduct(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.deactivateProduct(id, userId);
            log.info("产品下架成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("产品下架失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("产品下架失败：" + e.getMessage());
        }
    }

    /**
     * 保存产品为草稿
     */
    @PutMapping("/{id}/save-draft")
    public Result<ProductResponse> saveAsDraft(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.saveAsDraft(id, userId);
            log.info("产品保存为草稿成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("产品保存为草稿失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("产品保存为草稿失败：" + e.getMessage());
        }
    }

    /**
     * 从草稿发布产品
     */
    @PutMapping("/{id}/publish")
    public Result<ProductResponse> publishFromDraft(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.publishFromDraft(id, userId);
            log.info("产品从草稿发布成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("产品从草稿发布失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("产品从草稿发布失败：" + e.getMessage());
        }
    }

    /**
     * 获取草稿产品列表
     */
    @GetMapping("/status/drafts")
    public Result<List<ProductResponse>> getDraftProducts() {
        try {
            List<ProductResponse> response = productService.getDraftProducts();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取草稿产品列表失败，错误：{}", e.getMessage());
            return Result.error("获取草稿产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            productService.deleteProduct(id, userId);
            log.info("产品删除成功，产品ID：{}", id);
            return Result.success();
        } catch (Exception e) {
            log.error("产品删除失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("产品删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除产品
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteProducts(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            productService.batchDeleteProducts(ids, userId);
            log.info("批量删除产品成功，删除数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("批量删除产品失败，错误：{}", e.getMessage());
            return Result.error("批量删除产品失败：" + e.getMessage());
        }
    }

    /**
     * 批量上架产品
     */
    @PutMapping("/batch/activate")
    public Result<Void> batchActivateProducts(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            productService.batchActivateProducts(ids, userId);
            log.info("批量上架产品成功，上架数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("批量上架产品失败，错误：{}", e.getMessage());
            return Result.error("批量上架产品失败：" + e.getMessage());
        }
    }

    /**
     * 批量下架产品
     */
    @PutMapping("/batch/deactivate")
    public Result<Void> batchDeactivateProducts(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            productService.batchDeactivateProducts(ids, userId);
            log.info("批量下架产品成功，下架数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("批量下架产品失败，错误：{}", e.getMessage());
            return Result.error("批量下架产品失败：" + e.getMessage());
        }
    }

    /**
     * 批量保存为草稿
     */
    @PutMapping("/batch/save-draft")
    public Result<Void> batchSaveAsDraft(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            productService.batchSaveAsDraft(ids, userId);
            log.info("批量保存为草稿成功，数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("批量保存为草稿失败，错误：{}", e.getMessage());
            return Result.error("批量保存为草稿失败：" + e.getMessage());
        }
    }

    /**
     * 批量从草稿发布
     */
    @PutMapping("/batch/publish")
    public Result<Void> batchPublishFromDraft(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            productService.batchPublishFromDraft(ids, userId);
            log.info("批量从草稿发布成功，数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("批量从草稿发布失败，错误：{}", e.getMessage());
            return Result.error("批量从草稿发布失败：" + e.getMessage());
        }
    }

    /**
     * 更新产品库存
     */
    @PutMapping("/{id}/stock")
    public Result<ProductResponse> updateStock(@PathVariable Long id,
                                              @RequestParam Integer quantity) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.updateStock(id, quantity, userId);
            log.info("产品库存更新成功，产品ID：{}，新库存：{}", id, quantity);
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新产品库存失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("更新库存失败：" + e.getMessage());
        }
    }

    /**
     * 设置产品为推荐
     */
    @PutMapping("/{id}/featured")
    public Result<ProductResponse> setAsFeatured(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.setAsFeatured(id, userId);
            log.info("产品设置为推荐成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("设置产品推荐失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("设置产品推荐失败：" + e.getMessage());
        }
    }

    /**
     * 取消产品推荐
     */
    @DeleteMapping("/{id}/featured")
    public Result<ProductResponse> unsetAsFeatured(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.unsetAsFeatured(id, userId);
            log.info("取消产品推荐成功，产品ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("取消产品推荐失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("取消产品推荐失败：" + e.getMessage());
        }
    }

    /**
     * 上传产品图片
     */
    @PostMapping("/images")
    public Result<ProductResponse> uploadProductImages(@Valid @RequestBody ProductImageUploadRequest request) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.uploadProductImages(request, userId);
            log.info("产品图片上传成功，产品ID：{}", request.getProductId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("上传产品图片失败，产品ID：{}，错误：{}", request.getProductId(), e.getMessage());
            return Result.error("上传图片失败：" + e.getMessage());
        }
    }

    /**
     * 删除产品图片
     */
    @DeleteMapping("/{productId}/images")
    public Result<ProductResponse> deleteProductImage(@PathVariable Long productId,
                                                     @RequestParam String imageUrl) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.deleteProductImage(productId, imageUrl, userId);
            log.info("产品图片删除成功，产品ID：{}", productId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("删除产品图片失败，产品ID：{}，错误：{}", productId, e.getMessage());
            return Result.error("删除图片失败：" + e.getMessage());
        }
    }

    /**
     * 设置产品主图
     */
    @PutMapping("/{productId}/main-image")
    public Result<ProductResponse> setMainImage(@PathVariable Long productId,
                                               @RequestParam String imageUrl) {
        try {
            Long userId = getCurrentUserId();
            ProductResponse response = productService.setMainImage(productId, imageUrl, userId);
            log.info("产品主图设置成功，产品ID：{}", productId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("设置产品主图失败，产品ID：{}，错误：{}", productId, e.getMessage());
            return Result.error("设置主图失败：" + e.getMessage());
        }
    }

    /**
     * 根据价格区间查找产品
     */
    @GetMapping("/price-range")
    public Result<List<ProductResponse>> getProductsByPriceRange(@RequestParam BigDecimal minPrice,
                                                                @RequestParam BigDecimal maxPrice) {
        try {
            List<ProductResponse> response = productService.getProductsByPriceRange(minPrice, maxPrice);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据价格区间查找产品失败，价格区间：{}-{}，错误：{}", minPrice, maxPrice, e.getMessage());
            return Result.error("查找产品失败：" + e.getMessage());
        }
    }

    /**
     * 获取产品统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getProductStatistics() {
        try {
            Object response = productService.getProductStatistics();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取产品统计信息失败，错误：{}", e.getMessage());
            return Result.error("获取统计信息失败：" + e.getMessage());
        }
    }

    /**
     * 检查产品编码是否存在
     */
    @GetMapping("/check-code/{productCode}")
    public Result<Boolean> checkProductCodeExists(@PathVariable String productCode) {
        try {
            boolean exists = productService.existsByProductCode(productCode);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查产品编码是否存在失败，产品编码：{}，错误：{}", productCode, e.getMessage());
            return Result.error("检查产品编码失败：" + e.getMessage());
        }
    }

    /**
     * 根据标签查找产品
     */
    @GetMapping("/tag/{tag}")
    public Result<List<ProductResponse>> getProductsByTag(@PathVariable String tag) {
        try {
            List<ProductResponse> response = productService.getProductsByTag(tag);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据标签查找产品失败，标签：{}，错误：{}", tag, e.getMessage());
            return Result.error("查找产品失败：" + e.getMessage());
        }
    }
}