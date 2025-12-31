<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <!-- 顶部导航栏 -->
    <div class="bg-white border-b border-gray-200 px-4 md:px-6 py-3 md:py-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2 md:gap-3">
          <h1 class="text-xl md:text-2xl font-bold text-gray-900">📚 试题库</h1>
          <span class="text-xs md:text-sm text-gray-500 hidden sm:inline">职业技能试题练习</span>
        </div>
      </div>
    </div>

    <!-- 查询过滤器 -->
    <div v-show="showQuestionList" class="bg-white border-b border-gray-200 px-4 md:px-6 py-3 md:py-4">
      <div class="flex flex-col md:flex-row md:flex-wrap gap-3 md:gap-4 items-stretch md:items-end">
        <!-- 关键字搜索 -->
        <div class="flex-1 min-w-[200px]">
          <label class="block text-sm font-medium text-gray-700 mb-1">关键字搜索</label>
          <input
            v-model="filters.keyword"
            type="text"
            placeholder="搜索试题标题或描述..."
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            @keyup.enter="applyFilters"
          />
        </div>

        <!-- 职业路径选择 -->
        <div class="w-full md:w-48">
          <label class="block text-sm font-medium text-gray-700 mb-1">职业路径</label>
          <select
            v-model="filters.careerPathId"
            @change="onCareerPathChange"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option :value="null">全部职业</option>
            <option v-for="cp in careerPaths" :key="cp.id" :value="cp.id">
              {{ cp.name }}
            </option>
          </select>
        </div>

        <!-- 技能选择 -->
        <div class="w-full md:w-48">
          <label class="block text-sm font-medium text-gray-700 mb-1">技能</label>
          <select
            v-model="filters.skillId"
            @change="onSkillChange"
            :disabled="!filters.careerPathId"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            <option :value="null">全部技能</option>
            <option v-for="skill in skills" :key="skill.id" :value="skill.id">
              {{ skill.name }}
            </option>
          </select>
        </div>

        <!-- 大分类选择 -->
        <div class="w-48">
          <label class="block text-sm font-medium text-gray-700 mb-1">大分类</label>
          <select
            v-model="filters.majorCategoryId"
            @change="onMajorCategoryChange"
            :disabled="!filters.skillId"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            <option :value="null">全部分类</option>
            <option v-for="cat in majorCategories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>
        </div>

        <!-- Focus Area选择 -->
        <div class="w-48">
          <label class="block text-sm font-medium text-gray-700 mb-1">Focus Area</label>
          <select
            v-model="filters.focusAreaId"
            :disabled="!filters.majorCategoryId"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            <option :value="null">全部Focus Area</option>
            <option v-for="fa in focusAreas" :key="fa.id" :value="fa.id">
              {{ fa.name }}
            </option>
          </select>
        </div>

        <!-- 试题类型（多选） -->
        <div class="w-56">
          <label class="block text-sm font-medium text-gray-700 mb-1">试题类型</label>
          <div class="flex flex-wrap gap-2 p-2 border border-gray-300 rounded-lg bg-white">
            <label v-for="type in questionTypes" :key="type.value" class="inline-flex items-center">
              <input
                type="checkbox"
                :value="type.value"
                v-model="filters.questionTypes"
                class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <span class="ml-1.5 text-sm text-gray-700">{{ type.label }}</span>
            </label>
          </div>
        </div>

        <!-- 难度（多选） -->
        <div class="w-48">
          <label class="block text-sm font-medium text-gray-700 mb-1">难度</label>
          <div class="flex gap-2 p-2 border border-gray-300 rounded-lg bg-white">
            <label v-for="diff in difficulties" :key="diff.value" class="inline-flex items-center">
              <input
                type="checkbox"
                :value="diff.value"
                v-model="filters.difficulties"
                class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <span class="ml-1.5 text-sm text-gray-700">{{ diff.label }}</span>
            </label>
          </div>
        </div>

        <!-- 是否重点题 -->
        <div class="w-32">
          <label class="block text-sm font-medium text-gray-700 mb-1">重点题</label>
          <div class="flex items-center h-10">
            <label class="inline-flex items-center cursor-pointer">
              <input
                type="checkbox"
                v-model="filters.isPriorityOnly"
                class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <span class="ml-2 text-sm text-gray-700">只显示重点</span>
            </label>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="flex gap-2">
          <button
            @click="applyFilters()"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
          >
            🔍 搜索
          </button>
          <button
            @click="resetFilters"
            class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
          >
            🔄 重置
          </button>
        </div>
      </div>

      <!-- 搜索提示 -->
      <div v-if="!loading && questions.length === 0 && filters.skillId" class="px-6 py-2 bg-blue-50 border-t border-blue-100">
        <p class="text-sm text-blue-700">
          💡 提示：选择筛选条件后，点击 <span class="font-semibold">🔍 搜索</span> 按钮查看试题
        </p>
      </div>
    </div>

    <!-- 两栏布局：试题列表 + 答题区域 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：试题列表 -->
      <div v-show="showQuestionList" class="w-96 bg-white border-r border-gray-200 flex flex-col">
        <!-- 列表头部 -->
        <div class="px-4 py-3 border-b border-gray-200 bg-gray-50">
          <div class="flex items-center justify-between">
            <h3 class="text-sm font-semibold text-gray-700">
              试题列表
              <span v-if="pagination.totalElements > 0" class="ml-2 text-gray-500">
                (共 {{ pagination.totalElements }} 道题)
              </span>
            </h3>
          </div>
        </div>

        <!-- 试题列表 -->
        <div class="flex-1 overflow-y-auto">
          <div v-if="loading" class="flex items-center justify-center h-full">
            <div class="text-center text-gray-400">
              <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-3"></div>
              <p class="text-sm">加载中...</p>
            </div>
          </div>

          <div v-else-if="questions.length === 0" class="flex items-center justify-center h-full">
            <div class="text-center text-gray-400">
              <svg class="w-16 h-16 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p class="text-sm">未找到符合条件的试题</p>
              <p class="text-xs mt-2">尝试调整筛选条件</p>
            </div>
          </div>

          <div v-else class="divide-y divide-gray-100">
            <div
              v-for="question in questions"
              :key="question.id"
              @click="selectQuestion(question)"
              :class="[
                'px-4 py-3 cursor-pointer transition-all hover:bg-blue-50',
                selectedQuestion?.id === question.id ? 'bg-blue-100 border-l-4 border-blue-600' : ''
              ]"
            >
              <div class="flex items-start justify-between gap-2">
                <div class="flex items-start gap-2">
                  <!-- 试题类型标签 -->
                  <span :class="[
                    'px-2 py-0.5 rounded text-xs font-medium flex-shrink-0',
                    question.questionType === 'behavioral' ? 'bg-purple-100 text-purple-700' :
                    question.questionType === 'technical' ? 'bg-blue-100 text-blue-700' :
                    question.questionType === 'design' ? 'bg-green-100 text-green-700' :
                    'bg-orange-100 text-orange-700'
                  ]">
                    {{ question.questionType }}
                  </span>

                  <!-- 难度标签 -->
                  <span :class="[
                    'px-2 py-0.5 rounded text-xs font-medium flex-shrink-0',
                    question.difficulty === 'EASY' ? 'bg-green-100 text-green-700' :
                    question.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' :
                    'bg-red-100 text-red-700'
                  ]">
                    {{ question.difficulty }}
                  </span>
                </div>

                <!-- 重点题标记按钮 -->
                <button
                  @click.stop="togglePriority(question)"
                  :class="[
                    'p-1 rounded hover:bg-gray-200 transition-colors',
                    question.isPriority ? 'text-yellow-500' : 'text-gray-400'
                  ]"
                  :title="question.isPriority ? '取消重点标记' : '标记为重点题'"
                >
                  <svg class="w-4 h-4" :fill="question.isPriority ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                  </svg>
                </button>
              </div>

              <h4 class="text-sm font-medium text-gray-900 mt-2 line-clamp-2">
                {{ question.title }}
              </h4>

              <!-- 用户笔记状态 -->
              <div v-if="question.hasUserNote" class="mt-2 flex items-center gap-1 text-xs text-green-600">
                <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
                  <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd" />
                </svg>
                已答题
              </div>
            </div>
          </div>
        </div>

        <!-- 分页控件 -->
        <div v-if="pagination.totalPages > 1" class="px-4 py-3 border-t border-gray-200 bg-white">
          <div class="flex items-center justify-between text-sm">
            <div class="text-gray-600">
              第 {{ pagination.currentPage + 1 }} / {{ pagination.totalPages }} 页
            </div>
            <div class="flex gap-2">
              <button
                @click="previousPage()"
                :disabled="!pagination.hasPrevious"
                :class="[
                  'px-3 py-1 rounded transition-colors',
                  pagination.hasPrevious
                    ? 'bg-blue-600 text-white hover:bg-blue-700'
                    : 'bg-gray-200 text-gray-400 cursor-not-allowed'
                ]"
              >
                上一页
              </button>
              <button
                @click="nextPage()"
                :disabled="!pagination.hasNext"
                :class="[
                  'px-3 py-1 rounded transition-colors',
                  pagination.hasNext
                    ? 'bg-blue-600 text-white hover:bg-blue-700'
                    : 'bg-gray-200 text-gray-400 cursor-not-allowed'
                ]"
              >
                下一页
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：答题/批改区域 -->
      <div class="flex-1 flex flex-col bg-gray-50 relative">
        <!-- 切换左侧面板按钮 -->
        <div v-if="selectedQuestion" class="absolute top-4 left-4 z-10">
          <button
            @click="showQuestionList = !showQuestionList"
            class="p-2 bg-white border border-gray-300 rounded-lg shadow-md hover:bg-gray-50 transition-colors"
            :title="showQuestionList ? '隐藏试题列表' : '显示试题列表'"
          >
            <svg v-if="showQuestionList" class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
            </svg>
            <svg v-else class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 5l7 7-7 7M5 5l7 7-7 7" />
            </svg>
          </button>
        </div>

        <div v-if="!selectedQuestion" class="flex items-center justify-center h-full">
          <div class="text-center text-gray-400">
            <svg class="w-20 h-20 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="text-lg font-medium">请从左侧选择一道试题</p>
            <p class="text-sm mt-2">选择后即可开始答题或查看参考答案</p>
          </div>
        </div>

        <!-- 试题详情和答题区 -->
        <div v-else class="flex-1 flex flex-col overflow-hidden">
          <!-- 试题信息头部 -->
          <div class="bg-white border-b border-gray-200 px-6 py-4">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <span :class="[
                    'px-2 py-1 rounded text-sm font-medium',
                    selectedQuestion.questionType === 'behavioral' ? 'bg-purple-100 text-purple-700' :
                    selectedQuestion.questionType === 'technical' ? 'bg-blue-100 text-blue-700' :
                    selectedQuestion.questionType === 'design' ? 'bg-green-100 text-green-700' :
                    'bg-orange-100 text-orange-700'
                  ]">
                    {{ selectedQuestion.questionType }}
                  </span>
                  <span :class="[
                    'px-2 py-1 rounded text-sm font-medium',
                    selectedQuestion.difficulty === 'EASY' ? 'bg-green-100 text-green-700' :
                    selectedQuestion.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' :
                    'bg-red-100 text-red-700'
                  ]">
                    {{ selectedQuestion.difficulty }}
                  </span>
                </div>
                <h2 class="text-xl font-bold text-gray-900">{{ selectedQuestion.title }}</h2>
                <!-- 非编程题才显示描述，编程题描述在左栏 -->
                <p v-if="selectedQuestion.questionDescription && selectedQuestion.questionType !== 'programming'" class="text-sm text-gray-600 mt-2">
                  {{ selectedQuestion.questionDescription }}
                </p>
              </div>
            </div>
          </div>

          <!-- 模式切换标签页 -->
          <div class="bg-white border-b border-gray-200 px-6">
            <div class="flex gap-4">
              <button
                @click="currentMode = 'answer'"
                :class="[
                  'px-4 py-3 text-sm font-medium border-b-2 transition-colors',
                  currentMode === 'answer'
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700'
                ]"
              >
                ✍️ 答题模式
              </button>
              <button
                @click="currentMode = 'review'"
                :class="[
                  'px-4 py-3 text-sm font-medium border-b-2 transition-colors',
                  currentMode === 'review'
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700'
                ]"
              >
                📝 批改模式
              </button>
            </div>
          </div>

          <!-- 答题/批改内容区域 -->
          <div class="flex-1 overflow-y-auto p-6">
            <!-- 答题模式 -->
            <div v-if="currentMode === 'answer'">
              <AnswerMode
                :question="selectedQuestion"
                :template="currentTemplate"
                @goToReview="handleGoToReview"
              />
            </div>

            <!-- 批改模式 -->
            <div v-else>
              <ReviewMode
                :question="selectedQuestion"
                :current-answer="currentAnswer"
                :template="currentTemplate"
                @overwrite="handleOverwriteAnswer"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { questionApi } from '@/api/questionApi'
