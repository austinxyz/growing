import api from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/admin/skills')
// ✅ CORRECT: apiClient.get('/admin/skills')

/**
 * 获取Skill下的所有Focus Area（包含大分类信息）
 * GET /admin/skills/{skillId}/focus-areas
 * @param {Number} skillId - Skill ID
 * @returns {Promise} FocusAreaDTO[]
 */
export const getFocusAreasBySkillId = (skillId) => {
  return api.get(`/admin/skills/${skillId}/focus-areas`)
}

export default {
  getFocusAreasBySkillId
}
