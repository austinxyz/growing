<template>
  <div class="max-w-4xl mx-auto">
    <!-- 答题要求提示 -->
    <div v-if="question.answerRequirement" class="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
      <div class="flex items-start gap-2">
        <svg class="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
        </svg>
        <div class="flex-1">
          <h4 class="text-sm font-semibold text-blue-900 mb-1">答题要求</h4>
          <div class="text-sm text-blue-800 prose prose-sm max-w-none" v-html="renderMarkdown(question.answerRequirement)"></div>
        </div>
      </div>
    </div>

    <!-- Red Flags提示 -->
    <div v-if="question.redFlags && question.redFlags.length > 0" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
      <div class="flex items-start gap-2">
        <svg class="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
        </svg>
        <div class="flex-1">
          <h4 class="text-sm font-semibold text-red-900 mb-2">⚠️ Red Flags (避免以下情况)</h4>
          <ul class="text-sm text-red-800 space-y-1">
            <li v-for="(flag, index) in question.redFlags" :key="index" class="flex items-start gap-2">
              <span class="text-red-500">•</span>
              <span>{{ flag }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 答题模板（如果有） -->
    <div v-if="template" class="mb-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-gray-900">📋 答题模板：{{ template.templateName }}</h3>
        <button
          @click="showTemplateHelp = !showTemplateHelp"
          class="text-sm text-blue-600 hover:text-blue-700 flex items-center gap-1"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
          </svg>
          {{ showTemplateHelp ? '隐藏帮助' : '查看帮助' }}
        </button>
      </div>

      <div v-if="showTemplateHelp && template.description" class="mb-4 p-3 bg-gray-50 border border-gray-200 rounded-lg text-sm text-gray-700">
        {{ template.description }}
      </div>

      <div class="space-y-4">
        <div v-for="field in template.templateFields" :key="field.key" class="bg-white border border-gray-200 rounded-lg p-4">
          <label class="block text-sm font-medium text-gray-900 mb-2">
            {{ field.label }}
            <span v-if="field.required" class="text-red-500">*</span>
          </label>
          <textarea
            v-model="templateAnswers[field.key]"
            :placeholder="field.placeholder || `请输入${field.label}...`"
            rows="4"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm"
          ></textarea>
          <p v-if="field.hint" class="mt-1 text-xs text-gray-500">{{ field.hint }}</p>
        </div>
      </div>

      <!-- 核心策略（可选） -->
      <div class="mt-4 bg-white border border-gray-200 rounded-lg p-4">
        <label class="block text-sm font-medium text-gray-900 mb-2">
          💡 核心策略 / 关键要点
        </label>
        <textarea
          v-model="coreStrategy"
          placeholder="总结这道题的核心策略或关键要点..."
          rows="3"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm"
        ></textarea>
      </div>
    </div>

    <!-- 自由答题区（无模板时） -->
    <div v-else class="mb-6">
      <h3 class="text-lg font-semibold text-gray-900 mb-4">✍️ 答题区域</h3>
      <textarea
        v-model="freeAnswer"
        placeholder="在此输入你的答案...\n\n支持 Markdown 格式"
        rows="15"
        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm font-mono"
      ></textarea>

      <!-- 核心策略 -->
      <div class="mt-4">
        <label class="block text-sm font-medium text-gray-900 mb-2">
          💡 核心策略 / 关键要点
        </label>
        <textarea
          v-model="coreStrategy"
          placeholder="总结这道题的核心策略或关键要点..."
          rows="3"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm"
        ></textarea>
      </div>
    </div>

    <!-- Markdown预览 -->
    <div v-if="answerContent && answerContent.trim()" class="mb-6">
      <div class="flex items-center justify-between mb-3">
        <h3 class="text-lg font-semibold text-gray-900">👁️ 答案预览</h3>
        <button
          @click="showPreview = !showPreview"
          class="text-sm text-blue-600 hover:text-blue-700"
        >
          {{ showPreview ? '隐藏预览' : '显示预览' }}
        </button>
      </div>
      <div v-show="showPreview" class="p-4 bg-gray-50 border border-gray-200 rounded-lg">
        <div class="prose prose-sm max-w-none" v-html="renderMarkdown(answerContent)"></div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="flex justify-end gap-3">
      <button
        @click="clearAnswer"
        class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
      >
        清空
      </button>
      <button
        @click="saveAnswer"
        :disabled="!hasContent"
        class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
      >
        💾 保存答案
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { marked } from 'marked'

const props = defineProps({
  question: {
    type: Object,
    required: true
  },
  template: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['save'])

// 模板答案
const templateAnswers = ref({})
const coreStrategy = ref('')
const showTemplateHelp = ref(false)

// 自由答题内容
const freeAnswer = ref('')

// 预览控制
const showPreview = ref(false)

// 计算最终答案内容
const answerContent = computed(() => {
  if (props.template) {
    // 使用模板格式化答案
    let content = ''
    props.template.templateFields.forEach(field => {
      if (templateAnswers.value[field.key]) {
        content += `## ${field.label}\n\n${templateAnswers.value[field.key]}\n\n`
      }
    })
    return content
  } else {
    return freeAnswer.value
  }
})

// 判断是否有内容
const hasContent = computed(() => {
  if (props.template) {
    return Object.values(templateAnswers.value).some(v => v && v.trim())
  } else {
    return freeAnswer.value.trim() !== ''
  }
})

// Markdown渲染
const renderMarkdown = (content) => {
  if (!content) return ''
  return marked(content)
}

// 保存答案
const saveAnswer = () => {
  emit('save', {
    content: answerContent.value,
    coreStrategy: coreStrategy.value
  })
}

// 清空答案
const clearAnswer = () => {
  if (confirm('确定要清空当前答案吗？')) {
    if (props.template) {
      templateAnswers.value = {}
    } else {
      freeAnswer.value = ''
    }
    coreStrategy.value = ''
  }
}

// 监听模板变化，初始化答案对象
watch(() => props.template, (newTemplate) => {
  if (newTemplate) {
    templateAnswers.value = {}
    newTemplate.templateFields.forEach(field => {
      templateAnswers.value[field.key] = ''
    })
  }
}, { immediate: true })

// 监听试题变化，加载已有答案
watch(() => props.question, (newQuestion) => {
  if (newQuestion && newQuestion.userNote) {
    freeAnswer.value = newQuestion.userNote.noteContent || ''
    coreStrategy.value = newQuestion.userNote.coreStrategy || ''

    // 如果有模板，尝试解析已有答案到模板字段
    // TODO: 实现答案解析逻辑
  } else {
    freeAnswer.value = ''
    coreStrategy.value = ''
  }
}, { immediate: true })
</script>
