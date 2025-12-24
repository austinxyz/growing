import apiClient from './index'

/**
 * 学习总结API
 */
export default {
  /**
   * 获取用户的学习总结（按大分类和Focus Area分组）
   */
  getLearningReview() {
    return apiClient.get('/questions/learning-review')
  }
}
