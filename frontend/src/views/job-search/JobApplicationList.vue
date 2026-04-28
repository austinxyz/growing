<template>
  <div class="job-application-list h-full flex bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 左侧：职位申请列表 -->
    <div :class="[
      'bg-white border-r border-gray-200 flex flex-col shadow-lg transition-all duration-300',
      showApplicationList ? 'w-96' : 'w-0 overflow-hidden'
    ]">
      <div class="bg-gradient-to-r from-indigo-600 to-purple-600 p-4 border-b border-purple-700">
        <h2 class="text-lg font-bold text-white mb-3">📋 职位申请列表</h2>
        <div class="space-y-2">
          <select
            v-model="filterStatus"
            class="w-full px-3 py-2 bg-white text-gray-700 rounded-lg text-sm focus:ring-2 focus:ring-purple-300"
          >
            <option value="">全部申请状态</option>
            <option value="NotApplied">未申请</option>
            <option value="Applied">已投递</option>
            <option value="Screening">筛选中</option>
            <option value="Interviewing">面试中</option>
            <option value="Offer">Offer</option>
            <option value="Rejected">已拒绝</option>
            <option value="Withdrawn">已撤回</option>
          </select>
          <select
            v-model="filterJobStatus"
            class="w-full px-3 py-2 bg-white text-gray-700 rounded-lg text-sm focus:ring-2 focus:ring-purple-300"
          >
            <option value="!Closed">隐藏已关闭</option>
            <option value="">全部招聘状态</option>
            <option value="Open">开放招聘</option>
            <option value="ActivelyHiring">积极招聘</option>
            <option value="Closed">已关闭</option>
          </select>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto">
        <div class="p-2">
          <div
            v-for="app in filteredApplications"
            :key="app.id"
            @click="selectApplication(app.id)"
            :class="[
              'p-4 mb-2 rounded-lg cursor-pointer transition-all duration-200 border',
              selectedApplicationId === app.id
                ? 'bg-gradient-to-r from-indigo-50 to-purple-50 border-indigo-600 shadow-md'
                : 'bg-gray-50 border-gray-200 hover:bg-gradient-to-r hover:from-gray-50 hover:to-indigo-50 hover:shadow-sm'
            ]"
          >
            <div class="flex items-start justify-between mb-2">
              <div class="flex-1">
                <h3 class="font-bold text-gray-900">{{ app.positionName }}</h3>
                <p class="text-sm text-gray-600 mt-1">{{ app.companyName }}</p>
              </div>
              <span :class="['px-2 py-1 rounded-full text-xs font-medium', getStatusColor(app.applicationStatus)]">
                {{ getStatusText(app.applicationStatus) }}
              </span>
            </div>
            <div class="flex items-center gap-3 text-xs text-gray-500">
              <span v-if="app.positionLevel">{{ app.positionLevel }}</span>
              <span v-if="app.interviewStageCount">{{ app.interviewStageCount }} 个阶段</span>
              <span v-if="app.interviewRecordCount">{{ app.interviewRecordCount }} 条记录</span>
            </div>
          </div>

          <div v-if="filteredApplications.length === 0" class="text-center text-gray-500 py-8">
            <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="text-sm">暂无申请</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧：职位详情 + Tabs -->
    <div class="flex-1 flex flex-col">
      <div v-if="currentApplication" class="flex-1 flex flex-col">
        <!-- 顶部标题栏 -->
        <div class="bg-gradient-to-r from-indigo-600 to-purple-600 border-b border-purple-700 p-4">
          <div class="flex items-center justify-between">
            <div>
              <h1 class="text-xl font-bold text-white">{{ currentApplication.positionName }}</h1>
              <p class="text-sm text-purple-100 mt-1">{{ currentApplication.companyName }}</p>
            </div>
            <div class="flex gap-2">
              <button
                @click="showApplicationList = !showApplicationList"
                class="px-3 py-2 bg-white bg-opacity-20 hover:bg-opacity-30 text-white rounded-lg transition-all"
                :title="showApplicationList ? '隐藏职位列表' : '显示职位列表'"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path v-if="showApplicationList" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
                  <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 5l7 7-7 7M5 5l7 7-7 7" />
                </svg>
              </button>
              <button
                @click="goToCompanyPage"
                class="px-4 py-2 bg-white bg-opacity-20 hover:bg-opacity-30 text-white rounded-lg transition-all flex items-center gap-2"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                </svg>
                职位管理
              </button>
            </div>
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
        <div class="flex-1 overflow-y-auto p-6">

          <!-- Tab 1: 概览 -->
          <div v-if="activeTab === 'overview'" class="space-y-6">
            <!-- JD (Job Description) -->
            <div class="bg-white rounded-lg shadow p-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                Job Description
              </h3>

              <div class="grid grid-cols-3 gap-4 mb-4">
                <div>
                  <label class="block text-sm font-medium text-gray-600 mb-1">职位级别</label>
                  <p class="text-gray-900">{{ currentApplication.positionLevel || '未设置' }}</p>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-600 mb-1">招聘状态</label>
                  <span :class="['px-3 py-1 rounded-full text-sm font-medium inline-block', getJobStatusColor(currentApplication.jobStatus)]">
                    {{ getJobStatusLabel(currentApplication.jobStatus) }}
                  </span>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-600 mb-1">申请状态</label>
                  <div class="flex items-center gap-3">
                    <span v-if="!isEditingStatus" :class="['px-3 py-1 rounded-full text-sm font-medium inline-block', getStatusColor(currentApplication.applicationStatus)]">
                      {{ getStatusText(currentApplication.applicationStatus) }}
                    </span>
                    <select
                      v-else
                      v-model="editingStatusValue"
                      class="px-3 py-1 border border-gray-300 rounded-lg text-sm"
                    >
                      <option value="未申请">未申请</option>
                      <option value="已投递">已投递</option>
                      <option value="筛选中">筛选中</option>
                      <option value="面试中">面试中</option>
                      <option value="Offer">Offer</option>
                      <option value="已拒绝">已拒绝</option>
                      <option value="已撤回">已撤回</option>
                    </select>
                    <button
                      v-if="!isEditingStatus"
                      @click="startEditingStatus"
                      class="text-blue-600 hover:text-blue-700 text-sm"
                    >
                      编辑
                    </button>
                    <template v-else>
                      <button
                        @click="saveApplicationStatus"
                        class="px-2 py-1 bg-blue-600 text-white text-xs rounded hover:bg-blue-700"
                      >
                        保存
                      </button>
                      <button
                        @click="cancelEditingStatus"
                        class="px-2 py-1 bg-gray-500 text-white text-xs rounded hover:bg-gray-600"
                      >
                        取消
                      </button>
                    </template>
                  </div>
                </div>
              </div>

              <div v-if="currentApplication.jobUrl" class="mb-4">
                <label class="block text-sm font-medium text-gray-600 mb-1">职位链接</label>
                <a :href="currentApplication.jobUrl" target="_blank" class="text-blue-600 hover:underline break-all text-sm">
                  {{ currentApplication.jobUrl }}
                </a>
              </div>

              <div class="border-t pt-4 mb-4">
                <h4 class="font-semibold text-gray-900 mb-2">技能要求</h4>
                <div v-if="currentApplication.qualifications" v-html="renderMarkdown(currentApplication.qualifications)" class="prose max-w-none"></div>
                <p v-else class="text-gray-500 italic text-sm">未设置</p>
              </div>

              <div class="border-t pt-4">
                <h4 class="font-semibold text-gray-900 mb-2">岗位职责</h4>
                <div v-if="currentApplication.responsibilities" v-html="renderMarkdown(currentApplication.responsibilities)" class="prose max-w-none"></div>
                <p v-else class="text-gray-500 italic text-sm">未设置</p>
              </div>
            </div>

            <!-- Recruiter Insights -->
            <div class="bg-white rounded-lg shadow p-6">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold text-gray-900 flex items-center gap-2">
                  <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Recruiter Insights
                </h3>
                <button
                  @click="openRecruiterInsightsModal"
                  class="px-3 py-1 bg-purple-500 text-white text-sm rounded-lg hover:bg-purple-600"
                >
                  {{ currentApplication.recruiterInsights ? '编辑' : '添加' }}
                </button>
              </div>

              <div v-if="currentApplication.recruiterInsights" class="space-y-4">
                <div v-if="currentApplication.recruiterInsights.teamSize">
                  <label class="block text-sm font-medium text-gray-600 mb-1">团队规模</label>
                  <p class="text-gray-900">{{ currentApplication.recruiterInsights.teamSize }}</p>
                </div>

                <div v-if="currentApplication.recruiterInsights.teamCulture">
                  <label class="block text-sm font-medium text-gray-600 mb-1">团队文化</label>
                  <p class="text-gray-900 text-sm">{{ currentApplication.recruiterInsights.teamCulture }}</p>
                </div>

                <div v-if="currentApplication.recruiterInsights.techStackPreference && currentApplication.recruiterInsights.techStackPreference.length > 0">
                  <label class="block text-sm font-medium text-gray-600 mb-1">技术栈偏好</label>
                  <div class="flex flex-wrap gap-2">
                    <span
                      v-for="tech in currentApplication.recruiterInsights.techStackPreference"
                      :key="tech"
                      class="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs font-medium"
                    >
                      {{ tech }}
                    </span>
                  </div>
                </div>

                <div v-if="currentApplication.recruiterInsights.interviewFocus">
                  <label class="block text-sm font-medium text-gray-600 mb-1">面试重点</label>
                  <p class="text-gray-900 text-sm">{{ currentApplication.recruiterInsights.interviewFocus }}</p>
                </div>

                <div v-if="currentApplication.recruiterInsights.processTips">
                  <label class="block text-sm font-medium text-gray-600 mb-1">流程Tips</label>
                  <div class="bg-yellow-50 border-l-4 border-yellow-400 p-3 rounded">
                    <p class="text-sm text-yellow-800">{{ currentApplication.recruiterInsights.processTips }}</p>
                  </div>
                </div>
              </div>

              <div v-else class="text-center text-gray-500 py-8">
                <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <p class="text-sm">暂无Recruiter Insights</p>
                <p class="text-xs mt-1 text-gray-400">点击"添加"按钮记录Recruiter提供的信息</p>
              </div>
            </div>
          </div>

          <!-- Tab 2: 面试 (两栏布局) -->
          <div v-if="activeTab === 'interviews'" class="flex h-full gap-0">
            <!-- 左侧：面试阶段列表 -->
            <div class="w-80 bg-white border-r border-gray-200 flex flex-col shadow-sm">
              <div class="bg-gradient-to-r from-indigo-600 to-purple-600 p-4">
                <div class="flex items-center justify-between mb-2">
                  <h3 class="text-lg font-bold text-white">面试阶段</h3>
                  <button
                    @click="addStage"
                    class="px-3 py-1 bg-white text-indigo-600 text-sm rounded-lg hover:bg-indigo-50 font-medium"
                  >
                    + 添加
                  </button>
                </div>
                <p class="text-sm text-purple-100">{{ interviewStages.length }} 个阶段</p>
              </div>

              <div class="flex-1 overflow-y-auto p-2">
                <div
                  v-for="stage in interviewStages"
                  :key="stage.id"
                  @click="selectedStageId = stage.id"
                  :class="[
                    'p-4 mb-2 rounded-lg cursor-pointer transition-all duration-200 border',
                    selectedStageId === stage.id
                      ? 'bg-gradient-to-r from-indigo-50 to-purple-50 border-indigo-600 shadow-md'
                      : 'bg-gray-50 border-gray-200 hover:bg-gradient-to-r hover:from-gray-50 hover:to-indigo-50 hover:shadow-sm'
                  ]"
                >
                  <div class="flex items-start justify-between mb-2">
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-1">
                        <span class="px-2 py-1 bg-indigo-100 text-indigo-700 rounded text-xs font-semibold">
                          阶段 {{ stage.stageOrder }}
                        </span>
                      </div>
                      <h4 class="font-bold text-gray-900 text-sm">{{ stage.stageName }}</h4>
                      <!-- 关联技能显示 -->
                      <div v-if="stage.skillNames && stage.skillNames.length > 0" class="flex flex-wrap gap-1 mt-2">
                        <span
                          v-for="(skillName, index) in stage.skillNames"
                          :key="index"
                          class="px-2 py-0.5 bg-blue-50 text-blue-700 rounded text-xs"
                        >
                          {{ skillName }}
                        </span>
                      </div>
                    </div>
                    <button
                      @click.stop="editStage(stage)"
                      class="text-blue-600 hover:text-blue-700 text-sm"
                      title="编辑阶段"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                  </div>
                  <div class="flex items-center gap-3 text-xs text-gray-500 mt-2">
                    <span v-if="stage.focusAreas?.length > 0">{{ stage.focusAreas.length }} 个领域</span>
                    <span v-if="stage.checklistCount > 0">{{ stage.checklistCount }} 项清单</span>
                    <span v-if="getRecordsByStage(stage.id).length > 0">
                      {{ getRecordsByStage(stage.id).length }} 条记录
                    </span>
                  </div>
                </div>

                <div v-if="interviewStages.length === 0" class="text-center text-gray-500 py-8">
                  <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  <p class="text-sm">暂无面试阶段</p>
                  <p class="text-xs mt-1 text-gray-400">请先在"职位管理"页面添加</p>
                </div>
              </div>
            </div>

            <!-- 右侧：阶段详情 -->
            <div class="flex-1 flex flex-col bg-gray-50">
              <!-- Sub-Tab导航 -->
              <div v-if="selectedStage" class="bg-white border-b border-gray-200">
                <div class="flex px-4">
                  <button
                    v-for="subTab in interviewSubTabs"
                    :key="subTab.id"
                    @click="activeInterviewSubTab = subTab.id"
                    :class="[
                      'px-4 py-3 text-sm font-medium transition-all duration-200 flex items-center gap-2',
                      activeInterviewSubTab === subTab.id
                        ? 'text-indigo-600 border-b-2 border-indigo-600 bg-indigo-50'
                        : 'text-gray-600 hover:text-indigo-600 hover:bg-indigo-50'
                    ]"
                  >
                    {{ subTab.name }}
                    <span
                      v-if="subTab.count !== undefined && subTab.count > 0"
                      :class="[
                        'px-2 py-0.5 rounded-full text-xs font-semibold',
                        activeInterviewSubTab === subTab.id
                          ? 'bg-indigo-600 text-white'
                          : 'bg-gray-200 text-gray-700'
                      ]"
                    >
                      {{ subTab.count }}
                    </span>
                  </button>
                </div>
              </div>

              <!-- Sub-Tab内容 -->
              <div class="flex-1 overflow-y-auto">
                <div v-if="selectedStage" class="p-6 space-y-6">
                  <!-- Recruiter Insights 提示（如果有） - 始终显示在tabs上方 -->
                  <div v-if="currentApplication?.recruiterInsights && getStageSuggestions(selectedStage)" class="bg-gradient-to-r from-purple-50 to-indigo-50 rounded-lg shadow-md border border-purple-200 p-5">
                    <h4 class="text-sm font-bold text-purple-900 mb-3 flex items-center gap-2">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                      </svg>
                      💡 根据Recruiter建议，本阶段重点准备
                    </h4>
                    <ul class="text-xs text-purple-800 list-disc ml-5 space-y-1">
                      <li v-for="(suggestion, idx) in getStageSuggestions(selectedStage)" :key="idx">{{ suggestion }}</li>
                    </ul>
                  </div>

                  <!-- Tab 1: 准备笔记 -->
                  <div v-if="activeInterviewSubTab === 'notes'">
                    <!-- 准备笔记区域 -->
                    <div class="bg-white rounded-lg shadow p-5 mb-4">
                      <div class="flex items-center justify-between mb-3">
                        <h4 class="text-sm font-semibold text-gray-900">📝 准备笔记</h4>
                        <div class="flex gap-2">
                          <button
                            v-if="!isEditingNotes"
                            @click="startEditingNotes"
                            class="px-3 py-1.5 bg-blue-500 text-white text-xs rounded-lg hover:bg-blue-600 font-medium"
                          >
                            {{ selectedStage.preparationNotes ? '编辑' : '添加' }}笔记
                          </button>
                          <template v-else>
                            <button
                              @click="cancelEditingNotes"
                              class="px-3 py-1.5 border border-gray-300 text-gray-700 text-xs rounded-lg hover:bg-gray-50 font-medium"
                            >
                              取消
                            </button>
                            <button
                              @click="savePreparationNotes"
                              class="px-3 py-1.5 bg-blue-600 text-white text-xs rounded-lg hover:bg-blue-700 font-medium"
                            >
                              保存
                            </button>
                          </template>
                        </div>
                      </div>

                      <!-- 编辑模式 -->
                      <div v-if="isEditingNotes">
                        <div class="mb-2 text-xs text-gray-500">
                          支持 Markdown 格式: **粗体**, *斜体*, 列表(- 或 *), ## 标题
                        </div>
                        <textarea
                          v-model="editingNotes"
                          rows="15"
                          class="w-full px-4 py-3 border border-gray-300 rounded-lg font-mono text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                          placeholder="输入面试准备笔记...&#10;&#10;示例：&#10;## 技术准备&#10;- 复习算法和数据结构&#10;- 准备项目案例&#10;&#10;## 行为问题&#10;- STAR 方法回答问题"
                        ></textarea>
                      </div>

                      <!-- 查看模式 -->
                      <div v-else>
                        <div v-if="selectedStage.preparationNotes" v-html="renderMarkdown(selectedStage.preparationNotes)" class="prose max-w-none text-sm"></div>
                        <div v-else class="text-center text-gray-500 py-8">
                          <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                          </svg>
                          <p class="text-sm">暂无准备笔记</p>
                          <p class="text-xs mt-1 text-gray-400">点击"添加笔记"按钮开始编辑</p>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- Tab 2: 重点领域 -->
                  <div v-if="activeInterviewSubTab === 'areas'">
                    <div class="bg-white rounded-lg shadow p-5">
                      <div class="flex items-center justify-between mb-4">
                        <h4 class="text-sm font-semibold text-gray-900">🎯 重点领域 - 按技能分组</h4>
                      </div>

                      <!-- 按技能分组显示重点领域 -->
                      <div v-if="getSkillsWithFocusAreas().length > 0" class="space-y-4">
                        <div
                          v-for="skillData in getSkillsWithFocusAreas()"
                          :key="skillData.skillId"
                          class="border border-gray-200 rounded-lg p-4"
                        >
                          <div class="flex items-center justify-between mb-3">
                            <div class="flex items-center gap-2">
                              <h5 class="font-semibold text-gray-900">{{ skillData.skillName }}</h5>
                              <span class="text-sm text-gray-500">({{ skillData.selectedFocusAreas.length }} 个领域)</span>
                            </div>
                            <button
                              @click="editSkillFocusAreas(skillData)"
                              class="px-3 py-1 bg-purple-500 text-white text-xs rounded-lg hover:bg-purple-600"
                            >
                              {{ skillData.selectedFocusAreas.length > 0 ? '编辑' : '添加' }}领域
                            </button>
                          </div>

                          <!-- 该技能的重点领域 -->
                          <div v-if="skillData.selectedFocusAreas.length > 0" class="flex flex-wrap gap-2">
                            <button
                              v-for="focusArea in skillData.selectedFocusAreas"
                              :key="focusArea.id"
                              @click="navigateToSkillLearning(focusArea)"
                              class="px-3 py-2 bg-purple-100 text-purple-700 rounded-lg text-xs font-medium hover:bg-purple-200 hover:shadow-md transition-all cursor-pointer"
                              :title="`点击查看 ${focusArea.name} 的学习资料和试题库\n\n${focusArea.description}`"
                            >
                              {{ focusArea.name }}
                              <svg class="w-3 h-3 ml-1 inline-block" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                              </svg>
                            </button>
                          </div>
                          <div v-else class="text-sm text-gray-500">
                            暂未选择重点领域
                          </div>
                        </div>
                      </div>

                      <!-- 无技能提示 -->
                      <div v-else class="text-center text-gray-500 py-8">
                        <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                        </svg>
                        <p class="text-sm">该阶段暂无关联技能</p>
                        <p class="text-xs mt-1 text-gray-400">请先在"职位管理"页面为该阶段添加技能</p>
                      </div>
                    </div>
                  </div>

                  <!-- Tab 3: 准备清单 (合并Checklist + TODO) -->
                  <div v-if="activeInterviewSubTab === 'checklist'">
                    <PreparationChecklistMerged :stage-id="selectedStage.id" />
                  </div>

                  <!-- Tab 4: 面试记录 -->
                  <div v-if="activeInterviewSubTab === 'records'">
                    <div class="bg-white rounded-lg shadow p-5">
                      <div class="flex items-center justify-between mb-4">
                        <h4 class="text-sm font-semibold text-gray-900">📝 面试记录</h4>
                        <button
                          @click="addInterviewRecord(selectedStage.id)"
                          class="px-3 py-1.5 bg-blue-500 text-white text-xs rounded-lg hover:bg-blue-600 font-medium"
                        >
                          添加记录
                        </button>
                      </div>

                      <div v-if="getRecordsByStage(selectedStage.id).length > 0" class="space-y-4">
                        <div
                          v-for="record in getRecordsByStage(selectedStage.id)"
                          :key="record.id"
                          class="bg-gray-50 rounded-lg p-4 border border-gray-200"
                        >
                          <div class="flex items-start justify-between mb-3">
                            <div>
                              <span class="text-sm font-medium text-gray-900">
                                {{ formatDate(record.interviewDate) }}
                              </span>
                              <div v-if="record.interviewerName" class="mt-1 text-xs text-gray-600">
                                面试官: {{ record.interviewerName }}
                                <span v-if="record.interviewerPosition" class="text-gray-500 ml-1">
                                  ({{ record.interviewerPosition }})
                                </span>
                              </div>
                            </div>
                            <div class="flex items-center gap-2">
                              <span v-if="record.interviewFormat" class="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs">
                                {{ getInterviewFormatText(record.interviewFormat) }}
                              </span>
                              <button
                                @click="editInterviewRecord(record)"
                                class="text-blue-600 hover:text-blue-700 text-xs font-medium"
                                title="编辑"
                              >
                                编辑
                              </button>
                              <button
                                @click="deleteInterviewRecord(record.id)"
                                class="text-red-600 hover:text-red-700 text-xs font-medium"
                                title="删除"
                              >
                                删除
                              </button>
                            </div>
                          </div>

                          <!-- 评分 -->
                          <div v-if="record.overallPerformance" class="grid grid-cols-4 gap-3 mb-3 bg-white p-3 rounded">
                            <div class="text-center">
                              <label class="block text-xs text-gray-600 mb-1">综合</label>
                              <p :class="['text-lg font-bold', getPerformanceColor(record.overallPerformance)]">
                                {{ record.overallPerformance }}
                              </p>
                            </div>
                            <div v-if="record.technicalDepth" class="text-center">
                              <label class="block text-xs text-gray-600 mb-1">技术</label>
                              <p class="text-lg font-bold text-gray-700">{{ record.technicalDepth }}</p>
                            </div>
                            <div v-if="record.communication" class="text-center">
                              <label class="block text-xs text-gray-600 mb-1">沟通</label>
                              <p class="text-lg font-bold text-gray-700">{{ record.communication }}</p>
                            </div>
                            <div v-if="record.problemSolving" class="text-center">
                              <label class="block text-xs text-gray-600 mb-1">问题</label>
                              <p class="text-lg font-bold text-gray-700">{{ record.problemSolving }}</p>
                            </div>
                          </div>

                          <!-- 总结 -->
                          <div v-if="record.selfSummary" class="border-t border-gray-200 pt-3">
                            <h5 class="text-xs font-semibold text-gray-700 mb-1">总结与反思</h5>
                            <p class="text-xs text-gray-600 whitespace-pre-wrap">{{ record.selfSummary }}</p>
                          </div>
                        </div>
                      </div>
                      <div v-else class="text-center text-gray-500 py-8">
                        <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                        <p class="text-sm">该阶段暂无面试记录</p>
                      </div>
                    </div>
                  </div>
                </div>

              <!-- 未选中阶段时的占位符 -->
              <div v-else class="flex items-center justify-center h-full">
                <div class="text-center text-gray-500">
                  <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  <p class="text-lg">请从左侧选择一个面试阶段</p>
                </div>
              </div>
            </div>
          </div>
          </div>

          <!-- Tab 3: 简历 -->
          <div v-if="activeTab === 'resume'" class="space-y-6">
            <!-- 定制简历部分 -->
            <div class="bg-white rounded-lg shadow p-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                定制简历
              </h3>

              <!-- Loading State -->
              <div v-if="loadingCustomizedResume" class="text-center py-8">
                <svg class="animate-spin h-8 w-8 mx-auto mb-4 text-indigo-600" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <p class="text-sm text-gray-500">加载定制简历...</p>
              </div>

              <!-- No Customized Resume Yet -->
              <div v-else-if="!customizedResume" class="text-center py-8">
                <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <p class="text-sm text-gray-600 mb-4">还没有为此职位创建定制简历</p>
                <button
                  @click="cloneResumeForJob"
                  class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 font-medium transition-all"
                >
                  创建定制简历
                </button>
                <p class="text-xs text-gray-400 mt-2">将克隆您的默认简历，并可根据AI分析建议进行优化</p>
              </div>

              <!-- Customized Resume Exists -->
              <div v-else>
                <div class="flex items-center justify-between mb-4 pb-4 border-b">
                  <div>
                    <p class="text-sm font-medium text-gray-900">{{ customizedResume.versionName }}</p>
                    <p class="text-xs text-gray-500 mt-1">最后更新: {{ new Date(customizedResume.updatedAt).toLocaleString('zh-CN') }}</p>
                  </div>
                  <button
                    @click="editCustomizedResume"
                    class="px-4 py-2 bg-indigo-600 text-white text-sm rounded-lg hover:bg-indigo-700 font-medium transition-all"
                  >
                    编辑简历
                  </button>
                </div>

                <div class="grid grid-cols-2 gap-4 mb-4">
                  <div>
                    <label class="block text-xs font-medium text-gray-600 mb-1">邮箱</label>
                    <p class="text-sm text-gray-900">{{ customizedResume.email || '未设置' }}</p>
                  </div>
                  <div>
                    <label class="block text-xs font-medium text-gray-600 mb-1">电话</label>
                    <p class="text-sm text-gray-900">{{ customizedResume.phone || '未设置' }}</p>
                  </div>
                </div>

                <div v-if="customizedResume.about" class="mb-4">
                  <label class="block text-xs font-medium text-gray-600 mb-1">关于我</label>
                  <p class="text-sm text-gray-700">{{ customizedResume.about }}</p>
                </div>

                <!-- 工作经历（每个条目可展开） -->
                <div v-if="customizedResume.experiences && customizedResume.experiences.length > 0" class="mb-4">
                  <h4 class="text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                    <svg class="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                    </svg>
                    工作经历 ({{ customizedResume.experiences.length }})
                  </h4>
                  <div class="space-y-2">
                    <div
                      v-for="exp in customizedResume.experiences"
                      :key="exp.id"
                      class="bg-blue-50 rounded-lg border border-blue-200 overflow-hidden"
                    >
                      <!-- 标题栏（可点击展开/折叠） -->
                      <button
                        @click="toggleExperience(exp.id)"
                        class="w-full p-3 hover:bg-blue-100 transition-colors text-left"
                      >
                        <div class="flex items-start justify-between">
                          <div class="flex-1">
                            <div class="flex items-center gap-2 mb-1">
                              <h5 class="font-semibold text-gray-900 text-sm">{{ exp.companyName }}</h5>
                              <svg
                                class="w-4 h-4 text-gray-400 transition-transform flex-shrink-0"
                                :class="{ 'rotate-180': expandedExperiences.has(exp.id) }"
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                              >
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                              </svg>
                            </div>
                            <p class="text-xs text-gray-700">{{ exp.position }}</p>
                          </div>
                          <span class="text-xs text-gray-600 whitespace-nowrap ml-2">
                            {{ formatDate(exp.startDate) }} - {{ exp.isCurrent ? '至今' : formatDate(exp.endDate) }}
                          </span>
                        </div>
                      </button>

                      <!-- 详细描述（折叠内容） -->
                      <div
                        v-show="expandedExperiences.has(exp.id)"
                        class="px-3 pb-3 border-t border-blue-200 pt-2 space-y-2"
                      >
                        <div v-if="exp.responsibilities">
                          <p class="text-xs font-semibold text-gray-700 mb-1">职责描述:</p>
                          <p class="text-xs text-gray-600 leading-relaxed whitespace-pre-wrap">{{ exp.responsibilities }}</p>
                        </div>
                        <div v-if="exp.achievements">
                          <p class="text-xs font-semibold text-gray-700 mb-1">主要成就:</p>
                          <p class="text-xs text-gray-600 leading-relaxed whitespace-pre-wrap">{{ exp.achievements }}</p>
                        </div>
                        <p v-if="!exp.responsibilities && !exp.achievements" class="text-xs text-gray-400 italic">
                          暂无详细描述
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 技能 -->
                <div v-if="customizedResume.skills && customizedResume.skills.length > 0" class="mb-4">
                  <h4 class="text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                    <svg class="w-4 h-4 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
                    </svg>
                    技能 ({{ customizedResume.skills.length }})
                  </h4>
                  <div class="flex flex-wrap gap-2">
                    <span
                      v-for="skill in customizedResume.skills"
                      :key="skill.id"
                      class="px-3 py-1 bg-purple-100 text-purple-700 rounded-full text-xs font-medium"
                    >
                      {{ skill.skillName }}
                      <span v-if="skill.proficiencyLevel" class="text-purple-500 ml-1">
                        ({{ skill.proficiencyLevel }})
                      </span>
                    </span>
                  </div>
                </div>

                <!-- 教育背景 & 培训证书（两列布局） -->
                <div class="grid grid-cols-2 gap-3 mb-4">
                  <!-- 教育背景 -->
                  <div v-if="customizedResume.education && customizedResume.education.length > 0">
                    <h4 class="text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                      <svg class="w-4 h-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                      </svg>
                      教育背景 ({{ customizedResume.education.length }})
                    </h4>
                    <div class="space-y-2">
                      <div
                        v-for="edu in customizedResume.education"
                        :key="edu.id"
                        class="p-2 bg-green-50 rounded-lg border border-green-200"
                      >
                        <h5 class="font-semibold text-gray-900 text-xs mb-1">{{ edu.schoolName }}</h5>
                        <p class="text-xs text-gray-700 mb-0.5">{{ edu.degree }} - {{ edu.major }}</p>
                        <div class="flex items-center justify-between text-xs text-gray-600">
                          <span v-if="edu.gpa">GPA: {{ edu.gpa }}</span>
                          <span class="text-xs">{{ formatDate(edu.startDate) }} - {{ formatDate(edu.endDate) }}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 培训证书 -->
                  <div v-if="customizedResume.certifications && customizedResume.certifications.length > 0">
                    <h4 class="text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                      <svg class="w-4 h-4 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
                      </svg>
                      培训证书 ({{ customizedResume.certifications.length }})
                    </h4>
                    <div class="flex flex-wrap gap-2">
                      <div
                        v-for="cert in customizedResume.certifications"
                        :key="cert.id"
                        class="inline-flex flex-col px-3 py-2 bg-yellow-100 border border-yellow-300 rounded-lg text-xs"
                      >
                        <span class="font-semibold text-yellow-900">{{ cert.certName }}</span>
                        <span v-if="cert.issuer" class="text-yellow-700 mt-0.5">{{ cert.issuer }}</span>
                        <span v-if="cert.issueDate" class="text-yellow-600 text-xs mt-0.5">{{ formatDate(cert.issueDate) }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 业余爱好（支持markdown） -->
                <div v-if="customizedResume.hobbies" class="mb-4">
                  <h4 class="text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                    <svg class="w-4 h-4 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    业余爱好
                  </h4>
                  <div
                    v-html="renderMarkdown(customizedResume.hobbies)"
                    class="prose prose-sm max-w-none p-3 bg-pink-50 rounded-lg border border-pink-200"
                  ></div>
                </div>
              </div>
            </div>

            <!-- 已保存的AI分析结果 (可折叠) -->
            <div v-if="savedAnalyses && savedAnalyses.length > 0" class="bg-white rounded-lg shadow p-6">
              <button
                @click="showAIAnalysis = !showAIAnalysis"
                class="w-full flex items-center justify-between mb-4 hover:bg-gray-50 -m-2 p-2 rounded-lg transition-colors"
              >
                <h3 class="text-lg font-semibold text-gray-900 flex items-center gap-2">
                  <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  AI分析结果
                  <span class="text-sm font-normal text-gray-500">({{ savedAnalyses.length }})</span>
                </h3>
                <svg
                  :class="['w-5 h-5 text-gray-400 transition-transform', showAIAnalysis ? 'rotate-180' : '']"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              <div v-show="showAIAnalysis" v-for="analysis in savedAnalyses" :key="analysis.id" class="mb-4 last:mb-0">
                <div class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                  <div class="flex items-center justify-between mb-3">
                    <div class="flex items-center gap-3">
                      <div class="text-3xl font-bold text-indigo-600">{{ analysis.metadata.overallScore }}</div>
                      <div>
                        <div class="font-semibold text-gray-900">{{ analysis.metadata.recommendation }}</div>
                        <div class="text-xs text-gray-500">分析于 {{ new Date(analysis.createdAt).toLocaleString('zh-CN') }}</div>
                      </div>
                    </div>
                    <button
                      @click="viewAnalysisDetails(analysis)"
                      class="px-4 py-2 bg-indigo-600 text-white text-sm rounded-lg hover:bg-indigo-700"
                    >
                      查看详情
                    </button>
                  </div>

                  <!-- 简要展示 -->
                  <div class="grid grid-cols-2 gap-3 text-sm">
                    <div>
                      <span class="text-gray-600">技能匹配:</span>
                      <span class="ml-2 font-semibold text-gray-900">{{ analysis.metadata.skillMatchScore }}</span>
                    </div>
                    <div>
                      <span class="text-gray-600">经验匹配:</span>
                      <span class="ml-2 font-semibold text-gray-900">{{ analysis.metadata.experienceMatchScore }}</span>
                    </div>
                  </div>

                  <!-- 优势预览 -->
                  <div v-if="analysis.metadata.keyStrengths && analysis.metadata.keyStrengths.length > 0" class="mt-3">
                    <div class="text-xs text-gray-600 mb-1">核心优势:</div>
                    <ul class="text-sm text-gray-700 space-y-1">
                      <li v-for="(strength, idx) in analysis.metadata.keyStrengths.slice(0, 2)" :key="idx" class="flex items-start gap-2">
                        <span class="text-green-600">✓</span>
                        <span class="flex-1">{{ strength }}</span>
                      </li>
                      <li v-if="analysis.metadata.keyStrengths.length > 2" class="text-xs text-indigo-600 ml-6">
                        还有 {{ analysis.metadata.keyStrengths.length - 2 }} 项优势...
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 未选择职位时的占位符 -->
      <div v-else class="flex-1 flex items-center justify-center">
        <div class="text-center text-gray-500">
          <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p class="text-lg">请从左侧选择一个职位</p>
        </div>
      </div>
    </div>

    <!-- 准备清单 Modal -->
    <div v-if="showChecklistModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg w-[900px] max-h-[90vh] flex flex-col">
        <div class="bg-gradient-to-r from-green-600 to-emerald-600 p-4 rounded-t-lg flex items-center justify-between">
          <h3 class="text-lg font-bold text-white">{{ currentStage?.stageName }} - 准备清单</h3>
          <div class="flex gap-2">
            <button
              v-if="selectedChecklistIds.length > 0"
              @click="showMoveChecklistModal = true"
              class="px-3 py-1 bg-orange-600 hover:bg-orange-700 text-white rounded-lg text-sm font-medium transition-all flex items-center gap-1"
              title="移动选中的清单项到其他阶段"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
              </svg>
              移动 ({{ selectedChecklistIds.length }})
            </button>
            <button
              v-if="currentApplication?.recruiterInsights"
              @click="generateSuggestedChecklist"
              class="px-3 py-1 bg-purple-600 hover:bg-purple-700 text-white rounded-lg text-sm font-medium transition-all flex items-center gap-1"
              title="根据Recruiter Insights生成建议清单"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
              AI建议
            </button>
            <button
              @click="addChecklistItem"
              class="px-3 py-1 bg-white bg-opacity-20 hover:bg-opacity-30 text-white rounded-lg text-sm font-medium transition-all"
            >
              + 添加清单项
            </button>
            <button
              @click="showChecklistModal = false"
              class="text-white hover:text-gray-200"
            >
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <!-- 重点清单 -->
          <div v-if="priorityItems.length > 0" class="mb-6">
            <div class="flex items-center justify-between mb-3">
              <h4 class="font-semibold text-gray-900">⭐ 重点准备项</h4>
              <button
                @click="printPriorityItems"
                class="px-3 py-1 bg-purple-500 text-white text-sm rounded-lg hover:bg-purple-600"
              >
                打印重点材料
              </button>
            </div>
            <div class="space-y-2">
              <div
                v-for="item in priorityItems"
                :key="item.id"
                class="p-3 bg-yellow-50 border-l-4 border-yellow-400 rounded"
              >
                <div class="flex items-start gap-3">
                  <input
                    type="checkbox"
                    :value="item.id"
                    v-model="selectedChecklistIds"
                    class="mt-1 w-4 h-4 text-blue-600 rounded focus:ring-2 focus:ring-blue-500"
                  />
                  <div class="flex-1">
                    <div class="flex items-start justify-between">
                      <div class="flex-1">
                        <span class="px-2 py-1 bg-yellow-200 text-yellow-800 rounded text-xs font-medium mr-2">
                          {{ item.category }}
                        </span>
                        <span class="text-gray-900">{{ item.checklistItem }}</span>
                      </div>
                      <div class="flex gap-2">
                        <button
                          @click="editChecklistItem(item)"
                          class="text-blue-600 hover:text-blue-700 text-sm"
                          title="编辑"
                        >
                          编辑
                        </button>
                        <button
                          @click="deleteChecklistItem(item.id)"
                          class="text-red-600 hover:text-red-700 text-sm"
                          title="删除"
                        >
                          删除
                        </button>
                      </div>
                    </div>
                    <div v-if="item.notes" class="mt-2 text-sm text-gray-600 pl-2">
                      <div v-html="renderMarkdownWithLinks(item.notes)" class="prose-sm"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 全部清单 -->
          <div>
            <h4 class="font-semibold text-gray-900 mb-3">📝 全部准备项</h4>
            <div class="space-y-2">
              <div
                v-for="item in allChecklistItems"
                :key="item.id"
                :class="[
                  'p-3 rounded border',
                  item.isPriority
                    ? 'bg-yellow-50 border-yellow-200'
                    : 'bg-gray-50 border-gray-200'
                ]"
              >
                <div class="flex items-start gap-3">
                  <input
                    type="checkbox"
                    :value="item.id"
                    v-model="selectedChecklistIds"
                    class="mt-1 w-4 h-4 text-blue-600 rounded focus:ring-2 focus:ring-blue-500"
                  />
                  <div class="flex-1">
                    <div class="flex items-start justify-between">
                      <div class="flex-1">
                        <span class="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs font-medium mr-2">
                          {{ item.category }}
                        </span>
                        <span class="text-gray-900">{{ item.checklistItem }}</span>
                      </div>
                      <div class="flex gap-2">
                        <button
                          @click="editChecklistItem(item)"
                          class="text-blue-600 hover:text-blue-700 text-sm"
                          title="编辑"
                        >
                          编辑
                        </button>
                        <button
                          @click="deleteChecklistItem(item.id)"
                          class="text-red-600 hover:text-red-700 text-sm"
                          title="删除"
                        >
                          删除
                        </button>
                      </div>
                    </div>
                    <div v-if="item.notes" class="mt-2 text-sm text-gray-600 pl-2">
                      <div v-html="renderMarkdownWithLinks(item.notes)" class="prose-sm"></div>
                    </div>
                  </div>
                </div>
              </div>

              <div v-if="allChecklistItems.length === 0" class="text-center text-gray-500 py-8">
                <p class="text-sm">暂无准备清单</p>
                <p class="text-xs mt-1">点击上方"+ 添加清单项"开始添加</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 移动清单项 Modal -->
    <div v-if="showMoveChecklistModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-[60]">
      <div class="bg-white rounded-lg p-6 w-[500px]">
        <h3 class="text-lg font-semibold mb-4">移动清单项到其他阶段</h3>
        <div class="mb-4">
          <p class="text-sm text-gray-600 mb-2">
            将选中的 <span class="font-bold text-indigo-600">{{ selectedChecklistIds.length }}</span> 项清单移动到：
          </p>
          <select
            v-model="targetStageId"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          >
            <option value="" disabled>请选择目标阶段</option>
            <option
              v-for="stage in availableStagesForMove"
              :key="stage.id"
              :value="stage.id"
            >
              阶段 {{ stage.stageOrder }}: {{ stage.stageName }}
            </option>
          </select>
        </div>
        <div class="flex gap-2 justify-end">
          <button
            @click="showMoveChecklistModal = false; targetStageId = ''"
            class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="confirmMoveChecklists"
            :disabled="!targetStageId"
            class="px-4 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            确认移动
          </button>
        </div>
      </div>
    </div>

    <!-- Recruiter Insights Modal -->
    <div v-if="showRecruiterInsightsModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[700px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">{{ currentApplication?.recruiterInsights ? '编辑' : '添加' }} Recruiter Insights</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">团队规模</label>
            <input
              v-model="recruiterInsightsFormData.teamSize"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              placeholder="例如: 10-15人"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">团队文化</label>
            <textarea
              v-model="recruiterInsightsFormData.teamCulture"
              rows="3"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              placeholder="例如: 扁平化管理，鼓励创新，工作氛围轻松"
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              技术栈偏好
              <span class="text-xs text-gray-500 ml-2">(按Enter或逗号分隔)</span>
            </label>
            <div class="flex flex-wrap gap-2 mb-2">
              <span
                v-for="(tech, index) in recruiterInsightsFormData.techStackPreference"
                :key="index"
                class="px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-sm flex items-center gap-2"
              >
                {{ tech }}
                <button
                  @click="removeTechStack(index)"
                  class="text-blue-600 hover:text-blue-800"
                >
                  ×
                </button>
              </span>
            </div>
            <input
              v-model="techStackInput"
              @keydown.enter.prevent="addTechStack"
              @keydown.comma.prevent="addTechStack"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              placeholder="输入技术栈后按Enter，例如: Java, Spring Boot, Redis"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">面试重点</label>
            <textarea
              v-model="recruiterInsightsFormData.interviewFocus"
              rows="3"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              placeholder="例如: 系统设计和编程基础，特别关注分布式系统经验"
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">流程Tips</label>
            <textarea
              v-model="recruiterInsightsFormData.processTips"
              rows="3"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              placeholder="例如: 第一轮技术面试会很深入，建议准备常见算法题。系统设计轮会问实际项目经验。"
            ></textarea>
          </div>
        </div>

        <div class="flex justify-end gap-2 mt-6">
          <button
            @click="showRecruiterInsightsModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveRecruiterInsights"
            class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
          >
            保存
          </button>
        </div>
      </div>
    </div>

    <!-- 编辑清单项 Modal -->
    <div v-if="showEditChecklistModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[600px]">
        <h3 class="text-lg font-semibold mb-4">{{ editingChecklistItem?.id ? '编辑清单项' : '添加清单项' }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">清单项内容 *</label>
            <input
              v-model="checklistFormData.checklistItem"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              placeholder="例如: 复习数组相关算法题"
              required
            />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">分类</label>
              <select v-model="checklistFormData.category" class="w-full px-4 py-2 border border-gray-300 rounded-lg">
                <option value="算法">算法</option>
                <option value="系统设计">系统设计</option>
                <option value="行为面试">行为面试</option>
                <option value="项目经验">项目经验</option>
                <option value="技术栈">技术栈</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">排序</label>
              <input
                v-model.number="checklistFormData.sortOrder"
                type="number"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                min="0"
              />
            </div>
          </div>
          <div>
            <label class="flex items-center gap-2">
              <input
                v-model="checklistFormData.isPriority"
                type="checkbox"
                class="rounded border-gray-300"
              />
              <span class="text-sm font-medium text-gray-700">标记为重点</span>
            </label>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              备注/链接 (支持 Markdown)
              <span class="text-xs text-gray-500 ml-2">
                链接格式: [标题](URL)
              </span>
            </label>
            <textarea
              v-model="checklistFormData.notes"
              rows="4"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm"
              placeholder="例如:&#10;- LeetCode链接: [Two Sum](https://leetcode.com/problems/two-sum/)&#10;- 文档: [Hash Table详解](https://example.com)"
            ></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button
            @click="showEditChecklistModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveChecklistItem"
            class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
          >
            保存
          </button>
        </div>
      </div>
    </div>

    <!-- 面试记录编辑 Modal -->
    <div v-if="showInterviewRecordModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[700px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">{{ editingInterviewRecord?.id ? '编辑面试记录' : '添加面试记录' }}</h3>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">面试日期 *</label>
              <input
                v-model="interviewRecordFormData.interviewDate"
                type="date"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">面试形式</label>
              <select v-model="interviewRecordFormData.interviewFormat" class="w-full px-4 py-2 border border-gray-300 rounded-lg">
                <option value="VideoCall">视频面试</option>
                <option value="InPerson">现场面试</option>
                <option value="Phone">电话面试</option>
              </select>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">面试官姓名</label>
              <input
                v-model="interviewRecordFormData.interviewerName"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                placeholder="例如: 张三"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">面试官职位</label>
              <input
                v-model="interviewRecordFormData.interviewerPosition"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                placeholder="例如: Senior Engineer"
              />
            </div>
          </div>

          <div class="border-t pt-4">
            <h4 class="text-sm font-semibold text-gray-900 mb-3">评分 (1-10分)</h4>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">综合表现</label>
                <input
                  v-model.number="interviewRecordFormData.overallPerformance"
                  type="number"
                  min="1"
                  max="10"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                  placeholder="1-10"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">技术深度</label>
                <input
                  v-model.number="interviewRecordFormData.technicalDepth"
                  type="number"
                  min="1"
                  max="10"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                  placeholder="1-10"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">沟通表达</label>
                <input
                  v-model.number="interviewRecordFormData.communication"
                  type="number"
                  min="1"
                  max="10"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                  placeholder="1-10"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">问题解决</label>
                <input
                  v-model.number="interviewRecordFormData.problemSolving"
                  type="number"
                  min="1"
                  max="10"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                  placeholder="1-10"
                />
              </div>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">总结与反思</label>
            <textarea
              v-model="interviewRecordFormData.selfSummary"
              rows="6"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              placeholder="记录面试过程、遇到的问题、回答情况、需要改进的地方等..."
            ></textarea>
          </div>
        </div>

        <div class="flex justify-end gap-2 mt-6">
          <button
            @click="showInterviewRecordModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveInterviewRecord"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            保存
          </button>
        </div>
      </div>
    </div>

    <!-- 编辑重点领域 Modal -->
    <div v-if="showEditAreasModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[700px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">编辑重点领域 - {{ editingSkillData?.skillName }}</h3>
        <p class="text-sm text-gray-600 mb-4">
          选择该技能需要重点准备的Focus Area（可多选）
        </p>

        <!-- 单个技能的Focus Area选择 -->
        <div v-if="editingSkillData?.allFocusAreas && editingSkillData.allFocusAreas.length > 0" class="space-y-2 mb-4">
          <label
            v-for="fa in editingSkillData.allFocusAreas"
            :key="fa.id"
            class="flex items-start p-3 hover:bg-purple-50 rounded-lg cursor-pointer border border-gray-200"
          >
            <input
              type="checkbox"
              :value="fa.id"
              v-model="selectedFocusAreaIds"
              class="rounded border-gray-300 text-purple-600 focus:ring-purple-500 mt-1 mr-3"
            />
            <div class="flex-1">
              <div class="font-medium text-sm text-gray-900">{{ fa.name }}</div>
              <div class="text-xs text-gray-500 mt-1">{{ fa.description }}</div>
            </div>
          </label>
        </div>

        <div v-else class="text-center text-gray-500 py-8 border border-gray-200 rounded-lg">
          <p class="text-sm">该技能暂无Focus Area</p>
        </div>

        <div class="flex justify-end gap-2 border-t pt-4 mt-4">
          <button
            @click="showEditAreasModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveSkillFocusAreas"
            class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
          >
            保存 (已选 {{ selectedFocusAreaIds.length }} 个)
          </button>
        </div>
      </div>
    </div>

    <!-- AI分析详情 Modal -->
    <div v-if="showAnalysisDetailsModal && selectedAnalysis" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg w-[900px] max-h-[90vh] flex flex-col">
        <!-- Modal Header -->
        <div class="bg-gradient-to-r from-indigo-600 to-purple-600 p-6 rounded-t-lg flex items-center justify-between">
          <div>
            <h3 class="text-xl font-bold text-white">AI分析详情</h3>
            <p class="text-sm text-purple-100 mt-1">分析于 {{ new Date(selectedAnalysis.createdAt).toLocaleString('zh-CN') }}</p>
          </div>
          <button
            @click="showAnalysisDetailsModal = false"
            class="text-white hover:text-gray-200 transition-colors"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Modal Body -->
        <div class="flex-1 overflow-y-auto p-6 space-y-6">
          <!-- 总体评分卡片 -->
          <div class="bg-gradient-to-br from-indigo-50 to-purple-50 rounded-lg p-6 border border-indigo-200">
            <div class="flex items-center justify-between">
              <div>
                <h4 class="text-sm font-medium text-gray-600 mb-2">总体匹配度</h4>
                <div class="flex items-baseline gap-2">
                  <span class="text-5xl font-bold text-indigo-600">{{ selectedAnalysis.metadata.overallScore }}</span>
                  <span class="text-2xl text-gray-500">/100</span>
                </div>
              </div>
              <div class="text-right">
                <h4 class="text-sm font-medium text-gray-600 mb-2">推荐等级</h4>
                <span :class="[
                  'px-4 py-2 rounded-full text-lg font-semibold',
                  selectedAnalysis.metadata.recommendation === '强烈推荐' ? 'bg-green-100 text-green-700' :
                  selectedAnalysis.metadata.recommendation === '推荐' ? 'bg-blue-100 text-blue-700' :
                  selectedAnalysis.metadata.recommendation === '一般' ? 'bg-yellow-100 text-yellow-700' :
                  'bg-gray-100 text-gray-700'
                ]">
                  {{ selectedAnalysis.metadata.recommendation }}
                </span>
              </div>
            </div>
          </div>

          <!-- 细分评分 -->
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-white border border-gray-200 rounded-lg p-5">
              <div class="flex items-center gap-3 mb-3">
                <div class="p-2 bg-blue-100 rounded-lg">
                  <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
                  </svg>
                </div>
                <h4 class="font-semibold text-gray-900">技能匹配</h4>
              </div>
              <div class="flex items-baseline gap-2">
                <span class="text-3xl font-bold text-blue-600">{{ selectedAnalysis.metadata.skillMatchScore }}</span>
                <span class="text-lg text-gray-500">/100</span>
              </div>
            </div>

            <div class="bg-white border border-gray-200 rounded-lg p-5">
              <div class="flex items-center gap-3 mb-3">
                <div class="p-2 bg-purple-100 rounded-lg">
                  <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                  </svg>
                </div>
                <h4 class="font-semibold text-gray-900">经验匹配</h4>
              </div>
              <div class="flex items-baseline gap-2">
                <span class="text-3xl font-bold text-purple-600">{{ selectedAnalysis.metadata.experienceMatchScore }}</span>
                <span class="text-lg text-gray-500">/100</span>
              </div>
            </div>
          </div>

          <!-- 核心优势 -->
          <div class="bg-white border border-gray-200 rounded-lg p-5">
            <div class="flex items-center gap-2 mb-4">
              <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <h4 class="font-semibold text-gray-900">核心优势</h4>
            </div>
            <ul v-if="selectedAnalysis.metadata.keyStrengths && selectedAnalysis.metadata.keyStrengths.length > 0" class="space-y-2">
              <li
                v-for="(strength, idx) in selectedAnalysis.metadata.keyStrengths"
                :key="idx"
                class="flex items-start gap-3 p-3 bg-green-50 rounded-lg"
              >
                <span class="flex-shrink-0 w-6 h-6 bg-green-500 text-white rounded-full flex items-center justify-center text-sm font-semibold">
                  {{ idx + 1 }}
                </span>
                <span class="text-gray-700 flex-1">{{ strength }}</span>
              </li>
            </ul>
            <p v-else class="text-gray-500 text-sm italic">暂无核心优势信息</p>
          </div>

          <!-- 改进建议 -->
          <div class="bg-white border border-gray-200 rounded-lg p-5">
            <div class="flex items-center gap-2 mb-4">
              <svg class="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <h4 class="font-semibold text-gray-900">改进建议</h4>
            </div>
            <ul v-if="selectedAnalysis.metadata.keyWeaknesses && selectedAnalysis.metadata.keyWeaknesses.length > 0" class="space-y-2">
              <li
                v-for="(weakness, idx) in selectedAnalysis.metadata.keyWeaknesses"
                :key="idx"
                class="flex items-start gap-3 p-3 bg-orange-50 rounded-lg"
              >
                <span class="flex-shrink-0 w-6 h-6 bg-orange-500 text-white rounded-full flex items-center justify-center text-sm font-semibold">
                  {{ idx + 1 }}
                </span>
                <span class="text-gray-700 flex-1">{{ weakness }}</span>
              </li>
            </ul>
            <p v-else class="text-gray-500 text-sm italic">暂无改进建议</p>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="border-t border-gray-200 p-4 flex justify-end">
          <button
            @click="showAnalysisDetailsModal = false"
            class="px-6 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors"
          >
            关闭
          </button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑面试阶段 Modal -->
    <div v-if="showAddStageModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[700px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">{{ editingStage?.id ? '编辑面试阶段' : '添加面试阶段' }}</h3>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">阶段名称 *</label>
              <input v-model="stageFormData.stageName" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" placeholder="如: Phone Screen" required />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">阶段顺序</label>
              <input v-model.number="stageFormData.stageOrder" type="number" class="w-full px-4 py-2 border border-gray-300 rounded-lg" min="1" />
            </div>
          </div>

          <!-- Skill Selection -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">关联技能</label>
            <div class="border border-gray-300 rounded-lg p-3 max-h-48 overflow-y-auto">
              <div class="grid grid-cols-2 gap-x-4 gap-y-2">
                <div v-for="skill in availableSkills" :key="skill.id" class="flex items-center gap-2">
                  <input
                    type="checkbox"
                    :id="`skill-${skill.id}`"
                    :value="skill.id"
                    v-model="stageFormData.skillIds"
                    class="rounded border-gray-300"
                  />
                  <label :for="`skill-${skill.id}`" class="text-sm text-gray-700 cursor-pointer">
                    {{ skill.name }}
                  </label>
                </div>
              </div>
              <div v-if="availableSkills.length === 0" class="text-sm text-gray-500 text-center py-2">
                加载中...
              </div>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">准备要点 (支持 Markdown)</label>
            <textarea v-model="stageFormData.preparationNotes" rows="6" class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm" placeholder="## 准备重点&#10;- 算法题：Array, Hash Table&#10;- 系统设计：Database Schema"></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button @click="cancelAddStage" class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">取消</button>
          <button @click="saveStage" class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { jobApplicationApi } from '@/api/jobApplicationApi'
