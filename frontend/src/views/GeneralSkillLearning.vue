<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 顶部标题 -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg">
      <div class="px-6 py-4">
        <h1 class="text-2xl font-bold text-white">通用技能学习</h1>
        <p class="text-xs text-blue-100 mt-1">浏览学习资料和试题库</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex h-[calc(100vh-9rem)] relative">
      <!-- 左侧面板 (25%) - 上下两栏树形结构 -->
      <div
        :class="[
          'bg-white border-r border-gray-200 overflow-hidden flex flex-col transition-all duration-300',
          isSidebarCollapsed ? 'w-0' : 'w-1/4'
        ]"
      >

        <!-- 上栏树：职业路径 → 技能 (40% height) -->
        <div class="h-2/5 border-b-2 border-gray-200 overflow-hidden flex flex-col">
          <div class="p-3 bg-gradient-to-r from-indigo-500 to-purple-500 shadow-md">
            <h3 class="text-xs font-semibold text-white uppercase tracking-wider">📚 职业路径与技能</h3>
          </div>

          <div class="flex-1 overflow-y-auto p-2">
            <!-- 加载中 -->
            <div v-if="loadingCareerPaths" class="text-center py-8">
              <svg class="animate-spin h-6 w-6 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <p class="mt-2 text-xs text-gray-500">加载中...</p>
            </div>

            <!-- 职业路径树 -->
            <div v-else-if="careerPaths.length > 0" class="space-y-1">
              <div v-for="careerPath in careerPaths" :key="careerPath.id">
                <!-- 职业路径节点 -->
                <div class="flex items-center px-2 py-1.5 rounded hover:bg-gray-100 transition-colors group">
                  <!-- 展开/折叠图标 -->
                  <svg
                    @click="toggleCareerPath(careerPath.id)"
                    class="w-4 h-4 mr-1 text-gray-500 flex-shrink-0 transition-transform cursor-pointer"
                    :class="{ 'rotate-90': expandedCareerPaths.has(careerPath.id) }"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                  <!-- 职业路径名称 -->
                  <span
                    @click="toggleCareerPath(careerPath.id)"
                    class="text-sm font-medium text-gray-700 truncate flex-1 cursor-pointer"
                  >
                    {{ careerPath.icon }} {{ careerPath.name }}
                  </span>
                </div>

                <!-- 技能列表 (子节点) -->
                <div v-if="expandedCareerPaths.has(careerPath.id)" class="ml-5 mt-1 space-y-1">
                  <div
                    v-for="skill in careerPath.skills"
                    :key="skill.id"
                    @click.stop="selectSkill(skill.id)"
                    :class="[
                      'flex items-center px-2 py-1.5 rounded cursor-pointer transition-all duration-200',
                      selectedSkillId === skill.id
                        ? 'bg-gradient-to-r from-blue-50 to-purple-50 border-l-4 border-l-blue-600 shadow-md text-blue-700 font-medium'
                        : 'text-gray-600 hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-sm'
                    ]"
                  >
                    <!-- 技能图标 -->
                    <svg class="w-3.5 h-3.5 mr-1.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <!-- 技能名称 -->
                    <span class="text-sm truncate">{{ skill.name }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 无职业路径 -->
            <div v-else class="text-center text-gray-400 py-8">
              <p class="text-xs">暂无职业路径和技能</p>
            </div>
          </div>
        </div>

        <!-- 下栏树：大分类 → Focus Area (60% height) -->
        <div class="h-3/5 overflow-hidden flex flex-col">
          <div class="p-3 bg-gradient-to-r from-blue-500 to-indigo-500 shadow-md">
            <h3 class="text-xs font-semibold text-white uppercase tracking-wider">🎯 Focus Area</h3>
          </div>

          <!-- 未选择技能时的提示 -->
          <div v-if="!selectedSkillId" class="flex-1 flex items-center justify-center text-gray-400">
            <div class="text-center p-4">
              <p class="text-xs">请先从上方选择一个技能</p>
            </div>
          </div>

          <!-- 加载中 -->
          <div v-else-if="loadingCategories" class="flex-1 flex items-center justify-center">
            <div class="text-center">
              <svg class="animate-spin h-6 w-6 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <p class="mt-2 text-xs text-gray-500">加载中...</p>
            </div>
          </div>

          <!-- 分类树 -->
          <div v-else class="flex-1 overflow-y-auto p-2">
            <!-- 第二类技能: 只有General大分类,直接显示Focus Area -->
            <template v-if="isSecondTypeSkill">
              <div class="space-y-1">
                <div
                  v-for="focusArea in focusAreas"
                  :key="focusArea.id"
                  @click="selectFocusArea(focusArea.id)"
                  :class="[
                    'flex items-center px-2 py-1.5 rounded cursor-pointer transition-all duration-200',
                    selectedFocusAreaId === focusArea.id
                      ? 'bg-gradient-to-r from-blue-50 to-indigo-50 border-l-4 border-l-indigo-600 shadow-md text-indigo-700 font-medium'
                      : 'text-gray-600 hover:bg-gradient-to-r hover:from-gray-50 hover:to-indigo-50 hover:shadow-sm'
                  ]"
                >
                  <!-- Focus Area图标 -->
                  <svg class="w-3.5 h-3.5 mr-1.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 20l4-16m2 16l4-16M6 9h14M4 15h14" />
                  </svg>
                  <span class="text-sm truncate">{{ focusArea.name }}</span>
                </div>
              </div>
            </template>

            <!-- 第一类技能: 显示大分类 → Focus Area树 -->
            <template v-else>
              <div class="space-y-1">
                <div v-for="category in categories" :key="category.id">
                  <!-- 大分类节点 -->
                  <div
                    @click="toggleCategory(category.id)"
                    class="flex items-center px-2 py-1.5 rounded cursor-pointer hover:bg-gray-100 transition-colors"
                  >
                    <!-- 展开/折叠图标 -->
                    <svg
                      class="w-4 h-4 mr-1 text-gray-500 flex-shrink-0 transition-transform"
                      :class="{ 'rotate-90': expandedCategories.has(category.id) }"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                    </svg>
                    <!-- 大分类名称 -->
                    <span class="text-sm font-medium text-gray-700 truncate">{{ category.name }}</span>
                  </div>

                  <!-- Focus Area列表 (子节点) -->
                  <div v-if="expandedCategories.has(category.id)" class="ml-5 mt-1 space-y-1">
                    <div
                      v-for="focusArea in getFocusAreasByCategory(category.id)"
                      :key="focusArea.id"
                      @click.stop="selectFocusArea(focusArea.id)"
                      :class="[
                        'flex items-center px-2 py-1.5 rounded cursor-pointer transition-all duration-200',
                        selectedFocusAreaId === focusArea.id
                          ? 'bg-gradient-to-r from-blue-50 to-indigo-50 border-l-4 border-l-indigo-600 shadow-md text-indigo-700 font-medium'
                          : 'text-gray-600 hover:bg-gradient-to-r hover:from-gray-50 hover:to-indigo-50 hover:shadow-sm'
                      ]"
                    >
                      <!-- Focus Area图标 -->
                      <svg class="w-3.5 h-3.5 mr-1.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 20l4-16m2 16l4-16M6 9h14M4 15h14" />
                      </svg>
                      <span class="text-sm truncate">{{ focusArea.name }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>

      <!-- 右侧面板 (75%) - Tab 1: 学习资料 + Tab 2: 试题库 -->
      <div class="flex-1 bg-white overflow-hidden flex flex-col relative">
        <!-- 左侧栏折叠/展开按钮 -->
        <button
          @click="isSidebarCollapsed = !isSidebarCollapsed"
          class="absolute top-4 left-4 z-10 p-2 bg-white border border-gray-300 rounded-lg shadow-md hover:bg-gray-50 transition-colors"
          :title="isSidebarCollapsed ? '展开侧边栏' : '折叠侧边栏'"
        >
          <svg
            :class="['w-5 h-5 text-gray-600 transition-transform', isSidebarCollapsed ? '' : 'rotate-180']"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
          </svg>
        </button>


        <!-- 未选择Focus Area时的提示 -->
        <div v-if="!selectedFocusAreaId" class="flex-1 flex items-center justify-center text-gray-500">
          <div class="text-center">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            <p class="mt-2 text-sm">请先从左侧选择一个Focus Area</p>
          </div>
        </div>

        <!-- 选中Focus Area后显示Tabs -->
        <template v-else>
          <!-- Tab 标题 -->
          <div class="border-b border-gray-200 px-6 pt-4">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">
              {{ selectedFocusArea?.name || '学习内容' }}
            </h2>
            <!-- Tab 导航 -->
            <nav class="flex space-x-1" aria-label="Tabs">
              <button
                @click="activeTab = 'learning'"
                :class="[
                  'px-4 py-2 text-sm font-semibold transition-all duration-200 flex items-center gap-2',
                  activeTab === 'learning'
                    ? 'text-blue-600 border-b-2 border-blue-600 bg-blue-50'
                    : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50'
                ]"
              >
                <span>📚 学习资料</span>
                <span
                  v-if="learningContents.length > 0"
                  :class="[
                    'px-2 py-0.5 text-xs font-semibold rounded-full',
                    activeTab === 'learning'
                      ? 'bg-blue-100 text-blue-700'
                      : 'bg-gray-100 text-gray-600'
                  ]"
                >
                  {{ learningContents.length }}
                </span>
              </button>
              <button
                @click="activeTab = 'questions'"
                :class="[
                  'px-4 py-2 text-sm font-semibold transition-all duration-200 flex items-center gap-2',
                  activeTab === 'questions'
                    ? 'text-green-600 border-b-2 border-green-600 bg-green-50'
                    : 'text-gray-600 hover:text-green-600 hover:bg-green-50'
                ]"
              >
                <span>📝 试题库</span>
                <span
                  v-if="questions.length > 0"
                  :class="[
                    'px-2 py-0.5 text-xs font-semibold rounded-full',
                    activeTab === 'questions'
                      ? 'bg-green-100 text-green-700'
                      : 'bg-gray-100 text-gray-600'
                  ]"
                >
                  {{ questions.length }}
                </span>
              </button>
            </nav>
          </div>

          <!-- Tab 内容 -->
          <div class="flex-1 overflow-hidden">
            <!-- Tab 1: 学习资料 - 网格卡片布局 -->
            <div v-if="activeTab === 'learning'" class="h-full flex flex-col">
              <div v-if="loadingContents" class="text-center py-12">
                <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto mb-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                加载中...
              </div>

              <div v-else-if="learningContents.length === 0" class="text-center text-gray-400 py-12">
                该Focus Area暂无学习资料
              </div>

              <!-- 学习资料内容区域 -->
              <template v-else>
                <!-- 内容区域 -->
                <div class="flex-1 px-6 pt-3 pb-6 overflow-hidden flex flex-col">
                  <!-- iframe全屏查看模式 -->
                  <div v-if="selectedContent && isDocumentMaximized" class="flex-1 flex flex-col bg-white rounded-lg shadow-lg overflow-hidden">
                  <!-- 工具栏：学习资料下拉 + 操作按钮 -->
                  <div class="bg-gray-100 px-4 py-2 flex items-center justify-between border-b border-gray-200">
                    <!-- 左侧：学习资料下拉选择 -->
                    <div class="flex items-center gap-2 flex-1 min-w-0 mr-4">
                      <label class="text-xs font-medium text-gray-700 whitespace-nowrap">学习资料：</label>
                      <select
                        :value="selectedContent?.id"
                        @change="onContentSelectChange"
                        class="flex-1 max-w-md px-2 py-1 text-xs border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500 bg-white text-gray-900"
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
                    <!-- 右侧：操作按钮 -->
                    <div class="flex items-center gap-2 flex-shrink-0">
                      <!-- 在新窗口打开按钮 -->
                      <a
                        v-if="selectedContent.url"
                        :href="selectedContent.url"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="px-2 py-1 text-xs text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-50 transition-colors flex items-center gap-1"
                        title="在新窗口打开"
                      >
                        <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                        </svg>
                        新窗口
                      </a>
                      <!-- 返回卡片视图按钮 -->
                      <button
                        @click="minimizeDocument"
                        class="px-2 py-1 bg-blue-600 text-white text-xs font-medium rounded hover:bg-blue-700 transition-colors flex items-center gap-1"
                      >
                        <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                        </svg>
                        卡片视图
                      </button>
                    </div>
                  </div>

                  <!-- 内容容器 -->
                  <div class="flex-1 relative bg-white overflow-auto">
                    <!-- 视频类型：显示视频播放器 -->
                    <div v-if="selectedContent.contentType === 'video' && selectedContent.url" class="h-full flex items-center justify-center bg-black p-8">
                      <div class="w-full max-w-5xl">
                        <VideoPlayer :url="selectedContent.url" />
                      </div>
                    </div>

                    <!-- 文档类型：显示iframe -->
                    <iframe
                      v-else-if="selectedContent.url && selectedContent.contentType !== 'video' && !iframeLoadError"
                      :src="selectedContent.url"
                      class="w-full h-full border-0"
                      @error="handleIframeError"
                    ></iframe>

                    <!-- 纯文本内容（无URL） -->
                    <div v-else-if="!selectedContent.url && selectedContent.contentData" class="h-full overflow-auto p-8">
                      <div class="prose max-w-none">
                        <h2 class="text-2xl font-bold text-gray-900 mb-4">{{ selectedContent.title }}</h2>
                        <div class="text-gray-700 whitespace-pre-wrap">{{ selectedContent.contentData }}</div>
                      </div>
                    </div>

                    <!-- iframe加载失败提示 -->
                    <div v-else-if="iframeLoadError" class="absolute inset-0 bg-white flex items-center justify-center">
                      <div class="text-center p-8 max-w-md">
                        <svg class="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                        </svg>
                        <h3 class="text-lg font-semibold text-gray-900 mb-2">无法嵌入显示</h3>
                        <p class="text-sm text-gray-600 mb-4">
                          该网站不支持iframe嵌入显示，请点击下方按钮在新窗口打开。
                        </p>
                        <a
                          :href="selectedContent.url"
                          target="_blank"
                          rel="noopener noreferrer"
                          class="inline-flex items-center justify-center gap-2 px-6 py-3 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
                        >
                          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                          </svg>
                          在新窗口打开
                        </a>
                      </div>
                    </div>

                    <!-- 无内容提示 -->
                    <div v-else class="absolute inset-0 bg-white flex items-center justify-center">
                      <div class="text-center text-gray-400">
                        <p class="text-sm">暂无可显示的内容</p>
                      </div>
                    </div>
                  </div>
                </div>

                  <!-- 卡片最大化视图 -->
                  <div v-else-if="selectedContent && maximizedCard" class="flex-1 flex flex-col bg-white rounded-lg shadow-lg overflow-hidden">
                    <!-- 工具栏 -->
                    <div class="bg-gray-100 px-4 py-2 flex items-center justify-between border-b border-gray-200">
                      <h2 class="text-sm font-semibold text-gray-900">
                        {{
                          maximizedCard === 'aiNote' ? '🤖 AI 整体笔记' :
                          maximizedCard === 'userNote' ? '📝 整体笔记' :
                          maximizedCard.startsWith('aiKnowledgePoint-') ? '🤖 AI 知识点' :
                          '💡 知识点'
                        }}
                      </h2>
                      <button
                        @click="minimizeCard"
                        class="px-2 py-1 bg-blue-600 text-white text-xs font-medium rounded hover:bg-blue-700 transition-colors flex items-center gap-1"
                      >
                        <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                        </svg>
                        返回卡片视图
                      </button>
                    </div>

                    <!-- 内容区域 -->
                    <div class="flex-1 overflow-auto p-6">
                      <!-- AI整体笔记 -->
                      <div v-if="maximizedCard === 'aiNote' && selectedContent.aiNote?.noteContent" class="prose prose-lg max-w-none">
                        <div v-html="renderMarkdown(selectedContent.aiNote.noteContent)" class="text-gray-700"></div>
                      </div>

                      <!-- 用户整体笔记 -->
                      <div v-else-if="maximizedCard === 'userNote'">
                        <!-- 笔记编辑区 -->
                        <div v-if="isEditingNote || !selectedContent.userNote?.noteContent" class="max-w-4xl mx-auto">
                          <textarea
                            v-model="noteContent"
                            rows="20"
                            class="w-full px-4 py-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent resize-none font-mono"
                            placeholder="记录整体学习笔记 (支持Markdown)..."
                          ></textarea>
                          <div class="flex justify-end gap-2 mt-4">
                            <button
                              v-if="selectedContent.userNote?.noteContent"
                              @click="cancelEditNote"
                              class="px-4 py-2 text-sm text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-50"
                            >
                              取消
                            </button>
                            <button
                              @click="saveNote"
                              :disabled="!noteContent.trim()"
                              class="px-4 py-2 text-sm text-white bg-green-600 rounded hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                              {{ selectedContent.userNote?.noteContent ? '保存' : '添加' }}
                            </button>
                          </div>
                        </div>

                        <!-- 笔记显示区 -->
                        <div v-else-if="selectedContent.userNote?.noteContent" class="prose prose-lg max-w-none">
                          <div class="flex justify-end gap-2 mb-4">
                            <button
                              @click="startEditNote"
                              class="px-3 py-1.5 text-xs text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-50 flex items-center gap-1"
                            >
                              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                              </svg>
                              编辑
                            </button>
                            <button
                              @click="deleteNote"
                              class="px-3 py-1.5 text-xs text-red-700 bg-white border border-red-300 rounded hover:bg-red-50 flex items-center gap-1"
                            >
                              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                              </svg>
                              删除
                            </button>
                          </div>
                          <div v-html="renderMarkdown(selectedContent.userNote.noteContent)" class="text-gray-700"></div>
                        </div>
                      </div>

                      <!-- AI知识点 -->
                      <div v-else-if="maximizedCard.startsWith('aiKnowledgePoint-')" class="max-w-4xl mx-auto">
                        <template v-for="(kp, index) in selectedContent.aiKnowledgePoints" :key="kp.id || index">
                          <div v-if="maximizedCard === 'aiKnowledgePoint-' + (kp.id || index)">
                            <h3 class="text-xl font-bold text-gray-900 mb-4">{{ kp.title }}</h3>
                            <div class="prose prose-lg max-w-none">
                              <div v-html="renderMarkdown(kp.summary)" class="text-gray-700"></div>
                            </div>
                          </div>
                        </template>
                      </div>

                      <!-- 用户知识点 -->
                      <div v-else-if="maximizedCard.startsWith('userKnowledgePoint-')" class="max-w-4xl mx-auto">
                        <template v-for="(kp, index) in selectedContent.userKnowledgePoints" :key="kp.id || index">
                          <div v-if="maximizedCard === 'userKnowledgePoint-' + (kp.id || index)">
                            <div class="flex justify-between items-start mb-4">
                              <h3 class="text-xl font-bold text-gray-900">{{ kp.title }}</h3>
                              <div class="flex gap-2">
                                <button
                                  @click="editKnowledgePoint(kp)"
                                  class="px-3 py-1.5 text-xs text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-50 flex items-center gap-1"
                                >
                                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                  </svg>
                                  编辑
                                </button>
                                <button
                                  @click="deleteKnowledgePoint(kp.id)"
                                  class="px-3 py-1.5 text-xs text-red-700 bg-white border border-red-300 rounded hover:bg-red-50 flex items-center gap-1"
                                >
                                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                  </svg>
                                  删除
                                </button>
                              </div>
                            </div>
                            <div class="prose prose-lg max-w-none">
                              <div v-html="renderMarkdown(kp.summary)" class="text-gray-700"></div>
                            </div>
                          </div>
                        </template>
                      </div>
                    </div>
                  </div>

                  <!-- 卡片网格布局 -->
                  <div v-else-if="selectedContent" class="overflow-y-auto flex-1">
                    <!-- 顶部：学习资料下拉选择 -->
                    <div class="sticky top-0 bg-white z-10 pb-3 mb-3 border-b border-gray-200">
                      <div class="flex items-center gap-3">
                        <label class="text-xs font-medium text-gray-700 whitespace-nowrap">学习资料：</label>
                        <select
                          :value="selectedContent?.id"
                          @change="onContentSelectChange"
                          class="flex-1 max-w-md px-2 py-1 text-xs border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500 bg-white text-gray-900"
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
                    </div>

                    <!-- 卡片网格 -->
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 auto-rows-max">
                  <!-- 卡片1: 学习资料信息 - 使用渐变背景 -->
                  <div :class="[
                    'bg-gradient-to-br from-blue-50 to-white rounded-xl border-2 border-blue-200 shadow-md hover:shadow-xl transition-all flex flex-col',
                    selectedContent.contentType === 'video' ? 'md:col-span-2 md:row-span-2' : ''
                  ]">
                    <div
                      class="px-2 py-1.5 bg-gradient-to-r from-blue-500 to-blue-600 text-white flex items-center justify-between flex-shrink-0 rounded-t-xl"
                    >
                      <div
                        @click="cardStates.resourceInfo = !cardStates.resourceInfo"
                        class="flex items-center gap-1.5 cursor-pointer flex-1"
                      >
                        <div class="w-1.5 h-5 bg-gradient-to-b from-blue-300 to-white rounded-full"></div>
                        <h3 class="font-semibold text-xs">📚 学习资料</h3>
                      </div>
                      <div class="flex items-center gap-1">
                        <!-- 全屏查看按钮（所有类型的学习资料都可以最大化） -->
                        <button
                          @click.stop="maximizeDocument"
                          class="p-1 hover:bg-blue-600 rounded transition-all"
                          title="全屏查看"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
                          </svg>
                        </button>
                        <!-- 折叠按钮 -->
                        <button
                          @click.stop="cardStates.resourceInfo = !cardStates.resourceInfo"
                          class="p-1 hover:bg-blue-600 rounded transition-all"
                        >
                          <svg
                            :class="['w-3.5 h-3.5 transition-transform', cardStates.resourceInfo ? 'rotate-180' : '']"
                            fill="none" stroke="currentColor" viewBox="0 0 24 24"
                          >
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                          </svg>
                        </button>
                      </div>
                    </div>
                    <div v-show="cardStates.resourceInfo" class="p-3 flex flex-col flex-grow overflow-auto">
                      <h4 class="font-bold text-gray-900 mb-2 text-sm flex-shrink-0">{{ selectedContent.title }}</h4>

                      <!-- 视频播放器（仅在视频类型时显示） -->
                      <div v-if="selectedContent.contentType === 'video' && selectedContent.url" class="mb-4 flex-shrink-0">
                        <VideoPlayer :url="selectedContent.url" />
                      </div>

                      <!-- 描述（如果有） -->
                      <div v-if="selectedContent.description" class="mb-3 flex-shrink-0">
                        <p style="font-size: 0.7rem; line-height: 1.4;" class="text-gray-700">{{ selectedContent.description }}</p>
                      </div>

                      <!-- 内容数据 -->
                      <div v-if="selectedContent.contentData" class="mb-3 flex-shrink-0">
                        <div class="prose max-w-none text-gray-700" style="font-size: 0.7rem; line-height: 1.4;">
                          {{ selectedContent.contentData }}
                        </div>
                      </div>

                      <!-- 外部链接（对于非视频类型或作为备选链接） -->
                      <a
                        v-if="selectedContent.url"
                        :href="selectedContent.url"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="inline-flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white text-xs font-medium rounded-lg hover:bg-blue-700 transition-colors w-full mt-auto flex-shrink-0"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                        </svg>
                        {{ selectedContent.contentType === 'video' ? '在新窗口打开视频' : '打开学习资料' }}
                      </a>
                    </div>
                  </div>

                  <!-- 卡片2: AI整体笔记（如果有）- 使用蓝色渐变背景 -->
                  <div v-if="selectedContent.aiNote?.noteContent" class="bg-gradient-to-br from-blue-50 to-white rounded-xl border-2 border-blue-200 shadow-md hover:shadow-xl transition-all overflow-hidden">
                    <div class="px-2 py-1.5 bg-gradient-to-r from-blue-500 to-cyan-600 text-white flex items-center justify-between">
                      <div
                        @click="cardStates.aiNote = !cardStates.aiNote"
                        class="flex items-center gap-1.5 cursor-pointer flex-1"
                      >
                        <div class="w-1.5 h-5 bg-gradient-to-b from-blue-300 to-white rounded-full"></div>
                        <h3 class="font-semibold text-xs">🤖 AI 整体笔记</h3>
                      </div>
                      <div class="flex items-center gap-1">
                        <!-- 最大化按钮 -->
                        <button
                          @click.stop="maximizeCard('aiNote')"
                          class="p-1 hover:bg-blue-600 rounded transition-all"
                          title="全屏查看"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
                          </svg>
                        </button>
                        <!-- 折叠按钮 -->
                        <button
                          @click.stop="cardStates.aiNote = !cardStates.aiNote"
                          class="p-1 hover:bg-blue-600 rounded transition-all"
                        >
                          <svg
                            :class="['w-3.5 h-3.5 transition-transform', cardStates.aiNote ? 'rotate-180' : '']"
                            fill="none" stroke="currentColor" viewBox="0 0 24 24"
                          >
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                          </svg>
                        </button>
                      </div>
                    </div>
                    <div v-show="cardStates.aiNote" class="p-3">
                      <div
                        ref="aiNoteContent"
                        :class="['relative', !cardStates.aiNoteExpanded ? 'max-h-48 overflow-hidden' : '']"
                      >
                        <div class="prose prose-sm max-w-none text-xs">
                          <div v-html="renderMarkdown(selectedContent.aiNote.noteContent)" class="text-gray-700"></div>
                        </div>

                        <!-- 渐变遮罩 -->
                        <div v-if="!cardStates.aiNoteExpanded && cardStates.aiNoteOverflow" class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-white to-transparent pointer-events-none"></div>
                      </div>

                      <!-- 展开/收起按钮（仅在溢出时显示） -->
                      <button
                        v-if="cardStates.aiNoteOverflow"
                        @click="cardStates.aiNoteExpanded = !cardStates.aiNoteExpanded"
                        class="w-full mt-2 py-1 text-xs text-blue-600 hover:text-blue-700 font-medium flex items-center justify-center gap-1"
                      >
                        <span>{{ cardStates.aiNoteExpanded ? '收起' : '展开更多' }}</span>
                        <svg :class="['w-3 h-3 transition-transform', cardStates.aiNoteExpanded ? 'rotate-180' : '']" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                        </svg>
                      </button>
                    </div>
                  </div>

                  <!-- 卡片3: 用户整体笔记 - 使用绿色渐变背景 -->
                  <div class="bg-gradient-to-br from-green-50 to-white rounded-xl border-2 border-green-200 shadow-md hover:shadow-xl transition-all overflow-hidden">
                    <div class="px-2 py-1.5 bg-gradient-to-r from-green-500 to-emerald-600 text-white flex items-center justify-between">
                      <div
                        @click="cardStates.overallNote = !cardStates.overallNote"
                        class="flex items-center gap-1.5 cursor-pointer flex-1"
                      >
                        <div class="w-1.5 h-5 bg-gradient-to-b from-green-300 to-white rounded-full"></div>
                        <h3 class="font-semibold text-xs">📝 整体笔记</h3>
                      </div>
                      <div class="flex items-center gap-1">
                        <!-- 编辑/删除按钮 -->
                        <button
                          v-if="selectedContent.userNote?.noteContent && !isEditingNote"
                          @click.stop="startEditNote"
                          class="p-1 hover:bg-green-600 rounded transition-all"
                          title="编辑"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                          </svg>
                        </button>
                        <button
                          v-if="selectedContent.userNote?.noteContent && !isEditingNote"
                          @click.stop="deleteNote"
                          class="p-1 hover:bg-green-600 rounded transition-all"
                          title="删除"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                        <!-- 最大化按钮 -->
                        <button
                          @click.stop="maximizeCard('userNote')"
                          class="p-1 hover:bg-green-600 rounded transition-all"
                          title="全屏查看"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
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
                      <div v-if="isEditingNote || !selectedContent.userNote?.noteContent">
                        <textarea
                          v-model="noteContent"
                          rows="6"
                          class="w-full px-3 py-2 text-xs border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent resize-none"
                          placeholder="记录整体学习笔记 (支持Markdown)..."
                        ></textarea>
                        <div class="flex justify-end gap-2 mt-2">
                          <button
                            v-if="selectedContent.userNote?.noteContent"
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
                            {{ selectedContent.userNote?.noteContent ? '保存' : '添加' }}
                          </button>
                        </div>
                      </div>

                      <!-- 笔记显示区 -->
                      <div v-else-if="selectedContent.userNote?.noteContent">
                        <div
                          ref="overallNoteContent"
                          :class="['relative', !cardStates.overallNoteExpanded ? 'max-h-48 overflow-hidden' : '']"
                        >
                          <div class="prose prose-sm max-w-none text-xs">
                            <div v-html="renderMarkdown(selectedContent.userNote.noteContent)" class="text-gray-700"></div>
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
                        <p class="text-xs">点击上方编辑按钮添加整体笔记</p>
                      </div>
                    </div>
                  </div>

                  <!-- AI知识点卡片列表 - 使用靛蓝渐变背景 -->
                  <div
                    v-for="(point, index) in selectedContent.aiKnowledgePoints"
                    :key="'ai-kp-' + (point.id || index)"
                    class="bg-gradient-to-br from-indigo-50 to-white rounded-xl border-2 border-indigo-200 shadow-md hover:shadow-xl transition-all overflow-hidden"
                  >
                    <div class="px-2 py-1.5 bg-gradient-to-r from-indigo-500 to-blue-600 text-white flex items-center justify-between">
                      <div
                        @click="toggleAiKnowledgePointCard('ai-' + (point.id || index))"
                        class="flex items-center gap-1.5 cursor-pointer flex-1 min-w-0"
                      >
                        <div class="w-1.5 h-5 bg-gradient-to-b from-indigo-300 to-white rounded-full"></div>
                        <span class="text-xs">🤖</span>
                        <h3 class="font-semibold text-xs truncate">{{ index + 1 }}. {{ point.title || '未命名' }}</h3>
                      </div>
                      <div class="flex items-center gap-1 flex-shrink-0">
                        <!-- 最大化按钮 -->
                        <button
                          @click.stop="maximizeCard('aiKnowledgePoint-' + (point.id || index))"
                          class="p-1 hover:bg-indigo-600 rounded transition-all"
                          title="全屏查看"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
                          </svg>
                        </button>
                        <!-- 折叠按钮 -->
                        <button
                          @click.stop="toggleAiKnowledgePointCard('ai-' + (point.id || index))"
                          class="p-1 hover:bg-indigo-600 rounded transition-all"
                        >
                          <svg
                            :class="['w-3.5 h-3.5 transition-transform', cardStates.aiKnowledgePoints['ai-' + (point.id || index)] ? 'rotate-180' : '']"
                            fill="none" stroke="currentColor" viewBox="0 0 24 24"
                          >
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                          </svg>
                        </button>
                      </div>
                    </div>
                    <div v-show="cardStates.aiKnowledgePoints['ai-' + (point.id || index)]" class="p-3">
                      <div
                        :ref="el => { if (el) aiKnowledgePointContents['ai-' + (point.id || index)] = el }"
                        :class="['relative', !cardStates.aiKnowledgePointsExpanded['ai-' + (point.id || index)] ? 'max-h-48 overflow-hidden' : '']"
                      >
                        <div class="prose prose-sm max-w-none text-xs">
                          <div v-html="renderMarkdown(point.summary)" class="text-gray-700"></div>
                        </div>

                        <!-- 渐变遮罩 -->
                        <div v-if="!cardStates.aiKnowledgePointsExpanded['ai-' + (point.id || index)] && cardStates.aiKnowledgePointsOverflow['ai-' + (point.id || index)]" class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-white to-transparent pointer-events-none"></div>
                      </div>

                      <!-- 展开/收起按钮（仅在溢出时显示） -->
                      <button
                        v-if="cardStates.aiKnowledgePointsOverflow['ai-' + (point.id || index)]"
                        @click="cardStates.aiKnowledgePointsExpanded['ai-' + (point.id || index)] = !cardStates.aiKnowledgePointsExpanded['ai-' + (point.id || index)]"
                        class="w-full mt-2 py-1 text-xs text-indigo-600 hover:text-indigo-700 font-medium flex items-center justify-center gap-1"
                      >
                        <span>{{ cardStates.aiKnowledgePointsExpanded['ai-' + (point.id || index)] ? '收起' : '展开更多' }}</span>
                        <svg :class="['w-3 h-3 transition-transform', cardStates.aiKnowledgePointsExpanded['ai-' + (point.id || index)] ? 'rotate-180' : '']" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                        </svg>
                      </button>
                    </div>
                  </div>

                  <!-- 添加知识点卡片 - 使用渐变背景 -->
                  <div
                    @click="addNewKnowledgePointCard"
                    class="bg-gradient-to-br from-purple-50 to-white rounded-xl shadow-md overflow-hidden border-2 border-dashed border-purple-300 hover:border-purple-500 hover:from-purple-100 hover:shadow-xl transition-all cursor-pointer min-h-[150px]"
                  >
                    <div class="flex flex-col items-center justify-center h-full py-8">
                      <svg class="w-12 h-12 text-purple-400 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                      </svg>
                      <p class="text-sm font-semibold text-purple-600">添加知识点</p>
                      <p class="text-xs text-purple-400 mt-1">点击创建新卡片</p>
                    </div>
                  </div>

                  <!-- 知识点卡片列表 - 使用渐变背景 -->
                  <div
                    v-for="(point, index) in selectedContent.userKnowledgePoints"
                    :key="point.id || index"
                    class="bg-gradient-to-br from-purple-50 to-white rounded-xl border-2 border-purple-200 shadow-md hover:shadow-xl transition-all overflow-hidden"
                  >
                    <div class="px-2 py-1.5 bg-gradient-to-r from-purple-500 to-purple-600 text-white flex items-center justify-between">
                      <div
                        @click="toggleKnowledgePointCard(point.id || index)"
                        class="flex items-center gap-1.5 cursor-pointer flex-1 min-w-0"
                      >
                        <div class="w-1.5 h-5 bg-gradient-to-b from-purple-300 to-white rounded-full"></div>
                        <span class="text-xs">💡</span>
                        <h3 class="font-semibold text-xs truncate">{{ index + 1 }}. {{ point.title || '未命名' }}</h3>
                      </div>
                      <div class="flex items-center gap-1 flex-shrink-0">
                        <!-- 最大化按钮 -->
                        <button
                          v-if="point.title && point.summary"
                          @click.stop="maximizeCard('userKnowledgePoint-' + (point.id || index))"
                          class="p-1 hover:bg-purple-600 rounded transition-all"
                          title="全屏查看"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
                          </svg>
                        </button>
                        <!-- 编辑按钮 -->
                        <button
                          v-if="point.title && point.summary"
                          @click.stop="editKnowledgePoint(point)"
                          class="p-1 hover:bg-purple-600 rounded transition-all"
                          title="编辑"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                          </svg>
                        </button>
                        <!-- 删除按钮 -->
                        <button
                          @click.stop="deleteKnowledgePoint(selectedContent.id, point.id)"
                          class="p-1 hover:bg-purple-600 rounded transition-all"
                          title="删除"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                        <!-- 折叠按钮 -->
                        <button
                          @click.stop="toggleKnowledgePointCard(point.id || index)"
                          class="p-1 hover:bg-purple-600 rounded transition-all"
                        >
                          <svg
                            :class="['w-3.5 h-3.5 transition-transform', cardStates.knowledgePoints[point.id || index] ? 'rotate-180' : '']"
                            fill="none" stroke="currentColor" viewBox="0 0 24 24"
                          >
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                          </svg>
                        </button>
                      </div>
                    </div>
                    <div v-show="cardStates.knowledgePoints[point.id || index]" class="p-3">
                      <!-- 如果没有内容，显示编辑表单 -->
                      <div v-if="!point.title || !point.summary" class="space-y-2">
                        <input
                          v-model="point.title"
                          @blur="saveKnowledgePoint(selectedContent.id, point)"
                          placeholder="知识点标题"
                          class="w-full px-2 py-1 text-xs border border-gray-300 rounded focus:ring-purple-500 focus:border-purple-500"
                        />
                        <textarea
                          v-model="point.summary"
                          @blur="saveKnowledgePoint(selectedContent.id, point)"
                          placeholder="知识点内容 (支持Markdown)"
                          class="w-full px-2 py-1 text-xs border border-gray-300 rounded focus:ring-purple-500 focus:border-purple-500"
                          rows="4"
                        ></textarea>
                      </div>
                      <!-- 如果有内容，显示Markdown渲染 -->
                      <div v-else>
                        <div
                          :ref="el => { if (el) knowledgePointContents[point.id || index] = el }"
                          :class="['relative', !cardStates.knowledgePointsExpanded[point.id || index] ? 'max-h-48 overflow-hidden' : '']"
                        >
                          <div class="prose prose-sm max-w-none text-xs">
                            <div v-html="renderMarkdown(point.summary)" class="text-gray-700"></div>
                          </div>

                          <!-- 渐变遮罩 -->
                          <div v-if="!cardStates.knowledgePointsExpanded[point.id || index] && cardStates.knowledgePointsOverflow[point.id || index]" class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-white to-transparent pointer-events-none"></div>
                        </div>

                        <!-- 展开/收起按钮（仅在溢出时显示） -->
                        <button
                          v-if="cardStates.knowledgePointsOverflow[point.id || index]"
                          @click="cardStates.knowledgePointsExpanded[point.id || index] = !cardStates.knowledgePointsExpanded[point.id || index]"
                          class="w-full mt-2 py-1 text-xs text-purple-600 hover:text-purple-700 font-medium flex items-center justify-center gap-1"
                        >
                          <span>{{ cardStates.knowledgePointsExpanded[point.id || index] ? '收起' : '展开更多' }}</span>
                          <svg :class="['w-3 h-3 transition-transform', cardStates.knowledgePointsExpanded[point.id || index] ? 'rotate-180' : '']" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                          </svg>
                        </button>
                      </div>
                    </div>
                  </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>

            <!-- Tab 2: 试题库 - 两栏布局 -->
            <div v-if="activeTab === 'questions'" class="h-full flex overflow-hidden relative">
              <!-- 加载状态 -->
              <div v-if="loadingQuestions" class="flex-1 flex items-center justify-center">
                <div class="text-center">
                  <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto mb-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 0 1 4 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  加载中...
                </div>
              </div>

              <!-- 无试题状态 -->
              <div v-else-if="questions.length === 0" class="flex-1 flex items-center justify-center">
                <div class="text-center text-gray-400">
                  <svg class="mx-auto h-12 w-12 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <p class="text-sm">该Focus Area暂无试题</p>
                </div>
              </div>

              <!-- 试题库两栏布局 -->
              <template v-else>
                <!-- 左侧：试题列表 (可隐藏) -->
                <div
                  :class="[
                    'border-r border-gray-200 overflow-y-auto bg-gray-50 transition-all duration-300',
                    isQuestionListCollapsed ? 'w-0' : 'w-[30%]'
                  ]"
                >
                  <div class="p-4 space-y-2">
                    <!-- 添加试题按钮 -->
                    <button
                      @click="addNewQuestion"
                      class="w-full px-4 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-lg hover:from-blue-700 hover:to-purple-700 transition-all shadow-md hover:shadow-lg flex items-center justify-center gap-2 font-medium"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                      </svg>
                      添加试题
                    </button>

                    <!-- 分页信息和控件 -->
                    <div v-if="questionPagination.totalElements > 0" class="flex items-center justify-between px-2 py-2 bg-blue-50 rounded-lg border border-blue-200">
                      <span class="text-xs text-gray-600">
                        共 <span class="font-semibold text-blue-600">{{ questionPagination.totalElements }}</span> 道题
                        (第 {{ questionPagination.currentPage + 1 }}/{{ questionPagination.totalPages }} 页)
                      </span>
                      <div class="flex gap-1">
                        <button
                          @click="loadQuestions(questionPagination.currentPage - 1)"
                          :disabled="!questionPagination.hasPrevious"
                          :class="[
                            'px-2 py-1 text-xs font-medium rounded transition-colors',
                            questionPagination.hasPrevious
                              ? 'bg-white text-blue-600 hover:bg-blue-100 border border-blue-300'
                              : 'bg-gray-100 text-gray-400 cursor-not-allowed border border-gray-200'
                          ]"
                        >
                          上一页
                        </button>
                        <button
                          @click="loadQuestions(questionPagination.currentPage + 1)"
                          :disabled="!questionPagination.hasNext"
                          :class="[
                            'px-2 py-1 text-xs font-medium rounded transition-colors',
                            questionPagination.hasNext
                              ? 'bg-white text-blue-600 hover:bg-blue-100 border border-blue-300'
                              : 'bg-gray-100 text-gray-400 cursor-not-allowed border border-gray-200'
                          ]"
                        >
                          下一页
                        </button>
                      </div>
                    </div>

                    <!-- 试题列表 (表格) -->
                    <div class="bg-white rounded-lg border border-gray-200 overflow-hidden">
                      <table class="w-full text-xs">
                        <thead class="bg-gradient-to-r from-blue-50 to-indigo-50 border-b border-gray-200">
                          <tr>
                            <th class="px-2 py-1.5 text-left font-semibold text-gray-700 w-12">#</th>
                            <th class="px-2 py-1.5 text-left font-semibold text-gray-700">题目</th>
                            <th class="px-2 py-1.5 text-center font-semibold text-gray-700 w-16">类型</th>
                            <th class="px-2 py-1.5 text-center font-semibold text-gray-700 w-16">难度</th>
                            <th class="px-2 py-1.5 text-center font-semibold text-gray-700 w-12">状态</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr
                            v-for="(question, index) in questions"
                            :key="question.id"
                            @click="selectQuestion(question)"
                            :class="[
                              'cursor-pointer transition-colors border-b border-gray-100 last:border-0',
                              selectedQuestion?.id === question.id
                                ? 'bg-blue-50 hover:bg-blue-100'
                                : 'hover:bg-gray-50'
                            ]"
                          >
                            <!-- 序号 -->
                            <td class="px-2 py-1.5">
                              <span :class="[
                                'inline-block px-1.5 py-0.5 rounded text-xs font-bold',
                                selectedQuestion?.id === question.id ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600'
                              ]">
                                {{ questionPagination.currentPage * questionPagination.pageSize + index + 1 }}
                              </span>
                            </td>
                            <!-- 题目标题 -->
                            <td :class="[
                              'px-2 py-1.5 font-medium truncate max-w-0',
                              selectedQuestion?.id === question.id ? 'text-blue-700' : 'text-gray-900'
                            ]" :title="question.title">
                              {{ question.title }}
                            </td>
                            <!-- 类型 -->
                            <td class="px-2 py-1.5 text-center">
                              <span :class="[
                                'inline-block px-1.5 py-0.5 rounded text-xs',
                                question.questionType === 'programming' ? 'bg-purple-100 text-purple-700' :
                                question.questionType === 'technical' ? 'bg-blue-100 text-blue-700' :
                                question.questionType === 'behavioral' ? 'bg-green-100 text-green-700' :
                                'bg-orange-100 text-orange-700'
                              ]">
                                {{ question.questionType === 'programming' ? '编程' :
                                   question.questionType === 'technical' ? '技术' :
                                   question.questionType === 'behavioral' ? '行为' : '设计' }}
                              </span>
                            </td>
                            <!-- 难度 -->
                            <td class="px-2 py-1.5 text-center">
                              <span :class="[
                                'inline-block px-1.5 py-0.5 rounded text-xs font-medium',
                                question.difficulty === 'EASY' ? 'bg-green-100 text-green-700' :
                                question.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' :
                                'bg-red-100 text-red-700'
                              ]">
                                {{ question.difficulty === 'EASY' ? '简单' :
                                   question.difficulty === 'MEDIUM' ? '中等' : '困难' }}
                              </span>
                            </td>
                            <!-- 状态 -->
                            <td class="px-2 py-1.5 text-center">
                              <span v-if="question.hasUserNote" class="inline-block w-5 h-5 rounded-full bg-green-500 text-white flex items-center justify-center" title="已答题">
                                ✓
                              </span>
                              <span v-else class="text-gray-300">-</span>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>

                <!-- 右侧：浏览模式 / 答题模式 -->
                <div class="flex-1 overflow-y-auto bg-white relative">
                  <!-- 试题列表折叠/展开按钮 -->
                  <button
                    @click="isQuestionListCollapsed = !isQuestionListCollapsed"
                    class="absolute top-4 left-4 z-10 p-2 bg-white border border-gray-300 rounded-lg shadow-md hover:bg-gray-50 transition-colors"
                    :title="isQuestionListCollapsed ? '展开试题列表' : '折叠试题列表'"
                  >
                    <svg
                      :class="['w-5 h-5 text-gray-600 transition-transform', isQuestionListCollapsed ? '' : 'rotate-180']"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
                    </svg>
                  </button>

                  <!-- 未选中试题 -->
                  <div v-if="!selectedQuestion" class="h-full flex items-center justify-center text-gray-400">
                    <div class="text-center">
                      <svg class="mx-auto h-12 w-12 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                      </svg>
                      <p class="text-sm">请从左侧选择一道试题</p>
                    </div>
                  </div>

                  <!-- 已选中试题 -->
                  <div v-else class="h-full flex flex-col">
                    <!-- 标题栏 + 模式切换按钮 -->
                    <div class="px-6 pt-4 pb-3 flex-shrink-0 border-b border-gray-200">
                      <div class="flex items-center justify-between">
                        <!-- 左侧：试题标题 -->
                        <h3 class="text-base font-semibold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">
                          {{ selectedQuestion.title || '新建试题' }}
                        </h3>

                        <!-- 右侧：模式切换按钮组 -->
                        <div class="flex items-center gap-2">
                          <button
                            v-if="selectedQuestion.id"
                            @click="questionViewMode = 'browse'"
                            :class="[
                              'px-3 py-1.5 text-sm font-medium rounded-lg transition-all duration-200',
                              questionViewMode === 'browse'
                                ? 'bg-blue-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                            ]"
                          >
                            📖 浏览
                          </button>
                          <button
                            v-if="selectedQuestion.id"
                            @click="questionViewMode = 'answer'"
                            :class="[
                              'px-3 py-1.5 text-sm font-medium rounded-lg transition-all duration-200',
                              questionViewMode === 'answer'
                                ? 'bg-green-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                            ]"
                          >
                            ✍️ 答题
                          </button>
                          <button
                            v-if="canEditQuestion"
                            @click="questionViewMode = 'edit'"
                            :class="[
                              'px-3 py-1.5 text-sm font-medium rounded-lg transition-all duration-200',
                              questionViewMode === 'edit'
                                ? 'bg-purple-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                            ]"
                          >
                            ✏️ 编辑
                          </button>
                        </div>
                      </div>
                    </div>

                    <!-- 浏览模式 - 左右两栏布局 -->
                    <div v-if="questionViewMode === 'browse'" class="flex-1 flex gap-4 px-6 overflow-hidden">
                      <!-- 左栏：题目 (50%) -->
                      <div class="w-1/2 space-y-4 overflow-y-auto pr-2">
                        <!-- 题目类型 -->
                        <div class="bg-gray-50 p-3 rounded-lg">
                          <span class="text-xs font-semibold text-gray-500">题目类型：</span>
                          <span class="text-sm text-gray-900">{{ selectedQuestion.questionType }}</span>
                        </div>

                        <!-- 题目描述 -->
                        <div>
                          <h4 class="text-sm font-semibold text-gray-700 mb-2">📝 题目描述</h4>
                          <div class="prose prose-sm max-w-none compact-prose" v-html="renderMarkdown(selectedQuestion.questionDescription)"></div>
                        </div>

                        <!-- 答案要求 -->
                        <div v-if="selectedQuestion.answerRequirement">
                          <h4 class="text-sm font-semibold text-gray-700 mb-2">📋 答案要求</h4>
                          <div class="prose prose-sm max-w-none compact-prose" v-html="renderMarkdown(selectedQuestion.answerRequirement)"></div>
                        </div>
                      </div>

                      <!-- 右栏：答案 (50%) -->
                      <div class="w-1/2 flex flex-col overflow-hidden pl-2 border-l-2 border-gray-200">
                        <!-- 答案类型切换 - 始终显示两个按钮 -->
                        <div class="flex gap-2 mb-3 flex-shrink-0">
                          <button
                            @click="browseNoteType = 'ai'"
                            :class="[
                              'flex-1 px-4 py-2 text-sm font-medium rounded-lg transition-all',
                              browseNoteType === 'ai'
                                ? 'bg-gradient-to-r from-purple-600 to-indigo-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                            ]"
                          >
                            <span class="flex items-center justify-center gap-2">
                              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path d="M13 7H7v6h6V7z" />
                                <path fill-rule="evenodd" d="M7 2a1 1 0 012 0v1h2V2a1 1 0 112 0v1h2a2 2 0 012 2v2h1a1 1 0 110 2h-1v2h1a1 1 0 110 2h-1v2a2 2 0 01-2 2h-2v1a1 1 0 11-2 0v-1H9v1a1 1 0 11-2 0v-1H5a2 2 0 01-2-2v-2H2a1 1 0 110-2h1V9H2a1 1 0 010-2h1V5a2 2 0 012-2h2V2zM5 5h10v10H5V5z" clip-rule="evenodd" />
                              </svg>
                              AI 参考答案
                            </span>
                          </button>
                          <button
                            @click="browseNoteType = 'user'"
                            :class="[
                              'flex-1 px-4 py-2 text-sm font-medium rounded-lg transition-all',
                              browseNoteType === 'user'
                                ? 'bg-gradient-to-r from-green-600 to-emerald-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                            ]"
                          >
                            <span class="flex items-center justify-center gap-2">
                              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                              </svg>
                              我的答案
                            </span>
                          </button>
                        </div>

                        <!-- 答案内容区域 -->
                        <div class="flex-1 overflow-y-auto space-y-4">
                          <!-- AI笔记 -->
                          <template v-if="browseNoteType === 'ai' && selectedQuestion.aiNote?.noteContent">
                            <div class="bg-gradient-to-br from-purple-50 to-indigo-50 rounded-lg p-4 border-2 border-purple-200">
                              <div class="flex items-center gap-2 mb-3">
                                <svg class="w-5 h-5 text-purple-600" fill="currentColor" viewBox="0 0 20 20">
                                  <path d="M13 7H7v6h6V7z" />
                                  <path fill-rule="evenodd" d="M7 2a1 1 0 012 0v1h2V2a1 1 0 112 0v1h2a2 2 0 012 2v2h1a1 1 0 110 2h-1v2h1a1 1 0 110 2h-1v2a2 2 0 01-2 2h-2v1a1 1 0 11-2 0v-1H9v1a1 1 0 11-2 0v-1H5a2 2 0 01-2-2v-2H2a1 1 0 110-2h1V9H2a1 1 0 010-2h1V5a2 2 0 012-2h2V2zM5 5h10v10H5V5z" clip-rule="evenodd" />
                                </svg>
                                <h4 class="text-sm font-bold text-purple-900">🤖 AI 参考答案</h4>
                              </div>

                              <!-- AI核心要点（如果有） -->
                              <div v-if="selectedQuestion.aiNote?.coreStrategy" class="mb-4 bg-white/60 rounded-lg p-3">
                                <h5 class="text-xs font-semibold text-purple-800 mb-2 flex items-center gap-1">
                                  <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 20 20">
                                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                                  </svg>
                                  核心要点
                                </h5>
                                <div class="prose prose-sm max-w-none compact-prose text-gray-700" v-html="renderMarkdown(selectedQuestion.aiNote.coreStrategy)"></div>
                              </div>

                              <!-- AI答案主体 -->
                              <div class="prose prose-sm max-w-none compact-prose text-gray-800" v-html="renderMarkdown(selectedQuestion.aiNote.noteContent)"></div>
                            </div>
                          </template>

                          <!-- 用户笔记 -->
                          <template v-if="browseNoteType === 'user'">
                            <!-- 有用户笔记 -->
                            <div v-if="selectedQuestion.userNote?.noteContent || selectedQuestion.userNote?.coreStrategy" class="bg-gradient-to-br from-green-50 to-emerald-50 rounded-lg p-4 border-2 border-green-200">
                              <div class="flex items-center gap-2 mb-3">
                                <svg class="w-5 h-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                                  <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                                </svg>
                                <h4 class="text-sm font-bold text-green-900">✍️ 我的答案</h4>
                              </div>

                              <!-- 用户核心要点（如果有） -->
                              <div v-if="selectedQuestion.userNote?.coreStrategy" class="mb-4 bg-white/60 rounded-lg p-3">
                                <h5 class="text-xs font-semibold text-green-800 mb-2 flex items-center gap-1">
                                  <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 20 20">
                                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                                  </svg>
                                  核心要点
                                </h5>
                                <div class="prose prose-sm max-w-none compact-prose text-gray-700" v-html="renderMarkdown(selectedQuestion.userNote.coreStrategy)"></div>
                              </div>

                              <!-- 用户答案主体 -->
                              <div v-if="selectedQuestion.userNote?.noteContent" class="prose prose-sm max-w-none compact-prose text-gray-800" v-html="renderMarkdown(selectedQuestion.userNote.noteContent)"></div>
                            </div>

                            <!-- 无用户笔记 - 提示去答题 -->
                            <div v-else class="flex flex-col items-center justify-center h-full text-gray-400 py-12">
                              <svg class="w-16 h-16 mb-4 text-green-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                              </svg>
                              <p class="text-sm font-medium text-gray-600 mb-2">还没有你的答案</p>
                              <button
                                @click="questionViewMode = 'answer'"
                                class="mt-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-green-600 to-emerald-600 rounded-lg hover:from-green-700 hover:to-emerald-700 transition-all shadow-md"
                              >
                                开始答题
                              </button>
                            </div>
                          </template>

                          <!-- AI笔记不存在时的提示 -->
                          <div v-if="browseNoteType === 'ai' && !selectedQuestion.aiNote?.noteContent" class="flex flex-col items-center justify-center h-full text-gray-400 py-12">
                            <svg class="w-16 h-16 mb-4 text-purple-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                            <p class="text-sm font-medium text-gray-600 mb-1">暂无 AI 参考答案</p>

                            <!-- 引导用户到答题模式 -->
                            <template v-if="canEditAiNote">
                              <p class="text-xs text-gray-500 mb-4">切换到"✍️ 答题"模式，选择"AI参考答案"身份即可添加</p>
                              <button
                                @click="questionViewMode = 'answer'; answerNoteType = 'ai'"
                                class="px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-600 to-indigo-600 rounded-lg hover:from-purple-700 hover:to-indigo-700 transition-all shadow-md flex items-center gap-2"
                              >
                                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                </svg>
                                前往答题模式添加
                              </button>
                            </template>
                            <template v-else>
                              <p class="text-xs text-gray-500 mb-4">可以切换到"我的答案"开始作答</p>
                            </template>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!-- 答题模式 -->
                    <div v-else-if="questionViewMode === 'answer'" class="flex-1 flex overflow-hidden">
                      <!-- 左侧：编辑身份tab (侧边栏) -->
                      <div class="w-12 flex-shrink-0 bg-gradient-to-b from-gray-50 to-gray-100 border-r-2 border-gray-300">
                        <div class="py-6 space-y-3 flex flex-col items-center">
                          <!-- 我的答案 tab -->
                          <button
                            @click="answerNoteType = 'user'"
                            :class="[
                              'w-10 py-8 rounded-lg font-medium transition-all duration-200 flex items-center justify-center relative',
                              answerNoteType === 'user'
                                ? 'bg-gradient-to-br from-green-500 to-emerald-500 text-white shadow-lg'
                                : 'text-gray-600 hover:text-gray-800 hover:bg-white hover:shadow-md'
                            ]"
                            :title="answerNoteType === 'user' ? '当前：我的答案' : '切换到我的答案'"
                          >
                            <span class="vertical-text text-sm tracking-wider">我的答案</span>
                          </button>

                          <!-- AI参考答案 tab -->
                          <button
                            v-if="canEditAiNote"
                            @click="answerNoteType = 'ai'"
                            :class="[
                              'w-10 py-8 rounded-lg font-medium transition-all duration-200 flex items-center justify-center relative',
                              answerNoteType === 'ai'
                                ? 'bg-gradient-to-br from-purple-500 to-indigo-500 text-white shadow-lg'
                                : 'text-gray-600 hover:text-gray-800 hover:bg-white hover:shadow-md'
                            ]"
                            :title="answerNoteType === 'ai' ? '当前：AI参考答案' : '切换到AI参考答案'"
                          >
                            <span class="vertical-text text-sm tracking-wider">AI答案</span>
                          </button>
                        </div>
                      </div>

                      <!-- 右侧：答题内容区域 -->
                      <div class="flex-1 flex flex-col overflow-hidden">

                      <!-- Programming题目：左右两栏布局 -->
                      <template v-if="selectedQuestion.questionType === 'programming'">
                        <!-- 固定顶部：模式切换按钮（模版/自由） -->
                        <div v-if="answerTemplate && answerTemplate.templateFields" class="flex-shrink-0 px-6 pt-3 pb-2 bg-white border-b border-gray-200">
                          <div class="flex items-center justify-between">
                            <div class="flex items-center gap-3">
                              <span class="text-sm font-medium text-gray-700">答题模式:</span>
                              <div class="flex bg-gray-100 rounded-lg p-1">
                                <button
                                  @click="answerMode = 'template'"
                                  :class="[
                                    'px-4 py-2 rounded-md text-sm font-medium transition-all duration-200',
                                    answerMode === 'template'
                                      ? 'bg-white text-blue-700 shadow-sm'
                                      : 'text-gray-600 hover:text-gray-800'
                                  ]"
                                >
                                  📋 {{ answerTemplate.templateName }} 模版
                                </button>
                                <button
                                  @click="answerMode = 'free'"
                                  :class="[
                                    'px-4 py-2 rounded-md text-sm font-medium transition-all duration-200',
                                    answerMode === 'free'
                                      ? 'bg-white text-blue-700 shadow-sm'
                                      : 'text-gray-600 hover:text-gray-800'
                                  ]"
                                >
                                  ✍️ 自由答题
                                </button>
                              </div>
                            </div>
                            <!-- 模版说明（右侧提示） -->
                            <span v-if="answerMode === 'template'" class="text-xs text-gray-500 italic">
                              {{ answerTemplate.description }}
                            </span>
                          </div>
                        </div>

                        <!-- 左右两栏布局 -->
                        <div class="flex-1 flex overflow-hidden">
                          <!-- 左栏：题目描述（可滚动） -->
                          <div class="w-1/2 border-r border-gray-200 overflow-y-auto px-6 py-4">
                            <div class="bg-gray-50 rounded-lg p-3 border border-gray-200">
                              <h4 class="text-sm font-semibold text-gray-700 mb-2">📝 题目描述</h4>
                              <div class="prose prose-sm max-w-none compact-prose" v-html="renderMarkdown(selectedQuestion.questionDescription)"></div>
                            </div>
                          </div>

                          <!-- 右栏：答题内容（可滚动） -->
                          <div class="w-1/2 overflow-y-auto px-6 py-4 space-y-4">

                      <!-- 模版模式：根据模版字段渲染结构化输入 -->
                      <div v-if="answerTemplate && answerTemplate.templateFields && answerMode === 'template'" class="space-y-3">

                        <!-- 动态渲染模版字段 - 两栏卡片布局 -->
                        <div class="grid grid-cols-1 lg:grid-cols-2 gap-3">
                          <div
                            v-for="(field, index) in parseTemplateFields(answerTemplate.templateFields)"
                            :key="field.key"
                            :class="[
                              'relative rounded-lg shadow-md overflow-hidden',
                              index === 0 ? 'bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-300' :
                              index === 1 ? 'bg-gradient-to-br from-green-50 to-green-100 border border-green-300' :
                              index === 2 ? 'bg-gradient-to-br from-orange-50 to-orange-100 border border-orange-300' :
                              'bg-gradient-to-br from-purple-50 to-purple-100 border border-purple-300'
                            ]"
                          >
                            <!-- 卡片头部 - 更紧凑 -->
                            <div :class="[
                              'px-2 py-1 flex items-center space-x-1.5',
                              index === 0 ? 'bg-gradient-to-r from-blue-500 to-blue-600' :
                              index === 1 ? 'bg-gradient-to-r from-green-500 to-green-600' :
                              index === 2 ? 'bg-gradient-to-r from-orange-500 to-orange-600' :
                              'bg-gradient-to-r from-purple-500 to-purple-600'
                            ]">
                              <div :class="[
                                'w-5 h-5 rounded-full flex items-center justify-center font-bold text-xs',
                                'bg-white/20 backdrop-blur-sm text-white'
                              ]">
                                {{ index + 1 }}
                              </div>
                              <h5 class="text-xs font-semibold text-white">{{ field.label }}</h5>
                            </div>

                            <!-- 卡片内容 - 增加输入框行数 -->
                            <div class="p-2">
                              <textarea
                                v-model="answerForm[field.key]"
                                :placeholder="field.placeholder"
                                :rows="index >= 2 ? 12 : 10"
                                :class="[
                                  'w-full px-2 py-2 text-sm bg-white border rounded-lg',
                                  'focus:outline-none focus:ring-1 focus:ring-offset-1 resize-y',
                                  'placeholder:text-gray-400',
                                  index === 0 ? 'border-blue-200 focus:ring-blue-500 focus:border-blue-500' :
                                  index === 1 ? 'border-green-200 focus:ring-green-500 focus:border-green-500' :
                                  index === 2 ? 'border-orange-200 focus:ring-orange-500 focus:border-orange-500' :
                                  'border-purple-200 focus:ring-purple-500 focus:border-purple-500'
                                ]"
                              ></textarea>
                            </div>
                          </div>
                        </div>

                      </div>

                      <!-- 核心思路（所有题目类型，所有模式共享） -->
                      <div class="space-y-3 pt-4 border-t-2 border-gray-200">
                        <div class="flex items-center gap-2 mb-2">
                          <svg class="w-5 h-5 text-yellow-600" fill="currentColor" viewBox="0 0 20 20">
                            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                          </svg>
                          <label class="text-sm font-bold text-gray-800">
                            💡 核心思路
                          </label>
                          <span class="text-xs text-gray-500">
                            {{ selectedQuestion?.questionType === 'programming' ? '(算法要点、时间空间复杂度)' : '(关键思路、要点总结)' }}
                          </span>
                        </div>
                        <textarea
                          v-model="answerForm.coreStrategy"
                          :placeholder="coreStrategyPlaceholder"
                          rows="6"
                          class="w-full px-4 py-3 text-sm border-2 border-yellow-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-yellow-500 bg-yellow-50/30 resize-none"
                        ></textarea>
                        <p class="text-xs text-gray-500">
                          {{ selectedQuestion?.questionType === 'programming'
                            ? '记录算法思路、时间/空间复杂度、关键要点'
                            : '记录答题核心思路、关键要点、注意事项'
                          }}
                        </p>
                      </div>

                      <!-- 保存按钮（模版模式） -->
                      <div v-if="answerTemplate && answerTemplate.templateFields && answerMode === 'template'" class="flex justify-end gap-2 pt-4">
                        <button
                          @click="saveAnswer"
                          class="px-8 py-3 bg-gradient-to-r from-green-600 to-emerald-600 text-white text-sm font-bold rounded-lg hover:from-green-700 hover:to-emerald-700 shadow-md hover:shadow-lg transition-all duration-200 transform hover:scale-105"
                        >
                          💾 保存答案
                        </button>
                      </div>

                      <!-- 自由模式：自由文本输入 (无模版或用户选择自由模式) -->
                      <div v-if="!answerTemplate || answerMode === 'free'" class="space-y-4">
                        <div class="bg-gray-50 p-3 rounded-lg mb-4">
                          <h4 class="text-sm font-semibold text-gray-700">自由答题</h4>
                          <p class="text-xs text-gray-500 mt-1">请根据题目要求,自由发挥作答</p>
                        </div>

                        <textarea
                          v-model="answerForm.freeText"
                          placeholder="请输入你的答案..."
                          rows="12"
                          class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                        ></textarea>

                        <!-- 保存按钮（自由模式） -->
                        <div class="flex justify-end gap-2 pt-4 border-t border-gray-200">
                          <button
                            @click="saveAnswer"
                            class="px-6 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors"
                          >
                            💾 保存答案
                          </button>
                        </div>
                      </div>
                          </div>
                        </div>
                      </template>

                      <!-- 非Programming题目：单栏布局（题目描述固定） -->
                      <template v-else>
                        <!-- 固定顶部：题目描述 + 模式切换（横向布局） -->
                        <div class="flex-shrink-0 px-6 pt-4 pb-3 border-b border-gray-200">
                          <div class="flex gap-4">
                            <!-- 题目描述（左侧，自动占据剩余空间） -->
                            <div class="flex-1 bg-gray-50 rounded-lg p-3 border border-gray-200">
                              <h4 class="text-sm font-semibold text-gray-700 mb-2">📝 题目描述</h4>
                              <div class="prose prose-sm max-w-none compact-prose" v-html="renderMarkdown(selectedQuestion.questionDescription)"></div>
                            </div>

                            <!-- 答题模式切换（右侧，固定宽度，如果有模版） -->
                            <div v-if="answerTemplate && answerTemplate.templateFields" class="flex flex-col items-start">
                              <label class="block text-xs font-medium text-gray-700 mb-1.5">答题模式</label>
                              <div class="flex flex-col space-y-1.5">
                                <button
                                  @click="answerMode = 'template'"
                                  :class="[
                                    'px-2 py-1 rounded text-xs font-medium transition-colors whitespace-nowrap',
                                    answerMode === 'template'
                                      ? 'bg-blue-600 text-white'
                                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                  ]"
                                >
                                  {{ answerTemplate.templateName }} 模版
                                </button>
                                <button
                                  @click="answerMode = 'free'"
                                  :class="[
                                    'px-2 py-1 rounded text-xs font-medium transition-colors whitespace-nowrap',
                                    answerMode === 'free'
                                      ? 'bg-blue-600 text-white'
                                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                  ]"
                                >
                                  自由答题
                                </button>
                              </div>
                            </div>
                          </div>
                        </div>

                        <!-- 答题内容（可滚动） -->
                        <div class="flex-1 overflow-y-auto px-6 py-4 space-y-4">
                          <!-- 模版模式：根据模版字段渲染结构化输入 -->
                          <div v-if="answerTemplate && answerTemplate.templateFields && answerMode === 'template'" class="space-y-3">
                            <!-- 动态渲染模版字段 - 两栏卡片布局 -->
                            <div class="grid grid-cols-1 lg:grid-cols-2 gap-3">
                              <div
                                v-for="(field, index) in parseTemplateFields(answerTemplate.templateFields)"
                                :key="field.key"
                                :class="[
                                  'relative rounded-lg shadow-md overflow-hidden',
                                  index === 0 ? 'bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-300' :
                                  index === 1 ? 'bg-gradient-to-br from-green-50 to-green-100 border border-green-300' :
                                  index === 2 ? 'bg-gradient-to-br from-orange-50 to-orange-100 border border-orange-300' :
                                  'bg-gradient-to-br from-purple-50 to-purple-100 border border-purple-300'
                                ]"
                              >
                                <!-- 卡片头部 - 更紧凑 -->
                                <div :class="[
                                  'px-2 py-1 flex items-center space-x-1.5',
                                  index === 0 ? 'bg-gradient-to-r from-blue-500 to-blue-600' :
                                  index === 1 ? 'bg-gradient-to-r from-green-500 to-green-600' :
                                  index === 2 ? 'bg-gradient-to-r from-orange-500 to-orange-600' :
                                  'bg-gradient-to-r from-purple-500 to-purple-600'
                                ]">
                                  <div :class="[
                                    'w-5 h-5 rounded-full flex items-center justify-center font-bold text-xs',
                                    'bg-white/20 backdrop-blur-sm text-white'
                                  ]">
                                    {{ index + 1 }}
                                  </div>
                                  <h5 class="text-xs font-semibold text-white">{{ field.label }}</h5>
                                </div>

                                <!-- 卡片内容 - 增加输入框行数 -->
                                <div class="p-2">
                                  <textarea
                                    v-model="answerForm[field.key]"
                                    :placeholder="field.placeholder"
                                    :rows="index >= 2 ? 12 : 10"
                                    :class="[
                                      'w-full px-2 py-2 text-sm bg-white border rounded-lg',
                                      'focus:outline-none focus:ring-1 focus:ring-offset-1 resize-y',
                                      'placeholder:text-gray-400',
                                      index === 0 ? 'border-blue-200 focus:ring-blue-500 focus:border-blue-500' :
                                      index === 1 ? 'border-green-200 focus:ring-green-500 focus:border-green-500' :
                                      index === 2 ? 'border-orange-200 focus:ring-orange-500 focus:border-orange-500' :
                                      'border-purple-200 focus:ring-purple-500 focus:border-purple-500'
                                    ]"
                                  ></textarea>
                                </div>
                              </div>
                            </div>
                          </div>

                          <!-- 核心思路（所有题目类型，所有模式共享） -->
                          <div class="space-y-3 pt-4 border-t-2 border-gray-200">
                            <div class="flex items-center gap-2 mb-2">
                              <svg class="w-5 h-5 text-yellow-600" fill="currentColor" viewBox="0 0 20 20">
                                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                              </svg>
                              <label class="text-sm font-bold text-gray-800">
                                💡 核心思路
                              </label>
                              <span class="text-xs text-gray-500">
                                {{ selectedQuestion?.questionType === 'programming' ? '(算法要点、时间空间复杂度)' : '(关键思路、要点总结)' }}
                              </span>
                            </div>
                            <textarea
                              v-model="answerForm.coreStrategy"
                              :placeholder="coreStrategyPlaceholder"
                              rows="6"
                              class="w-full px-4 py-3 text-sm border-2 border-yellow-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-yellow-500 bg-yellow-50/30 resize-none"
                            ></textarea>
                            <p class="text-xs text-gray-500">
                              {{ selectedQuestion?.questionType === 'programming'
                                ? '记录算法思路、时间/空间复杂度、关键要点'
                                : '记录答题核心思路、关键要点、注意事项'
                              }}
                            </p>
                          </div>

                          <!-- 保存按钮（模版模式） -->
                          <div v-if="answerTemplate && answerTemplate.templateFields && answerMode === 'template'" class="flex justify-end gap-2 pt-4">
                            <button
                              @click="saveAnswer"
                              class="px-8 py-3 bg-gradient-to-r from-green-600 to-emerald-600 text-white text-sm font-bold rounded-lg hover:from-green-700 hover:to-emerald-700 shadow-md hover:shadow-lg transition-all duration-200 transform hover:scale-105"
                            >
                              💾 保存答案
                            </button>
                          </div>

                          <!-- 自由模式：自由文本输入 (无模版或用户选择自由模式) -->
                          <div v-if="!answerTemplate || answerMode === 'free'" class="space-y-4">
                            <div class="bg-gray-50 p-3 rounded-lg mb-4">
                              <h4 class="text-sm font-semibold text-gray-700">自由答题</h4>
                              <p class="text-xs text-gray-500 mt-1">请根据题目要求,自由发挥作答</p>
                            </div>

                            <textarea
                              v-model="answerForm.freeText"
                              placeholder="请输入你的答案..."
                              rows="12"
                              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                            ></textarea>

                            <!-- 保存按钮（自由模式） -->
                            <div class="flex justify-end gap-2 pt-4 border-t border-gray-200">
                              <button
                                @click="saveAnswer"
                                class="px-6 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors"
                              >
                                💾 保存答案
                              </button>
                            </div>
                          </div>
                        </div>
                      </template>
                      </div>
                    </div>
                    <!-- 编辑模式 -->
                    <div v-else-if="questionViewMode === 'edit'" class="flex-1 px-6 pb-6 overflow-y-auto">
                      <QuestionEditModal
                        :is-open="true"
                        :inline="true"
                        :question="editingQuestion"
                        :focus-areas="focusAreas"
                        :current-focus-area-id="selectedFocusAreaId"
                        :current-skill-id="selectedSkillId"
                        @save="saveQuestion"
                        @cancel="cancelEdit"
                      />
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </template>
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
          <!-- 如果是编辑模式，显示表单 -->
          <template v-if="editingKnowledgePoint">
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
          </template>
          <!-- 如果是添加模式，提示在卡片中编辑 -->
          <template v-else>
            <div class="bg-green-50 border border-green-200 rounded-lg p-6 text-center">
              <div class="flex justify-center mb-4">
                <svg class="w-16 h-16 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h4 class="text-lg font-semibold text-green-900 mb-2">新知识点已添加！</h4>
              <p class="text-sm text-green-700 mb-4">
                新卡片已自动展开，关闭此对话框后即可在卡片中直接编辑标题和内容
              </p>
              <div class="flex items-center justify-center gap-2 text-xs text-green-600 bg-green-100 rounded-md p-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span>输入内容后点击其他区域即可自动保存</span>
              </div>
            </div>
          </template>
        </div>
        <div class="px-6 py-4 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="closeKnowledgePointModal"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            {{ editingKnowledgePoint ? '取消' : '关闭' }}
          </button>
          <button
            v-if="editingKnowledgePoint"
            @click="saveKnowledgePointFromModal"
            :disabled="!knowledgePointForm.title.trim() || !knowledgePointForm.summary.trim()"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            保存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import careerPathApi from '@/api/careerPathApi'
