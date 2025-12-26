<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <!-- Header -->
    <div class="bg-white shadow-sm px-6 py-4 flex justify-between items-center">
      <h1 class="text-2xl font-bold text-gray-800">系统设计案例管理</h1>
      <button
        @click="openCreateModal"
        class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors"
      >
        <span class="text-xl">+</span>
        <span>新建案例</span>
      </button>
    </div>

    <!-- Main Content: Two-column layout -->
    <div class="flex-1 flex overflow-hidden">
      <!-- Left: Case List (Narrower) -->
      <div class="w-1/4 bg-white border-r border-gray-200 flex flex-col">
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
            <div class="text-sm text-gray-600">
              <span v-if="caseItem.companyTags && caseItem.companyTags.length > 0">
                🏢 {{ caseItem.companyTags.slice(0, 2).join(', ') }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Case Details -->
      <div class="flex-1 bg-white overflow-y-auto">
        <div v-if="!selectedCase" class="h-full flex items-center justify-center text-gray-400">
          <p>← 请从左侧选择一个案例</p>
        </div>
        <div v-else class="p-6 flex flex-col h-full">
          <!-- Case Header -->
          <div class="mb-4 pb-3 border-b border-gray-200 flex-shrink-0">
            <div class="flex justify-between items-center">
              <div class="flex items-center gap-3 flex-1">
                <h2 class="text-2xl font-bold text-gray-800">{{ selectedCase.title }}</h2>
                <span
                  :class="[
                    'px-3 py-1 text-xs rounded-full',
                    selectedCase.difficulty === 'EASY' ? 'bg-green-100 text-green-700' :
                    selectedCase.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' :
                    'bg-red-100 text-red-700'
                  ]"
                >
                  {{ difficultyText(selectedCase.difficulty) }} ({{ selectedCase.difficultyRating }}/10)
                </span>
                <span
                  v-if="selectedCase.isOfficial"
                  class="px-3 py-1 text-xs rounded-full bg-blue-100 text-blue-700"
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
              <div class="flex gap-2 ml-4">
                <button
                  @click="openEditModal(selectedCase)"
                  class="px-3 py-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors text-sm"
                >
                  编辑
                </button>
                <button
                  @click="confirmDelete(selectedCase.id)"
                  class="px-3 py-1.5 text-red-600 hover:bg-red-50 rounded-lg transition-colors text-sm"
                >
                  删除
                </button>
              </div>
            </div>
          </div>

          <!-- Top Section: Case Description (Left) + Learning Resources (Right) -->
          <div class="grid grid-cols-2 gap-6 mb-6 flex-shrink-0">
            <!-- Case Description -->
            <div>
              <h3 class="text-lg font-semibold mb-3">案例描述</h3>
              <div v-if="selectedCase.caseDescription" class="prose max-w-none text-sm">
                <div v-html="renderMarkdown(selectedCase.caseDescription)"></div>
              </div>
              <p v-else class="text-gray-400 text-sm">暂无描述</p>
            </div>

            <!-- Learning Resources -->
            <div>
              <div class="flex justify-between items-center mb-3">
                <h3 class="text-lg font-semibold">学习资源 ({{ selectedCase.resources?.length || 0 }})</h3>
                <button
                  @click="openResourceModal"
                  class="text-blue-600 hover:text-blue-700 text-sm"
                >
                  + 添加
                </button>
              </div>
              <div v-if="selectedCase.resources && selectedCase.resources.length > 0" class="space-y-2">
                <div
                  v-for="resource in selectedCase.resources"
                  :key="resource.id"
                  class="p-2 bg-gray-50 rounded-lg"
                >
                  <div class="flex justify-between items-start">
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-1">
                        <span class="text-xs px-2 py-0.5 rounded bg-blue-100 text-blue-700">
                          {{ resource.resourceType }}
                        </span>
                        <a
                          :href="resource.url"
                          target="_blank"
                          class="text-sm font-medium text-blue-600 hover:underline"
                        >
                          {{ resource.title }}
                        </a>
                      </div>
                      <p v-if="resource.description" class="text-xs text-gray-600">
                        {{ truncate(resource.description, 80) }}
                      </p>
                    </div>
                    <div class="flex gap-2 ml-2">
                      <button
                        @click="openEditResourceModal(resource)"
                        class="text-blue-600 hover:text-blue-700 text-xs"
                      >
                        编辑
                      </button>
                      <button
                        @click="confirmDeleteResource(resource.id)"
                        class="text-red-600 hover:text-red-700 text-xs"
                      >
                        删除
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <p v-else class="text-gray-400 text-sm">暂无学习资源</p>
            </div>
          </div>

          <!-- Bottom Section: Reference Solutions with Tabs -->
          <div class="flex-1 flex flex-col border-t border-gray-200 pt-4 min-h-0">
            <!-- Merged: Title + Solution Selector + Actions -->
            <div v-if="selectedCase.solutions && selectedCase.solutions.length > 0" class="mb-3 flex-shrink-0">
              <div class="flex gap-3 items-center">
                <h3 class="text-lg font-semibold whitespace-nowrap">参考答案:</h3>
                <select
                  v-model="selectedSolutionId"
                  class="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-sm"
                >
                  <option
                    v-for="solution in selectedCase.solutions"
                    :key="solution.id"
                    :value="solution.id"
                  >
                    {{ solution.solutionName }} {{ solution.author ? `(${solution.author})` : '' }}
                  </option>
                </select>
                <button
                  v-if="currentSolution"
                  @click="openEditSolutionModal(currentSolution)"
                  class="text-blue-600 hover:text-blue-700 text-sm px-3 py-1.5 border border-blue-600 rounded-lg whitespace-nowrap"
                >
                  编辑
                </button>
                <button
                  v-if="currentSolution"
                  @click="confirmDeleteSolution(currentSolution.id)"
                  class="text-red-600 hover:text-red-700 text-sm px-3 py-1.5 border border-red-600 rounded-lg whitespace-nowrap"
                >
                  删除
                </button>
                <button
                  @click="openSolutionModal"
                  class="text-blue-600 hover:text-blue-700 text-sm whitespace-nowrap"
                >
                  + 添加
                </button>
              </div>
            </div>

            <!-- No solutions -->
            <div v-else class="mb-3 flex justify-between items-center flex-shrink-0">
              <h3 class="text-lg font-semibold">参考答案</h3>
              <button
                @click="openSolutionModal"
                class="text-blue-600 hover:text-blue-700 text-sm"
              >
                + 添加参考答案
              </button>
            </div>

            <div v-if="selectedCase.solutions && selectedCase.solutions.length > 0" class="flex-1 flex flex-col min-h-0">

              <!-- Step Tabs -->
              <div v-if="currentSolution" class="flex-1 flex flex-col min-h-0">
                <!-- Tab Headers -->
                <div class="flex border-b border-gray-200 flex-shrink-0">
                  <button
                    v-for="(step, index) in stepTabs"
                    :key="index"
                    @click="activeStepTab = index"
                    :class="[
                      'px-4 py-2 text-sm font-medium transition-colors',
                      activeStepTab === index
                        ? 'text-blue-600 border-b-2 border-blue-600'
                        : 'text-gray-600 hover:text-gray-800'
                    ]"
                  >
                    {{ step.label }}
                  </button>
                </div>

                <!-- Tab Content -->
                <div class="flex-1 flex flex-col py-4 min-h-0">
                  <!-- Current active step content (only render the active one) -->
                  <div class="flex-1 flex flex-col min-h-0" :key="'step-' + activeStepTab">
                    <!-- Edit Mode -->
                    <div v-if="editingStepIndex === activeStepTab" class="flex-1 flex flex-col min-h-0">
                      <div class="mb-2 flex gap-2">
                        <button
                          @click="saveStepContent(activeStepTab)"
                          class="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm"
                        >
                          保存
                        </button>
                        <button
                          @click="cancelEditStep"
                          class="px-3 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 text-sm"
                        >
                          取消
                        </button>
                      </div>
                      <textarea
                        v-model="editingStepContent"
                        class="flex-1 w-full p-3 border border-gray-300 rounded-lg font-mono text-sm resize-none"
                        placeholder="输入Markdown格式的内容..."
                      ></textarea>
                    </div>

                    <!-- View Mode -->
                    <div v-else class="flex-1 overflow-y-auto prose-compact max-w-none relative">
                      <!-- Floating Edit Button -->
                      <button
                        @click="startEditStep(activeStepTab)"
                        class="absolute top-0 right-0 px-3 py-1 text-blue-600 hover:bg-blue-50 rounded text-sm bg-white shadow-sm border border-gray-200 z-10"
                      >
                        ✏️ 编辑
                      </button>
                      <!-- Content -->
                      <div v-if="currentSolution[stepTabs[activeStepTab].field]" v-html="renderMarkdown(currentSolution[stepTabs[activeStepTab].field])"></div>
                      <p v-else class="text-gray-400">暂无内容，点击"编辑"添加内容</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <p v-else class="text-gray-400 text-sm">暂无参考答案</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <SystemDesignCaseEditModal
      :is-open="showCaseModal"
      :case-data="editingCase"
      @close="closeCaseModal"
      @save="handleSaveCase"
    />
    <CaseResourceEditModal
      :is-open="showResourceModal"
      :resource-data="editingResource"
      @close="closeResourceModal"
      @save="handleSaveResource"
    />
    <CaseSolutionEditModal
      :is-open="showSolutionModal"
      :solution-data="editingSolution"
      @close="closeSolutionModal"
      @save="handleSaveSolution"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import * as systemDesignCaseApi from '@/api/systemDesignCaseApi'
