<template>
  <div class="p-8">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-foreground">仪表盘</h1>
      <p class="text-muted-foreground mt-2">欢迎回来，{{ displayName }}！</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <!-- 技能进度卡片 -->
      <div class="bg-card border border-border rounded-lg p-6">
        <h3 class="text-lg font-semibold text-card-foreground mb-2">学习进度</h3>
        <p class="text-muted-foreground text-sm mb-4">跟踪你的技能学习进度</p>
        <div class="text-3xl font-bold text-primary">0%</div>
        <p class="text-xs text-muted-foreground mt-2">即将推出</p>
      </div>

      <!-- 试题练习卡片 -->
      <div class="bg-card border border-border rounded-lg p-6">
        <h3 class="text-lg font-semibold text-card-foreground mb-2">试题练习</h3>
        <p class="text-muted-foreground text-sm mb-4">已完成的练习题目</p>
        <div class="text-3xl font-bold text-primary">0</div>
        <p class="text-xs text-muted-foreground mt-2">即将推出</p>
      </div>

      <!-- 公司跟踪卡片 -->
      <div class="bg-card border border-border rounded-lg p-6">
        <h3 class="text-lg font-semibold text-card-foreground mb-2">目标公司</h3>
        <p class="text-muted-foreground text-sm mb-4">正在关注的目标公司</p>
        <div class="text-3xl font-bold text-primary">0</div>
        <p class="text-xs text-muted-foreground mt-2">即将推出</p>
      </div>
    </div>

    <!-- 快速访问 -->
    <div class="mt-8">
      <h2 class="text-xl font-semibold text-foreground mb-4">快速访问</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <router-link
          to="/skills/tree"
          class="bg-card border border-border rounded-lg p-4 hover:bg-accent transition-colors"
        >
          <h3 class="font-semibold text-card-foreground">技能树</h3>
          <p class="text-sm text-muted-foreground mt-1">查看和管理你的技能体系</p>
        </router-link>

        <!-- 仅管理员可见 -->
        <router-link
          v-if="isAdmin"
          to="/settings/users"
          class="bg-card border border-border rounded-lg p-4 hover:bg-accent transition-colors"
        >
          <h3 class="font-semibold text-card-foreground">用户管理</h3>
          <p class="text-sm text-muted-foreground mt-1">管理系统用户（仅管理员）</p>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuth } from '@/composables/useAuth'

const { displayName, isAdmin } = useAuth()
</script>
