// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/system-design/cases')
// ✅ CORRECT: apiClient.get('/system-design/cases')

import apiClient from './index'

// ==================== 管理员API ====================

/**
 * 获取所有案例（管理员）
 */
export const getAllCases = () => {
  return apiClient.get('/admin/system-design-cases')
}

/**
 * 获取案例详情（管理员）
 */
export const getCaseById = (id) => {
  return apiClient.get(`/admin/system-design-cases/${id}`)
}

/**
 * 创建案例
 */
export const createCase = (data) => {
  return apiClient.post('/admin/system-design-cases', data)
}

/**
 * 更新案例
 */
export const updateCase = (id, data) => {
  return apiClient.put(`/admin/system-design-cases/${id}`, data)
}

/**
 * 删除案例
 */
export const deleteCase = (id) => {
  return apiClient.delete(`/admin/system-design-cases/${id}`)
}

// ==================== 学习资源API ====================

/**
 * 获取案例的学习资源
 */
export const getResourcesByCaseId = (caseId) => {
  return apiClient.get(`/admin/system-design-cases/${caseId}/resources`)
}

/**
 * 创建学习资源
 */
export const createResource = (caseId, data) => {
  return apiClient.post(`/admin/system-design-cases/${caseId}/resources`, data)
}

/**
 * 更新学习资源
 */
export const updateResource = (caseId, resourceId, data) => {
  return apiClient.put(`/admin/system-design-cases/${caseId}/resources/${resourceId}`, data)
}

/**
 * 删除学习资源
 */
export const deleteResource = (caseId, resourceId) => {
  return apiClient.delete(`/admin/system-design-cases/${caseId}/resources/${resourceId}`)
}

// ==================== 参考答案API ====================

/**
 * 获取案例的参考答案
 */
export const getSolutionsByCaseId = (caseId) => {
  return apiClient.get(`/admin/system-design-cases/${caseId}/solutions`)
}

/**
 * 获取参考答案详情（含配图）
 */
export const getSolutionById = (solutionId) => {
  return apiClient.get(`/admin/system-design-cases/solutions/${solutionId}`)
}

/**
 * 创建参考答案
 */
export const createSolution = (caseId, data) => {
  return apiClient.post(`/admin/system-design-cases/${caseId}/solutions`, data)
}

/**
 * 更新参考答案
 */
export const updateSolution = (solutionId, data) => {
  return apiClient.put(`/admin/system-design-cases/solutions/${solutionId}`, data)
}

/**
 * 删除参考答案
 */
export const deleteSolution = (solutionId) => {
  return apiClient.delete(`/admin/system-design-cases/solutions/${solutionId}`)
}

// ==================== 配图API ====================

/**
 * 获取参考答案的配图
 */
export const getDiagramsBySolutionId = (solutionId) => {
  return apiClient.get(`/admin/system-design-cases/solutions/${solutionId}/diagrams`)
}

/**
 * 创建配图
 */
export const createDiagram = (solutionId, data) => {
  return apiClient.post(`/admin/system-design-cases/solutions/${solutionId}/diagrams`, data)
}

/**
 * 更新配图
 */
export const updateDiagram = (diagramId, data) => {
  return apiClient.put(`/admin/system-design-cases/diagrams/${diagramId}`, data)
}

/**
 * 删除配图
 */
export const deleteDiagram = (diagramId) => {
  return apiClient.delete(`/admin/system-design-cases/diagrams/${diagramId}`)
}

// ==================== 用户端API ====================

/**
 * 获取所有官方案例
 */
export const getOfficialCases = () => {
  return apiClient.get('/system-design/cases')
}

/**
 * 获取案例详情（用户视角）
 */
export const getCaseByIdForUser = (id) => {
  return apiClient.get(`/system-design/cases/${id}`)
}

/**
 * 获取我的答题记录
 */
export const getMyNote = (caseId) => {
  return apiClient.get(`/system-design/cases/${caseId}/my-note`)
}

/**
 * 保存/更新我的答题记录
 */
export const saveOrUpdateMyNote = (caseId, data) => {
  return apiClient.post(`/system-design/cases/${caseId}/my-note`, data)
}

/**
 * 删除我的答题记录
 */
export const deleteMyNote = (caseId) => {
  return apiClient.delete(`/system-design/cases/${caseId}/my-note`)
}
