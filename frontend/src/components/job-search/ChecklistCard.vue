<template>
  <div
    :class="[
      'border rounded-lg transition-all duration-200',
      item.isCompleted
        ? 'bg-gray-50 border-gray-300 opacity-75'
        : 'bg-white border-gray-200 shadow-sm hover:shadow-md',
      expanded ? 'ring-2 ring-blue-300' : ''
    ]"
  >
    <!-- 卡片头部（始终显示） -->
    <div class="p-4 cursor-pointer" @click="toggleExpand">
      <div class="flex items-start gap-3">
        <!-- 复选框 -->
        <div class="flex-shrink-0 mt-1">
          <input
            v-if="item.hasDetails"
            type="checkbox"
            :checked="item.isCompleted"
            @change.stop="$emit('toggle-complete', item)"
            class="w-5 h-5 text-blue-600 rounded focus:ring-2 focus:ring-blue-500 cursor-pointer"
          />
          <div v-else class="w-5 h-5 border-2 border-gray-300 rounded bg-gray-100" title="未展开，无法勾选"></div>
        </div>

        <!-- 标题和标签 -->
        <div class="flex-1">
          <div class="flex items-center gap-2 mb-1">
            <!-- 优先级标签 -->
            <span
              v-if="item.checklistPriority"
              class="px-2 py-0.5 rounded-full text-xs font-bold bg-red-100 text-red-700 border border-red-300"
            >
              重要
            </span>

            <!-- 分类标签 -->
            <span
              v-if="item.checklistCategory"
              class="px-2 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-700 border border-purple-300"
            >
              {{ item.checklistCategory }}
            </span>

            <!-- 状态标签 -->
            <span
              v-if="!item.hasDetails"
              class="px-2 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-700"
            >
              待展开
            </span>
          </div>

          <!-- 标题（如果有资源则显示为链接） -->
          <h4
            :class="[
              'text-base font-semibold',
              item.isCompleted ? 'line-through text-gray-500' : 'text-gray-900'
            ]"
          >
            <a
              v-if="item.resourceLinks && item.resourceLinks.length > 0"
              @click.stop="handleTitleClick"
              class="text-blue-600 hover:text-blue-800 hover:underline cursor-pointer inline-flex items-center gap-1"
            >
              {{ item.title }}
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
              </svg>
            </a>
            <span v-else>{{ item.title }}</span>
          </h4>

          <!-- 简短描述（未展开时显示前100字，去掉Markdown标记） -->
          <div
            v-if="!expanded && item.description"
            class="text-sm text-gray-600 mt-1 line-clamp-2 prose prose-sm max-w-none"
            v-html="renderMarkdown(getShortDescription(item.description))"
          ></div>
        </div>

        <!-- 展开/收起图标 -->
        <div class="flex-shrink-0">
          <svg
            class="w-5 h-5 text-gray-400 transition-transform"
            :class="expanded ? 'transform rotate-180' : ''"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
          </svg>
        </div>
      </div>
    </div>

    <!-- 展开的详细内容（浏览视图） -->
    <div v-if="expanded" class="border-t border-gray-200 p-4 bg-gray-50">
      <!-- 描述 -->
      <div v-if="item.description" class="mb-4">
        <h5 class="text-sm font-semibold text-gray-700 mb-2">📝 描述</h5>
        <div
          v-html="renderMarkdown(item.description)"
          class="prose prose-sm max-w-none text-sm"
        ></div>
      </div>

      <!-- 关联资源 -->
      <div v-if="item.resourceLinks && item.resourceLinks.length > 0" class="mb-4">
        <h5 class="text-sm font-semibold text-gray-700 mb-2">🔗 相关资源</h5>
        <div class="space-y-2">
          <div
            v-for="(link, idx) in item.resourceLinks"
            :key="idx"
            class="flex items-center gap-2 p-2 bg-white rounded border border-gray-200 hover:border-blue-300 transition-colors group"
          >
            <span class="text-sm">{{ getResourceIcon(link.resourceType) }}</span>
            <a
              :href="getResourceUrl(link)"
              @click.prevent="handleResourceClick(link)"
              class="text-sm text-blue-600 hover:text-blue-800 hover:underline flex-1 cursor-pointer"
            >
              {{ link.title }}
            </a>
            <svg class="w-3 h-3 text-gray-400 group-hover:text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
            </svg>
          </div>
        </div>
      </div>

      <!-- 完成信息 -->
      <div v-if="item.isCompleted && item.completedAt" class="mb-4 p-3 bg-green-50 border border-green-200 rounded">
        <div class="flex items-center gap-2 text-sm text-green-800 font-medium mb-1">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          完成于 {{ formatDateTime(item.completedAt) }}
        </div>
        <p v-if="item.completionNotes" class="text-sm text-gray-700">{{ item.completionNotes }}</p>
      </div>

      <!-- 操作按钮 -->
      <div class="flex gap-2">
        <button
          v-if="item.hasDetails"
          @click.stop="$emit('edit', item)"
          class="px-4 py-2 bg-blue-500 text-white text-sm rounded-lg hover:bg-blue-600 transition-colors"
        >
          ✏️ 编辑详情
        </button>
        <button
          v-else
          @click.stop="addDetails"
          class="px-4 py-2 bg-green-500 text-white text-sm rounded-lg hover:bg-green-600 transition-colors"
        >
          ➕ 添加详情
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import MarkdownIt from 'markdown-it'
import { useRouter } from 'vue-router'

