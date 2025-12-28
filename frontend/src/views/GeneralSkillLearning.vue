<template>
  <div class="min-h-screen bg-gray-50">
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
          <div class="p-3 bg-gray-50 border-b border-gray-200">
            <h3 class="text-xs font-semibold text-gray-700 uppercase tracking-wider">职业路径与技能</h3>
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
                      'flex items-center px-2 py-1.5 rounded cursor-pointer transition-colors',
                      selectedSkillId === skill.id
                        ? 'bg-blue-50 text-blue-700 font-medium'
                        : 'text-gray-600 hover:bg-gray-50'
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
          <div class="p-3 bg-gray-50 border-b border-gray-200">
            <h3 class="text-xs font-semibold text-gray-700 uppercase tracking-wider">Focus Area</h3>
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
                    'flex items-center px-2 py-1.5 rounded cursor-pointer transition-colors',
                    selectedFocusAreaId === focusArea.id
                      ? 'bg-blue-50 text-blue-700 font-medium'
                      : 'text-gray-600 hover:bg-gray-50'
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
                        'flex items-center px-2 py-1.5 rounded cursor-pointer transition-colors',
                        selectedFocusAreaId === focusArea.id
                          ? 'bg-blue-50 text-blue-700 font-medium'
                          : 'text-gray-600 hover:bg-gray-50'
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
            <nav class="flex space-x-8" aria-label="Tabs">
              <button
                @click="activeTab = 'learning'"
                :class="[
                  'py-2 px-1 border-b-2 font-medium text-sm whitespace-nowrap',
                  activeTab === 'learning'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                📚 学习资料
              </button>
              <button
                @click="activeTab = 'questions'"
                :class="[
                  'py-2 px-1 border-b-2 font-medium text-sm whitespace-nowrap',
                  activeTab === 'questions'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                📝 试题库
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

                  <!-- iframe容器 -->
                  <div class="flex-1 relative bg-white">
                    <iframe
                      v-if="selectedContent.url && !iframeLoadError"
                      :src="selectedContent.url"
                      class="w-full h-full border-0"
                      @error="handleIframeError"
                    ></iframe>

                    <!-- iframe加载失败提示 -->
                    <div v-if="iframeLoadError" class="absolute inset-0 bg-white flex items-center justify-center">
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
                        <!-- 全屏查看按钮（仅对非视频类型的文档显示） -->
                        <button
                          v-if="selectedContent.contentType !== 'video' && selectedContent.url"
                          @click.stop="maximizeDocument"
                          class="p-1 hover:bg-blue-600 rounded transition-all"
                          title="文档全屏查看"
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

                  <!-- 卡片2: 整体笔记 - 使用渐变背景 -->
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
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 714 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
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
                    <div
                      v-for="(question, index) in questions"
                      :key="question.id"
                      @click="selectQuestion(question)"
                      :class="[
                        'p-3 rounded-lg cursor-pointer transition-all border-2',
                        selectedQuestion?.id === question.id
                          ? 'bg-blue-50 border-blue-400 shadow-md'
                          : 'bg-white border-gray-200 hover:border-blue-300 hover:shadow'
                      ]"
                    >
                      <div class="flex items-start gap-2">
                        <span class="text-xs font-bold text-gray-500 mt-0.5">Q{{ index + 1 }}</span>
                        <div class="flex-1 min-w-0">
                          <h4 :class="[
                            'text-sm font-semibold truncate',
                            selectedQuestion?.id === question.id ? 'text-blue-700' : 'text-gray-900'
                          ]">
                            {{ question.title }}
                          </h4>
                          <p class="text-xs text-gray-500 mt-1">{{ question.questionType }}</p>
                        </div>
                        <div class="flex-shrink-0">
                          <span v-if="question.note" class="inline-block px-2 py-0.5 text-xs font-semibold text-green-700 bg-green-100 rounded">
                            已答
                          </span>
                        </div>
                      </div>
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
                  <div v-else class="p-6">
                    <!-- 模式切换按钮 -->
                    <div class="mb-4 flex items-center justify-between">
                      <h3 class="text-lg font-bold text-gray-900">{{ selectedQuestion.title }}</h3>
                      <div class="flex gap-2">
                        <button
                          @click="questionViewMode = 'browse'"
                          :class="[
                            'px-4 py-2 text-sm font-medium rounded-lg transition-colors',
                            questionViewMode === 'browse'
                              ? 'bg-blue-600 text-white'
                              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                          ]"
                        >
                          📖 浏览模式
                        </button>
                        <button
                          @click="questionViewMode = 'answer'"
                          :class="[
                            'px-4 py-2 text-sm font-medium rounded-lg transition-colors',
                            questionViewMode === 'answer'
                              ? 'bg-green-600 text-white'
                              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                          ]"
                        >
                          ✍️ 答题模式
                        </button>
                      </div>
                    </div>

                    <!-- 浏览模式 -->
                    <div v-if="questionViewMode === 'browse'" class="space-y-4">
                      <!-- 题目类型 -->
                      <div class="bg-gray-50 p-3 rounded-lg">
                        <span class="text-xs font-semibold text-gray-500">题目类型：</span>
                        <span class="text-sm text-gray-900">{{ selectedQuestion.questionType }}</span>
                      </div>

                      <!-- 题目描述 -->
                      <div>
                        <h4 class="text-sm font-semibold text-gray-700 mb-2">题目描述</h4>
                        <div class="prose max-w-none text-sm" v-html="renderMarkdown(selectedQuestion.questionDescription)"></div>
                      </div>

                      <!-- 答案要求 -->
                      <div v-if="selectedQuestion.answerRequirement">
                        <h4 class="text-sm font-semibold text-gray-700 mb-2">答案要求</h4>
                        <div class="prose max-w-none text-sm" v-html="renderMarkdown(selectedQuestion.answerRequirement)"></div>
                      </div>
                    </div>

                    <!-- 答题模式 -->
                    <div v-else class="space-y-4">
                      <!-- 有模版：根据模版字段渲染结构化输入 -->
                      <div v-if="answerTemplate && answerTemplate.templateFields" class="space-y-4">
                        <div class="bg-blue-50 p-3 rounded-lg mb-4">
                          <h4 class="text-sm font-semibold text-blue-900 mb-1">{{ answerTemplate.templateName }} 答题框架</h4>
                          <p class="text-xs text-blue-700">{{ answerTemplate.description }}</p>
                        </div>

                        <!-- 动态渲染模版字段 -->
                        <div
                          v-for="field in parseTemplateFields(answerTemplate.templateFields)"
                          :key="field.key"
                          class="space-y-2"
                        >
                          <label class="block text-sm font-semibold text-gray-700">
                            {{ field.label }}
                          </label>
                          <textarea
                            v-model="answerForm[field.key]"
                            :placeholder="field.placeholder"
                            rows="4"
                            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                          ></textarea>
                        </div>

                        <!-- 保存按钮 -->
                        <div class="flex justify-end gap-2 pt-4 border-t border-gray-200">
                          <button
                            @click="saveAnswer"
                            class="px-6 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors"
                          >
                            💾 保存答案
                          </button>
                        </div>
                      </div>

                      <!-- 无模版：自由文本输入 -->
                      <div v-else class="space-y-4">
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

                        <!-- 保存按钮 -->
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
            <div class="text-sm text-gray-600">
              新知识点已添加，请在卡片中直接编辑内容
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
import careerPathApi from '@/api/careerPathApi'
import skillApi from '@/api/skillApi'
import majorCategoryApi from '@/api/majorCategoryApi'
import focusAreaApi from '@/api/focusAreaApi'
import learningContentApi from '@/api/learningContentApi'
import { questionApi } from '@/api/questionApi'
import skillTemplateApi from '@/api/skillTemplateApi'
import { marked } from 'marked'
import VideoPlayer from '@/components/VideoPlayer.vue'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ❌ WRONG: apiClient.get('/api/learning-contents')
// ✅ CORRECT: apiClient.get('/learning-contents')

