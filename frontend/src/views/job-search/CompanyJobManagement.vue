<template>
  <div class="company-job-management p-6">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">公司与职位管理</h1>

    <div class="grid grid-cols-4 gap-6">
      <!-- 左侧: 公司列表 -->
      <div class="col-span-1 bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold">公司列表</h2>
          <button
            @click="showCreateCompanyModal = true"
            class="px-2 py-1 bg-blue-600 text-white text-xs rounded hover:bg-blue-700"
          >
            新建
          </button>
        </div>

        <div class="space-y-2">
          <div
            v-for="company in companies"
            :key="company.id"
            @click="selectedCompanyId = company.id"
            class="p-3 border rounded cursor-pointer hover:bg-gray-50"
            :class="{ 'bg-blue-50 border-blue-500': selectedCompanyId === company.id }"
          >
            <div class="font-medium">{{ company.companyName }}</div>
            <div class="text-xs text-gray-500">{{ company.industry }}</div>
          </div>
        </div>
      </div>

      <!-- 中间: 公司详情 + Tab -->
      <div class="col-span-1 bg-white rounded-lg shadow p-4">
        <div v-if="currentCompany">
          <h2 class="font-semibold mb-4">{{ currentCompany.companyName }}</h2>

          <!-- Tab切换 -->
          <div class="flex border-b mb-4">
            <button
              @click="activeTab = 'jobs'"
              class="px-4 py-2 text-sm"
              :class="activeTab === 'jobs' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-600'"
            >
              职位
            </button>
            <button
              @click="activeTab = 'recruiters'"
              class="px-4 py-2 text-sm"
              :class="activeTab === 'recruiters' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-600'"
            >
              Recruiter
            </button>
            <button
              @click="activeTab = 'referrals'"
              class="px-4 py-2 text-sm"
              :class="activeTab === 'referrals' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-600'"
            >
              人脉
            </button>
          </div>

          <!-- 职位列表 -->
          <div v-if="activeTab === 'jobs'" class="space-y-2">
            <button
              @click="showCreateJobModal = true"
              class="w-full px-3 py-2 bg-green-600 text-white text-sm rounded hover:bg-green-700 mb-2"
            >
              添加职位
            </button>
            <div
              v-for="job in jobs"
              :key="job.id"
              @click="selectedJobId = job.id"
              class="p-2 border rounded cursor-pointer hover:bg-gray-50 text-sm"
              :class="{ 'bg-green-50 border-green-500': selectedJobId === job.id }"
            >
              <div class="font-medium">{{ job.positionName }}</div>
              <div class="text-xs text-gray-500">{{ job.applicationStatus }}</div>
            </div>
          </div>

          <!-- Recruiter列表 (简化) -->
          <div v-if="activeTab === 'recruiters'" class="text-gray-500 text-center py-4">
            Recruiter功能待实现
          </div>

          <!-- 人脉列表 (简化) -->
          <div v-if="activeTab === 'referrals'" class="text-gray-500 text-center py-4">
            人脉功能待实现
          </div>
        </div>
      </div>

      <!-- 右侧: 职位详情 -->
      <div class="col-span-2 bg-white rounded-lg shadow p-6">
        <div v-if="currentJob">
          <h2 class="text-xl font-semibold mb-4">{{ currentJob.positionName }}</h2>

          <div class="space-y-4">
            <div>
              <h3 class="font-semibold text-gray-700 mb-2">职位要求 (Qualifications)</h3>
              <div class="text-gray-600 whitespace-pre-wrap">{{ currentJob.qualifications }}</div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-700 mb-2">岗位职责 (Responsibilities)</h3>
              <div class="text-gray-600 whitespace-pre-wrap">{{ currentJob.responsibilities }}</div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-700 mb-2">申请状态</h3>
              <div class="text-lg font-medium text-blue-600">{{ currentJob.applicationStatus }}</div>
            </div>
          </div>

          <div class="flex justify-end gap-2 mt-6">
            <button
              @click="deleteJob"
              class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
            >
              删除职位
            </button>
          </div>
        </div>

        <div v-else class="text-center text-gray-500 py-12">
          请选择一个职位查看详情
        </div>
      </div>
    </div>

    <!-- 创建公司 Modal -->
    <div v-if="showCreateCompanyModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-96">
        <h3 class="text-lg font-semibold mb-4">创建公司</h3>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">公司名称 *</label>
          <input v-model="companyFormData.companyName" class="w-full px-4 py-2 border rounded-lg" />
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <button @click="showCreateCompanyModal = false" class="px-4 py-2 border rounded-lg">取消</button>
          <button @click="createCompany" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">创建</button>
        </div>
      </div>
    </div>

    <!-- 创建职位 Modal -->
    <div v-if="showCreateJobModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-2/3">
        <h3 class="text-lg font-semibold mb-4">创建职位</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">职位名称 *</label>
            <input v-model="jobFormData.positionName" class="w-full px-4 py-2 border rounded-lg" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">职位要求</label>
            <textarea v-model="jobFormData.qualifications" rows="3" class="w-full px-4 py-2 border rounded-lg"></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">岗位职责</label>
            <textarea v-model="jobFormData.responsibilities" rows="3" class="w-full px-4 py-2 border rounded-lg"></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <button @click="showCreateJobModal = false" class="px-4 py-2 border rounded-lg">取消</button>
          <button @click="createJob" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">创建</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { companyApi } from '@/api/companyApi'
