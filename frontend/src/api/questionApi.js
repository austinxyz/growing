import api from './index'

// 用户端API
export const questionApi = {
  // 获取Focus Area下的试题列表（公共 + 用户自己的）
  getQuestionsByFocusArea(focusAreaId, params = {}) {
    return api.get(`/questions/focus-areas/${focusAreaId}`, { params })
  },

  // 获取试题详情（包含答案要求、Red Flags和用户笔记）
  getQuestionById(id) {
    return api.get(`/questions/${id}`)
  },

  // 用户添加个人试题
  createQuestion(data) {
    return api.post('/questions', data)
  },

  // 更新试题（只能更新自己创建的）
  updateQuestion(id, data) {
    return api.put(`/questions/${id}`, data)
  },

  // 删除试题（只能删除自己创建的）
  deleteQuestion(id) {
    return api.delete(`/questions/${id}`)
  },

  // 获取用户对某试题的笔记
  getUserNote(questionId) {
    return api.get(`/questions/${questionId}/note`)
  },

  // 为试题添加/更新笔记（UPSERT逻辑）
  saveOrUpdateNote(questionId, data) {
    return api.post(`/questions/${questionId}/note`, data)
  },

  // 删除用户对某试题的笔记
  deleteUserNote(questionId) {
    return api.delete(`/questions/${questionId}/note`)
  }
}

// 管理员API
export const adminQuestionApi = {
  // 获取所有试题（支持分页和筛选）
  getAllQuestions(params = {}) {
    return api.get('/admin/questions', { params })
  },

  // 管理员添加公共试题
  createQuestion(data) {
    return api.post('/admin/questions', data)
  },

  // 更新任意试题
  updateQuestion(id, data) {
    return api.put(`/admin/questions/${id}`, data)
  },

  // 删除任意试题
  deleteQuestion(id) {
    return api.delete(`/admin/questions/${id}`)
  }
}
