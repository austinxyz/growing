<template>
  <!-- 编程题：三栏布局 -->
  <div v-if="question.questionType === 'programming'" class="w-full h-full">
    <div :class="[
      'grid gap-4 h-full',
      showQuestionDescription ? 'grid-cols-3' : 'grid-cols-2'
    ]" style="height: calc(100vh - 280px)">
      <!-- 左栏：题目描述 (33% - 可隐藏) -->
      <div v-if="showQuestionDescription" class="overflow-y-auto pr-4 border-r border-gray-200">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-900">📝 题目描述</h3>
          <button
            @click="showQuestionDescription = false"
            class="text-sm text-gray-500 hover:text-gray-700"
          >
            ✕ 隐藏
          </button>
        </div>

        <!-- 题目描述 -->
        <div v-if="question.questionDescription" class="mb-6 prose prose-sm max-w-none">
          <div v-html="renderMarkdown(question.questionDescription)"></div>
        </div>

        <!-- 编程题详情 -->
        <div v-if="question.programmingDetails" class="space-y-4">
          <!-- LeetCode按钮 -->
          <div v-if="question.programmingDetails.leetcodeUrl" class="mb-4">
            <a :href="question.programmingDetails.leetcodeUrl" target="_blank"
               class="inline-flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-lg hover:from-orange-600 hover:to-orange-700 transition-all shadow-md hover:shadow-lg">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M13.483 0a1.374 1.374 0 0 0-.961.438L7.116 6.226l-3.854 4.126a5.266 5.266 0 0 0-1.209 2.104 5.35 5.35 0 0 0-.125.513 5.527 5.527 0 0 0 .062 2.362 5.83 5.83 0 0 0 .349 1.017 5.938 5.938 0 0 0 1.271 1.818l4.277 4.193.039.038c2.248 2.165 5.852 2.133 8.063-.074l2.396-2.392c.54-.54.54-1.414.003-1.955a1.378 1.378 0 0 0-1.951-.003l-2.396 2.392a3.021 3.021 0 0 1-4.205.038l-.02-.019-4.276-4.193c-.652-.64-.972-1.469-.948-2.263a2.68 2.68 0 0 1 .066-.523 2.545 2.545 0 0 1 .619-1.164L9.13 8.114c1.058-1.134 3.204-1.27 4.43-.278l3.501 2.831c.593.48 1.461.387 1.94-.207a1.384 1.384 0 0 0-.207-1.943l-3.5-2.831c-.8-.647-1.766-1.045-2.774-1.202l2.015-2.158A1.384 1.384 0 0 0 13.483 0zm-2.866 12.815a1.38 1.38 0 0 0-1.38 1.382 1.38 1.38 0 0 0 1.38 1.382H20.79a1.38 1.38 0 0 0 1.38-1.382 1.38 1.38 0 0 0-1.38-1.382z"/>
              </svg>
              <span class="font-semibold">在LeetCode答题</span>
              <span v-if="question.programmingDetails.leetcodeNumber" class="text-xs bg-white/20 px-2 py-0.5 rounded">
                #{{ question.programmingDetails.leetcodeNumber }}
              </span>
            </a>
          </div>

          <!-- 标签 -->
          <div v-if="question.programmingDetails.tags && question.programmingDetails.tags.length > 0" class="flex items-start gap-2">
            <span class="text-sm font-medium text-gray-700">标签:</span>
            <div class="flex flex-wrap gap-1">
              <span v-for="tag in question.programmingDetails.tags" :key="tag" class="px-2 py-0.5 bg-gray-100 text-gray-700 rounded text-xs">
                {{ tag }}
              </span>
            </div>
          </div>

          <!-- 复杂度 -->
          <div v-if="question.programmingDetails.complexity" class="text-sm">
            <span class="font-medium text-gray-700">时空复杂度:</span>
            <span class="ml-2 text-gray-600">{{ question.programmingDetails.complexity }}</span>
          </div>
        </div>

        <!-- 答题要求 -->
        <div v-if="question.answerRequirement" class="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
          <h4 class="text-sm font-semibold text-blue-900 mb-2">💡 答题要求</h4>
          <div class="text-sm text-blue-800 prose prose-sm max-w-none" v-html="renderMarkdown(question.answerRequirement)"></div>
        </div>

        <!-- Red Flags -->
        <div v-if="question.redFlags && question.redFlags.length > 0" class="mt-6 p-4 bg-red-50 border border-red-200 rounded-lg">
          <h4 class="text-sm font-semibold text-red-900 mb-2">⚠️ Red Flags</h4>
          <ul class="text-sm text-red-800 space-y-1">
            <li v-for="(flag, index) in question.redFlags" :key="index">• {{ flag }}</li>
          </ul>
        </div>
      </div>

      <!-- 中栏：参考答案 (33%) - 可切换AI/旧答案 -->
      <div class="overflow-y-auto px-4 border-r border-gray-200">
        <!-- 切换按钮 + 显示题目描述按钮 -->
        <div class="flex items-center justify-between mb-4">
          <div class="flex gap-2">
            <button
              @click="referenceType = 'ai'"
              :class="[
                'px-3 py-1.5 text-sm rounded-lg transition-colors',
                referenceType === 'ai'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              ]"
            >
              🤖 AI答案
            </button>
            <button
              @click="referenceType = 'old'"
              :class="[
                'px-3 py-1.5 text-sm rounded-lg transition-colors',
                referenceType === 'old'
                  ? 'bg-green-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              ]"
            >
              📝 旧答案
            </button>
          </div>

          <!-- 显示题目描述按钮 -->
          <button
            v-if="!showQuestionDescription"
            @click="showQuestionDescription = true"
            class="px-3 py-1.5 text-sm text-blue-600 hover:text-blue-700"
          >
            👁️ 显示题目
          </button>
        </div>

        <!-- AI参考答案 -->
        <div v-if="referenceType === 'ai'" class="h-full">
          <div v-if="question.aiNote && (question.aiNote.coreStrategy?.trim() || question.aiNote.noteContent?.trim())">
            <!-- AI核心策略优先 -->
            <div v-if="question.aiNote.coreStrategy?.trim()" class="mb-6 p-4 bg-blue-50 border-l-4 border-blue-500 rounded">
              <h4 class="text-sm font-semibold text-blue-900 mb-2">💡 核心策略</h4>
              <p class="text-sm text-blue-800">{{ question.aiNote.coreStrategy }}</p>
            </div>

            <!-- AI详细答案 -->
            <div v-if="question.aiNote.noteContent?.trim()" class="prose prose-sm max-w-none" v-html="renderMarkdown(question.aiNote.noteContent)"></div>
          </div>
          <div v-else class="flex items-center justify-center h-full text-gray-400">
            <div class="text-center">
              <svg class="w-16 h-16 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
              <p class="text-sm">暂无AI参考答案</p>
            </div>
          </div>
        </div>

        <!-- 旧答案 -->
        <div v-else class="h-full">
          <div v-if="question.userNote && (question.userNote.coreStrategy?.trim() || question.userNote.noteContent?.trim())">
            <!-- 旧答案核心策略优先 -->
            <div v-if="question.userNote.coreStrategy?.trim()" class="mb-6 p-4 bg-green-50 border-l-4 border-green-500 rounded">
              <h4 class="text-sm font-semibold text-green-900 mb-2">💡 核心策略</h4>
              <p class="text-sm text-green-800">{{ question.userNote.coreStrategy }}</p>
            </div>

            <!-- 旧答案详细内容 -->
            <div v-if="question.userNote.noteContent?.trim()" class="prose prose-sm max-w-none" v-html="renderMarkdown(question.userNote.noteContent)"></div>
          </div>
          <div v-else class="flex items-center justify-center h-full text-gray-400">
            <div class="text-center">
              <svg class="w-16 h-16 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p class="text-sm">暂无旧答案</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 右栏：当前新答案 (33%) -->
      <div class="overflow-y-auto pl-4">
        <h3 class="text-lg font-semibold text-purple-900 mb-4">✨ 当前答案（可编辑）</h3>

        <!-- 核心策略优先 -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-900 mb-2">
            💡 核心策略 / 关键要点
          </label>
          <textarea
            v-model="currentCoreStrategy"
            placeholder="总结这道题的核心策略或关键要点..."
            rows="5"
            class="w-full px-3 py-2 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none text-sm"
          ></textarea>
        </div>

        <!-- 详细答案编辑器 -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-900 mb-2">
            📝 代码实现 / 详细思路（可选）
          </label>
          <textarea
            v-model="currentAnswer"
            rows="15"
            placeholder="在此输入或修改代码实现或详细思路...\n\n支持 Markdown 格式"
            class="w-full px-4 py-3 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none text-sm font-mono"
          ></textarea>
        </div>

        <!-- 操作按钮 -->
        <div class="flex justify-end gap-3">
          <button
            v-if="question.userNote && question.userNote.noteContent"
            @click="confirmOverwrite"
            :disabled="!currentAnswer.trim()"
            class="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
          >
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
            </svg>
            覆盖保存
          </button>
          <button
            v-else
            @click="confirmOverwrite"
            :disabled="!currentAnswer.trim()"
            class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
          >
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path d="M7.707 10.293a1 1 0 10-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 11.586V6h5a2 2 0 012 2v7a2 2 0 01-2 2H4a2 2 0 01-2-2V8a2 2 0 012-2h5v5.586l-1.293-1.293zM9 4a1 1 0 012 0v2H9V4z" />
            </svg>
            保存答案
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- 非编程题：原有的垂直布局 -->
  <div v-else>
    <!-- 有模板：答题卡对比模式 -->
    <div v-if="template" class="space-y-6">
      <!-- 答题卡对比网格 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-2 gap-4">
        <div
          v-for="(field, index) in template.templateFields"
          :key="field.key"
          :class="[
            'relative rounded-lg shadow-md overflow-hidden',
            index === 0 ? 'bg-gradient-to-br from-blue-50 to-blue-100 border-2 border-blue-300' :
            index === 1 ? 'bg-gradient-to-br from-green-50 to-green-100 border-2 border-green-300' :
            index === 2 ? 'bg-gradient-to-br from-orange-50 to-orange-100 border-2 border-orange-300' :
            'bg-gradient-to-br from-purple-50 to-purple-100 border-2 border-purple-300'
          ]"
        >
          <!-- 卡片头部 -->
          <div :class="[
            'px-3 py-2 flex items-center space-x-2',
            index === 0 ? 'bg-gradient-to-r from-blue-500 to-blue-600' :
            index === 1 ? 'bg-gradient-to-r from-green-500 to-green-600' :
            index === 2 ? 'bg-gradient-to-r from-orange-500 to-orange-600' :
            'bg-gradient-to-r from-purple-500 to-purple-600'
          ]">
            <div class="w-6 h-6 rounded-full flex items-center justify-center font-bold text-sm bg-white/20 backdrop-blur-sm text-white">
              {{ index + 1 }}
            </div>
            <h5 class="text-sm font-semibold text-white">
              {{ field.label }}
              <span v-if="field.required" class="text-yellow-200">*</span>
            </h5>
          </div>

          <!-- 卡片内容：左右对比 -->
          <div class="p-3">
            <div class="grid grid-cols-2 gap-3">
              <!-- 左侧：AI答案 -->
              <div class="bg-white rounded-lg p-2 border border-gray-200">
                <div class="flex items-center gap-1 mb-1">
                  <span class="text-xs font-semibold text-blue-600">🤖 AI答案</span>
                </div>
                <div v-if="aiTemplateAnswers[field.key]" class="prose prose-xs max-w-none text-gray-700 text-[11px]">
                  <div v-html="renderMarkdown(aiTemplateAnswers[field.key])"></div>
                </div>
                <div v-else class="text-xs text-gray-400 italic">暂无AI答案</div>
              </div>

              <!-- 右侧：用户答案（可编辑） -->
              <div class="bg-white rounded-lg p-2 border-2 border-purple-300">
                <div class="flex items-center gap-1 mb-1">
                  <span class="text-xs font-semibold text-purple-600">✨ 你的答案</span>
                </div>
                <textarea
                  v-model="templateAnswers[field.key]"
                  :placeholder="`输入${field.label}...`"
                  rows="8"
                  class="w-full px-2 py-1.5 text-[11px] bg-purple-50 border border-purple-200 rounded focus:outline-none focus:ring-2 focus:ring-purple-500 resize-none leading-relaxed"
                ></textarea>
              </div>
            </div>
            <p v-if="field.hint" class="mt-2 text-xs text-gray-600">💡 {{ field.hint }}</p>
          </div>
        </div>
      </div>

      <!-- 核心策略对比 -->
      <div class="relative rounded-lg shadow-md overflow-hidden bg-gradient-to-br from-indigo-50 to-indigo-100 border-2 border-indigo-300">
        <div class="px-3 py-2 flex items-center space-x-2 bg-gradient-to-r from-indigo-500 to-indigo-600">
          <div class="w-6 h-6 rounded-full flex items-center justify-center font-bold text-sm bg-white/20 backdrop-blur-sm text-white">
            💡
          </div>
          <h5 class="text-sm font-semibold text-white">核心策略对比</h5>
        </div>
        <div class="p-3">
          <div class="grid grid-cols-2 gap-3">
            <!-- AI核心策略 -->
            <div class="bg-white rounded-lg p-2 border border-gray-200">
              <div class="flex items-center gap-1 mb-1">
                <span class="text-xs font-semibold text-blue-600">🤖 AI核心策略</span>
              </div>
              <div v-if="question.aiNote && question.aiNote.coreStrategy" class="prose prose-xs max-w-none text-gray-700 text-[11px]">
                <div v-html="renderMarkdown(question.aiNote.coreStrategy)"></div>
              </div>
              <div v-else class="text-xs text-gray-400 italic">暂无AI核心策略</div>
            </div>

            <!-- 用户核心策略 -->
            <div class="bg-white rounded-lg p-2 border-2 border-purple-300">
              <div class="flex items-center gap-1 mb-1">
                <span class="text-xs font-semibold text-purple-600">✨ 你的核心策略</span>
              </div>
              <textarea
                v-model="currentCoreStrategy"
                placeholder="总结核心策略或关键要点..."
                rows="4"
                class="w-full px-2 py-1.5 text-[11px] bg-purple-50 border border-purple-200 rounded focus:outline-none focus:ring-2 focus:ring-purple-500 resize-none leading-relaxed"
              ></textarea>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="flex justify-end gap-3">
        <button
          v-if="question.userNote && question.userNote.noteContent"
          @click="confirmOverwriteTemplate"
          :disabled="!hasTemplateContent"
          class="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
          </svg>
          覆盖保存
        </button>
        <button
          v-else
          @click="confirmOverwriteTemplate"
          :disabled="!hasTemplateContent"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path d="M7.707 10.293a1 1 0 10-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 11.586V6h5a2 2 0 012 2v7a2 2 0 01-2 2H4a2 2 0 01-2-2V8a2 2 0 012-2h5v5.586l-1.293-1.293zM9 4a1 1 0 012 0v2H9V4z" />
          </svg>
          保存答案
        </button>
      </div>
    </div>

    <!-- 无模板：左右对比布局 -->
    <div v-else class="w-full h-full">
      <div class="grid grid-cols-2 gap-4 h-full" style="height: calc(100vh - 280px)">
        <!-- 左侧：AI参考答案 -->
        <div class="overflow-y-auto pr-4 border-r border-gray-200">
          <h3 class="text-lg font-semibold text-blue-900 mb-4 sticky top-0 bg-gray-50 py-2">🤖 AI 参考答案</h3>
          <div v-if="question.aiNote && question.aiNote.noteContent" class="mb-6">
            <div class="prose prose-sm max-w-none text-[11px]" v-html="renderMarkdown(question.aiNote.noteContent)"></div>

            <!-- AI核心策略 -->
            <div v-if="question.aiNote.coreStrategy" class="mt-6 p-4 bg-blue-50 border-l-4 border-blue-500 rounded">
              <h4 class="text-sm font-semibold text-blue-900 mb-2">💡 核心策略</h4>
              <p class="text-[11px] text-blue-800">{{ question.aiNote.coreStrategy }}</p>
            </div>
          </div>
          <div v-else class="flex items-center justify-center h-full text-gray-400">
            <div class="text-center">
              <svg class="w-16 h-16 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
              <p class="text-sm">暂无AI参考答案</p>
            </div>
          </div>
        </div>

        <!-- 右侧：当前新答案 -->
        <div class="overflow-y-auto pl-4">
          <h3 class="text-lg font-semibold text-purple-900 mb-4 sticky top-0 bg-gray-50 py-2">✨ 当前答案（可编辑）</h3>

          <!-- 答案编辑器 -->
          <textarea
            v-model="currentAnswer"
            rows="20"
            placeholder="在此输入或修改你的答案...\n\n支持 Markdown 格式"
            class="w-full px-4 py-3 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none text-[11px] font-mono mb-4 leading-relaxed"
          ></textarea>

          <!-- 核心策略 -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-900 mb-2">
              💡 核心策略 / 关键要点
            </label>
            <textarea
              v-model="currentCoreStrategy"
              placeholder="总结这道题的核心策略或关键要点..."
              rows="3"
              class="w-full px-3 py-2 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none text-[11px] leading-relaxed"
            ></textarea>
          </div>

          <!-- 操作按钮 -->
          <div class="flex justify-end gap-3">
            <button
              v-if="question.userNote && question.userNote.noteContent"
              @click="confirmOverwrite"
              :disabled="!currentAnswer.trim()"
              class="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
            >
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
              </svg>
              覆盖保存
            </button>
            <button
              v-else
              @click="confirmOverwrite"
              :disabled="!currentAnswer.trim()"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
            >
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path d="M7.707 10.293a1 1 0 10-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 11.586V6h5a2 2 0 012 2v7a2 2 0 01-2 2H4a2 2 0 01-2-2V8a2 2 0 012-2h5v5.586l-1.293-1.293zM9 4a1 1 0 012 0v2H9V4z" />
              </svg>
              保存答案
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { marked } from 'marked'

