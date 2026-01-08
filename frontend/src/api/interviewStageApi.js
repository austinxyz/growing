import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const interviewStageApi = {
  // 获取Job Application的所有面试阶段
  getByJobApplication(jobApplicationId) {
    return api.get(`/interview-stages/job-application/${jobApplicationId}`)
  },

  // 创建面试阶段
  create(data) {
    return api.post('/interview-stages', data)
  },

  // 更新面试阶段
  update(id, data) {
    return api.put(`/interview-stages/${id}`, data)
  },

  // 删除面试阶段
  delete(id) {
    return api.delete(`/interview-stages/${id}`)
  },

  // 获取单个面试阶段
  getById(id) {
    return api.get(`/interview-stages/${id}`)
  }
}
