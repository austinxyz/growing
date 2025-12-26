<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4">
      <!-- 弹窗头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ focusArea ? '编辑专注领域' : '添加专注领域' }}
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
        <!-- 所属技能 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">所属技能 *</label>
          <select
            v-model="form.skillId"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">请选择技能</option>
            <option
              v-for="skill in skills"
              :key="skill.id"
              :value="skill.id"
            >
              {{ skill.name }}
            </option>
          </select>
          <p v-if="focusArea && currentSkillName" class="mt-1 text-xs text-gray-500">
            当前: {{ currentSkillName }}（可修改为其他技能）
          </p>
        </div>

        <!-- 名称 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">名称 *</label>
          <input
            v-model="form.name"
            type="text"
            required
            placeholder="例如: 动态规划"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 描述 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">描述</label>
          <textarea
            v-model="form.description"
            rows="3"
            placeholder="简要描述这个专注领域..."
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          ></textarea>
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
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
          >
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { createFocusArea, updateFocusArea, getAllSkills } from '@/api/skills'

const props = defineProps({
  skillId: {
    type: Number,
    required: true
  },
  focusArea: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

const form = ref({
  skillId: '',
  name: '',
  description: ''
})

const skills = ref([])
const submitting = ref(false)

// 当前技能名称（用于编辑时显示）
const currentSkillName = computed(() => {
  if (props.focusArea && skills.value.length > 0) {
    const skill = skills.value.find(s => s.id === props.focusArea.skillId)
    return skill ? skill.name : ''
  }
  return ''
})

// 加载所有技能列表
onMounted(async () => {
  try {
    const data = await getAllSkills()
    skills.value = data || []
  } catch (error) {
    console.error('Failed to load skills:', error)
  }
})

const initForm = () => {
  if (props.focusArea) {
    form.value = {
      skillId: props.focusArea.skillId || props.skillId,
      name: props.focusArea.name || '',
      description: props.focusArea.description || ''
    }
  } else {
    form.value = {
      skillId: props.skillId,
      name: '',
      description: ''
    }
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (props.focusArea) {
      await updateFocusArea(props.focusArea.id, form.value)
    } else {
      await createFocusArea(form.value.skillId, form.value)
    }
    emit('success')
  } catch (error) {
    console.error('Failed to save focus area:', error)
    alert('保存失败,请重试')
  } finally {
    submitting.value = false
  }
}

watch(() => props.focusArea, initForm, { immediate: true })
</script>