import { interviewStageApi } from '@/api/interviewStageApi'
import { interviewRecordApi } from '@/api/interviewRecordApi'
import interviewPreparationChecklistApi from '@/api/interviewPreparationChecklistApi'
import { resumeAnalysisApi } from '@/api/resumeAnalysisApi'
import { aiJobAnalysisApi } from '@/api/aiJobAnalysisApi'
import { resumeApi } from '@/api/resumeApi'
import { getSkills } from '@/api/skillApi'
import MarkdownIt from 'markdown-it'
import PreparationChecklistMerged from '@/components/job-search/PreparationChecklistMerged.vue'

const router = useRouter()
const route = useRoute()
const md = new MarkdownIt()

const applications = ref([])
const interviewStages = ref([])
const interviewRecords = ref([])
const selectedApplicationId = ref(null)
const activeTab = ref('overview')
const filterStatus = ref('')
const filterJobStatus = ref('!Closed')
const selectedStageId = ref(null) // 选中的面试阶段ID
const showApplicationList = ref(true) // 控制职位申请列表显示/隐藏
const activeInterviewSubTab = ref('notes') // 面试详情子Tab: notes, areas, checklist, records

const showChecklistModal = ref(false)
const showEditChecklistModal = ref(false)
const showRecruiterInsightsModal = ref(false)
const showInterviewRecordModal = ref(false)
const showAnalysisDetailsModal = ref(false)
const showEditAreasModal = ref(false)
const showMoveChecklistModal = ref(false)
const currentStageId = ref(null)
const isEditingNotes = ref(false)
const editingNotes = ref('')
const selectedFocusAreaIds = ref([])
const availableSkills = ref([])
const allChecklistItems = ref([])
const priorityItems = ref([])
const editingChecklistItem = ref(null)
const editingInterviewRecord = ref(null)
const selectedChecklistIds = ref([])
const targetStageId = ref('')
const checklistFormData = ref({
  checklistItem: '',
  category: '算法',
  isPriority: false,
  notes: '',
  sortOrder: 0
})

