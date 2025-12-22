<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- 背景遮罩 -->
    <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="$emit('cancel')"></div>

    <!-- 弹窗内容 -->
    <div class="flex min-h-screen items-center justify-center p-4">
      <div class="relative bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
        <!-- 头部 -->
        <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
          <h2 class="text-xl font-semibold text-gray-900 flex items-center">
            <span class="mr-2">📝</span>
            我的笔记
          </h2>
          <button
            @click="$emit('cancel')"
            class="text-gray-400 hover:text-gray-600"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 表单 -->
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              笔记内容
            </label>
            <textarea
              v-model="noteContent"
              placeholder="记录你的答案思路、补充说明、心得...&#10;&#10;支持 Markdown 格式"
              rows="15"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
            ></textarea>
            <p class="mt-1 text-xs text-gray-500">支持 Markdown 格式</p>
          </div>

          <!-- Markdown 预览 -->
          <div v-if="noteContent" class="border-t border-gray-200 pt-4">
            <h3 class="text-sm font-medium text-gray-700 mb-2">预览</h3>
            <div
              class="prose prose-sm max-w-none bg-gray-50 rounded-md p-4"
              v-html="renderedMarkdown"
            ></div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex justify-end space-x-3 pt-4 border-t border-gray-200">
            <button
              @click="$emit('cancel')"
              type="button"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
            >
              取消
            </button>
            <button
              @click="handleSave"
              type="button"
              :disabled="!noteContent.trim()"
              class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              保存
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { marked } from 'marked'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  questionId: {
    type: Number,
    required: true
  },
  initialNote: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['save', 'cancel'])

const noteContent = ref('')

// 监听 initialNote 变化
watch(() => props.initialNote, (newNote) => {
  noteContent.value = newNote?.noteContent || ''
}, { immediate: true })

// Markdown 渲染
const renderedMarkdown = computed(() => {
  if (!noteContent.value) return ''
  try {
    return marked(noteContent.value, {
      breaks: true,
      gfm: true
    })
  } catch (error) {
    console.error('Markdown parsing error:', error)
    return noteContent.value
  }
})

const handleSave = () => {
  if (!noteContent.value.trim()) {
    return
  }
  emit('save', {
    questionId: props.questionId,
    noteContent: noteContent.value
  })
}
</script>

<style scoped>
/* Prose 样式 (简化版，如果已有 @tailwindcss/typography 插件则使用 prose 类) */
.prose {
  color: #374151;
  line-height: 1.75;
}

.prose p {
  margin-top: 1em;
  margin-bottom: 1em;
}

.prose h1,
.prose h2,
.prose h3,
.prose h4 {
  margin-top: 1.5em;
  margin-bottom: 0.75em;
  font-weight: 600;
}

.prose h1 {
  font-size: 1.5em;
}

.prose h2 {
  font-size: 1.25em;
}

.prose h3 {
  font-size: 1.125em;
}

.prose ul,
.prose ol {
  margin-top: 1em;
  margin-bottom: 1em;
  padding-left: 1.5em;
}

.prose li {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
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
  margin-top: 1em;
  margin-bottom: 1em;
}

.prose pre code {
  background-color: transparent;
  padding: 0;
  color: inherit;
}

.prose blockquote {
  border-left: 4px solid #e5e7eb;
  padding-left: 1em;
  margin-left: 0;
  font-style: italic;
  color: #6b7280;
}
</style>
