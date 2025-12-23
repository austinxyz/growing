<template>
  <teleport to="body">
    <transition name="modal">
      <div
        v-if="isOpen"
        class="fixed inset-0 z-50 overflow-y-auto"
        @click.self="handleCancel"
      >
        <div class="flex min-h-screen items-center justify-center p-4">
          <!-- 背景遮罩 -->
          <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity"></div>

          <!-- 弹窗内容 -->
          <div class="relative bg-white rounded-lg shadow-xl max-w-md w-full p-6 z-10">
            <!-- 标题 -->
            <div class="mb-4">
              <h3 class="text-lg font-semibold text-gray-900">
                {{ title }}
              </h3>
            </div>

            <!-- 消息 -->
            <div class="mb-6">
              <p class="text-sm text-gray-600">{{ message }}</p>
            </div>

            <!-- 按钮 -->
            <div class="flex justify-end space-x-3">
              <button
                type="button"
                @click="handleCancel"
                class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
              >
                {{ cancelText }}
              </button>
              <button
                type="button"
                @click="handleConfirm"
                :class="[
                  'px-4 py-2 text-sm font-medium text-white rounded-md',
                  confirmClass || 'bg-blue-600 hover:bg-blue-700'
                ]"
              >
                {{ confirmText }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
const props = defineProps({
  isOpen: Boolean,
  title: {
    type: String,
    default: '确认操作'
  },
  message: {
    type: String,
    required: true
  },
  confirmText: {
    type: String,
    default: '确认'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  confirmClass: String
})

const emit = defineEmits(['confirm', 'cancel'])

const handleConfirm = () => {
  emit('confirm')
}

const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
