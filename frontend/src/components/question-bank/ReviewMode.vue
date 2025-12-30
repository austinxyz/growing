<template>
  <div class="max-w-4xl mx-auto">
    <!-- AI参考答案（如果有） -->
    <div v-if="question.aiNote && question.aiNote.noteContent" class="mb-8">
      <div class="bg-gradient-to-br from-blue-50 to-white rounded-xl border-2 border-blue-200 shadow-md overflow-hidden">
        <div class="px-4 py-3 bg-gradient-to-r from-blue-500 to-cyan-600 text-white flex items-center justify-between">
          <div class="flex items-center gap-2">
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 015.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z" />
            </svg>
            <h3 class="font-semibold">🤖 AI 参考答案</h3>
          </div>
          <button
            @click="showAINote = !showAINote"
            class="p-1 hover:bg-blue-600 rounded transition-all"
          >
            <svg
              :class="['w-4 h-4 transition-transform', showAINote ? 'rotate-180' : '']"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </button>
        </div>
        <div v-show="showAINote" class="p-6">
          <div class="prose prose-sm max-w-none" v-html="renderMarkdown(question.aiNote.noteContent)"></div>

          <!-- AI核心策略 -->
          <div v-if="question.aiNote.coreStrategy" class="mt-6 p-4 bg-blue-50 border-l-4 border-blue-500 rounded">
            <h4 class="text-sm font-semibold text-blue-900 mb-2">💡 核心策略</h4>
            <p class="text-sm text-blue-800">{{ question.aiNote.coreStrategy }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 你之前的答案 -->
    <div v-if="question.userNote && question.userNote.noteContent" class="mb-8">
      <div class="bg-gradient-to-br from-green-50 to-white rounded-xl border-2 border-green-200 shadow-md overflow-hidden">
        <div class="px-4 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white flex items-center justify-between">
          <div class="flex items-center gap-2">
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
              <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd" />
            </svg>
            <h3 class="font-semibold">📝 你之前的答案</h3>
          </div>
          <button
            @click="showUserNote = !showUserNote"
            class="p-1 hover:bg-green-600 rounded transition-all"
          >
            <svg
              :class="['w-4 h-4 transition-transform', showUserNote ? 'rotate-180' : '']"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </button>
        </div>
        <div v-show="showUserNote" class="p-6">
          <div class="prose prose-sm max-w-none" v-html="renderMarkdown(question.userNote.noteContent)"></div>

          <!-- 用户核心策略 -->
          <div v-if="question.userNote.coreStrategy" class="mt-6 p-4 bg-green-50 border-l-4 border-green-500 rounded">
            <h4 class="text-sm font-semibold text-green-900 mb-2">💡 核心策略</h4>
            <p class="text-sm text-green-800">{{ question.userNote.coreStrategy }}</p>
          </div>

          <!-- 答案时间信息 -->
          <div class="mt-6 flex items-center gap-4 text-xs text-gray-500">
            <div class="flex items-center gap-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>更新于: {{ formatDate(question.userNote.updatedAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 当前新答案编辑区 -->
    <div class="mb-8">
      <div class="bg-gradient-to-br from-purple-50 to-white rounded-xl border-2 border-purple-200 shadow-md overflow-hidden">
        <div class="px-4 py-3 bg-gradient-to-r from-purple-500 to-purple-600 text-white flex items-center justify-between">
          <div class="flex items-center gap-2">
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
            </svg>
            <h3 class="font-semibold">✨ 当前答案（可编辑）</h3>
          </div>
        </div>
        <div class="p-6">
          <!-- 答案编辑器 -->
          <textarea
            v-model="currentAnswer"
            rows="15"
            placeholder="在此输入或修改你的答案...\n\n支持 Markdown 格式"
            class="w-full px-4 py-3 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none text-sm font-mono"
          ></textarea>

          <!-- 核心策略 -->
          <div class="mt-4">
            <label class="block text-sm font-medium text-gray-900 mb-2">
              💡 核心策略 / 关键要点
            </label>
            <textarea
              v-model="currentCoreStrategy"
              placeholder="总结这道题的核心策略或关键要点..."
              rows="3"
              class="w-full px-3 py-2 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none text-sm"
            ></textarea>
          </div>

          <!-- Markdown预览 -->
          <div v-if="currentAnswer && currentAnswer.trim()" class="mt-6">
            <div class="flex items-center justify-between mb-3">
              <h4 class="text-sm font-semibold text-gray-900">👁️ 答案预览</h4>
              <button
                @click="showPreview = !showPreview"
                class="text-sm text-purple-600 hover:text-purple-700"
              >
                {{ showPreview ? '隐藏预览' : '显示预览' }}
              </button>
            </div>
            <div v-show="showPreview" class="p-4 bg-gray-50 border border-gray-200 rounded-lg">
              <div class="prose prose-sm max-w-none" v-html="renderMarkdown(currentAnswer)"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 对比模式 -->
    <div v-if="question.aiNote && question.userNote && showComparison" class="mb-8">
      <div class="bg-white rounded-xl border-2 border-gray-200 shadow-md overflow-hidden">
        <div class="px-4 py-3 bg-gray-100 border-b border-gray-200">
          <h3 class="font-semibold text-gray-900 flex items-center gap-2">
            <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
            对比视图
          </h3>
        </div>
        <div class="grid grid-cols-2 divide-x divide-gray-200">
          <div class="p-6">
            <h4 class="text-sm font-semibold text-blue-600 mb-3">AI 参考答案</h4>
            <div class="prose prose-sm max-w-none text-xs" v-html="renderMarkdown(question.aiNote.noteContent)"></div>
          </div>
          <div class="p-6">
            <h4 class="text-sm font-semibold text-green-600 mb-3">你的答案</h4>
            <div class="prose prose-sm max-w-none text-xs" v-html="renderMarkdown(question.userNote.noteContent)"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="flex justify-between items-center">
      <button
        v-if="question.aiNote && question.userNote"
        @click="showComparison = !showComparison"
        class="text-sm text-gray-600 hover:text-gray-800 flex items-center gap-1"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
        {{ showComparison ? '隐藏对比' : '显示对比' }}
      </button>
      <div v-else></div>

      <div class="flex gap-3">
        <button
          v-if="question.userNote && question.userNote.noteContent"
          @click="confirmOverwrite"
          :disabled="!currentAnswer.trim()"
          class="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
          </svg>
          覆盖保存
        </button>
        <button
          v-else
          @click="confirmOverwrite"
          :disabled="!currentAnswer.trim()"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path d="M7.707 10.293a1 1 0 10-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 11.586V6h5a2 2 0 012 2v7a2 2 0 01-2 2H4a2 2 0 01-2-2V8a2 2 0 012-2h5v5.586l-1.293-1.293zM9 4a1 1 0 012 0v2H9V4z" />
          </svg>
          保存答案
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { marked } from 'marked'

const props = defineProps({
  question: {
    type: Object,
    required: true
  },
  currentAnswer: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['overwrite'])

// 显示控制
const showAINote = ref(true)
const showUserNote = ref(true)
const showPreview = ref(false)
const showComparison = ref(false)

// 当前编辑的答案
const currentAnswer = ref('')
const currentCoreStrategy = ref('')

// Markdown渲染
const renderMarkdown = (content) => {
  if (!content) return ''
  return marked(content)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 确认覆盖
const confirmOverwrite = () => {
  if (!currentAnswer.value.trim()) return

  if (props.question.userNote && props.question.userNote.noteContent) {
    if (confirm('确定要覆盖之前的答案吗？此操作不可撤销。')) {
      emit('overwrite', {
        content: currentAnswer.value,
        coreStrategy: currentCoreStrategy.value
      })
    }
  } else {
    emit('overwrite', {
      content: currentAnswer.value,
      coreStrategy: currentCoreStrategy.value
    })
  }
}

// 监听试题变化，加载答案
watch(() => props.question, (newQuestion) => {
  if (newQuestion && newQuestion.userNote) {
    currentAnswer.value = newQuestion.userNote.noteContent || ''
    currentCoreStrategy.value = newQuestion.userNote.coreStrategy || ''
  } else {
    currentAnswer.value = ''
    currentCoreStrategy.value = ''
  }
}, { immediate: true })

// 监听外部传入的答案
watch(() => props.currentAnswer, (newAnswer) => {
  if (newAnswer) {
    currentAnswer.value = newAnswer
  }
}, { immediate: true })
</script>
