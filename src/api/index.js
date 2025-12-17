// 统一导出所有API服务
import apiClient from './config.js'
import companyAPI from './company.js'
import productsAPI from './products.js'
import { healthCheck } from './config.js'

// 导出API配置
export { API_CONFIG } from './config'
export { default as apiClient } from './config'

// 导出API模块
export { default as companyAPI } from './company'
export { default as productsAPI } from './products'

// 导出健康检查
export { healthCheck }

// 便捷导出 - 公司信息相关
export const {
  getCompanyInfo,
  getCompanyContact,
  getCompanyQualifications,
  getCompanyTeam,
  getCoreTeam,
  getCompanyStatistics
} = companyAPI

// 便捷导出 - 产品信息相关
export const {
  getActiveProducts,
  // getFeaturedProducts,  // 暂未实现
  // getProductById,       // 暂未实现
  // getProductByCode,     // 暂未实现
  // getProducts,          // 暂未实现
  searchProducts,
  getProductsByCategory,
  // getProductStatistics, // 暂未实现
  getProductCategories
  // filterProducts        // 暂未实现
} = productsAPI