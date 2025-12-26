<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- Header -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg px-6 py-6 mb-8">
      <h1 class="text-3xl font-bold text-white mb-2">💼 职业技能</h1>
      <p class="text-sm text-blue-100">查看您的职业路径及相关技能</p>
    </div>

    <div class="container mx-auto px-4 pb-8 max-w-7xl">

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
    </div>

    <!-- No Career Paths -->
    <div v-else-if="!userCareerPaths || userCareerPaths.length === 0" class="text-center py-12">
      <div class="bg-muted rounded-lg p-8 max-w-md mx-auto">
        <p class="text-lg text-muted-foreground mb-4">您还没有选择职业路径</p>
        <p class="text-sm text-muted-foreground mb-6">请联系管理员为您设置职业路径</p>
      </div>
    </div>

    <!-- Career Path Tabs -->
    <div v-else>
      <!-- Tab Headers -->
      <div class="border-b border-gray-200 mb-6 bg-white rounded-t-lg shadow-sm">
        <div class="flex space-x-1 p-2">
          <button
            v-for="careerPath in userCareerPaths"
            :key="careerPath.id"
            @click="activeCareerPathId = careerPath.id"
            :class="[
              'px-6 py-3 font-semibold text-sm transition-all duration-200 rounded-lg',
              activeCareerPathId === careerPath.id
                ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-md'
                : 'text-gray-600 hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 hover:text-blue-700'
            ]"
          >
            <div class="flex items-center space-x-2">
              <span class="text-lg">{{ careerPath.icon || '💼' }}</span>
              <span>{{ careerPath.name }}</span>
              <span :class="[
                'ml-2 px-2 py-0.5 text-xs rounded-full font-medium shadow-sm',
                activeCareerPathId === careerPath.id
                  ? 'bg-white/30 text-white'
                  : 'bg-blue-100 text-blue-700'
              ]">
                {{ getSkillCount(careerPath.id) }}
              </span>
            </div>
          </button>
        </div>
      </div>

      <!-- Tab Content -->
      <div v-for="careerPath in userCareerPaths" :key="careerPath.id">
        <div v-show="activeCareerPathId === careerPath.id">
          <!-- Loading Skills -->
          <div v-if="loadingSkills[careerPath.id]" class="flex justify-center py-12">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>

          <!-- No Skills -->
          <div v-else-if="!careerPathSkills[careerPath.id] || careerPathSkills[careerPath.id].length === 0" class="text-center py-12">
            <div class="bg-muted rounded-lg p-8 max-w-md mx-auto">
              <p class="text-muted-foreground">该职业路径暂无关联技能</p>
            </div>
          </div>

          <!-- Skills Grid -->
          <div v-else>
            <!-- Career Path Description -->
            <div v-if="careerPath.description" class="mb-6 p-4 bg-accent/20 rounded-lg border border-border">
              <p class="text-sm text-muted-foreground">{{ careerPath.description }}</p>
            </div>

            <!-- Filter Buttons -->
            <div class="mb-6 flex flex-wrap gap-3">
              <button
                @click="filterType = 'all'"
                :class="[
                  'px-5 py-2.5 rounded-lg text-sm font-semibold transition-all duration-200 shadow-sm',
                  filterType === 'all'
                    ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-md hover:shadow-lg'
                    : 'bg-white text-gray-700 hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 hover:text-blue-700 hover:shadow-md'
                ]"
              >
                📚 全部 ({{ careerPathSkills[careerPath.id].length }})
              </button>
              <button
                @click="filterType = 'important'"
                :class="[
                  'px-5 py-2.5 rounded-lg text-sm font-semibold transition-all duration-200 shadow-sm',
                  filterType === 'important'
                    ? 'bg-gradient-to-r from-red-500 to-pink-500 text-white shadow-md hover:shadow-lg'
                    : 'bg-white text-gray-700 hover:bg-gradient-to-r hover:from-red-50 hover:to-pink-50 hover:text-red-700 hover:shadow-md'
                ]"
              >
                ⭐ 重要技能 ({{ getImportantSkillCount(careerPath.id) }})
              </button>
            </div>

            <!-- Skills Grid -->
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <router-link
                v-for="skill in getFilteredSkills(careerPath.id)"
                :key="skill.id"
                :to="`/skills/${skill.id}`"
                class="group"
              >
                <div class="bg-gradient-to-br from-white to-blue-50 border-2 border-blue-200 rounded-xl p-6 hover:shadow-2xl hover:border-blue-400 transition-all duration-300 h-full flex flex-col">
                  <!-- Skill Header -->
                  <div class="flex items-start justify-between mb-3">
                    <div class="flex items-center space-x-3 flex-1 min-w-0">
                      <div class="w-14 h-14 bg-gradient-to-br from-blue-400 to-purple-500 rounded-xl flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300 shadow-md">
                        <span class="text-2xl">{{ skill.icon || '📚' }}</span>
                      </div>
                      <div class="flex-1 min-w-0">
                        <h3 class="text-lg font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600 group-hover:from-blue-700 group-hover:to-purple-700 transition-all truncate">
                          {{ skill.name }}
                        </h3>
                      </div>
                    </div>
                    <span v-if="skill.isImportant" class="flex-shrink-0 ml-2 px-3 py-1 text-xs bg-gradient-to-r from-red-400 to-pink-500 text-white rounded-full font-semibold shadow-md">
                      ⭐ 重要
                    </span>
                  </div>

                  <!-- Description -->
                  <p class="text-sm text-gray-600 mb-4 flex-1 line-clamp-3 leading-relaxed">
                    {{ skill.description }}
                  </p>

                  <!-- Stats -->
                  <div class="flex items-center justify-between text-xs pt-4 border-t border-blue-200">
                    <div class="flex items-center space-x-4">
                      <span class="flex items-center space-x-1 px-2 py-1 bg-blue-50 rounded-lg text-blue-700 font-medium">
                        <span>📋</span>
                        <span>{{ skill.focusAreaCount || 0 }} 个重点</span>
                      </span>
                      <span class="flex items-center space-x-1 px-2 py-1 bg-purple-50 rounded-lg text-purple-700 font-medium">
                        <span>📖</span>
                        <span>{{ skill.resourceCount || 0 }} 个资源</span>
                      </span>
                    </div>
                  </div>
                </div>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { getSkillsByCareerPath } from '@/api/skills'
