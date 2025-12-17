-- 创建主页背景管理表
CREATE TABLE `home_page_background` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `background_type` VARCHAR(20) NOT NULL COMMENT '背景类型：image-图片背景, color-纯色背景, gradient-渐变背景',
    `background_image_url` VARCHAR(500) NULL COMMENT '背景图片URL',
    `background_color` VARCHAR(50) NULL COMMENT '背景颜色值',
    `gradient_start_color` VARCHAR(50) NULL COMMENT '渐变起始颜色',
    `gradient_end_color` VARCHAR(50) NULL COMMENT '渐变结束颜色',
    `gradient_direction` VARCHAR(20) NULL DEFAULT 'vertical' COMMENT '渐变方向：horizontal-水平渐变, vertical-垂直渐变, diagonal-对角渐变',
    `background_repeat` VARCHAR(20) NULL DEFAULT 'no-repeat' COMMENT '背景重复方式：repeat, repeat-x, repeat-y, no-repeat',
    `background_position` VARCHAR(50) NULL DEFAULT 'center' COMMENT '背景位置：center, top, bottom, left, right',
    `background_size` VARCHAR(50) NULL DEFAULT 'cover' COMMENT '背景尺寸：cover, contain, auto',
    `background_opacity` DECIMAL(3,2) NULL DEFAULT 1.00 COMMENT '背景透明度：0.0-1.0',
    `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
    `description` VARCHAR(200) NULL COMMENT '背景描述',
    `created_by` BIGINT NULL COMMENT '创建人ID',
    `updated_by` BIGINT NULL COMMENT '更新人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_background_type` (`background_type`),
    INDEX `idx_is_active` (`is_active`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主页背景管理表';

-- 插入默认背景配置
INSERT INTO `home_page_background` (
    `background_type`,
    `background_color`,
    `gradient_direction`,
    `background_repeat`,
    `background_position`,
    `background_size`,
    `background_opacity`,
    `is_active`,
    `description`,
    `created_by`,
    `updated_by`
) VALUES (
    'color',
    '#f5f5f5',
    'vertical',
    'no-repeat',
    'center',
    'cover',
    1.00,
    1,
    '默认主页背景',
    1,
    1
);