import { jobApplicationApi } from '@/api/jobApplicationApi'

const companies = ref([])
const jobs = ref([])
const selectedCompanyId = ref(null)
const selectedJobId = ref(null)
const activeTab = ref('jobs')
const showCreateCompanyModal = ref(false)
const showCreateJobModal = ref(false)

const companyFormData = ref({ companyName: '' })
const jobFormData = ref({ positionName: '', qualifications: '', responsibilities: '' })

const currentCompany = computed(() =>
  companies.value.find(c => c.id === selectedCompanyId.value)
)

const currentJob = computed(() =>
  jobs.value.find(j => j.id === selectedJobId.value)
)

onMounted(() => {
  loadCompanies()
})

watch(selectedCompanyId, (newVal) => {
  if (newVal) {
    loadJobs()
  } else {
    jobs.value = []
  }
})

const loadCompanies = async () => {
  try {
    const data = await companyApi.getCompanies()
    companies.value = data || []
  } catch (error) {
    console.error('加载公司列表失败:', error)
  }
}

const loadJobs = async () => {
  if (!selectedCompanyId.value) return

  try {
    const data = await jobApplicationApi.getJobsByCompany(selectedCompanyId.value)
    jobs.value = data || []
  } catch (error) {
    console.error('加载职位列表失败:', error)
  }
}

const createCompany = async () => {
  try {
    await companyApi.createCompany(companyFormData.value)
    showCreateCompanyModal.value = false
    companyFormData.value = { companyName: '' }
    await loadCompanies()
  } catch (error) {
    console.error('创建公司失败:', error)
    alert('创建失败')
  }
}

const createJob = async () => {
  try {
    await jobApplicationApi.createJobApplication({
      ...jobFormData.value,
      companyId: selectedCompanyId.value
    })
    showCreateJobModal.value = false
    jobFormData.value = { positionName: '', qualifications: '', responsibilities: '' }
    await loadJobs()
  } catch (error) {
    console.error('创建职位失败:', error)
    alert('创建失败')
  }
}

const deleteJob = async () => {
  if (!confirm('确定要删除这个职位吗?')) return

  try {
    await jobApplicationApi.deleteJobApplication(selectedJobId.value)
    selectedJobId.value = null
    await loadJobs()
  } catch (error) {
    console.error('删除职位失败:', error)
    alert('删除失败')
  }
}
</script>
