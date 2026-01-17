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
            type="checkbox"
            :checked="item.isCompleted"
            @change.stop="handleToggleComplete"
            class="w-5 h-5 text-blue-600 rounded focus:ring-2 focus:ring-blue-500 cursor-pointer"
          />
        </div>

        <!-- 标题和标签 -->
        <div class="flex-1">
          <div class="flex items-center gap-2 mb-1">
            <!-- 来源标签 -->
            <span
              :class="[
                'px-2 py-0.5 rounded-full text-xs font-medium border',
                item.source === 'AI'
                  ? 'bg-blue-50 text-blue-700 border-blue-300'
                  : 'bg-green-50 text-green-700 border-green-300'
              ]"
            >
              {{ item.source === 'AI' ? '🤖 AI生成' : '👤 用户创建' }}
            </span>

            <!-- 优先级标签 -->
            <span
              :class="[
                'px-2 py-0.5 rounded-full text-xs font-medium border',
                getPriorityClass(item.priority)
              ]"
            >
              优先级: {{ item.priority }}
            </span>

            <!-- 分类标签 -->
            <span
              v-if="item.todoType"
              class="px-2 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-700 border border-purple-300"
            >
              {{ getTodoTypeLabel(item.todoType) }}
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
          ✏️ 编辑
        </button>
        <button
          v-else
          @click.stop="addDetails"
          class="px-4 py-2 bg-green-500 text-white text-sm rounded-lg hover:bg-green-600 transition-colors"
        >
          ➕ 添加详情
        </button>
        <button
          v-if="item.id"
          @click.stop="$emit('delete', item.id)"
          class="px-4 py-2 bg-red-500 text-white text-sm rounded-lg hover:bg-red-600 transition-colors"
        >
          🗑️ 删除
        </button>
      </div>
    </div>

    <!-- 完成备注弹窗 -->
    <div
      v-if="showCompletionDialog"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click.self="showCompletionDialog = false"
    >
      <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">✅ 完成备注</h3>
        <textarea
          v-model="completionNotes"
          rows="4"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none"
          placeholder="记录完成心得、收获或遇到的问题..."
          autofocus
        ></textarea>
        <div class="flex justify-end gap-3 mt-4">
          <button
            @click="cancelComplete"
            class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition-colors"
          >
            取消
          </button>
          <button
            @click="confirmComplete"
            class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
          >
            确认完成
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import MarkdownIt from 'markdown-it'
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()
const md = new MarkdownIt()

// 完成备注相关状态
const showCompletionDialog = ref(false)
const completionNotes = ref('')

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
  // Use id if available (for both AI and user TODOs), fallback to checklistItemId for legacy support
  const itemId = props.item.id || props.item.checklistItemId
  emit('toggle-expand', itemId)
}

// 处理完成状态切换
const handleToggleComplete = () => {
  if (props.item.isCompleted) {
    // 取消完成：直接触发
    emit('toggle-complete', props.item)
  } else {
    // 标记为完成：显示备注输入框
    completionNotes.value = ''
    showCompletionDialog.value = true
  }
}

// 确认完成
const confirmComplete = () => {
  showCompletionDialog.value = false
  emit('toggle-complete', props.item, completionNotes.value)
}

// 取消完成
const cancelComplete = () => {
  showCompletionDialog.value = false
  completionNotes.value = ''
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

// 获取优先级样式
const getPriorityClass = (priority) => {
  if (priority >= 4) return 'bg-red-100 text-red-700 border-red-300 font-bold'
  if (priority >= 3) return 'bg-orange-100 text-orange-700 border-orange-300'
  if (priority >= 2) return 'bg-yellow-100 text-yellow-700 border-yellow-300'
  return 'bg-gray-100 text-gray-600 border-gray-300'
}

// 获取TODO类型标签
const getTodoTypeLabel = (type) => {
  const labels = {
    'General': '一般任务',
    'StudyMaterial': '学习材料',
    'Practice': '练习刷题',
    'ProjectReview': '项目回顾',
    'Checklist': '准备清单'
  }
  return labels[type] || type
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