import skillApi from '@/api/skillApi'
import majorCategoryApi from '@/api/majorCategoryApi'
import focusAreaApi from '@/api/focusAreaApi'
import learningContentApi from '@/api/learningContentApi'
import { questionApi, adminQuestionApi } from '@/api/questionApi'
import skillTemplateApi from '@/api/skillTemplateApi'
import { marked } from 'marked'
import VideoPlayer from '@/components/VideoPlayer.vue'
import QuestionEditModal from '@/components/questions/QuestionEditModal.vue'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/learning-contents')
// ✅ CORRECT: apiClient.get('/learning-contents')

const route = useRoute()
const { currentUser, isAdmin } = useAuth()

// State
const loadingCareerPaths = ref(false)
const loadingCategories = ref(false)
const loadingContents = ref(false)
const loadingQuestions = ref(false)

// 左侧树状态
const careerPaths = ref([])
const expandedCareerPaths = ref(new Set())
const selectedSkillId = ref(null)

const categories = ref([])
const expandedCategories = ref(new Set())
const focusAreas = ref([])
const selectedFocusAreaId = ref(null)

// 🚀 前端缓存：避免重复API调用
const categoriesCache = new Map()  // skillId -> categories
const focusAreasCache = new Map()  // skillId -> focusAreas

// 右侧Tab状态
const activeTab = ref('learning') // 'learning' or 'questions'
const learningContents = ref([])
const selectedContent = ref(null) // 当前选中的学习资料
const questions = ref([])
const selectedQuestion = ref(null) // 当前选中的试题
const questionViewMode = ref('browse') // 'browse', 'answer', or 'edit'
const browseNoteType = ref('ai') // 'ai' or 'user' - 浏览模式下显示的笔记类型
const answerNoteType = ref('user') // 'ai' or 'user' - 答题模式下编辑的笔记类型
const answerTemplate = ref(null) // 当前技能的答题模版

