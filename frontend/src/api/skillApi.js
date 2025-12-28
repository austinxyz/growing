import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/skills')
// ✅ CORRECT: apiClient.get('/skills')

/**
 * 获取所有技能
 * GET /skills
 * @returns {Promise} SkillDTO[]
 */
export const getSkills = () => {
  return api.get('/skills')
}

/**
 * 根据职业路径获取技能
 * GET /skills/career-path/{careerPathId}
 * @param {Number} careerPathId - 职业路径ID
 * @returns {Promise} SkillDTO[]
 */
export const getSkillsByCareerPath = (careerPathId) => {
  return api.get(`/skills/career-path/${careerPathId}`)
}

/**
 * 获取单个技能详情
 * GET /skills/{id}
 * @param {Number} id - 技能ID
 * @returns {Promise} SkillDTO
 */
export const getSkillById = (id) => {
  return api.get(`/skills/${id}`)
}

export default {
  getSkills,
  getSkillsByCareerPath,
  getSkillById
}
