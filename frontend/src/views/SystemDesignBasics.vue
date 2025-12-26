<template>
  <div class="h-screen flex flex-col bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 顶部标题栏 - 添加渐变背景 -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg">
      <div class="px-6 py-4">
        <h1 class="text-2xl font-bold text-white">系统设计 - 基础知识</h1>
        <p class="text-xs text-blue-100 mt-1">系统设计核心概念、关键技术和设计模式</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：大分类 + Focus Area列表 -->
      <div class="w-64 bg-white border-r border-gray-200 flex flex-col shadow-lg">
        <!-- 大分类Tab - 增强样式 -->
        <div class="border-b border-gray-200">
          <nav class="flex flex-col space-y-1 p-2" aria-label="Categories">
            <button
              v-for="category in majorCategories"
              :key="category.id"
              @click="selectCategory(category.id)"
              :class="[
                'px-4 py-2 text-sm font-medium rounded-md text-left transition-all duration-200',
                selectedCategoryId === category.id
                  ? 'bg-gradient-to-r from-blue-50 to-purple-50 text-blue-600 border-l-4 border-l-blue-600 shadow-md'
                  : 'text-gray-700 hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-sm'
              ]"
            >
              {{ category.name }}
            </button>
          </nav>
        </div>

        <!-- Focus Area列表 -->
        <div class="flex-1 overflow-y-auto p-4">
          <div v-if="loading.focusAreas" class="text-center text-gray-500 py-8">
            加载中...
          </div>

          <div v-else-if="focusAreas.length === 0" class="text-center text-gray-400 py-8">
            该分类下暂无学习主题
          </div>

          <div v-else class="space-y-1">
            <button
              v-for="fa in focusAreas"
              :key="fa.id"
              @click="selectFocusArea(fa)"
              :class="[
                'w-full text-left px-4 py-3 rounded-md transition-all duration-200',
                selectedFocusArea?.id === fa.id
                  ? 'bg-gradient-to-r from-blue-50 to-purple-50 text-blue-700 font-medium border-l-4 border-blue-600 shadow-md'
                  : 'text-gray-700 hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-sm'
              ]"
            >
              {{ fa.name }}
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧：学习资料单页显示 -->
      <div class="flex-1 overflow-y-auto">
        <!-- 未选中Focus Area -->
        <div v-if="!selectedFocusArea" class="flex items-center justify-center h-full">
          <div class="text-center text-gray-400">
            <svg class="mx-auto h-16 w-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="mt-4 text-sm">请从左侧选择一个学习主题</p>
          </div>
        </div>

        <!-- 学习内容区域 -->
        <div v-else class="p-6">
          <!-- 顶部：Focus Area标题 + 学习资料下拉选择 -->
          <div class="mb-6 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-xl shadow-md p-4">
            <!-- 标题 + 下拉选择 -->
            <div class="flex items-center gap-4">
              <h2 class="text-lg font-bold text-white">{{ selectedFocusArea.name }}</h2>

              <!-- 学习资料下拉选择（靠右） -->
              <div v-if="learningContents.length > 0" class="flex items-center gap-2 ml-auto">
                <label class="text-sm font-medium text-white whitespace-nowrap">学习资料：</label>
                <select
                  :value="selectedContentItem?.id"
                  @change="onContentSelectChange"
                  class="w-80 px-3 py-1.5 text-sm border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-white focus:border-transparent bg-white/90 text-gray-900 shadow-sm"
                >
                  <option
                    v-for="(content, index) in learningContents"
                    :key="content.id"
                    :value="content.id"
                  >
                    {{ content.contentType === 'video' ? '📹' : '📄' }} {{ index + 1 }}. {{ content.title }}
                  </option>
                </select>
              </div>

              <!-- Loading状态（靠右） -->
              <div v-else-if="loading.contents" class="flex items-center gap-2 text-white ml-auto">
                <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span class="text-sm">加载中...</span>
              </div>

              <!-- 无学习资料（靠右） -->
              <div v-else class="text-purple-100 text-sm ml-auto">
                暂无学习资料
              </div>
            </div>
          </div>

          <!-- 全屏卡片网格布局 -->
          <div v-if="selectedContentItem" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 auto-rows-max">
            <!-- 卡片1: 学习资料信息 - 使用渐变背景 -->
                  <div :class="[
                    'bg-gradient-to-br from-blue-50 to-white rounded-xl border-2 border-blue-200 shadow-md hover:shadow-xl transition-all flex flex-col',
                    selectedContentItem.contentType === 'video' ? 'md:col-span-2 md:row-span-2' : ''
                  ]">
                    <div
                      @click="cardStates.resourceInfo = !cardStates.resourceInfo"
                      class="px-3 py-2 bg-gradient-to-r from-blue-500 to-blue-600 text-white flex items-center justify-between cursor-pointer hover:from-blue-600 hover:to-blue-700 transition-all flex-shrink-0 rounded-t-xl"
                    >
                      <div class="flex items-center gap-2">
                        <div class="w-2 h-6 bg-gradient-to-b from-blue-300 to-white rounded-full"></div>
                        <h3 class="font-semibold text-sm">📚 学习资料</h3>
                      </div>
                      <svg
                        :class="['w-4 h-4 transition-transform', cardStates.resourceInfo ? 'rotate-180' : '']"
                        fill="none" stroke="currentColor" viewBox="0 0 24 24"
                      >
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                      </svg>
                    </div>
                    <div v-show="cardStates.resourceInfo" class="p-3 flex flex-col flex-grow overflow-auto">
                      <h4 class="font-bold text-gray-900 mb-3 text-base flex-shrink-0">{{ selectedContentItem.title }}</h4>

                      <!-- 视频播放器（仅在视频类型时显示） -->
                      <div v-if="selectedContentItem.contentType === 'video' && selectedContentItem.url" class="mb-4 flex-shrink-0">
                        <VideoPlayer :url="selectedContentItem.url" />
                      </div>

                      <!-- 描述（如果有） -->
                      <div v-if="selectedContentItem.description" class="mb-4 flex-shrink-0">
                        <p class="text-xs text-gray-700">{{ selectedContentItem.description }}</p>
                      </div>

                      <!-- 对于非视频类型或作为备选链接 -->
                      <a
                        v-if="selectedContentItem.url"
                        :href="selectedContentItem.url"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="inline-flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white text-xs font-medium rounded-lg hover:bg-blue-700 transition-colors w-full mt-auto flex-shrink-0"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                        </svg>
                        {{ selectedContentItem.contentType === 'video' ? '在新窗口打开视频' : '打开学习资料' }}
                      </a>
                    </div>
                  </div>

                  <!-- 卡片2: 整体笔记 - 使用渐变背景 -->
                  <div class="bg-gradient-to-br from-green-50 to-white rounded-xl border-2 border-green-200 shadow-md hover:shadow-xl transition-all overflow-hidden">
                    <div class="px-3 py-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white flex items-center justify-between">
                      <div
                        @click="cardStates.overallNote = !cardStates.overallNote"
                        class="flex items-center gap-2 cursor-pointer flex-1"
                      >
                        <div class="w-2 h-6 bg-gradient-to-b from-green-300 to-white rounded-full"></div>
                        <h3 class="font-semibold text-sm">📝 整体笔记</h3>
                      </div>
                      <div class="flex items-center gap-1">
                        <!-- 编辑/删除按钮 -->
                        <button
                          v-if="currentNote && !isEditingNote"
                          @click.stop="startEditNote"
                          class="p-1 hover:bg-green-600 rounded transition-all"
                          title="编辑"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                          </svg>
                        </button>
                        <button
                          v-if="currentNote && !isEditingNote"
                          @click.stop="deleteNote"
                          class="p-1 hover:bg-green-600 rounded transition-all"
                          title="删除"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                        <!-- 折叠按钮 -->
                        <button
                          @click.stop="cardStates.overallNote = !cardStates.overallNote"
                          class="p-1 hover:bg-green-600 rounded transition-all"
                        >
                          <svg
                            :class="['w-3.5 h-3.5 transition-transform', cardStates.overallNote ? 'rotate-180' : '']"
                            fill="none" stroke="currentColor" viewBox="0 0 24 24"
                          >
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                          </svg>
                        </button>
                      </div>
                    </div>
                    <div v-show="cardStates.overallNote" class="p-3">
                      <!-- 笔记编辑区 -->
                      <div v-if="isEditingNote || !currentNote">
                        <textarea
                          v-model="noteContent"
                          rows="6"
                          class="w-full px-3 py-2 text-xs border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent resize-none"
                          placeholder="记录整体学习笔记 (支持Markdown)..."
                        ></textarea>
                        <div class="flex justify-end gap-2 mt-2">
                          <button
                            v-if="currentNote"
                            @click="cancelEditNote"
                            class="px-3 py-1.5 text-xs text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-50"
                          >
                            取消
                          </button>
                          <button
                            @click="saveNote"
                            :disabled="!noteContent.trim()"
                            class="px-3 py-1.5 text-xs text-white bg-green-600 rounded hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
                          >
                            {{ currentNote ? '保存' : '添加' }}
                          </button>
                        </div>
                      </div>

                      <!-- 笔记显示区 -->
                      <div v-else-if="currentNote">
                        <div
                          ref="overallNoteContent"
                          :class="['relative', !cardStates.overallNoteExpanded ? 'max-h-48 overflow-hidden' : '']"
                        >
                          <div class="prose prose-sm max-w-none text-xs">
                            <div v-html="renderMarkdown(currentNote.noteContent)" class="text-gray-700"></div>
                          </div>

                          <!-- 渐变遮罩 -->
                          <div v-if="!cardStates.overallNoteExpanded && cardStates.overallNoteOverflow" class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-white to-transparent pointer-events-none"></div>
                        </div>

                        <!-- 展开/收起按钮（仅在溢出时显示） -->
                        <button
                          v-if="cardStates.overallNoteOverflow"
                          @click="cardStates.overallNoteExpanded = !cardStates.overallNoteExpanded"
                          class="w-full mt-2 py-1 text-xs text-green-600 hover:text-green-700 font-medium flex items-center justify-center gap-1"
                        >
                          <span>{{ cardStates.overallNoteExpanded ? '收起' : '展开更多' }}</span>
                          <svg :class="['w-3 h-3 transition-transform', cardStates.overallNoteExpanded ? 'rotate-180' : '']" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                          </svg>
                        </button>
                      </div>

                      <!-- 空状态 -->
                      <div v-else class="text-center text-gray-400 py-6">
                        <p class="text-xs">点击编辑按钮添加整体笔记</p>
                      </div>
                    </div>
                  </div>

                  <!-- 添加知识点卡片 - 使用渐变背景 -->
                  <div
                    @click="showAddKnowledgePointModal = true"
                    class="bg-gradient-to-br from-purple-50 to-white rounded-xl shadow-md overflow-hidden border-2 border-dashed border-purple-300 hover:border-purple-500 hover:from-purple-100 hover:shadow-xl transition-all cursor-pointer"
                  >
                    <div class="flex flex-col items-center justify-center h-full py-6">
                      <svg class="w-10 h-10 text-purple-400 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                      </svg>
                      <p class="text-sm font-medium text-purple-600">添加知识点</p>
                    </div>
                  </div>

                  <!-- 知识点卡片列表 - 使用渐变背景 -->
                  <div
                    v-for="(point, index) in knowledgePoints"
                    :key="point.id"
                    class="bg-gradient-to-br from-purple-50 to-white rounded-xl border-2 border-purple-200 shadow-md hover:shadow-xl transition-all overflow-hidden"
                  >
                    <div class="px-3 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white flex items-center justify-between">
                      <div
                        @click="toggleKnowledgePointCard(point.id)"
                        class="flex items-center gap-2 cursor-pointer flex-1 min-w-0"
                      >
                        <div class="w-2 h-6 bg-gradient-to-b from-purple-300 to-white rounded-full"></div>
                        <span class="text-sm">💡</span>
                        <h3 class="font-semibold text-sm truncate">{{ index + 1 }}. {{ point.title }}</h3>
                      </div>
                      <div class="flex items-center gap-1 flex-shrink-0">
                        <!-- 编辑/删除按钮 -->
                        <button
                          @click.stop="editKnowledgePoint(point)"
                          class="p-1 hover:bg-purple-600 rounded transition-all"
                          title="编辑"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                          </svg>
                        </button>
                        <button
                          @click.stop="deleteKnowledgePoint(point.id)"
                          class="p-1 hover:bg-purple-600 rounded transition-all"
                          title="删除"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                        <!-- 折叠按钮 -->
                        <button
                          @click.stop="toggleKnowledgePointCard(point.id)"
                          class="p-1 hover:bg-purple-600 rounded transition-all"
                        >
                          <svg
                            :class="['w-3.5 h-3.5 transition-transform', cardStates.knowledgePoints[point.id] ? 'rotate-180' : '']"
                            fill="none" stroke="currentColor" viewBox="0 0 24 24"
                          >
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                          </svg>
                        </button>
                      </div>
                    </div>
                    <div v-show="cardStates.knowledgePoints[point.id]" class="p-3">
                      <div
                        :ref="el => { if (el) knowledgePointContents[point.id] = el }"
                        :class="['relative', !cardStates.knowledgePointsExpanded[point.id] ? 'max-h-48 overflow-hidden' : '']"
                      >
                        <div class="prose prose-sm max-w-none text-xs">
                          <div v-html="renderMarkdown(point.summary)" class="text-gray-700"></div>
                        </div>

                        <!-- 渐变遮罩 -->
                        <div v-if="!cardStates.knowledgePointsExpanded[point.id] && cardStates.knowledgePointsOverflow[point.id]" class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-white to-transparent pointer-events-none"></div>
                      </div>

                      <!-- 展开/收起按钮（仅在溢出时显示） -->
                      <button
                        v-if="cardStates.knowledgePointsOverflow[point.id]"
                        @click="cardStates.knowledgePointsExpanded[point.id] = !cardStates.knowledgePointsExpanded[point.id]"
                        class="w-full mt-2 py-1 text-xs text-purple-600 hover:text-purple-700 font-medium flex items-center justify-center gap-1"
                      >
                        <span>{{ cardStates.knowledgePointsExpanded[point.id] ? '收起' : '展开更多' }}</span>
                        <svg :class="['w-3 h-3 transition-transform', cardStates.knowledgePointsExpanded[point.id] ? 'rotate-180' : '']" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                        </svg>
                      </button>
                    </div>
                  </div>
            </div>
          </div>
        </div>
      </div>

    <!-- 知识点编辑模态框 -->
    <div v-if="showAddKnowledgePointModal" class="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">
            {{ editingKnowledgePoint ? '编辑知识点' : '添加知识点' }}
          </h3>
        </div>
        <div class="px-6 py-4 space-y-4">
          <!-- 标题 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">知识点标题 *</label>
            <input
              v-model="knowledgePointForm.title"
              type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="输入知识点标题"
            />
          </div>

          <!-- 总结 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">知识点总结 * (支持Markdown)</label>
            <textarea
              v-model="knowledgePointForm.summary"
              rows="10"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
              placeholder="记录这个知识点的核心内容、关键概念、示例等..."
            ></textarea>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="closeKnowledgePointModal"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveKnowledgePoint"
            :disabled="!canSaveKnowledgePoint"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ editingKnowledgePoint ? '保存' : '添加' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/api'
