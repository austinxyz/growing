<template>
  <div class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-[60] p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-xl shadow-2xl max-w-4xl w-full max-h-[85vh] overflow-hidden flex flex-col">
      <!-- 标题栏 -->
      <div class="bg-gradient-to-r from-green-600 to-teal-600 px-6 py-4 flex items-center justify-between">
        <h3 class="text-xl font-bold text-white flex items-center gap-2">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
          </svg>
          选择资源
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

      <!-- 搜索区域 -->
      <div class="p-6 border-b border-gray-200 bg-gray-50 space-y-4">
        <!-- 资源类型筛选 -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">资源类型</label>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="type in resourceTypes"
              :key="type.value"
              @click="selectedType = type.value"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-all',
                selectedType === type.value
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
            v-model="keyword"
            @input="search"
            type="text"
            placeholder="输入关键词搜索..."
            class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 transition-colors"
          />
        </div>
      </div>

      <!-- 搜索结果列表 -->
      <div class="flex-1 overflow-y-auto p-6">
        <div v-if="loading" class="text-center py-8 text-gray-500">
          <svg class="animate-spin h-8 w-8 mx-auto mb-2 text-green-600" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          正在搜索...
        </div>

        <div v-else-if="results.length === 0" class="text-center py-8 text-gray-500">
          <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M12 12h.01M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
          </svg>
          暂无结果
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="resource in results"
            :key="`${resource.resourceType}-${resource.id}`"
            @click="selectResource(resource)"
            class="flex items-start gap-4 p-4 bg-white border border-gray-200 rounded-lg hover:border-green-500 hover:shadow-md cursor-pointer transition-all"
          >
            <span class="text-2xl flex-shrink-0">{{ getResourceIcon(resource.resourceType) }}</span>
            <div class="flex-1 min-w-0">
              <div class="font-semibold text-gray-900 mb-1 truncate">{{ resource.title }}</div>
              <div class="flex items-center gap-3 text-xs text-gray-600">
                <span class="px-2 py-1 bg-gray-100 rounded">{{ getResourceTypeLabel(resource.resourceType) }}</span>
                <span v-if="resource.meta && Object.keys(resource.meta).length > 0" class="text-gray-500">
                  {{ formatMeta(resource.meta) }}
                </span>
              </div>
            </div>
            <svg class="w-5 h-5 text-gray-400 flex-shrink-0 mt-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import resourceSearchApi from '@/api/resourceSearchApi'

const emit = defineEmits(['close', 'select'])

const resourceTypes = [
  { value: 'ALL', label: '全部', icon: '📋' },
  { value: 'Question', label: '试题', icon: '📝' },
  { value: 'LearningResource', label: '学习材料', icon: '📚' },
  { value: 'SystemDesignCase', label: '系统设计', icon: '🏗️' },
  { value: 'Project', label: '项目经验', icon: '📁' },
  { value: 'ManagementExperience', label: '管理经验', icon: '👥' }
]

const selectedType = ref('ALL')
const keyword = ref('')
const loading = ref(false)
const results = ref([])

// 搜索资源
const search = async () => {
  loading.value = true
  try {
    // From resourceSearchApi.js:15 - searchResources(resourceType, keyword)
    const data = await resourceSearchApi.searchResources(selectedType.value, keyword.value)
    results.value = data || []
  } catch (error) {
    console.error('搜索资源失败:', error)
    results.value = []
  } finally {
    loading.value = false
  }
}

// 选择资源
const selectResource = (resource) => {
  emit('select', resource)
}

// 获取资源图标
const getResourceIcon = (type) => {
  const icons = {
    'Question': '📝',
    'LearningResource': '📚',
    'SystemDesignCase': '🏗️',
    'Project': '📁',
    'ManagementExperience': '👥'
  }
  return icons[type] || '📄'
}

// 获取资源类型标签
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

// 格式化元数据
const formatMeta = (meta) => {
  return Object.entries(meta)
    .map(([key, value]) => `${key}: ${value}`)
    .join(' · ')
}

// 初始加载
onMounted(() => {
  search()
})
</script>
