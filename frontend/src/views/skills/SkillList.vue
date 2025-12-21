<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- 页头 -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-2">我的技能</h1>
      <p class="text-gray-600">根据你的职业路径推荐的核心技能</p>
    </div>

    <!-- 职业路径选择器 -->
    <div class="mb-6">
      <label class="block text-sm font-medium text-gray-700 mb-2">选择职业路径</label>
      <select
        v-model="selectedCareerPath"
        @change="loadSkills"
        class="w-full md:w-64 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
      >
        <option :value="null">所有技能</option>
        <option v-for="path in careerPaths" :key="path.id" :value="path.id">
          {{ path.name }}
        </option>
      </select>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      <p class="mt-4 text-gray-600">加载中...</p>
    </div>

    <!-- 技能卡片网格 -->
    <div v-else-if="skills.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <SkillCard
        v-for="skill in skills"
        :key="skill.id"
        :skill="skill"
        @click="goToSkillDetail(skill.id)"
      />
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">暂无技能</h3>
      <p class="mt-1 text-sm text-gray-500">请联系管理员添加技能</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { getAllSkills, getSkillsByCareerPath } from '@/api/skills'
import { getAllCareerPaths } from '@/api/careerPaths'
import SkillCard from '@/components/skills/SkillCard.vue'

const router = useRouter()
const { currentUser } = useAuth()

const loading = ref(false)
const skills = ref([])
const careerPaths = ref([])
const selectedCareerPath = ref(null)

const loadCareerPaths = async () => {
  try {
    const data = await getAllCareerPaths()
    careerPaths.value = data
  } catch (error) {
    console.error('Failed to load career paths:', error)
  }
}

const loadSkills = async () => {
  loading.value = true
  try {
    if (selectedCareerPath.value) {
      skills.value = await getSkillsByCareerPath(selectedCareerPath.value)
    } else {
      skills.value = await getAllSkills()
    }
  } catch (error) {
    console.error('Failed to load skills:', error)
  } finally {
    loading.value = false
  }
}

const goToSkillDetail = (skillId) => {
  router.push(`/skills/${skillId}`)
}

onMounted(async () => {
  await loadCareerPaths()
  await loadSkills()
})
</script>
