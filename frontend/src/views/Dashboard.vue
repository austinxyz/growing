<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50 p-4 md:p-8">
    <!-- Header -->
    <div class="mb-6 md:mb-8">
      <h1 class="text-2xl md:text-4xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
        学习仪表盘
      </h1>
      <p class="text-sm md:text-base text-gray-600 mt-2">欢迎回来，{{ displayName }}！</p>
    </div>

    <!-- 整体统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 md:gap-6 mb-6 md:mb-8">
      <!-- 职业技能 -->
      <div class="bg-white rounded-xl shadow-lg p-4 md:p-6 border-l-4 border-blue-500 hover:shadow-xl transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs md:text-sm text-gray-500 font-medium">职业技能</p>
            <h3 class="text-2xl md:text-3xl font-bold text-blue-600 mt-1 md:mt-2">{{ statistics.totalSkills }}</h3>
            <p class="text-[10px] md:text-xs text-gray-400 mt-1">Focus Area: {{ statistics.totalFocusAreas }}</p>
          </div>
          <div class="bg-blue-100 rounded-full p-2 md:p-3">
            <svg class="w-6 h-6 md:w-8 md:h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
          </div>
        </div>
      </div>

      <!-- 试题库 -->
      <div class="bg-white rounded-xl shadow-lg p-4 md:p-6 border-l-4 border-green-500 hover:shadow-xl transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs md:text-sm text-gray-500 font-medium">试题库</p>
            <h3 class="text-2xl md:text-3xl font-bold text-green-600 mt-1 md:mt-2">{{ statistics.totalQuestions }}</h3>
            <p class="text-[10px] md:text-xs text-gray-400 mt-1">道题目</p>
          </div>
          <div class="bg-green-100 rounded-full p-2 md:p-3">
            <svg class="w-6 h-6 md:w-8 md:h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
        </div>
      </div>

      <!-- 算法分类 -->
      <div class="bg-white rounded-xl shadow-lg p-4 md:p-6 border-l-4 border-purple-500 hover:shadow-xl transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs md:text-sm text-gray-500 font-medium">算法分类</p>
            <h3 class="text-2xl md:text-3xl font-bold text-purple-600 mt-1 md:mt-2">{{ statistics.algorithmCategories }}</h3>
            <p class="text-[10px] md:text-xs text-gray-400 mt-1">大分类</p>
          </div>
          <div class="bg-purple-100 rounded-full p-2 md:p-3">
            <svg class="w-6 h-6 md:w-8 md:h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
            </svg>
          </div>
        </div>
      </div>

      <!-- 系统设计基础 -->
      <div class="bg-white rounded-xl shadow-lg p-4 md:p-6 border-l-4 border-orange-500 hover:shadow-xl transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs md:text-sm text-gray-500 font-medium">系统设计基础</p>
            <h3 class="text-2xl md:text-3xl font-bold text-orange-600 mt-1 md:mt-2">{{ statistics.systemDesignBasics }}</h3>
            <p class="text-[10px] md:text-xs text-gray-400 mt-1">知识点</p>
          </div>
          <div class="bg-orange-100 rounded-full p-2 md:p-3">
            <svg class="w-6 h-6 md:w-8 md:h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01" />
            </svg>
          </div>
        </div>
      </div>

      <!-- 系统设计案例 -->
      <div class="bg-white rounded-xl shadow-lg p-4 md:p-6 border-l-4 border-pink-500 hover:shadow-xl transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs md:text-sm text-gray-500 font-medium">系统设计案例</p>
            <h3 class="text-2xl md:text-3xl font-bold text-pink-600 mt-1 md:mt-2">{{ statistics.systemDesignCases }}</h3>
            <p class="text-[10px] md:text-xs text-gray-400 mt-1">典型案例</p>
          </div>
          <div class="bg-pink-100 rounded-full p-2 md:p-3">
            <svg class="w-6 h-6 md:w-8 md:h-8 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
          </div>
        </div>
      </div>

      <!-- 算法模板库 -->
      <div class="bg-white rounded-xl shadow-lg p-4 md:p-6 border-l-4 border-indigo-500 hover:shadow-xl transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs md:text-sm text-gray-500 font-medium">算法模板库</p>
            <h3 class="text-2xl md:text-3xl font-bold text-indigo-600 mt-1 md:mt-2">{{ statistics.algorithmTemplates }}</h3>
            <p class="text-[10px] md:text-xs text-gray-400 mt-1">常用模板</p>
          </div>
          <div class="bg-indigo-100 rounded-full p-2 md:p-3">
            <svg class="w-6 h-6 md:w-8 md:h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 算法与数据结构 -->
    <div class="bg-white rounded-xl shadow-lg p-4 md:p-6 mb-6 md:mb-8">
      <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-4 md:mb-6 gap-3">
        <div>
          <h2 class="text-xl md:text-2xl font-bold text-gray-800">💻 算法与数据结构</h2>
          <p class="text-xs md:text-sm text-gray-500 mt-1" v-if="userNotes.algorithmNotesTotal > 0">
            已记录 {{ userNotes.algorithmNotesTotal }} 条笔记
            <span class="text-[10px] md:text-xs text-gray-400">
              (试题: {{ userNotes.algorithmQuestionNotes }}，模板: {{ userNotes.algorithmTemplateNotes }}，学习: {{ userNotes.algorithmLearningNotes }})
            </span>
          </p>
        </div>
        <router-link
          to="/learning-review"
          class="px-3 py-2 md:px-4 md:py-2 bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-lg hover:from-blue-600 hover:to-purple-600 transition-all text-xs md:text-sm font-semibold shadow-md whitespace-nowrap"
        >
          📖 查看学习总结
        </router-link>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="category in algorithmCategories"
          :key="category.id"
          class="bg-gradient-to-br from-blue-50 to-purple-50 rounded-lg p-4 border border-blue-200 hover:border-blue-400 transition-all hover:shadow-md"
        >
          <h3 class="font-semibold text-gray-800 mb-3">{{ category.name }}</h3>
          <div class="space-y-2 text-sm">
            <div class="flex items-center text-gray-600">
              <svg class="w-4 h-4 mr-2 text-purple-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
              <span>{{ category.focusAreaCount }} 个知识点</span>
            </div>
            <div class="flex items-center text-gray-600">
              <svg class="w-4 h-4 mr-2 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <span>{{ category.questionCount }} 道试题</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 系统设计 -->
    <div class="bg-white rounded-xl shadow-lg p-4 md:p-6">
      <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-4 md:mb-6 gap-3">
        <div>
          <h2 class="text-xl md:text-2xl font-bold text-gray-800">🏗️ 系统设计</h2>
          <p class="text-xs md:text-sm text-gray-500 mt-1" v-if="userNotes.systemDesignCaseNotes > 0">
            已答题 {{ userNotes.systemDesignCaseNotes }} 个案例
          </p>
        </div>
        <router-link
          to="/system-design/summary"
          class="px-3 py-2 md:px-4 md:py-2 bg-gradient-to-r from-orange-500 to-pink-500 text-white rounded-lg hover:from-orange-600 hover:to-pink-600 transition-all text-xs md:text-sm font-semibold shadow-md whitespace-nowrap"
        >
          📖 查看学习总结
        </router-link>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- 基础知识 -->
        <div class="bg-gradient-to-br from-orange-50 to-yellow-50 rounded-lg p-5 border border-orange-200">
          <h3 class="font-semibold text-gray-800 mb-3 flex items-center">
            <svg class="w-5 h-5 mr-2 text-orange-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
            基础知识
          </h3>
          <p class="text-sm text-gray-600 mb-3">共 {{ systemDesign.totalFocusAreas }} 个知识点</p>
          <div class="flex flex-wrap gap-2">
            <router-link
              v-for="fa in systemDesign.focusAreas"
              :key="fa.id"
              :to="`/general-skills/learning/2?focusAreaId=${fa.id}`"
              class="px-3 py-1 bg-white text-gray-700 rounded-full text-xs border border-orange-200 hover:border-orange-400 hover:bg-orange-50 transition-colors cursor-pointer"
            >
              {{ fa.name }}
            </router-link>
            <span v-if="systemDesign.totalFocusAreas > 10" class="px-3 py-1 text-gray-400 text-xs">
              ...
            </span>
          </div>
          <router-link
            to="/general-skills/learning/2"
            class="mt-4 inline-block text-sm text-orange-600 hover:text-orange-700 font-semibold"
          >
            查看全部 →
          </router-link>
        </div>

        <!-- 典型案例 -->
        <div class="bg-gradient-to-br from-pink-50 to-purple-50 rounded-lg p-5 border border-pink-200">
          <h3 class="font-semibold text-gray-800 mb-3 flex items-center">
            <svg class="w-5 h-5 mr-2 text-pink-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
            典型案例
          </h3>
          <p class="text-sm text-gray-600 mb-3">共 {{ systemDesign.totalCases }} 个经典案例</p>
          <div class="flex flex-wrap gap-2">
            <router-link
              v-for="caseItem in systemDesign.cases"
              :key="caseItem.id"
              :to="`/system-design/cases/${caseItem.id}`"
              class="px-3 py-1 bg-white text-gray-700 rounded-full text-xs border border-pink-200 hover:border-pink-400 hover:bg-pink-50 transition-colors cursor-pointer"
            >
              {{ caseItem.title }}
            </router-link>
            <span v-if="systemDesign.totalCases > 10" class="px-3 py-1 text-gray-400 text-xs">
              ...
            </span>
          </div>
          <router-link
            to="/system-design/cases"
            class="mt-4 inline-block text-sm text-pink-600 hover:text-pink-700 font-semibold"
          >
            查看全部 →
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import * as dashboardApi from '@/api/dashboardApi'