import careerPathApi from '@/api/careerPathApi'
import skillApi from '@/api/skillApi'
import majorCategoryApi from '@/api/majorCategoryApi'
import focusAreaApi from '@/api/focusAreaApi'
import AnswerMode from '@/components/question-bank/AnswerMode.vue'
import ReviewMode from '@/components/question-bank/ReviewMode.vue'

// 数据加载状态
const loading = ref(false)

// 试题类型选项
const questionTypes = [
  { value: 'behavioral', label: 'Behavioral' },
  { value: 'technical', label: 'Technical' },
  { value: 'design', label: 'Design' },
  { value: 'programming', label: 'Programming' }
]

// 难度选项
const difficulties = [
  { value: 'EASY', label: 'Easy' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'HARD', label: 'Hard' }
]

// 查询过滤器
const filters = reactive({
  keyword: '',
  careerPathId: null,
  skillId: null,
  majorCategoryId: null,
  focusAreaId: null,
  questionTypes: [],  // 多选数组
  difficulties: [],   // 多选数组
  isPriorityOnly: false  // 是否只显示重点题
})

// 分页数据
const pagination = reactive({
  currentPage: 0,
  pageSize: 20,
  totalElements: 0,
  totalPages: 0,
  hasNext: false,
  hasPrevious: false
})

