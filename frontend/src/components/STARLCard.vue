<template>
  <div :class="[
    'rounded-xl border-2 shadow-md hover:shadow-xl transition-all overflow-hidden',
    bgGradient,
    borderColor
  ]">
    <div :class="[
      'px-2 py-1.5 text-white flex items-center justify-between',
      headerGradient
    ]">
      <div
        @click="toggleCollapse"
        class="flex items-center gap-1.5 cursor-pointer flex-1"
      >
        <div :class="['w-1.5 h-5 rounded-full', decorColor]"></div>
        <h3 class="font-semibold text-xs">{{ title }}</h3>
      </div>
      <div class="flex items-center gap-1">
        <!-- 编辑按钮 -->
        <button
          v-if="!isEditing"
          @click.stop="toggleEdit"
          :class="['p-1 rounded transition-all', hoverColor]"
          title="编辑"
        >
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
        </button>
        <!-- 折叠按钮 -->
        <button
          @click.stop="toggleCollapse"
          :class="['p-1 rounded transition-all', hoverColor]"
        >
          <svg
            :class="['w-3.5 h-3.5 transition-transform', collapsed ? '' : 'rotate-180']"
            fill="none" stroke="currentColor" viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
          </svg>
        </button>
      </div>
    </div>
    <div v-show="!collapsed" class="p-3">
      <!-- 浏览模式 -->
      <div v-if="!isEditing">
        <div v-if="content" class="prose prose-sm max-w-none text-xs">
          <div v-html="renderMarkdown(content)" class="text-gray-700"></div>
        </div>
        <div v-else class="text-xs text-gray-400 italic">暂无内容</div>
      </div>

      <!-- 编辑模式 -->
      <div v-else>
        <textarea
          v-model="editContent"
          rows="8"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg text-xs font-mono mb-2"
          placeholder="支持 Markdown 格式..."
        ></textarea>
        <div class="flex justify-end gap-2">
          <button
            @click="toggleEdit"
            class="px-3 py-1.5 text-xs text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="save"
            :class="['px-3 py-1.5 text-xs text-white rounded', saveButtonColor]"
          >
            保存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()

const props = defineProps({
  title: String,
  content: String,
  isEditing: Boolean,
  color: {
    type: String,
    default: 'blue'
  }
})

const emit = defineEmits(['toggle-edit', 'save'])

const editContent = ref(props.content || '')
const collapsed = ref(false)

// 监听 content 变化，同步到 editContent
watch(() => props.content, (newValue) => {
  if (!props.isEditing) {
    editContent.value = newValue || ''
  }
})

// 颜色配置
const colorConfig = computed(() => {
  const colors = {
    blue: {
      bgGradient: 'bg-gradient-to-br from-blue-50 to-white',
      borderColor: 'border-blue-200',
      headerGradient: 'bg-gradient-to-r from-blue-500 to-blue-600',
      decorColor: 'bg-gradient-to-b from-blue-300 to-white',
      hoverColor: 'hover:bg-blue-600',
      saveButtonColor: 'bg-blue-600 hover:bg-blue-700'
    },
    green: {
      bgGradient: 'bg-gradient-to-br from-green-50 to-white',
      borderColor: 'border-green-200',
      headerGradient: 'bg-gradient-to-r from-green-500 to-emerald-600',
      decorColor: 'bg-gradient-to-b from-green-300 to-white',
      hoverColor: 'hover:bg-green-600',
      saveButtonColor: 'bg-green-600 hover:bg-green-700'
    },
    orange: {
      bgGradient: 'bg-gradient-to-br from-orange-50 to-white',
      borderColor: 'border-orange-200',
      headerGradient: 'bg-gradient-to-r from-orange-500 to-orange-600',
      decorColor: 'bg-gradient-to-b from-orange-300 to-white',
      hoverColor: 'hover:bg-orange-600',
      saveButtonColor: 'bg-orange-600 hover:bg-orange-700'
    },
    pink: {
      bgGradient: 'bg-gradient-to-br from-pink-50 to-white',
      borderColor: 'border-pink-200',
      headerGradient: 'bg-gradient-to-r from-pink-500 to-pink-600',
      decorColor: 'bg-gradient-to-b from-pink-300 to-white',
      hoverColor: 'hover:bg-pink-600',
      saveButtonColor: 'bg-pink-600 hover:bg-pink-700'
    }
  }
  return colors[props.color] || colors.blue
})

const bgGradient = computed(() => colorConfig.value.bgGradient)
const borderColor = computed(() => colorConfig.value.borderColor)
const headerGradient = computed(() => colorConfig.value.headerGradient)
const decorColor = computed(() => colorConfig.value.decorColor)
const hoverColor = computed(() => colorConfig.value.hoverColor)
const saveButtonColor = computed(() => colorConfig.value.saveButtonColor)

const toggleEdit = () => {
  if (!props.isEditing) {
    editContent.value = props.content || ''
  }
  emit('toggle-edit')
}

const toggleCollapse = () => {
  collapsed.value = !collapsed.value
}

const save = () => {
  emit('save', editContent.value)
}

const renderMarkdown = (text) => {
  if (!text) return ''
  return md.render(text)
}
</script>

<style scoped>
/* Markdown渲染样式 */
.prose {
  @apply text-gray-900;
}

.prose :deep(h1) {
  @apply text-2xl font-bold mt-4 mb-2;
}

.prose :deep(h2) {
  @apply text-xl font-bold mt-3 mb-2;
}

.prose :deep(h3) {
  @apply text-lg font-semibold mt-2 mb-1;
}

.prose :deep(p) {
  @apply mb-2;
}

.prose :deep(ul) {
  @apply list-disc ml-6 mb-2;
}

.prose :deep(ol) {
  @apply list-decimal ml-6 mb-2;
}

.prose :deep(li) {
  @apply mb-1;
}

.prose :deep(strong) {
  @apply font-semibold;
}

.prose :deep(em) {
  @apply italic;
}

.prose :deep(a) {
  @apply text-blue-600 hover:underline;
}

.prose :deep(code) {
  @apply bg-gray-100 px-1 py-0.5 rounded text-sm font-mono;
}

.prose :deep(pre) {
  @apply bg-gray-100 p-3 rounded overflow-x-auto mb-2;
}

.prose :deep(blockquote) {
  @apply border-l-4 border-gray-300 pl-4 italic my-2;
}
</style>
