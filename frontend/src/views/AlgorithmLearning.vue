<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <!-- 顶部标题栏 -->
    <div class="bg-white border-b border-gray-200">
      <div class="px-6 py-2">
        <h1 class="text-xl font-bold text-gray-900">算法学习</h1>
        <p class="text-xs text-gray-600 mt-0.5">浏览算法与数据结构学习内容</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：大分类 + Focus Area树 -->
      <div class="w-64 bg-white border-r border-gray-200 flex flex-col">
        <!-- 大分类Tab -->
        <div class="border-b border-gray-200">
          <nav class="flex flex-col space-y-1 p-2" aria-label="Categories">
            <button
              v-for="category in majorCategories"
              :key="category.id"
              @click="selectCategory(category.id)"
              :class="[
                'px-4 py-2 text-sm font-medium rounded-md text-left transition-colors',
                selectedCategoryId === category.id
                  ? 'bg-blue-50 text-blue-600'
                  : 'text-gray-700 hover:bg-gray-50'
              ]"
            >
              {{ category.name }}
            </button>
          </nav>
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
        <div v-else class="p-2">
          <!-- Focus Area标题 -->
          <div class="mb-2">
            <h2 class="text-lg font-bold text-gray-900">{{ selectedFocusArea.name }}</h2>
            <p v-if="selectedFocusArea.categoryName" class="text-xs text-gray-500 mt-0.5">
              {{ selectedFocusArea.categoryName }}
            </p>
          </div>

          <!-- 学习阶段Tab -->
          <div class="bg-white rounded-lg shadow mb-2">
              <div class="border-b border-gray-200">
                <div class="flex">
                  <button
                    v-for="stage in learningStages"
                    :key="stage.id"
                    @click="selectStage(stage.id)"
                    :class="[
                      'px-4 py-2 text-sm font-medium border-b-2 transition-colors',
                      selectedStageId === stage.id
                        ? 'border-blue-500 text-blue-600'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                    ]"
                  >
                    {{ stage.stageName }}
                    <span class="ml-2 text-xs text-gray-400">({{ stage.contents ? stage.contents.length : 0 }})</span>
                  </button>
                  <!-- 试题总结Tab -->
                  <button
                    @click="selectStage('questions-summary')"
                    :class="[
                      'px-4 py-2 text-sm font-medium border-b-2 transition-colors',
                      selectedStageId === 'questions-summary'
                        ? 'border-blue-500 text-blue-600'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                    ]"
                  >
                    试题总结
                    <span class="ml-2 text-xs text-gray-400">({{ questionCount }})</span>
                  </button>
                  <!-- 试题库Tab -->
                  <button
                    @click="selectStage('questions-detail')"
                    :class="[
                      'px-4 py-2 text-sm font-medium border-b-2 transition-colors',
                      selectedStageId === 'questions-detail'
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
              <div class="p-3">
                <!-- Loading状态 -->
                <div v-if="loading.contents" class="text-center text-gray-500 py-12">
                  <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <p class="mt-2 text-sm">加载中...</p>
                </div>

                <!-- 学习内容列表 -->
                <div v-else-if="!isQuestionTab && currentStageContents.length === 0" class="text-center text-gray-400 py-12">
                  该阶段暂无学习内容
                </div>

                <!-- 学习内容：左右分栏布局 -->
                <div v-else-if="!isQuestionTab" class="flex h-[calc(100vh-240px)]">
                  <!-- 左侧：资料列表 -->
                  <div class="w-56 border-r border-gray-200 overflow-y-auto">
                    <div
                      v-for="(content, index) in currentStageContents"
                      :key="content.id"
                      @click="selectContentItem(content)"
                      :class="[
                        'p-3 border-b border-gray-100 cursor-pointer transition-colors',
                        selectedContentItem?.id === content.id
                          ? 'bg-blue-50 border-l-4 border-l-blue-600'
                          : 'hover:bg-gray-50'
                      ]"
                    >
                      <h3 :class="[
                        'text-sm font-medium',
                        selectedContentItem?.id === content.id
                          ? 'text-blue-700'
                          : 'text-gray-900'
                      ]">
                        {{ index + 1 }}. {{ content.title }}
                      </h3>
                    </div>
                  </div>

                  <!-- 右侧：资料详情 -->
                  <div class="flex-1 overflow-y-auto bg-gray-50">
                    <!-- 未选中任何资料 -->
                    <div v-if="!selectedContentItem" class="flex items-center justify-center h-full">
                      <div class="text-center text-gray-400">
                        <svg class="mx-auto h-12 w-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                        </svg>
                        <p class="mt-4 text-sm">请从左侧选择一个学习资料查看详情</p>
                      </div>
                    </div>

                    <!-- 资料详情 -->
                    <div v-else class="p-4 h-full flex flex-col">
                      <!-- 描述 -->
                      <div v-if="selectedContentItem.description" class="prose max-w-none mb-4">
                        <div class="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
                          {{ selectedContentItem.description }}
                        </div>
                      </div>

                      <!-- 原文内容（iframe嵌入） -->
                      <div v-if="selectedContentItem.url" class="flex-1 flex flex-col">
                        <iframe
                          :src="selectedContentItem.url"
                          class="w-full h-full border border-gray-300 rounded"
                          frameborder="0"
                          sandbox="allow-same-origin allow-scripts allow-popups allow-forms"
                        ></iframe>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 试题总结：紧凑列表显示 -->
                <div v-else-if="selectedStageId === 'questions-summary'">
                  <div v-if="questions.length === 0" class="text-center text-gray-400 py-12">
                    该Focus Area暂无试题
                  </div>

                  <div v-else class="space-y-0 divide-y divide-gray-100">
                    <div
                      v-for="question in questions"
                      :key="question.id"
                      @click="openQuestionModal(question)"
                      class="p-2 bg-white hover:bg-gray-50 transition-colors cursor-pointer group"
                    >
                      <!-- 紧凑显示：难度+题目 | 核心思路 -->
                      <div class="flex items-start gap-3">
                        <!-- 左侧：难度 + 题目标题 -->
                        <div class="flex items-start gap-2 w-64 flex-shrink-0">
                          <!-- 难度图标 -->
                          <span :class="getDifficultyIconClass(question.difficulty)" class="flex-shrink-0 mt-0.5" :title="question.difficulty">
                            {{ getDifficultyIcon(question.difficulty) }}
                          </span>

                          <!-- 题目标题（作为LeetCode链接，文字超出省略） -->
                          <a
                            v-if="question.programmingDetails?.leetcodeUrl"
                            :href="question.programmingDetails.leetcodeUrl"
                            @click.stop
                            target="_blank"
                            rel="noopener noreferrer"
                            class="text-xs font-medium text-blue-600 hover:text-blue-800 hover:underline flex-1 truncate"
                            :title="question.title"
                          >
                            {{ question.title }}
                          </a>
                          <h3 v-else class="text-xs font-medium text-gray-900 flex-1 truncate" :title="question.title">
                            {{ question.title }}
                          </h3>
                        </div>

                        <!-- 右侧：核心思路（完整显示，自动换行） -->
                        <p class="text-xs text-gray-600 flex-1 leading-relaxed group-hover:text-gray-800">
                          {{ question.note?.coreStrategy || '暂无核心思路' }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 试题库：左右分栏详细显示 -->
                <div v-else-if="selectedStageId === 'questions-detail'" class="flex h-[calc(100vh-240px)]">
                  <!-- 左侧：试题列表 -->
                  <div class="w-80 border-r border-gray-200 overflow-y-auto">
                    <div v-if="questions.length === 0" class="text-center text-gray-400 py-12">
                      该Focus Area暂无试题
                    </div>

                    <div
                      v-for="question in questions"
                      :key="question.id"
                      @click="selectQuestionForDetail(question)"
                      :class="[
                        'p-3 border-b border-gray-100 cursor-pointer transition-colors',
                        selectedQuestionForDetail?.id === question.id
                          ? 'bg-blue-50 border-l-4 border-l-blue-600'
                          : 'hover:bg-gray-50'
                      ]"
                    >
                      <div class="flex items-start gap-2">
                        <!-- 难度图标 -->
                        <span :class="getDifficultyIconClass(question.difficulty)" class="flex-shrink-0 mt-0.5" :title="question.difficulty">
                          {{ getDifficultyIcon(question.difficulty) }}
                        </span>

                        <!-- 题目标题 -->
                        <div class="flex-1 min-w-0">
                          <h3 :class="[
                            'text-sm font-medium',
                            selectedQuestionForDetail?.id === question.id
                              ? 'text-blue-700'
                              : 'text-gray-900'
                          ]">
                            {{ question.title }}
                          </h3>
                          <!-- 笔记状态指示 -->
                          <div v-if="question.note" class="mt-1 flex items-center gap-1 text-xs text-gray-500">
                            <svg class="h-3 w-3" fill="currentColor" viewBox="0 0 20 20">
                              <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
                            </svg>
                            <span>已有笔记</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 右侧：试题详情 -->
                  <div class="flex-1 overflow-y-auto bg-gray-50">
                    <!-- 未选中任何试题 -->
                    <div v-if="!selectedQuestionForDetail" class="flex items-center justify-center h-full">
                      <div class="text-center text-gray-400">
                        <svg class="mx-auto h-12 w-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                        <p class="mt-4 text-sm">请从左侧选择一道试题查看详情</p>
                      </div>
                    </div>

                    <!-- 试题详情（内联显示） -->
                    <QuestionDetailPanel
                      v-else
                      :question="selectedQuestionForDetail"
                      :focus-area-id="selectedFocusArea?.id"
                      :focus-area-name="selectedFocusArea?.name"
                      @note-saved="handleNoteSavedInline"
                      @open-modal="openQuestionModalFromPanel"
                    />
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
import QuestionDetailPanel from '@/components/questions/QuestionDetailPanel.vue'

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
const selectedContentItem = ref(null)
const selectedQuestionForDetail = ref(null)

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
  if (!selectedStageId.value || isQuestionTab.value) return []
  const stage = learningStages.value.find(s => s.id === selectedStageId.value)
  return stage?.contents || []
})

