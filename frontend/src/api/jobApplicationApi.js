import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const jobApplicationApi = {
  // 获取所有职位申请
  getAllJobApplications() {
    return api.get('/job-applications')
  },

  // 获取公司的所有职位
  getJobsByCompany(companyId) {
    return api.get(`/job-applications/company/${companyId}`)
  },

  // 获取职位详情
  getJobApplicationById(id) {
    return api.get(`/job-applications/${id}`)
  },

  // 创建职位申请
  createJobApplication(data) {
    return api.post('/job-applications', data)
  },

  // 更新职位信息
  updateJobApplication(id, data) {
    return api.put(`/job-applications/${id}`, data)
  },

  // 删除职位
  deleteJobApplication(id) {
    return api.delete(`/job-applications/${id}`)
  },

  // 更新申请状态
  updateStatus(id, status) {
    return api.put(`/job-applications/${id}/status`, { status })
  },

  // --- 面试流程阶段 ---
  getInterviewStages(jobId) {
    return api.get(`/job-applications/${jobId}/stages`)
  },

  createInterviewStage(jobId, data) {
    return api.post(`/job-applications/${jobId}/stages`, data)
  },

  updateInterviewStage(stageId, data) {
    return api.put(`/interview-stages/${stageId}`, data)
  },

  deleteInterviewStage(stageId) {
    return api.delete(`/interview-stages/${stageId}`)
  }
}
