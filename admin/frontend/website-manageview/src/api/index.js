/**
 * API 模块统一导出
 */
import { authAPI } from './auth'
import { companyAPI } from './company'
import { uploadAPI } from './upload'
import { productAPI } from './product'
import { logsAPI } from './logs'
import { dashboardAPI } from './dashboard'
import http from './http'
import { API_CONFIG, API_ENDPOINTS } from './config'

export {
  authAPI,
  companyAPI,
  uploadAPI,
  productAPI,
  logsAPI,
  dashboardAPI,
  http,
  API_CONFIG,
  API_ENDPOINTS
}

export default {
  auth: authAPI,
  company: companyAPI,
  upload: uploadAPI,
  product: productAPI,
  logs: logsAPI,
  dashboard: dashboardAPI
}
