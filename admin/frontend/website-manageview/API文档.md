# Admin产品管理系统API文档

## 概述

本文档描述了Admin产品管理系统与前端的API接口规范，包括产品管理相关的所有接口定义、请求格式、响应格式以及集成示例。

## 基础信息

- **Base URL**: `http://localhost:33380`
- **认证方式**: JWT Token
- **数据格式**: JSON
- **字符编码**: UTF-8

## 认证机制

### JWT Token认证

所有需要认证的接口都需要在请求头中携带JWT Token：

```http
Authorization: Bearer <your-jwt-token>
```

### 登录获取Token

**POST** `/api/auth/login`

**请求参数：**
```json
{
  "username": "admin",
  "password": "password"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com"
    }
  }
}
```

## 产品管理接口

### 1. 获取产品列表

**GET** `/api/products`

**查询参数：**
- `page` (可选): 页码，默认为0
- `size` (可选): 每页数量，默认为10
- `sort` (可选): 排序字段，默认为id
- `direction` (可选): 排序方向，asc/desc，默认为desc

**请求示例：**
```http
GET /api/products?page=0&size=10&sort=id&direction=desc
```

**响应示例：**
```json
{
  "code": 200,
  "message": "获取产品列表成功",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "时尚牛仔裤",
        "description": "舒适修身的牛仔裤",
        "price": 299.00,
        "originalPrice": 399.00,
        "stock": 100,
        "categoryId": 1,
        "categoryName": "女装",
        "status": "ACTIVE",
        "tags": "时尚,女装,牛仔裤",
        "featured": false,
        "featuredImage": "http://example.com/image1.jpg",
        "additionalImages": ["http://example.com/image2.jpg"],
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
      }
    ],
    "totalElements": 50,
    "totalPages": 5,
    "size": 10,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

### 2. 获取单个产品详情

**GET** `/api/products/{id}`

**路径参数：**
- `id`: 产品ID

**响应示例：**
```json
{
  "code": 200,
  "message": "获取产品详情成功",
  "data": {
    "id": 1,
    "name": "时尚牛仔裤",
    "description": "舒适修身的牛仔裤，采用高品质面料制作",
    "details": "<p>产品详细介绍...</p>",
    "price": 299.00,
    "originalPrice": 399.00,
    "costPrice": 150.00,
    "stock": 100,
    "minStock": 10,
    "maxStock": 1000,
    "weight": 0.5,
    "dimensions": {
      "length": 100,
      "width": 50,
      "height": 10
    },
    "categoryId": 1,
    "categoryName": "女装",
    "brandId": 1,
    "brandName": "时尚品牌",
    "supplierId": 1,
    "supplierName": "优质供应商",
    "sku": "JEANS-001",
    "barcode": "1234567890123",
    "status": "ACTIVE",
    "tags": "时尚,女装,牛仔裤",
    "featured": false,
    "featuredImage": "http://example.com/image1.jpg",
    "additionalImages": ["http://example.com/image2.jpg", "http://example.com/image3.jpg"],
    "specifications": {
      "material": "纯棉",
      "color": "蓝色",
      "size": "M"
    },
    "seoTitle": "时尚牛仔裤 - 女装",
    "seoKeywords": "牛仔裤,女装,时尚",
    "seoDescription": "高品质时尚牛仔裤",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### 3. 创建产品

**POST** `/api/products`

**请求头：**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求参数：**
```json
{
  "name": "时尚牛仔裤",
  "description": "舒适修身的牛仔裤",
  "details": "<p>产品详细介绍...</p>",
  "price": 299.00,
  "originalPrice": 399.00,
  "costPrice": 150.00,
  "stock": 100,
  "minStock": 10,
  "maxStock": 1000,
  "weight": 0.5,
  "length": 100,
  "width": 50,
  "height": 10,
  "categoryId": 1,
  "brandId": 1,
  "supplierId": 1,
  "sku": "JEANS-001",
  "barcode": "1234567890123",
  "status": "ACTIVE",
  "tags": ["时尚", "女装", "牛仔裤"],
  "featured": false,
  "featuredImage": "http://example.com/image1.jpg",
  "additionalImages": ["http://example.com/image2.jpg"],
  "specifications": {
    "material": "纯棉",
    "color": "蓝色",
    "size": "M"
  },
  "seoTitle": "时尚牛仔裤 - 女装",
  "seoKeywords": "牛仔裤,女装,时尚",
  "seoDescription": "高品质时尚牛仔裤"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "产品创建成功",
  "data": {
    "id": 1,
    "name": "时尚牛仔裤",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

### 4. 更新产品

**PUT** `/api/products/{id}`

**路径参数：**
- `id`: 产品ID

**请求头：**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求参数：** (同创建产品)

**响应示例：**
```json
{
  "code": 200,
  "message": "产品更新成功",
  "data": {
    "id": 1,
    "name": "时尚牛仔裤",
    "updatedAt": "2024-01-15T11:30:00"
  }
}
```

### 5. 删除产品

**DELETE** `/api/products/{id}`

**路径参数：**
- `id`: 产品ID

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "产品删除成功"
}
```

### 6. 批量操作产品

**PUT** `/api/products/batch`

**请求头：**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求参数：**
```json
{
  "action": "UPDATE_STATUS", // UPDATE_STATUS, DELETE
  "productIds": [1, 2, 3],
  "status": "INACTIVE" // 仅当action为UPDATE_STATUS时需要
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "批量操作成功",
  "data": {
    "updatedCount": 3
  }
}
```

## 产品分类接口

### 1. 获取分类列表

**GET** `/api/categories`

**响应示例：**
```json
{
  "code": 200,
  "message": "获取分类列表成功",
  "data": [
    {
      "id": 1,
      "name": "女装",
      "description": "女装分类",
      "parentId": null,
      "level": 1,
      "sortOrder": 1,
      "status": "ACTIVE",
      "children": [
        {
          "id": 11,
          "name": "裤子",
          "parentId": 1,
          "level": 2
        }
      ]
    }
  ]
}
```

## 品牌管理接口

### 1. 获取品牌列表

**GET** `/api/brands`

**响应示例：**
```json
{
  "code": 200,
  "message": "获取品牌列表成功",
  "data": [
    {
      "id": 1,
      "name": "时尚品牌",
      "description": "知名时尚品牌",
      "logo": "http://example.com/logo.jpg",
      "status": "ACTIVE"
    }
  ]
}
```

## 文件上传接口

### 1. 上传产品图片

**POST** `/api/admin/upload/product-image`

**请求头：**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**请求参数：**
- `file`: 图片文件

**响应示例：**
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://39.97.60.191:33380/api/files/products/image_20240115_103000.jpg",
    "filename": "image_20240115_103000.jpg",
    "size": 1024000
  }
}
```

### 2. 批量上传图片

**POST** `/api/admin/upload/product-images`

**请求头：**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**请求参数：**
- `files`: 多个图片文件

**响应示例：**
```json
{
  "code": 200,
  "message": "批量上传成功",
  "data": [
    {
      "url": "http://39.97.60.191:33380/api/files/products/image1.jpg",
      "filename": "image1.jpg"
    },
    {
      "url": "http://39.97.60.191:33380/api/files/products/image2.jpg",
      "filename": "image2.jpg"
    }
  ]
}
```

## 仪表盘统计接口

### 1. 获取产品统计

**GET** `/api/dashboard/product-stats`

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "获取统计成功",
  "data": {
    "totalProducts": 150,
    "activeProducts": 120,
    "inactiveProducts": 30,
    "lowStockProducts": 5,
    "outOfStockProducts": 2,
    "featuredProducts": 10,
    "recentAddedProducts": 8
  }
}
```

