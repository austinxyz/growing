import request from './index'

// 获取技能的 Focus Areas
export const getFocusAreas = (skillId) => {
  return request.get(`/admin/skills/${skillId}/focus-areas`)
}

// 创建 Focus Area
export const createFocusArea = (skillId, focusAreaData) => {
  return request.post(`/admin/skills/${skillId}/focus-areas`, focusAreaData)
}

// 更新 Focus Area
export const updateFocusArea = (id, focusAreaData) => {
  return request.put(`/admin/skills/focus-areas/${id}`, focusAreaData)
}

// 删除 Focus Area
export const deleteFocusArea = (id) => {
  return request.delete(`/admin/skills/focus-areas/${id}`)
}
