<template>
  <div class="preparation-checklist-merged flex gap-4 h-full">
    <!-- 左侧：清单项（占主要空间） -->
    <div class="flex-1 flex flex-col">
      <!-- 进度条 -->
      <div class="mb-4 bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg p-4 border border-blue-200">
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm font-semibold text-gray-700">📊 完成进度</span>
          <span class="text-sm font-medium text-blue-600">
            {{ completedCount }}/{{ checklistItems.length }} 完成
          </span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-3 shadow-inner">
          <div
            class="bg-gradient-to-r from-green-500 to-green-600 h-3 rounded-full transition-all duration-500 shadow-sm"
            :style="{ width: completionProgress + '%' }"
          ></div>
        </div>
        <p class="text-xs text-gray-600 mt-2 text-right font-medium">{{ completionProgress }}%</p>
      </div>

      <!-- 清单项列表 -->
      <div class="flex-1 overflow-y-auto space-y-3">
        <div v-if="loading" class="text-center py-8">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          <p class="text-sm text-gray-500 mt-2">加载中...</p>
        </div>

        <div v-else-if="checklistItems.length > 0" class="space-y-3">
          <ChecklistCard
            v-for="item in sortedChecklistItems"
            :key="item.checklistItemId"
            :item="item"
            :expanded="expandedItemId === item.checklistItemId"
            @toggle-expand="toggleExpand"
            @toggle-complete="toggleComplete"
            @edit="editItem"
          />
        </div>

        <div v-else class="text-center py-12 bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
          <svg class="w-16 h-16 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
          </svg>
          <p class="text-gray-600 font-medium">暂无准备清单</p>
        </div>
      </div>
    </div>

    <!-- 右侧：独立TODO列表 -->
    <div class="w-80 flex flex-col bg-white rounded-lg shadow-md p-4 border border-gray-200">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-sm font-bold text-gray-900 flex items-center gap-2">
          <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          自定义TODO
        </h3>
        <button
          @click="createCustomTodo"
          class="px-2 py-1 bg-purple-500 text-white rounded text-xs hover:bg-purple-600 transition-colors"
          title="新建TODO"
        >
          ＋
        </button>
      </div>

      <div class="flex-1 overflow-y-auto space-y-2">
        <div v-if="customTodos.length === 0" class="text-center py-8 text-gray-400 text-xs">
          <p>暂无自定义TODO</p>
          <p class="mt-1">点击"+"添加</p>
        </div>

        <div
          v-for="todo in sortedCustomTodos"
          :key="todo.id"
          class="border rounded p-3 hover:shadow-sm transition-shadow"
          :class="todo.isCompleted ? 'bg-gray-50 border-gray-300 opacity-75' : 'bg-white border-gray-200'"
        >
          <div class="flex items-start gap-2">
            <input
              type="checkbox"
              :checked="todo.isCompleted"
              @change="toggleComplete(todo)"
              class="mt-1 w-4 h-4 text-purple-600 rounded cursor-pointer"
            />
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-1 mb-1">
                <span
                  v-if="todo.priority >= 3"
                  :class="[
                    'px-1.5 py-0.5 rounded-full text-xs font-bold',
                    todo.priority >= 4 ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-700'
                  ]"
                >
                  {{ todo.priority >= 4 ? '高' : '中' }}
                </span>
                <!-- 标题（如果有资源则显示为链接） -->
                <a
                  v-if="todo.resourceLinks && todo.resourceLinks.length > 0"
                  @click.stop="handleCustomTodoTitleClick(todo)"
                  class="text-sm font-medium text-blue-600 hover:text-blue-800 hover:underline cursor-pointer inline-flex items-center gap-1"
                  :class="todo.isCompleted ? 'line-through opacity-75' : ''"
                >
                  {{ todo.title }}
                  <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                  </svg>
                </a>
                <span
                  v-else
                  class="text-sm font-medium"
                  :class="todo.isCompleted ? 'line-through text-gray-500' : 'text-gray-900'"
                >
                  {{ todo.title }}
                </span>
              </div>
              <div class="flex gap-1">
                <button
                  @click="editItem(todo)"
                  class="text-xs text-blue-600 hover:text-blue-700"
                >
                  编辑
                </button>
                <button
                  @click="deleteTodo(todo.id)"
                  class="text-xs text-red-600 hover:text-red-700"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <TodoFormModal
      v-if="showTodoForm"
      :todo="editingTodo"
      @save="saveTodo"
      @close="closeTodoForm"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import preparationTodoApi from '@/api/preparationTodoApi'
import ChecklistCard from './ChecklistCard.vue'
import TodoFormModal from './TodoFormModal.vue'

const router = useRouter()

const props = defineProps({
  stageId: {
    type: Number,
    required: true
  }
})

const allItems = ref([])
const expandedItemId = ref(null)
const showTodoForm = ref(false)
const editingTodo = ref(null)
const loading = ref(false)

