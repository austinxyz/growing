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
            <!-- 测试按钮 -->
            <button
              @click="testItems = ['Item 1', 'Item 2', 'Item 3']"
              class="px-2 py-1 text-xs bg-green-600 text-white rounded hover:bg-green-700 transition-colors mr-2"
            >
              测试
            </button>
            <!-- 职业路径管理按钮 -->
            <button
              @click="showCareerPathModal = true"
              class="px-2 py-1 text-xs bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors"
              title="新增职业路径"
            >
              + 职业路径
            </button>
          </div>

          <!-- 测试拖拽区域 -->
          <div v-if="testItems.length > 0" class="p-2 bg-yellow-50 border border-yellow-200">
            <p class="text-xs mb-2">测试拖拽 (应该可以拖动下面的项目):</p>
            <draggable
              v-model="testItems"
              item-key="index"
              class="space-y-1"
            >
              <template #item="{ element }">
                <div class="bg-white p-2 rounded border cursor-move">
                  {{ element }}
                </div>
              </template>
            </draggable>
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
              <draggable
                v-model="focusAreas"
                @update:model-value="handleSecondTypeFocusAreaReorder"
                item-key="id"
                class="space-y-1"
                :animation="200"
              >
                <template #item="{ element: focusArea }">
                  <div
                    @click="selectFocusArea(focusArea.id)"
                    :class="[
                      'flex items-center px-2 py-1.5 rounded cursor-pointer transition-colors group',
                      selectedFocusAreaId === focusArea.id
                        ? 'bg-blue-50 text-blue-700 font-medium'
                        : 'text-gray-600 hover:bg-gray-50'
                    ]"
                  >
                    <!-- 拖拽手柄 (6个点) -->
                    <svg class="w-4 h-4 mr-1.5 flex-shrink-0 text-gray-400 hover:text-gray-600 cursor-move drag-handle transition-colors" fill="currentColor" viewBox="0 0 20 20">
                      <circle cx="7" cy="5" r="1.5"/>
                      <circle cx="13" cy="5" r="1.5"/>
                      <circle cx="7" cy="10" r="1.5"/>
                      <circle cx="13" cy="10" r="1.5"/>
                      <circle cx="7" cy="15" r="1.5"/>
                      <circle cx="13" cy="15" r="1.5"/>
                    </svg>
                    <!-- Focus Area图标 -->
                    <svg class="w-3.5 h-3.5 mr-1.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 20l4-16m2 16l4-16M6 9h14M4 15h14" />
                    </svg>
                    <span class="text-sm truncate">{{ focusArea.name }}</span>
                  </div>
                </template>
              </draggable>
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

                  <!-- Focus Area列表 (子节点) - 可拖拽 -->
                  <div v-if="expandedCategories.has(category.id)" class="ml-5 mt-1">
                    <draggable
                      :model-value="getFocusAreasByCategory(category.id)"
                      @update:model-value="(newList) => handleFocusAreaReorder(category.id, newList)"
                      item-key="id"
                      :animation="200"
                      class="space-y-1"
                    >
                    <template #item="{ element: focusArea }">
                      <div
                        @click.stop="selectFocusArea(focusArea.id)"
                        :class="[
                          'flex items-center px-2 py-1.5 rounded cursor-pointer transition-colors group',
                          selectedFocusAreaId === focusArea.id
                            ? 'bg-blue-50 text-blue-700 font-medium'
                            : 'text-gray-600 hover:bg-gray-50'
                        ]"
                      >
                        <!-- 拖拽手柄 (6个点) -->
                        <svg class="w-4 h-4 mr-1.5 flex-shrink-0 text-gray-400 hover:text-gray-600 cursor-move drag-handle transition-colors" fill="currentColor" viewBox="0 0 20 20">
                          <circle cx="7" cy="5" r="1.5"/>
                          <circle cx="13" cy="5" r="1.5"/>
                          <circle cx="7" cy="10" r="1.5"/>
                          <circle cx="13" cy="10" r="1.5"/>
                          <circle cx="7" cy="15" r="1.5"/>
                          <circle cx="13" cy="15" r="1.5"/>
                        </svg>
                        <!-- Focus Area图标 -->
                        <svg class="w-3.5 h-3.5 mr-1.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 20l4-16m2 16l4-16M6 9h14M4 15h14" />
                        </svg>
                        <span class="text-sm truncate">{{ focusArea.name }}</span>
                      </div>
                    </template>
                    </draggable>
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

              <!-- 右栏：试题详情/编辑/AI答题 (60%) -->
              <div class="w-3/5 flex pl-4">
                <!-- 主内容区域 -->
                <div class="flex-1 flex flex-col">

                <!-- 未选中试题时的提示 -->
                <div v-if="!selectedQuestionId && !editingQuestion" class="flex-1 flex items-center justify-center text-gray-400">
                  <div class="text-center">
                    <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <p class="mt-4 text-sm">选择一个试题进行查看/编辑，或点击"新增"创建新试题</p>
                  </div>
                </div>

                <!-- 浏览模式 - 显示试题详情 -->
                <div v-else-if="questionViewMode === 'view' && viewingQuestion" class="flex-1 overflow-y-auto">
                  <QuestionViewModal
                    :is-open="true"
                    :question="viewingQuestion"
                    :inline="true"
                    :is-admin="true"
                    @close="closeQuestionView"
                    @edit="editFromView"
                    @edit-ai-note="handleSwitchToAIAnswer"
                  />
                </div>

                <!-- 编辑模式 - 编辑试题 -->
                <div v-else-if="questionViewMode === 'edit' && editingQuestion" class="flex-1 overflow-hidden p-4">
                  <QuestionEditModal
                    :is-open="true"
                    :question="editingQuestion"
                    :focus-areas="focusAreas"
                    :current-focus-area-id="selectedFocusAreaId"
                    :current-focus-area-name="selectedFocusArea?.name"
                    :inline="true"
                    @save="saveQuestion"
                    @cancel="cancelEditQuestion"
                    @edit-ai-note="handleSwitchToAIAnswer"
                  />
                </div>

                <!-- AI答题模式 - 为试题添加AI参考答案 -->
                <div v-else-if="questionViewMode === 'ai-answer' && viewingQuestion" class="flex-1 overflow-y-auto">
                  <div class="bg-purple-50 border border-purple-200 rounded-lg p-4 mb-4">
                    <div class="flex items-center">
                      <span class="text-2xl mr-2">🤖</span>
                      <div>
                        <h3 class="text-sm font-semibold text-purple-900">AI答题模式</h3>
                        <p class="text-xs text-purple-700 mt-1">为试题添加AI参考答案（user_id = -1），供用户学习时参考</p>
                      </div>
                    </div>
                  </div>

                  <!-- 试题标题 -->
                  <h2 class="text-lg font-bold text-gray-900 mb-4">{{ viewingQuestion.title }}</h2>

                  <!-- 题目描述和答题模式切换 (横向布局) -->
                  <div class="flex gap-4 mb-4">
                    <!-- 试题描述（左侧，自动占据剩余空间） -->
                    <div class="flex-1 bg-gray-50 rounded-lg p-4 border border-gray-200">
                      <h4 class="text-sm font-semibold text-gray-700 mb-2">📝 题目描述</h4>
                      <div class="prose prose-sm max-w-none text-gray-700" v-html="renderMarkdown(viewingQuestion.questionDescription)"></div>
                    </div>

                    <!-- 答题模式切换 (右侧，固定宽度，如果有模版) -->
                    <div v-if="aiAnswerTemplate && aiAnswerTemplate.templateFields" class="flex flex-col items-start">
                      <label class="block text-xs font-medium text-gray-700 mb-1.5">答题模式</label>
                      <div class="flex flex-col space-y-1.5">
                        <button
                          @click="aiAnswerMode = 'template'"
                          :class="[
                            'px-2 py-1 rounded text-xs font-medium transition-colors whitespace-nowrap',
                            aiAnswerMode === 'template'
                              ? 'bg-blue-600 text-white'
                              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                          ]"
                        >
                          {{ aiAnswerTemplate.templateName }} 模版
                        </button>
                        <button
                          @click="aiAnswerMode = 'free'"
                          :class="[
                            'px-2 py-1 rounded text-xs font-medium transition-colors whitespace-nowrap',
                            aiAnswerMode === 'free'
                              ? 'bg-blue-600 text-white'
                              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                          ]"
                        >
                          自由答题
                        </button>
                      </div>
                    </div>
                  </div>

                  <!-- 模版答题 -->
                  <div v-if="aiAnswerTemplate && aiAnswerTemplate.templateFields && aiAnswerMode === 'template'" class="space-y-4">
                    <!-- 模版说明 -->
                    <div v-if="aiAnswerTemplate.description" class="bg-blue-50 border border-blue-200 rounded-md p-4">
                      <h3 class="text-sm font-semibold text-blue-900 mb-2">💡 {{ aiAnswerTemplate.templateName }}</h3>
                      <p class="text-xs text-blue-800">{{ aiAnswerTemplate.description }}</p>
                    </div>

                    <!-- 动态渲染模版字段 - 两栏卡片布局 -->
                    <div class="grid grid-cols-1 lg:grid-cols-2 gap-3">
                      <div
                        v-for="(field, index) in parseTemplateFields(aiAnswerTemplate.templateFields)"
                        :key="field.key"
                        :class="[
                          'relative rounded-lg shadow-md overflow-hidden',
                          index === 0 ? 'bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-300' :
                          index === 1 ? 'bg-gradient-to-br from-green-50 to-green-100 border border-green-300' :
                          index === 2 ? 'bg-gradient-to-br from-orange-50 to-orange-100 border border-orange-300' :
                          'bg-gradient-to-br from-purple-50 to-purple-100 border border-purple-300'
                        ]"
                      >
                        <!-- 卡片头部 -->
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

                        <!-- 卡片内容 -->
                        <div class="p-2">
                          <textarea
                            v-model="aiAnswerData[field.key]"
                            :placeholder="field.placeholder || `请输入${field.label}...`"
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

                    <!-- 核心思路 (模版模式也需要) -->
                    <div class="space-y-3 pt-4 border-t-2 border-gray-200">
                      <div class="flex items-center gap-2 mb-2">
                        <svg class="w-5 h-5 text-yellow-600" fill="currentColor" viewBox="0 0 20 20">
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                        <label class="text-sm font-bold text-gray-800">
                          💡 核心思路
                        </label>
                        <span class="text-xs text-gray-500">
                          {{ viewingQuestion?.questionType === 'programming' ? '(算法要点、时间空间复杂度)' : '(关键思路、要点总结)' }}
                        </span>
                      </div>
                      <textarea
                        v-model="aiAnswerData.coreStrategy"
                        :placeholder="coreStrategyPlaceholder"
                        rows="6"
                        class="w-full px-4 py-3 text-sm border-2 border-yellow-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-yellow-500 bg-yellow-50/30 resize-none"
                      ></textarea>
                      <p class="text-xs text-gray-500">
                        {{ viewingQuestion?.questionType === 'programming'
                          ? '记录算法思路、时间/空间复杂度、关键要点'
                          : '记录答题核心思路、关键要点、注意事项'
                        }}
                      </p>
                    </div>

                    <!-- 保存按钮 -->
                    <div class="flex justify-end gap-2 pt-4">
                      <button
                        @click="clearAIAnswer"
                        class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors"
                      >
                        清空
                      </button>
                      <button
                        @click="saveAIAnswer"
                        class="px-8 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white text-sm font-bold rounded-lg hover:from-purple-700 hover:to-indigo-700 shadow-md hover:shadow-lg transition-all duration-200 transform hover:scale-105"
                      >
                        💾 保存AI答案
                      </button>
                    </div>
                  </div>

                  <!-- 自由答题 -->
                  <div v-if="!aiAnswerTemplate || !aiAnswerTemplate.templateFields || aiAnswerMode === 'free'" class="space-y-4">
                    <div class="bg-gray-50 p-3 rounded-lg mb-4">
                      <h4 class="text-sm font-semibold text-gray-700">自由答题</h4>
                      <p class="text-xs text-gray-500 mt-1">请根据题目要求,自由发挥作答</p>
                    </div>

                    <label class="block text-sm font-medium text-gray-700 mb-2">
                      📝 AI参考答案
                    </label>
                    <textarea
                      v-model="aiAnswerData.freeAnswer"
                      placeholder="请输入AI参考答案..."
                      rows="12"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent text-sm resize-none"
                    ></textarea>

                    <!-- 核心思路 (自由模式也需要) -->
                    <div class="space-y-3 pt-4 border-t-2 border-gray-200">
                      <div class="flex items-center gap-2 mb-2">
                        <svg class="w-5 h-5 text-yellow-600" fill="currentColor" viewBox="0 0 20 20">
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                        <label class="text-sm font-bold text-gray-800">
                          💡 核心思路
                        </label>
                        <span class="text-xs text-gray-500">
                          {{ viewingQuestion?.questionType === 'programming' ? '(算法要点、时间空间复杂度)' : '(关键思路、要点总结)' }}
                        </span>
                      </div>
                      <textarea
                        v-model="aiAnswerData.coreStrategy"
                        :placeholder="coreStrategyPlaceholder"
                        rows="6"
                        class="w-full px-4 py-3 text-sm border-2 border-yellow-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-yellow-500 bg-yellow-50/30 resize-none"
                      ></textarea>
                      <p class="text-xs text-gray-500">
                        {{ viewingQuestion?.questionType === 'programming'
                          ? '记录算法思路、时间/空间复杂度、关键要点'
                          : '记录答题核心思路、关键要点、注意事项'
                        }}
                      </p>
                    </div>

                    <!-- 保存按钮 -->
                    <div class="flex justify-end gap-2 pt-4 border-t border-gray-200">
                      <button
                        @click="clearAIAnswer"
                        class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors"
                      >
                        清空
                      </button>
                      <button
                        @click="saveAIAnswer"
                        class="px-6 py-2 bg-purple-600 text-white text-sm font-medium rounded-lg hover:bg-purple-700 transition-colors"
                      >
                        💾 保存AI答案
                      </button>
                    </div>
                  </div>
                </div>
                </div>
                <!-- 主内容区域结束 -->

                <!-- 右侧垂直Tab栏 (选中试题后一直显示) -->
                <div v-if="viewingQuestion || editingQuestion" class="w-12 flex-shrink-0 bg-gradient-to-b from-gray-50 to-gray-100 border-l-2 border-gray-300 ml-2">
                  <div class="py-6 space-y-3 flex flex-col items-center">
                    <!-- 浏览按钮 -->
                    <button
                      @click="questionViewMode = 'view'"
                      :class="[
                        'w-10 py-6 rounded-lg font-medium transition-all duration-200 flex items-center justify-center',
                        questionViewMode === 'view'
                          ? 'bg-gradient-to-br from-blue-500 to-blue-600 text-white shadow-lg'
                          : 'text-gray-600 hover:text-gray-800 hover:bg-white hover:shadow-md'
                      ]"
                      :title="questionViewMode === 'view' ? '当前：浏览模式' : '切换到浏览模式'"
                    >
                      <span class="vertical-text text-sm tracking-wider">浏览</span>
                    </button>

                    <!-- 编辑按钮 -->
                    <button
                      @click="handleSwitchToEdit"
                      :class="[
                        'w-10 py-6 rounded-lg font-medium transition-all duration-200 flex items-center justify-center',
                        questionViewMode === 'edit'
                          ? 'bg-gradient-to-br from-green-500 to-emerald-500 text-white shadow-lg'
                          : 'text-gray-600 hover:text-gray-800 hover:bg-white hover:shadow-md'
                      ]"
                      :title="questionViewMode === 'edit' ? '当前：编辑模式' : '切换到编辑模式'"
                    >
                      <span class="vertical-text text-sm tracking-wider">编辑</span>
                    </button>

                    <!-- AI答题按钮 -->
                    <button
                      @click="handleSwitchToAIAnswer"
                      :class="[
                        'w-10 py-6 rounded-lg font-medium transition-all duration-200 flex items-center justify-center',
                        questionViewMode === 'ai-answer'
                          ? 'bg-gradient-to-br from-purple-500 to-indigo-500 text-white shadow-lg'
                          : 'text-gray-600 hover:text-gray-800 hover:bg-white hover:shadow-md'
                      ]"
                      :title="questionViewMode === 'ai-answer' ? '当前：AI答题模式' : '切换到AI答题模式'"
                    >
                      <span class="vertical-text text-sm tracking-wider">AI</span>
                    </button>
                  </div>
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

    <!-- 职业路径编辑Modal -->
    <CareerPathEditModal
      v-if="showCareerPathModal"
      :show="showCareerPathModal"
      :career-path="editingCareerPath"
      @close="showCareerPathModal = false; editingCareerPath = null"
      @success="handleCareerPathSaved"
    />

    <!-- AI答题Modal已移除，使用内联AI答题模式 -->
  </div>
