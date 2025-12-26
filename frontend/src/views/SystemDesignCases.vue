<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <!-- Header -->
    <div class="bg-white shadow-sm px-6 py-4">
      <h1 class="text-2xl font-bold text-gray-800">系统设计 - 典型案例实战</h1>
    </div>

    <!-- Main Content: Three-column layout -->
    <div class="flex-1 flex overflow-hidden">
      <!-- Left: Case List (Narrower) -->
      <div class="w-1/6 bg-white border-r border-gray-200 flex flex-col">
        <div class="p-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-700">案例列表 ({{ cases.length }})</h2>
        </div>
        <div class="flex-1 overflow-y-auto">
          <div
            v-for="caseItem in cases"
            :key="caseItem.id"
            @click="selectCase(caseItem)"
            :class="[
              'p-4 border-b border-gray-100 cursor-pointer transition-colors',
              selectedCase?.id === caseItem.id
                ? 'bg-blue-50 border-l-4 border-l-blue-600'
                : 'hover:bg-gray-50'
            ]"
          >
            <div class="flex justify-between items-start mb-2">
              <h3 class="font-semibold text-gray-800 flex-1">{{ caseItem.title }}</h3>
              <span
                :class="[
                  'px-2 py-1 text-xs rounded',
                  caseItem.difficulty === 'EASY' ? 'bg-green-100 text-green-700' :
                  caseItem.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' :
                  'bg-red-100 text-red-700'
                ]"
              >
                {{ difficultyText(caseItem.difficulty) }}
              </span>
            </div>
            <div class="text-xs text-gray-500">
              <span v-if="caseItem.companyTags && caseItem.companyTags.length > 0">
                🏢 {{ caseItem.companyTags.slice(0, 2).join(', ') }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Middle + Right Columns Container -->
      <div class="flex-1 flex flex-col overflow-hidden">
        <div v-if="!selectedCase" class="h-full flex items-center justify-center text-gray-400 w-full">
          <p>← 请从左侧选择一个案例开始学习</p>
        </div>
        <template v-else>
          <!-- Case Header (full width at top) -->
          <div class="bg-white border-b border-gray-200 px-6 py-3 flex-shrink-0">
            <div class="flex justify-between items-center">
              <div class="flex items-center gap-3 flex-1">
                <h2 class="text-xl font-bold text-gray-800">{{ selectedCase.title }}</h2>
                <span
                  :class="[
                    'px-2 py-1 text-xs rounded-full',
                    selectedCase.difficulty === 'EASY' ? 'bg-green-100 text-green-700' :
                    selectedCase.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' :
                    'bg-red-100 text-red-700'
                  ]"
                >
                  {{ difficultyText(selectedCase.difficulty) }} ({{ selectedCase.difficultyRating }}/10)
                </span>
                <span
                  v-if="selectedCase.isOfficial"
                  class="px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-700"
                >
                  官方案例
                </span>
                <span
                  v-for="tag in selectedCase.companyTags"
                  :key="tag"
                  class="px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-700"
                >
                  🏢 {{ tag }}
                </span>
              </div>
              <button
                @click="toggleMode"
                :class="[
                  'px-3 py-1.5 rounded-lg transition-colors text-sm font-medium',
                  isEditMode
                    ? 'bg-blue-600 text-white hover:bg-blue-700'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                ]"
              >
                {{ isEditMode ? '退出编辑' : '开始答题' }}
              </button>
            </div>
          </div>

          <!-- View Mode: Three columns -->
          <div v-if="!isEditMode" class="flex-1 flex overflow-hidden">
            <!-- Middle Column: Description + Resources (上下排列) -->
            <div class="w-1/4 bg-white border-r border-gray-200 p-4 overflow-y-auto">
              <!-- Case Description -->
              <div class="mb-6">
                <h3 class="text-lg font-semibold mb-3">案例描述</h3>
                <div v-if="selectedCase.caseDescription" class="prose-compact max-w-none">
                  <div v-html="renderMarkdown(selectedCase.caseDescription)"></div>
                </div>
                <p v-else class="text-gray-400 text-sm">暂无描述</p>
              </div>

              <!-- Learning Resources -->
              <div>
                <h3 class="text-lg font-semibold mb-3">学习资源 ({{ selectedCase.resources?.length || 0 }})</h3>
                <div v-if="selectedCase.resources && selectedCase.resources.length > 0" class="space-y-2">
                  <div v-for="resource in selectedCase.resources" :key="resource.id" class="p-2 bg-gray-50 rounded-lg">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-xs px-2 py-0.5 rounded bg-blue-100 text-blue-700">{{ resource.resourceType }}</span>
                      <!-- Video: Show play button -->
                      <button
                        v-if="resource.resourceType === 'VIDEO'"
                        @click="openVideoModal(resource)"
                        class="text-sm font-medium text-blue-600 hover:underline flex items-center gap-1"
                      >
                        <span>▶️</span>
                        <span>{{ resource.title }}</span>
                      </button>
                      <!-- Other types: Open in new tab -->
                      <a
                        v-else
                        :href="resource.url"
                        target="_blank"
                        class="text-sm font-medium text-blue-600 hover:underline"
                      >
                        {{ resource.title }}
                      </a>
                    </div>
                    <p v-if="resource.description" class="text-xs text-gray-600">{{ truncate(resource.description, 80) }}</p>
                  </div>
                </div>
                <p v-else class="text-gray-400 text-sm">暂无学习资源</p>
              </div>
            </div>

            <!-- Right Column: Answer Selection + Tabs (最宽) -->
            <div class="flex-1 bg-white p-4 flex flex-col overflow-hidden">
              <div v-if="selectedCase.solutions && selectedCase.solutions.length > 0" class="mb-3 flex-shrink-0">
                <div class="flex gap-3 items-center">
                  <h3 class="text-lg font-semibold whitespace-nowrap">答案选择:</h3>
                  <select v-model="selectedSolutionId" class="w-80 px-3 py-1.5 border border-gray-300 rounded-lg text-sm">
                    <option value="my-answer">我的答案</option>
                    <option v-for="solution in selectedCase.solutions" :key="solution.id" :value="solution.id">
                      {{ solution.solutionName }} {{ solution.author ? `(${solution.author})` : '' }}
                    </option>
                  </select>
                </div>
              </div>
              <div v-if="currentSolution" class="flex-1 flex flex-col min-h-0">
                <div class="flex border-b border-gray-200 flex-shrink-0">
                  <button
                    v-for="(step, index) in stepTabs"
                    :key="index"
                    @click="activeStepTab = index"
                    :class="['px-4 py-2 text-sm font-medium transition-colors', activeStepTab === index ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600 hover:text-gray-800']"
                  >
                    {{ step.label }}
                  </button>
                </div>
                <div class="flex-1 overflow-y-auto py-4 prose-compact max-w-none" :key="'step-' + activeStepTab">
                  <!-- Image display for architecture diagram tab -->
                  <div v-if="stepTabs[activeStepTab].type === 'image' && currentSolution[stepTabs[activeStepTab].field]" class="flex justify-center items-start">
                    <img
                      :src="currentSolution[stepTabs[activeStepTab].field]"
                      alt="架构图"
                      class="max-w-full h-auto border border-gray-200 rounded-lg"
                      @error="handleImageError"
                    />
                  </div>
                  <!-- Markdown content for other tabs -->
                  <div v-else-if="stepTabs[activeStepTab].type === 'markdown' && currentSolution[stepTabs[activeStepTab].field]" v-html="renderMarkdown(currentSolution[stepTabs[activeStepTab].field])"></div>
                  <p v-else class="text-gray-400">暂无内容</p>
                </div>
              </div>
              <p v-else class="text-gray-400 text-sm">暂无参考答案</p>
            </div>
          </div>

          <!-- Edit Mode: Description on top, split view below -->
          <div v-else class="flex-1 flex flex-col min-h-0 px-6 py-4">
            <!-- Top: Case Description (read-only in edit mode) -->
            <div class="mb-4 pb-4 border-b border-gray-200 flex-shrink-0">
              <h3 class="text-lg font-semibold mb-3">案例描述</h3>
              <div v-if="selectedCase.caseDescription" class="prose-compact max-w-none">
                <div v-html="renderMarkdown(selectedCase.caseDescription)"></div>
              </div>
              <p v-else class="text-gray-400 text-sm">暂无描述</p>
            </div>

            <!-- Bottom: Three columns (Reference Solution | My Answer | Key Points) -->
            <div class="flex-1 grid grid-cols-3 gap-6 min-h-0">
              <!-- Left: Reference Solution -->
              <div class="flex flex-col border-r-2 border-gray-300 pr-4 min-h-0">
                <div class="flex items-center gap-3 mb-3">
                  <h3 class="text-base font-semibold whitespace-nowrap">参考答案:</h3>
                  <select v-if="selectedCase.solutions && selectedCase.solutions.length > 0" v-model="selectedSolutionId" class="flex-1 px-2 py-1 border border-gray-300 rounded text-xs">
                    <option v-for="solution in selectedCase.solutions" :key="solution.id" :value="solution.id">{{ solution.solutionName }}</option>
                  </select>
                </div>
                <div v-if="currentSolution" class="flex-1 flex flex-col min-h-0">
                  <div class="flex border-b border-gray-200 flex-shrink-0 overflow-x-auto">
                    <button
                      v-for="(step, index) in stepTabs"
                      :key="index"
                      @click="activeStepTab = index"
                      :class="['px-2 py-1 text-xs font-medium transition-colors whitespace-nowrap', activeStepTab === index ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600 hover:text-gray-800']"
                    >
                      {{ step.shortLabel }}
                    </button>
                  </div>
                  <div class="flex-1 overflow-y-auto py-2 prose-compact max-w-none text-sm">
                    <div v-if="currentSolution[stepTabs[activeStepTab].field]" v-html="renderMarkdown(currentSolution[stepTabs[activeStepTab].field])"></div>
                    <p v-else class="text-gray-400 text-xs">暂无内容</p>
                  </div>
                </div>
              </div>

              <!-- Middle: My Answer (步骤) -->
              <div class="flex flex-col border-r-2 border-gray-300 pr-4 min-h-0">
                <div class="flex justify-between items-center mb-3">
                  <h3 class="text-base font-semibold">我的答案</h3>
                  <button @click="saveMyAnswer" class="px-2 py-1 bg-blue-600 text-white rounded hover:bg-blue-700 text-xs">保存</button>
                </div>
                <div class="flex-1 flex flex-col min-h-0">
                  <div class="flex border-b border-gray-200 flex-shrink-0 overflow-x-auto">
                    <button
                      v-for="(step, index) in stepTabs"
                      :key="index"
                      @click="activeStepTab = index"
                      :class="['px-2 py-1 text-xs font-medium transition-colors whitespace-nowrap', activeStepTab === index ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600 hover:text-gray-800']"
                    >
                      {{ step.shortLabel }}
                    </button>
                  </div>

                  <!-- Image URL input for architecture diagram -->
                  <div v-if="stepTabs[activeStepTab].type === 'image'" class="flex-1 flex flex-col mt-2 gap-2">
                    <input
                      v-model="myAnswer[stepTabs[activeStepTab].field]"
                      type="text"
                      class="px-2 py-1 border border-gray-300 rounded text-xs"
                      placeholder="输入架构图URL"
                    />
                    <div v-if="myAnswer[stepTabs[activeStepTab].field]" class="flex-1 border border-gray-200 rounded p-2 overflow-auto bg-gray-50">
                      <img
                        :src="myAnswer[stepTabs[activeStepTab].field]"
                        alt="架构图预览"
                        class="max-w-full h-auto"
                        @error="handleImageError"
                      />
                    </div>
                    <p v-else class="text-gray-400 text-xs">输入图片URL后会显示预览</p>
                  </div>

                  <!-- Markdown textarea for other tabs -->
                  <textarea
                    v-else
                    v-model="myAnswer[stepTabs[activeStepTab].field]"
                    class="flex-1 w-full p-2 border border-gray-300 rounded font-mono text-xs resize-none mt-2"
                    :placeholder="`输入${stepTabs[activeStepTab].label}的内容...`"
                  ></textarea>
                </div>
              </div>

              <!-- Right: Key Points (结构化要点) -->
              <div class="flex flex-col min-h-0">
                <h3 class="text-base font-semibold mb-3">要点拆分</h3>
                <div class="flex-1 overflow-y-auto space-y-2 pr-1">
                  <!-- Requirement -->
                  <div>
                    <label class="block text-xs font-medium text-gray-700 mb-0.5">Requirement</label>
                    <textarea
                      v-model="myAnswer.kpRequirement"
                      rows="2"
                      class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                      placeholder="功能需求..."
                    ></textarea>
                  </div>

                  <!-- NFR -->
                  <div>
                    <label class="block text-xs font-medium text-gray-700 mb-0.5">NFR</label>
                    <textarea
                      v-model="myAnswer.kpNfr"
                      rows="2"
                      class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                      placeholder="非功能性需求..."
                    ></textarea>
                  </div>

                  <!-- Entity -->
                  <div>
                    <label class="block text-xs font-medium text-gray-700 mb-0.5">Entity</label>
                    <textarea
                      v-model="myAnswer.kpEntity"
                      rows="2"
                      class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                      placeholder="核心实体..."
                    ></textarea>
                  </div>

                  <!-- Components -->
                  <div>
                    <label class="block text-xs font-medium text-gray-700 mb-0.5">Components</label>
                    <textarea
                      v-model="myAnswer.kpComponents"
                      rows="2"
                      class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                      placeholder="关键组件..."
                    ></textarea>
                  </div>

                  <!-- API -->
                  <div>
                    <label class="block text-xs font-medium text-gray-700 mb-0.5">API</label>
                    <textarea
                      v-model="myAnswer.kpApi"
                      rows="2"
                      class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                      placeholder="API设计..."
                    ></textarea>
                  </div>

                  <!-- Object1 -->
                  <div>
                    <label class="block text-xs font-medium text-gray-700 mb-0.5">Object1</label>
                    <textarea
                      v-model="myAnswer.kpObject1"
                      rows="2"
                      class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                      placeholder="核心对象1..."
                    ></textarea>
                  </div>

                  <!-- Object2 -->
                  <div>
                    <label class="block text-xs font-medium text-gray-700 mb-0.5">Object2</label>
                    <textarea
                      v-model="myAnswer.kpObject2"
                      rows="2"
                      class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                      placeholder="核心对象2..."
                    ></textarea>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- Video Modal (Draggable) -->
    <div
      v-if="showVideoModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @mousedown="closeVideoModal"
    >
      <div
        ref="videoModalRef"
        class="bg-white rounded-lg shadow-2xl overflow-hidden"
        :style="{ width: '800px', maxWidth: '90vw', position: 'relative', left: modalPosition.x + 'px', top: modalPosition.y + 'px' }"
        @mousedown.stop
      >
        <!-- Modal Header (Draggable) -->
        <div
          class="bg-blue-600 text-white px-4 py-3 flex justify-between items-center cursor-move"
          @mousedown="startDrag"
        >
          <h3 class="font-semibold">{{ currentVideo?.title || '视频播放' }}</h3>
          <button @click="closeVideoModal" class="text-white hover:text-gray-200 text-xl">✕</button>
        </div>

        <!-- Modal Body -->
        <div class="p-4 bg-gray-50">
          <div class="aspect-video bg-black rounded-lg overflow-hidden">
            <iframe
              v-if="currentVideo?.url"
              :src="getEmbedUrl(currentVideo.url)"
              class="w-full h-full"
              frameborder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen
            ></iframe>
          </div>
          <p v-if="currentVideo?.description" class="mt-3 text-sm text-gray-600">{{ currentVideo.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import * as systemDesignCaseApi from '@/api/systemDesignCaseApi'
import { marked } from 'marked'

const cases = ref([])
const selectedCase = ref(null)
const selectedSolutionId = ref(null)
const activeStepTab = ref(0)
const isEditMode = ref(false)
const myAnswer = ref({
  // 结构化答案字段（Key Points）
  kpRequirement: '',
  kpNfr: '',
  kpEntity: '',
  kpComponents: '',
  kpApi: '',
  kpObject1: '',
  kpObject2: '',
  // 原有的step字段
  step1Requirements: '',
  step2Entities: '',
  step3Api: '',
  step4DataFlow: '',
  step5Architecture: '',
  step6DeepDive: '',
  architectureDiagramUrl: '',
  keyPoints: ''
})

// Video modal state
const showVideoModal = ref(false)
const currentVideo = ref(null)
const videoModalRef = ref(null)
const modalPosition = ref({ x: 0, y: 0 })
const isDragging = ref(false)
const dragStart = ref({ x: 0, y: 0 })

const stepTabs = [
  { label: 'Step 1: 需求', shortLabel: 'Step1', field: 'step1Requirements', type: 'markdown' },
  { label: 'Step 2: 实体', shortLabel: 'Step2', field: 'step2Entities', type: 'markdown' },
  { label: 'Step 3: API', shortLabel: 'Step3', field: 'step3Api', type: 'markdown' },
  { label: 'Step 4: 数据流', shortLabel: 'Step4', field: 'step4DataFlow', type: 'markdown' },
  { label: 'Step 5: 架构', shortLabel: 'Step5', field: 'step5Architecture', type: 'markdown' },
  { label: 'Step 6: 深入', shortLabel: 'Step6', field: 'step6DeepDive', type: 'markdown' },
  { label: '架构图', shortLabel: '架构图', field: 'architectureDiagramUrl', type: 'image' },
  { label: '要点总结', shortLabel: '要点', field: 'keyPoints', type: 'markdown' }
]

const currentSolution = computed(() => {
  if (!selectedSolutionId.value) return null

  // 如果选择的是"我的答案"
  if (selectedSolutionId.value === 'my-answer') {
    return {
      id: 'my-answer',
      solutionName: '我的答案',
      step1Requirements: myAnswer.value.step1Requirements,
      step2Entities: myAnswer.value.step2Entities,
      step3Api: myAnswer.value.step3Api,
      step4DataFlow: myAnswer.value.step4DataFlow,
      step5Architecture: myAnswer.value.step5Architecture,
      step6DeepDive: myAnswer.value.step6DeepDive,
      architectureDiagramUrl: myAnswer.value.architectureDiagramUrl,
      keyPoints: myAnswer.value.keyPoints
    }
  }

  // 否则返回参考答案
  if (!selectedCase.value?.solutions) return null
  return selectedCase.value.solutions.find(s => s.id === selectedSolutionId.value)
})

watch(() => selectedCase.value?.solutions, (solutions) => {
  if (solutions && solutions.length > 0) {
    selectedSolutionId.value = solutions[0].id
  } else {
    selectedSolutionId.value = null
  }
  activeStepTab.value = 0
}, { immediate: true })

const difficultyText = (difficulty) => {
  const map = { EASY: '简单', MEDIUM: '中等', HARD: '困难' }
  return map[difficulty] || difficulty
}

const truncate = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const renderMarkdown = (markdown) => {
  return marked(markdown || '')
}

const loadCases = async () => {
  try {
    const data = await systemDesignCaseApi.getOfficialCases()
    cases.value = data || []
    if (cases.value.length > 0 && !selectedCase.value) {
      selectCase(cases.value[0])
    }
  } catch (error) {
    console.error('加载案例列表失败:', error)
  }
}

const selectCase = async (caseItem) => {
  try {
    const data = await systemDesignCaseApi.getCaseById(caseItem.id)
    selectedCase.value = data
    isEditMode.value = false
    activeStepTab.value = 0

    // 尝试加载用户之前保存的答案
    try {
      const myNote = await systemDesignCaseApi.getMyNote(caseItem.id)
      if (myNote) {
        // 映射后端字段到前端字段
        myAnswer.value = {
          // 结构化字段（Key Points）
          kpRequirement: myNote.kpRequirement || '',
          kpNfr: myNote.kpNfr || '',
          kpEntity: myNote.kpEntity || '',
          kpComponents: myNote.kpComponents || '',
          kpApi: myNote.kpApi || '',
          kpObject1: myNote.kpObject1 || '',
          kpObject2: myNote.kpObject2 || '',
          // 原有的step字段
          step1Requirements: myNote.userStep1RequirementsAndNfr || '',
          step2Entities: myNote.userStep2CoreEntities || '',
          step3Api: myNote.userStep3ApiDesign || '',
          step4DataFlow: myNote.userStep4DataFlow || '',
          step5Architecture: myNote.userStep5HighLevelDesign || '',
          step6DeepDive: myNote.userStep6DeepDiveAndTradeoffs || '',
          architectureDiagramUrl: myNote.architectureDiagramUrl || '',
          keyPoints: myNote.keyTakeaways || ''
        }
      } else {
        // 没有保存过的答案，初始化为空
        resetMyAnswer()
      }
    } catch (error) {
      // 404表示还没有保存过答案，这是正常情况
      if (error.response?.status === 404) {
        resetMyAnswer()
      } else {
        console.error('加载我的答案失败:', error)
        resetMyAnswer()
      }
    }
  } catch (error) {
    console.error('加载案例详情失败:', error)
  }
}

const resetMyAnswer = () => {
  myAnswer.value = {
    // 结构化字段（Key Points）
    kpRequirement: '',
    kpNfr: '',
    kpEntity: '',
    kpComponents: '',
    kpApi: '',
    kpObject1: '',
    kpObject2: '',
    // 原有的step字段
    step1Requirements: '',
    step2Entities: '',
    step3Api: '',
    step4DataFlow: '',
    step5Architecture: '',
    step6DeepDive: '',
    architectureDiagramUrl: '',
    keyPoints: ''
  }
}

const toggleMode = () => {
  isEditMode.value = !isEditMode.value
  activeStepTab.value = 0
}

const handleImageError = (event) => {
  console.error('图片加载失败:', event.target.src)
  event.target.alt = '图片加载失败，请检查URL是否正确'
}

const saveMyAnswer = async () => {
  if (!selectedCase.value) return

  try {
    // 映射前端字段到后端字段名（camelCase to snake_case）
    const data = {
      // 结构化字段（Key Points）
      kpRequirement: myAnswer.value.kpRequirement,
      kpNfr: myAnswer.value.kpNfr,
      kpEntity: myAnswer.value.kpEntity,
      kpComponents: myAnswer.value.kpComponents,
      kpApi: myAnswer.value.kpApi,
      kpObject1: myAnswer.value.kpObject1,
      kpObject2: myAnswer.value.kpObject2,
      // 原有的step字段
      userStep1RequirementsAndNfr: myAnswer.value.step1Requirements,
      userStep2CoreEntities: myAnswer.value.step2Entities,
      userStep3ApiDesign: myAnswer.value.step3Api,
      userStep4DataFlow: myAnswer.value.step4DataFlow,
      userStep5HighLevelDesign: myAnswer.value.step5Architecture,
      userStep6DeepDiveAndTradeoffs: myAnswer.value.step6DeepDive,
      architectureDiagramUrl: myAnswer.value.architectureDiagramUrl,
      keyTakeaways: myAnswer.value.keyPoints
    }

    await systemDesignCaseApi.saveOrUpdateMyNote(selectedCase.value.id, data)
    alert('答案已保存成功！')
  } catch (error) {
    console.error('保存答案失败:', error)
    alert('保存失败，请重试：' + (error.message || '未知错误'))
  }
}

// Video modal functions
const openVideoModal = (resource) => {
  currentVideo.value = resource
  modalPosition.value = { x: 0, y: 0 }
  showVideoModal.value = true
}

const closeVideoModal = () => {
  showVideoModal.value = false
  currentVideo.value = null
}

const getEmbedUrl = (url) => {
  // YouTube
  if (url.includes('youtube.com') || url.includes('youtu.be')) {
    const videoId = url.includes('youtu.be')
      ? url.split('youtu.be/')[1]?.split('?')[0]
      : new URLSearchParams(new URL(url).search).get('v')
    return `https://www.youtube.com/embed/${videoId}`
  }

  // Bilibili
  if (url.includes('bilibili.com')) {
    const bvMatch = url.match(/BV\w+/)
    if (bvMatch) {
      return `https://player.bilibili.com/player.html?bvid=${bvMatch[0]}&high_quality=1`
    }
  }

  // 直接返回URL（假设是可嵌入的视频链接）
  return url
}

const startDrag = (event) => {
  isDragging.value = true
  dragStart.value = {
    x: event.clientX - modalPosition.value.x,
    y: event.clientY - modalPosition.value.y
  }

  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}

const onDrag = (event) => {
  if (!isDragging.value) return

  modalPosition.value = {
    x: event.clientX - dragStart.value.x,
    y: event.clientY - dragStart.value.y
  }
}

const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

onMounted(() => {
  loadCases()
})
</script>

<style scoped>
.prose-compact {
  color: #374151;
  font-size: 0.875rem;
  line-height: 1.5;
}
.prose-compact :deep(h1) { font-size: 1.25rem; font-weight: 700; margin-top: 0.75em; margin-bottom: 0.5em; color: #1f2937; }
.prose-compact :deep(h2) { font-size: 1.125rem; font-weight: 600; margin-top: 0.75em; margin-bottom: 0.5em; color: #1f2937; }
.prose-compact :deep(h3) { font-size: 1rem; font-weight: 600; margin-top: 0.5em; margin-bottom: 0.375em; color: #374151; }
.prose-compact :deep(p) { margin-top: 0.5em; margin-bottom: 0.5em; }
.prose-compact :deep(ul), .prose-compact :deep(ol) { margin-top: 0.5em; margin-bottom: 0.5em; padding-left: 1.5em; }
.prose-compact :deep(li) { margin-top: 0.25em; margin-bottom: 0.25em; }
.prose-compact :deep(code) { background-color: #f3f4f6; padding: 0.125rem 0.25rem; border-radius: 0.25rem; font-size: 0.8125em; }
.prose-compact :deep(strong) { font-weight: 600; color: #1f2937; }
</style>
