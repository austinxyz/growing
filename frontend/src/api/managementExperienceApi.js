// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already
import apiClient from './index'

export const managementExperienceApi = {
  // 获取所有管理经验
  getExperiences: () => apiClient.get('/management-experiences'),

  // 获取单个管理经验详情
  getExperienceById: (id) => apiClient.get(`/management-experiences/${id}`),

  // 创建管理经验
  createExperience: (data) => apiClient.post('/management-experiences', data),

  // 更新管理经验
  updateExperience: (id, data) => apiClient.put(`/management-experiences/${id}`, data),

  // 删除管理经验
  deleteExperience: (id) => apiClient.delete(`/management-experiences/${id}`)
}
