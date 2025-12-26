import apiClient from './index'

/**
 * 获取仪表盘整体统计信息
 */
export const getOverallStatistics = () => {
  return apiClient.get('/dashboard/statistics')
}

/**
 * 获取算法与数据结构分类统计
 */
export const getAlgorithmCategories = () => {
  return apiClient.get('/dashboard/algorithm-categories')
}

/**
 * 获取系统设计模块统计
 */
export const getSystemDesignSummary = () => {
  return apiClient.get('/dashboard/system-design-summary')
}
