<template>
  <div class="job-application-list h-full flex bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 左侧：职位申请列表 -->
    <div class="w-96 bg-white border-r border-gray-200 flex flex-col shadow-lg">
      <div class="bg-gradient-to-r from-indigo-600 to-purple-600 p-4 border-b border-purple-700">
        <h2 class="text-lg font-bold text-white mb-3">📋 职位申请列表</h2>
        <div class="flex gap-2">
          <select
            v-model="filterStatus"
            class="flex-1 px-3 py-2 bg-white text-gray-700 rounded-lg text-sm focus:ring-2 focus:ring-purple-300"
          >
            <option value="">全部状态</option>
            <option value="NotApplied">未申请</option>
            <option value="Applied">已投递</option>
            <option value="Screening">筛选中</option>
            <option value="Interviewing">面试中</option>
            <option value="Offer">Offer</option>
            <option value="Rejected">已拒绝</option>
            <option value="Withdrawn">已撤回</option>
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

              <div class="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label class="block text-sm font-medium text-gray-600 mb-1">职位级别</label>
                  <p class="text-gray-900">{{ currentApplication.positionLevel || '未设置' }}</p>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-600 mb-1">申请状态</label>
                  <span :class="['px-3 py-1 rounded-full text-sm font-medium inline-block', getStatusColor(currentApplication.applicationStatus)]">
                    {{ getStatusText(currentApplication.applicationStatus) }}
                  </span>
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
                <h3 class="text-lg font-bold text-white">面试阶段</h3>
                <p class="text-sm text-purple-100 mt-1">{{ interviewStages.length }} 个阶段</p>
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
                    </div>
                  </div>
                  <div class="flex items-center gap-3 text-xs text-gray-500 mt-2">
                    <span v-if="stage.focusAreas?.length > 0">{{ stage.focusAreas.length }} 个领域</span>
                    <span v-if="stage.checklistItems?.length > 0">{{ stage.checklistItems.length }} 项清单</span>
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
            <div class="flex-1 overflow-y-auto bg-gray-50">
              <div v-if="selectedStage" class="p-6 space-y-6">
                <!-- Recruiter Insights 提示（如果有） -->
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

                <!-- 准备笔记 -->
                <div v-if="selectedStage.preparationNotes" class="bg-white rounded-lg shadow p-5">
                  <h4 class="text-sm font-semibold text-gray-900 mb-3">📝 准备笔记</h4>
                  <div v-html="renderMarkdown(selectedStage.preparationNotes)" class="prose max-w-none text-sm"></div>
                </div>

                <!-- 重点领域 -->
                <div v-if="selectedStage.focusAreas && selectedStage.focusAreas.length > 0" class="bg-white rounded-lg shadow p-5">
                  <h4 class="text-sm font-semibold text-gray-900 mb-3">🎯 重点领域</h4>
                  <div class="flex flex-wrap gap-2">
                    <span
                      v-for="focusArea in selectedStage.focusAreas"
                      :key="focusArea.id"
                      class="px-3 py-2 bg-purple-100 text-purple-700 rounded-lg text-xs font-medium"
                      :title="focusArea.description"
                    >
                      {{ focusArea.skillName }} / {{ focusArea.name }}
                    </span>
                  </div>
                </div>

                <!-- 准备清单 -->
                <div class="bg-white rounded-lg shadow p-5">
                  <div class="flex items-center justify-between mb-4">
                    <h4 class="text-sm font-semibold text-gray-900">✅ 准备清单</h4>
                    <button
                      @click="showPreparationChecklist(selectedStage.id)"
                      class="px-3 py-1.5 bg-green-500 text-white text-xs rounded-lg hover:bg-green-600 font-medium"
                    >
                      管理清单
                    </button>
                  </div>

                  <div v-if="selectedStage.checklistItems && selectedStage.checklistItems.length > 0" class="space-y-2">
                    <div
                      v-for="item in selectedStage.checklistItems.slice(0, 5)"
                      :key="item.id"
                      class="flex items-start gap-2 p-2 bg-gray-50 rounded"
                    >
                      <span :class="['px-2 py-0.5 rounded text-xs font-medium', item.isPriority ? 'bg-yellow-100 text-yellow-800' : 'bg-blue-100 text-blue-700']">
                        {{ item.category }}
                      </span>
                      <span class="text-xs text-gray-700 flex-1">{{ item.checklistItem }}</span>
                    </div>
                    <p v-if="selectedStage.checklistItems.length > 5" class="text-xs text-gray-500 text-center pt-2">
                      还有 {{ selectedStage.checklistItems.length - 5 }} 项，点击"管理清单"查看全部
                    </p>
                  </div>
                  <p v-else class="text-sm text-gray-500 text-center py-4">暂无清单项，点击"管理清单"添加</p>
                </div>

                <!-- 面试记录 -->
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
                  <p v-else class="text-sm text-gray-500 text-center py-6">该阶段暂无面试记录</p>
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

          <!-- Tab 3: 简历 -->
          <div v-if="activeTab === 'resume'" class="space-y-6">
            <!-- AI 分析 Prompt 生成 -->
            <div class="bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg shadow p-6 border border-purple-200">
              <div class="flex items-center justify-between mb-4">
                <div>
                  <h3 class="text-lg font-semibold text-gray-900 flex items-center gap-2">
                    <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                    </svg>
                    AI 深度分析
                  </h3>
                  <p class="text-sm text-gray-600 mt-1">使用Claude Code进行专业的AI分析</p>
                </div>
                <button
                  @click="generateAIPrompt"
                  class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg hover:from-purple-700 hover:to-indigo-700 font-medium transition-all shadow-md hover:shadow-lg"
                >
                  生成AI分析Prompt
                </button>
              </div>

              <div v-if="aiPromptData" class="bg-white rounded-lg p-4 border border-purple-200">
                <div class="mb-3">
                  <label class="block text-sm font-semibold text-gray-700 mb-2">步骤1: 复制以下Prompt到Claude Code</label>
                  <div class="relative">
                    <pre class="bg-gray-50 p-4 rounded border border-gray-300 text-sm overflow-x-auto whitespace-pre-wrap">{{ aiPromptData.prompt }}</pre>
                    <button
                      @click="copyToClipboard(aiPromptData.prompt)"
                      class="absolute top-2 right-2 px-3 py-1 bg-blue-500 text-white text-xs rounded hover:bg-blue-600"
                    >
                      复制
                    </button>
                  </div>
                </div>

                <div v-if="aiPromptData.setupInstructions" class="mt-4 p-3 bg-yellow-50 rounded border border-yellow-200">
                  <label class="block text-sm font-semibold text-gray-700 mb-2">首次使用配置 (只需运行一次)</label>
                  <pre class="text-xs text-gray-700 whitespace-pre-wrap">{{ aiPromptData.setupInstructions }}</pre>
                </div>
              </div>
            </div>

            <!-- 已保存的AI分析结果 -->
            <div v-if="savedAnalyses && savedAnalyses.length > 0" class="bg-white rounded-lg shadow p-6 mt-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                已保存的AI分析结果
              </h3>

              <div v-for="analysis in savedAnalyses" :key="analysis.id" class="mb-4 last:mb-0">
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

            <!-- 加载简历分析 -->
            <div v-if="!resumeAnalysis" class="bg-white rounded-lg shadow p-6">
              <div class="text-center py-12">
                <button
                  @click="loadResumeAnalysis"
                  class="px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 font-medium"
                >
                  分析简历匹配度
                </button>
                <p class="text-sm text-gray-500 mt-3">分析您的简历与此职位的匹配程度，并提供定制化建议</p>
              </div>
            </div>

            <!-- 简历分析结果 -->
            <div v-if="resumeAnalysis" class="space-y-6">
              <!-- 匹配度概览 -->
              <div class="bg-white rounded-lg shadow p-6">
                <div class="flex items-center justify-between mb-4">
                  <h3 class="text-lg font-semibold text-gray-900">匹配度分析</h3>
                  <button
                    @click="loadResumeAnalysis"
                    class="text-sm text-indigo-600 hover:text-indigo-700"
                  >
                    重新分析
                  </button>
                </div>

                <div class="flex items-center gap-6">
                  <!-- 匹配度圆环 -->
                  <div class="relative w-32 h-32">
                    <svg class="w-full h-full transform -rotate-90">
                      <circle
                        cx="64"
                        cy="64"
                        r="56"
                        stroke="#e5e7eb"
                        stroke-width="12"
                        fill="none"
                      />
                      <circle
                        cx="64"
                        cy="64"
                        r="56"
                        :stroke="getMatchScoreColor(resumeAnalysis.matchScore)"
                        stroke-width="12"
                        fill="none"
                        :stroke-dasharray="`${(resumeAnalysis.matchScore / 100) * 351.86} 351.86`"
                        stroke-linecap="round"
                      />
                    </svg>
                    <div class="absolute inset-0 flex items-center justify-center">
                      <div class="text-center">
                        <p class="text-2xl font-bold" :class="getMatchScoreColor(resumeAnalysis.matchScore)">
                          {{ resumeAnalysis.matchScore }}%
                        </p>
                        <p class="text-xs text-gray-500">匹配度</p>
                      </div>
                    </div>
                  </div>

                  <!-- 匹配详情 -->
                  <div class="flex-1">
                    <div class="grid grid-cols-2 gap-4">
                      <div>
                        <label class="block text-sm font-medium text-gray-600 mb-1">匹配技能</label>
                        <p class="text-2xl font-bold text-green-600">{{ resumeAnalysis.matchedSkills.length }}</p>
                      </div>
                      <div>
                        <label class="block text-sm font-medium text-gray-600 mb-1">缺失技能</label>
                        <p class="text-2xl font-bold text-red-600">{{ resumeAnalysis.missingSkills.length }}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 匹配的技能 -->
              <div v-if="resumeAnalysis.matchedSkills.length > 0" class="bg-white rounded-lg shadow p-6">
                <h4 class="text-sm font-semibold text-gray-900 mb-3">✅ 已匹配技能</h4>
                <div class="flex flex-wrap gap-2">
                  <span
                    v-for="skill in resumeAnalysis.matchedSkills"
                    :key="skill"
                    class="px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm font-medium"
                  >
                    {{ skill }}
                  </span>
                </div>
              </div>

              <!-- 缺失的技能 -->
              <div v-if="resumeAnalysis.missingSkills.length > 0" class="bg-white rounded-lg shadow p-6">
                <h4 class="text-sm font-semibold text-gray-900 mb-3">⚠️ 需要补充的技能</h4>
                <div class="flex flex-wrap gap-2">
                  <span
                    v-for="skill in resumeAnalysis.missingSkills"
                    :key="skill"
                    class="px-3 py-1 bg-red-100 text-red-700 rounded-full text-sm font-medium"
                  >
                    {{ skill }}
                  </span>
                </div>
              </div>

              <!-- 优势分析 -->
              <div v-if="resumeAnalysis.strengths.length > 0" class="bg-white rounded-lg shadow p-6">
                <h4 class="text-sm font-semibold text-gray-900 mb-3">💪 您的优势</h4>
                <ul class="space-y-2">
                  <li v-for="(strength, idx) in resumeAnalysis.strengths" :key="idx" class="flex items-start gap-2">
                    <svg class="w-5 h-5 text-green-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>
                    <span class="text-sm text-gray-700">{{ strength }}</span>
                  </li>
                </ul>
              </div>

              <!-- 改进建议 -->
              <div v-if="resumeAnalysis.improvements.length > 0" class="bg-white rounded-lg shadow p-6">
                <h4 class="text-sm font-semibold text-gray-900 mb-3">📈 改进建议</h4>
                <ul class="space-y-2">
                  <li v-for="(improvement, idx) in resumeAnalysis.improvements" :key="idx" class="flex items-start gap-2">
                    <svg class="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                      <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
                    </svg>
                    <span class="text-sm text-gray-700">{{ improvement }}</span>
                  </li>
                </ul>
              </div>

              <!-- 定制化建议 (仅在匹配度>70%时显示) -->
              <div v-if="resumeAnalysis.customization" class="bg-gradient-to-r from-purple-50 to-indigo-50 rounded-lg shadow-md border border-purple-200 p-6">
                <h3 class="text-lg font-semibold text-purple-900 mb-4 flex items-center gap-2">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
                  </svg>
                  定制化建议
                </h3>

                <!-- 关键词优化 -->
                <div v-if="resumeAnalysis.customization.keywordSuggestions?.length > 0" class="mb-6">
                  <h4 class="text-sm font-semibold text-gray-900 mb-3">🔑 关键词优化</h4>
                  <div class="space-y-2">
                    <div
                      v-for="(kw, idx) in resumeAnalysis.customization.keywordSuggestions"
                      :key="idx"
                      class="bg-white rounded-lg p-3 border border-purple-100"
                    >
                      <p class="font-medium text-sm text-purple-900">{{ kw.keyword }}</p>
                      <p class="text-xs text-gray-600 mt-1">{{ kw.reason }}</p>
                      <p class="text-xs text-purple-600 mt-1">建议位置: {{ kw.section }}</p>
                    </div>
                  </div>
                </div>

                <!-- 项目优化 -->
                <div v-if="resumeAnalysis.customization.projectOptimizations?.length > 0" class="mb-6">
                  <h4 class="text-sm font-semibold text-gray-900 mb-3">📁 项目经验优化</h4>
                  <div class="space-y-3">
                    <div
                      v-for="project in resumeAnalysis.customization.projectOptimizations"
                      :key="project.projectId"
                      class="bg-white rounded-lg p-3 border border-purple-100"
                    >
                      <p class="font-medium text-sm text-purple-900 mb-2">{{ project.projectName }}</p>
                      <ul class="space-y-1">
                        <li v-for="(suggestion, idx) in project.suggestions" :key="idx" class="text-xs text-gray-700 flex items-start gap-1">
                          <span class="text-purple-600 flex-shrink-0">•</span>
                          <span>{{ suggestion }}</span>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>

                <!-- 技能突出建议 -->
                <div v-if="resumeAnalysis.customization.skillHighlights?.length > 0" class="mb-6">
                  <h4 class="text-sm font-semibold text-gray-900 mb-3">⭐ 技能突出建议</h4>
                  <div class="space-y-2">
                    <div
                      v-for="(highlight, idx) in resumeAnalysis.customization.skillHighlights"
                      :key="idx"
                      class="bg-white rounded-lg p-3 border border-purple-100"
                    >
                      <p class="font-medium text-sm text-purple-900">{{ highlight.skill }}</p>
                      <p class="text-xs text-gray-600 mt-1">
                        <span class="text-gray-500">当前:</span> {{ highlight.currentMention }}
                      </p>
                      <p class="text-xs text-purple-700 mt-1">
                        <span class="font-medium">建议:</span> {{ highlight.suggestedMention }}
                      </p>
                    </div>
                  </div>
                </div>

                <!-- 结构建议 -->
                <div v-if="resumeAnalysis.customization.structuralSuggestions?.length > 0">
                  <h4 class="text-sm font-semibold text-gray-900 mb-3">📐 整体结构建议</h4>
                  <ul class="space-y-2">
                    <li v-for="(suggestion, idx) in resumeAnalysis.customization.structuralSuggestions" :key="idx" class="flex items-start gap-2">
                      <svg class="w-5 h-5 text-purple-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                      </svg>
                      <span class="text-sm text-gray-700">{{ suggestion }}</span>
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            <!-- 定制简历部分 -->
            <div class="bg-white rounded-lg shadow p-6 mt-6">
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
                  <p class="text-sm text-gray-700 line-clamp-3">{{ customizedResume.about }}</p>
                </div>

                <div class="grid grid-cols-3 gap-3 text-center">
                  <div class="p-3 bg-blue-50 rounded-lg">
                    <p class="text-xs text-gray-600 mb-1">工作经历</p>
                    <p class="text-lg font-bold text-blue-600">{{ customizedResume.experiences?.length || 0 }}</p>
                  </div>
                  <div class="p-3 bg-purple-50 rounded-lg">
                    <p class="text-xs text-gray-600 mb-1">技能</p>
                    <p class="text-lg font-bold text-purple-600">{{ customizedResume.skills?.length || 0 }}</p>
                  </div>
                  <div class="p-3 bg-green-50 rounded-lg">
                    <p class="text-xs text-gray-600 mb-1">项目</p>
                    <p class="text-lg font-bold text-green-600">{{ customizedResume.projects?.length || 0 }}</p>
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

              <div v-if="allChecklistItems.length === 0" class="text-center text-gray-500 py-8">
                <p class="text-sm">暂无准备清单</p>
                <p class="text-xs mt-1">点击上方"+ 添加清单项"开始添加</p>
              </div>
            </div>
          </div>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { jobApplicationApi } from '@/api/jobApplicationApi'
