import apiClient from './config'

/**
 * 获取支持的媒体分类列表
 * @returns {Promise<Object>} 分类列表
 */
export const getMediaCategories = () => {
  return apiClient.get('/api/public/media/categories')
    .catch(error => {
      console.error('❌ [MediaAPI] 获取媒体分类失败:', error)
      throw new Error(`获取媒体分类失败: ${error.message}`)
    })
}

/**
 * 根据分类获取媒体文件列表
 * @param {string} category - 分类名称
 * @returns {Promise<Object>} 媒体文件列表
 */
export const getMediaFiles = (category) => {
  if (!category || typeof category !== 'string') {
    const error = new Error('分类名称不能为空且必须为字符串')
    console.error('❌ [MediaAPI] 无效的分类参数:', category)
    return Promise.reject(error)
  }

  console.log(`🔄 [MediaAPI] 获取分类 "${category}" 的媒体文件...`)

  return apiClient.get(`/api/public/media/category/${encodeURIComponent(category)}`)
    .then(response => {
      console.log(`✅ [MediaAPI] 成功获取分类 "${category}" 的媒体文件:`, response.data?.data?.totalCount || 0, '个文件')
      return response
    })
    .catch(error => {
      console.error(`❌ [MediaAPI] 获取分类 "${category}" 的媒体文件失败:`, error)

      // 提供更详细的错误信息
      let errorMessage = `获取媒体文件失败`
      if (error.response) {
        // 服务器返回了错误状态码
        switch (error.response.status) {
          case 404:
            errorMessage = `分类 "${category}" 不存在或没有找到相关文件`
            break
          case 403:
            errorMessage = `没有权限访问分类 "${category}" 的文件`
            break
          case 500:
            errorMessage = `服务器内部错误，请稍后重试`
            break
          default:
            errorMessage = `获取分类 "${category}" 失败 (${error.response.status})`
        }
      } else if (error.request) {
        // 网络错误
        errorMessage = `网络连接失败，请检查网络连接后重试`
      } else {
        // 其他错误
        errorMessage = `获取媒体文件失败: ${error.message}`
      }

      throw new Error(errorMessage)
    })
}

/**
 * 获取文件详细信息
 * @param {string} fileUrl - 文件URL
 * @returns {Promise<Object>} 文件详细信息
 */
export const getFileInfo = (fileUrl) => {
  const encodedUrl = encodeURIComponent(fileUrl)
  return apiClient.get(`/api/public/media/file/info?fileUrl=${encodedUrl}`)
}

/**
 * 检查文件是否存在
 * @param {string} fileUrl - 文件URL
 * @returns {Promise<Object>} 文件存在状态
 */
export const checkFileExists = (fileUrl) => {
  const encodedUrl = encodeURIComponent(fileUrl)
  return apiClient.get(`/api/public/media/file/exists?fileUrl=${encodedUrl}`)
}

/**
 * 根据分类获取图片文件
 * @param {string} category - 分类名称
 * @returns {Promise<Object>} 图片文件列表
 */
export const getImagesByCategory = (category) => {
  return getMediaFiles(category).then(response => {
    if (response && response.data && response.data.files) {
      // 过滤出图片文件
      const imageFiles = response.data.files.filter(file => file.fileType === 'image')
      return {
        ...response,
        data: {
          ...response.data,
          files: imageFiles
        }
      }
    }
    return response
  })
}

/**
 * 根据分类获取视频文件
 * @param {string} category - 分类名称
 * @returns {Promise<Object>} 视频文件列表
 */
export const getVideosByCategory = (category) => {
  return getMediaFiles(category).then(response => {
    if (response && response.data && response.data.files) {
      // 过滤出视频文件
      const videoFiles = response.data.files.filter(file => file.fileType === 'video')
      return {
        ...response,
        data: {
          ...response.data,
          files: videoFiles
        }
      }
    }
    return response
  })
}

/**
 * 获取首页轮播图
 * @returns {Promise<Object>} 首页轮播图列表
 */
export const getHomeCarouselImages = () => {
  return getImagesByCategory('home-carousel')
}

/**
 * 获取产品照片
 * @returns {Promise<Object>} 产品照片列表
 */
export const getProductPhotos = () => {
  return getImagesByCategory('product-photos')
}

/**
 * 获取产品视频
 * @returns {Promise<Object>} 产品视频列表
 */
export const getProductVideos = () => {
  return getVideosByCategory('product-video')
}

/**
 * 获取企业宣传视频
 * @returns {Promise<Object>} 企业宣传视频列表
 */
export const getCompanyIntroVideos = () => {
  return getVideosByCategory('company-intro')
}

/**
 * 获取公司照片
 * @returns {Promise<Object>} 公司照片列表
 */
export const getCompanyPhotos = () => {
  return getImagesByCategory('company-photos')
}

/**
 * 获取工厂照片
 * @returns {Promise<Object>} 工厂照片列表
 */
export const getFactoryPhotos = () => {
  return getImagesByCategory('factory-photos')
}

/**
 * 获取资质证书
 * @returns {Promise<Object>} 资质证书列表
 */
export const getQualifications = () => {
  return getImagesByCategory('qualification')
}

/**
 * 获取团队头像
 * @returns {Promise<Object>} 团队头像列表
 */
export const getAvatars = () => {
  return getImagesByCategory('avatar')
}

// 统一导出
export default {
  // 分类相关
  getMediaCategories,
  getMediaFiles,

  // 便捷方法
  getImagesByCategory,
  getVideosByCategory,

  // 具体分类的便捷方法
  getHomeCarouselImages,
  getProductPhotos,
  getProductVideos,
  getCompanyIntroVideos,
  getCompanyPhotos,
  getFactoryPhotos,
  getQualifications,
  getAvatars,

  // 文件信息相关
  getFileInfo,
  checkFileExists
}