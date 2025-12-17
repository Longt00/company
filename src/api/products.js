import apiClient from './config'

/**
 * 获取所有产品列表（公开接口）
 * @param {Object} params 查询参数
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页大小，默认20
 * @param {string} params.category 产品分类筛选
 * @returns {Promise<Object>} 产品分页数据
 */
export const getAllProducts = (params = {}) => {
  return apiClient.get('/api/public/products', { params })
}

// 获取热门推荐产品列表（公开接口）
// 对应后端接口：GET /api/public/products/featured
export const getFeaturedProducts = (params = {}) => {
  return apiClient.get('/api/public/products/featured', { params })
}

// 获取新品产品列表（公开接口）
// 对应后端接口：GET /api/public/products/new
export const getNewProducts = (params = {}) => {
  return apiClient.get('/api/public/products/new', { params })
}

// 获取主要产品列表（公开接口）
// 对应后端接口：GET /api/public/products/main
export const getMainProducts = (params = {}) => {
  return apiClient.get('/api/public/products/main', { params })
}

/**
 * 根据分类获取产品（公开接口）
 * @param {string} category 产品分类
 * @param {Object} params 查询参数
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页大小，默认20
 * @returns {Promise<Object>} 产品分页数据
 */
export const getProductsByCategory = (category, params = {}) => {
  return apiClient.get(`/api/public/products/category/${encodeURIComponent(category)}`, { params })
}

/**
 * 获取产品分类列表（公开接口）
 * @returns {Promise<Array>} 产品分类列表
 */
export const getProductCategories = () => {
  return apiClient.get('/api/public/products/categories')
}

/**
 * 搜索产品（公开接口）
 * @param {string} keyword 搜索关键词
 * @param {Object} params 查询参数
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页大小，默认20
 * @returns {Promise<Object>} 搜索结果分页数据
 */
export const searchProducts = (keyword, params = {}) => {
  return apiClient.get('/api/public/products/search', {
    params: { keyword, ...params }
  })
}

/**
 * 获取产品详情（公开接口）
 * @param {number} id 产品ID
 * @returns {Promise<Object>} 产品详情
 */
export const getProductDetail = (id) => {
  return apiClient.get(`/api/public/products/${id}`)
}

// 兼容旧的API方法
/**
 * 获取所有上架产品列表（兼容旧方法）
 * @returns {Promise<Array>} 上架产品列表
 */
export const getActiveProducts = () => {
  return getAllProducts().then(response => {
    // 返回分页数据中的records数组
    return response.data?.records || []
  })
}

// ===== 公开产品API（新） =====

// 统一导出所有产品相关API
export default {
  // 新的公开API
  getAllProducts,
  getFeaturedProducts,
  getNewProducts,
  getMainProducts,
  getProductsByCategory,
  getProductCategories,
  searchProducts,
  getProductDetail,
  // 兼容旧API
  getActiveProducts
}

/**
 * 获取产品统计信息（包含分类列表）
 * @returns {Promise<Object>} 统计信息，包含categories数组
 */
export const getProductStatistics = () => {
  return apiClient.get('/api/products/statistics')
}

// 保留兼容的旧方法（单独导出）
export {
  // 以下函数暂时未实现，如需要请添加
  // getProductById,
  // getProductByCode,
  // getProducts,
  // searchProductsAdvanced,
  // filterProducts
}