// 分页相关
const questionPagination = ref({
  currentPage: 0,
  pageSize: 10,  // 每页10题
  totalElements: 0,
  totalPages: 0,
  hasNext: false,
  hasPrevious: false
})

// 判断当前试题是否可以编辑
const canEditQuestion = computed(() => {
  if (!selectedQuestion.value) return false

  // 管理员可以编辑所有试题
  if (isAdmin.value) return true

  // 公共试题（isOfficial = true）普通用户不能编辑
  if (selectedQuestion.value.isOfficial) return false

  // 私有试题只能由创建者编辑
  return selectedQuestion.value.createdByUserId === currentUser.value?.id
})

// 判断当前用户是否可以添加/编辑AI笔记
const canEditAiNote = computed(() => {
  if (!selectedQuestion.value) return false

  // 管理员可以编辑所有试题的AI笔记
  if (isAdmin.value) return true

  // 普通用户只能为自己创建的试题添加AI笔记
  return selectedQuestion.value.createdByUserId === currentUser.value?.id
})
const answerForm = ref({}) // 答题表单数据
const answerMode = ref('template') // 'template' or 'free' - 答题模式
const editingQuestion = ref(null) // 正在编辑的试题

// 卡片折叠状态
const cardStates = ref({
  resourceInfo: true,  // 学习资料卡片默认展开
  aiNote: true,        // AI整体笔记卡片默认展开
  overallNote: true,   // 用户整体笔记卡片默认展开
  aiKnowledgePoints: {},  // AI知识点卡片折叠状态 {id: boolean}
  knowledgePoints: {},  // 用户知识点卡片折叠状态 {id: boolean}
  // 卡片内容展开状态
  aiNoteExpanded: false,        // AI整体笔记内容是否展开
  overallNoteExpanded: false,   // 用户整体笔记内容是否展开
  aiKnowledgePointsExpanded: {},   // AI知识点内容展开状态 {id: boolean}
  knowledgePointsExpanded: {},   // 用户知识点内容展开状态 {id: boolean}
  // 卡片内容是否溢出（需要展开按钮）
  aiNoteOverflow: false,        // AI整体笔记内容是否溢出
  overallNoteOverflow: false,   // 用户整体笔记内容是否溢出
  aiKnowledgePointsOverflow: {},   // AI知识点内容溢出状态 {id: boolean}
  knowledgePointsOverflow: {}   // 用户知识点内容溢出状态 {id: boolean}
})