const props = defineProps({
  question: {
    type: Object,
    required: true
  },
  currentAnswer: {
    type: String,
    default: ''
  },
  template: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['overwrite'])

// 当前编辑的答案
const currentAnswer = ref('')
const currentCoreStrategy = ref('')

// 模板答案（用于对比）
const templateAnswers = ref({})
const aiTemplateAnswers = ref({})

// UI控制
const referenceType = ref('ai') // 'ai' | 'old'
const showQuestionDescription = ref(true)

// Markdown渲染
const renderMarkdown = (content) => {
  if (!content) return ''
  return marked(content)
}

// 确认覆盖（无模板）
const confirmOverwrite = () => {
  if (!currentAnswer.value.trim()) return

  if (props.question.userNote && props.question.userNote.noteContent) {
    if (confirm('确定要覆盖之前的答案吗？此操作不可撤销。')) {
      emit('overwrite', {
        content: currentAnswer.value,
        coreStrategy: currentCoreStrategy.value
      })
    }
  } else {
    emit('overwrite', {
      content: currentAnswer.value,
      coreStrategy: currentCoreStrategy.value
    })
  }
}

// 确认覆盖（模板）
const confirmOverwriteTemplate = () => {
  if (!hasTemplateContent.value) return

  // 将模板答案转换为Markdown格式
  let content = ''
  props.template.templateFields.forEach(field => {
    if (templateAnswers.value[field.key]) {
      content += `## ${field.label}\n\n${templateAnswers.value[field.key]}\n\n`
    }
  })

  if (props.question.userNote && props.question.userNote.noteContent) {
    if (confirm('确定要覆盖之前的答案吗？此操作不可撤销。')) {
      emit('overwrite', {
        content: content,
        coreStrategy: currentCoreStrategy.value
      })
    }
  } else {
    emit('overwrite', {
      content: content,
      coreStrategy: currentCoreStrategy.value
    })
  }
}

// 检查模板是否有内容
const hasTemplateContent = computed(() => {
  if (!props.template) return false
  return Object.values(templateAnswers.value).some(v => v && v.trim())
})

// 解析Markdown格式的答案到模板字段
const parseAnswerToTemplate = (content, template) => {
  if (!content || !template || !template.templateFields) return {}

  const answers = {}
  template.templateFields.forEach(field => {
    // 匹配 ## 字段名，支持带括号的说明如 "## Situation (情境)"
    // 模式1: 精确匹配 "## Situation"
    // 模式2: 带括号 "## Situation (情境)" 或 "## Situation（情境）"
    const escapedLabel = field.label.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    const regex = new RegExp(`## ${escapedLabel}(?:\\s*[\\(（][^\\)）]*[\\)）])?\\s*\\n\\n([\\s\\S]*?)(?=\\n\\n##|$)`, 'i')
    const match = content.match(regex)
    if (match) {
      answers[field.key] = match[1].trim()
    } else {
      answers[field.key] = ''
    }
  })
  return answers
}

// 监听试题变化，加载AI答案（用于对比）
watch(() => props.question, (newQuestion) => {
  // 批改模式：不自动加载userNote，等待父组件传入新答案
  // 只重置核心策略（新答案的核心策略由用户输入）
  if (!props.currentAnswer) {
    currentAnswer.value = ''
    currentCoreStrategy.value = ''
  }

  // 解析AI答案到模板字段
  if (props.template && newQuestion?.aiNote?.noteContent) {
    aiTemplateAnswers.value = parseAnswerToTemplate(newQuestion.aiNote.noteContent, props.template)
  }
}, { immediate: true })

// 监听外部传入的答案
watch(() => props.currentAnswer, (newAnswer) => {
  if (newAnswer) {
    currentAnswer.value = newAnswer

    // 如果有模板，解析答案到模板字段
    if (props.template) {
      templateAnswers.value = parseAnswerToTemplate(newAnswer, props.template)
    }
  }
}, { immediate: true })

// 监听模板变化
watch(() => props.template, (newTemplate) => {
  if (newTemplate) {
    // 重新解析当前答案和AI答案
    if (currentAnswer.value) {
      templateAnswers.value = parseAnswerToTemplate(currentAnswer.value, newTemplate)
    }
    if (props.question?.aiNote?.noteContent) {
      aiTemplateAnswers.value = parseAnswerToTemplate(props.question.aiNote.noteContent, newTemplate)
    }
  }
}, { immediate: true })
</script>
