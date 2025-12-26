<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- Header -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg px-6 py-6">
      <div class="max-w-7xl mx-auto">
        <!-- 返回按钮 -->
        <button
          @click="goBack"
          class="mb-4 flex items-center text-white hover:text-blue-100 transition-colors font-medium"
        >
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
          </svg>
          返回职业技能
        </button>

        <!-- 技能标题 -->
        <div v-if="skill" class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <div class="w-16 h-16 bg-white/20 rounded-xl flex items-center justify-center shadow-lg">
              <span class="text-4xl">{{ skill.icon || '📚' }}</span>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white mb-1 flex items-center">
                {{ skill.name }}
                <span v-if="skill.isImportant" class="ml-3 px-3 py-1 text-sm bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-full font-semibold shadow-md">
                  ⭐ 重要技能
                </span>
              </h1>
              <p class="text-blue-100">{{ skill.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="animate-spin rounded-full h-16 w-16 border-b-4 border-blue-600"></div>
    </div>

    <!-- 主内容 -->
    <div v-else-if="skill" class="max-w-7xl mx-auto px-6 py-8">
      <!-- Tab 导航 -->
      <div class="bg-white rounded-xl shadow-lg mb-6">
        <div class="border-b border-gray-200">
          <nav class="flex p-2 space-x-2">
            <button
              @click="activeTab = 'focus-areas'"
              :class="[
                'px-6 py-3 text-sm font-semibold rounded-lg transition-all duration-200',
                activeTab === 'focus-areas'
                  ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-md'
                  : 'text-gray-600 hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 hover:text-blue-700'
              ]"
            >
              🎯 专注领域 ({{ focusAreas.length }})
            </button>
            <button
              @click="activeTab = 'resources'"
              :class="[
                'px-6 py-3 text-sm font-semibold rounded-lg transition-all duration-200',
                activeTab === 'resources'
                  ? 'bg-gradient-to-r from-green-500 to-emerald-500 text-white shadow-md'
                  : 'text-gray-600 hover:bg-gradient-to-r hover:from-green-50 hover:to-emerald-50 hover:text-green-700'
              ]"
            >
              📖 学习资源 ({{ resources.length }})
            </button>
          </nav>
        </div>

        <!-- Tab 内容 -->
        <div class="p-6">
          <!-- 专注领域 Tab -->
          <div v-if="activeTab === 'focus-areas'">
            <div v-if="focusAreas.length === 0" class="text-center py-12 text-gray-400">
              暂无专注领域
            </div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div
                v-for="fa in focusAreas"
                :key="fa.id"
                class="bg-gradient-to-br from-white to-blue-50 border-2 border-blue-200 rounded-xl p-5 hover:shadow-xl hover:border-blue-400 transition-all duration-300"
              >
                <div class="flex items-center space-x-3 mb-3">
                  <div class="w-2 h-8 bg-gradient-to-b from-blue-400 to-blue-600 rounded-full"></div>
                  <h3 class="text-lg font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">
                    {{ fa.name }}
                  </h3>
                </div>
                <p class="text-sm text-gray-600 leading-relaxed">{{ fa.description }}</p>
              </div>
            </div>
          </div>

          <!-- 学习资源 Tab -->
          <div v-if="activeTab === 'resources'">
            <div v-if="resources.length === 0" class="text-center py-12 text-gray-400">
              暂无学习资源
            </div>
            <div v-else class="space-y-4">
              <div
                v-for="resource in resources"
                :key="resource.id"
                class="bg-gradient-to-br from-white to-green-50 border-2 border-green-200 rounded-xl p-5 hover:shadow-xl hover:border-green-400 transition-all duration-300"
              >
                <div class="flex items-start justify-between mb-3">
                  <div class="flex-1">
                    <div class="flex items-center space-x-2 mb-2">
                      <div class="w-2 h-6 bg-gradient-to-b from-green-400 to-green-600 rounded-full"></div>
                      <h3 class="text-lg font-bold text-transparent bg-clip-text bg-gradient-to-r from-green-600 to-emerald-600">
                        {{ resource.title }}
                      </h3>
                    </div>
                    <p class="text-sm text-gray-600 mb-3 leading-relaxed">{{ resource.description }}</p>

                    <!-- 资源类型和链接 -->
                    <div class="flex items-center space-x-3">
                      <span :class="[
                        'px-3 py-1 text-xs font-semibold rounded-full shadow-sm',
                        resource.resourceType === 'VIDEO' ? 'bg-gradient-to-r from-red-400 to-pink-500 text-white' :
                        resource.resourceType === 'ARTICLE' ? 'bg-gradient-to-r from-blue-400 to-blue-500 text-white' :
                        resource.resourceType === 'BOOK' ? 'bg-gradient-to-r from-purple-400 to-purple-500 text-white' :
                        resource.resourceType === 'COURSE' ? 'bg-gradient-to-r from-orange-400 to-orange-500 text-white' :
                        resource.resourceType === 'WEBSITE' ? 'bg-gradient-to-r from-green-400 to-emerald-500 text-white' :
                        'bg-gradient-to-r from-gray-400 to-gray-500 text-white'
                      ]">
                        {{ resourceTypeText(resource.resourceType) }}
                      </span>
                      <a
                        v-if="resource.url"
                        :href="resource.url"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="inline-flex items-center px-3 py-1 text-xs font-semibold bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-lg hover:from-blue-600 hover:to-purple-600 hover:shadow-lg transition-all duration-200 shadow-sm"
                      >
                        🔗 查看资源
                        <svg class="w-3 h-3 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                        </svg>
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="max-w-7xl mx-auto px-6 py-12 text-center">
      <div class="bg-white rounded-xl shadow-lg p-8 max-w-md mx-auto border-2 border-red-200">
        <p class="text-lg text-red-600 font-semibold mb-2">⚠️ 加载失败</p>
        <p class="text-sm text-gray-600">未能加载技能详情，请稍后重试</p>
        <button
          @click="goBack"
          class="mt-4 px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-lg hover:from-blue-600 hover:to-purple-600 transition-all shadow-md"
        >
          返回
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSkillById } from '@/api/skills'

const route = useRoute()
const router = useRouter()

const skill = ref(null)
const focusAreas = ref([])
const resources = ref([])
const loading = ref(true)
const activeTab = ref('focus-areas')

const goBack = () => {
  router.push('/skills/career-paths')
}

const resourceTypeText = (type) => {
  const types = {
    VIDEO: '📹 视频',
    ARTICLE: '📄 文章',
    BOOK: '📚 书籍',
    COURSE: '🎓 课程',
    WEBSITE: '🌐 网站',
    DOCUMENTATION: '📖 文档',
    OTHER: '🔗 其他'
  }
  return types[type] || type
}

const loadSkill = async () => {
  const skillId = route.params.id
  if (!skillId) {
    console.error('缺少技能ID')
    loading.value = false
    return
  }

  loading.value = true
  try {
    const data = await getSkillById(skillId)
    console.log('Loaded skill data:', data)
    skill.value = data
    focusAreas.value = data.focusAreas || []
    // 后端返回的字段名是 learningResources
    resources.value = data.learningResources || []
    console.log('Focus areas:', focusAreas.value.length)
    console.log('Learning resources:', resources.value.length)
  } catch (error) {
    console.error('加载技能详情失败:', error)
    skill.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSkill()
})
</script>
