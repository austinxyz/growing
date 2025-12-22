<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- 背景遮罩 -->
    <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="$emit('cancel')"></div>

    <!-- 弹窗内容 -->
    <div class="flex min-h-screen items-center justify-center p-4">
      <div class="relative bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <!-- 头部 -->
        <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
          <h2 class="text-xl font-semibold text-gray-900">
            {{ isEdit ? '编辑试题' : '添加试题' }}
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
        <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
          <!-- 所属 Focus Area（如果有预设则显示，否则显示选择器） -->
          <div v-if="currentFocusAreaName">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              所属专注领域
            </label>
            <div class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-md text-gray-700">
              {{ currentFocusAreaName }}
            </div>
          </div>
          <div v-else>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              所属专注领域 <span class="text-red-500">*</span>
            </label>
            <select
              v-model="form.focusAreaId"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">请选择专注领域</option>
              <option
                v-for="fa in focusAreas"
                :key="fa.id"
                :value="fa.id"
              >
                {{ fa.name }}
              </option>
            </select>
          </div>

          <!-- 问题描述 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              问题描述 <span class="text-red-500">*</span>
            </label>
            <textarea
              v-model="form.questionText"
              required
              rows="4"
              placeholder="输入问题描述（支持 Markdown）"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
            <p class="mt-1 text-xs text-gray-500">支持 Markdown 格式，包括代码块</p>
          </div>

          <!-- 难度级别 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              难度级别 <span class="text-red-500">*</span>
            </label>
            <div class="flex space-x-4">
              <label class="inline-flex items-center">
                <input
                  type="radio"
                  v-model="form.difficulty"
                  value="EASY"
                  required
                  class="form-radio text-green-500 focus:ring-green-500"
                />
                <span class="ml-2">Easy</span>
              </label>
              <label class="inline-flex items-center">
                <input
                  type="radio"
                  v-model="form.difficulty"
                  value="MEDIUM"
                  class="form-radio text-yellow-500 focus:ring-yellow-500"
                />
                <span class="ml-2">Medium</span>
              </label>
              <label class="inline-flex items-center">
                <input
                  type="radio"
                  v-model="form.difficulty"
                  value="HARD"
                  class="form-radio text-red-500 focus:ring-red-500"
                />
                <span class="ml-2">Hard</span>
              </label>
            </div>
          </div>

          <!-- 答案要求 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              答案要求
            </label>
            <textarea
              v-model="form.answerRequirement"
              rows="4"
              placeholder="说明如何回答这个问题（支持 Markdown）"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
            <p class="mt-1 text-xs text-gray-500">例如：1. 定义核心概念 2. 说明关键要点 3. 给出实例</p>
          </div>

          <!-- 针对职位 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              针对职位
            </label>
            <select
              v-model="form.targetPosition"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">不限职位</option>
              <option
                v-for="cp in careerPaths"
                :key="cp.id"
                :value="cp.name"
              >
                {{ cp.icon }} {{ cp.name }}
              </option>
            </select>
          </div>

          <!-- 针对级别 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              针对级别
            </label>
            <select
              v-model="form.targetLevel"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">不限级别</option>
              <option value="Junior">Junior</option>
              <option value="Senior">Senior</option>
              <option value="Staff">Staff</option>
              <option value="Principal">Principal</option>
            </select>
          </div>

          <!-- Red Flags -->
          <RedFlagList v-model="form.redFlags" />

          <!-- 操作按钮 -->
          <div class="flex justify-end space-x-3 pt-4 border-t border-gray-200">
            <button
              type="button"
              @click="$emit('cancel')"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
            >
              取消
            </button>
            <button
              type="submit"
              class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
            >
              保存
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import RedFlagList from './RedFlagList.vue'
import { getAllCareerPaths } from '@/api/careerPaths'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  question: {
    type: Object,
    default: null
  },
  focusAreas: {
    type: Array,
    default: () => []
  },
  currentFocusAreaId: {
    type: Number,
    default: null
  },
  currentFocusAreaName: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['save', 'cancel'])

const isEdit = ref(false)
const careerPaths = ref([])
const form = ref({
  focusAreaId: '',
  questionText: '',
  difficulty: 'EASY',
  answerRequirement: '',
  targetPosition: '',
  targetLevel: '',
  redFlags: []
})

// 初始化职业路径列表
onMounted(async () => {
  try {
    const response = await getAllCareerPaths()
    careerPaths.value = response.data || response || []
  } catch (error) {
    console.error('Failed to load career paths:', error)
  }
})

// 监听 question prop 变化
watch(() => props.question, (newQuestion) => {
  if (newQuestion) {
    isEdit.value = true
    form.value = {
      focusAreaId: newQuestion.focusAreaId || '',
      questionText: newQuestion.questionText || '',
      difficulty: newQuestion.difficulty || 'EASY',
      answerRequirement: newQuestion.answerRequirement || '',
      targetPosition: newQuestion.targetPosition || '',
      targetLevel: newQuestion.targetLevel || '',
      redFlags: newQuestion.redFlags || []
    }
  } else {
    isEdit.value = false
    form.value = {
      focusAreaId: props.currentFocusAreaId || '',  // 使用当前选中的Focus Area
      questionText: '',
      difficulty: 'EASY',
      answerRequirement: '',
      targetPosition: '',
      targetLevel: '',
      redFlags: []
    }
  }
}, { immediate: true })

const handleSubmit = () => {
  // 过滤掉空的 Red Flags
  const cleanedForm = {
    ...form.value,
    redFlags: form.value.redFlags.filter(flag => flag.trim() !== '')
  }
  emit('save', cleanedForm)
}
</script>
