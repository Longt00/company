package com.manage.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 产品信息响应DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class ProductResponse {

    /**
     * 主键ID
     */
    @JsonProperty("id")
    private Long id;

    /**
     * 产品名称
     */
    @JsonProperty("productName")
    private String productName;

    /**
     * 产品编码
     */
    @JsonProperty("productCode")
    private String productCode;

    /**
     * 产品分类
     */
    @JsonProperty("category")
    private String category;

    /**
     * 产品描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 产品规格
     */
    @JsonProperty("specifications")
    private String specifications;

    /**
     * 产品主图
     */
    @JsonProperty("mainImage")
    private String mainImage;

    /**
     * 产品图片列表
     */
    @JsonProperty("productImages")
    private List<String> productImages;

    /**
     * 产品价格
     */
    @JsonProperty("price")
    private BigDecimal price;

    /**
     * 市场价格
     */
    @JsonProperty("marketPrice")
    private BigDecimal marketPrice;

    /**
     * 库存数量
     */
    @JsonProperty("stockQuantity")
    private Integer stockQuantity;

    /**
     * 产品标签
     */
    @JsonProperty("tags")
    private List<String> tags;

    /**
     * 产品状态：0-下架 1-上架 2-草稿
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 状态描述
     */
    @JsonProperty("statusText")
    private String statusText;

    /**
     * 是否推荐：0-否 1-是
     */
    @JsonProperty("isFeatured")
    private Boolean isFeatured;

    /**
     * 是否新品：0-否 1-是
     */
    @JsonProperty("isNew")
    private Boolean isNew;

    /**
     * 是否主要产品：0-否 1-是
     */
    @JsonProperty("isMain")
    private Boolean isMain;

    /**
     * 排序值
     */
    @JsonProperty("sortOrder")
    private Integer sortOrder;

    /**
     * SEO关键词
     */
    @JsonProperty("seoKeywords")
    private String seoKeywords;

    /**
     * SEO描述
     */
    @JsonProperty("seoDescription")
    private String seoDescription;

    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @JsonProperty("createdBy")
    private Long createdBy;

    /**
     * 更新人ID
     */
    @JsonProperty("updatedBy")
    private Long updatedBy;

    // ============ 服装材质属性 ============

    /**
     * 材质（如：纯棉、涤纶、牛仔布）
     */
    @JsonProperty("material")
    private String material;

    /**
     * 面料类型（如：梭织布、针织布）
     */
    @JsonProperty("fabricType")
    private String fabricType;

    /**
     * 面料克重（如：180g、220g）
     */
    @JsonProperty("fabricWeight")
    private String fabricWeight;

    // ============ 服装款式属性 ============

    /**
     * 腰型（如：高腰、中腰、低腰）
     */
    @JsonProperty("waistStyle")
    private String waistStyle;

    /**
     * 牛仔裤款式（如：直筒、紧身、喇叭）
     */
    @JsonProperty("jeansStyle")
    private String jeansStyle;

    /**
     * 板型（如：修身、宽松、标准）
     */
    @JsonProperty("patternFit")
    private String patternFit;

    /**
     * 样式（如：休闲、商务、运动）
     */
    @JsonProperty("style")
    private String style;

    // ============ 工艺制造属性 ============

    /**
     * 工艺（如：水洗、石磨、激光）
     */
    @JsonProperty("craftsmanship")
    private String craftsmanship;

    /**
     * 编织方法（如：平纹、斜纹、提花）
     */
    @JsonProperty("weavingMethod")
    private String weavingMethod;

    /**
     * 洗水工艺（如：普洗、石洗、酵洗）
     */
    @JsonProperty("washingProcess")
    private String washingProcess;

    /**
     * 检针工艺（如：过检、磁检）
     */
    @JsonProperty("needleDetectionProcess")
    private String needleDetectionProcess;

    // ============ 图案设计属性 ============

    /**
     * 图案类型（如：纯色、条纹、印花）
     */
    @JsonProperty("patternType")
    private String patternType;

    /**
     * 印花方法（如：丝印、数码印花）
     */
    @JsonProperty("printingMethod")
    private String printingMethod;

    /**
     * 流行元素（如：破洞、刺绣、撞色）
     */
    @JsonProperty("fashionElements")
    private String fashionElements;

    /**
     * 徽标位置（如：左胸、后领、袖口）
     */
    @JsonProperty("logoPosition")
    private String logoPosition;

    // ============ 供应商业属性 ============

    /**
     * 供应类型（如：现货、订做、批发）
     */
    @JsonProperty("supplyType")
    private String supplyType;

    /**
     * 原产地（如：中国、越南、孟加拉）
     */
    @JsonProperty("origin")
    private String origin;

    /**
     * 7天快速打样：false-否 true-是
     */
    @JsonProperty("fastSampling")
    private Boolean fastSampling;

    // ============ 规格包装属性 ============

    /**
     * 型号（如：S/M/L、均码）
     */
    @JsonProperty("model")
    private String model;

    /**
     * 季节（如：春季、夏季、四季）
     */
    @JsonProperty("season")
    private String season;

    /**
     * 销售单位（如：件、套、打）
     */
    @JsonProperty("salesUnit")
    private String salesUnit;

    /**
     * 产品视频路径
     */
    @JsonProperty("videoPath")
    private String videoPath;
}