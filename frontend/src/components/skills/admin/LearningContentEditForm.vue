<template>
  <div class="space-y-4">
    <!-- 表单头部 -->
    <div class="flex items-center justify-between pb-4 border-b border-gray-200">
      <h3 class="text-lg font-semibold text-gray-900">
        {{ content && content.id ? '编辑学习内容' : '新增学习内容' }}
      </h3>
      <div class="flex gap-2">
        <!-- 导入AI笔记按钮 - 仅编辑模式显示 -->
        <button
          v-if="content && content.id"
          @click="$emit('import-ai')"
          type="button"
          class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors text-sm"
        >
          🤖 导入AI笔记
        </button>
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
      <!-- 标题 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">标题 *</label>
        <input
          v-model="form.title"
          type="text"
          required
          placeholder="例如: Kubernetes官方文档概览"
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
          <option value="book">书籍</option>
          <option value="course">课程</option>
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

      <!-- 作者 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">作者</label>
        <input
          v-model="form.author"
          type="text"
          placeholder="例如: Kubernetes Team"
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
          <option value="PUBLIC">公开（所有用户可见）</option>
          <option value="PRIVATE">私有（仅创建者可见）</option>
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
        <p class="mt-1 text-xs text-gray-500">数字越小越靠前</p>
      </div>
    </div>
  </div>
</template>

<script>
import learningContentApi from '@/api/learningContentApi'

export default {
  name: 'LearningContentEditForm',
  props: {
    content: {
      type: Object,
      default: null
    },
    focusAreaId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      form: {
        title: '',
        contentType: '',
        url: '',
        author: '',
        description: '',
        visibility: 'PUBLIC',
        sortOrder: 0
      }
    }
  },
  watch: {
    content: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.form = {
            title: newVal.title || '',
            contentType: newVal.contentType || '',
            url: newVal.url || '',
            author: newVal.author || '',
            description: newVal.description || '',
            visibility: newVal.visibility || 'PUBLIC',
            sortOrder: newVal.sortOrder || 0
          }
        } else {
          this.resetForm()
        }
      }
    }
  },
  methods: {
    resetForm() {
      this.form = {
        title: '',
        contentType: '',
        url: '',
        author: '',
        description: '',
        visibility: 'PUBLIC',
        sortOrder: 0
      }
    },
    async handleSubmit() {
      if (!this.form.title || !this.form.contentType) {
        alert('请填写必填项（标题和内容类型）')
        return
      }

      try {
        const data = {
          ...this.form,
          focusAreaId: this.focusAreaId
        }

        if (this.content) {
          // 更新
          await learningContentApi.updateContent(this.content.id, data)
        } else {
          // 创建
          await learningContentApi.createContent(data)
        }

        this.$emit('save')
      } catch (error) {
        console.error('保存失败:', error)
        alert('保存失败: ' + (error.message || '未知错误'))
      }
    }
  }
}
</script>
