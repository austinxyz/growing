import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  // 认证路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue')
  },
  // 职业技能
  {
    path: '/skills/career-paths',
    name: 'MyCareerPaths',
    component: () => import('../views/careerPaths/MyCareerPaths.vue')
  },
  // 管理员 - 技能管理
  {
    path: '/admin/skills',
    name: 'SkillManagement',
    component: () => import('../views/skills/admin/SkillManagement.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/skills/:id',
    name: 'SkillDetailAdmin',
    component: () => import('../views/skills/admin/SkillDetailAdmin.vue'),
    meta: { requiresAdmin: true }
  },
  // 设置模块 - 用户管理（仅管理员）
  {
    path: '/settings/users',
    name: 'UserManagement',
    component: () => import('../views/settings/UserManagement.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/settings/profile',
    name: 'Profile',
    component: () => import('../views/settings/Profile.vue')
  },
  // 试题库模块
  {
    path: '/my-questions',
    name: 'MyQuestionBank',
    component: () => import('../views/questions/MyQuestionBank.vue')
  },
  // 管理员 - 试题管理
  {
    path: '/admin/questions',
    name: 'QuestionManagement',
    component: () => import('../views/questions/admin/QuestionManagement.vue'),
    meta: { requiresAdmin: true }
  },
  // 管理员 - 算法内容管理（Phase 4）
  {
    path: '/admin/algorithm-content',
    name: 'AlgorithmContentManagement',
    component: () => import('../views/admin/AlgorithmContentManagement.vue'),
    meta: { requiresAdmin: true }
  },
  // 用户端 - 算法学习（Phase 4）
  {
    path: '/algorithm-learning',
    name: 'AlgorithmLearning',
    component: () => import('../views/AlgorithmLearning.vue')
  },
  // 用户端 - 算法模版库（Phase 4）
  {
    path: '/algorithm-templates',
    name: 'AlgorithmTemplates',
    component: () => import('../views/AlgorithmTemplates.vue')
  },
  // 用户端 - 学习总结
  {
    path: '/learning-review',
    name: 'LearningReview',
    component: () => import('../views/LearningReview.vue')
  },
  // 管理员 - 算法模版管理（Phase 4）
  {
    path: '/admin/algorithm-templates',
    name: 'AlgorithmTemplateManagement',
    component: () => import('../views/admin/AlgorithmTemplateManagement.vue'),
    meta: { requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：检查管理员权限
router.beforeEach((to, from, next) => {
  const { isAuthenticated, isAdmin } = useAuth()

  // 如果路由需要管理员权限
  if (to.meta.requiresAdmin) {
    if (!isAuthenticated.value) {
      // 未登录，重定向到登录页
      next({ name: 'Login', query: { redirect: to.fullPath } })
    } else if (!isAdmin.value) {
      // 已登录但不是管理员，显示无权限提示并返回
      alert('您没有访问此页面的权限')
      next(false)
    } else {
      // 是管理员，允许访问
      next()
    }
  } else {
    // 不需要管理员权限，直接放行
    next()
  }
})

export default router