// 知识点模态框
const showAddKnowledgePointModal = ref(false)

// 笔记编辑状态
const isEditingNote = ref(false)
const noteContent = ref('')

// 知识点编辑状态
const editingKnowledgePoint = ref(null)
const knowledgePointForm = ref({
  title: '',
  summary: ''
})

// 文档全屏查看状态
const isDocumentMaximized = ref(false)
const iframeLoadError = ref(false)

// 卡片最大化状态
const maximizedCard = ref(null) // 'aiNote' | 'userNote' | 'aiKnowledgePoint-{id}' | 'userKnowledgePoint-{id}' | null

// 左侧栏折叠状态
const isSidebarCollapsed = ref(false)

// 试题列表折叠状态
const isQuestionListCollapsed = ref(false)

// DOM引用
const aiNoteContent = ref(null)
const overallNoteContent = ref(null)
const aiKnowledgePointContents = ref({})
const knowledgePointContents = ref({})

// Computed
const isSecondTypeSkill = computed(() => {
  // ⚠️ Phase 6关键逻辑: 如果只有一个大分类且名称为"General",则为第二类技能
  if (!selectedSkillId.value || categories.value.length === 0) {
    return false
  }
  return categories.value.length === 1 && categories.value[0].name === 'General'
})

const selectedFocusArea = computed(() => {
  return focusAreas.value.find(fa => fa.id === selectedFocusAreaId.value)
})

