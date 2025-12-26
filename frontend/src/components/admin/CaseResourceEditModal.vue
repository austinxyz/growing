<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- Backdrop -->
    <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="close"></div>

    <!-- Modal -->
    <div class="flex min-h-screen items-center justify-center p-4">
      <div class="relative bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-hidden">
        <!-- Header -->
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h2 class="text-xl font-semibold text-gray-800">
            {{ isEditMode ? '编辑学习资源' : '添加学习资源' }}
          </h2>
          <button @click="close" class="text-gray-400 hover:text-gray-600">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Body -->
        <div class="px-6 py-4 overflow-y-auto max-h-[calc(90vh-140px)]">
          <div class="space-y-4">
            <!-- Resource Type -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                资源类型 <span class="text-red-500">*</span>
              </label>
              <select
                v-model="form.resourceType"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="VIDEO">视频</option>
                <option value="ARTICLE">文章</option>
              </select>
            </div>

            <!-- Title -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                资源标题 <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.title"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="例如: 系统设计面试指南"
              />
            </div>

            <!-- URL -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                资源链接 <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.url"
                type="url"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="https://..."
              />
            </div>

            <!-- Description -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                资源描述
              </label>
              <textarea
                v-model="form.description"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="简要描述这个资源的内容..."
              ></textarea>
            </div>

            <!-- Display Order -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                显示顺序
              </label>
              <input
                v-model.number="form.displayOrder"
                type="number"
                min="0"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="0"
              />
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="px-6 py-4 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="close"
            class="px-4 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
          >
            取消
          </button>
          <button
            @click="handleSave"
            :disabled="!isFormValid"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
          >
            {{ isEditMode ? '保存' : '添加' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  isOpen: Boolean,
  resourceData: Object // 编辑时传入
})

const emit = defineEmits(['close', 'save'])

const isEditMode = computed(() => !!props.resourceData)

const form = ref({
  resourceType: 'VIDEO',
  title: '',
  url: '',
  description: '',
  displayOrder: 0
})

// Watch for resourceData changes (when editing)
watch(() => props.resourceData, (newData) => {
  if (newData) {
    form.value = {
      resourceType: newData.resourceType || 'VIDEO',
      title: newData.title || '',
      url: newData.url || '',
      description: newData.description || '',
      displayOrder: newData.displayOrder || 0
    }
  }
}, { immediate: true })

const isFormValid = computed(() => {
  return form.value.title && form.value.title.trim().length > 0 &&
         form.value.url && form.value.url.trim().length > 0
})

const close = () => {
  emit('close')
  // Reset form
  form.value = {
    resourceType: 'VIDEO',
    title: '',
    url: '',
    description: '',
    displayOrder: 0
  }
}

const handleSave = () => {
  if (!isFormValid.value) return

  emit('save', {
    ...form.value,
    id: props.resourceData?.id
  })
  close()
}
</script>
