import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const jobApplicationReferralApi = {
  // 获取职位的所有关联人脉
  getReferralsByJob(jobApplicationId) {
    return api.get(`/job-applications/${jobApplicationId}/referrals`)
  },

  // 添加职位-人脉关联
  addReferralToJob(jobApplicationId, data) {
    return api.post(`/job-applications/${jobApplicationId}/referrals`, data)
  },

  // 更新职位-人脉关联
  updateJobReferral(jobApplicationId, id, data) {
    return api.put(`/job-applications/${jobApplicationId}/referrals/${id}`, data)
  },

  // 删除职位-人脉关联
  removeReferralFromJob(jobApplicationId, id) {
    return api.delete(`/job-applications/${jobApplicationId}/referrals/${id}`)
  }
}
