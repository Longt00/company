-- 为 home_page_background 表添加字体颜色相关字段

-- 添加是否自动计算字体颜色字段
ALTER TABLE home_page_background
ADD COLUMN auto_text_color BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否自动计算字体颜色：true-自动计算，false-使用自定义颜色';

-- 添加主要文本颜色字段
ALTER TABLE home_page_background
ADD COLUMN primary_text_color VARCHAR(50) COMMENT '主要文本颜色（十六进制格式）';

-- 添加次要文本颜色字段
ALTER TABLE home_page_background
ADD COLUMN secondary_text_color VARCHAR(50) COMMENT '次要文本颜色（十六进制格式）';

-- 添加链接颜色字段
ALTER TABLE home_page_background
ADD COLUMN link_color VARCHAR(50) COMMENT '链接颜色（十六进制格式）';

-- 添加强调色字段
ALTER TABLE home_page_background
ADD COLUMN accent_color VARCHAR(50) COMMENT '强调色（十六进制格式）';

-- 为字段添加索引以提高查询性能
CREATE INDEX idx_home_page_background_auto_text_color ON home_page_background(auto_text_color);
CREATE INDEX idx_home_page_background_active ON home_page_background(is_active);

-- 添加注释说明
ALTER TABLE home_page_background COMMENT = '主页背景管理表 - 支持背景色和字体颜色自动调整';