<template>
  <div class="preparation-checklist-merged flex flex-col h-full">
    <!-- 顶部工具栏 -->
    <div class="flex items-center justify-between mb-4">
      <!-- 进度条 -->
      <div class="flex-1 bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg p-4 border border-blue-200">
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm font-semibold text-gray-700">📊 完成进度</span>
          <span class="text-sm font-medium text-blue-600">
            {{ completedCount }}/{{ allItems.length }} 完成
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

      <!-- 新建按钮 -->
      <button
        @click="createCustomTodo"
        class="ml-4 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors flex items-center gap-2"
        title="新建准备项"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        新建准备项
      </button>
    </div>

    <!-- 清单项列表 -->
    <div class="flex-1 overflow-y-auto space-y-3">
      <div v-if="loading" class="text-center py-8">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        <p class="text-sm text-gray-500 mt-2">加载中...</p>
      </div>

      <div v-else-if="allItems.length > 0" class="space-y-3">
        <ChecklistCard
          v-for="item in sortedAllItems"
          :key="item.id || item.checklistItemId"
          :item="item"
          :expanded="expandedItemId === (item.id || item.checklistItemId)"
          :show-actions="true"
          @toggle-expand="toggleExpand"
          @toggle-complete="toggleComplete"
          @edit="editItem"
          @delete="deleteTodo"
        />
      </div>

      <div v-else class="text-center py-12 bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
        <svg class="w-16 h-16 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
        </svg>
        <p class="text-gray-600 font-medium">暂无准备清单</p>
        <p class="text-sm text-gray-500 mt-2">点击右上角"新建准备项"按钮添加</p>
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

// 完成数量（统计所有TODO）
const completedCount = computed(() => {
  return allItems.value.filter(t => t.isCompleted).length
})

// 完成进度
const completionProgress = computed(() => {
  if (allItems.value.length === 0) return 0
  return Math.round((completedCount.value / allItems.value.length) * 100)
})

// 统一排序：未完成在前，按优先级降序，同优先级按order排序
const sortedAllItems = computed(() => {
  return [...allItems.value].sort((a, b) => {
    // 未完成的在前
    if (a.isCompleted !== b.isCompleted) {
      return a.isCompleted ? 1 : -1
    }
    // 按优先级降序
    const aPriority = a.checklistPriority ? 4 : (a.priority || 2)
    const bPriority = b.checklistPriority ? 4 : (b.priority || 2)
    if (aPriority !== bPriority) {
      return bPriority - aPriority
    }
    // 同优先级按orderIndex排序
    return (a.orderIndex || 0) - (b.orderIndex || 0)
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
const toggleComplete = async (item, completionNotes = null) => {
  try {
    // 如果是未展开的checklist项（没有TODO id），需要先创建TODO记录
    if (item.fromChecklist && !item.id) {
      // 先展开checklist创建TODO，然后标记为完成
      const newTodo = await preparationTodoApi.expandChecklist(item.checklistItemId, {
        title: item.title,
        description: item.description || '',
        todoType: 'Checklist',
        priority: item.checklistPriority ? 4 : (item.priority || 2)
      })

      // 立即标记为完成
      if (newTodo && newTodo.id) {
        await preparationTodoApi.toggleComplete(newTodo.id, true, completionNotes)
      }
    } else {
      // 已有TODO id，直接切换完成状态
      await preparationTodoApi.toggleComplete(
        item.id,
        !item.isCompleted,
        completionNotes
      )
    }

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
