# 阿里巴巴普惠体字体文件

## 概述

本项目使用阿里巴巴普惠体作为主要字体，为用户提供优秀的中文显示效果。

## 字体配置

字体配置位于 `fonts.css` 文件中，包含以下字重：

- Regular (400) - 常规字重
- Bold (700) - 粗体字重

## 使用方法

字体已自动应用到整个网站，无需额外配置。如果需要在特定元素中使用，可以通过CSS变量：

```css
.custom-element {
  font-family: var(--font-family-base);
  font-weight: var(--font-weight-normal); /* 400 */
  font-weight: var(--font-weight-bold);   /* 700 */
}
```

## 字体回退方案

如果阿里巴巴普惠体无法加载，字体将回退到以下字体：

1. PingFang SC (macOS)
2. Microsoft YaHei (Windows)
3. Helvetica Neue
4. 其他系统默认中文字体

## 下载字体文件

如果需要下载完整的字体文件，可以运行：

```bash
cd src/assets/fonts
node download-fonts.js
```

## 文件结构

```
fonts/
├── fonts.css              # 字体配置文件
├── download-fonts.js      # 字体下载脚本
├── README.md              # 说明文档
└── AlibabaPuHuiTi-*.woff2 # 字体文件（下载后）
```

## 特性

- 优秀的中文字体显示效果
- 支持多种字重
- 自动回退机制
- 优化的加载性能 (font-display: swap)
- 完整的字符集支持