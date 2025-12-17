import apiClient from './config'

/**
 * 获取公司基本信息
 * @returns {Promise<Object>} 公司基本信息
 */
export const getCompanyInfo = () => {
  return apiClient.get('/api/public/company/info')
}

/**
 * 获取公司联系方式
 * @returns {Promise<Object>} 公司联系方式
 */
export const getCompanyContact = () => {
  return apiClient.get('/api/public/company/contact')
}

/**
 * 获取公司资质信息
 * @returns {Promise<Object>} 公司资质信息
 */
export const getCompanyQualifications = () => {
  return apiClient.get('/api/public/company/qualifications')
}

/**
 * 获取公司团队信息
 * @returns {Promise<Object>} 公司团队信息
 */
export const getCompanyTeam = () => {
  return apiClient.get('/api/public/company/team')
}

/**
 * 获取核心团队信息
 * @returns {Promise<Object>} 核心团队信息
 */
export const getCoreTeam = () => {
  return apiClient.get('/api/public/company/team/core')
}

/**
 * 获取公司统计数据
 * @returns {Promise<Object>} 公司统计数据
 */
export const getCompanyStatistics = () => {
  return apiClient.get('/api/public/company/statistics')
}

/**
 * 获取公司Logo信息（公开接口）
 * @returns {Promise<Object>} 公司Logo信息
 * 接口地址：GET /api/company/public/logo
 */
export const getPublicCompanyLogo = () => {
  return apiClient.get('/api/company/public/logo')
}

/**
 * 获取公司WhatsApp联系方式（公开接口）
 * @returns {Promise<Object>} WhatsApp联系信息
 * 接口地址：GET /api/company/public/contact/whatsapp
 */
export const getPublicWhatsAppContact = () => {
  return apiClient.get('/api/company/public/contact/whatsapp')
}

/**
 * 获取首页背景图片（公开接口）
 * @returns {Promise<Object>} 首页背景图片信息
 * 接口地址：GET /api/public/media/category/background-image
 */
export const getPublicHomepageBackground = () => {
  return apiClient.get('/api/public/media/category/background-image')
}

// ============ 管理系统API接口 ============
// 基于管理系统对接展示前端API文档实现

export const MANAGEMENT_API_BASE_URL = ''  // 使用相对路径，通过 Nginx 代理

/**
 * 获取管理系统的认证Token
 * 这里假设Token存储在localStorage中，实际项目中可能需要更复杂的Token管理
 */
function getManagementAuthToken() {
  return localStorage.getItem('authToken') || sessionStorage.getItem('authToken')
}

/**
 * 创建带认证的请求头（管理系统专用）
 */
function createManagementAuthHeaders() {
  const token = getManagementAuthToken()
  const headers = {
    'Content-Type': 'application/json'
  }

  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  return headers
}

/**
 * 管理系统通用API请求方法
 */
export async function managementApiRequest(endpoint, options = {}) {
  const url = `${MANAGEMENT_API_BASE_URL}${endpoint}`
  const config = {
    headers: createManagementAuthHeaders(),
    ...options
  }

  try {
    const response = await fetch(url, config)

    // 检查响应状态
    if (!response.ok) {
      // 处理401未授权错误
      if (response.status === 401) {
        console.warn('Token已过期或无效，需要重新登录')
        // 这里可以触发登出逻辑或跳转到登录页
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
 * 公司信息管理API（管理系统专用）
 */
export const companyManagementApi = {
  /**
   * 获取公司信息（管理系统）
   * GET /api/company/admin/info
   * 需要JWT认证
   */
  async getCompanyInfoForManagement() {
    return await managementApiRequest('/api/company/admin/info', {
      method: 'GET'
    })
  },

  /**
   * 更新公司信息（管理系统）
   * PUT /api/company/admin/info
   * 需要JWT认证
   * @param {Object} companyInfo - 公司信息对象
   */
  async updateCompanyInfoForManagement(companyInfo) {
    return await managementApiRequest('/api/company/admin/info', {
      method: 'PUT',
      body: JSON.stringify(companyInfo)
    })
  },

  /**
   * 初始化公司信息（管理系统）
   * POST /api/company/admin/info/init
   * 需要JWT认证
   * @param {Object} companyInfo - 初始化公司信息对象
   */
  async initCompanyInfoForManagement(companyInfo) {
    return await managementApiRequest('/api/company/admin/info/init', {
      method: 'POST',
      body: JSON.stringify(companyInfo)
    })
  }
}

// 统一导出所有公司相关API
export default {
  // 原有的公开API
  getCompanyInfo,
  getCompanyContact,
  getCompanyQualifications,
  getCompanyTeam,
  getCoreTeam,
  getCompanyStatistics,
  getPublicCompanyLogo,
  getPublicWhatsAppContact,
  getPublicHomepageBackground,

  // 新增的管理系统API
  companyManagementApi,

  // 工具函数
  getManagementAuthToken,
  createManagementAuthHeaders,
  MANAGEMENT_API_BASE_URL
}