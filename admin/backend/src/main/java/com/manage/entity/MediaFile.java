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
 * 媒体文件实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "media_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 原始文件名
     */
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    /**
     * 存储文件名
     */
    @NotBlank(message = "存储文件名不能为空")
    @Size(max = 255, message = "存储文件名长度不能超过255个字符")
    @Column(name = "storage_name", nullable = false, length = 255)
    private String storageName;

    /**
     * 文件路径
     */
    @NotBlank(message = "文件路径不能为空")
    @Size(max = 500, message = "文件路径长度不能超过500个字符")
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /**
     * 文件URL
     */
    @NotBlank(message = "文件URL不能为空")
    @Size(max = 500, message = "文件URL长度不能超过500个字符")
    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    /**
     * 文件类型（image, video, audio, document等）
     */
    @NotBlank(message = "文件类型不能为空")
    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    /**
     * MIME类型
     */
    @Size(max = 100, message = "MIME类型长度不能超过100个字符")
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /**
     * 文件分类
     */
    @Size(max = 100, message = "文件分类长度不能超过100个字符")
    @Column(name = "category", length = 100)
    private String category;

    /**
     * 文件大小（字节）
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 文件扩展名
     */
    @Size(max = 20, message = "文件扩展名长度不能超过20个字符")
    @Column(name = "file_extension", length = 20)
    private String fileExtension;

    /**
     * 文件宽度（像素）
     */
    @Column(name = "width")
    private Integer width;

    /**
     * 文件高度（像素）
     */
    @Column(name = "height")
    private Integer height;

    /**
     * 视频时长（秒）
     */
    @Column(name = "duration")
    private Long duration;

    /**
     * 缩略图URL
     */
    @Size(max = 500, message = "缩略图URL长度不能超过500个字符")
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    /**
     * 文件描述
     */
    @Size(max = 500, message = "文件描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 文件标签
     */
    @Size(max = 200, message = "文件标签长度不能超过200个字符")
    @Column(name = "tags", length = 200)
    private String tags;

    /**
     * 存储桶名称
     */
    @Size(max = 100, message = "存储桶名称长度不能超过100个字符")
    @Column(name = "bucket_name", length = 100)
    private String bucketName;

    /**
     * 存储区域
     */
    @Size(max = 100, message = "存储区域长度不能超过100个字符")
    @Column(name = "storage_region", length = 100)
    private String storageRegion;

    /**
     * CDN域名
     */
    @Size(max = 200, message = "CDN域名长度不能超过200个字符")
    @Column(name = "cdn_domain", length = 200)
    private String cdnDomain;

    /**
     * 是否公开访问
     */
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    /**
     * 是否启用CDN
     */
    @Column(name = "is_cdn_enabled", nullable = false)
    private Boolean isCdnEnabled = false;

    /**
     * 文件状态：0-上传中，1-已完成，2-上传失败，3-已删除
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    /**
     * 上传进度（0-100）
     */
    @Column(name = "upload_progress")
    private Integer uploadProgress = 0;

    /**
     * 错误信息
     */
    @Size(max = 500, message = "错误信息长度不能超过500个字符")
    @Column(name = "error_message", length = 500)
    private String errorMessage;

    /**
     * 关联实体ID
     */
    @Column(name = "related_id")
    private Long relatedId;

    /**
     * 关联实体类型
     */
    @Size(max = 100, message = "关联实体类型长度不能超过100个字符")
    @Column(name = "related_type", length = 100)
    private String relatedType;

    /**
     * 上传者ID
     */
    @Column(name = "uploaded_by")
    private Long uploadedBy;

    /**
     * 最后修改者ID
     */
    @Column(name = "modified_by")
    private Long modifiedBy;

    /**
     * 下载次数
     */
    @Column(name = "download_count")
    private Integer downloadCount = 0;

    /**
     * 查看次数
     */
    @Column(name = "view_count")
    private Integer viewCount = 0;

    /**
     * 最后访问时间
     */
    @Column(name = "last_access_time")
    private LocalDateTime lastAccessTime;

    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    /**
     * 元数据（JSON格式）
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

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