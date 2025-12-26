<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-full px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">系统设计 - 基础知识管理</h1>
        <p class="text-sm text-gray-600 mt-1">管理系统设计基础知识的学习资料</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex h-[calc(100vh-9rem)]">
      <!-- 左侧面板 (25%) - 大分类Tab + Focus Area列表 -->
      <div class="w-1/4 bg-white border-r border-gray-200 overflow-hidden flex flex-col">
        <!-- Loading状态 -->
        <div v-if="loading" class="flex-1 flex items-center justify-center">
          <div class="text-center">
            <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-2 text-sm text-gray-500">加载中...</p>
          </div>
        </div>

        <!-- 大分类Tab + Focus Area列表 -->
        <template v-else>
          <!-- 大分类 Tab -->
          <div class="border-b border-gray-200">
            <nav class="flex flex-col space-y-1 p-2" aria-label="Categories">
              <button
                v-for="category in categories"
                :key="category.id"
                @click="selectedCategoryId = category.id; selectedFocusAreaId = null"
                :class="[
                  'px-4 py-2 text-sm font-medium rounded-md text-left',
                  selectedCategoryId === category.id
                    ? 'bg-blue-50 text-blue-600'
                    : 'text-gray-700 hover:bg-gray-50'
                ]"
              >
                {{ category.name }}
              </button>
            </nav>
          </div>

          <!-- Focus Area 列表 -->
          <div class="flex-1 overflow-y-auto p-4">
            <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">
              Focus Area
            </h3>
            <div v-if="filteredFocusAreas.length === 0" class="text-sm text-gray-500 text-center py-4">
              该分类下暂无Focus Area
            </div>
            <div v-else class="space-y-1">
              <button
                v-for="focusArea in filteredFocusAreas"
                :key="focusArea.id"
                @click="selectedFocusAreaId = focusArea.id"
                :class="[
                  'w-full text-left px-3 py-2 text-sm rounded-md',
                  selectedFocusAreaId === focusArea.id
                    ? 'bg-blue-50 text-blue-600 font-medium'
                    : 'text-gray-700 hover:bg-gray-50'
                ]"
              >
                {{ focusArea.name }}
              </button>
            </div>
          </div>
        </template>
      </div>

      <!-- 右侧面板 (75%) - 学习资料管理 -->
      <div class="flex-1 bg-white overflow-hidden flex flex-col">
        <!-- 未选择Focus Area时的提示 -->
        <div v-if="!selectedFocusAreaId" class="flex-1 flex items-center justify-center text-gray-500">
          <div class="text-center">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            <p class="mt-2 text-sm">请先从左侧选择一个Focus Area</p>
          </div>
        </div>

        <!-- 选中Focus Area后显示学习资料 -->
        <template v-else>
          <!-- Loading状态 -->
          <div v-if="loadingContents" class="flex-1 flex items-center justify-center">
            <div class="text-center">
              <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <p class="mt-2 text-sm text-gray-500">加载学习资料中...</p>
            </div>
          </div>

          <!-- 学习资料内容 -->
          <template v-else>
            <!-- 标题和操作 -->
            <div class="border-b border-gray-200 px-6 py-4">
              <div class="flex justify-between items-center">
                <h2 class="text-lg font-semibold text-gray-900">
                  {{ selectedFocusArea?.name }} - 学习资料
                </h2>
                <button
                  @click="showAddResourceModal = true"
                  class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
                >
                  + 添加学习资料
                </button>
              </div>
            </div>

            <!-- 学习资料列表 -->
            <div class="flex-1 overflow-y-auto p-6">
              <div v-if="learningContents.length === 0" class="text-center py-12">
                <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
                <p class="mt-2 text-sm text-gray-500">暂无学习资料</p>
                <button
                  @click="showAddResourceModal = true"
                  class="mt-4 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
                >
                  添加第一个学习资料
                </button>
              </div>

              <div v-else class="space-y-4">
                <div
                  v-for="content in learningContents"
                  :key="content.id"
                  class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
                >
                  <div class="flex justify-between items-start">
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-2">
                        <span :class="getContentTypeBadgeClass(content.contentType)" class="px-2 py-1 text-xs font-medium rounded">
                          {{ getContentTypeLabel(content.contentType) }}
                        </span>
                        <h3 class="text-base font-medium text-gray-900">{{ content.title }}</h3>
                      </div>
                      <p v-if="content.author" class="text-sm text-gray-600 mb-2">作者: {{ content.author }}</p>
                      <p v-if="content.description" class="text-sm text-gray-600 mb-2">{{ content.description }}</p>
                      <a
                        v-if="content.url"
                        :href="content.url"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="text-sm text-blue-600 hover:text-blue-800"
                      >
                        {{ content.url }}
                      </a>
                    </div>
                    <div class="flex gap-2 ml-4">
                      <button
                        @click="editResource(content)"
                        class="text-gray-600 hover:text-gray-800"
                      >
                        编辑
                      </button>
                      <button
                        @click="deleteResource(content)"
                        class="text-red-600 hover:text-red-800"
                      >
                        删除
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </template>
      </div>
    </div>

    <!-- 添加/编辑学习资料Modal -->
    <div v-if="showAddResourceModal" class="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">
            {{ editingResource ? '编辑学习资料' : '添加学习资料' }}
          </h3>
        </div>
        <div class="px-6 py-4 space-y-4">
          <!-- 资料类型 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">资料类型 *</label>
            <select v-model="resourceForm.contentType" class="w-full px-3 py-2 border border-gray-300 rounded-md">
              <option value="article">文章</option>
              <option value="video">视频</option>
            </select>
          </div>

          <!-- 标题 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">标题 *</label>
            <input
              v-model="resourceForm.title"
              type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="输入资料标题"
            />
          </div>

          <!-- URL -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">链接 *</label>
            <input
              v-model="resourceForm.url"
              type="url"
              class="w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="https://..."
            />
          </div>

          <!-- 作者 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">作者</label>
            <input
              v-model="resourceForm.author"
              type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="作者名称"
            />
          </div>

          <!-- 描述 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
            <textarea
              v-model="resourceForm.description"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="简要描述资料内容"
            ></textarea>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="closeResourceModal"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveResource"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            :disabled="!canSaveResource"
          >
            {{ editingResource ? '保存' : '添加' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import api from '@/api'
import majorCategoryApi from '@/api/majorCategoryApi'

// State
const loading = ref(false)
const loadingContents = ref(false)
const categories = ref([])
const focusAreas = ref([])
const learningContents = ref([])
const selectedCategoryId = ref(null)
const selectedFocusAreaId = ref(null)
const showAddResourceModal = ref(false)
const editingResource = ref(null)

// 系统设计的Skill ID (固定为2)
const SYSTEM_DESIGN_SKILL_ID = 2

// Form
const resourceForm = ref({
  contentType: 'article',
  title: '',
  url: '',
  author: '',
  description: ''
})

// Computed
const filteredFocusAreas = computed(() => {
  if (!selectedCategoryId.value) return focusAreas.value
  return focusAreas.value.filter(fa =>
    fa.categoryIds && fa.categoryIds.includes(selectedCategoryId.value)
  )
})

const selectedFocusArea = computed(() => {
  return focusAreas.value.find(fa => fa.id === selectedFocusAreaId.value)
})

const canSaveResource = computed(() => {
  return resourceForm.value.title && resourceForm.value.url
})

// Methods
const loadCategories = async () => {
  try {
    loading.value = true
    // 只加载系统设计skill的分类（skillId=2）
    const data = await majorCategoryApi.getAllMajorCategories(SYSTEM_DESIGN_SKILL_ID)
    categories.value = data

    if (categories.value.length > 0) {
      selectedCategoryId.value = categories.value[0].id
    }
  } catch (error) {
    console.error('Failed to load categories:', error)
  } finally {
    loading.value = false
  }
}

const loadFocusAreas = async () => {
  try {
    const data = await api.get(`/skills/${SYSTEM_DESIGN_SKILL_ID}`)
    focusAreas.value = data.focusAreas || []
  } catch (error) {
    console.error('Failed to load focus areas:', error)
    focusAreas.value = []
  }
}

const loadLearningContents = async () => {
  if (!selectedFocusAreaId.value) return

  try {
    loadingContents.value = true
    // ⚠️ 修改为正确的API路径：使用查询参数而不是路径参数
    const data = await api.get(`/admin/learning-contents?focusAreaId=${selectedFocusAreaId.value}&page=0&size=1000`)
    // 注意：后端返回的是Page对象，需要取content字段
    learningContents.value = data.content || []
  } catch (error) {
    console.error('Failed to load learning contents:', error)
    learningContents.value = []
  } finally {
    loadingContents.value = false
  }
}

const editResource = (content) => {
  editingResource.value = content
  resourceForm.value = {
    contentType: content.contentType,
    title: content.title,
    url: content.url,
    author: content.author || '',
    description: content.description || ''
  }
  showAddResourceModal.value = true
}

const deleteResource = async (content) => {
  if (!confirm(`确定要删除学习资料"${content.title}"吗？`)) {
    return
  }

  try {
    await api.delete(`/admin/learning-contents/${content.id}`)
    await loadLearningContents()
  } catch (error) {
    console.error('Failed to delete learning content:', error)
    alert('删除失败')
  }
}

const closeResourceModal = () => {
  showAddResourceModal.value = false
  editingResource.value = null
  resourceForm.value = {
    contentType: 'article',
    title: '',
    url: '',
    author: '',
    description: ''
  }
}

const saveResource = async () => {
  if (!canSaveResource.value) return

  try {
    const payload = {
      focusAreaId: selectedFocusAreaId.value,
      contentType: resourceForm.value.contentType,
      title: resourceForm.value.title,
      url: resourceForm.value.url,
      author: resourceForm.value.author || null,
      description: resourceForm.value.description || null
    }

    if (editingResource.value) {
      await api.put(`/admin/learning-contents/${editingResource.value.id}`, payload)
    } else {
      await api.post('/admin/learning-contents', payload)
    }

    await loadLearningContents()
    closeResourceModal()
  } catch (error) {
    console.error('Failed to save learning content:', error)
    alert('保存失败')
  }
}

const getContentTypeBadgeClass = (type) => {
  return type === 'video'
    ? 'bg-purple-100 text-purple-700'
    : 'bg-green-100 text-green-700'
}

const getContentTypeLabel = (type) => {
  return type === 'video' ? '视频' : '文章'
}

// Watch
watch(selectedFocusAreaId, () => {
  if (selectedFocusAreaId.value) {
    loadLearningContents()
  }
})

// Lifecycle
onMounted(async () => {
  await loadCategories()
  await loadFocusAreas()
})
</script>
