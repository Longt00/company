package com.manage.dto.homepage;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 字体颜色管理响应 DTO
 *
 * @author System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextColorResponse {

    /**
     * 背景配置ID
     */
    private Long backgroundId;

    /**
     * 是否自动计算字体颜色
     */
    private Boolean autoTextColor;

    /**
     * 主要文本颜色
     */
    private String primaryTextColor;

    /**
     * 次要文本颜色
     */
    private String secondaryTextColor;

    /**
     * 链接颜色
     */
    private String linkColor;

    /**
     * 强调色
     */
    private String accentColor;

    /**
     * 背景类型
     */
    private String backgroundType;

    /**
     * 背景颜色（纯色背景时）
     */
    private String backgroundColor;

    /**
     * 渐变起始颜色（渐变背景时）
     */
    private String gradientStartColor;

    /**
     * 渐变结束颜色（渐变背景时）
     */
    private String gradientEndColor;

    /**
     * 主要文本颜色与背景的对比度
     */
    private Double primaryContrast;

    /**
     * 次要文本颜色与背景的对比度
     */
    private Double secondaryContrast;

    /**
     * 链接颜色与背景的对比度
     */
    private Double linkContrast;

    /**
     * WCAG 合规性检查结果
     */
    private WCAGCompliance wcagCompliance;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * WCAG 合规性内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WCAGCompliance {
        /**
         * 主要文本是否通过 AA 级标准
         */
        private Boolean primaryAA;

        /**
         * 主要文本是否通过 AAA 级标准
         */
        private Boolean primaryAAA;

        /**
         * 次要文本是否通过 AA 级标准
         */
        private Boolean secondaryAA;

        /**
         * 次要文本是否通过 AAA 级标准
         */
        private Boolean secondaryAAA;

        /**
         * 链接文本是否通过 AA 级标准
         */
        private Boolean linkAA;

        /**
         * 链接文本是否通过 AAA 级标准
         */
        private Boolean linkAAA;

        /**
         * 整体是否通过 AA 级标准
         */
        private Boolean overallAA;

        /**
         * 整体是否通过 AAA 级标准
         */
        private Boolean overallAAA;
    }
}