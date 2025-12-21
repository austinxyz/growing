<template>
  <div class="container mx-auto px-4 py-8 max-w-7xl">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-foreground mb-2">职业技能</h1>
      <p class="text-muted-foreground">查看您的职业路径及相关技能</p>
    </div>

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
      <div class="border-b border-border mb-6">
        <div class="flex space-x-1">
          <button
            v-for="careerPath in userCareerPaths"
            :key="careerPath.id"
            @click="activeCareerPathId = careerPath.id"
            :class="[
              'px-6 py-3 font-medium text-sm transition-colors border-b-2',
              activeCareerPathId === careerPath.id
                ? 'border-primary text-primary bg-primary/5'
                : 'border-transparent text-muted-foreground hover:text-foreground hover:bg-accent/50'
            ]"
          >
            <div class="flex items-center space-x-2">
              <span class="text-lg">{{ careerPath.icon || '💼' }}</span>
              <span>{{ careerPath.name }}</span>
              <span class="ml-2 px-2 py-0.5 text-xs rounded-full bg-primary/10 text-primary">
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
            <div class="mb-6 flex flex-wrap gap-2">
              <button
                @click="filterType = 'all'"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-colors',
                  filterType === 'all'
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-muted text-muted-foreground hover:bg-accent hover:text-foreground'
                ]"
              >
                全部 ({{ careerPathSkills[careerPath.id].length }})
              </button>
              <button
                @click="filterType = 'important'"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-colors',
                  filterType === 'important'
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-muted text-muted-foreground hover:bg-accent hover:text-foreground'
                ]"
              >
                重要技能 ({{ getImportantSkillCount(careerPath.id) }})
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
                <div class="bg-card border border-border rounded-lg p-6 hover:border-primary hover:shadow-lg transition-all h-full flex flex-col">
                  <!-- Skill Header -->
                  <div class="flex items-start justify-between mb-3">
                    <div class="flex items-center space-x-3 flex-1 min-w-0">
                      <div class="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0 group-hover:bg-primary/20 transition-colors">
                        <span class="text-2xl">{{ skill.icon || '📚' }}</span>
                      </div>
                      <div class="flex-1 min-w-0">
                        <h3 class="text-lg font-semibold text-foreground group-hover:text-primary transition-colors truncate">
                          {{ skill.name }}
                        </h3>
                      </div>
                    </div>
                    <span v-if="skill.isImportant" class="flex-shrink-0 ml-2 px-2 py-1 text-xs bg-destructive/10 text-destructive rounded font-medium">
                      重要
                    </span>
                  </div>

                  <!-- Description -->
                  <p class="text-sm text-muted-foreground mb-4 flex-1 line-clamp-3">
                    {{ skill.description }}
                  </p>

                  <!-- Stats -->
                  <div class="flex items-center justify-between text-xs text-muted-foreground pt-3 border-t border-border">
                    <div class="flex items-center space-x-4">
                      <span class="flex items-center space-x-1">
                        <span>📋</span>
                        <span>{{ skill.focusAreaCount || 0 }} 个重点</span>
                      </span>
                      <span class="flex items-center space-x-1">
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
