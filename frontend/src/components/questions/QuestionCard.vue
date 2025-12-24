<template>
  <div
    class="question-card bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer"
    @click="$emit('click')"
  >
    <div class="flex items-center justify-between mb-2">
      <div class="flex items-center space-x-2">
        <DifficultyBadge :difficulty="question.difficulty" />
        <span v-if="question.isOfficial" class="text-xs px-2 py-1 bg-blue-100 text-blue-600 rounded-full">
          公共试题
        </span>
        <span v-else class="text-xs px-2 py-1 bg-purple-100 text-purple-600 rounded-full">
          我的试题
        </span>
      </div>
      <div v-if="showActions" class="flex space-x-2" @click.stop>
        <button
          @click="$emit('edit')"
          class="text-sm text-blue-600 hover:text-blue-800"
        >
          编辑
        </button>
        <button
          @click="$emit('delete')"
          class="text-sm text-red-600 hover:text-red-800"
        >
          删除
        </button>
      </div>
    </div>

    <h3 class="text-base font-medium text-gray-900 mb-2 line-clamp-2">
      {{ truncate(question.questionDescription, 100) }}
    </h3>

    <div class="flex items-center space-x-3 text-sm text-gray-600">
      <span v-if="question.targetPosition">
        {{ question.targetPosition }}
      </span>
      <span v-if="question.targetLevel" class="text-gray-500">
        ({{ question.targetLevel }})
      </span>
      <span v-if="showCreator" class="text-gray-400">
        创建者: {{ question.createdByUserId ? `User ${question.createdByUserId}` : 'Admin' }}
      </span>
    </div>

    <div class="mt-2 text-xs text-gray-400">
      创建于 {{ formatDate(question.createdAt) }}
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import DifficultyBadge from './DifficultyBadge.vue'

const props = defineProps({
  question: {
    type: Object,
    required: true
  },
  showCreator: {
    type: Boolean,
    default: false
  },
  showActions: {
    type: Boolean,
    default: false
  }
})

defineEmits(['click', 'edit', 'delete'])

const truncate = (text, length) => {
  if (!text) return ''
  // 移除Markdown语法标记后再截断
  const plainText = text.replace(/[#*`_~\[\]()]/g, '')
  if (plainText.length <= length) return plainText
  return plainText.substring(0, length) + '...'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
