<template>
  <div class="w-full px-6 py-4">
    <!-- 标题和过滤器在同一行 -->
    <div class="flex items-center justify-between mb-4 bg-white rounded-lg shadow-sm p-4">
      <div>
        <h1 class="text-xl font-bold text-gray-800">学习总结 - 算法与数据结构</h1>
        <p class="text-xs text-gray-500 mt-1">共 {{ totalQuestions }} 题</p>
      </div>

      <!-- 紧凑的过滤器 -->
      <div class="flex items-center gap-3">
        <select
          v-model="selectedCategoryName"
          class="text-sm px-3 py-1.5 border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500"
        >
          <option value="">所有分类</option>
          <option v-for="categoryName in Object.keys(categories)" :key="categoryName" :value="categoryName">
            {{ categoryName }}
          </option>
        </select>

        <select
          v-model="selectedFocusAreaId"
          :disabled="!selectedCategoryName"
          class="text-sm px-3 py-1.5 border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 disabled:bg-gray-100"
        >
          <option value="">所有Focus Area</option>
          <option
            v-for="focusArea in availableFocusAreas"
            :key="focusArea.focusAreaId"
            :value="focusArea.focusAreaId"
          >
            {{ focusArea.focusAreaName }}
          </option>
        </select>

        <input
          v-model="leetcodeNumberFilter"
          type="number"
          placeholder="题号"
          class="text-sm px-3 py-1.5 border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 w-20"
        />

        <button
          @click="resetFilters"
          class="text-sm px-3 py-1.5 text-gray-600 bg-gray-100 rounded hover:bg-gray-200"
        >
          重置
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      <p class="mt-2 text-sm text-gray-600">加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="filteredQuestions.length === 0" class="bg-white rounded-lg shadow-sm p-8 text-center">
      <div class="text-gray-400 text-4xl mb-2">📝</div>
      <h3 class="text-base font-semibold text-gray-700 mb-1">暂无学习笔记</h3>
      <p class="text-sm text-gray-600">去「我的试题库」添加试题笔记吧！</p>
    </div>

    <!-- 紧凑的试题列表 - 双列双行布局 -->
    <div v-else class="bg-white rounded-lg shadow-sm p-4">
      <div class="grid grid-cols-2 gap-3">
        <div
          v-for="question in filteredQuestions"
          :key="question.questionId"
          class="p-2 hover:bg-blue-50 rounded transition-colors text-xs"
          :style="{ borderLeft: `3px solid ${getCategoryColor(question.categoryName)}` }"
        >
          <!-- 第一行：难度 + 题目 + Focus Area + 分类 -->
          <div class="flex items-center gap-2 mb-1">
            <!-- 难度图标 -->
            <span
              class="flex-shrink-0 w-2 h-2 rounded-full"
              :class="{
                'bg-green-500': question.difficulty === 'EASY',
                'bg-yellow-500': question.difficulty === 'MEDIUM',
                'bg-red-500': question.difficulty === 'HARD'
              }"
              :title="difficultyText(question.difficulty)"
            ></span>

            <!-- 题目 -->
            <button
              @click="showQuestionDetail(question)"
              class="text-left text-gray-900 hover:text-blue-600 font-medium hover:underline truncate flex-1"
            >
              {{ question.title }}
            </button>

            <!-- Focus Area -->
            <span class="text-gray-500 truncate flex-shrink-0 max-w-[100px]">
              {{ question.focusAreaName }}
            </span>

            <!-- 分类标签 -->
            <div
              class="inline-block px-1.5 py-0.5 rounded text-white text-xs font-medium flex-shrink-0"
              :style="{ backgroundColor: getCategoryColor(question.categoryName) }"
            >
              {{ getCategoryShortName(question.categoryName) }}
            </div>
          </div>

          <!-- 第二行：核心思路 -->
          <div v-if="question.coreStrategy" class="text-gray-700 leading-relaxed pl-4">
            {{ question.coreStrategy }}
          </div>
          <div v-else class="text-gray-400 italic pl-4">
            暂无核心思路
          </div>
        </div>
      </div>
    </div>

    <!-- 题目详情弹窗 -->
    <QuestionDetailModal
      v-if="selectedQuestion"
      :question="selectedQuestion"
      @close="selectedQuestion = null"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import learningReviewApi from '@/api/learningReview'