const router = useRouter()
const md = new MarkdownIt()

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  expanded: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle-expand', 'toggle-complete', 'edit'])

const toggleExpand = () => {
  emit('toggle-expand', props.item.checklistItemId)
}

const addDetails = () => {
  // 为未展开的清单项添加详情
  const editData = {
    checklistItemId: props.item.checklistItemId,
    title: props.item.title,
    description: props.item.description || '',
    todoType: 'Checklist',
    priority: props.item.priority || 2
  }
  emit('edit', editData)
}

const renderMarkdown = (text) => {
  if (!text) return ''
  return md.render(text)
}

const getShortDescription = (text) => {
  if (!text) return ''
  // 截取前100个字符
  const maxLength = 100
  if (text.length <= maxLength) {
    return text
  }
  return text.substring(0, maxLength) + '...'
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

const getResourceUrl = (link) => {
  if (link.resourceType === 'ExternalLink') {
    return link.url
  }
  return '#'
}

const handleTitleClick = () => {
  // 点击标题时，跳转到第一个资源链接
  if (props.item.resourceLinks && props.item.resourceLinks.length > 0) {
    handleResourceClick(props.item.resourceLinks[0])
  }
}

const handleResourceClick = (link) => {
  if (link.resourceType === 'ExternalLink') {
    window.open(link.url, '_blank')
    return
  }

  const routeMap = {
    'Question': () => {
      const route = router.resolve({
        name: 'QuestionBank',
        query: { questionId: link.resourceId }
      })
      window.open(route.href, '_blank')
    },
    'LearningResource': () => {
      const route = router.resolve({ name: 'MyCareerPaths' })
      window.open(route.href, '_blank')
    },
    'SystemDesignCase': () => {
      const route = router.resolve({
        name: 'SystemDesignCaseDetail',
        params: { id: link.resourceId }
      })
      window.open(route.href, '_blank')
    },
    'Project': () => {
      const route = router.resolve({ name: 'ProjectExperiences' })
      window.open(route.href, '_blank')
    },
    'ManagementExperience': () => {
      const route = router.resolve({ name: 'ManagementExperiences' })
      window.open(route.href, '_blank')
    }
  }

  const handler = routeMap[link.resourceType]
  if (handler) {
    handler()
  }
}
</script>

<style scoped>
/* Markdown内容样式 */
.prose :deep(p) {
  margin-bottom: 0.5rem;
}

.prose :deep(ul),
.prose :deep(ol) {
  margin-left: 1.25rem;
  margin-bottom: 0.5rem;
}

.prose :deep(li) {
  margin-bottom: 0.25rem;
}

.prose :deep(strong) {
  font-weight: 600;
  color: #1f2937;
}

.prose :deep(code) {
  background-color: #f3f4f6;
  padding: 0.125rem 0.25rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  font-family: monospace;
}

/* 限制行数 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
