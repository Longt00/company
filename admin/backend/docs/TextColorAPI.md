# 字体颜色管理 API 文档

## 概述

本文档描述了主页背景管理系统中字体颜色自动调整功能的 API 接口。该功能支持根据背景色自动计算最优字体颜色，确保文字在各种背景下都具有良好的可读性和对比度。

## 基础路径

```
/api/homepage/background
```

## 功能特性

- 🎨 自动计算最优字体颜色
- 📊 WCAG 2.1 对比度合规性检查
- 🎯 支持纯色和渐变背景
- ⚡ 批量处理支持
- 🔧 灵活的字体颜色配置

## API 接口

### 1. 设置字体颜色配置

**接口地址：** `PUT /{id}/text-color`

**请求参数：**
```json
{
  "autoTextColor": true,
  "primaryTextColor": "#000000",
  "secondaryTextColor": "#666666",
  "linkColor": "#1976d2",
  "accentColor": "#ff6a00",
  "remark": "字体颜色配置备注"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "字体颜色设置成功",
  "data": {
    "backgroundId": 1,
    "autoTextColor": true,
    "primaryTextColor": "#000000",
    "secondaryTextColor": "#666666",
    "linkColor": "#1976d2",
    "accentColor": "#ff6a00",
    "backgroundType": "solid",
    "backgroundColor": "#ffffff",
    "primaryContrast": 21.0,
    "secondaryContrast": 7.0,
    "linkContrast": 4.5,
    "wcagCompliance": {
      "primaryAA": true,
      "primaryAAA": true,
      "secondaryAA": true,
      "secondaryAAA": true,
      "linkAA": true,
      "linkAAA": false,
      "overallAA": true,
      "overallAAA": false
    },
    "updateTime": "2024-01-15 10:30:00"
  }
}
```

### 2. 获取字体颜色配置

**接口地址：** `GET /{id}/text-color`

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "backgroundId": 1,
    "autoTextColor": true,
    "primaryTextColor": "#000000",
    "secondaryTextColor": "#666666",
    "linkColor": "#1976d2",
    "accentColor": "#ff6a00",
    "backgroundType": "solid",
    "backgroundColor": "#ffffff",
    "primaryContrast": 21.0,
    "secondaryContrast": 7.0,
    "linkContrast": 4.5,
    "wcagCompliance": {
      "primaryAA": true,
      "primaryAAA": true,
      "secondaryAA": true,
      "secondaryAAA": true,
      "linkAA": true,
      "linkAAA": false,
      "overallAA": true,
      "overallAAA": false
    },
    "updateTime": "2024-01-15 10:30:00"
  }
}
```

### 3. 自动计算字体颜色

**接口地址：** `POST /{id}/auto-text-color`

**功能描述：** 根据背景色自动计算并设置最优的字体颜色

**响应示例：**
```json
{
  "code": 200,
  "message": "自动计算字体颜色成功",
  "data": {
    "backgroundId": 1,
    "autoTextColor": true,
    "primaryTextColor": "#000000",
    "secondaryTextColor": "#666666",
    "linkColor": "#1976d2",
    "accentColor": "#ff6a00",
    "backgroundType": "gradient",
    "gradientStartColor": "#667eea",
    "gradientEndColor": "#764ba2",
    "primaryContrast": 8.5,
    "secondaryContrast": 4.2,
    "linkContrast": 3.8,
    "wcagCompliance": {
      "primaryAA": true,
      "primaryAAA": true,
      "secondaryAA": true,
      "secondaryAAA": false,
      "linkAA": true,
      "linkAAA": false,
      "overallAA": true,
      "overallAAA": false
    },
    "updateTime": "2024-01-15 10:35:00"
  }
}
```

### 4. 批量自动计算字体颜色

**接口地址：** `POST /batch/auto-text-color`

**功能描述：** 批量为所有背景配置自动计算字体颜色

**响应示例：**
```json
{
  "code": 200,
  "message": "批量自动计算字体颜色成功",
  "data": [
    {
      "backgroundId": 1,
      "autoTextColor": true,
      "primaryTextColor": "#000000",
      "primaryContrast": 21.0,
      "wcagCompliance": {
        "overallAA": true,
        "overallAAA": true
      }
    },
    {
      "backgroundId": 2,
      "autoTextColor": true,
      "primaryTextColor": "#ffffff",
      "primaryContrast": 15.2,
      "wcagCompliance": {
        "overallAA": true,
        "overallAAA": true
      }
    }
  ]
}
```

### 5. 验证颜色对比度

**接口地址：** `POST /validate-contrast`

**请求参数：**
- `textColor`: 文字颜色（十六进制格式）
- `backgroundColor`: 背景颜色（十六进制格式）
- `wcagLevel`: WCAG 等级（AA/AAA，默认 AA）
- `textSize`: 文字大小（normal/large，默认 normal）

**示例请求：**
```
POST /api/homepage/background/validate-contrast?textColor=#000000&backgroundColor=#ffffff&wcagLevel=AA&textSize=normal
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "isValid": true,
    "contrast": 21.0,
    "wcagLevel": "AA",
    "textSize": "normal",
    "recommendation": "#000000"
  }
}
```

### 6. 获取推荐字体颜色

**接口地址：** `GET /recommended-colors/{backgroundColor}`

**路径参数：**
- `backgroundColor`: 背景颜色（十六进制格式，如 #ffffff）

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "backgroundColor": "#ffffff",
    "primaryTextColor": "#000000",
    "secondaryTextColor": "#666666",
    "linkColor": "#1976d2",
    "accentColor": "#ff6a00",
    "primaryContrast": 21.0,
    "secondaryContrast": 7.0,
    "linkContrast": 4.5
  }
}
```

