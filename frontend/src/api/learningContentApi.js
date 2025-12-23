import api from './index'

// ===== 用户端 API =====

/**
 * 获取Focus Area的完整学习内容（按学习阶段分组）
 * @param {number} focusAreaId - Focus Area ID
 * @returns {Promise} FocusAreaLearningDTO
 */
export const getContentsByFocusArea = (focusAreaId) => {
  return api.get(`/learning-contents/focus-areas/${focusAreaId}`)
}

/**
 * 获取单个学习内容详情
 * @param {number} id - 学习内容ID
 * @returns {Promise} LearningContentDTO
 */
export const getContentById = (id) => {
  return api.get(`/learning-contents/${id}`)
}

/**
 * 获取算法模版列表（支持搜索、分页）
 * @param {Object} params - 查询参数 { search, page, size }
 * @returns {Promise} Page<LearningContentDTO>
 */
export const getAlgorithmTemplates = (params = {}) => {
  return api.get('/learning-contents/algorithm-templates', { params })
}

/**
 * 获取单个算法模版详情
 * @param {number} id - 算法模版ID
 * @returns {Promise} LearningContentDTO
 */
export const getAlgorithmTemplateById = (id) => {
  return api.get(`/learning-contents/algorithm-templates/${id}`)
}

/**
 * 获取用户对指定模版的笔记
 * @param {number} templateId - 模版ID
 * @returns {Promise} UserTemplateNoteDTO
 */
export const getTemplateNote = (templateId) => {
  return api.get(`/learning-contents/algorithm-templates/${templateId}/note`)
}

/**
 * 保存或更新用户模版笔记
 * @param {number} templateId - 模版ID
 * @param {string} noteContent - 笔记内容
 * @returns {Promise} UserTemplateNoteDTO
 */
export const saveOrUpdateTemplateNote = (templateId, noteContent) => {
  return api.post(`/learning-contents/algorithm-templates/${templateId}/note`, { noteContent })
}

/**
 * 删除用户模版笔记
 * @param {number} templateId - 模版ID
 * @returns {Promise}
 */
export const deleteTemplateNote = (templateId) => {
  return api.delete(`/learning-contents/algorithm-templates/${templateId}/note`)
}

// ===== 管理员 API =====

/**
 * 管理员获取学习内容列表（支持多条件筛选、分页）
 * @param {Object} params - 查询参数 { skillId, stageId, focusAreaId, contentType, search, page, size }
 * @returns {Promise} Page<LearningContentDTO>
 */
export const getContentsForAdmin = (params = {}) => {
  return api.get('/admin/learning-contents', { params })
}

/**
 * 创建学习内容
 * @param {Object} data - LearningContentDTO
 * @returns {Promise} LearningContentDTO
 */
export const createContent = (data) => {
  return api.post('/admin/learning-contents', data)
}

/**
 * 更新学习内容
 * @param {number} id - 学习内容ID
 * @param {Object} data - LearningContentDTO
 * @returns {Promise} LearningContentDTO
 */
export const updateContent = (id, data) => {
  return api.put(`/admin/learning-contents/${id}`, data)
}

/**
 * 删除学习内容
 * @param {number} id - 学习内容ID
 * @returns {Promise} void
 */
export const deleteContent = (id) => {
  return api.delete(`/admin/learning-contents/${id}`)
}

/**
 * 批量调整学习内容排序
 * @param {Array} data - [{ id: number, displayOrder: number }, ...]
 * @returns {Promise} void
 */
export const reorderContents = (data) => {
  return api.put('/admin/learning-contents/reorder', data)
}

/**
 * 获取所有算法模版（管理端使用）
 * @returns {Promise} LearningContentDTO[]
 */
export const getTemplates = () => {
  return api.get('/admin/learning-contents/templates')
}

/**
 * 创建算法模版
 * @param {Object} data - 模版数据
 * @returns {Promise} LearningContentDTO
 */
export const createTemplate = (data) => {
  return api.post('/admin/learning-contents/templates', data)
}

export default {
  getContentsByFocusArea,
  getContentById,
  getAlgorithmTemplates,
  getAlgorithmTemplateById,
  getContentsForAdmin,
  createContent,
  updateContent,
  deleteContent,
  reorderContents,
  getTemplates,
  createTemplate
}
