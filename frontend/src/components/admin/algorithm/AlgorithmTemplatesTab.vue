<template>
  <div class="bg-white rounded-lg shadow">
    <!-- 头部：搜索和创建按钮 -->
    <div class="p-6 border-b border-gray-200">
      <div class="flex items-center justify-between">
        <div class="flex-1 max-w-lg">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索算法模版标题..."
              class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              @input="handleSearch"
            />
            <svg
              class="absolute left-3 top-2.5 h-5 w-5 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </div>
        </div>
        <button
          @click="openCreateModal"
          class="ml-4 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700"
        >
          + 创建算法模版
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="p-8 text-center text-gray-500">
      加载中...
    </div>

    <!-- 空状态 -->
    <div
      v-else-if="templates.length === 0"
      class="p-12 text-center text-gray-400"
    >
      <svg
        class="mx-auto h-12 w-12 text-gray-300"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
        />
      </svg>
      <p class="mt-2">暂无算法模版</p>
    </div>

    <!-- 模版列表 -->
    <div v-else class="divide-y divide-gray-200">
      <div
        v-for="template in templates"
        :key="template.id"
        class="p-6 hover:bg-gray-50 transition-colors"
      >
        <div class="flex items-start justify-between">
          <!-- 左侧：模版信息 -->
          <div class="flex-1">
            <div class="flex items-center space-x-3">
              <h3 class="text-lg font-semibold text-gray-900">
                {{ template.title }}
              </h3>
              <span class="px-2 py-1 text-xs rounded-full bg-orange-100 text-orange-700">
                算法模版
              </span>
            </div>

            <!-- 所属信息 -->
            <div class="mt-2 flex items-center space-x-4 text-sm text-gray-600">
              <span v-if="template.skillName">
                <span class="font-medium">技能:</span> {{ template.skillName }}
              </span>
              <span v-if="template.stageName">
                <span class="font-medium">阶段:</span> {{ template.stageName }}
              </span>
              <span v-if="template.focusAreaName">
                <span class="font-medium">Focus Area:</span> {{ template.focusAreaName }}
              </span>
              <span>
                <span class="font-medium">排序:</span> {{ template.displayOrder }}
              </span>
            </div>

            <!-- 代码预览 -->
            <div
              v-if="template.contentText"
              class="mt-3 p-3 bg-gray-50 rounded-md border border-gray-200"
            >
              <pre class="text-xs text-gray-700 font-mono overflow-x-auto max-h-32">{{ getCodePreview(template.contentText) }}</pre>
            </div>

            <!-- 时间信息 -->
            <div class="mt-2 text-xs text-gray-500">
              创建时间: {{ formatDate(template.createdAt) }}
              <span v-if="template.updatedAt" class="ml-3">
                更新时间: {{ formatDate(template.updatedAt) }}
              </span>
            </div>
          </div>

          <!-- 右侧：操作按钮 -->
          <div class="ml-4 flex items-center space-x-2">
            <button
              @click="editTemplate(template)"
              class="px-3 py-1.5 text-sm text-blue-600 hover:bg-blue-50 rounded-md"
            >
              编辑
            </button>
            <button
              @click="deleteTemplate(template)"
              class="px-3 py-1.5 text-sm text-red-600 hover:bg-red-50 rounded-md"
            >
              删除
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div
      v-if="totalPages > 1"
      class="p-4 border-t border-gray-200 flex items-center justify-between"
    >
      <div class="text-sm text-gray-700">
        共 {{ totalElements }} 个模版，第 {{ currentPage + 1 }} / {{ totalPages }} 页
      </div>
      <div class="flex space-x-2">
        <button
          @click="goToPage(currentPage - 1)"
          :disabled="currentPage === 0"
          class="px-3 py-1 text-sm border border-gray-300 rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
        >
          上一页
        </button>
        <button
          @click="goToPage(currentPage + 1)"
          :disabled="currentPage >= totalPages - 1"
          class="px-3 py-1 text-sm border border-gray-300 rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 创建/编辑模版弹窗 -->
    <AddLearningContentModal
      :is-open="showModal"
      :content="editingTemplate"
      :skills="skills"
      :stages="stages"
      :focus-areas="focusAreas"
      @close="closeModal"
      @save="saveTemplate"
    />

    <!-- 删除确认弹窗 -->
    <ConfirmDialog
      :is-open="showDeleteDialog"
      title="删除算法模版"
      :message="`确定要删除模版「${deletingTemplate?.title}」吗？此操作不可撤销。`"
      confirm-text="删除"
      cancel-text="取消"
      confirm-class="bg-red-600 hover:bg-red-700"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getContentsForAdmin, createContent, updateContent, deleteContent } from '@/api/learningContentApi'
