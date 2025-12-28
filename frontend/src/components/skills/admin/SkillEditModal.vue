<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
      <!-- 弹窗头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ skill ? '编辑技能' : '创建技能' }}
        </h2>
        <button
          @click="$emit('close')"
          class="text-gray-400 hover:text-gray-600"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <!-- 表单 -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-4">
        <!-- 技能名称 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">技能名称 *</label>
          <input
            v-model="form.name"
            type="text"
            required
            placeholder="例如: 编程"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 描述 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">描述</label>
          <textarea
            v-model="form.description"
            rows="3"
            placeholder="简要描述这个技能..."
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          ></textarea>
        </div>

        <!-- 是否重要 -->
        <div class="flex items-center">
          <input
            v-model="form.isImportant"
            type="checkbox"
            id="isImportant"
            class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
          />
          <label for="isImportant" class="ml-2 block text-sm text-gray-900">
            重要技能 ⭐
          </label>
        </div>

        <!-- 是否为通用分类 -->
        <div class="border border-gray-200 rounded-lg p-4 bg-gray-50">
          <div class="flex items-start">
            <input
              v-model="form.isGeneralOnly"
              type="checkbox"
              id="isGeneralOnly"
              :disabled="!canToggleGeneralOnly && !form.isGeneralOnly"
              class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded mt-0.5 disabled:opacity-50 disabled:cursor-not-allowed"
            />
            <div class="ml-3 flex-1">
              <label for="isGeneralOnly" class="block text-sm font-medium text-gray-900">
                通用分类模式 📋
              </label>
              <p class="text-xs text-gray-500 mt-1">
                启用后，该技能将跳过大分类层级，直接管理 Focus Area
              </p>
              <p v-if="generalOnlyError && !form.isGeneralOnly" class="text-xs text-red-600 mt-2">
                ⚠️ {{ generalOnlyError }}
              </p>
            </div>
          </div>
        </div>

        <!-- 图标 Emoji -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">图标 Emoji</label>
          <div class="flex items-center space-x-3 mb-2">
            <div class="text-3xl">{{ form.icon || '📚' }}</div>
            <input
              v-model="form.icon"
              type="text"
              placeholder="粘贴 emoji，如: 💻"
              class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <div class="grid grid-cols-8 gap-1 max-h-24 overflow-y-auto">
            <button
              v-for="emoji in commonEmojis"
              :key="emoji"
              type="button"
              @click="form.icon = emoji"
              class="text-xl p-1 hover:bg-gray-100 rounded transition-colors"
              :class="{ 'bg-blue-50 ring-1 ring-blue-500': form.icon === emoji }"
            >
              {{ emoji }}
            </button>
          </div>
        </div>

        <!-- 关联职业路径 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">关联职业路径</label>
          <div class="space-y-2">
            <div v-for="path in careerPaths" :key="path.id" class="flex items-center">
              <input
                v-model="form.careerPathIds"
                type="checkbox"
                :value="path.id"
                :id="'path-' + path.id"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label :for="'path-' + path.id" class="ml-2 block text-sm text-gray-900">
                {{ path.name }}
              </label>
            </div>
          </div>
          <p v-if="careerPaths.length === 0" class="text-sm text-gray-500">暂无职业路径</p>
        </div>

        <!-- 按钮 -->
        <div class="flex justify-end gap-3 pt-4">
          <button
            type="button"
            @click="$emit('close')"
            class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            type="submit"
            :disabled="submitting"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { getAllCareerPaths } from '@/api/careerPaths'
import { createSkill, updateSkill } from '@/api/skills'
import majorCategoryApi from '@/api/majorCategoryApi'

const props = defineProps({
  skill: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

const careerPaths = ref([])
const submitting = ref(false)
const categories = ref([])
const canToggleGeneralOnly = ref(true)
const generalOnlyError = ref('')

// 常用技能相关emoji
const commonEmojis = [
  '💻', '📱', '🖥️', '⌨️', '🖱️',
  '🎯', '🚀', '⚡', '🔥', '✨',
  '📊', '📈', '📉', '💡', '🧠',
  '🎨', '🔧', '⚙️', '🛠️', '🔨',
  '📚', '📖', '📝', '✏️', '📋',
  '🌐', '☁️', '🔐', '🔑', '🛡️',
  '📦', '🗂️', '🏗️', '🎓', '👥',
  '💼', '📞', '💬', '📧', '🎤'
]

const form = ref({
  name: '',
  description: '',
  isImportant: false,
  isGeneralOnly: false,
  icon: '',
  careerPathIds: []
})

const loadCareerPaths = async () => {
  try {
    careerPaths.value = await getAllCareerPaths()
  } catch (error) {
    console.error('Failed to load career paths:', error)
  }
}

// 检查是否可以切换通用分类模式
const checkGeneralOnlyToggle = async () => {
  if (!props.skill || !props.skill.id) {
    canToggleGeneralOnly.value = true
    generalOnlyError.value = ''
    return
  }

  try {
    categories.value = await majorCategoryApi.getAllMajorCategories(props.skill.id)
    const hasNonGeneral = categories.value.some(c => c.name !== 'General')

    if (hasNonGeneral) {
      canToggleGeneralOnly.value = false
      generalOnlyError.value = '该技能下存在非 General 的大分类，请先删除这些大分类后再启用通用分类模式'
    } else {
      canToggleGeneralOnly.value = true
      generalOnlyError.value = ''
    }
  } catch (error) {
    console.error('Failed to check categories:', error)
    canToggleGeneralOnly.value = true
    generalOnlyError.value = ''
  }
}

// 初始化表单数据
const initForm = () => {
  if (props.skill) {
    form.value = {
      name: props.skill.name || '',
      description: props.skill.description || '',
      isImportant: props.skill.isImportant || false,
      isGeneralOnly: props.skill.isGeneralOnly || false,
      icon: props.skill.icon || '',
      careerPathIds: props.skill.careerPaths?.map(p => p.id) || []
    }
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (props.skill) {
      await updateSkill(props.skill.id, form.value)
    } else {
      await createSkill(form.value)
    }
    emit('success')
  } catch (error) {
    console.error('Failed to save skill:', error)
    // 如果是后端验证错误，显示具体错误信息
    if (error.response?.data?.message) {
      alert(error.response.data.message)
    } else if (error.response?.status === 400) {
      alert('保存失败：' + (error.message || '请检查输入'))
    } else {
      alert('保存失败,请重试')
    }
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadCareerPaths()
  await checkGeneralOnlyToggle()
  initForm()
})

watch(() => props.skill, async () => {
  await checkGeneralOnlyToggle()
  initForm()
})
</script>
