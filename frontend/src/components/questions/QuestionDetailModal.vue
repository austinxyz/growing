<template>
  <div class="fixed inset-0 z-50 overflow-hidden">
    <!-- 背景遮罩（半透明，点击不关闭） -->
    <div class="fixed inset-0 bg-black bg-opacity-30 transition-opacity"></div>

    <!-- 可拖拽、可调整大小的弹窗 -->
    <div
      ref="modalRef"
      class="absolute bg-white rounded-lg shadow-2xl max-h-[85vh] overflow-hidden flex flex-col"
      :style="{
        left: modalPosition.x + 'px',
        top: modalPosition.y + 'px',
        width: modalWidth + 'px',
        cursor: isDragging ? 'grabbing' : 'default'
      }"
    >
      <!-- 可拖拽的头部 -->
      <div
        @mousedown="startDrag"
        class="bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between cursor-grab active:cursor-grabbing select-none flex-shrink-0"
      >
        <div class="flex items-center space-x-2">
          <svg class="w-4 h-4 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
            <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z" />
          </svg>
          <h2 class="text-base font-semibold text-gray-900">试题详情</h2>
          <DifficultyBadge :difficulty="question.difficulty" />
        </div>
        <button
          @click="$emit('close')"
          @mousedown.stop
          class="text-gray-400 hover:text-gray-600"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- 左右两栏布局 -->
      <div class="flex flex-1 overflow-hidden">
        <!-- Loading状态 -->
        <div v-if="loading" class="flex-1 flex items-center justify-center text-gray-500">
          <div class="text-center">
            <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-2 text-sm">加载中...</p>
          </div>
        </div>

        <template v-else-if="fullQuestion">
          <!-- 左栏：题目详情 -->
          <div class="w-1/2 overflow-y-auto border-r border-gray-200 p-4">
            <div class="space-y-4">
              <!-- 外部链接 -->
              <div v-if="fullQuestion.programmingDetails?.leetcodeUrl || fullQuestion.programmingDetails?.labuladongUrl || fullQuestion.programmingDetails?.hellointerviewUrl" class="flex flex-wrap gap-2">
              <a
                v-if="fullQuestion.programmingDetails.leetcodeUrl"
                :href="fullQuestion.programmingDetails.leetcodeUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="inline-flex items-center px-4 py-2 text-sm font-medium text-orange-700 bg-orange-100 rounded-lg hover:bg-orange-200 transition-colors"
              >
                <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M13.483 0a1.374 1.374 0 0 0-.961.438L7.116 6.226l-3.854 4.126a5.266 5.266 0 0 0-1.209 2.104 5.35 5.35 0 0 0-.125.513 5.527 5.527 0 0 0 .062 2.362 5.83 5.83 0 0 0 .349 1.017 5.938 5.938 0 0 0 1.271 1.818l4.277 4.193.039.038c2.248 2.165 5.852 2.133 8.063-.074l2.396-2.392c.54-.54.54-1.414.003-1.955a1.378 1.378 0 0 0-1.951-.003l-2.396 2.392a3.021 3.021 0 0 1-4.205.038l-.02-.019-4.276-4.193c-.652-.64-.972-1.469-.948-2.263a2.68 2.68 0 0 1 .066-.523 2.545 2.545 0 0 1 .619-1.164L9.13 8.114c1.058-1.134 3.204-1.27 4.43-.278l3.501 2.831c.593.48 1.461.387 1.94-.207a1.384 1.384 0 0 0-.207-1.943l-3.5-2.831c-.8-.647-1.766-1.045-2.774-1.202l2.015-2.158A1.384 1.384 0 0 0 13.483 0zm-2.866 12.815a1.38 1.38 0 0 0-1.38 1.382 1.38 1.38 0 0 0 1.38 1.382H20.79a1.38 1.38 0 0 0 1.38-1.382 1.38 1.38 0 0 0-1.38-1.382z"/>
                </svg>
                在 LeetCode 上练习
              </a>
              <a
                v-if="fullQuestion.programmingDetails.labuladongUrl"
                :href="fullQuestion.programmingDetails.labuladongUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="inline-flex items-center px-4 py-2 text-sm font-medium text-green-700 bg-green-100 rounded-lg hover:bg-green-200 transition-colors"
              >
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
                labuladong 算法教程
              </a>
              <a
                v-if="fullQuestion.programmingDetails.hellointerviewUrl"
                :href="fullQuestion.programmingDetails.hellointerviewUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="inline-flex items-center px-4 py-2 text-sm font-medium text-purple-700 bg-purple-100 rounded-lg hover:bg-purple-200 transition-colors"
              >
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                </svg>
                HelloInterview
              </a>
            </div>

            <!-- 标题 -->
            <div>
              <h3 class="text-sm font-medium text-gray-500 mb-1">题目标题</h3>
              <p class="text-lg font-semibold text-gray-900">{{ fullQuestion.title }}</p>
            </div>

            <!-- 问题描述 -->
            <div>
              <h3 class="text-sm font-medium text-gray-500 mb-2">问题描述</h3>
              <div class="prose prose-sm max-w-none bg-gray-50 rounded-lg p-4" v-html="renderMarkdown(fullQuestion.questionDescription)"></div>
            </div>

            <!-- 答案要求 -->
            <div v-if="fullQuestion.answerRequirement" class="border-t border-gray-200 pt-6">
              <h3 class="text-sm font-medium text-gray-700 mb-2">答案要求</h3>
              <div class="prose prose-sm max-w-none bg-blue-50 rounded-lg p-4" v-html="renderMarkdown(fullQuestion.answerRequirement)"></div>
            </div>

            <!-- 编程题详情 -->
            <div v-if="fullQuestion.programmingDetails" class="border-t border-gray-200 pt-6 space-y-4">
              <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-gray-900">编程题详情</h3>
                <span v-if="fullQuestion.programmingDetails.isImportant" class="px-3 py-1 text-xs font-medium bg-red-100 text-red-700 rounded-full">
                  ⭐ 重要必做
                </span>
              </div>

              <!-- 标签 -->
              <div v-if="fullQuestion.programmingDetails.tags && fullQuestion.programmingDetails.tags.length > 0">
                <h4 class="text-sm font-medium text-gray-700 mb-2">标签</h4>
                <div class="flex flex-wrap gap-2">
                  <span
                    v-for="(tag, index) in fullQuestion.programmingDetails.tags"
                    :key="index"
                    class="px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded-full"
                  >
                    {{ tag }}
                  </span>
                </div>
              </div>

              <!-- 复杂度 -->
              <div v-if="fullQuestion.programmingDetails.complexity">
                <h4 class="text-sm font-medium text-gray-700 mb-2">复杂度</h4>
                <p class="text-sm text-gray-600 bg-gray-50 rounded px-3 py-2">{{ fullQuestion.programmingDetails.complexity }}</p>
              </div>

              <!-- 相似题目 -->
              <div v-if="fullQuestion.programmingDetails.similarQuestions && fullQuestion.programmingDetails.similarQuestions.length > 0">
                <h4 class="text-sm font-medium text-gray-700 mb-2">相似题目</h4>
                <div class="space-y-2">
                  <div
                    v-for="(sq, index) in fullQuestion.programmingDetails.similarQuestions"
                    :key="index"
                    class="flex items-center text-sm text-gray-600"
                  >
                    <span class="mr-2">•</span>
                    <span>{{ sq.title || `题目 ${sq.id}` }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右栏：笔记 -->
        <div class="w-1/2 overflow-y-auto p-4 bg-gray-50">
          <div class="space-y-4">
            <!-- 核心思路（用户笔记） -->
            <div>
                <div class="flex items-center justify-between mb-3">
                  <h3 class="text-lg font-semibold text-gray-900">核心思路</h3>
                  <button
                    v-if="!isEditingStrategy"
                    @click="startEditStrategy"
                    class="text-sm text-blue-600 hover:text-blue-700"
                  >
                    {{ userNote?.coreStrategy ? '编辑' : '添加' }}
                  </button>
                </div>

                <!-- 显示模式 -->
                <div v-if="!isEditingStrategy && userNote?.coreStrategy" class="prose prose-sm max-w-none bg-yellow-50 rounded-lg p-4" v-html="renderMarkdown(userNote.coreStrategy)"></div>
                <div v-else-if="!isEditingStrategy" class="text-center text-gray-400 py-8 bg-gray-50 rounded-lg">
                  暂无核心思路，点击上方"添加"按钮记录你的解题思路
                </div>

                <!-- 编辑模式 -->
                <div v-else>
                  <textarea
                    v-model="editForm.coreStrategy"
                    rows="6"
                    placeholder="记录解题的核心思路，如使用的算法、数据结构、关键步骤等（支持Markdown）"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
                  ></textarea>
                  <div class="flex justify-end space-x-2 mt-2">
                    <button
                      @click="cancelEditStrategy"
                      class="px-4 py-2 text-sm text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                    >
                      取消
                    </button>
                    <button
                      @click="saveStrategy"
                      :disabled="saving"
                      class="px-4 py-2 text-sm text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:bg-blue-300"
                    >
                      {{ saving ? '保存中...' : '保存' }}
                    </button>
                  </div>
                </div>
              </div>

              <!-- 个人笔记 -->
              <div class="border-t border-gray-200 pt-4">
                <div class="flex items-center justify-between mb-3">
                  <h3 class="text-lg font-semibold text-gray-900">个人笔记</h3>
                  <button
                    v-if="!isEditingNote"
                    @click="startEditNote"
                    class="text-sm text-blue-600 hover:text-blue-700"
                  >
                    {{ userNote?.noteContent ? '编辑' : '添加' }}
                  </button>
                </div>

                <!-- 显示模式 -->
                <div v-if="!isEditingNote && userNote?.noteContent" class="prose prose-sm max-w-none bg-green-50 rounded-lg p-4" v-html="renderMarkdown(userNote.noteContent)"></div>
                <div v-else-if="!isEditingNote" class="text-center text-gray-400 py-8 bg-gray-50 rounded-lg">
                  暂无个人笔记，点击上方"添加"按钮记录你的心得体会
                </div>

                <!-- 编辑模式 -->
                <div v-else>
                  <textarea
                    v-model="editForm.noteContent"
                    rows="6"
                    placeholder="记录个人心得、注意事项、易错点等（支持Markdown）"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
                  ></textarea>
                  <div class="flex justify-end space-x-2 mt-2">
                    <button
                      @click="cancelEditNote"
                      class="px-4 py-2 text-sm text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                    >
                      取消
                    </button>
                    <button
                      @click="saveNote"
                      :disabled="saving"
                      class="px-4 py-2 text-sm text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:bg-blue-300"
                    >
                      {{ saving ? '保存中...' : '保存' }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 底部操作按钮 -->
      <div class="border-t border-gray-200 px-4 py-3 flex justify-end bg-white">
        <button
          @click="$emit('close')"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
        >
          关闭
        </button>
      </div>

      <!-- 右边缘拖拽手柄（用于调整宽度） -->
      <div
        @mousedown="startResize"
        class="absolute top-0 right-0 w-1 h-full cursor-ew-resize hover:bg-blue-500 transition-colors"
        :class="{ 'bg-blue-500': isResizing }"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { questionApi } from '@/api/questionApi'
import DifficultyBadge from './DifficultyBadge.vue'

const props = defineProps({
  question: {
    type: Object,
    required: true
  },
  focusAreaId: {
    type: Number,
    required: false
  },
  focusAreaName: {
    type: String,
    required: false
  }
})

const emit = defineEmits(['close', 'note-saved'])

// State
const loading = ref(false)
const saving = ref(false)
const fullQuestion = ref(null)
const userNote = ref(null)
const isEditingStrategy = ref(false)
const isEditingNote = ref(false)
const editForm = ref({
  coreStrategy: '',
  noteContent: ''
})

// 拖拽相关状态
const modalRef = ref(null)
const isDragging = ref(false)
const modalPosition = ref({ x: 0, y: 0 })
const dragStart = ref({ x: 0, y: 0 })

// 调整大小相关状态
const isResizing = ref(false)
const modalWidth = ref(1200) // 初始宽度1200px
const resizeStartX = ref(0)
const resizeStartWidth = ref(0)

// 初始化弹窗位置（屏幕居中）
const initModalPosition = () => {
  const windowWidth = window.innerWidth
  const windowHeight = window.innerHeight
  const modalHeight = windowHeight * 0.85

  // 将弹窗放在屏幕中央
  modalPosition.value = {
    x: (windowWidth - modalWidth.value) / 2,
    y: (windowHeight - modalHeight) / 2
  }
}

// 开始拖拽
const startDrag = (e) => {
  isDragging.value = true
  dragStart.value = {
    x: e.clientX - modalPosition.value.x,
    y: e.clientY - modalPosition.value.y
  }

  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}

// 拖拽中
const onDrag = (e) => {
  if (!isDragging.value) return

  modalPosition.value = {
    x: e.clientX - dragStart.value.x,
    y: e.clientY - dragStart.value.y
  }
}

// 停止拖拽
const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

// 开始调整大小
const startResize = (e) => {
  e.stopPropagation() // 防止触发拖拽
  isResizing.value = true
  resizeStartX.value = e.clientX
  resizeStartWidth.value = modalWidth.value

  document.addEventListener('mousemove', onResize)
  document.addEventListener('mouseup', stopResize)
}

// 调整大小中
const onResize = (e) => {
  if (!isResizing.value) return

  const deltaX = e.clientX - resizeStartX.value
  const newWidth = resizeStartWidth.value + deltaX

  // 限制最小宽度800px，最大宽度为屏幕宽度的90%
  const minWidth = 800
  const maxWidth = window.innerWidth * 0.9

  modalWidth.value = Math.max(minWidth, Math.min(newWidth, maxWidth))
}

// 停止调整大小
const stopResize = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', onResize)
  document.removeEventListener('mouseup', stopResize)
}

// Methods
const renderMarkdown = (text) => {
  if (!text) return ''
  try {
    const html = marked(text, { breaks: true, gfm: true })
    return DOMPurify.sanitize(html)
  } catch (error) {
    console.error('Markdown rendering error:', error)
    return text
  }
}

const loadQuestionDetails = async () => {
  loading.value = true
  try {
    const data = await questionApi.getQuestionById(props.question.id)
    fullQuestion.value = data
  } catch (error) {
    console.error('加载试题详情失败:', error)
    alert('加载试题详情失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const loadUserNote = async () => {
  try {
    const data = await questionApi.getUserNote(props.question.id)
    userNote.value = data
  } catch (error) {
    // 404表示用户还没有笔记，这是正常的
    if (error.response?.status !== 404) {
      console.error('加载用户笔记失败:', error)
    }
    userNote.value = null
  }
}

const startEditStrategy = () => {
  editForm.value.coreStrategy = userNote.value?.coreStrategy || ''
  isEditingStrategy.value = true
}

const cancelEditStrategy = () => {
  isEditingStrategy.value = false
  editForm.value.coreStrategy = ''
}

const saveStrategy = async () => {
  saving.value = true
  try {
    const data = {
      coreStrategy: editForm.value.coreStrategy,
      noteContent: userNote.value?.noteContent || ''
    }
    await questionApi.saveOrUpdateNote(props.question.id, data)
    await loadUserNote()
    isEditingStrategy.value = false
    emit('note-saved')
  } catch (error) {
    console.error('保存核心思路失败:', error)
    alert('保存核心思路失败: ' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

const startEditNote = () => {
  editForm.value.noteContent = userNote.value?.noteContent || ''
  isEditingNote.value = true
}

const cancelEditNote = () => {
  isEditingNote.value = false
  editForm.value.noteContent = ''
}

const saveNote = async () => {
  saving.value = true
  try {
    const data = {
      coreStrategy: userNote.value?.coreStrategy || '',
      noteContent: editForm.value.noteContent
    }
    await questionApi.saveOrUpdateNote(props.question.id, data)
    await loadUserNote()
    isEditingNote.value = false
    emit('note-saved')
  } catch (error) {
    console.error('保存个人笔记失败:', error)
    alert('保存个人笔记失败: ' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

// Initialize
onMounted(async () => {
  initModalPosition()
  await loadQuestionDetails()
  await loadUserNote()
})

// Cleanup
onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('mousemove', onResize)
  document.removeEventListener('mouseup', stopResize)
})
</script>

<style scoped>
/* Prose 样式 */
:deep(.prose) {
  color: #374151;
  line-height: 1.75;
}

:deep(.prose p) {
  margin-top: 0.75em;
  margin-bottom: 0.75em;
}

:deep(.prose h1),
:deep(.prose h2),
:deep(.prose h3) {
  margin-top: 1.25em;
  margin-bottom: 0.625em;
  font-weight: 600;
}

:deep(.prose ul),
:deep(.prose ol) {
  margin-top: 0.75em;
  margin-bottom: 0.75em;
  padding-left: 1.5em;
}

:deep(.prose code) {
  background-color: #f3f4f6;
  padding: 0.2em 0.4em;
  border-radius: 0.25em;
  font-size: 0.875em;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

:deep(.prose pre) {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 1em;
  border-radius: 0.375em;
  overflow-x: auto;
  margin-top: 1em;
  margin-bottom: 1em;
}

:deep(.prose pre code) {
  background-color: transparent;
  padding: 0;
  color: inherit;
  font-size: 0.875em;
}
</style>
