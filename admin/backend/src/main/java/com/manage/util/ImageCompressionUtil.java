package com.manage.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 图片压缩工具类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
public class ImageCompressionUtil {

    // 支持的图片格式
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    // 支持的MIME类型
    private static final List<String> SUPPORTED_MIME_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    /**
     * 图片信息
     */
    public static class ImageInfo {
        private final int width;
        private final int height;
        private final double sizeMB;
        private final String format;

        public ImageInfo(int width, int height, double sizeMB, String format) {
            this.width = width;
            this.height = height;
            this.sizeMB = sizeMB;
            this.format = format;
        }

        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public double getSizeMB() { return sizeMB; }
        public String getFormat() { return format; }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private int width;
            private int height;
            private double sizeMB;
            private String format;

            public Builder width(int width) {
                this.width = width;
                return this;
            }

            public Builder height(int height) {
                this.height = height;
                return this;
            }

            public Builder sizeMB(double sizeMB) {
                this.sizeMB = sizeMB;
                return this;
            }

            public Builder format(String format) {
                this.format = format;
                return this;
            }

            public ImageInfo build() {
                return new ImageInfo(width, height, sizeMB, format);
            }
        }
    }

    /**
     * 图片压缩结果
     */
    public static class ImageCompressionResult {
        private final long originalSize;
        private final long compressedSize;
        private final byte[] finalData;
        private final boolean compressed;
        private final long compressionTime;

        private ImageCompressionResult(long originalSize, long compressedSize, byte[] finalData, boolean compressed, long compressionTime) {
            this.originalSize = originalSize;
            this.compressedSize = compressedSize;
            this.finalData = finalData;
            this.compressed = compressed;
            this.compressionTime = compressionTime;
        }

        public long getOriginalSize() { return originalSize; }
        public long getCompressedSize() { return compressedSize; }
        public byte[] getFinalData() { return finalData; }
        public boolean isCompressed() { return compressed; }
        public long getCompressionTime() { return compressionTime; }

        public double getCompressionRatio() {
            if (originalSize == 0) return 0.0;
            return (double) (originalSize - compressedSize) / originalSize;
        }

        public static ImageCompressionResult notCompressed(long originalSize) {
            return new ImageCompressionResult(originalSize, originalSize, null, false, 0);
        }

        public static ImageCompressionResult compressed(long originalSize, long compressedSize, byte[] compressedData) {
            return new ImageCompressionResult(originalSize, compressedSize, compressedData, true, 0);
        }

        public static ImageCompressionResult compressed(long originalSize, long compressedSize, byte[] compressedData, long compressionTime) {
            return new ImageCompressionResult(originalSize, compressedSize, compressedData, true, compressionTime);
        }
    }

    /**
     * 获取图片信息
     *
     * @param file 上传的图片文件
     * @return 图片信息
     * @throws IOException 读取图片失败
     */
    public static ImageInfo getImageInfo(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new IOException("无法读取图片内容，可能文件已损坏或不是有效图片");
        }

        return ImageInfo.builder()
                .width(image.getWidth())
                .height(image.getHeight())
                .sizeMB(file.getSize() / (1024.0 * 1024.0))
                .format(getImageFormat(file))
                .build();
    }

    /**
     * 智能压缩图片
     *
     * @param file 上传的图片文件
     * @return 压缩结果
     * @throws IOException 压缩失败
     */
    public static ImageCompressionResult smartCompress(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        double sizeMB = file.getSize() / (1024.0 * 1024.0);
        String format = getImageFormat(file);

        // 如果文件小于500KB且尺寸小于800x600，不压缩
        if (sizeMB < 0.5 && originalImage.getWidth() <= 800 && originalImage.getHeight() <= 600) {
            log.info("文件很小({:.2f}MB)且尺寸合理({}x{})，无需压缩", sizeMB, originalImage.getWidth(), originalImage.getHeight());
            return ImageCompressionResult.notCompressed(file.getSize());
        }

        // 即使文件较小，但如果尺寸过大，也要压缩
        if (sizeMB < 2.0 && (originalImage.getWidth() > 1600 || originalImage.getHeight() > 1200)) {
            log.info("文件大小适中({:.2f}MB)但尺寸较大({}x{})，进行压缩", sizeMB, originalImage.getWidth(), originalImage.getHeight());
        }

        // 计算目标尺寸
        int targetWidth = calculateTargetWidth(originalImage.getWidth(), originalImage.getHeight(), sizeMB);
        int targetHeight = calculateTargetHeight(originalImage.getWidth(), originalImage.getHeight(), sizeMB);

        log.info("开始压缩图片: {}x{} -> {}x{}, 原始大小: {:.2f}MB",
            originalImage.getWidth(), originalImage.getHeight(), targetWidth, targetHeight, sizeMB);

        try {
            // 执行压缩
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .size(targetWidth, targetHeight)
                    .outputQuality(calculateOutputQuality(sizeMB))
                    .outputFormat(format)
                    .toOutputStream(outputStream);

            byte[] compressedData = outputStream.toByteArray();
            long compressionTime = System.currentTimeMillis() - startTime;

            log.info("压缩完成: {:.2f}MB -> {:.2f}MB, 耗时: {}ms",
                sizeMB, compressedData.length / (1024.0 * 1024.0), compressionTime);

            return ImageCompressionResult.compressed(
                file.getSize(),
                compressedData.length,
                compressedData,
                compressionTime
            );

        } catch (Exception e) {
            log.error("图片压缩失败: {}", e.getMessage(), e);
            throw new IOException("图片压缩失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成缩略图
     *
     * @param file 原始图片文件
     * @return 缩略图压缩结果
     * @throws IOException 生成缩略图失败
     */
    public static ImageCompressionResult generateThumbnail(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();

        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        if (originalImage == null) {
            throw new IOException("无法读取原始图片，无法生成缩略图");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .size(300, 300)                    // 缩略图固定尺寸
                    .keepAspectRatio(true)              // 保持宽高比
                    .outputQuality(0.8f)                // 缩略图质量
                    .outputFormat("jpg")                // 缩略图统一格式
                    .toOutputStream(outputStream);

            byte[] thumbnailData = outputStream.toByteArray();
            long compressionTime = System.currentTimeMillis() - startTime;

            log.info("缩略图生成完成: {}x{}, 大小: {:.2f}KB, 耗时: {}ms",
                300, 300, thumbnailData.length / 1024.0, compressionTime);

            return ImageCompressionResult.compressed(
                file.getSize(),
                thumbnailData.length,
                thumbnailData,
                compressionTime
            );

        } catch (Exception e) {
            log.error("生成缩略图失败: {}", e.getMessage(), e);
            throw new IOException("生成缩略图失败: " + e.getMessage(), e);
        }
    }

    /**
     * 计算目标宽度（更激进的压缩策略）
     *
     * @param originalWidth  原始宽度
     * @param originalHeight 原始高度
     * @param sizeMB        文件大小(MB)
     * @return 目标宽度
     */
    private static int calculateTargetWidth(int originalWidth, int originalHeight, double sizeMB) {
        // 根据文件大小设定更严格的尺寸限制
        if (sizeMB > 20) {
            return Math.min(1200, originalWidth);  // 超大图限制到1200宽
        } else if (sizeMB > 10) {
            return Math.min(1080, originalWidth);  // 大图限制到1080宽
        } else if (sizeMB > 5) {
            return Math.min(900, originalWidth);   // 中图限制到900宽
        } else if (sizeMB > 2) {
            return Math.min(800, originalWidth);   // 小图限制到800宽
        } else if (sizeMB > 0.5) {
            return Math.min(600, originalWidth);   // 很小的图也适当缩小
        } else {
            return originalWidth;                   // 极小图保持原始宽度
        }
    }

    /**
     * 计算目标高度（更激进的压缩策略）
     *
     * @param originalWidth  原始宽度
     * @param originalHeight 原始高度
     * @param sizeMB        文件大小(MB)
     * @return 目标高度
     */
    private static int calculateTargetHeight(int originalWidth, int originalHeight, double sizeMB) {
        // 保持宽高比的情况下计算目标高度
        int targetWidth = calculateTargetWidth(originalWidth, originalHeight, sizeMB);
        double aspectRatio = (double) originalHeight / originalWidth;
        int targetHeight = (int) (targetWidth * aspectRatio);

        // 根据文件大小进一步限制高度
        if (sizeMB > 20) {
            return Math.min(800, targetHeight);    // 超大图限制到800高
        } else if (sizeMB > 10) {
            return Math.min(700, targetHeight);    // 大图限制到700高
        } else if (sizeMB > 5) {
            return Math.min(600, targetHeight);    // 中图限制到600高
        } else if (sizeMB > 2) {
            return Math.min(500, targetHeight);    // 小图限制到500高
        } else {
            return targetHeight;                    // 其他情况按宽高比计算
        }
    }

    /**
     * 计算输出质量（更激进的压缩质量）
     *
     * @param sizeMB 文件大小(MB)
     * @return 输出质量
     */
    private static float calculateOutputQuality(double sizeMB) {
        if (sizeMB > 20) {
            return 0.60f;  // 超大图压缩到60%质量
        } else if (sizeMB > 10) {
            return 0.65f;  // 大图压缩到65%质量
        } else if (sizeMB > 5) {
            return 0.70f;  // 中图压缩到70%质量
        } else if (sizeMB > 2) {
            return 0.75f;  // 小图压缩到75%质量
        } else if (sizeMB > 0.5) {
            return 0.80f;  // 很小的图压缩到80%质量
        } else {
            return 0.85f;  // 极小图压缩到85%质量
        }
    }

    /**
     * 获取图片格式
     *
     * @param file 图片文件
     * @return 图片格式
     */
    private static String getImageFormat(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return "jpg"; // 默认格式
        }

        String filename = originalFilename.toLowerCase();
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "jpg";
        } else if (filename.endsWith(".png")) {
            return "png";
        } else if (filename.endsWith(".gif")) {
            return "gif";
        } else if (filename.endsWith(".webp")) {
            return "webp";
        } else {
            return "jpg"; // 默认格式
        }
    }

    /**
     * 检查是否支持该图片格式
     *
     * @param file 图片文件
     * @return 是否支持
     */
    public static boolean isSupportedFormat(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        // 检查MIME类型
        if (contentType != null && SUPPORTED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return true;
        }

        // 检查文件扩展名
        if (filename != null) {
            String lowerFilename = filename.toLowerCase();
            return SUPPORTED_FORMATS.stream().anyMatch(format -> lowerFilename.endsWith("." + format));
        }

        return false;
    }

    /**
     * 验证图片文件
     *
     * @param file    图片文件
     * @param maxSize 最大文件大小(字节)
     * @return 是否有效
     */
    public static boolean validateImageFile(MultipartFile file, long maxSize) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // 检查文件大小
        if (file.getSize() > maxSize) {
            return false;
        }

        // 检查文件格式
        return isSupportedFormat(file);
    }
}