const interviewRecordFormData = ref({
  interviewDate: '',
  interviewFormat: 'VideoCall',
  interviewerName: '',
  interviewerPosition: '',
  overallPerformance: null,
  technicalDepth: null,
  communication: null,
  problemSolving: null,
  selfSummary: ''
})

const recruiterInsightsFormData = ref({
  teamSize: '',
  teamCulture: '',
  techStackPreference: [],
  interviewFocus: '',
  processTips: ''
})

const techStackInput = ref('')
const resumeAnalysis = ref(null)
const aiPromptData = ref(null)
const savedAnalyses = ref([])
const selectedAnalysis = ref(null)
const customizedResume = ref(null)
const loadingCustomizedResume = ref(false)
const showAIAnalysis = ref(false) // 控制AI分析结果的显示/隐藏
const expandedExperiences = ref(new Set()) // 存储展开的工作经历ID集合
const showAddStageModal = ref(false) // 控制添加/编辑面试阶段模态框
const editingStage = ref(null) // 当前编辑的面试阶段
const editingSkillData = ref(null) // 当前编辑重点领域的技能数据
const stageFormData = ref({
  stageName: '',
  stageOrder: 1,
  skillIds: [],
  focusAreaIds: [],
  preparationNotes: ''
})
const isEditingStatus = ref(false) // 控制申请状态编辑模式
const editingStatusValue = ref('') // 编辑中的申请状态值