// Placeholder for core strategy textarea
const coreStrategyPlaceholder = computed(() => {
  if (selectedQuestion.value?.questionType === 'programming') {
    return '记录解题的核心思路和算法要点...\n\n例如：\n1. 使用双指针从两端向中间移动\n2. 时间复杂度 O(n)，空间复杂度 O(1)\n3. 注意边界条件...\n\n支持 Markdown 格式'
  } else {
    return '记录答题的核心思路和关键要点...\n\n例如：\n1. 明确问题核心诉求\n2. 关键解决步骤\n3. 注意事项...\n\n支持 Markdown 格式'
  }
})

// Methods

// 检查学习资料是否支持iframe嵌入
// 逻辑：如果learning_content有website_id关联，使用网站的supports_iframe配置；否则默认支持iframe
const isSupportIframe = (learningContent) => {
  if (!learningContent || !learningContent.url) {
    return true // 没有URL，默认支持（使用contentData）
  }

  // 如果有supportsIframe字段（从后端返回），直接使用
  if (learningContent.supportsIframe !== undefined && learningContent.supportsIframe !== null) {
    return learningContent.supportsIframe
  }

  // 如果没有website_id关联，默认支持iframe
  return true
}

// 加载职业路径
const loadCareerPaths = async () => {
  try {
    loadingCareerPaths.value = true
    // Note: interceptor returns response.data already
    const data = await careerPathApi.getCareerPaths()
    careerPaths.value = data || []
  } catch (error) {
    console.error('Failed to load career paths:', error)
    careerPaths.value = []
  } finally {
    loadingCareerPaths.value = false
  }
}

