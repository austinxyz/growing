# 面试准备TODO组件实现指南

## 实施状态

✅ **后端完成** (100%)
- [x] 数据库表创建
- [x] Entity, DTO, Repository
- [x] Service层实现
- [x] Controller API实现
- [x] 前端API文件创建

🚧 **前端待完成**
- [ ] 创建TodoList.vue组件
- [ ] 创建TodoItem.vue组件
- [ ] 创建TodoForm.vue对话框
- [ ] 在JobApplicationList.vue中集成

## 组件结构

```
frontend/src/components/job-search/
├── PreparationTodoList.vue    (TODO列表主组件)
├── TodoItem.vue                (TODO卡片组件)
└── TodoFormModal.vue           (创建/编辑对话框)
```

## 1. PreparationTodoList.vue（主组件）

### 功能
- 展示面试阶段的所有TODO项
- 显示完成进度条
- 新建TODO按钮
- 拖拽排序（可选，v2实现）

### Props
```javascript
{
  stageId: {
    type: Number,
    required: true
  }
}
```

### 核心逻辑
```javascript
import { ref, computed, onMounted, watch } from 'vue'
import preparationTodoApi from '@/api/preparationTodoApi'
import TodoItem from './TodoItem.vue'
import TodoFormModal from './TodoFormModal.vue'

const todos = ref([])
const showTodoForm = ref(false)
const editingTodo = ref(null)

// 完成进度
const completionProgress = computed(() => {
  if (todos.value.length === 0) return 0
  const completedCount = todos.value.filter(t => t.isCompleted).length
  return Math.round((completedCount / todos.value.length) * 100)
})

// 加载TODO列表
const loadTodos = async () => {
  const data = await preparationTodoApi.getTodosByStage(props.stageId)
  todos.value = data || []
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

// 保存TODO（创建或更新）
const saveTodo = async (todoData) => {
  if (editingTodo.value?.id) {
    await preparationTodoApi.updateTodo(editingTodo.value.id, todoData)
  } else {
    await preparationTodoApi.createTodo(props.stageId, todoData)
  }
  await loadTodos()
  showTodoForm.value = false
}

// 切换完成状态
const toggleComplete = async (todo) => {
  await preparationTodoApi.toggleComplete(
    todo.id,
    !todo.isCompleted,
    todo.isCompleted ? null : todo.completionNotes
  )
  await loadTodos()
}

// 删除TODO
const deleteTodo = async (id) => {
  if (confirm('确定删除此TODO？')) {
    await preparationTodoApi.deleteTodo(id)
    await loadTodos()
  }
}

// 监听stageId变化，重新加载
watch(() => props.stageId, loadTodos, { immediate: true })
```

### 模板结构
```vue
<template>
  <div class="preparation-todo-list">
    <!-- 进度条 -->
    <div class="mb-4">
      <div class="flex items-center justify-between mb-2">
        <span class="text-sm font-medium text-gray-700">完成进度</span>
        <span class="text-sm text-gray-600">
          {{ todos.filter(t => t.isCompleted).length }}/{{ todos.length }} 完成
        </span>
      </div>
      <div class="w-full bg-gray-200 rounded-full h-2">
        <div
          class="bg-green-600 h-2 rounded-full transition-all"
          :style="{ width: completionProgress + '%' }"
        ></div>
      </div>
      <p class="text-xs text-gray-500 mt-1">{{ completionProgress }}%</p>
    </div>

    <!-- 新建按钮 -->
    <button @click="createTodo" class="btn-primary mb-4">
      ➕ 新建TODO
    </button>

    <!-- TODO列表 -->
    <div class="space-y-3">
      <TodoItem
        v-for="todo in todos"
        :key="todo.id"
        :todo="todo"
        @toggle-complete="toggleComplete"
        @edit="editTodo"
        @delete="deleteTodo"
      />
    </div>

    <!-- 空状态 -->
    <div v-if="todos.length === 0" class="text-center py-8 text-gray-500">
      <p>暂无TODO</p>
      <p class="text-xs mt-1">点击"新建TODO"开始添加</p>
    </div>

    <!-- 创建/编辑对话框 -->
    <TodoFormModal
      v-if="showTodoForm"
      :todo="editingTodo"
      @save="saveTodo"
      @close="showTodoForm = false"
    />
  </div>
</template>
```

## 2. TodoItem.vue（TODO卡片组件）

