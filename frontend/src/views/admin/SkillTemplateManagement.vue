<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-full px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">技能模版库</h1>
        <p class="text-sm text-gray-600 mt-1">管理技能与答题模版的关联</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex h-[calc(100vh-9rem)]">
      <!-- 左侧面板 (30%) - 职业路径与技能树 -->
      <div class="w-[30%] bg-white border-r border-gray-200 overflow-hidden flex flex-col">
        <div class="p-3 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
          <h3 class="text-xs font-semibold text-gray-700 uppercase tracking-wider">选择技能</h3>
        </div>

        <div class="flex-1 overflow-y-auto p-2">
          <!-- 加载中 -->
          <div v-if="loadingCareerPaths" class="text-center py-8">
            <svg class="animate-spin h-6 w-6 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-2 text-xs text-gray-500">加载中...</p>
          </div>

          <!-- 职业路径树 -->
          <div v-else-if="careerPaths.length > 0" class="space-y-1">
            <div v-for="careerPath in careerPaths" :key="careerPath.id">
              <!-- 职业路径节点 -->
              <div class="flex items-center px-2 py-1.5 rounded hover:bg-gray-100 transition-colors">
                <!-- 展开/折叠图标 -->
                <svg
                  @click="toggleCareerPath(careerPath.id)"
                  class="w-4 h-4 mr-1 text-gray-500 flex-shrink-0 transition-transform cursor-pointer"
                  :class="{ 'rotate-90': expandedCareerPaths.has(careerPath.id) }"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
                <!-- 职业路径名称 -->
                <span
                  @click="toggleCareerPath(careerPath.id)"
                  class="text-sm font-medium text-gray-700 truncate flex-1 cursor-pointer"
                >
                  {{ careerPath.name }}
                </span>
              </div>

              <!-- 技能列表 (子节点) -->
              <div v-if="expandedCareerPaths.has(careerPath.id)" class="ml-5 mt-1 space-y-1">
                <div
                  v-for="skill in careerPath.skills"
                  :key="skill.id"
                  @click.stop="selectSkill(skill)"
                  :class="[
                    'flex items-center px-2 py-1.5 rounded cursor-pointer transition-colors',
                    selectedSkill?.id === skill.id
                      ? 'bg-blue-50 text-blue-700 font-medium'
                      : 'text-gray-600 hover:bg-gray-50'
                  ]"
                >
                  <svg class="w-3.5 h-3.5 mr-2 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <span class="text-xs truncate">{{ skill.name }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 无数据 -->
          <div v-else class="text-center py-8">
            <p class="text-sm text-gray-500">暂无职业路径</p>
          </div>
        </div>
      </div>

      <!-- 右侧面板 (70%) - 模版管理 -->
      <div class="flex-1 bg-white overflow-hidden flex flex-col">
        <!-- 未选择技能提示 -->
        <div v-if="!selectedSkill" class="flex items-center justify-center h-full">
          <div class="text-center">
            <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="text-gray-500">请从左侧选择一个技能</p>
          </div>
        </div>

        <!-- 已选择技能 - 显示模版管理 -->
        <div v-else class="flex flex-col h-full">
          <!-- 技能信息头部 -->
          <div class="p-4 bg-blue-50 border-b border-blue-100">
            <div class="flex items-center justify-between">
              <div>
                <h2 class="text-lg font-semibold text-gray-900">{{ selectedSkill.name }}</h2>
                <p class="text-sm text-gray-600 mt-1">管理该技能的答题模版</p>
              </div>
              <div class="flex space-x-2">
                <!-- 新增模版按钮 -->
                <button
                  @click="showCreateTemplateModal = true"
                  class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors text-sm font-medium"
                >
                  + 新增模版
                </button>
                <!-- 关联已有模版按钮 -->
                <button
                  @click="showAssociateModal = true"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
                >
                  关联已有模版
                </button>
              </div>
            </div>
          </div>

          <!-- 模版列表 -->
          <div class="flex-1 overflow-y-auto p-4">
            <!-- 加载中 -->
            <div v-if="loadingTemplates" class="text-center py-8">
              <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <p class="mt-2 text-sm text-gray-500">加载模版中...</p>
            </div>

            <!-- 模版卡片列表 -->
            <div v-else-if="skillTemplates.length > 0" class="space-y-3">
              <div
                v-for="template in skillTemplates"
                :key="template.templateId"
                class="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <div class="flex items-center space-x-2">
                      <h3 class="text-base font-semibold text-gray-900">{{ template.templateName }}</h3>
                      <span
                        v-if="template.isDefault"
                        class="px-2 py-0.5 text-xs font-semibold bg-green-100 text-green-800 rounded"
                      >
                        默认
                      </span>
                    </div>
                    <p v-if="template.description" class="text-sm text-gray-600 mt-1">{{ template.description }}</p>
                    <div class="mt-2 text-xs text-gray-500">
                      模版ID: {{ template.templateId }}
                    </div>
                  </div>
                  <div class="flex items-center space-x-2 ml-4">
                    <!-- 编辑按钮 -->
                    <button
                      @click="editTemplate(template)"
                      class="p-2 text-blue-600 hover:bg-blue-50 rounded transition-colors"
                      title="编辑模版"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <!-- 设为默认按钮 -->
                    <button
                      v-if="!template.isDefault"
                      @click="setAsDefault(template)"
                      class="p-2 text-green-600 hover:bg-green-50 rounded transition-colors"
                      title="设为默认"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                      </svg>
                    </button>
                    <!-- 取消关联按钮 -->
                    <button
                      @click="confirmDisassociate(template)"
                      class="p-2 text-red-600 hover:bg-red-50 rounded transition-colors"
                      title="取消关联"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 无模版提示 -->
            <div v-else class="text-center py-12">
              <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p class="text-gray-500 mb-4">该技能暂无关联的答题模版</p>
              <div class="flex justify-center space-x-3">
                <button
                  @click="showCreateTemplateModal = true"
                  class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors text-sm font-medium"
                >
                  新增模版
                </button>
                <button
                  @click="showAssociateModal = true"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
                >
                  关联已有模版
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/编辑模版Modal -->
    <TemplateEditorModal
      v-if="showCreateTemplateModal || showEditTemplateModal"
      :template="editingTemplate"
      @close="closeTemplateModal"
      @save="handleTemplateSave"
    />

    <!-- 关联已有模版Modal -->
    <AssociateTemplateModal
      v-if="showAssociateModal"
      :skill-id="selectedSkill?.id"
      :associated-template-ids="associatedTemplateIds"
      @close="showAssociateModal = false"
      @associate="handleAssociate"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAllCareerPaths } from '@/api/careerPaths'
