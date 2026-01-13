<template>
  <div class="h-screen flex flex-col bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 顶部标题栏 - 添加渐变背景 -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg">
      <div class="px-6 py-4">
        <h1 class="text-2xl font-bold text-white">算法学习</h1>
        <p class="text-xs text-blue-100 mt-1">浏览算法与数据结构学习内容</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：大分类 + Focus Area树 - 增强样式 -->
      <div class="w-64 bg-gradient-to-b from-gray-50 to-white border-r border-gray-200 flex flex-col shadow-lg">
        <!-- 大分类Tab - 增强样式 -->
        <div class="border-b-2 border-gray-200 bg-white">
          <nav class="flex flex-col space-y-1 p-3" aria-label="Categories">
            <button
              v-for="category in majorCategories"
              :key="category.id"
              @click="selectCategory(category.id)"
              :class="[
                'px-4 py-2.5 text-sm font-semibold rounded-lg text-left transition-all duration-200 relative',
                selectedCategoryId === category.id
                  ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-lg'
                  : 'text-gray-700 hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 hover:shadow-md hover:text-blue-700'
              ]"
            >
              <!-- 选中指示器 -->
              <div v-if="selectedCategoryId === category.id" class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-white rounded-r-full"></div>
              {{ category.name }}
            </button>
          </nav>
        </div>

        <!-- Focus Area列表 - 增强样式 -->
        <div class="flex-1 overflow-y-auto p-3">
          <div v-if="loading.focusAreas" class="text-center text-gray-500 py-8">
            <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto mb-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            加载中...
          </div>

          <div v-else-if="filteredFocusAreas.length === 0" class="text-center text-gray-400 py-8 bg-gradient-to-br from-gray-50 to-white rounded-lg border-2 border-gray-200 mx-2">
            <svg class="mx-auto h-10 w-10 text-gray-300 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
            </svg>
            <p class="text-xs font-medium">该分类下暂无学习主题</p>
          </div>

          <div v-else class="space-y-2">
            <button
              v-for="fa in filteredFocusAreas"
              :key="fa.id"
              @click="selectFocusArea(fa)"
              :class="[
                'w-full text-left px-3 py-2.5 rounded-lg transition-all duration-200 group relative',
                selectedFocusArea?.id === fa.id
                  ? 'bg-gradient-to-r from-blue-50 to-purple-50 text-blue-700 font-semibold shadow-md border-2 border-blue-200'
                  : 'text-gray-700 hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-md hover:text-blue-700 border-2 border-transparent'
              ]"
            >
              <!-- 装饰性accent bar -->
              <div :class="[
                'absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 rounded-r-full transition-all',
                selectedFocusArea?.id === fa.id
                  ? 'bg-gradient-to-b from-blue-500 to-purple-500'
                  : 'bg-gray-300 group-hover:bg-gradient-to-b group-hover:from-blue-400 group-hover:to-purple-400'
              ]"></div>
              <span class="pl-2 text-sm">{{ fa.name }}</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧：学习阶段Tab + 内容列表 -->
      <div class="flex-1 overflow-y-auto bg-gray-50">
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
          <!-- Focus Area标题 - 增强样式 -->
          <div class="mb-4 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-xl shadow-md p-4">
            <h2 class="text-xl font-bold text-white">{{ selectedFocusArea.name }}</h2>
            <p v-if="selectedFocusArea.categoryName" class="text-sm text-indigo-100 mt-1">
              {{ selectedFocusArea.categoryName }}
            </p>
          </div>

          <!-- 学习阶段Tab - 增强样式 -->
          <div class="bg-white rounded-xl shadow-lg mb-4">
              <div class="border-b border-gray-200">
                <div class="flex overflow-x-auto">
                  <button
                    v-for="stage in learningStages"
                    :key="stage.id"
                    @click="selectStage(stage.id)"
                    :class="[
                      'px-4 py-3 text-sm font-semibold border-b-2 transition-all duration-200 whitespace-nowrap',
                      selectedStageId === stage.id
                        ? 'border-blue-600 text-blue-600 bg-blue-50'
                        : 'border-transparent text-gray-500 hover:text-blue-600 hover:bg-blue-50'
                    ]"
                  >
                    {{ stage.stageName }}
                    <span class="ml-2 text-xs font-medium" :class="selectedStageId === stage.id ? 'text-blue-500' : 'text-gray-400'">
                      ({{ stage.contents ? stage.contents.length : 0 }})
                    </span>
                  </button>
                  <!-- 试题总结Tab -->
                  <button
                    @click="selectStage('questions-summary')"
                    :class="[
                      'px-4 py-3 text-sm font-semibold border-b-2 transition-all duration-200 whitespace-nowrap',
                      selectedStageId === 'questions-summary'
                        ? 'border-purple-600 text-purple-600 bg-purple-50'
                        : 'border-transparent text-gray-500 hover:text-purple-600 hover:bg-purple-50'
                    ]"
                  >
                    📋 试题总结
                    <span class="ml-2 text-xs font-medium" :class="selectedStageId === 'questions-summary' ? 'text-purple-500' : 'text-gray-400'">
                      ({{ questionCount }})
                    </span>
                  </button>
                  <!-- 试题库Tab -->
                  <button
                    @click="selectStage('questions-detail')"
                    :class="[
                      'px-4 py-3 text-sm font-semibold border-b-2 transition-all duration-200 whitespace-nowrap',
                      selectedStageId === 'questions-detail'
                        ? 'border-green-600 text-green-600 bg-green-50'
                        : 'border-transparent text-gray-500 hover:text-green-600 hover:bg-green-50'
                    ]"
                  >
                    📚 试题库
                    <span class="ml-2 text-xs font-medium" :class="selectedStageId === 'questions-detail' ? 'text-green-500' : 'text-gray-400'">
                      ({{ questionCount }})
                    </span>
                  </button>
                </div>
              </div>

              <!-- 阶段内容区域 -->
              <div class="p-3">
                <!-- Loading状态 -->
                <div v-if="loading.contents" class="text-center text-gray-500 py-12">
                  <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <p class="mt-2 text-sm">加载中...</p>
                </div>

                <!-- 学习内容列表 -->
                <div v-else-if="!isQuestionTab && currentStageContents.length === 0" class="text-center text-gray-400 py-12">
                  该阶段暂无学习内容
                </div>

                <!-- 学习内容:左右分栏布局 -->
                <div v-else-if="!isQuestionTab" class="flex h-[calc(100vh-240px)]">
                  <!-- 左侧:资料列表 - 增强样式 -->
                  <div class="w-56 border-r border-gray-200 overflow-y-auto bg-gradient-to-b from-gray-50 to-white shadow-inner">
                    <div
                      v-for="(content, index) in currentStageContents"
                      :key="content.id"
                      @click="selectContentItem(content)"
                      :class="[
                        'p-3 m-2 border-b border-gray-100 cursor-pointer transition-all duration-200 rounded-lg',
                        selectedContentItem?.id === content.id
                          ? 'bg-gradient-to-r from-blue-50 to-purple-50 border-l-4 border-l-blue-600 shadow-md border-2 border-blue-200'
                          : 'bg-white hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-md hover:border-blue-100 border border-transparent'
                      ]"
                    >
                      <div class="flex items-center gap-2">
                        <div :class="[
                          'w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 shadow-sm',
                          selectedContentItem?.id === content.id
                            ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white'
                            : 'bg-gradient-to-r from-gray-200 to-gray-300 text-gray-700'
                        ]">
                          {{ index + 1 }}
                        </div>
                        <h3 :class="[
                          'text-sm font-medium flex-1',
                          selectedContentItem?.id === content.id
                            ? 'text-blue-700'
                            : 'text-gray-900'
                        ]">
                          {{ content.title }}
                        </h3>
                      </div>
                    </div>
                  </div>

                  <!-- 右侧：资料详情 -->
                  <div class="flex-1 overflow-y-auto bg-gray-50">
                    <!-- 未选中任何资料 -->
                    <div v-if="!selectedContentItem" class="flex items-center justify-center h-full">
                      <div class="text-center text-gray-400">
                        <svg class="mx-auto h-12 w-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                        </svg>
                        <p class="mt-4 text-sm">请从左侧选择一个学习资料查看详情</p>
                      </div>
                    </div>

                    <!-- 资料详情 -->
                    <div v-else class="h-full flex flex-col">
                      <!-- 不支持iframe:显示跳转链接 -->
                      <div v-if="selectedContentItem.url && selectedContentItem.supportsIframe === false" class="flex items-center justify-center h-full bg-gradient-to-br from-blue-50 to-white p-8">
                        <div class="text-center">
                          <svg class="w-16 h-16 text-blue-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
                          </svg>
                          <h3 class="text-lg font-bold text-gray-900 mb-2">{{ selectedContentItem.title }}</h3>
                          <p class="text-sm text-gray-600 mb-6">此资源需要在新标签页中打开</p>
                          <a
                            :href="selectedContentItem.url"
                            target="_blank"
                            rel="noopener noreferrer"
                            class="inline-flex items-center gap-2 px-5 py-3 bg-gradient-to-r from-blue-500 to-purple-500 text-white text-sm font-semibold rounded-lg hover:from-blue-600 hover:to-purple-600 hover:shadow-xl transition-all shadow-md"
                          >
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                            </svg>
                            在新标签页打开
                          </a>
                        </div>
                      </div>

                      <!-- 支持iframe:直接全屏显示 -->
                      <div v-if="selectedContentItem.url && selectedContentItem.supportsIframe === true" class="flex-1 overflow-hidden">
                        <iframe
                          :src="selectedContentItem.url"
                          class="w-full h-full border-0"
                          frameborder="0"
                          sandbox="allow-same-origin allow-scripts allow-popups allow-forms"
                        ></iframe>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 试题总结:紧凑列表显示 -->
                <div v-else-if="selectedStageId === 'questions-summary'">
                  <div v-if="questions.length === 0" class="text-center text-gray-400 py-12 bg-gradient-to-br from-gray-50 to-white rounded-xl border-2 border-gray-200 shadow-md">
                    <svg class="mx-auto h-12 w-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <p class="mt-3 text-sm font-medium">该Focus Area暂无试题</p>
                  </div>

                  <div v-else class="space-y-2">
                    <div
                      v-for="question in questions"
                      :key="question.id"
                      @click="openQuestionModal(question)"
                      class="p-3 bg-gradient-to-br from-white to-gray-50 hover:from-blue-50 hover:to-purple-50 rounded-xl transition-all cursor-pointer group shadow-sm hover:shadow-lg border-2 border-transparent hover:border-blue-200"
                    >
                      <!-- 紧凑显示:难度+题目 | 核心思路 -->
                      <div class="flex items-start gap-3">
                        <!-- 左侧:难度 + 重要标识 + 题目标题 -->
                        <div class="flex items-start gap-2 w-64 flex-shrink-0">
                          <!-- 难度徽章 - 渐变样式 -->
                          <span :class="[
                            'flex-shrink-0 px-2 py-0.5 rounded-full text-xs font-semibold shadow-sm',
                            question.difficulty === 'EASY' ? 'bg-gradient-to-r from-green-400 to-emerald-500 text-white' :
                            question.difficulty === 'MEDIUM' ? 'bg-gradient-to-r from-yellow-400 to-orange-500 text-white' :
                            'bg-gradient-to-r from-red-400 to-pink-500 text-white'
                          ]" :title="question.difficulty">
                            {{ question.difficulty === 'EASY' ? '简单' : question.difficulty === 'MEDIUM' ? '中等' : '困难' }}
                          </span>

                          <!-- 重要标识 -->
                          <span v-if="question.programmingDetails?.isImportant" class="text-orange-500 text-base flex-shrink-0" title="重要题目">
                            ⭐
                          </span>

                          <!-- 题目标题(作为LeetCode链接,文字超出省略) -->
                          <a
                            v-if="question.programmingDetails?.leetcodeUrl"
                            :href="question.programmingDetails.leetcodeUrl"
                            @click.stop
                            target="_blank"
                            rel="noopener noreferrer"
                            class="text-sm font-semibold text-blue-600 hover:text-blue-800 hover:underline flex-1 truncate group-hover:text-blue-700"
                            :title="question.title"
                          >
                            {{ question.title }}
                          </a>
                          <h3 v-else class="text-sm font-semibold text-gray-900 flex-1 truncate group-hover:text-blue-700" :title="question.title">
                            {{ question.title }}
                          </h3>
                        </div>

                        <!-- 右侧:核心思路(完整显示,自动换行) -->
                        <p class="text-xs text-gray-600 flex-1 leading-relaxed group-hover:text-gray-800">
                          {{ question.note?.coreStrategy || '暂无核心思路' }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 试题库：左右分栏详细显示 -->
                <div v-else-if="selectedStageId === 'questions-detail'" class="flex h-[calc(100vh-240px)]">
                  <!-- 左侧：试题列表（紧凑模式） - 增强样式 -->
                  <div class="w-80 border-r border-gray-200 overflow-y-auto bg-gray-50">
                    <!-- 添加算法题按钮 - 增强样式 -->
                    <div class="sticky top-0 bg-gradient-to-r from-green-50 to-emerald-50 border-b-2 border-green-300 p-3 z-10 shadow-sm">
                      <button
                        @click="showAddQuestionModal"
                        class="w-full px-4 py-2.5 text-sm font-semibold text-white bg-gradient-to-r from-green-500 to-emerald-500 rounded-lg hover:from-green-600 hover:to-emerald-600 hover:shadow-lg transition-all flex items-center justify-center gap-2 shadow-md"
                      >
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                        </svg>
                        添加算法题
                      </button>
                    </div>

                    <div v-if="questions.length === 0" class="text-center text-gray-400 py-12">
                      该Focus Area暂无试题
                    </div>

                    <div
                      v-for="question in questions"
                      :key="question.id"
                      @click="selectQuestionForDetail(question)"
                      :class="[
                        'p-3 m-2 rounded-lg cursor-pointer transition-all duration-200 border border-transparent',
                        selectedQuestionForDetail?.id === question.id
                          ? 'bg-gradient-to-r from-blue-50 to-purple-50 border-l-4 border-l-blue-600 shadow-md'
                          : 'bg-white hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-sm hover:border-blue-200'
                      ]"
                    >
                      <div class="flex items-center gap-2">
                        <!-- 难度图标 -->
                        <span :class="getDifficultyIconClass(question.difficulty)" class="flex-shrink-0" :title="question.difficulty">
                          {{ getDifficultyIcon(question.difficulty) }}
                        </span>

                        <!-- 重要标识 -->
                        <span v-if="question.programmingDetails?.isImportant" class="text-orange-500 text-sm flex-shrink-0" title="重要题目">
                          ⭐
                        </span>

                        <!-- 题目标题（单行显示，超出省略） -->
                        <h3 :class="[
                          'text-sm font-medium truncate',
                          selectedQuestionForDetail?.id === question.id
                            ? 'text-blue-700'
                            : 'text-gray-900'
                        ]" :title="question.title">
                          {{ question.title }}
                        </h3>
                      </div>
                    </div>
                  </div>

                  <!-- 右侧：试题详情 -->
                  <div class="flex-1 overflow-y-auto bg-gray-50">
                    <!-- 未选中任何试题 -->
                    <div v-if="!selectedQuestionForDetail" class="flex items-center justify-center h-full">
                      <div class="text-center text-gray-400">
                        <svg class="mx-auto h-12 w-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                        <p class="mt-4 text-sm">请从左侧选择一道试题查看详情</p>
                      </div>
                    </div>

                    <!-- 试题详情（内联显示） -->
                    <div v-else class="p-4">
                      <!-- 操作按钮（仅针对用户自己创建的试题） - 增强样式 -->
                      <div v-if="selectedQuestionForDetail && !selectedQuestionForDetail.isOfficial" class="mb-4 flex justify-end gap-3">
                        <button
                          @click="editQuestion(selectedQuestionForDetail)"
                          class="px-4 py-2 text-sm font-semibold text-white bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg hover:from-blue-600 hover:to-blue-700 hover:shadow-md transition-all shadow-sm"
                        >
                          ✏️ 编辑
                        </button>
                        <button
                          @click="deleteQuestion(selectedQuestionForDetail.id)"
                          class="px-4 py-2 text-sm font-semibold text-white bg-gradient-to-r from-red-500 to-pink-500 rounded-lg hover:from-red-600 hover:to-pink-600 hover:shadow-md transition-all shadow-sm"
                        >
                          🗑️ 删除
                        </button>
                      </div>

                      <QuestionDetailPanel
                        :question="selectedQuestionForDetail"
                        :focus-area-id="selectedFocusArea?.id"
                        :focus-area-name="selectedFocusArea?.name"
                        @note-saved="handleNoteSavedInline"
                        @open-modal="openQuestionModalFromPanel"
                      />
                    </div>
                  </div>
                </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 试题详情Modal -->
    <QuestionDetailModal
      v-if="showQuestionModal"
      :question="selectedQuestion"
      :focus-area-id="selectedFocusArea?.id"
      :focus-area-name="selectedFocusArea?.name"
      @close="closeQuestionModal"
      @note-saved="handleNoteSaved"
    />

    <!-- 算法题编辑Modal -->
    <AlgorithmQuestionEditModal
      :is-open="showEditModal"
      :question="editingQuestion"
      :focus-areas="allFocusAreas"
      :current-focus-area-id="selectedFocusArea?.id"
      :current-focus-area-name="selectedFocusArea?.name"
      @save="saveQuestion"
      @cancel="closeEditModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import majorCategoryApi from '@/api/majorCategoryApi'
