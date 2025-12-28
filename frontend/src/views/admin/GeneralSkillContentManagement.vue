<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-full px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">通用技能 - 内容管理</h1>
        <p class="text-sm text-gray-600 mt-1">管理技能学习内容、试题库和AI笔记</p>
      </div>
    </div>

    <!-- 三栏布局 -->
    <div class="flex h-[calc(100vh-9rem)]">
      <!-- 左侧面板 (25%) - 上下两栏树形结构 -->
      <div class="w-1/4 bg-white border-r border-gray-200 overflow-hidden flex flex-col">

        <!-- 上栏树：职业路径 → 技能 (50% height) -->
        <div class="h-1/2 border-b border-gray-200 overflow-hidden flex flex-col">
          <div class="p-3 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
            <h3 class="text-xs font-semibold text-gray-700 uppercase tracking-wider">职业路径与技能</h3>
            <!-- 职业路径管理按钮 -->
            <button
              @click="showCareerPathModal = true"
              class="px-2 py-1 text-xs bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors"
              title="新增职业路径"
            >
              + 职业路径
            </button>
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
            <div v-else-if="careerPaths.length > 0 || unassociatedSkills.length > 0" class="space-y-1">
              <div v-for="careerPath in careerPaths" :key="careerPath.id">
                <!-- 职业路径节点 -->
                <div
                  class="flex items-center px-2 py-1.5 rounded hover:bg-gray-100 transition-colors group"
                >
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
                    {{ careerPath.name }}
                  </span>
                  <!-- 操作按钮 (hover显示) -->
                  <div class="opacity-0 group-hover:opacity-100 flex items-center space-x-1 ml-2">
                    <button
                      @click.stop="editCareerPath(careerPath)"
                      class="p-1 text-blue-600 hover:bg-blue-50 rounded"
                      title="编辑"
                    >
                      <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button
                      @click.stop="deleteCareerPathConfirm(careerPath)"
                      class="p-1 text-red-600 hover:bg-red-50 rounded"
                      title="删除"
                    >
                      <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
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

              <!-- 未关联技能节点 -->
              <div v-if="unassociatedSkills.length > 0" class="mt-2">
                <div
                  class="flex items-center px-2 py-1.5 rounded hover:bg-gray-100 transition-colors cursor-pointer"
                  @click="toggleUnassociated"
                >
                  <svg
                    class="w-4 h-4 mr-1 text-orange-500 flex-shrink-0 transition-transform"
                    :class="{ 'rotate-90': showUnassociated }"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                  <span class="text-sm font-medium text-orange-600 truncate">
                    未关联技能 ({{ unassociatedSkills.length }})
                  </span>
                </div>

                <!-- 未关联技能列表 -->
                <div v-if="showUnassociated" class="ml-5 mt-1 space-y-1">
                  <div
                    v-for="skill in unassociatedSkills"
                    :key="`unassociated-${skill.id}`"
                    @click.stop="selectSkill(skill.id)"
                    :class="[
                      'flex items-center px-2 py-1.5 rounded cursor-pointer transition-colors',
                      selectedSkillId === skill.id
                        ? 'bg-blue-50 text-blue-700 font-medium'
                        : 'text-gray-600 hover:bg-gray-50'
                    ]"
                  >
                    <svg class="w-3.5 h-3.5 mr-1.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
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

        <!-- 下栏树：大分类 → Focus Area (50% height) -->
        <div class="h-1/2 overflow-hidden flex flex-col">
          <div class="p-3 bg-gray-50 border-b border-gray-200">
            <h3 class="text-xs font-semibold text-gray-700 uppercase tracking-wider">大分类与 Focus Area</h3>
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
      <div class="flex-1 bg-white overflow-hidden flex flex-col">
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
              {{ selectedFocusArea?.name || '内容管理' }}
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
                学习资料
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
                试题库
              </button>
            </nav>
          </div>

          <!-- Tab 内容 -->
          <div class="flex-1 overflow-y-auto p-6">
            <!-- Tab 1: 学习资料 - 左右两栏布局 -->
            <div v-if="activeTab === 'learning'" class="flex h-full gap-4">
              <!-- 左栏：学习资料列表 (40%) -->
              <div class="w-2/5 flex flex-col border-r border-gray-200 pr-4">
                <div class="flex items-center justify-between mb-4">
                  <h3 class="text-lg font-semibold text-gray-900">资料列表 ({{ learningContents.length }})</h3>
                  <button
                    @click="handleAddContent"
                    class="px-3 py-1.5 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm"
                  >
                    + 新增资料
                  </button>
                </div>

                <!-- 加载中 -->
                <div v-if="loadingContents" class="flex-1 flex items-center justify-center">
                  <div class="text-center">
                    <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <p class="mt-2 text-sm text-gray-500">加载中...</p>
                  </div>
                </div>

                <!-- 资料列表 -->
                <div v-else-if="learningContents.length > 0" class="flex-1 overflow-y-auto space-y-2">
                  <div
                    v-for="(content, index) in learningContents"
                    :key="content.id"
                    @click="selectContent(content)"
                    :class="[
                      'p-3 rounded-lg cursor-pointer transition-colors border',
                      selectedContentId === content.id
                        ? 'bg-blue-50 border-blue-300'
                        : 'bg-white border-gray-200 hover:bg-gray-50'
                    ]"
                  >
                    <div class="flex items-start justify-between">
                      <div class="flex-1 min-w-0">
                        <div class="flex items-center gap-2 mb-1">
                          <span class="text-xs text-gray-500">#{{ index + 1 }}</span>
                          <span :class="getContentTypeBadgeClass(content.contentType)" class="text-xs px-2 py-0.5 rounded">
                            {{ getContentTypeLabel(content.contentType) }}
                          </span>
                        </div>
                        <h4 class="text-sm font-medium text-gray-900 truncate">
                          {{ content.title }}
                        </h4>
                        <p v-if="content.author" class="text-xs text-gray-500 mt-1">
                          作者: {{ content.author }}
                        </p>
                      </div>
                      <button
                        @click.stop="handleDeleteContent(content.id)"
                        class="ml-2 text-red-500 hover:text-red-700"
                        title="删除"
                      >
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  </div>
                </div>

                <!-- 无资料提示 -->
                <div v-else class="flex-1 flex items-center justify-center text-gray-400">
                  <div class="text-center">
                    <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <p class="mt-2 text-sm">暂无学习资料</p>
                  </div>
                </div>
              </div>

              <!-- 右栏：编辑表单 (60%) -->
              <div class="w-3/5 flex flex-col pl-4">
                <!-- 未选中内容时的提示 -->
                <div v-if="!selectedContentId && !editingContent" class="flex-1 flex items-center justify-center text-gray-400">
                  <div class="text-center">
                    <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    <p class="mt-4 text-sm">选择一个资料进行编辑，或点击"新增"创建新资料</p>
                  </div>
                </div>

                <!-- 编辑表单 -->
                <div v-else class="flex-1 overflow-y-auto">
                  <LearningContentEditForm
                    :content="editingContent"
                    :focus-area-id="selectedFocusAreaId"
                    @save="handleContentSaved"
                    @cancel="cancelEditContent"
                    @import-ai="handleImportAI"
                  />
                </div>
              </div>
            </div>

            <!-- Tab 2: 试题库 - 左右两栏布局 -->
            <div v-if="activeTab === 'questions'" class="flex h-full gap-4">
              <!-- 左栏：试题列表 (40%) -->
              <div class="w-2/5 flex flex-col border-r border-gray-200 pr-4">
                <div class="flex items-center justify-between mb-4">
                  <h3 class="text-lg font-semibold text-gray-900">试题列表 ({{ questions.length }})</h3>
                  <button
                    @click="handleAddQuestion"
                    class="px-3 py-1.5 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm"
                  >
                    + 新增试题
                  </button>
                </div>

                <!-- 加载中 -->
                <div v-if="loadingQuestions" class="flex-1 flex items-center justify-center">
                  <div class="text-center">
                    <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <p class="mt-2 text-sm text-gray-500">加载中...</p>
                  </div>
                </div>

                <!-- 试题列表 -->
                <div v-else-if="questions.length > 0" class="flex-1 overflow-y-auto space-y-2">
                  <div
                    v-for="(question, index) in questions"
                    :key="question.id"
                    @click="selectQuestion(question)"
                    :class="[
                      'p-3 rounded-lg cursor-pointer transition-colors border',
                      selectedQuestionId === question.id
                        ? 'bg-blue-50 border-blue-300'
                        : 'bg-white border-gray-200 hover:bg-gray-50'
                    ]"
                  >
                    <div class="flex items-start justify-between">
                      <div class="flex-1 min-w-0">
                        <div class="flex items-center gap-2 mb-1">
                          <span class="text-xs text-gray-500">#{{ index + 1 }}</span>
                          <span :class="getDifficultyBadgeClass(question.difficulty)" class="text-xs px-2 py-0.5 rounded">
                            {{ getDifficultyLabel(question.difficulty) }}
                          </span>
                          <span class="text-xs px-2 py-0.5 bg-gray-100 text-gray-800 rounded">
                            {{ getQuestionTypeLabel(question.questionType) }}
                          </span>
                        </div>
                        <h4 class="text-sm font-medium text-gray-900 line-clamp-2">
                          {{ question.title }}
                        </h4>
                      </div>
                      <button
                        @click.stop="deleteQuestionConfirm(question)"
                        class="ml-2 text-red-500 hover:text-red-700"
                        title="删除"
                      >
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  </div>
                </div>

                <!-- 无试题提示 -->
                <div v-else class="flex-1 flex items-center justify-center text-gray-400">
                  <div class="text-center">
                    <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                    <p class="mt-2 text-sm">暂无试题</p>
                  </div>
                </div>
              </div>

              <!-- 右栏：试题详情/编辑 (60%) -->
              <div class="w-3/5 flex flex-col pl-4">
                <!-- 未选中试题时的提示 -->
                <div v-if="!selectedQuestionId && !editingQuestion" class="flex-1 flex items-center justify-center text-gray-400">
                  <div class="text-center">
                    <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <p class="mt-4 text-sm">选择一个试题进行查看/编辑，或点击"新增"创建新试题</p>
                  </div>
                </div>

                <!-- 试题详情 - 使用现有的QuestionViewModal内容 -->
                <div v-else-if="viewingQuestion" class="flex-1 overflow-y-auto">
                  <QuestionViewModal
                    :is-open="true"
                    :question="viewingQuestion"
                    :inline="true"
                    @close="closeQuestionView"
                    @edit="editFromView"
                  />
                </div>

                <!-- 试题编辑 - 使用现有的QuestionEditModal内容 -->
                <div v-else-if="editingQuestion" class="flex-1 overflow-y-auto">
                  <QuestionEditModal
                    :is-open="true"
                    :question="editingQuestion"
                    :focus-areas="focusAreas"
                    :current-focus-area-id="selectedFocusAreaId"
                    :current-focus-area-name="selectedFocusArea?.name"
                    :inline="true"
                    @save="saveQuestion"
                    @cancel="cancelEditQuestion"
                  />
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- 导入AI笔记Modal -->
    <AIImportModal
      v-if="showAIImportModal"
      :contentId="selectedLearningContentId"
      @close="showAIImportModal = false"
      @success="handleAIImportSuccess"
    />

    <!-- 试题查看Modal -->
    <QuestionViewModal
      :is-open="showViewModal"
      :question="viewingQuestion"
      @close="showViewModal = false; viewingQuestion = null"
      @edit="editFromView"
    />

    <!-- 试题编辑Modal -->
    <QuestionEditModal
      :is-open="showQuestionModal"
      :question="editingQuestion"
      :focus-areas="focusAreas"
      :current-focus-area-id="selectedFocusAreaId"
      :current-focus-area-name="selectedFocusArea?.name"
      @save="saveQuestion"
      @cancel="showQuestionModal = false; editingQuestion = null"
    />

    <!-- 职业路径编辑Modal -->
    <CareerPathEditModal
      v-if="showCareerPathModal"
      :show="showCareerPathModal"
      :career-path="editingCareerPath"
      @close="showCareerPathModal = false; editingCareerPath = null"
      @success="handleCareerPathSaved"
    />
  </div>
