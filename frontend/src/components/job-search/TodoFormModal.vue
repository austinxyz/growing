<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click.self="$emit('close')">
    <div
      :class="[
        'bg-white rounded-xl shadow-2xl w-full max-h-[90vh] overflow-hidden flex transition-all duration-300',
        showResourceSelector ? 'max-w-6xl' : 'max-w-3xl'
      ]"
    >
      <!-- 左侧：表单区域 -->
      <div class="flex-1 flex flex-col overflow-hidden">
        <!-- 标题栏 -->
        <div class="bg-gradient-to-r from-blue-600 to-purple-600 px-6 py-4 flex items-center justify-between">
        <h3 class="text-xl font-bold text-white flex items-center gap-2">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          {{ isEditMode ? '编辑准备清单项' : '创建准备清单项' }}
          </h3>
          <button
            @click="$emit('close')"
            class="text-white hover:bg-white hover:bg-opacity-20 rounded-lg p-2 transition-colors"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

      <!-- 表单内容 -->
      <div class="flex-1 overflow-y-auto p-6 space-y-5">
        <!-- 准备项类型和优先级 -->
        <div class="grid grid-cols-2 gap-4">
          <!-- 准备项类型 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">准备项类型</label>
            <select
              v-model="formData.todoType"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
            >
              <option value="General">📝 一般任务</option>
              <option value="StudyMaterial">📚 学习材料</option>
              <option value="Practice">🏆 练习刷题</option>
              <option value="ProjectReview">📁 项目回顾</option>
            </select>
          </div>

          <!-- 优先级 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              优先级: <span :class="getPriorityColor(formData.priority)">{{ getPriorityLabel(formData.priority) }}</span>
            </label>
            <div class="flex items-center gap-3">
              <span class="text-xs text-gray-600">低</span>
              <input
                type="range"
                v-model.number="formData.priority"
                min="0"
                max="5"
                class="flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                :style="{ background: getPriorityGradient(formData.priority) }"
              />
              <span class="text-xs text-gray-600">高</span>
            </div>
            <div class="flex justify-between text-xs text-gray-500 mt-1 px-2">
              <span>0</span>
              <span>1</span>
              <span>2</span>
              <span>3</span>
              <span>4</span>
              <span>5</span>
            </div>
          </div>
        </div>

        <!-- 标题 -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            标题 <span class="text-red-500">*</span>
          </label>
          <input
            v-model="formData.title"
            type="text"
            class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
            placeholder="例如：刷LeetCode Medium难度题"
            required
          />
        </div>

        <!-- 描述 -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
            描述（支持Markdown）
            <span class="text-xs text-gray-500 font-normal">可选</span>
          </label>
          <div class="mb-2 flex gap-2 text-xs text-gray-600">
            <span class="px-2 py-1 bg-gray-100 rounded">**粗体**</span>
            <span class="px-2 py-1 bg-gray-100 rounded">*斜体*</span>
            <span class="px-2 py-1 bg-gray-100 rounded">- 列表</span>
          </div>
          <textarea
            v-model="formData.description"
            rows="10"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg font-mono text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors resize-none"
            placeholder="详细描述准备项内容...&#10;&#10;示例：&#10;- LeetCode 200: Number of Islands&#10;- LeetCode 146: LRU Cache&#10;- 重点：DFS/BFS, 哈希表+双向链表"
          ></textarea>
        </div>

        <!-- 关联资源 -->
        <div>
          <div class="flex items-center justify-between mb-3">
            <label class="block text-sm font-semibold text-gray-700">关联资源</label>
            <button
              @click="showResourceSelector = true"
              type="button"
              class="px-3 py-1.5 bg-green-500 text-white text-xs rounded-lg hover:bg-green-600 transition-colors font-medium flex items-center gap-1"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              添加资源
            </button>
          </div>

          <!-- 已选资源列表 -->
          <div v-if="formData.resourceLinks.length === 0" class="text-sm text-gray-500 text-center py-4 bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
            暂无关联资源
          </div>

          <div v-else class="space-y-2">
            <div
              v-for="(link, idx) in formData.resourceLinks"
              :key="idx"
              class="flex gap-2 items-center p-3 bg-gray-50 rounded-lg border border-gray-200"
            >
              <span class="text-base">{{ getResourceTypeIcon(link.resourceType) }}</span>
              <div class="flex-1">
                <div class="font-medium text-sm text-gray-900">{{ link.title }}</div>
                <div class="text-xs text-gray-500">{{ getResourceTypeText(link.resourceType) }}</div>
              </div>
              <button
                @click="removeResource(idx)"
                type="button"
                class="px-2 py-1 text-red-600 hover:bg-red-50 rounded text-sm font-medium transition-colors"
              >
                删除
              </button>
            </div>
          </div>
        </div>
      </div>

        <!-- 底部按钮 -->
        <div class="px-6 py-4 bg-gray-50 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="$emit('close')"
            type="button"
            class="px-6 py-2.5 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 font-medium transition-colors"
          >
            取消
          </button>
          <button
            @click="save"
            type="button"
            class="px-6 py-2.5 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-lg hover:from-blue-700 hover:to-purple-700 font-medium shadow-md hover:shadow-lg transition-all"
          >
            {{ isEditMode ? '保存' : '创建' }}
          </button>
        </div>
      </div>

      <!-- 右侧：资源选择器面板 -->
      <div
        v-if="showResourceSelector"
        class="w-[480px] border-l border-gray-200 flex flex-col bg-gray-50"
      >
        <!-- 资源选择器标题栏 -->
        <div class="bg-gradient-to-r from-green-600 to-teal-600 px-6 py-4 flex items-center justify-between">
          <h3 class="text-xl font-bold text-white flex items-center gap-2">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
            </svg>
            选择资源
          </h3>
          <button
            @click="showResourceSelector = false"
            class="text-white hover:bg-white hover:bg-opacity-20 rounded-lg p-2 transition-colors"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 资源类型筛选和搜索 -->
        <div class="p-4 border-b border-gray-200 bg-white space-y-4">
          <!-- 资源类型 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">资源类型</label>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="type in resourceTypes"
                :key="type.value"
                @click="selectedResourceType = type.value; searchResources()"
                :class="[
                  'px-3 py-1.5 rounded-lg text-sm font-medium transition-all',
                  selectedResourceType === type.value
                    ? 'bg-green-600 text-white shadow-md'
                    : 'bg-white text-gray-700 border border-gray-300 hover:border-green-500 hover:bg-green-50'
                ]"
              >
                {{ type.icon }} {{ type.label }}
              </button>
            </div>
          </div>

          <!-- 搜索框 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">搜索关键词</label>
            <input
              v-model="resourceKeyword"
              @input="searchResources"
              type="text"
              placeholder="输入关键词搜索..."
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 transition-colors"
            />
          </div>
        </div>

        <!-- 搜索结果 -->
        <div class="flex-1 overflow-y-auto p-4">
          <div v-if="resourceLoading" class="text-center py-8 text-gray-500">
            <svg class="animate-spin h-8 w-8 mx-auto mb-2 text-green-600" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            正在搜索...
          </div>

          <div v-else-if="resourceResults.length === 0" class="text-center py-8 text-gray-500">
            <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M12 12h.01M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            </svg>
            暂无结果
          </div>

          <div v-else class="space-y-2">
            <div
              v-for="resource in resourceResults"
              :key="`${resource.resourceType}-${resource.id}`"
              @click="handleResourceSelect(resource)"
              class="flex items-start gap-3 p-3 bg-white border border-gray-200 rounded-lg hover:border-green-500 hover:shadow-md cursor-pointer transition-all"
            >
              <span class="text-xl flex-shrink-0">{{ getResourceIcon(resource.resourceType) }}</span>
              <div class="flex-1 min-w-0">
                <div class="font-semibold text-sm text-gray-900 mb-1 truncate">{{ resource.title }}</div>
                <div class="flex items-center gap-2 text-xs text-gray-600">
                  <span class="px-2 py-0.5 bg-gray-100 rounded">{{ getResourceTypeLabel(resource.resourceType) }}</span>
                </div>
              </div>
              <svg class="w-4 h-4 text-gray-400 flex-shrink-0 mt-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted } from 'vue'
