<template>
  <div :class="question.questionType === 'programming' ? 'w-full h-full' : 'w-full'">
    <!-- Programming题目：左右两栏布局 -->
    <div v-if="question.questionType === 'programming'" class="grid grid-cols-2 gap-4 h-full">
      <!-- 左侧：题目描述 (50%) -->
      <div class="overflow-y-auto pr-4 border-r border-gray-200" style="height: calc(100vh - 280px)">
        <!-- 标题和LeetCode按钮在同一行 -->
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-900">📝 题目描述</h3>

          <!-- LeetCode按钮 - 和标题同一行 -->
          <a v-if="question.programmingDetails?.leetcodeUrl"
             :href="question.programmingDetails.leetcodeUrl"
             target="_blank"
             class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-lg hover:from-orange-600 hover:to-orange-700 transition-all shadow-md hover:shadow-lg text-sm">
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
              <path d="M13.483 0a1.374 1.374 0 0 0-.961.438L7.116 6.226l-3.854 4.126a5.266 5.266 0 0 0-1.209 2.104 5.35 5.35 0 0 0-.125.513 5.527 5.527 0 0 0 .062 2.362 5.83 5.83 0 0 0 .349 1.017 5.938 5.938 0 0 0 1.271 1.818l4.277 4.193.039.038c2.248 2.165 5.852 2.133 8.063-.074l2.396-2.392c.54-.54.54-1.414.003-1.955a1.378 1.378 0 0 0-1.951-.003l-2.396 2.392a3.021 3.021 0 0 1-4.205.038l-.02-.019-4.276-4.193c-.652-.64-.972-1.469-.948-2.263a2.68 2.68 0 0 1 .066-.523 2.545 2.545 0 0 1 .619-1.164L9.13 8.114c1.058-1.134 3.204-1.27 4.43-.278l3.501 2.831c.593.48 1.461.387 1.94-.207a1.384 1.384 0 0 0-.207-1.943l-3.5-2.831c-.8-.647-1.766-1.045-2.774-1.202l2.015-2.158A1.384 1.384 0 0 0 13.483 0zm-2.866 12.815a1.38 1.38 0 0 0-1.38 1.382 1.38 1.38 0 0 0 1.38 1.382H20.79a1.38 1.38 0 0 0 1.38-1.382 1.38 1.38 0 0 0-1.38-1.382z"/>
            </svg>
            <span class="font-semibold">LeetCode</span>
            <span v-if="question.programmingDetails.leetcodeNumber" class="text-xs bg-white/20 px-1.5 py-0.5 rounded">
              #{{ question.programmingDetails.leetcodeNumber }}
            </span>
          </a>
        </div>

        <!-- 题目描述 -->
        <div v-if="question.questionDescription" class="mb-6 prose prose-sm max-w-none">
          <div v-html="renderMarkdown(question.questionDescription)"></div>
        </div>

        <!-- 编程题详情 -->
        <div v-if="question.programmingDetails" class="space-y-4">

          <!-- 标签 -->
          <div v-if="question.programmingDetails.tags && question.programmingDetails.tags.length > 0" class="flex items-start gap-2">
            <span class="text-sm font-medium text-gray-700">标签:</span>
            <div class="flex flex-wrap gap-1">
              <span v-for="tag in question.programmingDetails.tags" :key="tag" class="px-2 py-0.5 bg-gray-100 text-gray-700 rounded text-xs">
                {{ tag }}
              </span>
            </div>
          </div>

          <!-- 复杂度 -->
          <div v-if="question.programmingDetails.complexity" class="text-sm">
            <span class="font-medium text-gray-700">时空复杂度:</span>
            <span class="ml-2 text-gray-600">{{ question.programmingDetails.complexity }}</span>
          </div>
        </div>

        <!-- Red Flags -->
        <div v-if="question.redFlags && question.redFlags.length > 0" class="mt-6 p-4 bg-red-50 border border-red-200 rounded-lg">
          <h4 class="text-sm font-semibold text-red-900 mb-2">⚠️ Red Flags</h4>
          <ul class="text-sm text-red-800 space-y-1">
            <li v-for="(flag, index) in question.redFlags" :key="index">• {{ flag }}</li>
          </ul>
        </div>
      </div>

      <!-- 右侧：答题区 (50%) -->
      <div class="overflow-y-auto pl-4" style="height: calc(100vh - 280px)">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-900">✍️ 记录思路</h3>

          <!-- 操作按钮 - 放在顶部 -->
          <div class="flex gap-2">
            <button
              @click="clearAnswer"
              class="px-4 py-1.5 text-sm border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              清空
            </button>
            <button
              @click="goToReview"
              :disabled="!hasContent"
              class="px-4 py-1.5 text-sm bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-1"
            >
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-8.707l-3-3a1 1 0 00-1.414 1.414L10.586 9H7a1 1 0 100 2h3.586l-1.293 1.293a1 1 0 101.414 1.414l3-3a1 1 0 000-1.414z" clip-rule="evenodd" />
              </svg>
              批改
            </button>
          </div>
        </div>

        <!-- 核心策略 - 放在最前面 -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-900 mb-2">💡 核心思路 / 算法策略</label>
          <textarea
            v-model="coreStrategy"
            placeholder="记录核心思路、算法策略、时间空间复杂度等..."
            rows="5"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm"
          ></textarea>
        </div>

        <!-- 代码/详细思路 - 可选填 -->
        <div>
          <label class="block text-sm font-medium text-gray-900 mb-2">📝 代码实现 / 详细思路（可选）</label>
          <textarea
            v-model="freeAnswer"
            placeholder="如需记录代码实现或详细解题思路，可在此输入...\n\n支持 Markdown 格式"
            rows="15"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm font-mono"
          ></textarea>
        </div>
      </div>
    </div>

    <!-- 非Programming题目：单栏布局 -->
    <div v-else>
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

      <!-- 模板选择（如果有模板） -->
      <div v-if="template" class="mb-6">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-4">
            <h3 class="text-lg font-semibold text-gray-900">答题方式</h3>
            <div class="flex gap-2">
              <button
                @click="useTemplate = true"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-colors',
                  useTemplate
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                ]"
              >
                📋 使用模板
              </button>
              <button
                @click="useTemplate = false"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-colors',
                  !useTemplate
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                ]"
              >
                ✏️ 自由答题
              </button>
            </div>
          </div>
          <button
            v-if="useTemplate"
            @click="showTemplateHelp = !showTemplateHelp"
            class="text-sm text-blue-600 hover:text-blue-700 flex items-center gap-1"
          >
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
            </svg>
            {{ showTemplateHelp ? '隐藏帮助' : '查看帮助' }}
          </button>
        </div>

        <!-- 模板帮助 -->
        <div v-if="useTemplate && showTemplateHelp && template.description" class="mb-4 p-3 bg-gray-50 border border-gray-200 rounded-lg text-sm text-gray-700">
          {{ template.description }}
        </div>

        <!-- 模板答题 -->
        <div v-if="useTemplate" class="space-y-4">
          <!-- 答题卡网格布局 -->
          <div class="grid grid-cols-1 lg:grid-cols-3 xl:grid-cols-4 gap-3">
            <div
              v-for="(field, index) in template.templateFields"
              :key="field.key"
              :class="[
                'relative rounded-lg shadow-md overflow-hidden',
                index === 0 ? 'bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-300' :
                index === 1 ? 'bg-gradient-to-br from-green-50 to-green-100 border border-green-300' :
                index === 2 ? 'bg-gradient-to-br from-orange-50 to-orange-100 border border-orange-300' :
                'bg-gradient-to-br from-purple-50 to-purple-100 border border-purple-300'
              ]"
            >
              <!-- 卡片头部 -->
              <div :class="[
                'px-2 py-1 flex items-center space-x-1.5',
                index === 0 ? 'bg-gradient-to-r from-blue-500 to-blue-600' :
                index === 1 ? 'bg-gradient-to-r from-green-500 to-green-600' :
                index === 2 ? 'bg-gradient-to-r from-orange-500 to-orange-600' :
                'bg-gradient-to-r from-purple-500 to-purple-600'
              ]">
                <div class="w-5 h-5 rounded-full flex items-center justify-center font-bold text-xs bg-white/20 backdrop-blur-sm text-white">
                  {{ index + 1 }}
                </div>
                <h5 class="text-xs font-semibold text-white">
                  {{ field.label }}
                  <span v-if="field.required" class="text-yellow-200">*</span>
                </h5>
              </div>

              <!-- 卡片内容 -->
              <div class="p-2">
                <textarea
                  v-model="templateAnswers[field.key]"
                  :placeholder="field.placeholder || `请输入${field.label}...`"
                  rows="12"
                  :class="[
                    'w-full px-2 py-2 text-sm bg-white border rounded-lg focus:outline-none focus:ring-1 resize-none',
                    index === 0 ? 'focus:ring-blue-500 focus:border-blue-500' :
                    index === 1 ? 'focus:ring-green-500 focus:border-green-500' :
                    index === 2 ? 'focus:ring-orange-500 focus:border-orange-500' :
                    'focus:ring-purple-500 focus:border-purple-500'
                  ]"
                ></textarea>
                <p v-if="field.hint" class="mt-1 text-xs text-gray-600">💡 {{ field.hint }}</p>
              </div>
            </div>
          </div>

          <!-- 核心策略 -->
          <div class="relative rounded-lg shadow-md overflow-hidden bg-gradient-to-br from-indigo-50 to-indigo-100 border border-indigo-300">
            <div class="px-2 py-1 flex items-center space-x-1.5 bg-gradient-to-r from-indigo-500 to-indigo-600">
              <div class="w-5 h-5 rounded-full flex items-center justify-center font-bold text-xs bg-white/20 backdrop-blur-sm text-white">
                💡
              </div>
              <h5 class="text-xs font-semibold text-white">核心策略</h5>
            </div>
            <div class="p-2">
              <textarea
                v-model="coreStrategy"
                placeholder="总结核心策略或关键要点..."
                rows="3"
                class="w-full px-2 py-2 text-sm bg-white border rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 resize-none"
              ></textarea>
            </div>
          </div>
        </div>

        <!-- 自由答题 -->
        <div v-else>
          <textarea
            v-model="freeAnswer"
            placeholder="在此输入你的答案...\n\n支持 Markdown 格式"
            rows="15"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm font-mono"
          ></textarea>

          <!-- 核心策略 -->
          <div class="mt-4">
            <label class="block text-sm font-medium text-gray-900 mb-2">💡 核心策略</label>
            <textarea
              v-model="coreStrategy"
              placeholder="总结核心策略或关键要点..."
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm"
            ></textarea>
          </div>
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
          <label class="block text-sm font-medium text-gray-900 mb-2">💡 核心策略</label>
          <textarea
            v-model="coreStrategy"
            placeholder="总结核心策略或关键要点..."
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
          @click="goToReview"
          :disabled="!hasContent"
          class="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-8.707l-3-3a1 1 0 00-1.414 1.414L10.586 9H7a1 1 0 100 2h3.586l-1.293 1.293a1 1 0 101.414 1.414l3-3a1 1 0 000-1.414z" clip-rule="evenodd" />
          </svg>
          转到批改模式
        </button>
      </div>
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