import authApi from '@/api/auth'

const { currentUser, updateUser } = useAuth()

const loading = ref(true)
const userCareerPaths = ref([])
const careerPathSkills = ref({})
const loadingSkills = ref({})
const activeCareerPathId = ref(null)
const filterType = ref('all') // 'all' or 'important'

// 获取技能数量
const getSkillCount = (careerPathId) => {
  return careerPathSkills.value[careerPathId]?.length || 0
}

// 获取重要技能数量
const getImportantSkillCount = (careerPathId) => {
  const skills = careerPathSkills.value[careerPathId] || []
  return skills.filter(skill => skill.isImportant).length
}

// 获取过滤后的技能
const getFilteredSkills = (careerPathId) => {
  const skills = careerPathSkills.value[careerPathId] || []
  if (filterType.value === 'important') {
    return skills.filter(skill => skill.isImportant)
  }
  return skills
}

// 加载职业路径的技能
const loadSkillsForCareerPath = async (careerPathId) => {
  if (careerPathSkills.value[careerPathId]) {
    return // 已加载
  }

  loadingSkills.value[careerPathId] = true
  try {
    const response = await getSkillsByCareerPath(careerPathId)
    careerPathSkills.value[careerPathId] = response.data || response
  } catch (error) {
    console.error(`加载职业路径 ${careerPathId} 的技能失败:`, error)
    careerPathSkills.value[careerPathId] = []
  } finally {
    loadingSkills.value[careerPathId] = false
  }
}

// 加载用户职业路径
onMounted(async () => {
  loading.value = true
  try {
    // 刷新用户信息以获取最新的职业路径数据（包括icon）
    try {
      const response = await authApi.getCurrentUser()
      const userData = response.data || response
      updateUser(userData)
    } catch (error) {
      console.error('刷新用户信息失败:', error)
    }

    // 从当前用户信息中获取职业路径
    if (currentUser.value && currentUser.value.careerPaths && currentUser.value.careerPaths.length > 0) {
      userCareerPaths.value = currentUser.value.careerPaths

      // 设置第一个职业路径为活动tab
      if (userCareerPaths.value.length > 0) {
        activeCareerPathId.value = userCareerPaths.value[0].id

        // 为每个职业路径加载技能
        for (const careerPath of userCareerPaths.value) {
          await loadSkillsForCareerPath(careerPath.id)
        }
      }
    }
  } catch (error) {
    console.error('加载职业路径失败:', error)
  } finally {
    loading.value = false
  }
})
</script>
