<template>
  <div class="project-experiences h-full flex bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 左侧: 项目列表 -->
    <div class="w-80 bg-white border-r border-gray-200 flex flex-col shadow-lg">
      <div class="bg-gradient-to-r from-indigo-600 to-purple-600 p-4 border-b border-purple-700">
        <h2 class="text-lg font-bold text-white mb-3">💼 项目经验库</h2>
        <button
          @click="showCreateModal = true"
          class="w-full px-3 py-1.5 text-sm bg-gradient-to-r from-green-500 to-emerald-500 text-white rounded-lg hover:from-green-600 hover:to-emerald-600 hover:shadow-lg transition-all flex items-center justify-center gap-2 font-semibold shadow-md"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          新建项目
        </button>
      </div>

      <div class="flex-1 overflow-y-auto">
        <div class="p-2">
          <div
            v-for="project in projects"
            :key="project.id"
            @click="selectProject(project.id)"
            :class="[
              'p-3 mb-2 rounded-lg cursor-pointer transition-all duration-200',
              selectedProjectId === project.id
                ? 'bg-gradient-to-r from-indigo-50 to-purple-50 border-l-4 border-l-indigo-600 shadow-md'
                : 'bg-gray-50 border border-gray-200 hover:bg-gradient-to-r hover:from-gray-50 hover:to-indigo-50 hover:shadow-sm'
            ]"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <h3 class="font-semibold text-gray-900 text-sm">{{ project.projectName }}</h3>
                <p class="text-xs text-gray-600 mt-1">{{ project.projectType }}</p>
                <p class="text-xs text-gray-400 mt-1">
                  {{ project.startDate }} - {{ project.endDate || '进行中' }}
                </p>
              </div>
            </div>
          </div>

          <div v-if="!projects.length" class="text-center text-gray-500 py-8">
            <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="text-sm">暂无项目经验</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧: 项目详情 -->
    <div class="flex-1 flex flex-col">
      <div v-if="currentProject" class="flex-1 flex flex-col">
        <!-- 顶部操作栏 -->
        <div class="bg-gradient-to-r from-indigo-600 to-purple-600 border-b border-purple-700 p-4 flex items-center justify-between shadow-lg">
          <h1 class="text-xl font-bold text-white">{{ currentProject.projectName }}</h1>
          <div class="flex gap-2">
            <button
              @click="deleteProject"
              class="px-4 py-2 bg-gradient-to-r from-red-500 to-pink-500 text-white rounded-lg hover:from-red-600 hover:to-pink-600 hover:shadow-lg transition-all font-semibold shadow-md"
            >
              删除项目
            </button>
          </div>
        </div>

        <!-- Focus Area 引导区（固定显示） -->
        <div class="bg-gradient-to-r from-blue-50 to-indigo-50 border-b border-blue-200 p-3">
          <!-- 已选择的 Focus Areas -->
          <div v-if="selectedFocusAreas.length > 0">
            <div class="flex items-center mb-3">
              <svg class="w-5 h-5 text-blue-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/>
                <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/>
              </svg>
              <h3 class="text-sm font-semibold text-gray-700">本项目关联的技能点</h3>
              <button
                @click="showFocusAreaSelector = true"
                class="ml-auto px-3 py-1 text-xs bg-white border border-blue-300 text-blue-600 rounded hover:bg-blue-50"
              >
                编辑关联
              </button>
            </div>

            <!-- Focus Area 卡片列表（水平滚动） -->
            <div class="flex gap-3 overflow-x-auto pb-2">
              <div
                v-for="fa in selectedFocusAreas"
                :key="fa.id"
                class="flex-shrink-0 w-96 bg-white rounded-lg border border-blue-200 p-3 shadow-sm"
              >
                <div class="flex items-start">
                  <span class="text-2xl mr-2">{{ getFocusAreaIcon(fa.name) }}</span>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between mb-1">
                      <h4 class="font-semibold text-gray-900 text-sm">{{ fa.name }}</h4>
                      <button
                        @click="toggleFocusAreaExpanded(fa.id)"
                        class="text-gray-500 hover:text-gray-700 focus:outline-none"
                      >
                        <svg
                          class="w-4 h-4 transition-transform"
                          :class="{ 'rotate-180': expandedFocusAreas[fa.id] }"
                          fill="none"
                          stroke="currentColor"
                          viewBox="0 0 24 24"
                        >
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                        </svg>
                      </button>
                    </div>
                    <div
                      v-show="expandedFocusAreas[fa.id]"
                      v-html="renderMarkdown(fa.description)"
                      class="text-xs text-gray-600 prose-sm"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 未选择 Focus Area 的提示 -->
          <div v-else class="text-center py-3">
            <p class="text-sm text-gray-600 mb-2">尚未关联技能点</p>
            <button
              @click="showFocusAreaSelector = true"
              class="px-3 py-1.5 text-sm bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700"
            >
              + 关联技能点
            </button>
          </div>
        </div>

        <!-- Tab导航 -->
        <div class="bg-white border-b border-gray-200">
          <div class="flex px-4">
            <button
              v-for="tab in tabs"
              :key="tab.id"
              @click="activeTab = tab.id"
              :class="[
                'px-6 py-3 font-semibold transition-all duration-200',
                activeTab === tab.id
                  ? 'text-indigo-600 border-b-2 border-indigo-600 bg-indigo-50'
                  : 'text-gray-600 hover:text-indigo-600 hover:bg-indigo-50'
              ]"
            >
              {{ tab.name }}
            </button>
          </div>
        </div>

        <!-- Tab内容 -->
        <div class="flex-1 overflow-y-auto">
          <!-- Tab 1: 4W (What/Why/Where/When) -->
          <div v-if="activeTab === '4w'" class="bg-white rounded-lg shadow p-6 space-y-6">
            <div class="space-y-4">
              <!-- What - 项目是什么 -->
              <div>
                <div class="flex items-center justify-between mb-2">
                  <h3 class="text-base font-semibold text-gray-900">What - 项目是什么</h3>
                  <div class="flex gap-2">
                    <button
                      v-if="!editModes.what"
                      @click="editModes.what = true"
                      class="px-3 py-1.5 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                      编辑
                    </button>
                    <template v-else>
                      <button
                        @click="editModes.what = false"
                        class="px-3 py-1.5 text-sm bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                      >
                        取消
                      </button>
                      <button
                        @click="saveWhat"
                        class="px-3 py-1.5 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700"
                      >
                        保存
                      </button>
                    </template>
                  </div>
                </div>
                <div v-if="!editModes.what" v-html="renderMarkdown(currentProject.whatDescription)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.whatDescription"
                  rows="4"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="描述项目的核心内容和目标..."
                ></textarea>
              </div>

              <!-- When - 时间范围 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">When - 时间范围</h3>
                <div v-if="!editModes.what" class="text-sm text-gray-900">
                  {{ currentProject.startDate }} - {{ currentProject.endDate || '进行中' }}
                </div>
                <div v-else class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">开始时间</label>
                    <input
                      v-model="currentProject.startDate"
                      type="month"
                      class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">结束时间</label>
                    <input
                      v-model="currentProject.endDate"
                      type="month"
                      class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg"
                    />
                  </div>
                </div>
              </div>

              <!-- Who - 团队规模和角色 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">Who - 团队规模和角色</h3>
                <div v-if="!editModes.what" class="text-sm text-gray-900">
                  <p v-if="currentProject.teamSize"><strong>团队规模:</strong> {{ currentProject.teamSize }} 人</p>
                  <p v-if="currentProject.myRole"><strong>我的角色:</strong> {{ currentProject.myRole }}</p>
                </div>
                <div v-else class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">团队规模</label>
                    <input
                      v-model="currentProject.teamSize"
                      type="number"
                      class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg"
                      placeholder="例如: 5"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">我的角色</label>
                    <input
                      v-model="currentProject.myRole"
                      type="text"
                      class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg"
                      placeholder="例如: Tech Lead"
                    />
                  </div>
                </div>
              </div>

              <!-- Why - 项目背景和目标 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">Why - 项目背景和目标</h3>
                <div v-if="!editModes.what" v-html="renderMarkdown(currentProject.background)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.background"
                  rows="4"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="项目背景、为什么做这个项目、业务目标..."
                ></textarea>
              </div>
            </div>
          </div>

          <!-- Tab 2: Problem & Challenge -->
          <div v-if="activeTab === 'problem'" class="bg-white rounded-lg shadow p-6 space-y-6">
            <div class="space-y-4">
              <!-- 核心问题 -->
              <div>
                <div class="flex items-center justify-between mb-2">
                  <h3 class="text-lg font-semibold text-gray-900">核心问题</h3>
                  <div class="flex gap-2">
                    <button
                      v-if="!editModes.problem"
                      @click="editModes.problem = true"
                      class="px-3 py-1.5 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                      编辑
                    </button>
                    <template v-else>
                      <button
                        @click="editModes.problem = false"
                        class="px-3 py-1.5 text-sm bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                      >
                        取消
                      </button>
                      <button
                        @click="saveProblem"
                        class="px-3 py-1.5 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700"
                      >
                        保存
                      </button>
                    </template>
                  </div>
                </div>
                <div v-if="!editModes.problem" v-html="renderMarkdown(currentProject.problemStatement)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.problemStatement"
                  rows="6"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="描述遇到的核心问题..."
                ></textarea>
              </div>

              <!-- 主要挑战和难点 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">主要挑战和难点</h3>
                <div v-if="!editModes.problem" v-html="renderMarkdown(currentProject.challenges)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.challenges"
                  rows="6"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="列出主要挑战和技术难点..."
                ></textarea>
              </div>

              <!-- 约束条件 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">约束条件</h3>
                <div v-if="!editModes.problem" v-html="renderMarkdown(currentProject.constraints)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.constraints"
                  rows="4"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="时间、资源、技术等约束条件..."
                ></textarea>
              </div>
            </div>
          </div>

          <!-- Tab 3: Solution -->
          <div v-if="activeTab === 'solution'" class="bg-white rounded-lg shadow p-6 space-y-6">
            <div class="space-y-4">
              <!-- 技术选型和理由 -->
              <div>
                <div class="flex items-center justify-between mb-2">
                  <h3 class="text-lg font-semibold text-gray-900">技术选型和理由</h3>
                  <div class="flex gap-2">
                    <button
                      v-if="!editModes.solution"
                      @click="editModes.solution = true"
                      class="px-3 py-1.5 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                      编辑
                    </button>
                    <template v-else>
                      <button
                        @click="editModes.solution = false"
                        class="px-3 py-1.5 text-sm bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                      >
                        取消
                      </button>
                      <button
                        @click="saveSolution"
                        class="px-3 py-1.5 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700"
                      >
                        保存
                      </button>
                    </template>
                  </div>
                </div>
                <div v-if="!editModes.solution" v-html="renderMarkdown(currentProject.techStack)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.techStack"
                  rows="6"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="使用的技术栈和选型理由..."
                ></textarea>
              </div>

              <!-- 架构设计 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">架构设计</h3>
                <div v-if="!editModes.solution" v-html="renderMarkdown(currentProject.architecture)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.architecture"
                  rows="6"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="系统架构设计..."
                ></textarea>
              </div>

              <!-- 创新点/差异化做法 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">创新点/差异化做法</h3>
                <div v-if="!editModes.solution" v-html="renderMarkdown(currentProject.innovation)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.innovation"
                  rows="4"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="与常规方案的差异和创新点..."
                ></textarea>
              </div>

              <!-- 个人贡献 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">个人贡献</h3>
                <div v-if="!editModes.solution" v-html="renderMarkdown(currentProject.myContribution)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.myContribution"
                  rows="6"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="我在项目中的具体工作和贡献..."
                ></textarea>
              </div>
            </div>
          </div>

          <!-- Tab 4: Result -->
          <div v-if="activeTab === 'result'" class="bg-white rounded-lg shadow p-6 space-y-6">
            <div class="space-y-4">
              <!-- 量化数据 -->
              <div>
                <div class="flex items-center justify-between mb-2">
                  <h3 class="text-lg font-semibold text-gray-900">量化数据</h3>
                  <div class="flex gap-2">
                    <button
                      v-if="!editModes.result"
                      @click="editModes.result = true"
                      class="px-3 py-1.5 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                      编辑
                    </button>
                    <template v-else>
                      <button
                        @click="editModes.result = false"
                        class="px-3 py-1.5 text-sm bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                      >
                        取消
                      </button>
                      <button
                        @click="saveResult"
                        class="px-3 py-1.5 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700"
                      >
                        保存
                      </button>
                    </template>
                  </div>
                </div>
                <div v-if="!editModes.result" v-html="renderMarkdown(currentProject.quantitativeResults)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.quantitativeResults"
                  rows="6"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="性能提升、成本节约、用户增长等量化数据..."
                ></textarea>
              </div>

              <!-- 业务影响 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">业务影响</h3>
                <div v-if="!editModes.result" v-html="renderMarkdown(currentProject.businessImpact)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.businessImpact"
                  rows="4"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="对业务的实际影响..."
                ></textarea>
              </div>

              <!-- 团队/个人成长 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">团队/个人成长</h3>
                <div v-if="!editModes.result" v-html="renderMarkdown(currentProject.personalGrowth)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.personalGrowth"
                  rows="4"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="团队能力提升、个人成长..."
                ></textarea>
              </div>

              <!-- 经验教训 -->
              <div>
                <h3 class="text-base font-semibold text-gray-900 mb-2">经验教训 (Lessons Learned)</h3>
                <div v-if="!editModes.result" v-html="renderMarkdown(currentProject.lessonsLearned)" class="prose prose-sm max-w-none"></div>
                <textarea
                  v-else
                  v-model="currentProject.lessonsLearned"
                  rows="6"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="项目中的经验教训、下次可以改进的地方..."
                ></textarea>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="flex-1 flex items-center justify-center">
        <div class="text-center text-gray-500">
          <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p class="text-lg">请选择或创建一个项目</p>
        </div>
      </div>
    </div>

    <!-- Focus Area 选择器 Modal -->
    <div v-if="showFocusAreaSelector" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[700px] max-h-[80vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">关联 Behavioral 技能点</h3>
        <p class="text-sm text-gray-600 mb-4">选择这个项目体现的 Behavioral 技能点（可多选）</p>

        <div class="space-y-2 mb-6">
          <div
            v-for="fa in behavioralFocusAreas"
            :key="fa.id"
            class="flex items-start p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer"
            @click="toggleFocusArea(fa.id)"
          >
            <input
              type="checkbox"
              :checked="currentProject.focusAreaIds?.includes(fa.id)"
              class="mt-1 mr-3"
              @click.stop="toggleFocusArea(fa.id)"
            />
            <div class="flex-1">
              <div class="flex items-center">
                <span class="text-lg mr-2">{{ getFocusAreaIcon(fa.name) }}</span>
                <h4 class="font-semibold text-gray-900">{{ fa.name }}</h4>
              </div>
              <p class="text-sm text-gray-600 mt-1">{{ fa.description }}</p>
            </div>
          </div>
        </div>

        <div class="flex justify-end gap-2">
          <button
            @click="showFocusAreaSelector = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            关闭
          </button>
          <button
            @click="saveFocusAreaSelection"
            class="px-3 py-1.5 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            保存
          </button>
        </div>
      </div>
    </div>

    <!-- 创建项目 Modal -->
    <div v-if="showCreateModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[600px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">创建项目</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">项目名称 *</label>
            <input
              v-model="formData.projectName"
              type="text"
              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg"
              placeholder="例如: Kubernetes Migration Project"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">项目类型</label>
            <select v-model="formData.projectType" class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg">
              <option value="Feature">Feature Development</option>
              <option value="Optimization">Performance Optimization</option>
              <option value="Migration">System Migration</option>
              <option value="Infrastructure">Infrastructure</option>
              <option value="BugFix">Bug Fix</option>
              <option value="Other">Other</option>
            </select>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">开始时间</label>
              <input
                v-model="formData.startDate"
                type="month"
                class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">结束时间</label>
              <input
                v-model="formData.endDate"
                type="month"
                class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg"
              />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">项目简介</label>
            <textarea
              v-model="formData.whatDescription"
              rows="4"
              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg"
              placeholder="简要描述项目内容..."
            ></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button
            @click="showCreateModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="createProject"
            class="px-3 py-1.5 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            创建
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { projectApi } from '@/api/projectApi'
import MarkdownIt from 'markdown-it'
import { getGuidanceText, getFocusAreaIcon } from '@/constants/focusAreaGuidance'

const md = new MarkdownIt()

const projects = ref([])
const selectedProjectId = ref(null)
const showCreateModal = ref(false)
const showFocusAreaSelector = ref(false)
const activeTab = ref('4w')
const behavioralFocusAreas = ref([])  // All available Behavioral Focus Areas
const expandedFocusAreas = ref({})  // Track which Focus Area cards are expanded
const formData = ref({
  projectName: '',
  projectType: 'Feature',
  whatDescription: '',
  startDate: '',
  endDate: '',
  focusAreaIds: []  // Selected Focus Area IDs
})

// 每个Tab独立的编辑模式
const editModes = ref({
  what: false,
  problem: false,
  solution: false,
  result: false
})

const tabs = [
  { id: '4w', name: '4W' },
  { id: 'problem', name: 'Problem & Challenge' },
  { id: 'solution', name: 'Solution' },
  { id: 'result', name: 'Result' }
]

const currentProject = computed(() =>
  projects.value.find(p => p.id === selectedProjectId.value)
)

// Selected Focus Areas for current project (with full details)
const selectedFocusAreas = computed(() => {
  if (!currentProject.value || !currentProject.value.focusAreas) {
    return []
  }
  return currentProject.value.focusAreas
})

// Tab-specific contextual guidance
const currentTabGuidance = computed(() => {
  if (selectedFocusAreas.value.length === 0) return null

  const tabKey = {
    '4w': 'what',
    'problem': 'problem',
    'solution': 'solution',
    'result': 'result'
  }[activeTab.value]

  if (!tabKey) return null

  const guidanceMap = {
    '4w': {
      title: '填写 4W 时，考虑如何体现以下技能点',
      tips: selectedFocusAreas.value.map(fa =>
        `${getFocusAreaIcon(fa.name)} ${fa.name}: ${getGuidanceText(fa.name, 'what')}`
      )
    },
    'problem': {
      title: '描述挑战时，强调这些技能点的应用场景',
      tips: selectedFocusAreas.value.map(fa =>
        `${getFocusAreaIcon(fa.name)} ${fa.name}: ${getGuidanceText(fa.name, 'problem')}`
      )
    },
    'solution': {
      title: '说明解决方案时，突出这些技能点的具体运用',
      tips: selectedFocusAreas.value.map(fa =>
        `${getFocusAreaIcon(fa.name)} ${fa.name}: ${getGuidanceText(fa.name, 'solution')}`
      )
    },
    'result': {
      title: '总结成果时，量化这些技能点带来的影响',
      tips: selectedFocusAreas.value.map(fa =>
        `${getFocusAreaIcon(fa.name)} ${fa.name}: ${getGuidanceText(fa.name, 'result')}`
      )
    }
  }

  return guidanceMap[activeTab.value]
})

// Markdown渲染
const renderMarkdown = (text) => {
  if (!text) return '<p class="text-gray-500 italic">暂无内容</p>'
  if (typeof text !== 'string') {
    text = String(text)
  }
  return md.render(text)
}

onMounted(async () => {
  await loadProjects()
  await loadBehavioralFocusAreas()
})

const loadBehavioralFocusAreas = async () => {
  try {
    const data = await projectApi.getBehavioralFocusAreas()
    behavioralFocusAreas.value = data || []
  } catch (error) {
    console.error('加载Behavioral Focus Areas失败:', error)
  }
}

const loadProjects = async () => {
  try {
    const data = await projectApi.getProjects()
    // 转换日期格式：YYYY-MM-DD -> YYYY-MM (用于month input)
    projects.value = (data || []).map(project => ({
      ...project,
      startDate: project.startDate ? project.startDate.substring(0, 7) : null,
      endDate: project.endDate ? project.endDate.substring(0, 7) : null
    }))
    // 默认选择第一个项目
    if (projects.value.length > 0) {
      selectedProjectId.value = projects.value[0].id
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
  }
}

const selectProject = (projectId) => {
  selectedProjectId.value = projectId
  // 重置所有编辑模式
  Object.keys(editModes.value).forEach(key => {
    editModes.value[key] = false
  })
  // 重置到第一个Tab
  activeTab.value = '4w'
}

const createProject = async () => {
  if (!formData.value.projectName.trim()) {
    alert('请输入项目名称')
    return
  }

  try {
    // 转换日期格式：YYYY-MM -> YYYY-MM-01
    const projectData = {
      ...formData.value,
      startDate: formData.value.startDate ? `${formData.value.startDate}-01` : null,
      endDate: formData.value.endDate ? `${formData.value.endDate}-01` : null
    }
    await projectApi.createProject(projectData)
    alert('创建成功')
    showCreateModal.value = false
    formData.value = {
      projectName: '',
      projectType: 'Feature',
      whatDescription: '',
      startDate: '',
      endDate: ''
    }
    await loadProjects()
  } catch (error) {
    console.error('创建项目失败:', error)
    alert('创建失败')
  }
}

const deleteProject = async () => {
  if (!confirm('确定要删除这个项目吗?')) return

  try {
    await projectApi.deleteProject(selectedProjectId.value)
    selectedProjectId.value = null
    await loadProjects()
    alert('删除成功')
  } catch (error) {
    console.error('删除项目失败:', error)
    alert('删除失败')
  }
}

// 保存 4W Tab
const saveWhat = async () => {
  try {
    // 转换日期格式：YYYY-MM -> YYYY-MM-01
    const projectData = {
      ...currentProject.value,
      startDate: currentProject.value.startDate ? `${currentProject.value.startDate}-01` : null,
      endDate: currentProject.value.endDate ? `${currentProject.value.endDate}-01` : null
    }
    await projectApi.updateProject(selectedProjectId.value, projectData)
    alert('保存成功')
    editModes.value.what = false
    await loadProjects()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

// 保存 Problem Tab
const saveProblem = async () => {
  try {
    // 转换日期格式：YYYY-MM -> YYYY-MM-01
    const projectData = {
      ...currentProject.value,
      startDate: currentProject.value.startDate ? `${currentProject.value.startDate}-01` : null,
      endDate: currentProject.value.endDate ? `${currentProject.value.endDate}-01` : null
    }
    await projectApi.updateProject(selectedProjectId.value, projectData)
    alert('保存成功')
    editModes.value.problem = false
    await loadProjects()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

// 保存 Solution Tab
const saveSolution = async () => {
  try {
    // 转换日期格式：YYYY-MM -> YYYY-MM-01
    const projectData = {
      ...currentProject.value,
      startDate: currentProject.value.startDate ? `${currentProject.value.startDate}-01` : null,
      endDate: currentProject.value.endDate ? `${currentProject.value.endDate}-01` : null
    }
    await projectApi.updateProject(selectedProjectId.value, projectData)
    alert('保存成功')
    editModes.value.solution = false
    await loadProjects()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

// 保存 Result Tab
const saveResult = async () => {
  try {
    // 转换日期格式：YYYY-MM -> YYYY-MM-01
    const projectData = {
      ...currentProject.value,
      startDate: currentProject.value.startDate ? `${currentProject.value.startDate}-01` : null,
      endDate: currentProject.value.endDate ? `${currentProject.value.endDate}-01` : null
    }
    await projectApi.updateProject(selectedProjectId.value, projectData)
    alert('保存成功')
    editModes.value.result = false
    await loadProjects()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

// Toggle Focus Area card expansion
const toggleFocusAreaExpanded = (focusAreaId) => {
  expandedFocusAreas.value[focusAreaId] = !expandedFocusAreas.value[focusAreaId]
}

// Toggle Focus Area selection
const toggleFocusArea = (focusAreaId) => {
  if (!currentProject.value.focusAreaIds) {
    currentProject.value.focusAreaIds = []
  }

  const index = currentProject.value.focusAreaIds.indexOf(focusAreaId)
  if (index > -1) {
    currentProject.value.focusAreaIds.splice(index, 1)
  } else {
    currentProject.value.focusAreaIds.push(focusAreaId)
  }
}

// Save Focus Area selection
const saveFocusAreaSelection = async () => {
  try {
    // 转换日期格式：YYYY-MM -> YYYY-MM-01
    const projectData = {
      ...currentProject.value,
      startDate: currentProject.value.startDate ? `${currentProject.value.startDate}-01` : null,
      endDate: currentProject.value.endDate ? `${currentProject.value.endDate}-01` : null
    }
    await projectApi.updateProject(selectedProjectId.value, projectData)
    alert('关联保存成功')
    showFocusAreaSelector.value = false
    await loadProjects()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}
</script>

<style scoped>
/* Markdown渲染样式 */
.prose {
  @apply text-gray-900;
}

.prose :deep(h1) {
  @apply text-2xl font-bold mt-4 mb-2;
}

.prose :deep(h2) {
  @apply text-xl font-bold mt-3 mb-2;
}

.prose :deep(h3) {
  @apply text-lg font-semibold mt-2 mb-1;
}

.prose :deep(p) {
  @apply mb-2;
}

.prose :deep(ul) {
  @apply list-disc ml-6 mb-2;
}

.prose :deep(ol) {
  @apply list-decimal ml-6 mb-2;
}

.prose :deep(li) {
  @apply mb-1;
}

.prose :deep(strong) {
  @apply font-semibold;
}

.prose :deep(em) {
  @apply italic;
}

.prose :deep(a) {
  @apply text-blue-600 hover:underline;
}

.prose :deep(code) {
  @apply bg-gray-100 px-1 py-0.5 rounded text-sm font-mono;
}

.prose :deep(pre) {
  @apply bg-gray-100 p-3 rounded overflow-x-auto mb-2;
}

.prose :deep(blockquote) {
  @apply border-l-4 border-gray-300 pl-4 italic my-2;
}
</style>
