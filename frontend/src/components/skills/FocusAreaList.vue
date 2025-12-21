<template>
  <div>
    <!-- 空状态 -->
    <div v-if="!focusAreas || focusAreas.length === 0" class="text-center py-8 text-gray-500">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
      </svg>
      <p class="mt-2">暂无专注领域</p>
    </div>

    <!-- 专注领域列表 -->
    <div v-else class="space-y-4">
      <div
        v-for="area in focusAreas"
        :key="area.id"
        class="border border-gray-200 rounded-lg"
      >
        <!-- 标题栏 -->
        <button
          @click="toggleArea(area.id)"
          class="w-full px-4 py-3 flex items-center justify-between hover:bg-gray-50 transition-colors"
        >
          <div class="flex items-center">
            <svg
              :class="[
                'w-5 h-5 mr-2 transition-transform',
                expandedAreas.has(area.id) ? 'transform rotate-90' : ''
              ]"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
            <span class="font-medium text-gray-900">{{ area.name }}</span>
          </div>
        </button>

        <!-- 展开内容 -->
        <div v-if="expandedAreas.has(area.id)" class="px-4 py-3 border-t border-gray-200 bg-gray-50">
          <p class="text-gray-600 text-sm">
            {{ area.description || '暂无描述' }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  focusAreas: {
    type: Array,
    default: () => []
  }
})

const expandedAreas = ref(new Set())

const toggleArea = (areaId) => {
  if (expandedAreas.value.has(areaId)) {
    expandedAreas.value.delete(areaId)
  } else {
    expandedAreas.value.add(areaId)
  }
}
</script>