import majorCategoryApi from '@/api/majorCategoryApi'
import { marked } from 'marked'
import VideoPlayer from '@/components/VideoPlayer.vue'

// 必须在setup顶层调用useRoute
const route = useRoute()

// State
const loading = ref({
  focusAreas: false,
  contents: false
})

const majorCategories = ref([])
const allFocusAreas = ref([]) // 所有focus areas（带分类信息）
const learningContents = ref([])
const selectedCategoryId = ref(null)
const selectedFocusArea = ref(null)
const selectedContentItem = ref(null) // 当前选中的学习资料
const currentNote = ref(null) // 当前学习资料的笔记
const noteContent = ref('') // 笔记编辑内容
const isEditingNote = ref(false) // 是否正在编辑笔记
const activeNoteTab = ref('overall') // 当前激活的笔记Tab: overall | knowledge
const knowledgePoints = ref([]) // 知识点列表
const showAddKnowledgePointModal = ref(false) // 显示知识点编辑模态框
const editingKnowledgePoint = ref(null) // 正在编辑的知识点
const knowledgePointForm = ref({
  title: '',
  summary: ''
})

// 卡片折叠状态
const cardStates = ref({
  resourceInfo: true,  // 学习资料卡片默认展开
  overallNote: true,   // 整体笔记卡片默认展开
  knowledgePoints: {},  // 知识点卡片折叠状态 {id: boolean}
  // 卡片内容展开状态
  resourceInfoExpanded: false,  // 学习资料内容是否展开
  overallNoteExpanded: false,   // 整体笔记内容是否展开
  knowledgePointsExpanded: {},   // 知识点内容展开状态 {id: boolean}
  // 卡片内容是否溢出（需要展开按钮）
  resourceInfoOverflow: false,  // 学习资料内容是否溢出
  overallNoteOverflow: false,   // 整体笔记内容是否溢出
  knowledgePointsOverflow: {}   // 知识点内容溢出状态 {id: boolean}
})

