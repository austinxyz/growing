<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题和职业路径tabs -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-full px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900 mb-4">我的题库</h1>

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

      <!-- 中间：试题列表 (35%) -->
      <section class="w-1/3 bg-gray-50 border-r border-gray-200 overflow-y-auto">
        <div class="p-4">
          <!-- 筛选器 -->
          <div class="flex items-center justify-between mb-4">
            <div class="flex space-x-2">
              <button
                @click="setDifficulty('ALL')"
                :class="[
                  'px-3 py-1 text-xs font-medium rounded-full',
                  difficultyFilter === 'ALL'
                    ? 'bg-gray-800 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                ]"
              >
                全部
              </button>
              <button
                @click="setDifficulty('EASY')"
                :class="[
                  'px-3 py-1 text-xs font-medium rounded-full',
                  difficultyFilter === 'EASY'
                    ? 'bg-green-500 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                ]"
              >
                Easy
              </button>
              <button
                @click="setDifficulty('MEDIUM')"
                :class="[
                  'px-3 py-1 text-xs font-medium rounded-full',
                  difficultyFilter === 'MEDIUM'
                    ? 'bg-yellow-500 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                ]"
              >
                Medium
              </button>
              <button
                @click="setDifficulty('HARD')"
                :class="[
                  'px-3 py-1 text-xs font-medium rounded-full',
                  difficultyFilter === 'HARD'
                    ? 'bg-red-500 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                ]"
              >
                Hard
              </button>
            </div>

            <button
              @click="showAddQuestionModal"
              :disabled="!selectedFocusAreaId"
              class="px-3 py-1 text-xs font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              + 添加我的试题
            </button>
          </div>

          <div v-if="!selectedFocusAreaId" class="text-center text-gray-400 py-8">
            请先选择专注领域
          </div>

          <div v-else-if="loading.questions" class="text-center text-gray-500 py-8">
            加载试题中...
          </div>

          <div v-else-if="filteredQuestions.length === 0" class="text-center text-gray-400 py-8">
            暂无试题
          </div>

          <div v-else class="space-y-3">
            <QuestionCard
              v-for="q in filteredQuestions"
              :key="q.id"
              :question="q"
              :show-actions="!q.isOfficial"
              @click="selectQuestion(q.id)"
              @edit="editQuestion(q)"
              @delete="deleteQuestion(q.id)"
            />
          </div>
        </div>
      </section>

      <!-- 右侧：试题详情 + 笔记 (40%) -->
      <section class="flex-1 bg-white overflow-y-auto">
        <div class="p-6">
          <div v-if="!selectedQuestion" class="text-center text-gray-400 py-12">
            选择一个试题查看详情
          </div>

          <div v-else class="space-y-6">
            <!-- 试题详情 -->
            <div>
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center space-x-2">
                  <DifficultyBadge :difficulty="selectedQuestion.difficulty" />
                  <span v-if="selectedQuestion.isOfficial" class="text-xs px-2 py-1 bg-blue-100 text-blue-600 rounded-full">
                    公共试题
                  </span>
                  <span v-else class="text-xs px-2 py-1 bg-purple-100 text-purple-600 rounded-full">
                    我的试题
                  </span>
                </div>

                <div v-if="!selectedQuestion.isOfficial" class="flex space-x-2">
                  <button
                    @click="editQuestion(selectedQuestion)"
                    class="text-sm text-blue-600 hover:text-blue-800"
                  >
                    编辑
                  </button>
                  <button
                    @click="deleteQuestion(selectedQuestion.id)"
                    class="text-sm text-red-600 hover:text-red-800"
                  >
                    删除
                  </button>
                </div>
              </div>

              <!-- LeetCode链接 (仅编程题) -->
              <div v-if="selectedQuestion.programmingDetails?.leetcodeUrl" class="mb-4">
                <a
                  :href="selectedQuestion.programmingDetails.leetcodeUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="inline-flex items-center px-3 py-2 text-sm font-medium text-orange-600 bg-orange-50 rounded-lg hover:bg-orange-100 hover:text-orange-700 transition-colors"
                >
                  <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M13.483 0a1.374 1.374 0 0 0-.961.438L7.116 6.226l-3.854 4.126a5.266 5.266 0 0 0-1.209 2.104 5.35 5.35 0 0 0-.125.513 5.527 5.527 0 0 0 .062 2.362 5.83 5.83 0 0 0 .349 1.017 5.938 5.938 0 0 0 1.271 1.818l4.277 4.193.039.038c2.248 2.165 5.852 2.133 8.063-.074l2.396-2.392c.54-.54.54-1.414.003-1.955a1.378 1.378 0 0 0-1.951-.003l-2.396 2.392a3.021 3.021 0 0 1-4.205.038l-.02-.019-4.276-4.193c-.652-.64-.972-1.469-.948-2.263a2.68 2.68 0 0 1 .066-.523 2.545 2.545 0 0 1 .619-1.164L9.13 8.114c1.058-1.134 3.204-1.27 4.43-.278l3.501 2.831c.593.48 1.461.387 1.94-.207a1.384 1.384 0 0 0-.207-1.943l-3.5-2.831c-.8-.647-1.766-1.045-2.774-1.202l2.015-2.158A1.384 1.384 0 0 0 13.483 0zm-2.866 12.815a1.38 1.38 0 0 0-1.38 1.382 1.38 1.38 0 0 0 1.38 1.382H20.79a1.38 1.38 0 0 0 1.38-1.382 1.38 1.38 0 0 0-1.38-1.382z"/>
                  </svg>
                  在 LeetCode 上练习
                  <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                  </svg>
                </a>
              </div>

              <h2 class="text-xl font-semibold text-gray-900 mb-4">
                问题
              </h2>
              <div class="prose prose-sm max-w-none mb-6" v-html="renderMarkdown(selectedQuestion.questionDescription)"></div>

              <div v-if="selectedQuestion.targetPosition || selectedQuestion.targetLevel" class="text-sm text-gray-600 mb-4">
                <span v-if="selectedQuestion.targetPosition">针对职位: {{ selectedQuestion.targetPosition }}</span>
                <span v-if="selectedQuestion.targetLevel" class="ml-3">级别: {{ selectedQuestion.targetLevel }}</span>
              </div>
            </div>

            <!-- 答案要求 -->
            <div v-if="selectedQuestion.answerRequirement" class="border-t border-gray-200 pt-6">
              <h3 class="text-lg font-medium text-gray-900 mb-3">答案要求</h3>
              <div class="prose prose-sm max-w-none bg-blue-50 rounded-md p-4" v-html="renderMarkdown(selectedQuestion.answerRequirement)"></div>
            </div>

            <!-- Red Flags -->
            <div v-if="selectedQuestion.redFlags && selectedQuestion.redFlags.length > 0" class="border-t border-gray-200 pt-6">
              <h3 class="text-lg font-medium text-gray-900 mb-3 flex items-center">
                <span class="text-red-500 mr-2">⚠️</span>
                Red Flags
              </h3>
              <ul class="space-y-2">
                <li
                  v-for="(flag, index) in selectedQuestion.redFlags"
                  :key="index"
                  class="flex items-start"
                >
                  <span class="text-red-500 mr-2">•</span>
                  <span class="text-sm text-gray-700">{{ flag }}</span>
                </li>
              </ul>
            </div>

            <!-- 我的笔记 -->
            <div class="border-t border-gray-200 pt-6">
              <div class="flex items-center justify-between mb-3">
                <h3 class="text-lg font-medium text-gray-900 flex items-center">
                  <span class="mr-2">📝</span>
                  我的笔记
                </h3>
                <button
                  @click="showNoteEditor"
                  class="text-sm text-blue-600 hover:text-blue-800"
                >
                  {{ selectedQuestion.userNote ? '编辑笔记' : '添加笔记' }}
                </button>
              </div>

              <div v-if="selectedQuestion.userNote" class="space-y-4">
                <!-- 核心思路（仅编程题） -->
                <div v-if="selectedQuestion.questionType === 'programming' && selectedQuestion.userNote.coreStrategy" class="bg-green-50 rounded-md p-4 border border-green-200">
                  <h4 class="text-sm font-semibold text-green-900 mb-2 flex items-center">
                    <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    核心思路
                  </h4>
                  <div class="prose prose-sm max-w-none" v-html="renderMarkdown(selectedQuestion.userNote.coreStrategy)"></div>
                </div>

                <!-- 笔记内容 -->
                <div v-if="selectedQuestion.userNote.noteContent" class="prose prose-sm max-w-none bg-gray-50 rounded-md p-4">
                  <div v-html="renderMarkdown(selectedQuestion.userNote.noteContent)"></div>
                </div>

                <!-- 底部信息 -->
                <div class="flex items-center justify-between text-xs text-gray-500">
                  <span>最后编辑: {{ formatDate(selectedQuestion.userNote.updatedAt) }}</span>
                  <button
                    @click="deleteNote"
                    class="text-red-600 hover:text-red-800"
                  >
                    删除笔记
                  </button>
                </div>
              </div>

              <div v-else class="text-sm text-gray-400 italic">
                暂无笔记，点击"添加笔记"开始记录
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 试题编辑弹窗 -->
    <QuestionEditModal
      :is-open="showEditModal"
      :question="editingQuestion"
      :focus-areas="allFocusAreas"
      @save="saveQuestion"
      @cancel="closeEditModal"
    />

    <!-- 笔记编辑器 -->
    <UserNoteEditor
      :is-open="showNoteModal"
      :question-id="selectedQuestionId"
      :initial-note="selectedQuestion?.userNote"
      :question-type="selectedQuestion?.questionType || 'behavioral'"
      @save="saveNote"
      @cancel="closeNoteModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { marked } from 'marked'
