<template>
  <aside
    :class="[
      'bg-card border-r border-border flex flex-col h-full transition-all duration-300 relative',
      isCollapsed ? 'w-16' : 'w-64'
    ]"
  >
    <!-- Header: Logo + User Info + Collapse -->
    <div class="border-b border-border">
      <!-- Expanded State -->
      <div v-if="!isCollapsed" class="p-4">
        <!-- Logo & Collapse Button Row -->
        <div class="flex items-center justify-between mb-3">
          <router-link to="/dashboard" class="flex items-center space-x-2">
            <div class="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
              <span class="text-primary-foreground font-bold text-lg">📚</span>
            </div>
            <div>
              <h1 class="text-base font-bold text-foreground">个人成长管理</h1>
            </div>
          </router-link>
          <button
            v-if="!isMobile"
            @click="isCollapsed = !isCollapsed"
            class="p-2.5 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 transition-colors min-w-[44px] min-h-[44px] flex items-center justify-center"
            title="折叠侧边栏"
          >
            <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
            </svg>
          </button>
        </div>

        <!-- User Info Row -->
        <div class="flex items-center justify-between bg-accent/20 rounded-lg p-2">
          <div class="flex items-center space-x-2 min-w-0 flex-1">
            <div class="w-7 h-7 bg-primary rounded-full flex items-center justify-center flex-shrink-0">
              <span class="text-primary-foreground font-semibold text-xs">
                {{ displayName.charAt(0).toUpperCase() }}
              </span>
            </div>
            <div class="min-w-0 flex-1">
              <div class="flex items-center gap-1">
                <p class="text-xs font-medium text-foreground truncate">{{ displayName }}</p>
                <span v-if="isAdmin" class="px-1 py-0.5 text-[9px] font-semibold bg-primary/20 text-primary rounded">
                  管理
                </span>
              </div>
            </div>
          </div>
          <button
            @click="handleLogout"
            class="flex-shrink-0 p-2 text-muted-foreground hover:text-destructive transition-colors min-w-[44px] min-h-[44px] flex items-center justify-center"
            title="登出"
          >
            <LogOut class="w-5 h-5" />
          </button>
        </div>
      </div>

      <!-- Collapsed State -->
      <div v-else class="p-2 flex flex-col items-center gap-2">
        <button
          @click="isCollapsed = !isCollapsed"
          class="p-2.5 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 transition-colors min-w-[44px] min-h-[44px] flex items-center justify-center"
          title="展开侧边栏"
        >
          <svg class="w-5 h-5 text-gray-600 rotate-180" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
          </svg>
        </button>
        <div class="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
          <span class="text-primary-foreground font-bold text-lg">📚</span>
        </div>
        <div class="w-7 h-7 bg-primary rounded-full flex items-center justify-center">
          <span class="text-primary-foreground font-semibold text-xs">
            {{ displayName.charAt(0).toUpperCase() }}
          </span>
        </div>
      </div>
    </div>

    <!-- Top Level Menu Tabs -->
    <div class="border-b border-border">
      <div v-if="!isCollapsed" class="flex">
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

      <!-- Collapsed Tabs (Vertical Icons) -->
      <div v-else class="flex flex-col items-center py-2 space-y-2">
        <button
          v-for="tab in topLevelTabs"
          :key="tab.key"
          @click="activeTopTab = tab.key"
          :class="[
            'p-2 rounded transition-colors',
            activeTopTab === tab.key
              ? 'text-primary bg-primary/10'
              : 'text-muted-foreground hover:text-foreground hover:bg-accent/50'
          ]"
          :title="tab.label"
        >
          <component :is="tab.icon" class="w-5 h-5" />
        </button>
      </div>
    </div>

    <!-- Navigation - Dynamic based on active tab -->
    <nav v-if="!isCollapsed" class="flex-1 overflow-y-auto p-4 space-y-2">
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
        <!-- 职业技能 -->
        <div class="space-y-1">
          <div class="nav-section-title">职业技能</div>
          <router-link
            to="/general-skills/learning"
            class="nav-item"
            :class="isActive('/general-skills/learning')"
          >
            <Briefcase class="w-5 h-5" />
            <span>技能学习</span>
          </router-link>
          <router-link
            to="/question-bank"
            class="nav-item"
            :class="isActive('/question-bank')"
          >
            <FileQuestion class="w-5 h-5" />
            <span>试题库</span>
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
        <!-- 简历管理 -->
        <div class="space-y-1">
          <div class="nav-section-title">简历</div>
          <router-link
            to="/job-search/resume"
            class="nav-item"
            :class="isActive('/job-search/resume')"
          >
            <User class="w-5 h-5" />
            <span>基本信息管理</span>
          </router-link>
          <router-link
            to="/job-search/projects"
            class="nav-item"
            :class="isActive('/job-search/projects')"
          >
            <FolderOpen class="w-5 h-5" />
            <span>项目经验库</span>
          </router-link>
          <router-link
            to="/job-search/management"
            class="nav-item"
            :class="isActive('/job-search/management')"
          >
            <Users class="w-5 h-5" />
            <span>人员管理经验库</span>
          </router-link>
        </div>

        <!-- 面试准备 -->
        <div class="space-y-1">
          <div class="nav-section-title">面试</div>
          <router-link
            to="/job-search/companies"
            class="nav-item"
            :class="isActive('/job-search/companies')"
          >
            <Building class="w-5 h-5" />
            <span>公司与职位</span>
          </router-link>
          <router-link
            to="/job-search/applications"
            class="nav-item"
            :class="isActive('/job-search/applications')"
          >
            <Calendar class="w-5 h-5" />
            <span>申请列表</span>
          </router-link>
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
            <Network class="w-5 h-5" />
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
            to="/admin/skill-templates"
            class="nav-item"
            :class="isActive('/admin/skill-templates')"
          >
            <FileCode class="w-5 h-5" />
            <span>技能模版库</span>
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
            <UserCog class="w-5 h-5" />
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

    <!-- Collapsed Navigation (Icons Only) -->
    <nav v-else class="flex-1 overflow-y-auto p-2 space-y-1">
      <router-link
        v-if="activeTopTab === 'learning'"
        to="/dashboard"
        class="flex items-center justify-center p-2 rounded-md transition-colors"
        :class="isActive('/dashboard')"
        title="仪表盘"
      >
        <LayoutDashboard class="w-5 h-5" />
      </router-link>

      <template v-if="activeTopTab === 'learning'">
        <router-link
          to="/general-skills/learning"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/general-skills/learning')"
          title="技能学习"
        >
          <Briefcase class="w-5 h-5" />
        </router-link>
        <router-link
          to="/question-bank"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/question-bank')"
          title="试题库"
        >
          <FileQuestion class="w-5 h-5" />
        </router-link>
        <router-link
          to="/algorithm-learning"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/algorithm-learning')"
          title="学习路径"
        >
          <Code class="w-5 h-5" />
        </router-link>
        <router-link
          to="/algorithm-templates"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/algorithm-templates')"
          title="模版库"
        >
          <FileCode class="w-5 h-5" />
        </router-link>
        <router-link
          to="/learning-review"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/learning-review')"
          title="学习总结"
        >
          <BookCheck class="w-5 h-5" />
        </router-link>
        <router-link
          to="/system-design/cases"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/system-design/cases')"
          title="典型案例"
        >
          <Layers class="w-5 h-5" />
        </router-link>
        <router-link
          to="/system-design/summary"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/system-design/summary')"
          title="学习总结"
        >
          <FileText class="w-5 h-5" />
        </router-link>
      </template>

      <template v-if="activeTopTab === 'settings' && isAdmin">
        <router-link
          to="/admin/skills"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/admin/skills')"
          title="技能架构管理"
        >
          <Network class="w-5 h-5" />
        </router-link>
        <router-link
          to="/admin/general-skills"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/admin/general-skills')"
          title="技能内容库"
        >
          <Target class="w-5 h-5" />
        </router-link>
        <router-link
          to="/settings/users"
          class="flex items-center justify-center p-2 rounded-md transition-colors"
          :class="isActive('/settings/users')"
          title="用户管理"
        >
          <UserCog class="w-5 h-5" />
        </router-link>
      </template>
    </nav>

    <!-- Footer/User Section -->
    <div v-if="!isCollapsed" class="p-4 border-t border-border">
      <div class="text-xs text-muted-foreground">
        <p>v1.0.0</p>
        <p>© 2025 Growing</p>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
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
  FileText,
  Building,
  Calendar,
  UserCheck,
  User,
  FolderOpen,
  UserCog
} from 'lucide-vue-next';
import { useAuth } from '@/composables/useAuth';

