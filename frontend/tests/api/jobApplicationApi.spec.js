import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mock the axios instance BEFORE importing the API client
vi.mock('@/api/index', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

import api from '@/api/index'
import { jobApplicationApi } from '@/api/jobApplicationApi'

describe('jobApplicationApi.getActiveProgress', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls /job-applications/active-progress (no /api prefix — baseURL handles it)', () => {
    api.get.mockResolvedValueOnce([])
    jobApplicationApi.getActiveProgress()
    expect(api.get).toHaveBeenCalledWith('/job-applications/active-progress')
    expect(api.get).toHaveBeenCalledTimes(1)
  })

  it('returns the data array directly (interceptor unwraps response.data)', async () => {
    const stub = [{ id: 1, companyName: 'Adobe' }]
    api.get.mockResolvedValueOnce(stub)
    const result = await jobApplicationApi.getActiveProgress()
    expect(result).toBe(stub)
  })
})

describe('jobApplicationApi.getClosedProgress', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls /job-applications/closed-progress', () => {
    api.get.mockResolvedValueOnce([])
    jobApplicationApi.getClosedProgress()
    expect(api.get).toHaveBeenCalledWith('/job-applications/closed-progress')
  })
})