const tabs = [
  { id: 'overview', name: '概览' },
  { id: 'interviews', name: '面试' },
  { id: 'resume', name: '简历' }
]

const interviewSubTabs = computed(() => [
  { id: 'notes', name: '准备笔记' },
  {
    id: 'areas',
    name: '重点领域',
    count: selectedStage.value?.focusAreas?.length || 0
  },
  {
    id: 'checklist',
    name: '准备清单',
    count: selectedStage.value?.checklistCount || 0
  },
  { id: 'records', name: '面试记录' }
])

const currentApplication = computed(() =>
  applications.value.find(app => app.id === selectedApplicationId.value)
)

const currentStage = computed(() =>
  interviewStages.value.find(s => s.id === currentStageId.value)
)

// 当前选中的面试阶段（用于右侧显示）
const selectedStage = computed(() =>
  interviewStages.value.find(s => s.id === selectedStageId.value)
)

const filteredApplications = computed(() => {
  let filtered = applications.value

  // 按申请状态筛选
  if (filterStatus.value) {
    filtered = filtered.filter(app => app.applicationStatus === filterStatus.value)
  }

  // 按职位招聘状态筛选
  if (filterJobStatus.value === '!Closed') {
    filtered = filtered.filter(app => app.jobStatus !== 'Closed')
  } else if (filterJobStatus.value) {
    filtered = filtered.filter(app => app.jobStatus === filterJobStatus.value)
  }

  return filtered
})

