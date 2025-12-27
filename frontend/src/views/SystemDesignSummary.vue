<template>
  <div class="h-screen flex flex-col bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- Header with Filters -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg px-6 py-4 no-print">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-white">系统设计 - 学习总结</h1>
          <p class="text-xs text-blue-100 mt-1">共 {{ filteredData.length }} 个案例</p>
        </div>

        <!-- Filters -->
        <div class="flex items-center gap-3">
          <!-- Print Button -->
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

          <!-- Difficulty Filter -->
          <select
            v-model="selectedDifficulty"
            class="text-sm px-3 py-1.5 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-white bg-white/90 text-gray-900 shadow-sm"
          >
            <option value="">所有难度</option>
            <option value="EASY">简单</option>
            <option value="MEDIUM">中等</option>
            <option value="HARD">困难</option>
          </select>

          <!-- Focus Area Filter -->
          <select
            v-model="selectedFocusAreaId"
            class="text-sm px-3 py-1.5 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-white bg-white/90 text-gray-900 shadow-sm"
          >
            <option value="">所有知识领域</option>
            <option v-for="fa in allFocusAreas" :key="fa.id" :value="fa.id">
              {{ fa.name }}
            </option>
          </select>

          <!-- Search by Case Name -->
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索案例名"
            class="text-sm px-3 py-1.5 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-white bg-white/90 text-gray-900 shadow-sm w-32"
          />

          <!-- Reset Button -->
          <button
            @click="resetFilters"
            class="text-sm px-3 py-1.5 bg-white/90 text-gray-700 rounded-lg hover:bg-white hover:shadow-md transition-all font-medium"
          >
            重置
          </button>
        </div>
      </div>
    </div>

    <!-- 打印用的标题（仅在打印时显示） -->
    <div class="print-only">
      <h1 class="text-center text-lg font-bold mb-2">系统设计学习总结</h1>
      <p class="text-center text-xs text-gray-600 mb-3">共 {{ filteredData.length }} 个案例</p>
    </div>

    <!-- Main Content -->
    <div class="flex-1 overflow-auto p-6 no-print">
      <div class="bg-white rounded-xl shadow-lg border-2 border-blue-100 overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gradient-to-r from-blue-100 to-purple-100">
            <tr>
              <th class="px-2 py-2 text-left text-[10px] font-semibold text-blue-900 uppercase tracking-wider sticky left-0 bg-gradient-to-r from-blue-100 to-purple-100 z-10 border-r-2 border-blue-200">
                案例名
              </th>
              <th class="px-2 py-2 text-left text-[10px] font-semibold text-blue-900 uppercase tracking-wider">
                📋 Requirement
              </th>
              <th class="px-2 py-2 text-left text-[10px] font-semibold text-green-900 uppercase tracking-wider">
                ⚡ NFR
              </th>
              <th class="px-2 py-2 text-left text-[10px] font-semibold text-yellow-900 uppercase tracking-wider">
                📦 Entity
              </th>
              <th class="px-2 py-2 text-left text-[10px] font-semibold text-purple-900 uppercase tracking-wider">
                🧩 Components
              </th>
              <th class="px-2 py-2 text-left text-[10px] font-semibold text-red-900 uppercase tracking-wider">
                🔌 API
              </th>
              <th class="px-2 py-2 text-left text-[10px] font-semibold text-indigo-900 uppercase tracking-wider">
                🎯 Object1
              </th>
              <th class="px-2 py-2 text-left text-[10px] font-semibold text-pink-900 uppercase tracking-wider">
                🎯 Object2
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="row in filteredData" :key="row.caseId" class="hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 transition-all">
              <td class="px-2 py-2 text-[11px] font-semibold sticky left-0 bg-white border-r-2 border-blue-200 hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 transition-all">
                <router-link
                  :to="{ name: 'SystemDesignCases', query: { caseId: row.caseId } }"
                  class="text-blue-600 hover:text-blue-800 hover:underline transition-colors font-bold"
                >
                  {{ row.caseName }}
                </router-link>
              </td>
              <td class="px-2 py-2 text-gray-700 max-w-xs align-top">
                <div class="prose-compact bg-blue-50/30 rounded p-1.5" v-html="renderMarkdown(row.kpRequirement)"></div>
              </td>
              <td class="px-2 py-2 text-gray-700 max-w-xs align-top">
                <div class="prose-compact bg-green-50/30 rounded p-1.5" v-html="renderMarkdown(row.kpNfr)"></div>
              </td>
              <td class="px-2 py-2 text-gray-700 max-w-xs align-top">
                <div class="prose-compact bg-yellow-50/30 rounded p-1.5" v-html="renderMarkdown(row.kpEntity)"></div>
              </td>
              <td class="px-2 py-2 text-gray-700 max-w-xs align-top">
                <div class="prose-compact bg-purple-50/30 rounded p-1.5" v-html="renderMarkdown(row.kpComponents)"></div>
              </td>
              <td class="px-2 py-2 text-gray-700 max-w-xs align-top">
                <div class="prose-compact bg-red-50/30 rounded p-1.5" v-html="renderMarkdown(row.kpApi)"></div>
              </td>
              <td class="px-2 py-2 text-gray-700 max-w-xs align-top">
                <div class="prose-compact bg-indigo-50/30 rounded p-1.5" v-html="renderMarkdown(row.kpObject1)"></div>
              </td>
              <td class="px-2 py-2 text-gray-700 max-w-xs align-top">
                <div class="prose-compact bg-pink-50/30 rounded p-1.5" v-html="renderMarkdown(row.kpObject2)"></div>
              </td>
            </tr>
            <tr v-if="filteredData.length === 0">
              <td colspan="8" class="px-4 py-12 text-center">
                <div class="text-blue-400 text-4xl mb-2">📊</div>
                <h3 class="text-lg font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600 mb-1">
                  {{ summaryData.length === 0 ? '暂无学习记录' : '没有符合条件的案例' }}
                </h3>
                <p class="text-sm text-gray-600">
                  {{ summaryData.length === 0 ? '去「系统设计案例」页面开始学习吧！' : '试试调整过滤条件' }}
                </p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 打印用的表格（仅在打印时显示） -->
    <div v-if="filteredData.length > 0" class="print-only">
      <table class="print-table">
        <thead>
          <tr>
            <th class="w-case">案例名</th>
            <th class="w-req">📋 Requirement</th>
            <th class="w-nfr">⚡ NFR</th>
            <th class="w-entity">📦 Entity</th>
            <th class="w-comp">🧩 Components</th>
            <th class="w-api">🔌 API</th>
            <th class="w-obj1">🎯 Object1</th>
            <th class="w-obj2">🎯 Object2</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredData" :key="row.caseId">
            <td class="font-semibold">{{ row.caseName }}</td>
            <td><div class="print-markdown" v-html="renderMarkdown(row.kpRequirement)"></div></td>
            <td><div class="print-markdown" v-html="renderMarkdown(row.kpNfr)"></div></td>
            <td><div class="print-markdown" v-html="renderMarkdown(row.kpEntity)"></div></td>
            <td><div class="print-markdown" v-html="renderMarkdown(row.kpComponents)"></div></td>
            <td><div class="print-markdown" v-html="renderMarkdown(row.kpApi)"></div></td>
            <td><div class="print-markdown" v-html="renderMarkdown(row.kpObject1)"></div></td>
            <td><div class="print-markdown" v-html="renderMarkdown(row.kpObject2)"></div></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import * as systemDesignCaseApi from '@/api/systemDesignCaseApi'
