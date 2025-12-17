package com.manage.dto.richcontent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 富文本内容请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class RichContentRequest {

    /**
     * Tags字段的自定义反序列化器
     */
    public static class TagsDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            if (node.isArray()) {
                // 如果是数组，转换为逗号分隔的字符串
                StringBuilder sb = new StringBuilder();
                for (JsonNode element : node) {
                    if (element.isTextual()) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append(element.asText());
                    }
                }
                return sb.toString();
            } else if (node.isTextual()) {
                // 如果是字符串，直接返回
                return node.asText();
            }
            // 其他情况返回空字符串
            return "";
        }
    }

    /**
     * 内容标题
     */
    @NotBlank(message = "内容标题不能为空")
    @Size(max = 200, message = "内容标题长度不能超过200个字符")
    @JsonProperty("title")
    private String title;

    /**
     * 内容类型：article-文章, page-页面, product_desc-产品描述, news-新闻
     */
    @JsonProperty("contentType")
    private String contentType = "article";

    /**
     * 内容摘要
     */
    @Size(max = 500, message = "内容摘要长度不能超过500个字符")
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
    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    @JsonProperty("coverImage")
    private String coverImage;

    /**
     * 内容标签（多个标签用逗号分隔）
     * 支持字符串和数组两种格式
     */
    @Size(max = 500, message = "内容标签长度不能超过500个字符")
    @JsonProperty("tags")
    @JsonDeserialize(using = TagsDeserializer.class)
    private String tags;

    
    /**
     * 作者
     */
    @Size(max = 100, message = "作者长度不能超过100个字符")
    @JsonProperty("author")
    private String author;

    /**
     * 来源
     */
    @Size(max = 200, message = "来源长度不能超过200个字符")
    @JsonProperty("source")
    private String source;

    /**
     * 关联ID（用于关联其他实体，如产品ID）
     */
    @JsonProperty("relatedId")
    private Long relatedId;

    /**
     * 关联类型
     */
    @Size(max = 50, message = "关联类型长度不能超过50个字符")
    @JsonProperty("relatedType")
    private String relatedType;

    /**
     * SEO标题
     */
    @Size(max = 200, message = "SEO标题长度不能超过200个字符")
    @JsonProperty("seoTitle")
    private String seoTitle;

    /**
     * SEO关键词
     */
    @Size(max = 500, message = "SEO关键词长度不能超过500个字符")
    @JsonProperty("seoKeywords")
    private String seoKeywords;

    /**
     * SEO描述
     */
    @Size(max = 500, message = "SEO描述长度不能超过500个字符")
    @JsonProperty("seoDescription")
    private String seoDescription;

    /**
     * 状态：0-草稿 1-发布 2-下线
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 2, message = "状态值无效")
    @JsonProperty("status")
    private Integer status = 0;

    /**
     * 是否置顶：0-否 1-是
     */
    @JsonProperty("isTop")
    private Boolean isTop = false;

    /**
     * 是否推荐：0-否 1-是
     */
    @JsonProperty("isFeatured")
    private Boolean isFeatured = false;

    /**
     * 排序值
     */
    @Min(value = 0, message = "排序值不能为负数")
    @JsonProperty("sortOrder")
    private Integer sortOrder = 0;
}