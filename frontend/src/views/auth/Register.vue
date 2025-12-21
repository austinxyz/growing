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

      <!-- Register Card -->
      <div class="bg-card border border-border rounded-lg p-8 shadow-lg">
        <h2 class="text-2xl font-bold text-foreground mb-6">注册</h2>

        <!-- Error Message -->
        <div v-if="errorMessage" class="mb-4 p-3 bg-destructive/10 border border-destructive rounded-md">
          <p class="text-destructive text-sm">{{ errorMessage }}</p>
        </div>

        <!-- Success Message -->
        <div v-if="successMessage" class="mb-4 p-3 bg-primary/10 border border-primary rounded-md">
          <p class="text-primary text-sm">{{ successMessage }}</p>
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
            <span class="px-2 bg-card text-muted-foreground">或使用邮箱注册</span>
          </div>
        </div>

        <!-- Register Form -->
        <form @submit.prevent="handleRegister">
          <div class="space-y-4">
            <!-- Username -->
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">
                用户名 *
              </label>
              <input
                v-model="registerForm.username"
                type="text"
                required
                placeholder="请输入用户名"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                :class="{ 'border-destructive': errors.username }"
              />
              <p v-if="errors.username" class="mt-1 text-xs text-destructive">
                {{ errors.username }}
              </p>
              <p v-else class="mt-1 text-xs text-muted-foreground">
                3-50个字符，只能包含字母、数字和下划线
              </p>
            </div>

            <!-- Email -->
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">
                邮箱 *
              </label>
              <input
                v-model="registerForm.email"
                type="email"
                required
                placeholder="请输入邮箱"
                :readonly="isGoogleSignIn"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                :class="{ 'border-destructive': errors.email, 'opacity-75 cursor-not-allowed': isGoogleSignIn }"
              />
              <p v-if="errors.email" class="mt-1 text-xs text-destructive">
                {{ errors.email }}
              </p>
              <p v-if="isGoogleSignIn" class="mt-1 text-xs text-primary">
                ✓ 已从 Google 账号获取
              </p>
            </div>

            <!-- Password -->
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">
                密码 {{ isGoogleSignIn ? '(可选)' : '*' }}
              </label>
              <input
                v-model="registerForm.password"
                type="password"
                :required="!isGoogleSignIn"
                placeholder="请输入密码"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                :class="{ 'border-destructive': errors.password }"
              />
              <p v-if="errors.password" class="mt-1 text-xs text-destructive">
                {{ errors.password }}
              </p>
              <p v-else class="mt-1 text-xs text-muted-foreground">
                {{ isGoogleSignIn ? '设置密码以便在不使用 Google 登录时也能访问账号' : '至少6个字符' }}
              </p>
            </div>

            <!-- Confirm Password -->
            <div v-if="registerForm.password">
              <label class="block text-sm font-medium text-foreground mb-1">
                确认密码 *
              </label>
              <input
                v-model="registerForm.confirmPassword"
                type="password"
                required
                placeholder="请再次输入密码"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                :class="{ 'border-destructive': errors.confirmPassword }"
              />
              <p v-if="errors.confirmPassword" class="mt-1 text-xs text-destructive">
                {{ errors.confirmPassword }}
              </p>
            </div>

            <!-- Full Name -->
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">
                全名
              </label>
              <input
                v-model="registerForm.fullName"
                type="text"
                placeholder="请输入全名（可选）"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
              <p v-if="isGoogleSignIn" class="mt-1 text-xs text-primary">
                ✓ 已从 Google 账号获取
              </p>
            </div>

            <!-- Career Paths -->
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">
                职业路径
              </label>
              <div class="space-y-2">
                <label
                  v-for="path in careerPaths"
                  :key="path.id"
                  class="flex items-start space-x-2 cursor-pointer p-2 rounded-md hover:bg-accent/50"
                >
                  <input
                    type="checkbox"
                    :value="path.id"
                    v-model="registerForm.careerPathIds"
                    class="mt-1 rounded border-border"
                  />
                  <div class="flex-1">
                    <span class="text-foreground font-medium">{{ path.name }}</span>
                    <p class="text-xs text-muted-foreground">{{ path.description }}</p>
                  </div>
                </label>
              </div>
            </div>

            <!-- Terms -->
            <div>
              <label class="flex items-start space-x-2 cursor-pointer">
                <input
                  v-model="registerForm.agreeTerms"
                  type="checkbox"
                  required
                  class="mt-1 rounded border-border"
                />
                <span class="text-sm text-muted-foreground">
                  我已阅读并同意
                  <a href="#" class="text-primary hover:underline">服务条款</a>
                  和
                  <a href="#" class="text-primary hover:underline">隐私政策</a>
                </span>
              </label>
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              :disabled="isLoading"
              class="w-full px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ isLoading ? '注册中...' : '注册' }}
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

        <!-- Login Link -->
        <div class="text-center">
          <span class="text-sm text-muted-foreground">已有账户？</span>
          <router-link to="/login" class="text-sm text-primary hover:underline ml-1">
            立即登录
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
import authApi from '@/api/auth'
import careerPathApi from '@/api/careerPath'

