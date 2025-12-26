<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <!-- Header -->
    <div class="bg-white shadow-sm px-6 py-4">
      <h1 class="text-2xl font-bold text-gray-800">系统设计 - 学习总结</h1>
    </div>

    <!-- Main Content -->
    <div class="flex-1 overflow-auto p-6">
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider sticky left-0 bg-gray-50 z-10 border-r border-gray-200">
                案例名
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                Requirement
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                NFR
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                Entity
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                Components
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                API
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                Object1
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                Object2
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="row in summaryData" :key="row.caseId" class="hover:bg-gray-50">
              <td class="px-4 py-3 text-sm font-medium sticky left-0 bg-white border-r border-gray-200">
                <router-link
                  :to="{ name: 'SystemDesignCases', query: { caseId: row.caseId } }"
                  class="text-blue-600 hover:text-blue-800 hover:underline transition-colors"
                >
                  {{ row.caseName }}
                </router-link>
              </td>
              <td class="px-4 py-3 text-xs text-gray-600 max-w-xs align-top">
                <div class="whitespace-pre-wrap">{{ row.kpRequirement || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-600 max-w-xs align-top">
                <div class="whitespace-pre-wrap">{{ row.kpNfr || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-600 max-w-xs align-top">
                <div class="whitespace-pre-wrap">{{ row.kpEntity || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-600 max-w-xs align-top">
                <div class="whitespace-pre-wrap">{{ row.kpComponents || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-600 max-w-xs align-top">
                <div class="whitespace-pre-wrap">{{ row.kpApi || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-600 max-w-xs align-top">
                <div class="whitespace-pre-wrap">{{ row.kpObject1 || '-' }}</div>
              </td>
              <td class="px-4 py-3 text-xs text-gray-600 max-w-xs align-top">
                <div class="whitespace-pre-wrap">{{ row.kpObject2 || '-' }}</div>
              </td>
            </tr>
            <tr v-if="summaryData.length === 0">
              <td colspan="8" class="px-4 py-8 text-center text-gray-400">
                暂无学习记录
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
