import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const interviewStageApi = {
  // 获取职位的所有面试阶段
  getStagesByJob(jobId) {
    return api.get(`/job-applications/${jobId}/stages`)
  },

  // 创建面试阶段
  createStage(jobId, data) {
    return api.post(`/job-applications/${jobId}/stages`, data)
  },

  // 更新面试阶段
  updateStage(stageId, data) {
    return api.put(`/interview-stages/${stageId}`, data)
  },

  // 删除面试阶段
  deleteStage(stageId) {
    return api.delete(`/interview-stages/${stageId}`)
  }
}
