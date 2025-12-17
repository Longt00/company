package com.manage.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公开产品响应DTO
 *
 * @author System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicProductResponse {

    /**
     * 产品ID
     */
    private Long id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 产品分类
     */
    private String category;

    /**
     * 产品价格
     */
    private BigDecimal price;

    /**
     * 市场价格
     */
    private BigDecimal marketPrice;

    /**
     * 产品主图
     */
    private String mainImage;

    /**
     * 产品图片列表
     */
    private List<String> productImages;

    /**
     * 产品规格
     */
    private String specifications;

    /**
     * 库存数量
     */
    private Integer stockQuantity;

    /**
     * 产品标签
     */
    private List<String> tags;

    /**
     * 产品状态
     */
    private Integer status;

    /**
     * 是否推荐（热门产品）：false-否 true-是
     */
    private Boolean isFeatured;

    /**
     * 是否新品：false-否 true-是
     */
    private Boolean isNew;

    /**
     * 是否主要产品：false-否 true-是
     */
    private Boolean isMain;

    /**
     * 排序值
     */
    private Integer sortOrder;

    /**
     * SEO关键词
     */
    private String seoKeywords;

    /**
     * SEO描述
     */
    private String seoDescription;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ============ 服装材质属性 ============

    /**
     * 材质（如：纯棉、涤纶、牛仔布）
     */
    private String material;

    /**
     * 面料类型（如：梭织布、针织布）
     */
    private String fabricType;

    /**
     * 面料克重（如：180g、220g）
     */
    private String fabricWeight;

    // ============ 服装款式属性 ============

    /**
     * 腰型（如：高腰、中腰、低腰）
     */
    private String waistStyle;

    /**
     * 牛仔裤款式（如：直筒、紧身、喇叭）
     */
    private String jeansStyle;

    /**
     * 板型（如：修身、宽松、标准）
     */
    private String patternFit;

    /**
     * 样式（如：休闲、商务、运动）
     */
    private String style;

    // ============ 工艺制造属性 ============

    /**
     * 工艺（如：水洗、石磨、激光）
     */
    private String craftsmanship;

    /**
     * 编织方法（如：平纹、斜纹、提花）
     */
    private String weavingMethod;

    /**
     * 洗水工艺（如：普洗、石洗、酵洗）
     */
    private String washingProcess;

    /**
     * 检针工艺（如：过检、磁检）
     */
    private String needleDetectionProcess;

    // ============ 图案设计属性 ============

    /**
     * 图案类型（如：纯色、条纹、印花）
     */
    private String patternType;

    /**
     * 印花方法（如：丝印、数码印花）
     */
    private String printingMethod;

    /**
     * 流行元素（如：破洞、刺绣、撞色）
     */
    private String fashionElements;

    /**
     * 徽标位置（如：左胸、后领、袖口）
     */
    private String logoPosition;

    // ============ 供应商业属性 ============

    /**
     * 供应类型（如：现货、订做、批发）
     */
    private String supplyType;

    /**
     * 原产地（如：中国、越南、孟加拉）
     */
    private String origin;

    /**
     * 7天快速打样：false-否 true-是
     */
    private Boolean fastSampling;

    // ============ 规格包装属性 ============

    /**
     * 型号（如：S/M/L、均码）
     */
    private String model;

    /**
     * 季节（如：春季、夏季、四季）
     */
    private String season;

    /**
     * 销售单位（如：件、套、打）
     */
    private String salesUnit;

    /**
     * 产品视频路径
     */
    private String videoPath;
}