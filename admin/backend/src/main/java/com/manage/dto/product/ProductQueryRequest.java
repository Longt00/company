package com.manage.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 产品查询请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class ProductQueryRequest {

    /**
     * 页码，从0开始
     */
    @JsonProperty("page")
    private Integer page = 0;

    /**
     * 每页大小
     */
    @JsonProperty("size")
    private Integer size = 10;

    /**
     * 排序字段
     */
    @JsonProperty("sort")
    private String sort = "createTime";

    /**
     * 排序方向：ASC/DESC
     */
    @JsonProperty("direction")
    private String direction = "DESC";

    /**
     * 产品名称（模糊查询）
     */
    @JsonProperty("productName")
    private String productName;

    /**
     * 产品分类
     */
    @JsonProperty("category")
    private String category;

    /**
     * 产品状态：0-下架 1-上架 2-草稿
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 是否推荐：0-否 1-是
     */
    @JsonProperty("isFeatured")
    private Boolean isFeatured;

    /**
     * 产品标签
     */
    @JsonProperty("tag")
    private String tag;

    /**
     * 最低价格
     */
    @JsonProperty("minPrice")
    private Double minPrice;

    /**
     * 最高价格
     */
    @JsonProperty("maxPrice")
    private Double maxPrice;

    /**
     * 产品编码
     */
    @JsonProperty("productCode")
    private String productCode;
}