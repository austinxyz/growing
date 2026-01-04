import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const managementApi = {
  // 获取用户所有管理经验
  getExperiences() {
    return api.get('/management-experiences')
  },

  // 获取管理经验详情
  getExperienceById(id) {
    return api.get(`/management-experiences/${id}`)
  },

  // 创建管理经验
  createExperience(data) {
    return api.post('/management-experiences', data)
  },

  // 更新管理经验
  updateExperience(id, data) {
    return api.put(`/management-experiences/${id}`, data)
  },

  // 删除管理经验
  deleteExperience(id) {
    return api.delete(`/management-experiences/${id}`)
  },

  // 获取相关 People Management 试题 (P1, 暂不实现)
  getRelatedQuestions(id) {
    return api.get(`/management-experiences/${id}/related-questions`)
  }
}
