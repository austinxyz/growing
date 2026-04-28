<template>
  <div
    @click="handleClick"
    :class="['relative rounded-xl border bg-white shadow-sm hover:shadow-lg hover:-translate-y-0.5 transition-all cursor-pointer p-4', borderClass, isClosed ? 'opacity-60 grayscale' : '']"
  >
    <!-- 右上角徽章：仅 stalled 或 offer-deadline -->
    <span
      v-if="badgeText"
      :class="['absolute top-3 right-3 text-xs font-semibold px-2 py-0.5 rounded', badgeClass]"
    >{{ badgeText }}</span>

    <!-- 顶部行：公司 / 职位 / 状态 pill -->
    <div class="flex justify-between items-start gap-3" :class="badgeText ? 'pr-20' : ''">
      <div class="min-w-0 flex-1">
        <div class="text-base font-bold text-gray-900 truncate">{{ app.companyName || '—' }}</div>
        <div class="text-sm text-gray-600 truncate">{{ app.positionName }}</div>
        <div class="text-xs text-gray-500 italic mt-0.5">{{ app.microStageLabel }}</div>
      </div>
      <span
        v-if="!badgeText"
        :class="['shrink-0 text-xs font-semibold px-2 py-0.5 rounded-full', pillClass]"
      >{{ statusDisplayLabel }}</span>
    </div>

    <!-- 4 步进度条 -->
    <div class="mt-3 flex items-center gap-1">
      <div
        v-for="step in 4"
        :key="step"
        :class="['flex-1 h-1.5 rounded-full relative', stepClass(step)]"
      >
        <span
          v-if="step === app.macroStageStep"
          class="absolute -right-1 -top-1 w-3 h-3 bg-indigo-500 border-2 border-white rounded-full ring-1 ring-indigo-500"
        ></span>
      </div>
    </div>
    <div class="mt-1 flex justify-between text-[10px] text-gray-400 uppercase tracking-wider">
      <span>Apply</span><span>Phone</span><span>Onsite</span><span>Offer</span>
    </div>

    <!-- 底部行：时间 + next-action chip -->
    <div class="mt-3 pt-2.5 border-t border-dashed border-gray-200 flex justify-between items-center gap-2">
      <div class="text-xs text-gray-600">
        已 <strong class="text-gray-900">{{ app.daysSinceApplied }} 天</strong>
        <span v-if="showLastUpdate"> · 上次更新 {{ app.daysSinceLastUpdate }}d</span>
      </div>
      <span
        v-if="app.nextActionLabel && app.nextActionLabel !== '—'"
        :class="['text-xs font-medium px-2 py-1 rounded', actionChipClass]"
      >{{ app.nextActionLabel }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  app: { type: Object, required: true }
})

const router = useRouter()

const handleClick = () => {
  router.push({ name: 'JobApplicationList', query: { id: props.app.id } })
}

// 左侧色条：根据 priority 染色
const borderClass = computed(() => {
  switch (props.app.priorityLevel) {
    case 'OFFER_DEADLINE': return 'border-l-4 border-l-emerald-500'
    case 'STALLED':        return 'border-l-4 border-l-rose-500'
    case 'SCHEDULED':      return 'border-l-4 border-l-amber-500'
    default:               return 'border-gray-200'
  }
})

// 右上角徽章
const badgeText = computed(() => {
  if (props.app.priorityLevel === 'STALLED') {
    return `⚠️ Stalled ${props.app.daysSinceLastUpdate}d`
  }
  if (props.app.priorityLevel === 'OFFER_DEADLINE' && props.app.nextActionDate) {
    const days = daysUntil(props.app.nextActionDate)
    return days >= 0 ? `⏰ ${days}d 截止` : '⏰ 已过期'
  }
  return null
})
const badgeClass = computed(() => {
  return props.app.priorityLevel === 'STALLED'
    ? 'bg-rose-100 text-rose-700'
    : 'bg-emerald-100 text-emerald-700'
})

// 状态 pill (右上角小标签) — 后端已返回 canonical (Applied/Screening/Interviewing/Offer)
// or untouched closed status (Rejected / Withdrawn / Chinese variants)
const CLOSED_STATUSES = ['Rejected', 'Withdrawn', '已拒绝', '已撤回']
const isClosed = computed(() => CLOSED_STATUSES.includes(props.app.applicationStatus))

const statusDisplayLabel = computed(() => {
  switch (props.app.applicationStatus) {
    case 'Screening':    return '已投递·筛选中'
    case 'Interviewing': return '面试中'
    case 'Applied':      return '已投递'
    case 'Offer':        return 'Offer'
    case 'Rejected':     return '已拒绝'
    case 'Withdrawn':    return '已撤回'
    default:             return props.app.applicationStatus
  }
})
const pillClass = computed(() => {
  switch (props.app.applicationStatus) {
    case 'Applied':      return 'bg-blue-100 text-blue-700'
    case 'Screening':    return 'bg-amber-100 text-amber-700'
    case 'Interviewing': return 'bg-pink-100 text-pink-700'
    case 'Offer':        return 'bg-emerald-100 text-emerald-700'
    case 'Rejected':
    case '已拒绝':       return 'bg-rose-100 text-rose-600 line-through'
    case 'Withdrawn':
    case '已撤回':       return 'bg-gray-200 text-gray-500 line-through'
    default:             return 'bg-gray-100 text-gray-700'
  }
})

// stepper 阶段染色
const stepClass = (n) => {
  return n <= (props.app.macroStageStep || 0)
    ? 'bg-indigo-500'
    : 'bg-gray-200'
}

// next-action chip 颜色
const actionChipClass = computed(() => {
  switch (props.app.nextActionType) {
    case 'OFFER_DEADLINE':      return 'bg-emerald-100 text-emerald-800'
    case 'FOLLOW_UP':           return 'bg-rose-100 text-rose-800'
    case 'SCHEDULED_INTERVIEW': return 'bg-amber-100 text-amber-800'
    default:                    return 'bg-gray-100 text-gray-700'
  }
})

// 仅当上次更新与申请时间不同时才显示 "上次更新"
const showLastUpdate = computed(() =>
  props.app.daysSinceLastUpdate != null
  && props.app.daysSinceLastUpdate !== props.app.daysSinceApplied
)

// Compare ISO dates as local-midnight to avoid UTC-vs-local off-by-one:
// `new Date("2026-05-05")` parses as UTC midnight; comparing against local-midnight
// `today` shifts by up to 1 day in any non-UTC timezone (causes "−1d 截止" on
// the actual deadline day for US users).
const daysUntil = (isoDate) => {
  const [y, m, d] = isoDate.split('-').map(Number)
  const target = new Date(y, m - 1, d)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return Math.round((target - today) / (1000 * 60 * 60 * 24))
}
</script>
