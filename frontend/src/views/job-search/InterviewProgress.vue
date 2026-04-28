<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50 p-8">
    <div class="max-w-7xl mx-auto">

      <ProgressSummaryHeader
        :counts="summary"
        :sort-mode="sortMode"
        :show-closed="showClosed"
        @update:sort-mode="sortMode = $event"
        @update:show-closed="onToggleClosed"
      />

      <!-- Loading -->
      <div v-if="loading" class="flex items-center justify-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-500"></div>
      </div>

      <!-- Error banner (preserves existing data) -->
      <div v-if="error" class="mb-4 bg-rose-50 border border-rose-200 rounded-lg p-3 flex items-center justify-between">
        <p class="text-rose-700 text-sm">{{ error }}</p>
        <button @click="loadProgress" class="text-rose-600 text-sm font-medium hover:text-rose-800">重试</button>
      </div>

      <!-- Empty state -->
      <div v-if="!loading && displayed.length === 0" class="bg-white rounded-xl shadow-sm p-12 text-center">
        <div class="text-5xl mb-3">📭</div>
        <h3 class="text-lg font-semibold text-gray-900 mb-1">暂无进行中的申请</h3>
        <p class="text-sm text-gray-500 mb-4">还没投或都已结案。</p>
        <button
          @click="$router.push('/job-search/applications')"
          class="inline-flex items-center px-4 py-2 bg-indigo-500 text-white text-sm rounded-lg hover:bg-indigo-600 transition"
        >去添加申请</button>
      </div>

      <!-- Card grid -->
      <div v-else-if="!loading" class="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <ProgressCard
          v-for="app in displayed"
          :key="app.id"
          :app="app"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { jobApplicationApi } from '@/api/jobApplicationApi'
import ProgressCard from '@/components/job-search/ProgressCard.vue'
import ProgressSummaryHeader from '@/components/job-search/ProgressSummaryHeader.vue'

const apps = ref([])
const closedApps = ref([])
const loading = ref(true)
const error = ref(null)
const sortMode = ref('attention')
const showClosed = ref(false)

// Note: interceptor returns response.data already
const loadProgress = async () => {
  try {
    loading.value = true
    error.value = null
    const data = await jobApplicationApi.getActiveProgress()
    apps.value = data || []
  } catch (err) {
    error.value = '加载面试进展失败，请重试'
  } finally {
    loading.value = false
  }
}

const loadClosed = async () => {
  // Lazy: fetch only on first toggle, then keep the list in memory
  if (closedApps.value.length > 0) return
  try {
    const data = await jobApplicationApi.getClosedProgress()
    closedApps.value = data || []
  } catch (err) {
    error.value = '加载已结案申请失败，请重试'
  }
}

const onToggleClosed = async (next) => {
  showClosed.value = next
  if (next) await loadClosed()
}

const stageOrder = { Offer: 0, Interviewing: 1, Screening: 2, Applied: 3 }

const displayed = computed(() => {
  let list = [...apps.value]
  if (showClosed.value) list = list.concat(closedApps.value)
  switch (sortMode.value) {
    case 'stage':
      return list.sort((a, b) =>
        (stageOrder[a.applicationStatus] ?? 99) - (stageOrder[b.applicationStatus] ?? 99)
        || (b.daysSinceApplied || 0) - (a.daysSinceApplied || 0)
      )
    case 'time':
      return list.sort((a, b) => (b.daysSinceApplied || 0) - (a.daysSinceApplied || 0))
    default:
      return list  // 'attention': 后端已排好序
  }
})

const summary = computed(() => ({
  inProgress: apps.value.length,
  stalled: apps.value.filter(a => a.priorityLevel === 'STALLED').length,
  offerPending: apps.value.filter(a => a.priorityLevel === 'OFFER_DEADLINE').length
}))

onMounted(loadProgress)
</script>
