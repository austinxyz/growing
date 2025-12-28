import apiClient from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export default {
  /**
   * 获取所有学习资源网站信息
   */
  getAllWebsites() {
    return apiClient.get('/learning-resource-websites')
  },

  /**
   * 获取所有网站的iframe支持情况
   * 返回格式: { "https://www.hellointerview.com": false, "https://labuladong.online": true }
   */
  getIframeSupportMap() {
    return apiClient.get('/learning-resource-websites/iframe-support')
  },

  /**
   * 根据名称获取网站信息
   */
  getWebsiteByName(name) {
    return apiClient.get(`/learning-resource-websites/${name}`)
  }
}
