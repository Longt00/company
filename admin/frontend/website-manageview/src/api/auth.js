import http from './http'
import { API_ENDPOINTS } from './config'

/**
 * 认证相关 API
 */
export const authAPI = {
  /**
   * 用户登录
   * @param {Object} data - 登录信息
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @returns {Promise} 返回包含 token 和用户信息的 Promise
   */
  login(data) {
    return http.post(API_ENDPOINTS.AUTH.LOGIN, data)
  },

  /**
   * 用户注册
   * @param {Object} data - 注册信息
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @returns {Promise}
   */
  register(data) {
    return http.post(API_ENDPOINTS.AUTH.REGISTER, data)
  },
  /**
   * 获取当前用户信息
   * @returns {Promise}
   */
  getUserInfo() {
    return http.get(API_ENDPOINTS.AUTH.USER_INFO)
  },

  /**
   * 修改密码
   * @param {Object} data - 密码信息
   * @param {string} data.oldPassword - 旧密码
   * @param {string} data.newPassword - 新密码
   * @returns {Promise}
   */
  changePassword(data) {
    return http.post(API_ENDPOINTS.AUTH.CHANGE_PASSWORD, data)
  },

  /**
   * 检查用户名是否存在
   * @param {string} username - 用户名
   * @returns {Promise}
   */
  checkUsername(username) {
    return http.get(API_ENDPOINTS.AUTH.CHECK_USERNAME, {
      params: { username }
    })
  },

  /**
   * 检查邮箱是否存在
   * @param {string} email - 邮箱
   * @returns {Promise}
   */
  checkEmail(email) {
    return http.get(API_ENDPOINTS.AUTH.CHECK_EMAIL, {
      params: { email }
    })
  }
}

export default authAPI