const emit = defineEmits(['goToReview'])

// 模板答案
const templateAnswers = ref({})
const coreStrategy = ref('')
const showTemplateHelp = ref(false)

// 自由答题内容
const freeAnswer = ref('')

// 预览控制
const showPreview = ref(false)

// 模板切换
const useTemplate = ref(true)

// 计算最终答案内容
const answerContent = computed(() => {
  if (props.template && useTemplate.value) {
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
  if (props.template && useTemplate.value) {
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

// 转到批改模式
const goToReview = () => {
  emit('goToReview', {
    content: answerContent.value,
    coreStrategy: coreStrategy.value
  })
}

// 清空答案
const clearAnswer = () => {
  if (confirm('确定要清空当前答案吗？')) {
    if (props.template && useTemplate.value) {
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
    useTemplate.value = true // 有模板时默认使用模板
  }
}, { immediate: true })

// 监听试题变化，清空答题区（答题模式不显示旧答案）
watch(() => props.question, (newQuestion) => {
  // 答题模式：总是从空白开始，不加载旧答案
  freeAnswer.value = ''
  coreStrategy.value = ''

  // 如果有模板，清空模板答案
  if (props.template) {
    templateAnswers.value = {}
    props.template.templateFields.forEach(field => {
      templateAnswers.value[field.key] = ''
    })
  }
}, { immediate: true })
</script>
