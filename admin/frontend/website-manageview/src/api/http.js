import axios from 'axios'
import { API_CONFIG } from './config'
import router from '../router'
import { getToken, removeToken } from '../utils/tokenManager'

// 创建 axios 实例
const http = axios.create({
  baseURL: API_CONFIG.baseURL,
  timeout: API_CONFIG.timeout,
  headers: API_CONFIG.headers
})

// 请求拦截器
http.interceptors.request.use(
  config => {
    // 从 tokenManager 获取 token
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 处理文件上传请求 - 移除默认的Content-Type
    if (config.data instanceof FormData) {
      // 对于FormData，让浏览器自动设置Content-Type，包含boundary
      delete config.headers['Content-Type']
      console.log('📁 检测到FormData请求，移除Content-Type头')
    }

    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
http.interceptors.response.use(
  response => {
    // 统一处理响应数据
    const res = response.data
    // 业务成功
    if (res.code === 200) {
      // 为了兼容性，添加 success 字段
      res.success = true
      return res
    }

    // 业务失败
    res.success = false
    console.error('业务错误:', res.message)
    
    // 创建一个自定义错误对象，包含响应信息
    const error = new Error(res.message || '请求失败')
    error.response = {
      data: res,
      status: response.status
    }
    error.businessError = true // 标记为业务错误
    return Promise.reject(error)
  },
  error => {
    // HTTP 错误处理
    if (error.response) {
      const status = error.response.status
      
      switch (status) {
        case 401:
          // 未认证，清除 token 并跳转到登录页
          removeToken()
          router.push('/login')
          console.error('未授权，请重新登录')
          break
        case 403:
          console.error('权限不足')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器内部错误')
          break
        default:
          console.error(`HTTP 错误: ${status}`)
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      console.error('网络错误，请检查您的网络连接')
    } else {
      // 其他错误
      console.error('请求配置错误:', error.message)
    }
    
    return Promise.reject(error)
  }
)

export default http