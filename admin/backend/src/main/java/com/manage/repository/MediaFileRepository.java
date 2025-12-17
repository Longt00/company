package com.manage.repository;

import com.manage.entity.MediaFile;
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
 * 媒体文件数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    /**
     * 根据文件类型查找文件
     *
     * @param fileType 文件类型
     * @return 文件列表
     */
    List<MediaFile> findByFileTypeOrderByCreateTimeDesc(String fileType);

    /**
     * 根据文件类型和状态查找文件
     *
     * @param fileType 文件类型
     * @param status   状态
     * @return 文件列表
     */
    List<MediaFile> findByFileTypeAndStatusOrderByCreateTimeDesc(String fileType, Integer status);

    /**
     * 根据分类查找文件
     *
     * @param category 文件分类
     * @return 文件列表
     */
    List<MediaFile> findByCategoryOrderByCreateTimeDesc(String category);

    /**
     * 根据分类查找文件（按创建时间升序）
     *
     * @param category 文件分类
     * @return 文件列表
     */
    List<MediaFile> findByCategoryOrderByCreateTimeAsc(String category);

    /**
     * 根据分类和状态查找文件
     *
     * @param category 文件分类
     * @param status   状态
     * @return 文件列表
     */
    List<MediaFile> findByCategoryAndStatusOrderByCreateTimeDesc(String category, Integer status);

    /**
     * 根据分类和状态查找文件（按创建时间升序）
     *
     * @param category 文件分类
     * @param status   状态
     * @return 文件列表
     */
    List<MediaFile> findByCategoryAndStatusOrderByCreateTimeAsc(String category, Integer status);

    /**
     * 根据上传者查找文件
     *
     * @param uploadedBy 上传者ID
     * @return 文件列表
     */
    List<MediaFile> findByUploadedByOrderByCreateTimeDesc(Long uploadedBy);

    /**
     * 根据关联实体查找文件
     *
     * @param relatedId   关联ID
     * @param relatedType 关联类型
     * @return 文件列表
     */
    List<MediaFile> findByRelatedIdAndRelatedTypeOrderByCreateTimeDesc(Long relatedId, String relatedType);

    /**
     * 根据文件名模糊查询
     *
     * @param originalName 原始文件名
     * @return 文件列表
     */
    @Query("SELECT mf FROM MediaFile mf WHERE mf.originalName LIKE %:originalName% ORDER BY mf.createTime DESC")
    List<MediaFile> findByOriginalNameContaining(@Param("originalName") String originalName);

    /**
     * 根据标签查找文件
     *
     * @param tag 标签
     * @return 文件列表
     */
    @Query("SELECT mf FROM MediaFile mf WHERE mf.tags LIKE %:tag% ORDER BY mf.createTime DESC")
    List<MediaFile> findByTag(@Param("tag") String tag);

    /**
     * 查找公开的文件
     *
     * @return 文件列表
     */
    List<MediaFile> findByIsPublicTrueOrderByCreateTimeDesc();

    /**
     * 查找已完成的文件
     *
     * @return 文件列表
     */
    List<MediaFile> findByStatusOrderByCreateTimeDesc(Integer status);

    /**
     * 查找需要清理的文件（已过期）
     *
     * @param currentTime 当前时间
     * @return 文件列表
     */
    @Query("SELECT mf FROM MediaFile mf WHERE mf.expireTime IS NOT NULL AND mf.expireTime < :currentTime")
    List<MediaFile> findExpiredFiles(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 统计各文件类型的数量
     *
     * @return 统计结果
     */
    @Query("SELECT mf.fileType, COUNT(mf) FROM MediaFile mf GROUP BY mf.fileType")
    List<Object[]> countFilesByType();

    /**
     * 统计各状态的文件数量
     *
     * @return 统计结果
     */
    @Query("SELECT mf.status, COUNT(mf) FROM MediaFile mf GROUP BY mf.status")
    List<Object[]> countFilesByStatus();

    /**
     * 统计各分类的文件数量
     *
     * @return 统计结果
     */
    @Query("SELECT mf.category, COUNT(mf) FROM MediaFile mf GROUP BY mf.category")
    List<Object[]> countFilesByCategory();

    /**
     * 根据大小范围查找文件
     *
     * @param minSize 最小大小
     * @param maxSize 最大大小
     * @return 文件列表
     */
    @Query("SELECT mf FROM MediaFile mf WHERE mf.fileSize BETWEEN :minSize AND :maxSize ORDER BY mf.fileSize DESC")
    List<MediaFile> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);

    /**
     * 根据时间范围查找文件
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 文件列表
     */
    @Query("SELECT mf FROM MediaFile mf WHERE mf.createTime BETWEEN :startTime AND :endTime ORDER BY mf.createTime DESC")
    List<MediaFile> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询文件
     *
     * @param fileType 文件类型
     * @param status   状态
     * @param pageable 分页参数
     * @return 文件分页结果
     */
    Page<MediaFile> findByFileTypeAndStatus(String fileType, Integer status, Pageable pageable);

    /**
     * 分页查询文件（按分类）
     *
     * @param category 文件分类
     * @param status   状态
     * @param pageable 分页参数
     * @return 文件分页结果
     */
    Page<MediaFile> findByCategoryAndStatus(String category, Integer status, Pageable pageable);

    /**
     * 分页查询文件（按上传者）
     *
     * @param uploadedBy 上传者ID
     * @param status     状态
     * @param pageable   分页参数
     * @return 文件分页结果
     */
    Page<MediaFile> findByUploadedByAndStatus(Long uploadedBy, Integer status, Pageable pageable);

    /**
     * 查找最近上传的文件
     *
     * @param limit 限制数量
     * @return 文件列表
     */
    @Query(value = "SELECT * FROM media_file WHERE status = 1 ORDER BY create_time DESC LIMIT :limit", nativeQuery = true)
    List<MediaFile> findRecentFiles(@Param("limit") Integer limit);

    /**
     * 查找最大文件
     *
     * @param limit 限制数量
     * @return 文件列表
     */
    @Query(value = "SELECT * FROM media_file WHERE status = 1 ORDER BY file_size DESC LIMIT :limit", nativeQuery = true)
    List<MediaFile> findLargestFiles(@Param("limit") Integer limit);

    /**
     * 检查文件是否存在
     *
     * @param storageName 存储文件名
     * @return 是否存在
     */
    boolean existsByStorageName(String storageName);

    /**
     * 根据存储路径查找文件
     *
     * @param filePath 文件路径
     * @return 文件信息
     */
    Optional<MediaFile> findByFilePath(String filePath);

    /**
     * 根据文件URL查找文件
     *
     * @param fileUrl 文件URL
     * @return 文件信息
     */
    Optional<MediaFile> findByFileUrl(String fileUrl);

    /**
     * 根据文件URL查找未删除的文件
     *
     * @param fileUrl 文件URL
     * @return 文件信息
     */
    @Query("SELECT mf FROM MediaFile mf WHERE mf.fileUrl = :fileUrl AND mf.status != 3")
    Optional<MediaFile> findActiveFileByUrl(@Param("fileUrl") String fileUrl);

    /**
     * 更新文件状态
     *
     * @param id     文件ID
     * @param status 新状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE MediaFile mf SET mf.status = :status WHERE mf.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 软删除文件（将状态改为已删除）
     *
     * @param fileUrl 文件URL
     */
    @Modifying
    @Transactional
    @Query("UPDATE MediaFile mf SET mf.status = 3 WHERE mf.fileUrl = :fileUrl")
    int softDeleteByUrl(@Param("fileUrl") String fileUrl);

    /**
     * 更新上传进度
     *
     * @param id             文件ID
     * @param uploadProgress 上传进度
     */
    @Modifying
    @Transactional
    @Query("UPDATE MediaFile mf SET mf.uploadProgress = :uploadProgress WHERE mf.id = :id")
    void updateUploadProgress(@Param("id") Long id, @Param("uploadProgress") Integer uploadProgress);

    /**
     * 增加下载次数
     *
     * @param id 文件ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE MediaFile mf SET mf.downloadCount = mf.downloadCount + 1 WHERE mf.id = :id")
    void incrementDownloadCount(@Param("id") Long id);

    /**
     * 增加查看次数
     *
     * @param id 文件ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE MediaFile mf SET mf.viewCount = mf.viewCount + 1 WHERE mf.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 更新最后访问时间
     *
     * @param id 文件ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE MediaFile mf SET mf.lastAccessTime = CURRENT_TIMESTAMP WHERE mf.id = :id")
    void updateLastAccessTime(@Param("id") Long id);

    /**
     * 根据MIME类型查找文件
     *
     * @param mimeType MIME类型
     * @return 文件列表
     */
    List<MediaFile> findByMimeTypeOrderByCreateTimeDesc(String mimeType);

    /**
     * 根据文件URL查找所有文件（包括已删除的）
     *
     * @param fileUrl 文件URL
     * @return 文件列表
     */
    @Query("SELECT mf FROM MediaFile mf WHERE mf.fileUrl = :fileUrl")
    List<MediaFile> findAllByUrl(@Param("fileUrl") String fileUrl);

    // ============ 公开媒体查询方法 ============

    /**
     * 根据分类、状态和公开标识查找文件（降序）
     *
     * @param category 文件分类
     * @param status   文件状态
     * @param isPublic 是否公开
     * @return 文件列表
     */
    List<MediaFile> findByCategoryAndStatusAndIsPublicOrderByCreateTimeDesc(String category, Integer status, Boolean isPublic);

    /**
     * 根据分类、状态和公开标识查找文件（升序）
     *
     * @param category 文件分类
     * @param status   文件状态
     * @param isPublic 是否公开
     * @return 文件列表
     */
    List<MediaFile> findByCategoryAndStatusAndIsPublicOrderByCreateTimeAsc(String category, Integer status, Boolean isPublic);

    /**
     * 获取所有有效的文件分类（状态为1且公开）
     *
     * @return 分类统计结果
     */
    @Query("SELECT mf.category, COUNT(mf) FROM MediaFile mf WHERE mf.status = :status AND mf.isPublic = :isPublic AND mf.category IS NOT NULL GROUP BY mf.category ORDER BY mf.category")
    List<Object[]> findActiveCategories(@Param("status") Integer status, @Param("isPublic") Boolean isPublic);
}