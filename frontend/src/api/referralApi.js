import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const referralApi = {
  // 获取公司的所有推荐人
  getReferralsByCompany(companyId) {
    return api.get(`/referrals/company/${companyId}`)
  },

  // 获取推荐人详情
  getReferralById(id) {
    return api.get(`/referrals/${id}`)
  },

  // 创建推荐人
  createReferral(data) {
    return api.post('/referrals', data)
  },

  // 更新推荐人
  updateReferral(id, data) {
    return api.put(`/referrals/${id}`, data)
  },

  // 删除推荐人
  deleteReferral(id) {
    return api.delete(`/referrals/${id}`)
  },

  // 更新推荐状态
  updateStatus(id, status) {
    return api.put(`/referrals/${id}/status`, { status })
  }
}
