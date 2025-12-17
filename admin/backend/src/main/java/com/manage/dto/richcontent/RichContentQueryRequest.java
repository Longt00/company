package com.manage.dto.richcontent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 富文本内容查询请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class RichContentQueryRequest {

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
     * 内容标题（模糊查询）
     */
    @JsonProperty("title")
    private String title;

    /**
     * 内容类型：article-文章, page-页面, product_desc-产品描述, news-新闻
     */
    @JsonProperty("contentType")
    private String contentType;

    /**
     * 内容状态：0-草稿 1-发布 2-下线
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 是否置顶：0-否 1-是
     */
    @JsonProperty("isTop")
    private Boolean isTop;

    /**
     * 是否推荐：0-否 1-是
     */
    @JsonProperty("isFeatured")
    private Boolean isFeatured;

    /**
     * 内容标签
     */
    @JsonProperty("tag")
    private String tag;

    /**
     * 作者
     */
    @JsonProperty("author")
    private String author;

    /**
     * 关联ID
     */
    @JsonProperty("relatedId")
    private Long relatedId;

    /**
     * 关联类型
     */
    @JsonProperty("relatedType")
    private String relatedType;

    /**
     * 关键词搜索（标题和内容）
     */
    @JsonProperty("keyword")
    private String keyword;

    /**
     * 是否包含内容详情
     */
    @JsonProperty("includeContent")
    private Boolean includeContent = false;

    /**
     * 开始时间
     */
    @JsonProperty("startTime")
    private String startTime;

    /**
     * 结束时间
     */
    @JsonProperty("endTime")
    private String endTime;
}