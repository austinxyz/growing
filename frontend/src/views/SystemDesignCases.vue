<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-7xl mx-auto">
      <!-- Page Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">系统设计典型案例</h1>
        <p class="text-gray-600">通过真实案例学习系统设计,掌握6步解题框架</p>
      </div>

      <!-- Case Grid -->
      <div v-if="cases.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="caseItem in cases"
          :key="caseItem.id"
          @click="goToCaseDetail(caseItem.id)"
          class="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow cursor-pointer overflow-hidden border border-gray-200"
        >
          <!-- Card Header -->
          <div class="p-5 border-b border-gray-100">
            <div class="flex justify-between items-start mb-3">
              <h3 class="text-lg font-semibold text-gray-900 flex-1 pr-2">
                {{ caseItem.title }}
              </h3>
              <span
                :class="[
                  'px-2 py-1 text-xs font-medium rounded whitespace-nowrap',
                  caseItem.difficulty === 'EASY' ? 'bg-green-100 text-green-700' :
                  caseItem.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' :
                  'bg-red-100 text-red-700'
                ]"
              >
                {{ difficultyText(caseItem.difficulty) }}
              </span>
            </div>

            <!-- Company Tags -->
            <div v-if="caseItem.companyTags && caseItem.companyTags.length > 0" class="flex flex-wrap gap-2 mb-3">
              <span
                v-for="tag in caseItem.companyTags.slice(0, 4)"
                :key="tag"
                class="px-2 py-1 text-xs bg-blue-50 text-blue-700 rounded"
              >
                🏢 {{ tag }}
              </span>
            </div>

            <!-- Description Preview -->
            <p v-if="caseItem.caseDescription" class="text-sm text-gray-600 line-clamp-2">
              {{ stripMarkdown(caseItem.caseDescription) }}
            </p>
          </div>

          <!-- Card Footer -->
          <div class="px-5 py-3 bg-gray-50 flex justify-between items-center text-sm">
            <div class="flex items-center gap-4">
              <span class="text-gray-600">
                📚 {{ caseItem.resources?.length || 0 }} 资源
              </span>
              <span class="text-gray-600">
                ✅ {{ caseItem.solutions?.length || 0 }} 答案
              </span>
            </div>
            <span
              v-if="caseItem.isOfficial"
              class="px-2 py-1 text-xs bg-blue-100 text-blue-700 rounded"
            >
              官方
            </span>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-else
        class="bg-white rounded-lg shadow-sm p-12 text-center"
      >
        <div class="text-gray-400 mb-4">
          <svg class="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
        </div>
        <p class="text-gray-600 text-lg">暂无案例</p>
        <p class="text-gray-500 text-sm mt-2">请联系管理员添加系统设计案例</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as systemDesignCaseApi from '@/api/systemDesignCaseApi'

const router = useRouter()
const cases = ref([])

const difficultyText = (difficulty) => {
  const map = { EASY: '简单', MEDIUM: '中等', HARD: '困难' }
  return map[difficulty] || difficulty
}

const stripMarkdown = (markdown) => {
  if (!markdown) return ''
  // Remove markdown syntax for preview
  return markdown
    .replace(/#{1,6}\s/g, '')
    .replace(/\*\*/g, '')
    .replace(/\*/g, '')
    .replace(/\[([^\]]+)\]\([^\)]+\)/g, '$1')
    .replace(/`/g, '')
}

const loadCases = async () => {
  try {
    const data = await systemDesignCaseApi.getOfficialCases()
    cases.value = data || []
  } catch (error) {
    console.error('加载案例列表失败:', error)
  }
}

const goToCaseDetail = (caseId) => {
  // TODO: Navigate to case detail page
  alert(`案例详情页面开发中... (Case ID: ${caseId})`)
}

onMounted(() => {
  loadCases()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
