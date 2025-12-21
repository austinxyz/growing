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
      返回技能列表
    </button>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      <p class="mt-4 text-gray-600">加载中...</p>
    </div>

    <!-- 技能详情 -->
    <div v-else-if="skill">
      <!-- 技能头部 -->
      <div class="bg-white rounded-lg shadow-md p-6 mb-6">
        <h1 class="text-3xl font-bold text-gray-900 mb-2 flex items-center">
          {{ skill.name }}
          <span v-if="skill.isImportant" class="ml-2 text-yellow-500" title="重要技能">⭐</span>
        </h1>
        <p class="text-gray-600">{{ skill.description }}</p>
      </div>

      <!-- Tab 导航 -->
      <div class="bg-white rounded-lg shadow-md mb-6">
        <div class="border-b border-gray-200">
          <nav class="flex -mb-px">
            <button
              @click="activeTab = 'focus-areas'"
              :class="[
                'px-6 py-4 text-sm font-medium border-b-2 transition-colors',
                activeTab === 'focus-areas'
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              专注领域 ({{ skill.focusAreas?.length || 0 }})
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
              学习资源 ({{ skill.learningResources?.length || 0 }})
            </button>
          </nav>
        </div>

        <!-- Tab 内容 -->
        <div class="p-6">
          <!-- 专注领域 Tab -->
          <div v-if="activeTab === 'focus-areas'">
            <FocusAreaList :focusAreas="skill.focusAreas || []" />
          </div>

          <!-- 学习资源 Tab -->
          <div v-if="activeTab === 'resources'">
            <LearningResourceList
              :resources="skill.learningResources || []"
              :skillId="skill.id"
              @add="handleAddResource"
              @delete="handleDeleteResource"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 添加资源弹窗 -->
    <ResourceAddModal
      v-if="showAddModal"
      :skillId="skill?.id"
      @close="showAddModal = false"
      @success="handleResourceAdded"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSkillById } from '@/api/skills'
import { deleteUserResource } from '@/api/skills'
import FocusAreaList from '@/components/skills/FocusAreaList.vue'
import LearningResourceList from '@/components/skills/LearningResourceList.vue'
import ResourceAddModal from '@/components/skills/ResourceAddModal.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const skill = ref(null)
const activeTab = ref('focus-areas')
const showAddModal = ref(false)

const loadSkill = async () => {
  loading.value = true
  try {
    skill.value = await getSkillById(route.params.id)
  } catch (error) {
    console.error('Failed to load skill:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/skills')
}

const handleAddResource = () => {
  showAddModal.value = true
}

const handleResourceAdded = () => {
  showAddModal.value = false
  loadSkill() // 重新加载技能数据
}

const handleDeleteResource = async (resourceId) => {
  if (!confirm('确定要删除这个学习资源吗?')) {
    return
  }

  try {
    await deleteUserResource(resourceId)
    await loadSkill() // 重新加载
  } catch (error) {
    console.error('Failed to delete resource:', error)
    alert('删除失败')
  }
}

onMounted(() => {
  loadSkill()
})
</script>
