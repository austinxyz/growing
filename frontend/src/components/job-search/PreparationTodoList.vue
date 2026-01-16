<template>
  <div class="preparation-todo-list">
    <!-- 进度条 -->
    <div class="mb-6 bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg p-4 border border-blue-200">
      <div class="flex items-center justify-between mb-2">
        <span class="text-sm font-semibold text-gray-700">📊 完成进度</span>
        <span class="text-sm font-medium text-blue-600">
          {{ completedCount }}/{{ todos.length }} 完成
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
    <div class="mb-4">
      <button
        @click="createTodo"
        class="w-full px-4 py-3 bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-lg hover:from-blue-600 hover:to-purple-600 font-medium shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        新建TODO
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
      <p class="text-sm text-gray-500 mt-2">加载中...</p>
    </div>

    <!-- TODO列表 -->
    <div v-else-if="todos.length > 0" class="space-y-3">
      <TodoItem
        v-for="todo in sortedTodos"
        :key="todo.id"
        :todo="todo"
        @toggle-complete="toggleComplete"
        @edit="editTodo"
        @delete="deleteTodo"
      />
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12 bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
      <svg class="w-16 h-16 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
      </svg>
      <p class="text-gray-600 font-medium">暂无TODO项</p>
      <p class="text-xs text-gray-500 mt-1">点击上方"新建TODO"按钮开始添加</p>
    </div>

    <!-- 创建/编辑对话框 -->
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
import preparationTodoApi from '@/api/preparationTodoApi'
import TodoItem from './TodoItem.vue'
import TodoFormModal from './TodoFormModal.vue'

const props = defineProps({
  stageId: {
    type: Number,
    required: true
  }
})

const todos = ref([])
const showTodoForm = ref(false)
const editingTodo = ref(null)
const loading = ref(false)

// 完成数量
const completedCount = computed(() => {
  return todos.value.filter(t => t.isCompleted).length
})

// 完成进度
const completionProgress = computed(() => {
  if (todos.value.length === 0) return 0
  return Math.round((completedCount.value / todos.value.length) * 100)
})

// 排序：未完成在前，按优先级降序，然后按orderIndex升序
const sortedTodos = computed(() => {
  return [...todos.value].sort((a, b) => {
    // 未完成的排在前面
    if (a.isCompleted !== b.isCompleted) {
      return a.isCompleted ? 1 : -1
    }
    // 优先级高的排在前面
    if (a.priority !== b.priority) {
      return b.priority - a.priority
    }
    // 按orderIndex排序
    return a.orderIndex - b.orderIndex
  })
})

// 加载TODO列表（纯TODO，不包含Checklist）
const loadTodos = async () => {
  loading.value = true
  try {
    // From preparationTodoApi.js - getTodosByStage(stageId)
    const data = await preparationTodoApi.getTodosByStage(props.stageId)
    todos.value = data || []
  } catch (error) {
    console.error('加载TODO列表失败:', error)
    alert('加载失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

// 创建TODO
const createTodo = () => {
  editingTodo.value = null
  showTodoForm.value = true
}

// 编辑TODO
const editTodo = (todo) => {
  editingTodo.value = todo
  showTodoForm.value = true
}

// 关闭表单
const closeTodoForm = () => {
  showTodoForm.value = false
  editingTodo.value = null
}

// 保存TODO（创建或更新）
const saveTodo = async (todoData) => {
  try {
    if (editingTodo.value?.id) {
      // From preparationTodoApi.js - updateTodo(id, todoData)
      await preparationTodoApi.updateTodo(editingTodo.value.id, todoData)
    } else {
      // From preparationTodoApi.js - createTodo(stageId, todoData)
      await preparationTodoApi.createTodo(props.stageId, todoData)
    }
    await loadTodos()
    closeTodoForm()
  } catch (error) {
    console.error('保存TODO失败:', error)
    alert('保存失败，请重试')
  }
}

// 切换完成状态
const toggleComplete = async (todo) => {
  try {
    // From preparationTodoApi.js - toggleComplete(id, isCompleted, completionNotes)
    await preparationTodoApi.toggleComplete(
      todo.id,
      !todo.isCompleted,
      null // 完成时不需要立即填写备注
    )
    await loadTodos()
  } catch (error) {
    console.error('切换完成状态失败:', error)
    alert('操作失败，请重试')
  }
}

// 删除TODO
const deleteTodo = async (id) => {
  if (!confirm('确定删除此TODO？')) return

  try {
    // From preparationTodoApi.js - deleteTodo(id)
    await preparationTodoApi.deleteTodo(id)
    await loadTodos()
  } catch (error) {
    console.error('删除TODO失败:', error)
    alert('删除失败，请重试')
  }
}

// 监听stageId变化，重新加载
watch(() => props.stageId, (newStageId) => {
  if (newStageId) {
    loadTodos()
  }
}, { immediate: true })
</script>

<style scoped>
/* 自定义样式（如需要） */
</style>