// 可移动到的阶段列表（排除当前阶段）
const availableStagesForMove = computed(() => {
  return interviewStages.value.filter(stage => stage.id !== currentStageId.value)
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

const renderMarkdown = (text) => {
  if (!text) return ''
  if (typeof text !== 'string') {
    text = String(text)
  }
  return md.render(text)
}

const renderMarkdownWithLinks = (text) => {
  if (!text) return ''
  if (typeof text !== 'string') {
    text = String(text)
  }
  // Render Markdown and ensure links open in new tab
  const html = md.render(text)
  return html.replace(/<a /g, '<a target="_blank" rel="noopener noreferrer" ')
}

const getStatusColor = (status) => {
  const colors = {
    'NotApplied': 'bg-gray-100 text-gray-700',
    'Applied': 'bg-blue-100 text-blue-700',
    'Screening': 'bg-yellow-100 text-yellow-700',
    'Interviewing': 'bg-purple-100 text-purple-700',
    'Offer': 'bg-green-100 text-green-700',
    'Rejected': 'bg-red-100 text-red-700',
    'Withdrawn': 'bg-gray-100 text-gray-700'
  }
  return colors[status] || 'bg-gray-100 text-gray-700'
}

const getStatusText = (status) => {
  const texts = {
    'NotApplied': '未申请',
    'Applied': '已投递',
    'Screening': '筛选中',
    'Interviewing': '面试中',
    'Offer': 'Offer',
    'Rejected': '已拒绝',
    'Withdrawn': '已撤回'
  }
  return texts[status] || status
}

const getJobStatusColor = (status) => {
  const colors = {
    'Open': 'bg-blue-100 text-blue-700',
    'ActivelyHiring': 'bg-green-100 text-green-700',
    'Closed': 'bg-gray-100 text-gray-700'
  }
  return colors[status] || 'bg-blue-100 text-blue-700'
}

const getJobStatusLabel = (status) => {
  const labels = {
    'Open': '开放招聘',
    'ActivelyHiring': '积极招聘',
    'Closed': '已关闭'
  }
  return labels[status] || '开放招聘'
}

const getInterviewFormatText = (format) => {
  const texts = {
    'VideoCall': '视频面试',
    'InPerson': '现场面试',
    'Phone': '电话面试'
  }
  return texts[format] || format
}

const getPerformanceColor = (score) => {
  if (score >= 8) return 'text-green-600'
  if (score >= 6) return 'text-yellow-600'
  return 'text-red-600'
}

const selectApplication = async (appId) => {
  selectedApplicationId.value = appId
  activeTab.value = 'overview'

  // Load saved AI analyses and customized resume for this job application
  await loadSavedAnalyses(appId)
  await loadCustomizedResume(appId)
}

const goToCompanyPage = () => {
  if (!currentApplication.value) return
  router.push({
    name: 'CompanyJobManagement',
    query: {
      companyId: currentApplication.value.companyId,
      jobId: currentApplication.value.id
    }
  })
}

// 切换工作经历的展开/折叠状态
const toggleExperience = (expId) => {
  if (expandedExperiences.value.has(expId)) {
    expandedExperiences.value.delete(expId)
  } else {
    expandedExperiences.value.add(expId)
  }
  // 触发响应式更新
  expandedExperiences.value = new Set(expandedExperiences.value)
}

const showPreparationChecklist = async (stageId) => {
  currentStageId.value = stageId
  selectedChecklistIds.value = []  // 重置选中的清单项
  showChecklistModal.value = true
  await loadChecklistItems(stageId)
}

const loadChecklistItems = async (stageId) => {
  try {
    const [all, priority] = await Promise.all([
      interviewPreparationChecklistApi.getChecklistByStageId(stageId),
      interviewPreparationChecklistApi.getPriorityChecklistByStageId(stageId)
    ])
    allChecklistItems.value = all || []
    priorityItems.value = priority || []
  } catch (error) {
    console.error('加载准备清单失败:', error)
  }
}

const addChecklistItem = () => {
  editingChecklistItem.value = null
  checklistFormData.value = {
    checklistItem: '',
    category: '算法',
    isPriority: false,
    notes: '',
    sortOrder: allChecklistItems.value.length
  }
  showEditChecklistModal.value = true
}

const editChecklistItem = (item) => {
  editingChecklistItem.value = item
  checklistFormData.value = {
    checklistItem: item.checklistItem || '',
    category: item.category || '算法',
    isPriority: item.isPriority || false,
    notes: item.notes || '',
    sortOrder: item.sortOrder || 0
  }
  showEditChecklistModal.value = true
}

const saveChecklistItem = async () => {
  if (!checklistFormData.value.checklistItem.trim()) {
    alert('请输入清单项内容')
    return
  }

  try {
    const payload = {
      ...checklistFormData.value,
      interviewStageId: currentStageId.value
    }

    if (editingChecklistItem.value?.id) {
      await interviewPreparationChecklistApi.updateChecklist(editingChecklistItem.value.id, payload)
    } else {
      await interviewPreparationChecklistApi.createChecklist(payload)
    }

    showEditChecklistModal.value = false
    editingChecklistItem.value = null
    await loadChecklistItems(currentStageId.value)
  } catch (error) {
    console.error('保存清单项失败:', error)
    alert('保存失败')
  }
}

const deleteChecklistItem = async (id) => {
  if (!confirm('确定要删除这个清单项吗?')) return

  try {
    await interviewPreparationChecklistApi.deleteChecklist(id)
    await loadChecklistItems(currentStageId.value)
  } catch (error) {
    console.error('删除清单项失败:', error)
    alert('删除失败')
  }
}

const confirmMoveChecklists = async () => {
  if (!targetStageId.value || selectedChecklistIds.value.length === 0) {
    alert('请选择目标阶段')
    return
  }

  try {
    // From interviewPreparationChecklistApi.js - batchMoveChecklists(checklistIds, targetStageId)
    await interviewPreparationChecklistApi.batchMoveChecklists(
      selectedChecklistIds.value,
      targetStageId.value
    )

    // 关闭模态框
    showMoveChecklistModal.value = false
    targetStageId.value = ''
    selectedChecklistIds.value = []

    // 重新加载当前阶段的清单
    await loadChecklistItems(currentStageId.value)

    // 刷新面试阶段数据（更新清单数量）
    await loadInterviewStages(selectedApplicationId.value)

    alert('清单项移动成功')
  } catch (error) {
    console.error('移动清单项失败:', error)
    alert('移动失败')
  }
}

const openRecruiterInsightsModal = () => {
  if (currentApplication.value?.recruiterInsights) {
    // 编辑模式 - 加载现有数据
    recruiterInsightsFormData.value = {
      teamSize: currentApplication.value.recruiterInsights.teamSize || '',
      teamCulture: currentApplication.value.recruiterInsights.teamCulture || '',
      techStackPreference: currentApplication.value.recruiterInsights.techStackPreference || [],
      interviewFocus: currentApplication.value.recruiterInsights.interviewFocus || '',
      processTips: currentApplication.value.recruiterInsights.processTips || ''
    }
  } else {
    // 新建模式
    recruiterInsightsFormData.value = {
      teamSize: '',
      teamCulture: '',
      techStackPreference: [],
      interviewFocus: '',
      processTips: ''
    }
  }
  techStackInput.value = ''
  showRecruiterInsightsModal.value = true
}

const addTechStack = () => {
  const tech = techStackInput.value.trim().replace(/,$/, '') // 移除末尾逗号
  if (tech && !recruiterInsightsFormData.value.techStackPreference.includes(tech)) {
    recruiterInsightsFormData.value.techStackPreference.push(tech)
    techStackInput.value = ''
  }
}

const removeTechStack = (index) => {
  recruiterInsightsFormData.value.techStackPreference.splice(index, 1)
}

const saveRecruiterInsights = async () => {
  try {
    // 合并现有数据，只更新recruiterInsights字段
    const payload = {
      ...currentApplication.value,  // 保留所有现有字段
      recruiterInsights: recruiterInsightsFormData.value  // 只更新recruiterInsights
    }

    await jobApplicationApi.updateJobApplication(selectedApplicationId.value, payload)

    showRecruiterInsightsModal.value = false
    await loadApplications()

    // 重新选中当前申请以刷新数据
    const currentId = selectedApplicationId.value
    selectedApplicationId.value = null
    setTimeout(() => {
      selectedApplicationId.value = currentId
    }, 100)
  } catch (error) {
    console.error('保存Recruiter Insights失败:', error)
    alert('保存失败')
  }
}

const printPriorityItems = () => {
  if (!currentStage.value || priorityItems.value.length === 0) {
    alert('无重点材料可打印')
    return
  }

  const printWindow = window.open('', '_blank')
  printWindow.document.write(`
    <html>
    <head>
      <title>${currentStage.value.stageName} - 重点准备材料</title>
      <style>
        body { font-family: Arial, sans-serif; padding: 20px; max-width: 800px; margin: 0 auto; }
        h1 { border-bottom: 2px solid #333; padding-bottom: 10px; }
        .item { margin: 15px 0; padding: 10px; border-left: 3px solid #007bff; }
        .category { color: #666; font-size: 0.9em; font-weight: bold; }
        .content { margin-top: 5px; }
        .notes { margin-top: 5px; color: #666; font-size: 0.9em; }
        .notes a { color: #007bff; text-decoration: underline; }
      </style>
    </head>
    <body>
      <h1>${currentStage.value.stageName} - 重点准备材料</h1>
      ${priorityItems.value.map(item => `
        <div class="item">
          <div class="category">[${item.category}]</div>
          <div class="content">${item.checklistItem}</div>
          ${item.notes ? `<div class="notes">${renderMarkdownWithLinks(item.notes)}</div>` : ''}
        </div>
      `).join('')}
    </body>
    </html>
  `)
  printWindow.document.close()
  printWindow.print()
}

onMounted(async () => {
  await loadApplications()
})

watch(selectedApplicationId, async (newVal) => {
  if (newVal) {
    await Promise.all([
      loadInterviewStages(newVal),
      loadInterviewRecords(newVal)
    ])
    // Auto-select first stage when switching applications
    if (interviewStages.value.length > 0) {
      selectedStageId.value = interviewStages.value[0].id
    }
  } else {
    interviewStages.value = []
    interviewRecords.value = []
    selectedStageId.value = null
  }
})

// Auto-select first stage when entering interviews tab
watch(activeTab, (newTab) => {
  if (newTab === 'interviews' && interviewStages.value.length > 0 && !selectedStageId.value) {
    selectedStageId.value = interviewStages.value[0].id
  }
})

// 当进入重点领域Tab时，加载所有技能数据
watch(activeInterviewSubTab, async (newSubTab) => {
  if (newSubTab === 'areas' && availableSkills.value.length === 0) {
    try {
      const skills = await getSkills()
      availableSkills.value = skills || []
    } catch (error) {
      console.error('加载技能列表失败:', error)
    }
  }
})

// 监听编辑面试阶段时skillIds的变化，自动清理不属于选中技能的focusAreaIds
watch(() => stageFormData.value.skillIds, (newSkillIds, oldSkillIds) => {
  // 只在skillIds减少时才清理focusAreaIds（即用户取消勾选某个技能）
  if (!oldSkillIds || newSkillIds.length >= oldSkillIds.length) {
    return
  }

  // 找出被移除的技能IDs
  const removedSkillIds = oldSkillIds.filter(id => !newSkillIds.includes(id))
  if (removedSkillIds.length === 0) {
    return
  }

  // 收集被移除技能的所有Focus Area IDs
  const removedFocusAreaIds = new Set()
  removedSkillIds.forEach(skillId => {
    const skill = availableSkills.value.find(s => s.id === skillId)
    if (skill && skill.focusAreas) {
      skill.focusAreas.forEach(fa => removedFocusAreaIds.add(fa.id))
    }
  })

  // 从focusAreaIds中移除这些Focus Area
  if (removedFocusAreaIds.size > 0) {
    stageFormData.value.focusAreaIds = stageFormData.value.focusAreaIds.filter(
      faId => !removedFocusAreaIds.has(faId)
    )
  }
}, { deep: true })

const loadApplications = async () => {
  try {
    const data = await jobApplicationApi.getAllJobApplications()
    applications.value = data || []
    if (applications.value.length > 0 && !selectedApplicationId.value) {
      // Auto-select logic: URL query > Default > First item (防 Phase 7 Mistake #14)
      const urlId = route.query.id ? Number(route.query.id) : null
      const target = urlId && applications.value.find(a => a.id === urlId)
        ? urlId
        : applications.value[0].id
      await selectApplication(target)
    }
  } catch (error) {
    console.error('加载职位申请失败:', error)
  }
}

// Re-select when navigated with a new ?id= (e.g., from interview-progress card click)
watch(() => route.query.id, async (newId) => {
  if (!newId) return
  const wantId = Number(newId)
  if (selectedApplicationId.value !== wantId
      && applications.value.some(a => a.id === wantId)) {
    await selectApplication(wantId)
  }
})

const loadInterviewStages = async (jobId) => {
  try {
    const data = await interviewStageApi.getByJobApplication(jobId)
    interviewStages.value = data || []
  } catch (error) {
    console.error('加载面试阶段失败:', error)
  }
}

const loadInterviewRecords = async (jobId) => {
  try {
    const data = await interviewRecordApi.getRecordsByJob(jobId)
    interviewRecords.value = data || []
  } catch (error) {
    console.error('加载面试记录失败:', error)
  }
}

// 根据阶段ID获取该阶段的面试记录
const getRecordsByStage = (stageId) => {
  return interviewRecords.value.filter(record => record.interviewStageId === stageId)
}

// 面试记录管理
const addInterviewRecord = (stageId) => {
  currentStageId.value = stageId
  editingInterviewRecord.value = null
  interviewRecordFormData.value = {
    interviewDate: new Date().toISOString().split('T')[0],
    interviewFormat: 'VideoCall',
    interviewerName: '',
    interviewerPosition: '',
    overallPerformance: null,
    technicalDepth: null,
    communication: null,
    problemSolving: null,
    selfSummary: ''
  }
  showInterviewRecordModal.value = true
}

const editInterviewRecord = (record) => {
  editingInterviewRecord.value = record
  currentStageId.value = record.interviewStageId
  interviewRecordFormData.value = {
    interviewDate: record.interviewDate?.split('T')[0] || '',
    interviewFormat: record.interviewFormat || 'VideoCall',
    interviewerName: record.interviewerName || '',
    interviewerPosition: record.interviewerPosition || '',
    overallPerformance: record.overallPerformance,
    technicalDepth: record.technicalDepth,
    communication: record.communication,
    problemSolving: record.problemSolving,
    selfSummary: record.selfSummary || ''
  }
  showInterviewRecordModal.value = true
}

const saveInterviewRecord = async () => {
  if (!interviewRecordFormData.value.interviewDate) {
    alert('请选择面试日期')
    return
  }

  try {
    // Convert date string (YYYY-MM-DD) to LocalDateTime format (YYYY-MM-DDTHH:MM:SS)
    const dateStr = interviewRecordFormData.value.interviewDate
    const dateTime = dateStr.includes('T') ? dateStr : `${dateStr}T12:00:00`

    const payload = {
      ...interviewRecordFormData.value,
      interviewDate: dateTime,
      interviewStageId: currentStageId.value,
      jobApplicationId: selectedApplicationId.value
    }

    if (editingInterviewRecord.value?.id) {
      await interviewRecordApi.updateRecord(editingInterviewRecord.value.id, payload)
    } else {
      await interviewRecordApi.createRecord(payload)
    }

    showInterviewRecordModal.value = false
    editingInterviewRecord.value = null
    await loadInterviewRecords(selectedApplicationId.value)
  } catch (error) {
    console.error('保存面试记录失败:', error)
    alert('保存失败')
  }
}

const deleteInterviewRecord = async (id) => {
  if (!confirm('确定要删除这条面试记录吗?')) return

  try {
    await interviewRecordApi.deleteRecord(id)
    await loadInterviewRecords(selectedApplicationId.value)
  } catch (error) {
    console.error('删除面试记录失败:', error)
    alert('删除失败')
  }
}

// 加载简历分析
const loadResumeAnalysis = async () => {
  if (!selectedApplicationId.value) return

  try {
    const data = await resumeAnalysisApi.analyzeResumeByApplication(selectedApplicationId.value)
    resumeAnalysis.value = data
  } catch (error) {
    console.error('加载简历分析失败:', error)
    alert('分析失败，请稍后重试')
  }
}

// 获取匹配度颜色
const getMatchScoreColor = (score) => {
  if (score >= 80) return 'text-green-600'
  if (score >= 60) return 'text-yellow-600'
  return 'text-red-600'
}

// 根据面试阶段和Recruiter Insights生成智能准备建议
const getStageSuggestions = (stage) => {
  if (!currentApplication.value?.recruiterInsights) {
    return null
  }

  const insights = currentApplication.value.recruiterInsights
  const stageName = stage.stageName.toLowerCase()
  const suggestions = []

  // 根据阶段类型提供建议
  if (stageName.includes('算法') || stageName.includes('coding') || stageName.includes('编程')) {
    // 算法/编程轮
    if (insights.techStackPreference?.length > 0) {
      suggestions.push(`重点复习 ${insights.techStackPreference.slice(0, 3).join('、')} 相关算法实现`)
    }
    if (insights.interviewFocus && insights.interviewFocus.includes('算法')) {
      suggestions.push('根据Recruiter反馈，算法难度较高，建议刷中等及以上难度题目')
    }
    if (insights.processTips) {
      suggestions.push(`流程建议：${insights.processTips.substring(0, 50)}...`)
    }
  } else if (stageName.includes('系统设计') || stageName.includes('system design')) {
    // 系统设计轮
    if (insights.techStackPreference?.length > 0) {
      suggestions.push(`准备使用 ${insights.techStackPreference.slice(0, 3).join('、')} 的系统设计案例`)
    }
    if (insights.interviewFocus && (insights.interviewFocus.includes('系统') || insights.interviewFocus.includes('分布式'))) {
      suggestions.push('重点准备分布式系统、高可用架构相关设计')
    }
    if (insights.teamSize) {
      suggestions.push(`团队规模${insights.teamSize}，准备适合该规模团队的技术方案`)
    }
  } else if (stageName.includes('行为') || stageName.includes('behavioral') || stageName.includes('hr')) {
    // 行为面试轮
    if (insights.teamCulture) {
      suggestions.push(`强调符合团队文化的经历：${insights.teamCulture.substring(0, 30)}...`)
    }
    if (insights.teamSize) {
      suggestions.push(`准备在${insights.teamSize}规模团队中的合作经验`)
    }
  } else if (stageName.includes('技术') || stageName.includes('technical')) {
    // 通用技术面试
    if (insights.techStackPreference?.length > 0) {
      suggestions.push(`深入准备 ${insights.techStackPreference.join('、')} 相关技术细节`)
    }
    if (insights.interviewFocus) {
      suggestions.push(`面试重点：${insights.interviewFocus}`)
    }
  }

  // 如果有流程Tips，对所有阶段都添加
  if (insights.processTips && suggestions.length < 3) {
    const tips = insights.processTips.split('。')[0]
    if (tips && !suggestions.some(s => s.includes(tips.substring(0, 10)))) {
      suggestions.push(`Recruiter建议：${tips}`)
    }
  }

  return suggestions.length > 0 ? suggestions : null
}

// 生成AI分析Prompt
const generateAIPrompt = async () => {
  if (!selectedApplicationId.value) {
    alert('请先选择一个职位申请')
    return
  }

  try {
    const data = await aiJobAnalysisApi.generatePrompt(selectedApplicationId.value)
    aiPromptData.value = data
  } catch (error) {
    console.error('生成AI分析Prompt失败:', error)
    alert('生成失败，请稍后重试')
  }
}

// 复制到剪贴板
const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text)
    alert('已复制到剪贴板！')
  } catch (error) {
    console.error('复制失败:', error)
    alert('复制失败，请手动复制')
  }
}

