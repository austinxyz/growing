import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

export const resumeAnalysisApi = {
  // 分析简历与职位的匹配度 (通过jobApplicationId - 已申请的职位)
  analyzeResumeByApplication(jobApplicationId) {
    return api.get(`/resume-analysis/job-application/${jobApplicationId}`)
  },

  // 分析简历与职位的匹配度 (通过jobId - 未申请的职位)
  analyzeResumeByJob(jobId) {
    return api.get(`/resume-analysis/job/${jobId}`)
  }
}
