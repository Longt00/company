# 管理后台系统

基于 Vue3 + Bootstrap 5 开发的企业管理后台前端项目。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vue Router 4** - 官方路由管理器
- **Bootstrap 5** - 响应式UI组件库
- **Bootstrap Icons** - 图标库
- **Vite** - 新一代前端构建工具
- **阿里巴巴普惠体** - 字体

## 项目结构

```
admin/
├── public/              # 静态资源
├── src/
│   ├── assets/          # 资源文件（CSS、图片等）
│   ├── components/      # 公共组件
│   │   └── Layout.vue   # 管理后台布局组件
│   ├── router/          # 路由配置
│   │   └── index.js     # 路由定义
│   ├── views/           # 页面组件
│   │   ├── Login.vue    # 登录页
│   │   └── Dashboard.vue # 仪表盘
│   ├── App.vue          # 根组件
│   └── main.js          # 入口文件
├── index.html           # HTML模板
├── package.json         # 项目依赖
└── vite.config.js       # Vite配置
```

## 快速开始

### 安装依赖

```bash
cd admin
npm install
```

### 开发模式

```bash
npm run dev
```

访问 http://localhost:3000 即可查看项目。

### 生产构建

```bash
npm run build
```

### 预览构建结果

```bash
npm run preview
```

## 功能特性

### 已实现

- ✅ 用户登录界面
- ✅ 管理后台布局（侧边栏 + 顶部导航）
- ✅ 侧边栏折叠功能
- ✅ 路由守卫（登录验证）
- ✅ 仪表盘统计展示
- ✅ 响应式布局

### 待开发

- ⏳ 图片管理模块
- ⏳ 视频管理模块
- ⏳ 产品管理模块
- ⏳ 公司信息管理模块
- ⏳ 与后端API对接

## 登录说明

当前为演示版本，输入任意用户名和密码即可登录。

待后端接口完成后，需要修改 `src/views/Login.vue` 中的登录逻辑：

```javascript
// TODO: 替换为实际的API调用
const response = await axios.post('/api/login', loginForm.value)
```

## 样式说明

- 使用阿里巴巴普惠体作为全局字体
- Bootstrap 5 作为UI框架
- 自定义颜色主题为紫色渐变系
- 支持响应式设计，适配各种屏幕尺寸

## 注意事项

1. 确保 Node.js 版本 >= 16
2. 首次运行需要先执行 `npm install` 安装依赖
3. 开发时修改代码会自动热更新
4. 需要配合后端API使用，目前登录为模拟数据

## 下一步计划

根据项目计划，本周需要完成：
- 图片上传管理功能页面开发

## 联系方式

如有问题，请联系开发团队。