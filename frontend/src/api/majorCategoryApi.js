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
  }
}
