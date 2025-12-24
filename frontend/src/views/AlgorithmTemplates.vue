<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <!-- 顶部标题栏 -->
    <div class="bg-white border-b border-gray-200">
      <div class="px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">算法模版库</h1>
        <p class="text-sm text-gray-600 mt-1">浏览和学习常用算法模版代码</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：模版列表 -->
      <div class="w-96 bg-white border-r border-gray-200 flex flex-col">
        <!-- 搜索栏 -->
        <div class="p-4 border-b border-gray-200">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索算法模版..."
              class="w-full px-3 py-2 pl-10 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
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
          <div class="text-xs text-gray-500 mt-2">
            共 {{ totalElements }} 个模版
          </div>
        </div>

        <!-- 模版列表 -->
        <div class="flex-1 overflow-y-auto">
          <!-- Loading状态 -->
          <div v-if="loading" class="flex items-center justify-center py-12">
            <div class="text-center">
              <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <p class="mt-2 text-sm text-gray-500">加载中...</p>
            </div>
          </div>

          <!-- 模版列表项 -->
          <div v-else-if="templates.length > 0">
            <div
              v-for="template in templates"
              :key="template.id"
              @click="selectTemplate(template)"
              :class="[
                'p-4 border-b border-gray-200 cursor-pointer hover:bg-gray-50 transition-colors',
                selectedTemplate?.id === template.id ? 'bg-blue-50 border-l-4 border-l-blue-600' : ''
              ]"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1 min-w-0">
                  <h3 class="text-sm font-medium text-gray-900 truncate">{{ template.title }}</h3>
                  <p class="text-xs text-gray-500 mt-1 line-clamp-2">{{ template.description || '无描述' }}</p>
                  <div class="flex items-center flex-wrap gap-1 mt-2">
                    <span v-if="template.contentData?.language" class="px-2 py-0.5 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                      {{ template.contentData.language }}
                    </span>
                    <span v-if="template.contentData?.complexity?.time" class="px-2 py-0.5 text-xs rounded-full bg-amber-100 text-amber-700">
                      时间: {{ template.contentData.complexity.time }}
                    </span>
                    <span v-if="template.contentData?.complexity?.space" class="px-2 py-0.5 text-xs rounded-full bg-emerald-100 text-emerald-700">
                      空间: {{ template.contentData.complexity.space }}
                    </span>
                    <span class="text-xs text-gray-400">{{ formatDate(template.updatedAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="flex flex-col items-center justify-center py-12 px-4">
            <svg class="h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <h3 class="mt-2 text-sm font-medium text-gray-900">暂无算法模版</h3>
            <p class="mt-1 text-xs text-gray-500 text-center">{{ searchQuery ? '未找到匹配的模版' : '系统中还没有模版' }}</p>
          </div>
        </div>
      </div>

      <!-- 右侧：详情查看面板 -->
      <div class="flex-1 overflow-y-auto bg-gray-50">
        <!-- 未选中任何模版 -->
        <div v-if="!selectedTemplate" class="flex items-center justify-center h-full">
          <div class="text-center text-gray-400">
            <svg class="mx-auto h-16 w-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="mt-4 text-sm">请从左侧选择一个模版</p>
          </div>
        </div>

        <!-- 模版详情查看区域 -->
        <div v-else class="p-6">
          <div class="max-w-4xl mx-auto">
            <!-- 模版标题和元信息 -->
            <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
              <h2 class="text-2xl font-bold text-gray-900 mb-4">{{ selectedTemplate.title }}</h2>

              <!-- 元信息标签 -->
              <div class="flex flex-wrap items-center gap-2">
                <span v-if="selectedTemplate.skillName" class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-purple-100 text-purple-700">
                  <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
                  </svg>
                  {{ selectedTemplate.skillName }}
                </span>
                <span v-if="selectedTemplate.stageName" class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-700">
                  <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  {{ selectedTemplate.stageName }}
                </span>
                <span v-if="selectedTemplate.focusAreaName" class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-green-100 text-green-700">
                  <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                  </svg>
                  {{ selectedTemplate.focusAreaName }}
                </span>
                <span class="inline-flex items-center px-3 py-1 text-sm text-gray-500">
                  <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  {{ formatDate(selectedTemplate.createdAt) }}
                </span>
              </div>
            </div>

            <!-- 复杂度信息 -->
            <div v-if="selectedTemplate.contentData?.complexity" class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <svg class="w-5 h-5 mr-2 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
                时间/空间复杂度
              </h3>
              <div class="grid grid-cols-2 gap-4">
                <div v-if="selectedTemplate.contentData.complexity.time" class="flex items-center space-x-3">
                  <span class="text-sm font-medium text-gray-700">时间复杂度:</span>
                  <span class="px-3 py-1 text-sm font-mono font-semibold rounded-md bg-amber-100 text-amber-800">
                    {{ selectedTemplate.contentData.complexity.time }}
                  </span>
                </div>
                <div v-if="selectedTemplate.contentData.complexity.space" class="flex items-center space-x-3">
                  <span class="text-sm font-medium text-gray-700">空间复杂度:</span>
                  <span class="px-3 py-1 text-sm font-mono font-semibold rounded-md bg-emerald-100 text-emerald-800">
                    {{ selectedTemplate.contentData.complexity.space }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 模版代码 -->
            <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <svg class="w-5 h-5 mr-2 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
                </svg>
                模版代码
                <span v-if="selectedTemplate.contentData?.language" class="ml-3 px-2 py-1 text-xs font-semibold rounded-md bg-blue-100 text-blue-800">
                  {{ selectedTemplate.contentData.language }}
                </span>
              </h3>
              <div v-if="selectedTemplate.contentData?.code" class="bg-gray-900 rounded-md p-4 overflow-x-auto">
                <pre class="text-sm text-gray-100 font-mono whitespace-pre-wrap">{{ selectedTemplate.contentData.code }}</pre>
              </div>
              <div v-else class="text-gray-400 text-center py-8">
                暂无代码
              </div>
            </div>

            <!-- 关键要点 -->
            <div v-if="selectedTemplate.contentData?.keyPoints?.length" class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <svg class="w-5 h-5 mr-2 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                关键要点
              </h3>
              <ul class="space-y-2">
                <li v-for="(point, index) in selectedTemplate.contentData.keyPoints" :key="index" class="flex items-start">
                  <svg class="w-5 h-5 mr-2 mt-0.5 text-green-600 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                  <span class="text-sm text-gray-700">{{ point }}</span>
                </li>
              </ul>
            </div>

            <!-- 我的笔记区域 -->
            <div class="bg-amber-50 rounded-lg border border-amber-200 p-6">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold text-gray-900 flex items-center">
                  <svg class="w-5 h-5 mr-2 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  我的笔记
                </h3>
                <div v-if="currentNote?.note" class="text-xs text-gray-500">
                  最后更新: {{ formatDate(currentNote.note.updatedAt) }}
                </div>
              </div>

              <!-- 查看模式 -->
              <div v-if="!isEditingNote">
                <div
                  v-if="currentNote?.note?.noteContent"
                  v-html="renderMarkdown(currentNote.note.noteContent)"
                  class="prose prose-sm max-w-none text-gray-700 bg-white rounded-md p-4 border border-amber-300"
                ></div>
                <div v-else class="text-gray-500 text-sm italic text-center py-8 bg-white rounded-md border border-amber-200">
                  点击"添加笔记"按钮记录你的学习心得
                </div>

                <div class="mt-4 flex space-x-3">
                  <button
                    @click="startEditingNote"
                    class="px-4 py-2 text-sm font-medium text-amber-700 bg-white border border-amber-300 rounded-md hover:bg-amber-50 transition-colors"
                  >
                    {{ currentNote?.note ? '编辑笔记' : '添加笔记' }}
                  </button>
                  <button
                    v-if="currentNote?.note"
                    @click="handleDeleteNote"
                    class="px-4 py-2 text-sm font-medium text-red-600 bg-white border border-red-300 rounded-md hover:bg-red-50 transition-colors"
                  >
                    删除笔记
                  </button>
                </div>
              </div>

              <!-- 编辑模式 -->
              <div v-else>
                <textarea
                  v-model="editContent"
                  placeholder="在此输入你的学习笔记（支持Markdown格式）..."
                  class="w-full h-48 px-4 py-3 text-sm border border-amber-300 rounded-md focus:outline-none focus:ring-2 focus:ring-amber-500 resize-none font-mono bg-white"
                ></textarea>

                <div class="mt-4 flex space-x-3">
                  <button
                    @click="handleSaveNote"
                    :disabled="savingNote"
                    class="px-4 py-2 text-sm font-medium text-white bg-amber-600 rounded-md hover:bg-amber-700 disabled:opacity-50 transition-colors"
                  >
                    {{ savingNote ? '保存中...' : '保存笔记' }}
                  </button>
                  <button
                    @click="cancelEditingNote"
                    :disabled="savingNote"
                    class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 transition-colors"
                  >
                    取消
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { marked } from 'marked'
import {
  getAlgorithmTemplates,
  getTemplateNote,
  saveOrUpdateTemplateNote,
  deleteTemplateNote
} from '@/api/learningContentApi'

// 数据状态
const templates = ref([])
const selectedTemplate = ref(null)
const searchQuery = ref('')
const loading = ref(false)
const totalElements = ref(0)

// 笔记相关状态
const currentNote = ref(null)
const isEditingNote = ref(false)
const editContent = ref('')
const savingNote = ref(false)

// 解析contentData JSON字符串
const parseTemplateData = (template) => {
  if (template.contentData && typeof template.contentData === 'string') {
    try {
      template.contentData = JSON.parse(template.contentData)
    } catch (error) {
      console.error('Failed to parse contentData:', error)
      template.contentData = {}
    }
  }
  return template
}

// 搜索处理
const handleSearch = async () => {
  loading.value = true
  try {
    const data = await getAlgorithmTemplates({ search: searchQuery.value.trim(), page: 0, size: 100 })
    templates.value = (data.content || []).map(parseTemplateData)
    totalElements.value = data.totalElements || 0
  } catch (error) {
    console.error('Failed to search templates:', error)
  } finally {
    loading.value = false
  }
}

// 选择模版
const selectTemplate = async (template) => {
  selectedTemplate.value = template
  isEditingNote.value = false
  await loadNoteForTemplate(template.id)
}

// 加载模版笔记
const loadNoteForTemplate = async (templateId) => {
  try {
    const note = await getTemplateNote(templateId)
    currentNote.value = { note }
  } catch (error) {
    if (error.response?.status === 404) {
      currentNote.value = { note: null }
    } else {
      console.error('Failed to load note:', error)
    }
  }
}

// 笔记编辑
const startEditingNote = () => {
  editContent.value = currentNote.value?.note?.noteContent || ''
  isEditingNote.value = true
}

const cancelEditingNote = () => {
  isEditingNote.value = false
  editContent.value = ''
}

const handleSaveNote = async () => {
  if (!selectedTemplate.value) return

  savingNote.value = true
  try {
    await saveOrUpdateTemplateNote(selectedTemplate.value.id, editContent.value.trim())

    // 重新加载笔记
    await loadNoteForTemplate(selectedTemplate.value.id)
    isEditingNote.value = false
    editContent.value = ''
  } catch (error) {
    console.error('Failed to save note:', error)
    alert('保存笔记失败')
  } finally {
    savingNote.value = false
  }
}

const handleDeleteNote = async () => {
  if (!selectedTemplate.value || !currentNote.value?.note) return

  if (!confirm('确定要删除这条笔记吗？')) return

  try {
    await deleteTemplateNote(selectedTemplate.value.id)
    currentNote.value = { note: null }
  } catch (error) {
    console.error('Failed to delete note:', error)
    alert('删除笔记失败')
  }
}

// Markdown渲染
const renderMarkdown = (text) => {
  if (!text) return ''
  return marked(text, {
    breaks: true,
    gfm: true
  })
}

// 日期格式化
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 初始化
onMounted(async () => {
  loading.value = true
  try {
    const data = await getAlgorithmTemplates({ search: '', page: 0, size: 100 })
    templates.value = (data.content || []).map(parseTemplateData)
    totalElements.value = data.totalElements || 0
  } catch (error) {
    console.error('Failed to load templates:', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* Prose 样式 */
.prose {
  color: #374151;
  line-height: 1.75;
}

.prose p {
  margin-top: 0.75em;
  margin-bottom: 0.75em;
}

.prose pre {
  background-color: #1f2937;
  color: #e5e7eb;
  padding: 1rem;
  border-radius: 0.375rem;
  overflow-x: auto;
  margin-top: 0.75em;
  margin-bottom: 0.75em;
}

.prose code {
  background-color: #f3f4f6;
  color: #1f2937;
  padding: 0.125rem 0.25rem;
  border-radius: 0.25rem;
  font-size: 0.875em;
}

.prose pre code {
  background-color: transparent;
  color: inherit;
  padding: 0;
}

.prose h1, .prose h2, .prose h3, .prose h4 {
  font-weight: 600;
  margin-top: 1.5em;
  margin-bottom: 0.75em;
  color: #111827;
}

.prose ul, .prose ol {
  margin-top: 0.75em;
  margin-bottom: 0.75em;
  padding-left: 1.5em;
}

.prose li {
  margin-top: 0.25em;
  margin-bottom: 0.25em;
}

.prose blockquote {
  border-left: 4px solid #d1d5db;
  padding-left: 1em;
  color: #6b7280;
  font-style: italic;
  margin-top: 0.75em;
  margin-bottom: 0.75em;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
