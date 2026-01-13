// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/interview-preparation-checklist')
// ✅ CORRECT: apiClient.get('/interview-preparation-checklist')

// Note: interceptor returns response.data already
import apiClient from './index'

export default {
  /**
   * 获取面试阶段的所有准备清单项
   * @param {number} stageId - 面试阶段ID
   */
  getChecklistByStageId(stageId) {
    return apiClient.get(`/interview-preparation-checklist/stage/${stageId}`)
  },

  /**
   * 获取面试阶段的重点准备清单项（用于打印）
   * @param {number} stageId - 面试阶段ID
   */
  getPriorityChecklistByStageId(stageId) {
    return apiClient.get(`/interview-preparation-checklist/stage/${stageId}/priority`)
  },

  /**
   * 创建准备清单项
   * @param {Object} checklistData - 准备清单数据
   */
  createChecklist(checklistData) {
    return apiClient.post('/interview-preparation-checklist', checklistData)
  },

  /**
   * 批量创建准备清单项
   * @param {Array} checklistItems - 准备清单项数组
   */
  batchCreateChecklist(checklistItems) {
    return apiClient.post('/interview-preparation-checklist/batch', checklistItems)
  },

  /**
   * 更新准备清单项
   * @param {number} id - 清单项ID
   * @param {Object} checklistData - 更新的数据
   */
  updateChecklist(id, checklistData) {
    return apiClient.put(`/interview-preparation-checklist/${id}`, checklistData)
  },

  /**
   * 删除准备清单项
   * @param {number} id - 清单项ID
   */
  deleteChecklist(id) {
    return apiClient.delete(`/interview-preparation-checklist/${id}`)
  },

  /**
   * 移动准备清单项到另一个面试阶段
   * @param {number} id - 清单项ID
   * @param {number} targetStageId - 目标面试阶段ID
   */
  moveChecklist(id, targetStageId) {
    return apiClient.put(`/interview-preparation-checklist/${id}/move`, null, {
      params: { targetStageId }
    })
  },

  /**
   * 批量移动准备清单项到另一个面试阶段
   * @param {Array} checklistIds - 清单项ID数组
   * @param {number} targetStageId - 目标面试阶段ID
   */
  batchMoveChecklists(checklistIds, targetStageId) {
    return apiClient.put('/interview-preparation-checklist/batch-move', checklistIds, {
      params: { targetStageId }
    })
  }
}