const route = useRoute()

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

// 右侧Tab状态
const activeTab = ref('learning') // 'learning' or 'questions'
const learningContents = ref([])
const selectedContent = ref(null) // 当前选中的学习资料
const questions = ref([])
const selectedQuestion = ref(null) // 当前选中的试题
const questionViewMode = ref('browse') // 'browse' or 'answer'
const answerTemplate = ref(null) // 当前技能的答题模版
const answerForm = ref({}) // 答题表单数据

// 卡片折叠状态
const cardStates = ref({
  resourceInfo: true,  // 学习资料卡片默认展开
  overallNote: true,   // 整体笔记卡片默认展开
  knowledgePoints: {},  // 知识点卡片折叠状态 {id: boolean}
  // 卡片内容展开状态
  overallNoteExpanded: false,   // 整体笔记内容是否展开
  knowledgePointsExpanded: {},   // 知识点内容展开状态 {id: boolean}
  // 卡片内容是否溢出（需要展开按钮）
  overallNoteOverflow: false,   // 整体笔记内容是否溢出
  knowledgePointsOverflow: {}   // 知识点内容溢出状态 {id: boolean}
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

// 左侧栏折叠状态
const isSidebarCollapsed = ref(false)

// 试题列表折叠状态
const isQuestionListCollapsed = ref(false)

// DOM引用
const overallNoteContent = ref(null)
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
    // 加载大分类 - 使用正确的API方法
    const categoriesData = await majorCategoryApi.getAllMajorCategories(selectedSkillId.value)
    categories.value = categoriesData || []

    // 加载Focus Area（包含大分类信息）- 使用公开API
    const focusAreasData = await majorCategoryApi.getFocusAreasWithCategories(selectedSkillId.value)
    focusAreas.value = focusAreasData || []

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
  // 触发加载学习资料或试题
  if (activeTab.value === 'learning') {
    loadLearningContents()
  } else {
    loadQuestions()
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

    // 为每个学习资料加载AI笔记和用户笔记
    for (const content of allContents) {
      // 获取AI笔记 + 用户笔记
      const notes = await learningContentApi.getNotes(content.id)
      content.aiNote = notes.aiNote || null
      content.userNote = notes.userNote || { noteContent: '' }

      // 获取AI知识点 + 用户知识点
      const kps = await learningContentApi.getKnowledgePoints(content.id)
      content.aiKnowledgePoints = kps.aiKnowledgePoints || []
      content.userKnowledgePoints = kps.userKnowledgePoints || []
    }

    learningContents.value = allContents
    // 自动选中第一个学习资料
    if (allContents.length > 0) {
      selectedContent.value = allContents[0]
      // 文档类型 + 支持iframe → 默认iframe模式；视频类型 → 卡片视图
      isDocumentMaximized.value = allContents[0].contentType !== 'video' && isSupportIframe(allContents[0])
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

    // 初始化知识点卡片折叠状态
    if (content.userKnowledgePoints) {
      content.userKnowledgePoints.forEach((kp, index) => {
        cardStates.value.knowledgePoints[kp.id || index] = true
        cardStates.value.knowledgePointsExpanded[kp.id || index] = false
      })
    }

    // 检测内容溢出
    await nextTick()
    // 检测整体笔记溢出
    cardStates.value.overallNoteOverflow = checkOverflow(overallNoteContent.value)
    // 检测知识点溢出
    if (content.userKnowledgePoints) {
      content.userKnowledgePoints.forEach((kp, index) => {
        const element = knowledgePointContents.value[kp.id || index]
        cardStates.value.knowledgePointsOverflow[kp.id || index] = checkOverflow(element)
      })
    }
  }
}

// 切换知识点卡片折叠状态
const toggleKnowledgePointCard = (pointKey) => {
  if (cardStates.value.knowledgePoints[pointKey] === undefined) {
    cardStates.value.knowledgePoints[pointKey] = true
  } else {
    cardStates.value.knowledgePoints[pointKey] = !cardStates.value.knowledgePoints[pointKey]
  }
}

const loadQuestions = async () => {
  if (!selectedFocusAreaId.value) return

  try {
    loadingQuestions.value = true
    // Note: interceptor returns response.data already
    const data = await questionApi.getQuestionsByFocusArea(selectedFocusAreaId.value)
    questions.value = data || []
    // 自动选中第一道试题
    if (data && data.length > 0) {
      selectedQuestion.value = data[0]
      questionViewMode.value = 'browse'
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
const selectQuestion = (question) => {
  selectedQuestion.value = question
  questionViewMode.value = 'browse'

  // 初始化答题表单
  initAnswerForm(question)
}

// 初始化答题表单
const initAnswerForm = (question) => {
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
    answerForm.value = fields
  } else {
    // 没有模版,使用自由文本
    answerForm.value = { freeText: '' }
  }

  // 如果已有答题笔记,加载到表单中
  if (question.note && question.note.noteContent) {
    try {
      const savedAnswer = JSON.parse(question.note.noteContent)
      answerForm.value = { ...answerForm.value, ...savedAnswer }
    } catch (error) {
      // 如果不是JSON格式,可能是旧版自由文本
      answerForm.value.freeText = question.note.noteContent
    }
  }
}

// 保存答题笔记
const saveAnswer = async () => {
  if (!selectedQuestion.value) return

  try {
    // 将答题表单转换为JSON字符串保存
    const noteContent = JSON.stringify(answerForm.value)

    // 调用API保存
    const savedNote = await questionApi.saveOrUpdateNote(selectedQuestion.value.id, noteContent)

    // 更新本地数据
    if (selectedQuestion.value.note) {
      selectedQuestion.value.note.noteContent = noteContent
    } else {
      selectedQuestion.value.note = { noteContent }
    }

    // 更新questions列表中的note标记
    const questionIndex = questions.value.findIndex(q => q.id === selectedQuestion.value.id)
    if (questionIndex !== -1) {
      questions.value[questionIndex].note = selectedQuestion.value.note
    }

    alert('答题笔记保存成功！')
  } catch (error) {
    console.error('Failed to save answer:', error)
    alert('保存失败: ' + (error.message || '未知错误'))
  }
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


const closeAddKnowledgePointModal = () => {
  showAddKnowledgePointModal.value = false
  // 自动添加一个空知识点到当前学习资料
  if (selectedContent.value) {
    addKnowledgePoint(selectedContent.value.id)
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

  // 切换到卡片视图后，重新检测内容溢出
  await nextTick()
  // 检测整体笔记溢出
  cardStates.value.overallNoteOverflow = checkOverflow(overallNoteContent.value)
  // 检测知识点溢出
  if (selectedContent.value?.userKnowledgePoints) {
    selectedContent.value.userKnowledgePoints.forEach((kp, index) => {
      const element = knowledgePointContents.value[kp.id || index]
      cardStates.value.knowledgePointsOverflow[kp.id || index] = checkOverflow(element)
    })
  }
}

// 处理iframe加载错误 - 自动回退到卡片视图
const handleIframeError = async () => {
  iframeLoadError.value = true
  // 网站不支持iframe，自动切换到卡片视图
  isDocumentMaximized.value = false

  // 切换到卡片视图后，重新检测内容溢出
  await nextTick()
  // 检测整体笔记溢出
  cardStates.value.overallNoteOverflow = checkOverflow(overallNoteContent.value)
  // 检测知识点溢出
  if (selectedContent.value?.userKnowledgePoints) {
    selectedContent.value.userKnowledgePoints.forEach((kp, index) => {
      const element = knowledgePointContents.value[kp.id || index]
      cardStates.value.knowledgePointsOverflow[kp.id || index] = checkOverflow(element)
    })
  }
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

init()
</script>
