<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-full px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">编程与数据结构 - 内容管理</h1>
        <p class="text-sm text-gray-600 mt-1">管理学习内容、编程题和算法模版</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex h-[calc(100vh-9rem)]">
      <!-- 左侧面板 (25%) - 大分类Tab + Focus Area列表 -->
      <div class="w-1/4 bg-white border-r border-gray-200 overflow-hidden flex flex-col">
        <!-- Loading状态 -->
        <div v-if="loading" class="flex-1 flex items-center justify-center">
          <div class="text-center">
            <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-2 text-sm text-gray-500">加载中...</p>
          </div>
        </div>

        <!-- 大分类Tab + Focus Area列表 -->
        <template v-else>
          <!-- 大分类 Tab -->
          <div class="border-b border-gray-200">
            <nav class="flex flex-col space-y-1 p-2" aria-label="Categories">
              <button
                v-for="category in categories"
                :key="category.id"
                @click="selectedCategoryId = category.id; selectedFocusAreaId = null"
                :class="[
                  'px-4 py-2 text-sm font-medium rounded-md text-left',
                  selectedCategoryId === category.id
                    ? 'bg-blue-50 text-blue-600'
                    : 'text-gray-700 hover:bg-gray-50'
                ]"
              >
                {{ category.name }}
              </button>
            </nav>
          </div>

          <!-- Focus Area 列表 -->
          <div class="flex-1 overflow-y-auto p-4">
            <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">
              Focus Area
            </h3>
            <div v-if="filteredFocusAreas.length === 0" class="text-sm text-gray-500 text-center py-4">
              该分类下暂无Focus Area
            </div>
            <draggable
              v-else
              :model-value="filteredFocusAreas"
              @update:model-value="handleFocusAreaReorder"
              item-key="id"
              class="space-y-1"
              :animation="200"
            >
              <template #item="{ element: focusArea }">
                <button
                  @click="selectedFocusAreaId = focusArea.id"
                  :class="[
                    'w-full text-left px-3 py-2 text-sm rounded-md flex items-center gap-2 group',
                    selectedFocusAreaId === focusArea.id
                      ? 'bg-blue-50 text-blue-600 font-medium'
                      : 'text-gray-700 hover:bg-gray-50'
                  ]"
                >
                  <!-- 拖拽手柄 (6个点) -->
                  <svg class="w-4 h-4 flex-shrink-0 text-gray-400 hover:text-gray-600 cursor-move drag-handle transition-colors" fill="currentColor" viewBox="0 0 20 20">
                    <circle cx="7" cy="5" r="1.5"/>
                    <circle cx="13" cy="5" r="1.5"/>
                    <circle cx="7" cy="10" r="1.5"/>
                    <circle cx="13" cy="10" r="1.5"/>
                    <circle cx="7" cy="15" r="1.5"/>
                    <circle cx="13" cy="15" r="1.5"/>
                  </svg>
                  <span class="flex-1">{{ focusArea.name }}</span>
                </button>
              </template>
            </draggable>
          </div>
        </template>
      </div>

      <!-- 右侧面板 (75%) - 4个学习阶段Tab -->
      <div class="flex-1 bg-white overflow-hidden flex flex-col">
        <!-- 未选择Focus Area时的提示 -->
        <div v-if="!selectedFocusAreaId" class="flex-1 flex items-center justify-center text-gray-500">
          <div class="text-center">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            <p class="mt-2 text-sm">请先从左侧选择一个Focus Area</p>
          </div>
        </div>

        <!-- 选中Focus Area后显示Tabs -->
        <template v-else>
          <!-- Loading状态 -->
          <div v-if="loadingContents" class="flex-1 flex items-center justify-center">
            <div class="text-center">
              <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <p class="mt-2 text-sm text-gray-500">加载学习内容中...</p>
            </div>
          </div>

          <!-- 无学习阶段 -->
          <div v-else-if="skillStages.length === 0" class="flex-1 flex items-center justify-center">
            <div class="text-center text-gray-500">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p class="mt-2 text-sm">该Skill暂无学习阶段</p>
            </div>
          </div>

          <!-- 有学习阶段，显示Tabs -->
          <template v-else>
            <!-- Tab 标题 -->
            <div class="border-b border-gray-200 px-6 pt-4">
              <h2 class="text-lg font-semibold text-gray-900 mb-4">
                {{ selectedFocusArea?.name }} - 内容管理
              </h2>
              <!-- Tab 导航 -->
              <nav class="flex space-x-8 overflow-x-auto" aria-label="Stage Tabs">
                <button
                  v-for="stage in skillStages"
                  :key="stage.id"
                  @click="activeTab = 'stage'; activeStageId = stage.id"
                  :class="[
                    'py-2 px-1 border-b-2 font-medium text-sm whitespace-nowrap',
                    activeTab === 'stage' && activeStageId === stage.id
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  ]"
                >
                  {{ stage.stageName }}
                </button>
                <!-- 试题库Tab -->
                <button
                  @click="activeTab = 'questions'; loadQuestions()"
                  :class="[
                    'py-2 px-1 border-b-2 font-medium text-sm whitespace-nowrap',
                    activeTab === 'questions'
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  ]"
                >
                  试题库
                </button>
              </nav>
            </div>

            <!-- Tab 内容 -->
            <div class="flex-1 overflow-y-auto p-6">
              <!-- 试题库Tab内容 -->
              <div v-if="activeTab === 'questions'">
                <!-- 操作栏 -->
                <div class="mb-4 flex justify-between items-center">
                  <h3 class="text-base font-semibold text-gray-900">
                    试题列表 ({{ questions.length }} 题)
                  </h3>
                  <button
                    @click="showQuestionModal = true; editingQuestion = null"
                    class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
                  >
                    + 新增试题
                  </button>
                </div>

                <!-- 加载中 -->
                <div v-if="loadingQuestions" class="text-center py-12">
                  <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <p class="mt-2 text-sm text-gray-500">加载试题中...</p>
                </div>

                <!-- 试题表格 -->
                <div v-else-if="questions.length > 0" class="overflow-x-auto">
                  <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                      <tr>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-12">
                          #
                        </th>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          题目
                        </th>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-24">
                          难度
                        </th>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-32">
                          外部链接
                        </th>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-32">
                          操作
                        </th>
                      </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                      <tr v-for="(question, index) in questions" :key="question.id" class="hover:bg-gray-50">
                        <!-- 序号 -->
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">
                          {{ index + 1 }}
                        </td>
                        <!-- 题目 -->
                        <td class="px-4 py-3 text-sm text-gray-900">
                          <div class="line-clamp-2">{{ question.title }}</div>
                        </td>
                        <!-- 难度 -->
                        <td class="px-4 py-3 whitespace-nowrap">
                          <span :class="getDifficultyBadgeClass(question.difficulty)" class="px-2 py-1 text-xs font-medium rounded">
                            {{ getDifficultyLabel(question.difficulty) }}
                          </span>
                        </td>
                        <!-- 外部链接 -->
                        <td class="px-4 py-3 text-sm">
                          <div class="flex gap-2">
                            <a
                              v-if="question.programmingDetails?.leetcodeUrl"
                              :href="question.programmingDetails.leetcodeUrl"
                              target="_blank"
                              rel="noopener noreferrer"
                              class="inline-flex items-center px-2 py-1 text-xs font-medium text-orange-700 bg-orange-100 rounded hover:bg-orange-200 transition-colors"
                              title="在 LeetCode 上查看"
                            >
                              <svg class="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 24 24">
                                <path d="M13.483 0a1.374 1.374 0 0 0-.961.438L7.116 6.226l-3.854 4.126a5.266 5.266 0 0 0-1.209 2.104 5.35 5.35 0 0 0-.125.513 5.527 5.527 0 0 0 .062 2.362 5.83 5.83 0 0 0 .349 1.017 5.938 5.938 0 0 0 1.271 1.818l4.277 4.193.039.038c2.248 2.165 5.852 2.133 8.063-.074l2.396-2.392c.54-.54.54-1.414.003-1.955a1.378 1.378 0 0 0-1.951-.003l-2.396 2.392a3.021 3.021 0 0 1-4.205.038l-.02-.019-4.276-4.193c-.652-.64-.972-1.469-.948-2.263a2.68 2.68 0 0 1 .066-.523 2.545 2.545 0 0 1 .619-1.164L9.13 8.114c1.058-1.134 3.204-1.27 4.43-.278l3.501 2.831c.593.48 1.461.387 1.94-.207a1.384 1.384 0 0 0-.207-1.943l-3.5-2.831c-.8-.647-1.766-1.045-2.774-1.202l2.015-2.158A1.384 1.384 0 0 0 13.483 0zm-2.866 12.815a1.38 1.38 0 0 0-1.38 1.382 1.38 1.38 0 0 0 1.38 1.382H20.79a1.38 1.38 0 0 0 1.38-1.382 1.38 1.38 0 0 0-1.38-1.382z"/>
                              </svg>
                              LeetCode
                            </a>
                            <a
                              v-if="question.programmingDetails?.labuladongUrl"
                              :href="question.programmingDetails.labuladongUrl"
                              target="_blank"
                              rel="noopener noreferrer"
                              class="inline-flex items-center px-2 py-1 text-xs font-medium text-green-700 bg-green-100 rounded hover:bg-green-200 transition-colors"
                              title="labuladong 算法教程"
                            >
                              <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                              </svg>
                              labu
                            </a>
                          </div>
                        </td>
                        <!-- 操作 -->
                        <td class="px-4 py-3 whitespace-nowrap text-sm">
                          <button
                            @click="viewQuestion(question)"
                            class="text-gray-600 hover:text-gray-800 mr-3"
                          >
                            查看
                          </button>
                          <button
                            @click="editQuestion(question)"
                            class="text-blue-600 hover:text-blue-800 mr-3"
                          >
                            编辑
                          </button>
                          <button
                            @click="deleteQuestionConfirm(question)"
                            class="text-red-600 hover:text-red-800"
                          >
                            删除
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>

                <!-- 无试题提示 -->
                <div v-else class="text-center text-gray-400 py-12">
                  该Focus Area暂无试题
                </div>
              </div>

              <!-- 学习内容Tab -->
              <div v-else>
                <!-- 加载中 -->
                <div v-if="loadingContents" class="text-center py-12">
                  <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <p class="mt-2 text-sm text-gray-500">加载内容中...</p>
                </div>

                <!-- 内容展示 -->
                <div v-else-if="currentStage">
                <!-- 阶段描述 -->
                <div v-if="currentStage.description" class="mb-4 p-4 bg-blue-50 rounded-lg">
                  <p class="text-sm text-gray-700">{{ currentStage.description }}</p>
                </div>

                <!-- 学习内容列表 - 紧凑表格模式 -->
                <div v-if="currentStageContents.length > 0" class="overflow-x-auto">
                  <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                      <tr>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          #
                        </th>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          标题
                        </th>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          类型
                        </th>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          作者
                        </th>
                        <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          链接
                        </th>
                        <th scope="col" class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                          操作
                        </th>
                      </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                      <tr
                        v-for="(content, index) in currentStageContents"
                        :key="content.id"
                        class="hover:bg-gray-50"
                      >
                        <!-- 序号 -->
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">
                          {{ index + 1 }}
                        </td>
                        <!-- 标题 -->
                        <td class="px-4 py-3 text-sm text-gray-900">
                          {{ content.title }}
                        </td>
                        <!-- 类型 -->
                        <td class="px-4 py-3 whitespace-nowrap">
                          <span :class="getContentTypeBadgeClass(content.contentType)" class="text-xs">
                            {{ getContentTypeLabel(content.contentType) }}
                          </span>
                        </td>
                        <!-- 作者 -->
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-600">
                          {{ content.author || '-' }}
                        </td>
                        <!-- 链接 -->
                        <td class="px-4 py-3 text-sm">
                          <a
                            v-if="content.url"
                            :href="content.url"
                            target="_blank"
                            rel="noopener noreferrer"
                            class="text-blue-600 hover:text-blue-800 hover:underline inline-flex items-center"
                          >
                            访问
                            <svg class="ml-1 w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                            </svg>
                          </a>
                          <span v-else class="text-gray-400">-</span>
                        </td>
                        <!-- 操作 -->
                        <td class="px-4 py-3 text-sm text-right whitespace-nowrap">
                          <button
                            @click="handleEditContent(content)"
                            class="text-blue-600 hover:text-blue-800 mr-3"
                          >
                            编辑
                          </button>
                          <button
                            @click="handleDeleteContent(content.id)"
                            class="text-red-600 hover:text-red-800"
                          >
                            删除
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>

                <!-- 无内容提示 -->
                <div v-else class="text-center text-gray-400 py-12">
                  该Focus Area在此阶段暂无学习内容
                </div>
                </div>
              </div>
            </div>
          </template>
        </template>
      </div>

    </div>
  </div>

  <!-- 试题查看Modal -->
  <QuestionViewModal
    :is-open="showViewModal"
    :question="viewingQuestion"
    @close="showViewModal = false; viewingQuestion = null"
    @edit="editFromView"
  />

  <!-- 试题编辑Modal -->
  <AlgorithmQuestionEditModal
    :is-open="showQuestionModal"
    :question="editingQuestion"
    :focus-areas="allFocusAreas"
    :current-focus-area-id="selectedFocusAreaId"
    :current-focus-area-name="selectedFocusArea?.name"
    @save="saveQuestion"
    @cancel="showQuestionModal = false; editingQuestion = null"
  />

  <!-- 学习内容编辑Modal -->
  <LearningContentEditModal
    v-if="showContentModal"
    :focusAreaId="selectedFocusAreaId"
    :stageId="activeStageId"
    :content="editingContent"
    @close="showContentModal = false; editingContent = null"
    @success="handleContentSaved"
  />
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import draggable from 'vuedraggable'
import majorCategoryApi from '@/api/majorCategoryApi'
import learningContentApi from '@/api/learningContentApi'
import { getStagesBySkill } from '@/api/learningStageApi'
import { adminQuestionApi } from '@/api/questionApi'
import QuestionViewModal from '@/components/questions/QuestionViewModal.vue'
import AlgorithmQuestionEditModal from '@/components/questions/AlgorithmQuestionEditModal.vue'
import LearningContentEditModal from '@/components/skills/admin/LearningContentEditModal.vue'

