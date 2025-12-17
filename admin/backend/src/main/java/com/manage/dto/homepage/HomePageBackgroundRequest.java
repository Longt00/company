package com.manage.dto.homepage;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 主页背景管理请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePageBackgroundRequest {

    /**
     * 背景类型：image-图片背景, color-纯色背景, gradient-渐变背景
     */
    @NotBlank(message = "背景类型不能为空")
    @Size(max = 20, message = "背景类型长度不能超过20个字符")
    private String backgroundType;

    /**
     * 背景图片URL（当background_type为image时使用）
     */
    @Size(max = 500, message = "背景图片URL长度不能超过500个字符")
    private String backgroundImageUrl;

    /**
     * 背景颜色值（当background_type为color时使用）
     */
    @Size(max = 50, message = "背景颜色值长度不能超过50个字符")
    private String backgroundColor;

    /**
     * 渐变起始颜色（当background_type为gradient时使用）
     */
    @Size(max = 50, message = "渐变起始颜色长度不能超过50个字符")
    private String gradientStartColor;

    /**
     * 渐变结束颜色（当background_type为gradient时使用）
     */
    @Size(max = 50, message = "渐变结束颜色长度不能超过50个字符")
    private String gradientEndColor;

    /**
     * 渐变方向：horizontal-水平渐变, vertical-垂直渐变, diagonal-对角渐变
     */
    @Size(max = 20, message = "渐变方向长度不能超过20个字符")
    private String gradientDirection = "vertical";

    /**
     * 背景重复方式：repeat, repeat-x, repeat-y, no-repeat
     */
    @Size(max = 20, message = "背景重复方式长度不能超过20个字符")
    private String backgroundRepeat = "no-repeat";

    /**
     * 背景位置：center, top, bottom, left, right, 或组合
     */
    @Size(max = 50, message = "背景位置长度不能超过50个字符")
    private String backgroundPosition = "center";

    /**
     * 背景尺寸：cover, contain, auto, 或具体像素值
     */
    @Size(max = 50, message = "背景尺寸长度不能超过50个字符")
    private String backgroundSize = "cover";

    /**
     * 背景透明度：0.0-1.0
     */
    @Min(value = 0, message = "透明度不能小于0")
    @Max(value = 1, message = "透明度不能大于1")
    private Double backgroundOpacity = 1.0;

    /**
     * 是否启用：0-禁用 1-启用
     */
    private Boolean isActive = true;

    /**
     * 背景描述
     */
    @Size(max = 200, message = "背景描述长度不能超过200个字符")
    private String description;
}