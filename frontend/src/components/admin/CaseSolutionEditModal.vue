<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- Backdrop -->
    <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="close"></div>

    <!-- Modal -->
    <div class="flex min-h-screen items-center justify-center p-4">
      <div class="relative bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-hidden">
        <!-- Header -->
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h2 class="text-xl font-semibold text-gray-800">
            {{ isEditMode ? '编辑参考答案' : '添加参考答案' }}
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
            <!-- Basic Info -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  方案名称 <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.solutionName"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="例如: 方案A - 基础版本"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  作者
                </label>
                <input
                  v-model="form.author"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="作者名称"
                />
              </div>
            </div>

            <!-- Step 1: Requirements -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                步骤1: 需求澄清与功能列表 (Markdown)
              </label>
              <textarea
                v-model="form.step1Requirements"
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-sm"
                placeholder="## 功能需求&#10;- 核心功能1&#10;- 核心功能2&#10;&#10;## 非功能需求&#10;- 性能要求&#10;- 可用性要求"
              ></textarea>
            </div>

            <!-- Step 2: Entities -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                步骤2: 核心实体定义 (Markdown)
              </label>
              <textarea
                v-model="form.step2Entities"
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-sm"
                placeholder="## User&#10;- id: Long&#10;- username: String&#10;- email: String"
              ></textarea>
            </div>

            <!-- Step 3: API -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                步骤3: API设计 (Markdown)
              </label>
              <textarea
                v-model="form.step3Api"
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-sm"
                placeholder="### 创建用户&#10;`POST /api/users`&#10;```json&#10;{ &quot;username&quot;: &quot;...&quot; }&#10;```"
              ></textarea>
            </div>

            <!-- Step 4: Data Flow -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                步骤4: 数据流设计 (Markdown)
              </label>
              <textarea
                v-model="form.step4DataFlow"
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-sm"
                placeholder="1. 用户请求 → API Gateway&#10;2. API Gateway → 服务层&#10;3. 服务层 → 数据库"
              ></textarea>
            </div>

            <!-- Step 5: Architecture -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                步骤5: 高层架构设计 (Markdown)
              </label>
              <textarea
                v-model="form.step5Architecture"
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-sm"
                placeholder="## 系统组件&#10;- Load Balancer&#10;- Application Servers&#10;- Database Cluster"
              ></textarea>
            </div>

            <!-- Step 6: Deep Dive -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                步骤6: 深入讨论 (Markdown)
              </label>
              <textarea
                v-model="form.step6DeepDive"
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-sm"
                placeholder="## 扩展性考虑&#10;- 水平扩展策略&#10;- 缓存策略&#10;&#10;## 可靠性&#10;- 容错机制&#10;- 备份策略"
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
  solutionData: Object // 编辑时传入
})

const emit = defineEmits(['close', 'save'])

const isEditMode = computed(() => !!props.solutionData)

const form = ref({
  solutionName: '',
  author: '',
  step1Requirements: '',
  step2Entities: '',
  step3Api: '',
  step4DataFlow: '',
  step5Architecture: '',
  step6DeepDive: '',
  displayOrder: 0
})

// Watch for solutionData changes (when editing)
watch(() => props.solutionData, (newData) => {
  if (newData) {
    form.value = {
      solutionName: newData.solutionName || '',
      author: newData.author || '',
      step1Requirements: newData.step1Requirements || '',
      step2Entities: newData.step2Entities || '',
      step3Api: newData.step3Api || '',
      step4DataFlow: newData.step4DataFlow || '',
      step5Architecture: newData.step5Architecture || '',
      step6DeepDive: newData.step6DeepDive || '',
      displayOrder: newData.displayOrder || 0
    }
  }
}, { immediate: true })

const isFormValid = computed(() => {
  return form.value.solutionName && form.value.solutionName.trim().length > 0
})

const close = () => {
  emit('close')
  // Reset form
  form.value = {
    solutionName: '',
    author: '',
    step1Requirements: '',
    step2Entities: '',
    step3Api: '',
    step4DataFlow: '',
    step5Architecture: '',
    step6DeepDive: '',
    displayOrder: 0
  }
}

const handleSave = () => {
  if (!isFormValid.value) return

  emit('save', {
    ...form.value,
    id: props.solutionData?.id
  })
  close()
}
</script>
