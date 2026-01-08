import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const interviewChecklistApi = {
  // 获取某个面试阶段的所有清单项
  getByStage(stageId) {
    return api.get(`/interview-preparation-checklist/stage/${stageId}`)
  },

  // 创建清单项
  create(data) {
    return api.post('/interview-preparation-checklist', data)
  },

  // 更新清单项
  update(id, data) {
    return api.put(`/interview-preparation-checklist/${id}`, data)
  },

  // 删除清单项
  delete(id) {
    return api.delete(`/interview-preparation-checklist/${id}`)
  },

  // 批量创建清单项
  createBatch(data) {
    return api.post('/interview-preparation-checklist/batch', data)
  }
}
