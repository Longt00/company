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
 * 主页背景管理实体类
 *
 * @author System
 * @version 1.0
 */
@Entity
@Table(name = "home_page_background")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePageBackground {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 背景类型：image-图片背景, color-纯色背景, gradient-渐变背景
     */
    @NotBlank(message = "背景类型不能为空")
    @Column(name = "background_type", nullable = false, length = 20)
    private String backgroundType;

    /**
     * 背景图片URL（当background_type为image时使用）
     */
    @Size(max = 500, message = "背景图片URL长度不能超过500个字符")
    @Column(name = "background_image_url", length = 500)
    private String backgroundImageUrl;

    /**
     * 背景颜色值（当background_type为color时使用）
     */
    @Size(max = 50, message = "背景颜色值长度不能超过50个字符")
    @Column(name = "background_color", length = 50)
    private String backgroundColor;

    /**
     * 渐变起始颜色（当background_type为gradient时使用）
     */
    @Size(max = 50, message = "渐变起始颜色长度不能超过50个字符")
    @Column(name = "gradient_start_color", length = 50)
    private String gradientStartColor;

    /**
     * 渐变结束颜色（当background_type为gradient时使用）
     */
    @Size(max = 50, message = "渐变结束颜色长度不能超过50个字符")
    @Column(name = "gradient_end_color", length = 50)
    private String gradientEndColor;

    /**
     * 渐变方向：horizontal-水平渐变, vertical-垂直渐变, diagonal-对角渐变
     */
    @Size(max = 20, message = "渐变方向长度不能超过20个字符")
    @Column(name = "gradient_direction", length = 20)
    private String gradientDirection;

    /**
     * 背景重复方式：repeat, repeat-x, repeat-y, no-repeat
     */
    @Size(max = 20, message = "背景重复方式长度不能超过20个字符")
    @Column(name = "background_repeat", length = 20)
    private String backgroundRepeat = "no-repeat";

    /**
     * 背景位置：center, top, bottom, left, right, 或组合
     */
    @Size(max = 50, message = "背景位置长度不能超过50个字符")
    @Column(name = "background_position", length = 50)
    private String backgroundPosition = "center";

    /**
     * 背景尺寸：cover, contain, auto, 或具体像素值
     */
    @Size(max = 50, message = "背景尺寸长度不能超过50个字符")
    @Column(name = "background_size", length = 50)
    private String backgroundSize = "cover";

    /**
     * 背景透明度：0.0-1.0
     */
    @Column(name = "background_opacity")
    private Double backgroundOpacity = 1.0;

    /**
     * 是否自动计算字体颜色：0-不自动计算 1-自动计算
     */
    @Column(name = "auto_text_color", nullable = false)
    private Boolean autoTextColor = true;

    /**
     * 主要文本颜色（当auto_text_color为false时使用）
     */
    @Size(max = 50, message = "主要文本颜色长度不能超过50个字符")
    @Column(name = "primary_text_color", length = 50)
    private String primaryTextColor;

    /**
     * 次要文本颜色（当auto_text_color为false时使用）
     */
    @Size(max = 50, message = "次要文本颜色长度不能超过50个字符")
    @Column(name = "secondary_text_color", length = 50)
    private String secondaryTextColor;

    /**
     * 链接颜色（当auto_text_color为false时使用）
     */
    @Size(max = 50, message = "链接颜色长度不能超过50个字符")
    @Column(name = "link_color", length = 50)
    private String linkColor;

    /**
     * 强调色（当auto_text_color为false时使用）
     */
    @Size(max = 50, message = "强调色长度不能超过50个字符")
    @Column(name = "accent_color", length = 50)
    private String accentColor;

    /**
     * 是否启用：0-禁用 1-启用
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 背景描述
     */
    @Size(max = 200, message = "背景描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 创建人ID
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 更新人ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;

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

    /**
     * JPA生命周期回调 - 更新前自动设置更新时间
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}