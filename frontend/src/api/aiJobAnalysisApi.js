import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const aiJobAnalysisApi = {
  // 生成AI分析Prompt
  generatePrompt(jobApplicationId) {
    return api.post('/ai-job-analysis/generate-prompt', { jobApplicationId })
  },

  // 保存AI分析结果（支持upsert）
  saveAnalysis(data) {
    return api.post('/ai-job-analysis/save', data)
  },

  // 查询Job Application的所有分析记录
  getByJobApplication(jobApplicationId) {
    return api.get(`/ai-job-analysis/job-application/${jobApplicationId}`)
  },

  // 获取单个分析详情
  getById(id) {
    return api.get(`/ai-job-analysis/${id}`)
  },

  // 删除分析记录
  deleteAnalysis(id) {
    return api.delete(`/ai-job-analysis/${id}`)
  }
}