import { marked } from 'marked'
import SystemDesignCaseEditModal from '@/components/admin/SystemDesignCaseEditModal.vue'
import CaseResourceEditModal from '@/components/admin/CaseResourceEditModal.vue'
import CaseSolutionEditModal from '@/components/admin/CaseSolutionEditModal.vue'

const cases = ref([])
const selectedCase = ref(null)
const showCaseModal = ref(false)
const editingCase = ref(null)
const showResourceModal = ref(false)
const editingResource = ref(null)
const showSolutionModal = ref(false)
const editingSolution = ref(null)

// Solution tabs state
const selectedSolutionId = ref(null)
const activeStepTab = ref(0)
const editingStepIndex = ref(null)
const editingStepContent = ref('')

// Step tabs configuration
const stepTabs = [
  { label: 'Step 1: 需求', field: 'step1Requirements' },
  { label: 'Step 2: 实体', field: 'step2Entities' },
  { label: 'Step 3: API', field: 'step3Api' },
  { label: 'Step 4: 数据流', field: 'step4DataFlow' },
  { label: 'Step 5: 架构', field: 'step5Architecture' },
  { label: 'Step 6: 深入', field: 'step6DeepDive' }
]

// Computed: current selected solution
const currentSolution = computed(() => {
  if (!selectedCase.value?.solutions || !selectedSolutionId.value) return null
  return selectedCase.value.solutions.find(s => s.id === selectedSolutionId.value)
})

