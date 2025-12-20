import { createRouter, createWebHistory } from 'vue-router'

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
  // 学习模块 - 技能管理
  {
    path: '/skills/tree',
    name: 'SkillTree',
    component: () => import('../views/skills/SkillTree.vue')
  },
  {
    path: '/skills/progress',
    name: 'LearningProgress',
    component: () => import('../views/skills/LearningProgress.vue')
  },
  // 设置模块 - 用户管理
  {
    path: '/settings/users',
    name: 'UserManagement',
    component: () => import('../views/settings/UserManagement.vue')
  },
  {
    path: '/settings/profile',
    name: 'Profile',
    component: () => import('../views/settings/Profile.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
