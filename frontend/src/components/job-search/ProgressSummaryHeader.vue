<template>
  <div class="flex flex-col md:flex-row md:justify-between md:items-start gap-4 mb-6">
    <!-- 标题 -->
    <div>
      <h1 class="text-3xl font-bold text-gray-900">面试进展</h1>
      <p class="text-sm text-gray-500 mt-1">跟进当前所有 in-progress 的面试</p>
    </div>

    <!-- 统计 stat boxes -->
    <div class="flex gap-3">
      <div class="flex flex-col items-center bg-white rounded-lg shadow-sm px-4 py-2 min-w-[80px]">
        <div class="text-2xl font-bold text-gray-900 leading-none">{{ counts.inProgress }}</div>
        <div class="text-[10px] text-gray-500 uppercase mt-1">进行中</div>
      </div>
      <div class="flex flex-col items-center bg-white rounded-lg shadow-sm px-4 py-2 min-w-[80px]">
        <div class="text-2xl font-bold text-rose-600 leading-none">{{ counts.stalled }}</div>
        <div class="text-[10px] text-gray-500 uppercase mt-1">需要跟进</div>
      </div>
      <div class="flex flex-col items-center bg-white rounded-lg shadow-sm px-4 py-2 min-w-[80px]">
        <div class="text-2xl font-bold text-emerald-600 leading-none">{{ counts.offerPending }}</div>
        <div class="text-[10px] text-gray-500 uppercase mt-1">Offer 待决定</div>
      </div>
    </div>
  </div>

  <!-- Toolbar -->
  <div class="flex flex-wrap items-center gap-2 mb-5">
    <span class="text-xs text-gray-500 uppercase font-semibold">排序</span>
    <div class="inline-flex bg-white border border-gray-200 rounded-md overflow-hidden">
      <button
        v-for="opt in sortOptions"
        :key="opt.value"
        @click="$emit('update:sortMode', opt.value)"
        :class="['px-3 py-1.5 text-sm transition', sortMode === opt.value ? 'bg-indigo-500 text-white' : 'text-gray-600 hover:bg-gray-50']"
      >{{ opt.label }}</button>
    </div>
    <button
      @click="$emit('update:showClosed', !showClosed)"
      :class="['ml-auto text-sm px-3 py-1.5 border rounded-md transition', showClosed ? 'border-gray-400 text-gray-700 bg-gray-50' : 'border-gray-300 text-gray-500 hover:bg-gray-50']"
    >
      {{ showClosed ? '✓ 已显示已结案' : '+ 显示已结案' }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  counts: { type: Object, required: true },          // { inProgress, stalled, offerPending }
  sortMode: { type: String, required: true },        // 'attention' | 'stage' | 'time'
  showClosed: { type: Boolean, default: false }
})
defineEmits(['update:sortMode', 'update:showClosed'])

const sortOptions = [
  { value: 'attention', label: '🔥 Needs Attention' },
  { value: 'stage',     label: 'By Stage' },
  { value: 'time',      label: 'By Time' }
]
</script>