import { questionApi } from '@/api/questionApi'
import { getMyCareerPaths } from '@/api/careerPaths'
import { getSkillsByCareerPath } from '@/api/skills'
import QuestionCard from '@/components/questions/QuestionCard.vue'
import DifficultyBadge from '@/components/questions/DifficultyBadge.vue'
import QuestionEditModal from '@/components/questions/QuestionEditModal.vue'
import UserNoteEditor from '@/components/questions/UserNoteEditor.vue'

// State
const careerPaths = ref([])
const selectedCareerPathId = ref(null)
const skills = ref([])
const expandedSkills = ref([])
const selectedFocusAreaId = ref(null)
const selectedFocusAreaName = ref('')
const questions = ref([])
const selectedQuestionId = ref(null)
const difficultyFilter = ref('ALL')
const showEditModal = ref(false)
const showNoteModal = ref(false)
const editingQuestion = ref(null)

const loading = ref({
  skills: false,
  questions: false
})

// Computed
const allFocusAreas = computed(() => {
  return skills.value.flatMap(skill => skill.focusAreas || [])
})

const filteredQuestions = computed(() => {
  if (difficultyFilter.value === 'ALL') {
    return questions.value
  }
  return questions.value.filter(q => q.difficulty === difficultyFilter.value)
})

