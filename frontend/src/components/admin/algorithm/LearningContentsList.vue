<template>
  <div>
    <!-- 顶部操作栏 -->
    <div class="flex items-center justify-between mb-6">
      <div class="text-sm text-gray-600">
        <span class="font-medium">{{ stageLabel }}</span> - 学习内容
      </div>
      <button
        @click="showAddModal = true"
        class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
      >
        <svg class="-ml-1 mr-2 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        添加内容
      </button>
    </div>

    <!-- 内容列表 -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      <p class="mt-2 text-sm text-gray-500">加载中...</p>
    </div>

    <div v-else-if="contents.length === 0" class="text-center py-12">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
      <p class="mt-2 text-sm text-gray-500">暂无学习内容</p>
      <p class="text-xs text-gray-400 mt-1">点击"添加内容"按钮开始添加</p>
    </div>

    <div v-else class="space-y-3">
      <div
        v-for="content in contents"
        :key="content.id"
        class="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <div class="flex items-center space-x-2 mb-2">
              <!-- 内容类型图标 -->
              <span v-if="content.contentType === 'article'" class="text-xl">📄</span>
              <span v-else-if="content.contentType === 'video'" class="text-xl">🎥</span>
              <span v-else-if="content.contentType === 'code_example'" class="text-xl">💻</span>
              <span v-else class="text-xl">📝</span>

              <h3 class="text-base font-medium text-gray-900">{{ content.title }}</h3>
            </div>

            <p v-if="content.description" class="text-sm text-gray-600 mb-2">
              {{ content.description }}
            </p>

            <div class="flex items-center space-x-4 text-xs text-gray-500">
              <span v-if="content.author">作者: {{ content.author }}</span>
              <a
                v-if="content.url"
                :href="content.url"
                target="_blank"
                rel="noopener noreferrer"
                class="text-blue-600 hover:text-blue-800 flex items-center"
              >
                查看链接
                <svg class="ml-1 h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                </svg>
              </a>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex items-center space-x-2 ml-4">
            <button
              @click="editContent(content)"
              class="p-1 text-gray-400 hover:text-blue-600"
              title="编辑"
            >
              <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </button>
            <button
              @click="deleteContent(content)"
              class="p-1 text-gray-400 hover:text-red-600"
              title="删除"
            >
              <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑内容Modal (TODO: 实现) -->
    <div v-if="showAddModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="showAddModal = false"></div>
        <div class="relative bg-white rounded-lg shadow-xl max-w-2xl w-full p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">添加学习内容</h3>
          <p class="text-sm text-gray-500">Modal待实现 - 将支持添加文章、视频、代码示例等</p>
          <div class="mt-4">
            <button
              @click="showAddModal = false"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
            >
              关闭
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  focusAreaId: {
    type: Number,
    required: true
  },
  stageType: {
    type: String,
    required: true
  }
})

// 学习阶段标签映射
const stageLabels = {
  theory: '基础原理',
  implementation: '实现代码',
  practice: '实战题目'
}

const stageLabel = computed(() => stageLabels[props.stageType] || '未知阶段')

// 数据状态
const loading = ref(false)
const contents = ref([])
const showAddModal = ref(false)

// 监听props变化，重新加载数据
watch([() => props.focusAreaId, () => props.stageType], () => {
  loadContents()
}, { immediate: true })

// 加载学习内容
function loadContents() {
  loading.value = true
  // TODO: 调用API加载数据
  // learningContentApi.getByFocusAreaAndStage(props.focusAreaId, props.stageType)

  // 模拟数据
  setTimeout(() => {
    contents.value = [
      {
        id: 1,
        title: '数组（顺序存储）基本原理',
        contentType: 'article',
        description: '介绍数组的基本特性、时间复杂度等',
        url: 'https://labuladong.online/algo/data-structure-basic/array-basic/',
        author: 'labuladong'
      },
      {
        id: 2,
        title: '动态数组代码实现',
        contentType: 'article',
        description: '深入讲解动态数组的扩容策略',
        url: 'https://labuladong.online/algo/data-structure-basic/array-implement/',
        author: 'labuladong'
      }
    ]
    loading.value = false
  }, 500)
}

// 编辑内容
function editContent(content) {
  // TODO: 打开编辑Modal
}

// 删除内容
function deleteContent(content) {
  if (confirm(`确定要删除"${content.title}"吗?`)) {
    // TODO: 调用API删除
  }
}
</script>
