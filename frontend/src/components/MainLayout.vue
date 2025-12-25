<template>
  <div class="flex h-screen bg-background">
    <!-- Sidebar -->
    <Sidebar class="no-print" />

    <!-- Main Content Area -->
    <main class="flex-1 overflow-auto">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import Sidebar from './Sidebar.vue'
</script>

<style>
/* 全局打印样式 */
@media print {
  /* 隐藏侧边栏 */
  .no-print {
    display: none !important;
  }

  /* 主内容区域占满整个页面 */
  main {
    width: 100% !important;
    height: auto !important;
    overflow: visible !important;
  }

  /* 移除背景色 */
  body,
  .bg-background {
    background: white !important;
  }

  /* 移除页面边距和内边距 */
  html,
  body {
    margin: 0 !important;
    padding: 0 !important;
    height: auto !important;
    overflow: visible !important;
  }
}
</style>
