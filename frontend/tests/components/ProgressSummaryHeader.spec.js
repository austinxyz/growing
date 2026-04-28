import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ProgressSummaryHeader from '@/components/job-search/ProgressSummaryHeader.vue'

const baseProps = {
  counts: { inProgress: 5, stalled: 2, offerPending: 1 },
  sortMode: 'attention',
  showClosed: false,
}

describe('ProgressSummaryHeader', () => {
  it('renders the three counts', () => {
    const wrapper = mount(ProgressSummaryHeader, { props: baseProps })
    const text = wrapper.text()
    expect(text).toContain('5')
    expect(text).toContain('2')
    expect(text).toContain('1')
    expect(text).toContain('进行中')
    expect(text).toContain('需要跟进')
    expect(text).toContain('Offer 待决定')
  })

  it('marks the active sort button visually', () => {
    const wrapper = mount(ProgressSummaryHeader, {
      props: { ...baseProps, sortMode: 'stage' },
    })
    const buttons = wrapper.findAll('.seg button, button')
    const stageButton = buttons.find((b) => b.text().includes('By Stage'))
    expect(stageButton).toBeDefined()
    // Active button has the indigo background class
    expect(stageButton.classes().some((c) => c.includes('indigo-500'))).toBe(true)
  })

  it('emits update:sortMode when a sort option is clicked', async () => {
    const wrapper = mount(ProgressSummaryHeader, { props: baseProps })
    const stageBtn = wrapper.findAll('button').find((b) => b.text().includes('By Stage'))
    await stageBtn.trigger('click')
    expect(wrapper.emitted('update:sortMode')).toBeTruthy()
    expect(wrapper.emitted('update:sortMode')[0]).toEqual(['stage'])
  })

  it('emits update:showClosed (toggles current value) when closed button clicked', async () => {
    const wrapper = mount(ProgressSummaryHeader, { props: baseProps })
    const closedBtn = wrapper.findAll('button').find((b) => b.text().includes('显示已结案'))
    await closedBtn.trigger('click')
    expect(wrapper.emitted('update:showClosed')).toBeTruthy()
    expect(wrapper.emitted('update:showClosed')[0]).toEqual([true])
  })
})
