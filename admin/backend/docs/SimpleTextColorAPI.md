# 简单文字颜色管理 API 文档

## 概述

本文档描述了简单的文字颜色管理功能 API。该功能允许为每个文字段落设置独立的颜色，支持多种预设主题和自定义颜色配置。

## 基础路径

```
/api/text-color
```

## 功能特性

- 🎨 独立的文字段落颜色控制
- 📚 多种预设主题（默认、深色、蓝色、绿色等）
- 🔧 灵活的自定义颜色配置
- 💾 本地存储支持
- 🎯 简单易用的 API 接口

## API 接口

### 1. 获取颜色配置

**接口地址：** `GET /config`

**响应示例：**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "primary": "#333333",
    "secondary": "#666666",
    "muted": "#999999",
    "link": "#1976d2",
    "accent": "#ff6a00",
    "success": "#28a745",
    "warning": "#ffc107",
    "danger": "#dc3545",
    "info": "#17a2b8"
  }
}
```

### 2. 设置颜色配置

**接口地址：** `POST /config`

**请求参数：**
```json
{
  "primary": "#333333",
  "secondary": "#666666",
  "muted": "#999999",
  "link": "#1976d2",
  "accent": "#ff6a00",
  "success": "#28a745",
  "warning": "#ffc107",
  "danger": "#dc3545",
  "info": "#17a2b8"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "颜色配置设置成功",
  "data": {
    "primary": "#333333",
    "secondary": "#666666",
    "muted": "#999999",
    "link": "#1976d2",
    "accent": "#ff6a00",
    "success": "#28a745",
    "warning": "#ffc107",
    "danger": "#dc3545",
    "info": "#17a2b8"
  }
}
```

### 3. 设置单个颜色

**接口地址：** `PUT /color/{colorType}`

**路径参数：**
- `colorType`: 颜色类型（primary, secondary, muted, link, accent, success, warning, danger, info）

**请求参数：**
```json
{
  "color": "#333333"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "颜色设置成功",
  "data": {
    "colorType": "primary",
    "color": "#333333"
  }
}
```

### 4. 获取预设主题列表

**接口地址：** `GET /presets`

**响应示例：**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "name": "默认",
      "colors": {
        "primary": "#333333",
        "secondary": "#666666",
        "muted": "#999999",
        "link": "#1976d2",
        "accent": "#ff6a00"
      }
    },
    {
      "name": "深色主题",
      "colors": {
        "primary": "#ffffff",
        "secondary": "#cccccc",
        "muted": "#888888",
        "link": "#64b5f6",
        "accent": "#ff9500"
      }
    }
  ]
}
```

### 5. 应用预设主题

**接口地址：** `POST /presets/{presetName}`

**路径参数：**
- `presetName`: 预设主题名称

**响应示例：**
```json
{
  "code": 200,
  "message": "预设主题应用成功",
  "data": {
    "presetName": "深色主题",
    "colors": {
      "primary": "#ffffff",
      "secondary": "#cccccc",
      "muted": "#888888",
      "link": "#64b5f6",
      "accent": "#ff9500"
    }
  }
}
```

### 6. 重置为默认颜色

**接口地址：** `POST /reset`

**响应示例：**
```json
{
  "code": 200,
  "message": "颜色重置成功",
  "data": {
    "primary": "#333333",
    "secondary": "#666666",
    "muted": "#999999",
    "link": "#1976d2",
    "accent": "#ff6a00"
  }
}
```

## 数据模型

### ColorConfig

```json
{
  "primary": "string - 主要文字颜色",
  "secondary": "string - 次要文字颜色",
  "muted": "string - 弱化文字颜色",
  "link": "string - 链接颜色",
  "accent": "string - 强调颜色",
  "success": "string - 成功颜色",
  "warning": "string - 警告颜色",
  "danger": "string - 危险颜色",
  "info": "string - 信息颜色"
}
```

### Preset

```json
{
  "name": "string - 预设主题名称",
  "colors": "ColorConfig - 颜色配置"
}
```

## 前端使用示例

### 基本使用

