<template>
  <div class="h-screen flex flex-col bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 顶部标题栏 - 添加渐变背景 -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg">
      <div class="px-6 py-4">
        <h1 class="text-2xl font-bold text-white">算法模版库</h1>
        <p class="text-sm text-blue-100 mt-1">浏览和学习常用算法模版代码</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：模版列表 -->
      <div class="w-96 bg-white border-r border-gray-200 flex flex-col shadow-lg">
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
                'p-3 m-2 rounded-lg cursor-pointer transition-all duration-200 border',
                selectedTemplate?.id === template.id
                  ? 'bg-gradient-to-r from-blue-50 to-purple-50 border-blue-500 shadow-md'
                  : 'bg-white border-gray-200 hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-sm hover:border-blue-300'
              ]"
            >
              <h3 :class="[
                'text-sm font-semibold mb-1',
                selectedTemplate?.id === template.id
                  ? 'text-blue-700'
                  : 'text-gray-900'
              ]">{{ template.title }}</h3>
              <p class="text-xs text-gray-500 line-clamp-2">{{ template.description || '无描述' }}</p>
            </div>
          </div>

          <!-- 空状态 - 增强样式 -->
          <div v-else class="flex flex-col items-center justify-center py-12 px-4 m-2 bg-gradient-to-br from-gray-50 to-white rounded-xl border-2 border-gray-200 shadow-md">
            <svg class="h-16 w-16 text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <h3 class="text-base font-bold text-transparent bg-clip-text bg-gradient-to-r from-gray-600 to-gray-800">暂无算法模版</h3>
            <p class="mt-2 text-sm text-gray-500 text-center">{{ searchQuery ? '未找到匹配的模版' : '系统中还没有模版' }}</p>
          </div>
        </div>
      </div>

      <!-- 右侧：详情查看面板 -->
      <div class="flex-1 overflow-y-auto">
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
        <div v-else class="p-6 h-full flex flex-col">
          <!-- 标题栏：算法名 + 复杂度等信息（一行显示） -->
          <div class="bg-gradient-to-r from-indigo-600 to-purple-600 rounded-xl shadow-lg p-4 mb-4 flex-shrink-0">
            <div class="flex items-center justify-between flex-wrap gap-3">
              <!-- 左侧：算法名 -->
              <h2 class="text-2xl font-bold text-white">{{ selectedTemplate.title }}</h2>

              <!-- 右侧：语言 + 复杂度信息 -->
              <div class="flex items-center gap-2 flex-wrap">
                <span v-if="selectedTemplate.contentData?.language" class="inline-flex items-center px-3 py-1 text-sm font-semibold rounded-full bg-white/90 text-indigo-700 shadow-md">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
                  </svg>
                  {{ selectedTemplate.contentData.language }}
                </span>
                <span v-if="selectedTemplate.contentData?.complexity?.time" class="inline-flex items-center px-3 py-1 text-sm font-mono font-semibold rounded-full bg-gradient-to-r from-amber-400 to-orange-500 text-white shadow-md">
                  ⏱️ {{ selectedTemplate.contentData.complexity.time }}
                </span>
                <span v-if="selectedTemplate.contentData?.complexity?.space" class="inline-flex items-center px-3 py-1 text-sm font-mono font-semibold rounded-full bg-gradient-to-r from-emerald-400 to-green-500 text-white shadow-md">
                  💾 {{ selectedTemplate.contentData.complexity.space }}
                </span>
              </div>
            </div>
          </div>

          <!-- 两列卡片布局 -->
          <div class="flex-1 flex gap-4 overflow-hidden">
            <!-- 左列：模版代码 -->
            <div class="w-1/2 flex flex-col">
              <div class="bg-white rounded-xl shadow-lg border-2 border-gray-200 flex flex-col h-full overflow-hidden">
                <div class="p-4 border-b border-gray-200 flex-shrink-0">
                  <h3 class="text-lg font-semibold text-gray-800 flex items-center">
                    <svg class="w-5 h-5 mr-2 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
                    </svg>
                    模版代码
                  </h3>
                </div>
                <div class="flex-1 overflow-y-auto p-4">
                  <div v-if="selectedTemplate.contentData?.code" class="bg-gradient-to-br from-gray-900 to-gray-800 rounded-lg p-4 overflow-x-auto">
                    <pre class="text-sm text-gray-100 font-mono whitespace-pre-wrap">{{ selectedTemplate.contentData.code }}</pre>
                  </div>
                  <div v-else class="text-gray-400 text-center py-12 bg-gray-50 rounded-lg">
                    暂无代码
                  </div>
                </div>
              </div>
            </div>

            <!-- 右列：关键要点 + 我的笔记 -->
            <div class="w-1/2 flex flex-col gap-4 overflow-hidden">
              <!-- 关键要点卡片 -->
              <div class="flex-1 bg-white rounded-xl shadow-lg border-2 border-green-200 flex flex-col overflow-hidden">
                <div class="p-4 bg-gradient-to-r from-green-50 to-emerald-50 border-b border-green-200 flex-shrink-0">
                  <h3 class="text-lg font-semibold text-green-700 flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    关键要点
                  </h3>
                </div>
                <div class="flex-1 overflow-y-auto p-4">
                  <ul v-if="selectedTemplate.contentData?.keyPoints?.length" class="space-y-2">
                    <li v-for="(point, index) in selectedTemplate.contentData.keyPoints" :key="index" class="flex items-start bg-green-50/50 rounded-lg p-3">
                      <svg class="w-5 h-5 mr-2 mt-0.5 text-green-600 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                      </svg>
                      <span class="text-sm text-gray-700">{{ point }}</span>
                    </li>
                  </ul>
                  <div v-else class="text-gray-400 text-center py-12 text-sm">
                    暂无关键要点
                  </div>
                </div>
              </div>

              <!-- 我的笔记卡片 -->
              <div class="flex-1 bg-white rounded-xl shadow-lg border-2 border-amber-300 flex flex-col overflow-hidden">
                <div class="p-4 bg-gradient-to-r from-amber-50 to-orange-50 border-b border-amber-200 flex-shrink-0">
                  <div class="flex items-center justify-between">
                    <h3 class="text-lg font-semibold text-amber-700 flex items-center">
                      <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                      我的笔记
                    </h3>
                    <div v-if="currentNote?.note" class="text-xs text-amber-600 font-medium">
                      {{ formatDate(currentNote.note.updatedAt) }}
                    </div>
                  </div>
                </div>
                <div class="flex-1 overflow-y-auto p-4">
                  <!-- 查看模式 -->
                  <div v-if="!isEditingNote" class="h-full flex flex-col">
                    <div
                      v-if="currentNote?.note?.noteContent"
                      v-html="renderMarkdown(currentNote.note.noteContent)"
                      class="prose prose-sm max-w-none text-gray-700 flex-1"
                    ></div>
                    <div v-else class="text-gray-400 text-center py-12 text-sm italic flex-1 flex items-center justify-center">
                      点击下方按钮添加笔记
                    </div>

                    <div class="mt-4 flex gap-2 flex-shrink-0">
                      <button
                        @click="startEditingNote"
                        class="px-3 py-1.5 text-sm font-medium bg-gradient-to-r from-amber-500 to-orange-500 text-white rounded-lg hover:from-amber-600 hover:to-orange-600 hover:shadow-md transition-all"
                      >
                        {{ currentNote?.note ? '✏️ 编辑' : '➕ 添加' }}
                      </button>
                      <button
                        v-if="currentNote?.note"
                        @click="handleDeleteNote"
                        class="px-3 py-1.5 text-sm font-medium bg-gradient-to-r from-red-500 to-pink-500 text-white rounded-lg hover:from-red-600 hover:to-pink-600 hover:shadow-md transition-all"
                      >
                        🗑️ 删除
                      </button>
                    </div>
                  </div>

                  <!-- 编辑模式 -->
                  <div v-else class="h-full flex flex-col">
                    <textarea
                      v-model="editContent"
                      placeholder="在此输入你的学习笔记（支持Markdown格式）..."
                      class="flex-1 px-3 py-2 text-sm border border-amber-300 rounded-md focus:outline-none focus:ring-2 focus:ring-amber-500 resize-none font-mono"
                    ></textarea>

                    <div class="mt-3 flex gap-2 flex-shrink-0">
                      <button
                        @click="handleSaveNote"
                        :disabled="savingNote"
                        class="px-3 py-1.5 text-sm font-medium text-white bg-gradient-to-r from-green-500 to-emerald-500 rounded-lg hover:from-green-600 hover:to-emerald-600 hover:shadow-md disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                      >
                        {{ savingNote ? '💾 保存中...' : '💾 保存' }}
                      </button>
                      <button
                        @click="cancelEditingNote"
                        :disabled="savingNote"
                        class="px-3 py-1.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 hover:shadow-md disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                      >
                        ✖️ 取消
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 关联试题卡片 -->
              <div class="flex-1 bg-white rounded-xl shadow-lg border-2 border-purple-200 flex flex-col overflow-hidden">
                <div class="p-4 bg-gradient-to-r from-purple-50 to-pink-50 border-b border-purple-200 flex-shrink-0">
                  <h3 class="text-lg font-semibold text-purple-700 flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                    相关试题
                  </h3>
                </div>
                <div class="flex-1 overflow-y-auto p-4">
                  <div v-if="loadingRelatedQuestions" class="text-center py-8">
                    <svg class="animate-spin h-6 w-6 text-purple-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <p class="mt-2 text-sm text-gray-500">加载中...</p>
                  </div>
                  <div v-else-if="relatedQuestions.length === 0" class="text-gray-400 text-center py-12 text-sm">
                    暂无关联试题
                  </div>
                  <div v-else class="space-y-2">
                    <div
                      v-for="question in relatedQuestions"
                      :key="question.id"
                      class="bg-purple-50/50 rounded-lg p-3 hover:bg-purple-100/70 transition-colors cursor-pointer"
                      @click="viewQuestion(question.questionId)"
                    >
                      <div class="flex items-start justify-between">
                        <div class="flex-1 min-w-0">
                          <div class="flex items-center space-x-2">
                            <span v-if="question.leetcodeNumber" class="text-xs font-bold text-purple-600">
                              #{{ question.leetcodeNumber }}
                            </span>
                            <h4 class="text-sm font-medium text-gray-900 truncate">
                              {{ question.questionTitle }}
                            </h4>
                          </div>
                          <div class="flex items-center space-x-2 mt-1">
                            <span
                              v-if="question.difficulty"
                              :class="[
                                'px-2 py-0.5 text-xs font-semibold rounded',
                                question.difficulty === 'EASY' ? 'bg-green-100 text-green-800' :
                                question.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' :
                                'bg-red-100 text-red-800'
                              ]"
                            >
                              {{ question.difficulty === 'EASY' ? '简单' : question.difficulty === 'MEDIUM' ? '中等' : '困难' }}
                            </span>
                          </div>
                        </div>
                        <svg class="w-4 h-4 text-purple-400 flex-shrink-0 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                        </svg>
                      </div>
                    </div>
                  </div>
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
import { useRouter } from 'vue-router'
import { marked } from 'marked'
import {
  getAlgorithmTemplates,
  getTemplateNote,
  saveOrUpdateTemplateNote,
  deleteTemplateNote
} from '@/api/learningContentApi'
import templateQuestionApi from '@/api/templateQuestionApi'

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