const props = defineProps({
  isMobile: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close']);

const route = useRoute();
const router = useRouter();
const activeTopTab = ref('learning');
const isCollapsed = ref(false);

// 从localStorage加载折叠状态（仅在桌面端）
onMounted(() => {
  if (!props.isMobile) {
    const saved = localStorage.getItem('sidebar-collapsed');
    if (saved !== null) {
      isCollapsed.value = saved === 'true';
    }
  }
});

// 保存折叠状态到localStorage（仅在桌面端）
watch(isCollapsed, (newValue) => {
  if (!props.isMobile) {
    localStorage.setItem('sidebar-collapsed', newValue.toString());
  }
});

// 监听路由变化，在移动端导航后自动关闭侧边栏
watch(() => route.path, () => {
  if (props.isMobile) {
    emit('close');
  }
});

// 用户认证状态
const { displayName, username, isAdmin, logout } = useAuth();

// 登出处理
const handleLogout = () => {
  logout();
};

// 顶级分类
const topLevelTabs = [
  { key: 'learning', label: '学习', icon: BookOpen },
  { key: 'career', label: '求职', icon: UserCheck },
  { key: 'settings', label: '设置', icon: Settings }
];

// 根据当前路由自动切换顶级tab
watch(() => route.path, (newPath) => {
  if (newPath.startsWith('/settings') || newPath.startsWith('/admin')) {
    activeTopTab.value = 'settings';
  } else if (newPath.startsWith('/job-search') || newPath.startsWith('/career') ||
             newPath.startsWith('/companies') || newPath.startsWith('/resumes')) {
    activeTopTab.value = 'career';
  } else if (newPath.startsWith('/skills') || newPath.startsWith('/dashboard') ||
             newPath.startsWith('/algorithm-learning') ||
             newPath.startsWith('/algorithm-templates') || newPath.startsWith('/system-design') ||
             newPath.startsWith('/learning-review') || newPath.startsWith('/question-bank') ||
             newPath.startsWith('/general-skills')) {
    activeTopTab.value = 'learning';
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
