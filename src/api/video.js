/**
 * 视频管理API服务
 * 基于管理系统对接展示前端API文档实现
 */

// 管理系统API基础配置
const MANAGEMENT_API_BASE_URL = ''  // 使用相对路径，通过 Nginx 代理

/**
 * 获取管理系统的认证Token
 */
function getManagementAuthToken() {
  return localStorage.getItem('authToken') || sessionStorage.getItem('authToken')
}

/**
 * 创建带认证的请求头
 */
function createManagementAuthHeaders() {
  const token = getManagementAuthToken()
  const headers = {}

  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  return headers
}

/**
 * 管理系统通用API请求方法
 */
async function managementApiRequest(endpoint, options = {}) {
  const url = `${MANAGEMENT_API_BASE_URL}${endpoint}`
  const config = {
    headers: createManagementAuthHeaders(),
    ...options
  }

  try {
    const response = await fetch(url, config)

    // 检查响应状态
    if (!response.ok) {
      if (response.status === 401) {
        console.warn('Token已过期或无效，需要重新登录')
        throw new Error('UNAUTHORIZED')
      }
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const data = await response.json()
    return data
  } catch (error) {
    console.error(`管理系统API请求失败: ${endpoint}`, error)
    throw error
  }
}

/**
 * 视频管理API
 */
export const videoApi = {
  /**
   * 上传企业视频
   * POST /api/admin/upload/video
   * @param {File} file - 视频文件
   * @param {Object} options - 可选参数
   * @param {string} options.title - 视频标题
   * @param {string} options.description - 视频描述
   * @param {string} options.category - 视频分类
   * @returns {Promise<Object>} 上传结果
   */
  async uploadVideo(file, options = {}) {
    const formData = new FormData()
    formData.append('file', file)

    // 添加可选参数
    Object.keys(options).forEach(key => {
      if (options[key] !== undefined) {
        formData.append(key, options[key])
      }
    })

    return await managementApiRequest('/api/admin/upload/video', {
      method: 'POST',
      body: formData,
      // 注意：不要设置 Content-Type，让浏览器自动设置 multipart/form-data
      headers: createManagementAuthHeaders() // 移除Content-Type让浏览器自动设置
    })
  },

  /**
   * 增强视频上传（支持转码和切片）
   * POST /api/admin/upload/video/enhanced
   * @param {File} file - 视频文件
   * @param {Object} options - 可选参数
   * @param {string} options.description - 视频描述
   * @param {string} options.quality - 转码质量（high, medium, low）
   * @param {boolean} options.generateThumbnail - 是否生成缩略图
   * @returns {Promise<Object>} 增强上传结果
   */
  async uploadEnhancedVideo(file, options = {}) {
    const formData = new FormData()
    formData.append('file', file)

    Object.keys(options).forEach(key => {
      if (options[key] !== undefined) {
        formData.append(key, options[key])
      }
    })

    return await managementApiRequest('/api/admin/upload/video/enhanced', {
      method: 'POST',
      body: formData,
      headers: createManagementAuthHeaders()
    })
  },

  /**
   * 上传产品视频
   * POST /api/admin/upload/product/video
   * @param {File} file - 视频文件
   * @param {string} productId - 产品ID
   * @param {Object} options - 可选参数
   * @param {string} options.title - 视频标题
   * @param {string} options.description - 视频描述
   * @returns {Promise<Object>} 上传结果
   */
  async uploadProductVideo(file, productId, options = {}) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('productId', productId)

    Object.keys(options).forEach(key => {
      if (options[key] !== undefined) {
        formData.append(key, options[key])
      }
    })

    return await managementApiRequest('/api/admin/upload/product/video', {
      method: 'POST',
      body: formData,
      headers: createManagementAuthHeaders()
    })
  },

  /**
   * 删除产品视频
   * DELETE /api/admin/products/{productId}/video
   * @param {string} productId - 产品ID
   * @returns {Promise<Object>} 删除结果
   */
  async deleteProductVideo(productId) {
    return await managementApiRequest(`/api/admin/products/${productId}/video`, {
      method: 'DELETE'
    })
  },

  /**
   * 获取公司视频信息
   * 通过公司信息API获取已上传的企业视频
   * @returns {Promise<Object>} 公司视频信息
   */
  async getCompanyVideo() {
    try {
      // 调用公司信息API获取视频URL
      const response = await managementApiRequest('/api/company/admin/info')

      if (response && response.success && response.data) {
        return {
          success: true,
          data: {
            companyVideo: response.data.companyVideo,
            companyVideoThumbnail: response.data.companyVideoThumbnail,
            videoTitle: response.data.videoTitle || 'Company Introduction Video',
            videoDescription: response.data.videoDescription || 'Professional manufacturing facility overview'
          }
        }
      }

      return { success: false, message: '未找到公司视频信息' }
    } catch (error) {
      console.error('获取公司视频信息失败:', error)
      return { success: false, error: error.message }
    }
  },

  /**
   * 上传Product Display组件视频
   * POST /api/admin/upload/product-display/video
   * @param {File} file - 视频文件
   * @param {Object} productData - 产品数据
   * @returns {Promise<Object>} 上传结果
   */
  async uploadProductDisplayVideo(file, productData) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', 'product-display')
    formData.append('productId', productData.productId || '')
    formData.append('title', productData.title || 'Product Display Video')
    formData.append('description', productData.description || '产品展示视频')
    formData.append('category', 'product-showcase')

    return await managementApiRequest('/api/admin/upload/product-display/video', {
      method: 'POST',
      body: formData,
      headers: createManagementAuthHeaders()
    })
  },

  /**
   * 上传公司简介页面视频
   * @param {File} file - 视频文件
   * @param {Object} videoData - 视频数据
   * @returns {Promise<Object>} 上传结果
   */
  async uploadCompanyIntroVideo(file, videoData) {
    return await this.uploadVideo(file, {
      title: videoData.title || 'Company Introduction Video',
      description: videoData.description || '公司简介宣传视频',
      category: 'company-introduction',
      section: 'company-profile',
      ...videoData
    })
  },

  /**
   * 刷新文件列表
   * GET /api/admin/upload/files/refresh
   * @returns {Promise<Object>} 刷新结果
   */
  async refreshFileList() {
    return await managementApiRequest('/api/admin/upload/files/refresh', {
      method: 'GET'
    })
  }
}

