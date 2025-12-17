package com.manage.dto.richcontent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 富文本内容响应DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class RichContentResponse {

    /**
     * 主键ID
     */
    @JsonProperty("id")
    private Long id;

    /**
     * 内容标题
     */
    @JsonProperty("title")
    private String title;

    /**
     * 内容类型
     */
    @JsonProperty("contentType")
    private String contentType;

    /**
     * 内容类型文本
     */
    @JsonProperty("contentTypeText")
    private String contentTypeText;

    /**
     * 内容摘要
     */
    @JsonProperty("summary")
    private String summary;

    /**
     * 富文本内容（HTML格式）
     */
    @JsonProperty("content")
    private String content;

    /**
     * 纯文本内容（用于搜索）
     */
    @JsonProperty("plainText")
    private String plainText;

    /**
     * 封面图片
     */
    @JsonProperty("coverImage")
    private String coverImage;

    /**
     * 内容标签
     */
    @JsonProperty("tags")
    private List<String> tags;

    /**
     * 作者
     */
    @JsonProperty("author")
    private String author;

    /**
     * 来源
     */
    @JsonProperty("source")
    private String source;

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
     * SEO标题
     */
    @JsonProperty("seoTitle")
    private String seoTitle;

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
     * 状态
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 状态文本
     */
    @JsonProperty("statusText")
    private String statusText;

    /**
     * 是否置顶
     */
    @JsonProperty("isTop")
    private Boolean isTop;

    /**
     * 是否推荐
     */
    @JsonProperty("isFeatured")
    private Boolean isFeatured;

    /**
     * 浏览次数
     */
    @JsonProperty("viewCount")
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @JsonProperty("likeCount")
    private Integer likeCount;

    /**
     * 评论次数
     */
    @JsonProperty("commentCount")
    private Integer commentCount;

    /**
     * 发布时间
     */
    @JsonProperty("publishTime")
    private LocalDateTime publishTime;

    /**
     * 排序值
     */
    @JsonProperty("sortOrder")
    private Integer sortOrder;

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

    /**
     * 内容字数
     */
    @JsonProperty("wordCount")
    private Integer wordCount;

    /**
     * 阅读时长（分钟）
     */
    @JsonProperty("readTime")
    private Integer readTime;
}