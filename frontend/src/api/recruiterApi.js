import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const recruiterApi = {
  // 获取公司的所有Recruiter
  getRecruitersByCompany(companyId) {
    return api.get(`/recruiters/company/${companyId}`)
  },

  // 获取Recruiter详情
  getRecruiterById(id) {
    return api.get(`/recruiters/${id}`)
  },

  // 创建Recruiter
  createRecruiter(data) {
    return api.post('/recruiters', data)
  },

  // 更新Recruiter
  updateRecruiter(id, data) {
    return api.put(`/recruiters/${id}`, data)
  },

  // 删除Recruiter
  deleteRecruiter(id) {
    return api.delete(`/recruiters/${id}`)
  },

  // --- 沟通记录 ---
  getCommunications(recruiterId) {
    return api.get(`/recruiters/${recruiterId}/communications`)
  },

  createCommunication(recruiterId, data) {
    return api.post(`/recruiters/${recruiterId}/communications`, data)
  },

  updateCommunication(communicationId, data) {
    return api.put(`/recruiter-communications/${communicationId}`, data)
  },

  deleteCommunication(communicationId) {
    return api.delete(`/recruiter-communications/${communicationId}`)
  }
}
