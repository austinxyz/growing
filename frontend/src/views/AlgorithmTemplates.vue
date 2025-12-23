<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">算法模版库</h1>
        <p class="text-sm text-gray-600 mt-1">浏览和学习常用算法模版代码</p>
      </div>
    </div>

    <!-- 搜索和筛选栏 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex items-center space-x-4">
          <!-- 搜索框 -->
          <div class="flex-1 max-w-md">
            <div class="relative">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索算法模版..."
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

          <!-- 结果统计 -->
          <div class="text-sm text-gray-600">
            共 {{ totalElements }} 个模版
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="max-w-7xl mx-auto px-6 py-6">
      <!-- 加载状态 -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        <p class="mt-2 text-gray-500">加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="templates.length === 0" class="text-center py-12">
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
        <p class="mt-2 text-gray-400">暂无算法模版</p>
      </div>

      <!-- 模版列表 -->
      <div v-else class="grid grid-cols-1 gap-6">
        <div
          v-for="template in templates"
          :key="template.id"
          class="bg-white rounded-lg border border-gray-200 shadow-sm hover:shadow-md transition-shadow overflow-hidden"
        >
          <!-- 模版头部 -->
          <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <h3 class="text-lg font-semibold text-gray-900">{{ template.title }}</h3>

                <!-- 元信息 -->
                <div class="mt-2 flex items-center space-x-4 text-sm text-gray-600">
                  <span v-if="template.skillName" class="flex items-center">
                    <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
                    </svg>
                    {{ template.skillName }}
                  </span>
                  <span v-if="template.stageName" class="flex items-center">
                    <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                    {{ template.stageName }}
                  </span>
                  <span v-if="template.focusAreaName" class="flex items-center">
                    <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                    </svg>
                    {{ template.focusAreaName }}
                  </span>
                  <span class="flex items-center text-gray-500">
                    <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    {{ formatDate(template.createdAt) }}
                  </span>
                </div>
              </div>

              <!-- 展开/折叠按钮 -->
              <button
                @click="toggleTemplate(template.id)"
                class="ml-4 p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-md transition-colors"
              >
                <svg
                  :class="['w-5 h-5 transition-transform', expandedTemplates.includes(template.id) ? 'rotate-180' : '']"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
            </div>
          </div>

          <!-- 模版内容（展开时显示） -->
          <div v-if="expandedTemplates.includes(template.id)" class="px-6 py-4">
            <div
              v-if="template.contentText"
              v-html="renderMarkdown(template.contentText)"
              class="prose prose-sm max-w-none"
            ></div>
            <div v-else class="text-gray-400 text-center py-4">
              暂无内容
            </div>
          </div>

          <!-- 快速预览（折叠时显示） -->
          <div v-else class="px-6 py-4 bg-gray-50">
            <pre class="text-xs text-gray-600 font-mono overflow-x-auto max-h-24">{{ getCodePreview(template.contentText) }}</pre>
            <div class="mt-2 text-xs text-blue-600 text-center">
              点击展开查看完整代码
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div
        v-if="totalPages > 1"
        class="mt-6 flex items-center justify-center space-x-2"
      >
        <button
          @click="goToPage(currentPage - 1)"
          :disabled="currentPage === 0"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          上一页
        </button>

        <div class="flex items-center space-x-1">
          <span class="px-3 py-2 text-sm text-gray-700">
            第 {{ currentPage + 1 }} / {{ totalPages }} 页
          </span>
        </div>

        <button
          @click="goToPage(currentPage + 1)"
          :disabled="currentPage >= totalPages - 1"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { getAlgorithmTemplates } from '@/api/learningContentApi'

// Configure marked for code highlighting
marked.setOptions({
  breaks: true,
  gfm: true,
  highlight: function(code, lang) {
    return `<pre><code class="language-${lang}">${code}</code></pre>`
  }
})

// State
const templates = ref([])
const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(0)
const totalElements = ref(0)
const expandedTemplates = ref([])

// Methods
const loadTemplates = async () => {
  loading.value = true
  try {
    const params = {
      search: searchQuery.value || undefined,
      page: currentPage.value,
      size: pageSize.value
    }

    const response = await getAlgorithmTemplates(params)

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

const toggleTemplate = (templateId) => {
  const index = expandedTemplates.value.indexOf(templateId)
  if (index > -1) {
    expandedTemplates.value.splice(index, 1)
  } else {
    expandedTemplates.value.push(templateId)
  }
}

const getCodePreview = (text) => {
  if (!text) return '暂无内容'

  // Extract first 150 characters
  const preview = text.substring(0, 150)
  return text.length > 150 ? preview + '...' : preview
}

const renderMarkdown = (text) => {
  if (!text) return ''
  const html = marked(text)
  return DOMPurify.sanitize(html)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// Initialize
onMounted(() => {
  loadTemplates()
})
</script>

<style scoped>
/* Markdown prose styling */
:deep(.prose) {
  color: #374151;
}

:deep(.prose h1),
:deep(.prose h2),
:deep(.prose h3),
:deep(.prose h4) {
  color: #111827;
  font-weight: 600;
  margin-top: 1em;
  margin-bottom: 0.5em;
}

:deep(.prose p) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  line-height: 1.6;
}

:deep(.prose code) {
  background-color: #f3f4f6;
  padding: 0.2em 0.4em;
  border-radius: 0.25rem;
  font-size: 0.875em;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

:deep(.prose pre) {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 1em;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin-top: 1em;
  margin-bottom: 1em;
}

:deep(.prose pre code) {
  background-color: transparent;
  padding: 0;
  color: #f9fafb;
  font-size: 0.875em;
}

:deep(.prose ul),
:deep(.prose ol) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  padding-left: 1.5em;
}

:deep(.prose li) {
  margin-top: 0.25em;
  margin-bottom: 0.25em;
}

:deep(.prose a) {
  color: #2563eb;
  text-decoration: none;
}

:deep(.prose a:hover) {
  text-decoration: underline;
}

:deep(.prose blockquote) {
  border-left: 4px solid #e5e7eb;
  padding-left: 1em;
  color: #6b7280;
  font-style: italic;
  margin-top: 1em;
  margin-bottom: 1em;
}

:deep(.prose table) {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1em;
  margin-bottom: 1em;
}

:deep(.prose th),
:deep(.prose td) {
  border: 1px solid #e5e7eb;
  padding: 0.5em;
  text-align: left;
}

:deep(.prose th) {
  background-color: #f9fafb;
  font-weight: 600;
}
</style>
