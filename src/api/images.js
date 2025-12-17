/**
 * 图片管理API服务
 * 基于管理系统对接展示前端API文档实现
 */

import { managementApiRequest, MANAGEMENT_API_BASE_URL } from './company'

export const imageApi = {
  // === 首页图片管理 ===

  /**
   * 上传顶部轮播图
   * POST /api/admin/upload/carousel/image
   */
  async uploadCarouselImage(file, carouselData) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', 'home-carousel')
    formData.append('order', carouselData.order || 0)
    formData.append('title', carouselData.title || '')
    formData.append('link', carouselData.link || '')

    return await managementApiRequest('/api/admin/upload/carousel/image', {
      method: 'POST',
      body: formData,
      headers: {}
    })
  },

  /**
   * 上传公司简介左侧轮播图
   */
  async uploadCompanyIntroCarousel(file, imageData) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('section', 'intro-carousel')
    formData.append('order', imageData.order || 0)
    formData.append('title', imageData.title || '')

    return await managementApiRequest('/api/admin/upload/company-intro/carousel', {
      method: 'POST',
      body: formData,
      headers: {}
    })
  },

  /**
   * 上传公司简介底部图片
   */
  async uploadCompanyIntroBottom(file, imageData) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('section', 'intro-bottom')
    formData.append('position', imageData.position) // 'left' 或 'right'
    formData.append('title', imageData.title || '')

    return await managementApiRequest('/api/admin/upload/company-intro/bottom', {
      method: 'POST',
      body: formData,
      headers: {}
    })
  },

  // === 公司简介页面图片管理 ===

  /**
   * 批量上传轮播展示图片
   */
  async uploadShowcaseImages(files, category) {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    formData.append('category', category) // 'overview', 'production', 'rd', 'quality'
    formData.append('maxImages', this.getCategoryMaxImages(category))

    return await managementApiRequest('/api/admin/upload/showcase/images/batch', {
      method: 'POST',
      body: formData,
      headers: {}
    })
  },

  /**
   * 单张图片上传/替换
   */
  async uploadShowcaseImage(file, category, index) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('category', category)
    formData.append('index', index)

    return await managementApiRequest('/api/admin/upload/showcase/image', {
      method: 'POST',
      body: formData,
      headers: {}
    })
  },

  /**
   * 获取分类最大图片数量
   */
  getCategoryMaxImages(category) {
    const maxImages = {
      'overview': 6,
      'production': 8,
      'rd': 5,
      'quality': 1
    }
    return maxImages[category] || 5
  },

  // === 产品图片管理 ===

  /**
   * 上传产品图片
   */
  async uploadProductImage(file, productId) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('productId', productId)

    return await managementApiRequest('/api/admin/upload/product/image', {
      method: 'POST',
      body: formData,
      headers: {}
    })
  },

  /**
   * 上传产品主图
   */
  async uploadProductMainImage(file, productId) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('productId', productId)

    return await managementApiRequest('/api/admin/upload/product/main-image', {
      method: 'POST',
      body: formData,
      headers: {}
    })
  },

  /**
   * 批量上传产品图片
   */
  async batchUploadProductImages(files, productId) {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    formData.append('productId', productId)

    return await managementApiRequest('/api/admin/upload/product/images/batch', {
      method: 'POST',
      body: formData,
      headers: {}
    })
  },

  // === 公司Logo上传 ===

  /**
   * 上传公司Logo
   */
  async uploadCompanyLogo(file) {
    const formData = new FormData()
    formData.append('file', file)

    return await managementApiRequest('/api/admin/upload/logo', {
      method: 'POST',
      body: formData,
      headers: {}
    })
  },

  // === 工具方法 ===

  /**
   * 验证图片文件
   */
  validateImageFile(file, options = {}) {
    const {
      maxSize = 10 * 1024 * 1024, // 默认10MB
      allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
    } = options

    const result = {
      valid: true,
      errors: []
    }

    // 检查文件类型
    if (!allowedTypes.includes(file.type)) {
      result.valid = false
      result.errors.push(`不支持的文件类型: ${file.type}`)
    }

    // 检查文件大小
    if (file.size > maxSize) {
      result.valid = false
      result.errors.push(`文件大小超过限制: ${(file.size / 1024 / 1024).toFixed(2)}MB > ${(maxSize / 1024 / 1024).toFixed(2)}MB`)
    }

    return result
  },

  /**
   * 格式化文件大小
   */
  formatFileSize(bytes) {
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    if (bytes === 0) return '0 Bytes'
    const i = Math.floor(Math.log(bytes) / Math.log(1024))
    return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
  }
}

// 统一导出
export default {
  imageApi,
  validateImageFile: imageApi.validateImageFile,
  formatFileSize: imageApi.formatFileSize,
  getCategoryMaxImages: imageApi.getCategoryMaxImages,
  MANAGEMENT_API_BASE_URL
}