### Props
```javascript
{
  todo: {
    type: Object,
    required: true
  }
}
```

### Emits
```javascript
['toggle-complete', 'edit', 'delete']
```

### 模板结构
```vue
<template>
  <div :class="[
    'border rounded-lg p-4 transition-all',
    todo.isCompleted ? 'bg-gray-50 border-gray-300' : 'bg-white border-gray-200'
  ]">
    <div class="flex items-start gap-3">
      <!-- 复选框 -->
      <input
        type="checkbox"
        :checked="todo.isCompleted"
        @change="$emit('toggle-complete', todo)"
        class="mt-1 w-5 h-5 text-blue-600 rounded focus:ring-2 focus:ring-blue-500"
      />

      <!-- 内容区域 -->
      <div class="flex-1">
        <!-- 标题行 -->
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <!-- 优先级标签 -->
            <span
              v-if="todo.priority >= 3"
              :class="[
                'inline-block px-2 py-0.5 rounded text-xs font-medium mr-2',
                todo.priority >= 4 ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-700'
              ]"
            >
              {{ todo.priority >= 4 ? '高' : '中' }}
            </span>

            <!-- 类型图标 -->
            <span class="text-sm mr-2">
              {{ getTypeIcon(todo.todoType) }}
            </span>

            <!-- 标题 -->
            <span :class="['font-medium', todo.isCompleted && 'line-through text-gray-500']">
              {{ todo.title }}
            </span>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-2 ml-2">
            <button @click="$emit('edit', todo)" class="text-blue-600 hover:text-blue-800 text-sm">
              编辑
            </button>
            <button @click="$emit('delete', todo.id)" class="text-red-600 hover:text-red-800 text-sm">
              删除
            </button>
          </div>
        </div>

        <!-- 描述 -->
        <div v-if="todo.description" class="mt-2 text-sm text-gray-600 prose max-w-none">
          <div v-html="renderMarkdown(todo.description)"></div>
        </div>

        <!-- 资源链接 -->
        <div v-if="todo.resourceLinks && todo.resourceLinks.length > 0" class="mt-3 space-y-1">
          <a
            v-for="(link, idx) in todo.resourceLinks"
            :key="idx"
            :href="link.url"
            target="_blank"
            class="block text-sm text-blue-600 hover:text-blue-800 hover:underline"
          >
            🔗 {{ link.title }}
          </a>
        </div>

        <!-- 完成信息 -->
        <div v-if="todo.isCompleted && todo.completedAt" class="mt-3 text-xs text-gray-500">
          <span>✓ 完成于 {{ formatDate(todo.completedAt) }}</span>
          <p v-if="todo.completionNotes" class="mt-1 text-gray-600">{{ todo.completionNotes }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()

const getTypeIcon = (type) => {
  const icons = {
    'General': '📝',
    'StudyMaterial': '📚',
    'Practice': '🏆',
    'ProjectReview': '📁',
    'Checklist': '☑'
  }
  return icons[type] || '📝'
}

const renderMarkdown = (text) => {
  return md.render(text || '')
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>
```

## 3. TodoFormModal.vue（创建/编辑对话框）

### Props
```javascript
{
  todo: {
    type: Object,
    default: null  // null表示创建新TODO
  }
}
```

### Emits
```javascript
['save', 'close']
```

### 核心逻辑
```javascript
const formData = ref({
  title: '',
  description: '',
  todoType: 'General',
  priority: 0,
  resourceLinks: []
})

// 如果是编辑模式，加载现有数据
watch(() => props.todo, (todo) => {
  if (todo) {
    formData.value = {
      title: todo.title,
      description: todo.description || '',
      todoType: todo.todoType,
      priority: todo.priority || 0,
      resourceLinks: todo.resourceLinks || []
    }
  } else {
    // 重置表单
    formData.value = {
      title: '',
      description: '',
      todoType: 'General',
      priority: 0,
      resourceLinks: []
    }
  }
}, { immediate: true })

// 添加资源链接
const addResourceLink = () => {
  formData.value.resourceLinks.push({
    title: '',
    url: '',
    type: 'study'
  })
}

// 删除资源链接
const removeResourceLink = (index) => {
  formData.value.resourceLinks.splice(index, 1)
}

// 保存
const save = () => {
  if (!formData.value.title.trim()) {
    alert('请输入TODO标题')
    return
  }
  emit('save', formData.value)
}
```

