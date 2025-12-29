<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[80vh] overflow-hidden flex flex-col">
      <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
        <h2 class="text-xl font-bold text-gray-900">关联已有模版</h2>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <div class="flex-1 overflow-y-auto p-6">
        <div v-if="loading" class="text-center py-8">
          <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="mt-2 text-sm text-gray-500">加载中...</p>
        </div>

        <div v-else-if="availableTemplates.length === 0" class="text-center py-8">
          <p class="text-gray-500">没有可关联的模版</p>
        </div>

        <div v-else class="space-y-2">
          <div v-for="template in availableTemplates" :key="template.id"
            @click="selectTemplate(template)"
            :class="[
              'p-4 border-2 rounded-lg cursor-pointer transition-all',
              selectedTemplateId === template.id
                ? 'border-blue-500 bg-blue-50'
                : 'border-gray-200 hover:border-blue-300 hover:bg-gray-50'
            ]">
            <div class="flex items-start">
              <div class="flex-shrink-0 mt-1">
                <div :class="[
                  'w-5 h-5 rounded-full border-2 flex items-center justify-center',
                  selectedTemplateId === template.id ? 'border-blue-500 bg-blue-500' : 'border-gray-300'
                ]">
                  <svg v-if="selectedTemplateId === template.id" class="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" />
                  </svg>
                </div>
              </div>
              <div class="ml-3 flex-1">
                <h3 class="text-base font-semibold text-gray-900">{{ template.templateName }}</h3>
                <p v-if="template.description" class="text-sm text-gray-600 mt-1">{{ template.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="px-6 py-4 border-t flex justify-end space-x-3">
        <button @click="$emit('close')" class="px-4 py-2 border rounded-lg hover:bg-gray-50">取消</button>
        <button @click="handleAssociate" :disabled="!selectedTemplateId"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed">
          关联
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import answerTemplateApi from '@/api/answerTemplateApi'

const props = defineProps({
  skillId: { type: Number, required: true },
  associatedTemplateIds: { type: Array, default: () => [] }
})

const emit = defineEmits(['close', 'associate'])

const allTemplates = ref([])
const loading = ref(false)
const selectedTemplateId = ref(null)

const availableTemplates = computed(() => {
  return allTemplates.value.filter(t => !props.associatedTemplateIds.includes(t.id))
})

const loadTemplates = async () => {
  loading.value = true
  try {
    const data = await answerTemplateApi.getAllTemplates()
    allTemplates.value = data || []
  } catch (error) {
    console.error('Failed to load templates:', error)
    alert('加载模版失败')
  } finally {
    loading.value = false
  }
}

const selectTemplate = (template) => {
  selectedTemplateId.value = template.id
}

const handleAssociate = () => {
  if (!selectedTemplateId.value) return
  emit('associate', selectedTemplateId.value)
}

onMounted(() => {
  loadTemplates()
})
</script>
