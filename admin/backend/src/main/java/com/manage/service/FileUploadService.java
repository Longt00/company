package com.manage.service;

import com.manage.entity.MediaFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件上传服务接口
 *
 * @author System
 * @version 1.0
 */
public interface FileUploadService {

    /**
     * 上传文件到本地存储
     *
     * @param file     上传的文件
     * @param category 文件分类（logo、qualification、avatar、video等）
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String category);

    /**
     * 批量上传文件到本地存储
     *
     * @param files    文件列表
     * @param category 文件分类
     * @return 上传结果映射
     */
    Map<String, Object> uploadFiles(List<MultipartFile> files, String category);

    /**
     * 上传图片（增强版）
     *
     * @param file     图片文件
     * @param category 图片分类
     * @param options  上传选项（压缩、水印等）
     * @return 上传结果
     */
    Map<String, Object> uploadImage(MultipartFile file, String category, Map<String, Object> options);

    /**
     * 上传视频
     *
     * @param file     视频文件
     * @param category 视频分类
     * @param options  上传选项（转码、切片等）
     * @return 上传结果
     */
    Map<String, Object> uploadVideo(MultipartFile file, String category, Map<String, Object> options);

    /**
     * 删除本地文件
     *
     * @param fileUrl 文件URL
     * @return 删除结果
     */
    boolean deleteFile(String fileUrl);

    /**
     * 批量删除本地文件
     *
     * @param fileUrls 文件URL列表
     * @return 删除结果映射
     */
    Map<String, Object> deleteFiles(List<String> fileUrls);

    /**
     * 验证文件类型
     *
     * @param file           文件
     * @param allowedTypes   允许的文件类型
     * @return 是否通过验证
     */
    boolean validateFileType(MultipartFile file, String[] allowedTypes);

    /**
     * 验证文件大小
     *
     * @param file     文件
     * @param maxSize  最大大小（字节）
     * @return 是否通过验证
     */
    boolean validateFileSize(MultipartFile file, long maxSize);

    /**
     * 生成文件名
     *
     * @param originalFilename 原始文件名
     * @param category         文件分类
     * @return 生成的文件名
     */
    String generateFileName(String originalFilename, String category);

    /**
     * 获取文件信息
     *
     * @param fileUrl 文件URL
     * @return 文件信息
     */
    Map<String, Object> getFileInfo(String fileUrl);

    /**
     * 检查文件是否存在
     *
     * @param fileUrl 文件URL
     * @return 是否存在
     */
    boolean fileExists(String fileUrl);

    /**
     * 获取文件预签名URL（用于前端直接上传）
     *
     * @param fileName 文件名
     * @param category 文件分类
     * @param expires  过期时间（秒）
     * @return 预签名URL
     */
    String getPresignedUrl(String fileName, String category, int expires);

    /**
     * 初始化分片上传
     *
     * @param fileName 文件名
     * @param category 文件分类
     * @param fileSize 文件大小
     * @return 上传ID
     */
    String initiateMultipartUpload(String fileName, String category, long fileSize);

    /**
     * 完成分片上传
     *
     * @param uploadId 上传ID
     * @param parts    分片列表
     * @return 文件URL
     */
    String completeMultipartUpload(String uploadId, List<String> parts);

    /**
     * 复制文件
     *
     * @param sourceUrl 源文件URL
     * @param targetCategory 目标分类
     * @return 新文件URL
     */
    String copyFile(String sourceUrl, String targetCategory);

    /**
     * 移动文件
     *
     * @param sourceUrl 源文件URL
     * @param targetCategory 目标分类
     * @return 新文件URL
     */
    String moveFile(String sourceUrl, String targetCategory);

    /**
     * 根据分类查询文件列表
     *
     * @param category 文件分类
     * @return 文件列表
     */
    List<MediaFile> getFilesByCategory(String category);

    /**
     * 根据分类和状态查询文件列表
     *
     * @param category 文件分类
     * @param status   文件状态（1-已完成）
     * @return 文件列表
     */
    List<MediaFile> getFilesByCategoryAndStatus(String category, Integer status);

    /**
     * 根据URL获取原始文件名
     *
     * @param fileUrl 文件URL
     * @return 原始文件名，如果不存在则返回null
     */
    String getOriginalFileName(String fileUrl);

    // ============ 公开媒体查询方法 ============

    /**
     * 根据分类和状态查询公开文件列表
     *
     * @param category 文件分类
     * @param status   文件状态（1-已完成）
     * @return 公开文件列表
     */
    List<MediaFile> getPublicFilesByCategoryAndStatus(String category, Integer status);

    /**
     * 获取所有有效的媒体分类列表
     *
     * @return 分类列表和对应的文件数量
     */
    Map<String, Integer> getActiveCategories();

    /**
     * 获取公开文件信息
     *
     * @param fileUrl 文件URL
     * @return 文件信息
     */
    Map<String, Object> getPublicFileInfo(String fileUrl);

    /**
     * 检查公开文件是否存在
     *
     * @param fileUrl 文件URL
     * @return 是否存在
     */
    boolean checkPublicFileExists(String fileUrl);
}
