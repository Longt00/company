# 产品视频功能 API 接口文档

## 基础信息

- **API版本**: v1.0
- **基础URL**: `http://localhost:33380`
- **认证方式**: JWT Token
- **内容类型**: `application/json` (除文件上传接口外)

---

## 1. 产品视频文件上传

### 接口地址
`POST /api/admin/upload/product/video`

### 功能描述
上传产品视频文件到服务器，返回视频文件的访问URL。

### 请求参数
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| file | File | FormData | 是 | 视频文件 |

### 支持的视频格式
- MP4 (推荐)
- AVI
- MOV
- WMV
- FLV
- WebM
- MKV
- M4V

### 文件大小限制
- 最大文件大小：150MB

### 请求示例 (FormData)
```
Content-Type: multipart/form-data

file: [视频文件]
```

### 响应示例
**成功响应 (200)**
```json
{
  "code": 200,
  "message": "产品视频上传成功",
  "data": {
    "url": "/uploads/videos/product/2025/11/13/example-video.mp4",
    "fileName": "example-video.mp4",
    "fileSize": "52428800"
  },
  "success": true,
  "timestamp": 1731513480000
}
```

**错误响应**
```json
{
  "code": 400,
  "message": "只支持mp4、avi、mov、wmv、flv、webm格式的视频文件",
  "data": null,
  "success": false,
  "timestamp": 1731513480000
}
```

```json
{
  "code": 400,
  "message": "视频文件大小不能超过150MB",
  "data": null,
  "success": false,
  "timestamp": 1731513480000
}
```

---

## 2. 关联视频到产品

### 接口地址
`POST /api/products/video`

### 功能描述
将已上传的视频文件关联到指定产品。

### 请求头
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### 请求体
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 产品ID |
| videoUrl | String | 是 | 视频文件URL（从文件上传接口获取） |
| videoTitle | String | 否 | 视频标题（最大200字符） |
| videoDescription | String | 否 | 视频描述（最大500字符） |

### 请求示例
```json
{
  "productId": 20,
  "videoUrl": "/uploads/videos/product/2025/11/13/example-video.mp4",
  "videoTitle": "产品展示视频",
  "videoDescription": "这是产品的详细介绍和演示视频"
}
```

### 响应示例
**成功响应 (200)**
```json
{
  "code": 200,
  "message": "视频关联成功",
  "data": {
    "id": 20,
    "productName": "测试产品",
    "productCode": "PROD001",
    "category": "电子产品",
    "description": "这是一个测试产品",
    "specifications": "规格说明",
    "mainImage": "/uploads/images/product/main.jpg",
    "productImages": [
      "/uploads/images/product/img1.jpg",
      "/uploads/images/product/img2.jpg"
    ],
    "videoPath": "/uploads/videos/product/2025/11/13/example-video.mp4",
    "price": 99.99,
    "marketPrice": 129.99,
    "stockQuantity": 100,
    "tags": ["热销", "新品"],
    "status": 1,
    "statusText": "已上架",
    "isFeatured": false,
    "sortOrder": 0,
    "seoKeywords": "关键词",
    "seoDescription": "SEO描述",
    "createTime": "2025-11-13T14:00:00",
    "updateTime": "2025-11-13T14:30:00",
    "createdBy": 1,
    "updatedBy": 1
  },
  "success": true,
  "timestamp": 1731513480000
}
```

**错误响应**
```json
{
  "code": 404,
  "message": "产品不存在",
  "data": null,
  "success": false,
  "timestamp": 1731513480000
}
```

```json
{
  "code": 400,
  "message": "视频URL不能为空",
  "data": null,
  "success": false,
  "timestamp": 1731513480000
}
```

---

## 3. 删除产品视频

### 接口地址
`DELETE /api/products/{productId}/video`

### 功能描述
删除指定产品的视频关联（仅删除数据库中的关联记录，不删除实际文件）。

### 请求头
```
Authorization: Bearer <JWT_TOKEN>
```

### 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 产品ID |

### 请求示例
```
DELETE /api/products/20/video
```

