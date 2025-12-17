package com.manage.controller;

import com.manage.common.Result;
import com.manage.dto.richcontent.RichContentPublishRequest;
import com.manage.dto.richcontent.RichContentQueryRequest;
import com.manage.dto.richcontent.RichContentRequest;
import com.manage.dto.richcontent.RichContentResponse;
import com.manage.entity.User;
import com.manage.service.RichContentService;
import com.manage.service.UserService;
import com.manage.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 富文本内容管理控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/rich-content")
@Validated
@RequiredArgsConstructor
public class RichContentController {

    private final RichContentService richContentService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userService.findByUsername(username);
            return user.getId();
        }
        throw new RuntimeException("未找到当前用户信息");
    }

    /**
     * 创建富文本内容
     */
    @PostMapping
    public Result<RichContentResponse> createContent(@Valid @RequestBody RichContentRequest request) {
        try {
            Long userId = getCurrentUserId();
            RichContentResponse response = richContentService.createContent(request, userId);
            log.info("富文本内容创建成功，内容ID：{}", response.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建富文本内容失败：{}", e.getMessage());
            return Result.error("创建内容失败：" + e.getMessage());
        }
    }

    /**
     * 更新富文本内容
     */
    @PutMapping("/{id}")
    public Result<RichContentResponse> updateContent(@PathVariable Long id,
                                                   @Valid @RequestBody RichContentRequest request) {
        try {
            Long userId = getCurrentUserId();
            RichContentResponse response = richContentService.updateContent(id, request, userId);
            log.info("富文本内容更新成功，内容ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新富文本内容失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("更新内容失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取内容详情
     */
    @GetMapping("/{id}")
    public Result<RichContentResponse> getContentById(@PathVariable Long id) {
        try {
            RichContentResponse response = richContentService.getContentById(id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取内容详情失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("获取内容详情失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取内容详情（公开接口，增加浏览次数）
     */
    @GetMapping("/public/{id}")
    public Result<RichContentResponse> getContentByIdPublic(@PathVariable Long id) {
        try {
            RichContentResponse response = richContentService.getContentByIdPublic(id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取公开内容详情失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("获取内容详情失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询内容列表
     */
    @GetMapping
    public Result<Page<RichContentResponse>> getContents(@Valid RichContentQueryRequest queryRequest) {
        try {
            Page<RichContentResponse> response = richContentService.getContents(queryRequest);
            return Result.success(response);
        } catch (Exception e) {
            log.error("分页查询内容列表失败，错误：{}", e.getMessage());
            return Result.error("查询内容列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取已发布的内容列表
     */
    @GetMapping("/published")
    public Result<List<RichContentResponse>> getPublishedContent() {
        try {
            List<RichContentResponse> response = richContentService.getPublishedContent();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取已发布内容列表失败，错误：{}", e.getMessage());
            return Result.error("获取内容列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据内容类型获取内容列表
     */
    @GetMapping("/type/{contentType}")
    public Result<List<RichContentResponse>> getContentsByType(@PathVariable String contentType) {
        try {
            List<RichContentResponse> response = richContentService.getContentsByType(contentType);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据类型获取内容列表失败，类型：{}，错误：{}", contentType, e.getMessage());
            return Result.error("获取内容列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据内容类型获取内容列表（分页）
     */
    @GetMapping("/type/{contentType}/page")
    public Result<Page<RichContentResponse>> getContentsByTypePage(
            @PathVariable String contentType,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<RichContentResponse> response = richContentService.getContentsByType(contentType, page, size);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据类型分页获取内容列表失败，类型：{}，错误：{}", contentType, e.getMessage());
            return Result.error("获取内容列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据状态获取内容列表
     */
    @GetMapping("/status/{status}")
    public Result<List<RichContentResponse>> getContentsByStatus(@PathVariable Integer status) {
        try {
            List<RichContentResponse> response = richContentService.getContentsByStatus(status);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据状态获取内容列表失败，状态：{}，错误：{}", status, e.getMessage());
            return Result.error("获取内容列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取置顶内容列表
     */
    @GetMapping("/top")
    public Result<List<RichContentResponse>> getTopContent(
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            List<RichContentResponse> response = richContentService.getTopContent(status);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取置顶内容列表失败，错误：{}", e.getMessage());
            return Result.error("获取内容列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取推荐内容列表
     */
    @GetMapping("/featured")
    public Result<List<RichContentResponse>> getFeaturedContent(
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            List<RichContentResponse> response = richContentService.getFeaturedContent(status);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取推荐内容列表失败，错误：{}", e.getMessage());
            return Result.error("获取内容列表失败：" + e.getMessage());
        }
    }

    /**
     * 搜索内容
     */
    @GetMapping("/search")
    public Result<List<RichContentResponse>> searchContent(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            List<RichContentResponse> response = richContentService.searchContent(keyword, status);
            return Result.success(response);
        } catch (Exception e) {
            log.error("搜索内容失败，关键词：{}，错误：{}", keyword, e.getMessage());
            return Result.error("搜索内容失败：" + e.getMessage());
        }
    }

    /**
     * 根据标签查找内容
     */
    @GetMapping("/tag/{tag}")
    public Result<List<RichContentResponse>> getContentByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            List<RichContentResponse> response = richContentService.getContentByTag(tag, status);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据标签查找内容失败，标签：{}，错误：{}", tag, e.getMessage());
            return Result.error("查找内容失败：" + e.getMessage());
        }
    }

    /**
     * 根据作者查找内容
     */
    @GetMapping("/author/{author}")
    public Result<List<RichContentResponse>> getContentByAuthor(
            @PathVariable String author,
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            List<RichContentResponse> response = richContentService.getContentByAuthor(author, status);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据作者查找内容失败，作者：{}，错误：{}", author, e.getMessage());
            return Result.error("查找内容失败：" + e.getMessage());
        }
    }

    /**
     * 根据关联信息查找内容
     */
    @GetMapping("/related")
    public Result<List<RichContentResponse>> getContentByRelated(
            @RequestParam Long relatedId,
            @RequestParam String relatedType) {
        try {
            List<RichContentResponse> response = richContentService.getContentByRelated(relatedId, relatedType);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据关联信息查找内容失败，关联ID：{}，关联类型：{}，错误：{}",
                    relatedId, relatedType, e.getMessage());
            return Result.error("查找内容失败：" + e.getMessage());
        }
    }

    /**
     * 发布内容
     */
    @PostMapping("/publish")
    public Result<RichContentResponse> publishContent(@Valid @RequestBody RichContentPublishRequest request) {
        try {
            Long userId = getCurrentUserId();
            RichContentResponse response = richContentService.publishContent(request, userId);
            log.info("富文本内容发布成功，内容ID：{}", request.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("发布内容失败，内容ID：{}，错误：{}", request.getId(), e.getMessage());
            return Result.error("发布内容失败：" + e.getMessage());
        }
    }

    /**
     * 下线内容
     */
    @PutMapping("/{id}/unpublish")
    public Result<RichContentResponse> unpublishContent(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            RichContentResponse response = richContentService.unpublishContent(id, userId);
            log.info("富文本内容下线成功，内容ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("下线内容失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("下线内容失败：" + e.getMessage());
        }
    }

    /**
     * 删除内容
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteContent(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            richContentService.deleteContent(id, userId);
            log.info("富文本内容删除成功，内容ID：{}", id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除内容失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("删除内容失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除内容
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteContent(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            richContentService.batchDeleteContent(ids, userId);
            log.info("批量删除富文本内容成功，删除数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("批量删除内容失败，错误：{}", e.getMessage());
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量发布内容
     */
    @PutMapping("/batch/publish")
    public Result<Void> batchPublishContent(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            richContentService.batchPublishContent(ids, userId);
            log.info("批量发布富文本内容成功，发布数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("批量发布内容失败，错误：{}", e.getMessage());
            return Result.error("批量发布失败：" + e.getMessage());
        }
    }

    /**
     * 批量下线内容
     */
    @PutMapping("/batch/unpublish")
    public Result<Void> batchUnpublishContent(@RequestBody List<Long> ids) {
        try {
            Long userId = getCurrentUserId();
            richContentService.batchUnpublishContent(ids, userId);
            log.info("批量下线富文本内容成功，下线数量：{}", ids.size());
            return Result.success();
        } catch (Exception e) {
            log.error("批量下线内容失败，错误：{}", e.getMessage());
            return Result.error("批量下线失败：" + e.getMessage());
        }
    }

    /**
     * 设置内容为置顶
     */
    @PutMapping("/{id}/top")
    public Result<RichContentResponse> setAsTop(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            RichContentResponse response = richContentService.setAsTop(id, userId);
            log.info("内容设置为置顶成功，内容ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("设置内容置顶失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("设置置顶失败：" + e.getMessage());
        }
    }

    /**
     * 取消内容置顶
     */
    @DeleteMapping("/{id}/top")
    public Result<RichContentResponse> unsetAsTop(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            RichContentResponse response = richContentService.unsetAsTop(id, userId);
            log.info("取消内容置顶成功，内容ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("取消内容置顶失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("取消置顶失败：" + e.getMessage());
        }
    }

    /**
     * 设置内容为推荐
     */
    @PutMapping("/{id}/featured")
    public Result<RichContentResponse> setAsFeatured(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            RichContentResponse response = richContentService.setAsFeatured(id, userId);
            log.info("内容设置为推荐成功，内容ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("设置内容推荐失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("设置推荐失败：" + e.getMessage());
        }
    }

    /**
     * 取消内容推荐
     */
    @DeleteMapping("/{id}/featured")
    public Result<RichContentResponse> unsetAsFeatured(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            RichContentResponse response = richContentService.unsetAsFeatured(id, userId);
            log.info("取消内容推荐成功，内容ID：{}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("取消内容推荐失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("取消推荐失败：" + e.getMessage());
        }
    }

    /**
     * 增加浏览次数
     */
    @PutMapping("/{id}/view")
    public Result<Void> incrementViewCount(@PathVariable Long id) {
        try {
            richContentService.incrementViewCount(id);
            return Result.success();
        } catch (Exception e) {
            log.error("增加浏览次数失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 增加点赞次数
     */
    @PutMapping("/{id}/like")
    public Result<Void> incrementLikeCount(@PathVariable Long id) {
        try {
            richContentService.incrementLikeCount(id);
            return Result.success();
        } catch (Exception e) {
            log.error("增加点赞次数失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 增加评论次数
     */
    @PutMapping("/{id}/comment")
    public Result<Void> incrementCommentCount(@PathVariable Long id) {
        try {
            richContentService.incrementCommentCount(id);
            return Result.success();
        } catch (Exception e) {
            log.error("增加评论次数失败，内容ID：{}，错误：{}", id, e.getMessage());
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 获取最近发布的内容
     */
    @GetMapping("/recent")
    public Result<List<RichContentResponse>> getRecentContent(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            List<RichContentResponse> response = richContentService.getRecentContent(limit, status);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取最近内容失败，错误：{}", e.getMessage());
            return Result.error("获取内容失败：" + e.getMessage());
        }
    }

    /**
     * 获取热门内容（按浏览次数排序）
     */
    @GetMapping("/popular")
    public Result<List<RichContentResponse>> getPopularContent(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            List<RichContentResponse> response = richContentService.getPopularContent(limit, status);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取热门内容失败，错误：{}", e.getMessage());
            return Result.error("获取内容失败：" + e.getMessage());
        }
    }

    /**
     * 根据发布时间范围查找内容
     */
    @GetMapping("/date-range")
    public Result<List<RichContentResponse>> getContentByDateRange(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);

            List<RichContentResponse> response = richContentService.getContentByDateRange(start, end, status);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据时间范围查找内容失败，错误：{}", e.getMessage());
            return Result.error("查找内容失败：" + e.getMessage());
        }
    }

    /**
     * 检查标题是否存在
     */
    @GetMapping("/check-title/{title}")
    public Result<Boolean> checkTitleExists(@PathVariable String title) {
        try {
            boolean exists = richContentService.existsByTitle(title);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查标题是否存在失败，标题：{}，错误：{}", title, e.getMessage());
            return Result.error("检查标题失败：" + e.getMessage());
        }
    }

    /**
     * 获取内容统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getContentStatistics() {
        try {
            Object response = richContentService.getContentStatistics();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取内容统计信息失败，错误：{}", e.getMessage());
            return Result.error("获取统计信息失败：" + e.getMessage());
        }
    }
}