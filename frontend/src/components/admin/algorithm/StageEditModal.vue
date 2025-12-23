<template>
  <teleport to="body">
    <transition name="modal">
      <div
        v-if="isOpen"
        class="fixed inset-0 z-50 overflow-y-auto"
        @click.self="handleClose"
      >
        <div class="flex min-h-screen items-center justify-center p-4">
          <!-- 背景遮罩 -->
          <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity"></div>

          <!-- 弹窗内容 -->
          <div class="relative bg-white rounded-lg shadow-xl max-w-2xl w-full p-6 z-10">
            <!-- 标题 -->
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-lg font-semibold text-gray-900">
                {{ isEditing ? '编辑学习阶段' : '创建学习阶段' }}
              </h3>
              <button
                @click="handleClose"
                class="text-gray-400 hover:text-gray-500"
              >
                <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <!-- 表单 -->
            <form @submit.prevent="handleSubmit">
              <!-- 技能选择 -->
              <div class="mb-4">
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  所属技能 <span class="text-red-500">*</span>
                </label>
                <select
                  v-model="formData.skillId"
                  required
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="" disabled>请选择技能</option>
                  <option v-for="skill in skills" :key="skill.id" :value="skill.id">
                    {{ skill.name }}
                  </option>
                </select>
              </div>

              <!-- 阶段名称 -->
              <div class="mb-4">
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  阶段名称 <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="formData.stageName"
                  type="text"
                  required
                  placeholder="例如：基础原理、实现代码、实战题目"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <!-- 阶段描述 -->
              <div class="mb-4">
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  阶段描述
                </label>
                <textarea
                  v-model="formData.stageDescription"
                  rows="3"
                  placeholder="描述这个学习阶段的目标和内容..."
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                ></textarea>
              </div>

              <!-- 排序 -->
              <div class="mb-6">
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  显示顺序 <span class="text-red-500">*</span>
                </label>
                <input
                  v-model.number="formData.displayOrder"
                  type="number"
                  required
                  min="1"
                  placeholder="数字越小越靠前"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <!-- 按钮 -->
              <div class="flex justify-end space-x-3">
                <button
                  type="button"
                  @click="handleClose"
                  class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                >
                  取消
                </button>
                <button
                  type="submit"
                  class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                >
                  {{ isEditing ? '保存' : '创建' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

const props = defineProps({
  isOpen: Boolean,
  stage: Object,
  skills: Array
})

const emit = defineEmits(['close', 'save'])

const isEditing = computed(() => !!props.stage?.id)

const formData = ref({
  skillId: '',
  stageName: '',
  stageDescription: '',
  displayOrder: 1
})

// 重置表单函数（必须在 watch 之前定义）
const resetForm = () => {
  formData.value = {
    skillId: '',
    stageName: '',
    stageDescription: '',
    displayOrder: 1
  }
}

// 监听stage变化，重置表单
watch(() => props.stage, (newStage) => {
  if (newStage) {
    formData.value = { ...newStage }
  } else {
    resetForm()
  }
}, { immediate: true })

const handleClose = () => {
  emit('close')
  resetForm()
}

const handleSubmit = () => {
  emit('save', { ...formData.value })
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
