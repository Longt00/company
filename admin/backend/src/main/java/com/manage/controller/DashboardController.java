package com.manage.controller;

import com.manage.common.Result;
import com.manage.entity.MediaFile;
import com.manage.repository.MediaFileRepository;
import com.manage.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final MediaFileRepository mediaFileRepository;
    private final ProductRepository productRepository;

    /**
     * 获取仪表盘统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getDashboardStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 统计未删除的图片数量（status != 3），排除产品相关的图片
            List<MediaFile> allImages = mediaFileRepository.findByFileTypeAndStatusOrderByCreateTimeDesc("image", 1);
            long imageCount = allImages.stream()
                    .filter(file -> {
                        String category = file.getCategory();
                        // 排除产品相关的图片：category为product、product/images、product-image或以product开头的
                        return category == null || 
                               (!category.equals("product") &&
                                !category.startsWith("product/") && 
                                !category.equals("product-image") && 
                                !category.startsWith("product-") &&
                                !category.equals("editor/images"));
                    })
                    .count();
            
            // 统计未删除的视频数量（status != 3）
            long videoCount = mediaFileRepository.findByFileTypeAndStatusOrderByCreateTimeDesc("video", 1).size();

            // 统计产品总数
            long productCount = productRepository.count();

            // 构建返回数据
            statistics.put("imageCount", imageCount);
            statistics.put("videoCount", videoCount);
            statistics.put("productCount", productCount);

            log.info("获取仪表盘统计信息成功: 图片={}, 视频={}, 产品={}", imageCount, videoCount, productCount);
            return Result.success("获取统计信息成功", statistics);

        } catch (Exception e) {
            log.error("获取仪表盘统计信息失败: {}", e.getMessage(), e);
            return Result.error("获取统计信息失败：" + e.getMessage());
        }
    }
}
