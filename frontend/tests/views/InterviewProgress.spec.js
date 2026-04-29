import { describe, it, expect, vi, beforeEach } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'

// Mock the API before importing the SUT.
// vi.mock is hoisted to top of file, so we use vi.hoisted() to declare the spy
// in a way that's visible inside the factory.
const { getActiveProgressMock, getClosedProgressMock } = vi.hoisted(() => ({
  getActiveProgressMock: vi.fn(),
  getClosedProgressMock: vi.fn(),
}))
vi.mock('@/api/jobApplicationApi', () => ({
  jobApplicationApi: {
    getActiveProgress: getActiveProgressMock,
    getClosedProgress: getClosedProgressMock,
  },
}))

// Stub the children so this test focuses on InterviewProgress's own logic
vi.mock('@/components/job-search/ProgressCard.vue', () => ({
  default: {
    name: 'ProgressCard',
    props: ['app'],
    template: '<div class="card-stub" :data-id="app.id">{{ app.companyName }}</div>',
  },
}))
vi.mock('@/components/job-search/ProgressSummaryHeader.vue', () => ({
  default: {
    name: 'ProgressSummaryHeader',
    props: ['counts', 'sortMode', 'showClosed'],
    emits: ['update:sortMode', 'update:showClosed'],
    template: `
      <div class="header-stub">
        <button class="sort-stage" @click="$emit('update:sortMode', 'stage')"></button>
        <button class="sort-time" @click="$emit('update:sortMode', 'time')"></button>
        <button class="toggle-closed" @click="$emit('update:showClosed', !showClosed)"></button>
        <span class="summary">{{ counts.inProgress }}/{{ counts.stalled }}/{{ counts.offerPending }}</span>
      </div>`,
  },
}))

// Stub vue-router (used inside InterviewProgress empty-state CTA)
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: vi.fn() }),
}))

import InterviewProgress from '@/views/job-search/InterviewProgress.vue'

const sample = [
  // Server returns priority order; tests verify front-end re-sort doesn't refetch
  { id: 1, companyName: 'A', positionName: 'p1', applicationStatus: 'Applied',
    macroStageStep: 1, microStageLabel: '', daysSinceApplied: 5, daysSinceLastUpdate: 5,
    priorityLevel: 'WAITING', nextActionType: 'WAITING_FEEDBACK', nextActionLabel: '', nextActionDate: null,
    submissionType: 'Direct' },
  { id: 2, companyName: 'B', positionName: 'p2', applicationStatus: 'Interviewing',
    macroStageStep: 3, microStageLabel: '', daysSinceApplied: 20, daysSinceLastUpdate: 20,
    priorityLevel: 'STALLED', nextActionType: 'FOLLOW_UP', nextActionLabel: '', nextActionDate: null,
    submissionType: 'Referral' },
  { id: 3, companyName: 'C', positionName: 'p3', applicationStatus: 'Offer',
    macroStageStep: 4, microStageLabel: '', daysSinceApplied: 35, daysSinceLastUpdate: 5,
    priorityLevel: 'OFFER_DEADLINE', nextActionType: 'OFFER_DEADLINE', nextActionLabel: '', nextActionDate: null,
    submissionType: 'Direct' },
]

