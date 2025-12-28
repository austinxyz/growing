<template>
  <aside class="w-64 lg:w-64 md:w-56 sm:w-64 bg-card border-r border-border flex flex-col h-full">
    <!-- User Info Section -->
    <div class="p-4 border-b border-border bg-accent/20">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-2 min-w-0 flex-1">
          <div class="w-8 h-8 bg-primary rounded-full flex items-center justify-center flex-shrink-0">
            <span class="text-primary-foreground font-semibold text-sm">
              {{ displayName.charAt(0).toUpperCase() }}
            </span>
          </div>
          <div class="min-w-0 flex-1">
            <div class="flex items-center gap-1">
              <p class="text-sm font-medium text-foreground truncate">{{ displayName }}</p>
              <span v-if="isAdmin" class="px-1.5 py-0.5 text-[10px] font-semibold bg-primary/20 text-primary rounded">
                管理员
              </span>
            </div>
            <p class="text-xs text-muted-foreground truncate">{{ username }}</p>
          </div>
        </div>
        <button
          @click="handleLogout"
          class="flex-shrink-0 p-1 text-muted-foreground hover:text-destructive transition-colors"
          title="登出"
        >
          <LogOut class="w-4 h-4" />
        </button>
      </div>
    </div>

    <!-- Logo/Brand -->
    <div class="p-6 border-b border-border">
      <router-link to="/dashboard" class="flex items-center space-x-2">
        <div class="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
          <span class="text-primary-foreground font-bold text-lg">📚</span>
        </div>
        <div>
          <h1 class="text-lg font-bold text-foreground">Growing</h1>
          <p class="text-xs text-muted-foreground">Personal Growth</p>
        </div>
      </router-link>
    </div>

    <!-- Top Level Menu Tabs -->
    <div class="border-b border-border">
      <div class="flex">
        <button
          v-for="tab in topLevelTabs"
          :key="tab.key"
          @click="activeTopTab = tab.key"
          :class="[
            'flex-1 px-4 py-3 text-sm font-medium transition-colors',
            activeTopTab === tab.key
              ? 'text-primary border-b-2 border-primary bg-primary/5'
              : 'text-muted-foreground hover:text-foreground hover:bg-accent/50'
          ]"
        >
          <component :is="tab.icon" class="w-4 h-4 mx-auto mb-1" />
          <span class="block text-xs">{{ tab.label }}</span>
        </button>
      </div>
    </div>

    <!-- Navigation - Dynamic based on active tab -->
    <nav class="flex-1 overflow-y-auto p-4 space-y-2">
      <!-- 仪表盘 (always visible) -->
      <router-link
        v-if="activeTopTab === 'learning'"
        to="/dashboard"
        class="nav-item"
        :class="isActive('/dashboard')"
      >
        <LayoutDashboard class="w-5 h-5" />
        <span>仪表盘</span>
      </router-link>

      <!-- 学习类菜单 -->
      <template v-if="activeTopTab === 'learning'">
        <!-- 职业路径 -->
        <div class="space-y-1">
          <div class="nav-section-title">职业路径</div>
          <router-link
            to="/skills/career-paths"
            class="nav-item"
            :class="isActive('/skills/career-paths')"
          >
            <Briefcase class="w-5 h-5" />
            <span>职业技能</span>
          </router-link>
          <router-link
            to="/my-questions"
            class="nav-item"
            :class="isActive('/my-questions')"
          >
            <BookOpen class="w-5 h-5" />
            <span>我的试题库</span>
          </router-link>
        </div>

        <!-- 算法与数据结构 -->
        <div class="space-y-1">
          <div class="nav-section-title">算法与数据结构</div>
          <router-link
            to="/algorithm-learning"
            class="nav-item"
            :class="isActive('/algorithm-learning')"
          >
            <Code class="w-5 h-5" />
            <span>学习路径</span>
          </router-link>
          <router-link
            to="/algorithm-templates"
            class="nav-item"
            :class="isActive('/algorithm-templates')"
          >
            <FileCode class="w-5 h-5" />
            <span>模版库</span>
          </router-link>
          <router-link
            to="/learning-review"
            class="nav-item"
            :class="isActive('/learning-review')"
          >
            <BookCheck class="w-5 h-5" />
            <span>学习总结</span>
          </router-link>
        </div>

        <!-- 系统设计 -->
        <div class="space-y-1">
          <div class="nav-section-title">系统设计</div>
          <router-link
            to="/system-design/basics"
            class="nav-item"
            :class="isActive('/system-design/basics')"
          >
            <Network class="w-5 h-5" />
            <span>基础知识</span>
          </router-link>
          <router-link
            to="/system-design/cases"
            class="nav-item"
            :class="isActive('/system-design/cases')"
          >
            <Layers class="w-5 h-5" />
            <span>典型案例</span>
          </router-link>
          <router-link
            to="/system-design/summary"
            class="nav-item"
            :class="isActive('/system-design/summary')"
          >
            <FileText class="w-5 h-5" />
            <span>学习总结</span>
          </router-link>
        </div>
      </template>

      <!-- 求职类菜单 -->
      <template v-if="activeTopTab === 'career'">
        <div class="text-sm text-muted-foreground px-3 py-2">
          求职模块即将推出...
        </div>
      </template>

      <!-- 设置类菜单 -->
      <template v-if="activeTopTab === 'settings'">
        <!-- 内容管理（仅管理员可见） -->
        <div v-if="isAdmin" class="space-y-1">
          <div class="nav-section-title">内容管理</div>
          <router-link
            to="/admin/skills"
            class="nav-item"
            :class="isActive('/admin/skills')"
          >
            <Layers class="w-5 h-5" />
            <span>技能架构管理</span>
          </router-link>
          <router-link
            to="/admin/general-skills"
            class="nav-item"
            :class="isActive('/admin/general-skills')"
          >
            <Target class="w-5 h-5" />
            <span>技能内容库</span>
          </router-link>
          <router-link
            to="/admin/algorithm-content"
            class="nav-item"
            :class="isActive('/admin/algorithm-content')"
          >
            <Code class="w-5 h-5" />
            <span>算法与数据结构</span>
          </router-link>
          <router-link
            to="/admin/algorithm-templates"
            class="nav-item"
            :class="isActive('/admin/algorithm-templates')"
          >
            <FileCode class="w-5 h-5" />
            <span>算法模版库</span>
          </router-link>
          <router-link
            to="/admin/system-design-basics"
            class="nav-item"
            :class="isActive('/admin/system-design-basics')"
          >
            <Network class="w-5 h-5" />
            <span>系统设计基础</span>
          </router-link>
          <router-link
            to="/admin/system-design-cases"
            class="nav-item"
            :class="isActive('/admin/system-design-cases')"
          >
            <Layers class="w-5 h-5" />
            <span>系统设计案例</span>
          </router-link>
        </div>

        <!-- 用户管理（仅管理员可见） -->
        <div v-if="isAdmin" class="space-y-1">
          <div class="nav-section-title">用户管理</div>
          <router-link
            to="/settings/users"
            class="nav-item"
            :class="isActive('/settings/users')"
          >
            <Users class="w-5 h-5" />
            <span>用户管理</span>
          </router-link>
        </div>

        <!-- 个人设置 -->
        <div class="space-y-1">
          <div class="nav-section-title">个人设置</div>
          <router-link
            to="/settings/profile"
            class="nav-item"
            :class="isActive('/settings/profile')"
          >
            <UserCircle class="w-5 h-5" />
            <span>个人资料</span>
          </router-link>
        </div>
      </template>
    </nav>

    <!-- Footer/User Section -->
    <div class="p-4 border-t border-border">
      <div class="text-xs text-muted-foreground">
        <p>v1.0.0</p>
        <p>© 2025 Growing</p>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import {
  LayoutDashboard,
  BookOpen,
  Briefcase,
  Settings,
  Network,
  TrendingUp,
  Users,
  UserCircle,
  LogOut,
  Code,
  FileCode,
  Target,
  FileQuestion,
  BookCheck,
  Layers,
  FileText
} from 'lucide-vue-next';
import { useAuth } from '@/composables/useAuth';

