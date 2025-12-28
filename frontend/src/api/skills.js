import request from './index'

// 获取所有技能
export const getAllSkills = () => {
  return request.get('/skills')
}

// 获取技能详情（用户视角）
export const getSkillById = (id) => {
  return request.get(`/skills/${id}`)
}

// 根据职业路径获取技能
export const getSkillsByCareerPath = (careerPathId) => {
  return request.get(`/skills/career-path/${careerPathId}`)
}

// 获取技能下的学习资源（用户视角）
export const getSkillResources = (skillId) => {
  return request.get(`/skills/${skillId}/resources`)
}

// 用户添加学习资源
export const addUserResource = (skillId, resourceData) => {
  return request.post(`/skills/${skillId}/resources`, resourceData)
}

// 更新用户资源
export const updateUserResource = (resourceId, resourceData) => {
  return request.put(`/skills/resources/${resourceId}`, resourceData)
}

// 删除用户资源
export const deleteUserResource = (resourceId) => {
  return request.delete(`/skills/resources/${resourceId}`)
}

// 获取用户创建的所有资源
export const getMyResources = () => {
  return request.get('/skills/my-resources')
}

// ===== 管理员 API =====

// 获取技能详情（管理员视角）
export const getSkillByIdAdmin = (id) => {
  return request.get(`/admin/skills/${id}`)
}

// 创建技能
export const createSkill = (skillData) => {
  return request.post('/admin/skills', skillData)
}

// 更新技能
export const updateSkill = (id, skillData) => {
  return request.put(`/admin/skills/${id}`, skillData)
}

// 删除技能
export const deleteSkill = (id) => {
  return request.delete(`/admin/skills/${id}`)
}

// 关联技能到职业路径
export const associateSkillToCareerPath = (careerPathId, skillId) => {
  return request.post(`/admin/skills/career-path/${careerPathId}/skill/${skillId}`)
}

// 取消关联
export const dissociateSkillFromCareerPath = (careerPathId, skillId) => {
  return request.delete(`/admin/skills/career-path/${careerPathId}/skill/${skillId}`)
}

// ===== Focus Area 管理 API =====

// 创建专注领域
export const createFocusArea = (skillId, focusAreaData) => {
  return request.post(`/admin/skills/${skillId}/focus-areas`, focusAreaData)
}

// 更新专注领域
export const updateFocusArea = (focusAreaId, focusAreaData) => {
  return request.put(`/admin/skills/focus-areas/${focusAreaId}`, focusAreaData)
}

// 删除专注领域
export const deleteFocusArea = (focusAreaId) => {
  return request.delete(`/admin/skills/focus-areas/${focusAreaId}`)
}

// ===== Learning Resource 管理 API =====

// 创建官方学习资源
export const createOfficialResource = (skillId, resourceData) => {
  return request.post(`/admin/skills/${skillId}/resources`, resourceData)
}

// 更新学习资源
export const updateResource = (resourceId, resourceData) => {
  return request.put(`/admin/skills/resources/${resourceId}`, resourceData)
}

// 删除学习资源
export const deleteResource = (resourceId) => {
  return request.delete(`/admin/skills/resources/${resourceId}`)
}

// 获取未关联到任何职业路径的技能（管理员）
export const getUnassociatedSkills = () => {
  return request.get('/skills/unassociated')
}
