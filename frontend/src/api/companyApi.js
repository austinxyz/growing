import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const companyApi = {
  // 获取用户所有公司
  getCompanies() {
    return api.get('/companies')
  },

  // 获取公司详情
  getCompanyById(id) {
    return api.get(`/companies/${id}`)
  },

  // 创建公司档案
  createCompany(data) {
    return api.post('/companies', data)
  },

  // 更新公司信息
  updateCompany(id, data) {
    return api.put(`/companies/${id}`, data)
  },

  // 删除公司(级联删除)
  deleteCompany(id) {
    return api.delete(`/companies/${id}`)
  },

  // --- 公司链接 ---
  getCompanyLinks(companyId) {
    return api.get(`/companies/${companyId}/links`)
  },

  createCompanyLink(companyId, data) {
    return api.post(`/companies/${companyId}/links`, data)
  },

  updateCompanyLink(companyId, linkId, data) {
    return api.put(`/companies/${companyId}/links/${linkId}`, data)
  },

  deleteCompanyLink(companyId, linkId) {
    return api.delete(`/companies/${companyId}/links/${linkId}`)
  }
}