import learningContentApi from '@/api/learningContentApi'
import { questionApi, adminQuestionApi } from '@/api/questionApi'
import QuestionDetailModal from '@/components/questions/QuestionDetailModal.vue'
import QuestionDetailPanel from '@/components/questions/QuestionDetailPanel.vue'
import AlgorithmQuestionEditModal from '@/components/questions/AlgorithmQuestionEditModal.vue'
import { useAuth } from '@/composables/useAuth'

const { isAdmin } = useAuth()

// 算法与数据结构的skillId（根据实际数据调整）
const ALGORITHM_SKILL_ID = 1

// State
const loading = ref({
  focusAreas: false,
  contents: false
})

const majorCategories = ref([])
const selectedCategoryId = ref(null)
const allFocusAreas = ref([])
const selectedFocusArea = ref(null)
const learningData = ref(null)
const selectedStageId = ref(null)
const questions = ref([])
const showQuestionModal = ref(false)
const selectedQuestion = ref(null)
const selectedContentItem = ref(null)
const selectedQuestionForDetail = ref(null)
const showEditModal = ref(false)
const editingQuestion = ref(null)

// Computed
const filteredFocusAreas = computed(() => {
  if (!selectedCategoryId.value) return allFocusAreas.value
  return allFocusAreas.value.filter(fa =>
    fa.categoryIds && fa.categoryIds.includes(selectedCategoryId.value)
  )
})

