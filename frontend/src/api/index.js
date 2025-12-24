import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000, // 增加到30秒，适应大数据量返回
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 添加 JWT token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    // 统一错误处理
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权,清除token并跳转到登录页
          console.error('未授权,请登录')
          localStorage.removeItem('token')
          localStorage.removeItem('currentUser')
          if (window.location.pathname !== '/login') {
            window.location.href = '/login'
          }
          break
        case 403:
          console.error('没有权限')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器错误')
          break
        default:
          console.error('请求失败:', error.response.data.message || error.message)
      }
    } else {
      console.error('网络错误:', error.message)
    }
    return Promise.reject(error)
  }
)

export default api