```javascript
// 获取颜色配置
async function getColorConfig() {
  try {
    const response = await fetch('/api/text-color/config');
    const result = await response.json();
    if (result.code === 200) {
      applyColors(result.data);
    }
  } catch (error) {
    console.error('获取颜色配置失败:', error);
  }
}

// 设置颜色配置
async function setColorConfig(colors) {
  try {
    const response = await fetch('/api/text-color/config', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(colors)
    });
    const result = await response.json();
    if (result.code === 200) {
      console.log('颜色配置设置成功');
    }
  } catch (error) {
    console.error('设置颜色配置失败:', error);
  }
}

// 应用颜色到页面
function applyColors(colors) {
  const root = document.documentElement;
  Object.keys(colors).forEach(key => {
    root.style.setProperty(`--color-${key}`, colors[key]);
  });
}
```

### 应用预设主题

```javascript
// 应用预设主题
async function applyPreset(presetName) {
  try {
    const response = await fetch(`/api/text-color/presets/${presetName}`, {
      method: 'POST'
    });
    const result = await response.json();
    if (result.code === 200) {
      applyColors(result.data.colors);
    }
  } catch (error) {
    console.error('应用预设主题失败:', error);
  }
}

// 使用示例
applyPreset('深色主题');
```

### 设置单个颜色

```javascript
// 设置主要文字颜色
async function setPrimaryColor(color) {
  try {
    const response = await fetch('/api/text-color/color/primary', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ color })
    });
    const result = await response.json();
    if (result.code === 200) {
      document.documentElement.style.setProperty('--color-primary', color);
    }
  } catch (error) {
    console.error('设置颜色失败:', error);
  }
}

// 使用示例
setPrimaryColor('#ff0000');
```

## CSS 类使用

### 基础颜色类

```html
<!-- 主要文字 -->
<p class="text-primary">这是主要文字</p>

<!-- 次要文字 -->
<p class="text-secondary">这是次要文字</p>

<!-- 弱化文字 -->
<p class="text-muted">这是弱化文字</p>

<!-- 链接 -->
<a href="#" class="text-link">链接文字</a>

<!-- 强调文字 -->
<p class="text-accent">这是强调文字</p>
```

### 组件颜色类

```html
<!-- 表单元素 -->
<input type="text" class="form-control text-primary">
<select class="form-control text-secondary"></select>

<!-- 表格 -->
<table class="table">
  <tr>
    <th class="text-primary">标题</th>
    <td class="text-secondary">内容</td>
  </tr>
</table>

<!-- 按钮 -->
<button class="btn text-primary">主要按钮</button>
<button class="btn text-accent">强调按钮</button>
```

### 主题类

```html
<!-- 应用主题 -->
<div class="theme-light">
  <p class="text-primary">浅色主题文字</p>
</div>

<div class="theme-dark">
  <p class="text-primary">深色主题文字</p>
</div>

<div class="theme-blue">
  <p class="text-primary">蓝色主题文字</p>
</div>
```

## 错误处理

### 常见错误码

- **400**: 请求参数错误
  - 无效的颜色格式
  - 必填参数缺失

- **404**: 预设主题不存在

- **422**: 颜色格式验证失败

- **500**: 服务器内部错误

### 错误响应示例

```json
{
  "code": 400,
  "message": "无效的颜色格式，请使用十六进制格式（如 #ffffff）",
  "data": null
}
```

## 最佳实践

### 1. 颜色格式
- 使用十六进制格式：`#RRGGBB` 或 `#RGB`
- 确保颜色具有良好的可读性
- 考虑无障碍设计要求

### 2. 性能优化
- 批量更新多个颜色
- 使用 CSS 变量避免重复计算
- 缓存颜色配置

### 3. 用户体验
- 提供预设主题供快速选择
- 支持颜色预览功能
- 保存用户的自定义配置

### 4. 兼容性
- 确保在所有目标浏览器中正常工作
- 提供回退方案
- 测试不同设备的显示效果

## 版本信息

- **当前版本**: v1.0
- **API 版本**: v1
- **最后更新**: 2024-01-15

## 技术支持

如有问题，请联系开发团队或查看项目文档。