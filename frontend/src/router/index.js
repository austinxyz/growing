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
  // 职业技能 - 旧版本（保留兼容性）
  {
    path: '/skills/career-paths',
    name: 'MyCareerPaths',
    component: () => import('../views/careerPaths/MyCareerPaths.vue')
  },
  // 通用技能学习 - 新版（Phase 6 v2 - 复用管理页面布局）
  {
    path: '/general-skills/learning',
    name: 'GeneralSkillLearning',
    component: () => import('../views/GeneralSkillLearning.vue')
  },
  // 通用技能学习 - 带skillId参数
  {
    path: '/general-skills/learning/:skillId',
    name: 'GeneralSkillLearningWithId',
    component: () => import('../views/GeneralSkillLearning.vue')
  },
  // 用户端 - 技能详情（旧版，保留兼容性）⚠️ 动态路由必须放在最后
  {
    path: '/skills/:id',
    name: 'SkillDetail',
    component: () => import('../views/skills/SkillDetail.vue')
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
  // 设置模块 - 数据库备份管理（仅管理员）
  {
    path: '/settings/backup',
    name: 'BackupManagement',
    component: () => import('../views/settings/BackupManagement.vue'),
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
  },
  // 管理员 - 系统设计案例管理（Phase 5.2）
  {
    path: '/admin/system-design-cases',
    name: 'SystemDesignCaseManagement',
    component: () => import('../views/admin/SystemDesignCaseManagement.vue'),
    meta: { requiresAdmin: true }
  },
  // 用户端 - 系统设计案例学习（Phase 5.2）
  {
    path: '/system-design/cases',
    name: 'SystemDesignCases',
    component: () => import('../views/SystemDesignCases.vue')
  },
  // 用户端 - 系统设计案例详情（通过URL参数）
  {
    path: '/system-design/cases/:id',
    name: 'SystemDesignCaseDetail',
    component: () => import('../views/SystemDesignCases.vue'),
    props: route => ({ caseId: route.params.id })
  },
  // 用户端 - 系统设计学习总结（Phase 5.2）
  {
    path: '/system-design/summary',
    name: 'SystemDesignSummary',
    component: () => import('../views/SystemDesignSummary.vue')
  },
  // 管理员 - 通用技能内容管理（Phase 6）
  {
    path: '/admin/general-skills',
    name: 'GeneralSkillContentManagement',
    component: () => import('../views/admin/GeneralSkillContentManagement.vue'),
    meta: { requiresAdmin: true }
  },
  // 管理员 - 答题模版管理（Phase 6）
  {
    path: '/admin/answer-templates',
    name: 'AnswerTemplateManagement',
    component: () => import('../views/admin/AnswerTemplateManagement.vue'),
    meta: { requiresAdmin: true }
  },
  // 管理员 - 技能模版库管理（Phase 6）
  {
    path: '/admin/skill-templates',
    name: 'SkillTemplateManagement',
    component: () => import('../views/admin/SkillTemplateManagement.vue'),
    meta: { requiresAdmin: true }
  },
  // 用户端 - 试题库（职业技能试题练习）
  {
    path: '/question-bank',
    name: 'QuestionBank',
    component: () => import('../views/QuestionBank.vue')
  },
  // 用户端 - 求职管理（Phase 7）
  {
    path: '/job-search',
    redirect: '/job-search/dashboard',
    meta: { requiresAuth: true }
  },
  {
    path: '/job-search/dashboard',
    name: 'JobSearchDashboard',
    component: () => import('../views/job-search/JobSearchDashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/job-search/resume',
    name: 'ResumeManagement',
    component: () => import('../views/job-search/ResumeManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/job-search/projects',
    name: 'ProjectExperiences',
    component: () => import('../views/job-search/ProjectExperiences.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/job-search/management',
    name: 'ManagementExperiences',
    component: () => import('../views/job-search/ManagementExperiences.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/job-search/companies',
    name: 'CompanyJobManagement',
    component: () => import('../views/job-search/CompanyJobManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/job-search/applications',
    name: 'JobApplicationList',
    component: () => import('../views/job-search/JobApplicationList.vue'),
    meta: { requiresAuth: true }
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
