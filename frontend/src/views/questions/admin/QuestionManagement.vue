<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-full px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">试题管理</h1>
        <p class="text-sm text-gray-600 mt-1">管理公共试题和用户创建的试题</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex h-[calc(100vh-100px)]">
      <!-- 左侧：技能和Focus Area选择 (384px) -->
      <aside class="w-96 bg-white border-r border-gray-200 overflow-y-auto">
        <div class="p-4">
          <!-- 职业路径 Tabs -->
          <div class="mb-4">
            <h3 class="text-sm font-semibold text-gray-700 mb-2">职业路径</h3>
            <div class="space-y-1">
              <button
                v-for="cp in careerPaths"
                :key="cp.id"
                @click="selectCareerPath(cp.id)"
                :class="[
                  'w-full px-3 py-2 text-left text-sm rounded-md transition-colors',
                  selectedCareerPathId === cp.id
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                ]"
              >
                {{ cp.icon }} {{ cp.name }}
              </button>
            </div>
          </div>

          <!-- 技能列表 -->
          <div class="mb-4">
            <h3 class="text-sm font-semibold text-gray-700 mb-2">技能</h3>

            <div v-if="loading.skills" class="text-center text-gray-500 py-4">
              加载中...
            </div>

            <div v-else-if="skills.length === 0" class="text-center text-gray-400 py-4">
              暂无技能
            </div>

            <div v-else class="space-y-1">
              <button
                v-for="skill in skills"
                :key="skill.id"
                @click="selectSkill(skill)"
                :class="[
                  'w-full px-3 py-2 text-left text-sm rounded-md transition-colors',
                  selectedSkillId === skill.id
                    ? 'bg-blue-50 text-blue-700 font-medium'
                    : 'text-gray-700 hover:bg-gray-50'
                ]"
              >
                {{ skill.icon }} {{ skill.name }}
              </button>
            </div>
          </div>

          <!-- Focus Area 列表 -->
          <div v-if="selectedSkill">
            <h3 class="text-sm font-semibold text-gray-700 mb-2">Focus Areas</h3>

            <div v-if="selectedSkill.focusAreas && selectedSkill.focusAreas.length > 0" class="space-y-1">
              <button
                v-for="fa in selectedSkill.focusAreas"
                :key="fa.id"
                @click="selectFocusArea(fa.id, fa.name)"
                :class="[
                  'w-full px-3 py-2 text-left text-sm rounded-md transition-colors',
                  selectedFocusAreaId === fa.id
                    ? 'bg-green-50 text-green-700 font-medium'
                    : 'text-gray-600 hover:bg-gray-50'
                ]"
              >
                • {{ fa.name }}
              </button>
            </div>

            <div v-else class="text-center text-gray-400 py-4 text-sm">
              该技能暂无 Focus Areas
            </div>
          </div>
        </div>
      </aside>

      <!-- 右侧：试题列表 -->
      <section class="flex-1 bg-gray-50 overflow-y-auto">
        <div class="p-6">
          <div v-if="!selectedFocusAreaId" class="text-center text-gray-400 py-12">
            请先选择 Focus Area
          </div>

          <div v-else>
            <!-- 头部 -->
            <div class="flex items-center justify-between mb-6">
              <div>
                <h2 class="text-xl font-semibold text-gray-900">
                  {{ selectedFocusAreaName }} - 试题列表
                </h2>
                <p class="text-sm text-gray-600 mt-1">
                  共 {{ questions.length }} 道试题
                </p>
              </div>
              <button
                @click="showAddQuestionModal"
                class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
              >
                + 添加试题
              </button>
            </div>

            <!-- 加载状态 -->
            <div v-if="loading.questions" class="text-center text-gray-500 py-8">
              加载试题中...
            </div>

            <!-- 试题列表 -->
            <div v-else-if="questions.length === 0" class="text-center text-gray-400 py-8">
              暂无试题
            </div>

            <div v-else class="space-y-3">
              <QuestionCard
                v-for="q in questions"
                :key="q.id"
                :question="q"
                :show-creator="true"
                :show-actions="true"
                @click="viewQuestion(q)"
                @edit="editQuestion(q)"
                @delete="deleteQuestion(q.id)"
              />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminQuestionApi } from '@/api/questionApi'
import { getAllCareerPaths } from '@/api/careerPaths'
import { getSkillsByCareerPath } from '@/api/skills'
import QuestionCard from '@/components/questions/QuestionCard.vue'
import QuestionEditModal from '@/components/questions/QuestionEditModal.vue'

// State
const careerPaths = ref([])
const selectedCareerPathId = ref(null)
const skills = ref([])
const selectedSkillId = ref(null)
const selectedSkill = ref(null)
const selectedFocusAreaId = ref(null)
const selectedFocusAreaName = ref('')
const questions = ref([])
const showEditModal = ref(false)
const editingQuestion = ref(null)

const loading = ref({
  skills: false,
  questions: false
})

// Computed
const allFocusAreas = computed(() => {
  return skills.value.flatMap(skill => skill.focusAreas || [])
})

// Methods
const selectCareerPath = async (id) => {
  selectedCareerPathId.value = id
  selectedSkillId.value = null
  selectedSkill.value = null
  selectedFocusAreaId.value = null
  questions.value = []
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

const selectSkill = (skill) => {
  selectedSkillId.value = skill.id
  selectedSkill.value = skill
  selectedFocusAreaId.value = null
  questions.value = []
}

const selectFocusArea = async (id, name) => {
  selectedFocusAreaId.value = id
  selectedFocusAreaName.value = name
  await loadQuestions()
}

const loadQuestions = async () => {
  if (!selectedFocusAreaId.value) return

  loading.value.questions = true
  try {
    const response = await adminQuestionApi.getAllQuestions({
      focusAreaId: selectedFocusAreaId.value
    })
    questions.value = response.data || []
  } catch (error) {
    console.error('Failed to load questions:', error)
    alert('加载试题失败')
  } finally {
    loading.value.questions = false
  }
}

const showAddQuestionModal = () => {
  editingQuestion.value = null
  showEditModal.value = true
}

const viewQuestion = (question) => {
  // 可以实现查看详情功能，目前直接编辑
  editQuestion(question)
}

const editQuestion = (question) => {
  editingQuestion.value = question
  showEditModal.value = true
}

const saveQuestion = async (formData) => {
  try {
    if (editingQuestion.value) {
      // 更新
      await adminQuestionApi.updateQuestion(editingQuestion.value.id, formData)
      alert('试题更新成功')
    } else {
      // 新建
      const data = {
        ...formData,
        focusAreaId: formData.focusAreaId || selectedFocusAreaId.value
      }
      await adminQuestionApi.createQuestion(data)
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
    await adminQuestionApi.deleteQuestion(id)
    alert('删除成功')
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
