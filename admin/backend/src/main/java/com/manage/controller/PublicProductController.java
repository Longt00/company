package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.product.PublicProductResponse;
import com.manage.entity.Product;
import com.manage.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公开产品查询接口控制器（无需认证）
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/public/products")
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductService productService;

    /**
     * 获取所有产品（不限状态）
     */
    @GetMapping
    public Result<Page<PublicProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String category) {
        try {
            // 转换为Spring Data的分页参数（从0开始）
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<Product> products;

            if (StringUtils.hasText(category)) {
                products = productService.getProductsByCategoryContaining(category, pageable);
            } else {
                products = productService.getAllProducts(pageable);
            }

            // 转换为公开响应格式
            Page<PublicProductResponse> response = products.map(this::convertToPublicResponse);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取产品列表失败：{}", e.getMessage());
            return Result.error("获取产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 按分类获取产品
     */
    @GetMapping("/category/{category}")
    public Result<Page<PublicProductResponse>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<Product> products = productService.getProductsByCategoryContaining(category, pageable);

            // 转换为公开响应格式
            Page<PublicProductResponse> response = products.map(this::convertToPublicResponse);
            return Result.success(response);
        } catch (Exception e) {
            log.error("按分类获取产品失败，分类：{}，错误：{}", category, e.getMessage());
            return Result.error("获取产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取产品分类
     */
    @GetMapping("/categories")
    public Result<List<String>> getProductCategories() {
        try {
            List<String> categories = productService.getActiveProductCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取产品分类失败：{}", e.getMessage());
            return Result.error("获取产品分类失败：" + e.getMessage());
        }
    }

    /**
     * 搜索产品
     */
    @GetMapping("/search")
    public Result<Page<PublicProductResponse>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<Product> products = productService.searchActiveProducts(keyword, pageable);
            
            // 转换为公开响应格式
            Page<PublicProductResponse> response = products.map(this::convertToPublicResponse);
            return Result.success(response);
        } catch (Exception e) {
            log.error("搜索产品失败，关键词：{}，错误：{}", keyword, e.getMessage());
            return Result.error("搜索产品失败：" + e.getMessage());
        }
    }

    /**
     * 获取产品详情
     */
    @GetMapping("/{id}")
    public Result<PublicProductResponse> getProductDetail(@PathVariable Long id) {
        try {
            Product product = productService.getActiveProductById(id);
            if (product == null) {
                return Result.error("产品不存在或已下架");
            }

            PublicProductResponse response = convertToPublicResponse(product);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取产品详情失败，产品ID：{}，错误：{}", id, e.getMessage());
            return Result.error("获取产品详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取新品产品列表（公开展示）
     */
    @GetMapping("/new")
    public Result<Page<PublicProductResponse>> getNewProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            // 获取上架新品产品列表
            List<com.manage.dto.product.ProductResponse> newProducts = productService.getActiveNewProducts();

            // 手动分页处理
            int start = (page - 1) * size;
            int end = Math.min(start + size, newProducts.size());

            List<com.manage.dto.product.ProductResponse> pageContent;
            if (start < newProducts.size()) {
                pageContent = newProducts.subList(start, end);
            } else {
                pageContent = new ArrayList<>();
            }

            // 转换为公开响应格式
            List<PublicProductResponse> publicProducts = pageContent.stream()
                    .map(this::convertProductResponseToPublic)
                    .collect(java.util.stream.Collectors.toList());

            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<PublicProductResponse> response = new org.springframework.data.domain.PageImpl<>(publicProducts, pageable, newProducts.size());

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取新品产品列表失败：{}", e.getMessage());
            return Result.error("获取新品产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取主要产品列表（公开展示）
     */
    @GetMapping("/main")
    public Result<Page<PublicProductResponse>> getMainProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            // 获取上架主要产品列表
            List<com.manage.dto.product.ProductResponse> mainProducts = productService.getActiveMainProducts();

            // 手动分页处理
            int start = (page - 1) * size;
            int end = Math.min(start + size, mainProducts.size());

            List<com.manage.dto.product.ProductResponse> pageContent;
            if (start < mainProducts.size()) {
                pageContent = mainProducts.subList(start, end);
            } else {
                pageContent = new ArrayList<>();
            }

            // 转换为公开响应格式
            List<PublicProductResponse> publicProducts = pageContent.stream()
                    .map(this::convertProductResponseToPublic)
                    .collect(java.util.stream.Collectors.toList());

            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<PublicProductResponse> response = new org.springframework.data.domain.PageImpl<>(publicProducts, pageable, mainProducts.size());

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取主要产品列表失败：{}", e.getMessage());
            return Result.error("获取主要产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取热门推荐产品列表（公开展示）
     */
    @GetMapping("/featured")
    public Result<Page<PublicProductResponse>> getFeaturedProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            // 获取上架热门推荐产品列表
            List<com.manage.dto.product.ProductResponse> featuredProducts = productService.getActiveFeaturedProducts();

            // 手动分页处理
            int start = (page - 1) * size;
            int end = Math.min(start + size, featuredProducts.size());

            List<com.manage.dto.product.ProductResponse> pageContent;
            if (start < featuredProducts.size()) {
                pageContent = featuredProducts.subList(start, end);
            } else {
                pageContent = new ArrayList<>();
            }

            // 转换为公开响应格式
            List<PublicProductResponse> publicProducts = pageContent.stream()
                    .map(this::convertProductResponseToPublic)
                    .collect(java.util.stream.Collectors.toList());

            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<PublicProductResponse> response = new org.springframework.data.domain.PageImpl<>(publicProducts, pageable, featuredProducts.size());

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取热门推荐产品列表失败：{}", e.getMessage());
            return Result.error("获取热门推荐产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 转换为公开响应格式
     */
    private PublicProductResponse convertToPublicResponse(Product product) {
        PublicProductResponse response = new PublicProductResponse();
        response.setId(product.getId());
        response.setName(product.getProductName());
        response.setProductCode(product.getProductCode());
        response.setDescription(product.getDescription());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setMarketPrice(product.getMarketPrice());
        response.setMainImage(product.getMainImage());
        
        // 处理产品图片列表
        if (StringUtils.hasText(product.getProductImages())) {
            try {
                // 假设productImages是JSON数组格式
                response.setProductImages(parseProductImages(product.getProductImages()));
            } catch (Exception e) {
                log.warn("解析产品图片失败：{}", e.getMessage());
                response.setProductImages(new ArrayList<>());
            }
        } else {
            response.setProductImages(new ArrayList<>());
        }
        
        response.setSpecifications(product.getSpecifications());
        response.setStockQuantity(product.getStockQuantity());
        response.setTags(parseTags(product.getTags()));
        response.setStatus(product.getStatus());
        response.setIsFeatured(product.getIsFeatured()); // 设置热门推荐标识
        response.setIsNew(product.getIsNew()); // 设置新品标识
        response.setIsMain(product.getIsMain()); // 设置主要产品标识
        response.setSortOrder(product.getSortOrder());
        response.setSeoKeywords(product.getSeoKeywords());
        response.setSeoDescription(product.getSeoDescription());
        response.setCreatedAt(product.getCreateTime());
        response.setUpdatedAt(product.getUpdateTime());
        
        // ============ 服装专业属性 ============
        response.setMaterial(product.getMaterial());
        response.setFabricType(product.getFabricType());
        response.setFabricWeight(product.getFabricWeight());
        response.setWaistStyle(product.getWaistStyle());
        response.setJeansStyle(product.getJeansStyle());
        response.setPatternFit(product.getPatternFit());
        response.setStyle(product.getStyle());
        response.setCraftsmanship(product.getCraftsmanship());
        response.setWeavingMethod(product.getWeavingMethod());
        response.setWashingProcess(product.getWashingProcess());
        response.setNeedleDetectionProcess(product.getNeedleDetectionProcess());
        response.setPatternType(product.getPatternType());
        response.setPrintingMethod(product.getPrintingMethod());
        response.setFashionElements(product.getFashionElements());
        response.setLogoPosition(product.getLogoPosition());
        response.setSupplyType(product.getSupplyType());
        response.setOrigin(product.getOrigin());
        response.setFastSampling(product.getFastSampling());
        response.setModel(product.getModel());
        response.setSeason(product.getSeason());
        response.setSalesUnit(product.getSalesUnit());
        response.setVideoPath(product.getVideoPath());
        
        return response;
    }

    /**
     * 解析产品图片列表
     */
    private List<String> parseProductImages(String productImages) {
        if (!StringUtils.hasText(productImages)) {
            return new ArrayList<>();
        }

        try {
            // 如果是JSON格式，需要解析
            if (productImages.startsWith("[") && productImages.endsWith("]")) {
                // 这里应该使用JSON解析库，为了简化暂时用字符串分割
                String[] images = productImages.replaceAll("[\\[\\]\"]", "").split(",");
                return Arrays.asList(images);
            } else {
                // 如果是逗号分隔的字符串
                return Arrays.asList(productImages.split(","));
            }
        } catch (Exception e) {
            log.warn("解析产品图片列表失败：{}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 解析标签
     */
    private List<String> parseTags(String tags) {
        if (!StringUtils.hasText(tags)) {
            return new ArrayList<>();
        }

        return Arrays.asList(tags.split(","))
                .stream()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    /**
     * 将ProductResponse转换为PublicProductResponse
     */
    private PublicProductResponse convertProductResponseToPublic(com.manage.dto.product.ProductResponse productResponse) {
        PublicProductResponse response = new PublicProductResponse();
        response.setId(productResponse.getId());
        response.setName(productResponse.getProductName());
        response.setProductCode(productResponse.getProductCode());
        response.setDescription(productResponse.getDescription());
        response.setCategory(productResponse.getCategory());
        response.setPrice(productResponse.getPrice());
        response.setMarketPrice(productResponse.getMarketPrice());
        response.setMainImage(productResponse.getMainImage());
        response.setProductImages(productResponse.getProductImages());
        response.setSpecifications(productResponse.getSpecifications());
        response.setStockQuantity(productResponse.getStockQuantity());
        response.setTags(productResponse.getTags());
        response.setStatus(productResponse.getStatus());
        response.setIsFeatured(productResponse.getIsFeatured());
        response.setIsNew(productResponse.getIsNew());
        response.setIsMain(productResponse.getIsMain());
        response.setSortOrder(productResponse.getSortOrder());
        response.setSeoKeywords(productResponse.getSeoKeywords());
        response.setSeoDescription(productResponse.getSeoDescription());
        response.setCreatedAt(productResponse.getCreateTime());
        response.setUpdatedAt(productResponse.getUpdateTime());

        // ============ 服装专业属性 ============
        response.setMaterial(productResponse.getMaterial());
        response.setFabricType(productResponse.getFabricType());
        response.setFabricWeight(productResponse.getFabricWeight());
        response.setWaistStyle(productResponse.getWaistStyle());
        response.setJeansStyle(productResponse.getJeansStyle());
        response.setPatternFit(productResponse.getPatternFit());
        response.setStyle(productResponse.getStyle());
        response.setCraftsmanship(productResponse.getCraftsmanship());
        response.setWeavingMethod(productResponse.getWeavingMethod());
        response.setWashingProcess(productResponse.getWashingProcess());
        response.setNeedleDetectionProcess(productResponse.getNeedleDetectionProcess());
        response.setPatternType(productResponse.getPatternType());
        response.setPrintingMethod(productResponse.getPrintingMethod());
        response.setFashionElements(productResponse.getFashionElements());
        response.setLogoPosition(productResponse.getLogoPosition());
        response.setSupplyType(productResponse.getSupplyType());
        response.setOrigin(productResponse.getOrigin());
        response.setFastSampling(productResponse.getFastSampling());
        response.setModel(productResponse.getModel());
        response.setSeason(productResponse.getSeason());
        response.setSalesUnit(productResponse.getSalesUnit());
        response.setVideoPath(productResponse.getVideoPath());

        return response;
    }
}