// 切换职业路径展开/折叠
const toggleCareerPath = (careerPathId) => {
  if (expandedCareerPaths.value.has(careerPathId)) {
    expandedCareerPaths.value.delete(careerPathId)
  } else {
    expandedCareerPaths.value.add(careerPathId)
  }
}

// 选择技能
const selectSkill = async (skillId) => {
  selectedSkillId.value = skillId
  selectedFocusAreaId.value = null
  categories.value = []
  focusAreas.value = []
  expandedCategories.value = new Set()

  // 加载该技能的大分类和Focus Area
  await loadCategoriesAndFocusAreas()

  // 加载该技能的答题模版
  await loadSkillTemplate(skillId)
}

// 加载技能的答题模版
const loadSkillTemplate = async (skillId) => {
  try {
    // 获取技能的默认模版
    const template = await skillTemplateApi.getDefaultTemplatePublic(skillId)
    answerTemplate.value = template
  } catch (error) {
    console.error('Failed to load skill template:', error)
    answerTemplate.value = null
  }
}

const loadCategoriesAndFocusAreas = async () => {
  if (!selectedSkillId.value) return

  try {
    loadingCategories.value = true

    // 🚀 关键优化1：检查缓存
    const skillId = selectedSkillId.value
    if (categoriesCache.has(skillId) && focusAreasCache.has(skillId)) {
      // 从缓存读取（瞬间完成）
      categories.value = categoriesCache.get(skillId)
      focusAreas.value = focusAreasCache.get(skillId)

      // 如果是第一类技能，自动展开第一个大分类
      if (!isSecondTypeSkill.value && categories.value.length > 0) {
        expandedCategories.value.add(categories.value[0].id)
      }

      loadingCategories.value = false
      return
    }

    // 🚀 关键优化2：并行加载大分类和Focus Area（不再串行等待）
    const [categoriesData, focusAreasData] = await Promise.all([
      majorCategoryApi.getAllMajorCategories(skillId),
      majorCategoryApi.getFocusAreasWithCategories(skillId)
    ])

    categories.value = categoriesData || []
    focusAreas.value = focusAreasData || []

    // 存入缓存
    categoriesCache.set(skillId, categories.value)
    focusAreasCache.set(skillId, focusAreas.value)

    // 如果是第一类技能，自动展开第一个大分类
    if (!isSecondTypeSkill.value && categories.value.length > 0) {
      expandedCategories.value.add(categories.value[0].id)
    }
  } catch (error) {
    console.error('Failed to load categories and focus areas:', error)
    categories.value = []
    focusAreas.value = []
  } finally {
    loadingCategories.value = false
  }
}

// 切换大分类展开/折叠
const toggleCategory = (categoryId) => {
  if (expandedCategories.value.has(categoryId)) {
    expandedCategories.value.delete(categoryId)
  } else {
    expandedCategories.value.add(categoryId)
  }
}

// 根据大分类获取Focus Area
const getFocusAreasByCategory = (categoryId) => {
  return focusAreas.value.filter(fa =>
    fa.categoryIds && fa.categoryIds.includes(categoryId)
  )
}

// 选择Focus Area
const selectFocusArea = (focusAreaId) => {
  selectedFocusAreaId.value = focusAreaId
  // ✅ 优化: 只加载当前活跃tab的数据（懒加载）
  if (activeTab.value === 'learning') {
    loadLearningContents()
  } else if (activeTab.value === 'questions') {
    loadQuestions()
  }
}

/**
 * 加载单个学习资料的详细信息（笔记和知识点）
 * ✅ 优化: 只在用户选择查看某个资料时才加载，避免列表加载时的N+1查询
 */
const loadContentDetails = async (content) => {
  if (!content || content._detailsLoaded) {
    // 如果已经加载过详情，跳过
    return
  }

  try {
    // 获取AI笔记 + 用户笔记
    const notes = await learningContentApi.getNotes(content.id)
    content.aiNote = notes.aiNote || null
    content.userNote = notes.userNote || { noteContent: '' }

    // 获取AI知识点 + 用户知识点
    const kps = await learningContentApi.getKnowledgePoints(content.id)
    content.aiKnowledgePoints = kps.aiKnowledgePoints || []
    content.userKnowledgePoints = kps.userKnowledgePoints || []

    // 标记已加载详情
    content._detailsLoaded = true
  } catch (error) {
    console.error('Failed to load details for content', content.id, error)
    // 即使获取失败也要初始化为空，避免渲染错误
    content.aiNote = null
    content.userNote = { noteContent: '' }
    content.aiKnowledgePoints = []
    content.userKnowledgePoints = []
  }
}