import majorCategoryApi from '@/api/majorCategoryApi'
import { marked } from 'marked'

const summaryData = ref([])
const allFocusAreas = ref([])

// Filter states
const selectedDifficulty = ref('')
const selectedFocusAreaId = ref('')
const searchKeyword = ref('')

const renderMarkdown = (markdown) => {
  if (!markdown) return '-'
  // Convert escaped newlines to actual newlines
  const processedMarkdown = markdown.replace(/\\n/g, '\n')
  return marked(processedMarkdown)
}

// Computed filtered data
const filteredData = computed(() => {
  let result = summaryData.value

  // Filter by difficulty
  if (selectedDifficulty.value) {
    result = result.filter(row => row.difficulty === selectedDifficulty.value)
  }

  // Filter by focus area
  if (selectedFocusAreaId.value) {
    result = result.filter(row => {
      // row.relatedFocusAreas is an array of focus area IDs
      return row.relatedFocusAreas && row.relatedFocusAreas.includes(parseInt(selectedFocusAreaId.value))
    })
  }

  // Filter by search keyword (case name)
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    result = result.filter(row =>
      row.caseName && row.caseName.toLowerCase().includes(keyword)
    )
  }

  return result
})

const resetFilters = () => {
  selectedDifficulty.value = ''
  selectedFocusAreaId.value = ''
  searchKeyword.value = ''
}

/**
 * 打印学习总结
 */
const printSummary = () => {
  window.print()
}

const loadFocusAreas = async () => {
  try {
    const SYSTEM_DESIGN_SKILL_ID = 2
    allFocusAreas.value = await majorCategoryApi.getFocusAreasWithCategories(SYSTEM_DESIGN_SKILL_ID)
  } catch (error) {
    console.error('加载知识领域失败:', error)
  }
}

