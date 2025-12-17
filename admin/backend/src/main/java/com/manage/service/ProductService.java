package com.manage.service;

import com.manage.dto.product.ProductImageUploadRequest;
import com.manage.dto.product.ProductQueryRequest;
import com.manage.dto.product.ProductRequest;
import com.manage.dto.product.ProductResponse;
import com.manage.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品信息服务接口
 *
 * @author System
 * @version 1.0
 */
public interface ProductService {

    /**
     * 创建新产品
     *
     * @param request 产品创建请求
     * @param userId  操作用户ID
     * @return 创建的产品信息
     */
    ProductResponse createProduct(ProductRequest request, Long userId);

    /**
     * 更新产品信息
     *
     * @param id      产品ID
     * @param request 产品更新请求
     * @param userId  操作用户ID
     * @return 更新后的产品信息
     */
    ProductResponse updateProduct(Long id, ProductRequest request, Long userId);

    /**
     * 根据ID获取产品详情
     *
     * @param id 产品ID
     * @return 产品详情
     */
    ProductResponse getProductById(Long id);

    /**
     * 根据产品编码获取产品详情
     *
     * @param productCode 产品编码
     * @return 产品详情
     */
    ProductResponse getProductByCode(String productCode);

    /**
     * 分页查询产品列表
     *
     * @param queryRequest 查询条件
     * @return 产品分页列表
     */
    Page<ProductResponse> getProducts(ProductQueryRequest queryRequest);

    /**
     * 获取上架产品列表
     *
     * @return 上架产品列表
     */
    List<ProductResponse> getActiveProducts();

    /**
     * 获取推荐产品列表
     *
     * @return 推荐产品列表
     */
    List<ProductResponse> getFeaturedProducts();

    /**
     * 根据分类获取产品列表
     *
     * @param category 产品分类
     * @return 产品列表
     */
    List<ProductResponse> getProductsByCategory(String category);

    /**
     * 搜索产品
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    List<ProductResponse> searchProducts(String keyword);

    /**
     * 上架产品
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse activateProduct(Long id, Long userId);

    /**
     * 下架产品
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse deactivateProduct(Long id, Long userId);

    /**
     * 保存产品为草稿
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse saveAsDraft(Long id, Long userId);

    /**
     * 从草稿发布产品
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse publishFromDraft(Long id, Long userId);

    /**
     * 获取草稿产品列表
     *
     * @return 草稿产品列表
     */
    List<ProductResponse> getDraftProducts();

    /**
     * 批量保存为草稿
     *
     * @param ids    产品ID列表
     * @param userId 操作用户ID
     */
    void batchSaveAsDraft(List<Long> ids, Long userId);

    /**
     * 批量从草稿发布
     *
     * @param ids    产品ID列表
     * @param userId 操作用户ID
     */
    void batchPublishFromDraft(List<Long> ids, Long userId);

    /**
     * 删除产品
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     */
    void deleteProduct(Long id, Long userId);

    /**
     * 批量删除产品
     *
     * @param ids    产品ID列表
     * @param userId 操作用户ID
     */
    void batchDeleteProducts(List<Long> ids, Long userId);

    /**
     * 批量上架产品
     *
     * @param ids    产品ID列表
     * @param userId 操作用户ID
     */
    void batchActivateProducts(List<Long> ids, Long userId);

    /**
     * 批量下架产品
     *
     * @param ids    产品ID列表
     * @param userId 操作用户ID
     */
    void batchDeactivateProducts(List<Long> ids, Long userId);

    /**
     * 更新产品库存
     *
     * @param id     产品ID
     * @param quantity 新库存数量
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse updateStock(Long id, Integer quantity, Long userId);

    /**
     * 设置产品为推荐
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse setAsFeatured(Long id, Long userId);

    /**
     * 取消产品推荐
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse unsetAsFeatured(Long id, Long userId);

    /**
     * 上传产品图片
     *
     * @param request 图片上传请求
     * @param userId  操作用户ID
     * @return 操作结果
     */
    ProductResponse uploadProductImages(ProductImageUploadRequest request, Long userId);

    /**
     * 删除产品图片
     *
     * @param productId  产品ID
     * @param imageUrl   图片URL
     * @param userId     操作用户ID
     * @return 操作结果
     */
    ProductResponse deleteProductImage(Long productId, String imageUrl, Long userId);

