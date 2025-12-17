package com.manage.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 压缩文件包装类
 * 用于包装压缩后的图片数据，实现MultipartFile接口
 *
 * @author System
 * @version 1.0
 */
@Slf4j
public class CompressedMultipartFile implements MultipartFile {

    private final MultipartFile originalFile;
    private final byte[] compressedData;
    private final String newFilename;

    /**
     * 从原始文件创建压缩文件包装器
     *
     * @param original     原始文件
     * @param compressedData 压缩后的数据
     * @param newFilename    新文件名
     */
    private CompressedMultipartFile(MultipartFile original, byte[] compressedData, String newFilename) {
        this.originalFile = original;
        this.compressedData = compressedData;
        this.newFilename = newFilename;
    }

    /**
     * 从原始文件创建压缩文件
     *
     * @param original        原始文件
     * @param compressedData  压缩数据
     * @return 压缩文件包装器
     */
    public static CompressedMultipartFile fromOriginal(MultipartFile original, byte[] compressedData) {
        String originalName = original.getOriginalFilename();
        if (originalName == null) {
            throw new IllegalArgumentException("原始文件名不能为空");
        }

        // 获取文件扩展名
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalName.substring(dotIndex);
        }

        // 生成新的文件名
        String nameWithoutExt = originalName.substring(0, dotIndex > 0 ? dotIndex : originalName.length());
        String newName = nameWithoutExt + "_compressed_" + System.currentTimeMillis() + extension;

        log.debug("创建压缩文件: {} -> {}", originalName, newName);
        return new CompressedMultipartFile(original, compressedData, newName);
    }

    /**
     * 创建缩略图文件
     *
     * @param original       原始文件
     * @param thumbnailData 缩略图数据
     * @return 缩略图文件包装器
     */
    public static CompressedMultipartFile createThumbnail(MultipartFile original, byte[] thumbnailData) {
        String originalName = original.getOriginalFilename();
        if (originalName == null) {
            throw new IllegalArgumentException("原始文件名不能为空");
        }

        // 获取不包含扩展名的文件名
        String nameWithoutExt = originalName;
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            nameWithoutExt = originalName.substring(0, dotIndex);
        }

        // 缩略图统一使用jpg格式
        String thumbnailName = nameWithoutExt + "_thumbnail.jpg";

        log.debug("创建缩略图: {} -> {}", originalName, thumbnailName);
        return new CompressedMultipartFile(original, thumbnailData, thumbnailName);
    }

    /**
     * 创建自定义名称的压缩文件
     *
     * @param original        原始文件
     * @param compressedData  压缩数据
     * @param customFilename  自定义文件名
     * @return 压缩文件包装器
     */
    public static CompressedMultipartFile createCustom(MultipartFile original, byte[] compressedData, String customFilename) {
        log.debug("创建自定义压缩文件: {} -> {}", original.getOriginalFilename(), customFilename);
        return new CompressedMultipartFile(original, compressedData, customFilename);
    }

    @Override
    public String getName() {
        return newFilename;
    }

    @Override
    public String getOriginalFilename() {
        return newFilename;
    }

    @Override
    public String getContentType() {
        return originalFile.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return compressedData == null || compressedData.length == 0;
    }

    @Override
    public long getSize() {
        return compressedData != null ? compressedData.length : 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return compressedData != null ? compressedData.clone() : new byte[0];
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return compressedData != null ? new ByteArrayInputStream(compressedData) : new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        if (compressedData == null) {
            throw new IllegalStateException("压缩数据为空，无法转移文件");
        }

        try (InputStream inputStream = getInputStream()) {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, dest);
        }
    }

    /**
     * 获取原始文件
     *
     * @return 原始文件
     */
    public MultipartFile getOriginalFile() {
        return originalFile;
    }

    /**
     * 获取压缩数据
     *
     * @return 压缩数据
     */
    public byte[] getCompressedData() {
        return compressedData != null ? compressedData.clone() : null;
    }

    /**
     * 获取原始文件名
     *
     * @return 原始文件名
     */
    public String getOriginalName() {
        return originalFile.getOriginalFilename();
    }

    /**
     * 获取原始文件大小
     *
     * @return 原始文件大小
     */
    public long getOriginalSize() {
        return originalFile.getSize();
    }

    /**
     * 获取压缩比率
     *
     * @return 压缩比率 (0-1之间)
     */
    public double getCompressionRatio() {
        long originalSize = originalFile.getSize();
        long compressedSize = getSize();

        if (originalSize == 0) {
            return 0.0;
        }

        return (double) (originalSize - compressedSize) / originalSize;
    }

    /**
     * 获取压缩百分比
     *
     * @return 压缩百分比字符串
     */
    public String getCompressionPercentage() {
        return String.format("%.1f%%", getCompressionRatio() * 100);
    }

    /**
     * 是否为压缩文件
     *
     * @return 是否压缩
     */
    public boolean isCompressed() {
        long originalSize = originalFile.getSize();
        long compressedSize = getSize();
        return compressedSize < originalSize;
    }

    @Override
    public String toString() {
        return String.format("CompressedMultipartFile{name='%s', originalSize=%d, compressedSize=%d, ratio=%s}",
            newFilename, getOriginalSize(), getSize(), getCompressionPercentage());
    }
}