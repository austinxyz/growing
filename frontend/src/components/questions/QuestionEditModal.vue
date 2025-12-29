<template>
  <!-- Modal模式 -->
  <div v-if="isOpen && !inline" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- 背景遮罩 -->
    <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="$emit('cancel')"></div>

    <!-- 弹窗内容 -->
    <div class="flex min-h-screen items-center justify-center p-4">
      <div class="relative bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <!-- 头部 -->
        <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
          <h2 class="text-xl font-semibold text-gray-900">
            {{ isEdit ? '编辑试题' : '添加试题' }}
          </h2>
          <button
            @click="$emit('cancel')"
            class="text-gray-400 hover:text-gray-600"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 表单 -->
        <form @submit.prevent="handleSubmit" class="p-4 space-y-4">
          <!-- 所属技能和专注领域（同一行） -->
          <div class="grid grid-cols-2 gap-3">
            <!-- 所属技能（级联选择第一步） -->
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">
                所属技能 <span class="text-red-500">*</span>
              </label>
              <select
                v-model="selectedSkillId"
                required
                class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">请先选择技能</option>
                <option
                  v-for="skill in skills"
                  :key="skill.id"
                  :value="skill.id"
                >
                  {{ skill.name }}
                </option>
              </select>
            </div>

            <!-- 所属 Focus Area（级联选择第二步） -->
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">
                所属专注领域 <span class="text-red-500">*</span>
              </label>
              <select
                v-model="form.focusAreaId"
                required
                :disabled="!selectedSkillId"
                class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
              >
                <option value="">{{ selectedSkillId ? '请选择专注领域' : '请先选择技能' }}</option>
                <option
                  v-for="fa in filteredFocusAreas"
                  :key="fa.id"
                  :value="fa.id"
                >
                  {{ fa.name }}
                </option>
              </select>
            </div>
          </div>

          <!-- 当前提示信息（编辑时显示） -->
          <p v-if="isEdit && currentFocusAreaName" class="text-xs text-gray-500 -mt-2">
            当前: {{ currentSkillName }} / {{ currentFocusAreaName }}
          </p>

          <!-- 题目标题 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              题目标题 <span class="text-red-500">*</span>
            </label>
            <textarea
              v-model="form.title"
              rows="3"
              required
              placeholder="例如: [5] Longest Palindromic Substring"
              class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 resize-y"
            ></textarea>
          </div>

          <!-- 问题描述 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              问题描述
            </label>
            <textarea
              v-model="form.questionDescription"
              rows="4"
              placeholder="输入详细的题目描述（支持 Markdown）"
              class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
          </div>

          <!-- 难度级别 + 针对职位 + 针对级别（同一行） -->
          <div class="grid grid-cols-3 gap-3">
            <!-- 难度级别 -->
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">
                难度 <span class="text-red-500">*</span>
              </label>
              <select
                v-model="form.difficulty"
                required
                class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="EASY">Easy</option>
                <option value="MEDIUM">Medium</option>
                <option value="HARD">Hard</option>
              </select>
            </div>

            <!-- 针对职位 -->
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">
                针对职位
              </label>
              <select
                v-model="form.targetPosition"
                class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">不限职位</option>
                <option
                  v-for="cp in careerPaths"
                  :key="cp.id"
                  :value="cp.name"
                >
                  {{ cp.icon }} {{ cp.name }}
                </option>
              </select>
            </div>

            <!-- 针对级别 -->
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">
                针对级别
              </label>
              <select
                v-model="form.targetLevel"
                class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">不限级别</option>
                <option value="Junior">Junior</option>
                <option value="Senior">Senior</option>
                <option value="Staff">Staff</option>
                <option value="Principal">Principal</option>
              </select>
            </div>
          </div>

          <!-- 答案要求 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              答案要求
            </label>
            <textarea
              v-model="form.answerRequirement"
              rows="3"
              placeholder="说明如何回答这个问题（支持 Markdown）"
              class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
          </div>

          <!-- Red Flags -->
          <RedFlagList v-model="form.redFlags" />

          <!-- 操作按钮 -->
          <div class="flex justify-end items-center pt-4 border-t border-gray-200">
            <div class="flex space-x-3">
              <button
                type="button"
                @click="$emit('cancel')"
                class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
              >
                取消
              </button>
              <button
                type="submit"
                class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
              >
                保存
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
  <!-- Inline模式 -->
  <div v-else-if="inline" class="space-y-4">
    <!-- 表单头部 -->
    <div class="flex items-center justify-between pb-4 border-b border-gray-200">
      <h3 class="text-lg font-semibold text-gray-900">
        {{ isEdit ? '编辑试题' : '新增试题' }}
      </h3>
      <div class="flex gap-2">
        <button
          @click="$emit('cancel')"
          type="button"
          class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors text-sm"
        >
          取消
        </button>
        <button
          @click="handleSubmit"
          type="button"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm"
        >
          保存
        </button>
      </div>
    </div>

    <!-- 表单内容 -->
    <div class="space-y-4">
      <!-- 所属技能和专注领域（同一行） -->
      <div class="grid grid-cols-2 gap-3">
        <!-- 所属技能（级联选择第一步） -->
        <div>
          <label class="block text-xs font-medium text-gray-700 mb-1">
            所属技能 <span class="text-red-500">*</span>
          </label>
          <select
            v-model="selectedSkillId"
            required
            class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">请先选择技能</option>
            <option
              v-for="skill in skills"
              :key="skill.id"
              :value="skill.id"
            >
              {{ skill.name }}
            </option>
          </select>
        </div>

        <!-- 所属 Focus Area（级联选择第二步） -->
        <div>
          <label class="block text-xs font-medium text-gray-700 mb-1">
            所属专注领域 <span class="text-red-500">*</span>
          </label>
          <select
            v-model="form.focusAreaId"
            required
            :disabled="!selectedSkillId"
            class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            <option value="">{{ selectedSkillId ? '请选择专注领域' : '请先选择技能' }}</option>
            <option
              v-for="fa in filteredFocusAreas"
              :key="fa.id"
              :value="fa.id"
            >
              {{ fa.name }}
            </option>
          </select>
        </div>
      </div>

      <!-- 当前提示信息（编辑时显示） -->
      <p v-if="isEdit && currentFocusAreaName" class="text-xs text-gray-500 -mt-2">
        当前: {{ currentSkillName }} / {{ currentFocusAreaName }}
      </p>

      <!-- 题目标题 -->
      <div>
        <label class="block text-xs font-medium text-gray-700 mb-1">
          题目标题 <span class="text-red-500">*</span>
        </label>
        <textarea
          v-model="form.title"
          rows="3"
          required
          placeholder="例如: [5] Longest Palindromic Substring"
          class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 resize-y"
        ></textarea>
      </div>

      <!-- 问题描述 -->
      <div>
        <label class="block text-xs font-medium text-gray-700 mb-1">
          问题描述
        </label>
        <textarea
          v-model="form.questionDescription"
          rows="4"
          placeholder="输入详细的题目描述（支持 Markdown）"
          class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        ></textarea>
      </div>

      <!-- 难度级别 + 针对职位 + 针对级别（同一行） -->
      <div class="grid grid-cols-3 gap-3">
        <!-- 难度级别 -->
        <div>
          <label class="block text-xs font-medium text-gray-700 mb-1">
            难度 <span class="text-red-500">*</span>
          </label>
          <select
            v-model="form.difficulty"
            required
            class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>

        <!-- 针对职位 -->
        <div>
          <label class="block text-xs font-medium text-gray-700 mb-1">
            针对职位
          </label>
          <select
            v-model="form.targetPosition"
            class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">不限职位</option>
            <option
              v-for="cp in careerPaths"
              :key="cp.id"
              :value="cp.name"
            >
              {{ cp.icon }} {{ cp.name }}
            </option>
          </select>
        </div>

        <!-- 针对级别 -->
        <div>
          <label class="block text-xs font-medium text-gray-700 mb-1">
            针对级别
          </label>
          <select
            v-model="form.targetLevel"
            class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">不限级别</option>
            <option value="Junior">Junior</option>
            <option value="Senior">Senior</option>
            <option value="Staff">Staff</option>
            <option value="Principal">Principal</option>
          </select>
        </div>
      </div>

      <!-- 答案要求 -->
      <div>
        <label class="block text-xs font-medium text-gray-700 mb-1">
          答案要求
        </label>
        <textarea
          v-model="form.answerRequirement"
          rows="3"
          placeholder="说明如何回答这个问题（支持 Markdown）"
          class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        ></textarea>
      </div>

      <!-- Red Flags -->
      <RedFlagList v-model="form.redFlags" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import RedFlagList from './RedFlagList.vue'