## 标签功能特殊说明

### 标签数据格式

**前端处理逻辑：**
1. **输入格式**: 逗号分隔的文本，如：`"时尚,女装,牛仔裤"`
2. **显示格式**: 输入时实时预览标签效果
3. **存储格式**: 数据库中存储为逗号分隔的文本字符串
4. **API格式**: 后端接口接收和返回数组格式，前端需转换

### 数据转换示例

**前端发送数据：**
```javascript
// 用户输入: "时尚,女装,牛仔裤"
const tagsText = "时尚,女装,牛仔裤";
const tagsArray = tagsText.split(',').map(tag => tag.trim());
// 发送到后端: ["时尚", "女装", "牛仔裤"]
```

**前端接收数据：**
```javascript
// 后端返回: ["时尚", "女装", "牛仔裤"]
const tagsArray = ["时尚", "女装", "牛仔裤"];
const tagsText = tagsArray.join(', ');
// 显示给用户: "时尚, 女装, 牛仔裤"
```

### Vue.js 组件实现示例

```vue
<template>
  <div class="tags-input">
    <input
      v-model="tagsText"
      @input="handleTagsInput"
      placeholder="请输入产品标签，多个标签用逗号分隔"
    />
    <div class="tags-preview" v-if="previewTags.length > 0">
      <span v-for="tag in previewTags" :key="tag" class="tag">
        {{ tag }}
      </span>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tagsText: '',
      previewTags: []
    }
  },
  methods: {
    handleTagsInput() {
      this.previewTags = this.tagsText
        .split(',')
        .map(tag => tag.trim())
        .filter(tag => tag);
    },

    getTagsArray() {
      return this.tagsText
        .split(',')
        .map(tag => tag.trim())
        .filter(tag => tag);
    },

    setTagsFromBackend(tags) {
      if (Array.isArray(tags)) {
        this.tagsText = tags.join(', ');
      } else if (typeof tags === 'string') {
        this.tagsText = tags;
      }
      this.handleTagsInput();
    }
  }
}
</script>
```