// Skill ID（编程与数据结构）
const PROGRAMMING_SKILL_ID = 1

// 大分类数据
const categories = ref([])

// Focus Area 数据（包含分类信息）
const allFocusAreas = ref([])

// 学习阶段 tabs（从 Skill 获取，固定的）
const skillStages = ref([])

// Focus Area 的学习内容（按阶段分组）
const focusAreaContents = ref({})

// 选中状态
const selectedCategoryId = ref(null)
const selectedFocusAreaId = ref(null)
const activeTab = ref('stage') // 'stage' 或 'questions'
const activeStageId = ref(null)
const loading = ref(false)
const loadingStages = ref(false)
const loadingContents = ref(false)

// 试题库相关
const questions = ref([])
const loadingQuestions = ref(false)
const showViewModal = ref(false)
const viewingQuestion = ref(null)
const showQuestionModal = ref(false)
const editingQuestion = ref(null)

// 学习内容编辑相关
const showContentModal = ref(false)
const editingContent = ref(null)

// 计算属性：根据大分类过滤Focus Area
const filteredFocusAreas = computed(() => {
  if (!selectedCategoryId.value) return []

  return allFocusAreas.value
    .filter(fa => {
      return fa.categoryIds && fa.categoryIds.includes(selectedCategoryId.value)
    })
    .sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0))
})