// Load saved AI analyses for a job application
const loadSavedAnalyses = async (jobAppId) => {
  try {
    const data = await aiJobAnalysisApi.getByJobApplication(jobAppId)
    savedAnalyses.value = data || []
  } catch (error) {
    console.error('加载AI分析结果失败:', error)
    savedAnalyses.value = []
  }
}

// Load customized resume for current job
const loadCustomizedResume = async (jobId) => {
  if (!jobId) {
    console.warn('⚠️ loadCustomizedResume: jobId is empty')
    return
  }

  loadingCustomizedResume.value = true
  try {
    const data = await resumeApi.getResumeByJob(jobId)
    // 处理后端返回204 No Content的情况（data可能是undefined, null, 空字符串或空对象）
    if (!data || (typeof data === 'object' && Object.keys(data).length === 0) || data === '') {
      customizedResume.value = null
    } else {
      customizedResume.value = data
    }
  } catch (error) {
    console.error('❌ 加载定制简历失败:', error)
    customizedResume.value = null
  } finally {
    loadingCustomizedResume.value = false
  }
}

// Clone default resume for job
const cloneResumeForJob = async () => {
  if (!selectedApplicationId.value) return

  try {
    const clonedResume = await resumeApi.cloneResumeForJob(selectedApplicationId.value)
    customizedResume.value = clonedResume

    // Navigate to resume management
    router.push({
      path: '/job-search/resume',
      query: {
        resumeId: clonedResume.id
      }
    })
  } catch (error) {
    console.error('克隆简历失败:', error)
    alert('创建定制简历失败，请稍后重试')
  }
}