### 响应示例
**成功响应 (200)**
```json
{
  "code": 200,
  "message": "产品视频删除成功",
  "data": {
    "id": 20,
    "productName": "测试产品",
    "videoPath": null,
    "updateTime": "2025-11-13T15:00:00"
  },
  "success": true,
  "timestamp": 1731513480000
}
```

**错误响应**
```json
{
  "code": 404,
  "message": "产品不存在",
  "data": null,
  "success": false,
  "timestamp": 1731513480000
}
```

```json
{
  "code": 404,
  "message": "产品没有视频",
  "data": null,
  "success": false,
  "timestamp": 1731513480000
}
```

---

## 4. 获取产品详情（包含视频信息）

### 接口地址
`GET /api/products/{id}`

### 功能描述
获取产品详细信息，包含产品关联的视频路径。

### 请求头
```
Authorization: Bearer <JWT_TOKEN>
```

### 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 产品ID |

### 请求示例
```
GET /api/products/20
```

### 响应示例
**成功响应 (200)**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 20,
    "productName": "测试产品",
    "productCode": "PROD001",
    "category": "电子产品",
    "description": "这是一个测试产品",
    "specifications": "规格说明",
    "mainImage": "/uploads/images/product/main.jpg",
    "productImages": [
      "/uploads/images/product/img1.jpg",
      "/uploads/images/product/img2.jpg"
    ],
    "videoPath": "/uploads/videos/product/2025/11/13/example-video.mp4",
    "price": 99.99,
    "marketPrice": 129.99,
    "stockQuantity": 100,
    "tags": ["热销", "新品"],
    "status": 1,
    "statusText": "已上架",
    "isFeatured": false,
    "sortOrder": 0,
    "seoKeywords": "关键词",
    "seoDescription": "SEO描述",
    "createTime": "2025-11-13T14:00:00",
    "updateTime": "2025-11-13T14:30:00",
    "createdBy": 1,
    "updatedBy": 1
  },
  "success": true,
  "timestamp": 1731513480000
}
```

**说明**
- `videoPath` 字段为 `null` 表示该产品没有关联视频
- `videoPath` 字段有值表示该产品有关联的视频，可直接用于视频播放

---

## 5. 更新产品信息（包含视频路径）

### 接口地址
`PUT /api/products/{id}`

### 功能描述
更新产品信息，支持同时更新视频路径。

### 请求头
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 产品ID |

### 请求体
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productName | String | 是 | 产品名称（最大200字符） |
| productCode | String | 否 | 产品编码（最大100字符，唯一） |
| category | String | 否 | 产品分类（最大100字符） |
| description | String | 否 | 产品描述（最大2000字符） |
| specifications | String | 否 | 产品规格（最大500字符） |
| mainImage | String | 否 | 主图URL（最大500字符） |
| productImages | String | 否 | 产品图片列表JSON格式 |
| **videoPath** | **String** | **否** | **视频路径（最大500字符）** |
| price | BigDecimal | 否 | 产品价格 |
| marketPrice | BigDecimal | 否 | 市场价格 |
| stockQuantity | Integer | 否 | 库存数量 |
| tags | List<String> | 否 | 产品标签列表 |
| status | Integer | 否 | 产品状态（0-下架 1-上架 2-草稿） |
| isFeatured | Boolean | 否 | 是否推荐 |
| sortOrder | Integer | 否 | 排序值 |
| seoKeywords | String | 否 | SEO关键词（最大200字符） |
| seoDescription | String | 否 | SEO描述（最大500字符） |

### 请求示例
```json
{
  "productName": "更新后的产品名称",
  "productCode": "PROD001",
  "category": "电子产品",
  "description": "更新后的产品描述",
  "price": 199.99,
  "videoPath": "/uploads/videos/product/2025/11/13/new-video.mp4",
  "status": 1
}
```

### 响应示例
**成功响应 (200)**
```json
{
  "code": 200,
  "message": "产品更新成功",
  "data": {
    "id": 20,
    "productName": "更新后的产品名称",
    "videoPath": "/uploads/videos/product/2025/11/13/new-video.mp4",
    "updateTime": "2025-11-13T16:00:00"
  },
  "success": true,
  "timestamp": 1731513480000
}
```

---

## 6. 创建产品（包含视频路径）

### 接口地址
`POST /api/products`

### 功能描述
创建新产品，支持直接指定视频路径。

### 请求头
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### 请求体
与更新产品接口相同，所有字段均支持，包括 `videoPath` 字段。

### 请求示例
```json
{
  "productName": "新产品",
  "productCode": "NEW001",
  "category": "电子产品",
  "description": "这是一个新产品",
  "price": 299.99,
  "videoPath": "/uploads/videos/product/2025/11/13/product-video.mp4",
  "status": 1
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 413 | 文件过大 |
| 415 | 不支持的文件类型 |
| 500 | 服务器内部错误 |

---

## 使用流程示例

### 完整的产品视频上传流程

1. **上传视频文件**
```bash
curl -X POST "http://localhost:33380/api/admin/upload/product/video" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/video.mp4"
```

2. **关联视频到产品**
```bash
curl -X POST "http://localhost:33380/api/products/video" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 20,
    "videoUrl": "/uploads/videos/product/2025/11/13/video.mp4",
    "videoTitle": "产品展示视频"
  }'
```

3. **获取产品详情验证**
```bash
curl -X GET "http://localhost:33380/api/products/20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 前端集成建议

### JavaScript 示例代码

```javascript
// 1. 上传视频文件
async function uploadVideoFile(file) {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch('/api/admin/upload/product/video', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${getAuthToken()}`
    },
    body: formData
  });

  const result = await response.json();
  if (result.success) {
    return result.data.url; // 返回视频URL
  } else {
    throw new Error(result.message);
  }
}

// 2. 关联视频到产品
async function attachVideoToProduct(productId, videoUrl, title, description) {
  const response = await fetch('/api/products/video', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${getAuthToken()}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      productId: productId,
      videoUrl: videoUrl,
      videoTitle: title,
      videoDescription: description
    })
  });

  return await response.json();
}

// 3. 完整的上传流程
async function uploadProductVideo(productId, file, title, description) {
  try {
    // 上传文件
    const videoUrl = await uploadVideoFile(file);

    // 关联到产品
    const result = await attachVideoToProduct(productId, videoUrl, title, description);

    if (result.success) {
      console.log('视频上传并关联成功');
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('视频上传失败:', error);
    throw error;
  }
}

// 4. 在产品详情页展示视频
function renderProductVideo(product) {
  if (product.videoPath) {
    return `
      <div class="product-video">
        <h3>产品视频</h3>
        <video controls width="100%" style="max-width: 800px;">
          <source src="${product.videoPath}" type="video/mp4">
          您的浏览器不支持视频播放。
        </video>
      </div>
    `;
  }
  return '';
}
```

### HTML 视频播放器示例

```html
<div class="product-detail">
  <!-- 产品图片展示 -->
  <div class="product-images">
    <img src="${product.mainImage}" alt="${product.productName}">
  </div>

  <!-- 产品视频展示 -->
  ${product.videoPath ? `
  <div class="product-video">
    <h3>产品展示视频</h3>
    <video controls poster="${product.mainImage}" width="100%">
      <source src="${product.videoPath}" type="video/mp4">
      <source src="${product.videoPath}" type="video/webm">
      您的浏览器不支持视频播放。
    </video>
  </div>
  ` : ''}

  <!-- 其他产品信息 -->
  <div class="product-info">
    <h2>${product.productName}</h2>
    <p>${product.description}</p>
    <p class="price">¥${product.price}</p>
  </div>
</div>
```

---

## 注意事项

1. **文件安全**: 上传的视频文件会进行格式和大小验证
2. **存储路径**: 视频文件按日期自动组织存储目录
3. **URL格式**: 返回的视频URL是相对路径，需要拼接完整域名使用
4. **删除操作**: 删除产品视频只会移除数据库关联，不会删除实际文件
5. **并发处理**: 同一产品只能关联一个视频，新视频会覆盖旧视频
6. **权限控制**: 所有视频相关操作都需要登录认证

---

## 更新日志

### v1.0 (2025-11-13)
- 初始版本发布
- 支持产品视频文件上传
- 支持视频与产品的关联管理
- 支持产品详情中视频信息的展示
- 提供完整的CRUD操作接口