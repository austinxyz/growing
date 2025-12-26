<template>
  <div class="h-screen flex flex-col bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- Header - 添加渐变背景 -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg px-6 py-4">
      <h1 class="text-2xl font-bold text-white">系统设计 - 学习总结</h1>
      <p class="text-xs text-blue-100 mt-1">系统设计案例关键点总结</p>
    </div>

    <!-- Main Content -->
    <div class="flex-1 overflow-auto p-6">
      <div class="bg-white rounded-xl shadow-lg border-2 border-blue-100 overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gradient-to-r from-blue-100 to-purple-100">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-semibold text-blue-900 uppercase tracking-wider sticky left-0 bg-gradient-to-r from-blue-100 to-purple-100 z-10 border-r-2 border-blue-200">
                案例名
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-blue-900 uppercase tracking-wider">
                📋 Requirement
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-green-900 uppercase tracking-wider">
                ⚡ NFR
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-yellow-900 uppercase tracking-wider">
                📦 Entity
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-purple-900 uppercase tracking-wider">
                🧩 Components
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-red-900 uppercase tracking-wider">
                🔌 API
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-indigo-900 uppercase tracking-wider">
                🎯 Object1
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-pink-900 uppercase tracking-wider">
                🎯 Object2
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="row in summaryData" :key="row.caseId" class="hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 transition-all">
              <td class="px-4 py-3 text-sm font-semibold sticky left-0 bg-white border-r-2 border-blue-200 hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 transition-all">
                <router-link
                  :to="{ name: 'SystemDesignCases', query: { caseId: row.caseId } }"
                  class="text-blue-600 hover:text-blue-800 hover:underline transition-colors font-bold"
                >
                  {{ row.caseName }}
                </router-link>
              </td>
              <td class="px-4 py-3 text-xs text-gray-700 max-w-xs align-top">
                <div class="whitespace-pre-wrap bg-blue-50/30 rounded p-2">{{ row.kpRequirement || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-700 max-w-xs align-top">
                <div class="whitespace-pre-wrap bg-green-50/30 rounded p-2">{{ row.kpNfr || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-700 max-w-xs align-top">
                <div class="whitespace-pre-wrap bg-yellow-50/30 rounded p-2">{{ row.kpEntity || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-700 max-w-xs align-top">
                <div class="whitespace-pre-wrap bg-purple-50/30 rounded p-2">{{ row.kpComponents || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-700 max-w-xs align-top">
                <div class="whitespace-pre-wrap bg-red-50/30 rounded p-2">{{ row.kpApi || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-700 max-w-xs align-top">
                <div class="whitespace-pre-wrap bg-indigo-50/30 rounded p-2">{{ row.kpObject1 || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-700 max-w-xs align-top">
                <div class="whitespace-pre-wrap bg-pink-50/30 rounded p-2">{{ row.kpObject2 || '-' }}</div>
              </td>
            </tr>
            <tr v-if="summaryData.length === 0">
              <td colspan="8" class="px-4 py-12 text-center">
                <div class="text-blue-400 text-4xl mb-2">📊</div>
                <h3 class="text-lg font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600 mb-1">暂无学习记录</h3>
                <p class="text-sm text-gray-600">去「系统设计案例」页面开始学习吧！</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as systemDesignCaseApi from '@/api/systemDesignCaseApi'

const summaryData = ref([])

const loadSummary = async () => {
  try {
    const data = await systemDesignCaseApi.getMySummary()
    summaryData.value = data || []
  } catch (error) {
    console.error('加载学习总结失败:', error)
  }
}

onMounted(() => {
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
</style>