// Edit customized resume
const editCustomizedResume = () => {
  if (!customizedResume.value) return

  router.push({
    path: '/job-search/resume',
    query: {
      resumeId: customizedResume.value.id
    }
  })
}

// View analysis details
const viewAnalysisDetails = (analysis) => {
  selectedAnalysis.value = analysis
  showAnalysisDetailsModal.value = true
}

// 开始编辑准备笔记
const startEditingNotes = () => {
  if (!selectedStage.value) return
  editingNotes.value = selectedStage.value.preparationNotes || ''
  isEditingNotes.value = true
}

// 取消编辑准备笔记
const cancelEditingNotes = () => {
  isEditingNotes.value = false
  editingNotes.value = ''
}

// 保存准备笔记
const savePreparationNotes = async () => {
  if (!selectedStage.value) return

  try {
    const payload = {
      ...selectedStage.value,
      preparationNotes: editingNotes.value
    }
    await interviewStageApi.update(selectedStage.value.id, payload)

    // 刷新面试阶段列表
    await loadInterviewStages(selectedApplicationId.value)
    isEditingNotes.value = false
  } catch (error) {
    console.error('保存准备笔记失败:', error)
    alert('保存失败，请稍后重试')
  }
}

// 编辑重点领域
// 获取当前阶段按技能分组的重点领域数据
const getSkillsWithFocusAreas = () => {
  if (!selectedStage.value || !selectedStage.value.skillIds) return []

  const skills = availableSkills.value
  const stageFocusAreaIds = selectedStage.value.focusAreaIds || []

  return selectedStage.value.skillIds.map(skillId => {
    const skill = skills.find(s => s.id === skillId)
    if (!skill) return null

    // 找出属于该技能的所有selected focus areas
    const selectedFocusAreas = (skill.focusAreas || []).filter(fa =>
      stageFocusAreaIds.includes(fa.id)
    )

    return {
      skillId: skill.id,
      skillName: skill.name,
      allFocusAreas: skill.focusAreas || [],
      selectedFocusAreas: selectedFocusAreas
    }
  }).filter(data => data !== null)
}

// 编辑某个技能的重点领域
const editSkillFocusAreas = (skillData) => {
  editingSkillData.value = skillData

  // 预选该技能已选中的Focus Area IDs
  selectedFocusAreaIds.value = skillData.selectedFocusAreas.map(fa => fa.id)

  showEditAreasModal.value = true
}

// 保存单个技能的重点领域
const saveSkillFocusAreas = async () => {
  if (!selectedStage.value || !editingSkillData.value) return

  try {
    // 获取当前阶段的所有focusAreaIds
    const currentFocusAreaIds = selectedStage.value.focusAreaIds || []

    // 移除该技能的旧Focus Area IDs
    const editingSkillFocusAreaIds = editingSkillData.value.allFocusAreas.map(fa => fa.id)
    const otherFocusAreaIds = currentFocusAreaIds.filter(
      id => !editingSkillFocusAreaIds.includes(id)
    )

    // 合并其他技能的Focus Area IDs 和 当前技能新选中的IDs
    const newFocusAreaIds = [...otherFocusAreaIds, ...selectedFocusAreaIds.value]

    const payload = {
      ...selectedStage.value,
      focusAreaIds: newFocusAreaIds
    }
    await interviewStageApi.update(selectedStage.value.id, payload)

    // 刷新面试阶段列表
    await loadInterviewStages(selectedApplicationId.value)
    showEditAreasModal.value = false
    editingSkillData.value = null
  } catch (error) {
    console.error('保存重点领域失败:', error)
    alert('保存失败，请稍后重试')
  }
}

// 检查某个技能是否被选中（其下任意Focus Area被选中）
const isSkillSelected = (skillId) => {
  const skill = availableSkills.value.find(s => s.id === skillId)
  if (!skill || !skill.focusAreas) return false

  return skill.focusAreas.some(fa => selectedFocusAreaIds.value.includes(fa.id))
}

// 切换整个技能的选中状态
const toggleSkill = (skillId) => {
  const skill = availableSkills.value.find(s => s.id === skillId)
  if (!skill || !skill.focusAreas) return

  const allFocusAreaIds = skill.focusAreas.map(fa => fa.id)
  const isAllSelected = allFocusAreaIds.every(id => selectedFocusAreaIds.value.includes(id))

  if (isAllSelected) {
    // 取消选中该技能的所有Focus Areas
    selectedFocusAreaIds.value = selectedFocusAreaIds.value.filter(
      id => !allFocusAreaIds.includes(id)
    )
  } else {
    // 选中该技能的所有Focus Areas
    allFocusAreaIds.forEach(id => {
      if (!selectedFocusAreaIds.value.includes(id)) {
        selectedFocusAreaIds.value.push(id)
      }
    })
  }
}

// 跳转到技能学习页面
const navigateToSkillLearning = (focusArea) => {
  if (!focusArea || !focusArea.skillId) {
    console.error('Focus Area缺少skillId:', focusArea)
    return
  }

  // 在新标签页打开通用技能学习页面，通过query参数传递focusAreaId以便自动定位
  const url = `/general-skills/learning/${focusArea.skillId}?focusAreaId=${focusArea.id}`
  window.open(url, '_blank')
}

// 获取分类样式
const getCategoryStyle = (category) => {
  const styles = {
    'Project': {
      border: 'border-blue-300',
      bg: 'bg-blue-50 hover:bg-blue-100',
      badge: 'bg-blue-500 text-white'
    },
    'People Management': {
      border: 'border-purple-300',
      bg: 'bg-purple-50 hover:bg-purple-100',
      badge: 'bg-purple-500 text-white'
    },
    'Skill': {
      border: 'border-green-300',
      bg: 'bg-green-50 hover:bg-green-100',
      badge: 'bg-green-500 text-white'
    },
    'Focus Area': {
      border: 'border-emerald-300',
      bg: 'bg-emerald-50 hover:bg-emerald-100',
      badge: 'bg-emerald-500 text-white'
    },
    'Study': {
      border: 'border-indigo-300',
      bg: 'bg-indigo-50',
      badge: 'bg-indigo-500 text-white'
    },
    'Practice': {
      border: 'border-orange-300',
      bg: 'bg-orange-50',
      badge: 'bg-orange-500 text-white'
    },
    'Research': {
      border: 'border-pink-300',
      bg: 'bg-pink-50',
      badge: 'bg-pink-500 text-white'
    },
    'Review': {
      border: 'border-teal-300',
      bg: 'bg-teal-50',
      badge: 'bg-teal-500 text-white'
    },
    'Other': {
      border: 'border-gray-300',
      bg: 'bg-gray-50',
      badge: 'bg-gray-500 text-white'
    }
  }
  return styles[category] || styles['Other']
}

