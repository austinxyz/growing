import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: api.get('/api/resumes')
// ✅ CORRECT: api.get('/resumes')
// Note: interceptor returns response.data already

export const resumeApi = {
  // 获取用户所有简历版本
  getResumes() {
    return api.get('/resumes')
  },

  // 获取简历详情(包含所有子资源)
  getResumeById(id) {
    return api.get(`/resumes/${id}`)
  },

  // 创建新简历版本
  createResume(data) {
    return api.post('/resumes', data)
  },

  // 更新简历基本信息
  updateResume(id, data) {
    return api.put(`/resumes/${id}`, data)
  },

  // 删除简历版本
  deleteResume(id) {
    return api.delete(`/resumes/${id}`)
  },

  // 复制简历版本
  copyResume(id) {
    return api.post(`/resumes/${id}/copy`)
  },

  // 设置为默认简历
  setDefaultResume(id) {
    return api.put(`/resumes/${id}/set-default`)
  },

  // 导出简历 (P1, 暂不实现)
  exportResume(id, format) {
    return api.post(`/resumes/${id}/export`, { format })
  },

  // --- 子资源 API: 工作经历 ---
  getExperiences(resumeId) {
    return api.get(`/resumes/${resumeId}/experiences`)
  },

  createExperience(resumeId, data) {
    return api.post(`/resumes/${resumeId}/experiences`, data)
  },

  updateExperience(resumeId, experienceId, data) {
    return api.put(`/resumes/${resumeId}/experiences/${experienceId}`, data)
  },

  deleteExperience(resumeId, experienceId) {
    return api.delete(`/resumes/${resumeId}/experiences/${experienceId}`)
  },

  // --- 子资源 API: 技能列表 ---
  getSkills(resumeId) {
    return api.get(`/resumes/${resumeId}/skills`)
  },

  createSkill(resumeId, data) {
    return api.post(`/resumes/${resumeId}/skills`, data)
  },

  updateSkill(resumeId, skillId, data) {
    return api.put(`/resumes/${resumeId}/skills/${skillId}`, data)
  },

  deleteSkill(resumeId, skillId) {
    return api.delete(`/resumes/${resumeId}/skills/${skillId}`)
  },

  // --- 子资源 API: 教育背景 ---
  getEducation(resumeId) {
    return api.get(`/resumes/${resumeId}/education`)
  },

  createEducation(resumeId, data) {
    return api.post(`/resumes/${resumeId}/education`, data)
  },

  updateEducation(resumeId, educationId, data) {
    return api.put(`/resumes/${resumeId}/education/${educationId}`, data)
  },

  deleteEducation(resumeId, educationId) {
    return api.delete(`/resumes/${resumeId}/education/${educationId}`)
  },

  // --- 子资源 API: 培训证书 ---
  getCertifications(resumeId) {
    return api.get(`/resumes/${resumeId}/certifications`)
  },

  createCertification(resumeId, data) {
    return api.post(`/resumes/${resumeId}/certifications`, data)
  },

  updateCertification(resumeId, certificationId, data) {
    return api.put(`/resumes/${resumeId}/certifications/${certificationId}`, data)
  },

  deleteCertification(resumeId, certificationId) {
    return api.delete(`/resumes/${resumeId}/certifications/${certificationId}`)
  }
}
