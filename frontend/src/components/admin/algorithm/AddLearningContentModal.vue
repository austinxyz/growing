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
          <div class="relative bg-white rounded-lg shadow-xl max-w-4xl w-full p-6 z-10 max-h-[90vh] overflow-y-auto">
            <!-- 标题 -->
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-lg font-semibold text-gray-900">
                {{ isEditing ? '编辑学习内容' : '创建学习内容' }}
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
              <div class="grid grid-cols-2 gap-4">
                <!-- 左列 -->
                <div class="space-y-4">
                  <!-- 技能选择 -->
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      所属技能 <span class="text-red-500">*</span>
                    </label>
                    <select
                      v-model="formData.skillId"
                      @change="onSkillChange"
                      required
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                      <option value="" disabled>请选择技能</option>
                      <option v-for="skill in skills" :key="skill.id" :value="skill.id">
                        {{ skill.name }}
                      </option>
                    </select>
                  </div>

                  <!-- 学习阶段选择 -->
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      学习阶段 <span class="text-red-500">*</span>
                    </label>
                    <select
                      v-model="formData.stageId"
                      required
                      :disabled="!formData.skillId"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
                    >
                      <option value="" disabled>请选择学习阶段</option>
                      <option v-for="stage in availableStages" :key="stage.id" :value="stage.id">
                        {{ stage.stageName }}
                      </option>
                    </select>
                  </div>

                  <!-- Focus Area选择（可选） -->
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      Focus Area（可选）
                    </label>
                    <select
                      v-model="formData.focusAreaId"
                      :disabled="!formData.skillId"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
                    >
                      <option value="">不关联Focus Area</option>
                      <option v-for="fa in availableFocusAreas" :key="fa.id" :value="fa.id">
                        {{ fa.name }}
                      </option>
                    </select>
                  </div>

                  <!-- 内容类型 -->
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      内容类型 <span class="text-red-500">*</span>
                    </label>
                    <select
                      v-model="formData.contentType"
                      required
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                      <option value="" disabled>请选择类型</option>
                      <option value="text">文本</option>
                      <option value="code">代码</option>
                      <option value="link">链接</option>
                      <option value="algorithm_template">算法模版</option>
                    </select>
                  </div>

                  <!-- 排序 -->
                  <div>
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
                </div>

                <!-- 右列 -->
                <div class="space-y-4">
                  <!-- 标题 -->
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      标题 <span class="text-red-500">*</span>
                    </label>
                    <input
                      v-model="formData.title"
                      type="text"
                      required
                      placeholder="例如：二分查找基本原理"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <!-- 文本内容（text/code类型） -->
                  <div v-if="formData.contentType === 'text' || formData.contentType === 'code' || formData.contentType === 'algorithm_template'">
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      {{ formData.contentType === 'code' || formData.contentType === 'algorithm_template' ? '代码内容' : '文本内容' }}
                      <span class="text-red-500">*</span>
                    </label>
                    <textarea
                      v-model="formData.contentText"
                      :required="formData.contentType === 'text' || formData.contentType === 'code' || formData.contentType === 'algorithm_template'"
                      rows="12"
                      :placeholder="formData.contentType === 'code' || formData.contentType === 'algorithm_template'
                        ? '支持Markdown格式，使用```代码块包裹代码'
                        : '支持Markdown格式'"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
                    ></textarea>
                    <p class="mt-1 text-xs text-gray-500">
                      支持Markdown语法。代码块使用 ```language 开头，``` 结尾
                    </p>
                  </div>

                  <!-- 链接URL（link类型） -->
                  <div v-if="formData.contentType === 'link'">
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      链接URL <span class="text-red-500">*</span>
                    </label>
                    <input
                      v-model="formData.linkUrl"
                      type="url"
                      :required="formData.contentType === 'link'"
                      placeholder="https://example.com"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>
              </div>

              <!-- 按钮 -->
              <div class="flex justify-end space-x-3 mt-6 pt-4 border-t border-gray-200">
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
  content: Object,
  skills: Array,
  stages: Array,
  focusAreas: Array
})

const emit = defineEmits(['close', 'save'])

const isEditing = computed(() => !!props.content?.id)

const formData = ref({
  skillId: '',
  stageId: '',
  focusAreaId: '',
  contentType: '',
  title: '',
  contentText: '',
  linkUrl: '',
  displayOrder: 1
})

const availableStages = ref([])
const availableFocusAreas = ref([])

// 当技能改变时，过滤可用的阶段和Focus Areas
const onSkillChange = () => {
  formData.value.stageId = ''
  formData.value.focusAreaId = ''

  if (!formData.value.skillId) {
    availableStages.value = []
    availableFocusAreas.value = []
    return
  }

  const skillId = Number(formData.value.skillId)

  // 过滤学习阶段
  availableStages.value = props.stages.filter(s => s.skillId === skillId)

  // 过滤Focus Areas
  const skill = props.skills.find(s => s.id === skillId)
  availableFocusAreas.value = skill?.focusAreas || []
}

// 重置表单函数（必须在 watch 之前定义）
const resetForm = () => {
  formData.value = {
    skillId: '',
    stageId: '',
    focusAreaId: '',
    contentType: '',
    title: '',
    contentText: '',
    linkUrl: '',
    displayOrder: 1
  }
  availableStages.value = []
  availableFocusAreas.value = []
}

// 监听content变化，重置表单
watch(() => props.content, (newContent) => {
  if (newContent) {
    formData.value = {
      ...newContent,
      skillId: newContent.skillId || '',
      focusAreaId: newContent.focusAreaId || ''
    }

    // 更新可用的阶段和Focus Areas
    if (formData.value.skillId) {
      onSkillChange()
    }
  } else {
    resetForm()
  }
}, { immediate: true })

const handleClose = () => {
  emit('close')
  resetForm()
}

const handleSubmit = () => {
  // 清理不需要的字段
  const data = { ...formData.value }

  // 根据contentType清理字段
  if (data.contentType === 'link') {
    data.contentText = null
  } else {
    data.linkUrl = null
  }

  // focusAreaId为空字符串时转为null
  if (data.focusAreaId === '') {
    data.focusAreaId = null
  }

  emit('save', data)
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
