<template>
  <div class="h-screen flex flex-col bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- Header -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg px-6 py-4">
      <h1 class="text-2xl font-bold text-white">系统设计 - 典型案例实战</h1>
      <p class="text-xs text-blue-100 mt-1">掌握经典系统设计案例，提升架构设计能力</p>
    </div>

    <!-- Main Content: Three-column layout -->
    <div class="flex-1 flex overflow-hidden">
      <!-- Left: Case List (Narrower) -->
      <div class="w-1/6 bg-white border-r border-gray-200 flex flex-col shadow-lg">
        <div class="p-4 border-b border-gray-200 bg-gradient-to-r from-indigo-500 to-purple-500">
          <h2 class="text-lg font-semibold text-white">📋 案例列表</h2>
          <p class="text-xs text-indigo-100 mt-1">共 {{ filteredCases.length }} 个案例</p>
        </div>

        <!-- Filters -->
        <div class="p-2 border-b border-gray-200 bg-gray-50">
          <div class="flex gap-1 mb-1">
            <!-- Difficulty Filter -->
            <select
              v-model="filterDifficulty"
              class="flex-1 text-[10px] px-1.5 py-1 border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-indigo-500"
            >
              <option value="">难度</option>
              <option value="EASY">简单</option>
              <option value="MEDIUM">中等</option>
              <option value="HARD">困难</option>
            </select>

            <!-- Focus Area Filter -->
            <select
              v-model="filterFocusAreaId"
              class="flex-1 text-[10px] px-1.5 py-1 border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-indigo-500"
            >
              <option value="">领域</option>
              <option v-for="fa in allFocusAreas" :key="fa.id" :value="fa.id">
                {{ fa.name }}
              </option>
            </select>
          </div>

          <!-- Reset Button -->
          <button
            v-if="filterDifficulty || filterFocusAreaId"
            @click="resetFilters"
            class="w-full text-[10px] px-2 py-0.5 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition-colors"
          >
            重置
          </button>
        </div>

        <div class="flex-1 overflow-y-auto">
          <div
            v-for="caseItem in filteredCases"
            :key="caseItem.id"
            @click="selectCase(caseItem)"
            :class="[
              'p-2 border-b border-gray-100 cursor-pointer transition-all duration-200',
              selectedCase?.id === caseItem.id
                ? 'bg-gradient-to-r from-blue-50 to-purple-50 border-l-4 border-l-blue-600 shadow-md'
                : 'hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-sm'
            ]"
          >
            <div class="flex justify-between items-start mb-1">
              <h3 class="text-xs font-semibold text-gray-800 flex-1 leading-tight">{{ caseItem.title }}</h3>
              <span
                :class="[
                  'px-1.5 py-0.5 text-[9px] rounded-full font-semibold shadow-sm ml-1 flex-shrink-0',
                  caseItem.difficulty === 'EASY' ? 'bg-gradient-to-r from-green-400 to-emerald-500 text-white' :
                  caseItem.difficulty === 'MEDIUM' ? 'bg-gradient-to-r from-yellow-400 to-orange-500 text-white' :
                  'bg-gradient-to-r from-red-400 to-pink-500 text-white'
                ]"
              >
                {{ difficultyText(caseItem.difficulty) }}
              </span>
            </div>
            <div class="text-[10px] text-gray-500">
              <span v-if="caseItem.companyTags && caseItem.companyTags.length > 0">
                🏢 {{ caseItem.companyTags.slice(0, 2).join(', ') }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Middle + Right Columns Container -->
      <div class="flex-1 flex flex-col overflow-hidden">
        <div v-if="!selectedCase" class="h-full flex items-center justify-center text-gray-400 w-full">
          <p>← 请从左侧选择一个案例开始学习</p>
        </div>
        <template v-else>
          <!-- Case Header (full width at top) -->
          <div class="bg-gradient-to-r from-indigo-600 to-purple-600 border-b border-purple-700 px-6 py-4 flex-shrink-0 shadow-lg">
            <div class="flex justify-between items-center">
              <div class="flex items-center gap-3 flex-1">
                <h2 class="text-xl font-bold text-white">{{ selectedCase.title }}</h2>
                <span
                  :class="[
                    'px-3 py-1 text-xs rounded-full font-semibold shadow-md',
                    selectedCase.difficulty === 'EASY' ? 'bg-gradient-to-r from-green-400 to-emerald-500 text-white' :
                    selectedCase.difficulty === 'MEDIUM' ? 'bg-gradient-to-r from-yellow-400 to-orange-500 text-white' :
                    'bg-gradient-to-r from-red-400 to-pink-500 text-white'
                  ]"
                >
                  {{ difficultyText(selectedCase.difficulty) }} ({{ selectedCase.difficultyRating }}/10)
                </span>
                <span
                  v-if="selectedCase.isOfficial"
                  class="px-3 py-1 text-xs rounded-full bg-white text-indigo-600 font-semibold shadow-md"
                >
                  ⭐ 官方案例
                </span>
                <span
                  v-for="tag in selectedCase.companyTags"
                  :key="tag"
                  class="px-3 py-1 text-xs rounded-full bg-white/90 text-indigo-700 font-medium shadow-sm"
                >
                  🏢 {{ tag }}
                </span>
              </div>
              <button
                @click="toggleMode"
                :class="[
                  'px-4 py-2 rounded-lg transition-all text-sm font-bold shadow-md',
                  isEditMode
                    ? 'bg-gradient-to-r from-red-500 to-pink-500 text-white hover:from-red-600 hover:to-pink-600 hover:shadow-lg'
                    : 'bg-gradient-to-r from-green-500 to-emerald-500 text-white hover:from-green-600 hover:to-emerald-600 hover:shadow-lg'
                ]"
              >
                {{ isEditMode ? '✓ 退出编辑' : '✏️ 开始答题' }}
              </button>
            </div>
          </div>

          <!-- View Mode: Three columns -->
          <div v-if="!isEditMode" class="flex-1 flex overflow-hidden">
            <!-- Middle Column: Description + Resources (上下排列) -->
            <div class="w-1/4 bg-white border-r border-gray-200 p-4 overflow-y-auto shadow-inner">
              <!-- Case Description -->
              <div class="mb-6">
                <h3 class="text-lg font-bold mb-3 text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">📖 案例描述</h3>
                <div v-if="selectedCase.caseDescription" class="prose-compact max-w-none">
                  <div v-html="renderMarkdown(selectedCase.caseDescription)"></div>
                </div>
                <p v-else class="text-gray-400 text-sm">暂无描述</p>
              </div>

              <!-- Related Focus Areas -->
              <div class="mb-6">
                <h3 class="text-lg font-bold mb-3 text-transparent bg-clip-text bg-gradient-to-r from-purple-600 to-pink-600">🎯 相关知识领域 ({{ selectedCase.relatedFocusAreas?.length || 0 }})</h3>
                <div v-if="selectedCase.relatedFocusAreas && selectedCase.relatedFocusAreas.length > 0" class="flex flex-wrap gap-2">
                  <router-link
                    v-for="faId in selectedCase.relatedFocusAreas"
                    :key="faId"
                    :to="{ name: 'SystemDesignBasics', query: { focusAreaId: faId } }"
                    class="px-3 py-1.5 text-sm bg-purple-50 text-purple-700 rounded-lg border border-purple-200 hover:bg-purple-100 hover:border-purple-300 transition-colors cursor-pointer"
                  >
                    {{ getFocusAreaName(faId) }}
                  </router-link>
                </div>
                <p v-else class="text-gray-400 text-sm">暂无相关知识领域</p>
              </div>

              <!-- Learning Resources -->
              <div>
                <h3 class="text-lg font-bold mb-3 text-transparent bg-clip-text bg-gradient-to-r from-green-600 to-teal-600">📚 学习资源 ({{ selectedCase.resources?.length || 0 }})</h3>
                <div v-if="selectedCase.resources && selectedCase.resources.length > 0" class="space-y-2">
                  <div v-for="resource in selectedCase.resources" :key="resource.id" class="p-2 bg-gray-50 rounded-lg">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-xs px-2 py-0.5 rounded bg-blue-100 text-blue-700">{{ resource.resourceType }}</span>
                      <!-- Video: Show play button -->
                      <button
                        v-if="resource.resourceType === 'VIDEO'"
                        @click="openVideoModal(resource)"
                        class="text-sm font-medium text-blue-600 hover:underline flex items-center gap-1"
                      >
                        <span>▶️</span>
                        <span>{{ resource.title }}</span>
                      </button>
                      <!-- Other types: Open in new tab -->
                      <a
                        v-else
                        :href="resource.url"
                        target="_blank"
                        class="text-sm font-medium text-blue-600 hover:underline"
                      >
                        {{ resource.title }}
                      </a>
                    </div>
                    <p v-if="resource.description" class="text-xs text-gray-600">{{ truncate(resource.description, 80) }}</p>
                  </div>
                </div>
                <p v-else class="text-gray-400 text-sm">暂无学习资源</p>
              </div>
            </div>

            <!-- Right Column: Answer Selection + Tabs (最宽) -->
            <div class="flex-1 bg-white p-4 flex flex-col overflow-hidden">
              <!-- 答案选择下拉框 - 始终显示 -->
              <div class="mb-3 flex-shrink-0">
                <div class="flex gap-3 items-center">
                  <h3 class="text-lg font-semibold whitespace-nowrap">答案选择:</h3>
                  <select v-model="selectedSolutionId" class="w-80 px-3 py-1.5 border border-gray-300 rounded-lg text-sm">
                    <option value="my-answer">我的答案</option>
                    <option v-if="selectedCase.solutions && selectedCase.solutions.length > 0" v-for="solution in selectedCase.solutions" :key="solution.id" :value="solution.id">
                      {{ solution.solutionName }} {{ solution.author ? `(${solution.author})` : '' }}
                    </option>
                  </select>
                </div>
              </div>
              <div v-if="currentSolution || selectedSolutionId === 'my-answer'" class="flex-1 flex flex-col min-h-0">
                <div class="flex border-b border-gray-200 flex-shrink-0 bg-gray-50">
                  <button
                    v-for="(step, index) in stepTabs"
                    :key="index"
                    @click="activeStepTab = index"
                    :class="['px-4 py-2 text-sm font-semibold transition-all duration-200', getTabColorClass(step.color, activeStepTab === index)]"
                  >
                    {{ step.label }}
                  </button>
                </div>
                <div class="flex-1 overflow-y-auto py-4 prose-compact max-w-none" :key="'step-' + activeStepTab">
                  <!-- Image display for architecture diagram tab -->
                  <div v-if="stepTabs[activeStepTab].type === 'image' && currentSolution[stepTabs[activeStepTab].field]" class="flex justify-center items-start">
                    <img
                      :src="currentSolution[stepTabs[activeStepTab].field]"
                      alt="架构图"
                      class="max-w-full h-auto border border-gray-200 rounded-lg"
                      @error="handleImageError"
                    />
                  </div>
                  <!-- 要点拆分 - 卡片模式显示（我的答案专用） -->
                  <div v-else-if="stepTabs[activeStepTab].field === 'keyPoints' && selectedSolutionId === 'my-answer'" class="grid grid-cols-2 gap-4">
                    <!-- Requirement 卡片 -->
                    <div v-if="myAnswer.kpRequirement" class="bg-gradient-to-br from-blue-50 to-white rounded-xl border-2 border-blue-200 shadow-md hover:shadow-xl transition-all p-3">
                      <div class="flex items-center gap-2 mb-2">
                        <div class="w-2 h-6 bg-gradient-to-b from-blue-400 to-blue-600 rounded-full"></div>
                        <h4 class="text-sm font-bold text-blue-700">📋 Requirement</h4>
                      </div>
                      <div class="text-xs text-gray-700 leading-relaxed prose-compact" v-html="renderMarkdown(myAnswer.kpRequirement)"></div>
                    </div>

                    <!-- Components 卡片（跨两行，高度更大） -->
                    <div v-if="myAnswer.kpComponents" class="row-span-2 bg-gradient-to-br from-purple-50 to-white rounded-xl border-2 border-purple-200 shadow-md hover:shadow-xl transition-all p-3">
                      <div class="flex items-center gap-2 mb-2">
                        <div class="w-2 h-6 bg-gradient-to-b from-purple-400 to-purple-600 rounded-full"></div>
                        <h4 class="text-sm font-bold text-purple-700">🧩 Components</h4>
                      </div>
                      <div class="text-xs text-gray-700 leading-relaxed prose-compact" v-html="renderMarkdown(myAnswer.kpComponents)"></div>
                    </div>

                    <!-- NFR 卡片 -->
                    <div v-if="myAnswer.kpNfr" class="bg-gradient-to-br from-green-50 to-white rounded-xl border-2 border-green-200 shadow-md hover:shadow-xl transition-all p-3">
                      <div class="flex items-center gap-2 mb-2">
                        <div class="w-2 h-6 bg-gradient-to-b from-green-400 to-green-600 rounded-full"></div>
                        <h4 class="text-sm font-bold text-green-700">⚡ NFR</h4>
                      </div>
                      <div class="text-xs text-gray-700 leading-relaxed prose-compact" v-html="renderMarkdown(myAnswer.kpNfr)"></div>
                    </div>

                    <!-- Entity 卡片 -->
                    <div v-if="myAnswer.kpEntity" class="bg-gradient-to-br from-yellow-50 to-white rounded-xl border-2 border-yellow-200 shadow-md hover:shadow-xl transition-all p-3">
                      <div class="flex items-center gap-2 mb-2">
                        <div class="w-2 h-6 bg-gradient-to-b from-yellow-400 to-yellow-600 rounded-full"></div>
                        <h4 class="text-sm font-bold text-yellow-700">🗂️ Entity</h4>
                      </div>
                      <div class="text-xs text-gray-700 leading-relaxed prose-compact" v-html="renderMarkdown(myAnswer.kpEntity)"></div>
                    </div>

                    <!-- API 卡片 -->
                    <div v-if="myAnswer.kpApi" class="bg-gradient-to-br from-red-50 to-white rounded-xl border-2 border-red-200 shadow-md hover:shadow-xl transition-all p-3">
                      <div class="flex items-center gap-2 mb-2">
                        <div class="w-2 h-6 bg-gradient-to-b from-red-400 to-red-600 rounded-full"></div>
                        <h4 class="text-sm font-bold text-red-700">🔌 API</h4>
                      </div>
                      <div class="text-xs text-gray-700 leading-relaxed prose-compact" v-html="renderMarkdown(myAnswer.kpApi)"></div>
                    </div>

                    <!-- Object1 卡片 -->
                    <div v-if="myAnswer.kpObject1" class="bg-gradient-to-br from-indigo-50 to-white rounded-xl border-2 border-indigo-200 shadow-md hover:shadow-xl transition-all p-3">
                      <div class="flex items-center gap-2 mb-2">
                        <div class="w-2 h-6 bg-gradient-to-b from-indigo-400 to-indigo-600 rounded-full"></div>
                        <h4 class="text-sm font-bold text-indigo-700">🎯 Object1</h4>
                      </div>
                      <div class="text-xs text-gray-700 leading-relaxed prose-compact" v-html="renderMarkdown(myAnswer.kpObject1)"></div>
                    </div>

                    <!-- Object2 卡片 -->
                    <div v-if="myAnswer.kpObject2" class="bg-gradient-to-br from-pink-50 to-white rounded-xl border-2 border-pink-200 shadow-md hover:shadow-xl transition-all p-3">
                      <div class="flex items-center gap-2 mb-2">
                        <div class="w-2 h-6 bg-gradient-to-b from-pink-400 to-pink-600 rounded-full"></div>
                        <h4 class="text-sm font-bold text-pink-700">🎪 Object2</h4>
                      </div>
                      <div class="text-xs text-gray-700 leading-relaxed prose-compact" v-html="renderMarkdown(myAnswer.kpObject2)"></div>
                    </div>

                    <p v-if="!myAnswer.kpRequirement && !myAnswer.kpNfr && !myAnswer.kpEntity && !myAnswer.kpComponents && !myAnswer.kpApi && !myAnswer.kpObject1 && !myAnswer.kpObject2" class="col-span-2 text-gray-400">暂无要点内容</p>
                  </div>
                  <!-- Markdown content for other tabs -->
                  <div v-else-if="stepTabs[activeStepTab].type === 'markdown' && currentSolution[stepTabs[activeStepTab].field]" v-html="renderMarkdown(currentSolution[stepTabs[activeStepTab].field])"></div>
                  <p v-else class="text-gray-400">暂无内容</p>
                </div>
              </div>
              <p v-else class="text-gray-400 text-sm">暂无参考答案</p>
            </div>
          </div>

          <!-- Edit Mode: Description on top, split view below -->
          <div v-else class="flex-1 flex flex-col min-h-0 px-6 py-4">
            <!-- Top: Case Description (read-only in edit mode, collapsible) -->
            <div class="mb-4 pb-4 border-b border-gray-200 flex-shrink-0">
              <div class="flex justify-between items-center mb-3">
                <h3 class="text-lg font-semibold">案例描述</h3>
                <button
                  @click="isDescriptionExpanded = !isDescriptionExpanded"
                  class="text-sm text-blue-600 hover:text-blue-700 flex items-center gap-1"
                >
                  <span>{{ isDescriptionExpanded ? '折叠' : '展开' }}</span>
                  <span>{{ isDescriptionExpanded ? '▲' : '▼' }}</span>
                </button>
              </div>
              <div v-if="isDescriptionExpanded">
                <div v-if="selectedCase.caseDescription" class="prose-compact max-w-none">
                  <div v-html="renderMarkdown(selectedCase.caseDescription)"></div>
                </div>
                <p v-else class="text-gray-400 text-sm">暂无描述</p>
              </div>
            </div>

            <!-- Bottom: Two columns (Reference Solution | My Answer) - 动态布局 -->
            <div :class="['flex-1 grid gap-6 min-h-0', showAnswer ? 'grid-cols-2' : 'grid-cols-[auto_1fr]']">
              <!-- Left: Reference Solution - 隐藏时收缩 -->
              <div :class="['flex flex-col border-r-2 border-gray-300 pr-4 min-h-0', showAnswer ? '' : 'w-48']">
                <div class="flex items-center gap-2 mb-3">
                  <h3 v-if="showAnswer" class="text-base font-semibold whitespace-nowrap">参考答案:</h3>
                  <h3 v-else class="text-sm font-semibold whitespace-nowrap">答案</h3>
                  <select v-if="showAnswer && selectedCase.solutions && selectedCase.solutions.length > 0" v-model="selectedSolutionId" class="flex-1 px-2 py-1 border border-gray-300 rounded text-xs">
                    <option v-for="solution in selectedCase.solutions" :key="solution.id" :value="solution.id">{{ solution.solutionName }}</option>
                  </select>
                  <!-- 显示/隐藏答案按钮 -->
                  <button
                    v-if="selectedCase.solutions && selectedCase.solutions.length > 0"
                    @click="showAnswer = !showAnswer"
                    :class="[
                      'px-3 py-1 rounded text-xs font-semibold transition-all shadow-sm whitespace-nowrap',
                      showAnswer
                        ? 'bg-gradient-to-r from-green-500 to-emerald-500 text-white hover:from-green-600 hover:to-emerald-600'
                        : 'bg-gradient-to-r from-gray-400 to-gray-500 text-white hover:from-gray-500 hover:to-gray-600'
                    ]"
                  >
                    {{ showAnswer ? '👁️ 隐藏' : '👁️ 显示' }}
                  </button>
                </div>
                <!-- 显示参考答案 -->
                <div v-if="showAnswer && currentSolution" class="flex-1 flex flex-col min-h-0">
                  <div class="flex border-b border-gray-200 flex-shrink-0 overflow-x-auto bg-gray-50">
                    <button
                      v-for="(step, index) in stepTabs"
                      :key="index"
                      @click="activeStepTab = index"
                      :class="['px-2 py-1 text-xs font-semibold transition-all duration-200 whitespace-nowrap', getTabColorClass(step.color, activeStepTab === index)]"
                    >
                      {{ step.shortLabel }}
                    </button>
                  </div>
                  <div class="flex-1 overflow-y-auto py-2 prose-compact max-w-none text-sm">
                    <div v-if="currentSolution[stepTabs[activeStepTab].field]" v-html="renderMarkdown(currentSolution[stepTabs[activeStepTab].field])"></div>
                    <p v-else class="text-gray-400 text-xs">暂无内容</p>
                  </div>
                </div>
                <!-- 隐藏答案时显示提示 -->
                <div v-else-if="!showAnswer && selectedCase.solutions && selectedCase.solutions.length > 0" class="flex-1 flex items-center justify-center bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
                  <div class="text-center px-2">
                    <div class="text-4xl mb-2">🔒</div>
                    <p class="text-gray-400 text-xs mb-1 font-semibold">已隐藏</p>
                    <p class="text-gray-500 text-xs leading-tight">点击"显示"<br>查看答案</p>
                  </div>
                </div>
                <!-- 无参考答案 -->
                <div v-else class="flex items-center justify-center text-gray-400 text-xs">
                  暂无参考答案
                </div>
              </div>

              <!-- Right: My Answer (步骤 + 要点拆分) -->
              <div class="flex flex-col min-h-0">
                <div class="flex justify-between items-center mb-3">
                  <h3 class="text-base font-semibold">我的答案</h3>
                  <button @click="saveMyAnswer" class="px-2 py-1 bg-blue-600 text-white rounded hover:bg-blue-700 text-xs">保存</button>
                </div>
                <div class="flex-1 flex flex-col min-h-0">
                  <div class="flex border-b border-gray-200 flex-shrink-0 overflow-x-auto bg-gray-50">
                    <button
                      v-for="(step, index) in stepTabs"
                      :key="index"
                      @click="activeStepTab = index"
                      :class="['px-2 py-1 text-xs font-semibold transition-all duration-200 whitespace-nowrap', getTabColorClass(step.color, activeStepTab === index)]"
                    >
                      {{ step.shortLabel }}
                    </button>
                  </div>

                  <!-- Image URL input for architecture diagram -->
                  <div v-if="stepTabs[activeStepTab].type === 'image'" class="flex-1 flex flex-col mt-2 gap-2">
                    <input
                      v-model="myAnswer[stepTabs[activeStepTab].field]"
                      type="text"
                      class="px-2 py-1 border border-gray-300 rounded text-xs"
                      placeholder="输入架构图URL"
                    />
                    <div v-if="myAnswer[stepTabs[activeStepTab].field]" class="flex-1 border border-gray-200 rounded p-2 overflow-auto bg-gray-50">
                      <img
                        :src="myAnswer[stepTabs[activeStepTab].field]"
                        alt="架构图预览"
                        class="max-w-full h-auto"
                        @error="handleImageError"
                      />
                    </div>
                    <p v-else class="text-gray-400 text-xs">输入图片URL后会显示预览</p>
                  </div>

                  <!-- Key Points 结构化编辑 (要点拆分) - 两列布局 -->
                  <div v-else-if="stepTabs[activeStepTab].field === 'keyPoints'" class="flex-1 overflow-y-auto pr-1 mt-2">
                    <div class="grid grid-cols-2 gap-3">
                      <!-- Requirement -->
                      <div>
                        <label class="block text-xs font-medium text-gray-700 mb-0.5">Requirement</label>
                        <textarea
                          v-model="myAnswer.kpRequirement"
                          rows="5"
                          class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                          placeholder="功能需求..."
                        ></textarea>
                      </div>

                      <!-- Components (跨两行) -->
                      <div class="row-span-2">
                        <label class="block text-xs font-medium text-gray-700 mb-0.5">Components</label>
                        <textarea
                          v-model="myAnswer.kpComponents"
                          rows="12"
                          class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                          placeholder="关键组件..."
                        ></textarea>
                      </div>

                      <!-- NFR -->
                      <div>
                        <label class="block text-xs font-medium text-gray-700 mb-0.5">NFR</label>
                        <textarea
                          v-model="myAnswer.kpNfr"
                          rows="5"
                          class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                          placeholder="非功能性需求..."
                        ></textarea>
                      </div>

                      <!-- Entity -->
                      <div>
                        <label class="block text-xs font-medium text-gray-700 mb-0.5">Entity</label>
                        <textarea
                          v-model="myAnswer.kpEntity"
                          rows="5"
                          class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                          placeholder="核心实体..."
                        ></textarea>
                      </div>

                      <!-- API -->
                      <div>
                        <label class="block text-xs font-medium text-gray-700 mb-0.5">API</label>
                        <textarea
                          v-model="myAnswer.kpApi"
                          rows="5"
                          class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                          placeholder="API设计..."
                        ></textarea>
                      </div>

                      <!-- Object1 -->
                      <div>
                        <label class="block text-xs font-medium text-gray-700 mb-0.5">Object1</label>
                        <textarea
                          v-model="myAnswer.kpObject1"
                          rows="5"
                          class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                          placeholder="核心对象1..."
                        ></textarea>
                      </div>

                      <!-- Object2 -->
                      <div>
                        <label class="block text-xs font-medium text-gray-700 mb-0.5">Object2</label>
                        <textarea
                          v-model="myAnswer.kpObject2"
                          rows="5"
                          class="w-full px-2 py-1 border border-gray-300 rounded text-xs resize-none"
                          placeholder="核心对象2..."
                        ></textarea>
                      </div>
                    </div>
                  </div>

                  <!-- Markdown textarea for other tabs -->
                  <textarea
                    v-else
                    v-model="myAnswer[stepTabs[activeStepTab].field]"
                    class="flex-1 w-full p-2 border border-gray-300 rounded font-mono text-xs resize-none mt-2"
                    :placeholder="`输入${stepTabs[activeStepTab].label}的内容...`"
                  ></textarea>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- Video Modal (Draggable) -->
    <div
      v-if="showVideoModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @mousedown="closeVideoModal"
    >
      <div
        ref="videoModalRef"
        class="bg-white rounded-lg shadow-2xl overflow-hidden"
        :style="{ width: '800px', maxWidth: '90vw', position: 'relative', left: modalPosition.x + 'px', top: modalPosition.y + 'px' }"
        @mousedown.stop
      >
        <!-- Modal Header (Draggable) -->
        <div
          class="bg-blue-600 text-white px-4 py-3 flex justify-between items-center cursor-move"
          @mousedown="startDrag"
        >
          <h3 class="font-semibold">{{ currentVideo?.title || '视频播放' }}</h3>
          <button @click="closeVideoModal" class="text-white hover:text-gray-200 text-xl">✕</button>
        </div>

        <!-- Modal Body -->
        <div class="p-4 bg-gray-50">
          <div class="aspect-video bg-black rounded-lg overflow-hidden">
            <iframe
              v-if="currentVideo?.url"
              :src="getEmbedUrl(currentVideo.url)"
              class="w-full h-full"
              frameborder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen
            ></iframe>
          </div>
          <p v-if="currentVideo?.description" class="mt-3 text-sm text-gray-600">{{ currentVideo.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import * as systemDesignCaseApi from '@/api/systemDesignCaseApi'
import majorCategoryApi from '@/api/majorCategoryApi'
import { marked } from 'marked'

// 必须在setup顶层调用useRoute
const route = useRoute()

const cases = ref([])
const selectedCase = ref(null)
const selectedSolutionId = ref(null)
const activeStepTab = ref(0)
const isEditMode = ref(false)
const isDescriptionExpanded = ref(true) // 案例描述展开/折叠状态（默认展开）
const showAnswer = ref(false) // 答题模式：是否显示参考答案（默认隐藏）

// Focus Areas data for displaying names
const allFocusAreas = ref([])

// Filter states
const filterDifficulty = ref('')
const filterFocusAreaId = ref('')
const myAnswer = ref({
  // 结构化答案字段（Key Points）
  kpRequirement: '',
  kpNfr: '',
  kpEntity: '',
  kpComponents: '',
  kpApi: '',
  kpObject1: '',
  kpObject2: '',
  // 原有的step字段
  step1Requirements: '',
  step2Entities: '',
  step3Api: '',
  step4DataFlow: '',
  step5Architecture: '',
  step6DeepDive: '',
  architectureDiagramUrl: '',
  keyPoints: ''
})

// Video modal state
const showVideoModal = ref(false)
const currentVideo = ref(null)
const videoModalRef = ref(null)
const modalPosition = ref({ x: 0, y: 0 })
const isDragging = ref(false)
const dragStart = ref({ x: 0, y: 0 })

const stepTabs = [
  { label: 'Step 1: 需求', shortLabel: 'Step1', field: 'step1Requirements', type: 'markdown', color: 'blue' },
  { label: 'Step 2: 实体', shortLabel: 'Step2', field: 'step2Entities', type: 'markdown', color: 'purple' },
  { label: 'Step 3: API', shortLabel: 'Step3', field: 'step3Api', type: 'markdown', color: 'green' },
  { label: 'Step 4: 数据流', shortLabel: 'Step4', field: 'step4DataFlow', type: 'markdown', color: 'yellow' },
  { label: 'Step 5: 架构', shortLabel: 'Step5', field: 'step5Architecture', type: 'markdown', color: 'red' },
  { label: 'Step 6: 深入', shortLabel: 'Step6', field: 'step6DeepDive', type: 'markdown', color: 'indigo' },
  { label: '架构图', shortLabel: '架构图', field: 'architectureDiagramUrl', type: 'image', color: 'pink' },
  { label: '要点总结', shortLabel: '要点', field: 'keyPoints', type: 'markdown', color: 'teal' }
]

const currentSolution = computed(() => {
  if (!selectedSolutionId.value) return null

  // 如果选择的是"我的答案"
  if (selectedSolutionId.value === 'my-answer') {
    return {
      id: 'my-answer',
      solutionName: '我的答案',
      step1Requirements: myAnswer.value.step1Requirements,
      step2Entities: myAnswer.value.step2Entities,
      step3Api: myAnswer.value.step3Api,
      step4DataFlow: myAnswer.value.step4DataFlow,
      step5Architecture: myAnswer.value.step5Architecture,
      step6DeepDive: myAnswer.value.step6DeepDive,
      architectureDiagramUrl: myAnswer.value.architectureDiagramUrl,
      keyPoints: myAnswer.value.keyPoints
    }
  }

  // 否则返回参考答案
  if (!selectedCase.value?.solutions) return null
  return selectedCase.value.solutions.find(s => s.id === selectedSolutionId.value)
})

watch(() => selectedCase.value?.solutions, (solutions) => {
  if (solutions && solutions.length > 0) {
    selectedSolutionId.value = solutions[0].id
  } else {
    // 没有官方答案时，默认选择"我的答案"
    selectedSolutionId.value = 'my-answer'
  }
  activeStepTab.value = 0
}, { immediate: true })

// Filtered cases based on difficulty and focus area
const filteredCases = computed(() => {
  let result = cases.value

  // Filter by difficulty
  if (filterDifficulty.value) {
    result = result.filter(c => c.difficulty === filterDifficulty.value)
  }

  // Filter by focus area
  if (filterFocusAreaId.value) {
    result = result.filter(c => {
      // c.relatedFocusAreas is an array of focus area IDs
      return c.relatedFocusAreas && c.relatedFocusAreas.includes(parseInt(filterFocusAreaId.value))
    })
  }

  return result
})

// Reset filters
const resetFilters = () => {
  filterDifficulty.value = ''
  filterFocusAreaId.value = ''
}

const difficultyText = (difficulty) => {
  const map = { EASY: '简单', MEDIUM: '中等', HARD: '困难' }
  return map[difficulty] || difficulty
}

const getTabColorClass = (color, isActive) => {
  const colorMap = {
    blue: isActive ? 'text-blue-600 border-b-2 border-blue-600 bg-blue-50' : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50',
    purple: isActive ? 'text-purple-600 border-b-2 border-purple-600 bg-purple-50' : 'text-gray-600 hover:text-purple-600 hover:bg-purple-50',
    green: isActive ? 'text-green-600 border-b-2 border-green-600 bg-green-50' : 'text-gray-600 hover:text-green-600 hover:bg-green-50',
    yellow: isActive ? 'text-yellow-600 border-b-2 border-yellow-600 bg-yellow-50' : 'text-gray-600 hover:text-yellow-600 hover:bg-yellow-50',
    red: isActive ? 'text-red-600 border-b-2 border-red-600 bg-red-50' : 'text-gray-600 hover:text-red-600 hover:bg-red-50',
    indigo: isActive ? 'text-indigo-600 border-b-2 border-indigo-600 bg-indigo-50' : 'text-gray-600 hover:text-indigo-600 hover:bg-indigo-50',
    pink: isActive ? 'text-pink-600 border-b-2 border-pink-600 bg-pink-50' : 'text-gray-600 hover:text-pink-600 hover:bg-pink-50',
    teal: isActive ? 'text-teal-600 border-b-2 border-teal-600 bg-teal-50' : 'text-gray-600 hover:text-teal-600 hover:bg-teal-50'
  }
  return colorMap[color] || (isActive ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600 hover:text-gray-800')
}

const truncate = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const renderMarkdown = (markdown) => {
  if (!markdown) return ''
  // Convert escaped newlines to actual newlines
  const processedMarkdown = markdown.replace(/\\n/g, '\n')
  return marked(processedMarkdown)
}

// Helper function to get focus area name by ID
const getFocusAreaName = (faId) => {
  const fa = allFocusAreas.value.find(f => f.id === faId)
  return fa ? fa.name : `ID: ${faId}`
}

// Load focus areas for displaying names
const loadFocusAreas = async () => {
  try {
    const SYSTEM_DESIGN_SKILL_ID = 2
    allFocusAreas.value = await majorCategoryApi.getFocusAreasWithCategories(SYSTEM_DESIGN_SKILL_ID)
  } catch (error) {
    console.error('加载知识领域失败:', error)
  }
}

const loadCases = async () => {
  try {
    const data = await systemDesignCaseApi.getOfficialCases()
    cases.value = data || []

    // 检查URL查询参数，如果有caseId则自动选择对应案例
    const caseIdFromQuery = route.query.caseId

    if (caseIdFromQuery) {
      const targetCase = cases.value.find(c => c.id == caseIdFromQuery)
      if (targetCase) {
        await selectCase(targetCase)
        return
      }
    }

    // 默认选择第一个案例
    if (cases.value.length > 0 && !selectedCase.value) {
      await selectCase(cases.value[0])
    }
  } catch (error) {
    console.error('加载案例列表失败:', error)
  }
}

const selectCase = async (caseItem) => {
  try {
    const data = await systemDesignCaseApi.getCaseByIdForUser(caseItem.id)
    selectedCase.value = data
    isEditMode.value = false
    activeStepTab.value = 0

    // 尝试加载用户之前保存的答案
    try {
      const myNote = await systemDesignCaseApi.getMyNote(caseItem.id)
      if (myNote) {
        // 映射后端字段到前端字段
        myAnswer.value = {
          // 结构化字段（Key Points）
          kpRequirement: myNote.kpRequirement || '',
          kpNfr: myNote.kpNfr || '',
          kpEntity: myNote.kpEntity || '',
          kpComponents: myNote.kpComponents || '',
          kpApi: myNote.kpApi || '',
          kpObject1: myNote.kpObject1 || '',
          kpObject2: myNote.kpObject2 || '',
          // 原有的step字段
          step1Requirements: myNote.userStep1RequirementsAndNfr || '',
          step2Entities: myNote.userStep2CoreEntities || '',
          step3Api: myNote.userStep3ApiDesign || '',
          step4DataFlow: myNote.userStep4DataFlow || '',
          step5Architecture: myNote.userStep5HighLevelDesign || '',
          step6DeepDive: myNote.userStep6DeepDiveAndTradeoffs || '',
          architectureDiagramUrl: myNote.architectureDiagramUrl || '',
          keyPoints: myNote.keyTakeaways || ''
        }
      } else {
        // 没有保存过的答案，初始化为空
        resetMyAnswer()
      }
    } catch (error) {
      // 404表示还没有保存过答案，这是正常情况
      if (error.response?.status === 404) {
        resetMyAnswer()
      } else {
        console.error('加载我的答案失败:', error)
        resetMyAnswer()
      }
    }
  } catch (error) {
    console.error('加载案例详情失败:', error)
  }
}

const resetMyAnswer = () => {
  myAnswer.value = {
    // 结构化字段（Key Points）
    kpRequirement: '',
    kpNfr: '',
    kpEntity: '',
    kpComponents: '',
    kpApi: '',
    kpObject1: '',
    kpObject2: '',
    // 原有的step字段
    step1Requirements: '',
    step2Entities: '',
    step3Api: '',
    step4DataFlow: '',
    step5Architecture: '',
    step6DeepDive: '',
    architectureDiagramUrl: '',
    keyPoints: ''
  }
}

const toggleMode = () => {
  isEditMode.value = !isEditMode.value
  activeStepTab.value = 0
}

const handleImageError = (event) => {
  console.error('图片加载失败:', event.target.src)
  event.target.alt = '图片加载失败，请检查URL是否正确'
}

const saveMyAnswer = async () => {
  if (!selectedCase.value) return

  try {
    // 映射前端字段到后端字段名（camelCase to snake_case）
    const data = {
      // 结构化字段（Key Points）
      kpRequirement: myAnswer.value.kpRequirement,
      kpNfr: myAnswer.value.kpNfr,
      kpEntity: myAnswer.value.kpEntity,
      kpComponents: myAnswer.value.kpComponents,
      kpApi: myAnswer.value.kpApi,
      kpObject1: myAnswer.value.kpObject1,
      kpObject2: myAnswer.value.kpObject2,
      // 原有的step字段
      userStep1RequirementsAndNfr: myAnswer.value.step1Requirements,
      userStep2CoreEntities: myAnswer.value.step2Entities,
      userStep3ApiDesign: myAnswer.value.step3Api,
      userStep4DataFlow: myAnswer.value.step4DataFlow,
      userStep5HighLevelDesign: myAnswer.value.step5Architecture,
      userStep6DeepDiveAndTradeoffs: myAnswer.value.step6DeepDive,
      architectureDiagramUrl: myAnswer.value.architectureDiagramUrl,
      keyTakeaways: myAnswer.value.keyPoints
    }

    await systemDesignCaseApi.saveOrUpdateMyNote(selectedCase.value.id, data)
    alert('答案已保存成功！')
  } catch (error) {
    console.error('保存答案失败:', error)
    alert('保存失败，请重试：' + (error.message || '未知错误'))
  }
}

// Video modal functions
const openVideoModal = (resource) => {
  currentVideo.value = resource
  modalPosition.value = { x: 0, y: 0 }
  showVideoModal.value = true
}

const closeVideoModal = () => {
  showVideoModal.value = false
  currentVideo.value = null
}

const getEmbedUrl = (url) => {
  // YouTube
  if (url.includes('youtube.com') || url.includes('youtu.be')) {
    const videoId = url.includes('youtu.be')
      ? url.split('youtu.be/')[1]?.split('?')[0]
      : new URLSearchParams(new URL(url).search).get('v')
    return `https://www.youtube.com/embed/${videoId}`
  }

  // Bilibili
  if (url.includes('bilibili.com')) {
    const bvMatch = url.match(/BV\w+/)
    if (bvMatch) {
      return `https://player.bilibili.com/player.html?bvid=${bvMatch[0]}&high_quality=1`
    }
  }

  // 直接返回URL（假设是可嵌入的视频链接）
  return url
}

const startDrag = (event) => {
  isDragging.value = true
  dragStart.value = {
    x: event.clientX - modalPosition.value.x,
    y: event.clientY - modalPosition.value.y
  }

  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}

const onDrag = (event) => {
  if (!isDragging.value) return

  modalPosition.value = {
    x: event.clientX - dragStart.value.x,
    y: event.clientY - dragStart.value.y
  }
}

const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

onMounted(() => {
  loadFocusAreas()
  loadCases()
})
</script>

<style scoped>
.prose-compact {
  color: #374151;
  font-size: 0.875rem;
  line-height: 1.5;
}
.prose-compact :deep(h1) { font-size: 1.25rem; font-weight: 700; margin-top: 0.75em; margin-bottom: 0.5em; color: #1f2937; }
.prose-compact :deep(h2) { font-size: 1.125rem; font-weight: 600; margin-top: 0.75em; margin-bottom: 0.5em; color: #1f2937; }
.prose-compact :deep(h3) { font-size: 1rem; font-weight: 600; margin-top: 0.5em; margin-bottom: 0.375em; color: #374151; }
.prose-compact :deep(p) { margin-top: 0.5em; margin-bottom: 0.5em; }
.prose-compact :deep(ul), .prose-compact :deep(ol) { margin-top: 0.5em; margin-bottom: 0.5em; padding-left: 1.5em; }
.prose-compact :deep(li) { margin-top: 0.25em; margin-bottom: 0.25em; }
.prose-compact :deep(code) { background-color: #f3f4f6; padding: 0.125rem 0.25rem; border-radius: 0.25rem; font-size: 0.8125em; }
.prose-compact :deep(strong) { font-weight: 600; color: #1f2937; }
</style>
