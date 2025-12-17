package com.manage.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 图标文件生成器
 * 用于生成默认的首页图标文件
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Component
public class IconGenerator {

    /**
     * 生成所有默认图标文件
     */
    public void generateDefaultIcons() {
        try {
            // 创建目录
            createDirectories();

            // 生成默认图标
            generateIcon("src/main/resources/static/images/default-icons/company.png",
                        new Color(70, 130, 180), "CO");
            generateIcon("src/main/resources/static/images/default-icons/team.png",
                        new Color(46, 204, 113), "TE");
            generateIcon("src/main/resources/static/images/default-icons/prices.png",
                        new Color(241, 196, 15), "PR");
            generateIcon("src/main/resources/static/images/default-icons/quality.png",
                        new Color(231, 76, 60), "QU");

            // 生成自定义图标
            generateIcon("uploads/icons/custom-company-icon.png",
                        new Color(52, 152, 219), "CO");
            generateIcon("uploads/icons/custom-team-icon.png",
                        new Color(39, 174, 96), "TE");
            generateIcon("uploads/icons/custom-prices-icon.png",
                        new Color(243, 156, 18), "PR");
            generateIcon("uploads/icons/custom-quality-icon.png",
                        new Color(192, 57, 43), "QU");

            log.info("所有图标文件生成完成");

        } catch (Exception e) {
            log.error("生成图标文件失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 创建必要的目录
     */
    private void createDirectories() throws IOException {
        Files.createDirectories(Paths.get("src/main/resources/static/images/default-icons"));
        Files.createDirectories(Paths.get("uploads/icons"));
    }

    /**
     * 生成单个图标文件
     *
     * @param filePath 文件路径
     * @param bgColor  背景颜色
     * @param text     显示文字
     */
    private void generateIcon(String filePath, Color bgColor, String text) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                log.debug("图标文件已存在，跳过：{}", filePath);
                return;
            }

            // 创建64x64像素的图像
            BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 绘制圆形背景
            g2d.setColor(bgColor);
            g2d.fillOval(2, 2, 60, 60);

            // 添加边框
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawOval(2, 2, 60, 60);

            // 绘制文字
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int x = (64 - textWidth) / 2;
            int y = (64 - textHeight) / 2 + fm.getAscent();
            g2d.drawString(text, x, y);

            // 释放资源
            g2d.dispose();

            // 确保父目录存在
            File parentDir = path.getParent().toFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 保存为PNG文件
            boolean saved = ImageIO.write(image, "PNG", path.toFile());
            if (saved) {
                log.info("图标生成成功：{}", filePath);
            } else {
                log.error("图标保存失败：{}", filePath);
            }

        } catch (Exception e) {
            log.error("生成图标文件失败：{}，错误：{}", filePath, e.getMessage(), e);
        }
    }

    /**
     * 检查图标文件是否存在，如果不存在则生成
     */
    public void ensureIconsExist() {
        String[] iconPaths = {
            "src/main/resources/static/images/default-icons/company.png",
            "src/main/resources/static/images/default-icons/team.png",
            "src/main/resources/static/images/default-icons/prices.png",
            "src/main/resources/static/images/default-icons/quality.png",
            "uploads/icons/custom-company-icon.png",
            "uploads/icons/custom-team-icon.png",
            "uploads/icons/custom-prices-icon.png",
            "uploads/icons/custom-quality-icon.png"
        };

        boolean needGenerate = false;
        for (String iconPath : iconPaths) {
            if (!Files.exists(Paths.get(iconPath))) {
                needGenerate = true;
                break;
            }
        }

        if (needGenerate) {
            log.info("检测到图标文件缺失，开始生成默认图标");
            generateDefaultIcons();
        } else {
            log.debug("所有图标文件已存在");
        }
    }
}