// 分离checklist项和自定义TODO
const checklistItems = computed(() => {
  return allItems.value.filter(item => item.fromChecklist)
})

const customTodos = computed(() => {
  return allItems.value.filter(item => !item.fromChecklist)
})

// 完成数量（只统计checklist）
const completedCount = computed(() => {
  return checklistItems.value.filter(t => t.isCompleted).length
})

// 完成进度
const completionProgress = computed(() => {
  if (checklistItems.value.length === 0) return 0
  return Math.round((completedCount.value / checklistItems.value.length) * 100)
})

// 排序：未完成在前，按优先级降序
const sortedChecklistItems = computed(() => {
  return [...checklistItems.value].sort((a, b) => {
    if (a.isCompleted !== b.isCompleted) {
      return a.isCompleted ? 1 : -1
    }
    const aPriority = a.checklistPriority ? 4 : (a.priority || 2)
    const bPriority = b.checklistPriority ? 4 : (b.priority || 2)
    return bPriority - aPriority
  })
})

const sortedCustomTodos = computed(() => {
  return [...customTodos.value].sort((a, b) => {
    if (a.isCompleted !== b.isCompleted) {
      return a.isCompleted ? 1 : -1
    }
    if (a.priority !== b.priority) {
      return b.priority - a.priority
    }
    return a.orderIndex - b.orderIndex
  })
})

// 加载所有项
const loadItems = async () => {
  loading.value = true
  try {
    const data = await preparationTodoApi.getTodosWithChecklist(props.stageId)
    allItems.value = data || []
  } catch (error) {
    console.error('加载准备项列表失败:', error)
    alert('加载失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

// 切换展开/收起
const toggleExpand = (itemId) => {
  if (expandedItemId.value === itemId) {
    expandedItemId.value = null
  } else {
    expandedItemId.value = itemId
  }
}

// 创建自定义TODO
const createCustomTodo = () => {
  editingTodo.value = null
  showTodoForm.value = true
}

// 编辑项
const editItem = (item) => {
  // 如果是checklist项且没有id，需要补充interviewStageId
  if (item.checklistItemId && !item.id) {
    editingTodo.value = {
      ...item,
      interviewStageId: props.stageId
    }
  } else {
    editingTodo.value = item
  }
  showTodoForm.value = true
}

// 关闭表单
const closeTodoForm = () => {
  showTodoForm.value = false
  editingTodo.value = null
}

// 保存TODO
const saveTodo = async (todoData) => {
  try {
    // 记住当前展开的项（如果是checklist项）
    const checklistItemId = editingTodo.value?.checklistItemId
    let shouldKeepExpanded = false

    if (editingTodo.value?.id) {
      // 更新已有TODO
      await preparationTodoApi.updateTodo(editingTodo.value.id, todoData)
      // 如果是checklist关联的TODO，保持展开
      if (checklistItemId) {
        shouldKeepExpanded = true
      }
    } else if (checklistItemId && !editingTodo.value?.id) {
      // 展开checklist项
      await preparationTodoApi.expandChecklist(checklistItemId, todoData)
      shouldKeepExpanded = true
    } else {
      // 创建全新TODO
      await preparationTodoApi.createTodo(props.stageId, todoData)
    }

    await loadItems()

    // 保存后保持展开状态
    if (shouldKeepExpanded && checklistItemId) {
      expandedItemId.value = checklistItemId
    }

    closeTodoForm()
  } catch (error) {
    console.error('保存TODO失败:', error)
    alert('保存失败，请重试')
  }
}

// 切换完成状态
const toggleComplete = async (item) => {
  // 未展开的checklist不能标记完成
  if (item.fromChecklist && !item.hasDetails) {
    alert('请先展开此清单项，添加详细信息后再标记完成')
    return
  }

  try {
    await preparationTodoApi.toggleComplete(
      item.id,
      !item.isCompleted,
      null
    )
    await loadItems()
  } catch (error) {
    console.error('切换完成状态失败:', error)
    alert('操作失败，请重试')
  }
}

// 删除TODO
const deleteTodo = async (id) => {
  if (!confirm('确定删除此TODO？')) return

  try {
    await preparationTodoApi.deleteTodo(id)
    await loadItems()
  } catch (error) {
    console.error('删除TODO失败:', error)
    alert('删除失败，请重试')
  }
}

// 处理自定义TODO标题点击
const handleCustomTodoTitleClick = (todo) => {
  if (todo.resourceLinks && todo.resourceLinks.length > 0) {
    handleResourceClick(todo.resourceLinks[0])
  }
}

// 处理资源链接点击
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

// 监听stageId变化
watch(() => props.stageId, (newStageId) => {
  if (newStageId) {
    loadItems()
  }
}, { immediate: true })
</script>

<style scoped>
/* 自定义样式 */
</style>