// Watch: auto-select first solution when case changes
watch(() => selectedCase.value?.solutions, (solutions) => {
  if (solutions && solutions.length > 0) {
    selectedSolutionId.value = solutions[0].id
  } else {
    selectedSolutionId.value = null
  }
  activeStepTab.value = 0
  editingStepIndex.value = null
}, { immediate: true })

// Watch: cancel edit when switching tabs or solutions
watch([activeStepTab, selectedSolutionId], () => {
  editingStepIndex.value = null
  editingStepContent.value = ''
})

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
    const data = await systemDesignCaseApi.getAllCases()
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
  } catch (error) {
    console.error('加载案例详情失败:', error)
  }
}

const openCreateModal = () => {
  editingCase.value = null
  showCaseModal.value = true
}

const openEditModal = (caseItem) => {
  editingCase.value = caseItem
  showCaseModal.value = true
}

const closeCaseModal = () => {
  showCaseModal.value = false
  editingCase.value = null
}

const handleSaveCase = async (formData) => {
  try {
    if (formData.id) {
      // Update existing case
      await systemDesignCaseApi.updateCase(formData.id, formData)
      await loadCases()
      if (selectedCase.value?.id === formData.id) {
        await selectCase(selectedCase.value)
      }
    } else {
      // Create new case
      await systemDesignCaseApi.createCase(formData)
      await loadCases()
    }
  } catch (error) {
    console.error('保存案例失败:', error)
    alert('保存失败，请重试')
  }
}

const openResourceModal = () => {
  editingResource.value = null
  showResourceModal.value = true
}

const openEditResourceModal = (resource) => {
  editingResource.value = resource
  showResourceModal.value = true
}

const closeResourceModal = () => {
  showResourceModal.value = false
  editingResource.value = null
}

const handleSaveResource = async (formData) => {
  try {
    if (formData.id) {
      // Update existing resource
      await systemDesignCaseApi.updateResource(selectedCase.value.id, formData.id, formData)
    } else {
      // Create new resource
      await systemDesignCaseApi.createResource(selectedCase.value.id, formData)
    }
    // Reload case details to show updated resources
    await selectCase(selectedCase.value)
  } catch (error) {
    console.error('保存资源失败:', error)
    alert('保存失败，请重试')
  }
}

const openSolutionModal = () => {
  editingSolution.value = null
  showSolutionModal.value = true
}

const openEditSolutionModal = (solution) => {
  editingSolution.value = solution
  showSolutionModal.value = true
}

const closeSolutionModal = () => {
  showSolutionModal.value = false
  editingSolution.value = null
}

const handleSaveSolution = async (formData) => {
  try {
    if (formData.id) {
      // Update existing solution
      await systemDesignCaseApi.updateSolution(formData.id, formData)
    } else {
      // Create new solution
      await systemDesignCaseApi.createSolution(selectedCase.value.id, formData)
    }
    // Reload case details to show updated solutions
    await selectCase(selectedCase.value)
  } catch (error) {
    console.error('保存答案失败:', error)
    alert('保存失败，请重试')
  }
}

