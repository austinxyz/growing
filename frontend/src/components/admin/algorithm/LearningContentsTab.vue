<template>
  <div>
    <!-- 筛选和操作栏 -->
    <div class="bg-white rounded-lg shadow p-4 mb-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <!-- Skill筛选 -->
          <div class="min-w-48">
            <label class="block text-sm font-medium text-gray-700 mb-1">技能</label>
            <select
              v-model="filters.skillId"
              @change="onSkillChange"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">全部技能</option>
              <option v-for="skill in skills" :key="skill.id" :value="skill.id">
                {{ skill.name }}
              </option>
            </select>
          </div>

          <!-- Stage筛选 -->
          <div class="min-w-48">
            <label class="block text-sm font-medium text-gray-700 mb-1">学习阶段</label>
            <select
              v-model="filters.stageId"
              @change="fetchContents"
              :disabled="!filters.skillId"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
            >
              <option value="">全部阶段</option>
              <option v-for="stage in stages" :key="stage.id" :value="stage.id">
                {{ stage.stageName }}
              </option>
            </select>
          </div>

          <!-- Focus Area筛选 -->
          <div class="min-w-48">
            <label class="block text-sm font-medium text-gray-700 mb-1">Focus Area</label>
            <select
              v-model="filters.focusAreaId"
              @change="fetchContents"
              :disabled="!filters.skillId"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
            >
              <option value="">全部</option>
              <option v-for="fa in focusAreas" :key="fa.id" :value="fa.id">
                {{ fa.name }}
              </option>
            </select>
          </div>

          <!-- Content Type筛选 -->
          <div class="min-w-40">
            <label class="block text-sm font-medium text-gray-700 mb-1">类型</label>
            <select
              v-model="filters.contentType"
              @change="fetchContents"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">全部类型</option>
              <option value="text">文本</option>
              <option value="code">代码</option>
              <option value="link">链接</option>
              <option value="algorithm_template">算法模版</option>
            </select>
          </div>
        </div>

        <button
          @click="showCreateModal"
          class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
        >
          + 创建学习内容
        </button>
      </div>
    </div>

    <!-- 学习内容列表 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <!-- 加载状态 -->
      <div v-if="loading" class="text-center py-12 text-gray-500">
        加载中...
      </div>

      <!-- 空状态 -->
      <div v-else-if="contents.length === 0" class="text-center py-12 text-gray-400">
        暂无学习内容
      </div>

      <!-- 表格 -->
      <table v-else class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              标题
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              类型
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              所属阶段
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Focus Area
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              排序
            </th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
              操作
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="content in contents" :key="content.id" class="hover:bg-gray-50">
            <td class="px-6 py-4">
              <div class="text-sm font-medium text-gray-900">{{ content.title }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="getContentTypeClass(content.contentType)">
                {{ getContentTypeLabel(content.contentType) }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500">{{ getStageName(content.stageId) }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500">{{ getFocusAreaName(content.focusAreaId) }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500">{{ content.displayOrder }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button
                @click="editContent(content)"
                class="text-blue-600 hover:text-blue-900 mr-3"
              >
                编辑
              </button>
              <button
                @click="confirmDelete(content)"
                class="text-red-600 hover:text-red-900"
              >
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 创建/编辑弹窗 -->
    <AddLearningContentModal
      :is-open="showModal"
      :content="editingContent"
      :skills="skills"
      :stages="allStages"
      :focus-areas="allFocusAreas"
      @close="closeModal"
      @save="handleSave"
    />

    <!-- 删除确认弹窗 -->
    <ConfirmDialog
      :is-open="showDeleteDialog"
      title="确认删除"
      :message="`确定要删除学习内容「${deletingContent?.title}」吗？此操作不可恢复。`"
      confirm-text="删除"
      confirm-class="bg-red-600 hover:bg-red-700"
      @confirm="handleDelete"
      @cancel="showDeleteDialog = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getContentsForAdmin, createContent, updateContent, deleteContent } from '@/api/learningContentApi'
import { getStagesBySkill } from '@/api/learningStageApi'
import { getSkillsByCareerPath } from '@/api/skills'
import { getAllCareerPaths } from '@/api/careerPaths'
import AddLearningContentModal from './AddLearningContentModal.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const loading = ref(false)
const contents = ref([])
const skills = ref([])
const stages = ref([])
const allStages = ref([])
const focusAreas = ref([])
const allFocusAreas = ref([])

const filters = ref({
  skillId: '',
  stageId: '',
  focusAreaId: '',
  contentType: ''
})

const showModal = ref(false)
const editingContent = ref(null)
const showDeleteDialog = ref(false)
const deletingContent = ref(null)

// 加载所有技能
const loadSkills = async () => {
  try {
    const careerPaths = await getAllCareerPaths()
    const allSkills = []
    for (const cp of careerPaths) {
      const cpSkills = await getSkillsByCareerPath(cp.id)
      allSkills.push(...cpSkills)
    }
    skills.value = allSkills

    // 收集所有Focus Areas
    const allFAs = []
    allSkills.forEach(skill => {
      if (skill.focusAreas) {
        allFAs.push(...skill.focusAreas)
      }
    })
    allFocusAreas.value = allFAs
  } catch (error) {
    console.error('Failed to load skills:', error)
  }
}

// 当技能改变时，加载对应的阶段和Focus Areas
const onSkillChange = async () => {
  filters.value.stageId = ''
  filters.value.focusAreaId = ''

  if (!filters.value.skillId) {
    stages.value = []
    focusAreas.value = []
    await fetchContents()
    return
  }

  try {
    // 加载学习阶段
    const stageList = await getStagesBySkill(filters.value.skillId)
    stages.value = stageList
    allStages.value = stageList

    // 加载Focus Areas
    const skill = skills.value.find(s => s.id === Number(filters.value.skillId))
    focusAreas.value = skill?.focusAreas || []
  } catch (error) {
    console.error('Failed to load stages/focus areas:', error)
  }

  await fetchContents()
}

// 获取学习内容列表
const fetchContents = async () => {
  loading.value = true
  try {
    const params = {}
    if (filters.value.skillId) params.skillId = filters.value.skillId
    if (filters.value.stageId) params.stageId = filters.value.stageId
    if (filters.value.focusAreaId) params.focusAreaId = filters.value.focusAreaId
    if (filters.value.contentType) params.contentType = filters.value.contentType

    const page = await getContentsForAdmin(params)
    contents.value = page.content || []
  } catch (error) {
    console.error('Failed to fetch contents:', error)
    contents.value = []
  } finally {
    loading.value = false
  }
}

// 辅助方法
const getStageName = (stageId) => {
  const stage = allStages.value.find(s => s.id === stageId)
  return stage ? stage.stageName : '未知'
}

const getFocusAreaName = (focusAreaId) => {
  if (!focusAreaId) return '-'
  const fa = allFocusAreas.value.find(f => f.id === focusAreaId)
  return fa ? fa.name : '未知'
}

const getContentTypeLabel = (type) => {
  const labels = {
    text: '文本',
    code: '代码',
    link: '链接',
    algorithm_template: '算法模版'
  }
  return labels[type] || type
}

const getContentTypeClass = (type) => {
  const classes = {
    text: 'px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-700',
    code: 'px-2 py-1 text-xs rounded-full bg-green-100 text-green-700',
    link: 'px-2 py-1 text-xs rounded-full bg-purple-100 text-purple-700',
    algorithm_template: 'px-2 py-1 text-xs rounded-full bg-orange-100 text-orange-700'
  }
  return classes[type] || 'px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-700'
}

// CRUD操作
const showCreateModal = () => {
  editingContent.value = null
  showModal.value = true
}

const editContent = (content) => {
  editingContent.value = { ...content }
  showModal.value = true
}

const handleSave = async (contentData) => {
  try {
    if (contentData.id) {
      await updateContent(contentData.id, contentData)
    } else {
      await createContent(contentData)
    }
    await fetchContents()
    closeModal()
  } catch (error) {
    console.error('Failed to save content:', error)
    alert('保存失败：' + (error.response?.data?.message || error.message))
  }
}

const confirmDelete = (content) => {
  deletingContent.value = content
  showDeleteDialog.value = true
}

const handleDelete = async () => {
  if (!deletingContent.value) return

  try {
    await deleteContent(deletingContent.value.id)
    await fetchContents()
    showDeleteDialog.value = false
    deletingContent.value = null
  } catch (error) {
    console.error('Failed to delete content:', error)
    alert('删除失败：' + (error.response?.data?.message || error.message))
  }
}

const closeModal = () => {
  showModal.value = false
  editingContent.value = null
}

onMounted(async () => {
  await loadSkills()
  await fetchContents()
})
</script>