const loadSummary = async () => {
  try {
    const data = await systemDesignCaseApi.getMySummary()
    summaryData.value = data || []
  } catch (error) {
    console.error('加载学习总结失败:', error)
  }
}

onMounted(() => {
  loadFocusAreas()
  loadSummary()
})
</script>

<style scoped>
/* 固定第一列 */
th.sticky,
td.sticky {
  position: sticky;
  left: 0;
  z-index: 5;
}

thead th.sticky {
  z-index: 10;
}

/* Markdown rendering - 紧凑显示 */
.prose-compact {
  color: #374151;
  font-size: 0.7rem;
  line-height: 1.4;
}
.prose-compact :deep(h1) { font-size: 0.95rem; font-weight: 700; margin-top: 0.5em; margin-bottom: 0.3em; color: #1f2937; }
.prose-compact :deep(h2) { font-size: 0.875rem; font-weight: 600; margin-top: 0.5em; margin-bottom: 0.3em; color: #1f2937; }
.prose-compact :deep(h3) { font-size: 0.8rem; font-weight: 600; margin-top: 0.4em; margin-bottom: 0.25em; color: #374151; }
.prose-compact :deep(p) { margin-top: 0.3em; margin-bottom: 0.3em; }
.prose-compact :deep(ul), .prose-compact :deep(ol) { margin-top: 0.3em; margin-bottom: 0.3em; padding-left: 1.2em; }
.prose-compact :deep(li) { margin-top: 0.15em; margin-bottom: 0.15em; }
.prose-compact :deep(code) { background-color: #f3f4f6; padding: 0.1rem 0.2rem; border-radius: 0.2rem; font-size: 0.75em; }
.prose-compact :deep(strong) { font-weight: 600; color: #1f2937; }

/* 屏幕显示时隐藏打印内容 */
.print-only {
  display: none;
}

/* 打印时的样式 */
@media print {
  /* 隐藏屏幕显示的内容 */
  .no-print {
    display: none !important;
  }

  /* 显示打印内容 */
  .print-only {
    display: block !important;
  }

  /* 打印页面设置 */
  @page {
    size: A4 landscape;
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
    font-size: 6pt;
    line-height: 1.2;
  }

  .print-table th {
    background-color: #f3f4f6;
    border: 1px solid #d1d5db;
    padding: 2px 3px;
    text-align: left;
    font-weight: 600;
    font-size: 6pt;
  }

  .print-table td {
    border: 1px solid #e5e7eb;
    padding: 2px 3px;
    font-size: 5pt;
    vertical-align: top;
  }

  /* 打印时的Markdown样式 - 紧凑模式 */
  .print-markdown {
    color: #374151;
    font-size: 5pt;
    line-height: 1.3;
  }
  .print-markdown :deep(h1) { font-size: 6pt; font-weight: 700; margin: 1px 0; }
  .print-markdown :deep(h2) { font-size: 5.5pt; font-weight: 600; margin: 1px 0; }
  .print-markdown :deep(h3) { font-size: 5pt; font-weight: 600; margin: 0.5px 0; }
  .print-markdown :deep(p) { margin: 0.5px 0; }
  .print-markdown :deep(ul), .print-markdown :deep(ol) { margin: 1px 0; padding-left: 8px; }
  .print-markdown :deep(li) { margin: 0.3px 0; }
  .print-markdown :deep(code) { background-color: #f3f4f6; padding: 0.5px 1px; font-size: 4.5pt; }
  .print-markdown :deep(strong) { font-weight: 600; }
  .print-markdown :deep(br) { display: block; margin: 0; }

  /* 表格行跨页控制 - 避免行被截断 */
  .print-table tr {
    page-break-inside: avoid;
    break-inside: avoid;
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
  .print-table th.w-case,
  .print-table td:nth-child(1) {
    width: 12%;
  }

  .print-table th.w-req,
  .print-table td:nth-child(2) {
    width: 13%;
  }

  .print-table th.w-nfr,
  .print-table td:nth-child(3) {
    width: 13%;
  }

  .print-table th.w-entity,
  .print-table td:nth-child(4) {
    width: 12%;
  }

  .print-table th.w-comp,
  .print-table td:nth-child(5) {
    width: 15%;
  }

  .print-table th.w-api,
  .print-table td:nth-child(6) {
    width: 12%;
  }

  .print-table th.w-obj1,
  .print-table td:nth-child(7) {
    width: 11.5%;
  }

  .print-table th.w-obj2,
  .print-table td:nth-child(8) {
    width: 11.5%;
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
