<template>
  <div class="job-search-dashboard min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50 p-8">
    <div class="max-w-7xl mx-auto">
      <!-- 标题 -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">求职管理仪表板</h1>
        <p class="text-gray-600">快速查看您的求职准备进度</p>
      </div>

      <!-- 统计卡片网格 -->
      <div v-if="!loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <!-- 简历卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-blue-500 to-blue-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-blue-100 text-sm font-medium">简历版本</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ stats.resumeCount || 0 }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-blue-50">
            <button
              @click="navigateTo('/job-search/resume')"
              class="text-blue-600 hover:text-blue-700 text-sm font-medium flex items-center gap-1"
            >
              管理简历
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 项目经验卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-purple-500 to-purple-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-purple-100 text-sm font-medium">项目经验</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ stats.projectCount || 0 }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-purple-50">
            <button
              @click="navigateTo('/job-search/projects')"
              class="text-purple-600 hover:text-purple-700 text-sm font-medium flex items-center gap-1"
            >
              管理项目
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 管理案例卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-pink-500 to-pink-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-pink-100 text-sm font-medium">人员管理案例</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ stats.managementCount || 0 }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-pink-50">
            <button
              @click="navigateTo('/job-search/management')"
              class="text-pink-600 hover:text-pink-700 text-sm font-medium flex items-center gap-1"
            >
              管理案例
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 公司卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-green-500 to-green-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-green-100 text-sm font-medium">目标公司</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ stats.companyCount || 0 }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-green-50">
            <button
              @click="navigateTo('/job-search/companies')"
              class="text-green-600 hover:text-green-700 text-sm font-medium flex items-center gap-1"
            >
              管理公司
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 职位申请卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-orange-500 to-orange-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-orange-100 text-sm font-medium">职位申请</p>
                <h3 class="text-white text-4xl font-bold mt-2">{{ stats.jobApplicationCount || 0 }}</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-orange-50">
            <button
              @click="navigateTo('/job-search/applications')"
              class="text-orange-600 hover:text-orange-700 text-sm font-medium flex items-center gap-1"
            >
              管理申请
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 快捷操作卡片 -->
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden">
          <div class="bg-gradient-to-r from-indigo-500 to-indigo-600 p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-indigo-100 text-sm font-medium">快捷操作</p>
                <h3 class="text-white text-2xl font-bold mt-2">一键开始</h3>
              </div>
              <div class="bg-white bg-opacity-30 rounded-full p-4">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
            </div>
          </div>
          <div class="p-4 bg-indigo-50 space-y-2">
            <button
              @click="navigateTo('/job-search/resume')"
              class="w-full text-left px-3 py-2 text-indigo-600 hover:bg-indigo-100 rounded-lg text-sm font-medium transition-colors"
            >
              📝 创建新简历
            </button>
            <button
              @click="navigateTo('/job-search/companies')"
              class="w-full text-left px-3 py-2 text-indigo-600 hover:bg-indigo-100 rounded-lg text-sm font-medium transition-colors"
            >
              🏢 添加公司
            </button>
            <button
              @click="navigateTo('/job-search/applications')"
              class="w-full text-left px-3 py-2 text-indigo-600 hover:bg-indigo-100 rounded-lg text-sm font-medium transition-colors"
            >
              ✉️ 记录申请
            </button>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-else class="flex items-center justify-center h-64">
        <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600"></div>
      </div>

      <!-- 错误提示 -->
      <div v-if="error" class="mt-8 bg-red-50 border border-red-200 rounded-lg p-4">
        <p class="text-red-600">{{ error }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import jobSearchStatsApi from '@/api/jobSearchStatsApi'

const router = useRouter()

const loading = ref(true)
const error = ref(null)
const stats = ref({
  resumeCount: 0,
  projectCount: 0,
  managementCount: 0,
  companyCount: 0,
  jobApplicationCount: 0
})

// 加载统计数据
// Note: interceptor returns response.data already
const loadStats = async () => {
  try {
    loading.value = true
    error.value = null
    const data = await jobSearchStatsApi.getStats()
    stats.value = data
  } catch (err) {
    console.error('Failed to load stats:', err)
    error.value = '加载统计数据失败，请刷新页面重试'
  } finally {
    loading.value = false
  }
}

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
}

onMounted(() => {
  loadStats()
})
</script>