// DOM引用
const resourceInfoContent = ref(null)
const overallNoteContent = ref(null)
const knowledgePointContents = ref({})

// 系统设计的Skill ID (固定为2)
const SYSTEM_DESIGN_SKILL_ID = 2

// Computed
const focusAreas = computed(() => {
  // 根据选中的大分类过滤focus areas
  if (!selectedCategoryId.value) return []
  return allFocusAreas.value.filter(fa =>
    fa.categoryIds && fa.categoryIds.includes(selectedCategoryId.value)
  )
})

const canSaveKnowledgePoint = computed(() => {
  return knowledgePointForm.value.title.trim() && knowledgePointForm.value.summary.trim()
})

// Methods
const loadCategories = async () => {
  try {
    // 只加载系统设计skill的分类（skillId=2）
    const data = await majorCategoryApi.getAllMajorCategories(SYSTEM_DESIGN_SKILL_ID)
    majorCategories.value = data

    if (majorCategories.value.length > 0) {
      selectCategory(majorCategories.value[0].id)
    }
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

const loadAllFocusAreas = async () => {
  try {
    loading.value.focusAreas = true
    // 加载系统设计技能下的所有focus areas（带分类信息）
    const data = await majorCategoryApi.getFocusAreasWithCategories(SYSTEM_DESIGN_SKILL_ID)
    allFocusAreas.value = data || []
  } catch (error) {
    console.error('Failed to load focus areas:', error)
    allFocusAreas.value = []
  } finally {
    loading.value.focusAreas = false
  }
}

const loadLearningContents = async () => {
  if (!selectedFocusArea.value) return

  try {
    loading.value.contents = true
    // ⚠️ 使用管理端API（更简单，不依赖stages）
    // 只获取PUBLIC可见的内容
    const data = await api.get(`/admin/learning-contents?focusAreaId=${selectedFocusArea.value.id}&page=0&size=1000`)
    // 注意：后端返回的是Page对象，需要取content字段
    learningContents.value = data.content || []

    // 自动选择第一个学习资料，并加载笔记和知识点
    if (learningContents.value.length > 0) {
      selectedContentItem.value = learningContents.value[0]
      // 加载该学习资料的笔记和知识点
      await loadNote()
      await loadKnowledgePoints()
    } else {
      selectedContentItem.value = null
    }
  } catch (error) {
    console.error('Failed to load learning contents:', error)
    learningContents.value = []
    selectedContentItem.value = null
  } finally {
    loading.value.contents = false
  }
}

const selectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  selectedFocusArea.value = null
  learningContents.value = []
  selectedContentItem.value = null

  // 由于focusAreas是computed属性，会自动过滤
  // 自动选择第一个focus area
  if (focusAreas.value.length > 0) {
    selectFocusArea(focusAreas.value[0])
  }
}

const selectFocusArea = (focusArea) => {
  selectedFocusArea.value = focusArea
  selectedContentItem.value = null
  // 清空笔记和知识点数据
  currentNote.value = null
  noteContent.value = ''
  isEditingNote.value = false
  knowledgePoints.value = []
  activeNoteTab.value = 'overall'
  // 重置卡片折叠状态
  cardStates.value.resourceInfo = true
  cardStates.value.overallNote = true
  cardStates.value.knowledgePoints = {}
  // 重置展开状态
  cardStates.value.resourceInfoExpanded = false
  cardStates.value.overallNoteExpanded = false
  cardStates.value.knowledgePointsExpanded = {}
  // 重置溢出状态
  cardStates.value.resourceInfoOverflow = false
  cardStates.value.overallNoteOverflow = false
  cardStates.value.knowledgePointsOverflow = {}
  loadLearningContents()
}

const selectContentItem = async (content) => {
  selectedContentItem.value = content
  // 加载该学习资料的笔记和知识点
  await loadNote()
  await loadKnowledgePoints()
}

// 下拉选择学习资料变化处理
const onContentSelectChange = async (event) => {
  const contentId = Number(event.target.value)
  const content = learningContents.value.find(c => c.id === contentId)
  if (content) {
    await selectContentItem(content)
  }
}

// 加载笔记
const loadNote = async () => {
  if (!selectedContentItem.value) {
    currentNote.value = null
    noteContent.value = ''
    isEditingNote.value = false
    return
  }

  try {
    const data = await api.get(`/learning-contents/${selectedContentItem.value.id}/note`)
    currentNote.value = data || null
    noteContent.value = data?.noteContent || ''
    isEditingNote.value = false

    // 检测笔记内容是否溢出
    await nextTick()
    cardStates.value.overallNoteOverflow = checkOverflow(overallNoteContent.value)
  } catch (error) {
    // 204 No Content表示没有笔记
    if (error.response?.status === 204) {
      currentNote.value = null
      noteContent.value = ''
      isEditingNote.value = false
    } else {
      console.error('Failed to load note:', error)
    }
  }
}

// 开始编辑笔记
const startEditNote = () => {
  isEditingNote.value = true
  noteContent.value = currentNote.value?.noteContent || ''
}

// 取消编辑
const cancelEditNote = () => {
  isEditingNote.value = false
  noteContent.value = currentNote.value?.noteContent || ''
}

// 保存笔记
const saveNote = async () => {
  if (!noteContent.value.trim() || !selectedContentItem.value) return

  try {
    const data = await api.post(`/learning-contents/${selectedContentItem.value.id}/note`, {
      noteContent: noteContent.value
    })
    currentNote.value = data
    isEditingNote.value = false

    // 检测笔记内容是否溢出
    await nextTick()
    cardStates.value.overallNoteOverflow = checkOverflow(overallNoteContent.value)
  } catch (error) {
    console.error('Failed to save note:', error)
    alert('保存失败')
  }
}

// 删除笔记
const deleteNote = async () => {
  if (!selectedContentItem.value || !confirm('确定要删除这条笔记吗？')) return

  try {
    await api.delete(`/learning-contents/${selectedContentItem.value.id}/note`)
    currentNote.value = null
    noteContent.value = ''
    isEditingNote.value = false
  } catch (error) {
    console.error('Failed to delete note:', error)
    alert('删除失败')
  }
}

// Markdown渲染
const renderMarkdown = (content) => {
  if (!content) return ''
  return marked(content, { breaks: true })
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 检测内容是否溢出
const checkOverflow = (element) => {
  if (!element) return false
  return element.scrollHeight > element.clientHeight
}

// ==================== 知识点相关方法 ====================

// 加载知识点
const loadKnowledgePoints = async () => {
  if (!selectedContentItem.value) {
    knowledgePoints.value = []
    return
  }

  try {
    const data = await api.get(`/learning-contents/${selectedContentItem.value.id}/knowledge-points`)
    knowledgePoints.value = data || []

    // 初始化知识点卡片折叠状态（默认展开）
    data.forEach(point => {
      if (!(point.id in cardStates.value.knowledgePoints)) {
        cardStates.value.knowledgePoints[point.id] = true
      }
      // 初始化知识点内容展开状态（默认收起）
      if (!(point.id in cardStates.value.knowledgePointsExpanded)) {
        cardStates.value.knowledgePointsExpanded[point.id] = false
      }
    })

    // 检测每个知识点内容是否溢出
    await nextTick()
    data.forEach(point => {
      const element = knowledgePointContents.value[point.id]
      cardStates.value.knowledgePointsOverflow[point.id] = checkOverflow(element)
    })
  } catch (error) {
    console.error('Failed to load knowledge points:', error)
    knowledgePoints.value = []
  }
}

// 切换知识点卡片折叠状态
const toggleKnowledgePointCard = (pointId) => {
  cardStates.value.knowledgePoints[pointId] = !cardStates.value.knowledgePoints[pointId]
}

// 打开知识点模态框（编辑）
const editKnowledgePoint = (point) => {
  editingKnowledgePoint.value = point
  knowledgePointForm.value = {
    title: point.title,
    summary: point.summary
  }
  showAddKnowledgePointModal.value = true
}

// 关闭知识点模态框
const closeKnowledgePointModal = () => {
  showAddKnowledgePointModal.value = false
  editingKnowledgePoint.value = null
  knowledgePointForm.value = {
    title: '',
    summary: ''
  }
}

// 保存知识点
const saveKnowledgePoint = async () => {
  if (!canSaveKnowledgePoint.value || !selectedContentItem.value) return

  try {
    if (editingKnowledgePoint.value) {
      // 更新
      await api.put(`/learning-contents/knowledge-points/${editingKnowledgePoint.value.id}`, {
        title: knowledgePointForm.value.title,
        summary: knowledgePointForm.value.summary
      })
    } else {
      // 创建
      await api.post(`/learning-contents/${selectedContentItem.value.id}/knowledge-points`, {
        title: knowledgePointForm.value.title,
        summary: knowledgePointForm.value.summary
      })
    }

    await loadKnowledgePoints()
    closeKnowledgePointModal()
  } catch (error) {
    console.error('Failed to save knowledge point:', error)
    alert('保存失败')
  }
}

// 删除知识点
const deleteKnowledgePoint = async (pointId) => {
  if (!confirm('确定要删除这个知识点吗？')) return

  try {
    await api.delete(`/learning-contents/knowledge-points/${pointId}`)
    await loadKnowledgePoints()
  } catch (error) {
    console.error('Failed to delete knowledge point:', error)
    alert('删除失败')
  }
}

// Lifecycle
onMounted(async () => {
  // 先加载所有focus areas（带分类信息）
  await loadAllFocusAreas()

  // 加载分类数据（用于左侧导航）
  try {
    const data = await majorCategoryApi.getAllMajorCategories(SYSTEM_DESIGN_SKILL_ID)
    majorCategories.value = data
  } catch (error) {
    console.error('Failed to load categories:', error)
  }

  // 检查URL查询参数，如果有focusAreaId则自动选择对应的focus area
  const focusAreaIdFromQuery = route.query.focusAreaId

  if (focusAreaIdFromQuery) {
    const targetFocusArea = allFocusAreas.value.find(fa => fa.id == focusAreaIdFromQuery)
    if (targetFocusArea) {
      // 找到对应的focus area，自动选择其分类
      const categoryId = targetFocusArea.categoryIds?.[0]
      if (categoryId) {
        selectedCategoryId.value = categoryId
        // 等待下一个tick确保focusAreas computed已更新
        await nextTick()
        selectFocusArea(targetFocusArea)
        return
      }
    }
  }

  // 默认流程：选择第一个category和第一个focus area
  if (majorCategories.value.length > 0) {
    selectCategory(majorCategories.value[0].id)
  }
})
</script>
