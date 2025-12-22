<template>
  <div class="red-flag-list">
    <h4 class="text-sm font-medium text-gray-700 mb-3 flex items-center">
      <span class="text-red-500 mr-2">⚠️</span>
      Red Flags
    </h4>

    <div v-if="redFlags.length === 0" class="text-sm text-gray-400 mb-3">
      暂无 Red Flags
    </div>

    <div v-for="(flag, index) in redFlags" :key="index" class="flag-item flex items-center space-x-2 mb-2">
      <input
        v-model="redFlags[index]"
        type="text"
        placeholder="Red Flag 描述"
        class="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
        @input="updateParent"
      />
      <button
        @click="removeFlag(index)"
        type="button"
        class="px-3 py-2 text-sm text-red-600 hover:text-red-800 hover:bg-red-50 rounded-md transition-colors"
      >
        删除
      </button>
    </div>

    <button
      @click="addFlag"
      type="button"
      class="mt-2 px-4 py-2 text-sm text-blue-600 hover:text-blue-800 hover:bg-blue-50 rounded-md transition-colors"
    >
      + 添加 Red Flag
    </button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const redFlags = ref([...(props.modelValue || [])])

const addFlag = () => {
  redFlags.value.push('')
  updateParent()
}

const removeFlag = (index) => {
  redFlags.value.splice(index, 1)
  updateParent()
}

const updateParent = () => {
  emit('update:modelValue', redFlags.value)
}

watch(() => props.modelValue, (newVal) => {
  redFlags.value = [...(newVal || [])]
}, { deep: true })
</script>

<style scoped>
.flag-item {
  animation: fadeIn 0.2s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
