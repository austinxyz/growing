import request from './index'

// 获取技能的所有学习资源（管理员视角）
export const getResourcesAdmin = (skillId) => {
  return request.get(`/admin/skills/${skillId}/resources`)
}

// 创建官方资源
export const createOfficialResource = (skillId, resourceData) => {
  return request.post(`/admin/skills/${skillId}/resources`, resourceData)
}

// 更新资源（管理员）
export const updateResourceAdmin = (id, resourceData) => {
  return request.put(`/admin/skills/resources/${id}`, resourceData)
}

// 删除资源（管理员）
export const deleteResourceAdmin = (id) => {
  return request.delete(`/admin/skills/resources/${id}`)
}
