<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <!-- 顶部导航栏 -->
    <div class="bg-white border-b border-gray-200 px-6 py-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <h1 class="text-2xl font-bold text-gray-900">📚 试题库</h1>
          <span class="text-sm text-gray-500">职业技能试题练习</span>
        </div>
      </div>
    </div>

    <!-- 查询过滤器 -->
    <div class="bg-white border-b border-gray-200 px-6 py-4">
      <div class="flex flex-wrap gap-4 items-end">
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
        <div class="w-48">
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
        <div class="w-48">
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

        <!-- 试题类型 -->
        <div class="w-40">
          <label class="block text-sm font-medium text-gray-700 mb-1">试题类型</label>
          <select
            v-model="filters.questionType"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option :value="null">全部类型</option>
            <option value="behavioral">Behavioral</option>
            <option value="technical">Technical</option>
            <option value="design">Design</option>
            <option value="programming">Programming</option>
          </select>
        </div>

        <!-- 难度 -->
        <div class="w-32">
          <label class="block text-sm font-medium text-gray-700 mb-1">难度</label>
          <select
            v-model="filters.difficulty"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option :value="null">全部</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>

        <!-- 操作按钮 -->
        <div class="flex gap-2">
          <button
            @click="applyFilters"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
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
    </div>

    <!-- 两栏布局：试题列表 + 答题区域 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：试题列表 -->
      <div class="w-96 bg-white border-r border-gray-200 flex flex-col">
        <!-- 列表头部 -->
        <div class="px-4 py-3 border-b border-gray-200 bg-gray-50">
          <div class="flex items-center justify-between">
            <h3 class="text-sm font-semibold text-gray-700">
              试题列表
              <span v-if="questions.length > 0" class="ml-2 text-gray-500">
                ({{ questions.length }} 道题)
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

              <h4 class="text-sm font-medium text-gray-900 mt-2 line-clamp-2">
                {{ question.title }}
              </h4>

              <p v-if="question.questionDescription" class="text-xs text-gray-500 mt-1 line-clamp-1">
                {{ question.questionDescription }}
              </p>

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
      </div>

      <!-- 右侧：答题/批改区域 -->
      <div class="flex-1 flex flex-col bg-gray-50">
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
                <p v-if="selectedQuestion.questionDescription" class="text-sm text-gray-600 mt-2">
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
                @save="handleSaveAnswer"
              />
            </div>

            <!-- 批改模式 -->
            <div v-else>
              <ReviewMode
                :question="selectedQuestion"
                :current-answer="currentAnswer"
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
import { careerPathApi } from '@/api/careerPathApi'
import { skillApi } from '@/api/skillApi'
import { majorCategoryApi } from '@/api/majorCategoryApi'
import { focusAreaApi } from '@/api/focusAreaApi'
import AnswerMode from '@/components/question-bank/AnswerMode.vue'
import ReviewMode from '@/components/question-bank/ReviewMode.vue'

// 数据加载状态
const loading = ref(false)

// 查询过滤器
const filters = reactive({
  keyword: '',
  careerPathId: null,
  skillId: null,
  majorCategoryId: null,
  focusAreaId: null,
  questionType: null,
  difficulty: null
})

// 级联选择数据
const careerPaths = ref([])
const skills = ref([])
const majorCategories = ref([])
const focusAreas = ref([])

// 试题列表和选中的试题
const questions = ref([])
const selectedQuestion = ref(null)

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
    const data = await careerPathApi.getAll()
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

  if (filters.careerPathId) {
    await loadSkills()
  }
}

// 加载技能
const loadSkills = async () => {
  try {
    const data = await skillApi.getSkillsByCareerPath(filters.careerPathId)
    skills.value = data
  } catch (error) {
    console.error('Failed to load skills:', error)
  }
}

// 技能改变
const onSkillChange = async () => {
  filters.majorCategoryId = null
  filters.focusAreaId = null
  majorCategories.value = []
  focusAreas.value = []

  if (filters.skillId) {
    await loadMajorCategories()
  }
}

// 加载大分类
const loadMajorCategories = async () => {
  try {
    const data = await majorCategoryApi.getCategoriesBySkill(filters.skillId)
    majorCategories.value = data
  } catch (error) {
    console.error('Failed to load major categories:', error)
  }
}

// 大分类改变
const onMajorCategoryChange = async () => {
  filters.focusAreaId = null
  focusAreas.value = []

  if (filters.majorCategoryId) {
    await loadFocusAreas()
  }
}

// 加载Focus Area
const loadFocusAreas = async () => {
  try {
    const data = await focusAreaApi.getFocusAreasByMajorCategory(filters.majorCategoryId)
    focusAreas.value = data
  } catch (error) {
    console.error('Failed to load focus areas:', error)
  }
}

// 应用过滤器搜索
const applyFilters = async () => {
  loading.value = true
  try {
    const params = {
      keyword: filters.keyword || null,
      careerPathId: filters.careerPathId,
      skillId: filters.skillId,
      majorCategoryId: filters.majorCategoryId,
      focusAreaId: filters.focusAreaId,
      questionType: filters.questionType,
      difficulty: filters.difficulty
    }

    // 移除null值
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === '') {
        delete params[key]
      }
    })

    const data = await questionApi.searchQuestions(params)
    questions.value = data
  } catch (error) {
    console.error('Failed to search questions:', error)
  } finally {
    loading.value = false
  }
}

// 重置过滤器
const resetFilters = () => {
  filters.keyword = ''
  filters.careerPathId = null
  filters.skillId = null
  filters.majorCategoryId = null
  filters.focusAreaId = null
  filters.questionType = null
  filters.difficulty = null

  skills.value = []
  majorCategories.value = []
  focusAreas.value = []
  questions.value = []
  selectedQuestion.value = null
}

// 选择试题
const selectQuestion = async (question) => {
  selectedQuestion.value = question
  currentMode.value = 'answer'
  currentAnswer.value = ''

  // 加载试题详情（包含答题模板）
  try {
    const details = await questionApi.getQuestionById(question.id)
    selectedQuestion.value = details

    // 如果有用户笔记，加载笔记内容
    if (details.userNote) {
      currentAnswer.value = details.userNote.noteContent || ''
    }

    // 加载答题模板（如果是behavioral类型）
    // TODO: 实现模板加载逻辑
  } catch (error) {
    console.error('Failed to load question details:', error)
  }
}

// 保存答案
const handleSaveAnswer = async (answerData) => {
  try {
    await questionApi.saveOrUpdateNote(selectedQuestion.value.id, {
      noteContent: answerData.content,
      coreStrategy: answerData.coreStrategy || ''
    })

    // 更新当前答案
    currentAnswer.value = answerData.content

    // 标记该试题已答题
    const questionIndex = questions.value.findIndex(q => q.id === selectedQuestion.value.id)
    if (questionIndex !== -1) {
      questions.value[questionIndex].hasUserNote = true
    }

    alert('答案保存成功！')
  } catch (error) {
    console.error('Failed to save answer:', error)
    alert('保存失败，请重试')
  }
}

// 覆盖之前的答案
const handleOverwriteAnswer = async (newAnswer) => {
  await handleSaveAnswer(newAnswer)
  currentMode.value = 'answer'
}
</script>
