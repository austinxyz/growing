import api from './index'

export default {
  // 获取所有用户
  getUsers() {
    return api.get('/users')
  },

  // 获取单个用户
  getUser(id) {
    return api.get(`/users/${id}`)
  },

  // 创建用户
  createUser(userData) {
    return api.post('/users', userData)
  },

  // 更新用户
  updateUser(id, userData) {
    return api.put(`/users/${id}`, userData)
  },

  // 删除用户
  deleteUser(id) {
    return api.delete(`/users/${id}`)
  }
}