const learningStages = computed(() => {
  return learningData.value?.stages || []
})

const currentStageContents = computed(() => {
  if (!selectedStageId.value || isQuestionTab.value) return []
  const stage = learningStages.value.find(s => s.id === selectedStageId.value)
  return stage?.contents || []
})

const questionCount = computed(() => questions.value.length)

const isQuestionTab = computed(() => {
  return selectedStageId.value === 'questions-summary' || selectedStageId.value === 'questions-detail'
})

// Methods
const loadMajorCategories = async () => {
  try {
    // 只加载算法与数据结构skill的分类（skillId=1）
    const data = await majorCategoryApi.getAllMajorCategories(ALGORITHM_SKILL_ID)
    majorCategories.value = data || []

    // 默认选中第一个分类
    if (majorCategories.value.length > 0) {
      selectedCategoryId.value = majorCategories.value[0].id
    }
  } catch (error) {
    console.error('加载大分类失败:', error)
    alert('加载大分类失败: ' + (error.message || '未知错误'))
  }
}

const loadFocusAreas = async () => {
  loading.value.focusAreas = true
  try {
    const data = await majorCategoryApi.getFocusAreasWithCategories(ALGORITHM_SKILL_ID)
    allFocusAreas.value = data || []
  } catch (error) {
    console.error('加载Focus Areas失败:', error)
    alert('加载学习主题失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value.focusAreas = false
  }
}

const selectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  // 清空选中的Focus Area
  selectedFocusArea.value = null
  learningData.value = null
  selectedStageId.value = null
  questions.value = []
}

