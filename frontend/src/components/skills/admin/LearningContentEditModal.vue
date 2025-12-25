<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-3xl w-full mx-4 max-h-[90vh] overflow-y-auto">
      <!-- 弹窗头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200 sticky top-0 bg-white">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ content ? '编辑学习内容' : '添加学习内容' }}
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
        <!-- 标题 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">标题 *</label>
          <input
            v-model="form.title"
            type="text"
            required
            placeholder="例如: 双指针技巧秒杀七道数组题目"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 内容类型 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">内容类型 *</label>
          <select
            v-model="form.contentType"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">请选择内容类型</option>
            <option value="article">文章</option>
            <option value="video">视频</option>
            <option value="code_example">代码示例</option>
            <option value="interactive">交互式教程</option>
            <option value="documentation">文档</option>
          </select>
        </div>

        <!-- URL -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">URL</label>
          <input
            v-model="form.url"
            type="url"
            placeholder="https://example.com/article"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 网站 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">来源网站</label>
          <select
            v-model="form.websiteId"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option :value="null">无</option>
            <option value="1">labuladong的算法小抄 (支持iframe)</option>
            <option value="2">Hello Interview (不支持iframe)</option>
          </select>
          <p class="mt-1 text-xs text-gray-500">
            选择网站后，系统会自动根据网站配置决定是否支持iframe内嵌显示
          </p>
        </div>

        <!-- 作者 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">作者</label>
          <input
            v-model="form.author"
            type="text"
            placeholder="例如: labuladong"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- 描述 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">描述</label>
          <textarea
            v-model="form.description"
            rows="3"
            placeholder="简要描述这个学习内容..."
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          ></textarea>
        </div>

        <!-- 可见性 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">可见性</label>
          <select
            v-model="form.visibility"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="PUBLIC">公开</option>
            <option value="PRIVATE">私有</option>
          </select>
        </div>

        <!-- 排序 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">排序</label>
          <input
            v-model.number="form.sortOrder"
            type="number"
            min="0"
            placeholder="0"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
          <p class="mt-1 text-xs text-gray-500">
            数字越小越靠前，默认为0
          </p>
        </div>

        <!-- 按钮 -->
        <div class="flex justify-end gap-3 pt-4 sticky bottom-0 bg-white border-t border-gray-200 -mx-6 px-6 py-4">
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
import learningContentApi from '@/api/learningContentApi'

const props = defineProps({
  focusAreaId: {
    type: Number,
    required: true
  },
  stageId: {
    type: Number,
    required: true
  },
  content: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

const form = ref({
  title: '',
  contentType: '',
  url: '',
  websiteId: null,
  author: '',
  description: '',
  visibility: 'PUBLIC',
  sortOrder: 0
})

const submitting = ref(false)

const initForm = () => {
  if (props.content) {
    // 编辑模式：填充现有数据
    form.value = {
      title: props.content.title || '',
      contentType: props.content.contentType || '',
      url: props.content.url || '',
      websiteId: props.content.websiteId || null,
      author: props.content.author || '',
      description: props.content.description || '',
      visibility: props.content.visibility || 'PUBLIC',
      sortOrder: props.content.sortOrder || 0
    }
  } else {
    // 新建模式：重置表单
    form.value = {
      title: '',
      contentType: '',
      url: '',
      websiteId: null,
      author: '',
      description: '',
      visibility: 'PUBLIC',
      sortOrder: 0
    }
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    const data = {
      ...form.value,
      focusAreaId: props.focusAreaId,
      stageId: props.stageId,
      // 将 websiteId 转换为数字或null
      websiteId: form.value.websiteId ? parseInt(form.value.websiteId) : null
    }

    if (props.content) {
      // 编辑模式
      await learningContentApi.updateContent(props.content.id, data)
    } else {
      // 新建模式
      await learningContentApi.createContent(data)
    }

    emit('success')
  } catch (error) {
    console.error('Failed to save learning content:', error)
    alert('保存失败: ' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

watch(() => props.content, initForm, { immediate: true })
</script>
