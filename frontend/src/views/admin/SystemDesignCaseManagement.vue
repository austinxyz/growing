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
      <!-- Left: Case List -->
      <div class="w-1/3 bg-white border-r border-gray-200 flex flex-col">
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
                🏢 {{ caseItem.companyTags.slice(0, 3).join(', ') }}
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
        <div v-else class="p-6">
          <!-- Case Header -->
          <div class="mb-6 pb-6 border-b border-gray-200">
            <div class="flex justify-between items-start mb-4">
              <div class="flex-1">
                <h2 class="text-2xl font-bold text-gray-800 mb-2">{{ selectedCase.title }}</h2>
                <div class="flex gap-2 flex-wrap">
                  <span
                    :class="[
                      'px-3 py-1 text-sm rounded-full',
                      selectedCase.difficulty === 'EASY' ? 'bg-green-100 text-green-700' :
                      selectedCase.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' :
                      'bg-red-100 text-red-700'
                    ]"
                  >
                    {{ difficultyText(selectedCase.difficulty) }} ({{ selectedCase.difficultyRating }}/10)
                  </span>
                  <span
                    v-if="selectedCase.isOfficial"
                    class="px-3 py-1 text-sm rounded-full bg-blue-100 text-blue-700"
                  >
                    官方案例
                  </span>
                  <span
                    v-for="tag in selectedCase.companyTags"
                    :key="tag"
                    class="px-3 py-1 text-sm rounded-full bg-gray-100 text-gray-700"
                  >
                    🏢 {{ tag }}
                  </span>
                </div>
              </div>
              <div class="flex gap-2">
                <button
                  @click="openEditModal(selectedCase)"
                  class="px-4 py-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                >
                  编辑
                </button>
                <button
                  @click="confirmDelete(selectedCase.id)"
                  class="px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                >
                  删除
                </button>
              </div>
            </div>

            <!-- Case Description -->
            <div v-if="selectedCase.caseDescription" class="prose max-w-none">
              <h3 class="text-lg font-semibold mb-2">案例描述</h3>
              <div v-html="renderMarkdown(selectedCase.caseDescription)"></div>
            </div>
          </div>

          <!-- Learning Resources Section -->
          <div class="mb-6 pb-6 border-b border-gray-200">
            <div class="flex justify-between items-center mb-4">
              <h3 class="text-lg font-semibold">学习资源 ({{ selectedCase.resources?.length || 0 }})</h3>
              <button
                @click="openResourceModal"
                class="text-blue-600 hover:text-blue-700 text-sm"
              >
                + 添加资源
              </button>
            </div>
            <div v-if="selectedCase.resources && selectedCase.resources.length > 0" class="space-y-2">
              <div
                v-for="resource in selectedCase.resources"
                :key="resource.id"
                class="p-3 bg-gray-50 rounded-lg flex justify-between items-start"
              >
                <div class="flex-1">
                  <div class="flex items-center gap-2 mb-1">
                    <span class="text-xs px-2 py-1 rounded bg-blue-100 text-blue-700">
                      {{ resource.resourceType }}
                    </span>
                    <a
                      :href="resource.url"
                      target="_blank"
                      class="font-medium text-blue-600 hover:underline"
                    >
                      {{ resource.title }}
                    </a>
                  </div>
                  <p v-if="resource.description" class="text-sm text-gray-600">
                    {{ resource.description }}
                  </p>
                </div>
                <button
                  @click="confirmDeleteResource(resource.id)"
                  class="text-red-600 hover:text-red-700 text-sm ml-4"
                >
                  删除
                </button>
              </div>
            </div>
            <p v-else class="text-gray-400 text-sm">暂无学习资源</p>
          </div>

          <!-- Reference Solutions Section -->
          <div>
            <div class="flex justify-between items-center mb-4">
              <h3 class="text-lg font-semibold">参考答案 ({{ selectedCase.solutions?.length || 0 }})</h3>
              <button
                @click="openSolutionModal"
                class="text-blue-600 hover:text-blue-700 text-sm"
              >
                + 添加参考答案
              </button>
            </div>
            <div v-if="selectedCase.solutions && selectedCase.solutions.length > 0" class="space-y-4">
              <div
                v-for="solution in selectedCase.solutions"
                :key="solution.id"
                class="p-4 border border-gray-200 rounded-lg"
              >
                <div class="flex justify-between items-start mb-3">
                  <div>
                    <h4 class="font-semibold text-gray-800">{{ solution.solutionName }}</h4>
                    <p v-if="solution.author" class="text-sm text-gray-600">作者: {{ solution.author }}</p>
                  </div>
                  <button
                    @click="confirmDeleteSolution(solution.id)"
                    class="text-red-600 hover:text-red-700 text-sm"
                  >
                    删除
                  </button>
                </div>
                <div class="space-y-2 text-sm">
                  <div v-if="solution.step1Requirements">
                    <span class="font-medium">步骤1-需求澄清:</span>
                    <span class="text-gray-600 ml-2">{{ truncate(solution.step1Requirements, 100) }}</span>
                  </div>
                  <div v-if="solution.step2Entities">
                    <span class="font-medium">步骤2-核心实体:</span>
                    <span class="text-gray-600 ml-2">{{ truncate(solution.step2Entities, 100) }}</span>
                  </div>
                  <div v-if="solution.step3Api">
                    <span class="font-medium">步骤3-API设计:</span>
                    <span class="text-gray-600 ml-2">{{ truncate(solution.step3Api, 100) }}</span>
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
    <!-- TODO: Create modals for case/resource/solution CRUD -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as systemDesignCaseApi from '@/api/systemDesignCaseApi'
import { marked } from 'marked'

const cases = ref([])
const selectedCase = ref(null)

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
  // TODO: Implement create modal
  alert('创建案例功能开发中...')
}

const openEditModal = (caseItem) => {
  // TODO: Implement edit modal
  alert(`编辑案例: ${caseItem.title}`)
}

const openResourceModal = () => {
  // TODO: Implement resource modal
  alert('添加学习资源功能开发中...')
}

const openSolutionModal = () => {
  // TODO: Implement solution modal
  alert('添加参考答案功能开发中...')
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
</style>
