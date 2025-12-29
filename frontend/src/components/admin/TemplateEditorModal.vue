<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-hidden flex flex-col">
      <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
        <h2 class="text-xl font-bold text-gray-900">
          {{ template ? '编辑答题模版' : '新增答题模版' }}
        </h2>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <div class="flex-1 overflow-y-auto p-6">
        <form class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              模版名称 <span class="text-red-500">*</span>
            </label>
            <input v-model="form.templateName" type="text" required
              class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">模版描述</label>
            <textarea v-model="form.description" rows="3"
              class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"></textarea>
          </div>

          <div>
            <div class="flex justify-between mb-2">
              <label class="text-sm font-medium text-gray-700">模版字段 <span class="text-red-500">*</span></label>
              <button type="button" @click="addField"
                class="px-3 py-1 bg-green-600 text-white text-sm rounded hover:bg-green-700">+ 添加字段</button>
            </div>

            <div v-if="form.templateFields.length === 0" class="text-sm text-gray-500 text-center py-4 border border-dashed rounded">
              暂无字段
            </div>

            <div v-else class="space-y-3">
              <div v-for="(field, index) in form.templateFields" :key="index" class="border rounded-lg p-4 bg-gray-50">
                <div class="flex justify-between mb-3">
                  <span class="text-sm font-medium">字段 {{ index + 1 }}</span>
                  <button type="button" @click="removeField(index)" class="text-red-600">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
                <div class="grid grid-cols-3 gap-3">
                  <div>
                    <label class="block text-xs font-medium mb-1">字段Key</label>
                    <input v-model="field.key" required class="w-full px-2 py-1.5 text-sm border rounded" />
                  </div>
                  <div>
                    <label class="block text-xs font-medium mb-1">显示名称</label>
                    <input v-model="field.label" required class="w-full px-2 py-1.5 text-sm border rounded" />
                  </div>
                  <div>
                    <label class="block text-xs font-medium mb-1">提示文本</label>
                    <input v-model="field.placeholder" class="w-full px-2 py-1.5 text-sm border rounded" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>

      <div class="px-6 py-4 border-t flex justify-end space-x-3">
        <button @click="$emit('close')" class="px-4 py-2 border rounded-lg hover:bg-gray-50">取消</button>
        <button @click="handleSubmit" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
          {{ template ? '更新' : '创建' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({ template: { type: Object, default: null } })
const emit = defineEmits(['close', 'save'])

const form = ref({ templateName: '', description: '', templateFields: [] })

const initForm = () => {
  if (props.template) {
    // templateFields可能是字符串（需要parse）或已经是数组（后端/axios自动转换）
    let fields = []
    if (props.template.templateFields) {
      if (typeof props.template.templateFields === 'string') {
        fields = JSON.parse(props.template.templateFields)
      } else {
        fields = props.template.templateFields
      }
    }

    form.value = {
      templateName: props.template.templateName || '',
      description: props.template.description || '',
      templateFields: fields
    }
  } else {
    form.value = { templateName: '', description: '', templateFields: [] }
  }
}

watch(() => props.template, initForm, { immediate: true })

const addField = () => {
  form.value.templateFields.push({ key: '', label: '', placeholder: '' })
}

const removeField = (index) => {
  form.value.templateFields.splice(index, 1)
}

const handleSubmit = () => {
  if (!form.value.templateName.trim()) {
    alert('请输入模版名称')
    return
  }
  if (form.value.templateFields.length === 0) {
    alert('请至少添加一个字段')
    return
  }
  for (let i = 0; i < form.value.templateFields.length; i++) {
    const field = form.value.templateFields[i]
    if (!field.key.trim() || !field.label.trim()) {
      alert('字段的Key和显示名称不能为空')
      return
    }
  }
  const data = {
    templateName: form.value.templateName.trim(),
    description: form.value.description.trim(),
    templateFields: JSON.stringify(form.value.templateFields)
  }
  emit('save', data)
}
</script>
