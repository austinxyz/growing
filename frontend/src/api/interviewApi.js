import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const interviewApi = {
  // 获取职位的所有面试记录
  getInterviewsByJob(jobId) {
    return api.get(`/interview-records/job/${jobId}`)
  },

  // 获取面试记录详情
  getInterviewById(id) {
    return api.get(`/interview-records/${id}`)
  },

  // 创建面试记录
  createInterview(data) {
    return api.post('/interview-records', data)
  },

  // 更新面试记录
  updateInterview(id, data) {
    return api.put(`/interview-records/${id}`, data)
  },

  // 删除面试记录
  deleteInterview(id) {
    return api.delete(`/interview-records/${id}`)
  },

  // --- 面试问题 ---
  getQuestions(recordId) {
    return api.get(`/interview-records/${recordId}/questions`)
  },

  createQuestion(recordId, data) {
    return api.post(`/interview-records/${recordId}/questions`, data)
  },

  updateQuestion(questionId, data) {
    return api.put(`/interview-questions/${questionId}`, data)
  },

  deleteQuestion(questionId) {
    return api.delete(`/interview-questions/${questionId}`)
  },

  // --- 面试反馈 ---
  getFeedback(recordId) {
    return api.get(`/interview-records/${recordId}/feedback`)
  },

  createFeedback(recordId, data) {
    return api.post(`/interview-records/${recordId}/feedback`, data)
  },

  updateFeedback(feedbackId, data) {
    return api.put(`/interview-feedback/${feedbackId}`, data)
  },

  deleteFeedback(feedbackId) {
    return api.delete(`/interview-feedback/${feedbackId}`)
  }
}
