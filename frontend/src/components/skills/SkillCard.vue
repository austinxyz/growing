<template>
  <div
    class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 cursor-pointer border border-gray-200 overflow-hidden"
    @click="$emit('click')"
  >
    <div class="p-6">
      <!-- 技能标题 -->
      <div class="flex items-start justify-between mb-3">
        <h3 class="text-xl font-semibold text-gray-900 flex items-center">
          {{ skill.name }}
          <span v-if="skill.isImportant" class="ml-2 text-yellow-500" title="重要技能">⭐</span>
        </h3>
      </div>

      <!-- 技能描述 -->
      <p class="text-gray-600 text-sm mb-4 line-clamp-2">
        {{ skill.description || '暂无描述' }}
      </p>

      <!-- 统计信息 -->
      <div class="flex items-center gap-4 text-sm text-gray-500">
        <div class="flex items-center">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
          </svg>
          <span>{{ skill.focusAreaCount || 0 }}个专注领域</span>
        </div>
        <div class="flex items-center">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
          </svg>
          <span>{{ skill.resourceCount || 0 }}个学习资源</span>
        </div>
      </div>
    </div>

    <!-- 底部操作区 -->
    <div class="bg-gray-50 px-6 py-3 border-t border-gray-200">
      <button class="text-blue-600 hover:text-blue-800 text-sm font-medium">
        查看详情 →
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  skill: {
    type: Object,
    required: true
  }
})

defineEmits(['click'])
</script>
