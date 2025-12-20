<template>
  <div class="min-h-screen flex items-center justify-center bg-background p-4">
    <div class="w-full max-w-md">
      <!-- Logo/Brand -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-primary rounded-2xl mb-4">
          <span class="text-4xl">📚</span>
        </div>
        <h1 class="text-3xl font-bold text-foreground mb-2">Growing</h1>
        <p class="text-muted-foreground">Personal Growth Management System</p>
      </div>

      <!-- Login Card -->
      <div class="bg-card border border-border rounded-lg p-8 shadow-lg">
        <h2 class="text-2xl font-bold text-foreground mb-6">登录</h2>

        <!-- Error Message -->
        <div v-if="errorMessage" class="mb-4 p-3 bg-destructive/10 border border-destructive rounded-md">
          <p class="text-destructive text-sm">{{ errorMessage }}</p>
        </div>

        <!-- Google Sign-In Button -->
        <div class="mb-6">
          <div id="google-signin-button" class="flex justify-center"></div>
        </div>

        <!-- Divider -->
        <div class="relative mb-6">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-border"></div>
          </div>
          <div class="relative flex justify-center text-sm">
            <span class="px-2 bg-card text-muted-foreground">或使用邮箱登录</span>
          </div>
        </div>

        <!-- Login Form -->
        <form @submit.prevent="handleLogin">
          <div class="space-y-4">
            <!-- Username -->
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">
                用户名或邮箱
              </label>
              <input
                v-model="loginForm.username"
                type="text"
                required
                placeholder="请输入用户名或邮箱"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                :class="{ 'border-destructive': errors.username }"
              />
              <p v-if="errors.username" class="mt-1 text-xs text-destructive">
                {{ errors.username }}
              </p>
            </div>

            <!-- Password -->
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">
                密码
              </label>
              <input
                v-model="loginForm.password"
                type="password"
                required
                placeholder="请输入密码"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                :class="{ 'border-destructive': errors.password }"
              />
              <p v-if="errors.password" class="mt-1 text-xs text-destructive">
                {{ errors.password }}
              </p>
            </div>

            <!-- Remember Me -->
            <div class="flex items-center justify-between">
              <label class="flex items-center space-x-2 cursor-pointer">
                <input
                  v-model="loginForm.rememberMe"
                  type="checkbox"
                  class="rounded border-border"
                />
                <span class="text-sm text-muted-foreground">记住我</span>
              </label>
              <a href="#" class="text-sm text-primary hover:underline">
                忘记密码？
              </a>
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              :disabled="isLoading"
              class="w-full px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ isLoading ? '登录中...' : '登录' }}
            </button>
          </div>
        </form>

        <!-- Divider -->
        <div class="relative my-6">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-border"></div>
          </div>
          <div class="relative flex justify-center text-sm">
            <span class="px-2 bg-card text-muted-foreground">或</span>
          </div>
        </div>

        <!-- Register Link -->
        <div class="text-center">
          <span class="text-sm text-muted-foreground">还没有账户？</span>
          <router-link to="/register" class="text-sm text-primary hover:underline ml-1">
            立即注册
          </router-link>
        </div>
      </div>

      <!-- Footer -->
      <div class="text-center mt-8 text-xs text-muted-foreground">
        <p>© 2025 Growing. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import userApi from '@/api/user'

const router = useRouter()
const { login } = useAuth()

// Google Client ID
const GOOGLE_CLIENT_ID = '85048175163-25v00bc8dflnpfq04hgsc5brls1fv4h2.apps.googleusercontent.com'

// 表单数据
const loginForm = ref({
  username: '',
  password: '',
  rememberMe: false
})

// 错误信息
const errors = ref({
  username: '',
  password: ''
})
const errorMessage = ref('')

// 加载状态
const isLoading = ref(false)

// 表单验证
const validateForm = () => {
  errors.value = {
    username: '',
    password: ''
  }

  let isValid = true

  // 验证用户名
  if (!loginForm.value.username.trim()) {
    errors.value.username = '请输入用户名或邮箱'
    isValid = false
  } else if (loginForm.value.username.trim().length < 3) {
    errors.value.username = '用户名至少3个字符'
    isValid = false
  }

  // 验证密码
  if (!loginForm.value.password) {
    errors.value.password = '请输入密码'
    isValid = false
  } else if (loginForm.value.password.length < 6) {
    errors.value.password = '密码至少6个字符'
    isValid = false
  }

  return isValid
}

// 处理登录
const handleLogin = async () => {
  errorMessage.value = ''

  // 验证表单
  if (!validateForm()) {
    return
  }

  isLoading.value = true

  try {
    // TODO: 实际的登录 API - 目前使用模拟查询
    // 通过用户名或邮箱查找用户
    const response = await userApi.getUsers()
    const users = response.data || []

    const user = users.find(u =>
      u.username === loginForm.value.username ||
      u.email === loginForm.value.username
    )

    if (!user) {
      errorMessage.value = '用户名或邮箱不存在'
      return
    }

    // TODO: 实际应该验证密码，这里先跳过密码验证
    // if (!verifyPassword(loginForm.value.password, user.password)) {
    //   errorMessage.value = '密码错误'
    //   return
    // }

    // 保存用户状态
    login({
      id: user.id,
      username: user.username,
      email: user.email,
      fullName: user.fullName
    })

    // 登录成功后跳转到首页
    router.push('/dashboard')
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '登录失败，请检查用户名和密码'
  } finally {
    isLoading.value = false
  }
}

// 加载 Google Sign-In SDK
const loadGoogleSignIn = () => {
  const script = document.createElement('script')
  script.src = 'https://accounts.google.com/gsi/client'
  script.async = true
  script.defer = true
  script.onload = initializeGoogleSignIn
  document.head.appendChild(script)
}

// 初始化 Google Sign-In
const initializeGoogleSignIn = () => {
  if (window.google) {
    window.google.accounts.id.initialize({
      client_id: GOOGLE_CLIENT_ID,
      callback: handleGoogleCallback,
      auto_select: false
    })

    window.google.accounts.id.renderButton(
      document.getElementById('google-signin-button'),
      {
        theme: 'outline',
        size: 'large',
        width: 320,
        text: 'signin_with',
        locale: 'zh_CN'
      }
    )
  }
}

// 处理 Google 回调
const handleGoogleCallback = async (response) => {
  try {
    // 解码 JWT token 获取用户信息
    const payload = JSON.parse(atob(response.credential.split('.')[1]))
    const googleEmail = payload.email

    // 查找是否已有此邮箱的用户
    const usersResponse = await userApi.getUsers()
    const users = usersResponse.data || []

    const existingUser = users.find(u => u.email === googleEmail)

    if (existingUser) {
      // 用户已存在，直接登录
      login({
        id: existingUser.id,
        username: existingUser.username,
        email: existingUser.email,
        fullName: existingUser.fullName
      })
      router.push('/dashboard')
    } else {
      // 用户不存在，跳转到注册页面并预填信息
      errorMessage.value = '此 Google 账号尚未注册，请先注册'
      setTimeout(() => {
        router.push('/register')
      }, 2000)
    }
  } catch (error) {
    console.error('Google Sign-In 错误:', error)
    errorMessage.value = 'Google 登录失败，请重试'
  }
}

// 组件挂载时加载 Google Sign-In
onMounted(() => {
  loadGoogleSignIn()
})
</script>
