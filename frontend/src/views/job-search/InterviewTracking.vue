<template>
  <div class="interview-tracking p-6">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">面试记录跟踪</h1>

    <div class="grid grid-cols-4 gap-6">
      <!-- 左侧: 职位列表 -->
      <div class="col-span-1 bg-white rounded-lg shadow p-4">
        <h2 class="text-lg font-semibold mb-4">职位列表</h2>
        <div class="space-y-4">
          <div v-for="company in companiesWithJobs" :key="company.id">
            <h3 class="text-sm font-semibold text-gray-700 mb-2">{{ company.companyName }}</h3>
            <div class="space-y-2">
              <div
                v-for="job in company.jobs"
                :key="job.id"
                @click="selectedJobId = job.id"
                class="p-2 border rounded cursor-pointer hover:bg-gray-50 text-sm"
                :class="{ 'bg-blue-50 border-blue-500': selectedJobId === job.id }"
              >
                <div class="font-medium">{{ job.positionName }}</div>
                <div class="text-xs text-gray-500">{{ job.applicationStatus }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间: 面试记录列表 -->
      <div class="col-span-1 bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold">面试记录</h2>
          <button
            v-if="selectedJobId"
            @click="showCreateModal = true"
            class="px-2 py-1 bg-blue-600 text-white text-xs rounded hover:bg-blue-700"
          >
            新建
          </button>
        </div>

        <div class="space-y-2">
          <div
            v-for="interview in interviews"
            :key="interview.id"
            @click="selectedInterviewId = interview.id"
            class="p-3 border rounded cursor-pointer hover:bg-gray-50"
            :class="{ 'bg-green-50 border-green-500': selectedInterviewId === interview.id }"
          >
            <div class="text-sm font-medium">{{ interview.interviewerName }}</div>
            <div class="text-xs text-gray-500">
              {{ formatDate(interview.interviewDate) }}
            </div>
            <div class="text-xs text-gray-400">{{ interview.interviewFormat }}</div>
          </div>

          <div v-if="selectedJobId && !interviews.length" class="text-gray-500 text-center py-4">
            暂无面试记录
          </div>
        </div>
      </div>

      <!-- 右侧: 面试详情 -->
      <div class="col-span-2 bg-white rounded-lg shadow p-6">
        <div v-if="currentInterview">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-xl font-semibold">面试详情</h2>
            <button
              @click="deleteInterview"
              class="px-3 py-1 bg-red-600 text-white text-sm rounded hover:bg-red-700"
            >
              删除
            </button>
          </div>

          <div class="space-y-4">
            <div>
              <h3 class="font-semibold text-gray-700 mb-2">面试官信息</h3>
              <div class="text-gray-600">
                <p>姓名: {{ currentInterview.interviewerName }}</p>
                <p>职位: {{ currentInterview.interviewerPosition }}</p>
                <p>面试形式: {{ currentInterview.interviewFormat }}</p>
                <p>时长: {{ currentInterview.interviewDuration }} 分钟</p>
              </div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-700 mb-2">面试问题</h3>
              <div class="space-y-2">
                <div
                  v-for="question in currentInterview.questions"
                  :key="question.id"
                  class="p-3 border rounded"
                >
                  <div class="font-medium">{{ question.questionDescription }}</div>
                  <div class="text-sm text-gray-500">类型: {{ question.questionType }}</div>
                  <div class="text-sm text-gray-600 mt-2 whitespace-pre-wrap">{{ question.myAnswer }}</div>
                </div>
                <div v-if="!currentInterview.questions?.length" class="text-gray-500 text-center py-4">
                  暂无问题记录
                </div>
              </div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-700 mb-2">自我评估</h3>
              <div class="grid grid-cols-4 gap-2 text-sm">
                <div>
                  <span class="text-gray-600">整体表现:</span>
                  <span class="font-medium ml-1">{{ currentInterview.overallPerformance }}/5</span>
                </div>
                <div>
                  <span class="text-gray-600">技术深度:</span>
                  <span class="font-medium ml-1">{{ currentInterview.technicalDepth }}/5</span>
                </div>
                <div>
                  <span class="text-gray-600">沟通表达:</span>
                  <span class="font-medium ml-1">{{ currentInterview.communication }}/5</span>
                </div>
                <div>
                  <span class="text-gray-600">问题回答:</span>
                  <span class="font-medium ml-1">{{ currentInterview.problemSolving }}/5</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center text-gray-500 py-12">
          请选择一个面试记录查看详情
        </div>
      </div>
    </div>

    <!-- 创建面试记录 Modal (简化版) -->
    <div v-if="showCreateModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-2/3">
        <h3 class="text-lg font-semibold mb-4">创建面试记录</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">面试官姓名 *</label>
            <input v-model="formData.interviewerName" class="w-full px-4 py-2 border rounded-lg" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">面试官职位</label>
            <input v-model="formData.interviewerPosition" class="w-full px-4 py-2 border rounded-lg" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">面试形式</label>
            <select v-model="formData.interviewFormat" class="w-full px-4 py-2 border rounded-lg">
              <option value="VideoCall">Video Call</option>
              <option value="InPerson">In-person</option>
              <option value="Phone">Phone</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">面试时长 (分钟)</label>
            <input v-model.number="formData.interviewDuration" type="number" class="w-full px-4 py-2 border rounded-lg" />
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <button @click="showCreateModal = false" class="px-4 py-2 border rounded-lg">取消</button>
          <button @click="createInterview" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">创建</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { companyApi } from '@/api/companyApi'
import { jobApplicationApi } from '@/api/jobApplicationApi'
import { interviewApi } from '@/api/interviewApi'

const companies = ref([])
const allJobs = ref([])
const interviews = ref([])
const selectedJobId = ref(null)
const selectedInterviewId = ref(null)
const showCreateModal = ref(false)
const formData = ref({
  interviewerName: '',
  interviewerPosition: '',
  interviewFormat: 'VideoCall',
  interviewDuration: 60,
  interviewDate: new Date().toISOString()
})

const companiesWithJobs = computed(() => {
  const result = []
  companies.value.forEach(company => {
    const jobs = allJobs.value.filter(j => j.companyId === company.id)
    if (jobs.length > 0) {
      result.push({ ...company, jobs })
    }
  })
  return result
})

const currentInterview = computed(() =>
  interviews.value.find(i => i.id === selectedInterviewId.value)
)

onMounted(() => {
  loadData()
})

watch(selectedJobId, (newVal) => {
  if (newVal) {
    loadInterviews()
  } else {
    interviews.value = []
  }
})

const loadData = async () => {
  try {
    const [compData, jobData] = await Promise.all([
      companyApi.getCompanies(),
      jobApplicationApi.getAllJobApplications()
    ])
    companies.value = compData || []
    allJobs.value = jobData || []
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const loadInterviews = async () => {
  if (!selectedJobId.value) return

  try {
    const data = await interviewApi.getInterviewsByJob(selectedJobId.value)
    interviews.value = data || []
  } catch (error) {
    console.error('加载面试记录失败:', error)
  }
}

const createInterview = async () => {
  try {
    await interviewApi.createInterview({
      ...formData.value,
      jobApplicationId: selectedJobId.value
    })
    showCreateModal.value = false
    formData.value = {
      interviewerName: '',
      interviewerPosition: '',
      interviewFormat: 'VideoCall',
      interviewDuration: 60,
      interviewDate: new Date().toISOString()
    }
    await loadInterviews()
  } catch (error) {
    console.error('创建面试记录失败:', error)
    alert('创建失败')
  }
}

const deleteInterview = async () => {
  if (!confirm('确定要删除这个面试记录吗?')) return

  try {
    await interviewApi.deleteInterview(selectedInterviewId.value)
    selectedInterviewId.value = null
    await loadInterviews()
  } catch (error) {
    console.error('删除面试记录失败:', error)
    alert('删除失败')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>
