import apiClient from './config'
import { MANAGEMENT_API_BASE_URL } from './company'

/**
 * 管理系统产品API
 * 用于连接管理后台的产品数据，实现实时同步
 */

/**
 * 获取管理系统中的产品列表
 * GET /api/products/admin/all
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 产品列表数据
 */
export const getManagementProducts = async (params = {}) => {
  try {
    const response = await apiClient.get('/api/products/admin/all', { params })
    return response.data
  } catch (error) {
    console.error('获取管理后台产品列表失败:', error)
    throw error
  }
}

/**
 * 从管理系统获取产品详情
 * GET /api/products/admin/{id}
 * @param {string|number} productId 产品ID
 * @returns {Promise<Object>} 产品详情数据
 */
export const getManagementProductById = async (productId) => {
  try {
    const response = await apiClient.get(`/api/products/admin/${productId}`)
    return response.data
  } catch (error) {
    console.error(`获取管理后台产品详情失败 (ID: ${productId}):`, error)
    throw error
  }
}

/**
 * 从管理系统获取上架产品列表
 * GET /api/products/admin/active
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 上架产品列表
 */
export const getManagementActiveProducts = async (params = {}) => {
  try {
    const response = await apiClient.get('/api/products/admin/active', { params })
    return response.data
  } catch (error) {
    console.error('获取管理后台上架产品失败:', error)
    throw error
  }
}

/**
 * 从管理系统获取推荐产品列表
 * GET /api/products/admin/featured
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 推荐产品列表
 */
export const getManagementFeaturedProducts = async (params = {}) => {
  try {
    const response = await apiClient.get('/api/products/admin/featured', { params })
    return response.data
  } catch (error) {
    console.error('获取管理后台推荐产品失败:', error)
    throw error
  }
}

/**
 * 从管理系统搜索产品
 * GET /api/products/admin/search
 * @param {Object} searchParams 搜索参数
 * @returns {Promise<Object>} 搜索结果
 */
export const searchManagementProducts = async (searchParams = {}) => {
  try {
    const response = await apiClient.get('/api/products/admin/search', {
      params: searchParams
    })
    return response.data
  } catch (error) {
    console.error('管理后台产品搜索失败:', error)
    throw error
  }
}

/**
 * 从管理系统获取产品统计信息
 * GET /api/products/admin/statistics
 * @returns {Promise<Object>} 产品统计数据
 */
export const getManagementProductStatistics = async () => {
  try {
    const response = await apiClient.get('/api/products/admin/statistics')
    return response.data
  } catch (error) {
    console.error('获取管理后台产品统计失败:', error)
    throw error
  }
}

/**
 * 从管理系统获取产品分类
 * GET /api/products/admin/categories
 * @returns {Promise<Object>} 产品分类数据
 */
export const getManagementProductCategories = async () => {
  try {
    const response = await apiClient.get('/api/products/admin/categories')
    return response.data
  } catch (error) {
    console.error('获取管理后台产品分类失败:', error)
    throw error
  }
}

/**
 * 智能产品数据获取 - 优先从管理后台获取，失败时回退到本地数据
 * @param {string} productId 产品ID
 * @returns {Promise<Object>} 产品数据
 */
export const getSmartProductData = async (productId) => {
  try {
    // 首先尝试从管理后台获取数据
    const managementData = await getManagementProductById(productId)

    console.log(`✅ 从管理后台获取产品数据成功: ${productId}`)

    // 标准化管理后台返回的数据格式
    if (managementData.success && managementData.data) {
      return normalizeManagementProductData(managementData.data)
    }

    throw new Error('管理后台返回数据格式错误')
  } catch (error) {
    console.warn(`⚠️ 管理后台获取产品失败，回退到本地数据: ${productId}`, error)

    // 回退到原有的本地API
    const { getProductById } = await import('./products')
    return await getProductById(productId)
  }
}

/**
 * 智能产品列表获取 - 优先从管理后台获取
 * @param {Object} params 查询参数
 * @returns {Promise<Array>} 产品列表
 */
export const getSmartProductList = async (params = {}) => {
  try {
    // 首先尝试从管理后台获取数据
    const managementData = await getManagementActiveProducts(params)

    console.log('✅ 从管理后台获取产品列表成功')

    // 标准化管理后台返回的数据格式
    if (managementData.success && managementData.data) {
      const products = Array.isArray(managementData.data)
        ? managementData.data
        : managementData.data.content || []

      return products.map(product => normalizeManagementProductData(product))
    }

    throw new Error('管理后台返回数据格式错误')
  } catch (error) {
    console.warn('⚠️ 管理后台获取产品列表失败，回退到本地数据', error)

    // 回退到原有的本地API
    const { getActiveProducts } = await import('./products')
    return await getActiveProducts()
  }
}

