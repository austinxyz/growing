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
          <!-- 答题模式切换 (如果有模版) -->
          <div v-if="answerTemplate" class="border-b border-gray-200 pb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">答题模式</label>
            <div class="flex space-x-4">
              <button
                @click="answerMode = 'template'"
                :class="[
                  'px-4 py-2 rounded-md text-sm font-medium transition-colors',
                  answerMode === 'template'
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                ]"
              >
                {{ answerTemplate.templateName }} 模版
              </button>
              <button
                @click="answerMode = 'free'"
                :class="[
                  'px-4 py-2 rounded-md text-sm font-medium transition-colors',
                  answerMode === 'free'
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                ]"
              >
                自由答题
              </button>
            </div>
          </div>

          <!-- 动态模版结构 -->
          <div v-if="answerTemplate && answerMode === 'template'" class="space-y-4">
            <!-- 模版说明 -->
            <div v-if="answerTemplate.description" class="bg-blue-50 border border-blue-200 rounded-md p-4">
              <h3 class="text-sm font-semibold text-blue-900 mb-2">💡 {{ answerTemplate.templateName }}</h3>
              <p class="text-xs text-blue-800">{{ answerTemplate.description }}</p>
            </div>

            <!-- 动态渲染模版字段 -->
            <div
              v-for="(field, index) in answerTemplate.templateFields"
              :key="field.key"
            >
              <label class="block text-sm font-medium text-gray-700 mb-2">
                <span :class="[
                  'inline-flex items-center justify-center w-6 h-6 text-white text-xs font-bold rounded-full mr-2',
                  index === 0 ? 'bg-blue-600' :
                  index === 1 ? 'bg-green-600' :
                  index === 2 ? 'bg-orange-600' :
                  'bg-purple-600'
                ]">
                  {{ index + 1 }}
                </span>
                {{ field.label }}
              </label>
              <textarea
                v-model="templateValues[field.key]"
                :placeholder="field.placeholder || `请输入${field.label}...`"
                :rows="index === 2 ? 6 : 4"
                :class="[
                  'w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 font-mono text-sm',
                  index === 0 ? 'focus:ring-blue-500' :
                  index === 1 ? 'focus:ring-green-500' :
                  index === 2 ? 'focus:ring-orange-500' :
                  'focus:ring-purple-500'
                ]"
              ></textarea>
            </div>

            <!-- 完整预览 -->
            <div v-if="Object.values(templateValues).some(v => v && v.trim())" class="border-t border-gray-200 pt-4">
              <h3 class="text-sm font-medium text-gray-700 mb-2">完整答案预览</h3>
              <div class="prose prose-sm max-w-none bg-gray-50 rounded-md p-4" v-html="renderedTemplateAnswer"></div>
            </div>
          </div>

          <!-- 核心思路（所有题目类型） -->
          <div class="border-b border-gray-200 pb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              核心思路
              <span class="text-xs text-gray-500">
                {{ questionType === 'programming' ? '(算法要点、时间空间复杂度)' : '(关键思路、要点总结)' }}
              </span>
            </label>
            <textarea
              v-model="coreStrategy"
              :placeholder="coreStrategyPlaceholder"
              rows="8"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
            ></textarea>
            <p class="mt-1 text-xs text-gray-500">
              {{ questionType === 'programming'
                ? '记录算法思路、时间/空间复杂度、关键要点'
                : '记录答题核心思路、关键要点、注意事项'
              }}
            </p>

            <!-- 核心思路预览 -->
            <div v-if="coreStrategy" class="mt-3 border-t border-gray-200 pt-3">
              <h4 class="text-xs font-medium text-gray-700 mb-2">核心思路预览</h4>
              <div
                class="prose prose-sm max-w-none bg-green-50 rounded-md p-3 text-sm"
                v-html="renderedStrategy"
              ></div>
            </div>
          </div>

          <!-- 自由笔记内容（无模版或自由模式） -->
          <div v-if="!answerTemplate || answerMode === 'free'">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              笔记内容
            </label>
            <textarea
              v-model="noteContent"
              :placeholder="noteContentPlaceholder"
              :rows="questionType === 'programming' ? 10 : 15"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
            ></textarea>
            <p class="mt-1 text-xs text-gray-500">支持 Markdown 格式</p>
          </div>

          <!-- Markdown 预览 -->
          <div v-if="noteContent" class="border-t border-gray-200 pt-4">
            <h3 class="text-sm font-medium text-gray-700 mb-2">笔记预览</h3>
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
              :disabled="isSaveDisabled"
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
    type: [Number, null],
    default: null
  },
  initialNote: {
    type: Object,
    default: null
  },
  questionType: {
    type: String,
    default: 'behavioral'
  },
  answerTemplate: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['save', 'cancel'])

const noteContent = ref('')
const coreStrategy = ref('')

// 答题模式：template（使用模版） 或 free（自由答题）
const answerMode = ref('template')