const selectFocusArea = async (focusArea) => {
  selectedFocusArea.value = focusArea
  selectedContentItem.value = null // 清空选中的学习资料

  // 保存当前选中的tab
  const previousStageId = selectedStageId.value

  await loadLearningContents()
  await loadQuestions()

  // 如果之前有选中的tab，尝试保持在相同的tab
  if (previousStageId) {
    // 检查新Focus Area是否有相同的tab
    const hasSameStage = learningStages.value.some(s => s.id === previousStageId) || previousStageId === 'questions-summary' || previousStageId === 'questions-detail'
    if (hasSameStage) {
      selectedStageId.value = previousStageId
    } else {
      // 如果没有相同的tab，默认选中第一个
      if (learningStages.value.length > 0) {
        selectedStageId.value = learningStages.value[0].id
      }
    }
  } else {
    // 首次选择Focus Area，默认选中第一个学习阶段
    if (learningStages.value.length > 0) {
      selectedStageId.value = learningStages.value[0].id
    }
  }
}

const loadLearningContents = async () => {
  if (!selectedFocusArea.value) return

  loading.value.contents = true
  try {
    const data = await learningContentApi.getContentsByFocusArea(selectedFocusArea.value.id)
    learningData.value = data || { stages: [] }
  } catch (error) {
    console.error('加载学习内容失败:', error)
    alert('加载学习内容失败: ' + (error.message || '未知错误'))
    learningData.value = { stages: [] }
  } finally {
    loading.value.contents = false
  }
}

