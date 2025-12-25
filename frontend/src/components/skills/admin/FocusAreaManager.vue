<template>
  <div>
    <!-- 添加按钮 -->
    <div class="mb-6">
      <button
        @click="showAddModal = true"
        class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
        </svg>
        添加专注领域
      </button>
    </div>

    <!-- 专注领域列表（可展开） -->
    <div v-if="focusAreas.length > 0" class="space-y-3">
      <div
        v-for="(area, index) in focusAreas"
        :key="area.id"
        class="bg-white border border-gray-200 rounded-lg overflow-hidden"
      >
        <!-- Focus Area 头部 -->
        <div class="p-4 flex items-start justify-between hover:bg-gray-50 transition-colors">
          <div class="flex-1">
            <div class="flex items-center gap-2">
              <!-- 展开/收起按钮 -->
              <button
                @click="toggleFocusArea(area.id)"
                class="text-gray-500 hover:text-gray-700"
              >
                <svg
                  class="w-5 h-5 transition-transform"
                  :class="{ 'rotate-90': expandedFocusAreaId === area.id }"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                </svg>
              </button>
              <span class="text-gray-500 font-mono">{{ index + 1 }}.</span>
              <h4 class="font-medium text-gray-900 cursor-pointer" @click="toggleFocusArea(area.id)">
                {{ area.name }}
              </h4>
            </div>
            <p class="text-sm text-gray-600 mt-1 ml-12">{{ area.description || '暂无描述' }}</p>
          </div>

          <div class="flex items-center gap-2">
            <button
              @click="handleEdit(area)"
              class="text-blue-600 hover:text-blue-800 text-sm"
            >
              编辑
            </button>
            <button
              @click="handleDelete(area.id)"
              class="text-red-600 hover:text-red-800 text-sm"
            >
              删除
            </button>
          </div>
        </div>

        <!-- 展开的学习内容区域 -->
        <div v-if="expandedFocusAreaId === area.id" class="border-t border-gray-200 bg-gray-50 p-4">
          <!-- 加载中状态 -->
          <div v-if="loadingContents" class="text-center py-8 text-gray-500">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            <p class="mt-2 text-sm">加载学习内容...</p>
          </div>

          <!-- 学习阶段和内容 -->
          <div v-else-if="learningData">
            <!-- 学习阶段 Tab -->
            <div class="flex gap-2 mb-4 border-b border-gray-300">
              <button
                v-for="stage in learningData.stages"
                :key="stage.id"
                @click="selectedStageId = stage.id"
                :class="[
                  'px-4 py-2 text-sm font-medium border-b-2 transition-colors',
                  selectedStageId === stage.id
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-600 hover:text-gray-900'
                ]"
              >
                {{ stage.stageName }}
                <span class="ml-1 text-xs text-gray-400">({{ stage.contents ? stage.contents.length : 0 }})</span>
              </button>
            </div>

            <!-- 当前阶段的学习内容 -->
            <div v-if="currentStageContents">
              <!-- 添加学习内容按钮 -->
              <div class="mb-4">
                <button
                  @click="handleAddContent"
                  class="inline-flex items-center px-3 py-1.5 bg-green-600 text-white text-sm rounded hover:bg-green-700"
                >
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                  </svg>
                  添加学习内容
                </button>
              </div>

              <!-- 学习内容列表 -->
              <div v-if="currentStageContents.length > 0" class="space-y-2">
                <div
                  v-for="content in currentStageContents"
                  :key="content.id"
                  class="bg-white border border-gray-200 rounded p-3 flex items-start justify-between hover:shadow-sm transition-shadow"
                >
                  <div class="flex-1">
                    <div class="flex items-center gap-2">
                      <h5 class="font-medium text-gray-900">{{ content.title }}</h5>
                      <span class="px-2 py-0.5 text-xs rounded bg-gray-100 text-gray-600">
                        {{ content.contentType }}
                      </span>
                      <span v-if="content.websiteDisplayName" class="px-2 py-0.5 text-xs rounded bg-blue-50 text-blue-600">
                        {{ content.websiteDisplayName }}
                      </span>
                    </div>
                    <p v-if="content.description" class="text-sm text-gray-600 mt-1">{{ content.description }}</p>
                    <div v-if="content.url" class="mt-1">
                      <a
                        :href="content.url"
                        target="_blank"
                        class="text-xs text-blue-500 hover:underline"
                      >
                        {{ content.url }}
                      </a>
                    </div>
                  </div>

                  <div class="flex items-center gap-2 ml-4">
                    <button
                      @click="handleEditContent(content)"
                      class="text-blue-600 hover:text-blue-800 text-sm"
                    >
                      编辑
                    </button>
                    <button
                      @click="handleDeleteContent(content.id)"
                      class="text-red-600 hover:text-red-800 text-sm"
                    >
                      删除
                    </button>
                  </div>
                </div>
              </div>

              <!-- 空状态 -->
              <div v-else class="text-center py-8 text-gray-400 bg-white rounded border border-gray-200">
                <p class="text-sm">该阶段暂无学习内容</p>
                <p class="text-xs mt-1">点击上方按钮添加</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12 text-gray-500 bg-gray-50 rounded-lg">
      <p>暂无专注领域</p>
      <p class="text-sm mt-1">点击上方按钮添加</p>
    </div>

    <!-- 添加/编辑 Focus Area 弹窗 -->
    <FocusAreaEditModal
      v-if="showAddModal || editingArea"
      :skillId="skillId"
      :focusArea="editingArea"
      @close="closeModal"
      @success="handleSuccess"
    />

    <!-- 添加/编辑学习内容弹窗 -->
    <LearningContentEditModal
      v-if="showContentModal"
      :focusAreaId="expandedFocusAreaId"
      :stageId="selectedStageId"
      :content="editingContent"
      @close="closeContentModal"
      @success="handleContentSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { deleteFocusArea } from '@/api/focusAreas'
