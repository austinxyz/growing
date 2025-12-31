<template>
  <div class="flex h-screen bg-background">
    <!-- Mobile Menu Button (visible only on mobile) -->
    <button
      @click="toggleMobileSidebar"
      class="md:hidden fixed top-4 left-4 z-50 p-3 bg-white rounded-lg shadow-lg border border-gray-200 hover:bg-gray-50 transition-colors"
      :class="{ 'left-4': !isMobileSidebarOpen, 'left-[calc(16rem+1rem)]': isMobileSidebarOpen }"
      aria-label="Toggle menu"
    >
      <svg v-if="!isMobileSidebarOpen" class="w-6 h-6 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
      </svg>
      <svg v-else class="w-6 h-6 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
      </svg>
    </button>

    <!-- Mobile Overlay (dark backdrop) -->
    <transition name="fade">
      <div
        v-if="isMobileSidebarOpen"
        @click="closeMobileSidebar"
        class="md:hidden fixed inset-0 bg-black/50 z-30"
      ></div>
    </transition>

    <!-- Sidebar -->
    <aside
      class="no-print transition-transform duration-300 ease-in-out z-40"
      :class="{
        // Desktop: always visible
        'relative': !isMobile,
        // Mobile: drawer style
        'fixed inset-y-0 left-0': isMobile,
        '-translate-x-full': isMobile && !isMobileSidebarOpen,
        'translate-x-0': isMobile && isMobileSidebarOpen
      }"
    >
      <Sidebar @close="closeMobileSidebar" :is-mobile="isMobile" />
    </aside>

    <!-- Main Content Area -->
    <main class="flex-1 overflow-auto">
      <!-- Mobile top spacing for hamburger button -->
      <div class="md:hidden h-16"></div>
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import Sidebar from './Sidebar.vue'

const isMobileSidebarOpen = ref(false)
const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
  // Auto-close sidebar when switching to desktop
  if (!isMobile.value) {
    isMobileSidebarOpen.value = false
  }
}

const toggleMobileSidebar = () => {
  isMobileSidebarOpen.value = !isMobileSidebarOpen.value
}

const closeMobileSidebar = () => {
  if (isMobile.value) {
    isMobileSidebarOpen.value = false
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style>
/* Fade transition for overlay */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

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