const loadQuestions = async () => {
  if (!selectedFocusArea.value) return

  try {
    const data = await questionApi.getQuestionsByFocusArea(selectedFocusArea.value.id)
    // 排序逻辑：先按isImportant降序（重要的在前），再按leetcodeNumber升序
    const sortedData = (data || []).sort((a, b) => {
      // 1. 先比较isImportant（重要的在前）
      const aImportant = a.programmingDetails?.isImportant || false
      const bImportant = b.programmingDetails?.isImportant || false
      if (aImportant !== bImportant) {
        return bImportant ? 1 : -1 // true在前
      }

      // 2. 再比较leetcodeNumber（题号小的在前）
      const aNumber = a.programmingDetails?.leetcodeNumber || Number.MAX_SAFE_INTEGER
      const bNumber = b.programmingDetails?.leetcodeNumber || Number.MAX_SAFE_INTEGER
      return aNumber - bNumber
    })

    questions.value = sortedData
  } catch (error) {
    console.error('加载试题失败:', error)
    questions.value = []
  }
}

const selectStage = (stageId) => {
  selectedStageId.value = stageId
  selectedContentItem.value = null // 切换tab时清空选中的学习资料
  selectedQuestionForDetail.value = null // 切换tab时清空选中的试题
}

const getDifficultyClass = (difficulty) => {
  const classes = {
    EASY: 'px-2 py-1 text-xs rounded-full bg-green-100 text-green-700',
    MEDIUM: 'px-2 py-1 text-xs rounded-full bg-yellow-100 text-yellow-700',
    HARD: 'px-2 py-1 text-xs rounded-full bg-red-100 text-red-700'
  }
  return classes[difficulty] || 'px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-700'
}