import { getAllCareerPaths } from '@/api/careerPaths'
import { getSkillsByCareerPath } from '@/api/skills'
import { getStagesBySkill } from '@/api/learningStageApi'
import AddLearningContentModal from './AddLearningContentModal.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

// State
const templates = ref([])
const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(0)
const totalElements = ref(0)

const showModal = ref(false)
const editingTemplate = ref(null)
const showDeleteDialog = ref(false)
const deletingTemplate = ref(null)

// Data for modal
const skills = ref([])
const stages = ref([])
const focusAreas = ref([])

// Methods
const loadTemplates = async () => {
  loading.value = true
  try {
    const params = {
      contentType: 'algorithm_template',
      search: searchQuery.value || undefined,
      page: currentPage.value,
      size: pageSize.value
    }

    const response = await getContentsForAdmin(params)

    // Handle paginated response
    if (response.content) {
      templates.value = response.content
      totalPages.value = response.totalPages
      totalElements.value = response.totalElements
    } else {
      templates.value = response || []
      totalPages.value = 1
      totalElements.value = templates.value.length
    }
  } catch (error) {
    console.error('Failed to load algorithm templates:', error)
    alert('加载算法模版失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 0
  loadTemplates()
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadTemplates()
  }
}

const openCreateModal = async () => {
  await loadModalData()
  editingTemplate.value = null
  showModal.value = true
}

const editTemplate = async (template) => {
  await loadModalData()
  editingTemplate.value = { ...template }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingTemplate.value = null
}

const saveTemplate = async (formData) => {
  try {
    // Force content type to algorithm_template
    const data = {
      ...formData,
      contentType: 'algorithm_template'
    }

    if (editingTemplate.value?.id) {
      await updateContent(editingTemplate.value.id, data)
      alert('算法模版更新成功')
    } else {
      await createContent(data)
      alert('算法模版创建成功')
    }

    closeModal()
    await loadTemplates()
  } catch (error) {
    console.error('Failed to save template:', error)
    alert(error.response?.data?.message || '保存失败')
  }
}

const deleteTemplate = (template) => {
  deletingTemplate.value = template
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  try {
    await deleteContent(deletingTemplate.value.id)
    alert('删除成功')
    showDeleteDialog.value = false
    deletingTemplate.value = null
    await loadTemplates()
  } catch (error) {
    console.error('Failed to delete template:', error)
    alert(error.response?.data?.message || '删除失败')
  }
}

const cancelDelete = () => {
  showDeleteDialog.value = false
  deletingTemplate.value = null
}

const getCodePreview = (text) => {
  if (!text) return ''

  // Extract first 200 characters
  const preview = text.substring(0, 200)
  return text.length > 200 ? preview + '...' : preview
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Load data for modal (skills, stages, focus areas)
const loadModalData = async () => {
  try {
    // Load all career paths
    const careerPathsData = await getAllCareerPaths()
    const careerPaths = careerPathsData.data || careerPathsData || []

    // Load skills for all career paths
    const skillsPromises = careerPaths.map(cp => getSkillsByCareerPath(cp.id))
    const skillsResponses = await Promise.all(skillsPromises)

    const allSkills = skillsResponses.flatMap(response => {
      const data = response.data || response || []
      return Array.isArray(data) ? data : []
    })

    skills.value = allSkills

    // Extract all focus areas from skills
    focusAreas.value = allSkills.flatMap(skill => skill.focusAreas || [])

    // Load all stages for all skills
    const stagesPromises = allSkills.map(skill => getStagesBySkill(skill.id))
    const stagesResponses = await Promise.all(stagesPromises)

    stages.value = stagesResponses.flatMap(response => response || [])
  } catch (error) {
    console.error('Failed to load modal data:', error)
  }
}

// Initialize
onMounted(() => {
  loadTemplates()
})
</script>
