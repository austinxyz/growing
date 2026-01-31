import apiClient from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/template-questions')
// ✅ CORRECT: apiClient.get('/template-questions')

export default {
  // 用户端API - 查询模板关联的题目
  getQuestionsByTemplate(templateId) {
    return apiClient.get(`/template-questions/template/${templateId}`)
  },

  // 用户端API - 查询题目关联的模板
  getTemplatesByQuestion(questionId) {
    return apiClient.get(`/template-questions/question/${questionId}`)
  },

  // 管理员API - 添加模板-题目关联
  addTemplateQuestion(templateId, questionId) {
    return apiClient.post('/admin/template-questions', {
      templateId,
      questionId
    })
  },

  // 管理员API - 删除模板-题目关联
  removeTemplateQuestion(templateId, questionId) {
    return apiClient.delete(`/admin/template-questions/${templateId}/${questionId}`)
  },

  // 管理员API - 获取模板的关联题目（管理端）
  getQuestionsByTemplateAdmin(templateId) {
    return apiClient.get(`/admin/template-questions/template/${templateId}`)
  }
}