## 错误处理

### 标准错误响应格式

```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "errors": [
    {
      "field": "name",
      "message": "产品名称不能为空"
    }
  ]
}
```

### 常见错误码

- `200`: 成功
- `400`: 请求参数错误
- `401`: 未认证
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器内部错误

## 前端集成示例

### Axios 配置

```javascript
import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:33380',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 添加token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      // token过期，重新登录
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 产品管理API封装

```javascript
import api from './api';

export const productApi = {
  // 获取产品列表
  getProducts(params) {
    return api.get('/api/products', { params });
  },

  // 获取产品详情
  getProduct(id) {
    return api.get(`/api/products/${id}`);
  },

  // 创建产品
  createProduct(data) {
    // 标签数据处理
    const processedData = {
      ...data,
      tags: data.tagsText
        ? data.tagsText.split(',').map(tag => tag.trim()).filter(tag => tag)
        : []
    };
    return api.post('/api/products', processedData);
  },

  // 更新产品
  updateProduct(id, data) {
    // 标签数据处理
    const processedData = {
      ...data,
      tags: data.tagsText
        ? data.tagsText.split(',').map(tag => tag.trim()).filter(tag => tag)
        : []
    };
    return api.put(`/api/products/${id}`, processedData);
  },

  // 删除产品
  deleteProduct(id) {
    return api.delete(`/api/products/${id}`);
  },

  // 批量操作
  batchOperation(action, productIds, status) {
    return api.put('/api/products/batch', {
      action,
      productIds,
      status
    });
  },

  // 上传图片
  uploadImage(file) {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/api/admin/upload/product-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
};
```

## 测试用例

### 创建产品测试

```javascript
// 测试数据
const productData = {
  name: '测试产品',
  description: '这是一个测试产品',
  price: 99.99,
  stock: 100,
  categoryId: 1,
  tagsText: '测试,新品,推荐', // 前端格式
  status: 'ACTIVE'
};

// 调用API
productApi.createProduct(productData)
  .then(response => {
    console.log('产品创建成功:', response.data);
  })
  .catch(error => {
    console.error('创建失败:', error);
  });
```

### 标签功能测试

```javascript
// 测试标签转换
const testTagsConversion = {
  frontendInput: "时尚,女装,牛仔裤",
  expectedBackend: ["时尚", "女装", "牛仔裤"],
  expectedFrontend: "时尚, 女装, 牛仔裤"
};

// 验证转换逻辑
const tagsArray = testTagsConversion.frontendInput
  .split(',')
  .map(tag => tag.trim())
  .filter(tag => tag);

console.log('转换结果:', tagsArray);
console.log('是否匹配:', JSON.stringify(tagsArray) === JSON.stringify(testTagsConversion.expectedBackend));
```

## 部署配置

### 开发环境配置

```javascript
// .env.development
VITE_API_BASE_URL=http://localhost:33380
VITE_IMAGE_BASE_URL=http://39.97.60.191:33380
```

### 生产环境配置

```javascript
// .env.production
VITE_API_BASE_URL=https://api.yourdomain.com
VITE_IMAGE_BASE_URL=https://files.yourdomain.com
```

## 注意事项

1. **标签处理**: 务必在前端正确处理标签格式的转换
2. **图片上传**: 注意文件大小限制（150MB）
3. **认证过期**: 处理401错误时需要引导用户重新登录
4. **CORS配置**: 确保后端允许前端域名的跨域请求
5. **数据验证**: 前端应该进行基本的数据验证，但最终验证以服务端为准
6. **分页处理**: 大数据量时使用分页加载，避免一次性加载过多数据
7. **错误提示**: 提供用户友好的错误提示信息

## 更新日志

- **v1.0.0** (2024-01-15): 初始版本，包含产品管理核心功能
- **v1.1.0** (2024-01-16): 优化标签功能，支持逗号分隔文本格式
- **v1.2.0** (2024-01-17): 添加批量操作和文件上传功能