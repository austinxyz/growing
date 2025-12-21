<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
      <!-- 弹窗头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ resource ? '编辑学习资源' : '添加学习资源' }}
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
        <!-- 资源标题 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">资源标题 *</label>
          <input
            v-model="form.title"
            type="text"
            required
            placeholder="例如: LeetCode 算法题库"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 资源类型 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">资源类型 *</label>
          <select
            v-model="form.resourceType"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">请选择类型</option>
            <option value="DOCUMENT">文档</option>
            <option value="VIDEO">视频</option>
            <option value="COURSE">课程</option>
            <option value="BOOK">书籍</option>
            <option value="BLOG">博客</option>
            <option value="PRACTICE">练习</option>
            <option value="TOOL">工具</option>
            <option value="ARTICLE">文章</option>
            <option value="WEBSITE">网站</option>
            <option value="OTHER">其他</option>
          </select>
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

        <!-- 链接 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">链接 URL</label>
          <input
            v-model="form.url"
            type="url"
            placeholder="https://..."
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 作者 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">作者/来源</label>
          <input
            v-model="form.author"
            type="text"
            placeholder="例如: MIT, Google, 某某老师"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 是否官方 -->
        <div class="flex items-center">
          <input
            v-model="form.isOfficial"
            type="checkbox"
            id="isOfficial"
            class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
          />
          <label for="isOfficial" class="ml-2 block text-sm text-gray-900">
            官方资源 ⭐
          </label>
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
import { createOfficialResource, updateResource } from '@/api/skills'

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

const submitting = ref(false)

const form = ref({
  title: '',
  resourceType: '',
  description: '',
  url: '',
  author: '',
  isOfficial: true
})

// 初始化表单数据
const initForm = () => {
  if (props.resource) {
    form.value = {
      title: props.resource.title || '',
      resourceType: props.resource.resourceType || '',
      description: props.resource.description || '',
      url: props.resource.url || '',
      author: props.resource.author || '',
      isOfficial: props.resource.isOfficial !== undefined ? props.resource.isOfficial : true
    }
  } else {
    form.value = {
      title: '',
      resourceType: '',
      description: '',
      url: '',
      author: '',
      isOfficial: true
    }
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (props.resource) {
      await updateResource(props.resource.id, form.value)
    } else {
      await createOfficialResource(props.skillId, form.value)
    }
    emit('success')
  } catch (error) {
    console.error('Failed to save learning resource:', error)
    alert('保存失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 监听 resource prop 变化
watch(() => props.resource, () => {
  initForm()
}, { immediate: true })
</script>
