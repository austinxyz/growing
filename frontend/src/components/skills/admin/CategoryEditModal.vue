<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4">
      <!-- 弹窗头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ category ? '编辑大分类' : '创建大分类' }}
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
        <!-- 大分类名称 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">大分类名称 *</label>
          <input
            v-model="form.name"
            type="text"
            required
            placeholder="例如: 核心概念"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 描述 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">描述</label>
          <textarea
            v-model="form.description"
            rows="3"
            placeholder="简要描述这个大分类..."
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
import majorCategoryApi from '@/api/majorCategoryApi'

const props = defineProps({
  skillId: {
    type: Number,
    required: true
  },
  category: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

const submitting = ref(false)

const form = ref({
  name: '',
  description: ''
})

const initForm = () => {
  if (props.category) {
    form.value = {
      name: props.category.name || '',
      description: props.category.description || ''
    }
  } else {
    form.value = {
      name: '',
      description: ''
    }
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (props.category) {
      await majorCategoryApi.updateMajorCategory(props.category.id, {
        ...form.value,
        skillId: props.skillId
      })
    } else {
      await majorCategoryApi.createMajorCategory(props.skillId, form.value)
    }
    emit('success')
  } catch (error) {
    console.error('Failed to save category:', error)
    alert('保存失败，请重试')
  } finally {
    submitting.value = false
  }
}

watch(() => props.category, initForm, { immediate: true })
</script>