import { interviewStageApi } from '@/api/interviewStageApi'
import { interviewRecordApi } from '@/api/interviewRecordApi'
import interviewPreparationChecklistApi from '@/api/interviewPreparationChecklistApi'
import { resumeAnalysisApi } from '@/api/resumeAnalysisApi'
import { aiJobAnalysisApi } from '@/api/aiJobAnalysisApi'
import { resumeApi } from '@/api/resumeApi'
import MarkdownIt from 'markdown-it'

const router = useRouter()
const md = new MarkdownIt()

const applications = ref([])
const interviewStages = ref([])
const interviewRecords = ref([])
const selectedApplicationId = ref(null)
const activeTab = ref('overview')
const filterStatus = ref('')
const selectedStageId = ref(null) // 选中的面试阶段ID

const showChecklistModal = ref(false)
const showEditChecklistModal = ref(false)
const showRecruiterInsightsModal = ref(false)
const showInterviewRecordModal = ref(false)
const currentStageId = ref(null)
const allChecklistItems = ref([])
const priorityItems = ref([])
const editingChecklistItem = ref(null)
const editingInterviewRecord = ref(null)
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

const tabs = [
  { id: 'overview', name: '概览' },
  { id: 'interviews', name: '面试' },
  { id: 'resume', name: '简历' }
]

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
  if (!filterStatus.value) return applications.value
  return applications.value.filter(app => app.applicationStatus === filterStatus.value)
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

