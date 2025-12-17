package com.manage.service.impl;

import com.manage.entity.MediaFile;
import com.manage.exception.BusinessException;
import com.manage.repository.MediaFileRepository;
import com.manage.service.FileUploadService;
import com.manage.util.ImageCompressionUtil;
import com.manage.util.CompressedMultipartFile;
import com.manage.util.IconGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件上传服务实现类（基于本地存储）
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.access.url}")
    private String accessUrl;

    private final MediaFileRepository mediaFileRepository;
    private final IconGenerator iconGenerator;

    /**
     * 初始化方法，在服务启动时自动生成缺失的图标文件
     */
    @PostConstruct
    public void init() {
        try {
            log.info("初始化文件上传服务，检查并生成默认图标文件");
            iconGenerator.ensureIconsExist();
            log.info("文件上传服务初始化完成");
        } catch (Exception e) {
            log.error("文件上传服务初始化失败：{}", e.getMessage(), e);
        }
    }

    // 允许的图片类型
    private static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    // 允许的视频类型
    private static final String[] ALLOWED_VIDEO_TYPES = {
            "video/mp4", "video/avi", "video/mov", "video/wmv", "video/flv", "video/webm",
            "video/mkv", "video/3gpp", "video/quicktime", "video/x-msvideo", "video/x-matroska",
            "video/ogg", "application/octet-stream"
    };

    // 文件大小限制（100MB）
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    /**
     * 上传文件到本地存储
     *
     * @param file     上传的文件
     * @param category 文件分类
     * @return 文件访问URL
     */
    @Override
    public String uploadFile(MultipartFile file, String category) {
        try {
            // 文件验证
            if (!validateFile(file, category)) {
                throw new BusinessException("文件验证失败");
            }

            // 生成文件名
            String fileName = generateFileName(file.getOriginalFilename(), category);
            log.info("开始上传文件：category={}, fileName={}, uploadPath={}", category, fileName, uploadPath);

            // 创建目录
            Path targetPath = Paths.get(uploadPath, fileName);
            Path parentDir = targetPath.getParent();
            log.info("目标文件路径：{}, 父目录：{}", targetPath, parentDir);

            Files.createDirectories(parentDir);
            log.info("目录创建成功：{}", parentDir);

            // 保存文件到本地
            long fileSize = file.getSize();
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 验证文件是否真的保存成功
            if (Files.exists(targetPath) && Files.size(targetPath) == fileSize) {
                log.info("文件上传成功：{}, 实际大小：{}", fileName, Files.size(targetPath));
            } else {
                log.error("文件保存失败：{}, 文件不存在或大小不匹配", fileName);
                throw new BusinessException("文件保存失败");
            }

            // 返回文件URL
            return getFileUrl(fileName);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传出现未知错误：{}", e.getMessage(), e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除本地文件
     *
     * @param fileUrl 文件URL
     * @return 删除结果
     */
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            log.info("开始删除文件：{}", fileUrl);

            // 从URL中提取文件名
            String fileName = extractFileNameFromUrl(fileUrl);
            if (fileName == null) {
                log.error("无法从URL中提取文件名：{}", fileUrl);
                return false;
            }

            log.info("提取的文件名：{}", fileName);

            // 构建本地文件路径，使用标准的路径分隔符
            Path filePath = Paths.get(uploadPath.replace("/", File.separator), fileName.replace("/", File.separator));
            File file = filePath.toFile();

            log.info("尝试删除物理文件：{}", filePath.toString());

            boolean fileDeleted = false;
            if (file.exists()) {
                // 尝试删除文件，如果失败则尝试多次
                int maxAttempts = 3;
                for (int i = 0; i < maxAttempts; i++) {
                    fileDeleted = file.delete();
                    if (fileDeleted) {
                        log.info("物理文件删除成功：{}", fileName);
                        break;
                    } else {
                        log.warn("物理文件删除失败（尝试 {}/{}）：{}", i + 1, maxAttempts, fileName);
                        if (i < maxAttempts - 1) {
                            // 等待一段时间后重试
                            Thread.sleep(100);
                            // 强制垃圾回收，可能有助于释放文件句柄
                            System.gc();
                        }
                    }
                }

                if (!fileDeleted) {
                    log.error("物理文件删除失败，已尝试{}次：{}", maxAttempts, fileName);
                    // 尝试删除时退出程序（如果是Windows系统）
                    if (file.exists() && !file.delete()) {
                        // 最后的尝试：在JVM退出时删除
                        file.deleteOnExit();
                        log.warn("标记文件在JVM退出时删除：{}", fileName);
                        fileDeleted = true; // 软删除成功
                    }
                }
            } else {
                log.warn("物理文件不存在：{}", fileName);
                // 即使物理文件不存在，也继续更新数据库状态
                fileDeleted = true;
            }

            // 更新数据库状态为已删除（status = 3）
            if (fileDeleted) {
                try {
                    log.info("开始更新数据库记录状态：{}", fileUrl);

                    // 首先尝试软删除
                    int updatedRows = mediaFileRepository.softDeleteByUrl(fileUrl);
                    if (updatedRows > 0) {
                        // 软删除成功，需要删除关联的缩略图
                        List<MediaFile> files = mediaFileRepository.findAllByUrl(fileUrl);
                        for (MediaFile mediaFile : files) {
                            if (mediaFile.getThumbnailUrl() != null && !mediaFile.getThumbnailUrl().trim().isEmpty()) {
                                deleteThumbnailFile(mediaFile.getThumbnailUrl());
                            }
                        }
                        log.info("数据库记录状态更新成功：{}, 更新行数：{}", fileUrl, updatedRows);
                        return true;
                    } else {
                        log.warn("软删除未找到记录，尝试查找并删除所有匹配的记录：{}", fileUrl);

                        // 查找所有匹配的记录
                        List<MediaFile> files = mediaFileRepository.findAllByUrl(fileUrl);
                        if (files.isEmpty()) {
                            log.warn("数据库中未找到匹配的文件记录：{}", fileUrl);
                            // 即使没有数据库记录，如果物理文件已删除，也算成功
                            return true;
                        }

                        boolean allUpdated = true;
                        for (MediaFile mediaFile : files) {
                            try {
                                log.info("更新数据库记录状态：ID={}, 当前状态={}", mediaFile.getId(), mediaFile.getStatus());

                                // 删除缩略图文件
                                if (mediaFile.getThumbnailUrl() != null && !mediaFile.getThumbnailUrl().trim().isEmpty()) {
                                    deleteThumbnailFile(mediaFile.getThumbnailUrl());
                                }

                                mediaFile.setStatus(3); // 已删除
                                mediaFileRepository.save(mediaFile);
                                log.info("数据库记录状态更新成功：ID={}, URL={}", mediaFile.getId(), fileUrl);
                            } catch (Exception saveException) {
                                log.error("更新单个数据库记录失败：ID={}, 错误：{}", mediaFile.getId(), saveException.getMessage(), saveException);
                                allUpdated = false;
                            }
                        }

                        if (allUpdated) {
                            log.info("所有数据库记录状态更新成功：{}", fileUrl);
                            return true;
                        } else {
                            log.warn("部分数据库记录更新失败，但物理文件已删除：{}", fileUrl);
                            return true; // 物理文件已删除，返回成功
                        }
                    }
                } catch (Exception e) {
                    log.error("更新数据库状态失败：{}", e.getMessage(), e);
                    // 数据库更新失败，但物理文件已删除，返回true
                    log.warn("数据库更新失败，但物理文件已删除，返回成功：{}", fileUrl);
                    return true;
                }
            } else {
                log.error("物理文件删除失败，跳过数据库操作");
                return false;
            }
        } catch (Exception e) {
            log.error("文件删除过程中发生异常：{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 验证文件类型
     *
     * @param file           文件
     * @param allowedTypes   允许的文件类型
     * @return 是否通过验证
     */
    @Override
    public boolean validateFileType(MultipartFile file, String[] allowedTypes) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        for (String allowedType : allowedTypes) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 验证文件扩展名
     *
     * @param file              文件
     * @param allowedExtensions 允许的文件扩展名
     * @return 是否通过验证
     */
    private boolean validateFileExtension(MultipartFile file, String[] allowedExtensions) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            return false;
        }

        String originalFilename = file.getOriginalFilename().toLowerCase();

        for (String allowedExtension : allowedExtensions) {
            if (originalFilename.endsWith(allowedExtension.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 验证文件大小
     *
     * @param file     文件
     * @param maxSize  最大大小（字节）
     * @return 是否通过验证
     */
    @Override
    public boolean validateFileSize(MultipartFile file, long maxSize) {
        return file != null && !file.isEmpty() && file.getSize() <= maxSize;
    }

    /**
     * 生成文件名
     *
     * @param originalFilename 原始文件名
     * @param category         文件分类
     * @return 生成的文件名
     */
    @Override
    public String generateFileName(String originalFilename, String category) {
        // 获取文件扩展名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成日期路径
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String datePath = sdf.format(new Date());

        // 生成唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 组合文件名
        return String.format("%s/%s/%s%s", category, datePath, uuid, extension);
    }

    /**
     * 文件验证
     *
     * @param file     文件
     * @param category 文件分类
     * @return 是否通过验证
     */
    private boolean validateFile(MultipartFile file, String category) {
        // 基本验证
        if (file == null || file.isEmpty()) {
            log.error("文件为空");
            return false;
        }

        // 大小验证
        if (!validateFileSize(file, MAX_FILE_SIZE)) {
            log.error("文件大小超过限制：{} bytes", file.getSize());
            return false;
        }

        // 类型验证 - MIME类型和文件扩展名双重验证
        String[] allowedTypes;
        String[] allowedExtensions;
        switch (category.toLowerCase()) {
            case "logo":
            case "avatar":
            case "qualification":
                allowedTypes = ALLOWED_IMAGE_TYPES;
                allowedExtensions = new String[]{".jpg", ".jpeg", ".png", ".gif", ".webp"};
                break;
            case "video":
                allowedTypes = ALLOWED_VIDEO_TYPES;
                allowedExtensions = new String[]{".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".mkv", ".3gp", ".qt"};
                break;
            default:
                allowedTypes = ALLOWED_IMAGE_TYPES; // 默认只允许图片
                allowedExtensions = new String[]{".jpg", ".jpeg", ".png", ".gif", ".webp"};
                break;
        }

        // MIME类型验证
        if (!validateFileType(file, allowedTypes)) {
            log.error("不支持的文件类型：{}", file.getContentType());
            // 如果MIME类型验证失败，尝试通过文件扩展名验证
            if (!validateFileExtension(file, allowedExtensions)) {
                log.error("不支持的文件扩展名：{}", file.getOriginalFilename());
                return false;
            } else {
                log.warn("MIME类型验证失败，但文件扩展名验证通过，继续上传：{}", file.getOriginalFilename());
            }
        }

        return true;
    }

    /**
     * 获取文件访问URL
     *
     * @param fileName 文件名
     * @return 文件URL
     */
    private String getFileUrl(String fileName) {
        return accessUrl + "/" + fileName;
    }

    /**
     * 从URL中提取文件名
     *
     * @param fileUrl 文件URL
     * @return 文件名
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            log.warn("文件URL为空");
            return null;
        }

        try {
            log.debug("开始从URL提取文件名：{}", fileUrl);

            // 标准化URL，移除可能的查询参数和锚点
            String normalizedUrl = fileUrl;
            int queryIndex = normalizedUrl.indexOf('?');
            if (queryIndex != -1) {
                normalizedUrl = normalizedUrl.substring(0, queryIndex);
            }
            int anchorIndex = normalizedUrl.indexOf('#');
            if (anchorIndex != -1) {
                normalizedUrl = normalizedUrl.substring(0, anchorIndex);
            }

            // 移除域名部分，只保留路径
            if (normalizedUrl.startsWith(accessUrl + "/")) {
                String fileName = normalizedUrl.substring(accessUrl.length() + 1);
                log.debug("通过accessUrl提取文件名：{}", fileName);
                return fileName;
            }

            // 如果URL不匹配，尝试从完整URL中提取路径
            int protocolIndex = normalizedUrl.indexOf("://");
            if (protocolIndex != -1) {
                String withoutProtocol = normalizedUrl.substring(protocolIndex + 3);
                int firstSlashIndex = withoutProtocol.indexOf("/");
                if (firstSlashIndex != -1) {
                    String fileName = withoutProtocol.substring(firstSlashIndex + 1);
                    log.debug("通过协议解析提取文件名：{}", fileName);
                    return fileName;
                }
            }

            // 如果URL中没有协议，可能是相对路径
            if (normalizedUrl.startsWith("/")) {
                String fileName = normalizedUrl.substring(1);
                log.debug("从绝对路径提取文件名：{}", fileName);
                return fileName;
            }

            // 直接返回URL作为文件名
            log.debug("直接使用URL作为文件名：{}", normalizedUrl);
            return normalizedUrl;
        } catch (Exception e) {
            log.error("提取文件名失败：URL={}, 错误={}", fileUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> uploadFiles(List<MultipartFile> files, String category) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> successResults = new ArrayList<>();
        List<Map<String, Object>> errorResults = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String fileUrl = uploadFile(file, category);
                Map<String, Object> success = new HashMap<>();
                success.put("fileName", file.getOriginalFilename());
                success.put("url", fileUrl);
                success.put("fileSize", file.getSize());
                successResults.add(success);
            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("fileName", file.getOriginalFilename());
                error.put("error", e.getMessage());
                errorResults.add(error);
            }
        }

        result.put("success", successResults);
        result.put("errors", errorResults);
        result.put("totalCount", files.size());
        result.put("successCount", successResults.size());
        result.put("errorCount", errorResults.size());

        return result;
    }

    @Override
    public Map<String, Object> uploadImage(MultipartFile file, String category, Map<String, Object> options) {
        try {
            // 验证图片文件格式
            if (!ImageCompressionUtil.isSupportedFormat(file)) {
                throw new BusinessException("不支持的图片格式，支持格式：JPEG、PNG、GIF、WEBP");
            }

            // 检查图片大小限制
            Long maxSize = options != null && options.containsKey("maxSize") ?
                    (Long) options.get("maxSize") : 20 * 1024 * 1024L; // 默认20MB
            if (!validateFileSize(file, maxSize)) {
                throw new BusinessException("图片大小超过限制");
            }

            // 获取图片信息
            ImageCompressionUtil.ImageInfo imageInfo = ImageCompressionUtil.getImageInfo(file);
            log.info("原始图片信息：{}x{}, {:.2f}MB, 格式：{}",
                imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getSizeMB(), imageInfo.getFormat());

            // 图片压缩处理
            MultipartFile processedFile = file;
            ImageCompressionUtil.ImageCompressionResult compressionResult = null;
            boolean generateThumbnail = options != null && options.containsKey("thumbnail") ?
                    (Boolean) options.get("thumbnail") : true;

            try {
                // 智能压缩图片
                compressionResult = ImageCompressionUtil.smartCompress(file);
                if (compressionResult.isCompressed()) {
                    processedFile = CompressedMultipartFile.fromOriginal(file, compressionResult.getFinalData());
                    log.info("图片压缩成功：{}, 压缩率：{}%, 耗时：{}ms",
                        file.getOriginalFilename(),
                        String.format("%.1f", compressionResult.getCompressionRatio() * 100),
                        compressionResult.getCompressionTime());
                } else {
                    log.info("图片无需压缩，使用原始文件：{}", file.getOriginalFilename());
                }
            } catch (Exception compressionError) {
                log.warn("图片压缩失败，使用原始文件：{}", compressionError.getMessage());
                compressionResult = null;
            }

            // 生成文件名并上传
            String fileName = generateFileName(processedFile.getOriginalFilename(), category);
            String fileUrl = uploadToLocalStorage(processedFile, fileName);

            // 创建媒体文件记录
            MediaFile mediaFile = createMediaFileRecord(processedFile, fileName, fileUrl, category, "image");

            // 设置图片尺寸信息
            mediaFile.setWidth(imageInfo.getWidth());
            mediaFile.setHeight(imageInfo.getHeight());

            // 处理图片选项（尺寸、描述等）
            if (options != null) {
                processImageOptions(mediaFile, options);
            }

            // 生成缩略图
            if (generateThumbnail) {
                try {
                    ImageCompressionUtil.ImageCompressionResult thumbnailResult =
                            ImageCompressionUtil.generateThumbnail(file);
                    if (thumbnailResult.isCompressed()) {
                        String thumbnailFileName = generateFileName(
                                CompressedMultipartFile.createThumbnail(file, thumbnailResult.getFinalData()).getOriginalFilename(),
                                category + "/thumbnails");
                        String thumbnailUrl = uploadToLocalStorage(
                                CompressedMultipartFile.createThumbnail(file, thumbnailResult.getFinalData()),
                                thumbnailFileName);
                        mediaFile.setThumbnailUrl(thumbnailUrl);
                        log.info("缩略图生成成功：{}", thumbnailUrl);
                    }
                } catch (Exception thumbnailError) {
                    log.warn("缩略图生成失败：{}", thumbnailError.getMessage());
                }
            }

            // 保存媒体文件记录
            mediaFileRepository.save(mediaFile);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("id", mediaFile.getId());
            result.put("fileName", processedFile.getOriginalFilename());
            result.put("originalFileName", file.getOriginalFilename());
            result.put("url", fileUrl);
            result.put("fileSize", processedFile.getSize());
            result.put("originalFileSize", file.getSize());
            result.put("fileType", "image");
            result.put("category", category);
            result.put("width", imageInfo.getWidth());
            result.put("height", imageInfo.getHeight());
            result.put("uploadTime", mediaFile.getCreateTime());

            // 添加压缩信息
            if (compressionResult != null && compressionResult.isCompressed()) {
                result.put("compressed", true);
                result.put("compressionRatio", String.format("%.1f", compressionResult.getCompressionRatio() * 100) + "%");
                result.put("compressionTime", compressionResult.getCompressionTime() + "ms");
            }

            // 添加缩略图信息
            if (mediaFile.getThumbnailUrl() != null) {
                result.put("thumbnailUrl", mediaFile.getThumbnailUrl());
            }

            log.info("增强图片上传成功：{}, 压缩状态：{}", fileName,
                compressionResult != null && compressionResult.isCompressed() ? "已压缩" : "无需压缩");
            return result;

        } catch (Exception e) {
            log.error("增强图片上传失败：{}", e.getMessage(), e);
            throw new BusinessException("图片上传失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> uploadVideo(MultipartFile file, String category, Map<String, Object> options) {
        try {
            log.info("开始上传视频文件：{}, 大小：{} bytes", file.getOriginalFilename(), file.getSize());

            // 基本验证
            if (file == null || file.isEmpty()) {
                throw new BusinessException("视频文件不能为空");
            }

            // 检查视频大小限制
            Long maxSize = options != null && options.containsKey("maxSize") ?
                    (Long) options.get("maxSize") : 100 * 1024 * 1024L; // 默认100MB

            if (!validateFileSize(file, maxSize)) {
                throw new BusinessException(String.format("视频大小超过限制，最大允许：%d MB，当前大小：%.2f MB",
                    maxSize / (1024 * 1024), file.getSize() / (1024.0 * 1024.0)));
            }

            // 验证视频格式（MIME类型和扩展名双重验证）
            if (!validateFileType(file, ALLOWED_VIDEO_TYPES)) {
                // MIME类型验证失败，检查扩展名
                String[] videoExtensions = {".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".mkv", ".3gp", ".qt", ".ogg"};
                if (!validateFileExtension(file, videoExtensions)) {
                    throw new BusinessException("不支持的视频格式，支持格式：MP4, AVI, MOV, WMV, FLV, WEBM, MKV, 3GP, OGG");
                } else {
                    log.warn("MIME类型验证失败但扩展名验证通过，继续上传：{}", file.getOriginalFilename());
                }
            }

            // 生成文件名
            String fileName = generateFileName(file.getOriginalFilename(), category);
            log.info("生成文件名：{}", fileName);

            // 上传到本地存储
            String fileUrl = uploadToLocalStorage(file, fileName);

            // 创建媒体文件记录
            MediaFile mediaFile = createMediaFileRecord(file, fileName, fileUrl, category, "video");

            // 处理视频选项
            if (options != null) {
                processVideoOptions(mediaFile, options);
            }

            // 保存媒体文件记录
            mediaFileRepository.save(mediaFile);
            log.info("媒体文件记录已保存，ID：{}", mediaFile.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("id", mediaFile.getId());
            result.put("fileName", file.getOriginalFilename());
            result.put("url", fileUrl);
            result.put("fileSize", file.getSize());
            result.put("fileSizeMB", String.format("%.2f", file.getSize() / (1024.0 * 1024.0)));
            result.put("fileType", "video");
            result.put("uploadTime", mediaFile.getCreateTime());
            result.put("category", category);

            log.info("视频上传成功：{}", fileName);
            return result;

        } catch (BusinessException e) {
            log.error("视频上传失败 - 业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("视频上传失败 - 系统异常：{}", e.getMessage(), e);
            throw new BusinessException("视频上传失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> deleteFiles(List<String> fileUrls) {
        Map<String, Object> result = new HashMap<>();
        List<String> successUrls = new ArrayList<>();
        List<Map<String, String>> errorUrls = new ArrayList<>();

        for (String fileUrl : fileUrls) {
            try {
                boolean success = deleteFile(fileUrl);
                if (success) {
                    successUrls.add(fileUrl);
                    log.info("批量删除成功：{}", fileUrl);
                } else {
                    Map<String, String> error = new HashMap<>();
                    error.put("url", fileUrl);
                    error.put("error", "删除失败");
                    errorUrls.add(error);
                    log.warn("批量删除失败：{}", fileUrl);
                }
            } catch (Exception e) {
                Map<String, String> error = new HashMap<>();
                error.put("url", fileUrl);
                error.put("error", e.getMessage());
                errorUrls.add(error);
                log.error("批量删除异常：{}, 错误：{}", fileUrl, e.getMessage());
            }
        }

        result.put("successUrls", successUrls);
        result.put("errors", errorUrls);
        result.put("totalCount", fileUrls.size());
        result.put("successCount", successUrls.size());
        result.put("errorCount", errorUrls.size());

        log.info("批量删除完成，总数：{}, 成功：{}, 失败：{}",
                fileUrls.size(), successUrls.size(), errorUrls.size());

        return result;
    }

    @Override
    public Map<String, Object> getFileInfo(String fileUrl) {
        try {
            Optional<MediaFile> mediaFile = mediaFileRepository.findActiveFileByUrl(fileUrl);
            if (!mediaFile.isPresent()) {
                throw new BusinessException("文件不存在或已被删除");
            }

            MediaFile file = mediaFile.get();
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", file.getId());
            fileInfo.put("originalName", file.getOriginalName());
            fileInfo.put("fileUrl", file.getFileUrl());
            fileInfo.put("fileType", file.getFileType());
            fileInfo.put("mimeType", file.getMimeType());
            fileInfo.put("fileSize", file.getFileSize());
            fileInfo.put("category", file.getCategory());
            fileInfo.put("uploadTime", file.getCreateTime());
            fileInfo.put("downloadCount", file.getDownloadCount());
            fileInfo.put("viewCount", file.getViewCount());

            if ("image".equals(file.getFileType())) {
                fileInfo.put("width", file.getWidth());
                fileInfo.put("height", file.getHeight());
                fileInfo.put("thumbnailUrl", file.getThumbnailUrl());
            } else if ("video".equals(file.getFileType())) {
                fileInfo.put("duration", file.getDuration());
                fileInfo.put("thumbnailUrl", file.getThumbnailUrl());
            }

            return fileInfo;

        } catch (Exception e) {
            log.error("获取文件信息失败：{}", e.getMessage());
            throw new BusinessException("获取文件信息失败：" + e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            if (fileName == null) {
                return false;
            }

            Path filePath = Paths.get(uploadPath, fileName);
            return Files.exists(filePath) && Files.isRegularFile(filePath);
        } catch (Exception e) {
            log.error("检查文件是否存在失败：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getPresignedUrl(String fileName, String category, int expires) {
        try {
            String fullFileName = category + "/" + fileName;
            // 本地存储不支持预签名URL，直接返回文件访问URL
            return getFileUrl(fullFileName);
        } catch (Exception e) {
            log.error("生成文件URL失败：{}", e.getMessage());
            throw new BusinessException("生成文件URL失败：" + e.getMessage());
        }
    }

    @Override
    public String initiateMultipartUpload(String fileName, String category, long fileSize) {
        try {
            String fullFileName = category + "/" + fileName;
            // 本地存储不支持分片上传，直接生成唯一ID
            return "local-upload-" + UUID.randomUUID().toString();
        } catch (Exception e) {
            log.error("初始化文件上传失败：{}", e.getMessage());
            throw new BusinessException("初始化文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public String completeMultipartUpload(String uploadId, List<String> parts) {
        try {
            // 本地存储不支持分片上传，直接返回空或抛出异常
            throw new BusinessException("本地存储不支持分片上传功能");
        } catch (Exception e) {
            log.error("完成文件上传失败：{}", e.getMessage());
            throw new BusinessException("本地存储不支持分片上传功能");
        }
    }

    @Override
    public String copyFile(String sourceUrl, String targetCategory) {
        try {
            String sourceFileName = extractFileNameFromUrl(sourceUrl);
            if (sourceFileName == null) {
                throw new BusinessException("源文件路径无效");
            }

            Path sourcePath = Paths.get(uploadPath, sourceFileName);
            if (!Files.exists(sourcePath)) {
                throw new BusinessException("源文件不存在");
            }

            // 生成目标文件名
            String targetFileName = generateFileName(sourceFileName, targetCategory);
            Path targetPath = Paths.get(uploadPath, targetFileName);

            // 创建目标目录
            Files.createDirectories(targetPath.getParent());

            // 复制文件
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("文件复制成功：{} -> {}", sourceFileName, targetFileName);
            return getFileUrl(targetFileName);
        } catch (Exception e) {
            log.error("文件复制失败：{}", e.getMessage());
            throw new BusinessException("文件复制失败：" + e.getMessage());
        }
    }

    @Override
    public String moveFile(String sourceUrl, String targetCategory) {
        String newFileUrl = copyFile(sourceUrl, targetCategory);
        deleteFile(sourceUrl);
        return newFileUrl;
    }

    /**
     * 上传文件到本地存储（通用方法）
     */
    private String uploadToLocalStorage(MultipartFile file, String fileName) throws IOException {
        // 创建目录
        Path targetPath = Paths.get(uploadPath, fileName);
        Files.createDirectories(targetPath.getParent());

        // 保存文件到本地
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return getFileUrl(fileName);
    }

    /**
     * 创建媒体文件记录
     */
    private MediaFile createMediaFileRecord(MultipartFile file, String fileName, String fileUrl,
                                            String category, String fileType) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setOriginalName(file.getOriginalFilename());
        mediaFile.setStorageName(fileName);
        mediaFile.setFilePath(fileName);
        mediaFile.setFileUrl(fileUrl);
        mediaFile.setFileType(fileType);
        mediaFile.setMimeType(file.getContentType());
        mediaFile.setCategory(category);
        mediaFile.setFileSize(file.getSize());

        // 提取文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            mediaFile.setFileExtension(originalFilename.substring(originalFilename.lastIndexOf(".")));
        }

        mediaFile.setBucketName("local-storage");
        mediaFile.setStorageRegion("local");
        mediaFile.setIsPublic(true);
        mediaFile.setStatus(1); // 已完成
        mediaFile.setUploadProgress(100);

        return mediaFile;
    }

    /**
     * 处理图片选项
     */
    private void processImageOptions(MediaFile mediaFile, Map<String, Object> options) {
        // 设置图片尺寸（如果有提供）
        if (options.containsKey("width")) {
            mediaFile.setWidth((Integer) options.get("width"));
        }
        if (options.containsKey("height")) {
            mediaFile.setHeight((Integer) options.get("height"));
        }

        // 设置缩略图（如果有提供）
        if (options.containsKey("thumbnailUrl")) {
            mediaFile.setThumbnailUrl((String) options.get("thumbnailUrl"));
        }

        // 设置描述
        if (options.containsKey("description")) {
            mediaFile.setDescription((String) options.get("description"));
        }

        // 设置标签
        if (options.containsKey("tags")) {
            mediaFile.setTags((String) options.get("tags"));
        }

        // 设置关联信息
        if (options.containsKey("relatedId")) {
            mediaFile.setRelatedId(((Number) options.get("relatedId")).longValue());
        }
        if (options.containsKey("relatedType")) {
            mediaFile.setRelatedType((String) options.get("relatedType"));
        }

        // 设置上传者
        if (options.containsKey("uploadedBy")) {
            mediaFile.setUploadedBy(((Number) options.get("uploadedBy")).longValue());
        }
    }

    /**
     * 处理视频选项
     */
    private void processVideoOptions(MediaFile mediaFile, Map<String, Object> options) {
        // 设置视频时长（如果有提供）
        if (options.containsKey("duration")) {
            mediaFile.setDuration(((Number) options.get("duration")).longValue());
        }

        // 设置缩略图（如果有提供）
        if (options.containsKey("thumbnailUrl")) {
            mediaFile.setThumbnailUrl((String) options.get("thumbnailUrl"));
        }

        // 设置描述
        if (options.containsKey("description")) {
            mediaFile.setDescription((String) options.get("description"));
        }

        // 设置标签
        if (options.containsKey("tags")) {
            mediaFile.setTags((String) options.get("tags"));
        }

        // 设置关联信息
        if (options.containsKey("relatedId")) {
            mediaFile.setRelatedId(((Number) options.get("relatedId")).longValue());
        }
        if (options.containsKey("relatedType")) {
            mediaFile.setRelatedType((String) options.get("relatedType"));
        }

        // 设置上传者
        if (options.containsKey("uploadedBy")) {
            mediaFile.setUploadedBy(((Number) options.get("uploadedBy")).longValue());
        }
    }

    
    /**
     * 根据分类查询文件列表
     */
    @Override
    public List<MediaFile> getFilesByCategory(String category) {
        return mediaFileRepository.findByCategoryOrderByCreateTimeAsc(category);
    }

    /**
     * 根据分类和状态查询文件列表
     */
    @Override
    public List<MediaFile> getFilesByCategoryAndStatus(String category, Integer status) {
        return mediaFileRepository.findByCategoryAndStatusOrderByCreateTimeAsc(category, status);
    }

    /**
     * 根据URL获取原始文件名
     */
    @Override
    public String getOriginalFileName(String fileUrl) {
        try {
            // 从数据库查找文件记录
            List<MediaFile> files = mediaFileRepository.findAllByUrl(fileUrl);
            if (files != null && !files.isEmpty()) {
                MediaFile file = files.get(0);
                // 返回原始文件名
                return file.getOriginalName();
            }
            log.warn("数据库中未找到文件记录，URL: {}", fileUrl);
            return null;
        } catch (Exception e) {
            log.error("获取原始文件名失败，URL: {}, 错误: {}", fileUrl, e.getMessage());
            return null;
        }
    }

    // ============ 公开媒体查询方法实现 ============

    /**
     * 根据分类和状态查询公开文件列表
     */
    @Override
    public List<MediaFile> getPublicFilesByCategoryAndStatus(String category, Integer status) {
        try {
            // 使用 Asc（升序）确保按上传时间从早到晚显示，保持上传顺序
            return mediaFileRepository.findByCategoryAndStatusAndIsPublicOrderByCreateTimeAsc(category, status, true);
        } catch (Exception e) {
            log.error("查询公开文件列表失败，分类：{}，状态：{}，错误：{}", category, status, e.getMessage());
            throw new RuntimeException("查询公开文件列表失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Integer> getActiveCategories() {
        try {
            List<Object[]> results = mediaFileRepository.findActiveCategories(1, true); // 状态为1且公开
            Map<String, Integer> categories = new HashMap<>();

            for (Object[] result : results) {
                String category = (String) result[0];
                Long count = (Long) result[1];
                if (StringUtils.hasText(category)) {
                    categories.put(category.trim(), count.intValue());
                }
            }

            log.info("获取有效媒体分类列表成功，分类数量：{}", categories.size());
            return categories;
        } catch (Exception e) {
            log.error("获取有效媒体分类列表失败：{}", e.getMessage());
            throw new RuntimeException("获取分类列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取公开文件信息
     */
    @Override
    public Map<String, Object> getPublicFileInfo(String fileUrl) {
        try {
            Map<String, Object> fileInfo = new HashMap<>();
            
            // 从数据库查找文件记录
            List<MediaFile> files = mediaFileRepository.findAllByUrl(fileUrl);
            if (files != null && !files.isEmpty()) {
                MediaFile file = files.get(0);
                
                // 验证文件是否为公开且已完成状态
                if (file.getIsPublic() == null || !file.getIsPublic()) {
                    throw new RuntimeException("文件未公开访问");
                }
                
                if (file.getStatus() == null || file.getStatus() != 1) {
                    throw new RuntimeException("文件未完成上传");
                }
                
                // 构建文件信息
                fileInfo.put("fileName", file.getOriginalName());
                fileInfo.put("fileUrl", file.getFileUrl());
                fileInfo.put("fileSize", file.getFileSize());
                fileInfo.put("contentType", file.getMimeType());
                fileInfo.put("uploadTime", file.getCreateTime());
                fileInfo.put("category", file.getCategory());
                fileInfo.put("exists", true);
                fileInfo.put("isPublic", file.getIsPublic());
                
                // 添加媒体文件特有信息
                if ("image".equals(file.getFileType())) {
                    fileInfo.put("width", file.getWidth());
                    fileInfo.put("height", file.getHeight());
                } else if ("video".equals(file.getFileType())) {
                    fileInfo.put("duration", file.getDuration());
                    fileInfo.put("thumbnailUrl", file.getThumbnailUrl());
                }
                
                log.debug("获取公开文件信息成功，文件名：{}", file.getOriginalName());
                
            } else {
                // 数据库中不存在，但检查文件系统是否存在
                fileInfo.put("fileName", getFileNameFromUrl(fileUrl));
                fileInfo.put("fileUrl", fileUrl);
                fileInfo.put("fileSize", null);
                fileInfo.put("contentType", guessContentTypeFromUrl(fileUrl));
                fileInfo.put("uploadTime", null);
                fileInfo.put("category", null);
                fileInfo.put("exists", false);
                fileInfo.put("isPublic", false);
                
                log.warn("数据库中未找到文件记录，URL：{}", fileUrl);
            }
            
            return fileInfo;
            
        } catch (Exception e) {
            log.error("获取公开文件信息失败，URL：{}，错误：{}", fileUrl, e.getMessage());
            throw new RuntimeException("获取文件信息失败：" + e.getMessage());
        }
    }

    /**
     * 检查公开文件是否存在
     */
    @Override
    public boolean checkPublicFileExists(String fileUrl) {
        try {
            if (!StringUtils.hasText(fileUrl)) {
                return false;
            }

            // 先检查数据库中的文件记录
            List<MediaFile> files = mediaFileRepository.findAllByUrl(fileUrl);
            if (files != null && !files.isEmpty()) {
                MediaFile file = files.get(0);
                // 验证文件是否为公开且已完成状态
                return file.getIsPublic() != null && 
                       file.getIsPublic() && 
                       file.getStatus() != null && 
                       file.getStatus() == 1;
            }

            // 数据库中不存在，检查文件系统
            return fileExists(fileUrl);
            
        } catch (Exception e) {
            log.error("检查公开文件是否存在失败，URL：{}，错误：{}", fileUrl, e.getMessage());
            return false;
        }
    }

    /**
     * 从URL提取文件名
     */
    private String getFileNameFromUrl(String fileUrl) {
        try {
            if (fileUrl == null) {
                return "unknown";
            }
            
            int lastSlashIndex = fileUrl.lastIndexOf('/');
            if (lastSlashIndex >= 0 && lastSlashIndex < fileUrl.length() - 1) {
                return fileUrl.substring(lastSlashIndex + 1);
            }
            
            return fileUrl;
        } catch (Exception e) {
            log.warn("从URL提取文件名失败，URL：{}，错误：{}", fileUrl, e.getMessage());
            return "unknown";
        }
    }

    /**
     * 根据URL推测内容类型
     */
    private String guessContentTypeFromUrl(String fileUrl) {
        try {
            if (fileUrl == null) {
                return "application/octet-stream";
            }
            
            String lowerCaseUrl = fileUrl.toLowerCase();
            
            if (lowerCaseUrl.endsWith(".jpg") || lowerCaseUrl.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (lowerCaseUrl.endsWith(".png")) {
                return "image/png";
            } else if (lowerCaseUrl.endsWith(".gif")) {
                return "image/gif";
            } else if (lowerCaseUrl.endsWith(".webp")) {
                return "image/webp";
            } else if (lowerCaseUrl.endsWith(".mp4")) {
                return "video/mp4";
            } else if (lowerCaseUrl.endsWith(".avi")) {
                return "video/avi";
            } else if (lowerCaseUrl.endsWith(".mov")) {
                return "video/quicktime";
            } else if (lowerCaseUrl.endsWith(".pdf")) {
                return "application/pdf";
            } else if (lowerCaseUrl.endsWith(".doc") || lowerCaseUrl.endsWith(".docx")) {
                return "application/msword";
            } else if (lowerCaseUrl.endsWith(".xls") || lowerCaseUrl.endsWith(".xlsx")) {
                return "application/vnd.ms-excel";
            } else {
                return "application/octet-stream";
            }
        } catch (Exception e) {
            log.warn("推测内容类型失败，URL：{}，错误：{}", fileUrl, e.getMessage());
            return "application/octet-stream";
        }
    }

    /**
     * 删除缩略图文件
     *
     * @param thumbnailUrl 缩略图URL
     */
    private void deleteThumbnailFile(String thumbnailUrl) {
        try {
            if (thumbnailUrl == null || thumbnailUrl.trim().isEmpty()) {
                return;
            }

            // 将URL转换为文件路径
            String relativePath = thumbnailUrl.replaceFirst("^" + accessUrl, "");
            Path thumbnailPath = Paths.get(uploadPath, relativePath.replace("/", File.separator));

            File thumbnailFile = thumbnailPath.toFile();

            if (thumbnailFile.exists()) {
                boolean deleted = thumbnailFile.delete();
                if (deleted) {
                    log.info("缩略图删除成功：{}", thumbnailUrl);
                } else {
                    log.warn("缩略图删除失败：{}", thumbnailUrl);
                }
            } else {
                log.info("缩略图文件不存在，无需删除：{}", thumbnailUrl);
            }

        } catch (Exception e) {
            log.error("删除缩略图失败：{}, 错误：{}", thumbnailUrl, e.getMessage(), e);
            // 缩略图删除失败不影响主文件删除流程
        }
    }
}
