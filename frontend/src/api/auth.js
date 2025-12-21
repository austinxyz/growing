import api from './index'

export default {
  // 用户登录
  login(credentials) {
    return api.post('/auth/login', credentials)
  },

  // 用户注册
  register(userData) {
    return api.post('/auth/register', userData)
  },

  // Google 登录
  googleLogin(credential) {
    return api.post('/auth/google', { credential })
  },

  // 获取当前用户信息
  getCurrentUser() {
    return api.get('/auth/me')
  },

  // 登出
  logout() {
    return api.post('/auth/logout')
  }
}
