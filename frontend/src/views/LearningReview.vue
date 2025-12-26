<template>
  <div class="w-full px-6 py-4 bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50 min-h-screen">
    <!-- 标题和过滤器在同一行 - 添加渐变背景 -->
    <div class="flex items-center justify-between mb-4 bg-gradient-to-r from-blue-600 to-purple-600 rounded-xl shadow-lg p-4 no-print">
      <div>
        <h1 class="text-2xl font-bold text-white">学习总结 - 算法与数据结构</h1>
        <p class="text-xs text-blue-100 mt-1">共 {{ totalQuestions }} 题</p>
      </div>

      <!-- 紧凑的过滤器 -->
      <div class="flex items-center gap-3">
        <!-- 打印按钮 - 增强样式 -->
        <button
          @click="printSummary"
          class="flex items-center gap-1 text-sm px-3 py-1.5 bg-white/90 text-indigo-700 rounded-lg hover:bg-white hover:shadow-md transition-all font-medium"
          title="打印学习总结"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
          </svg>
          打印
        </button>
        <select
          v-model="selectedCategoryName"
          class="text-sm px-3 py-1.5 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-white bg-white/90 text-gray-900 shadow-sm"
        >
          <option value="">所有分类</option>
          <option v-for="categoryName in Object.keys(categories)" :key="categoryName" :value="categoryName">
            {{ categoryName }}
          </option>
        </select>

        <select
          v-model="selectedFocusAreaId"
          :disabled="!selectedCategoryName"
          class="text-sm px-3 py-1.5 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-white bg-white/90 text-gray-900 shadow-sm disabled:opacity-50"
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
          class="text-sm px-3 py-1.5 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-white bg-white/90 text-gray-900 shadow-sm w-20"
        />

        <!-- 重要题目筛选 - 增强样式 -->
        <label class="flex items-center gap-2 text-sm text-white cursor-pointer bg-white/20 px-3 py-1.5 rounded-lg hover:bg-white/30 transition-all">
          <input
            v-model="showImportantOnly"
            type="checkbox"
            class="w-4 h-4 text-orange-500 border-white rounded focus:ring-white"
          />
          <span class="flex items-center gap-1 font-medium">
            <span class="text-yellow-300">⭐</span>
            仅重要题
          </span>
        </label>

        <button
          @click="resetFilters"
          class="text-sm px-3 py-1.5 bg-white/90 text-gray-700 rounded-lg hover:bg-white hover:shadow-md transition-all font-medium"
        >
          重置
        </button>
      </div>
    </div>

    <!-- 打印用的标题（仅在打印时显示） -->
    <div class="print-only">
      <h1 class="text-center text-lg font-bold mb-2">算法与数据结构学习总结</h1>
      <p class="text-center text-xs text-gray-600 mb-3">共 {{ filteredQuestions.length }} 题</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12 no-print">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      <p class="mt-2 text-sm text-gray-600">加载中...</p>
    </div>

    <!-- 空状态 - 增强样式 -->
    <div v-else-if="filteredQuestions.length === 0" class="bg-gradient-to-br from-white to-blue-50 rounded-xl shadow-lg p-8 text-center no-print border-2 border-blue-100">
      <div class="text-blue-400 text-4xl mb-2">📝</div>
      <h3 class="text-lg font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600 mb-1">暂无学习笔记</h3>
      <p class="text-sm text-gray-600">去「我的试题库」添加试题笔记吧！</p>
    </div>

    <!-- 紧凑的试题列表 - 双列布局(屏幕显示) - 紧凑样式 -->
    <div v-else class="bg-white rounded-xl shadow-lg p-4 screen-only">
      <div class="grid grid-cols-2 gap-3">
        <div
          v-for="question in filteredQuestions"
          :key="question.questionId"
          @click="showQuestionDetail(question)"
          class="p-2.5 bg-gradient-to-br from-white to-gray-50 rounded-lg hover:shadow-lg transition-all text-xs border-l-4 cursor-pointer group hover:border-blue-300"
          :style="{ borderLeftColor: getCategoryColor(question.categoryName) }"
        >
          <!-- 第一行:难度 + 重要 + 题目 + Focus Area + 分类 -->
          <div class="flex items-center gap-1.5 mb-1.5">
            <!-- 难度徽章 - 紧凑版 -->
            <span
              :class="[
                'flex-shrink-0 px-1.5 py-0.5 rounded-full text-xs font-bold shadow-sm',
                question.difficulty === 'EASY' ? 'bg-gradient-to-r from-green-400 to-emerald-500 text-white' :
                question.difficulty === 'MEDIUM' ? 'bg-gradient-to-r from-yellow-400 to-orange-500 text-white' :
                'bg-gradient-to-r from-red-400 to-pink-500 text-white'
              ]"
              :title="difficultyText(question.difficulty)"
            >
              {{ difficultyText(question.difficulty)[0] }}
            </span>

            <!-- 重要标识 -->
            <span v-if="question.isImportant" class="text-orange-500 text-sm flex-shrink-0" title="重要题目">⭐</span>

            <!-- 题目 -->
            <div class="text-left text-gray-900 group-hover:text-blue-700 font-semibold truncate flex-1 transition-colors text-xs">
              {{ question.title }}
            </div>

            <!-- Focus Area标签 - 紧凑版 -->
            <span class="inline-flex items-center px-2 py-0.5 rounded-full bg-gradient-to-r from-purple-100 to-blue-100 text-purple-700 text-xs font-medium flex-shrink-0 max-w-[100px] truncate">
              {{ question.focusAreaName }}
            </span>

            <!-- 分类标签 - 紧凑版 -->
            <div
              class="inline-block px-1.5 py-0.5 rounded text-white text-xs font-bold flex-shrink-0 shadow-sm"
              :style="{ background: `linear-gradient(135deg, ${getCategoryColor(question.categoryName)}, ${adjustColorBrightness(getCategoryColor(question.categoryName), -20)})` }"
            >
              {{ getCategoryShortName(question.categoryName) }}
            </div>
          </div>

          <!-- 第二行:核心思路 - 紧凑版 -->
          <div v-if="question.coreStrategy" class="bg-blue-50/50 rounded p-1.5 pl-2">
            <p class="text-gray-700 leading-snug text-xs group-hover:text-gray-900">
              {{ question.coreStrategy }}
            </p>
          </div>
          <div v-else class="text-gray-400 italic text-xs text-center py-1">
            暂无核心思路
          </div>
        </div>
      </div>
    </div>

    <!-- 打印用的表格（仅在打印时显示） -->
    <div v-if="filteredQuestions.length > 0" class="print-only">
      <table class="print-table">
        <thead>
          <tr>
            <th class="w-title">题目</th>
            <th class="w-strategy">核心策略</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="question in filteredQuestions" :key="question.questionId">
            <td>
              <span v-if="question.isImportant" class="important-mark">⭐</span>
              {{ question.title }}
            </td>
            <td>{{ question.coreStrategy || '暂无' }}</td>
          </tr>
        </tbody>
      </table>
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
const showImportantOnly = ref(false)

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

        // 按重要性过滤
        if (showImportantOnly.value && !question.isImportant) {
          return
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
  showImportantOnly.value = false
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

/**
 * 难度CSS类名
 */
function difficultyClass(difficulty) {
  const classMap = {
    EASY: 'difficulty-easy',
    MEDIUM: 'difficulty-medium',
    HARD: 'difficulty-hard'
  }
  return classMap[difficulty] || ''
}

/**
 * 打印学习总结
 */
function printSummary() {
  window.print()
}

/**
 * 调整颜色亮度（用于创建渐变）
 */
function adjustColorBrightness(color, percent) {
  // 移除 # 号
  const num = parseInt(color.replace('#', ''), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) + amt
  const G = (num >> 8 & 0x00FF) + amt
  const B = (num & 0x0000FF) + amt
  return '#' + (
    0x1000000 +
    (R < 255 ? (R < 1 ? 0 : R) : 255) * 0x10000 +
    (G < 255 ? (G < 1 ? 0 : G) : 255) * 0x100 +
    (B < 255 ? (B < 1 ? 0 : B) : 255)
  ).toString(16).slice(1)
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

/* 屏幕显示时隐藏打印内容 */
.print-only {
  display: none;
}

/* 打印时的样式 */
@media print {
  /* 隐藏屏幕显示的内容 */
  .no-print,
  .screen-only {
    display: none !important;
  }

  /* 显示打印内容 */
  .print-only {
    display: block !important;
  }

  /* 打印页面设置 */
  @page {
    size: A4 portrait;
    margin: 8mm 6mm;
  }

  /* 全局打印样式 */
  body {
    print-color-adjust: exact;
    -webkit-print-color-adjust: exact;
  }

  /* 确保内容可以跨页 */
  .print-only {
    page-break-after: auto;
  }

  /* 打印表格样式 - 紧凑模式 */
  .print-table {
    width: 100%;
    border-collapse: collapse;
    font-size: 7pt; /* 小字体以容纳更多内容 */
    line-height: 1.2;
  }

  .print-table th {
    background-color: #f3f4f6;
    border: 1px solid #d1d5db;
    padding: 2px 3px;
    text-align: left;
    font-weight: 600;
    font-size: 7pt;
  }

  .print-table td {
    border: 1px solid #e5e7eb;
    padding: 2px 3px;
    font-size: 6pt; /* 内容字体更小 */
    vertical-align: top;
  }

  /* 难度标签 */
  .difficulty-badge {
    display: inline-block;
    padding: 1px 3px;
    border-radius: 2px;
    font-size: 5pt;
    font-weight: 500;
  }

  .difficulty-easy {
    background-color: #d1fae5;
    color: #065f46;
  }

  .difficulty-medium {
    background-color: #fef3c7;
    color: #92400e;
  }

  .difficulty-hard {
    background-color: #fee2e2;
    color: #991b1b;
  }

  /* 重要标记 */
  .important-mark {
    color: #f97316;
    font-size: 7pt;
  }

  /* 尽量避免表格行跨页，但允许必要时跨页 */
  .print-table tr {
    page-break-inside: auto;
  }

  /* 表头在每页重复 */
  .print-table thead {
    display: table-header-group;
  }

  .print-table tbody {
    page-break-before: auto;
    page-break-after: auto;
  }

  /* 列宽控制（紧凑模式） */
  .print-table th.w-title,
  .print-table td:nth-child(1) {
    width: 30%;
  }

  .print-table th.w-strategy,
  .print-table td:nth-child(2) {
    width: 70%;
  }

  /* 标题样式 */
  .print-only h1 {
    font-size: 12pt;
    margin-bottom: 3px;
  }

  .print-only p {
    font-size: 8pt;
    margin-bottom: 4px;
  }
}
</style>