/**
 * 处理图片路径，确保指向完整的服务器地址
 * @param {string|Array} images 图片路径或图片数组
 * @returns {string|Array} 处理后的图片路径
 */
function processImagePaths(images) {
  if (!images) {
    return images
  }

  const processSingleImage = (img) => {
    if (!img) return img

    // 使用相对路径
    if (img.startsWith('/uploads/')) {
      return `/api/files${img}`
    }
    return img
  }

  if (Array.isArray(images)) {
    return images.map(processSingleImage)
  }

  if (typeof images === 'string') {
    return processSingleImage(images)
  }

  return images
}

/**
 * 标准化管理后台产品数据格式
 * 将管理后台的数据格式转换为前端需要的格式
 * @param {Object} managementProduct 管理后台产品数据
 * @returns {Object} 标准化后的产品数据
 */
function normalizeManagementProductData(managementProduct) {
  // 处理图片路径
  const mainImage = processImagePaths(managementProduct.mainImage || managementProduct.featuredImage || managementProduct.images?.[0])
  const images = processImagePaths(
    Array.isArray(managementProduct.images)
      ? managementProduct.images
      : managementProduct.mainImage
        ? [managementProduct.mainImage]
        : []
  )

  return {
    id: managementProduct.id || managementProduct.productId,
    productName: managementProduct.productName || managementProduct.name,
    productNameEn: managementProduct.productNameEn || managementProduct.nameEn,
    productTitle: managementProduct.productTitle || managementProduct.title,
    productSubtitle: managementProduct.productSubtitle || managementProduct.subtitle,
    category: managementProduct.category || managementProduct.productCategory,
    price: parseFloat(managementProduct.price) || 0,
    originalPrice: parseFloat(managementProduct.originalPrice) || parseFloat(managementProduct.price) || 0,
    stock: parseInt(managementProduct.stock) || managementProduct.inventory || 0,
    minOrderQuantity: parseInt(managementProduct.minOrderQuantity) || managementProduct.moq || 100,
    mainImage: mainImage,
    images: images,
    description: managementProduct.description || managementProduct.productDescription,
    descriptionEn: managementProduct.descriptionEn || managementProduct.productDescriptionEn,
    viewCount: parseInt(managementProduct.viewCount) || 0,
    favoriteCount: parseInt(managementProduct.favoriteCount) || 0,
    inquiryCount: parseInt(managementProduct.inquiryCount) || 0,
    isFeatured: Boolean(managementProduct.isFeatured || managementProduct.featured),
    isActive: Boolean(managementProduct.isActive !== false), // 默认为true
    tags: Array.isArray(managementProduct.tags)
      ? managementProduct.tags
      : (managementProduct.tags ? managementProduct.tags.split(',').map(tag => tag.trim()) : []),
    specifications: managementProduct.specifications || managementProduct.attributes || {},
    createdAt: managementProduct.createdAt || managementProduct.createTime,
    updatedAt: managementProduct.updatedAt || managementProduct.updateTime,
    videoPath: processImagePaths(managementProduct.videoPath),

    // 管理后台特有字段
    managementData: {
      source: 'management_api',
      lastSync: new Date().toISOString(),
      raw: managementProduct
    }
  }
}

/**
 * 实时数据同步检查
 * 检查管理后台是否有新数据更新
 * @param {string} lastSyncTime 上次同步时间
 * @returns {Promise<Object>} 同步状态
 */
export const checkDataSync = async (lastSyncTime = null) => {
  try {
    const params = lastSyncTime ? { since: lastSyncTime } : {}
    const response = await apiClient.get('/api/products/admin/sync/check', { params })

    return {
      hasUpdates: response.data?.hasUpdates || false,
      updateCount: response.data?.updateCount || 0,
      lastSync: new Date().toISOString(),
      success: true
    }
  } catch (error) {
    console.warn('检查数据同步状态失败:', error)
    return {
      hasUpdates: false,
      updateCount: 0,
      lastSync: new Date().toISOString(),
      success: false,
      error: error.message
    }
  }
}

// 统一导出
export default {
  // 管理后台API
  getManagementProducts,
  getManagementProductById,
  getManagementActiveProducts,
  getManagementFeaturedProducts,
  searchManagementProducts,
  getManagementProductStatistics,
  getManagementProductCategories,

  // 智能API（带回退机制）
  getSmartProductData,
  getSmartProductList,

  // 工具函数
  normalizeManagementProductData,
  checkDataSync
}