import resourceSearchApi from '@/api/resourceSearchApi'

const props = defineProps({
  todo: {
    type: Object,
    default: null  // null表示创建新准备项
  }
})

const emit = defineEmits(['save', 'close'])

const isEditMode = computed(() => props.todo !== null)
const showResourceSelector = ref(false)

// 资源选择器相关状态
const resourceTypes = [
  { value: 'ALL', label: '全部', icon: '📋' },
  { value: 'Question', label: '试题', icon: '📝' },
  { value: 'LearningResource', label: '学习材料', icon: '📚' },
  { value: 'SystemDesignCase', label: '系统设计', icon: '🏗️' },
  { value: 'Project', label: '项目经验', icon: '📁' },
  { value: 'ManagementExperience', label: '管理经验', icon: '👥' }
]

const selectedResourceType = ref('ALL')
const resourceKeyword = ref('')
const resourceLoading = ref(false)
const resourceResults = ref([])

const formData = ref({
  title: '',
  description: '',
  todoType: 'General',
  priority: 0,
  resourceLinks: []
})

// 监听props.todo变化，初始化表单
watch(() => props.todo, (todo) => {
  if (todo) {
    // 编辑模式：加载现有数据
    formData.value = {
      title: todo.title,
      description: todo.description || '',
      todoType: todo.todoType || 'General',
      priority: todo.priority || 0,
      resourceLinks: todo.resourceLinks ? [...todo.resourceLinks] : []
    }
  } else {
    // 创建模式：重置表单
    formData.value = {
      title: '',
      description: '',
      todoType: 'General',
      priority: 0,
      resourceLinks: []
    }
  }
}, { immediate: true })

