package com.manage.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 图片压缩工具类测试
 *
 * @author System
 * @version 1.0
 */
class ImageCompressionUtilTest {

    @Test
    @DisplayName("测试图片格式验证")
    void testIsSupportedFormat() {
        // 测试支持的格式
        assertTrue(ImageCompressionUtil.isSupportedFormat(
            createMockImageFile("test.jpg", "image/jpeg", 1024)));
        assertTrue(ImageCompressionUtil.isSupportedFormat(
            createMockImageFile("test.png", "image/png", 1024)));
        assertTrue(ImageCompressionUtil.isSupportedFormat(
            createMockImageFile("test.gif", "image/gif", 1024)));

        // 测试不支持的格式
        assertFalse(ImageCompressionUtil.isSupportedFormat(
            createMockImageFile("test.txt", "text/plain", 1024)));
        assertFalse(ImageCompressionUtil.isSupportedFormat(
            createMockImageFile("test.pdf", "application/pdf", 1024)));
    }

    @Test
    @DisplayName("测试图片验证")
    void testValidateImageFile() {
        // 测试有效图片
        assertTrue(ImageCompressionUtil.validateImageFile(
            createMockImageFile("test.jpg", "image/jpeg", 1024), 10 * 1024 * 1024));

        // 测试过大文件
        assertFalse(ImageCompressionUtil.validateImageFile(
            createMockImageFile("test.jpg", "image/jpeg", 20 * 1024 * 1024), 10 * 1024 * 1024));

        // 测试空文件
        assertFalse(ImageCompressionUtil.validateImageFile(
            new MockMultipartFile("test", "".getBytes()), 10 * 1024 * 1024));

        // 测试null文件
        assertFalse(ImageCompressionUtil.validateImageFile(null, 10 * 1024 * 1024));
    }

    @Test
    @DisplayName("测试获取图片信息")
    void testGetImageInfo() throws IOException {
        // 创建一个简单的测试图片
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile mockFile = new MockMultipartFile(
            "test", "test.jpg", "image/jpeg", baos.toByteArray());

        ImageCompressionUtil.ImageInfo info = ImageCompressionUtil.getImageInfo(mockFile);

        assertEquals(800, info.getWidth());
        assertEquals(600, info.getHeight());
        assertEquals("jpg", info.getFormat());
        assertTrue(info.getSizeMB() > 0);
    }

    @Test
    @DisplayName("测试智能压缩 - 超小文件无需压缩")
    void testSmartCompressTinyFile() throws IOException {
        // 创建一个很小的图片（500KB以内，800x600以内）
        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile mockFile = new MockMultipartFile(
            "test", "test.jpg", "image/jpeg", baos.toByteArray());

        ImageCompressionUtil.ImageCompressionResult result = ImageCompressionUtil.smartCompress(mockFile);

        // 超小文件应该无需压缩
        assertFalse(result.isCompressed());
        assertEquals(result.getOriginalSize(), result.getCompressedSize());
    }

    @Test
    @DisplayName("测试智能压缩 - 基础功能测试")
    void testSmartCompressBasicFunctionality() throws IOException {
        // 创建一个图片
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(image, "jpg", baos);

        MockMultipartFile mockFile = new MockMultipartFile(
            "test", "test.jpg", "image/jpeg", baos.toByteArray());

        ImageCompressionUtil.ImageCompressionResult result = ImageCompressionUtil.smartCompress(mockFile);

        // 测试基础功能
        assertNotNull(result);
        assertTrue(result.getOriginalSize() > 0);

        if (result.isCompressed()) {
            assertTrue(result.getCompressedSize() < result.getOriginalSize());
            assertTrue(result.getCompressionRatio() >= 0);
            assertNotNull(result.getFinalData());
            assertTrue(result.getFinalData().length > 0);
        } else {
            assertEquals(result.getOriginalSize(), result.getCompressedSize());
        }

        assertTrue(result.getCompressionTime() >= 0);
    }

    @Test
    @DisplayName("测试生成缩略图")
    void testGenerateThumbnail() throws IOException {
        // 创建一个测试图片
        BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile mockFile = new MockMultipartFile(
            "test", "test.jpg", "image/jpeg", baos.toByteArray());

        ImageCompressionUtil.ImageCompressionResult result = ImageCompressionUtil.generateThumbnail(mockFile);

        // 缩略图应该被成功生成
        assertTrue(result.isCompressed());
        assertTrue(result.getCompressedSize() > 0);
        assertTrue(result.getCompressedSize() < result.getOriginalSize());

        // 验证生成的缩略图是有效图片
        BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(result.getFinalData()));
        assertNotNull(thumbnail);
        assertTrue(thumbnail.getWidth() <= 300);
        assertTrue(thumbnail.getHeight() <= 300);
    }

    @Test
    @DisplayName("测试压缩比率计算")
    void testCompressionRatio() {
        // 测试压缩比率计算
        ImageCompressionUtil.ImageCompressionResult result1 =
            ImageCompressionUtil.ImageCompressionResult.compressed(1000, 500, new byte[500]);
        assertEquals(0.5, result1.getCompressionRatio(), 0.01);

        ImageCompressionUtil.ImageCompressionResult result2 =
            ImageCompressionUtil.ImageCompressionResult.compressed(1000, 1000, new byte[1000]);
        assertEquals(0.0, result2.getCompressionRatio(), 0.01);

        // 未压缩文件压缩比率应为0
        ImageCompressionUtil.ImageCompressionResult result3 =
            ImageCompressionUtil.ImageCompressionResult.notCompressed(1000);
        assertEquals(0.0, result3.getCompressionRatio(), 0.01);
    }

    /**
     * 创建模拟图片文件
     */
    private MultipartFile createMockImageFile(String filename, String contentType, int size) {
        byte[] content = new byte[size];
        // 填充一些模拟数据
        for (int i = 0; i < size; i++) {
            content[i] = (byte) (i % 256);
        }
        return new MockMultipartFile("file", filename, contentType, content);
    }
}