import { getAllCareerPaths } from '@/api/careerPaths'
import { getAllSkills } from '@/api/skills'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  question: {
    type: Object,
    default: null
  },
  focusAreas: {
    type: Array,
    default: () => []
  },
  currentFocusAreaId: {
    type: Number,
    default: null
  },
  currentFocusAreaName: {
    type: String,
    default: ''
  },
  currentSkillId: {
    type: Number,
    default: null
  },
  inline: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['save', 'cancel', 'edit-ai-note'])

const isEdit = ref(false)
const careerPaths = ref([])
const skills = ref([])
const selectedSkillId = ref('')
const form = ref({
  focusAreaId: '',
  title: '',
  questionDescription: '',
  difficulty: 'EASY',
  answerRequirement: '',
  targetPosition: '',
  targetLevel: '',
  redFlags: []
})

// 根据选择的Skill过滤Focus Areas
const filteredFocusAreas = computed(() => {
  if (!selectedSkillId.value) return []
  return props.focusAreas.filter(fa => fa.skillId === selectedSkillId.value)
})

// 当前技能名称
const currentSkillName = computed(() => {
  const currentFocusArea = props.focusAreas.find(fa => fa.id === props.currentFocusAreaId)
  if (currentFocusArea && skills.value.length > 0) {
    const skill = skills.value.find(s => s.id === currentFocusArea.skillId)
    return skill ? skill.name : ''
  }
  return ''
})


