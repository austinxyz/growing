import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const projectApi = {
  // 获取用户所有项目经验
  getProjects() {
    return api.get('/project-experiences')
  },

  // 获取项目详情
  getProjectById(id) {
    return api.get(`/project-experiences/${id}`)
  },

  // 创建项目经验
  createProject(data) {
    return api.post('/project-experiences', data)
  },

  // 更新项目经验
  updateProject(id, data) {
    return api.put(`/project-experiences/${id}`, data)
  },

  // 删除项目经验
  deleteProject(id) {
    return api.delete(`/project-experiences/${id}`)
  },

  // 获取相关 Behavioral 试题 (P1, 暂不实现)
  getRelatedQuestions(id) {
    return api.get(`/project-experiences/${id}/related-questions`)
  },

  // 获取所有 Behavioral Focus Areas（用于项目关联选择）
  getBehavioralFocusAreas() {
    return api.get('/skills/behavioral/focus-areas')
  }
}