// 级联选择数据
const careerPaths = ref([])
const skills = ref([])
const majorCategories = ref([])
const focusAreas = ref([])

// 前端缓存：避免重复API调用
const skillsCache = new Map()  // careerPathId -> skills
const majorCategoriesCache = new Map()  // skillId -> majorCategories
const focusAreasCache = new Map()  // skillId -> focusAreas

// 试题列表和选中的试题
const questions = ref([])
const selectedQuestion = ref(null)

// 显示/隐藏试题列表
const showQuestionList = ref(true)

// 当前模式：answer | review
const currentMode = ref('answer')

// 当前答题内容
const currentAnswer = ref('')

// 当前答题模板
const currentTemplate = ref(null)

// 初始化加载数据
onMounted(async () => {
  await loadCareerPaths()
})

// 加载职业路径
const loadCareerPaths = async () => {
  try {
    const data = await careerPathApi.getCareerPaths()
    careerPaths.value = data
  } catch (error) {
    console.error('Failed to load career paths:', error)
  }
}

// 职业路径改变
const onCareerPathChange = async () => {
  filters.skillId = null
  filters.majorCategoryId = null
  filters.focusAreaId = null
  skills.value = []
  majorCategories.value = []
  focusAreas.value = []
  questions.value = []  // 清空试题列表，提示用户需要点击"搜索"

  if (filters.careerPathId) {
    await loadSkills()
  }
  // 不再自动触发查询，让用户点击"搜索"按钮
}