// 动态模版字段值（key-value 对象）
const templateValues = ref({})

// Placeholder for note content textarea
const noteContentPlaceholder = computed(() => {
  return '记录你的答案思路、补充说明、心得...\n\n支持 Markdown 格式'
})

// Placeholder for core strategy textarea
const coreStrategyPlaceholder = computed(() => {
  if (props.questionType === 'programming') {
    return '记录解题的核心思路和算法要点...\n\n例如：\n1. 使用双指针从两端向中间移动\n2. 时间复杂度 O(n)，空间复杂度 O(1)\n3. 注意边界条件...\n\n支持 Markdown 格式'
  } else {
    return '记录答题的核心思路和关键要点...\n\n例如：\n1. 明确问题核心诉求\n2. 关键解决步骤\n3. 注意事项...\n\n支持 Markdown 格式'
  }
})

// 初始化模版字段
watch(() => props.answerTemplate, (template) => {
  if (template && template.templateFields) {
    // 初始化所有字段为空字符串
    const values = {}
    template.templateFields.forEach(field => {
      values[field.key] = ''
    })
    templateValues.value = values
  }
}, { immediate: true })

// 监听 initialNote 变化
watch(() => props.initialNote, (newNote) => {
  noteContent.value = newNote?.noteContent || ''
  coreStrategy.value = newNote?.coreStrategy || ''

  // 解析模版格式（如果存在）
  if (newNote?.noteContent && props.answerTemplate) {
    const fields = props.answerTemplate.templateFields || []
    let isTemplateFormat = true
    const values = {}

    // 尝试解析模版格式
    for (const field of fields) {
      // 转义正则表达式中的特殊字符
      const escapedLabel = field.label.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
      const pattern = new RegExp(`## ${escapedLabel}\\s*\\n([\\s\\S]*?)(?=\\n## |$)`, 'i')
      const match = newNote.noteContent.match(pattern)

      if (match) {
        values[field.key] = match[1].trim()
      } else {
        isTemplateFormat = false
        break
      }
    }

    if (isTemplateFormat) {
      answerMode.value = 'template'
      templateValues.value = values
    } else {
      answerMode.value = 'free'
    }
  }
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

// Markdown 渲染 - 核心思路
const renderedStrategy = computed(() => {
  if (!coreStrategy.value) return ''
  try {
    return marked(coreStrategy.value, {
      breaks: true,
      gfm: true
    })
  } catch (error) {
    console.error('Markdown parsing error:', error)
    return coreStrategy.value
  }
})

// 模版完整答案渲染
const renderedTemplateAnswer = computed(() => {
  if (!props.answerTemplate || !props.answerTemplate.templateFields) return ''

  const parts = props.answerTemplate.templateFields.map(field => {
    const value = templateValues.value[field.key] || ''
    return `## ${field.label}\n${value}`
  })

  const templateMarkdown = parts.join('\n\n')

  try {
    return marked(templateMarkdown, {
      breaks: true,
      gfm: true
    })
  } catch (error) {
    console.error('Markdown parsing error:', error)
    return templateMarkdown
  }
})

// 保存按钮禁用状态
const isSaveDisabled = computed(() => {
  // 模版模式：至少填写一个模版字段或核心思路
  if (answerMode.value === 'template' && props.answerTemplate) {
    const hasTemplateValue = Object.values(templateValues.value).some(v => v && v.trim())
    const hasStrategy = coreStrategy.value && coreStrategy.value.trim()
    return !hasTemplateValue && !hasStrategy
  }

  // 自由模式：至少填写笔记内容或核心思路
  return !noteContent.value.trim() && !coreStrategy.value.trim()
})

const handleSave = () => {
  // 模版模式下，至少填写一个模版字段或核心思路
  if (answerMode.value === 'template' && props.answerTemplate) {
    const hasTemplateValue = Object.values(templateValues.value).some(v => v && v.trim())
    const hasStrategy = coreStrategy.value && coreStrategy.value.trim()

    if (!hasTemplateValue && !hasStrategy) {
      alert('请至少填写一项内容')
      return
    }

    // 将模版字段合并为 Markdown 格式存入 noteContent
    const parts = props.answerTemplate.templateFields.map(field => {
      const value = templateValues.value[field.key] || ''
      return `## ${field.label}\n${value}`
    })
    const templateMarkdown = parts.join('\n\n')

    const data = {
      questionId: props.questionId,
      noteContent: templateMarkdown,
      coreStrategy: coreStrategy.value
    }

    emit('save', data)
    return
  }

  // 自由模式：至少填写笔记内容或核心思路
  if (!noteContent.value.trim() && !coreStrategy.value.trim()) {
    alert('笔记内容或核心思路至少填写一项')
    return
  }

  const data = {
    questionId: props.questionId,
    noteContent: noteContent.value,
    coreStrategy: coreStrategy.value
  }

  emit('save', data)
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