import learningContentApi from '@/api/learningContentApi'
import FocusAreaEditModal from './FocusAreaEditModal.vue'
import LearningContentEditModal from './LearningContentEditModal.vue'

const props = defineProps({
  skillId: {
    type: Number,
    required: true
  },
  focusAreas: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['updated'])

const showAddModal = ref(false)
const editingArea = ref(null)
const expandedFocusAreaId = ref(null)
const loadingContents = ref(false)
const learningData = ref(null)
const selectedStageId = ref(null)
const showContentModal = ref(false)
const editingContent = ref(null)

const currentStageContents = computed(() => {
  if (!learningData.value || !selectedStageId.value) return []
  const stage = learningData.value.stages.find(s => s.id === selectedStageId.value)
  return stage?.contents || []
})

const toggleFocusArea = async (focusAreaId) => {
  if (expandedFocusAreaId.value === focusAreaId) {
    // 收起
    expandedFocusAreaId.value = null
    learningData.value = null
    selectedStageId.value = null
  } else {
    // 展开并加载学习内容
    expandedFocusAreaId.value = focusAreaId
    await loadLearningContents(focusAreaId)
  }
}

const loadLearningContents = async (focusAreaId) => {
  loadingContents.value = true
  try {
    const data = await learningContentApi.getContentsByFocusArea(focusAreaId)
    learningData.value = data

    // 默认选中第一个阶段
    if (data.stages && data.stages.length > 0) {
      selectedStageId.value = data.stages[0].id
    }
  } catch (error) {
    console.error('Failed to load learning contents:', error)
    alert('加载学习内容失败')
  } finally {
    loadingContents.value = false
  }
}

const handleEdit = (area) => {
  editingArea.value = area
}

const handleDelete = async (areaId) => {
  if (!confirm('确定要删除这个专注领域吗?')) {
    return
  }

  try {
    await deleteFocusArea(areaId)
    emit('updated')
  } catch (error) {
    console.error('Failed to delete focus area:', error)
    alert('删除失败')
  }
}

const closeModal = () => {
  showAddModal.value = false
  editingArea.value = null
}

const handleSuccess = () => {
  closeModal()
  emit('updated')
}

const handleAddContent = () => {
  editingContent.value = null
  showContentModal.value = true
}

const handleEditContent = (content) => {
  editingContent.value = content
  showContentModal.value = true
}

const handleDeleteContent = async (contentId) => {
  if (!confirm('确定要删除这个学习内容吗?')) {
    return
  }

  try {
    await learningContentApi.deleteContent(contentId)
    // 重新加载当前 Focus Area 的内容
    await loadLearningContents(expandedFocusAreaId.value)
  } catch (error) {
    console.error('Failed to delete learning content:', error)
    alert('删除失败')
  }
}

const closeContentModal = () => {
  showContentModal.value = false
  editingContent.value = null
}

const handleContentSuccess = async () => {
  closeContentModal()
  // 重新加载当前 Focus Area 的内容
  await loadLearningContents(expandedFocusAreaId.value)
}
</script>