// 加载技能（带缓存）
const loadSkills = async () => {
  // 检查缓存
  if (skillsCache.has(filters.careerPathId)) {
    skills.value = skillsCache.get(filters.careerPathId)
    return
  }

  try {
    const data = await skillApi.getSkillsByCareerPath(filters.careerPathId)
    skills.value = data
    // 存入缓存
    skillsCache.set(filters.careerPathId, data)
  } catch (error) {
    console.error('Failed to load skills:', error)
  }
}

// 技能改变（并行加载大分类和Focus Area）
const onSkillChange = async () => {
  filters.majorCategoryId = null
  filters.focusAreaId = null
  majorCategories.value = []
  focusAreas.value = []
  questions.value = []  // 清空试题列表，提示用户需要点击"搜索"

  if (filters.skillId) {
    // 🚀 关键优化：并行加载大分类和Focus Area，而不是串行
    // 这样用户选择技能后，两个下拉框会同时填充，无需等待
    await Promise.all([
      loadMajorCategories(),
      loadAllFocusAreasForSkill()  // 预加载所有Focus Area
    ])
  }
  // 不再自动触发查询，让用户点击"搜索"按钮
}

// 加载大分类（带缓存）
const loadMajorCategories = async () => {
  // 检查缓存
  if (majorCategoriesCache.has(filters.skillId)) {
    majorCategories.value = majorCategoriesCache.get(filters.skillId)
    return
  }

  try {
    const data = await majorCategoryApi.getAllMajorCategories(filters.skillId)
    majorCategories.value = data
    // 存入缓存
    majorCategoriesCache.set(filters.skillId, data)
  } catch (error) {
    console.error('Failed to load major categories:', error)
  }
}

