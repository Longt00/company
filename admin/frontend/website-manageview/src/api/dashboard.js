import http from './http'
import { API_ENDPOINTS } from './config'

/**
 * 仪表盘相关 API
 */
export const dashboardAPI = {
  /**
   * 获取仪表盘统计信息
   * @returns {Promise}
   */
  getStatistics() {
    return http.get(API_ENDPOINTS.DASHBOARD.STATISTICS)
  }
}

export default dashboardAPI
