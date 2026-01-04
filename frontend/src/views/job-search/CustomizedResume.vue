<template>
  <div class="customized-resume p-6">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">定制简历生成</h1>

    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <h2 class="text-lg font-semibold mb-4">生成定制简历</h2>

      <div class="grid grid-cols-2 gap-4 mb-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">选择公司</label>
          <select
            v-model="selectedCompanyId"
            @change="loadJobs"
            class="w-full px-4 py-2 border rounded-lg"
          >
            <option value="">-- 选择公司 --</option>
            <option v-for="company in companies" :key="company.id" :value="company.id">
              {{ company.companyName }}
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">选择职位</label>
          <select
            v-model="selectedJobId"
            class="w-full px-4 py-2 border rounded-lg"
            :disabled="!selectedCompanyId"
          >
            <option value="">-- 选择职位 --</option>
            <option v-for="job in jobs" :key="job.id" :value="job.id">
              {{ job.positionName }}
            </option>
          </select>
        </div>
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">选择基础简历</label>
        <select
          v-model="selectedResumeId"
          class="w-full px-4 py-2 border rounded-lg"
        >
          <option value="">-- 选择简历版本 --</option>
          <option v-for="resume in resumes" :key="resume.id" :value="resume.id">
            {{ resume.versionName }}
          </option>
        </select>
      </div>

      <div class="flex gap-4">
        <button
          @click="generateCustomizedResume"
          :disabled="!selectedCompanyId || !selectedJobId || !selectedResumeId"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
        >
          生成定制简历
        </button>

        <button
          v-if="customizedResume"
          class="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
        >
          导出简历 (P1)
        </button>
      </div>
    </div>

    <!-- 对比视图 (简化版) -->
    <div v-if="customizedResume" class="grid grid-cols-2 gap-6">
      <div class="bg-white rounded-lg shadow p-6">
        <h2 class="text-lg font-semibold mb-4">基础简历</h2>
        <div class="space-y-4">
          <div>
            <h3 class="font-semibold text-gray-700">简历版本</h3>
            <p class="text-gray-600">{{ baseResume?.versionName }}</p>
          </div>
          <div>
            <h3 class="font-semibold text-gray-700">个人简介</h3>
            <p class="text-gray-600 whitespace-pre-wrap">{{ baseResume?.about }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-6">
        <h2 class="text-lg font-semibold mb-4">定制简历 (可编辑)</h2>
        <div class="space-y-4">
          <div>
            <h3 class="font-semibold text-gray-700">简历版本</h3>
            <input
              v-model="customizedResume.versionName"
              class="w-full px-3 py-2 border rounded"
            />
          </div>
          <div>
            <h3 class="font-semibold text-gray-700">个人简介</h3>
            <textarea
              v-model="customizedResume.about"
              rows="4"
              class="w-full px-3 py-2 border rounded"
            ></textarea>
          </div>
        </div>

        <div class="flex justify-end mt-4">
          <button
            @click="saveCustomizedResume"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            保存定制简历
          </button>
        </div>
      </div>
    </div>

    <div v-else class="text-center text-gray-500 py-12">
      请选择公司、职位和基础简历，然后点击"生成定制简历"
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { companyApi } from '@/api/companyApi'
import { jobApplicationApi } from '@/api/jobApplicationApi'
import { resumeApi } from '@/api/resumeApi'

const companies = ref([])
const jobs = ref([])
const resumes = ref([])
const selectedCompanyId = ref('')
const selectedJobId = ref('')
const selectedResumeId = ref('')
const baseResume = ref(null)
const customizedResume = ref(null)

onMounted(() => {
  loadCompanies()
  loadResumes()
})

watch(selectedCompanyId, (newVal) => {
  if (newVal) {
    loadJobs()
  } else {
    jobs.value = []
    selectedJobId.value = ''
  }
})

const loadCompanies = async () => {
  try {
    const data = await companyApi.getCompanies()
    companies.value = data || []
  } catch (error) {
    console.error('加载公司失败:', error)
  }
}

const loadJobs = async () => {
  try {
    const data = await jobApplicationApi.getJobsByCompany(selectedCompanyId.value)
    jobs.value = data || []
  } catch (error) {
    console.error('加载职位失败:', error)
  }
}

const loadResumes = async () => {
  try {
    const data = await resumeApi.getResumes()
    resumes.value = data || []
  } catch (error) {
    console.error('加载简历失败:', error)
  }
}

const generateCustomizedResume = async () => {
  try {
    // 加载基础简历
    const baseData = await resumeApi.getResumeById(selectedResumeId.value)
    baseResume.value = baseData

    // 生成定制简历 (简化版: 直接复制基础简历)
    // P1: 实现自动匹配技能和项目经验
    const job = jobs.value.find(j => j.id === selectedJobId.value)
    const company = companies.value.find(c => c.id === selectedCompanyId.value)

    customizedResume.value = {
      ...baseData,
      id: null, // 新简历
      versionName: `${company.companyName}_${job.positionName}_${new Date().toISOString().split('T')[0]}`,
      isDefault: false
    }

    alert('定制简历生成成功')
  } catch (error) {
    console.error('生成定制简历失败:', error)
    alert('生成失败')
  }
}

const saveCustomizedResume = async () => {
  try {
    await resumeApi.createResume(customizedResume.value)
    alert('保存成功')
  } catch (error) {
    console.error('保存定制简历失败:', error)
    alert('保存失败')
  }
}
</script>