import skillTemplateApi from '@/api/skillTemplateApi'
import answerTemplateApi from '@/api/answerTemplateApi'
import TemplateEditorModal from '@/components/admin/TemplateEditorModal.vue'
import AssociateTemplateModal from '@/components/admin/AssociateTemplateModal.vue'

// 职业路径数据
const careerPaths = ref([])
const loadingCareerPaths = ref(false)
const expandedCareerPaths = ref(new Set())

// 选中的技能
const selectedSkill = ref(null)

// 模版数据
const skillTemplates = ref([])
const loadingTemplates = ref(false)

// Modal状态
const showCreateTemplateModal = ref(false)
const showEditTemplateModal = ref(false)
const showAssociateModal = ref(false)
const editingTemplate = ref(null)

// 已关联的模版ID列表
const associatedTemplateIds = computed(() => {
  return skillTemplates.value.map(t => t.templateId)
})

// 加载职业路径和技能
const loadCareerPaths = async () => {
  loadingCareerPaths.value = true
  try {
    const data = await getAllCareerPaths()
    careerPaths.value = data || []
    // 默认展开所有职业路径
    careerPaths.value.forEach(cp => {
      expandedCareerPaths.value.add(cp.id)
    })
  } catch (error) {
    console.error('Failed to load career paths:', error)
    alert('加载职业路径失败')
  } finally {
    loadingCareerPaths.value = false
  }
}