// 搜索资源
const searchResources = async () => {
  resourceLoading.value = true
  try {
    // From resourceSearchApi.js:15 - searchResources(resourceType, keyword)
    const data = await resourceSearchApi.searchResources(selectedResourceType.value, resourceKeyword.value)
    resourceResults.value = data || []
  } catch (error) {
    console.error('搜索资源失败:', error)
    resourceResults.value = []
  } finally {
    resourceLoading.value = false
  }
}

// 处理资源选择
const handleResourceSelect = (resource) => {
  // 避免重复添加
  const exists = formData.value.resourceLinks.some(
    link => link.resourceType === resource.resourceType && link.resourceId === resource.id
  )
  if (!exists) {
    formData.value.resourceLinks.push({
      resourceType: resource.resourceType,
      resourceId: resource.id,
      title: resource.title
    })
  }
  // 不关闭面板，允许继续选择
}

// 监听资源选择器打开，自动加载资源
watch(showResourceSelector, (newValue) => {
  if (newValue) {
    searchResources()
  }
})

// 删除资源
const removeResource = (index) => {
  formData.value.resourceLinks.splice(index, 1)
}

// 获取资源类型图标（用于资源选择器搜索结果）
const getResourceIcon = (type) => {
  const icons = {
    'Question': '📝',
    'LearningResource': '📚',
    'SystemDesignCase': '🏗️',
    'Project': '📁',
    'ManagementExperience': '👥',
    'ExternalLink': '🔗'
  }
  return icons[type] || '📄'
}

// 获取资源类型图标（用于已选资源列表）
const getResourceTypeIcon = (type) => {
  const icons = {
    'Question': '📝',
    'LearningResource': '📚',
    'SystemDesignCase': '🏗️',
    'Project': '📁',
    'ManagementExperience': '👥',
    'ExternalLink': '🔗'
  }
  return icons[type] || '📄'
}

// 获取资源类型标签（用于资源选择器）
const getResourceTypeLabel = (type) => {
  const labels = {
    'Question': '试题',
    'LearningResource': '学习材料',
    'SystemDesignCase': '系统设计案例',
    'Project': '项目经验',
    'ManagementExperience': '人员管理经验'
  }
  return labels[type] || '未知类型'
}

// 获取资源类型文本（用于已选资源列表）
const getResourceTypeText = (type) => {
  const texts = {
    'Question': '试题',
    'LearningResource': '学习材料',
    'SystemDesignCase': '系统设计案例',
    'Project': '项目经验',
    'ManagementExperience': '人员管理经验',
    'ExternalLink': '外部链接'
  }
  return texts[type] || '未知类型'
}

// 获取优先级标签
const getPriorityLabel = (priority) => {
  if (priority >= 4) return '高优先级'
  if (priority >= 3) return '中优先级'
  if (priority >= 1) return '低优先级'
  return '无优先级'
}

// 获取优先级颜色
const getPriorityColor = (priority) => {
  if (priority >= 4) return 'text-red-600 font-bold'
  if (priority >= 3) return 'text-yellow-600 font-semibold'
  if (priority >= 1) return 'text-blue-600'
  return 'text-gray-600'
}

// 获取优先级渐变色
const getPriorityGradient = (priority) => {
  const percentage = (priority / 5) * 100
  if (priority >= 4) {
    return `linear-gradient(to right, #fecaca 0%, #dc2626 ${percentage}%, #e5e7eb ${percentage}%)`
  } else if (priority >= 3) {
    return `linear-gradient(to right, #fef3c7 0%, #f59e0b ${percentage}%, #e5e7eb ${percentage}%)`
  } else if (priority >= 1) {
    return `linear-gradient(to right, #dbeafe 0%, #3b82f6 ${percentage}%, #e5e7eb ${percentage}%)`
  }
  return '#e5e7eb'
}

// 保存
const save = () => {
  if (!formData.value.title.trim()) {
    alert('请输入准备项标题')
    return
  }

  emit('save', {
    ...formData.value
  })
}
</script>

<style scoped>
/* 滑块样式优化 */
input[type="range"]::-webkit-slider-thumb {
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: white;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  border: 2px solid #3b82f6;
}

input[type="range"]::-moz-range-thumb {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: white;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  border: 2px solid #3b82f6;
}
</style>
