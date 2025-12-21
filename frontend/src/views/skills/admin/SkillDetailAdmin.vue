<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- 返回按钮 -->
    <button
      @click="goBack"
      class="mb-6 flex items-center text-gray-600 hover:text-gray-900"
    >
      <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
      </svg>
      返回技能管理
    </button>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      <p class="mt-4 text-gray-600">加载中...</p>
    </div>

    <!-- 技能详细管理 -->
    <div v-else-if="skill">
      <!-- 页头 -->
      <div class="bg-white rounded-lg shadow-md p-6 mb-6">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900 mb-2 flex items-center">
              技能管理: {{ skill.name }}
              <span v-if="skill.isImportant" class="ml-2 text-yellow-500">⭐</span>
            </h1>
            <p class="text-gray-600">{{ skill.description }}</p>
          </div>
        </div>
      </div>

      <!-- Tab 导航 -->
      <div class="bg-white rounded-lg shadow-md mb-6">
        <div class="border-b border-gray-200">
          <nav class="flex -mb-px">
            <button
              @click="activeTab = 'basic'"
              :class="[
                'px-6 py-4 text-sm font-medium border-b-2 transition-colors',
                activeTab === 'basic'
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              基本信息
            </button>
            <button
              @click="activeTab = 'focus-areas'"
              :class="[
                'px-6 py-4 text-sm font-medium border-b-2 transition-colors',
                activeTab === 'focus-areas'
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              专注领域 ({{ focusAreas.length }})
            </button>
            <button
              @click="activeTab = 'resources'"
              :class="[
                'px-6 py-4 text-sm font-medium border-b-2 transition-colors',
                activeTab === 'resources'
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              学习资源 ({{ resources.length }})
            </button>
          </nav>
        </div>

        <!-- Tab 内容 -->
        <div class="p-6">
          <!-- 基本信息 Tab -->
          <div v-if="activeTab === 'basic'">
            <SkillBasicInfo :skill="skill" @updated="loadSkill" />
          </div>

          <!-- 专注领域 Tab -->
          <div v-if="activeTab === 'focus-areas'">
            <FocusAreaManager
              :skillId="skill.id"
              :focusAreas="focusAreas"
              @updated="loadFocusAreas"
            />
          </div>

          <!-- 学习资源 Tab -->
          <div v-if="activeTab === 'resources'">
            <ResourceManager
              :skillId="skill.id"
              :resources="resources"
              @updated="loadResources"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSkillByIdAdmin } from '@/api/skills'
import { getFocusAreas } from '@/api/focusAreas'
import { getResourcesAdmin } from '@/api/learningResources'
import SkillBasicInfo from '@/components/skills/admin/SkillBasicInfo.vue'
import FocusAreaManager from '@/components/skills/admin/FocusAreaManager.vue'
import ResourceManager from '@/components/skills/admin/ResourceManager.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const skill = ref(null)
const focusAreas = ref([])
const resources = ref([])
const activeTab = ref('basic')

const loadSkill = async () => {
  loading.value = true
  try {
    skill.value = await getSkillByIdAdmin(route.params.id)
  } catch (error) {
    console.error('Failed to load skill:', error)
  } finally {
    loading.value = false
  }
}

const loadFocusAreas = async () => {
  try {
    focusAreas.value = await getFocusAreas(route.params.id)
  } catch (error) {
    console.error('Failed to load focus areas:', error)
  }
}

const loadResources = async () => {
  try {
    resources.value = await getResourcesAdmin(route.params.id)
  } catch (error) {
    console.error('Failed to load resources:', error)
  }
}

const goBack = () => {
  router.push('/admin/skills')
}

onMounted(async () => {
  await loadSkill()
  await loadFocusAreas()
  await loadResources()
})
</script>
