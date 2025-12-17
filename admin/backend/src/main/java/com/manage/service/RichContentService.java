package com.manage.service;

import com.manage.dto.richcontent.RichContentPublishRequest;
import com.manage.dto.richcontent.RichContentQueryRequest;
import com.manage.dto.richcontent.RichContentRequest;
import com.manage.dto.richcontent.RichContentResponse;
import com.manage.entity.RichContent;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 富文本内容服务接口
 *
 * @author System
 * @version 1.0
 */
public interface RichContentService {

    /**
     * 创建富文本内容
     *
     * @param request 创建请求
     * @param userId  操作用户ID
     * @return 创建的内容
     */
    RichContentResponse createContent(RichContentRequest request, Long userId);

    /**
     * 更新富文本内容
     *
     * @param id      内容ID
     * @param request 更新请求
     * @param userId  操作用户ID
     * @return 更新后的内容
     */
    RichContentResponse updateContent(Long id, RichContentRequest request, Long userId);

    /**
     * 根据ID获取内容详情
     *
     * @param id 内容ID
     * @return 内容详情
     */
    RichContentResponse getContentById(Long id);

    /**
     * 根据ID获取内容详情（公开接口，增加浏览次数）
     *
     * @param id 内容ID
     * @return 内容详情
     */
    RichContentResponse getContentByIdPublic(Long id);

    /**
     * 分页查询内容列表
     *
     * @param queryRequest 查询条件
     * @return 内容分页列表
     */
    Page<RichContentResponse> getContents(RichContentQueryRequest queryRequest);

    /**
     * 获取已发布的内容列表
     *
     * @return 已发布内容列表
     */
    List<RichContentResponse> getPublishedContent();

    /**
     * 根据内容类型获取内容列表
     *
     * @param contentType 内容类型
     * @return 内容列表
     */
    List<RichContentResponse> getContentsByType(String contentType);

    /**
     * 根据内容类型获取内容列表（分页）
     *
     * @param contentType 内容类型
     * @param page        页码
     * @param size        每页大小
     * @return 内容分页列表
     */
    Page<RichContentResponse> getContentsByType(String contentType, Integer page, Integer size);

    /**
     * 根据状态获取内容列表
     *
     * @param status 状态
     * @return 内容列表
     */
    List<RichContentResponse> getContentsByStatus(Integer status);

    /**
     * 获取置顶内容列表
     *
     * @param status 状态
     * @return 置顶内容列表
     */
    List<RichContentResponse> getTopContent(Integer status);

    /**
     * 获取推荐内容列表
     *
     * @param status 状态
     * @return 推荐内容列表
     */
    List<RichContentResponse> getFeaturedContent(Integer status);

    /**
     * 搜索内容
     *
     * @param keyword 关键词
     * @param status  状态
     * @return 搜索结果
     */
    List<RichContentResponse> searchContent(String keyword, Integer status);

    /**
     * 根据标签查找内容
     *
     * @param tag    标签
     * @param status 状态
     * @return 内容列表
     */
    List<RichContentResponse> getContentByTag(String tag, Integer status);

    /**
     * 根据作者查找内容
     *
     * @param author 作者
     * @param status 状态
     * @return 内容列表
     */
    List<RichContentResponse> getContentByAuthor(String author, Integer status);

    /**
     * 根据关联信息查找内容
     *
     * @param relatedId   关联ID
     * @param relatedType 关联类型
     * @return 内容列表
     */
    List<RichContentResponse> getContentByRelated(Long relatedId, String relatedType);

    /**
     * 发布内容
     *
     * @param request 发布请求
     * @param userId  操作用户ID
     * @return 发布结果
     */
    RichContentResponse publishContent(RichContentPublishRequest request, Long userId);

    /**
     * 下线内容
     *
     * @param id     内容ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    RichContentResponse unpublishContent(Long id, Long userId);

    /**
     * 删除内容
     *
     * @param id     内容ID
     * @param userId 操作用户ID
     */
    void deleteContent(Long id, Long userId);

    /**
     * 批量删除内容
     *
     * @param ids    内容ID列表
     * @param userId 操作用户ID
     */
    void batchDeleteContent(List<Long> ids, Long userId);

    /**
     * 批量发布内容
     *
     * @param ids    内容ID列表
     * @param userId 操作用户ID
     */
    void batchPublishContent(List<Long> ids, Long userId);

    /**
     * 批量下线内容
     *
     * @param ids    内容ID列表
     * @param userId 操作用户ID
     */
    void batchUnpublishContent(List<Long> ids, Long userId);

    /**
     * 设置内容为置顶
     *
     * @param id     内容ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    RichContentResponse setAsTop(Long id, Long userId);

    /**
     * 取消内容置顶
     *
     * @param id     内容ID
     * @ @param userId 操作用户ID
     * @return 操作结果
     */
    RichContentResponse unsetAsTop(Long id, Long userId);

    /**
     * 设置内容为推荐
     *
     * @param id     内容ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    RichContentResponse setAsFeatured(Long id, Long userId);

    /**
     * 取消内容推荐
     *
     * @param id     内容ID
     * @param userId 操作用户ID
     * @return 操作结果
     */
    RichContentResponse unsetAsFeatured(Long id, Long userId);

    /**
     * 增加浏览次数
     *
     * @param id 内容ID
     */
    void incrementViewCount(Long id);

    /**
     * 增加点赞次数
     *
     * @param id 内容ID
     */
    void incrementLikeCount(Long id);

    /**
     * 增加评论次数
     *
     * @param id 内容ID
     */
    void incrementCommentCount(Long id);

    /**
     * 获取最近发布的内容
     *
     * @param limit  限制数量
     * @param status 状态
     * @return 内容列表
     */
    List<RichContentResponse> getRecentContent(Integer limit, Integer status);

    /**
     * 获取热门内容（按浏览次数排序）
     *
     * @param limit  限制数量
     * @param status 状态
     * @return 内容列表
     */
    List<RichContentResponse> getPopularContent(Integer limit, Integer status);

    /**
     * 根据发布时间范围查找内容
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param status    状态
     * @return 内容列表
     */
    List<RichContentResponse> getContentByDateRange(LocalDateTime startTime,
                                                     LocalDateTime endTime,
                                                     Integer status);

    /**
     * 检查标题是否存在
     *
     * @param title 标题
     * @return 存在返回true，不存在返回false
     */
    boolean existsByTitle(String title);

    /**
     * 检查标题是否存在（排除指定ID）
     *
     * @param title 标题
     * @param id    排除的ID
     * @return 存在返回true，不存在返回false
     */
    boolean existsByTitleAndIdNot(String title, Long id);

    /**
     * 获取内容统计信息
     *
     * @return 统计信息
     */
    Object getContentStatistics();

    /**
     * 提取HTML中的纯文本内容
     *
     * @param htmlContent HTML内容
     * @return 纯文本内容
     */
    String extractPlainText(String htmlContent);

    /**
     * 计算内容字数
     *
     * @param content 内容
     * @return 字数
     */
    Integer calculateWordCount(String content);

    /**
     * 估算阅读时长（分钟）
     *
     * @param wordCount 字数
     * @return 阅读时长（分钟）
     */
    Integer calculateReadTime(Integer wordCount);

    /**
     * 处理内容中的图片URL（可选：转换为相对路径、添加CDN前缀等）
     *
     * @param content HTML内容
     * @return 处理后的内容
     */
    String processContentImages(String content);
}