package com.manage.service.impl;

import com.manage.common.ResultCode;
import com.manage.dto.richcontent.RichContentPublishRequest;
import com.manage.dto.richcontent.RichContentQueryRequest;
import com.manage.dto.richcontent.RichContentRequest;
import com.manage.dto.richcontent.RichContentResponse;
import com.manage.entity.RichContent;
import com.manage.exception.BusinessException;
import com.manage.repository.RichContentRepository;
import com.manage.service.RichContentService;
import com.manage.service.RichContentWebSocketService;
import com.manage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 富文本内容服务实现类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RichContentServiceImpl implements RichContentService {

    private final RichContentRepository richContentRepository;
    private final RichContentWebSocketService webSocketService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RichContentResponse createContent(RichContentRequest request, Long userId) {
        log.info("创建富文本内容，标题：{}，操作用户ID：{}", request.getTitle(), userId);

        // 检查标题是否已存在
        if (richContentRepository.existsByTitle(request.getTitle())) {
            throw new BusinessException(ResultCode.CONFLICT, "内容标题已存在");
        }

        RichContent richContent = new RichContent();
        BeanUtils.copyProperties(request, richContent);

        // 处理tags字段
        richContent.setTags(request.getTags());

        // 处理富文本内容
        if (StringUtils.hasText(request.getContent())) {
            // 提取纯文本内容
            String plainText = extractPlainText(request.getContent());
            richContent.setPlainText(plainText);

            // 处理内容中的图片
            String processedContent = processContentImages(request.getContent());
            richContent.setContent(processedContent);
        }

        richContent.setCreatedBy(userId);

        RichContent savedContent = richContentRepository.save(richContent);
        log.info("富文本内容创建成功，内容ID：{}", savedContent.getId());

        return convertToResponse(savedContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RichContentResponse updateContent(Long id, RichContentRequest request, Long userId) {
        log.info("更新富文本内容，内容ID：{}，操作用户ID：{}", id, userId);

        RichContent richContent = richContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        // 检查标题是否已被其他内容使用
        if (!request.getTitle().equals(richContent.getTitle()) &&
            richContentRepository.existsByTitleAndIdNot(request.getTitle(), id)) {
            throw new BusinessException(ResultCode.CONFLICT, "内容标题已存在");
        }

        BeanUtils.copyProperties(request, richContent);

        // 处理tags字段
        richContent.setTags(request.getTags());

        // 处理富文本内容
        if (StringUtils.hasText(request.getContent())) {
            // 提取纯文本内容
            String plainText = extractPlainText(request.getContent());
            richContent.setPlainText(plainText);

            // 处理内容中的图片
            String processedContent = processContentImages(request.getContent());
            richContent.setContent(processedContent);
        }

        richContent.setUpdatedBy(userId);

        RichContent savedContent = richContentRepository.save(richContent);
        log.info("富文本内容更新成功，内容ID：{}", savedContent.getId());

        return convertToResponse(savedContent);
    }

    @Override
    public RichContentResponse getContentById(Long id) {
        RichContent richContent = richContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));
        return convertToResponse(richContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RichContentResponse getContentByIdPublic(Long id) {
        RichContent richContent = richContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        // 只有已发布的内容才能公开访问
        if (richContent.getStatus() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "内容未发布");
        }

        // 增加浏览次数
        richContentRepository.incrementViewCount(id);
        richContent.setViewCount(richContent.getViewCount() + 1);

        return convertToResponse(richContent);
    }

    @Override
    public Page<RichContentResponse> getContents(RichContentQueryRequest queryRequest) {
        log.info("分页查询富文本内容列表，查询条件：{}", queryRequest);

        // 构建分页和排序参数
        Sort.Direction direction = "ASC".equalsIgnoreCase(queryRequest.getDirection()) ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(queryRequest.getPage(), queryRequest.getSize(),
                Sort.by(direction, queryRequest.getSort()));

        Page<RichContent> contentPage;

        // 根据查询条件选择不同的查询方法
        if (StringUtils.hasText(queryRequest.getKeyword())) {
            // 关键词搜索
            contentPage = richContentRepository.findByContentTypeAndStatusOrderByCreateTimeDesc(
                    queryRequest.getContentType(),
                    queryRequest.getStatus() != null ? queryRequest.getStatus() : 1,
                    pageable);
        } else if (StringUtils.hasText(queryRequest.getContentType())) {
            // 按内容类型查询
            Integer status = queryRequest.getStatus() != null ? queryRequest.getStatus() : 1;
            contentPage = richContentRepository.findByContentTypeAndStatusOrderByCreateTimeDesc(
                    queryRequest.getContentType(), status, pageable);
        } else {
            // 默认查询
            contentPage = richContentRepository.findAll(pageable);
        }

        return contentPage.map(this::convertToResponse);
    }

    @Override
    public List<RichContentResponse> getPublishedContent() {
        List<RichContent> contents = richContentRepository.findPublishedContent();
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RichContentResponse> getContentsByType(String contentType) {
        List<RichContent> contents = richContentRepository.findByContentTypeAndStatusOrderBySortOrderAscCreateTimeDesc(
                contentType, 1);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RichContentResponse> getContentsByType(String contentType, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<RichContent> contentPage = richContentRepository.findByContentTypeOrderByCreateTimeDesc(
                contentType, pageable);
        return contentPage.map(this::convertToResponse);
    }

    @Override
    public List<RichContentResponse> getContentsByStatus(Integer status) {
        List<RichContent> contents = richContentRepository.findByStatusOrderByCreateTimeDesc(status);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RichContentResponse> getTopContent(Integer status) {
        List<RichContent> contents = richContentRepository.findTopContentByStatus(status);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RichContentResponse> getFeaturedContent(Integer status) {
        List<RichContent> contents = richContentRepository.findFeaturedContentByStatus(status);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RichContentResponse> searchContent(String keyword, Integer status) {
        List<RichContent> contents = richContentRepository.findByKeywordAndStatus(keyword, status);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RichContentResponse> getContentByTag(String tag, Integer status) {
        List<RichContent> contents = richContentRepository.findByTagAndStatus(tag, status);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RichContentResponse> getContentByAuthor(String author, Integer status) {
        List<RichContent> contents = richContentRepository.findByAuthorAndStatusOrderByCreateTimeDesc(author, status);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RichContentResponse> getContentByRelated(Long relatedId, String relatedType) {
        List<RichContent> contents = richContentRepository.findByRelatedIdAndRelatedTypeOrderByCreateTimeDesc(
                relatedId, relatedType);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RichContentResponse publishContent(RichContentPublishRequest request, Long userId) {
        log.info("发布富文本内容，内容ID：{}，操作用户ID：{}", request.getId(), userId);

        RichContent richContent = richContentRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        richContent.setStatus(1); // 设置为已发布

        if (request.getPublishNow()) {
            richContent.setPublishTime(LocalDateTime.now());
        } else if (StringUtils.hasText(request.getPublishTime())) {
            try {
                LocalDateTime publishTime = LocalDateTime.parse(request.getPublishTime(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                richContent.setPublishTime(publishTime);
            } catch (Exception e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "发布时间格式错误");
            }
        } else {
            richContent.setPublishTime(LocalDateTime.now());
        }

        richContent.setUpdatedBy(userId);

        RichContent savedContent = richContentRepository.save(richContent);
        log.info("富文本内容发布成功，内容ID：{}", savedContent.getId());

        // 发送WebSocket通知
        RichContentResponse response = convertToResponse(savedContent);
        try {
            String username = userService.findById(userId).getUsername();
            webSocketService.notifyContentPublish(
                    savedContent.getId(),
                    username,
                    response,
                    request.getNotification()
            );
        } catch (Exception e) {
            log.warn("发送内容发布WebSocket通知失败：{}", e.getMessage());
        }

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RichContentResponse unpublishContent(Long id, Long userId) {
        log.info("下线富文本内容，内容ID：{}，操作用户ID：{}", id, userId);

        RichContent richContent = richContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        richContent.setStatus(2); // 设置为已下线
        richContent.setUpdatedBy(userId);

        RichContent savedContent = richContentRepository.save(richContent);
        log.info("富文本内容下线成功，内容ID：{}", savedContent.getId());

        return convertToResponse(savedContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContent(Long id, Long userId) {
        log.info("删除富文本内容，内容ID：{}，操作用户ID：{}", id, userId);

        RichContent richContent = richContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        richContentRepository.delete(richContent);
        log.info("富文本内容删除成功，内容ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteContent(List<Long> ids, Long userId) {
        log.info("批量删除富文本内容，内容ID列表：{}，操作用户ID：{}", ids, userId);

        List<RichContent> contents = richContentRepository.findAllById(ids);
        if (contents.size() != ids.size()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部分内容不存在");
        }

        richContentRepository.deleteAll(contents);
        log.info("批量删除富文本内容成功，删除数量：{}", contents.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchPublishContent(List<Long> ids, Long userId) {
        log.info("批量发布富文本内容，内容ID列表：{}，操作用户ID：{}", ids, userId);

        List<RichContent> contents = richContentRepository.findAllById(ids);
        if (contents.size() != ids.size()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部分内容不存在");
        }

        contents.forEach(content -> {
            content.setStatus(1);
            content.setPublishTime(LocalDateTime.now());
            content.setUpdatedBy(userId);
        });

        richContentRepository.saveAll(contents);
        log.info("批量发布富文本内容成功，发布数量：{}", contents.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUnpublishContent(List<Long> ids, Long userId) {
        log.info("批量下线富文本内容，内容ID列表：{}，操作用户ID：{}", ids, userId);

        List<RichContent> contents = richContentRepository.findAllById(ids);
        if (contents.size() != ids.size()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部分内容不存在");
        }

        contents.forEach(content -> {
            content.setStatus(2);
            content.setUpdatedBy(userId);
        });

        richContentRepository.saveAll(contents);
        log.info("批量下线富文本内容成功，下线数量：{}", contents.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RichContentResponse setAsTop(Long id, Long userId) {
        log.info("设置富文本内容为置顶，内容ID：{}，操作用户ID：{}", id, userId);

        RichContent richContent = richContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        richContent.setIsTop(true);
        richContent.setUpdatedBy(userId);

        RichContent savedContent = richContentRepository.save(richContent);
        log.info("富文本内容设置为置顶成功，内容ID：{}", savedContent.getId());

        return convertToResponse(savedContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RichContentResponse unsetAsTop(Long id, Long userId) {
        log.info("取消富文本内容置顶，内容ID：{}，操作用户ID：{}", id, userId);

        RichContent richContent = richContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        richContent.setIsTop(false);
        richContent.setUpdatedBy(userId);

        RichContent savedContent = richContentRepository.save(richContent);
        log.info("取消富文本内容置顶成功，内容ID：{}", savedContent.getId());

        return convertToResponse(savedContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RichContentResponse setAsFeatured(Long id, Long userId) {
        log.info("设置富文本内容为推荐，内容ID：{}，操作用户ID：{}", id, userId);

        RichContent richContent = richContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        richContent.setIsFeatured(true);
        richContent.setUpdatedBy(userId);

        RichContent savedContent = richContentRepository.save(richContent);
        log.info("富文本内容设置为推荐成功，内容ID：{}", savedContent.getId());

        return convertToResponse(savedContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RichContentResponse unsetAsFeatured(Long id, Long userId) {
        log.info("取消富文本内容推荐，内容ID：{}，操作用户ID：{}", id, userId);

        RichContent richContent = richContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        richContent.setIsFeatured(false);
        richContent.setUpdatedBy(userId);

        RichContent savedContent = richContentRepository.save(richContent);
        log.info("取消富文本内容推荐成功，内容ID：{}", savedContent.getId());

        return convertToResponse(savedContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        richContentRepository.incrementViewCount(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementLikeCount(Long id) {
        richContentRepository.incrementLikeCount(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementCommentCount(Long id) {
        richContentRepository.incrementCommentCount(id);
    }

    @Override
    public List<RichContentResponse> getRecentContent(Integer limit, Integer status) {
        List<RichContent> contents = richContentRepository.findRecentContent(limit, status);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RichContentResponse> getPopularContent(Integer limit, Integer status) {
        List<RichContent> contents = richContentRepository.findPopularContent(limit, status);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RichContentResponse> getContentByDateRange(LocalDateTime startTime,
                                                           LocalDateTime endTime,
                                                           Integer status) {
        List<RichContent> contents = richContentRepository.findByPublishTimeBetweenAndStatus(
                startTime, endTime, status);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTitle(String title) {
        return richContentRepository.existsByTitle(title);
    }

    @Override
    public boolean existsByTitleAndIdNot(String title, Long id) {
        return richContentRepository.existsByTitleAndIdNot(title, id);
    }

    @Override
    public Object getContentStatistics() {
        List<Object[]> statusResults = richContentRepository.countContentsByStatus();
        List<Object[]> typeResults = richContentRepository.countContentsByType();

        return new Object() {
            public final List<Object[]> statusStatistics = statusResults;
            public final List<Object[]> typeStatistics = typeResults;
        };
    }

    @Override
    public String extractPlainText(String htmlContent) {
        if (!StringUtils.hasText(htmlContent)) {
            return "";
        }

        // 简单的HTML标签去除，实际项目中建议使用Jsoup等HTML解析库
        String plainText = htmlContent
                .replaceAll("<[^>]*>", "") // 移除HTML标签
                .replaceAll("&nbsp;", " ") // 替换空格实体
                .replaceAll("&amp;", "&") // 替换&实体
                .replaceAll("&lt;", "<") // 替换<实体
                .replaceAll("&gt;", ">") // 替换>实体
                .replaceAll("&quot;", "\"") // 替换"实体
                .replaceAll("&#39;", "'") // 替换'实体
                .trim();

        // 限制纯文本长度，避免过长
        if (plainText.length() > 1000) {
            plainText = plainText.substring(0, 1000);
        }

        return plainText;
    }

    @Override
    public Integer calculateWordCount(String content) {
        if (!StringUtils.hasText(content)) {
            return 0;
        }

        // 移除HTML标签后计算字数
        String plainText = extractPlainText(content);

        // 中文字符计数
        int chineseCharCount = plainText.replaceAll("[^\\u4e00-\\u9fa5]", "").length();

        // 英文单词计数（简单按空格分割）
        int englishWordCount = plainText.replaceAll("[\\u4e00-\\u9fa5]", " ")
                .trim().isEmpty() ? 0 : plainText.replaceAll("[\\u4e00-\\u9fa5]", " ")
                .trim().split("\\s+").length;

        return chineseCharCount + englishWordCount;
    }

    @Override
    public Integer calculateReadTime(Integer wordCount) {
        // 按照每分钟300字的阅读速度估算
        if (wordCount <= 0) {
            return 1;
        }

        int readTime = Math.max(1, wordCount / 300);
        return Math.min(readTime, 60); // 最大不超过60分钟
    }

    @Override
    public String processContentImages(String content) {
        if (!StringUtils.hasText(content)) {
            return content;
        }

        // 这里可以添加图片处理逻辑，例如：
        // 1. 将相对路径转换为绝对路径
        // 2. 添加CDN前缀
        // 3. 图片压缩处理
        // 4. 添加水印等

        // 示例：处理相对路径为绝对路径
        String processedContent = content.replaceAll("src=\"/uploads/",
                "src=\"https://your-cdn-domain.com/uploads/");

        return processedContent;
    }

    /**
     * 转换为响应DTO
     */
    private RichContentResponse convertToResponse(RichContent richContent) {
        RichContentResponse response = new RichContentResponse();
        BeanUtils.copyProperties(richContent, response);

        // 设置状态文本
        response.setStatusText(getStatusText(richContent.getStatus()));

        // 设置内容类型文本
        response.setContentTypeText(getContentTypeText(richContent.getContentType()));

        // 处理标签字符串转换
        if (StringUtils.hasText(richContent.getTags())) {
            response.setTags(Arrays.asList(richContent.getTags().split(",")));
        }

        // 计算字数和阅读时长
        if (StringUtils.hasText(richContent.getPlainText())) {
            response.setWordCount(calculateWordCount(richContent.getPlainText()));
            response.setReadTime(calculateReadTime(response.getWordCount()));
        } else if (StringUtils.hasText(richContent.getContent())) {
            response.setWordCount(calculateWordCount(richContent.getContent()));
            response.setReadTime(calculateReadTime(response.getWordCount()));
        }

        return response;
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0:
                return "草稿";
            case 1:
                return "已发布";
            case 2:
                return "已下线";
            default:
                return "未知";
        }
    }

    /**
     * 获取内容类型文本
     */
    private String getContentTypeText(String contentType) {
        switch (contentType) {
            case "article":
                return "文章";
            case "page":
                return "页面";
            case "product_desc":
                return "产品描述";
            case "news":
                return "新闻";
            default:
                return contentType;
        }
    }
}