const getDifficultyIcon = (difficulty) => {
  const icons = {
    EASY: '●',
    MEDIUM: '●',
    HARD: '●'
  }
  return icons[difficulty] || '●'
}

const getDifficultyIconClass = (difficulty) => {
  const classes = {
    EASY: 'text-green-500 text-sm',
    MEDIUM: 'text-yellow-500 text-sm',
    HARD: 'text-red-500 text-sm'
  }
  return classes[difficulty] || 'text-gray-500 text-sm'
}

const openQuestionModal = (question) => {
  selectedQuestion.value = question
  showQuestionModal.value = true
}

const closeQuestionModal = () => {
  showQuestionModal.value = false
  selectedQuestion.value = null
}

const handleNoteSaved = () => {
  // 笔记保存成功后的处理（如果需要）
}

const selectContentItem = (content) => {
  selectedContentItem.value = content
}

const selectQuestionForDetail = (question) => {
  selectedQuestionForDetail.value = question
}

const handleNoteSavedInline = async () => {
  // 笔记保存成功后，重新加载试题数据以更新笔记状态
  await loadQuestions()
  // 更新当前选中的试题数据
  if (selectedQuestionForDetail.value) {
    const updated = questions.value.find(q => q.id === selectedQuestionForDetail.value.id)
    if (updated) {
      selectedQuestionForDetail.value = updated
    }
  }
}