// 关联试题相关状态
const loadingRelatedQuestions = ref(false)
const relatedQuestions = ref([])

// Router
const router = useRouter()
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
  await loadRelatedQuestions(template.id)
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
  try {
    // 使用marked渲染，启用breaks选项将换行符转换为<br>
    return marked(text, {
      breaks: true,  // 单个换行符转换为<br>
      gfm: true      // GitHub Flavored Markdown
    })
  } catch (error) {
    console.error('Markdown rendering error:', error)
    // 如果markdown渲染失败，将换行符转换为<br>标签
    return text.replace(/\n/g, '<br>')
  }
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

// 加载关联试题
const loadRelatedQuestions = async (templateId) => {
  if (!templateId) {
    relatedQuestions.value = []
    return
  }

  try {
    loadingRelatedQuestions.value = true
    const data = await templateQuestionApi.getQuestionsByTemplate(templateId)
    relatedQuestions.value = data || []
  } catch (error) {
    console.error('加载关联试题失败:', error)
    relatedQuestions.value = []
  } finally {
    loadingRelatedQuestions.value = false
  }
}

// 查看试题详情
const viewQuestion = (questionId) => {
  // 跳转到试题库页面并选中该题目
  router.push({
    name: 'QuestionBank',
    query: { questionId }
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