const route = useRoute();
const activeTopTab = ref('learning');

// 用户认证状态
const { displayName, username, isAdmin, logout } = useAuth();

// 登出处理
const handleLogout = () => {
  logout();
};

// 顶级分类
const topLevelTabs = [
  { key: 'learning', label: '学习', icon: BookOpen },
  { key: 'career', label: '求职', icon: Briefcase },
  { key: 'settings', label: '设置', icon: Settings }
];

// 根据当前路由自动切换顶级tab
watch(() => route.path, (newPath) => {
  if (newPath.startsWith('/settings') || newPath.startsWith('/admin')) {
    activeTopTab.value = 'settings';
  } else if (newPath.startsWith('/skills') || newPath.startsWith('/dashboard') ||
             newPath.startsWith('/my-questions') || newPath.startsWith('/algorithm-learning') ||
             newPath.startsWith('/algorithm-templates') || newPath.startsWith('/system-design') ||
             newPath.startsWith('/learning-review')) {
    activeTopTab.value = 'learning';
  } else if (newPath.startsWith('/career') || newPath.startsWith('/companies') || newPath.startsWith('/resumes')) {
    activeTopTab.value = 'career';
  } else {
    activeTopTab.value = 'learning';
  }
}, { immediate: true });

const isActive = (path) => {
  return route.path.startsWith(path)
    ? 'bg-accent text-accent-foreground'
    : 'text-muted-foreground hover:bg-accent/50 hover:text-foreground';
};
</script>

<style scoped>
.nav-item {
  @apply flex items-center space-x-3 px-3 py-2 rounded-md text-sm font-medium transition-colors cursor-pointer;
}

.nav-section-title {
  @apply px-3 py-2 text-xs font-semibold text-muted-foreground uppercase tracking-wider;
}
</style>