// 计算属性：获取选中的Focus Area对象
const selectedFocusArea = computed(() => {
  return allFocusAreas.value.find(fa => fa.id === selectedFocusAreaId.value)
})

// 加载初始数据
const loadData = async () => {
  loading.value = true
  try {
    // 1. 加载大分类（只加载算法与数据结构的分类）
    const categoriesData = await majorCategoryApi.getAllMajorCategories(PROGRAMMING_SKILL_ID)
    categories.value = categoriesData

    // 2. 加载Focus Areas（skill_id=1 为"编程与数据结构"）
    const focusAreasData = await majorCategoryApi.getFocusAreasWithCategories(PROGRAMMING_SKILL_ID)
    allFocusAreas.value = focusAreasData

    // 3. 加载学习阶段（从 Skill 获取，作为固定的 tabs）
    await loadSkillStages()

    // 默认选中第一个大分类
    if (categories.value.length > 0) {
      selectedCategoryId.value = categories.value[0].id
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载 Skill 的学习阶段（固定的 tabs）
const loadSkillStages = async () => {
  loadingStages.value = true
  try {
    const stages = await getStagesBySkill(PROGRAMMING_SKILL_ID)
    skillStages.value = stages || []

    // 默认选中第一个学习阶段
    if (skillStages.value.length > 0) {
      activeStageId.value = skillStages.value[0].id
    } else {
      console.warn('没有获取到学习阶段数据')
    }
  } catch (error) {
    console.error('加载学习阶段失败:', error)
    skillStages.value = []
  } finally {
    loadingStages.value = false
  }
}

// 加载 Focus Area 的学习内容（当选中 Focus Area 时）
const loadFocusAreaContents = async () => {
  if (!selectedFocusAreaId.value) {
    focusAreaContents.value = {}
    return
  }

  loadingContents.value = true
  try {
    const data = await learningContentApi.getContentsByFocusArea(selectedFocusAreaId.value)

    // 将内容按阶段 ID 分组存储，方便查找
    const contentsByStage = {}
    if (data && data.stages) {
      data.stages.forEach(stage => {
        contentsByStage[stage.id] = stage.contents || []
      })
    }
    focusAreaContents.value = contentsByStage
  } catch (error) {
    console.error('加载学习内容失败:', error)
    focusAreaContents.value = {}
  } finally {
    loadingContents.value = false
  }
}

// 监听 Focus Area 变化，自动加载内容和试题
watch(selectedFocusAreaId, () => {
  loadFocusAreaContents()

  // 如果当前在试题库Tab，也刷新试题
  if (activeTab.value === 'questions') {
    loadQuestions()
  }
})

// 计算当前选中阶段的内容
const currentStageContents = computed(() => {
  if (!activeStageId.value || !focusAreaContents.value) return []
  return focusAreaContents.value[activeStageId.value] || []
})

// 计算当前选中的阶段信息
const currentStage = computed(() => {
  if (!activeStageId.value) return null
  return skillStages.value.find(stage => stage.id === activeStageId.value)
})

// 辅助函数：获取内容类型的badge样式
const getContentTypeBadgeClass = (type) => {
  const classes = {
    text: 'px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-700',
    code: 'px-2 py-1 text-xs rounded-full bg-green-100 text-green-700',
    link: 'px-2 py-1 text-xs rounded-full bg-purple-100 text-purple-700',
    algorithm_template: 'px-2 py-1 text-xs rounded-full bg-orange-100 text-orange-700'
  }
  return classes[type] || 'px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-700'
}

// 辅助函数：获取内容类型的显示文本
const getContentTypeLabel = (type) => {
  const labels = {
    text: '文本',
    code: '代码',
    link: '链接',
    algorithm_template: '算法模版'
  }
  return labels[type] || type
}

// 辅助函数：格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// ==================== 学习内容编辑/删除功能 ====================

// 编辑学习内容
const handleEditContent = (content) => {
  editingContent.value = content
  showContentModal.value = true
}

// 删除学习内容
const handleDeleteContent = async (contentId) => {
  if (!confirm('确定要删除这个学习内容吗？')) {
    return
  }

  try {
    await learningContentApi.deleteContent(contentId)
    await loadFocusAreaContents()
  } catch (error) {
    console.error('删除学习内容失败:', error)
    alert('删除失败: ' + (error.message || '未知错误'))
  }
}

// 保存学习内容后刷新
const handleContentSaved = async () => {
  showContentModal.value = false
  editingContent.value = null
  await loadFocusAreaContents()
}

// ==================== 试题库功能 ====================

// 加载试题列表
const loadQuestions = async () => {
  if (!selectedFocusAreaId.value) return

  loadingQuestions.value = true
  try {
    const data = await adminQuestionApi.getAllQuestions({
      focusAreaId: selectedFocusAreaId.value
    })
    questions.value = data || []
  } catch (error) {
    console.error('加载试题失败:', error)
    questions.value = []
  } finally {
    loadingQuestions.value = false
  }
}

// 查看试题（只读）
const viewQuestion = async (question) => {
  try {
    // 加载完整数据（包含 programmingDetails）
    const fullQuestion = await adminQuestionApi.getQuestionById(question.id)
    viewingQuestion.value = fullQuestion
    showViewModal.value = true
  } catch (error) {
    console.error('加载试题详情失败:', error)
    alert('加载失败')
  }
}

// 从查看弹窗切换到编辑
const editFromView = () => {
  editingQuestion.value = viewingQuestion.value
  showViewModal.value = false
  showQuestionModal.value = true
}

// 编辑试题
const editQuestion = async (question) => {
  try {
    // 加载完整数据（包含 programmingDetails）
    const fullQuestion = await adminQuestionApi.getQuestionById(question.id)
    editingQuestion.value = fullQuestion
    showQuestionModal.value = true
  } catch (error) {
    console.error('加载试题详情失败:', error)
    alert('加载失败')
  }
}

// 删除试题确认
const deleteQuestionConfirm = async (question) => {
  if (!confirm(`确定要删除试题"${(question.questionDescription || question.title).substring(0, 30)}..."吗？`)) {
    return
  }

  try {
    await adminQuestionApi.deleteQuestion(question.id)
    // 重新加载试题列表
    await loadQuestions()
  } catch (error) {
    console.error('删除试题失败:', error)
    alert('删除失败: ' + (error.message || '未知错误'))
  }
}

// 难度徽章样式
const getDifficultyBadgeClass = (difficulty) => {
  const classes = {
    EASY: 'bg-green-100 text-green-800',
    MEDIUM: 'bg-yellow-100 text-yellow-800',
    HARD: 'bg-red-100 text-red-800'
  }
  return classes[difficulty] || 'bg-gray-100 text-gray-800'
}

// 难度标签
const getDifficultyLabel = (difficulty) => {
  const labels = {
    EASY: '简单',
    MEDIUM: '中等',
    HARD: '困难'
  }
  return labels[difficulty] || difficulty
}

// 保存试题
const saveQuestion = async (formData) => {
  try {
    if (editingQuestion.value) {
      // 编辑现有试题
      await adminQuestionApi.updateQuestion(editingQuestion.value.id, formData)
    } else {
      // 创建新试题
      await adminQuestionApi.createQuestion(formData)
    }

    // 关闭Modal
    showQuestionModal.value = false
    editingQuestion.value = null

    // 重新加载试题列表
    await loadQuestions()
  } catch (error) {
    console.error('保存试题失败:', error)
    alert('保存失败: ' + (error.message || '未知错误'))
  }
}

// ===== Focus Area拖拽排序 =====
const handleFocusAreaReorder = async (newList) => {
  try {
    // 批量更新displayOrder
    const updates = newList.map((fa, index) => ({
      id: fa.id,
      displayOrder: index
    }))

    await majorCategoryApi.batchUpdateFocusAreaOrder(updates)

    // 刷新Focus Areas以获取最新顺序
    await loadData()
  } catch (error) {
    console.error('更新Focus Area顺序失败:', error)
    alert('更新顺序失败: ' + (error.message || '未知错误'))
    await loadData()
  }
}

const handleFocusAreaDragEnd = async (evt) => {
  if (evt.oldIndex === evt.newIndex || !selectedCategoryId.value) {
    return // 没有移动
  }

  try {
    // 获取当前分类下的Focus Areas
    const focusAreasInCategory = filteredFocusAreas.value

    // 移动元素
    const movedItem = focusAreasInCategory[evt.oldIndex]
    focusAreasInCategory.splice(evt.oldIndex, 1)
    focusAreasInCategory.splice(evt.newIndex, 0, movedItem)

    // 批量更新displayOrder
    const updates = focusAreasInCategory.map((fa, index) => ({
      id: fa.id,
      displayOrder: index
    }))

    await majorCategoryApi.batchUpdateFocusAreaOrder(updates)

    // 刷新Focus Areas
    await loadFocusAreas()
  } catch (error) {
    console.error('更新Focus Area顺序失败:', error)
    alert('更新顺序失败: ' + (error.message || '未知错误'))
    // 失败时重新加载恢复原顺序
    await loadFocusAreas()
  }
}

// 页面挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

