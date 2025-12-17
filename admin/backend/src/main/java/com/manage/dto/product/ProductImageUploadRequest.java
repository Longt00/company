package com.manage.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 产品图片上传请求DTO
 *
 * @author System
 * @version 1.0
 */
@Data
public class ProductImageUploadRequest {

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    @JsonProperty("productId")
    private Long productId;

    /**
     * 图片URL列表
     */
    @Size(min = 1, max = 10, message = "图片数量必须在1-10张之间")
    @JsonProperty("imageUrls")
    private List<String> imageUrls;

    /**
     * 是否设置为主图
     */
    @JsonProperty("setAsMainImage")
    private Boolean setAsMainImage = false;

    /**
     * 主图索引（当setAsMainImage为true时使用）
     */
    @JsonProperty("mainImageIndex")
    private Integer mainImageIndex = 0;
}