import http from './http'
import { API_ENDPOINTS } from './config'

/**
 * 文件上传相关 API
 */
export const uploadAPI = {
  /**
   * 增强图片上传
   * @param {File} file - 图片文件
   * @param {Object} options - 上传选项
   * @param {string} options.category - 分类（必填）
   * @param {string} options.description - 描述（可选）
   * @param {string} options.tags - 标签（可选）
   * @param {number} options.relatedId - 关联ID（可选）
   * @param {string} options.relatedType - 关联类型（可选）
   * @param {number} options.width - 目标宽度（可选）
   * @param {number} options.height - 目标高度（可选）
   * @param {number} options.maxSize - 最大文件大小（可选）
   * @param {boolean} options.watermark - 是否添加水印（可选）
   * @param {boolean} options.thumbnail - 是否生成缩略图（可选）
   * @param {number} options.position - 图片位置（可选，用于固定位置上传）
   * @param {Function} options.onProgress - 上传进度回调（可选）
   * @returns {Promise}
   */
  uploadImage(file, options = {}) {
    const formData = new FormData()
    formData.append('file', file)

    // 必填：分类
    if (options.category) {
      formData.append('category', options.category)
    }

    // 可选参数
    if (options.description) {
      formData.append('description', options.description)
    }
    if (options.tags) {
      formData.append('tags', options.tags)
    }
    if (options.relatedId) {
      formData.append('relatedId', options.relatedId)
    }
    if (options.relatedType) {
      formData.append('relatedType', options.relatedType)
    }
    if (options.width) {
      formData.append('width', options.width)
    }
    if (options.height) {
      formData.append('height', options.height)
    }
    if (options.maxSize) {
      formData.append('maxSize', options.maxSize)
    }
    if (options.watermark !== undefined) {
      formData.append('watermark', options.watermark)
    }
    if (options.thumbnail !== undefined) {
      formData.append('thumbnail', options.thumbnail)
    }
    if (options.position !== undefined) {
      formData.append('position', options.position)
    }

    // 配置上传进度回调
    const config = {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 120000 // 图片上传设置2分钟超时
    }

    // 如果提供了进度回调函数，添加到配置中
    if (typeof options.onProgress === 'function') {
      config.onUploadProgress = (progressEvent) => {
        const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        options.onProgress(percentCompleted, progressEvent)
      }
    }

    return http.post(API_ENDPOINTS.UPLOAD.IMAGE, formData, config)
  },

  /**
   * 上传Logo
   * @param {File} file - Logo文件
   * @returns {Promise}
   */
  uploadLogo(file) {
    const formData = new FormData()
    formData.append('file', file)

    return http.post(API_ENDPOINTS.UPLOAD.LOGO, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 上传视频
   * @param {File} file - 视频文件
   * @param {Object} options - 上传选项
   * @param {string} options.category - 分类（必填）
   * @param {string} options.description - 描述（可选）
   * @param {string} options.tags - 标签（可选）
   * @param {number} options.relatedId - 关联ID（可选）
   * @param {string} options.relatedType - 关联类型（可选）
   * @param {boolean} options.thumbnail - 是否生成缩略图（可选）
   * @param {number} options.maxSize - 最大文件大小（可选）
   * @param {Function} options.onProgress - 上传进度回调（可选）
   * @returns {Promise}
   */
  uploadVideo(file, options = {}) {
    const formData = new FormData()
    formData.append('file', file)

    // 必填：分类
    if (options.category) {
      formData.append('category', options.category)
    }

    // 可选参数
    if (options.description) {
      formData.append('description', options.description)
    }
    if (options.tags) {
      formData.append('tags', options.tags)
    }
    if (options.relatedId) {
      formData.append('relatedId', options.relatedId)
    }
    if (options.relatedType) {
      formData.append('relatedType', options.relatedType)
    }
    if (options.thumbnail !== undefined) {
      formData.append('thumbnail', options.thumbnail)
    }
    if (options.maxSize) {
      formData.append('maxSize', options.maxSize)
    }

    // 配置上传进度回调
    const config = {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 300000 // 视频上传单独设置5分钟超时
    }

    // 如果提供了进度回调函数，添加到配置中
    if (typeof options.onProgress === 'function') {
      config.onUploadProgress = (progressEvent) => {
        const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        options.onProgress(percentCompleted, progressEvent)
      }
    }

    return http.post(API_ENDPOINTS.UPLOAD.VIDEO_ENHANCED, formData, config)
  },

  /**
   * 批量上传图片
   * @param {File[]} files - 图片文件数组
   * @param {string} category - 分类
   * @returns {Promise}
   */  batchUploadImages(files, category) {
    const formData = new FormData()
    
    // 添加多个文件
    files.forEach(file => {
      formData.append('files', file)
    })
    
    formData.append('category', category)
    
    return http.post(API_ENDPOINTS.UPLOAD.BATCH_IMAGES, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 删除文件（增强版）
   * @param {string} fileUrl - 文件URL
   * @returns {Promise}
   */
  deleteFile(fileUrl) {
    return http.delete(API_ENDPOINTS.UPLOAD.DELETE, {
      params: { fileUrl }
    })
  },

  /**
   * 刷新文件列表
   * @param {string} category - 文件分类（可选）
   * @returns {Promise}
   */
  refreshFileList(category = null) {
    const params = category ? { category } : {}
    return http.get(API_ENDPOINTS.UPLOAD.REFRESH, { params })
  },

  /**
   * 根据分类查询文件列表
   * @param {string} category - 文件分类
   * @returns {Promise}
   */
  getFilesByCategory(category) {
    return http.get(`${API_ENDPOINTS.UPLOAD.BASE}/files/category/${category}`)
  },

  /**
   * 根据分类查询单个最新文件
   * @param {string} category - 文件分类
   * @returns {Promise}
   */
  getLatestFileByCategory(category) {
    return http.get(`${API_ENDPOINTS.UPLOAD.BASE}/file/category/${category}/latest`)
  }
}

export default uploadAPI