const { displayName } = useAuth()

const statistics = ref({
  totalSkills: 0,
  totalFocusAreas: 0,
  totalQuestions: 0,
  algorithmCategories: 0,
  systemDesignBasics: 0,
  systemDesignCases: 0,
  algorithmTemplates: 0
})

const algorithmCategories = ref([])
const systemDesign = ref({
  focusAreas: [],
  totalFocusAreas: 0,
  cases: [],
  totalCases: 0
})

const userNotes = ref({
  algorithmQuestionNotes: 0,
  algorithmTemplateNotes: 0,
  algorithmLearningNotes: 0,
  algorithmNotesTotal: 0,
  systemDesignCaseNotes: 0
})

const loadStatistics = async () => {
  try {
    const data = await dashboardApi.getOverallStatistics()
    statistics.value = data
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadAlgorithmCategories = async () => {
  try {
    const data = await dashboardApi.getAlgorithmCategories()
    algorithmCategories.value = data.categories || []
  } catch (error) {
    console.error('加载算法分类失败:', error)
  }
}

const loadSystemDesign = async () => {
  try {
    const data = await dashboardApi.getSystemDesignSummary()
    systemDesign.value = data
  } catch (error) {
    console.error('加载系统设计数据失败:', error)
  }
}

const loadUserNotes = async () => {
  try {
    const data = await dashboardApi.getUserNotesStatistics()
    userNotes.value = data
  } catch (error) {
    console.error('加载用户笔记统计失败:', error)
  }
}

onMounted(() => {
  loadStatistics()
  loadAlgorithmCategories()
  loadSystemDesign()
  loadUserNotes()
})
</script>