const confirmDelete = async (id) => {
  if (!confirm('确定要删除这个案例吗?')) return

  try {
    await systemDesignCaseApi.deleteCase(id)
    await loadCases()
    selectedCase.value = null
  } catch (error) {
    console.error('删除案例失败:', error)
    alert('删除失败,请重试')
  }
}

const confirmDeleteResource = async (resourceId) => {
  if (!confirm('确定要删除这个学习资源吗?')) return

  try {
    await systemDesignCaseApi.deleteResource(selectedCase.value.id, resourceId)
    await selectCase(selectedCase.value)
  } catch (error) {
    console.error('删除资源失败:', error)
    alert('删除失败,请重试')
  }
}

const confirmDeleteSolution = async (solutionId) => {
  if (!confirm('确定要删除这个参考答案吗?')) return

  try {
    await systemDesignCaseApi.deleteSolution(solutionId)
    await selectCase(selectedCase.value)
  } catch (error) {
    console.error('删除答案失败:', error)
    alert('删除失败,请重试')
  }
}

// Step editing functions
const startEditStep = (stepIndex) => {
  if (!currentSolution.value) return

  const field = stepTabs[stepIndex].field
  editingStepIndex.value = stepIndex
  editingStepContent.value = currentSolution.value[field] || ''
}

const cancelEditStep = () => {
  editingStepIndex.value = null
  editingStepContent.value = ''
}

const saveStepContent = async (stepIndex) => {
  if (!currentSolution.value) return

  try {
    const field = stepTabs[stepIndex].field
    const updateData = {
      ...currentSolution.value,
      [field]: editingStepContent.value
    }

    await systemDesignCaseApi.updateSolution(currentSolution.value.id, updateData)
    await selectCase(selectedCase.value)

    editingStepIndex.value = null
    editingStepContent.value = ''
  } catch (error) {
    console.error('保存步骤内容失败:', error)
    alert('保存失败，请重试')
  }
}

onMounted(() => {
  loadCases()
})
</script>

<style scoped>
.prose {
  color: #374151;
}
.prose h1, .prose h2, .prose h3, .prose h4 {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}
.prose ul, .prose ol {
  padding-left: 1.5em;
}
.prose code {
  background-color: #f3f4f6;
  padding: 0.125rem 0.25rem;
  border-radius: 0.25rem;
  font-size: 0.875em;
}

/* Compact prose mode for tab content */
.prose-compact {
  color: #374151;
  font-size: 0.875rem;
  line-height: 1.5;
}
.prose-compact :deep(h1) {
  font-size: 1.25rem;
  font-weight: 700;
  margin-top: 0.75em;
  margin-bottom: 0.5em;
  color: #1f2937;
}
.prose-compact :deep(h2) {
  font-size: 1.125rem;
  font-weight: 600;
  margin-top: 0.75em;
  margin-bottom: 0.5em;
  color: #1f2937;
}
.prose-compact :deep(h3) {
  font-size: 1rem;
  font-weight: 600;
  margin-top: 0.5em;
  margin-bottom: 0.375em;
  color: #374151;
}
.prose-compact :deep(h4) {
  font-size: 0.875rem;
  font-weight: 600;
  margin-top: 0.5em;
  margin-bottom: 0.25em;
  color: #374151;
}
.prose-compact :deep(p) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}
.prose-compact :deep(ul),
.prose-compact :deep(ol) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  padding-left: 1.5em;
}
.prose-compact :deep(li) {
  margin-top: 0.25em;
  margin-bottom: 0.25em;
}
.prose-compact :deep(code) {
  background-color: #f3f4f6;
  padding: 0.125rem 0.25rem;
  border-radius: 0.25rem;
  font-size: 0.8125em;
  font-family: 'Monaco', 'Courier New', monospace;
}
.prose-compact :deep(pre) {
  background-color: #f9fafb;
  padding: 0.75rem;
  border-radius: 0.375rem;
  overflow-x: auto;
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  border: 1px solid #e5e7eb;
}
.prose-compact :deep(pre code) {
  background-color: transparent;
  padding: 0;
  font-size: 0.8125em;
}
.prose-compact :deep(strong) {
  font-weight: 600;
  color: #1f2937;
}
.prose-compact :deep(hr) {
  margin-top: 1em;
  margin-bottom: 1em;
  border-color: #e5e7eb;
}
.prose-compact :deep(table) {
  font-size: 0.8125em;
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}
.prose-compact :deep(blockquote) {
  border-left: 3px solid #3b82f6;
  padding-left: 0.75em;
  margin-left: 0;
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  color: #6b7280;
  font-style: italic;
}
</style>
