package com.manage.repository;

import com.manage.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 产品信息数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据产品编码查找产品
     *
     * @param productCode 产品编码
     * @return 产品信息
     */
    Optional<Product> findByProductCode(String productCode);

    /**
     * 根据产品名称查找产品（模糊查询）
     *
     * @param productName 产品名称
     * @return 产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:productName%")
    List<Product> findByProductNameContaining(@Param("productName") String productName);

    /**
     * 根据分类查找产品
     *
     * @param category 产品分类
     * @return 产品列表
     */
    List<Product> findByCategory(String category);

    /**
     * 根据分类分页查找产品（不限状态）
     *
     * @param category  产品分类
     * @param pageable  分页参数
     * @return 产品分页结果
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * 根据分类模糊查找产品（不限状态）
     *
     * @param category  产品分类关键词
     * @param pageable  分页参数
     * @return 产品分页结果
     */
    @Query("SELECT p FROM Product p WHERE (:category IS NULL OR p.category LIKE %:category%)")
    Page<Product> findByCategoryContaining(@Param("category") String category, Pageable pageable);

    /**
     * 根据状态查找产品
     *
     * @param status 产品状态
     * @return 产品列表
     */
    List<Product> findByStatus(Integer status);

    /**
     * 查找上架的产品
     *
     * @return 上架产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.status = 1 ORDER BY p.sortOrder ASC, p.createTime DESC")
    List<Product> findActiveProducts();

    /**
     * 查找推荐的产品
     *
     * @return 推荐产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.status = 1 ORDER BY p.sortOrder ASC, p.createTime DESC")
    List<Product> findFeaturedProducts();

    /**
     * 查找新品产品（不限状态）
     *
     * @return 新品产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.isNew = true ORDER BY p.sortOrder ASC, p.createTime DESC")
    List<Product> findNewProducts();

    /**
     * 查找主要产品（不限状态）
     *
     * @return 主要产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.isMain = true ORDER BY p.sortOrder ASC, p.createTime DESC")
    List<Product> findMainProducts();

    /**
     * 查找上架的新品产品
     *
     * @return 上架新品产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.isNew = true AND p.status = 1 ORDER BY p.sortOrder ASC, p.createTime DESC")
    List<Product> findActiveNewProducts();

    /**
     * 查找上架的主要产品
     *
     * @return 上架主要产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.isMain = true AND p.status = 1 ORDER BY p.sortOrder ASC, p.createTime DESC")
    List<Product> findActiveMainProducts();

    /**
     * 查找上架的热门推荐产品
     *
     * @return 上架热门推荐产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.status = 1 ORDER BY p.sortOrder ASC, p.createTime DESC")
    List<Product> findActiveFeaturedProducts();

    /**
     * 根据状态分页查找产品
     *
     * @param status   产品状态
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    Page<Product> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据名称和状态分页查找产品
     *
     * @param productName 产品名称
     * @param status      产品状态
     * @param pageable    分页参数
     * @return 产品分页结果
     */
    @Query("SELECT p FROM Product p WHERE (:productName IS NULL OR p.productName LIKE %:productName%) AND p.status = :status")
    Page<Product> findByProductNameAndStatus(@Param("productName") String productName,
                                            @Param("status") Integer status,
                                            Pageable pageable);

    /**
     * 根据分类和状态分页查找产品
     *
     * @param category  产品分类
     * @param status    产品状态
     * @param pageable  分页参数
     * @return 产品分页结果
     */
    Page<Product> findByCategoryAndStatus(String category, Integer status, Pageable pageable);

    /**
     * 根据标签查找产品
     *
     * @param tag 标签
     * @return 产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.tags LIKE %:tag% AND p.status = 1")
    List<Product> findByTag(@Param("tag") String tag);

    /**
     * 检查产品编码是否存在
     *
     * @param productCode 产品编码
     * @return 存在返回true，不存在返回false
     */
    boolean existsByProductCode(String productCode);

    /**
     * 统计各状态的产品数量
     *
     * @return 统计结果
     */
    @Query("SELECT p.status, COUNT(p) FROM Product p GROUP BY p.status")
    List<Object[]> countProductsByStatus();

    /**
     * 根据价格区间查找产品
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param status   产品状态
     * @return 产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.price IS NOT NULL AND p.price BETWEEN :minPrice AND :maxPrice AND p.status = :status")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("status") Integer status);

    // ============ 公开产品查询方法 ============

    /**
     * 根据ID和状态查找产品
     *
     * @param id     产品ID
     * @param status 产品状态
     * @return 产品信息
     */
    Optional<Product> findByIdAndStatus(Long id, Integer status);

    /**
     * 根据产品名称模糊查询和状态查找产品（分页）
     *
     * @param keyword  关键词
     * @param status   产品状态
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @Query("SELECT p FROM Product p WHERE (p.productName LIKE %:keyword% OR p.description LIKE %:keyword%) AND p.status = :status")
    Page<Product> findByProductNameContainingIgnoreCaseAndStatus(@Param("keyword") String keyword,
                                                               @Param("status") Integer status,
                                                               Pageable pageable);

    /**
     * 根据状态查找产品分类
     *
     * @param status 产品状态
     * @return 分类统计结果
     */
    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.category IS NOT NULL AND p.status = :status GROUP BY p.category")
    List<Object[]> findCategoriesByStatus(@Param("status") Integer status);
}