/**
 * 创建视频文件选择器辅助函数
 */
export const createVideoSelector = (accept = 'video/*', multiple = false) => {
  return new Promise((resolve, reject) => {
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = accept
    input.multiple = multiple

    input.onchange = (event) => {
      const files = Array.from(event.target.files)
      if (multiple) {
        resolve(files)
      } else {
        resolve(files[0] || null)
      }
    }

    input.oncancel = () => resolve(null)
    input.onerror = reject

    input.click()
  })
}

/**
 * 验证视频文件
 * @param {File} file - 视频文件
 * @param {Object} options - 验证选项
 * @returns {Object} 验证结果
 */
export const validateVideoFile = (file, options = {}) => {
  const {
    maxSize = 500 * 1024 * 1024, // 默认500MB
    allowedTypes = ['video/mp4', 'video/avi', 'video/mov', 'video/wmv', 'video/flv']
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
}

/**
 * 格式化视频时长
 * @param {number} seconds - 秒数
 * @returns {string} 格式化的时长字符串
 */
export const formatVideoDuration = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = Math.floor(seconds % 60)
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

/**
 * 格式化文件大小
 * @param {number} bytes - 字节数
 * @returns {string} 格式化的文件大小字符串
 */
export const formatFileSize = (bytes) => {
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  if (bytes === 0) return '0 Bytes'
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
}

// 统一导出
export default {
  videoApi,
  createVideoSelector,
  validateVideoFile,
  formatVideoDuration,
  formatFileSize,
  MANAGEMENT_API_BASE_URL
}