import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const interviewRecordApi = {
  // 获取职位的所有面试记录
  getRecordsByJob(jobId) {
    return api.get(`/interview-records/application/${jobId}`)
  },

  // 获取面试记录详情（包含questions和feedback）
  getRecordById(id) {
    return api.get(`/interview-records/${id}`)
  },

  // 创建面试记录
  createRecord(data) {
    return api.post('/interview-records', data)
  },

  // 更新面试记录
  updateRecord(id, data) {
    return api.put(`/interview-records/${id}`, data)
  },

  // 删除面试记录
  deleteRecord(id) {
    return api.delete(`/interview-records/${id}`)
  }
}
