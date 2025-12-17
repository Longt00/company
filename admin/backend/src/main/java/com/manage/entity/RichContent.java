package com.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * 富文本内容实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "rich_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichContent {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 内容标题
     */
    @NotBlank(message = "内容标题不能为空")
    @Size(max = 200, message = "内容标题长度不能超过200个字符")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 内容类型：article-文章, page-页面, product_desc-产品描述, news-新闻
     */
    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    /**
     * 内容摘要
     */
    @Size(max = 500, message = "内容摘要长度不能超过500个字符")
    @Column(name = "summary", length = 500)
    private String summary;

    /**
     * 富文本内容（HTML格式）
     */
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    /**
     * 纯文本内容（用于搜索）
     */
    @Column(name = "plain_text", columnDefinition = "TEXT")
    private String plainText;

    /**
     * 封面图片
     */
    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    /**
     * 内容标签（多个标签用逗号分隔）
     */
    @Size(max = 500, message = "内容标签长度不能超过500个字符")
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * 作者
     */
    @Size(max = 100, message = "作者长度不能超过100个字符")
    @Column(name = "author", length = 100)
    private String author;

    /**
     * 来源
     */
    @Size(max = 200, message = "来源长度不能超过200个字符")
    @Column(name = "source", length = 200)
    private String source;

    /**
     * 关联ID（用于关联其他实体，如产品ID）
     */
    @Column(name = "related_id")
    private Long relatedId;

    /**
     * 关联类型
     */
    @Size(max = 50, message = "关联类型长度不能超过50个字符")
    @Column(name = "related_type", length = 50)
    private String relatedType;

    /**
     * SEO标题
     */
    @Size(max = 200, message = "SEO标题长度不能超过200个字符")
    @Column(name = "seo_title", length = 200)
    private String seoTitle;

    /**
     * SEO关键词
     */
    @Size(max = 500, message = "SEO关键词长度不能超过500个字符")
    @Column(name = "seo_keywords", length = 500)
    private String seoKeywords;

    /**
     * SEO描述
     */
    @Size(max = 500, message = "SEO描述长度不能超过500个字符")
    @Column(name = "seo_description", length = 500)
    private String seoDescription;

    /**
     * 状态：0-草稿 1-发布 2-下线
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    /**
     * 是否置顶：0-否 1-是
     */
    @Column(name = "is_top", nullable = false)
    private Boolean isTop = false;

    /**
     * 是否推荐：0-否 1-是
     */
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    /**
     * 浏览次数
     */
    @Column(name = "view_count")
    private Integer viewCount = 0;

    /**
     * 点赞次数
     */
    @Column(name = "like_count")
    private Integer likeCount = 0;

    /**
     * 评论次数
     */
    @Column(name = "comment_count")
    private Integer commentCount = 0;

    /**
     * 发布时间
     */
    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    /**
     * 排序值
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

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