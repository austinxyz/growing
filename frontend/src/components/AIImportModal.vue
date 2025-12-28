<template>
  <div class="fixed inset-0 z-50 overflow-y-auto" @click.self="$emit('close')">
    <div class="flex items-center justify-center min-h-screen px-4">
      <!-- 遮罩层 -->
      <div class="fixed inset-0 bg-black opacity-30"></div>

      <!-- Modal主体 -->
      <div class="relative bg-white rounded-lg max-w-4xl w-full p-6 shadow-xl">
        <!-- 标题 -->
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-xl font-semibold text-gray-900">🤖 导入AI学习笔记</h3>
          <button
            @click="$emit('close')"
            class="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 说明文字 -->
        <div class="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-md">
          <p class="text-sm text-blue-700">
            为学习内容导入AI生成的笔记和知识点,帮助用户更好地理解和记忆。AI笔记会以特殊样式显示,用户可以在此基础上添加自己的笔记。
          </p>
        </div>

        <!-- 表单 -->
        <div class="space-y-6">
          <!-- AI整体笔记 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              AI整体笔记 <span class="text-red-500">*</span>
            </label>
            <p class="text-xs text-gray-500 mb-2">
              对本学习内容的总体性笔记,支持Markdown格式
            </p>
            <textarea
              v-model="form.aiNote"
              rows="8"
              placeholder="# 学习要点&#10;&#10;1. **核心概念**: ...&#10;2. **关键技术**: ...&#10;3. **实践建议**: ..."
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
            ></textarea>
          </div>

          <!-- AI知识点列表 -->
          <div>
            <div class="flex items-center justify-between mb-2">
              <label class="block text-sm font-medium text-gray-700">
                AI知识点列表 <span class="text-gray-400">(可选)</span>
              </label>
              <button
                @click="addKnowledgePoint"
                class="px-3 py-1 text-sm bg-green-50 text-green-600 border border-green-200 rounded-md hover:bg-green-100 transition-colors"
              >
                + 添加知识点
              </button>
            </div>
            <p class="text-xs text-gray-500 mb-3">
              拆分学习内容中的关键知识点,便于用户针对性学习
            </p>

            <!-- 知识点列表 -->
            <div v-if="form.knowledgePoints.length === 0" class="text-center py-8 text-gray-400 border-2 border-dashed border-gray-200 rounded-md">
              <p class="text-sm">暂无知识点,点击上方按钮添加</p>
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="(kp, index) in form.knowledgePoints"
                :key="index"
                class="p-4 border border-gray-200 rounded-md bg-gray-50"
              >
                <div class="flex items-start justify-between mb-2">
                  <span class="text-sm font-medium text-gray-700">知识点 #{{ index + 1 }}</span>
                  <button
                    @click="removeKnowledgePoint(index)"
                    class="text-red-500 hover:text-red-700 text-sm"
                  >
                    删除
                  </button>
                </div>
                <div class="space-y-2">
                  <input
                    v-model="kp.title"
                    type="text"
                    placeholder="知识点标题 (例如: 什么是闭包?)"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                  />
                  <textarea
                    v-model="kp.content"
                    rows="3"
                    placeholder="知识点详细内容,支持Markdown..."
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
                  ></textarea>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="flex items-center justify-end space-x-3 mt-6 pt-6 border-t border-gray-200">
          <button
            @click="$emit('close')"
            class="px-4 py-2 text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors"
          >
            取消
          </button>
          <button
            @click="handleSubmit"
            :disabled="saving || !canSubmit"
            :class="[
              'px-4 py-2 rounded-md transition-colors',
              canSubmit && !saving
                ? 'bg-blue-600 text-white hover:bg-blue-700'
                : 'bg-gray-300 text-gray-500 cursor-not-allowed'
            ]"
          >
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { importAINote, importAIKnowledgePoints } from '@/api/learningContentApi'

export default {
  name: 'AIImportModal',
  props: {
    contentId: {
      type: Number,
      required: true
    }
  },
  emits: ['close', 'success'],
  data() {
    return {
      form: {
        aiNote: '',
        knowledgePoints: []
      },
      saving: false
    }
  },
  computed: {
    canSubmit() {
      // 至少需要AI整体笔记
      return this.form.aiNote.trim().length > 0
    }
  },
  methods: {
    addKnowledgePoint() {
      this.form.knowledgePoints.push({
        title: '',
        content: ''
      })
    },
    removeKnowledgePoint(index) {
      this.form.knowledgePoints.splice(index, 1)
    },
    async handleSubmit() {
      if (!this.canSubmit || this.saving) {
        return
      }

      this.saving = true
      try {
        // 1. 导入AI整体笔记
        await importAINote(this.contentId, this.form.aiNote)

        // 2. 导入AI知识点 (如果有)
        const validKPs = this.form.knowledgePoints.filter(kp =>
          kp.title.trim() && kp.content.trim()
        )
        if (validKPs.length > 0) {
          await importAIKnowledgePoints(this.contentId, validKPs)
        }

        this.$emit('success')
        alert('AI笔记导入成功!')
      } catch (error) {
        console.error('导入AI笔记失败:', error)
        alert('导入失败,请稍后重试')
      } finally {
        this.saving = false
      }
    }
  }
}
</script>
