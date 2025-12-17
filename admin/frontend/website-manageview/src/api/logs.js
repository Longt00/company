import http from './http'
import { API_ENDPOINTS } from './config'

/**
 * 操作日志相关 API
 */
export const logsAPI = {
  /**
   * 获取最近的操作日志
   * @param {number} limit - 限制数量，默认10
   * @returns {Promise}
   */
  getRecentLogs(limit = 10) {
    return http.get(API_ENDPOINTS.LOGS.RECENT, {
      params: { limit }
    })
  },

  /**
   * 获取当前用户的操作日志
   * @returns {Promise}
   */
  getMyLogs() {
    return http.get(API_ENDPOINTS.LOGS.MY)
  },

  /**
   * 分页查询操作日志
   * @param {number} page - 页码，从0开始
   * @param {number} size - 每页大小
   * @returns {Promise}
   */
  getLogsPage(page = 0, size = 20) {
    return http.get(API_ENDPOINTS.LOGS.PAGE, {
      params: { page, size }
    })
  },

  /**
   * 根据操作模块查询日志
   * @param {string} operationModule - 操作模块
   * @param {number} page - 页码
   * @param {number} size - 每页大小
   * @returns {Promise}
   */
  getLogsByModule(operationModule, page = 0, size = 20) {
    return http.get(`${API_ENDPOINTS.LOGS.MODULE}/${operationModule}`, {
      params: { page, size }
    })
  },

  /**
   * 根据操作类型查询日志
   * @param {string} operationType - 操作类型
   * @param {number} page - 页码
   * @param {number} size - 每页大小
   * @returns {Promise}
   */
  getLogsByType(operationType, page = 0, size = 20) {
    return http.get(`${API_ENDPOINTS.LOGS.TYPE}/${operationType}`, {
      params: { page, size }
    })
  },

  /**
   * 根据用户名查询日志
   * @param {string} username - 用户名
   * @param {number} page - 页码
   * @param {number} size - 每页大小
   * @returns {Promise}
   */
  getLogsByUsername(username, page = 0, size = 20) {
    return http.get(`${API_ENDPOINTS.LOGS.USER}/${username}`, {
      params: { page, size }
    })
  },

  /**
   * 根据时间范围查询日志
   * @param {string} startTime - 开始时间（ISO格式）
   * @param {string} endTime - 结束时间（ISO格式）
   * @param {number} page - 页码
   * @param {number} size - 每页大小
   * @returns {Promise}
   */
  getLogsByTimeRange(startTime, endTime, page = 0, size = 20) {
    return http.get(API_ENDPOINTS.LOGS.TIME_RANGE, {
      params: { startTime, endTime, page, size }
    })
  },

  /**
   * 搜索操作日志
   * @param {string} keyword - 关键词
   * @param {number} page - 页码
   * @param {number} size - 每页大小
   * @returns {Promise}
   */
  searchLogs(keyword, page = 0, size = 20) {
    return http.get(API_ENDPOINTS.LOGS.SEARCH, {
      params: { keyword, page, size }
    })
  },

  /**
   * 获取操作统计信息
   * @returns {Promise}
   */
  getStatistics() {
    return http.get(API_ENDPOINTS.LOGS.STATISTICS)
  },

  /**
   * 复合查询操作日志
   * @param {Object} filters - 过滤条件
   * @param {number} page - 页码
   * @param {number} size - 每页大小
   * @returns {Promise}
   */
  getLogsByConditions(filters = {}, page = 0, size = 20) {
    return http.get(API_ENDPOINTS.LOGS.FILTER, {
      params: { ...filters, page, size }
    })
  },

  /**
   * 清理过期日志
   * @param {number} days - 保留天数
   * @returns {Promise}
   */
  cleanExpiredLogs(days) {
    return http.delete(API_ENDPOINTS.LOGS.CLEANUP, {
      params: { days }
    })
  }
}

export default logsAPI
