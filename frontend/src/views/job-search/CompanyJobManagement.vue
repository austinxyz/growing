<template>
  <div class="company-job-management h-full flex bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 左侧：公司列表 -->
    <div class="w-80 bg-white border-r border-gray-200 flex flex-col shadow-lg">
      <div class="bg-gradient-to-r from-indigo-600 to-purple-600 p-4 border-b border-purple-700">
        <h2 class="text-lg font-bold text-white mb-3">🏢 公司列表</h2>
        <button
          @click="showCreateCompanyModal = true"
          class="w-full px-4 py-2.5 bg-gradient-to-r from-green-500 to-emerald-500 text-white rounded-lg hover:from-green-600 hover:to-emerald-600 hover:shadow-lg transition-all flex items-center justify-center gap-2 font-semibold shadow-md text-sm"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          新建公司
        </button>
      </div>

      <div class="flex-1 overflow-y-auto">
        <div class="p-2">
          <div
            v-for="company in companies"
            :key="company.id"
            @click="selectCompany(company.id)"
            :class="[
              'p-3 mb-2 rounded-lg cursor-pointer transition-all duration-200',
              selectedCompanyId === company.id
                ? 'bg-gradient-to-r from-indigo-50 to-purple-50 border-l-4 border-l-indigo-600 shadow-md'
                : 'bg-gray-50 border border-gray-200 hover:bg-gradient-to-r hover:from-gray-50 hover:to-indigo-50 hover:shadow-sm'
            ]"
          >
            <div class="font-semibold text-gray-900">{{ company.companyName }}</div>
            <div class="text-xs text-gray-600 mt-1">{{ company.industry || '未分类' }}</div>
            <div class="text-xs text-gray-500 mt-1">
              {{ company.companySize ? `规模: ${company.companySize}` : '' }}
            </div>
          </div>

          <div v-if="!companies.length" class="text-center text-gray-500 py-8">
            <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
            <p class="text-sm">暂无公司</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧：公司详情 + Tab -->
    <div class="flex-1 flex flex-col">
      <div v-if="currentCompany" class="flex-1 flex flex-col">
        <!-- 顶部操作栏 -->
        <div class="bg-gradient-to-r from-indigo-600 to-purple-600 border-b border-purple-700 p-4 flex items-center justify-between shadow-lg">
          <h1 class="text-xl font-bold text-white">{{ currentCompany.companyName }}</h1>
          <div class="flex gap-2">
            <button
              @click="deleteCompany"
              class="px-4 py-2 bg-gradient-to-r from-red-500 to-pink-500 text-white rounded-lg hover:from-red-600 hover:to-pink-600 hover:shadow-lg transition-all font-semibold shadow-md"
            >
              删除公司
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
        <div class="flex-1 overflow-y-auto p-4">
          <!-- Tab 1: 公司基本信息 -->
          <div v-if="activeTab === 'info'" class="bg-white rounded-lg shadow p-6 space-y-6">
            <div class="flex justify-end gap-2">
              <button
                v-if="!editModes.info"
                @click="editModes.info = true; loadCompanyForEdit()"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                编辑
              </button>
              <template v-else>
                <button
                  @click="editModes.info = false"
                  class="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                >
                  取消
                </button>
                <button
                  @click="saveCompanyInfo"
                  class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                >
                  保存
                </button>
              </template>
            </div>

            <div v-if="!editModes.info" class="space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-600 mb-1">行业</label>
                  <p class="text-gray-900">{{ currentCompany.industry || '未设置' }}</p>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-600 mb-1">公司规模</label>
                  <p class="text-gray-900">{{ currentCompany.companySize || '未设置' }}</p>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-600 mb-1">地点</label>
                  <p class="text-gray-900">{{ currentCompany.location || '未设置' }}</p>
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-600 mb-1">公司简介</label>
                <div v-if="currentCompany.companyDescription" v-html="renderMarkdown(currentCompany.companyDescription)" class="prose max-w-none"></div>
                <p v-else class="text-gray-500 italic">未设置</p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-600 mb-1">企业文化</label>
                <div v-if="currentCompany.companyCulture" v-html="renderMarkdown(currentCompany.companyCulture)" class="prose max-w-none"></div>
                <p v-else class="text-gray-500 italic">未设置</p>
              </div>
            </div>

            <div v-else class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">公司名称 *</label>
                <input v-model="editCompanyData.companyName" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" required />
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">行业</label>
                  <input v-model="editCompanyData.industry" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">公司规模</label>
                  <select v-model="editCompanyData.companySize" class="w-full px-4 py-2 border border-gray-300 rounded-lg">
                    <option value="">请选择</option>
                    <option value="<50">小于50人</option>
                    <option value="50-200">50-200人</option>
                    <option value="200-1000">200-1000人</option>
                    <option value="1000-5000">1000-5000人</option>
                    <option value=">5000">5000人以上</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">地点</label>
                  <input v-model="editCompanyData.location" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" placeholder="例如: San Francisco, CA" />
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">公司简介 (支持 Markdown)</label>
                <textarea v-model="editCompanyData.companyDescription" rows="4" class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm"></textarea>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">企业文化 (支持 Markdown)</label>
                <textarea v-model="editCompanyData.companyCulture" rows="4" class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm"></textarea>
              </div>
            </div>

            <!-- 相关链接（独立管理区域，不在编辑模式中） -->
            <div class="border-t pt-6 mt-6">
              <div class="flex justify-between items-center mb-4">
                <label class="block text-sm font-medium text-gray-700">相关链接</label>
                <button
                  @click="showCreateLinkModal = true"
                  class="px-3 py-1 bg-blue-500 text-white text-sm rounded-lg hover:bg-blue-600 transition-all"
                >
                  + 添加链接
                </button>
              </div>
              <div v-if="currentCompany.links && currentCompany.links.length > 0" class="space-y-2">
                <div v-for="link in currentCompany.links" :key="link.id" class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div class="flex-1">
                    <span class="text-sm font-medium text-gray-700">{{ link.linkTitle }}:</span>
                    <a :href="link.linkUrl" target="_blank" class="text-blue-600 hover:underline text-sm ml-2">{{ link.linkUrl }}</a>
                    <p v-if="link.notes" class="text-xs text-gray-500 mt-1">{{ link.notes }}</p>
                  </div>
                  <div class="flex gap-2">
                    <button
                      @click="openLinkModal(link)"
                      class="text-blue-600 hover:text-blue-700 text-sm"
                      title="编辑"
                    >
                      编辑
                    </button>
                    <button
                      @click="deleteLink(link.id)"
                      class="text-red-600 hover:text-red-700 text-sm"
                      title="删除"
                    >
                      删除
                    </button>
                  </div>
                </div>
              </div>
              <p v-else class="text-gray-500 italic text-sm">暂无相关链接</p>
            </div>
          </div>

          <!-- Tab 2: 职位管理 - 左右两栏布局 -->
          <div v-if="activeTab === 'jobs'" class="flex gap-4 h-full">
            <!-- 左侧：职位列表 -->
            <div class="w-80 bg-white rounded-lg shadow p-4 flex flex-col">
              <div class="flex justify-between items-center mb-4">
                <h3 class="text-lg font-semibold text-gray-900">职位列表</h3>
                <button
                  @click="openJobModal()"
                  class="px-4 py-2.5 bg-gradient-to-r from-green-500 to-emerald-500 text-white text-sm rounded-lg hover:from-green-600 hover:to-emerald-600 transition-all font-semibold shadow-md"
                >
                  + 添加
                </button>
              </div>

              <div class="flex-1 overflow-y-auto space-y-2">
                <div
                  v-for="job in jobs"
                  :key="job.id"
                  @click="selectJob(job.id)"
                  :class="[
                    'p-3 rounded-lg cursor-pointer transition-all duration-200 border',
                    selectedJobId === job.id
                      ? 'bg-gradient-to-r from-green-50 to-emerald-50 border-green-500 shadow-md'
                      : 'bg-gray-50 border-gray-200 hover:border-green-300 hover:shadow-sm'
                  ]"
                >
                  <h4 class="font-semibold text-gray-900 text-sm">{{ job.positionName }}</h4>
                  <p class="text-xs text-gray-600 mt-1">{{ job.positionLevel || '级别未设置' }}</p>
                  <div class="flex items-center gap-2 mt-2">
                    <span :class="['px-2 py-0.5 rounded-full text-xs font-medium', getStatusColor(job.applicationStatus)]">
                      {{ job.applicationStatus || '未申请' }}
                    </span>
                  </div>
                </div>

                <div v-if="!jobs.length" class="text-center text-gray-500 py-8">
                  <p class="text-sm">暂无职位</p>
                  <p class="text-xs mt-1">点击上方按钮添加</p>
                </div>
              </div>
            </div>

            <!-- 右侧：职位详情 -->
            <div v-if="currentJob" class="flex-1 bg-white rounded-lg shadow flex flex-col">
              <!-- 职位标题栏 -->
              <div class="bg-gradient-to-r from-green-50 to-emerald-50 border-b border-green-200 p-4 flex items-center justify-between">
                <div>
                  <h2 class="text-xl font-bold text-gray-900">{{ currentJob.positionName }}</h2>
                  <p class="text-sm text-gray-600 mt-1">{{ currentJob.positionLevel || '级别未设置' }}</p>
                </div>
                <div class="flex gap-2">
                  <button
                    @click="openJobModal(currentJob)"
                    class="px-3 py-1 bg-blue-500 text-white text-sm rounded-lg hover:bg-blue-600"
                  >
                    编辑
                  </button>
                  <button
                    @click="deleteJob(currentJob.id)"
                    class="px-3 py-1 bg-red-500 text-white text-sm rounded-lg hover:bg-red-600"
                  >
                    删除
                  </button>
                </div>
              </div>

              <!-- Sub-Tab导航 -->
              <div class="border-b border-gray-200 bg-gray-50">
                <div class="flex px-4">
                  <button
                    v-for="tab in jobDetailTabs"
                    :key="tab.id"
                    @click="activeJobDetailTab = tab.id"
                    :class="[
                      'px-4 py-3 text-sm font-medium transition-all duration-200',
                      activeJobDetailTab === tab.id
                        ? 'text-green-600 border-b-2 border-green-600 bg-white'
                        : 'text-gray-600 hover:text-green-600 hover:bg-gray-100'
                    ]"
                  >
                    {{ tab.name }}
                  </button>
                </div>
              </div>

              <!-- Sub-Tab内容 -->
              <div class="flex-1 overflow-y-auto p-6">
                <!-- Tab 1: JD (Job Description) -->
                <div v-if="activeJobDetailTab === 'jd'" class="space-y-6">
                  <div class="grid grid-cols-2 gap-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">职位名称</label>
                      <p class="text-gray-900">{{ currentJob.positionName }}</p>
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">职位级别</label>
                      <p class="text-gray-900">{{ currentJob.positionLevel || '未设置' }}</p>
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">发布时间</label>
                      <p class="text-gray-900">{{ currentJob.postedDate ? formatDate(currentJob.postedDate) : '未设置' }}</p>
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">申请状态</label>
                      <span :class="['px-3 py-1 rounded-full text-sm font-medium inline-block', getStatusColor(currentJob.applicationStatus)]">
                        {{ currentJob.applicationStatus || '未申请' }}
                      </span>
                    </div>
                  </div>

                  <div v-if="currentJob.jobUrl">
                    <label class="block text-sm font-medium text-gray-700 mb-1">职位链接</label>
                    <a :href="currentJob.jobUrl" target="_blank" class="text-blue-600 hover:underline break-all">
                      {{ currentJob.jobUrl }}
                    </a>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">职位招聘状态</label>
                    <span :class="['px-3 py-1 rounded-full text-sm font-medium inline-block', getJobStatusColor(currentJob.jobStatus)]">
                      {{ getJobStatusLabel(currentJob.jobStatus) }}
                    </span>
                  </div>

                  <div class="border-t pt-6">
                    <label class="block text-lg font-semibold text-gray-900 mb-3">📋 Qualifications（技能要求）</label>
                    <div v-if="currentJob.qualifications" v-html="renderMarkdown(currentJob.qualifications)" class="prose max-w-none"></div>
                    <p v-else class="text-gray-500 italic">未设置</p>
                  </div>

                  <div class="border-t pt-6">
                    <label class="block text-lg font-semibold text-gray-900 mb-3">💼 Responsibilities（岗位职责）</label>
                    <div v-if="currentJob.responsibilities" v-html="renderMarkdown(currentJob.responsibilities)" class="prose max-w-none"></div>
                    <p v-else class="text-gray-500 italic">未设置</p>
                  </div>

                  <div v-if="currentJob.notes" class="border-t pt-6">
                    <label class="block text-sm font-medium text-gray-700 mb-1">备注</label>
                    <div v-html="renderMarkdown(currentJob.notes)" class="prose max-w-none text-sm"></div>
                  </div>
                </div>

                <!-- Tab 2: 面试流程 -->
                <div v-if="activeJobDetailTab === 'interview'" class="space-y-6">
                  <!-- 面试阶段部分 -->
                  <div>
                    <div class="flex justify-between items-center mb-4">
                      <h3 class="text-lg font-semibold text-gray-900">📝 面试阶段</h3>
                      <button
                        @click="showCreateStageModal = true"
                        class="px-3 py-1 bg-green-500 text-white text-sm rounded-lg hover:bg-green-600"
                      >
                        + 添加阶段
                      </button>
                    </div>

                    <div v-if="interviewStages.length > 0" class="space-y-2">
                      <div
                        v-for="stage in interviewStages"
                        :key="stage.id"
                        class="p-4 border border-gray-200 rounded-lg hover:border-green-300 hover:shadow-sm transition-all"
                      >
                        <div class="flex items-start justify-between">
                          <div class="flex-1">
                            <div class="flex items-center gap-3">
                              <span class="text-sm font-medium text-gray-500">阶段 {{ stage.stageOrder }}</span>
                              <h4 class="text-lg font-semibold text-gray-900">{{ stage.stageName }}</h4>
                            </div>
                            <div v-if="stage.preparationNotes" class="mt-2 text-sm text-gray-600">
                              <strong>准备要点：</strong>
                              <div v-html="renderMarkdown(stage.preparationNotes)" class="prose max-w-none mt-1"></div>
                            </div>
                            <div v-if="stage.skillIds && stage.skillIds.length > 0" class="mt-2">
                              <span class="text-xs text-gray-500">关联技能ID: {{ stage.skillIds.join(', ') }}</span>
                            </div>
                          </div>
                          <div class="flex gap-2">
                            <button
                              @click="openStageModal(stage)"
                              class="text-blue-600 hover:text-blue-700 text-sm"
                              title="编辑"
                            >
                              编辑
                            </button>
                            <button
                              @click="deleteStage(stage.id)"
                              class="text-red-600 hover:text-red-700 text-sm"
                              title="删除"
                            >
                              删除
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div v-else class="text-center text-gray-500 py-8 bg-gray-50 rounded-lg">
                      <p class="text-sm">暂无面试阶段</p>
                      <p class="text-xs mt-1">点击上方按钮添加（如 Phone Screen、Technical、Behavioral 等）</p>
                    </div>
                  </div>

                </div>

                <!-- Tab 3: 定制简历 -->
                <div v-if="activeJobDetailTab === 'resume'" class="space-y-6">
                  <!-- Loading State -->
                  <div v-if="loadingCustomizedResume" class="bg-white rounded-lg shadow p-6">
                    <div class="text-center py-12">
                      <svg class="animate-spin h-8 w-8 mx-auto mb-4 text-indigo-600" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      <p class="text-sm text-gray-500">加载定制简历...</p>
                    </div>
                  </div>

                  <!-- No Customized Resume Yet -->
                  <div v-else-if="!customizedResume" class="bg-white rounded-lg shadow p-6">
                    <div class="text-center py-12">
                      <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      </svg>
                      <h3 class="text-lg font-semibold text-gray-900 mb-2">还没有定制简历</h3>
                      <p class="text-sm text-gray-500 mb-6">为此职位创建定制简历，根据AI分析建议优化您的简历</p>
                      <button
                        @click="cloneResumeForJob"
                        class="px-6 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-lg hover:from-indigo-700 hover:to-purple-700 font-medium shadow-md hover:shadow-lg transition-all"
                      >
                        创建定制简历
                      </button>
                      <p class="text-xs text-gray-400 mt-3">将克隆您的默认简历，并可根据建议进行优化</p>
                    </div>
                  </div>

                  <!-- Customized Resume Exists -->
                  <div v-else class="space-y-6">
                    <!-- Resume Summary -->
                    <div class="bg-white rounded-lg shadow p-6">
                      <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-semibold text-gray-900 flex items-center gap-2">
                          <svg class="w-5 h-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                          </svg>
                          定制简历 - {{ customizedResume.versionName }}
                        </h3>
                        <button
                          @click="editCustomizedResume"
                          class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 font-medium transition-all"
                        >
                          编辑简历
                        </button>
                      </div>

                      <div class="grid grid-cols-2 gap-4 mb-6">
                        <div>
                          <label class="block text-sm font-medium text-gray-600 mb-1">邮箱</label>
                          <p class="text-sm text-gray-900">{{ customizedResume.email || '未设置' }}</p>
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-600 mb-1">电话</label>
                          <p class="text-sm text-gray-900">{{ customizedResume.phone || '未设置' }}</p>
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-600 mb-1">GitHub</label>
                          <a v-if="customizedResume.githubUrl" :href="customizedResume.githubUrl" target="_blank" class="text-sm text-blue-600 hover:underline">
                            {{ customizedResume.githubUrl }}
                          </a>
                          <p v-else class="text-sm text-gray-500">未设置</p>
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-600 mb-1">LinkedIn</label>
                          <a v-if="customizedResume.linkedinUrl" :href="customizedResume.linkedinUrl" target="_blank" class="text-sm text-blue-600 hover:underline">
                            {{ customizedResume.linkedinUrl }}
                          </a>
                          <p v-else class="text-sm text-gray-500">未设置</p>
                        </div>
                      </div>

                      <div v-if="customizedResume.about" class="mb-4">
                        <label class="block text-sm font-medium text-gray-600 mb-1">关于我</label>
                        <p class="text-sm text-gray-700 whitespace-pre-wrap">{{ customizedResume.about }}</p>
                      </div>

                      <div v-if="customizedResume.careerObjective" class="mb-4">
                        <label class="block text-sm font-medium text-gray-600 mb-1">职业目标</label>
                        <p class="text-sm text-gray-700 whitespace-pre-wrap">{{ customizedResume.careerObjective }}</p>
                      </div>
                    </div>

                    <!-- Work Experiences -->
                    <div v-if="customizedResume.experiences && customizedResume.experiences.length > 0" class="bg-white rounded-lg shadow p-6">
                      <h4 class="text-sm font-semibold text-gray-900 mb-3">💼 工作经历 ({{ customizedResume.experiences.length }})</h4>
                      <div class="space-y-3">
                        <div v-for="exp in customizedResume.experiences" :key="exp.id" class="border-l-2 border-indigo-600 pl-4">
                          <p class="font-medium text-gray-900">{{ exp.position }} @ {{ exp.companyName }}</p>
                          <p class="text-xs text-gray-500">{{ formatDate(exp.startDate) }} - {{ exp.isCurrent ? '至今' : formatDate(exp.endDate) }}</p>
                        </div>
                      </div>
                    </div>

                    <!-- Skills -->
                    <div v-if="customizedResume.skills && customizedResume.skills.length > 0" class="bg-white rounded-lg shadow p-6">
                      <h4 class="text-sm font-semibold text-gray-900 mb-3">🎯 技能 ({{ customizedResume.skills.length }})</h4>
                      <div class="flex flex-wrap gap-2">
                        <span
                          v-for="skill in customizedResume.skills"
                          :key="skill.id"
                          class="px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-sm"
                        >
                          {{ skill.skillName }}
                        </span>
                      </div>
                    </div>

                    <!-- Education -->
                    <div v-if="customizedResume.education && customizedResume.education.length > 0" class="bg-white rounded-lg shadow p-6">
                      <h4 class="text-sm font-semibold text-gray-900 mb-3">🎓 教育背景 ({{ customizedResume.education.length }})</h4>
                      <div class="space-y-3">
                        <div v-for="edu in customizedResume.education" :key="edu.id" class="border-l-2 border-purple-600 pl-4">
                          <p class="font-medium text-gray-900">{{ edu.degree }} @ {{ edu.schoolName }}</p>
                          <p class="text-xs text-gray-600">{{ edu.major }}</p>
                          <p class="text-xs text-gray-500">{{ formatDate(edu.startDate) }} - {{ formatDate(edu.endDate) }}</p>
                        </div>
                      </div>
                    </div>

                  </div>
                </div>

                <!-- Tab 4: Recruiter -->
                <div v-if="activeJobDetailTab === 'recruiter'" class="space-y-4">
                  <div class="flex justify-between items-center mb-4">
                    <h3 class="text-lg font-semibold text-gray-900">👤 招聘人员</h3>
                    <button
                      @click="addRecruiterContact"
                      class="px-3 py-1 bg-indigo-500 text-white text-sm rounded-lg hover:bg-indigo-600"
                    >
                      + 添加Recruiter
                    </button>
                  </div>

                  <div v-if="recruiterContacts.length > 0" class="grid grid-cols-2 gap-4">
                    <div
                      v-for="contact in recruiterContacts"
                      :key="contact.id"
                      class="p-4 border border-gray-200 rounded-lg hover:border-indigo-300 hover:shadow-md transition-all"
                    >
                      <div class="flex items-start justify-between mb-3">
                        <div class="flex-1">
                          <h4 class="font-semibold text-gray-900">{{ contact.referralName }}</h4>
                          <p class="text-sm text-gray-600 mt-1">{{ contact.position || 'Recruiter' }}</p>
                        </div>
                        <div class="flex gap-2">
                          <button
                            @click="openContactModal(contact)"
                            class="text-blue-600 hover:text-blue-700 text-sm"
                          >
                            编辑
                          </button>
                          <button
                            @click="deleteContact(contact.id)"
                            class="text-red-600 hover:text-red-700 text-sm"
                          >
                            删除
                          </button>
                        </div>
                      </div>

                      <!-- 联系方式 -->
                      <div class="space-y-1 mb-2">
                        <div v-if="contact.email" class="flex items-center gap-1 text-xs text-gray-700">
                          <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                          </svg>
                          <a :href="`mailto:${contact.email}`" class="hover:text-blue-600 truncate">{{ contact.email }}</a>
                        </div>
                        <div v-if="contact.phone" class="flex items-center gap-1 text-xs text-gray-700">
                          <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                          </svg>
                          <a :href="`tel:${contact.phone}`" class="hover:text-blue-600">{{ contact.phone }}</a>
                        </div>
                        <div v-if="contact.linkedinUrl" class="flex items-center gap-1 text-xs">
                          <svg class="w-4 h-4 text-blue-600" fill="currentColor" viewBox="0 0 24 24">
                            <path d="M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.762 0 5-2.239 5-5v-14c0-2.761-2.238-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z"/>
                          </svg>
                          <a :href="contact.linkedinUrl" target="_blank" class="text-blue-600 hover:underline">LinkedIn</a>
                        </div>
                      </div>

                      <!-- 沟通记录 -->
                      <div v-if="contact.notes" class="text-xs text-gray-600 border-t pt-2 mt-2">
                        <strong class="text-gray-700">备注:</strong>
                        <p class="line-clamp-2 mt-1">{{ contact.notes }}</p>
                      </div>
                    </div>
                  </div>

                  <div v-else class="text-center text-gray-500 py-12 bg-gray-50 rounded-lg">
                    <svg class="w-12 h-12 mx-auto mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    <p class="text-sm">暂无Recruiter信息</p>
                    <p class="text-xs mt-1">点击上方按钮添加招聘人员联系方式</p>
                  </div>

                  <!-- Recruiter Insights Section -->
                  <div class="mt-6 bg-white rounded-lg shadow p-6">
                    <div class="flex items-center justify-between mb-4">
                      <h3 class="text-lg font-semibold text-gray-900 flex items-center gap-2">
                        <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        💡 Recruiter Insights
                      </h3>
                      <button
                        @click="openRecruiterInsightsModal"
                        class="px-3 py-1 bg-purple-500 text-white text-sm rounded-lg hover:bg-purple-600"
                      >
                        {{ selectedJob?.recruiterInsights ? '编辑' : '添加' }}
                      </button>
                    </div>

                    <div v-if="selectedJob?.recruiterInsights" class="space-y-4">
                      <div v-if="selectedJob.recruiterInsights.teamSize">
                        <label class="block text-sm font-medium text-gray-600 mb-1">团队规模</label>
                        <p class="text-gray-900">{{ selectedJob.recruiterInsights.teamSize }}</p>
                      </div>

                      <div v-if="selectedJob.recruiterInsights.teamCulture">
                        <label class="block text-sm font-medium text-gray-600 mb-1">团队文化</label>
                        <p class="text-gray-900 text-sm">{{ selectedJob.recruiterInsights.teamCulture }}</p>
                      </div>

                      <div v-if="selectedJob.recruiterInsights.techStackPreference && selectedJob.recruiterInsights.techStackPreference.length > 0">
                        <label class="block text-sm font-medium text-gray-600 mb-1">技术栈偏好</label>
                        <div class="flex flex-wrap gap-2">
                          <span
                            v-for="tech in selectedJob.recruiterInsights.techStackPreference"
                            :key="tech"
                            class="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs font-medium"
                          >
                            {{ tech }}
                          </span>
                        </div>
                      </div>

                      <div v-if="selectedJob.recruiterInsights.interviewFocus">
                        <label class="block text-sm font-medium text-gray-600 mb-1">面试重点</label>
                        <p class="text-gray-900 text-sm">{{ selectedJob.recruiterInsights.interviewFocus }}</p>
                      </div>

                      <div v-if="selectedJob.recruiterInsights.processTips">
                        <label class="block text-sm font-medium text-gray-600 mb-1">流程Tips</label>
                        <div class="bg-yellow-50 border-l-4 border-yellow-400 p-3 rounded">
                          <p class="text-sm text-yellow-800">{{ selectedJob.recruiterInsights.processTips }}</p>
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

                <!-- Tab 5: AI 简历分析 -->
                <div v-if="activeJobDetailTab === 'ai-analysis'" class="space-y-6">
                  <!-- Prompt生成区域（紧凑型） -->
                  <div v-if="aiPromptData" class="bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg p-4 border border-purple-200">
                    <div class="mb-2">
                      <label class="block text-sm font-semibold text-gray-700 mb-2">复制以下Prompt到Claude Code执行分析</label>
                      <div class="relative">
                        <pre class="bg-white p-3 rounded border border-gray-300 text-xs overflow-x-auto whitespace-pre-wrap max-h-32">{{ aiPromptData.prompt }}</pre>
                        <button
                          @click="copyToClipboard(aiPromptData.prompt)"
                          class="absolute top-2 right-2 px-3 py-1 bg-indigo-600 text-white text-xs rounded hover:bg-indigo-700"
                        >
                          复制
                        </button>
                      </div>
                    </div>
                  </div>

                  <!-- 已保存的AI分析结果 -->
                  <div class="bg-white rounded-lg shadow p-6">
                    <div class="flex items-center justify-between mb-4">
                      <h3 class="text-lg font-semibold text-gray-900 flex items-center gap-2">
                        <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        AI分析结果
                      </h3>
                      <button
                        @click="generateAIPrompt"
                        class="px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white text-sm rounded-lg hover:from-purple-700 hover:to-indigo-700 font-medium transition-all shadow-md hover:shadow-lg flex items-center gap-2"
                      >
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                        </svg>
                        生成AI分析Prompt
                      </button>
                    </div>

                    <!-- 分析结果列表 -->
                    <div v-if="savedAnalyses && savedAnalyses.length > 0" class="space-y-6">
                      <div v-for="analysis in savedAnalyses" :key="analysis.id" class="border border-gray-200 rounded-lg p-5">
                        <!-- 头部：分数和时间 -->
                        <div class="flex items-center justify-between mb-4 pb-4 border-b">
                          <div class="flex items-center gap-4">
                            <div class="text-4xl font-bold text-indigo-600">{{ analysis.metadata.overallScore }}</div>
                            <div>
                              <div class="text-xl font-semibold text-gray-900">{{ analysis.metadata.recommendation }}</div>
                              <div class="text-sm text-gray-500">分析于 {{ new Date(analysis.createdAt).toLocaleString('zh-CN') }}</div>
                            </div>
                          </div>
                        </div>

                        <!-- 各维度得分 -->
                        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                          <div class="text-center p-3 bg-blue-50 rounded-lg">
                            <div class="text-xs text-gray-600 mb-1">教育匹配</div>
                            <div class="text-2xl font-bold text-blue-600">{{ parseAnalysisResult(analysis).educationMatch?.score || '-' }}</div>
                          </div>
                          <div class="text-center p-3 bg-green-50 rounded-lg">
                            <div class="text-xs text-gray-600 mb-1">经验匹配</div>
                            <div class="text-2xl font-bold text-green-600">{{ parseAnalysisResult(analysis).experienceMatch?.score || '-' }}</div>
                          </div>
                          <div class="text-center p-3 bg-purple-50 rounded-lg">
                            <div class="text-xs text-gray-600 mb-1">技能匹配</div>
                            <div class="text-2xl font-bold text-purple-600">{{ parseAnalysisResult(analysis).skillMatch?.score || '-' }}</div>
                          </div>
                          <div class="text-center p-3 bg-pink-50 rounded-lg">
                            <div class="text-xs text-gray-600 mb-1">软技能</div>
                            <div class="text-2xl font-bold text-pink-600">{{ parseAnalysisResult(analysis).softSkillMatch?.score || '-' }}</div>
                          </div>
                        </div>

                        <!-- 核心优势 -->
                        <div class="mb-4">
                          <h4 class="text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                            <svg class="w-4 h-4 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                            </svg>
                            核心优势
                          </h4>
                          <ul class="space-y-2">
                            <li v-for="(strength, idx) in parseAnalysisResult(analysis).strengths" :key="idx" class="flex items-start gap-2 text-sm text-gray-700 bg-green-50 p-2 rounded">
                              <span class="text-green-600 font-bold flex-shrink-0">✓</span>
                              <span>{{ strength }}</span>
                            </li>
                          </ul>
                        </div>

                        <!-- 改进建议 -->
                        <div>
                          <h4 class="text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                            <svg class="w-4 h-4 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                              <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
                            </svg>
                            改进建议
                          </h4>
                          <ul class="space-y-2">
                            <li v-for="(improvement, idx) in parseAnalysisResult(analysis).improvements" :key="idx" class="flex items-start gap-2 text-sm text-gray-700 bg-blue-50 p-2 rounded">
                              <span class="text-blue-600 font-bold flex-shrink-0">💡</span>
                              <span>{{ improvement }}</span>
                            </li>
                          </ul>
                        </div>
                      </div>
                    </div>

                    <!-- 无分析结果时的提示 -->
                    <div v-else class="text-center py-12 text-gray-500">
                      <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      </svg>
                      <p class="text-lg mb-2">暂无AI分析结果</p>
                      <p class="text-sm">点击右上角"生成AI分析Prompt"按钮开始分析</p>
                    </div>
                  </div>
                </div>

                <!-- Tab 6: 面试准备 -->
                <div v-if="activeJobDetailTab === 'interview-prep'" class="space-y-6">
                  <!-- Prompt生成区域（紧凑型） -->
                  <div v-if="interviewPrepPromptData" class="bg-gradient-to-r from-green-50 to-teal-50 rounded-lg p-4 border border-teal-200">
                    <div class="mb-2">
                      <label class="block text-sm font-semibold text-gray-700 mb-2">复制以下Prompt到Claude Code执行面试准备分析</label>
                      <div class="relative">
                        <pre class="bg-white p-3 rounded border border-gray-300 text-xs overflow-x-auto whitespace-pre-wrap max-h-32">{{ interviewPrepPromptData.prompt }}</pre>
                        <button
                          @click="copyToClipboard(interviewPrepPromptData.prompt)"
                          class="absolute top-2 right-2 px-3 py-1 bg-teal-600 text-white text-xs rounded hover:bg-teal-700"
                        >
                          复制
                        </button>
                      </div>
                    </div>
                  </div>

                  <!-- 面试准备计划结果 -->
                  <div class="bg-white rounded-lg shadow p-6">
                    <div class="flex items-center justify-between mb-4">
                      <h3 class="text-lg font-semibold text-gray-900 flex items-center gap-2">
                        <svg class="w-5 h-5 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                        </svg>
                        面试准备计划
                      </h3>
                      <button
                        @click="generateInterviewPrepPrompt"
                        class="px-4 py-2 bg-gradient-to-r from-teal-600 to-green-600 text-white text-sm rounded-lg hover:from-teal-700 hover:to-green-700 font-medium transition-all shadow-md hover:shadow-lg flex items-center gap-2"
                      >
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                        </svg>
                        生成面试准备Prompt
                      </button>
                    </div>

                    <!-- 面试阶段列表 -->
                    <div v-if="interviewStages && interviewStages.length > 0" class="space-y-4">
                      <div v-for="stage in interviewStages" :key="stage.id" class="border border-gray-200 rounded-lg p-5">
                        <!-- 头部：阶段名称 -->
                        <div class="flex items-center justify-between mb-4 pb-4 border-b">
                          <div>
                            <div class="text-xl font-semibold text-gray-900">{{ stage.stageName }}</div>
                            <div class="text-sm text-gray-500 mt-1">阶段 {{ stage.stageOrder }}</div>
                          </div>
                          <div class="text-sm text-gray-600">
                            {{ stage.skillIds ? stage.skillIds.length : 0 }} 个技能 ·
                            {{ stage.focusAreaIds ? stage.focusAreaIds.length : 0 }} 个Focus Area
                          </div>
                        </div>

                        <!-- 准备笔记 -->
                        <div v-if="stage.preparationNotes" class="mb-4 p-4 bg-teal-50 rounded-lg">
                          <h4 class="text-sm font-semibold text-gray-900 mb-2">准备笔记</h4>
                          <div class="text-sm text-gray-700 whitespace-pre-wrap">{{ stage.preparationNotes }}</div>
                        </div>

                        <!-- 准备清单 -->
                        <div v-if="stage.checklistItems && stage.checklistItems.length > 0">
                          <h4 class="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
                            <svg class="w-4 h-4 text-teal-600" fill="currentColor" viewBox="0 0 20 20">
                              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                            </svg>
                            准备清单 ({{ stage.checklistItems.length }} 项)
                          </h4>
                          <div class="space-y-2">
                            <div
                              v-for="item in stage.checklistItems"
                              :key="item.id"
                              :class="[
                                'p-3 rounded-lg border-l-4',
                                item.isPriority ? 'bg-yellow-50 border-yellow-500' : 'bg-gray-50 border-gray-300'
                              ]"
                            >
                              <div class="flex items-start gap-3">
                                <div class="flex-shrink-0 mt-0.5">
                                  <span v-if="item.isPriority" class="inline-block px-2 py-0.5 bg-yellow-500 text-white text-xs rounded font-medium">高优先级</span>
                                  <span v-else class="inline-block px-2 py-0.5 bg-gray-400 text-white text-xs rounded">普通</span>
                                </div>
                                <div class="flex-1">
                                  <div class="font-medium text-gray-900 mb-1">{{ item.checklistItem }}</div>
                                  <div v-if="item.category" class="text-xs text-gray-600 mb-2">
                                    分类: <span class="px-2 py-0.5 bg-gray-200 rounded">{{ item.category }}</span>
                                  </div>
                                  <div v-if="item.notes" class="text-sm text-gray-700 whitespace-pre-wrap bg-white p-2 rounded border border-gray-200">{{ item.notes }}</div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- 无准备计划时的提示 -->
                    <div v-else class="text-center py-12 text-gray-500">
                      <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                      </svg>
                      <p class="text-lg mb-2">暂无面试准备计划</p>
                      <p class="text-sm">点击右上角"生成面试准备Prompt"按钮开始创建准备计划</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 右侧：未选择职位时的占位符 -->
            <div v-else class="flex-1 bg-white rounded-lg shadow flex items-center justify-center">
              <div class="text-center text-gray-500">
                <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <p class="text-lg">请选择一个职位查看详情</p>
              </div>
            </div>
          </div>

          <!-- Tab 3: 人脉管理 -->
          <div v-if="activeTab === 'contacts'" class="bg-white rounded-lg shadow p-6">
            <div class="flex justify-between items-center mb-4">
              <h3 class="text-lg font-semibold text-gray-900">人脉列表</h3>
              <button
                @click="showCreateContactModal = true"
                class="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg hover:from-purple-600 hover:to-pink-600 hover:shadow-lg transition-all font-semibold shadow-md"
              >
                + 添加人脉
              </button>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div
                v-for="contact in contacts"
                :key="contact.id"
                class="p-4 border border-gray-200 rounded-lg hover:border-purple-300 hover:shadow-md transition-all"
              >
                <div class="flex items-start justify-between mb-3">
                  <div class="flex-1">
                    <div class="flex items-center gap-2">
                      <h4 class="font-semibold text-gray-900">{{ contact.referralName }}</h4>
                      <span v-if="contact.referralStatus" :class="['px-2 py-0.5 rounded-full text-xs font-medium', getReferralStatusColor(contact.referralStatus)]">
                        {{ contact.referralStatus }}
                      </span>
                    </div>
                    <p class="text-sm text-gray-600 mt-1">{{ contact.position || '职位未知' }}</p>
                    <p class="text-sm text-gray-500 mt-1">
                      <span class="font-medium">关系:</span> {{ contact.relationship || '未知' }}
                    </p>
                  </div>
                  <div class="flex gap-2">
                    <button
                      @click="openContactModal(contact)"
                      class="text-blue-600 hover:text-blue-700"
                      title="编辑"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button
                      @click="deleteContact(contact.id)"
                      class="text-red-600 hover:text-red-700"
                      title="删除"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                </div>

                <!-- 联系方式 -->
                <div class="space-y-1 mb-2">
                  <div v-if="contact.phone" class="flex items-center gap-1 text-xs text-gray-700">
                    <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                    </svg>
                    <a :href="`tel:${contact.phone}`" class="hover:text-blue-600">{{ contact.phone }}</a>
                  </div>
                  <div v-if="contact.email" class="flex items-center gap-1 text-xs text-gray-700">
                    <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                    </svg>
                    <a :href="`mailto:${contact.email}`" class="hover:text-blue-600 truncate">{{ contact.email }}</a>
                  </div>
                  <div v-if="contact.linkedinUrl" class="flex items-center gap-1 text-xs">
                    <svg class="w-4 h-4 text-blue-600" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.762 0 5-2.239 5-5v-14c0-2.761-2.238-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z"/>
                    </svg>
                    <a :href="contact.linkedinUrl" target="_blank" class="text-blue-600 hover:underline">LinkedIn</a>
                  </div>
                </div>

                <!-- 内推日期 -->
                <div v-if="contact.requestDate || contact.submissionDate" class="text-xs text-gray-600 mb-2">
                  <span v-if="contact.requestDate" class="mr-3">
                    <span class="font-medium">请求日期:</span> {{ formatDate(contact.requestDate) }}
                  </span>
                  <span v-if="contact.submissionDate">
                    <span class="font-medium">提交日期:</span> {{ formatDate(contact.submissionDate) }}
                  </span>
                </div>

                <!-- 内推结果 -->
                <div v-if="contact.referralResult" class="text-xs mb-2">
                  <span class="font-medium text-gray-700">结果:</span>
                  <span :class="getReferralResultColor(contact.referralResult)">
                    {{ contact.referralResult }}
                  </span>
                </div>

                <!-- 备注预览 -->
                <div v-if="contact.notes" class="text-xs text-gray-600 border-t pt-2 mt-2">
                  <p class="line-clamp-2">{{ contact.notes }}</p>
                </div>
              </div>

              <div v-if="!contacts.length" class="col-span-2 text-center text-gray-500 py-8">
                暂无人脉
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="flex-1 flex items-center justify-center">
        <div class="text-center text-gray-500">
          <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
          </svg>
          <p class="text-lg">请选择或创建一个公司</p>
        </div>
      </div>
    </div>

    <!-- 创建公司 Modal -->
    <div v-if="showCreateCompanyModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[500px]">
        <h3 class="text-lg font-semibold mb-4">创建公司</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">公司名称 *</label>
            <input v-model="companyFormData.companyName" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" required />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">行业</label>
            <input v-model="companyFormData.industry" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" placeholder="例如: 科技、金融" />
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button @click="showCreateCompanyModal = false" class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">取消</button>
          <button @click="createCompany" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">创建</button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑职位 Modal -->
    <div v-if="showCreateJobModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[700px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">{{ editingJob?.id ? '编辑职位' : '创建职位' }}</h3>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">职位名称 *</label>
              <input v-model="jobFormData.positionName" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" required />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">职位级别</label>
              <select v-model="jobFormData.positionLevel" class="w-full px-4 py-2 border border-gray-300 rounded-lg">
                <option value="">请选择</option>
                <option value="Junior">Junior</option>
                <option value="Mid">Mid</option>
                <option value="Senior">Senior</option>
                <option value="Staff">Staff</option>
                <option value="Principal">Principal</option>
              </select>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">职位链接</label>
            <input v-model="jobFormData.jobUrl" type="url" class="w-full px-4 py-2 border border-gray-300 rounded-lg" placeholder="https://" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">职位招聘状态</label>
            <select v-model="jobFormData.jobStatus" class="w-full px-4 py-2 border border-gray-300 rounded-lg">
              <option value="Open">开放招聘 (Open)</option>
              <option value="ActivelyHiring">积极招聘 (Actively Hiring)</option>
              <option value="Closed">已关闭 (Closed)</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">职位要求 (支持 Markdown)</label>
            <textarea v-model="jobFormData.qualifications" rows="4" class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm"></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">岗位职责 (支持 Markdown)</label>
            <textarea v-model="jobFormData.responsibilities" rows="4" class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm"></textarea>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">申请状态</label>
              <select v-model="jobFormData.applicationStatus" class="w-full px-4 py-2 border border-gray-300 rounded-lg">
                <option value="未申请">未申请</option>
                <option value="已投递">已投递</option>
                <option value="筛选中">筛选中</option>
                <option value="面试中">面试中</option>
                <option value="Offer">Offer</option>
                <option value="已拒绝">已拒绝</option>
                <option value="已撤回">已撤回</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">申请日期</label>
              <input v-model="jobFormData.appliedDate" type="date" class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button @click="showCreateJobModal = false" class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">取消</button>
          <button @click="saveJob" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">保存</button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑人脉 Modal -->
    <div v-if="showCreateContactModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[600px]">
        <h3 class="text-lg font-semibold mb-4">{{ editingContact?.id ? '编辑人脉' : '添加人脉' }}</h3>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">姓名 *</label>
              <input v-model="contactFormData.referralName" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" required />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">职位</label>
              <input v-model="contactFormData.position" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">关系类型</label>
            <select v-model="contactFormData.relationship" class="w-full px-4 py-2 border border-gray-300 rounded-lg">
              <option value="">请选择</option>
              <option value="Recruiter">Recruiter</option>
              <option value="Referral">Referral (内推人)</option>
              <option value="Colleague">Colleague (前同事)</option>
              <option value="Friend">Friend (朋友)</option>
              <option value="Other">Other (其他)</option>
            </select>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
              <input v-model="contactFormData.email" type="email" class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">电话</label>
              <input v-model="contactFormData.phone" type="tel" class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">LinkedIn URL</label>
            <input v-model="contactFormData.linkedinUrl" type="url" class="w-full px-4 py-2 border border-gray-300 rounded-lg" placeholder="https://linkedin.com/in/..." />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">备注 (支持 Markdown)</label>
            <textarea v-model="contactFormData.notes" rows="3" class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm"></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button @click="showCreateContactModal = false" class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">取消</button>
          <button @click="saveContact" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">保存</button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑面试阶段 Modal -->
    <div v-if="showCreateStageModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
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
              <div v-for="skill in allSkills" :key="skill.id" class="flex items-center gap-2 mb-2">
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
              <div v-if="allSkills.length === 0" class="text-sm text-gray-500 text-center py-2">
                加载中...
              </div>
            </div>
          </div>

          <!-- Focus Area Selection (only show if at least one skill is selected) -->
          <div v-if="stageFormData.skillIds && stageFormData.skillIds.length > 0">
            <label class="block text-sm font-medium text-gray-700 mb-2">关联Focus Area</label>
            <div class="border border-gray-300 rounded-lg p-3 max-h-64 overflow-y-auto">
              <div v-for="skill in selectedSkillsWithFocusAreas" :key="skill.id" class="mb-4 last:mb-0">
                <div class="text-sm font-medium text-gray-900 mb-2 bg-gray-100 px-2 py-1 rounded">
                  {{ skill.name }}
                </div>
                <div v-for="focusArea in skill.focusAreas" :key="focusArea.id" class="flex items-center gap-2 ml-4 mb-2">
                  <input
                    type="checkbox"
                    :id="`focus-${focusArea.id}`"
                    :value="focusArea.id"
                    v-model="stageFormData.focusAreaIds"
                    class="rounded border-gray-300"
                  />
                  <label :for="`focus-${focusArea.id}`" class="text-sm text-gray-600 cursor-pointer">
                    {{ focusArea.name }}
                    <span v-if="focusArea.category" class="text-xs text-gray-400 ml-1">({{ focusArea.category }})</span>
                  </label>
                </div>
                <div v-if="!skill.focusAreas || skill.focusAreas.length === 0" class="text-xs text-gray-400 ml-4">
                  该技能暂无Focus Area
                </div>
              </div>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">准备要点 (支持 Markdown)</label>
            <textarea v-model="stageFormData.preparationNotes" rows="6" class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm" placeholder="## 准备重点&#10;- 算法题：Array, Hash Table&#10;- 系统设计：Database Schema"></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button @click="showCreateStageModal = false" class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">取消</button>
          <button @click="saveStage" class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700">保存</button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑链接 Modal -->
    <div v-if="showCreateLinkModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[500px]">
        <h3 class="text-lg font-semibold mb-4">{{ editingLink?.id ? '编辑链接' : '添加链接' }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">链接标题 *</label>
            <input v-model="linkFormData.linkTitle" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg" placeholder="例如: 官网、招聘页面" required />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">链接URL *</label>
            <input v-model="linkFormData.linkUrl" type="url" class="w-full px-4 py-2 border border-gray-300 rounded-lg" placeholder="https://" required />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">备注</label>
            <textarea v-model="linkFormData.notes" rows="2" class="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm"></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">排序</label>
            <input v-model.number="linkFormData.sortOrder" type="number" class="w-full px-4 py-2 border border-gray-300 rounded-lg" min="0" />
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button @click="showCreateLinkModal = false" class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">取消</button>
          <button @click="saveLink" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">保存</button>
        </div>
      </div>
    </div>

    <!-- Recruiter Insights Modal -->
    <div v-if="showRecruiterInsightsModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[700px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">{{ selectedJob?.recruiterInsights ? '编辑' : '添加' }} Recruiter Insights</h3>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { companyApi } from '@/api/companyApi'
import { jobApplicationApi } from '@/api/jobApplicationApi'
import { referralApi } from '@/api/referralApi'
import { interviewStageApi } from '@/api/interviewStageApi'
import { interviewChecklistApi } from '@/api/interviewChecklistApi'
import { getSkills } from '@/api/skillApi'
import { getFocusAreasBySkillId } from '@/api/focusAreaApi'
import { resumeAnalysisApi } from '@/api/resumeAnalysisApi'
import { aiJobAnalysisApi } from '@/api/aiJobAnalysisApi'
import { resumeApi } from '@/api/resumeApi'
import MarkdownIt from 'markdown-it'

const route = useRoute()
const router = useRouter()
const md = new MarkdownIt()

const companies = ref([])
const jobs = ref([])
const contacts = ref([])
const interviewStages = ref([])
const allSkills = ref([])
const skillFocusAreas = ref({}) // { skillId: [...focusAreas] }
const selectedCompanyId = ref(null)
const selectedJobId = ref(null)
const activeTab = ref('info')
const activeJobDetailTab = ref('jd')
const resumeAnalysis = ref(null)
const aiPromptData = ref(null)
const savedAnalyses = ref([])
const interviewPrepPromptData = ref(null)
const selectedAnalysis = ref(null)
const customizedResume = ref(null) // 定制简历
const loadingCustomizedResume = ref(false) // 加载状态
const showCreateCompanyModal = ref(false)
const showCreateJobModal = ref(false)
const showCreateContactModal = ref(false)
const showCreateLinkModal = ref(false)
const showCreateStageModal = ref(false)
const showRecruiterInsightsModal = ref(false)

const editModes = ref({
  info: false
})

const tabs = [
  { id: 'info', name: '公司信息' },
  { id: 'jobs', name: '职位管理' },
  { id: 'contacts', name: '人脉管理' }
]

const jobDetailTabs = [
  { id: 'jd', name: '职位描述' },
  { id: 'ai-analysis', name: '简历分析' },
  { id: 'resume', name: '定制简历' },
  { id: 'interview-prep', name: '面试准备' },
  { id: 'recruiter', name: '招聘人员' },
  { id: 'interview', name: '面试流程' }
]

const companyFormData = ref({
  companyName: '',
  industry: ''
})

const editCompanyData = ref({
  companyName: '',
  industry: '',
  companySize: '',
  location: '',
  companyDescription: '',
  companyCulture: ''
})

const jobFormData = ref({
  positionName: '',
  positionLevel: '',
  jobUrl: '',
  jobStatus: 'Open',
  qualifications: '',
  responsibilities: '',
  applicationStatus: '未申请',
  appliedDate: ''
})

const editingJob = ref(null)

const contactFormData = ref({
  referralName: '',
  position: '',
  relationship: '',
  email: '',
  phone: '',
  linkedinUrl: '',
  notes: ''
})

const editingContact = ref(null)

const linkFormData = ref({
  linkTitle: '',
  linkUrl: '',
  notes: '',
  sortOrder: 0
})

const editingLink = ref(null)

const stageFormData = ref({
  stageName: '',
  stageOrder: 1,
  skillIds: [],
  focusAreaIds: [],
  preparationNotes: ''
})

const editingStage = ref(null)

const recruiterInsightsFormData = ref({
  teamSize: '',
  teamCulture: '',
  techStackPreference: [],
  interviewFocus: '',
  processTips: ''
})

const techStackInput = ref('')

const currentCompany = computed(() =>
  companies.value.find(c => c.id === selectedCompanyId.value)
)

const currentJob = computed(() =>
  jobs.value.find(j => j.id === selectedJobId.value)
)

const recruiterContacts = computed(() =>
  contacts.value.filter(c => c.relationship === 'Recruiter')
)

// Get skills with their focus areas for the selected skills
const selectedSkillsWithFocusAreas = computed(() => {
  if (!stageFormData.value.skillIds || stageFormData.value.skillIds.length === 0) {
    return []
  }

  return stageFormData.value.skillIds
    .map(skillId => {
      const skill = allSkills.value.find(s => s.id === skillId)
      if (!skill) return null

      return {
        id: skill.id,
        name: skill.name,
        focusAreas: skillFocusAreas.value[skillId] || []
      }
    })
    .filter(s => s !== null)
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

// 加载简历分析
const loadResumeAnalysis = async () => {
  if (!selectedJobId.value) return

  try {
    const data = await resumeAnalysisApi.analyzeResumeByJob(selectedJobId.value)
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

const getStatusColor = (status) => {
  const colors = {
    '未申请': 'bg-gray-100 text-gray-700',
    '已投递': 'bg-blue-100 text-blue-700',
    '筛选中': 'bg-yellow-100 text-yellow-700',
    '面试中': 'bg-purple-100 text-purple-700',
    'Offer': 'bg-green-100 text-green-700',
    '已拒绝': 'bg-red-100 text-red-700',
    '已撤回': 'bg-gray-100 text-gray-700'
  }
  return colors[status] || 'bg-gray-100 text-gray-700'
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

const getReferralStatusColor = (status) => {
  const colors = {
    'NotRequested': 'bg-gray-100 text-gray-700',
    'Requested': 'bg-blue-100 text-blue-700',
    'Agreed': 'bg-green-100 text-green-700',
    'Declined': 'bg-red-100 text-red-700',
    'Submitted': 'bg-purple-100 text-purple-700'
  }
  return colors[status] || 'bg-gray-100 text-gray-700'
}

const getReferralResultColor = (result) => {
  const colors = {
    'InterviewScheduled': 'text-green-600 font-medium',
    'NoResponse': 'text-yellow-600',
    'Rejected': 'text-red-600'
  }
  return colors[result] || 'text-gray-600'
}

onMounted(async () => {
  await loadCompanies()
  await loadAllSkills()

  // 处理路由query参数（从申请列表跳转过来）
  if (route.query.companyId) {
    const companyId = parseInt(route.query.companyId)
    if (companies.value.some(c => c.id === companyId)) {
      selectedCompanyId.value = companyId
      activeTab.value = 'jobs'

      // 等待jobs加载完成后选中职位
      if (route.query.jobId) {
        setTimeout(() => {
          const jobId = parseInt(route.query.jobId)
          if (jobs.value.some(j => j.id === jobId)) {
            selectedJobId.value = jobId
          }
        }, 500)
      }
    }
  }
})

watch(selectedCompanyId, async (newVal) => {
  if (newVal) {
    // 加载公司完整详情（包含 links）
    try {
      const fullCompany = await companyApi.getCompanyById(newVal)
      const index = companies.value.findIndex(c => c.id === newVal)
      if (index !== -1) {
        companies.value[index] = fullCompany
      }
    } catch (error) {
      console.error('加载公司详情失败:', error)
    }

    loadJobs()
    loadContacts()
  } else {
    jobs.value = []
    contacts.value = []
  }
  // 重置编辑模式
  editModes.value.info = false
})

watch(selectedJobId, async (newVal) => {
  if (newVal) {
    await loadInterviewStages()
    // Also load AI analyses and customized resume when job changes
    await loadSavedAnalyses(newVal)
    await loadCustomizedResume(newVal)
  } else {
    interviewStages.value = []
    savedAnalyses.value = []
    customizedResume.value = null
  }
})

// Watch for skillIds changes to load focus areas
watch(() => stageFormData.value.skillIds, async (newSkillIds, oldSkillIds) => {
  if (!newSkillIds || newSkillIds.length === 0) {
    return
  }

  // Find newly added skills
  const oldIds = oldSkillIds || []
  const newIds = newSkillIds.filter(id => !oldIds.includes(id))

  // Load focus areas for new skills
  for (const skillId of newIds) {
    if (!skillFocusAreas.value[skillId]) {
      try {
        const focusAreas = await getFocusAreasBySkillId(skillId)
        skillFocusAreas.value[skillId] = focusAreas || []
      } catch (error) {
        console.error(`加载Skill ${skillId}的Focus Areas失败:`, error)
        skillFocusAreas.value[skillId] = []
      }
    }
  }
}, { deep: true })

const selectCompany = (companyId) => {
  selectedCompanyId.value = companyId
  activeTab.value = 'info'
}

const selectJob = async (jobId) => {
  selectedJobId.value = jobId
  activeJobDetailTab.value = 'jd'
  // Note: watch(selectedJobId) will handle loading data
}

const loadCompanies = async () => {
  try {
    const data = await companyApi.getCompanies()
    companies.value = data || []
    if (companies.value.length > 0 && !selectedCompanyId.value) {
      selectedCompanyId.value = companies.value[0].id
    }
  } catch (error) {
    console.error('加载公司列表失败:', error)
  }
}

const loadJobs = async () => {
  if (!selectedCompanyId.value) return

  try {
    const data = await jobApplicationApi.getJobsByCompany(selectedCompanyId.value)
    jobs.value = data || []
    // 自动选中第一个职位
    if (jobs.value.length > 0 && !selectedJobId.value) {
      selectedJobId.value = jobs.value[0].id
    }
  } catch (error) {
    console.error('加载职位列表失败:', error)
  }
}

const loadContacts = async () => {
  if (!selectedCompanyId.value) return

  try {
    const data = await referralApi.getReferralsByCompany(selectedCompanyId.value)
    contacts.value = data || []
  } catch (error) {
    console.error('加载人脉列表失败:', error)
  }
}

const loadAllSkills = async () => {
  try {
    const data = await getSkills()
    allSkills.value = data || []
  } catch (error) {
    console.error('加载技能列表失败:', error)
  }
}

const createCompany = async () => {
  if (!companyFormData.value.companyName.trim()) {
    alert('请输入公司名称')
    return
  }

  try {
    await companyApi.createCompany(companyFormData.value)
    showCreateCompanyModal.value = false
    companyFormData.value = { companyName: '', industry: '' }
    await loadCompanies()
  } catch (error) {
    console.error('创建公司失败:', error)
    alert('创建失败')
  }
}

const deleteCompany = async () => {
  if (!confirm('确定要删除这个公司吗？所有关联的职位和人脉也将被删除。')) return

  try {
    await companyApi.deleteCompany(selectedCompanyId.value)
    selectedCompanyId.value = null
    await loadCompanies()
    alert('删除成功')
  } catch (error) {
    console.error('删除公司失败:', error)
    alert('删除失败')
  }
}

const loadCompanyForEdit = () => {
  if (!currentCompany.value) return
  editCompanyData.value = {
    companyName: currentCompany.value.companyName || '',
    industry: currentCompany.value.industry || '',
    companySize: currentCompany.value.companySize || '',
    location: currentCompany.value.location || '',
    companyDescription: currentCompany.value.companyDescription || '',
    companyCulture: currentCompany.value.companyCulture || ''
  }
}

const saveCompanyInfo = async () => {
  try {
    await companyApi.updateCompany(selectedCompanyId.value, editCompanyData.value)
    alert('保存成功')
    editModes.value.info = false
    await loadCompanies()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

const openJobModal = (job = null) => {
  if (job) {
    editingJob.value = job
    jobFormData.value = {
      positionName: job.positionName || '',
      positionLevel: job.positionLevel || '',
      jobUrl: job.jobUrl || '',
      jobStatus: job.jobStatus || 'Open',
      qualifications: job.qualifications || '',
      responsibilities: job.responsibilities || '',
      applicationStatus: job.applicationStatus || '未申请',
      appliedDate: job.appliedDate ? job.appliedDate.substring(0, 10) : ''
    }
  } else {
    editingJob.value = null
    jobFormData.value = {
      positionName: '',
      positionLevel: '',
      jobUrl: '',
      jobStatus: 'Open',
      qualifications: '',
      responsibilities: '',
      applicationStatus: '未申请',
      appliedDate: ''
    }
  }
  showCreateJobModal.value = true
}

const saveJob = async () => {
  if (!jobFormData.value.positionName.trim()) {
    alert('请输入职位名称')
    return
  }

  try {
    const payload = {
      ...jobFormData.value,
      companyId: selectedCompanyId.value
    }

    if (editingJob.value?.id) {
      await jobApplicationApi.updateJobApplication(editingJob.value.id, payload)
    } else {
      await jobApplicationApi.createJobApplication(payload)
    }

    showCreateJobModal.value = false
    editingJob.value = null
    await loadJobs()
  } catch (error) {
    console.error('保存职位失败:', error)
    alert('保存失败')
  }
}

const deleteJob = async (jobId) => {
  if (!confirm('确定要删除这个职位吗?')) return

  try {
    await jobApplicationApi.deleteJobApplication(jobId)
    if (selectedJobId.value === jobId) {
      selectedJobId.value = null
    }
    await loadJobs()
  } catch (error) {
    console.error('删除职位失败:', error)
    alert('删除失败')
  }
}

const openContactModal = (contact = null) => {
  if (contact) {
    editingContact.value = contact
    contactFormData.value = {
      referralName: contact.referralName || '',
      position: contact.position || '',
      relationship: contact.relationship || '',
      email: contact.email || '',
      phone: contact.phone || '',
      linkedinUrl: contact.linkedinUrl || '',
      notes: contact.notes || ''
    }
  } else {
    editingContact.value = null
    contactFormData.value = {
      referralName: '',
      position: '',
      relationship: '',
      email: '',
      phone: '',
      linkedinUrl: '',
      notes: ''
    }
  }
  showCreateContactModal.value = true
}

const saveContact = async () => {
  if (!contactFormData.value.referralName.trim()) {
    alert('请输入姓名')
    return
  }

  try {
    const payload = {
      ...contactFormData.value,
      companyId: selectedCompanyId.value
    }

    if (editingContact.value?.id) {
      await referralApi.updateReferral(editingContact.value.id, payload)
    } else {
      await referralApi.createReferral(payload)
    }

    showCreateContactModal.value = false
    editingContact.value = null
    await loadContacts()
  } catch (error) {
    console.error('保存人脉失败:', error)
    alert('保存失败')
  }
}

const deleteContact = async (contactId) => {
  if (!confirm('确定要删除这个人脉吗?')) return

  try {
    await referralApi.deleteReferral(contactId)
    await loadContacts()
  } catch (error) {
    console.error('删除人脉失败:', error)
    alert('删除失败')
  }
}

// Company Links 管理
const openLinkModal = (link = null) => {
  if (link) {
    editingLink.value = link
    linkFormData.value = {
      linkTitle: link.linkTitle || '',
      linkUrl: link.linkUrl || '',
      notes: link.notes || '',
      sortOrder: link.sortOrder || 0
    }
  } else {
    editingLink.value = null
    linkFormData.value = {
      linkTitle: '',
      linkUrl: '',
      notes: '',
      sortOrder: 0
    }
  }
  showCreateLinkModal.value = true
}

const saveLink = async () => {
  if (!linkFormData.value.linkTitle.trim() || !linkFormData.value.linkUrl.trim()) {
    alert('请输入链接标题和URL')
    return
  }

  try {
    if (editingLink.value?.id) {
      await companyApi.updateCompanyLink(selectedCompanyId.value, editingLink.value.id, linkFormData.value)
    } else {
      await companyApi.createCompanyLink(selectedCompanyId.value, linkFormData.value)
    }

    showCreateLinkModal.value = false
    editingLink.value = null
    // 重新加载公司详情以获取更新后的 links
    const updated = await companyApi.getCompanyById(selectedCompanyId.value)
    const index = companies.value.findIndex(c => c.id === selectedCompanyId.value)
    if (index !== -1) {
      companies.value[index] = updated
    }
  } catch (error) {
    console.error('保存链接失败:', error)
    alert('保存失败')
  }
}

const deleteLink = async (linkId) => {
  if (!confirm('确定要删除这个链接吗?')) return

  try {
    await companyApi.deleteCompanyLink(selectedCompanyId.value, linkId)
    // 重新加载公司详情以获取更新后的 links
    const updated = await companyApi.getCompanyById(selectedCompanyId.value)
    const index = companies.value.findIndex(c => c.id === selectedCompanyId.value)
    if (index !== -1) {
      companies.value[index] = updated
    }
  } catch (error) {
    console.error('删除链接失败:', error)
    alert('删除失败')
  }
}

// --- Interview Stages Management ---
const loadInterviewStages = async () => {
  if (!selectedJobId.value) return

  try {
    const data = await interviewStageApi.getByJobApplication(selectedJobId.value)
    // Also load checklist items for each stage
    if (data && data.length > 0) {
      const stagesWithChecklists = await Promise.all(
        data.map(async (stage) => {
          const checklist = await interviewChecklistApi.getByStage(stage.id)
          return {
            ...stage,
            checklistItems: checklist || []
          }
        })
      )
      interviewStages.value = stagesWithChecklists
    } else {
      interviewStages.value = []
    }
  } catch (error) {
    console.error('加载面试阶段失败:', error)
    interviewStages.value = []
  }
}

const openStageModal = (stage = null) => {
  if (stage) {
    editingStage.value = stage
    stageFormData.value = {
      stageName: stage.stageName || '',
      stageOrder: stage.stageOrder || 1,
      skillIds: stage.skillIds || [],
      focusAreaIds: stage.focusAreaIds || [],
      preparationNotes: stage.preparationNotes || ''
    }
  } else {
    editingStage.value = null
    stageFormData.value = {
      stageName: '',
      stageOrder: interviewStages.value.length + 1,
      skillIds: [],
      focusAreaIds: [],
      preparationNotes: ''
    }
  }
  showCreateStageModal.value = true
}

const saveStage = async () => {
  if (!stageFormData.value.stageName.trim()) {
    alert('请输入阶段名称')
    return
  }

  try {
    const payload = {
      ...stageFormData.value,
      jobApplicationId: selectedJobId.value
    }

    if (editingStage.value?.id) {
      await interviewStageApi.updateStage(editingStage.value.id, payload)
    } else {
      await interviewStageApi.createStage(selectedJobId.value, payload)
    }

    showCreateStageModal.value = false
    editingStage.value = null
    await loadInterviewStages()
  } catch (error) {
    console.error('保存面试阶段失败:', error)
    alert('保存失败')
  }
}

const deleteStage = async (stageId) => {
  if (!confirm('确定要删除这个面试阶段吗?')) return

  try {
    await interviewStageApi.deleteStage(stageId)
    await loadInterviewStages()
  } catch (error) {
    console.error('删除面试阶段失败:', error)
    alert('删除失败')
  }
}

// --- Interview Records Management ---

// --- Recruiter Management ---
const addRecruiterContact = () => {
  // 预填充relationship为Recruiter
  contactFormData.value = {
    referralName: '',
    position: 'Recruiter',
    relationship: 'Recruiter',
    email: '',
    phone: '',
    linkedinUrl: '',
    notes: ''
  }
  editingContact.value = null
  showCreateContactModal.value = true
}

// ========== AI Job Analysis Functions ==========

// Generate AI analysis prompt
const generateAIPrompt = async () => {
  if (!selectedJobId.value) {
    alert('请先选择一个职位')
    return
  }

  try {
    const data = await aiJobAnalysisApi.generatePrompt(selectedJobId.value)
    aiPromptData.value = data
  } catch (error) {
    console.error('生成AI分析Prompt失败:', error)
    alert('生成失败，请稍后重试')
  }
}

// Load saved AI analyses for a job
const loadSavedAnalyses = async (jobId) => {
  try {
    const data = await aiJobAnalysisApi.getByJobApplication(jobId)
    savedAnalyses.value = data || []
  } catch (error) {
    console.error('加载AI分析结果失败:', error)
    savedAnalyses.value = []
  }
}

// View analysis details
const viewAnalysisDetails = (analysis) => {
  selectedAnalysis.value = analysis
  try {
    const fullAnalysis = JSON.parse(analysis.aiAnalysisResult)

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

// Copy to clipboard
const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text)
    alert('已复制到剪贴板！')
  } catch (error) {
    console.error('复制失败:', error)
    alert('复制失败，请手动复制')
  }
}

// ========== Interview Preparation Functions ==========

// Generate interview preparation prompt
const generateInterviewPrepPrompt = async () => {
  if (!selectedJobId.value) {
    alert('请先选择一个职位')
    return
  }

  const userId = 3 // TODO: Get from auth context
  const prompt = `Prepare for interview job ${selectedJobId.value} user ${userId}`

  interviewPrepPromptData.value = {
    jobId: selectedJobId.value,
    userId: userId,
    prompt: prompt
  }
}

// Parse analysis result safely
const parseAnalysisResult = (analysis) => {
  try {
    return JSON.parse(analysis.aiAnalysisResult)
  } catch (error) {
    console.error('解析分析结果失败:', error)
    return {
      educationMatch: { score: 0 },
      experienceMatch: { score: 0 },
      skillMatch: { score: 0 },
      softSkillMatch: { score: 0 },
      strengths: [],
      improvements: []
    }
  }
}

// ========== Customized Resume Functions ==========

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
  if (!selectedJobId.value) return

  try {
    const clonedResume = await resumeApi.cloneResumeForJob(selectedJobId.value)
    customizedResume.value = clonedResume

    // Navigate to resume management - AI suggestions will auto-load based on jobApplicationId
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

  // Navigate to resume management - AI suggestions will auto-load based on jobApplicationId
  router.push({
    path: '/job-search/resume',
    query: {
      resumeId: customizedResume.value.id
    }
  })
}

// Extract improvement suggestions from AI analysis
const extractImprovementSuggestions = () => {
  const suggestions = []

  // From resumeAnalysis (built-in analysis)
  if (resumeAnalysis.value) {
    if (resumeAnalysis.value.improvements && resumeAnalysis.value.improvements.length > 0) {
      suggestions.push(...resumeAnalysis.value.improvements)
    }
    if (resumeAnalysis.value.customization) {
      const customization = resumeAnalysis.value.customization
      if (customization.keywordSuggestions) {
        customization.keywordSuggestions.forEach(kw => {
          suggestions.push(`关键词优化: 在${kw.section}添加"${kw.keyword}" - ${kw.reason}`)
        })
      }
      if (customization.projectOptimizations) {
        customization.projectOptimizations.forEach(proj => {
          suggestions.push(`项目 "${proj.projectName}": ${proj.suggestions.join('; ')}`)
        })
      }
      if (customization.skillHighlights) {
        customization.skillHighlights.forEach(skill => {
          suggestions.push(`技能 "${skill.skill}": ${skill.suggestedMention}`)
        })
      }
      if (customization.structuralSuggestions) {
        suggestions.push(...customization.structuralSuggestions)
      }
    }
  }

  // From savedAnalyses (AI analysis)
  if (savedAnalyses.value && savedAnalyses.value.length > 0) {
    const latestAnalysis = savedAnalyses.value[0]
    const parsedAnalysis = parseAnalysisResult(latestAnalysis)
    if (parsedAnalysis.improvements && parsedAnalysis.improvements.length > 0) {
      suggestions.push(...parsedAnalysis.improvements)
    }
  }

  return suggestions
}

// ========== Recruiter Insights Functions ==========

const openRecruiterInsightsModal = () => {
  if (selectedJob.value?.recruiterInsights) {
    // 编辑模式 - 加载现有数据
    recruiterInsightsFormData.value = {
      teamSize: selectedJob.value.recruiterInsights.teamSize || '',
      teamCulture: selectedJob.value.recruiterInsights.teamCulture || '',
      techStackPreference: selectedJob.value.recruiterInsights.techStackPreference || [],
      interviewFocus: selectedJob.value.recruiterInsights.interviewFocus || '',
      processTips: selectedJob.value.recruiterInsights.processTips || ''
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
  if (!selectedJobId.value) {
    alert('请先选择一个职位')
    return
  }

  try {
    // 合并现有数据，只更新recruiterInsights字段
    const payload = {
      ...currentJob.value,  // 保留所有现有字段
      recruiterInsights: recruiterInsightsFormData.value  // 只更新recruiterInsights
    }

    await jobApplicationApi.updateJobApplication(selectedJobId.value, payload)

    showRecruiterInsightsModal.value = false
    await loadJobs()

    // 重新选中当前职位以刷新数据
    const currentId = selectedJobId.value
    selectedJobId.value = null
    setTimeout(() => {
      selectedJobId.value = currentId
    }, 100)

    alert('保存成功')
  } catch (error) {
    console.error('保存Recruiter Insights失败:', error)
    alert('保存失败，请稍后重试')
  }
}
</script>

<style scoped>
/* 文本截断样式 */
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