## 数据模型

### TextColorRequest

```json
{
  "autoTextColor": "boolean - 是否自动计算字体颜色",
  "primaryTextColor": "string - 主要文本颜色（当autoTextColor为false时使用）",
  "secondaryTextColor": "string - 次要文本颜色",
  "linkColor": "string - 链接颜色",
  "accentColor": "string - 强调色",
  "remark": "string - 备注"
}
```

### TextColorResponse

```json
{
  "backgroundId": "long - 背景配置ID",
  "autoTextColor": "boolean - 是否自动计算字体颜色",
  "primaryTextColor": "string - 主要文本颜色",
  "secondaryTextColor": "string - 次要文本颜色",
  "linkColor": "string - 链接颜色",
  "accentColor": "string - 强调色",
  "backgroundType": "string - 背景类型",
  "backgroundColor": "string - 背景颜色",
  "gradientStartColor": "string - 渐变起始颜色",
  "gradientEndColor": "string - 渐变结束颜色",
  "primaryContrast": "double - 主要文本对比度",
  "secondaryContrast": "double - 次要文本对比度",
  "linkContrast": "double - 链接对比度",
  "wcagCompliance": {
    "primaryAA": "boolean - 主要文本AA级合规",
    "primaryAAA": "boolean - 主要文本AAA级合规",
    "secondaryAA": "boolean - 次要文本AA级合规",
    "secondaryAAA": "boolean - 次要文本AAA级合规",
    "linkAA": "boolean - 链接AA级合规",
    "linkAAA": "boolean - 链接AAA级合规",
    "overallAA": "boolean - 整体AA级合规",
    "overallAAA": "boolean - 整体AAA级合规"
  },
  "updateTime": "datetime - 更新时间"
}
```

## WCAG 合规性标准

### 对比度要求

- **AA 级标准：**
  - 普通文本：≥ 4.5:1
  - 大号文本（18pt+ 或 14pt 粗体+）：≥ 3.0:1

- **AAA 级标准：**
  - 普通文本：≥ 7.0:1
  - 大号文本：≥ 4.5:1

### 颜色格式要求

- 使用十六进制格式：`#RRGGBB` 或 `#RGB`
- 不支持颜色名称（如 red, blue）
- 区分大小写：推荐使用小写

## 使用示例

### 前端集成示例

```javascript
// 自动计算字体颜色
async function calculateTextColor(backgroundId) {
  try {
    const response = await fetch(`/api/homepage/background/${backgroundId}/auto-text-color`, {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer your-token',
        'Content-Type': 'application/json'
      }
    });

    const result = await response.json();
    if (result.code === 200) {
      applyTextColorScheme(result.data);
    }
  } catch (error) {
    console.error('计算字体颜色失败:', error);
  }
}

// 应用颜色方案
function applyTextColorScheme(scheme) {
  document.documentElement.style.setProperty('--color-text-auto-primary', scheme.primaryTextColor);
  document.documentElement.style.setProperty('--color-text-auto-secondary', scheme.secondaryTextColor);
  document.documentElement.style.setProperty('--color-text-auto-link', scheme.linkColor);
  document.documentElement.style.setProperty('--color-text-auto-accent', scheme.accentColor);
}
```

### 批量处理示例

```javascript
// 批量更新所有背景的字体颜色
async function batchUpdateTextColors() {
  try {
    const response = await fetch('/api/homepage/background/batch/auto-text-color', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer your-token',
        'Content-Type': 'application/json'
      }
    });

    const result = await response.json();
    console.log('批量更新完成:', result.data);
  } catch (error) {
    console.error('批量更新失败:', error);
  }
}
```

## 错误处理

### 常见错误码

- **400**: 请求参数错误
  - 无效的颜色格式
  - 必填参数缺失

- **404**: 背景配置不存在

- **401**: 认证失败

- **403**: 权限不足

- **500**: 服务器内部错误

### 错误响应示例

```json
{
  "code": 400,
  "message": "无效的背景颜色格式，请使用十六进制格式（如 #ffffff）",
  "data": null
}
```

## 最佳实践

### 1. 自动计算优先
- 推荐启用 `autoTextColor: true`，让系统自动计算最优字体颜色
- 只在特殊需求下使用自定义字体颜色

### 2. 定期检查合规性
- 定期调用对比度验证接口
- 确保满足 WCAG 2.1 标准

### 3. 渐变背景处理
- 对于渐变背景，系统会自动计算中间色作为参考
- 确保渐变色之间有足够的对比度

### 4. 性能优化
- 使用批量处理接口减少请求次数
- 缓存计算结果避免重复计算

## 版本信息

- **当前版本**: v1.0
- **API 版本**: v1
- **最后更新**: 2024-01-15

## 技术支持

如有问题，请联系开发团队或查看项目文档。