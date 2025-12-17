// API 配置
const envUrl = import.meta.env.VITE_API_BASE_URL

function getApiBaseUrl() {
  // 优先使用环境变量
  if (envUrl) {
    return envUrl
  }
  
  // 生产环境智能检测
  if (import.meta.env.PROD) {
    // 如果部署在服务器上，使用相对路径通过nginx代理
    const currentHost = window.location.hostname
    if (currentHost === '39.97.60.191') {
      console.log('🔧 检测到同源服务器部署，使用相对路径 /api')
      return '/api' // 通过nginx代理
    } else {
      console.log('🔧 检测到跨域部署，使用完整地址')
      return 'http://39.97.60.191:33380'
    }
  }
  
  // 开发环境默认值
  return 'http://localhost:33380'
}

const API_BASE_URL = getApiBaseUrl()

// 详细调试信息
console.log('🔧 环境变量调试:')
console.log('  - import.meta.env.VITE_API_BASE_URL:', envUrl)
console.log('  - 最终 API_BASE_URL:', API_BASE_URL)
console.log('  - 当前域名:', window.location.hostname)
console.log('  - 是否生产环境:', import.meta.env.PROD)
console.log('  - import.meta.env对象:', JSON.stringify(import.meta.env, null, 2))

// 强制使用33380端口（调试用）
if (API_BASE_URL.includes('8081')) {
  console.error('❌ 严重问题：检测到8081端口配置！')
  console.error('环境变量值:', envUrl)
  console.error('正在强制修正为33380端口...')

  // 强制修正
  window.API_BASE_URL_FIXED = 'http://localhost:33380'
} else {
  console.log('✅ 端口配置正确，使用33380端口')
  window.API_BASE_URL_FIXED = API_BASE_URL
}

export const API_CONFIG = {
  // 强制使用33380端口（临时解决方案）
  baseURL: API_BASE_URL.includes('8081') ? 'http://localhost:33380' : API_BASE_URL,
  timeout: 300000, // 增加到5分钟（300秒），处理大文件上传
  headers: {
    'Content-Type': 'application/json'
  }
}

// 验证修正结果
if (API_CONFIG.baseURL.includes('8081')) {
  console.error('❌ 修正失败！API_CONFIG.baseURL 仍然是8081:', API_CONFIG.baseURL)
} else {
  console.log('✅ API_CONFIG.baseURL 修正成功:', API_CONFIG.baseURL)
}

// API 端点
export const API_ENDPOINTS = {
  // 认证相关
  AUTH: {
    LOGIN: '/api/auth/login',
    REGISTER: '/api/auth/register',
    USER_INFO: '/api/auth/user-info',
    CHANGE_PASSWORD: '/api/auth/change-password',
    CHECK_USERNAME: '/api/auth/check-username',
    CHECK_EMAIL: '/api/auth/check-email'
  },
  
  // 公司信息相关
  COMPANY: {
    INFO: '/api/company/info',
    ADMIN_INFO: '/api/company/admin/info',
    INIT: '/api/company/admin/info/init',
    CHECK: '/api/company/admin/info/check'
  },
  
  // 产品相关
  PRODUCTS: {
    LIST: '/api/products',
    DETAIL: '/api/products',
    CREATE: '/api/products',
    UPDATE: '/api/products',
    DELETE: '/api/products',
    ACTIVATE: '/api/products/{id}/activate',
    DEACTIVATE: '/api/products/{id}/deactivate',
    STOCK: '/api/products/{id}/stock'
  },
  
  // 文件上传相关
  UPLOAD: {
    BASE: '/api/admin/upload',
    LOGO: '/api/admin/upload/logo',
    VIDEO: '/api/admin/upload/video',
    VIDEO_ENHANCED: '/api/admin/upload/video/enhanced',
    IMAGE: '/api/admin/upload/image/enhanced',
    PRODUCT_IMAGE: '/api/admin/upload/product/image',
    BATCH_IMAGES: '/api/admin/upload/images/batch',
    DELETE: '/api/admin/upload/file',
    REFRESH: '/api/admin/upload/files/refresh'
  },
  
  // 系统健康检查
  HEALTH: {
    CHECK: '/api/health',
    ALIVE: '/api/alive'
  },
  
  // 操作日志相关
  LOGS: {
    RECENT: '/api/admin/logs/recent',
    MY: '/api/admin/logs/my',
    PAGE: '/api/admin/logs/page',
    MODULE: '/api/admin/logs/module',
    TYPE: '/api/admin/logs/type',
    USER: '/api/admin/logs/user',
    TIME_RANGE: '/api/admin/logs/time-range',
    SEARCH: '/api/admin/logs/search',
    STATISTICS: '/api/admin/logs/statistics',
    FILTER: '/api/admin/logs/filter',
    CLEANUP: '/api/admin/logs/cleanup'
  },
  
  // 仪表盘相关
  DASHBOARD: {
    STATISTICS: '/api/dashboard/statistics'
  }
}

export default {
  API_CONFIG,
  API_ENDPOINTS
}