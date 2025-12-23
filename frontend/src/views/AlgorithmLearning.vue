<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题和职业路径tabs -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-full px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900 mb-4">算法学习</h1>

        <!-- 职业路径 Tabs -->
        <div class="flex space-x-2 overflow-x-auto">
          <button
            v-for="cp in careerPaths"
            :key="cp.id"
            @click="selectCareerPath(cp.id)"
            :class="[
              'px-4 py-2 rounded-lg text-sm font-medium transition-colors whitespace-nowrap',
              selectedCareerPathId === cp.id
                ? 'bg-blue-500 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            ]"
          >
            {{ cp.icon }} {{ cp.name }}
          </button>
        </div>
      </div>
    </div>

    <!-- 三栏布局 -->
    <div class="flex h-[calc(100vh-140px)]">
      <!-- 左侧：技能-Focus Area树 (25%) -->
      <aside class="w-1/4 bg-white border-r border-gray-200 overflow-y-auto">
        <div class="p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">技能与专注领域</h3>

          <div v-if="loading.skills" class="text-center text-gray-500 py-4">
            加载中...
          </div>

          <div v-else-if="skills.length === 0" class="text-center text-gray-400 py-4">
            暂无技能
          </div>

          <div v-else class="space-y-2">
            <div v-for="skill in skills" :key="skill.id">
              <!-- 技能节点 -->
              <button
                @click="toggleSkill(skill.id)"
                class="w-full flex items-center justify-between px-3 py-2 text-left hover:bg-gray-50 rounded-md"
              >
                <span class="text-sm font-medium text-gray-900">
                  {{ skill.icon }} {{ skill.name }}
                </span>
                <svg
                  :class="['w-4 h-4 transition-transform', expandedSkills.includes(skill.id) ? 'rotate-90' : '']"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </button>

              <!-- Focus Areas (展开时显示) -->
              <div v-if="expandedSkills.includes(skill.id)" class="ml-4 mt-1 space-y-1">
                <button
                  v-for="fa in skill.focusAreas"
                  :key="fa.id"
                  @click="selectFocusArea(fa.id, fa.name)"
                  :class="[
                    'w-full text-left px-3 py-2 text-sm rounded-md transition-colors',
                    selectedFocusAreaId === fa.id
                      ? 'bg-blue-50 text-blue-700 font-medium'
                      : 'text-gray-600 hover:bg-gray-50'
                  ]"
                >
                  • {{ fa.name }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <!-- 中间：学习阶段列表 (30%) -->
      <section class="w-[30%] bg-gray-50 border-r border-gray-200 overflow-y-auto">
        <div class="p-4">
          <div v-if="!selectedFocusAreaId" class="text-center text-gray-400 py-8">
            请先选择专注领域
          </div>

          <div v-else-if="loading.contents" class="text-center text-gray-500 py-8">
            加载学习内容中...
          </div>

          <div v-else-if="learningData.stages.length === 0" class="text-center text-gray-400 py-8">
            该专注领域暂无学习内容
          </div>

          <div v-else>
            <!-- Focus Area 标题 -->
            <div class="mb-4">
              <h2 class="text-lg font-semibold text-gray-900">{{ selectedFocusAreaName }}</h2>
              <p class="text-xs text-gray-500 mt-1">选择学习阶段查看内容</p>
            </div>

            <!-- 学习阶段列表 -->
            <div class="space-y-2">
              <button
                v-for="stage in learningData.stages"
                :key="stage.stageId"
                @click="selectStage(stage)"
                :class="[
                  'w-full text-left px-4 py-3 rounded-lg border transition-all',
                  selectedStageId === stage.stageId
                    ? 'bg-blue-50 border-blue-500 shadow-sm'
                    : 'bg-white border-gray-200 hover:border-gray-300 hover:shadow'
                ]"
              >
                <div class="flex items-center justify-between">
                  <div>
                    <h3 class="font-medium text-gray-900">{{ stage.stageName }}</h3>
                    <p class="text-xs text-gray-500 mt-1">{{ stage.stageDescription }}</p>
                    <div class="flex items-center mt-2 text-xs text-gray-600">
                      <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      </svg>
                      {{ stage.contents.length }} 个学习内容
                    </div>
                  </div>
                  <svg
                    v-if="selectedStageId === stage.stageId"
                    class="w-5 h-5 text-blue-500"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                </div>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- 右侧：学习内容详情 (45%) -->
      <main class="flex-1 bg-white overflow-y-auto">
        <div class="p-6">
          <div v-if="!selectedStageId" class="text-center text-gray-400 py-12">
            请先选择学习阶段
          </div>

          <div v-else-if="selectedStage">
            <!-- 阶段标题 -->
            <div class="mb-6 pb-4 border-b border-gray-200">
              <h2 class="text-2xl font-bold text-gray-900">{{ selectedStage.stageName }}</h2>
              <p class="text-sm text-gray-600 mt-2">{{ selectedStage.stageDescription }}</p>
            </div>

            <!-- 学习内容列表 -->
            <div v-if="selectedStage.contents.length === 0" class="text-center text-gray-400 py-8">
              该阶段暂无学习内容
            </div>

            <div v-else class="space-y-6">
              <div
                v-for="content in selectedStage.contents"
                :key="content.id"
                class="border border-gray-200 rounded-lg p-5 hover:shadow-md transition-shadow"
              >
                <!-- 内容标题和类型 -->
                <div class="flex items-start justify-between mb-3">
                  <h3 class="text-lg font-semibold text-gray-900">{{ content.title }}</h3>
                  <span :class="getContentTypeBadgeClass(content.contentType)">
                    {{ getContentTypeLabel(content.contentType) }}
                  </span>
                </div>

                <!-- 内容主体 -->
                <div class="prose prose-sm max-w-none">
                  <!-- 文本内容 (Markdown) -->
                  <div
                    v-if="content.contentType === 'text' && content.contentText"
                    v-html="renderMarkdown(content.contentText)"
                    class="text-gray-700"
                  ></div>

                  <!-- 代码内容 (Markdown with code block) -->
                  <div
                    v-else-if="(content.contentType === 'code' || content.contentType === 'algorithm_template') && content.contentText"
                    v-html="renderMarkdown(content.contentText)"
                    class="text-gray-700"
                  ></div>

                  <!-- 链接内容 -->
                  <div v-else-if="content.contentType === 'link' && content.linkUrl" class="flex items-center space-x-2">
                    <svg class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                    </svg>
                    <a
                      :href="content.linkUrl"
                      target="_blank"
                      rel="noopener noreferrer"
                      class="text-blue-600 hover:text-blue-800 hover:underline"
                    >
                      {{ content.linkUrl }}
                    </a>
                  </div>
                </div>

                <!-- 时间信息 -->
                <div class="mt-3 pt-3 border-t border-gray-100 text-xs text-gray-500">
                  创建时间: {{ formatDate(content.createdAt) }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { getAllCareerPaths } from '@/api/careerPaths'
import { getSkillsByCareerPath } from '@/api/skills'
import { getContentsByFocusArea } from '@/api/learningContentApi'

// Configure marked for code highlighting
marked.setOptions({
  breaks: true,
  gfm: true,
  highlight: function(code, lang) {
    // Basic syntax highlighting would go here
    // For now, just return the code wrapped in a pre/code block
    return `<pre><code class="language-${lang}">${code}</code></pre>`
  }
})

// State
const careerPaths = ref([])
const selectedCareerPathId = ref(null)
const skills = ref([])
const expandedSkills = ref([])
const selectedFocusAreaId = ref(null)
const selectedFocusAreaName = ref('')
const learningData = ref({
  focusAreaId: null,
  focusAreaName: '',
  stages: []
})
const selectedStageId = ref(null)
const selectedStage = ref(null)

const loading = ref({
  skills: false,
  contents: false
})

// Methods
const selectCareerPath = async (id) => {
  selectedCareerPathId.value = id
  selectedFocusAreaId.value = null
  selectedFocusAreaName.value = ''
  selectedStageId.value = null
  selectedStage.value = null
  expandedSkills.value = []
  learningData.value = { focusAreaId: null, focusAreaName: '', stages: [] }
  await loadSkills()
}

const loadSkills = async () => {
  if (!selectedCareerPathId.value) return

  loading.value.skills = true
  try {
    const response = await getSkillsByCareerPath(selectedCareerPathId.value)
    skills.value = response.data || response || []
  } catch (error) {
    console.error('Failed to load skills:', error)
    alert('加载技能失败')
  } finally {
    loading.value.skills = false
  }
}

const toggleSkill = (skillId) => {
  const index = expandedSkills.value.indexOf(skillId)
  if (index > -1) {
    expandedSkills.value.splice(index, 1)
  } else {
    expandedSkills.value.push(skillId)
  }
}

const selectFocusArea = async (id, name) => {
  selectedFocusAreaId.value = id
  selectedFocusAreaName.value = name
  selectedStageId.value = null
  selectedStage.value = null
  await loadLearningContents()
}

const loadLearningContents = async () => {
  if (!selectedFocusAreaId.value) return

  loading.value.contents = true
  try {
    const response = await getContentsByFocusArea(selectedFocusAreaId.value)
    learningData.value = response || { focusAreaId: null, focusAreaName: '', stages: [] }
  } catch (error) {
    console.error('Failed to load learning contents:', error)
    alert('加载学习内容失败')
    learningData.value = { focusAreaId: null, focusAreaName: '', stages: [] }
  } finally {
    loading.value.contents = false
  }
}

const selectStage = (stage) => {
  selectedStageId.value = stage.stageId
  selectedStage.value = stage
}

const getContentTypeBadgeClass = (type) => {
  const classes = {
    text: 'px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-700',
    code: 'px-2 py-1 text-xs rounded-full bg-green-100 text-green-700',
    link: 'px-2 py-1 text-xs rounded-full bg-purple-100 text-purple-700',
    algorithm_template: 'px-2 py-1 text-xs rounded-full bg-orange-100 text-orange-700'
  }
  return classes[type] || 'px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-700'
}

const getContentTypeLabel = (type) => {
  const labels = {
    text: '文本',
    code: '代码',
    link: '链接',
    algorithm_template: '算法模版'
  }
  return labels[type] || type
}

const renderMarkdown = (text) => {
  if (!text) return ''
  const html = marked(text)
  return DOMPurify.sanitize(html)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Initialize
onMounted(async () => {
  try {
    const response = await getAllCareerPaths()
    careerPaths.value = response.data || response || []
    if (careerPaths.value.length > 0) {
      await selectCareerPath(careerPaths.value[0].id)
    }
  } catch (error) {
    console.error('Failed to load career paths:', error)
  }
})
</script>

<style scoped>
/* Markdown prose styling */
:deep(.prose) {
  color: #374151;
}

:deep(.prose h1),
:deep(.prose h2),
:deep(.prose h3),
:deep(.prose h4) {
  color: #111827;
  font-weight: 600;
  margin-top: 1em;
  margin-bottom: 0.5em;
}

:deep(.prose p) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  line-height: 1.6;
}

:deep(.prose code) {
  background-color: #f3f4f6;
  padding: 0.2em 0.4em;
  border-radius: 0.25rem;
  font-size: 0.875em;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

:deep(.prose pre) {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 1em;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin-top: 1em;
  margin-bottom: 1em;
}

:deep(.prose pre code) {
  background-color: transparent;
  padding: 0;
  color: #f9fafb;
  font-size: 0.875em;
}

:deep(.prose ul),
:deep(.prose ol) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  padding-left: 1.5em;
}

:deep(.prose li) {
  margin-top: 0.25em;
  margin-bottom: 0.25em;
}

:deep(.prose a) {
  color: #2563eb;
  text-decoration: none;
}

:deep(.prose a:hover) {
  text-decoration: underline;
}

:deep(.prose blockquote) {
  border-left: 4px solid #e5e7eb;
  padding-left: 1em;
  color: #6b7280;
  font-style: italic;
  margin-top: 1em;
  margin-bottom: 1em;
}

:deep(.prose table) {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1em;
  margin-bottom: 1em;
}

:deep(.prose th),
:deep(.prose td) {
  border: 1px solid #e5e7eb;
  padding: 0.5em;
  text-align: left;
}

:deep(.prose th) {
  background-color: #f9fafb;
  font-weight: 600;
}
</style>