const showPreparationChecklist = async (stageId) => {
  currentStageId.value = stageId
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

const loadApplications = async () => {
  try {
    const data = await jobApplicationApi.getAllJobApplications()
    applications.value = data || []
    if (applications.value.length > 0 && !selectedApplicationId.value) {
      selectedApplicationId.value = applications.value[0].id
    }
  } catch (error) {
    console.error('加载职位申请失败:', error)
  }
}

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
  if (!jobId) return

  loadingCustomizedResume.value = true
  try {
    const data = await resumeApi.getResumeByJob(jobId)
    customizedResume.value = data
  } catch (error) {
    console.error('加载定制简历失败:', error)
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
  // Parse the full analysis result
  try {
    const fullAnalysis = JSON.parse(analysis.aiAnalysisResult)
    console.log('完整分析结果:', fullAnalysis)

    // Show a detailed modal or expand the view
    alert(`
AI分析详情 (ID: ${analysis.id})

总体匹配度: ${analysis.metadata.overallScore}/100
推荐等级: ${analysis.metadata.recommendation}

技能匹配: ${analysis.metadata.skillMatchScore}/100
经验匹配: ${analysis.metadata.experienceMatchScore}/100

核心优势:
${analysis.metadata.keyStrengths ? analysis.metadata.keyStrengths.map((s, i) => `${i + 1}. ${s}`).join('\n') : '无'}

改进建议:
${analysis.metadata.keyWeaknesses ? analysis.metadata.keyWeaknesses.map((s, i) => `${i + 1}. ${s}`).join('\n') : '无'}

查看完整JSON分析请打开浏览器控制台
    `)
  } catch (error) {
    console.error('解析分析结果失败:', error)
    alert('无法显示详细信息')
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
