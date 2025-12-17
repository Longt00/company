package com.manage.dto.homepage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 主页背景管理响应DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class HomePageBackgroundResponse {

    /**
     * 主键ID
     */
    @JsonProperty("id")
    private Long id;

    /**
     * 背景类型：image-图片背景, color-纯色背景, gradient-渐变背景
     */
    @JsonProperty("backgroundType")
    private String backgroundType;

    /**
     * 背景图片URL（当background_type为image时使用）
     */
    @JsonProperty("backgroundImageUrl")
    private String backgroundImageUrl;

    /**
     * 背景颜色值（当background_type为color时使用）
     */
    @JsonProperty("backgroundColor")
    private String backgroundColor;

    /**
     * 渐变起始颜色（当background_type为gradient时使用）
     */
    @JsonProperty("gradientStartColor")
    private String gradientStartColor;

    /**
     * 渐变结束颜色（当background_type为gradient时使用）
     */
    @JsonProperty("gradientEndColor")
    private String gradientEndColor;

    /**
     * 渐变方向：horizontal-水平渐变, vertical-垂直渐变, diagonal-对角渐变
     */
    @JsonProperty("gradientDirection")
    private String gradientDirection;

    /**
     * 背景重复方式：repeat, repeat-x, repeat-y, no-repeat
     */
    @JsonProperty("backgroundRepeat")
    private String backgroundRepeat;

    /**
     * 背景位置：center, top, bottom, left, right, 或组合
     */
    @JsonProperty("backgroundPosition")
    private String backgroundPosition;

    /**
     * 背景尺寸：cover, contain, auto, 或具体像素值
     */
    @JsonProperty("backgroundSize")
    private String backgroundSize;

    /**
     * 背景透明度：0.0-1.0
     */
    @JsonProperty("backgroundOpacity")
    private Double backgroundOpacity;

    /**
     * 是否启用：0-禁用 1-启用
     */
    @JsonProperty("isActive")
    private Boolean isActive;

    /**
     * 背景描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;

    /**
     * CSS样式字符串（用于前端直接应用）
     */
    @JsonProperty("cssStyle")
    private String cssStyle;

    /**
     * 生成CSS样式字符串
     */
    public void generateCssStyle() {
        StringBuilder css = new StringBuilder();

        switch (backgroundType) {
            case "image":
                if (backgroundImageUrl != null && !backgroundImageUrl.trim().isEmpty()) {
                    css.append("background-image: url('").append(backgroundImageUrl).append("');");
                }
                css.append("background-repeat: ").append(backgroundRepeat != null ? backgroundRepeat : "no-repeat").append(";");
                css.append("background-position: ").append(backgroundPosition != null ? backgroundPosition : "center").append(";");
                css.append("background-size: ").append(backgroundSize != null ? backgroundSize : "cover").append(";");
                break;

            case "color":
                if (backgroundColor != null && !backgroundColor.trim().isEmpty()) {
                    css.append("background-color: ").append(backgroundColor).append(";");
                }
                break;

            case "gradient":
                String direction = gradientDirection != null ? gradientDirection : "vertical";
                String gradientDir = "to " + direction;

                if ("horizontal".equals(direction)) {
                    gradientDir = "to right";
                } else if ("diagonal".equals(direction)) {
                    gradientDir = "to bottom right";
                }

                if (gradientStartColor != null && gradientEndColor != null) {
                    css.append("background: linear-gradient(")
                       .append(gradientDir).append(", ")
                       .append(gradientStartColor).append(", ")
                       .append(gradientEndColor).append(");");
                }
                break;
        }

        if (backgroundOpacity != null && backgroundOpacity < 1.0) {
            css.append("opacity: ").append(backgroundOpacity).append(";");
        }

        this.cssStyle = css.toString();
    }
}