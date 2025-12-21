<template>
  <div>
    <!-- 添加按钮 -->
    <div class="mb-6">
      <button
        @click="showAddModal = true"
        class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
        </svg>
        添加官方资源
      </button>
    </div>

    <!-- 资源列表 -->
    <div v-if="resources.length > 0" class="space-y-4">
      <div
        v-for="resource in resources"
        :key="resource.id"
        class="bg-white border border-gray-200 rounded-lg p-4"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <!-- 标题和类型 -->
            <div class="flex items-center gap-2 mb-2">
              <span class="text-lg">{{ getTypeIcon(resource.type) }}</span>
              <h4 class="font-medium text-gray-900">{{ resource.title }}</h4>
              <span class="text-xs px-2 py-1 bg-gray-100 text-gray-600 rounded">
                {{ getTypeLabel(resource.type) }}
              </span>
            </div>

            <!-- 元信息 -->
            <div class="flex items-center gap-3 text-sm text-gray-600 mb-2">
              <span v-if="resource.author">作者: {{ resource.author }}</span>
              <span v-if="resource.isOfficial" class="text-blue-600 font-medium">
                ✓ 官方资源
              </span>
              <span v-else-if="resource.createdByUser" class="text-green-600">
                用户添加 ({{ resource.createdByUser.username }})
              </span>
            </div>

            <!-- 描述 -->
            <p v-if="resource.description" class="text-sm text-gray-600 mb-2">
              {{ resource.description }}
            </p>

            <!-- 链接 -->
            <a
              v-if="resource.url"
              :href="resource.url"
              target="_blank"
              class="text-blue-600 hover:text-blue-800 text-sm"
            >
              {{ resource.url }}
            </a>
          </div>

          <!-- 操作按钮 -->
          <div class="flex items-center gap-2 ml-4">
            <button
              @click="handleEdit(resource)"
              class="text-blue-600 hover:text-blue-800 text-sm"
            >
              编辑
            </button>
            <button
              @click="handleDelete(resource.id)"
              class="text-red-600 hover:text-red-800 text-sm"
            >
              删除
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12 text-gray-500 bg-gray-50 rounded-lg">
      <p>暂无学习资源</p>
      <p class="text-sm mt-1">点击上方按钮添加官方资源</p>
    </div>

    <!-- 添加/编辑弹窗 -->
    <ResourceEditModal
      v-if="showAddModal || editingResource"
      :skillId="skillId"
      :resource="editingResource"
      @close="closeModal"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { deleteResourceAdmin } from '@/api/learningResources'
import ResourceEditModal from './ResourceEditModal.vue'

const props = defineProps({
  skillId: {
    type: Number,
    required: true
  },
  resources: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['updated'])

const showAddModal = ref(false)
const editingResource = ref(null)

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

const handleEdit = (resource) => {
  editingResource.value = resource
}

const handleDelete = async (resourceId) => {
  if (!confirm('确定要删除这个学习资源吗?')) {
    return
  }

  try {
    await deleteResourceAdmin(resourceId)
    emit('updated')
  } catch (error) {
    console.error('Failed to delete resource:', error)
    alert('删除失败')
  }
}

const closeModal = () => {
  showAddModal.value = false
  editingResource.value = null
}

const handleSuccess = () => {
  closeModal()
  emit('updated')
}
</script>
