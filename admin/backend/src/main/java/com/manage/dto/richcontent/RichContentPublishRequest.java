package com.manage.dto.richcontent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 富文本内容发布请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class RichContentPublishRequest {

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    @JsonProperty("id")
    private Long id;

    /**
     * 是否立即发布
     */
    @JsonProperty("publishNow")
    private Boolean publishNow = true;

    /**
     * 定时发布时间（当publishNow为false时使用）
     */
    @JsonProperty("publishTime")
    private String publishTime;

    /**
     * 发布通知（可选）
     */
    @JsonProperty("notification")
    private String notification;
}