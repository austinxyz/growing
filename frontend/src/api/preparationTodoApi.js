// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/todos')
// ✅ CORRECT: apiClient.get('/todos')

// Note: interceptor returns response.data already
// Use pattern: const data = await api.method()

import apiClient from './index'

export default {
  /**
   * 获取面试阶段的所有TODO
   * @param {number} stageId - 面试阶段ID
   * @returns {Promise<Array>} TODO列表
   */
  getTodosByStage(stageId) {
    return apiClient.get(`/interview-stages/${stageId}/todos`)
  },

  /**
   * 创建TODO
   * @param {number} stageId - 面试阶段ID
   * @param {object} todoData - TODO数据
   * @returns {Promise<object>} 创建的TODO
   */
  createTodo(stageId, todoData) {
    return apiClient.post(`/interview-stages/${stageId}/todos`, todoData)
  },

  /**
   * 更新TODO
   * @param {number} id - TODO ID
   * @param {object} todoData - 更新的TODO数据
   * @returns {Promise<object>} 更新后的TODO
   */
  updateTodo(id, todoData) {
    return apiClient.put(`/todos/${id}`, todoData)
  },

  /**
   * 标记TODO完成/未完成
   * @param {number} id - TODO ID
   * @param {boolean} isCompleted - 是否完成
   * @param {string} completionNotes - 完成备注
   * @returns {Promise<object>} 更新后的TODO
   */
  toggleComplete(id, isCompleted, completionNotes = null) {
    return apiClient.patch(`/todos/${id}/complete`, {
      isCompleted,
      completionNotes
    })
  },

  /**
   * 删除TODO
   * @param {number} id - TODO ID
   * @returns {Promise<void>}
   */
  deleteTodo(id) {
    return apiClient.delete(`/todos/${id}`)
  },

  /**
   * 批量更新TODO顺序
   * @param {number} stageId - 面试阶段ID
   * @param {Array} reorderRequests - 重排序请求数组 [{id, orderIndex}]
   * @returns {Promise<void>}
   */
  reorderTodos(stageId, reorderRequests) {
    return apiClient.patch(`/interview-stages/${stageId}/todos/reorder`, reorderRequests)
  },

  /**
   * 获取面试阶段的所有准备项（Checklist + TODO统一列表）
   * @param {number} stageId - 面试阶段ID
   * @returns {Promise<Array>} 准备项列表
   */
  getTodosWithChecklist(stageId) {
    return apiClient.get(`/interview-stages/${stageId}/todos-with-checklist`)
  },

  /**
   * 为checklist项创建详细TODO（展开checklist）
   * @param {number} checklistId - Checklist项ID
   * @param {object} todoData - TODO数据
   * @returns {Promise<object>} 创建的TODO
   */
  expandChecklist(checklistId, todoData) {
    return apiClient.post(`/checklist/${checklistId}/expand`, todoData)
  }
}