</template>

<script>
import { getAllCareerPaths, deleteCareerPath } from '@/api/careerPaths'
import { getUnassociatedSkills } from '@/api/skills'
import majorCategoryApi from '@/api/majorCategoryApi'
import learningContentApi from '@/api/learningContentApi'
import { adminQuestionApi } from '@/api/questionApi'
import api from '@/api/index'
import { marked } from 'marked'
import draggable from 'vuedraggable'
import AIImportModal from '@/components/AIImportModal.vue'
import LearningContentEditForm from '@/components/skills/admin/LearningContentEditForm.vue'
import QuestionViewModal from '@/components/questions/QuestionViewModal.vue'
import QuestionEditModal from '@/components/questions/QuestionEditModal.vue'
import CareerPathEditModal from '@/components/skills/admin/CareerPathEditModal.vue'

export default {
  name: 'GeneralSkillContentManagement',
  components: {
    draggable,
    AIImportModal,
    LearningContentEditForm,
    QuestionViewModal,
    QuestionEditModal,
    CareerPathEditModal
  },
  data() {
    return {
      // 测试拖拽
      testItems: [],

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
      viewingQuestion: null,
      editingQuestion: null,

      // Modal状态
      showAIImportModal: false,
      selectedLearningContentId: null,
      showCareerPathModal: false,
      editingCareerPath: null,

      // 试题查看模式: 'view'(浏览) | 'edit'(编辑) | 'ai-answer'(AI答题)
      questionViewMode: 'view',
      // AI答题相关数据
      aiAnswerMode: 'template', // 'template' or 'free'
      aiAnswerTemplate: null, // 答题模版
      aiAnswerData: {}, // AI的答案数据
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
      if (newVal) {
        if (this.activeTab === 'learning') {
          await this.loadLearningContents()
        } else if (this.activeTab === 'questions') {
          await this.loadQuestions()
        }
      }
    },
    // 监听Tab切换
    async activeTab(newVal) {
      if (this.selectedFocusAreaId) {
        if (newVal === 'learning') {
          await this.loadLearningContents()
        } else if (newVal === 'questions') {
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
      this.selectedFocusAreaId = focusAreaId
    },

    // 获取某个大分类下的Focus Areas
    getFocusAreasByCategory(categoryId) {
      return this.focusAreas.filter(fa =>
        fa.categoryIds && fa.categoryIds.includes(categoryId)
      ).sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0))
    },

    // ===== Focus Area拖拽排序 =====
    async handleFocusAreaReorder(categoryId, newList) {
      try {
        // 批量更新displayOrder
        const updates = newList.map((fa, index) => ({
          id: fa.id,
          displayOrder: index
        }))

        await majorCategoryApi.batchUpdateFocusAreaOrder(updates)

        // 刷新Focus Areas
        await this.loadCategoriesAndFocusAreas()
      } catch (error) {
        console.error('更新Focus Area顺序失败:', error)
        alert('更新顺序失败: ' + (error.message || '未知错误'))
        await this.loadCategoriesAndFocusAreas()
      }
    },

    async handleFocusAreaDragEnd(categoryId, evt) {
      // evt.oldIndex和evt.newIndex是拖拽前后的索引
      if (evt.oldIndex === evt.newIndex) {
        return // 没有移动
      }

      try {

        // 获取该分类下的所有Focus Areas
        const focusAreasInCategory = this.getFocusAreasByCategory(categoryId)

        // 移动元素
        const movedItem = focusAreasInCategory[evt.oldIndex]
        focusAreasInCategory.splice(evt.oldIndex, 1)
        focusAreasInCategory.splice(evt.newIndex, 0, movedItem)


        // 批量更新displayOrder
        const updates = focusAreasInCategory.map((fa, index) => ({
          id: fa.id,
          displayOrder: index
        }))

        await majorCategoryApi.batchUpdateFocusAreaOrder(updates)

        // 刷新Focus Areas以获取最新顺序
        await this.loadFocusAreas()
      } catch (error) {
        console.error('更新Focus Area顺序失败:', error)
        alert('更新顺序失败: ' + (error.message || '未知错误'))
        // 失败时重新加载恢复原顺序
        await this.loadFocusAreas()
      }
    },

    // 处理第二类技能（无分类）的拖拽 - 使用v-model
    async handleSecondTypeFocusAreaReorder(newList) {
      try {
        // 批量更新displayOrder（按新顺序）
        const updates = newList.map((fa, index) => ({
          id: fa.id,
          displayOrder: index
        }))

        await majorCategoryApi.batchUpdateFocusAreaOrder(updates)

        // 刷新Focus Areas
        await this.loadCategoriesAndFocusAreas()
      } catch (error) {
        console.error('更新Focus Area顺序失败:', error)
        alert('更新顺序失败: ' + (error.message || '未知错误'))
        await this.loadCategoriesAndFocusAreas()
      }
    },

    // 处理第二类技能（无分类）的拖拽
    async handleSecondTypeFocusAreaDragEnd(evt) {
      if (evt.oldIndex === evt.newIndex) {
        return // 没有移动
      }

      try {

        // 批量更新displayOrder（按当前数组顺序）
        const updates = this.focusAreas.map((fa, index) => ({
          id: fa.id,
          displayOrder: index
        }))

        await majorCategoryApi.batchUpdateFocusAreaOrder(updates)

        // 刷新Focus Areas
        await this.loadFocusAreas()
      } catch (error) {
        console.error('更新Focus Area顺序失败:', error)
        alert('更新顺序失败: ' + (error.message || '未知错误'))
        await this.loadFocusAreas()
      }
    },

    // ===== 学习资料相关方法 =====
    async loadLearningContents() {
      if (!this.selectedFocusAreaId) {
        this.learningContents = []
        return
      }

      this.loadingContents = true
      try {
        // 查询该Focus Area的所有学习资料
        const data = await learningContentApi.getContentsForAdmin({
          focusAreaId: this.selectedFocusAreaId
        })

        // 检查数据结构 - API可能返回分页对象
        if (data && data.content) {
          this.learningContents = data.content || []
        } else {
          this.learningContents = data || []
        }
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
      if (!this.selectedFocusAreaId) {
        this.questions = []
        return
      }

      this.loadingQuestions = true
      try {
        // 查询该Focus Area的所有试题
        const data = await adminQuestionApi.getAllQuestions({
          focusAreaId: this.selectedFocusAreaId
        })
        this.questions = data || []
      } catch (error) {
        console.error('加载试题失败:', error)
        this.questions = []
      } finally {
        this.loadingQuestions = false
      }
    },
    async selectQuestion(question) {
      this.selectedQuestionId = question.id
      this.questionViewMode = 'view' // 默认进入浏览模式
      this.viewingQuestion = null
      this.editingQuestion = null

      // 加载完整的试题详情（包括AI笔记）
      try {
        const fullQuestion = await adminQuestionApi.getQuestionById(question.id)
        this.viewingQuestion = fullQuestion
        this.editingQuestion = fullQuestion // 也保留一份给编辑模式使用
      } catch (error) {
        console.error('加载试题详情失败:', error)
        this.viewingQuestion = question // 降级使用列表中的数据
        this.editingQuestion = question
      }
    },
    closeQuestionView() {
      this.selectedQuestionId = null
      this.viewingQuestion = null
      this.questionViewMode = 'view'
    },
    cancelEditQuestion() {
      // 取消编辑，回到浏览模式
      this.questionViewMode = 'view'
    },
    handleAddQuestion() {
      this.selectedQuestionId = null
      this.viewingQuestion = null
      this.editingQuestion = null
      this.questionViewMode = 'edit'
      // 触发立即显示编辑表单（新增模式）
      this.$nextTick(() => {
        this.editingQuestion = {}
      })
    },
    editFromView() {
      // 从浏览模式切换到编辑模式
      this.questionViewMode = 'edit'
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
        // 清除状态，回到浏览模式
        this.questionViewMode = 'view'
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
    },

    // Markdown渲染
    renderMarkdown(content) {
      if (!content) return ''
      return marked(content, { breaks: true })
    },

    // 解析模版字段（支持JSON字符串或对象）
    parseTemplateFields(templateFields) {
      if (!templateFields) return []
      try {
        return typeof templateFields === 'string' ? JSON.parse(templateFields) : templateFields
      } catch (error) {
        console.error('Failed to parse template fields:', error)
        return []
      }
    },

    // ===== 视图模式切换相关方法 =====
    handleSwitchToEdit() {
      this.questionViewMode = 'edit'
      // 确保editingQuestion有值
      if (this.viewingQuestion && !this.editingQuestion) {
        this.editingQuestion = { ...this.viewingQuestion }
      }
    },
    async handleSwitchToAIAnswer() {
      this.questionViewMode = 'ai-answer'
      // 加载AI答案和答题模版
      await this.loadAIAnswer()
    },

    // ===== AI答题相关方法 =====
    async loadAIAnswer() {
      if (!this.viewingQuestion || !this.viewingQuestion.id) return

      try {
        // 获取AI的笔记（user_id = -1）
        let aiNote = null
        try {
          aiNote = await adminQuestionApi.getAINote(this.viewingQuestion.id)
        } catch (error) {
          // 404是正常的，表示还没有AI笔记
          if (error.response && error.response.status === 404) {
            aiNote = null
          } else {
            throw error
          }
        }

        // 尝试通过Focus Area获取关联的答题模版
        // 首先从viewingQuestion的focusAreaId获取对应的skill_id

        let skillId = null

        if (this.viewingQuestion.focusAreaId) {
          const focusArea = this.focusAreas.find(fa => fa.id === this.viewingQuestion.focusAreaId)
          if (focusArea && focusArea.skillId) {
            skillId = focusArea.skillId
          } else {
          }
        } else {
        }

        // 如果没有从Focus Area获取到skill_id，尝试使用selectedSkillId
        if (!skillId && this.selectedSkillId) {
          skillId = this.selectedSkillId
        }

        if (skillId) {
          try {
            const template = await api.get(`/skills/${skillId}/templates/default`)

            if (template && template.templateFields && template.templateFields.length > 0) {
              this.aiAnswerTemplate = template
              this.aiAnswerMode = 'template'

              // 解析AI笔记到模版字段
              if (aiNote && aiNote.noteContent) {
                // 尝试解析模版格式（Markdown 格式：## 标题\n内容）
                const templateFields = this.parseTemplateFields(template.templateFields)
                let isTemplateFormat = true
                const parsedValues = {}


                // 尝试解析模版格式
                for (const field of templateFields) {
                  // 转义正则表达式中的特殊字符
                  const escapedLabel = field.label.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
                  const pattern = new RegExp(`## ${escapedLabel}\\s*\\n([\\s\\S]*?)(?=\\n## |$)`, 'i')
                  const match = aiNote.noteContent.match(pattern)


                  if (match) {
                    parsedValues[field.key] = match[1].trim()
                  } else {
                    isTemplateFormat = false
                    break
                  }
                }


                if (isTemplateFormat) {
                  // 是模版格式，填充到模版字段
                  this.aiAnswerData = { ...parsedValues, coreStrategy: aiNote.coreStrategy || '' }
                  this.aiAnswerMode = 'template'
                } else {
                  // 不是模版格式，作为自由文本
                  this.aiAnswerData = {
                    freeAnswer: aiNote.noteContent,
                    coreStrategy: aiNote.coreStrategy || ''
                  }
                  this.aiAnswerMode = 'free'
                }
              } else {
                // 初始化空对象
                const templateFields = this.parseTemplateFields(template.templateFields)
                this.aiAnswerData = { coreStrategy: '' }
                templateFields.forEach(field => {
                  this.aiAnswerData[field.key] = ''
                })
              }
              return
            }
          } catch (error) {
          }
        }

        // 无模版，使用自由模式
        this.aiAnswerTemplate = null
        this.aiAnswerMode = 'free'
        this.aiAnswerData = {
          freeAnswer: aiNote?.noteContent || '',
          coreStrategy: aiNote?.coreStrategy || ''
        }
      } catch (error) {
        console.error('[loadAIAnswer] 加载AI答案失败:', error)
        // 初始化为空答案
        this.aiAnswerTemplate = null
        this.aiAnswerMode = 'free'
        this.aiAnswerData = { freeAnswer: '', coreStrategy: '' }
      }
    },

    clearAIAnswer() {
      if (this.aiAnswerMode === 'template' && this.aiAnswerTemplate) {
        // 清空模版字段
        const templateFields = this.parseTemplateFields(this.aiAnswerTemplate.templateFields)
        templateFields.forEach(field => {
          this.aiAnswerData[field.key] = ''
        })
        this.aiAnswerData.coreStrategy = ''
      } else {
        // 清空自由答题
        this.aiAnswerData = { freeAnswer: '', coreStrategy: '' }
      }
    },
    // Placeholder for core strategy textarea (添加computed属性)
    coreStrategyPlaceholder() {
      if (this.viewingQuestion?.questionType === 'programming') {
        return '记录解题的核心思路和算法要点...\n\n例如：\n1. 使用双指针从两端向中间移动\n2. 时间复杂度 O(n)，空间复杂度 O(1)\n3. 注意边界条件...\n\n支持 Markdown 格式'
      } else {
        return '记录答题的核心思路和关键要点...\n\n例如：\n1. 明确问题核心诉求\n2. 关键解决步骤\n3. 注意事项...\n\n支持 Markdown 格式'
      }
    },

    async saveAIAnswer() {
      if (!this.viewingQuestion || !this.viewingQuestion.id) {
        alert('未选择试题')
        return
      }

      try {
        let noteContent = ''
        const coreStrategy = this.aiAnswerData.coreStrategy || ''

        // 模版模式：将模版字段合并为 Markdown 格式
        if (this.aiAnswerMode === 'template' && this.aiAnswerTemplate && this.aiAnswerTemplate.templateFields) {
          const templateFields = this.parseTemplateFields(this.aiAnswerTemplate.templateFields)
          const hasAnyValue = templateFields.some(field => this.aiAnswerData[field.key] && this.aiAnswerData[field.key].trim())

          if (!hasAnyValue && !coreStrategy.trim()) {
            alert('请至少填写一项内容')
            return
          }

          const parts = templateFields.map(field => {
            const value = this.aiAnswerData[field.key] || ''
            return `## ${field.label}\n${value}`
          })
          noteContent = parts.join('\n\n')
        } else {
          // 自由模式：保存纯文本
          noteContent = this.aiAnswerData.freeAnswer || ''

          if (!noteContent.trim() && !coreStrategy.trim()) {
            alert('请输入答案内容或核心思路')
            return
          }
        }

        // 调用保存AI笔记API（user_id = -1在后端处理）
        await adminQuestionApi.saveOrUpdateAINote(this.viewingQuestion.id, {
          noteContent,
          coreStrategy
        })

        alert('✅ AI答案保存成功！')

        // 刷新试题详情（更新aiNote字段）
        if (this.selectedQuestionId) {
          await this.selectQuestion({ id: this.selectedQuestionId })
        }
      } catch (error) {
        console.error('保存AI答案失败:', error)
        alert('❌ 保存失败：' + (error.response?.data?.message || error.message))
      }
    }
  }
}
</script>

<style scoped>
/* 垂直文本样式 */
.vertical-text {
  writing-mode: vertical-rl;
  text-orientation: upright;
}
</style>