### 模板结构
```vue
<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content max-w-2xl">
      <div class="modal-header">
        <h3 class="text-lg font-bold">{{ todo ? '编辑' : '创建' }}TODO</h3>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          ✕
        </button>
      </div>

      <div class="modal-body space-y-4">
        <!-- TODO类型 -->
        <div>
          <label class="block text-sm font-medium mb-1">类型</label>
          <select v-model="formData.todoType" class="form-select">
            <option value="General">一般任务</option>
            <option value="StudyMaterial">学习材料</option>
            <option value="Practice">练习刷题</option>
            <option value="ProjectReview">项目回顾</option>
            <option value="Checklist">关联准备清单</option>
          </select>
        </div>

        <!-- 优先级 -->
        <div>
          <label class="block text-sm font-medium mb-1">
            优先级: {{ formData.priority }} (0=低, 5=高)
          </label>
          <input
            type="range"
            v-model="formData.priority"
            min="0"
            max="5"
            class="w-full"
          />
        </div>

        <!-- 标题 -->
        <div>
          <label class="block text-sm font-medium mb-1">标题*</label>
          <input
            v-model="formData.title"
            type="text"
            class="form-input"
            placeholder="例如：刷LeetCode Medium难度题"
          />
        </div>

        <!-- 描述 -->
        <div>
          <label class="block text-sm font-medium mb-1">描述（支持Markdown）</label>
          <textarea
            v-model="formData.description"
            rows="6"
            class="form-textarea font-mono"
            placeholder="详细描述TODO内容..."
          ></textarea>
        </div>

        <!-- 资源链接 -->
        <div>
          <div class="flex items-center justify-between mb-2">
            <label class="block text-sm font-medium">资源链接</label>
            <button @click="addResourceLink" class="text-blue-600 text-sm">
              + 添加链接
            </button>
          </div>

          <div
            v-for="(link, idx) in formData.resourceLinks"
            :key="idx"
            class="flex gap-2 mb-2"
          >
            <input
              v-model="link.title"
              type="text"
              placeholder="链接标题"
              class="form-input flex-1"
            />
            <input
              v-model="link.url"
              type="url"
              placeholder="https://..."
              class="form-input flex-1"
            />
            <button @click="removeResourceLink(idx)" class="text-red-600">
              删除
            </button>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button @click="$emit('close')" class="btn-secondary">
          取消
        </button>
        <button @click="save" class="btn-primary">
          保存
        </button>
      </div>
    </div>
  </div>
</template>
```

## 4. 集成到JobApplicationList.vue

### 步骤1：导入组件
```javascript
import PreparationTodoList from '@/components/job-search/PreparationTodoList.vue'
```

### 步骤2：修改"准备笔记" Tab
在`activeInterviewSubTab === 'notes'`部分，在准备笔记下方添加TODO列表：

```vue
<!-- Tab 1: 准备笔记 -->
<div v-if="activeInterviewSubTab === 'notes'">
  <!-- 原有的准备笔记区域 -->
  <div class="bg-white rounded-lg shadow p-5 mb-4">
    <!-- ... 原有代码 ... -->
  </div>

  <!-- 新增：TODO列表 -->
  <div class="bg-white rounded-lg shadow p-5">
    <h4 class="text-sm font-semibold text-gray-900 mb-4">✅ 准备TODO清单</h4>
    <PreparationTodoList
      v-if="selectedStageId"
      :stage-id="selectedStageId"
    />
  </div>
</div>
```

## 验收标准

- [ ] 可为每个面试阶段查看TODO列表
- [ ] 可创建新TODO，填写标题、描述、类型、优先级
- [ ] 可添加资源链接到TODO
- [ ] 可编辑已有TODO
- [ ] 可标记TODO为完成/未完成
- [ ] 完成的TODO显示完成时间
- [ ] 可删除TODO
- [ ] 显示整体完成进度（百分比和进度条）
- [ ] 描述支持Markdown渲染
- [ ] 不同优先级显示不同颜色标签

## 后续优化（v2）

- [ ] 拖拽排序功能（使用vue-draggable）
- [ ] 批量操作（批量标记完成、批量删除）
- [ ] 筛选和排序（按类型、优先级、完成状态）
- [ ] 从准备清单一键生成TODO
- [ ] 关联到试题库（可选）
