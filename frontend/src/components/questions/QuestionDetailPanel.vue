<template>
  <div class="h-full flex flex-col bg-white">
    <!-- Loading状态 -->
    <div v-if="loading" class="text-center text-gray-500 py-12">
      <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
      <p class="mt-2 text-sm">加载中...</p>
    </div>

    <!-- 内容：上下两栏布局 -->
    <div v-else-if="fullQuestion" class="flex-1 flex flex-col overflow-hidden">
      <!-- 上半部分：题目详情（可滚动） -->
      <div class="flex-1 overflow-y-auto border-b border-gray-300 p-3">
        <!-- 超紧凑头部：一行显示所有元信息 -->
        <div class="mb-2 pb-1.5 border-b border-gray-200">
          <!-- 第一行：标题 + 难度 -->
          <div class="flex items-center gap-2 mb-1">
            <h2 class="text-sm font-semibold text-gray-900">{{ fullQuestion.title }}</h2>
            <DifficultyBadge :difficulty="fullQuestion.difficulty" />
            <span v-if="fullQuestion.programmingDetails?.isImportant" class="px-1.5 py-0.5 text-xs font-medium bg-red-100 text-red-700 rounded">
              ⭐
            </span>
          </div>

          <!-- 第二行：外部链接 + 标签 + 复杂度 (单行显示) -->
          <div class="flex items-center gap-1.5 text-xs overflow-x-auto whitespace-nowrap">
            <!-- 外部链接 -->
            <a
              v-if="fullQuestion.programmingDetails?.leetcodeUrl"
              :href="fullQuestion.programmingDetails.leetcodeUrl"
              target="_blank"
              rel="noopener noreferrer"
              class="inline-flex items-center px-1.5 py-0.5 font-medium text-orange-700 bg-orange-100 rounded hover:bg-orange-200 transition-colors flex-shrink-0"
            >
              LeetCode
            </a>
            <a
              v-if="fullQuestion.programmingDetails?.labuladongUrl"
              :href="fullQuestion.programmingDetails.labuladongUrl"
              target="_blank"
              rel="noopener noreferrer"
              class="inline-flex items-center px-1.5 py-0.5 font-medium text-green-700 bg-green-100 rounded hover:bg-green-200 transition-colors flex-shrink-0"
            >
              算法教程
            </a>
            <a
              v-if="fullQuestion.programmingDetails?.hellointerviewUrl"
              :href="fullQuestion.programmingDetails.hellointerviewUrl"
              target="_blank"
              rel="noopener noreferrer"
              class="inline-flex items-center px-1.5 py-0.5 font-medium text-purple-700 bg-purple-100 rounded hover:bg-purple-200 transition-colors flex-shrink-0"
            >
              面试题
            </a>

            <!-- 分隔符 + 标签 -->
            <template v-if="fullQuestion.programmingDetails?.tags?.length > 0">
              <span class="text-gray-300 flex-shrink-0">|</span>
              <span
                v-for="(tag, index) in fullQuestion.programmingDetails.tags"
                :key="index"
                class="px-1.5 py-0.5 bg-blue-100 text-blue-700 rounded flex-shrink-0"
              >
                {{ tag }}
              </span>
            </template>

            <!-- 复杂度 -->
            <template v-if="fullQuestion.programmingDetails?.complexity">
              <span class="text-gray-300 flex-shrink-0">|</span>
              <span class="text-gray-600 flex-shrink-0">{{ fullQuestion.programmingDetails.complexity }}</span>
            </template>
          </div>
        </div>

        <!-- 问题描述（无标题） -->
        <div class="prose-compact max-w-none bg-gray-50 rounded p-2 mb-2" v-html="renderMarkdown(fullQuestion.questionDescription)"></div>

        <!-- 答案要求（无标题） -->
        <div v-if="fullQuestion.answerRequirement" class="prose-compact max-w-none bg-blue-50 rounded p-2 mb-2" v-html="renderMarkdown(fullQuestion.answerRequirement)"></div>

        <!-- 相似题目（如果有，单行显示） -->
        <div v-if="fullQuestion.programmingDetails?.similarQuestions?.length > 0" class="text-xs text-gray-600 pt-1.5 border-t border-gray-200 truncate">
          <span class="font-medium">相似：</span><span v-for="(sq, index) in fullQuestion.programmingDetails.similarQuestions" :key="index">{{ sq.title || `题目 ${sq.id}` }}<span v-if="index < fullQuestion.programmingDetails.similarQuestions.length - 1">, </span></span>
        </div>
      </div>

      <!-- 下半部分：核心思路（可滚动） -->
      <div class="h-48 overflow-y-auto bg-white p-3 border-t border-gray-200">
        <div class="flex items-center justify-between mb-1.5">
          <h3 class="text-sm font-semibold text-gray-900">核心思路</h3>
          <div class="flex items-center gap-2">
            <button
              @click="$emit('open-modal')"
              class="text-xs text-gray-600 hover:text-gray-800"
              title="查看完整笔记"
            >
              📝 完整笔记
            </button>
            <button
              v-if="!isEditingStrategy"
              @click="startEditStrategy"
              class="text-xs text-blue-600 hover:text-blue-700"
            >
              {{ userNote?.coreStrategy ? '编辑' : '添加' }}
            </button>
          </div>
        </div>

        <!-- 显示模式 -->
        <div v-if="!isEditingStrategy && userNote?.coreStrategy" class="prose-compact max-w-none bg-yellow-50 rounded p-2" v-html="renderMarkdown(userNote.coreStrategy)"></div>
        <div v-else-if="!isEditingStrategy" class="text-center text-gray-400 py-4 bg-gray-50 rounded text-xs">
          点击上方"添加"记录解题思路
        </div>

        <!-- 编辑模式 -->
        <div v-else>
          <textarea
            v-model="editForm.coreStrategy"
            rows="6"
            placeholder="记录解题核心思路（支持Markdown）"
            class="w-full px-2 py-1.5 border border-gray-300 rounded text-xs focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono"
          ></textarea>
          <div class="flex justify-end space-x-1.5 mt-1.5">
            <button
              @click="cancelEditStrategy"
              class="px-3 py-1 text-xs text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-50"
            >
              取消
            </button>
            <button
              @click="saveStrategy"
              :disabled="saving"
              class="px-3 py-1 text-xs text-white bg-blue-600 rounded hover:bg-blue-700 disabled:bg-blue-300"
            >
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
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

