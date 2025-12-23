<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- 背景遮罩 -->
    <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="$emit('close')"></div>

    <!-- 弹窗内容 -->
    <div class="flex min-h-screen items-center justify-center p-4">
      <div class="relative bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <!-- 头部 -->
        <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <h2 class="text-xl font-semibold text-gray-900">试题详情</h2>
            <DifficultyBadge :difficulty="question.difficulty" />
            <span v-if="question.isOfficial" class="text-xs px-2 py-1 bg-blue-100 text-blue-600 rounded-full">
              公共试题
            </span>
            <span v-else class="text-xs px-2 py-1 bg-purple-100 text-purple-600 rounded-full">
              我的试题
            </span>
          </div>
          <button
            @click="$emit('close')"
            class="text-gray-400 hover:text-gray-600"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 内容 -->
        <div class="p-6 space-y-6">
          <!-- 外部链接 -->
          <div v-if="question.programmingDetails?.leetcodeUrl || question.programmingDetails?.labuladongUrl || question.programmingDetails?.hellointerviewUrl" class="flex flex-wrap gap-3">
            <a
              v-if="question.programmingDetails?.leetcodeUrl"
              :href="question.programmingDetails.leetcodeUrl"
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
              v-if="question.programmingDetails?.labuladongUrl"
              :href="question.programmingDetails.labuladongUrl"
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
              v-if="question.programmingDetails?.hellointerviewUrl"
              :href="question.programmingDetails.hellointerviewUrl"
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
            <p class="text-lg font-semibold text-gray-900">{{ question.title }}</p>
          </div>

          <!-- 问题描述 -->
          <div>
            <h3 class="text-sm font-medium text-gray-500 mb-2">问题描述</h3>
            <div class="prose prose-sm max-w-none bg-gray-50 rounded-lg p-4" v-html="renderMarkdown(question.questionText)"></div>
          </div>

          <!-- 编程题详情 -->
          <div v-if="question.programmingDetails" class="border-t border-gray-200 pt-6 space-y-4">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-900">编程题详情</h3>
              <span v-if="question.programmingDetails.isImportant" class="px-3 py-1 text-xs font-medium bg-red-100 text-red-700 rounded-full">
                ⭐ 重要必做
              </span>
            </div>

            <!-- 标签 -->
            <div v-if="question.programmingDetails.tags && question.programmingDetails.tags.length > 0">
              <h4 class="text-sm font-medium text-gray-700 mb-2">标签</h4>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="(tag, index) in question.programmingDetails.tags"
                  :key="index"
                  class="px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded-full"
                >
                  {{ tag }}
                </span>
              </div>
            </div>

            <!-- 复杂度 -->
            <div v-if="question.programmingDetails.complexity">
              <h4 class="text-sm font-medium text-gray-700 mb-2">复杂度</h4>
              <p class="text-sm text-gray-600 bg-gray-50 rounded px-3 py-2">{{ question.programmingDetails.complexity }}</p>
            </div>

            <!-- 相似题目 -->
            <div v-if="question.programmingDetails.similarQuestions && question.programmingDetails.similarQuestions.length > 0">
              <h4 class="text-sm font-medium text-gray-700 mb-2">相似题目</h4>
              <div class="space-y-2">
                <div
                  v-for="(sq, index) in question.programmingDetails.similarQuestions"
                  :key="index"
                  class="flex items-center text-sm text-gray-600"
                >
                  <span class="mr-2">•</span>
                  <span>{{ sq.title || `题目 ${sq.id}` }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 答案要求 -->
          <div v-if="question.answerRequirement" class="border-t border-gray-200 pt-6">
            <h3 class="text-sm font-medium text-gray-700 mb-2">答案要求</h3>
            <div class="prose prose-sm max-w-none bg-blue-50 rounded-lg p-4" v-html="renderMarkdown(question.answerRequirement)"></div>
          </div>

          <!-- Red Flags -->
          <div v-if="question.redFlags && question.redFlags.length > 0" class="border-t border-gray-200 pt-6">
            <h3 class="text-sm font-medium text-gray-700 mb-3 flex items-center">
              <span class="text-red-500 mr-2">⚠️</span>
              Red Flags
            </h3>
            <ul class="space-y-2">
              <li
                v-for="(flag, index) in question.redFlags"
                :key="index"
                class="flex items-start text-sm"
              >
                <span class="text-red-500 mr-2">•</span>
                <span class="text-gray-700">{{ flag }}</span>
              </li>
            </ul>
          </div>

          <!-- 目标职位和级别 -->
          <div v-if="question.targetPosition || question.targetLevel" class="border-t border-gray-200 pt-6">
            <h3 class="text-sm font-medium text-gray-700 mb-2">目标受众</h3>
            <div class="flex gap-4 text-sm text-gray-600">
              <span v-if="question.targetPosition">职位: {{ question.targetPosition }}</span>
              <span v-if="question.targetLevel">级别: {{ question.targetLevel }}</span>
            </div>
          </div>

          <!-- 底部信息 -->
          <div class="border-t border-gray-200 pt-4 flex justify-between items-center text-xs text-gray-500">
            <span>创建于 {{ formatDate(question.createdAt) }}</span>
            <span v-if="question.updatedAt">最后更新 {{ formatDate(question.updatedAt) }}</span>
          </div>
        </div>

        <!-- 底部操作按钮 -->
        <div class="border-t border-gray-200 px-6 py-4 flex justify-end space-x-3">
          <button
            @click="$emit('close')"
            class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
          >
            关闭
          </button>
          <button
            v-if="!question.isOfficial"
            @click="$emit('edit')"
            class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
          >
            编辑
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { marked } from 'marked'
import DifficultyBadge from './DifficultyBadge.vue'

defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  question: {
    type: Object,
    required: true
  }
})

defineEmits(['close', 'edit'])

const renderMarkdown = (text) => {
  if (!text) return ''
  try {
    return marked(text, { breaks: true, gfm: true })
  } catch (error) {
    console.error('Markdown rendering error:', error)
    return text
  }
}

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

.prose h1, .prose h2, .prose h3 {
  margin-top: 1.25em;
  margin-bottom: 0.625em;
  font-weight: 600;
}

.prose ul, .prose ol {
  margin-top: 0.75em;
  margin-bottom: 0.75em;
  padding-left: 1.5em;
}

.prose code {
  background-color: #f3f4f6;
  padding: 0.2em 0.4em;
  border-radius: 0.25em;
  font-size: 0.875em;
}

.prose pre {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 1em;
  border-radius: 0.375em;
  overflow-x: auto;
}

.prose pre code {
  background-color: transparent;
  padding: 0;
  color: inherit;
}
</style>
