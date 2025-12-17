# Admin管理系统对接展示前端API文档

## 📋 目录
- [功能概述](#功能概述)
- [后端API信息](#后端api信息)
- [🔑 认证说明](#认证说明)
- [📋 完整接口列表](#完整接口列表)
- [图片管理接口](#图片管理接口)
- [视频管理接口](#视频管理接口)
- [公司信息接口](#公司信息接口)
- [前端对接示例代码](#前端对接示例代码)
- [数据结构说明](#数据结构说明)
- [常见问题FAQ](#常见问题faq)

---

## 功能概述

### Admin管理系统功能
本系统提供完整的后台管理功能，支持：

- **图片管理**：产品图片、公司Logo、资质证书等文件的上传、管理、预览和删除
- **视频管理**：企业宣传视频、产品展示视频的上传、管理和删除
- **公司信息管理**：企业基本信息、联系方式、团队介绍等的完整管理
- **权限控制**：基于JWT的安全认证机制

### 管理功能结构
```
Admin管理系统
├── 图片管理
│   ├── 产品图片管理
│   │   ├── 单图片上传
│   │   ├── 批量图片上传
│   │   ├── 图片预览和删除
│   │   └── 主图设置
│   ├── 公司Logo管理
│   ├── 资质证书管理
│   └── 团队头像管理
├── 视频管理
│   ├── 企业宣传视频
│   ├── 产品展示视频
│   └── 视频预览和删除
└── 公司信息管理
    ├── 基本信息
    ├── 联系方式
    ├── 团队介绍
    └── 资质展示
```

---

## 后端API信息

### 服务器地址
```
基础URL: http://localhost:33380
生产环境: http://39.97.60.191:33380
```

### 数据库连接信息
```properties
数据库地址: 39.97.60.191:33306
数据库名称: manage_system
用户名: root
密码: 123456
```

**⚠️ 重要：Admin管理接口需要JWT认证！**

---

## 🔑 认证说明

### 🔒 Admin管理接口需要JWT认证

与展示前端不同，Admin管理系统的所有接口都需要有效的JWT Token：

- ✅ **需要登录**
- ✅ **需要Token**
- ✅ **需要Authorization请求头**

### JWT Token格式
```http
Authorization: Bearer <your-jwt-token>
```

### 认证流程
1. 调用登录接口获取Token
2. 在后续请求中携带Token
3. Token过期后需要重新登录

---

## 📋 完整接口列表

### 快速索引

```javascript
// 🔐 认证相关
POST /api/auth/login                           // 管理员登录
POST /api/auth/register                        // 用户注册
GET  /api/auth/user-info                       // 获取用户信息

// 🖼️ 图片管理
POST /api/admin/upload/logo                    // 上传公司Logo
POST /api/admin/upload/image/enhanced          // 增强图片上传
POST /api/admin/upload/images/batch            // 批量图片上传
POST /api/admin/upload/product/image           // 上传产品图片
POST /api/admin/upload/product/main-image      // 上传产品主图
POST /api/admin/upload/product/images/batch    // 批量上传产品图片
DELETE /api/admin/upload/file                  // 删除文件
GET  /api/admin/upload/files/refresh          // 刷新文件列表

// 🎥 视频管理
POST /api/admin/upload/video                   // 上传企业视频
POST /api/admin/upload/video/enhanced          // 增强视频上传
POST /api/admin/upload/product/video           // 上传产品视频
DELETE /api/admin/products/{productId}/video   // 删除产品视频

// 🏢 公司信息管理
GET  /api/company/admin/info                   // 获取公司信息
PUT  /api/company/admin/info                   // 更新公司信息
POST /api/company/admin/info/init              // 初始化公司信息
GET  /api/company/admin/info/check             // 检查公司信息状态

// 📁 文件访问
GET  /api/files/**                             // 访问上传的文件
GET  /api/files/download/**                    // 下载文件
```

---

## 图片管理接口

### 1. 上传公司Logo

**接口地址：**
```
POST http://localhost:33380/api/admin/upload/logo
```

**请求头：**
```http
Authorization: Bearer <jwt-token>
Content-Type: multipart/form-data
```

**请求参数：**
- `file` - 图片文件（支持jpg, png, gif格式）

**请求示例：**
```javascript
const formData = new FormData()
formData.append('file', logoFile)

const response = await fetch('http://localhost:33380/api/admin/upload/logo', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
})
```

**响应示例：**
```json
{
  "code": 200,
  "message": "Logo上传成功",
  "data": {
    "fileName": "company-logo.png",
    "originalName": "my-logo.png",
    "fileSize": 256780,
    "fileType": "image/png",
    "uploadTime": "2025-11-15T22:30:00",
    "filePath": "/uploads/logo/2025/11/15/company-logo.png",
    "accessUrl": "http://localhost:33380/api/files/logo/2025/11/15/company-logo.png",
    "relativeUrl": "/api/files/logo/2025/11/15/company-logo.png"
  },
  "success": true,
  "timestamp": 1731653400000
}
```

---

### 2. 增强图片上传（支持压缩和水印）

**接口地址：**
```
POST http://localhost:33380/api/admin/upload/image/enhanced
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 图片文件 |
| quality | Integer | 否 | 压缩质量（1-100，默认85） |
| watermark | Boolean | 否 | 是否添加水印（默认false） |
| watermarkText | String | 否 | 水印文字 |
| category | String | 否 | 文件分类（如：product, company, team） |

**响应示例：**
```json
{
  "code": 200,
  "message": "图片上传并处理成功",
  "data": {
    "originalFile": {
      "fileName": "original-product.jpg",
      "fileSize": 2048576,
      "accessUrl": "http://localhost:33380/api/files/product/2025/11/15/original-product.jpg"
    },
    "processedFile": {
      "fileName": "processed-product.jpg",
      "fileSize": 512456,
      "accessUrl": "http://localhost:33380/api/files/product/2025/11/15/processed-product.jpg",
      "compressionRatio": 0.75
    },
    "thumbnailFile": {
      "fileName": "thumbnail-product.jpg",
      "fileSize": 25680,
      "accessUrl": "http://localhost:33380/api/files/product/2025/11/15/thumbnail-product.jpg"
    }
  },
  "success": true
}
```

---

### 3. 批量图片上传

**接口地址：**
```
POST http://localhost:33380/api/admin/upload/images/batch
```

**请求参数：**
- `files` - 图片文件数组
- `category` - 文件分类

**请求示例：**
```javascript
const formData = new FormData()
Array.from(files).forEach(file => {
  formData.append('files', file)
})
formData.append('category', 'product')

const response = await fetch('/api/admin/upload/images/batch', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
})
```

**响应示例：**
```json
{
  "code": 200,
  "message": "批量上传完成",
  "data": {
    "totalCount": 5,
    "successCount": 4,
    "failedCount": 1,
    "successFiles": [
      {
        "fileName": "product-1.jpg",
        "accessUrl": "http://localhost:33380/api/files/product/2025/11/15/product-1.jpg"
      },
      {
        "fileName": "product-2.jpg",
        "accessUrl": "http://localhost:33380/api/files/product/2025/11/15/product-2.jpg"
      }
    ],
    "failedFiles": [
      {
        "fileName": "invalid-file.txt",
        "error": "不支持的文件格式"
      }
    ]
  },
  "success": true
}
```

---

### 4. 上传产品图片

**接口地址：**
```
POST http://localhost:33380/api/admin/upload/product/image
```

**请求参数：**
- `file` - 图片文件
- `productId` - 产品ID（可选）

---

### 5. 删除文件

**接口地址：**
```
DELETE http://localhost:33380/api/admin/upload/file
```

**请求参数：**
- `filePath` - 文件路径
- `category` - 文件分类

**请求示例：**
```javascript
const response = await fetch('/api/admin/upload/file', {
  method: 'DELETE',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    filePath: '/uploads/product/2025/11/15/old-product.jpg',
    category: 'product'
  })
})
```

---

## 视频管理接口

### 1. 上传企业视频

**接口地址：**
```
POST http://localhost:33380/api/admin/upload/video
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 视频文件 |
| title | String | 否 | 视频标题 |
| description | String | 否 | 视频描述 |
| category | String | 否 | 视频分类 |

**支持格式：** mp4, avi, mov, wmv, flv

**响应示例：**
```json
{
  "code": 200,
  "message": "视频上传成功",
  "data": {
    "fileName": "company-intro.mp4",
    "originalName": "my-intro-video.mp4",
    "fileSize": 52428800,
    "duration": 120.5,
    "resolution": "1920x1080",
    "format": "mp4",
    "uploadTime": "2025-11-15T22:30:00",
    "filePath": "/uploads/videos/2025/11/15/company-intro.mp4",
    "accessUrl": "http://localhost:33380/api/files/videos/2025/11/15/company-intro.mp4",
    "thumbnailUrl": "http://localhost:33380/api/files/videos/2025/11/15/company-intro-thumbnail.jpg"
  },
  "success": true
}
```

---

### 2. 增强视频上传（支持转码和切片）

**接口地址：**
```
POST http://localhost:33380/api/admin/upload/video/enhanced
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 视频文件 |
| description | String | 否 | 视频描述 |
| quality | String | 否 | 转码质量（high, medium, low） |
| generateThumbnail | Boolean | 否 | 是否生成缩略图（默认true） |
| enableStreaming | Boolean | 否 | 是否启用流媒体（默认false） |

**响应示例：**
```json
{
  "code": 200,
  "message": "视频处理完成",
  "data": {
    "originalVideo": {
      "fileName": "original-demo.mp4",
      "fileSize": 104857600,
      "duration": 180.5,
      "accessUrl": "http://localhost:33380/api/files/videos/2025/11/15/original-demo.mp4"
    },
    "processedVideo": {
      "fileName": "processed-demo.mp4",
      "fileSize": 52428800,
      "duration": 180.5,
      "resolution": "1280x720",
      "accessUrl": "http://localhost:33380/api/files/videos/2025/11/15/processed-demo.mp4"
    },
    "thumbnail": {
      "fileName": "demo-thumbnail.jpg",
      "accessUrl": "http://localhost:33380/api/files/videos/2025/11/15/demo-thumbnail.jpg"
    },
    "streamingFiles": [
      {
        "resolution": "1080p",
        "url": "http://localhost:33380/api/files/videos/2025/11/15/demo-1080p.m3u8"
      },
      {
        "resolution": "720p",
        "url": "http://localhost:33380/api/files/videos/2025/11/15/demo-720p.m3u8"
      }
    ]
  },
  "success": true
}
```

---

### 3. 上传产品视频

**接口地址：**
```
POST http://localhost:33380/api/admin/upload/product/video
```

**请求参数：**
- `file` - 视频文件
- `productId` - 产品ID
- `title` - 视频标题
- `description` - 视频描述

---

### 4. 删除产品视频

**接口地址：**
```
DELETE http://localhost:33380/api/admin/products/{productId}/video
```

---

## 公司信息接口

### 1. 获取公司信息

**接口地址：**
```
GET http://localhost:33380/api/company/admin/info
```

**请求头：**
```http
Authorization: Bearer <jwt-token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 12,
    "companyName": "示例服装有限公司",
    "companyShortName": "示例服装",
    "companyDescription": "专业从事牛仔裤设计、生产和销售的企业",
    "companyLogo": "http://localhost:33380/api/files/logo/2025/11/15/company-logo.png",
    "companyVideo": "http://localhost:33380/api/files/videos/2025/11/15/company-intro.mp4",
    "establishDate": "2010-05-15",
    "registeredCapital": "1000万元",
    "legalRepresentative": "张三",
    "businessScope": "服装设计、生产、销售",
    "companyAddress": "广东省广州市天河区科技园区A座",
    "companyWebsite": "https://example.com",
    "companyPhone": "020-12345678",
    "companyEmail": "contact@example.com",
    "status": 1,
    "createTime": "2025-11-01T14:00:47",
    "updateTime": "2025-11-08T21:55:59"
  },
  "success": true,
  "timestamp": 1731653400000
}
```

---

### 2. 更新公司信息

**接口地址：**
```
PUT http://localhost:33380/api/company/admin/info
```

**请求参数：**
```json
{
  "companyName": "示例服装有限公司",
  "companyShortName": "示例服装",
  "companyDescription": "专业从事牛仔裤设计、生产和销售的企业",
  "companyLogo": "http://localhost:33380/api/files/logo/2025/11/15/new-logo.png",
  "establishDate": "2010-05-15",
  "registeredCapital": "1000万元",
  "legalRepresentative": "张三",
  "businessScope": "服装设计、生产、销售",
  "companyAddress": "广东省广州市天河区科技园区A座",
  "companyWebsite": "https://example.com",
  "companyPhone": "020-12345678",
  "companyEmail": "contact@example.com"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "公司信息更新成功",
  "data": {
    "id": 12,
    "companyName": "示例服装有限公司",
    "updateTime": "2025-11-15T22:35:00"
  },
  "success": true
}
```

---

### 3. 初始化公司信息

**接口地址：**
```
POST http://localhost:33380/api/company/admin/info/init
```

**请求参数：**
```json
{
  "companyName": "新公司名称",
  "companyDescription": "公司描述",
  "companyPhone": "联系电话",
  "companyEmail": "联系邮箱"
}
```

---

### 4. 检查公司信息状态

**接口地址：**
```
GET http://localhost:33380/api/company/admin/info/check
```

**响应示例：**
```json
{
  "code": 200,
  "message": "检查完成",
  "data": {
    "hasCompanyInfo": true,
    "isComplete": false,
    "missingFields": [
      "companyLogo",
      "companyVideo",
      "businessScope"
    ],
    "completionPercentage": 75
  },
  "success": true
}
```

---

## 前端对接示例代码

### Vue 3 + Composition API 完整示例

#### 1. Admin API 配置文件 (`src/api/admin.js`)
```javascript
import axios from 'axios'

// 创建API实例
const adminApiClient = axios.create({
  baseURL: 'http://localhost:33380',
  timeout: 60000, // 60秒，适应大文件上传
})

// 请求拦截器 - 添加JWT Token
adminApiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('adminToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
adminApiClient.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      throw new Error(res.message || '操作失败')
    }
  },
  error => {
    if (error.response?.status === 401) {
      // Token过期，跳转到登录页
      localStorage.removeItem('adminToken')
      window.location.href = '/admin/login'
    }
    throw error
  }
)

export default adminApiClient
```

#### 2. 图片管理API (`src/api/image.js`)
```javascript
import adminApiClient from './admin'

export const imageAPI = {
  // 上传Logo
  uploadLogo(file) {
    const formData = new FormData()
    formData.append('file', file)
    return adminApiClient.post('/api/admin/upload/logo', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // 增强图片上传
  uploadEnhanced(file, options = {}) {
    const formData = new FormData()
    formData.append('file', file)

    // 添加可选参数
    Object.keys(options).forEach(key => {
      if (options[key] !== undefined) {
        formData.append(key, options[key])
      }
    })

    return adminApiClient.post('/api/admin/upload/image/enhanced', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // 批量图片上传
  uploadBatch(files, category = 'product') {
    const formData = new FormData()
    Array.from(files).forEach(file => {
      formData.append('files', file)
    })
    formData.append('category', category)

    return adminApiClient.post('/api/admin/upload/images/batch', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // 上传产品图片
  uploadProductImage(file, productId) {
    const formData = new FormData()
    formData.append('file', file)
    if (productId) {
      formData.append('productId', productId)
    }

    return adminApiClient.post('/api/admin/upload/product/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // 删除文件
  deleteFile(filePath, category) {
    return adminApiClient.delete('/api/admin/upload/file', {
      data: { filePath, category }
    })
  },

  // 刷新文件列表
  refreshFiles(category) {
    return adminApiClient.get('/api/admin/upload/files/refresh', {
      params: { category }
    })
  }
}
```

#### 3. 视频管理API (`src/api/video.js`)
```javascript
import adminApiClient from './admin'

export const videoAPI = {
  // 上传企业视频
  uploadVideo(file, options = {}) {
    const formData = new FormData()
    formData.append('file', file)

    Object.keys(options).forEach(key => {
      if (options[key] !== undefined) {
        formData.append(key, options[key])
      }
    })

    return adminApiClient.post('/api/admin/upload/video', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // 增强视频上传
  uploadEnhancedVideo(file, options = {}) {
    const formData = new FormData()
    formData.append('file', file)

    Object.keys(options).forEach(key => {
      if (options[key] !== undefined) {
        formData.append(key, options[key])
      }
    })

    return adminApiClient.post('/api/admin/upload/video/enhanced', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // 上传产品视频
  uploadProductVideo(file, productId, options = {}) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('productId', productId)

    Object.keys(options).forEach(key => {
      if (options[key] !== undefined) {
        formData.append(key, options[key])
      }
    })

    return adminApiClient.post('/api/admin/upload/product/video', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // 删除产品视频
  deleteProductVideo(productId) {
    return adminApiClient.delete(`/api/admin/products/${productId}/video`)
  }
}
```

#### 4. 公司信息API (`src/api/company.js`)
```javascript
import adminApiClient from './admin'

export const companyAPI = {
  // 获取公司信息
  getCompanyInfo() {
    return adminApiClient.get('/api/company/admin/info')
  },

  // 更新公司信息
  updateCompanyInfo(companyData) {
    return adminApiClient.put('/api/company/admin/info', companyData)
  },

  // 初始化公司信息
  initCompanyInfo(companyData) {
    return adminApiClient.post('/api/company/admin/info/init', companyData)
  },

  // 检查公司信息状态
  checkCompanyInfo() {
    return adminApiClient.get('/api/company/admin/info/check')
  }
}
```

#### 5. 图片管理组件 (`src/components/ImageManager.vue`)
```vue
<template>
  <div class="image-manager">
    <div class="upload-section">
      <h3>图片上传</h3>

      <!-- Logo上传 -->
      <div class="upload-group">
        <h4>公司Logo</h4>
        <input
          type="file"
          @change="handleLogoUpload"
          accept="image/*"
          ref="logoInput"
        />
        <button @click="$refs.logoInput.click()" class="upload-btn">
          选择Logo
        </button>

        <div v-if="companyInfo.companyLogo" class="logo-preview">
          <img :src="companyInfo.companyLogo" alt="公司Logo" />
          <button @click="deleteLogo" class="delete-btn">删除</button>
        </div>
      </div>

      <!-- 批量图片上传 -->
      <div class="upload-group">
        <h4>批量图片上传</h4>
        <input
          type="file"
          @change="handleBatchUpload"
          multiple
          accept="image/*"
          ref="batchInput"
        />
        <button @click="$refs.batchInput.click()" class="upload-btn">
          选择多张图片
        </button>

        <div class="upload-options">
          <label>
            压缩质量：
            <input v-model="uploadOptions.quality" type="range" min="1" max="100" />
            {{ uploadOptions.quality }}%
          </label>
          <label>
            <input v-model="uploadOptions.watermark" type="checkbox" />
            添加水印
          </label>
        </div>
      </div>
    </div>

    <!-- 图片列表 -->
    <div class="image-list">
      <h3>已上传图片</h3>
      <div class="image-grid">
        <div v-for="image in images" :key="image.fileName" class="image-item">
          <img :src="image.accessUrl" :alt="image.fileName" />
          <div class="image-info">
            <p class="filename">{{ image.originalName }}</p>
            <p class="filesize">{{ formatFileSize(image.fileSize) }}</p>
          </div>
          <div class="image-actions">
            <button @click="previewImage(image)" class="preview-btn">预览</button>
            <button @click="deleteImage(image)" class="delete-btn">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 上传进度 -->
    <div v-if="uploading" class="upload-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <p>上传进度: {{ uploadProgress }}%</p>
    </div>

    <!-- 图片预览模态框 -->
    <div v-if="previewImage" class="image-modal" @click="closePreview">
      <div class="modal-content">
        <img :src="previewImage.accessUrl" :alt="previewImage.fileName" />
        <button @click="closePreview" class="close-btn">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { imageAPI } from '@/api/image'
import { companyAPI } from '@/api/company'

// 响应式数据
const companyInfo = ref({})
const images = ref([])
const uploading = ref(false)
const uploadProgress = ref(0)
const previewImage = ref(null)
const uploadOptions = ref({
  quality: 85,
  watermark: false,
  category: 'product'
})

// 获取公司信息
const loadCompanyInfo = async () => {
  try {
    const response = await companyAPI.getCompanyInfo()
    companyInfo.value = response.data
  } catch (error) {
    console.error('获取公司信息失败:', error)
  }
}

// Logo上传
const handleLogoUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    uploading.value = true
    const response = await imageAPI.uploadLogo(file)

    // 更新公司信息中的Logo
    companyInfo.value.companyLogo = response.data.accessUrl

    alert('Logo上传成功！')
  } catch (error) {
    alert('Logo上传失败: ' + error.message)
  } finally {
    uploading.value = false
  }
}

// 批量图片上传
const handleBatchUpload = async (event) => {
  const files = event.target.files
  if (!files.length) return

  try {
    uploading.value = true
    uploadProgress.value = 0

    // 模拟进度更新
    const progressInterval = setInterval(() => {
      uploadProgress.value = Math.min(uploadProgress.value + 10, 90)
    }, 200)

    const response = await imageAPI.uploadBatch(
      files,
      uploadOptions.value.category
    )

    clearInterval(progressInterval)
    uploadProgress.value = 100

    // 刷新图片列表
    await loadImages()

    alert(`批量上传完成！成功: ${response.data.successCount}, 失败: ${response.data.failedCount}`)
  } catch (error) {
    alert('批量上传失败: ' + error.message)
  } finally {
    uploading.value = false
    setTimeout(() => {
      uploadProgress.value = 0
    }, 1000)
  }
}

// 加载图片列表
const loadImages = async () => {
  try {
    const response = await imageAPI.refreshFiles(uploadOptions.value.category)
    images.value = response.data.files || []
  } catch (error) {
    console.error('加载图片列表失败:', error)
  }
}

// 删除Logo
const deleteLogo = async () => {
  if (!confirm('确定要删除公司Logo吗？')) return

  try {
    await imageAPI.deleteFile(companyInfo.value.companyLogo, 'logo')
    companyInfo.value.companyLogo = null
    alert('Logo删除成功！')
  } catch (error) {
    alert('Logo删除失败: ' + error.message)
  }
}

// 删除图片
const deleteImage = async (image) => {
  if (!confirm(`确定要删除图片 "${image.originalName}" 吗？`)) return

  try {
    await imageAPI.deleteFile(image.filePath, uploadOptions.value.category)
    await loadImages()
    alert('图片删除成功！')
  } catch (error) {
    alert('图片删除失败: ' + error.message)
  }
}

// 预览图片
const previewImage = (image) => {
  previewImage.value = image
}

// 关闭预览
const closePreview = () => {
  previewImage.value = null
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 初始化
onMounted(() => {
  loadCompanyInfo()
  loadImages()
})
</script>

<style scoped>
.image-manager {
  padding: 20px;
}

.upload-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.upload-group {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.upload-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  margin: 10px 0;
}

.upload-options {
  margin-top: 10px;
  display: flex;
  gap: 20px;
}

.logo-preview {
  margin-top: 10px;
}

.logo-preview img {
  max-width: 200px;
  max-height: 100px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.image-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

.image-item img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.image-info {
  padding: 10px;
}

.filename {
  font-weight: bold;
  margin: 0 0 5px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.filesize {
  color: #666;
  font-size: 12px;
  margin: 0;
}

.image-actions {
  padding: 10px;
  display: flex;
  gap: 10px;
}

.preview-btn, .delete-btn {
  padding: 5px 10px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
}

.preview-btn {
  background: #28a745;
  color: white;
}

.delete-btn {
  background: #dc3545;
  color: white;
}

.upload-progress {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  min-width: 300px;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background: #e9ecef;
  border-radius: 5px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: #007bff;
  transition: width 0.3s ease;
}

.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  max-width: 90%;
  max-height: 90%;
  position: relative;
}

.modal-content img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255,255,255,0.8);
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}
</style>
```

#### 6. 公司信息管理组件 (`src/components/CompanyInfoManager.vue`)
```vue
<template>
  <div class="company-info-manager">
    <h2>公司信息管理</h2>

    <div v-if="loading" class="loading">加载中...</div>

    <form v-else @submit.prevent="saveCompanyInfo" class="company-form">
      <!-- 基本信息 -->
      <div class="form-section">
        <h3>基本信息</h3>

        <div class="form-group">
          <label for="companyName">公司名称 *</label>
          <input
            id="companyName"
            v-model="companyInfo.companyName"
            type="text"
            required
          />
        </div>

        <div class="form-group">
          <label for="companyShortName">公司简称</label>
          <input
            id="companyShortName"
            v-model="companyInfo.companyShortName"
            type="text"
          />
        </div>

        <div class="form-group">
          <label for="companyDescription">公司描述</label>
          <textarea
            id="companyDescription"
            v-model="companyInfo.companyDescription"
            rows="4"
          ></textarea>
        </div>

        <div class="form-group">
          <label for="establishDate">成立日期</label>
          <input
            id="establishDate"
            v-model="companyInfo.establishDate"
            type="date"
          />
        </div>

        <div class="form-group">
          <label for="registeredCapital">注册资本</label>
          <input
            id="registeredCapital"
            v-model="companyInfo.registeredCapital"
            type="text"
          />
        </div>

        <div class="form-group">
          <label for="legalRepresentative">法定代表人</label>
          <input
            id="legalRepresentative"
            v-model="companyInfo.legalRepresentative"
            type="text"
          />
        </div>
      </div>

      <!-- 联系信息 -->
      <div class="form-section">
        <h3>联系信息</h3>

        <div class="form-group">
          <label for="companyAddress">公司地址</label>
          <input
            id="companyAddress"
            v-model="companyInfo.companyAddress"
            type="text"
          />
        </div>

        <div class="form-group">
          <label for="companyWebsite">公司网站</label>
          <input
            id="companyWebsite"
            v-model="companyInfo.companyWebsite"
            type="url"
          />
        </div>

        <div class="form-group">
          <label for="companyPhone">联系电话</label>
          <input
            id="companyPhone"
            v-model="companyInfo.companyPhone"
            type="tel"
          />
        </div>

        <div class="form-group">
          <label for="companyEmail">联系邮箱</label>
          <input
            id="companyEmail"
            v-model="companyInfo.companyEmail"
            type="email"
          />
        </div>
      </div>

      <!-- Logo和视频 -->
      <div class="form-section">
        <h3>Logo和视频</h3>

        <div class="form-group">
          <label>公司Logo</label>
          <div class="media-upload">
            <input
              type="file"
              @change="handleLogoUpload"
              accept="image/*"
              ref="logoInput"
              style="display: none"
            />
            <button
              type="button"
              @click="$refs.logoInput.click()"
              class="upload-btn"
            >
              选择Logo
            </button>

            <div v-if="companyInfo.companyLogo" class="logo-preview">
              <img :src="companyInfo.companyLogo" alt="公司Logo" />
              <button
                type="button"
                @click="removeLogo"
                class="remove-btn"
              >
                删除
              </button>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label>公司视频</label>
          <div class="media-upload">
            <input
              type="file"
              @change="handleVideoUpload"
              accept="video/*"
              ref="videoInput"
              style="display: none"
            />
            <button
              type="button"
              @click="$refs.videoInput.click()"
              class="upload-btn"
            >
              选择视频
            </button>

            <div v-if="companyInfo.companyVideo" class="video-preview">
              <video :src="companyInfo.companyVideo" controls></video>
              <button
                type="button"
                @click="removeVideo"
                class="remove-btn"
              >
                删除
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 保存按钮 -->
      <div class="form-actions">
        <button type="submit" :disabled="saving" class="save-btn">
          {{ saving ? '保存中...' : '保存信息' }}
        </button>
        <button type="button" @click="resetForm" class="reset-btn">
          重置
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { companyAPI } from '@/api/company'
import { imageAPI } from '@/api/image'
import { videoAPI } from '@/api/video'

// 响应式数据
const companyInfo = ref({})
const loading = ref(false)
const saving = ref(false)

// 加载公司信息
const loadCompanyInfo = async () => {
  try {
    loading.value = true
    const response = await companyAPI.getCompanyInfo()
    companyInfo.value = response.data
  } catch (error) {
    console.error('加载公司信息失败:', error)

    // 如果没有公司信息，尝试初始化
    if (error.response?.status === 404) {
      await initializeCompanyInfo()
    }
  } finally {
    loading.value = false
  }
}

// 初始化公司信息
const initializeCompanyInfo = async () => {
  try {
    const defaultInfo = {
      companyName: '',
      companyShortName: '',
      companyDescription: '',
      establishDate: '',
      registeredCapital: '',
      legalRepresentative: '',
      businessScope: '',
      companyAddress: '',
      companyWebsite: '',
      companyPhone: '',
      companyEmail: ''
    }

    const response = await companyAPI.initCompanyInfo(defaultInfo)
    companyInfo.value = response.data
  } catch (error) {
    console.error('初始化公司信息失败:', error)
  }
}

// 保存公司信息
const saveCompanyInfo = async () => {
  try {
    saving.value = true
    await companyAPI.updateCompanyInfo(companyInfo.value)
    alert('公司信息保存成功！')
  } catch (error) {
    alert('保存失败: ' + error.message)
  } finally {
    saving.value = false
  }
}

// Logo上传
const handleLogoUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    const response = await imageAPI.uploadLogo(file)
    companyInfo.value.companyLogo = response.data.accessUrl
    alert('Logo上传成功！')
  } catch (error) {
    alert('Logo上传失败: ' + error.message)
  }
}

// 视频上传
const handleVideoUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    const response = await videoAPI.uploadVideo(file, {
      title: '公司宣传视频',
      description: '企业宣传介绍视频'
    })
    companyInfo.value.companyVideo = response.data.accessUrl
    alert('视频上传成功！')
  } catch (error) {
    alert('视频上传失败: ' + error.message)
  }
}

// 删除Logo
const removeLogo = () => {
  if (confirm('确定要删除公司Logo吗？')) {
    companyInfo.value.companyLogo = null
  }
}

// 删除视频
const removeVideo = () => {
  if (confirm('确定要删除公司视频吗？')) {
    companyInfo.value.companyVideo = null
  }
}

// 重置表单
const resetForm = async () => {
  if (confirm('确定要重置所有信息吗？未保存的更改将丢失。')) {
    await loadCompanyInfo()
  }
}

// 初始化
onMounted(() => {
  loadCompanyInfo()
})
</script>

<style scoped>
.company-info-manager {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.company-form {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.form-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.form-section:last-child {
  border-bottom: none;
}

.form-section h3 {
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #007bff;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.media-upload {
  margin-top: 10px;
}

.upload-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 10px;
}

.logo-preview,
.video-preview {
  margin-top: 15px;
  position: relative;
}

.logo-preview img {
  max-width: 200px;
  max-height: 100px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.video-preview video {
  max-width: 400px;
  max-height: 200px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.remove-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 10px;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.save-btn {
  background: #28a745;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.save-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.reset-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.loading {
  text-align: center;
  padding: 50px;
  font-size: 18px;
  color: #666;
}
</style>
```

---

## 数据结构说明

### JWT Token结构
```javascript
// 登录响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "refresh_token_here",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "roles": ["ADMIN"]
    }
  },
  "success": true
}
```

### 图片文件结构
```javascript
{
  "fileName": "generated-filename.jpg",
  "originalName": "user-upload.jpg",
  "fileSize": 1024576,
  "fileType": "image/jpeg",
  "uploadTime": "2025-11-15T22:30:00",
  "filePath": "/uploads/category/2025/11/15/filename.jpg",
  "accessUrl": "http://localhost:33380/api/files/category/2025/11/15/filename.jpg",
  "relativeUrl": "/api/files/category/2025/11/15/filename.jpg"
}
```

### 视频文件结构
```javascript
{
  "fileName": "video.mp4",
  "originalName": "user-video.mp4",
  "fileSize": 52428800,
  "duration": 120.5,
  "resolution": "1920x1080",
  "format": "mp4",
  "uploadTime": "2025-11-15T22:30:00",
  "filePath": "/uploads/videos/2025/11/15/video.mp4",
  "accessUrl": "http://localhost:33380/api/files/videos/2025/11/15/video.mp4",
  "thumbnailUrl": "http://localhost:33380/api/files/videos/2025/11/15/video-thumbnail.jpg"
}
```

### 公司信息结构
```javascript
{
  "id": 12,
  "companyName": "示例服装有限公司",
  "companyShortName": "示例服装",
  "companyDescription": "专业从事牛仔裤设计、生产和销售的企业",
  "companyLogo": "http://localhost:33380/api/files/logo/2025/11/15/company-logo.png",
  "companyVideo": "http://localhost:33380/api/files/videos/2025/11/15/company-intro.mp4",
  "establishDate": "2010-05-15",
  "registeredCapital": "1000万元",
  "legalRepresentative": "张三",
  "businessScope": "服装设计、生产、销售",
  "companyAddress": "广东省广州市天河区科技园区A座",
  "companyWebsite": "https://example.com",
  "companyPhone": "020-12345678",
  "companyEmail": "contact@example.com",
  "status": 1,
  "createTime": "2025-11-01T14:00:47",
  "updateTime": "2025-11-08T21:55:59"
}
```

---

## 常见问题FAQ

### Q1: 如何处理JWT Token过期？
**A:** Token默认有效期为24小时。过期时会收到401错误，需要：
1. 清除本地存储的Token
2. 跳转到登录页面
3. 重新登录获取新Token

```javascript
// 在响应拦截器中处理
if (error.response?.status === 401) {
  localStorage.removeItem('adminToken')
  window.location.href = '/admin/login'
}
```

### Q2: 大文件上传超时怎么办？
**A:** 可以：
1. 增加axios超时时间到60秒或更长
2. 使用分片上传功能
3. 显示上传进度给用户反馈

### Q3: 图片和视频文件大小限制是多少？
**A:** 默认限制：
- 图片：单文件最大150MB
- 视频：单文件最大150MB
- 批量上传：总大小不超过500MB

可以在application.properties中调整：
```properties
spring.servlet.multipart.max-file-size=150MB
spring.servlet.multipart.max-request-size=150MB
```

### Q4: 如何实现图片压缩和水印？
**A:** 使用增强上传接口：
```javascript
imageAPI.uploadEnhanced(file, {
  quality: 75,        // 压缩质量1-100
  watermark: true,    // 是否添加水印
  watermarkText: '版权所有',  // 水印文字
  category: 'product' // 文件分类
})
```

### Q5: 视频上传支持哪些格式？
**A:** 支持主流视频格式：
- MP4（推荐）
- AVI
- MOV
- WMV
- FLV

### Q6: 如何获取视频缩略图？
**A:** 上传视频时会自动生成缩略图：
```javascript
const response = await videoAPI.uploadVideo(file)
const thumbnailUrl = response.data.thumbnailUrl
```

### Q7: 公司信息字段哪些是必填的？
**A:** 必填字段：
- companyName（公司名称）

推荐字段：
- companyDescription（公司描述）
- companyPhone（联系电话）
- companyEmail（联系邮箱）

### Q8: 如何批量删除文件？
**A:** 目前需要逐个删除，可以：
```javascript
const deleteFiles = async (files) => {
  for (const file of files) {
    await imageAPI.deleteFile(file.filePath, file.category)
  }
}
```

### Q9: 文件存储路径是什么格式？
**A:** 存储路径格式：
```
/uploads/{category}/{year}/{month}/{day}/{filename}
例如：/uploads/product/2025/11/15/product-image.jpg
访问URL：http://localhost:33380/api/files/product/2025/11/15/product-image.jpg
```

### Q10: 如何处理文件上传失败的错误？
**A:** 上传接口会返回详细错误信息：
```javascript
try {
  const response = await imageAPI.uploadLogo(file)
} catch (error) {
  if (error.response?.data?.message) {
    console.error('上传失败:', error.response.data.message)
  } else {
    console.error('网络错误:', error.message)
  }
}
```

---

## 总结

**核心信息：**

- 🌐 后端地址：`http://localhost:33380`（开发）/ `http://39.97.60.191:33380`（生产）
- 🔒 需要认证：所有Admin接口都需要JWT Token
- 📁 文件管理：完整的图片和视频上传、管理功能
- 🏢 公司信息：完整的企业信息管理系统
- ⚡ 高性能：支持批量上传、压缩、水印等高级功能

**快速开始：**
1. 实现JWT登录认证
2. 集成图片管理组件
3. 集成视频管理组件
4. 配置公司信息管理
5. 实现文件预览和删除

**技术特点：**
- JWT安全认证
- 大文件上传支持
- 图片压缩和水印
- 视频转码和缩略图
- 响应式设计
- 完整的错误处理

**注意事项：**
- 所有接口都需要JWT Token认证
- 大文件上传需要较长超时时间
- 定期清理不需要的文件
- 备份重要的公司信息数据

如有问题，请联系后端开发团队。