// 获取分类图标
const getCategoryIcon = (category) => {
  const icons = {
    'Project': '📁',
    'People Management': '👥',
    'Skill': '⚡',
    'Focus Area': '🎯',
    'Study': '📚',
    'Practice': '💪',
    'Research': '🔍',
    'Review': '📝',
    'Other': '📌'
  }
  return icons[category] || '📌'
}

// 检查分类是否可以跳转
const canNavigateToCategory = (category) => {
  return ['Project', 'People Management', 'Skill', 'Focus Area'].includes(category)
}

// 跳转到清单分类对应的页面
const navigateToChecklistCategory = (item) => {
  if (!canNavigateToCategory(item.category)) return

  switch (item.category) {
    case 'Project':
      router.push('/job-search/projects')
      break
    case 'People Management':
      router.push('/job-search/management')
      break
    case 'Skill':
    case 'Focus Area':
      // 需要从notes中解析skillId和focusAreaId
      // 假设notes格式为: "Skill ID: 5, Focus Area ID: 10"
      const skillMatch = item.notes?.match(/Skill ID:\s*(\d+)/)
      const focusAreaMatch = item.notes?.match(/Focus Area ID:\s*(\d+)/)

      if (skillMatch && focusAreaMatch) {
        const skillId = skillMatch[1]
        const focusAreaId = focusAreaMatch[1]
        router.push({
          path: `/skills/${skillId}`,
          query: { focusAreaId }
        })
      } else if (skillMatch) {
        // 只有skillId，跳转到技能页面
        router.push(`/skills/${skillMatch[1]}`)
      }
      break
  }
}

// 根据Recruiter Insights自动生成准备清单建议
const generateSuggestedChecklist = async () => {
  if (!currentStage.value || !currentApplication.value?.recruiterInsights) {
    alert('无法生成建议：缺少阶段或Recruiter Insights信息')
    return
  }

  const insights = currentApplication.value.recruiterInsights
  const stageName = currentStage.value.stageName.toLowerCase()
  const suggestedItems = []

  // 根据阶段类型生成不同的准备建议
  if (stageName.includes('算法') || stageName.includes('coding') || stageName.includes('编程')) {
    // 算法/编程轮建议
    if (insights.techStackPreference?.length > 0) {
      insights.techStackPreference.slice(0, 3).forEach(tech => {
        suggestedItems.push({
          checklistItem: `复习${tech}相关算法题`,
          category: '算法',
          isPriority: true,
          notes: `参考：\n- LeetCode ${tech} tag\n- [HackerRank ${tech} Practice](https://www.hackerrank.com/)`,
          sortOrder: suggestedItems.length
        })
      })
    }
    suggestedItems.push({
      checklistItem: '刷常见数据结构题（数组、链表、树、图）',
      category: '算法',
      isPriority: true,
      notes: '- [LeetCode Hot 100](https://leetcode.com/problemset/all/)\n- 重点关注中等难度',
      sortOrder: suggestedItems.length
    })
  } else if (stageName.includes('系统设计') || stageName.includes('system design')) {
    // 系统设计轮建议
    if (insights.techStackPreference?.length > 0) {
      suggestedItems.push({
        checklistItem: `准备使用${insights.techStackPreference.slice(0, 2).join('、')}的系统设计案例`,
        category: '系统设计',
        isPriority: true,
        notes: '准备至少2个完整案例，包括架构图、技术选型理由、权衡分析',
        sortOrder: suggestedItems.length
      })
    }
    suggestedItems.push({
      checklistItem: '复习分布式系统核心概念',
      category: '系统设计',
      isPriority: true,
      notes: '- CAP定理\n- 一致性协议（Raft, Paxos）\n- 负载均衡\n- 缓存策略\n- [System Design Primer](https://github.com/donnemartin/system-design-primer)',
      sortOrder: suggestedItems.length
    })
    suggestedItems.push({
      checklistItem: '准备高可用、高并发架构设计',
      category: '系统设计',
      isPriority: false,
      notes: '- 微服务架构\n- 数据库分库分表\n- 消息队列\n- 服务降级和熔断',
      sortOrder: suggestedItems.length
    })
  } else if (stageName.includes('行为') || stageName.includes('behavioral') || stageName.includes('hr')) {
    // 行为面试轮建议
    if (insights.teamCulture) {
      suggestedItems.push({
        checklistItem: `准备符合团队文化的STAR案例：${insights.teamCulture.substring(0, 20)}`,
        category: '行为面试',
        isPriority: true,
        notes: '重点强调与团队文化匹配的经历',
        sortOrder: suggestedItems.length
      })
    }
    suggestedItems.push({
      checklistItem: '准备3-5个STAR案例',
      category: '行为面试',
      isPriority: true,
      notes: '涵盖：\n- 团队冲突解决\n- 项目压力处理\n- 技术决策过程\n- 领导力展示\n- 失败经验反思',
      sortOrder: suggestedItems.length
    })
  } else if (stageName.includes('技术') || stageName.includes('technical')) {
    // 通用技术面试建议
    if (insights.techStackPreference?.length > 0) {
      insights.techStackPreference.forEach(tech => {
        suggestedItems.push({
          checklistItem: `深入复习${tech}核心原理`,
          category: '技术栈',
          isPriority: true,
          notes: `- 底层实现原理\n- 常见问题和解决方案\n- 性能优化技巧`,
          sortOrder: suggestedItems.length
        })
      })
    }
    if (insights.interviewFocus) {
      suggestedItems.push({
        checklistItem: `针对性准备：${insights.interviewFocus}`,
        category: '项目经验',
        isPriority: true,
        notes: 'Recruiter强调的面试重点',
        sortOrder: suggestedItems.length
      })
    }
  }

  // 如果有流程Tips，添加一个通用建议
  if (insights.processTips) {
    suggestedItems.push({
      checklistItem: 'Recruiter流程建议',
      category: '其他',
      isPriority: true,
      notes: insights.processTips,
      sortOrder: suggestedItems.length
    })
  }

  if (suggestedItems.length === 0) {
    alert('当前阶段和Recruiter Insights无法匹配生成建议，请手动添加')
    return
  }

  // 批量创建建议清单项
  try {
    for (const item of suggestedItems) {
      await interviewPreparationChecklistApi.createChecklist({
        ...item,
        interviewStageId: currentStageId.value
      })
    }
    alert(`成功生成${suggestedItems.length}条建议清单项！`)
    await loadChecklistItems(currentStageId.value)
  } catch (error) {
    console.error('生成建议清单失败:', error)
    alert('生成失败，请稍后重试')
  }
}

// 添加面试阶段
const addStage = async () => {
  editingStage.value = null
  stageFormData.value = {
    stageName: '',
    stageOrder: interviewStages.value.length + 1,
    skillIds: [],
    focusAreaIds: [],
    preparationNotes: ''
  }

  // 加载所有可用技能和Focus Areas
  try {
    const skills = await getSkills()
    availableSkills.value = skills || []
    showAddStageModal.value = true
  } catch (error) {
    console.error('加载技能列表失败:', error)
    alert('加载失败，请稍后重试')
  }
}

// 编辑面试阶段
const editStage = async (stage) => {
  editingStage.value = stage
  stageFormData.value = {
    stageName: stage.stageName || '',
    stageOrder: stage.stageOrder || 1,
    skillIds: stage.skillIds ? [...stage.skillIds] : [],
    focusAreaIds: stage.focusAreaIds ? [...stage.focusAreaIds] : [],
    preparationNotes: stage.preparationNotes || ''
  }

  // 加载所有可用技能和Focus Areas
  try {
    const skills = await getSkills()
    availableSkills.value = skills || []
    showAddStageModal.value = true
  } catch (error) {
    console.error('加载技能列表失败:', error)
    alert('加载失败，请稍后重试')
  }
}

// 取消添加/编辑面试阶段
const cancelAddStage = () => {
  showAddStageModal.value = false
  editingStage.value = null
  stageFormData.value = {
    stageName: '',
    stageOrder: 1,
    skillIds: [],
    focusAreaIds: [],
    preparationNotes: ''
  }
  availableSkills.value = []
}

// 保存面试阶段
const saveStage = async () => {
  if (!stageFormData.value.stageName.trim()) {
    alert('请输入阶段名称')
    return
  }

  try {
    // 数据一致性检查：清理不属于选中技能的Focus Area
    const selectedSkillIds = stageFormData.value.skillIds || []
    let cleanedFocusAreaIds = stageFormData.value.focusAreaIds || []

    if (selectedSkillIds.length > 0 && cleanedFocusAreaIds.length > 0) {
      // 收集所有选中技能的Focus Area IDs
      const validFocusAreaIds = new Set()
      selectedSkillIds.forEach(skillId => {
        const skill = availableSkills.value.find(s => s.id === skillId)
        if (skill && skill.focusAreas) {
          skill.focusAreas.forEach(fa => validFocusAreaIds.add(fa.id))
        }
      })

      // 只保留属于选中技能的Focus Area
      cleanedFocusAreaIds = cleanedFocusAreaIds.filter(faId => validFocusAreaIds.has(faId))
    }

    const payload = {
      ...stageFormData.value,
      focusAreaIds: cleanedFocusAreaIds,
      jobApplicationId: selectedApplicationId.value
    }

    if (editingStage.value?.id) {
      // 编辑模式
      await interviewStageApi.update(editingStage.value.id, payload)
    } else {
      // 新建模式
      await interviewStageApi.create(payload)
    }

    showAddStageModal.value = false
    editingStage.value = null
    await loadInterviewStages(selectedApplicationId.value)
  } catch (error) {
    console.error('保存面试阶段失败:', error)
    alert('保存失败，请稍后重试')
  }
}

// 检查某个技能是否被选中（为面试阶段选择）
const isSkillSelectedForStage = (skillId) => {
  const skill = availableSkills.value.find(s => s.id === skillId)
  if (!skill || !skill.focusAreas) return false

  return skill.focusAreas.some(fa => stageFormData.value.focusAreaIds.includes(fa.id))
}

// 切换整个技能的选中状态（为面试阶段选择）
const toggleSkillForStage = (skillId) => {
  const skill = availableSkills.value.find(s => s.id === skillId)
  if (!skill || !skill.focusAreas) return

  const allFocusAreaIds = skill.focusAreas.map(fa => fa.id)
  const isAllSelected = allFocusAreaIds.every(id => stageFormData.value.focusAreaIds.includes(id))

  if (isAllSelected) {
    // 取消选中该技能的所有Focus Areas
    stageFormData.value.focusAreaIds = stageFormData.value.focusAreaIds.filter(
      id => !allFocusAreaIds.includes(id)
    )
  } else {
    // 选中该技能的所有Focus Areas
    allFocusAreaIds.forEach(id => {
      if (!stageFormData.value.focusAreaIds.includes(id)) {
        stageFormData.value.focusAreaIds.push(id)
      }
    })
  }
}

// 开始编辑申请状态
const startEditingStatus = () => {
  if (!currentApplication.value) return
  editingStatusValue.value = currentApplication.value.applicationStatus || '未申请'
  isEditingStatus.value = true
}

// 取消编辑申请状态
const cancelEditingStatus = () => {
  isEditingStatus.value = false
  editingStatusValue.value = ''
}

// 保存申请状态
const saveApplicationStatus = async () => {
  if (!currentApplication.value || !selectedApplicationId.value) return

  try {
    const payload = {
      ...currentApplication.value,
      applicationStatus: editingStatusValue.value
    }
    await jobApplicationApi.updateJobApplication(selectedApplicationId.value, payload)

    isEditingStatus.value = false
    await loadApplications()

    // 重新选中当前申请以刷新数据
    const currentId = selectedApplicationId.value
    selectedApplicationId.value = null
    setTimeout(() => {
      selectedApplicationId.value = currentId
    }, 100)
  } catch (error) {
    console.error('保存申请状态失败:', error)
    alert('保存失败，请稍后重试')
  }
}
</script>

<style scoped>
/* 文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

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

.prose :deep(code) {
  @apply bg-gray-100 px-1 py-0.5 rounded text-sm font-mono;
}

.prose :deep(a) {
  @apply text-blue-600 hover:underline;
}

/* 准备清单Markdown链接样式 */
.prose-sm :deep(ul) {
  @apply list-disc ml-4 mb-1;
}

.prose-sm :deep(li) {
  @apply mb-0.5;
}

.prose-sm :deep(a) {
  @apply text-blue-600 hover:text-blue-800 underline;
}

.prose-sm :deep(p) {
  @apply mb-1;
}

/* 文本截断样式 */
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
