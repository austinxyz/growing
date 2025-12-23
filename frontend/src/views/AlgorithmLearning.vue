<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <!-- 顶部标题栏 -->
    <div class="bg-white border-b border-gray-200">
      <div class="px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">算法学习</h1>
        <p class="text-sm text-gray-600 mt-1">浏览算法与数据结构学习内容</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：大分类 + Focus Area树 -->
      <div class="w-96 bg-white border-r border-gray-200 flex flex-col">
        <!-- 大分类Tab -->
        <div class="border-b border-gray-200">
          <div class="flex overflow-x-auto">
            <button
              v-for="category in majorCategories"
              :key="category.id"
              @click="selectCategory(category.id)"
              :class="[
                'flex-shrink-0 px-4 py-3 text-sm font-medium border-b-2 transition-colors',
                selectedCategoryId === category.id
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              {{ category.name }}
            </button>
          </div>
        </div>

        <!-- Focus Area列表 -->
        <div class="flex-1 overflow-y-auto p-4">
          <div v-if="loading.focusAreas" class="text-center text-gray-500 py-8">
            加载中...
          </div>

          <div v-else-if="filteredFocusAreas.length === 0" class="text-center text-gray-400 py-8">
            该分类下暂无学习主题
          </div>

          <div v-else class="space-y-1">
            <button
              v-for="fa in filteredFocusAreas"
              :key="fa.id"
              @click="selectFocusArea(fa)"
              :class="[
                'w-full text-left px-4 py-3 rounded-md transition-colors',
                selectedFocusArea?.id === fa.id
                  ? 'bg-blue-50 text-blue-700 font-medium border-l-4 border-blue-600'
                  : 'text-gray-700 hover:bg-gray-50'
              ]"
            >
              {{ fa.name }}
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧：学习阶段Tab + 内容列表 -->
      <div class="flex-1 overflow-y-auto bg-gray-50">
        <!-- 未选中Focus Area -->
        <div v-if="!selectedFocusArea" class="flex items-center justify-center h-full">
          <div class="text-center text-gray-400">
            <svg class="mx-auto h-16 w-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="mt-4 text-sm">请从左侧选择一个学习主题</p>
          </div>
        </div>

        <!-- 学习内容区域 -->
        <div v-else class="p-6">
          <div class="max-w-4xl mx-auto">
            <!-- Focus Area标题 -->
            <div class="mb-6">
              <h2 class="text-2xl font-bold text-gray-900">{{ selectedFocusArea.name }}</h2>
              <p v-if="selectedFocusArea.categoryName" class="text-sm text-gray-500 mt-1">
                {{ selectedFocusArea.categoryName }}
              </p>
            </div>

            <!-- 学习阶段Tab -->
            <div class="bg-white rounded-lg shadow mb-6">
              <div class="border-b border-gray-200">
                <div class="flex">
                  <button
                    v-for="stage in learningStages"
                    :key="stage.id"
                    @click="selectStage(stage.id)"
                    :class="[
                      'px-6 py-3 text-sm font-medium border-b-2 transition-colors',
                      selectedStageId === stage.id
                        ? 'border-blue-500 text-blue-600'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                    ]"
                  >
                    {{ stage.stageName }}
                    <span class="ml-2 text-xs text-gray-400">({{ stage.contents ? stage.contents.length : 0 }})</span>
                  </button>
                  <!-- 试题库Tab -->
                  <button
                    @click="selectStage('questions')"
                    :class="[
                      'px-6 py-3 text-sm font-medium border-b-2 transition-colors',
                      selectedStageId === 'questions'
                        ? 'border-blue-500 text-blue-600'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                    ]"
                  >
                    试题库
                    <span class="ml-2 text-xs text-gray-400">({{ questionCount }})</span>
                  </button>
                </div>
              </div>

              <!-- 阶段内容区域 -->
              <div class="p-6">
                <!-- Loading状态 -->
                <div v-if="loading.contents" class="text-center text-gray-500 py-12">
                  <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <p class="mt-2 text-sm">加载中...</p>
                </div>

                <!-- 学习内容列表 -->
                <div v-else-if="selectedStageId !== 'questions' && currentStageContents.length === 0" class="text-center text-gray-400 py-12">
                  该阶段暂无学习内容
                </div>

                <div v-else-if="selectedStageId !== 'questions'" class="space-y-4">
                  <div
                    v-for="(content, index) in currentStageContents"
                    :key="content.id"
                    class="border border-gray-200 rounded-lg p-5 bg-white hover:shadow-md transition-shadow"
                  >
                    <!-- 内容标题 -->
                    <div class="flex items-start justify-between mb-3">
                      <h3 class="text-base font-semibold text-gray-900 flex-1">
                        {{ index + 1 }}. {{ content.title }}
                      </h3>
                      <span v-if="content.author" class="text-xs text-gray-500 ml-2">
                        作者: {{ content.author }}
                      </span>
                    </div>

                    <!-- 描述 -->
                    <p v-if="content.description" class="text-sm text-gray-600 mb-3">
                      {{ content.description }}
                    </p>

                    <!-- 链接 -->
                    <div v-if="content.url" class="flex items-center space-x-2">
                      <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                      </svg>
                      <a
                        :href="content.url"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="text-sm text-blue-600 hover:text-blue-800 hover:underline"
                      >
                        查看详情
                      </a>
                    </div>
                  </div>
                </div>

                <!-- 试题库列表 -->
                <div v-else>
                  <div v-if="questions.length === 0" class="text-center text-gray-400 py-12">
                    该Focus Area暂无试题
                  </div>

                  <div v-else class="space-y-2">
                    <div
                      v-for="question in questions"
                      :key="question.id"
                      @click="openQuestionModal(question)"
                      class="border border-gray-200 rounded-lg p-3 bg-white hover:shadow-md transition-shadow cursor-pointer"
                    >
                      <!-- 紧凑显示：LeetCode链接(题目) + 难度图标 + 核心思路 -->
                      <div class="flex items-start gap-3">
                        <!-- 题目标题（作为LeetCode链接） -->
                        <a
                          v-if="question.programmingDetails?.leetcodeUrl"
                          :href="question.programmingDetails.leetcodeUrl"
                          @click.stop
                          target="_blank"
                          rel="noopener noreferrer"
                          class="text-sm font-semibold text-blue-600 hover:text-blue-800 hover:underline w-72 flex-shrink-0"
                        >
                          {{ question.title }}
                        </a>
                        <h3 v-else class="text-sm font-semibold text-gray-900 w-72 flex-shrink-0">
                          {{ question.title }}
                        </h3>

                        <!-- 难度图标 -->
                        <span :class="getDifficultyIconClass(question.difficulty)" class="flex-shrink-0 mt-0.5" :title="question.difficulty">
                          {{ getDifficultyIcon(question.difficulty) }}
                        </span>

                        <!-- 核心思路（自动换行） -->
                        <p class="text-sm text-gray-600 flex-1 leading-relaxed">
                          {{ question.note?.coreStrategy || '暂无核心思路' }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 试题详情Modal -->
    <QuestionDetailModal
      v-if="showQuestionModal"
      :question="selectedQuestion"
      :focus-area-id="selectedFocusArea?.id"
      :focus-area-name="selectedFocusArea?.name"
      @close="closeQuestionModal"
      @note-saved="handleNoteSaved"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import majorCategoryApi from '@/api/majorCategoryApi'
import learningContentApi from '@/api/learningContentApi'
import { questionApi } from '@/api/questionApi'
import QuestionDetailModal from '@/components/questions/QuestionDetailModal.vue'

// 算法与数据结构的skillId（根据实际数据调整）
const ALGORITHM_SKILL_ID = 1

// State
const loading = ref({
  focusAreas: false,
  contents: false
})

const majorCategories = ref([])
const selectedCategoryId = ref(null)
const allFocusAreas = ref([])
const selectedFocusArea = ref(null)
const learningData = ref(null)
const selectedStageId = ref(null)
const questions = ref([])
const showQuestionModal = ref(false)
const selectedQuestion = ref(null)

// Computed
const filteredFocusAreas = computed(() => {
  if (!selectedCategoryId.value) return allFocusAreas.value
  return allFocusAreas.value.filter(fa =>
    fa.categoryIds && fa.categoryIds.includes(selectedCategoryId.value)
  )
})

const learningStages = computed(() => {
  return learningData.value?.stages || []
})

const currentStageContents = computed(() => {
  if (!selectedStageId.value || selectedStageId.value === 'questions') return []
  const stage = learningStages.value.find(s => s.id === selectedStageId.value)
  return stage?.contents || []
})

const questionCount = computed(() => questions.value.length)

// Methods
const loadMajorCategories = async () => {
  try {
    const data = await majorCategoryApi.getAllMajorCategories()
    majorCategories.value = data || []

    // 默认选中第一个分类
    if (majorCategories.value.length > 0) {
      selectedCategoryId.value = majorCategories.value[0].id
    }
  } catch (error) {
    console.error('加载大分类失败:', error)
    alert('加载大分类失败: ' + (error.message || '未知错误'))
  }
}

const loadFocusAreas = async () => {
  loading.value.focusAreas = true
  try {
    const data = await majorCategoryApi.getFocusAreasWithCategories(ALGORITHM_SKILL_ID)
    allFocusAreas.value = data || []
  } catch (error) {
    console.error('加载Focus Areas失败:', error)
    alert('加载学习主题失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value.focusAreas = false
  }
}

const selectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  // 清空选中的Focus Area
  selectedFocusArea.value = null
  learningData.value = null
  selectedStageId.value = null
  questions.value = []
}

const selectFocusArea = async (focusArea) => {
  selectedFocusArea.value = focusArea
  await loadLearningContents()
  await loadQuestions()

  // 默认选中第一个学习阶段
  if (learningStages.value.length > 0) {
    selectedStageId.value = learningStages.value[0].id
  }
}

const loadLearningContents = async () => {
  if (!selectedFocusArea.value) return

  loading.value.contents = true
  try {
    const data = await learningContentApi.getContentsByFocusArea(selectedFocusArea.value.id)
    learningData.value = data || { stages: [] }
  } catch (error) {
    console.error('加载学习内容失败:', error)
    alert('加载学习内容失败: ' + (error.message || '未知错误'))
    learningData.value = { stages: [] }
  } finally {
    loading.value.contents = false
  }
}

const loadQuestions = async () => {
  if (!selectedFocusArea.value) return

  try {
    const data = await questionApi.getQuestionsByFocusArea(selectedFocusArea.value.id)
    questions.value = data || []
  } catch (error) {
    console.error('加载试题失败:', error)
    questions.value = []
  }
}

const selectStage = (stageId) => {
  selectedStageId.value = stageId
}

const getDifficultyClass = (difficulty) => {
  const classes = {
    EASY: 'px-2 py-1 text-xs rounded-full bg-green-100 text-green-700',
    MEDIUM: 'px-2 py-1 text-xs rounded-full bg-yellow-100 text-yellow-700',
    HARD: 'px-2 py-1 text-xs rounded-full bg-red-100 text-red-700'
  }
  return classes[difficulty] || 'px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-700'
}

const getDifficultyIcon = (difficulty) => {
  const icons = {
    EASY: '●',
    MEDIUM: '●●',
    HARD: '●●●'
  }
  return icons[difficulty] || '●'
}

const getDifficultyIconClass = (difficulty) => {
  const classes = {
    EASY: 'text-green-500 text-base',
    MEDIUM: 'text-yellow-500 text-base',
    HARD: 'text-red-500 text-base'
  }
  return classes[difficulty] || 'text-gray-500 text-base'
}

const openQuestionModal = (question) => {
  selectedQuestion.value = question
  showQuestionModal.value = true
}

const closeQuestionModal = () => {
  showQuestionModal.value = false
  selectedQuestion.value = null
}

const handleNoteSaved = () => {
  // 笔记保存成功后的处理（如果需要）
  console.log('笔记已保存')
}

// Initialize
onMounted(async () => {
  await loadMajorCategories()
  await loadFocusAreas()
})
</script>
