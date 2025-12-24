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

          <!-- 题目标题 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              题目标题 <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.title"
              type="text"
              required
              placeholder="例如: [5] Longest Palindromic Substring"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <p class="mt-1 text-xs text-gray-500">简短标题，用于列表显示</p>
          </div>

          <!-- 问题描述 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              问题描述
            </label>
            <textarea
              v-model="form.questionDescription"
              rows="6"
              placeholder="输入详细的题目描述（支持 Markdown）&#10;&#10;例如：&#10;**题目描述**: ...&#10;&#10;**考察点**: ...&#10;&#10;**示例**:&#10;```&#10;输入: ...&#10;输出: ...&#10;```"
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

          <!-- 编程题详情 -->
          <div class="border-t border-gray-200 pt-6 mt-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">编程题详情</h3>

            <div class="space-y-4">
              <!-- LeetCode链接 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  LeetCode 链接
                </label>
                <input
                  v-model="form.programmingDetails.leetcodeUrl"
                  type="url"
                  placeholder="https://leetcode.com/problems/xxx/"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <p class="mt-1 text-xs text-gray-500">LeetCode 题目链接</p>
              </div>

              <!-- labuladong链接 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  labuladong 链接
                </label>
                <input
                  v-model="form.programmingDetails.labuladongUrl"
                  type="url"
                  placeholder="https://labuladong.online/algo/xxx/"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <p class="mt-1 text-xs text-gray-500">labuladong 算法教程链接</p>
              </div>

              <!-- 标签（Tags） -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  标签（Tags）
                </label>
                <input
                  v-model="form.programmingDetails.tags"
                  type="text"
                  placeholder="多个标签用逗号分隔，例如：数组,双指针,滑动窗口"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <p class="mt-1 text-xs text-gray-500">用于分类和搜索，多个标签用逗号分隔</p>
              </div>

              <!-- HelloInterview链接 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  HelloInterview 链接
                </label>
                <input
                  v-model="form.programmingDetails.hellointerviewUrl"
                  type="url"
                  placeholder="https://www.hellointerview.com/xxx"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <p class="mt-1 text-xs text-gray-500">HelloInterview 面试题链接</p>
              </div>

              <!-- 相似题目 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  相似题目
                </label>
                <input
                  v-model="form.programmingDetails.similarQuestions"
                  type="text"
                  placeholder="相似题目编号，用逗号分隔，例如：15,18,167"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <p class="mt-1 text-xs text-gray-500">填写相似题目的ID，用逗号分隔</p>
              </div>

              <!-- 复杂度 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  复杂度
                </label>
                <input
                  v-model="form.programmingDetails.complexity"
                  type="text"
                  placeholder="例如：时间O(n), 空间O(1)"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <p class="mt-1 text-xs text-gray-500">同时填写时间和空间复杂度</p>
              </div>

              <!-- 重要标记 -->
              <div>
                <label class="inline-flex items-center">
                  <input
                    type="checkbox"
                    v-model="form.programmingDetails.isImportant"
                    class="form-checkbox h-4 w-4 text-blue-600 rounded focus:ring-blue-500"
                  />
                  <span class="ml-2 text-sm font-medium text-gray-700">标记为重要题目</span>
                </label>
                <p class="mt-1 text-xs text-gray-500">勾选表示这是一道重要的必做题</p>
              </div>
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
  title: '',
  questionDescription: '',
  difficulty: 'EASY',
  answerRequirement: '',
  targetPosition: '',
  targetLevel: '',
  redFlags: [],
  // 编程题详情
  programmingDetails: {
    leetcodeUrl: '',
    labuladongUrl: '',
    hellointerviewUrl: '',
    tags: '',
    similarQuestions: '',
    complexity: '',
    isImportant: false
  }
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
    const programmingDetails = newQuestion.programmingDetails || {}
    form.value = {
      focusAreaId: newQuestion.focusAreaId || '',
      title: newQuestion.title || '',
      questionDescription: newQuestion.questionDescription || '',
      difficulty: newQuestion.difficulty || 'EASY',
      answerRequirement: newQuestion.answerRequirement || '',
      targetPosition: newQuestion.targetPosition || '',
      targetLevel: newQuestion.targetLevel || '',
      redFlags: newQuestion.redFlags || [],
      programmingDetails: {
        leetcodeUrl: programmingDetails.leetcodeUrl || '',
        labuladongUrl: programmingDetails.labuladongUrl || '',
        hellointerviewUrl: programmingDetails.hellointerviewUrl || '',
        tags: programmingDetails.tags || '',
        similarQuestions: programmingDetails.similarQuestions || '',
        complexity: programmingDetails.complexity || '',
        isImportant: programmingDetails.isImportant || false
      }
    }
  } else {
    isEdit.value = false
    form.value = {
      focusAreaId: props.currentFocusAreaId || '',  // 使用当前选中的Focus Area
      title: '',
      questionDescription: '',
      difficulty: 'EASY',
      answerRequirement: '',
      targetPosition: '',
      targetLevel: '',
      redFlags: [],
      programmingDetails: {
        leetcodeUrl: '',
        labuladongUrl: '',
        hellointerviewUrl: '',
        tags: '',
        similarQuestions: '',
        complexity: '',
        isImportant: false
      }
    }
  }
}, { immediate: true })

const handleSubmit = () => {
  const cleanedForm = {
    ...form.value,
    redFlags: form.value.redFlags.filter(flag => flag.trim() !== '')
  }

  emit('save', cleanedForm)
}
</script>
