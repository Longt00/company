package com.manage.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 产品信息请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class ProductRequest {

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称长度不能超过200个字符")
    @JsonProperty("productName")
    private String productName;

    /**
     * 产品编码
     */
    @Size(max = 100, message = "产品编码长度不能超过100个字符")
    @JsonProperty("productCode")
    private String productCode;

    /**
     * 产品分类
     */
    @Size(max = 100, message = "产品分类长度不能超过100个字符")
    @JsonProperty("category")
    private String category;

    /**
     * 产品描述
     */
    @Size(max = 2000, message = "产品描述长度不能超过2000个字符")
    @JsonProperty("description")
    private String description;

    /**
     * 产品规格
     */
    @Size(max = 500, message = "产品规格长度不能超过500个字符")
    @JsonProperty("specifications")
    private String specifications;

    /**
     * 产品主图
     */
    @Size(max = 500, message = "主图URL长度不能超过500个字符")
    @JsonProperty("mainImage")
    private String mainImage;

    /**
     * 产品图片列表
     */
    @JsonProperty("productImages")
    private String productImages;

    /**
     * 产品价格
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "产品价格不能为负数")
    @JsonProperty("price")
    private BigDecimal price;

    /**
     * 市场价格
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "市场价格不能为负数")
    @JsonProperty("marketPrice")
    private BigDecimal marketPrice;

    /**
     * 库存数量
     */
    @Min(value = 0, message = "库存数量不能为负数")
    @JsonProperty("stockQuantity")
    private Integer stockQuantity = 0;

    /**
     * 产品标签
     */
    @JsonProperty("tags")
    private List<String> tags;

    /**
     * 产品状态：0-下架 1-上架 2-草稿
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 2, message = "状态值无效")
    @JsonProperty("status")
    private Integer status = 1;

    /**
     * 是否推荐：0-否 1-是
     */
    @JsonProperty("isFeatured")
    private Boolean isFeatured = false;

    /**
     * 是否新品：0-否 1-是
     */
    @JsonProperty("isNew")
    private Boolean isNew = false;

    /**
     * 是否主要产品：0-否 1-是
     */
    @JsonProperty("isMain")
    private Boolean isMain = false;

    /**
     * 排序值
     */
    @Min(value = 0, message = "排序值不能为负数")
    @JsonProperty("sortOrder")
    private Integer sortOrder = 0;

    /**
     * SEO关键词
     */
    @Size(max = 200, message = "SEO关键词长度不能超过200个字符")
    @JsonProperty("seoKeywords")
    private String seoKeywords;

    /**
     * SEO描述
     */
    @Size(max = 500, message = "SEO描述长度不能超过500个字符")
    @JsonProperty("seoDescription")
    private String seoDescription;

    // ============ 服装材质属性 ============

    /**
     * 材质（如：纯棉、涤纶、牛仔布）
     */
    @Size(max = 100, message = "材质长度不能超过100个字符")
    @JsonProperty("material")
    private String material;

    /**
     * 面料类型（如：梭织布、针织布）
     */
    @Size(max = 100, message = "面料类型长度不能超过100个字符")
    @JsonProperty("fabricType")
    private String fabricType;

    /**
     * 面料克重（如：180g、220g）
     */
    @Size(max = 50, message = "面料克重长度不能超过50个字符")
    @JsonProperty("fabricWeight")
    private String fabricWeight;

    // ============ 服装款式属性 ============

    /**
     * 腰型（如：高腰、中腰、低腰）
     */
    @Size(max = 50, message = "腰型长度不能超过50个字符")
    @JsonProperty("waistStyle")
    private String waistStyle;

    /**
     * 牛仔裤款式（如：直筒、紧身、喇叭）
     */
    @Size(max = 50, message = "牛仔裤款式长度不能超过50个字符")
    @JsonProperty("jeansStyle")
    private String jeansStyle;

    /**
     * 板型（如：修身、宽松、标准）
     */
    @Size(max = 50, message = "板型长度不能超过50个字符")
    @JsonProperty("patternFit")
    private String patternFit;

    /**
     * 样式（如：休闲、商务、运动）
     */
    @Size(max = 100, message = "样式长度不能超过100个字符")
    @JsonProperty("style")
    private String style;

    // ============ 工艺制造属性 ============

    /**
     * 工艺（如：水洗、石磨、激光）
     */
    @Size(max = 200, message = "工艺长度不能超过200个字符")
    @JsonProperty("craftsmanship")
    private String craftsmanship;

    /**
     * 编织方法（如：平纹、斜纹、提花）
     */
    @Size(max = 100, message = "编织方法长度不能超过100个字符")
    @JsonProperty("weavingMethod")
    private String weavingMethod;

    /**
     * 洗水工艺（如：普洗、石洗、酵洗）
     */
    @Size(max = 100, message = "洗水工艺长度不能超过100个字符")
    @JsonProperty("washingProcess")
    private String washingProcess;

    /**
     * 检针工艺（如：过检、磁检）
     */
    @Size(max = 50, message = "检针工艺长度不能超过50个字符")
    @JsonProperty("needleDetectionProcess")
    private String needleDetectionProcess;

    // ============ 图案设计属性 ============

    /**
     * 图案类型（如：纯色、条纹、印花）
     */
    @Size(max = 100, message = "图案类型长度不能超过100个字符")
    @JsonProperty("patternType")
    private String patternType;

    /**
     * 印花方法（如：丝印、数码印花）
     */
    @Size(max = 100, message = "印花方法长度不能超过100个字符")
    @JsonProperty("printingMethod")
    private String printingMethod;

    /**
     * 流行元素（如：破洞、刺绣、撞色）
     */
    @Size(max = 200, message = "流行元素长度不能超过200个字符")
    @JsonProperty("fashionElements")
    private String fashionElements;

    /**
     * 徽标位置（如：左胸、后领、袖口）
     */
    @Size(max = 100, message = "徽标位置长度不能超过100个字符")
    @JsonProperty("logoPosition")
    private String logoPosition;

    // ============ 供应商业属性 ============

    /**
     * 供应类型（如：现货、订做、批发）
     */
    @Size(max = 50, message = "供应类型长度不能超过50个字符")
    @JsonProperty("supplyType")
    private String supplyType;

    /**
     * 原产地（如：中国、越南、孟加拉）
     */
    @Size(max = 100, message = "原产地长度不能超过100个字符")
    @JsonProperty("origin")
    private String origin;

    /**
     * 7天快速打样：false-否 true-是
     */
    @JsonProperty("fastSampling")
    private Boolean fastSampling = false;

    // ============ 规格包装属性 ============

    /**
     * 型号（如：S/M/L、均码）
     */
    @Size(max = 50, message = "型号长度不能超过50个字符")
    @JsonProperty("model")
    private String model;

    /**
     * 季节（如：春季、夏季、四季）
     */
    @Size(max = 50, message = "季节长度不能超过50个字符")
    @JsonProperty("season")
    private String season;

    /**
     * 销售单位（如：件、套、打）
     */
    @Size(max = 50, message = "销售单位长度不能超过50个字符")
    @JsonProperty("salesUnit")
    private String salesUnit;

    /**
     * 产品视频路径
     */
    @Size(max = 500, message = "产品视频路径长度不能超过500个字符")
    @JsonProperty("videoPath")
    private String videoPath;
}