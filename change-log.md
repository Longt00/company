## CompanyAdvantagesSection.vue
- `.advantages-grid` 改为 `flex` 布局，添加 `justify-content: space-between`、`flex-wrap: nowrap`、`gap: 24px`，实现横向等宽自适应卡片。
- `.advantage-block` 新增 `flex: 1 1 0` 与 `min-width: 0`，保证各卡片均分空间且内容不溢出。
- `.icon-container` 调整底部间距至 16px，并为内部 `svg` 增加 `clamp` 尺寸规则；`.block-title`、`.block-description` 改用 `clamp` 字号同时略增最大宽度。
- 删除 1024px 的 `grid` 响应式段落；768px/480px 档针对 padding 与 `gap` 做精简，移除冗余字号覆盖。

## CompanyInfoSection.vue
- 模板背景区域新增 `<div class="scissors-banner">`，内部懒加载剪刀量尺装饰图。
- 逻辑层引入 `headerScissors` 资源并在 `data` 中暴露 `scissorsImage` 供模板绑定。
- `.background-image-container` 改为 `100vw` 出血深色背景的容器，新增 `.scissors-banner`、`.scissors-decoration` 及阴影渐变，形成独立横幅。
- 768px/480px 断点为背景容器增加水平内边距，`scissors-banner` 获得圆角与不同最小高度以适配移动端。

## Footer.vue
- Footer 根容器 `min-height` 提升至 320px，改用 `padding-top: 60px` 与底部对齐的内容布局。
- `footer-content` 设为 100% 宽，内部内容块整体上移（`transform: translateY(-100px)`）配合新的背景高度。
- 联系方式行允许换行并居中排列，增强窄屏可读性。

## ImageCarousel.vue
- `.carousel-container` 拓展为 `100vw` 全宽区块，两侧出血，使用 `flex` 居中以保证背景无缝。
- `.carousel` 移除固定像素尺寸，改为 `width: 100%` + `aspect-ratio: 16 / 9` 的伸缩布局，并以 `flex` 纵向排列内部元素。
- 删除各断点对 `.carousel` 的手动高度设置，依赖比例自动缩放。

## ProductsPage.vue
- `.b2b-marketplace-container` 使用 `min(96vw, 1800px)` 宽度及 `clamp` 内边距，兼顾大屏与小屏。
- `.marketplace-content` 网格列宽改为 `minmax` 组合并 `align-items: start`，在多个断点调整列占比以支撑粘性导航。
- `.category-navigation` 及内部标题、按钮、文字普遍使用 `clamp` 控制尺寸，并新增 `position: sticky; top: 100px`。
- 热门产品模块（标题、列表、按钮、分页）统一 `clamp` 的 `gap`、`padding`、字号与图片尺寸，提升一致性。
- 商品网格在所有断点改成 `repeat(..., minmax(0, 1fr))` 防止溢出；新增 600px 断点；768px 以下保持左右并排，至 600px/480px 时再降级为单列产品。
- 其他按钮与分页组件的 spacing、字号同步更新，保持跨设备节奏。

## 新增资源
- `src/images/background/background-head.jpg`
- `src/images/background/background-head1.jpg`
- `src/images/background/background-head2.jpg`
- `src/images/background/header-scissors.png`
- `src/images/background/header-scissors1.jpg`

其中 `header-scissors.png` 已在 `CompanyInfoSection` 中启用，其余背景图可用于后续替换或扩展。

