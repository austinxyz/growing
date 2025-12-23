import api from './index'

// ===== 用户端 API =====

/**
 * 获取Skill的所有学习阶段
 * @param {number} skillId - Skill ID
 * @returns {Promise} Array<LearningStageDTO>
 */
export const getStagesBySkill = (skillId) => {
  return api.get(`/learning-stages/skills/${skillId}`)
}

/**
 * 获取单个学习阶段详情（含学习内容列表）
 * @param {number} id - 学习阶段ID
 * @returns {Promise} LearningStageDTO (包含contents列表)
 */
export const getStageById = (id) => {
  return api.get(`/learning-stages/${id}`)
}

// ===== 管理员 API =====

/**
 * 创建学习阶段
 * @param {Object} data - LearningStageDTO
 * @returns {Promise} LearningStageDTO
 */
export const createStage = (data) => {
  return api.post('/admin/learning-stages', data)
}

/**
 * 更新学习阶段
 * @param {number} id - 学习阶段ID
 * @param {Object} data - LearningStageDTO
 * @returns {Promise} LearningStageDTO
 */
export const updateStage = (id, data) => {
  return api.put(`/admin/learning-stages/${id}`, data)
}

/**
 * 删除学习阶段
 * @param {number} id - 学习阶段ID
 * @returns {Promise} void
 */
export const deleteStage = (id) => {
  return api.delete(`/admin/learning-stages/${id}`)
}