// 切换职业路径展开/折叠
const toggleCareerPath = (careerPathId) => {
  if (expandedCareerPaths.value.has(careerPathId)) {
    expandedCareerPaths.value.delete(careerPathId)
  } else {
    expandedCareerPaths.value.add(careerPathId)
  }
}

// 选择技能
const selectSkill = async (skill) => {
  selectedSkill.value = skill
  await loadSkillTemplates()
}

// 加载技能的模版列表
const loadSkillTemplates = async () => {
  if (!selectedSkill.value) return

  loadingTemplates.value = true
  try {
    const data = await skillTemplateApi.getSkillTemplates(selectedSkill.value.id)
    skillTemplates.value = data || []
  } catch (error) {
    console.error('Failed to load skill templates:', error)
    alert('加载模版列表失败')
  } finally {
    loadingTemplates.value = false
  }
}

// 新增模版
const handleTemplateSave = async (templateData) => {
  try {
    if (editingTemplate.value) {
      // 编辑模式
      await answerTemplateApi.updateTemplate(editingTemplate.value.id, templateData)
      alert('模版更新成功')
    } else {
      // 新增模式
      const created = await answerTemplateApi.createTemplate(templateData)
      // 自动关联到当前技能
      await skillTemplateApi.associateTemplate(selectedSkill.value.id, created.id)
      alert('模版创建并关联成功')
    }
    closeTemplateModal()
    await loadSkillTemplates()
  } catch (error) {
    console.error('Failed to save template:', error)
    alert('保存模版失败: ' + (error.response?.data?.message || error.message))
  }
}

// 关联已有模版
const handleAssociate = async (templateId) => {
  try {
    await skillTemplateApi.associateTemplate(selectedSkill.value.id, templateId)
    alert('关联成功')
    showAssociateModal.value = false
    await loadSkillTemplates()
  } catch (error) {
    console.error('Failed to associate template:', error)
    alert('关联失败: ' + (error.response?.data?.message || error.message))
  }
}

// 编辑模版
const editTemplate = async (template) => {
  try {
    // 获取完整的模版数据
    const fullTemplate = await answerTemplateApi.getTemplate(template.templateId)
    editingTemplate.value = fullTemplate
    showEditTemplateModal.value = true
  } catch (error) {
    console.error('Failed to load template:', error)
    alert('加载模版失败')
  }
}

// 设为默认模版
const setAsDefault = async (template) => {
  if (!confirm(`确定将 "${template.templateName}" 设为默认模版吗？`)) return

  try {
    await skillTemplateApi.setDefaultTemplate(selectedSkill.value.id, template.templateId)
    alert('设置成功')
    await loadSkillTemplates()
  } catch (error) {
    console.error('Failed to set default template:', error)
    alert('设置失败: ' + (error.response?.data?.message || error.message))
  }
}

// 确认取消关联
const confirmDisassociate = async (template) => {
  if (!confirm(`确定取消关联模版 "${template.templateName}" 吗？`)) return

  try {
    await skillTemplateApi.disassociateTemplate(selectedSkill.value.id, template.templateId)
    alert('取消关联成功')
    await loadSkillTemplates()
  } catch (error) {
    console.error('Failed to disassociate template:', error)
    alert('取消关联失败: ' + (error.response?.data?.message || error.message))
  }
}

// 关闭模版编辑Modal
const closeTemplateModal = () => {
  showCreateTemplateModal.value = false
  showEditTemplateModal.value = false
  editingTemplate.value = null
}

// 初始化
onMounted(() => {
  loadCareerPaths()
})
</script>
