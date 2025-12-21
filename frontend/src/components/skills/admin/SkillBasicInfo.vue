<template>
  <div class="max-w-3xl">
    <form @submit.prevent="handleSubmit" class="space-y-6">
      <!-- 技能名称 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">技能名称 *</label>
        <input
          v-model="form.name"
          type="text"
          required
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        />
      </div>

      <!-- 描述 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">描述</label>
        <textarea
          v-model="form.description"
          rows="4"
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

      <!-- 图标 Emoji -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">图标 Emoji</label>
        <div class="flex items-center space-x-3 mb-3">
          <div class="text-4xl">{{ form.icon || '📚' }}</div>
          <input
            v-model="form.icon"
            type="text"
            placeholder="直接粘贴 emoji，如: 💻 📱 🚀"
            class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
        <div class="grid grid-cols-10 gap-2">
          <button
            v-for="emoji in commonEmojis"
            :key="emoji"
            type="button"
            @click="form.icon = emoji"
            class="text-2xl p-2 hover:bg-gray-100 rounded-lg transition-colors"
            :class="{ 'bg-blue-50 ring-2 ring-blue-500': form.icon === emoji }"
          >
            {{ emoji }}
          </button>
        </div>
        <p class="mt-2 text-xs text-gray-500">点击选择常用emoji，或直接在输入框粘贴任意emoji</p>
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
      </div>

      <!-- 保存按钮 -->
      <div class="flex justify-start">
        <button
          type="submit"
          :disabled="submitting"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ submitting ? '保存中...' : '保存更改' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getAllCareerPaths } from '@/api/careerPaths'
import { updateSkill } from '@/api/skills'

const props = defineProps({
  skill: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['updated'])

const careerPaths = ref([])
const submitting = ref(false)

// 常用技能相关emoji
const commonEmojis = [
  '💻', '📱', '🖥️', '⌨️', '🖱️',
  '🎯', '🚀', '⚡', '🔥', '✨',
  '📊', '📈', '📉', '💡', '🧠',
  '🎨', '🔧', '⚙️', '🛠️', '🔨',
  '📚', '📖', '📝', '✏️', '📋',
  '🌐', '☁️', '🔐', '🔑', '🛡️',
  '📦', '🗂️', '🏗️', '🎓', '👥',
  '💼', '📞', '💬', '📧', '🎤',
  '🎮', '🎲', '🧩', '🎪', '🎭',
  '⭐', '🌟', '💫', '🔔', '🏆'
]

const form = ref({
  name: '',
  description: '',
  isImportant: false,
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

const initForm = () => {
  form.value = {
    name: props.skill.name || '',
    description: props.skill.description || '',
    isImportant: props.skill.isImportant || false,
    icon: props.skill.icon || '',
    careerPathIds: props.skill.careerPaths?.map(p => p.id) || []
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    await updateSkill(props.skill.id, form.value)
    alert('保存成功')
    emit('updated')
  } catch (error) {
    console.error('Failed to update skill:', error)
    alert('保存失败,请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadCareerPaths()
  initForm()
})

watch(() => props.skill, () => {
  initForm()
}, { deep: true })
</script>
