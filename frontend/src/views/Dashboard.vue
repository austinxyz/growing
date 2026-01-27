<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50 p-8">
    <div class="max-w-7xl mx-auto">
      <!-- 标题 -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">学习仪表盘</h1>
        <p class="text-gray-600">欢迎回来，{{ displayName }}！快速查看您的学习进度</p>
      </div>

      <!-- 统计卡片网格 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <!-- 职业技能卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-blue-500 to-blue-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-blue-100 text-sm font-medium">职业技能</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ statistics.totalSkills }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-blue-50">
            <div class="flex items-center justify-between text-sm text-gray-600 mb-3">
              <span>Focus Areas</span>
              <span class="font-semibold text-blue-600">{{ statistics.totalFocusAreas }}</span>
            </div>
            <router-link
              to="/career-paths"
              class="text-blue-600 hover:text-blue-700 text-sm font-medium flex items-center gap-1"
            >
              管理技能
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>
        </div>

        <!-- 试题库卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-green-500 to-green-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-green-100 text-sm font-medium">试题库</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ statistics.totalQuestions }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-green-50">
            <div class="flex items-center justify-between text-sm text-gray-600 mb-3">
              <span>我的笔记</span>
              <span class="font-semibold text-green-600">{{ userNotes.algorithmQuestionNotes }}</span>
            </div>
            <router-link
              to="/question-bank"
              class="text-green-600 hover:text-green-700 text-sm font-medium flex items-center gap-1"
            >
              浏览试题
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>
        </div>

        <!-- 算法分类卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-purple-500 to-purple-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-purple-100 text-sm font-medium">算法分类</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ statistics.algorithmCategories }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-purple-50">
            <div class="flex items-center justify-between text-sm text-gray-600 mb-3">
              <span>学习笔记</span>
              <span class="font-semibold text-purple-600">{{ userNotes.algorithmLearningNotes }}</span>
            </div>
            <router-link
              to="/learning-review"
              class="text-purple-600 hover:text-purple-700 text-sm font-medium flex items-center gap-1"
            >
              查看学习总结
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>
        </div>

        <!-- 算法模板库卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-indigo-500 to-indigo-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-indigo-100 text-sm font-medium">算法模板库</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ statistics.algorithmTemplates }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-indigo-50">
            <div class="flex items-center justify-between text-sm text-gray-600 mb-3">
              <span>模板笔记</span>
              <span class="font-semibold text-indigo-600">{{ userNotes.algorithmTemplateNotes }}</span>
            </div>
            <router-link
              to="/algorithm-templates"
              class="text-indigo-600 hover:text-indigo-700 text-sm font-medium flex items-center gap-1"
            >
              查看模板
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>
        </div>

        <!-- 系统设计基础卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-orange-500 to-orange-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-orange-100 text-sm font-medium">系统设计基础</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ statistics.systemDesignBasics }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-orange-50">
            <div class="flex items-center justify-between text-sm text-gray-600 mb-3">
              <span>知识点</span>
              <span class="font-semibold text-orange-600">{{ systemDesign.totalFocusAreas }}</span>
            </div>
            <router-link
              to="/general-skills/learning/2"
              class="text-orange-600 hover:text-orange-700 text-sm font-medium flex items-center gap-1"
            >
              学习基础
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>
        </div>

        <!-- 系统设计案例卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-pink-500 to-pink-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-pink-100 text-sm font-medium">系统设计案例</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ statistics.systemDesignCases }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-pink-50">
            <div class="flex items-center justify-between text-sm text-gray-600 mb-3">
              <span>已完成</span>
              <span class="font-semibold text-pink-600">{{ userNotes.systemDesignCaseNotes }}</span>
            </div>
            <router-link
              to="/system-design/cases"
              class="text-pink-600 hover:text-pink-700 text-sm font-medium flex items-center gap-1"
            >
              查看案例
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>
        </div>
      </div>


      <!-- 算法与数据结构详情 -->
      <div class="mt-8 bg-white rounded-xl shadow-lg p-6">
        <div class="flex justify-between items-center mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">💻 算法与数据结构</h2>
            <p class="text-sm text-gray-600 mt-1" v-if="userNotes.algorithmNotesTotal > 0">
              已记录 {{ userNotes.algorithmNotesTotal }} 条笔记
              <span class="text-xs text-gray-400">
                (试题: {{ userNotes.algorithmQuestionNotes }}，模板: {{ userNotes.algorithmTemplateNotes }}，学习: {{ userNotes.algorithmLearningNotes }})
              </span>
            </p>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div
            v-for="category in algorithmCategories"
            :key="category.id"
            class="bg-gradient-to-br from-blue-50 to-purple-50 rounded-lg p-5 border border-blue-200 hover:border-blue-400 transition-all hover:shadow-md cursor-pointer"
          >
            <h3 class="font-semibold text-gray-800 mb-3 text-lg">{{ category.name }}</h3>
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

      <!-- 系统设计详情 -->
      <div class="mt-8 bg-white rounded-xl shadow-lg p-6">
        <div class="flex justify-between items-center mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">🏗️ 系统设计</h2>
            <p class="text-sm text-gray-600 mt-1" v-if="userNotes.systemDesignCaseNotes > 0">
              已完成 {{ userNotes.systemDesignCaseNotes }} 个案例
            </p>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- 基础知识 -->
          <div class="bg-gradient-to-br from-orange-50 to-yellow-50 rounded-lg p-5 border border-orange-200 hover:border-orange-400 transition-all hover:shadow-md">
            <h3 class="font-semibold text-gray-800 mb-3 flex items-center text-lg">
              <svg class="w-5 h-5 mr-2 text-orange-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
              基础知识
            </h3>
            <p class="text-sm text-gray-600 mb-4">共 {{ systemDesign.totalFocusAreas }} 个知识点</p>
            <div class="flex flex-wrap gap-2 mb-4">
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
              class="inline-flex items-center gap-1 text-sm text-orange-600 hover:text-orange-700 font-semibold"
            >
              查看全部
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>

          <!-- 典型案例 -->
          <div class="bg-gradient-to-br from-pink-50 to-purple-50 rounded-lg p-5 border border-pink-200 hover:border-pink-400 transition-all hover:shadow-md">
            <h3 class="font-semibold text-gray-800 mb-3 flex items-center text-lg">
              <svg class="w-5 h-5 mr-2 text-pink-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
              典型案例
            </h3>
            <p class="text-sm text-gray-600 mb-4">共 {{ systemDesign.totalCases }} 个经典案例</p>
            <div class="flex flex-wrap gap-2 mb-4">
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
              class="inline-flex items-center gap-1 text-sm text-pink-600 hover:text-pink-700 font-semibold"
            >
              查看全部
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>
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