const emit = defineEmits(['note-saved', 'open-modal'])

// State
const loading = ref(false)
const saving = ref(false)
const fullQuestion = ref(null)
const userNote = ref(null)
const isEditingStrategy = ref(false)
const editForm = ref({
  coreStrategy: ''
})

// Methods
const renderMarkdown = (text) => {
  if (!text) return ''
  try {
    // 使用marked渲染，启用breaks选项将换行符转换为<br>
    const html = marked(text, {
      breaks: true,  // 单个换行符转换为<br>
      gfm: true      // GitHub Flavored Markdown
    })
    return DOMPurify.sanitize(html)
  } catch (error) {
    console.error('Markdown rendering error:', error)
    // 如果markdown渲染失败，将换行符转换为<br>标签
    return text.replace(/\n/g, '<br>')
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

// Watch for question changes
watch(() => props.question, async () => {
  if (props.question) {
    await loadQuestionDetails()
    await loadUserNote()
  }
})

// Initialize
onMounted(async () => {
  await loadQuestionDetails()
  await loadUserNote()
})
</script>

<style scoped>
/* 紧凑Prose样式 */
:deep(.prose-compact) {
  color: #374151;
  line-height: 1.5;
  font-size: 0.8125rem; /* 13px */
}

:deep(.prose-compact p) {
  margin-top: 0.4em;
  margin-bottom: 0.4em;
}

:deep(.prose-compact h1),
:deep(.prose-compact h2),
:deep(.prose-compact h3) {
  margin-top: 0.75em;
  margin-bottom: 0.4em;
  font-weight: 600;
  font-size: 0.875rem; /* 14px */
}

:deep(.prose-compact ul),
:deep(.prose-compact ol) {
  margin-top: 0.4em;
  margin-bottom: 0.4em;
  padding-left: 1.25em;
}

:deep(.prose-compact li) {
  margin-top: 0.2em;
  margin-bottom: 0.2em;
}

:deep(.prose-compact code) {
  background-color: #f3f4f6;
  padding: 0.15em 0.3em;
  border-radius: 0.2em;
  font-size: 0.75rem; /* 12px */
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

:deep(.prose-compact pre) {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 0.75em;
  border-radius: 0.3em;
  overflow-x: auto;
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  font-size: 0.75rem; /* 12px */
}

:deep(.prose-compact pre code) {
  background-color: transparent;
  padding: 0;
  color: inherit;
  font-size: inherit;
}

:deep(.prose-compact strong) {
  font-weight: 600;
}

:deep(.prose-compact em) {
  font-style: italic;
}

:deep(.prose-compact blockquote) {
  border-left: 3px solid #e5e7eb;
  padding-left: 0.75em;
  margin: 0.5em 0;
  color: #6b7280;
  font-style: italic;
}
</style>
