// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/admin/skill-templates')
// ✅ CORRECT: apiClient.get('/admin/skill-templates')
//
// Note: interceptor returns response.data already
// ❌ WRONG: const response = await api.getSkillTemplates(); return response.data
// ✅ CORRECT: const data = await api.getSkillTemplates(); return data

import api from './index'

const skillTemplateApi = {
  /**
   * 公开API: 获取技能的所有关联模版（供用户答题使用）
   * GET /skills/{skillId}/templates
   * @param {Number} skillId
   */
  getSkillTemplatesPublic(skillId) {
    return api.get(`/skills/${skillId}/templates`)
  },

  /**
   * 公开API: 获取技能的默认模版（供用户答题使用）
   * GET /skills/{skillId}/templates/default
   * @param {Number} skillId
   */
  getDefaultTemplatePublic(skillId) {
    return api.get(`/skills/${skillId}/templates/default`)
  },

  /**
   * 管理员API: 获取技能的所有关联模版
   * GET /admin/skill-templates?skillId=X
   * @param {Number} skillId
   */
  getSkillTemplates(skillId) {
    return api.get('/admin/skill-templates', { params: { skillId } })
  },

  /**
   * 获取技能的默认模版
   * GET /admin/skill-templates/default?skillId=X
   * @param {Number} skillId
   */
  getDefaultTemplate(skillId) {
    return api.get('/admin/skill-templates/default', { params: { skillId } })
  },

  /**
   * 关联技能与模版
   * POST /admin/skill-templates
   * @param {Number} skillId
   * @param {Number} templateId
   */
  associateTemplate(skillId, templateId) {
    return api.post('/admin/skill-templates', { skillId, templateId })
  },

  /**
   * 设置默认模版
   * PUT /admin/skill-templates/default
   * @param {Number} skillId
   * @param {Number} templateId
   */
  setDefaultTemplate(skillId, templateId) {
    return api.put('/admin/skill-templates/default', { skillId, templateId })
  },

  /**
   * 取消关联
   * DELETE /admin/skill-templates?skillId=X&templateId=Y
   * @param {Number} skillId
   * @param {Number} templateId
   */
  disassociateTemplate(skillId, templateId) {
    return api.delete('/admin/skill-templates', { params: { skillId, templateId } })
  },

  /**
   * 获取模版关联的所有技能（反向查询）
   * GET /admin/skill-templates/by-template?templateId=X
   * @param {Number} templateId
   * @returns {Promise} Array of { skillId, skillName, isDefault }
   */
  getSkillsByTemplate(templateId) {
    return api.get('/admin/skill-templates/by-template', { params: { templateId } })
  }
}

export default skillTemplateApi