// 大分类改变
const onMajorCategoryChange = async () => {
  filters.focusAreaId = null
  focusAreas.value = []
  questions.value = []  // 清空试题列表，提示用户需要点击"搜索"

  if (filters.majorCategoryId) {
    await loadFocusAreas()
  }
  // 不再自动触发查询，让用户点击"搜索"按钮
}

// 预加载技能下的所有Focus Area（不过滤，用于缓存）
const loadAllFocusAreasForSkill = async () => {
  // 检查缓存
  if (focusAreasCache.has(filters.skillId)) {
    return // 已缓存，直接返回
  }

  try {
    // 使用getFocusAreasWithCategories获取skillId下的所有Focus Areas
    const allFocusAreas = await majorCategoryApi.getFocusAreasWithCategories(filters.skillId)
    // 存入缓存
    focusAreasCache.set(filters.skillId, allFocusAreas)
  } catch (error) {
    console.error('Failed to load focus areas:', error)
  }
}

// 加载Focus Area（带缓存 + 过滤）
const loadFocusAreas = async () => {
  // 检查缓存
  let allFocusAreas
  if (focusAreasCache.has(filters.skillId)) {
    allFocusAreas = focusAreasCache.get(filters.skillId)
  } else {
    try {
      // 使用getFocusAreasWithCategories获取skillId下的所有Focus Areas
      allFocusAreas = await majorCategoryApi.getFocusAreasWithCategories(filters.skillId)
      // 存入缓存
      focusAreasCache.set(filters.skillId, allFocusAreas)
    } catch (error) {
      console.error('Failed to load focus areas:', error)
      return
    }
  }

  // 根据majorCategoryId过滤
  if (filters.majorCategoryId) {
    focusAreas.value = allFocusAreas.filter(fa =>
      fa.categoryIds && fa.categoryIds.includes(filters.majorCategoryId)
    )
  } else {
    focusAreas.value = allFocusAreas
  }
}

// 应用过滤器搜索
const applyFilters = async (page = 0) => {
  loading.value = true
  try {
    const params = {
      keyword: filters.keyword || null,
      careerPathId: filters.careerPathId,
      skillId: filters.skillId,
      majorCategoryId: filters.majorCategoryId,
      focusAreaId: filters.focusAreaId,
      questionTypes: filters.questionTypes.length > 0 ? filters.questionTypes : null,  // 多选数组
      difficulties: filters.difficulties.length > 0 ? filters.difficulties : null,    // 多选数组
      isPriorityOnly: filters.isPriorityOnly || null,  // 是否只显示重点题
      page: page,
      size: pagination.pageSize
    }

    // 移除null值和空数组
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === '' || (Array.isArray(params[key]) && params[key].length === 0)) {
        delete params[key]
      }
    })

    const result = await questionApi.searchQuestions(params)

    // 更新试题列表和分页信息
    questions.value = result.content || []
    pagination.currentPage = result.currentPage || 0
    pagination.totalElements = result.totalElements || 0
    pagination.totalPages = result.totalPages || 0
    pagination.hasNext = result.hasNext || false
    pagination.hasPrevious = result.hasPrevious || false
  } catch (error) {
    console.error('Failed to search questions:', error)
  } finally {
    loading.value = false
  }
}