</template>

<script>
import { getAllCareerPaths, deleteCareerPath } from '@/api/careerPaths'
import { getUnassociatedSkills } from '@/api/skills'
import majorCategoryApi from '@/api/majorCategoryApi'
import learningContentApi from '@/api/learningContentApi'
import { adminQuestionApi } from '@/api/questionApi'
import AIImportModal from '@/components/AIImportModal.vue'
import LearningContentEditForm from '@/components/skills/admin/LearningContentEditForm.vue'
import QuestionViewModal from '@/components/questions/QuestionViewModal.vue'
import QuestionEditModal from '@/components/questions/QuestionEditModal.vue'
import CareerPathEditModal from '@/components/skills/admin/CareerPathEditModal.vue'

export default {
  name: 'GeneralSkillContentManagement',
  components: {
    AIImportModal,
    LearningContentEditForm,
    QuestionViewModal,
    QuestionEditModal,
    CareerPathEditModal
  },
  data() {
    return {
      // 职业路径和技能树
      careerPaths: [],
      loadingCareerPaths: false,
      expandedCareerPaths: new Set(),
      selectedSkillId: null,

      // 未关联技能
      unassociatedSkills: [],
      showUnassociated: false,

      // 大分类和Focus Area树
      categories: [],
      focusAreas: [],
      loadingCategories: false,
      expandedCategories: new Set(),
      selectedFocusAreaId: null,

      // Tab状态
      activeTab: 'learning',

      // 学习资料相关
      learningContents: [],
      loadingContents: false,
      selectedContentId: null,
      editingContent: null,

      // 试题库相关
      questions: [],
      loadingQuestions: false,
      selectedQuestionId: null,
      showViewModal: false,
      viewingQuestion: null,
      showQuestionModal: false,
      editingQuestion: null,

      // Modal状态
      showAIImportModal: false,
      selectedLearningContentId: null,
      showCareerPathModal: false,
      editingCareerPath: null
    }
  },
  computed: {
    // 判断是否为第二类技能(检测是否有General大分类)
    isSecondTypeSkill() {
      if (!this.selectedSkillId || this.categories.length === 0) {
        return false
      }
      // ⚠️ Phase 6关键逻辑: 如果只有一个大分类且名称为"General",则为第二类技能
      return this.categories.length === 1 && this.categories[0].name === 'General'
    },
    // 当前选中的Focus Area
    selectedFocusArea() {
      return this.focusAreas.find(fa => fa.id === this.selectedFocusAreaId)
    }
  },
  async mounted() {
    await this.loadCareerPaths()
  },
  watch: {
    // 监听Focus Area变化，自动加载对应的学习资料或试题
    async selectedFocusAreaId(newVal) {
      console.log('[watch selectedFocusAreaId] New value:', newVal, 'Active tab:', this.activeTab)
      if (newVal) {
        if (this.activeTab === 'learning') {
          await this.loadLearningContents()
        } else if (this.activeTab === 'questions') {
          console.log('[watch selectedFocusAreaId] Calling loadQuestions')
          await this.loadQuestions()
        }
      }
    },
    // 监听Tab切换
    async activeTab(newVal) {
      console.log('[watch activeTab] New value:', newVal, 'Selected focus area:', this.selectedFocusAreaId)
      if (this.selectedFocusAreaId) {
        if (newVal === 'learning') {
          await this.loadLearningContents()
        } else if (newVal === 'questions') {
          console.log('[watch activeTab] Calling loadQuestions')
          await this.loadQuestions()
        }
      }
    }
  },
  methods: {
    // ⚠️ Guardrail #8: interceptor已返回response.data,使用const data = await api.method()
    async loadCareerPaths() {
      this.loadingCareerPaths = true
      try {
        const data = await getAllCareerPaths()
        this.careerPaths = data || []

        // 加载未关联技能
        await this.loadUnassociatedSkills()

        // 自动展开第一个职业路径
        if (this.careerPaths.length > 0) {
          this.expandedCareerPaths.add(this.careerPaths[0].id)
        }
      } catch (error) {
        console.error('加载职业路径失败:', error)
        this.careerPaths = []
      } finally {
        this.loadingCareerPaths = false
      }
    },

    // 加载未关联技能
    async loadUnassociatedSkills() {
      try {
        const skills = await getUnassociatedSkills()
        this.unassociatedSkills = skills || []
      } catch (error) {
        console.error('加载未关联技能失败:', error)
        this.unassociatedSkills = []
      }
    },

    // 展开/折叠未关联技能
    toggleUnassociated() {
      this.showUnassociated = !this.showUnassociated
    },

    // 编辑职业路径
    editCareerPath(careerPath) {
      this.editingCareerPath = careerPath
      this.showCareerPathModal = true
    },

    // 删除职业路径确认
    async deleteCareerPathConfirm(careerPath) {
      if (!confirm(`确定要删除职业路径"${careerPath.name}"吗？\n\n注意：这只会删除职业路径，不会删除关联的技能。`)) {
        return
      }

      try {
        await deleteCareerPath(careerPath.id)
        alert('删除成功')
        await this.loadCareerPaths()
      } catch (error) {
        console.error('删除职业路径失败:', error)
        alert('删除失败：' + (error.response?.data?.message || error.message))
      }
    },

    // 职业路径保存成功后的回调
    async handleCareerPathSaved() {
      this.showCareerPathModal = false
      this.editingCareerPath = null
      await this.loadCareerPaths()
    },

    // 展开/折叠职业路径
    toggleCareerPath(careerPathId) {
      if (this.expandedCareerPaths.has(careerPathId)) {
        this.expandedCareerPaths.delete(careerPathId)
      } else {
        this.expandedCareerPaths.add(careerPathId)
      }
    },

    // 选择技能
    async selectSkill(skillId) {
      this.selectedSkillId = skillId
      this.selectedFocusAreaId = null
      this.categories = []
      this.focusAreas = []
      this.expandedCategories.clear()
      this.learningContents = []
      this.questions = []

      await this.loadCategoriesAndFocusAreas()
    },

    // 加载大分类和Focus Area
    async loadCategoriesAndFocusAreas() {
      if (!this.selectedSkillId) {
        return
      }

      this.loadingCategories = true
      try {
        // 加载大分类
        const categoriesData = await majorCategoryApi.getAllMajorCategories(this.selectedSkillId)
        this.categories = categoriesData || []

        // 加载Focus Areas
        const focusAreasData = await majorCategoryApi.getFocusAreasWithCategories(this.selectedSkillId)
        this.focusAreas = focusAreasData || []

        // 自动展开行为
        if (this.isSecondTypeSkill && this.focusAreas.length > 0) {
          // 第二类技能: 自动选择第一个Focus Area
          this.selectedFocusAreaId = this.focusAreas[0].id
        } else if (this.categories.length > 0) {
          // 第一类技能: 自动展开第一个大分类
          this.expandedCategories.add(this.categories[0].id)
        }
      } catch (error) {
        console.error('加载大分类和Focus Area失败:', error)
      } finally {
        this.loadingCategories = false
      }
    },

    // 展开/折叠大分类
    toggleCategory(categoryId) {
      if (this.expandedCategories.has(categoryId)) {
        this.expandedCategories.delete(categoryId)
      } else {
        this.expandedCategories.add(categoryId)
      }
    },

    // 选择Focus Area
    selectFocusArea(focusAreaId) {
      console.log('[selectFocusArea] Selected focus area ID:', focusAreaId)
      this.selectedFocusAreaId = focusAreaId
    },

    // 获取某个大分类下的Focus Areas
    getFocusAreasByCategory(categoryId) {
      return this.focusAreas.filter(fa =>
        fa.categoryIds && fa.categoryIds.includes(categoryId)
      )
    },

    // ===== 学习资料相关方法 =====
    async loadLearningContents() {
      console.log('[loadLearningContents] Called with selectedFocusAreaId:', this.selectedFocusAreaId)
      if (!this.selectedFocusAreaId) {
        console.log('[loadLearningContents] No focus area selected, clearing contents')
        this.learningContents = []
        return
      }

      this.loadingContents = true
      try {
        // 查询该Focus Area的所有学习资料
        console.log('[loadLearningContents] Fetching learning contents for focus area:', this.selectedFocusAreaId)
        const data = await learningContentApi.getContentsForAdmin({
          focusAreaId: this.selectedFocusAreaId
        })
        console.log('[loadLearningContents] Received data:', data)
        console.log('[loadLearningContents] Data type:', typeof data)
        console.log('[loadLearningContents] Is array?', Array.isArray(data))

        // 检查数据结构 - API可能返回分页对象
        if (data && data.content) {
          console.log('[loadLearningContents] Data has .content property, using data.content')
          this.learningContents = data.content || []
        } else {
          console.log('[loadLearningContents] Data is array or object, using directly')
          this.learningContents = data || []
        }
        console.log('[loadLearningContents] Updated this.learningContents:', this.learningContents.length, 'items')
      } catch (error) {
        console.error('加载学习资料失败:', error)
        this.learningContents = []
      } finally {
        this.loadingContents = false
      }
    },
    selectContent(content) {
      this.selectedContentId = content.id
      this.editingContent = content
    },
    handleAddContent() {
      this.selectedContentId = null
      this.editingContent = null
      // 触发立即显示编辑表单（新增模式）
      this.$nextTick(() => {
        this.editingContent = {}
      })
    },
    cancelEditContent() {
      this.selectedContentId = null
      this.editingContent = null
    },
    async handleDeleteContent(id) {
      if (!confirm('确定要删除这个学习资料吗？')) {
        return
      }

      try {
        await learningContentApi.deleteContent(id)
        await this.loadLearningContents()
      } catch (error) {
        console.error('删除学习资料失败:', error)
        alert('删除失败: ' + (error.message || '未知错误'))
      }
    },
    async handleContentSaved() {
      this.selectedContentId = null
      this.editingContent = null
      await this.loadLearningContents()
    },
    handleImportAI() {
      // 使用当前编辑的学习资料ID
      if (this.editingContent && this.editingContent.id) {
        this.selectedLearningContentId = this.editingContent.id
        this.showAIImportModal = true
      }
    },
    async handleAIImportSuccess() {
      this.showAIImportModal = false
      // 刷新学习资料列表
      await this.loadLearningContents()
    },
    getContentTypeLabel(type) {
      const labels = {
        article: '文章',
        video: '视频',
        book: '书籍',
        course: '课程'
      }
      return labels[type] || type
    },
    getContentTypeBadgeClass(type) {
      const classes = {
        article: 'px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded',
        video: 'px-2 py-1 text-xs font-medium bg-purple-100 text-purple-800 rounded',
        book: 'px-2 py-1 text-xs font-medium bg-green-100 text-green-800 rounded',
        course: 'px-2 py-1 text-xs font-medium bg-yellow-100 text-yellow-800 rounded'
      }
      return classes[type] || 'px-2 py-1 text-xs font-medium bg-gray-100 text-gray-800 rounded'
    },

    // ===== 试题库相关方法 =====
    async loadQuestions() {
      console.log('[loadQuestions] Called with selectedFocusAreaId:', this.selectedFocusAreaId)
      if (!this.selectedFocusAreaId) {
        console.log('[loadQuestions] No focus area selected, clearing questions')
        this.questions = []
        return
      }

      this.loadingQuestions = true
      try {
        // 查询该Focus Area的所有试题
        console.log('[loadQuestions] Fetching questions for focus area:', this.selectedFocusAreaId)
        const data = await adminQuestionApi.getAllQuestions({
          focusAreaId: this.selectedFocusAreaId
        })
        console.log('[loadQuestions] Received data:', data)
        this.questions = data || []
        console.log('[loadQuestions] Updated this.questions:', this.questions.length, 'questions')
      } catch (error) {
        console.error('加载试题失败:', error)
        this.questions = []
      } finally {
        this.loadingQuestions = false
      }
    },
    selectQuestion(question) {
      this.selectedQuestionId = question.id
      this.editingQuestion = question
      this.viewingQuestion = null
    },
    closeQuestionView() {
      this.selectedQuestionId = null
      this.viewingQuestion = null
    },
    cancelEditQuestion() {
      this.selectedQuestionId = null
      this.editingQuestion = null
    },
    handleAddQuestion() {
      this.selectedQuestionId = null
      this.viewingQuestion = null
      this.editingQuestion = null
      // 触发立即显示编辑表单（新增模式）
      this.$nextTick(() => {
        this.editingQuestion = {}
      })
    },
    viewQuestion(question) {
      this.viewingQuestion = question
      this.showViewModal = true
    },
    editQuestion(question) {
      this.editingQuestion = question
      this.showQuestionModal = true
    },
    editFromView(question) {
      this.showViewModal = false
      this.viewingQuestion = null
      this.editQuestion(question)
    },
    async saveQuestion(questionData) {
      try {
        if (this.editingQuestion && this.editingQuestion.id) {
          // 更新试题
          await adminQuestionApi.updateQuestion(this.editingQuestion.id, questionData)
        } else {
          // 创建试题
          await adminQuestionApi.createQuestion(questionData)
        }
        // 清除状态
        this.showQuestionModal = false
        this.selectedQuestionId = null
        this.viewingQuestion = null
        this.editingQuestion = null
        // 重新加载试题列表
        await this.loadQuestions()
      } catch (error) {
        console.error('保存试题失败:', error)
        alert('保存失败: ' + (error.message || '未知错误'))
      }
    },
    async deleteQuestionConfirm(question) {
      if (!confirm(`确定要删除试题"${question.title}"吗？`)) {
        return
      }

      try {
        await adminQuestionApi.deleteQuestion(question.id)
        await this.loadQuestions()
      } catch (error) {
        console.error('删除试题失败:', error)
        alert('删除失败: ' + (error.message || '未知错误'))
      }
    },
    getDifficultyLabel(difficulty) {
      const labels = {
        easy: '简单',
        medium: '中等',
        hard: '困难'
      }
      return labels[difficulty] || difficulty
    },
    getDifficultyBadgeClass(difficulty) {
      const classes = {
        easy: 'bg-green-100 text-green-800',
        medium: 'bg-yellow-100 text-yellow-800',
        hard: 'bg-red-100 text-red-800'
      }
      return classes[difficulty] || 'bg-gray-100 text-gray-800'
    },
    getQuestionTypeLabel(type) {
      const labels = {
        behavioral: 'Behavioral',
        technical: '技术题',
        design: '设计题',
        programming: '编程题'
      }
      return labels[type] || type
    }
  }
}
</script>