// 初始化数据
onMounted(async () => {
  try {
    // 加载职业路径
    // Note: interceptor returns response.data already
    const careerPathsData = await getAllCareerPaths()
    careerPaths.value = careerPathsData || []

    // 加载所有技能
    // Note: interceptor returns response.data already
    const skillsData = await getAllSkills()
    skills.value = skillsData || []
  } catch (error) {
    console.error('Failed to load data:', error)
  }
})

// 监听Skill选择变化，清空Focus Area选择
watch(selectedSkillId, (newSkillId, oldSkillId) => {
  if (oldSkillId && newSkillId !== oldSkillId) {
    // 只在Skill改变时清空Focus Area，初始化时不清空
    form.value.focusAreaId = ''
  }
})

// 监听 question prop 变化
watch(() => props.question, (newQuestion) => {
  if (newQuestion) {
    isEdit.value = true
    form.value = {
      focusAreaId: newQuestion.focusAreaId || '',
      title: newQuestion.title || '',
      questionDescription: newQuestion.questionDescription || '',
      difficulty: newQuestion.difficulty || 'EASY',
      answerRequirement: newQuestion.answerRequirement || '',
      targetPosition: newQuestion.targetPosition || '',
      targetLevel: newQuestion.targetLevel || '',
      redFlags: newQuestion.redFlags || []
    }

    // 设置选中的Skill（从focusAreaId反推）
    if (newQuestion.focusAreaId && props.focusAreas.length > 0) {
      const focusArea = props.focusAreas.find(fa => fa.id === newQuestion.focusAreaId)
      if (focusArea) {
        selectedSkillId.value = focusArea.skillId
      }
    }
  } else {
    isEdit.value = false
    form.value = {
      focusAreaId: props.currentFocusAreaId || '',  // 使用当前选中的Focus Area
      title: '',
      questionDescription: '',
      difficulty: 'EASY',
      answerRequirement: '',
      targetPosition: '',
      targetLevel: '',
      redFlags: []
    }

    // 新建时，优先使用 currentSkillId，否则从 currentFocusAreaId 反推
    if (props.currentSkillId) {
      selectedSkillId.value = props.currentSkillId
    } else if (props.currentFocusAreaId && props.focusAreas.length > 0) {
      const focusArea = props.focusAreas.find(fa => fa.id === props.currentFocusAreaId)
      if (focusArea) {
        selectedSkillId.value = focusArea.skillId
      }
    }
  }
}, { immediate: true })

const handleSubmit = () => {
  const cleanedForm = {
    ...form.value,
    redFlags: form.value.redFlags.filter(flag => flag.trim() !== '')
  }

  emit('save', cleanedForm)
}
</script>
