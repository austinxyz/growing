import api from './index'

export default {
  // 获取所有职业路径
  getCareerPaths() {
    return api.get('/career-paths')
  }
}