const selectedQuestion = computed(() => {
  return questions.value.find(q => q.id === selectedQuestionId.value)
})

// Methods
const renderMarkdown = (text) => {
  if (!text) return ''
  try {
    return marked(text, { breaks: true, gfm: true })
  } catch (error) {
    console.error('Markdown rendering error:', error)
    return text
  }
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

const selectCareerPath = async (id) => {
  selectedCareerPathId.value = id
  selectedFocusAreaId.value = null
  selectedQuestionId.value = null
  questions.value = []
  await loadSkills()
}

const loadSkills = async () => {
  if (!selectedCareerPathId.value) return

  loading.value.skills = true
  try {
    const data = await getSkillsByCareerPath(selectedCareerPathId.value)
    skills.value = data || []
    console.log('Loaded skills:', skills.value)
    console.log('Skills count:', skills.value.length)
    if (skills.value.length > 0) {
      console.log('First skill:', skills.value[0])
      console.log('First skill focusAreas:', skills.value[0].focusAreas)
      console.log('FocusAreas length:', skills.value[0].focusAreas?.length)
      if (skills.value[0].focusAreas?.length > 0) {
        console.log('First focusArea:', skills.value[0].focusAreas[0])
      }
    }
  } catch (error) {
    console.error('Failed to load skills:', error)
    alert('加载技能失败: ' + (error.message || '未知错误'))
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
  selectedQuestionId.value = null
  await loadQuestions()
}

const loadQuestions = async () => {
  if (!selectedFocusAreaId.value) return

  loading.value.questions = true
  try {
    console.log('Loading questions for focusAreaId:', selectedFocusAreaId.value)
    const response = await questionApi.getQuestionsByFocusArea(selectedFocusAreaId.value)
    // response已经是data数组（axios拦截器已处理）
    questions.value = response || []
    console.log('Loaded questions:', questions.value)
    console.log('Questions count:', questions.value.length)
  } catch (error) {
    console.error('Failed to load questions:', error)
    alert('加载试题失败')
  } finally {
    loading.value.questions = false
  }
}

const setDifficulty = (difficulty) => {
  difficultyFilter.value = difficulty
}

const selectQuestion = async (id) => {
  selectedQuestionId.value = id
  // 加载完整的试题详情（包含用户笔记）
  try {
    const response = await questionApi.getQuestionById(id)
    // response已经是data对象（axios拦截器已处理）
    const fullQuestion = response
    // 更新questions数组中的对应项
    const index = questions.value.findIndex(q => q.id === id)
    if (index > -1) {
      questions.value[index] = fullQuestion
    }
  } catch (error) {
    console.error('Failed to load question detail:', error)
  }
}

const showAddQuestionModal = () => {
  editingQuestion.value = null
  showEditModal.value = true
}

const editQuestion = (question) => {
  editingQuestion.value = question
  showEditModal.value = true
}

const saveQuestion = async (formData) => {
  try {
    if (editingQuestion.value) {
      // 更新
      await questionApi.updateQuestion(editingQuestion.value.id, formData)
      alert('试题更新成功')
    } else {
      // 新建
      const data = {
        ...formData,
        focusAreaId: formData.focusAreaId || selectedFocusAreaId.value
      }
      await questionApi.createQuestion(data)
      alert('试题创建成功')
    }
    closeEditModal()
    await loadQuestions()
  } catch (error) {
    console.error('Failed to save question:', error)
    alert(error.response?.data?.message || '保存失败')
  }
}

const deleteQuestion = async (id) => {
  if (!confirm('确定要删除这个试题吗？')) return

  try {
    await questionApi.deleteQuestion(id)
    alert('删除成功')
    if (selectedQuestionId.value === id) {
      selectedQuestionId.value = null
    }
    await loadQuestions()
  } catch (error) {
    console.error('Failed to delete question:', error)
    alert(error.response?.data?.message || '删除失败')
  }
}

const closeEditModal = () => {
  showEditModal.value = false
  editingQuestion.value = null
}

const showNoteEditor = () => {
  showNoteModal.value = true
}

const saveNote = async (data) => {
  try {
    await questionApi.saveOrUpdateNote(data.questionId, {
      noteContent: data.noteContent,
      coreStrategy: data.coreStrategy
    })
    alert('笔记保存成功')
    closeNoteModal()
    // 重新加载试题详情
    await selectQuestion(data.questionId)
  } catch (error) {
    console.error('Failed to save note:', error)
    alert(error.response?.data?.message || '保存笔记失败')
  }
}

const deleteNote = async () => {
  if (!confirm('确定要删除这条笔记吗？')) return

  try {
    await questionApi.deleteUserNote(selectedQuestionId.value)
    alert('笔记删除成功')
    // 重新加载试题详情
    await selectQuestion(selectedQuestionId.value)
  } catch (error) {
    console.error('Failed to delete note:', error)
    alert(error.response?.data?.message || '删除笔记失败')
  }
}

const closeNoteModal = () => {
  showNoteModal.value = false
}

// Initialize
onMounted(async () => {
  try {
    const response = await getMyCareerPaths()
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
/* Prose 样式 */
.prose {
  color: #374151;
  line-height: 1.75;
}

.prose p {
  margin-top: 0.75em;
  margin-bottom: 0.75em;
}

.prose h1, .prose h2, .prose h3 {
  margin-top: 1.25em;
  margin-bottom: 0.625em;
  font-weight: 600;
}

.prose ul, .prose ol {
  margin-top: 0.75em;
  margin-bottom: 0.75em;
  padding-left: 1.5em;
}

.prose code {
  background-color: #f3f4f6;
  padding: 0.2em 0.4em;
  border-radius: 0.25em;
  font-size: 0.875em;
}

.prose pre {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 1em;
  border-radius: 0.375em;
  overflow-x: auto;
}

.prose pre code {
  background-color: transparent;
  padding: 0;
  color: inherit;
}
</style>