const router = useRouter()
const { login } = useAuth()

// Google Client ID - 需要替换为您的实际 Client ID
const GOOGLE_CLIENT_ID = 'YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com'

// 职业路径列表
const careerPaths = ref([])

// 是否通过 Google 登录
const isGoogleSignIn = ref(false)

// 表单数据
const registerForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  fullName: '',
  careerPathIds: [],
  agreeTerms: false
})

// 错误信息
const errors = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})
const errorMessage = ref('')
const successMessage = ref('')

// 加载状态
const isLoading = ref(false)

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
      client_id: '85048175163-25v00bc8dflnpfq04hgsc5brls1fv4h2.apps.googleusercontent.com',
      callback: handleGoogleCallback,
      auto_select: false
    })

    window.google.accounts.id.renderButton(
      document.getElementById('google-signin-button'),
      {
        theme: 'outline',
        size: 'large',
        width: 320,
        text: 'signup_with',
        locale: 'zh_CN'
      }
    )
  }
}

// 处理 Google 回调
const handleGoogleCallback = (response) => {
  try {
    // 解码 JWT token 获取用户信息
    const payload = JSON.parse(atob(response.credential.split('.')[1]))

    // 自动填充表单
    isGoogleSignIn.value = true
    registerForm.value.email = payload.email
    registerForm.value.fullName = payload.name || ''

    // 使用邮箱前缀作为默认用户名
    const emailPrefix = payload.email.split('@')[0].replace(/[^a-zA-Z0-9_]/g, '_')
    registerForm.value.username = emailPrefix

    // 清空密码字段（用户可选择设置或不设置）
    registerForm.value.password = ''
    registerForm.value.confirmPassword = ''

  } catch (error) {
    console.error('Google Sign-In 错误:', error)
    errorMessage.value = 'Google 登录失败，请重试'
  }
}

// 加载职业路径
const loadCareerPaths = async () => {
  try {
    const response = await careerPathApi.getCareerPaths()
    careerPaths.value = response.data || []
  } catch (error) {
    console.error('加载职业路径失败:', error)
  }
}

// 表单验证
const validateForm = () => {
  errors.value = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  }

  let isValid = true

  // 验证用户名
  if (!registerForm.value.username.trim()) {
    errors.value.username = '请输入用户名'
    isValid = false
  } else if (registerForm.value.username.trim().length < 3) {
    errors.value.username = '用户名至少3个字符'
    isValid = false
  } else if (registerForm.value.username.trim().length > 50) {
    errors.value.username = '用户名最多50个字符'
    isValid = false
  } else if (!/^[a-zA-Z0-9_]+$/.test(registerForm.value.username)) {
    errors.value.username = '用户名只能包含字母、数字和下划线'
    isValid = false
  }

  // 验证邮箱
  if (!registerForm.value.email.trim()) {
    errors.value.email = '请输入邮箱'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.value.email)) {
    errors.value.email = '请输入有效的邮箱地址'
    isValid = false
  }

  // 验证密码（如果设置了密码）
  if (registerForm.value.password) {
    if (registerForm.value.password.length < 6) {
      errors.value.password = '密码至少6个字符'
      isValid = false
    }

    // 验证确认密码
    if (registerForm.value.password !== registerForm.value.confirmPassword) {
      errors.value.confirmPassword = '两次输入的密码不一致'
      isValid = false
    }
  } else if (!isGoogleSignIn.value) {
    // 非 Google 登录必须设置密码
    errors.value.password = '请输入密码'
    isValid = false
  }

  return isValid
}

// 处理注册
const handleRegister = async () => {
  errorMessage.value = ''
  successMessage.value = ''

  // 验证表单
  if (!validateForm()) {
    return
  }

  isLoading.value = true

  try {
    // 调用注册 API
    const response = await authApi.register({
      username: registerForm.value.username,
      email: registerForm.value.email,
      password: registerForm.value.password || `google_${Date.now()}`, // 如果没设置密码，生成一个随机密码
      fullName: registerForm.value.fullName || null,
      careerPathIds: registerForm.value.careerPathIds
    })

    successMessage.value = '注册成功！正在为您登录...'

    // 自动登录：保存用户状态和 token
    login(response.user, response.token)

    // 1秒后跳转到仪表盘
    setTimeout(() => {
      router.push('/dashboard')
    }, 1000)
  } catch (error) {
    if (error.response?.status === 409) {
      errorMessage.value = '用户名或邮箱已存在'
    } else {
      errorMessage.value = error.response?.data?.message || '注册失败，请重试'
    }
  } finally {
    isLoading.value = false
  }
}

// 组件挂载时加载职业路径和 Google Sign-In
onMounted(() => {
  loadCareerPaths()
  loadGoogleSignIn()
})
</script>
