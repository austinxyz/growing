import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ProgressCard from '@/components/job-search/ProgressCard.vue'

// Mock vue-router's useRouter with a hoisted spy
const pushMock = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: pushMock }),
}))

const baseApp = {
  id: 42,
  companyId: 50,
  companyName: 'Adobe',
  positionName: 'Sr EM, AI Platform',
  applicationStatus: 'Interviewing',
  macroStageStep: 3,
  microStageLabel: 'Onsite · 2/4 (Tech Round 1 已完成)',
  daysSinceApplied: 25,
  daysSinceLastUpdate: 9,
  priorityLevel: 'WAITING',
  nextActionType: 'WAITING_FEEDBACK',
  nextActionLabel: '⏳ 等待反馈',
  nextActionDate: null,
}

describe('ProgressCard', () => {
  it('renders company, position, micro-label', () => {
    const wrapper = mount(ProgressCard, { props: { app: baseApp } })
    const text = wrapper.text()
    expect(text).toContain('Adobe')
    expect(text).toContain('Sr EM, AI Platform')
    expect(text).toContain('Tech Round 1 已完成')
    expect(text).toContain('25 天')
  })

  it('shows red left border + stalled badge for STALLED priority', () => {
    const wrapper = mount(ProgressCard, {
      props: { app: { ...baseApp, priorityLevel: 'STALLED', daysSinceLastUpdate: 12 } },
    })
    const html = wrapper.html()
    expect(html).toMatch(/border-l-rose-500/)
    expect(wrapper.text()).toContain('Stalled 12d')
  })

  it('shows green left border + deadline badge for OFFER_DEADLINE priority', () => {
    const future = new Date()
    future.setDate(future.getDate() + 7)
    const iso = future.toISOString().split('T')[0] // YYYY-MM-DD
    const wrapper = mount(ProgressCard, {
      props: {
        app: {
          ...baseApp,
          priorityLevel: 'OFFER_DEADLINE',
          applicationStatus: 'Offer',
          macroStageStep: 4,
          nextActionType: 'OFFER_DEADLINE',
          nextActionLabel: '⏰ 截止 5/5',
          nextActionDate: iso,
        },
      },
    })
    expect(wrapper.html()).toMatch(/border-l-emerald-500/)
    expect(wrapper.text()).toMatch(/\d+d 截止/)
  })

  it('shows orange left border for SCHEDULED priority', () => {
    const wrapper = mount(ProgressCard, {
      props: { app: { ...baseApp, priorityLevel: 'SCHEDULED' } },
    })
    expect(wrapper.html()).toMatch(/border-l-amber-500/)
  })

  it('renders the 4-step stepper with the correct steps highlighted', () => {
    // macroStageStep=3 → first 3 bars indigo, 4th bar gray
    const wrapper = mount(ProgressCard, { props: { app: baseApp } })
    const bars = wrapper.findAll('.stepper > div, .h-1\\.5')
    // Some bars include the dot child div, so filter to top-level step bars
    const topBars = wrapper.findAll('div.flex-1.h-1\\.5')
    expect(topBars).toHaveLength(4)
    expect(topBars[0].classes()).toContain('bg-indigo-500')
    expect(topBars[1].classes()).toContain('bg-indigo-500')
    expect(topBars[2].classes()).toContain('bg-indigo-500')
    expect(topBars[3].classes()).toContain('bg-gray-200')
  })

  it('mutes closed (Rejected) cards visually', () => {
    const wrapper = mount(ProgressCard, {
      props: { app: { ...baseApp, applicationStatus: 'Rejected', priorityLevel: 'WAITING' } },
    })
    expect(wrapper.classes()).toContain('opacity-60')
    expect(wrapper.classes()).toContain('grayscale')
    expect(wrapper.text()).toContain('已拒绝')
  })

  it('mutes closed (Chinese 已拒绝) cards visually', () => {
    const wrapper = mount(ProgressCard, {
      props: { app: { ...baseApp, applicationStatus: '已拒绝', priorityLevel: 'WAITING' } },
    })
    expect(wrapper.classes()).toContain('opacity-60')
  })

  it('navigates to JobApplicationList with id query when clicked', async () => {
    pushMock.mockClear()
    const wrapper = mount(ProgressCard, { props: { app: baseApp } })
    await wrapper.trigger('click')
    expect(pushMock).toHaveBeenCalledWith({
      name: 'JobApplicationList',
      query: { id: 42 },
    })
  })
})
