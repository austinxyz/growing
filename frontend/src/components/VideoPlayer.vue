<template>
  <div class="video-player">
    <!-- YouTube视频 -->
    <iframe
      v-if="videoType === 'youtube'"
      :src="`https://www.youtube.com/embed/${videoId}`"
      class="w-full aspect-video rounded-lg"
      frameborder="0"
      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
      allowfullscreen
    ></iframe>

    <!-- Bilibili视频 -->
    <iframe
      v-else-if="videoType === 'bilibili'"
      :src="`https://player.bilibili.com/player.html?bvid=${videoId}&page=1&high_quality=1`"
      class="w-full aspect-video rounded-lg"
      scrolling="no"
      border="0"
      frameborder="no"
      framespacing="0"
      allowfullscreen="true"
    ></iframe>

    <!-- 通用视频（HTML5） -->
    <video
      v-else-if="videoType === 'direct'"
      :src="url"
      class="w-full aspect-video rounded-lg"
      controls
      preload="metadata"
    >
      您的浏览器不支持视频播放
    </video>

    <!-- 不支持的视频链接，显示提示 -->
    <div v-else class="w-full aspect-video rounded-lg bg-gray-100 flex items-center justify-center">
      <div class="text-center text-gray-500">
        <svg class="mx-auto h-12 w-12 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
        </svg>
        <p class="text-sm">暂不支持该视频格式</p>
        <a
          v-if="url"
          :href="url"
          target="_blank"
          rel="noopener noreferrer"
          class="mt-2 inline-flex items-center gap-1 text-xs text-blue-600 hover:text-blue-800"
        >
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
          </svg>
          在新窗口打开
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  url: {
    type: String,
    required: true
  }
})

// 检测视频类型和提取视频ID
const videoInfo = computed(() => {
  if (!props.url) return { type: null, id: null }

  const url = props.url.trim()

  // YouTube检测
  // 支持格式：
  // - https://www.youtube.com/watch?v=VIDEO_ID
  // - https://youtu.be/VIDEO_ID
  // - https://www.youtube.com/embed/VIDEO_ID
  const youtubeMatch = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([a-zA-Z0-9_-]{11})/)
  if (youtubeMatch) {
    return { type: 'youtube', id: youtubeMatch[1] }
  }

  // Bilibili检测
  // 支持格式：
  // - https://www.bilibili.com/video/BV1xx411c7XZ
  // - https://b23.tv/BV1xx411c7XZ (短链接)
  const bilibiliMatch = url.match(/(?:bilibili\.com\/video\/|b23\.tv\/)(BV[a-zA-Z0-9]+)/)
  if (bilibiliMatch) {
    return { type: 'bilibili', id: bilibiliMatch[1] }
  }

  // 直接视频文件（.mp4, .webm, .ogg）
  if (url.match(/\.(mp4|webm|ogg)(\?.*)?$/i)) {
    return { type: 'direct', id: null }
  }

  // 未识别的格式
  return { type: 'unknown', id: null }
})

const videoType = computed(() => videoInfo.value.type)
const videoId = computed(() => videoInfo.value.id)
</script>
