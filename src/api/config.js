// API配置文件
// 支持管理后台API连接和本地数据回退

export const API_CONFIG = {
  // 后端API基础URL - 使用相对路径，通过 Nginx 代理
  BASE_URL: '',
  TIMEOUT: 10000,

  // 重试配置
  RETRY_ATTEMPTS: 3,
  RETRY_DELAY: 1000
}

// 创建真实的API客户端
import axios from 'axios'

// 获取认证Token
function getAuthToken() {
  return localStorage.getItem('authToken') || sessionStorage.getItem('authToken')
}

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  timeout: API_CONFIG.TIMEOUT,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加认证token
apiClient.interceptors.request.use(
  (config) => {
    const token = getAuthToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  async (error) => {
    const originalRequest = error.config

    // 如果是401错误，尝试刷新token或跳转登录
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      console.warn('Token已过期或无效，尝试重新获取')
      // 这里可以实现token刷新逻辑
    }

    // 直接返回错误，不再回退到假数据
    console.error('❌ API请求失败:', error.message)
    return Promise.reject(error)
  }
)


// 导出真实的API客户端
export default apiClient
export { apiClient }