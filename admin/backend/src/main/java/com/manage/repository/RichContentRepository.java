package com.manage.repository;

import com.manage.entity.RichContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 富文本内容数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface RichContentRepository extends JpaRepository<RichContent, Long> {

    /**
     * 根据内容类型和状态查找内容
     *
     * @param contentType 内容类型
     * @param status      状态
     * @return 内容列表
     */
    List<RichContent> findByContentTypeAndStatusOrderBySortOrderAscCreateTimeDesc(
            String contentType, Integer status);

    /**
     * 根据内容类型查找内容（分页）
     *
     * @param contentType 内容类型
     * @param pageable    分页参数
     * @return 内容分页结果
     */
    Page<RichContent> findByContentTypeOrderByCreateTimeDesc(String contentType, Pageable pageable);

    /**
     * 根据状态查找内容
     *
     * @param status 状态
     * @return 内容列表
     */
    List<RichContent> findByStatusOrderByCreateTimeDesc(Integer status);

    /**
     * 查找已发布的内容
     *
     * @return 已发布内容列表
     */
    @Query("SELECT rc FROM RichContent rc WHERE rc.status = 1 ORDER BY rc.isTop DESC, rc.sortOrder ASC, rc.createTime DESC")
    List<RichContent> findPublishedContent();

    /**
     * 查找置顶的内容
     *
     * @param status 状态
     * @return 置顶内容列表
     */
    @Query("SELECT rc FROM RichContent rc WHERE rc.isTop = true AND rc.status = :status ORDER BY rc.sortOrder ASC, rc.createTime DESC")
    List<RichContent> findTopContentByStatus(@Param("status") Integer status);

    /**
     * 查找推荐的内容
     *
     * @param status 状态
     * @return 推荐内容列表
     */
    @Query("SELECT rc FROM RichContent rc WHERE rc.isFeatured = true AND rc.status = :status ORDER BY rc.sortOrder ASC, rc.createTime DESC")
    List<RichContent> findFeaturedContentByStatus(@Param("status") Integer status);

    /**
     * 根据标题模糊查询内容
     *
     * @param title  标题关键词
     * @param status 状态
     * @return 内容列表
     */
    @Query("SELECT rc FROM RichContent rc WHERE rc.title LIKE %:title% AND rc.status = :status ORDER BY rc.createTime DESC")
    List<RichContent> findByTitleContainingAndStatus(@Param("title") String title,
                                                     @Param("status") Integer status);

    /**
     * 根据内容模糊查询（支持标题和纯文本内容）
     *
     * @param keyword 关键词
     * @param status  状态
     * @return 内容列表
     */
    @Query("SELECT rc FROM RichContent rc WHERE (rc.title LIKE %:keyword% OR rc.plainText LIKE %:keyword%) AND rc.status = :status ORDER BY rc.createTime DESC")
    List<RichContent> findByKeywordAndStatus(@Param("keyword") String keyword,
                                            @Param("status") Integer status);

    /**
     * 根据标签查找内容
     *
     * @param tag    标签
     * @param status 状态
     * @return 内容列表
     */
    @Query("SELECT rc FROM RichContent rc WHERE rc.tags LIKE %:tag% AND rc.status = :status ORDER BY rc.createTime DESC")
    List<RichContent> findByTagAndStatus(@Param("tag") String tag,
                                        @Param("status") Integer status);

    /**
     * 根据作者查找内容
     *
     * @param author 作者
     * @param status 状态
     * @return 内容列表
     */
    List<RichContent> findByAuthorAndStatusOrderByCreateTimeDesc(String author, Integer status);

    /**
     * 根据关联ID和类型查找内容
     *
     * @param relatedId   关联ID
     * @param relatedType 关联类型
     * @return 内容列表
     */
    List<RichContent> findByRelatedIdAndRelatedTypeOrderByCreateTimeDesc(Long relatedId, String relatedType);

    /**
     * 根据发布时间范围查找内容
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param status    状态
     * @return 内容列表
     */
    @Query("SELECT rc FROM RichContent rc WHERE rc.publishTime BETWEEN :startTime AND :endTime AND rc.status = :status ORDER BY rc.publishTime DESC")
    List<RichContent> findByPublishTimeBetweenAndStatus(@Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime,
                                                        @Param("status") Integer status);

    /**
     * 统计各状态的内容数量
     *
     * @return 统计结果
     */
    @Query("SELECT rc.status, COUNT(rc) FROM RichContent rc GROUP BY rc.status")
    List<Object[]> countContentsByStatus();

    /**
     * 统计各内容类型的内容数量
     *
     * @return 统计结果
     */
    @Query("SELECT rc.contentType, COUNT(rc) FROM RichContent rc GROUP BY rc.contentType")
    List<Object[]> countContentsByType();

    /**
     * 查找最近发布的内容
     *
     * @param limit 限制数量
     * @param status 状态
     * @return 内容列表
     */
    @Query(value = "SELECT * FROM rich_content WHERE status = :status ORDER BY create_time DESC LIMIT :limit", nativeQuery = true)
    List<RichContent> findRecentContent(@Param("limit") Integer limit, @Param("status") Integer status);

    /**
     * 查找热门内容（按浏览次数排序）
     *
     * @param limit  限制数量
     * @param status 状态
     * @return 内容列表
     */
    @Query(value = "SELECT * FROM rich_content WHERE status = :status ORDER BY view_count DESC, create_time DESC LIMIT :limit", nativeQuery = true)
    List<RichContent> findPopularContent(@Param("limit") Integer limit, @Param("status") Integer status);

    /**
     * 增加浏览次数
     *
     * @param id 内容ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE RichContent rc SET rc.viewCount = rc.viewCount + 1 WHERE rc.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 增加点赞次数
     *
     * @param id 内容ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE RichContent rc SET rc.likeCount = rc.likeCount + 1 WHERE rc.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    /**
     * 增加评论次数
     *
     * @param id 内容ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE RichContent rc SET rc.commentCount = rc.commentCount + 1 WHERE rc.id = :id")
    void incrementCommentCount(@Param("id") Long id);

    /**
     * 根据内容类型分页查找内容
     *
     * @param contentType 内容类型
     * @param status      状态
     * @param pageable    分页参数
     * @return 内容分页结果
     */
    Page<RichContent> findByContentTypeAndStatusOrderByCreateTimeDesc(String contentType,
                                                                      Integer status,
                                                                      Pageable pageable);

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
}