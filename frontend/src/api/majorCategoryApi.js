import apiClient from './index'

/**
 * 大分类API
 */
export default {
  /**
   * 获取所有大分类
   * @param {number|null} skillId - 可选的技能ID，用于过滤特定技能下的分类
   */
  getAllMajorCategories(skillId = null) {
    if (skillId) {
      return apiClient.get('/major-categories', {
        params: { skillId }
      })
    }
    return apiClient.get('/major-categories')
  },

  /**
   * 获取指定技能下的所有Focus Areas（包含分类信息）
   * @param {number} skillId - 技能ID
   */
  getFocusAreasWithCategories(skillId) {
    return apiClient.get('/focus-areas-with-categories', {
      params: { skillId }
    })
  },

  /**
   * 获取指定技能下未分类的Focus Areas
   * @param {number} skillId - 技能ID
   */
  getUncategorizedFocusAreas(skillId) {
    return apiClient.get(`/admin/skills/${skillId}/focus-areas/uncategorized`)
  },

  /**
   * 创建大分类
   * @param {number} skillId - 技能ID
   * @param {object} categoryData - 分类数据
   */
  createMajorCategory(skillId, categoryData) {
    return apiClient.post('/major-categories', {
      ...categoryData,
      skillId
    })
  },

  /**
   * 更新大分类
   * @param {number} id - 分类ID
   * @param {object} categoryData - 分类数据
   */
  updateMajorCategory(id, categoryData) {
    return apiClient.put(`/major-categories/${id}`, categoryData)
  },

  /**
   * 删除大分类
   * @param {number} id - 分类ID
   */
  deleteMajorCategory(id) {
    return apiClient.delete(`/major-categories/${id}`)
  },

  /**
   * 创建Focus Area
   * @param {number} skillId - 技能ID
   * @param {number|null} categoryId - 大分类ID（可选，第二类技能不需要）
   * @param {object} focusAreaData - Focus Area数据
   */
  createFocusArea(skillId, categoryId, focusAreaData) {
    return apiClient.post('/focus-areas', {
      ...focusAreaData,
      skillId,
      categoryIds: categoryId ? [categoryId] : []
    })
  },

  /**
   * 更新Focus Area
   * @param {number} id - Focus Area ID
   * @param {object} focusAreaData - Focus Area数据
   */
  updateFocusArea(id, focusAreaData) {
    return apiClient.put(`/focus-areas/${id}`, focusAreaData)
  },

  /**
   * 删除Focus Area
   * @param {number} id - Focus Area ID
   */
  deleteFocusArea(id) {
    return apiClient.delete(`/focus-areas/${id}`)
  }
}
