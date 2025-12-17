package com.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品信息实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称长度不能超过200个字符")
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /**
     * 产品编码
     */
    @Size(max = 100, message = "产品编码长度不能超过100个字符")
    @Column(name = "product_code", length = 100, unique = true)
    private String productCode;

    /**
     * 产品分类
     */
    @Size(max = 100, message = "产品分类长度不能超过100个字符")
    @Column(name = "category", length = 100)
    private String category;

    /**
     * 产品描述
     */
    @Size(max = 2000, message = "产品描述长度不能超过2000个字符")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 产品规格
     */
    @Size(max = 500, message = "产品规格长度不能超过500个字符")
    @Column(name = "specifications", length = 500)
    private String specifications;

    /**
     * 产品主图
     */
    @Size(max = 500, message = "主图URL长度不能超过500个字符")
    @Column(name = "main_image", length = 500)
    private String mainImage;

    /**
     * 产品图片列表（多张图片，JSON格式存储）
     */
    @Column(name = "product_images", columnDefinition = "TEXT")
    private String productImages;

    /**
     * 产品价格
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "产品价格不能为负数")
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 市场价格
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "市场价格不能为负数")
    @Column(name = "market_price", precision = 10, scale = 2)
    private BigDecimal marketPrice;

    /**
     * 库存数量
     */
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    // ============ 服装材质属性 ============

    /**
     * 材质（如：纯棉、涤纶、牛仔布）
     */
    @Size(max = 100, message = "材质长度不能超过100个字符")
    @Column(name = "material", length = 100)
    private String material;

    /**
     * 面料类型（如：梭织布、针织布）
     */
    @Size(max = 100, message = "面料类型长度不能超过100个字符")
    @Column(name = "fabric_type", length = 100)
    private String fabricType;

    /**
     * 面料克重（如：180g、220g）
     */
    @Size(max = 50, message = "面料克重长度不能超过50个字符")
    @Column(name = "fabric_weight", length = 50)
    private String fabricWeight;

    // ============ 服装款式属性 ============

    /**
     * 腰型（如：高腰、中腰、低腰）
     */
    @Size(max = 50, message = "腰型长度不能超过50个字符")
    @Column(name = "waist_style", length = 50)
    private String waistStyle;

    /**
     * 牛仔裤款式（如：直筒、紧身、喇叭）
     */
    @Size(max = 50, message = "牛仔裤款式长度不能超过50个字符")
    @Column(name = "jeans_style", length = 50)
    private String jeansStyle;

    /**
     * 板型（如：修身、宽松、标准）
     */
    @Size(max = 50, message = "板型长度不能超过50个字符")
    @Column(name = "pattern_fit", length = 50)
    private String patternFit;

    /**
     * 样式（如：休闲、商务、运动）
     */
    @Size(max = 100, message = "样式长度不能超过100个字符")
    @Column(name = "style", length = 100)
    private String style;

    // ============ 工艺制造属性 ============

    /**
     * 工艺（如：水洗、石磨、激光）
     */
    @Size(max = 200, message = "工艺长度不能超过200个字符")
    @Column(name = "craftsmanship", length = 200)
    private String craftsmanship;

    /**
     * 编织方法（如：平纹、斜纹、提花）
     */
    @Size(max = 100, message = "编织方法长度不能超过100个字符")
    @Column(name = "weaving_method", length = 100)
    private String weavingMethod;

    /**
     * 洗水工艺（如：普洗、石洗、酵洗）
     */
    @Size(max = 100, message = "洗水工艺长度不能超过100个字符")
    @Column(name = "washing_process", length = 100)
    private String washingProcess;

    /**
     * 检针工艺（如：过检、磁检）
     */
    @Size(max = 50, message = "检针工艺长度不能超过50个字符")
    @Column(name = "needle_detection_process", length = 50)
    private String needleDetectionProcess;

    // ============ 图案设计属性 ============

    /**
     * 图案类型（如：纯色、条纹、印花）
     */
    @Size(max = 100, message = "图案类型长度不能超过100个字符")
    @Column(name = "pattern_type", length = 100)
    private String patternType;

    /**
     * 印花方法（如：丝印、数码印花）
     */
    @Size(max = 100, message = "印花方法长度不能超过100个字符")
    @Column(name = "printing_method", length = 100)
    private String printingMethod;

    /**
     * 流行元素（如：破洞、刺绣、撞色）
     */
    @Size(max = 200, message = "流行元素长度不能超过200个字符")
    @Column(name = "fashion_elements", length = 200)
    private String fashionElements;

    /**
     * 徽标位置（如：左胸、后领、袖口）
     */
    @Size(max = 100, message = "徽标位置长度不能超过100个字符")
    @Column(name = "logo_position", length = 100)
    private String logoPosition;

    // ============ 供应商业属性 ============

    /**
     * 供应类型（如：现货、订做、批发）
     */
    @Size(max = 50, message = "供应类型长度不能超过50个字符")
    @Column(name = "supply_type", length = 50)
    private String supplyType;

    /**
     * 原产地（如：中国、越南、孟加拉）
     */
    @Size(max = 100, message = "原产地长度不能超过100个字符")
    @Column(name = "origin", length = 100)
    private String origin;

    /**
     * 7天快速打样：false-否 true-是
     */
    @Column(name = "fast_sampling", nullable = false)
    private Boolean fastSampling = false;

    // ============ 规格包装属性 ============

    /**
     * 型号（如：S/M/L、均码）
     */
    @Size(max = 50, message = "型号长度不能超过50个字符")
    @Column(name = "model", length = 50)
    private String model;

    /**
     * 季节（如：春季、夏季、四季）
     */
    @Size(max = 50, message = "季节长度不能超过50个字符")
    @Column(name = "season", length = 50)
    private String season;

    /**
     * 销售单位（如：件、套、打）
     */
    @Size(max = 50, message = "销售单位长度不能超过50个字符")
    @Column(name = "sales_unit", length = 50)
    private String salesUnit;

    /**
     * 产品视频路径
     */
    @Size(max = 500, message = "产品视频路径长度不能超过500个字符")
    @Column(name = "video_path", length = 500)
    private String videoPath;

    /**
     * 产品标签（多个标签用逗号分隔）
     */
    @Size(max = 500, message = "产品标签长度不能超过500个字符")
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * 产品状态：0-下架 1-上架 2-草稿
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 是否推荐：0-否 1-是
     */
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    /**
     * 是否新品：0-否 1-是
     */
    @Column(name = "is_new", nullable = false)
    private Boolean isNew = false;

    /**
     * 是否主要产品：0-否 1-是
     */
    @Column(name = "is_main", nullable = false)
    private Boolean isMain = false;

    /**
     * 排序值
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * SEO关键词
     */
    @Size(max = 200, message = "SEO关键词长度不能超过200个字符")
    @Column(name = "seo_keywords", length = 200)
    private String seoKeywords;

    /**
     * SEO描述
     */
    @Size(max = 500, message = "SEO描述长度不能超过500个字符")
    @Column(name = "seo_description", length = 500)
    private String seoDescription;

    /**
     * 创建人ID
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 更新人ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}