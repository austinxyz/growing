<template>
  <div :class="[
    'border rounded-lg p-4 transition-all duration-200 shadow-sm hover:shadow-md',
    todo.isCompleted
      ? 'bg-gray-50 border-gray-300 opacity-75'
      : 'bg-white border-gray-200 hover:border-blue-300'
  ]">
    <div class="flex items-start gap-3">
      <!-- 复选框 -->
      <div class="flex-shrink-0 mt-1">
        <input
          type="checkbox"
          :checked="todo.isCompleted"
          @change="$emit('toggle-complete', todo)"
          class="w-5 h-5 text-blue-600 rounded focus:ring-2 focus:ring-blue-500 cursor-pointer transition-transform hover:scale-110"
        />
      </div>

      <!-- 内容区域 -->
      <div class="flex-1 min-w-0">
        <!-- 标题行 -->
        <div class="flex items-start justify-between gap-2 mb-2">
          <div class="flex-1">
            <!-- 优先级标签 -->
            <span
              v-if="todo.priority >= 3"
              :class="[
                'inline-block px-2 py-0.5 rounded-full text-xs font-bold mr-2',
                getPriorityClass(todo.priority)
              ]"
            >
              {{ getPriorityText(todo.priority) }}
            </span>

            <!-- 类型图标 -->
            <span class="text-base mr-2" :title="getTypeText(todo.todoType)">
              {{ getTypeIcon(todo.todoType) }}
            </span>

            <!-- 标题 -->
            <span :class="[
              'font-semibold text-gray-900',
              todo.isCompleted && 'line-through text-gray-500'
            ]">
              {{ todo.title }}
            </span>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-1 flex-shrink-0">
            <button
              @click="$emit('edit', todo)"
              class="px-2 py-1 text-blue-600 hover:bg-blue-50 rounded text-xs font-medium transition-colors"
              title="编辑"
            >
              编辑
            </button>
            <button
              @click="$emit('delete', todo.id)"
              class="px-2 py-1 text-red-600 hover:bg-red-50 rounded text-xs font-medium transition-colors"
              title="删除"
            >
              删除
            </button>
          </div>
        </div>

        <!-- 描述 -->
        <div v-if="todo.description" class="mt-2 text-sm text-gray-700 prose prose-sm max-w-none">
          <div v-html="renderMarkdown(todo.description)" class="markdown-content"></div>
        </div>

        <!-- 关联资源 -->
        <div v-if="todo.resourceLinks && todo.resourceLinks.length > 0" class="mt-3 space-y-1.5">
          <div
            v-for="(link, idx) in todo.resourceLinks"
            :key="idx"
            class="flex items-center gap-2 group"
          >
            <span class="text-xs">{{ getResourceIcon(link.resourceType) }}</span>
            <a
              :href="getResourceUrl(link)"
              @click.prevent="handleResourceClick(link)"
              class="text-sm text-blue-600 hover:text-blue-800 hover:underline flex-1 truncate cursor-pointer"
            >
              {{ link.title }}
            </a>
            <svg class="w-3 h-3 text-gray-400 flex-shrink-0 group-hover:text-blue-600 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
            </svg>
          </div>
        </div>

        <!-- 完成信息 -->
        <div v-if="todo.isCompleted && todo.completedAt" class="mt-3 pt-3 border-t border-gray-200">
          <div class="flex items-center gap-1 text-xs text-gray-600">
            <svg class="w-4 h-4 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="font-medium">完成于 {{ formatDateTime(todo.completedAt) }}</span>
          </div>
          <p v-if="todo.completionNotes" class="mt-1.5 text-sm text-gray-700 bg-green-50 border-l-4 border-green-400 p-2 rounded">
            {{ todo.completionNotes }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import MarkdownIt from 'markdown-it'
import { useRouter } from 'vue-router'

const router = useRouter()

defineProps({
  todo: {
    type: Object,
    required: true
  }
})

defineEmits(['toggle-complete', 'edit', 'delete'])

const md = new MarkdownIt()

const getTypeIcon = (type) => {
  const icons = {
    'General': '📝',
    'StudyMaterial': '📚',
    'Practice': '🏆',
    'ProjectReview': '📁',
    'Checklist': '☑️'
  }
  return icons[type] || '📝'
}

const getTypeText = (type) => {
  const texts = {
    'General': '一般任务',
    'StudyMaterial': '学习材料',
    'Practice': '练习刷题',
    'ProjectReview': '项目回顾',
    'Checklist': '准备清单'
  }
  return texts[type] || '一般任务'
}

const getPriorityClass = (priority) => {
  if (priority >= 4) {
    return 'bg-red-100 text-red-700 border border-red-300'
  } else if (priority >= 3) {
    return 'bg-yellow-100 text-yellow-700 border border-yellow-300'
  }
  return 'bg-gray-100 text-gray-700'
}

const getPriorityText = (priority) => {
  if (priority >= 4) return '高'
  if (priority >= 3) return '中'
  return '低'
}

// 获取资源图标
const getResourceIcon = (resourceType) => {
  const icons = {
    'Question': '📝',
    'LearningResource': '📚',
    'SystemDesignCase': '🏗️',
    'Project': '📁',
    'ManagementExperience': '👥',
    'ExternalLink': '🔗'
  }
  return icons[resourceType] || '📄'
}

// 获取资源URL（仅用于显示，实际跳转通过handleResourceClick）
const getResourceUrl = (link) => {
  if (link.resourceType === 'ExternalLink') {
    return link.url
  }
  return '#' // 系统资源不需要真实URL
}

// 处理资源点击
const handleResourceClick = (link) => {
  if (link.resourceType === 'ExternalLink') {
    // 外部链接：新标签页打开
    window.open(link.url, '_blank')
    return
  }

  // 系统资源：在新标签页打开对应详情页
  const routeMap = {
    'Question': () => {
      // 跳转到试题库页面
      const route = router.resolve({ name: 'QuestionBank' })
      window.open(route.href, '_blank')
    },
    'LearningResource': () => {
      // 跳转到职业技能页面（学习材料在技能详情中）
      const route = router.resolve({ name: 'MyCareerPaths' })
      window.open(route.href, '_blank')
    },
    'SystemDesignCase': () => {
      // 跳转到系统设计案例详情
      const route = router.resolve({
        name: 'SystemDesignCaseDetail',
        params: { id: link.resourceId }
      })
      window.open(route.href, '_blank')
    },
    'Project': () => {
      // 跳转到项目经验页面
      const route = router.resolve({ name: 'ProjectExperiences' })
      window.open(route.href, '_blank')
    },
    'ManagementExperience': () => {
      // 跳转到管理经验页面
      const route = router.resolve({ name: 'ManagementExperiences' })
      window.open(route.href, '_blank')
    }
  }

  const handler = routeMap[link.resourceType]
  if (handler) {
    handler()
  }
}

const renderMarkdown = (text) => {
  if (!text) return ''
  return md.render(text)
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
/* Markdown内容样式 */
.markdown-content :deep(p) {
  margin-bottom: 0.5rem;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  margin-left: 1.25rem;
  margin-bottom: 0.5rem;
}

.markdown-content :deep(li) {
  margin-bottom: 0.25rem;
}

.markdown-content :deep(strong) {
  font-weight: 600;
  color: #1f2937;
}

.markdown-content :deep(code) {
  background-color: #f3f4f6;
  padding: 0.125rem 0.25rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  font-family: monospace;
}
</style>
