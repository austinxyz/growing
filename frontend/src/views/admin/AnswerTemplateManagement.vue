<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-full px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">答题模版管理</h1>
        <p class="text-sm text-gray-600 mt-1">管理答题模版和技能关联</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex h-[calc(100vh-9rem)]">
      <!-- 左侧面板 (30%) - 模版列表 -->
      <div class="w-[30%] bg-white border-r border-gray-200 overflow-hidden flex flex-col">
        <!-- 顶部操作栏 -->
        <div class="border-b border-gray-200 p-4">
          <button
            @click="handleAddTemplate"
            class="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm font-medium"
          >
            + 新建模版
          </button>
        </div>

        <!-- 加载中 -->
        <div v-if="loading" class="flex-1 flex items-center justify-center">
          <div class="text-center">
            <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-2 text-sm text-gray-500">加载中...</p>
          </div>
        </div>

        <!-- 模版列表 -->
        <div v-else class="flex-1 overflow-y-auto p-4">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">
            所有模版 ({{ templates.length }})
          </h3>
          <div v-if="templates.length === 0" class="text-sm text-gray-500 text-center py-4">
            暂无模版
          </div>
          <div v-else class="space-y-2">
            <button
              v-for="template in templates"
              :key="template.id"
              @click="selectTemplate(template)"
              :class="[
                'w-full text-left px-4 py-3 rounded-lg border transition-all',
                selectedTemplateId === template.id
                  ? 'bg-blue-50 border-blue-500 shadow-sm'
                  : 'bg-white border-gray-200 hover:border-gray-300 hover:shadow-sm'
              ]"
            >
              <div class="font-medium text-gray-900 mb-1">
                {{ template.templateName }}
              </div>
              <div class="text-xs text-gray-500 line-clamp-2">
                {{ template.description || '无描述' }}
              </div>
              <div class="mt-2 text-xs text-gray-400">
                {{ template.templateFields?.length || 0 }} 个字段
              </div>
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧面板 (70%) - 模版编辑器 -->
      <div class="flex-1 bg-white overflow-hidden flex flex-col">
        <!-- 未选择模版时的提示 -->
        <div v-if="!selectedTemplateId" class="flex-1 flex items-center justify-center text-gray-500">
          <div class="text-center">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="mt-2 text-sm">请从左侧选择一个模版或创建新模版</p>
          </div>
        </div>

        <!-- 选中模版后显示编辑器 -->
        <div v-else class="flex-1 overflow-y-auto">
          <!-- 头部 -->
          <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">
              {{ isNewTemplate ? '新建模版' : '编辑模版' }}
            </h2>
            <div class="flex gap-2">
              <button
                v-if="!isNewTemplate"
                @click="handleDeleteTemplate"
                class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors text-sm"
              >
                删除模版
              </button>
              <button
                @click="handleSaveTemplate"
                class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm"
              >
                保存
              </button>
            </div>
          </div>

          <!-- 编辑表单 -->
          <div class="p-6 space-y-6">
            <!-- 模版名称 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                模版名称 <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.templateName"
                type="text"
                required
                placeholder="例如: STAR、Technical"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <!-- 模版说明 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                模版说明
              </label>
              <textarea
                v-model="form.description"
                rows="3"
                placeholder="描述该模版的用途和适用场景"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              ></textarea>
            </div>

            <!-- 字段定义编辑器 -->
            <div>
              <div class="flex items-center justify-between mb-2">
                <label class="block text-sm font-medium text-gray-700">
                  字段定义 <span class="text-red-500">*</span>
                </label>
                <button
                  @click="addField"
                  class="px-3 py-1 text-sm bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
                >
                  + 添加字段
                </button>
              </div>
              <p class="text-xs text-gray-500 mb-3">定义答题时需要填写的字段</p>

              <!-- 字段列表 -->
              <div v-if="form.templateFields.length === 0" class="text-sm text-gray-400 text-center py-8 border-2 border-dashed border-gray-200 rounded-lg">
                暂无字段,点击上方"添加字段"按钮创建
              </div>
              <div v-else class="space-y-4">
                <div
                  v-for="(field, index) in form.templateFields"
                  :key="index"
                  class="p-4 border border-gray-200 rounded-lg bg-gray-50"
                >
                  <div class="flex items-center justify-between mb-3">
                    <span class="text-sm font-medium text-gray-700">字段 #{{ index + 1 }}</span>
                    <button
                      @click="removeField(index)"
                      class="text-red-600 hover:text-red-800 text-sm"
                    >
                      删除
                    </button>
                  </div>
                  <div class="grid grid-cols-3 gap-3">
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1">
                        字段Key <span class="text-red-500">*</span>
                      </label>
                      <input
                        v-model="field.key"
                        type="text"
                        placeholder="例如: situation"
                        class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500"
                      />
                    </div>
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1">
                        显示标签 <span class="text-red-500">*</span>
                      </label>
                      <input
                        v-model="field.label"
                        type="text"
                        placeholder="例如: Situation"
                        class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500"
                      />
                    </div>
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1">
                        占位符提示
                      </label>
                      <input
                        v-model="field.placeholder"
                        type="text"
                        placeholder="例如: 描述情境"
                        class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Skill关联管理 -->
            <div v-if="!isNewTemplate">
              <div class="flex items-center justify-between mb-2">
                <label class="block text-sm font-medium text-gray-700">
                  Skill关联
                </label>
                <button
                  @click="showSkillAssociationModal = true"
                  class="px-3 py-1 text-sm bg-purple-600 text-white rounded-md hover:bg-purple-700 transition-colors"
                >
                  + 关联新Skill
                </button>
              </div>
              <p class="text-xs text-gray-500 mb-3">管理哪些技能使用此模版</p>

              <!-- 加载关联的Skills -->
              <div v-if="loadingSkillAssociations" class="text-center py-4">
                <svg class="animate-spin h-6 w-6 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <p class="mt-2 text-xs text-gray-500">加载关联中...</p>
              </div>
              <div v-else-if="associatedSkills.length === 0" class="text-sm text-gray-400 text-center py-4 border-2 border-dashed border-gray-200 rounded-lg">
                暂无关联的Skill
              </div>
              <div v-else class="space-y-2">
                <div
                  v-for="skill in associatedSkills"
                  :key="skill.skillId"
                  class="flex items-center justify-between p-3 bg-white border border-gray-200 rounded-lg"
                >
                  <div class="flex-1">
                    <div class="font-medium text-gray-900">{{ skill.skillName }}</div>
                    <div v-if="skill.isDefault" class="text-xs text-green-600 mt-1">
                      ✓ 默认模版
                    </div>
                  </div>
                  <div class="flex items-center gap-2">
                    <button
                      v-if="!skill.isDefault"
                      @click="setDefaultTemplate(skill.skillId)"
                      class="px-2 py-1 text-xs bg-green-100 text-green-700 rounded hover:bg-green-200 transition-colors"
                    >
                      设为默认
                    </button>
                    <button
                      @click="disassociateSkill(skill.skillId)"
                      class="px-2 py-1 text-xs bg-red-100 text-red-700 rounded hover:bg-red-200 transition-colors"
                    >
                      取消关联
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Skill关联Modal -->
    <div v-if="showSkillAssociationModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="showSkillAssociationModal = false"></div>
      <div class="flex min-h-screen items-center justify-center p-4">
        <div class="relative bg-white rounded-lg shadow-xl max-w-md w-full">
          <div class="border-b border-gray-200 px-6 py-4">
            <h3 class="text-lg font-semibold text-gray-900">关联Skill到模版</h3>
          </div>
          <div class="p-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              选择Skill <span class="text-red-500">*</span>
            </label>
            <select
              v-model="selectedSkillForAssociation"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">请选择Skill</option>
              <option
                v-for="skill in availableSkills"
                :key="skill.id"
                :value="skill.id"
              >
                {{ skill.name }}
              </option>
            </select>
          </div>
          <div class="border-t border-gray-200 px-6 py-4 flex justify-end gap-2">
            <button
              @click="showSkillAssociationModal = false"
              class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors text-sm"
            >
              取消
            </button>
            <button
              @click="handleAssociateSkill"
              :disabled="!selectedSkillForAssociation"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm disabled:opacity-50 disabled:cursor-not-allowed"
            >
              确认关联
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import answerTemplateApi from '@/api/answerTemplateApi'
import skillTemplateApi from '@/api/skillTemplateApi'
import { getAllSkills } from '@/api/skills'