const loadLearningContents = async () => {
  if (!selectedFocusAreaId.value) return

  try {
    loadingContents.value = true
    // Note: interceptor returns response.data already
    const data = await learningContentApi.getContentsByFocusArea(selectedFocusAreaId.value)

    // 后端返回的数据结构: { focusArea: {...}, stages: [...] }
    // 需要从stages中提取所有学习内容
    const allContents = []
    if (data.stages && Array.isArray(data.stages)) {
      for (const stage of data.stages) {
        if (stage.contents && Array.isArray(stage.contents)) {
          allContents.push(...stage.contents)
        }
      }
    }

    // ✅ 优化: 列表加载时不再预加载所有笔记和知识点
    // 只在用户选择单个学习资料时才加载（详情懒加载）

    learningContents.value = allContents
    // 自动选中第一个学习资料（会触发loadContentDetails）
    if (allContents.length > 0) {
      const firstContent = allContents[0]
      selectedContent.value = firstContent
      // 加载第一个资料的笔记和知识点
      await loadContentDetails(firstContent)

      // 初始化AI知识点卡片折叠状态（默认展开卡片，内容折叠显示max-h-48）
      if (firstContent.aiKnowledgePoints) {
        firstContent.aiKnowledgePoints.forEach((kp, index) => {
          const key = 'ai-' + (kp.id || index)
          cardStates.value.aiKnowledgePoints[key] = true  // 卡片展开
          cardStates.value.aiKnowledgePointsExpanded[key] = false  // 内容初始折叠（显示max-h-48高度）
        })
      }

      // 初始化用户知识点卡片折叠状态（默认展开）
      if (firstContent.userKnowledgePoints) {
        firstContent.userKnowledgePoints.forEach((kp, index) => {
          cardStates.value.knowledgePoints[kp.id || index] = true
          cardStates.value.knowledgePointsExpanded[kp.id || index] = false
        })
      }

      // 优先显示卡片视图的情况：
      // 1. 视频类型
      // 2. 有AI笔记或AI知识点（便于查看学习笔记）
      // 3. 不支持iframe
      const hasAIContent = firstContent.aiNote || (firstContent.aiKnowledgePoints && firstContent.aiKnowledgePoints.length > 0)
      const shouldShowCards = firstContent.contentType === 'video' || hasAIContent || !isSupportIframe(firstContent)
      isDocumentMaximized.value = !shouldShowCards

      // 检测内容溢出 - 使用延迟以等待 Markdown 渲染完成
      await nextTick()
      // 延迟检测，确保 Markdown 已完全渲染到 DOM
      setTimeout(() => {
        cardStates.value.aiNoteOverflow = checkOverflow(aiNoteContent.value)
        cardStates.value.overallNoteOverflow = checkOverflow(overallNoteContent.value)

        // 检测AI知识点溢出
        if (firstContent.aiKnowledgePoints) {
          firstContent.aiKnowledgePoints.forEach((kp, index) => {
            const element = aiKnowledgePointContents.value['ai-' + (kp.id || index)]
            cardStates.value.aiKnowledgePointsOverflow['ai-' + (kp.id || index)] = checkOverflow(element)
          })
        }

        // 检测用户知识点溢出
        if (firstContent.userKnowledgePoints) {
          firstContent.userKnowledgePoints.forEach((kp, index) => {
            const element = knowledgePointContents.value[kp.id || index]
            cardStates.value.knowledgePointsOverflow[kp.id || index] = checkOverflow(element)
          })
        }
      }, 200) // 200ms 延迟，确保 Markdown 渲染完成
    } else {
      selectedContent.value = null
      isDocumentMaximized.value = false
    }
  } catch (error) {
    console.error('Failed to load learning contents:', error)
    learningContents.value = []
    selectedContent.value = null
  } finally {
    loadingContents.value = false
  }
}

// 下拉选择学习资料变化处理
const onContentSelectChange = async (event) => {
  const contentId = Number(event.target.value)
  const content = learningContents.value.find(c => c.id === contentId)
  if (content) {
    selectedContent.value = content
    // ✅ 优化: 懒加载笔记和知识点
    await loadContentDetails(content)

    // 重置卡片状态
    cardStates.value.knowledgePoints = {}
    cardStates.value.overallNoteExpanded = false
    cardStates.value.knowledgePointsExpanded = {}
    cardStates.value.overallNoteOverflow = false
    cardStates.value.knowledgePointsOverflow = {}

    // 重置编辑状态
    isEditingNote.value = false
    noteContent.value = content.userNote?.noteContent || ''

    // 根据内容类型和URL设置显示模式：文档类型+支持iframe → iframe模式；视频或不支持iframe → 卡片视图
    isDocumentMaximized.value = content.contentType !== 'video' && isSupportIframe(content)
    iframeLoadError.value = false

    // 初始化AI知识点卡片折叠状态（默认展开）
    if (content.aiKnowledgePoints) {
      content.aiKnowledgePoints.forEach((kp, index) => {
        const key = 'ai-' + (kp.id || index)
        cardStates.value.aiKnowledgePoints[key] = true
        cardStates.value.aiKnowledgePointsExpanded[key] = false
      })
    }

    // 初始化用户知识点卡片折叠状态（默认展开）
    if (content.userKnowledgePoints) {
      content.userKnowledgePoints.forEach((kp, index) => {
        cardStates.value.knowledgePoints[kp.id || index] = true
        cardStates.value.knowledgePointsExpanded[kp.id || index] = false
      })
    }

    // 检测内容溢出 - 使用延迟以等待 Markdown 渲染完成
    await nextTick()
    setTimeout(() => {
      // 检测AI笔记溢出
      cardStates.value.aiNoteOverflow = checkOverflow(aiNoteContent.value)
      // 检测整体笔记溢出
      cardStates.value.overallNoteOverflow = checkOverflow(overallNoteContent.value)
      // 检测AI知识点溢出
      if (content.aiKnowledgePoints) {
        content.aiKnowledgePoints.forEach((kp, index) => {
          const element = aiKnowledgePointContents.value['ai-' + (kp.id || index)]
          cardStates.value.aiKnowledgePointsOverflow['ai-' + (kp.id || index)] = checkOverflow(element)
        })
      }
      // 检测用户知识点溢出
      if (content.userKnowledgePoints) {
        content.userKnowledgePoints.forEach((kp, index) => {
          const element = knowledgePointContents.value[kp.id || index]
          cardStates.value.knowledgePointsOverflow[kp.id || index] = checkOverflow(element)
        })
      }
    }, 200)
  }
}

// 切换AI知识点卡片折叠状态
const toggleAiKnowledgePointCard = (pointKey) => {
  if (cardStates.value.aiKnowledgePoints[pointKey] === undefined) {
    cardStates.value.aiKnowledgePoints[pointKey] = true
  } else {
    cardStates.value.aiKnowledgePoints[pointKey] = !cardStates.value.aiKnowledgePoints[pointKey]
  }
}

// 切换用户知识点卡片折叠状态
const toggleKnowledgePointCard = (pointKey) => {
  if (cardStates.value.knowledgePoints[pointKey] === undefined) {
    cardStates.value.knowledgePoints[pointKey] = true
  } else {
    cardStates.value.knowledgePoints[pointKey] = !cardStates.value.knowledgePoints[pointKey]
  }
}

const loadQuestions = async (page = 0) => {
  if (!selectedFocusAreaId.value) return

  try {
    loadingQuestions.value = true
    // ✅ 优化: 使用分页API，只加载QuestionListDTO（不包含description、笔记、编程题详情）
    const response = await questionApi.getQuestionsByFocusArea(selectedFocusAreaId.value, { page, size: 10 })
    questions.value = response.content || []

    // 保存分页信息
    questionPagination.value = {
      currentPage: response.currentPage || 0,
      pageSize: response.pageSize || 10,
      totalElements: response.totalElements || 0,
      totalPages: response.totalPages || 0,
      hasNext: response.hasNext || false,
      hasPrevious: response.hasPrevious || false
    }

    // 自动选中第一道试题（需要加载详情）
    if (questions.value.length > 0) {
      await selectQuestion(questions.value[0])
    } else {
      selectedQuestion.value = null
    }
  } catch (error) {
    console.error('Failed to load questions:', error)
    questions.value = []
    selectedQuestion.value = null
  } finally {
    loadingQuestions.value = false
  }
}

// 选择试题
const selectQuestion = async (question) => {
  // ✅ 优化: 懒加载试题详情（只在用户点击试题时才加载description、笔记、编程题详情）
  if (!question._detailsLoaded) {
    try {
      // 从列表项（QuestionListDTO）加载完整详情（QuestionDTO）
      const fullQuestion = await questionApi.getQuestionById(question.id)

      // 将详情合并到列表项中
      Object.assign(question, fullQuestion)
      question._detailsLoaded = true
    } catch (error) {
      console.error('Failed to load question details:', error)
      // 即使失败也要设置默认值，避免渲染错误
      question.questionDescription = question.questionDescription || ''
      question.aiNote = null
      question.userNote = { noteContent: '', coreStrategy: '' }
    }
  }

  selectedQuestion.value = question
  questionViewMode.value = 'browse'

  // 设置浏览笔记类型（优先AI笔记）
  if (question.aiNote?.noteContent) {
    browseNoteType.value = 'ai'
  } else if (question.userNote?.noteContent || question.userNote?.coreStrategy) {
    browseNoteType.value = 'user'
  }

  // 初始化答题表单
  initAnswerForm(question)
}

// 初始化答题表单
const initAnswerForm = (question, noteSource = null) => {
  // 如果有模版,根据模版字段初始化表单
  if (answerTemplate.value && answerTemplate.value.templateFields) {
    const fields = {}
    try {
      const templateFields = typeof answerTemplate.value.templateFields === 'string'
        ? JSON.parse(answerTemplate.value.templateFields)
        : answerTemplate.value.templateFields

      templateFields.forEach(field => {
        fields[field.key] = ''
      })
    } catch (error) {
      console.error('Failed to parse template fields:', error)
    }
    answerForm.value = { ...fields, coreStrategy: '' }
  } else {
    // 没有模版,使用自由文本
    answerForm.value = { freeText: '', coreStrategy: '' }
  }

  // 确定要加载的笔记来源
  // 如果显式传入noteSource，使用它；否则根据当前模式决定
  let noteToLoad = noteSource
  if (!noteToLoad) {
    noteToLoad = answerNoteType.value === 'ai' ? question.aiNote : question.note
  }

  // 如果已有答题笔记,加载到表单中
  if (noteToLoad) {
    const noteContent = noteToLoad.noteContent || ''
    const coreStrategy = noteToLoad.coreStrategy || ''

    // 先加载核心思路
    answerForm.value.coreStrategy = coreStrategy

    // 再解析笔记内容
    if (noteContent) {
      // 尝试解析模版格式（Markdown 格式：## 标题\n内容）
      if (answerTemplate.value && answerTemplate.value.templateFields) {
        const templateFields = parseTemplateFields(answerTemplate.value.templateFields)
        let isTemplateFormat = true
        const parsedValues = {}

        // 尝试解析模版格式
        for (const field of templateFields) {
          // 转义正则表达式中的特殊字符
          const escapedLabel = field.label.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
          const pattern = new RegExp(`## ${escapedLabel}\\s*\\n([\\s\\S]*?)(?=\\n## |$)`, 'i')
          const match = noteContent.match(pattern)

          if (match) {
            parsedValues[field.key] = match[1].trim()
          } else {
            isTemplateFormat = false
            break
          }
        }

        if (isTemplateFormat) {
          // 是模版格式，填充到模版字段
          answerForm.value = { ...answerForm.value, ...parsedValues, coreStrategy }
          answerMode.value = 'template'
        } else {
          // 不是模版格式，作为自由文本
          answerForm.value.freeText = noteContent
          answerMode.value = 'free'
        }
      } else {
        // 没有模版，直接作为自由文本
        answerForm.value.freeText = noteContent
      }
    }
  }
}

// 保存答题笔记
const saveAnswer = async () => {
  if (!selectedQuestion.value) return

  try {
    let noteContent = ''
    const coreStrategy = answerForm.value.coreStrategy || ''

    // 模版模式：将模版字段合并为 Markdown 格式
    if (answerMode.value === 'template' && answerTemplate.value && answerTemplate.value.templateFields) {
      const templateFields = parseTemplateFields(answerTemplate.value.templateFields)
      const hasAnyValue = templateFields.some(field => answerForm.value[field.key] && answerForm.value[field.key].trim())

      if (!hasAnyValue && !coreStrategy.trim()) {
        alert('请至少填写一项内容')
        return
      }

      const parts = templateFields.map(field => {
        const value = answerForm.value[field.key] || ''
        return `## ${field.label}\n${value}`
      })
      noteContent = parts.join('\n\n')
    } else {
      // 自由模式：保存自由文本
      noteContent = answerForm.value.freeText || ''

      if (!noteContent.trim() && !coreStrategy.trim()) {
        alert('请输入答案内容或核心思路')
        return
      }
    }

    // 根据答题身份选择API
    if (answerNoteType.value === 'ai') {
      // 保存AI笔记（需要管理员权限或创建者权限）
      await adminQuestionApi.saveOrUpdateAINote(selectedQuestion.value.id, {
        noteContent,
        coreStrategy
      })

      // 更新本地AI笔记数据
      if (selectedQuestion.value.aiNote) {
        selectedQuestion.value.aiNote.noteContent = noteContent
        selectedQuestion.value.aiNote.coreStrategy = coreStrategy
      } else {
        selectedQuestion.value.aiNote = { noteContent, coreStrategy }
      }

      alert('AI参考答案保存成功！')
    } else {
      // 保存用户笔记
      await questionApi.saveOrUpdateNote(selectedQuestion.value.id, {
        noteContent,
        coreStrategy
      })

      // 更新本地用户笔记数据
      if (selectedQuestion.value.note) {
        selectedQuestion.value.note.noteContent = noteContent
        selectedQuestion.value.note.coreStrategy = coreStrategy
      } else {
        selectedQuestion.value.note = { noteContent, coreStrategy }
      }

      // 更新questions列表中的note标记
      const questionIndex = questions.value.findIndex(q => q.id === selectedQuestion.value.id)
      if (questionIndex !== -1) {
        questions.value[questionIndex].note = selectedQuestion.value.note
      }

      alert('答题笔记保存成功！')
    }
  } catch (error) {
    console.error('Failed to save answer:', error)
    alert('保存失败: ' + (error.message || '未知错误'))
  }
}