    /**
     * 设置产品主图
     *
     * @param productId  产品ID
     * @param imageUrl   主图URL
     * @param userId     操作用户ID
     * @return 操作结果
     */
    ProductResponse setMainImage(Long productId, String imageUrl, Long userId);

    /**
     * 检查产品编码是否存在
     *
     * @param productCode 产品编码
     * @return 存在返回true，不存在返回false
     */
    boolean existsByProductCode(String productCode);

    /**
     * 根据价格区间查找产品
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 产品列表
     */
    List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 根据标签查找产品
     *
     * @param tag 标签
     * @return 产品列表
     */
    List<ProductResponse> getProductsByTag(String tag);

    /**
     * 获取产品统计信息
     *
     * @return 统计信息
     */
    Object getProductStatistics();

    /**
     * 获取所有产品分页列表（不限状态）
     *
     * @param pageable 分页参数
     * @return 所有产品分页列表
     */
    Page<Product> getAllProducts(Pageable pageable);

    /**
     * 根据分类获取产品分页列表（不限状态）
     *
     * @param category 产品分类
     * @param pageable 分页参数
     * @return 产品分页列表
     */
    Page<Product> getProductsByCategory(String category, Pageable pageable);

  /**
     * 根据分类模糊搜索产品分页列表（不限状态）
     *
     * @param category 产品分类关键词
     * @param pageable 分页参数
     * @return 产品分页列表
     */
    Page<Product> getProductsByCategoryContaining(String category, Pageable pageable);

    /**
     * 获取激活产品分页列表
     *
     * @param pageable 分页参数
     * @return 激活产品分页列表
     */
    Page<Product> getActiveProducts(Pageable pageable);

    /**
     * 根据分类获取激活产品分页列表
     *
     * @param category 产品分类
     * @param pageable 分页参数
     * @return 激活产品分页列表
     */
    Page<Product> getActiveProductsByCategory(String category, Pageable pageable);

    /**
     * 获取所有激活产品分类
     *
     * @return 分类列表
     */
    List<String> getActiveProductCategories();

    /**
     * 搜索激活产品
     *
     * @param keyword  搜索关键词
     * @param pageable 分页参数
     * @return 搜索结果分页列表
     */
    Page<Product> searchActiveProducts(String keyword, Pageable pageable);

    /**
     * 根据ID获取激活产品
     *
     * @param id 产品ID
     * @return 激活产品
     */
    Product getActiveProductById(Long id);

    /**
     * 获取新品产品列表
     *
     * @return 新品产品列表
     */
    List<com.manage.dto.product.ProductResponse> getNewProducts();

    /**
     * 获取主要产品列表
     *
     * @return 主要产品列表
     */
    List<com.manage.dto.product.ProductResponse> getMainProducts();

    /**
     * 获取上架新品产品列表
     *
     * @return 上架新品产品列表
     */
    List<com.manage.dto.product.ProductResponse> getActiveNewProducts();

    /**
     * 获取上架主要产品列表
     *
     * @return 上架主要产品列表
     */
    List<com.manage.dto.product.ProductResponse> getActiveMainProducts();

    /**
     * 获取上架热门推荐产品列表
     *
     * @return 上架热门推荐产品列表
     */
    List<com.manage.dto.product.ProductResponse> getActiveFeaturedProducts();

    /**
     * 设置产品为主要产品
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse setAsMain(Long id, Long userId);

    /**
     * 取消产品主要产品标识
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse unsetAsMain(Long id, Long userId);

    /**
     * 批量设置产品为主要产品
     *
     * @param ids    产品ID列表
     * @param userId 操作用户ID
     */
    void batchSetAsMain(List<Long> ids, Long userId);

    /**
     * 批量取消产品主要产品标识
     *
     * @param ids    产品ID列表
     * @param userId 操作用户ID
     */
    void batchUnsetAsMain(List<Long> ids, Long userId);

    /**
     * 设置产品为新品
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse setAsNew(Long id, Long userId);

    /**
     * 取消产品新品标识
     *
     * @param id     产品ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    ProductResponse unsetAsNew(Long id, Long userId);

    /**
     * 批量设置产品为新品
     *
     * @param ids    产品ID列表
     * @param userId 操作用户ID
     */
    void batchSetAsNew(List<Long> ids, Long userId);

    /**
     * 批量取消产品新品标识
     *
     * @param ids    产品ID列表
     * @param userId 操作用户ID
     */
    void batchUnsetAsNew(List<Long> ids, Long userId);
}