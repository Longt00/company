package com.manage.dto.homepage;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 字体颜色管理请求 DTO
 *
 * @author System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextColorRequest {

    /**
     * 是否自动计算字体颜色
     */
    @NotNull(message = "是否自动计算字体颜色不能为空")
    private Boolean autoTextColor;

    /**
     * 主要文本颜色（当autoTextColor为false时必填）
     */
    @Size(max = 50, message = "主要文本颜色长度不能超过50个字符")
    private String primaryTextColor;

    /**
     * 次要文本颜色（当autoTextColor为false时可选）
     */
    @Size(max = 50, message = "次要文本颜色长度不能超过50个字符")
    private String secondaryTextColor;

    /**
     * 链接颜色（当autoTextColor为false时可选）
     */
    @Size(max = 50, message = "链接颜色长度不能超过50个字符")
    private String linkColor;

    /**
     * 强调色（当autoTextColor为false时可选）
     */
    @Size(max = 50, message = "强调色长度不能超过50个字符")
    private String accentColor;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;
}