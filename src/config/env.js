// 环境配置统一管理
const ENV_CONFIG = {
  // API基础URL - 使用相对路径，通过 Nginx 代理
  API_BASE_URL: import.meta.env?.VITE_API_BASE_URL || '',

  // 静态资源基础URL - 使用相对路径，通过 Nginx 代理
  STATIC_BASE_URL: import.meta.env?.VITE_STATIC_BASE_URL || '',

  // 应用基础路径
  BASE_PATH: '/frontend/',

  // 是否为开发环境
  IS_DEVELOPMENT: import.meta.env?.MODE === 'development'
}

// 导出配置
export default ENV_CONFIG

// 导出便捷函数
export const getApiUrl = (path) => {
  return `${ENV_CONFIG.API_BASE_URL}${path}`
}

export const getStaticUrl = (path) => {
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  return `${ENV_CONFIG.STATIC_BASE_URL}${path}`
}