const openQuestionModalFromPanel = () => {
  // 从面板打开弹窗，显示完整笔记
  if (selectedQuestionForDetail.value) {
    selectedQuestion.value = selectedQuestionForDetail.value
    showQuestionModal.value = true
  }
}

// 算法题编辑相关方法
const showAddQuestionModal = () => {
  editingQuestion.value = null
  showEditModal.value = true
}

const editQuestion = (question) => {
  editingQuestion.value = question
  showEditModal.value = true
}

const saveQuestion = async (formData) => {
  try {
    // 确定使用哪个API（管理员使用adminQuestionApi，用户使用questionApi）
    const api = isAdmin.value ? adminQuestionApi : questionApi

    if (editingQuestion.value) {
      // 更新现有试题
      if (isAdmin.value) {
        // 管理员使用updateQuestionWithDetails（支持编程题详情）
        await adminQuestionApi.updateQuestionWithDetails(editingQuestion.value.id, formData)
      } else {
        // 普通用户使用updateQuestion
        await questionApi.updateQuestion(editingQuestion.value.id, formData)
      }
      alert('试题更新成功')
    } else {
      // 新建试题
      const data = {
        ...formData,
        focusAreaId: formData.focusAreaId || selectedFocusArea.value?.id,
        questionType: 'programming' // 算法题固定为programming类型
      }

      if (isAdmin.value) {
        // 管理员使用createQuestionWithDetails（支持编程题详情）
        await adminQuestionApi.createQuestionWithDetails(data)
      } else {
        // 普通用户使用createQuestion
        await questionApi.createQuestion(data)
      }
      alert('试题创建成功')
    }

    closeEditModal()
    await loadQuestions() // 重新加载试题列表
  } catch (error) {
    console.error('Failed to save question:', error)
    alert(error.response?.data?.message || '保存失败')
  }
}

const deleteQuestion = async (id) => {
  if (!confirm('确定要删除这道试题吗？')) return

  try {
    // 确定使用哪个API
    const api = isAdmin.value ? adminQuestionApi : questionApi
    await api.deleteQuestion(id)
    alert('删除成功')

    // 如果删除的是当前选中的试题，清空选中状态
    if (selectedQuestionForDetail.value?.id === id) {
      selectedQuestionForDetail.value = null
    }

    await loadQuestions() // 重新加载试题列表
  } catch (error) {
    console.error('Failed to delete question:', error)
    alert(error.response?.data?.message || '删除失败')
  }
}

const closeEditModal = () => {
  showEditModal.value = false
  editingQuestion.value = null
}

// Initialize
onMounted(async () => {
  await loadMajorCategories()
  await loadFocusAreas()
})
</script>