export default {
  name: 'AnswerTemplateManagement',
  data() {
    return {
      // 模版列表
      templates: [],
      loading: false,
      selectedTemplateId: null,

      // 编辑表单
      form: {
        templateName: '',
        description: '',
        templateFields: []
      },
      isNewTemplate: false,

      // Skill关联
      loadingSkillAssociations: false,
      associatedSkills: [],
      showSkillAssociationModal: false,
      selectedSkillForAssociation: '',
      allSkills: []
    }
  },
  computed: {
    selectedTemplate() {
      return this.templates.find(t => t.id === this.selectedTemplateId)
    },
    // 可用于关联的Skills（排除已关联的）
    availableSkills() {
      const associatedSkillIds = this.associatedSkills.map(s => s.skillId)
      return this.allSkills.filter(s => !associatedSkillIds.includes(s.id))
    }
  },
  async mounted() {
    await this.loadTemplates()
    await this.loadAllSkills()
  },
  methods: {
    // ⚠️ Guardrail #8: interceptor已返回response.data,使用const data = await api.method()
    async loadTemplates() {
      this.loading = true
      try {
        const data = await answerTemplateApi.getAllTemplates()
        this.templates = data || []
      } catch (error) {
        console.error('加载模版列表失败:', error)
        this.templates = []
      } finally {
        this.loading = false
      }
    },
    async loadAllSkills() {
      try {
        const data = await getAllSkills()
        this.allSkills = data || []
      } catch (error) {
        console.error('加载技能列表失败:', error)
        this.allSkills = []
      }
    },
    selectTemplate(template) {
      this.selectedTemplateId = template.id
      this.isNewTemplate = false
      // 复制模版数据到表单
      this.form = {
        templateName: template.templateName,
        description: template.description || '',
        templateFields: JSON.parse(JSON.stringify(template.templateFields || []))
      }
      // 加载Skill关联
      this.loadSkillAssociations()
    },
    handleAddTemplate() {
      // 创建新模版
      this.selectedTemplateId = -1 // 临时ID
      this.isNewTemplate = true
      this.form = {
        templateName: '',
        description: '',
        templateFields: []
      }
      this.associatedSkills = []
    },
    addField() {
      this.form.templateFields.push({
        key: '',
        label: '',
        placeholder: ''
      })
    },
    removeField(index) {
      this.form.templateFields.splice(index, 1)
    },
    async handleSaveTemplate() {
      // 验证表单
      if (!this.form.templateName.trim()) {
        alert('请输入模版名称')
        return
      }
      if (this.form.templateFields.length === 0) {
        alert('请至少添加一个字段')
        return
      }
      // 验证所有字段的key和label
      for (let i = 0; i < this.form.templateFields.length; i++) {
        const field = this.form.templateFields[i]
        if (!field.key.trim() || !field.label.trim()) {
          alert(`字段 #${i + 1} 的Key和Label不能为空`)
          return
        }
      }

      try {
        if (this.isNewTemplate) {
          // 创建新模版
          const newTemplate = await answerTemplateApi.createTemplate(this.form)
          this.templates.push(newTemplate)
          this.selectedTemplateId = newTemplate.id
          this.isNewTemplate = false
          alert('模版创建成功')
        } else {
          // 更新现有模版
          const updated = await answerTemplateApi.updateTemplate(this.selectedTemplateId, this.form)
          const index = this.templates.findIndex(t => t.id === this.selectedTemplateId)
          if (index !== -1) {
            this.templates.splice(index, 1, updated)
          }
          alert('模版保存成功')
        }
      } catch (error) {
        console.error('保存模版失败:', error)
        alert('保存失败: ' + (error.message || '未知错误'))
      }
    },
    async handleDeleteTemplate() {
      if (!confirm(`确定要删除模版"${this.form.templateName}"吗？\n删除后将同时删除所有Skill关联。`)) {
        return
      }

      try {
        await answerTemplateApi.deleteTemplate(this.selectedTemplateId)
        this.templates = this.templates.filter(t => t.id !== this.selectedTemplateId)
        this.selectedTemplateId = null
        alert('模版删除成功')
      } catch (error) {
        console.error('删除模版失败:', error)
        alert('删除失败: ' + (error.message || '未知错误'))
      }
    },

    // ===== Skill关联管理 =====
    async loadSkillAssociations() {
      if (this.isNewTemplate) {
        this.associatedSkills = []
        return
      }

      this.loadingSkillAssociations = true
      try {
        const data = await skillTemplateApi.getSkillsByTemplate(this.selectedTemplateId)
        this.associatedSkills = data || []
      } catch (error) {
        console.error('加载Skill关联失败:', error)
        this.associatedSkills = []
      } finally {
        this.loadingSkillAssociations = false
      }
    },
    async handleAssociateSkill() {
      if (!this.selectedSkillForAssociation) {
        return
      }

      try {
        await skillTemplateApi.associateTemplate(
          this.selectedSkillForAssociation,
          this.selectedTemplateId
        )
        this.showSkillAssociationModal = false
        this.selectedSkillForAssociation = ''
        await this.loadSkillAssociations()
        alert('关联成功')
      } catch (error) {
        console.error('关联Skill失败:', error)
        alert('关联失败: ' + (error.message || '未知错误'))
      }
    },
    async setDefaultTemplate(skillId) {
      try {
        await skillTemplateApi.setDefaultTemplate(skillId, this.selectedTemplateId)
        await this.loadSkillAssociations()
        alert('设为默认成功')
      } catch (error) {
        console.error('设为默认失败:', error)
        alert('设为默认失败: ' + (error.message || '未知错误'))
      }
    },
    async disassociateSkill(skillId) {
      const skill = this.associatedSkills.find(s => s.skillId === skillId)
      if (!confirm(`确定要取消"${skill?.skillName}"与此模版的关联吗？`)) {
        return
      }

      try {
        await skillTemplateApi.disassociateTemplate(skillId, this.selectedTemplateId)
        await this.loadSkillAssociations()
        alert('取消关联成功')
      } catch (error) {
        console.error('取消关联失败:', error)
        alert('取消关联失败: ' + (error.message || '未知错误'))
      }
    }
  }
}
</script>
