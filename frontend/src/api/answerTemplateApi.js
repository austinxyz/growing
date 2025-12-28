// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/admin/answer-templates')
// ✅ CORRECT: apiClient.get('/admin/answer-templates')
//
// Note: interceptor returns response.data already
// ❌ WRONG: const response = await api.getTemplates(); return response.data
// ✅ CORRECT: const data = await api.getTemplates(); return data

import api from './index'

const answerTemplateApi = {
  /**
   * 获取所有答题模版
   * GET /admin/answer-templates
   */
  getAllTemplates() {
    return api.get('/admin/answer-templates')
  },

  /**
   * 获取单个模版详情
   * GET /admin/answer-templates/{id}
   */
  getTemplate(id) {
    return api.get(`/admin/answer-templates/${id}`)
  },

  /**
   * 创建答题模版
   * POST /admin/answer-templates
   * @param {Object} data - { templateName, description, templateFields: [{key, label, placeholder}] }
   */
  createTemplate(data) {
    return api.post('/admin/answer-templates', data)
  },

  /**
   * 更新答题模版
   * PUT /admin/answer-templates/{id}
   * @param {Number} id
   * @param {Object} data - { templateName, description, templateFields }
   */
  updateTemplate(id, data) {
    return api.put(`/admin/answer-templates/${id}`, data)
  },

  /**
   * 删除答题模版
   * DELETE /admin/answer-templates/{id}
   * ⚠️ 会级联删除skill_templates关联
   */
  deleteTemplate(id) {
    return api.delete(`/admin/answer-templates/${id}`)
  }
}

export default answerTemplateApi
