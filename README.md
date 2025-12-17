# 企业网站 - Vue3 前端框架

这是一个基于Vue3技术栈构建的企业网站前端框架，仿照阿里巴巴企业展示页面设计。

## 项目特性

- 🚀 基于Vue3 Composition API
- 📱 完全响应式设计
- 🎨 现代化UI设计
- ⚡ Vite构建工具
- 🔧 组件化架构

## 项目结构

```
src/
├── components/          # Vue组件
│   ├── Header.vue       # 头部导航
│   ├── HeroSection.vue  # 主要展示区域
│   ├── AdvantagesSection.vue # 公司优势
│   ├── CompanyProfile.vue    # 公司简介
│   ├── ContactSection.vue    # 联系方式
│   └── Footer.vue       # 页脚
├── App.vue             # 主应用组件
├── main.js             # 应用入口
└── style.css           # 全局样式
```

## 安装和运行

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

项目将在 `http://localhost:3000` 运行

### 3. 构建生产版本

```bash
npm run build
```

## 主要功能模块

### 1. 头部导航 (Header)
- 企业Logo和名称
- 主导航菜单
- 联系供应商按钮

### 2. 主要展示区域 (HeroSection)
- 企业核心价值展示
- 产品特色介绍
- 行动号召按钮

### 3. 公司优势 (AdvantagesSection)
- 多厂合作优势
- 款式多样性
- 专业团队
- 最优价格
- 质量保证
- SGS认证

### 4. 公司简介 (CompanyProfile)
- 公司发展历程
- 统计数据展示
- 核心特色介绍

### 5. 联系方式 (ContactSection)
- 在线客服团队
- 联系信息
- 供应商验证信息

### 6. 页脚 (Footer)
- 公司信息
- 产品类别
- 服务特色
- 社交媒体链接

## 技术栈

- **Vue 3** - 渐进式JavaScript框架
- **Vite** - 快速构建工具
- **CSS3** - 现代样式设计
- **Font Awesome** - 图标库

## 响应式设计

项目采用移动优先的响应式设计，支持：
- 桌面端 (1200px+)
- 平板端 (768px - 1199px)
- 移动端 (< 768px)

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 开发说明

1. 所有组件都使用Vue3 Composition API编写
2. 样式采用scoped CSS，避免样式冲突
3. 使用CSS Grid和Flexbox进行布局
4. 支持现代浏览器特性

## 自定义配置

可以通过修改以下文件来自定义项目：

- `src/style.css` - 全局样式
- `src/components/*.vue` - 各组件样式
- `vite.config.js` - 构建配置

## 部署

构建完成后，将 `dist` 目录部署到任何静态文件服务器即可。

## 许可证

MIT License







