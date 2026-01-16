// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ✅ CORRECT: apiClient.get('/resources/search')

import apiClient from './index'

/**
 * 资源搜索API
 * 用于TODO功能搜索系统内部资源
 */
export default {
  /**
   * 搜索系统资源
   * @param {String} resourceType - 资源类型 (Question/LearningResource/SystemDesignCase/Project/ManagementExperience/ALL)
   * @param {String} keyword - 搜索关键词
   * @returns {Promise<Array>} 资源列表
   */
  searchResources(resourceType = 'ALL', keyword = '') {
    return apiClient.get('/resources/search', {
      params: {
        resourceType,
        keyword
      }
    })
  }
}