const questionCount = computed(() => questions.value.length)

const isQuestionTab = computed(() => {
  return selectedStageId.value === 'questions-summary' || selectedStageId.value === 'questions-detail'
})

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
  selectedContentItem.value = null // 清空选中的学习资料

  // 保存当前选中的tab
  const previousStageId = selectedStageId.value

  await loadLearningContents()
  await loadQuestions()

  // 如果之前有选中的tab，尝试保持在相同的tab
  if (previousStageId) {
    // 检查新Focus Area是否有相同的tab
    const hasSameStage = learningStages.value.some(s => s.id === previousStageId) || previousStageId === 'questions-summary' || previousStageId === 'questions-detail'
    if (hasSameStage) {
      selectedStageId.value = previousStageId
    } else {
      // 如果没有相同的tab，默认选中第一个
      if (learningStages.value.length > 0) {
        selectedStageId.value = learningStages.value[0].id
      }
    }
  } else {
    // 首次选择Focus Area，默认选中第一个学习阶段
    if (learningStages.value.length > 0) {
      selectedStageId.value = learningStages.value[0].id
    }
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
  selectedContentItem.value = null // 切换tab时清空选中的学习资料
  selectedQuestionForDetail.value = null // 切换tab时清空选中的试题
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
    MEDIUM: '●',
    HARD: '●'
  }
  return icons[difficulty] || '●'
}

const getDifficultyIconClass = (difficulty) => {
  const classes = {
    EASY: 'text-green-500 text-sm',
    MEDIUM: 'text-yellow-500 text-sm',
    HARD: 'text-red-500 text-sm'
  }
  return classes[difficulty] || 'text-gray-500 text-sm'
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

const selectContentItem = (content) => {
  selectedContentItem.value = content
}

const selectQuestionForDetail = (question) => {
  selectedQuestionForDetail.value = question
}

const handleNoteSavedInline = async () => {
  // 笔记保存成功后，重新加载试题数据以更新笔记状态
  await loadQuestions()
  // 更新当前选中的试题数据
  if (selectedQuestionForDetail.value) {
    const updated = questions.value.find(q => q.id === selectedQuestionForDetail.value.id)
    if (updated) {
      selectedQuestionForDetail.value = updated
    }
  }
}

const openQuestionModalFromPanel = () => {
  // 从面板打开弹窗，显示完整笔记
  if (selectedQuestionForDetail.value) {
    selectedQuestion.value = selectedQuestionForDetail.value
    showQuestionModal.value = true
  }
}

// Initialize
onMounted(async () => {
  await loadMajorCategories()
  await loadFocusAreas()
})
</script>
