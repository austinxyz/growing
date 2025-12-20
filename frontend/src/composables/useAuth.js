import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

// 全局状态（在模块作用域中）
const currentUser = ref(null)

// 从 localStorage 恢复用户信息
const loadUserFromStorage = () => {
  try {
    const userJson = localStorage.getItem('currentUser')
    if (userJson) {
      currentUser.value = JSON.parse(userJson)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    localStorage.removeItem('currentUser')
  }
}

// 初始化时加载用户信息
loadUserFromStorage()

export const useAuth = () => {
  const router = useRouter()

  // 是否已登录
  const isAuthenticated = computed(() => !!currentUser.value)

  // 用户名
  const username = computed(() => currentUser.value?.username || '')

  // 全名
  const fullName = computed(() => currentUser.value?.fullName || '')

  // 显示名称（优先显示全名，否则显示用户名）
  const displayName = computed(() => {
    if (!currentUser.value) return ''
    return currentUser.value.fullName || currentUser.value.username
  })

  // 登录
  const login = (userData) => {
    currentUser.value = userData
    // 保存到 localStorage
    localStorage.setItem('currentUser', JSON.stringify(userData))
  }

  // 登出
  const logout = () => {
    currentUser.value = null
    localStorage.removeItem('currentUser')
    router.push('/login')
  }

  // 更新用户信息
  const updateUser = (userData) => {
    currentUser.value = { ...currentUser.value, ...userData }
    localStorage.setItem('currentUser', JSON.stringify(currentUser.value))
  }

  return {
    currentUser,
    isAuthenticated,
    username,
    fullName,
    displayName,
    login,
    logout,
    updateUser
  }
}
