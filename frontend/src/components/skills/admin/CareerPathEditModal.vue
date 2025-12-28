<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4">
      <!-- 弹窗头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ careerPath ? '编辑职业路径' : '创建职业路径' }}
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
        <!-- 职业路径名称 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">职业路径名称 *</label>
          <input
            v-model="form.name"
            type="text"
            required
            placeholder="例如: Backend Engineer"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 代码 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">代码 *</label>
          <input
            v-model="form.code"
            type="text"
            required
            placeholder="例如: backend-engineer"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 描述 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">描述</label>
          <textarea
            v-model="form.description"
            rows="3"
            placeholder="简要描述这个职业路径..."
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          ></textarea>
        </div>

        <!-- 图标 Emoji -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">图标 Emoji</label>
          <div class="flex items-center space-x-3 mb-2">
            <div class="text-3xl">{{ form.icon || '💼' }}</div>
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

        <!-- 激活状态 -->
        <div class="flex items-center">
          <input
            v-model="form.isActive"
            type="checkbox"
            id="isActive"
            class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
          />
          <label for="isActive" class="ml-2 block text-sm text-gray-700">
            激活状态
          </label>
        </div>

        <!-- 关联技能 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">关联技能</label>
          <div class="border border-gray-300 rounded-lg p-3 max-h-60 overflow-y-auto">
            <div v-if="loadingSkills" class="text-center py-4 text-gray-500">
              <svg class="animate-spin h-5 w-5 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <p class="mt-2 text-xs">加载中...</p>
            </div>
            <div v-else-if="availableSkills.length === 0" class="text-center py-4 text-gray-400">
              没有可用的技能
            </div>
            <div v-else class="space-y-2">
              <div v-for="skill in availableSkills" :key="skill.id" class="flex items-center">
                <input
                  :id="`skill-${skill.id}`"
                  v-model="form.skillIds"
                  :value="skill.id"
                  type="checkbox"
                  class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <label :for="`skill-${skill.id}`" class="ml-2 block text-sm text-gray-700">
                  {{ skill.name }}
                  <span v-if="skill.description" class="text-xs text-gray-500 ml-1">({{ skill.description }})</span>
                </label>
              </div>
            </div>
          </div>
          <p class="mt-1 text-xs text-gray-500">
            已选择 {{ form.skillIds.length }} 个技能
          </p>
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
import { ref, watch } from 'vue'
import { createCareerPath, updateCareerPath } from '@/api/careerPaths'
import { getAllSkills } from '@/api/skills'

const props = defineProps({
  careerPath: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

const submitting = ref(false)

// 常用职业相关emoji
const commonEmojis = [
  '💼', '👨‍💻', '👩‍💻', '🚀', '🎯',
  '💻', '📱', '🖥️', '⌨️', '🖱️',
  '🎨', '🎬', '📊', '📈', '💡',
  '🧠', '🔧', '⚙️', '🛠️', '🔨',
  '📚', '📖', '🎓', '👥', '🌐',
  '☁️', '🔐', '🔑', '🛡️', '📦',
  '🏗️', '🎤', '💬', '📧', '📞'
]

const form = ref({
  name: '',
  code: '',
  description: '',
  icon: '',
  isActive: true,
  skillIds: []
})

const loadingSkills = ref(false)
const availableSkills = ref([])

// 加载所有技能
const loadSkills = async () => {
  try {
    loadingSkills.value = true
    const skills = await getAllSkills()
    availableSkills.value = skills || []
  } catch (error) {
    console.error('Failed to load skills:', error)
  } finally {
    loadingSkills.value = false
  }
}

const initForm = () => {
  loadSkills() // 加载技能列表

  if (props.careerPath) {
    form.value = {
      name: props.careerPath.name || '',
      code: props.careerPath.code || '',
      description: props.careerPath.description || '',
      icon: props.careerPath.icon || '',
      isActive: props.careerPath.isActive !== false,
      skillIds: props.careerPath.skills?.map(s => s.id) || []
    }
  } else {
    form.value = {
      name: '',
      code: '',
      description: '',
      icon: '',
      isActive: true,
      skillIds: []
    }
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (props.careerPath) {
      await updateCareerPath(props.careerPath.id, form.value)
    } else {
      await createCareerPath(form.value)
    }
    emit('success')
  } catch (error) {
    console.error('Failed to save career path:', error)
    alert('保存失败，请重试')
  } finally {
    submitting.value = false
  }
}

watch(() => props.careerPath, initForm, { immediate: true })
</script>