// 添加新试题
const addNewQuestion = () => {
  editingQuestion.value = {
    id: null,
    title: '',
    questionType: 'behavioral',
    questionDescription: '',
    answerRequirement: '',
    focusAreaId: selectedFocusAreaId.value || '',  // 预填当前选中的Focus Area
    difficulty: 'EASY',
    isPublic: false
  }
  selectedQuestion.value = editingQuestion.value
  questionViewMode.value = 'edit'
}

// 保存试题
const saveQuestion = async (questionData) => {
  try {
    // 调用API保存
    const savedQuestion = await questionApi.saveOrUpdateQuestion(questionData)

    // 更新本地列表
    if (questionData.id) {
      // 编辑现有试题
      const index = questions.value.findIndex(q => q.id === questionData.id)
      if (index !== -1) {
        questions.value[index] = savedQuestion
      }
    } else {
      // 新建试题
      questions.value.unshift(savedQuestion)
    }

    // 选中新保存的试题并切换到浏览模式
    selectedQuestion.value = savedQuestion
    editingQuestion.value = null
    questionViewMode.value = 'browse'

    alert('试题保存成功！')
  } catch (error) {
    console.error('Failed to save question:', error)
    alert('保存失败: ' + (error.message || '未知错误'))
  }
}

// 取消编辑
const cancelEdit = () => {
  if (!editingQuestion.value?.id) {
    // 如果是新建试题，返回空状态
    selectedQuestion.value = null
  }
  editingQuestion.value = null
  questionViewMode.value = 'browse'
}

const saveUserNote = async (contentId) => {
  try {
    const content = learningContents.value.find(c => c.id === contentId)
    if (!content || !content.userNote) return

    // Note: interceptor returns response.data already
    const savedNote = await learningContentApi.saveOrUpdateNote(contentId, content.userNote.noteContent)
    console.log('User note saved:', savedNote)
  } catch (error) {
    console.error('Failed to save user note:', error)
    alert('保存笔记失败: ' + (error.message || '未知错误'))
  }
}

const addKnowledgePoint = (contentId) => {
  const content = learningContents.value.find(c => c.id === contentId)
  if (content) {
    content.userKnowledgePoints.push({
      id: null, // 新知识点暂无ID
      title: '',
      summary: ''
    })
  }
}

const saveKnowledgePoint = async (contentId, kp) => {
  try {
    // 只有标题和内容都有值才保存
    if (!kp.title.trim() || !kp.summary.trim()) {
      return
    }

    // Note: interceptor returns response.data already
    const savedKp = await learningContentApi.saveOrUpdateKnowledgePoint(contentId, kp)

    // 更新ID（如果是新建的）
    if (!kp.id && savedKp.id) {
      kp.id = savedKp.id
    }
    console.log('Knowledge point saved:', savedKp)
  } catch (error) {
    console.error('Failed to save knowledge point:', error)
    alert('保存知识点失败: ' + (error.message || '未知错误'))
  }
}

const deleteKnowledgePoint = async (contentId, kpId) => {
  if (!kpId) {
    // 如果是未保存的知识点（没有ID），直接从数组删除
    const content = learningContents.value.find(c => c.id === contentId)
    if (content) {
      content.userKnowledgePoints = content.userKnowledgePoints.filter(kp => kp.id !== null)
    }
    return
  }

  try {
    if (!confirm('确定要删除这个知识点吗？')) return

    // Note: interceptor returns response.data already
    await learningContentApi.deleteKnowledgePoint(kpId)

    // 从数组中删除
    const content = learningContents.value.find(c => c.id === contentId)
    if (content) {
      content.userKnowledgePoints = content.userKnowledgePoints.filter(kp => kp.id !== kpId)
    }
    console.log('Knowledge point deleted:', kpId)
  } catch (error) {
    console.error('Failed to delete knowledge point:', error)
    alert('删除知识点失败: ' + (error.message || '未知错误'))
  }
}


// 直接添加新知识点卡片（不显示模态框）
const addNewKnowledgePointCard = () => {
  if (!selectedContent.value) return

  // 立即添加一个空知识点到当前学习资料
  addKnowledgePoint(selectedContent.value.id)

  // 自动展开新添加的知识点卡片（使用index作为key）
  nextTick(() => {
    const newIndex = selectedContent.value.userKnowledgePoints.length - 1
    if (newIndex >= 0) {
      // 展开新卡片
      cardStates.value.knowledgePoints[newIndex] = true

      // 滚动到新卡片位置（可选）
      setTimeout(() => {
        const cardElements = document.querySelectorAll('.bg-gradient-to-br.from-purple-50')
        if (cardElements && cardElements.length > 0) {
          const lastCard = cardElements[cardElements.length - 1]
          lastCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
        }
      }, 100)
    }
  })
}

const closeAddKnowledgePointModal = () => {
  showAddKnowledgePointModal.value = false
  // 自动添加一个空知识点到当前学习资料
  if (selectedContent.value) {
    addKnowledgePoint(selectedContent.value.id)

    // 自动展开新添加的知识点卡片（使用index作为key）
    nextTick(() => {
      const newIndex = selectedContent.value.userKnowledgePoints.length - 1
      if (newIndex >= 0) {
        // 展开新卡片
        cardStates.value.knowledgePoints[newIndex] = true
      }
    })
  }
}

// Markdown渲染
const renderMarkdown = (content) => {
  if (!content) return ''
  return marked(content, { breaks: true })
}

// 解析模版字段（JSON字符串 → 数组对象）
const parseTemplateFields = (templateFields) => {
  if (!templateFields) return []
  try {
    return typeof templateFields === 'string' ? JSON.parse(templateFields) : templateFields
  } catch (error) {
    console.error('Failed to parse template fields:', error)
    return []
  }
}

// 检测内容是否溢出
const checkOverflow = (element) => {
  if (!element) return false
  return element.scrollHeight > element.clientHeight
}

// 开始编辑笔记
const startEditNote = () => {
  isEditingNote.value = true
  noteContent.value = selectedContent.value?.userNote?.noteContent || ''
}

// 取消编辑笔记
const cancelEditNote = () => {
  isEditingNote.value = false
  noteContent.value = selectedContent.value?.userNote?.noteContent || ''
}

// 保存笔记（新版，用于编辑模式）
const saveNote = async () => {
  if (!noteContent.value.trim() || !selectedContent.value) return

  try {
    const savedNote = await learningContentApi.saveOrUpdateNote(selectedContent.value.id, noteContent.value)
    // 更新当前选中内容的userNote
    if (selectedContent.value.userNote) {
      selectedContent.value.userNote.noteContent = noteContent.value
    } else {
      selectedContent.value.userNote = { noteContent: noteContent.value }
    }
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
  if (!selectedContent.value || !confirm('确定要删除这条笔记吗？')) return

  try {
    await learningContentApi.deleteUserNote(selectedContent.value.id)
    if (selectedContent.value.userNote) {
      selectedContent.value.userNote.noteContent = ''
    }
    noteContent.value = ''
    isEditingNote.value = false
  } catch (error) {
    console.error('Failed to delete note:', error)
    alert('删除失败')
  }
}

// 编辑知识点（打开编辑模式）
const editKnowledgePoint = (point) => {
  editingKnowledgePoint.value = point
  knowledgePointForm.value = {
    title: point.title,
    summary: point.summary
  }
  showAddKnowledgePointModal.value = true
}

// 保存知识点（从模态框）
const saveKnowledgePointFromModal = async () => {
  if (!knowledgePointForm.value.title.trim() || !knowledgePointForm.value.summary.trim()) {
    return
  }

  if (!selectedContent.value) return

  try {
    if (editingKnowledgePoint.value) {
      // 更新已有知识点
      const savedKp = await learningContentApi.saveOrUpdateKnowledgePoint(
        selectedContent.value.id,
        {
          id: editingKnowledgePoint.value.id,
          title: knowledgePointForm.value.title,
          summary: knowledgePointForm.value.summary
        }
      )
      // 更新本地数据
      Object.assign(editingKnowledgePoint.value, savedKp)
    }

    // 关闭模态框
    showAddKnowledgePointModal.value = false
    editingKnowledgePoint.value = null
    knowledgePointForm.value = { title: '', summary: '' }

    // 检测知识点内容是否溢出
    await nextTick()
    selectedContent.value.userKnowledgePoints.forEach((kp, index) => {
      const element = knowledgePointContents.value[kp.id || index]
      cardStates.value.knowledgePointsOverflow[kp.id || index] = checkOverflow(element)
    })
  } catch (error) {
    console.error('Failed to save knowledge point:', error)
    alert('保存失败')
  }
}

// 关闭知识点模态框
const closeKnowledgePointModal = () => {
  showAddKnowledgePointModal.value = false
  editingKnowledgePoint.value = null
  knowledgePointForm.value = { title: '', summary: '' }
}

// 最大化文档查看
const maximizeDocument = () => {
  isDocumentMaximized.value = true
  iframeLoadError.value = false
}

// 最小化文档查看
const minimizeDocument = async () => {
  isDocumentMaximized.value = false
  iframeLoadError.value = false

  // 切换到卡片视图后，重新检测内容溢出 - 使用延迟
  await nextTick()
  setTimeout(() => {
    // 检测AI笔记溢出
    cardStates.value.aiNoteOverflow = checkOverflow(aiNoteContent.value)
    // 检测整体笔记溢出
    cardStates.value.overallNoteOverflow = checkOverflow(overallNoteContent.value)
    // 检测AI知识点溢出
    if (selectedContent.value?.aiKnowledgePoints) {
      selectedContent.value.aiKnowledgePoints.forEach((kp, index) => {
        const element = aiKnowledgePointContents.value['ai-' + (kp.id || index)]
        cardStates.value.aiKnowledgePointsOverflow['ai-' + (kp.id || index)] = checkOverflow(element)
      })
    }
    // 检测用户知识点溢出
    if (selectedContent.value?.userKnowledgePoints) {
      selectedContent.value.userKnowledgePoints.forEach((kp, index) => {
        const element = knowledgePointContents.value[kp.id || index]
        cardStates.value.knowledgePointsOverflow[kp.id || index] = checkOverflow(element)
      })
    }
  }, 200)
}

// 最大化卡片
const maximizeCard = (cardType) => {
  maximizedCard.value = cardType
}

// 最小化卡片
const minimizeCard = () => {
  maximizedCard.value = null
}

// 处理iframe加载错误 - 自动回退到卡片视图
const handleIframeError = async () => {
  iframeLoadError.value = true
  // 网站不支持iframe，自动切换到卡片视图
  isDocumentMaximized.value = false

  // 切换到卡片视图后，重新检测内容溢出 - 使用延迟
  await nextTick()
  setTimeout(() => {
    // 检测AI笔记溢出
    cardStates.value.aiNoteOverflow = checkOverflow(aiNoteContent.value)
    // 检测整体笔记溢出
    cardStates.value.overallNoteOverflow = checkOverflow(overallNoteContent.value)
    // 检测AI知识点溢出
    if (selectedContent.value?.aiKnowledgePoints) {
      selectedContent.value.aiKnowledgePoints.forEach((kp, index) => {
        const element = aiKnowledgePointContents.value['ai-' + (kp.id || index)]
        cardStates.value.aiKnowledgePointsOverflow['ai-' + (kp.id || index)] = checkOverflow(element)
      })
    }
    // 检测用户知识点溢出
    if (selectedContent.value?.userKnowledgePoints) {
      selectedContent.value.userKnowledgePoints.forEach((kp, index) => {
        const element = knowledgePointContents.value[kp.id || index]
        cardStates.value.knowledgePointsOverflow[kp.id || index] = checkOverflow(element)
      })
    }
  }, 200)
}

// Watch activeTab切换
watch(activeTab, (newTab) => {
  if (!selectedFocusAreaId.value) return

  if (newTab === 'learning') {
    loadLearningContents()
  } else if (newTab === 'questions') {
    loadQuestions()
  }
})

// 初始化
const init = async () => {
  await loadCareerPaths()

  // 如果URL包含skillId参数，自动选中该技能
  const urlSkillId = route.params.skillId ? parseInt(route.params.skillId) : null
  if (urlSkillId) {
    // 自动展开包含该技能的职业路径
    for (const cp of careerPaths.value) {
      if (cp.skills && cp.skills.some(s => s.id === urlSkillId)) {
        expandedCareerPaths.value.add(cp.id)
        break
      }
    }
    selectedSkillId.value = urlSkillId
    await loadCategoriesAndFocusAreas()
  }
}

// Watch questionViewMode - 当切换模式时执行相应初始化
watch(questionViewMode, (newMode) => {
  if (newMode === 'answer' && selectedQuestion.value) {
    // 切换到答题模式：重新初始化表单以加载现有答案
    initAnswerForm(selectedQuestion.value)
  } else if (newMode === 'edit' && selectedQuestion.value) {
    // 切换到编辑模式：复制selectedQuestion到editingQuestion
    editingQuestion.value = { ...selectedQuestion.value }
  }
})

// Watch answerNoteType - 当切换答题身份时重新加载对应笔记
watch(answerNoteType, (newType) => {
  if (!selectedQuestion.value || questionViewMode.value !== 'answer') return

  // 根据新的答题身份，加载对应的笔记数据
  const noteToLoad = newType === 'ai' ? selectedQuestion.value.aiNote : selectedQuestion.value.note
  initAnswerForm(selectedQuestion.value, noteToLoad)
})

init()
</script>

<style scoped>
/* 垂直文本样式 */
.vertical-text {
  writing-mode: vertical-rl;
  text-orientation: upright;
}

/* 紧凑模式 Markdown 渲染 - 减少间距 */
.compact-prose :deep(p) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}

.compact-prose :deep(h1),
.compact-prose :deep(h2),
.compact-prose :deep(h3),
.compact-prose :deep(h4),
.compact-prose :deep(h5),
.compact-prose :deep(h6) {
  margin-top: 0.75em;
  margin-bottom: 0.5em;
}

.compact-prose :deep(ul),
.compact-prose :deep(ol) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  padding-left: 1.5em;
}

.compact-prose :deep(li) {
  margin-top: 0.25em;
  margin-bottom: 0.25em;
}

.compact-prose :deep(pre) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}

.compact-prose :deep(blockquote) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}

.compact-prose :deep(hr) {
  margin-top: 1em;
  margin-bottom: 1em;
}

/* 第一个元素去掉顶部间距 */
.compact-prose :deep(> *:first-child) {
  margin-top: 0;
}

/* 最后一个元素去掉底部间距 */
.compact-prose :deep(> *:last-child) {
  margin-bottom: 0;
}
</style>