// 分页操作
const goToPage = (page) => {
  if (page >= 0 && page < pagination.totalPages) {
    applyFilters(page)
  }
}

const nextPage = () => {
  if (pagination.hasNext) {
    applyFilters(pagination.currentPage + 1)
  }
}

const previousPage = () => {
  if (pagination.hasPrevious) {
    applyFilters(pagination.currentPage - 1)
  }
}

// 重置过滤器
const resetFilters = () => {
  filters.keyword = ''
  filters.careerPathId = null
  filters.skillId = null
  filters.majorCategoryId = null
  filters.focusAreaId = null
  filters.questionTypes = []  // 清空多选数组
  filters.difficulties = []   // 清空多选数组
  filters.isPriorityOnly = false  // 重置重点题过滤

  skills.value = []
  majorCategories.value = []
  focusAreas.value = []
  questions.value = []
  selectedQuestion.value = null

  // 注意：不清空缓存，保留已加载的数据供后续使用
  // 如果需要强制刷新，取消注释以下代码：
  // skillsCache.clear()
  // majorCategoriesCache.clear()
  // focusAreasCache.clear()
}

// 选择试题
const selectQuestion = async (question) => {
  selectedQuestion.value = question
  currentMode.value = 'answer'
  currentAnswer.value = ''
  currentTemplate.value = null

  // 加载试题详情（包含答题模板）
  try {
    const details = await questionApi.getQuestionById(question.id)
    selectedQuestion.value = details

    // 如果有用户笔记，加载笔记内容
    if (details.userNote) {
      currentAnswer.value = details.userNote.noteContent || ''
    }

    // 加载答题模板（从试题详情中）
    if (details.answerTemplate) {
      currentTemplate.value = details.answerTemplate
    }
  } catch (error) {
    console.error('Failed to load question details:', error)
  }
}

// 转到批改模式
const handleGoToReview = (answerData) => {
  // 保存当前答案内容到临时变量
  currentAnswer.value = answerData.content

  // 切换到批改模式
  currentMode.value = 'review'
}

// 保存答案（从批改模式）
const handleOverwriteAnswer = async (answerData) => {
  try {
    await questionApi.saveOrUpdateNote(selectedQuestion.value.id, {
      noteContent: answerData.content,
      coreStrategy: answerData.coreStrategy || ''
    })

    // 更新试题的用户笔记
    if (selectedQuestion.value.userNote) {
      selectedQuestion.value.userNote.noteContent = answerData.content
      selectedQuestion.value.userNote.coreStrategy = answerData.coreStrategy || ''
    } else {
      selectedQuestion.value.userNote = {
        noteContent: answerData.content,
        coreStrategy: answerData.coreStrategy || ''
      }
    }

    // 标记该试题已答题
    const questionIndex = questions.value.findIndex(q => q.id === selectedQuestion.value.id)
    if (questionIndex !== -1) {
      questions.value[questionIndex].hasUserNote = true
    }

    alert('答案保存成功！')
    currentMode.value = 'answer'
  } catch (error) {
    console.error('Failed to save answer:', error)
    alert('保存失败，请重试')
  }
}

// 切换重点题标记
const togglePriority = async (question) => {
  try {
    const updatedNote = await questionApi.togglePriority(question.id)

    // 更新列表中的试题状态（QuestionListDTO直接有isPriority和hasUserNote字段）
    const questionIndex = questions.value.findIndex(q => q.id === question.id)
    if (questionIndex !== -1) {
      questions.value[questionIndex].isPriority = updatedNote.isPriority
      questions.value[questionIndex].hasUserNote = true
    }

    // 如果当前选中的试题就是这道题，也更新选中试题的数据
    if (selectedQuestion.value && selectedQuestion.value.id === question.id) {
      if (selectedQuestion.value.userNote) {
        selectedQuestion.value.userNote.isPriority = updatedNote.isPriority
      } else {
        selectedQuestion.value.userNote = updatedNote
      }
    }
  } catch (error) {
    console.error('Failed to toggle priority:', error)
    alert('操作失败，请重试')
  }
}
</script>