describe('InterviewProgress', () => {
  beforeEach(() => {
    getActiveProgressMock.mockReset()
    getClosedProgressMock.mockReset()
    // Default: closed-progress returns empty unless overridden per test
    getClosedProgressMock.mockResolvedValue([])
  })

  it('shows loading spinner before data resolves', async () => {
    let resolve
    getActiveProgressMock.mockReturnValue(new Promise((r) => (resolve = r)))
    const wrapper = mount(InterviewProgress)
    expect(wrapper.find('.animate-spin').exists()).toBe(true)
    resolve([])
    await flushPromises()
  })

  it('renders empty state when no applications', async () => {
    getActiveProgressMock.mockResolvedValue([])
    const wrapper = mount(InterviewProgress)
    await flushPromises()
    expect(wrapper.text()).toContain('暂无进行中的申请')
    expect(wrapper.findAll('.card-stub')).toHaveLength(0)
  })

  it('renders a card per application returned from the API', async () => {
    getActiveProgressMock.mockResolvedValue(sample)
    const wrapper = mount(InterviewProgress)
    await flushPromises()
    expect(wrapper.findAll('.card-stub')).toHaveLength(3)
  })

  it('toggling showClosed fetches closed jobs and merges them with active list', async () => {
    getActiveProgressMock.mockResolvedValue(sample)
    const closedSamples = [
      { id: 99, companyName: 'Z', positionName: 'pZ', applicationStatus: 'Rejected',
        macroStageStep: 0, microStageLabel: '已拒绝', daysSinceApplied: 90, daysSinceLastUpdate: 30,
        priorityLevel: 'WAITING', nextActionType: 'WAITING_FEEDBACK', nextActionLabel: '—', nextActionDate: null },
    ]
    getClosedProgressMock.mockResolvedValue(closedSamples)

    const wrapper = mount(InterviewProgress)
    await flushPromises()
    expect(wrapper.findAll('.card-stub')).toHaveLength(3)
    expect(getClosedProgressMock).not.toHaveBeenCalled()

    await wrapper.find('.toggle-closed').trigger('click')
    await flushPromises()

    expect(getClosedProgressMock).toHaveBeenCalledTimes(1)
    expect(wrapper.findAll('.card-stub')).toHaveLength(4)
  })

  it('toggling showClosed off again hides the closed cards (no extra fetch)', async () => {
    getActiveProgressMock.mockResolvedValue(sample)
    getClosedProgressMock.mockResolvedValue([
      { id: 99, companyName: 'Z', positionName: 'pZ', applicationStatus: 'Rejected',
        macroStageStep: 0, microStageLabel: '已拒绝', daysSinceApplied: 90, daysSinceLastUpdate: 30,
        priorityLevel: 'WAITING', nextActionType: 'WAITING_FEEDBACK', nextActionLabel: '—', nextActionDate: null },
    ])

    const wrapper = mount(InterviewProgress)
    await flushPromises()
    await wrapper.find('.toggle-closed').trigger('click')
    await flushPromises()
    expect(wrapper.findAll('.card-stub')).toHaveLength(4)

    // Toggle off — closed cards should disappear, no second fetch
    await wrapper.find('.toggle-closed').trigger('click')
    await flushPromises()
    expect(wrapper.findAll('.card-stub')).toHaveLength(3)
    expect(getClosedProgressMock).toHaveBeenCalledTimes(1)
  })

  it('switching sort mode does NOT issue a new network request', async () => {
    getActiveProgressMock.mockResolvedValue(sample)
    const wrapper = mount(InterviewProgress)
    await flushPromises()
    expect(getActiveProgressMock).toHaveBeenCalledTimes(1)

    await wrapper.find('.sort-stage').trigger('click')
    await wrapper.find('.sort-time').trigger('click')
    await flushPromises()

    expect(getActiveProgressMock).toHaveBeenCalledTimes(1) // still only the initial call
  })

  it('default render preserves backend (attention) order; By Time sorts oldest first', async () => {
    getActiveProgressMock.mockResolvedValue(sample)
    const wrapper = mount(InterviewProgress)
    await flushPromises()

    // Default 'attention' = backend order: 1 (WAITING), 2 (STALLED), 3 (OFFER_DEADLINE)
    let ids = wrapper.findAll('.card-stub').map((c) => c.attributes('data-id'))
    expect(ids).toEqual(['1', '2', '3'])

    // 'time' sort by daysSinceApplied DESC: 35, 20, 5 → 3, 2, 1
    await wrapper.find('.sort-time').trigger('click')
    await flushPromises()
    ids = wrapper.findAll('.card-stub').map((c) => c.attributes('data-id'))
    expect(ids).toEqual(['3', '2', '1'])
  })
})
