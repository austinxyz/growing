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
        添加专注领域
      </button>
    </div>

    <!-- 专注领域列表 -->
    <div v-if="focusAreas.length > 0" class="space-y-3">
      <div
        v-for="(area, index) in focusAreas"
        :key="area.id"
        class="bg-gray-50 border border-gray-200 rounded-lg p-4 flex items-start justify-between"
      >
        <div class="flex-1">
          <div class="flex items-center gap-2">
            <span class="text-gray-500 font-mono">{{ index + 1 }}.</span>
            <h4 class="font-medium text-gray-900">{{ area.name }}</h4>
          </div>
          <p class="text-sm text-gray-600 mt-1 ml-6">{{ area.description || '暂无描述' }}</p>
        </div>

        <div class="flex items-center gap-2">
          <button
            @click="handleEdit(area)"
            class="text-blue-600 hover:text-blue-800 text-sm"
          >
            编辑
          </button>
          <button
            @click="handleDelete(area.id)"
            class="text-red-600 hover:text-red-800 text-sm"
          >
            删除
          </button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12 text-gray-500 bg-gray-50 rounded-lg">
      <p>暂无专注领域</p>
      <p class="text-sm mt-1">点击上方按钮添加</p>
    </div>

    <!-- 添加/编辑弹窗 -->
    <FocusAreaEditModal
      v-if="showAddModal || editingArea"
      :skillId="skillId"
      :focusArea="editingArea"
      @close="closeModal"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { deleteFocusArea } from '@/api/focusAreas'
import FocusAreaEditModal from './FocusAreaEditModal.vue'

const props = defineProps({
  skillId: {
    type: Number,
    required: true
  },
  focusAreas: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['updated'])

const showAddModal = ref(false)
const editingArea = ref(null)

const handleEdit = (area) => {
  editingArea.value = area
}

const handleDelete = async (areaId) => {
  if (!confirm('确定要删除这个专注领域吗?')) {
    return
  }

  try {
    await deleteFocusArea(areaId)
    emit('updated')
  } catch (error) {
    console.error('Failed to delete focus area:', error)
    alert('删除失败')
  }
}

const closeModal = () => {
  showAddModal.value = false
  editingArea.value = null
}

const handleSuccess = () => {
  closeModal()
  emit('updated')
}
</script>