import QuestionDetailModal from '@/components/questions/QuestionDetailModal.vue'

// 数据状态
const loading = ref(false)
const categories = ref({})
const totalQuestions = ref(0)

// 过滤状态
const selectedCategoryName = ref('')
const selectedFocusAreaId = ref('')
const leetcodeNumberFilter = ref('')

// 弹窗状态
const selectedQuestion = ref(null)

/**
 * 获取学习总结数据
 */
async function fetchLearningReview() {
  loading.value = true
  try {
    const data = await learningReviewApi.getLearningReview()
    categories.value = data.categories || {}
    totalQuestions.value = data.totalQuestions || 0
  } catch (error) {
    console.error('获取学习总结失败:', error)
    alert('获取学习总结失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

/**
 * 获取选中分类下的所有Focus Areas
 */
const availableFocusAreas = computed(() => {
  if (!selectedCategoryName.value) return []

  const categoryData = categories.value[selectedCategoryName.value]
  if (!categoryData) return []

  return categoryData.focusAreas || []
})

/**
 * 扁平化的试题列表（带分类和Focus Area信息）
 */
const filteredQuestions = computed(() => {
  const result = []

  Object.entries(categories.value).forEach(([categoryName, categoryData]) => {
    // 按大分类过滤
    if (selectedCategoryName.value && categoryName !== selectedCategoryName.value) {
      return
    }

    categoryData.focusAreas.forEach(focusArea => {
      // 按Focus Area过滤
      if (selectedFocusAreaId.value && focusArea.focusAreaId !== selectedFocusAreaId.value) {
        return
      }

      focusArea.questions.forEach(question => {
        // 按LeetCode题号过滤
        if (leetcodeNumberFilter.value) {
          const filterNumber = parseInt(leetcodeNumberFilter.value)
          if (question.leetcodeNumber !== filterNumber) {
            return
          }
        }

        // 添加分类和Focus Area信息
        result.push({
          ...question,
          categoryName,
          focusAreaName: focusArea.focusAreaName
        })
      })
    })
  })

  return result
})

/**
 * 重置过滤器
 */
function resetFilters() {
  selectedCategoryName.value = ''
  selectedFocusAreaId.value = ''
  leetcodeNumberFilter.value = ''
}

/**
 * 难度文本映射
 */
function difficultyText(difficulty) {
  const map = {
    EASY: '简单',
    MEDIUM: '中等',
    HARD: '困难'
  }
  return map[difficulty] || difficulty
}

/**
 * 获取分类颜色
 */
function getCategoryColor(categoryName) {
  const colorMap = {
    '数据结构': '#3B82F6', // blue-500
    '算法': '#10B981', // green-500
    '设计': '#F59E0B', // amber-500
    '数学': '#8B5CF6', // violet-500
    '其他': '#6B7280'  // gray-500
  }
  return colorMap[categoryName] || '#6B7280'
}

/**
 * 获取分类简称
 */
function getCategoryShortName(categoryName) {
  const shortMap = {
    '数据结构': '数据',
    '算法': '算法',
    '设计': '设计',
    '数学': '数学',
    '其他': '其他'
  }
  return shortMap[categoryName] || categoryName.substring(0, 2)
}

/**
 * 显示题目详情弹窗
 */
function showQuestionDetail(question) {
  // 将 questionId 映射为 id，因为 QuestionDetailModal 期望 question.id
  selectedQuestion.value = {
    ...question,
    id: question.questionId
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchLearningReview()
})
</script>

<style scoped>
/* 紧凑模式下的小字体 */
.text-xs {
  font-size: 0.75rem;
  line-height: 1rem;
}
</style>
