<template>
  <div>
    <!-- 添加资源按钮 -->
    <div class="mb-6">
      <button
        @click="$emit('add')"
        class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
      >
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
        </svg>
        添加我的资源
      </button>
    </div>

    <!-- 空状态 -->
    <div v-if="!resources || resources.length === 0" class="text-center py-8 text-gray-500">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
      </svg>
      <p class="mt-2">暂无学习资源</p>
      <p class="text-sm mt-1">点击上方按钮添加你的学习资源</p>
    </div>

    <!-- 按资源类型分组显示 -->
    <div v-else class="space-y-6">
      <div v-for="(group, type) in groupedResources" :key="type">
        <!-- 资源类型标题 -->
        <h3 class="text-lg font-semibold text-gray-900 mb-3 flex items-center">
          <span class="mr-2">{{ getTypeIcon(type) }}</span>
          {{ getTypeLabel(type) }} ({{ group.length }})
        </h3>

        <!-- 资源列表 -->
        <div class="space-y-3">
          <div
            v-for="resource in group"
            :key="resource.id"
            class="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <!-- 资源标题 -->
                <h4 class="font-medium text-gray-900 mb-1">{{ resource.title }}</h4>

                <!-- 资源元信息 -->
                <div class="flex items-center gap-3 text-sm text-gray-600 mb-2">
                  <span v-if="resource.author">作者: {{ resource.author }}</span>
                  <span v-if="resource.isOfficial" class="text-blue-600 font-medium">
                    ✓ 官方推荐
                  </span>
                  <span v-else-if="resource.createdByUser" class="text-green-600">
                    我添加的
                  </span>
                </div>

                <!-- 资源描述 -->
                <p v-if="resource.description" class="text-sm text-gray-600 mb-2">
                  {{ resource.description }}
                </p>

                <!-- 链接 -->
                <div class="flex items-center gap-3">
                  <a
                    v-if="resource.url"
                    :href="resource.url"
                    target="_blank"
                    class="text-blue-600 hover:text-blue-800 text-sm font-medium"
                  >
                    查看链接 →
                  </a>

                  <!-- 删除按钮（仅用户自己添加的资源） -->
                  <button
                    v-if="!resource.isOfficial && resource.createdByUser"
                    @click="$emit('delete', resource.id)"
                    class="text-red-600 hover:text-red-800 text-sm font-medium"
                  >
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  resources: {
    type: Array,
    default: () => []
  },
  skillId: {
    type: Number,
    required: true
  }
})

defineEmits(['add', 'delete'])

// 按资源类型分组
const groupedResources = computed(() => {
  const groups = {}
  props.resources.forEach(resource => {
    const type = resource.type || 'OTHER'
    if (!groups[type]) {
      groups[type] = []
    }
    groups[type].push(resource)
  })
  return groups
})

// 资源类型图标
const getTypeIcon = (type) => {
  const icons = {
    BOOK: '📚',
    COURSE: '🎥',
    WEBSITE: '🌐',
    ARTICLE: '📝',
    OTHER: '📌'
  }
  return icons[type] || '📌'
}

// 资源类型标签
const getTypeLabel = (type) => {
  const labels = {
    BOOK: '书籍',
    COURSE: '在线课程',
    WEBSITE: '网站',
    ARTICLE: '文章',
    OTHER: '其他'
  }
  return labels[type] || '其他'
}
</script>
