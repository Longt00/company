import http from './http'
import { API_ENDPOINTS } from './config'

/**
 * 公司信息相关 API
 */
export const companyAPI = {
  /**
   * 获取公司基本信息（公开）
   * @returns {Promise}
   */
  getPublicInfo() {
    return http.get(API_ENDPOINTS.COMPANY.INFO)
  },

  /**
   * 获取公司基本信息（管理）
   * @returns {Promise}
   */
  getAdminInfo() {
    return http.get(API_ENDPOINTS.COMPANY.ADMIN_INFO)
  },

  /**
   * 更新公司基本信息
   * @param {Object} data - 公司信息
   * @param {string} data.companyName - 公司名称
   * @param {string} data.companyDescription - 公司简介
   * @param {string} data.companyPhone - 联系电话
   * @param {string} data.companyEmail - 联系邮箱
   * @param {string} data.companyWebsite - 公司网站
   * @param {string} data.companyAddress - 公司地址
   * @returns {Promise}
   */
  updateInfo(data) {
    return http.put(API_ENDPOINTS.COMPANY.ADMIN_INFO, data)
  },

  /**
   * 初始化公司信息
   * @param {Object} data - 公司信息
   * @returns {Promise}
   */
  initInfo(data) {
    return http.post(API_ENDPOINTS.COMPANY.INIT, data)
  },

  /**
   * 检查公司信息是否存在
   * @returns {Promise}
   */
  checkInfo() {
    return http.get(API_ENDPOINTS.COMPANY.CHECK)
  }
}

export default companyAPI