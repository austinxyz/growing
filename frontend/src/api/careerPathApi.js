import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/career-paths')
// ✅ CORRECT: apiClient.get('/career-paths')

/**
 * 获取所有职业路径
 * GET /career-paths
 * @returns {Promise} CareerPathDTO[]
 */
export const getCareerPaths = () => {
  return api.get('/career-paths')
}

/**
 * 获取单个职业路径详情
 * GET /career-paths/{id}
 * @param {Number} id - 职业路径ID
 * @returns {Promise} CareerPathDTO
 */
export const getCareerPathById = (id) => {
  return api.get(`/career-paths/${id}`)
}

/**
 * 获取当前用户的职业路径
 * GET /career-paths/my
 * @returns {Promise} CareerPathDTO[]
 */
export const getMyCareerPaths = () => {
  return api.get('/career-paths/my')
}

export default {
  getCareerPaths,
  getCareerPathById,
  getMyCareerPaths
}
