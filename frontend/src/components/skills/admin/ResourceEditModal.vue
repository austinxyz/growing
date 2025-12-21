<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
      <!-- 弹窗头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ resource ? '编辑资源' : '添加官方资源' }}
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
        <!-- 资源类型 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">资源类型 *</label>
          <select
            v-model="form.type"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">请选择</option>
            <option value="BOOK">书籍</option>
            <option value="COURSE">在线课程</option>
            <option value="WEBSITE">网站</option>
            <option value="ARTICLE">文章</option>
            <option value="OTHER">其他</option>
          </select>
        </div>

        <!-- 资源标题 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">标题 *</label>
          <input
            v-model="form.title"
            type="text"
            required
            placeholder="例如: 算法导论"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 链接 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">链接</label>
          <input
            v-model="form.url"
            type="url"
            placeholder="https://example.com"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 作者/来源 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">作者/来源</label>
          <input
            v-model="form.author"
            type="text"
            placeholder="例如: CLRS"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 描述 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">描述</label>
          <textarea
            v-model="form.description"
            rows="3"
            placeholder="简要描述这个学习资源..."
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
import { ref, watch } from 'vue'
import { createOfficialResource, updateResourceAdmin } from '@/api/learningResources'

const props = defineProps({
  skillId: {
    type: Number,
    required: true
  },
  resource: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

const form = ref({
  type: '',
  title: '',
  url: '',
  author: '',
  description: ''
})

const submitting = ref(false)

const initForm = () => {
  if (props.resource) {
    form.value = {
      type: props.resource.type || '',
      title: props.resource.title || '',
      url: props.resource.url || '',
      author: props.resource.author || '',
      description: props.resource.description || ''
    }
  } else {
    form.value = {
      type: '',
      title: '',
      url: '',
      author: '',
      description: ''
    }
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (props.resource) {
      await updateResourceAdmin(props.resource.id, form.value)
    } else {
      await createOfficialResource(props.skillId, form.value)
    }
    emit('success')
  } catch (error) {
    console.error('Failed to save resource:', error)
    alert('保存失败,请重试')
  } finally {
    submitting.value = false
  }
}

watch(() => props.resource, initForm, { immediate: true })
</script>
