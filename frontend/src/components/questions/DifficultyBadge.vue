<template>
  <span :class="['inline-block px-3 py-1 text-xs font-semibold rounded-full', difficultyClass]">
    {{ difficulty }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  difficulty: {
    type: String,
    required: true,
    validator: (value) => ['EASY', 'MEDIUM', 'HARD'].includes(value)
  }
})

const difficultyClass = computed(() => {
  switch (props.difficulty) {
    case 'EASY':
      return 'bg-green-500 text-white'
    case 'MEDIUM':
      return 'bg-yellow-500 text-white'
    case 'HARD':
      return 'bg-red-500 text-white'
    default:
      return 'bg-gray-500 text-white'
  }
})
</script>
