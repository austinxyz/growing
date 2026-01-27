// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

import apiClient from './index'

export default {
  /**
   * 获取求职管理统计数据
   */
  getStats() {
    return apiClient.get('/job-search/stats')
  }
}
