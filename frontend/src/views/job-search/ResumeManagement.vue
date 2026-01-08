<template>
  <div class="resume-management h-full flex bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 左侧：简历版本列表 -->
    <div class="w-80 bg-white border-r border-gray-200 flex flex-col shadow-lg">
      <div class="bg-gradient-to-r from-blue-600 to-purple-600 p-4 border-b border-purple-700">
        <h2 class="text-lg font-bold text-white mb-3">📋 简历版本</h2>
        <button
          @click="showCreateModal = true"
          class="w-full px-4 py-2.5 bg-gradient-to-r from-green-500 to-emerald-500 text-white rounded-lg hover:from-green-600 hover:to-emerald-600 hover:shadow-lg transition-all flex items-center justify-center gap-2 font-semibold shadow-md text-sm"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          创建新版本
        </button>
      </div>

      <div class="flex-1 overflow-y-auto">
        <div class="p-2">
          <div
            v-for="resume in resumes"
            :key="resume.id"
            @click="selectResume(resume.id)"
            :class="[
              'p-3 mb-2 rounded-lg cursor-pointer transition-all duration-200',
              selectedResumeId === resume.id
                ? 'bg-gradient-to-r from-blue-50 to-purple-50 border-l-4 border-l-blue-600 shadow-md'
                : 'bg-gray-50 border border-gray-200 hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-sm'
            ]"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <h3 class="font-semibold text-gray-900">{{ resume.versionName }}</h3>
                <p v-if="resume.isDefault" class="text-xs text-blue-600 mt-1">默认简历</p>
              </div>
            </div>
          </div>

          <div v-if="!resumes.length" class="text-center text-gray-500 py-8">
            暂无简历版本
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧：简历详情 -->
    <div class="flex-1 flex flex-col">
      <div v-if="currentResume" class="flex-1 flex flex-col">
        <!-- 顶部操作栏 -->
        <div class="bg-gradient-to-r from-indigo-600 to-purple-600 border-b border-purple-700 p-4 flex items-center justify-between shadow-lg">
          <h1 class="text-xl font-bold text-white">{{ currentResume.versionName }}</h1>
          <div class="flex gap-2">
            <button
              @click="copyResume"
              class="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg hover:from-purple-600 hover:to-pink-600 hover:shadow-lg transition-all font-semibold shadow-md"
            >
              复制简历
            </button>
            <button
              @click="setAsDefault"
              :disabled="currentResume.isDefault"
              class="px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-500 text-white rounded-lg hover:from-green-600 hover:to-emerald-600 hover:shadow-lg transition-all font-semibold shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
            >
              设为默认
            </button>
            <button
              @click="deleteResume"
              :disabled="currentResume.isDefault"
              class="px-4 py-2 bg-gradient-to-r from-red-500 to-pink-500 text-white rounded-lg hover:from-red-600 hover:to-pink-600 hover:shadow-lg transition-all font-semibold shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
            >
              删除简历
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
                  ? 'text-blue-600 border-b-2 border-blue-600 bg-blue-50'
                  : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50'
              ]"
            >
              {{ tab.name }}
            </button>
          </div>
        </div>

        <!-- Tab内容 -->
        <div class="flex-1 overflow-y-auto p-4">
          <!-- AI改进建议卡片 -->
          <div v-if="improvementSuggestions.length > 0" class="bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg border border-purple-200 p-4 mb-4">
            <div class="flex items-start gap-3">
              <svg class="w-6 h-6 text-purple-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
              </svg>
              <div class="flex-1">
                <h3 class="text-sm font-semibold text-gray-900 mb-2">💡 AI分析改进建议</h3>
                <ul class="space-y-2">
                  <li v-for="(suggestion, idx) in improvementSuggestions" :key="idx" class="text-sm text-gray-700 flex items-start gap-2">
                    <span class="text-purple-600 font-bold flex-shrink-0">•</span>
                    <span>{{ suggestion }}</span>
                  </li>
                </ul>
              </div>
              <button
                @click="improvementSuggestions = []"
                class="text-gray-400 hover:text-gray-600"
                title="关闭建议"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Tab 1: 基本信息 -->
          <div v-if="activeTab === 'basic'" class="bg-white rounded-lg shadow p-4 space-y-4 text-sm">
            <!-- 顶部编辑按钮 -->
            <div class="flex justify-end gap-2 border-b pb-3">
              <button
                v-if="!editModes.basic"
                @click="editModes.basic = true"
                class="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                编辑
              </button>
              <template v-else>
                <button
                  @click="editModes.basic = false"
                  class="px-3 py-1 text-sm bg-gray-600 text-white rounded hover:bg-gray-700"
                >
                  取消
                </button>
                <button
                  @click="saveBasicInfo"
                  class="px-3 py-1 text-sm bg-green-600 text-white rounded hover:bg-green-700"
                >
                  保存
                </button>
              </template>
            </div>

            <!-- 简历版本名称 -->
            <div>
              <h3 class="text-sm font-semibold text-gray-900 mb-2">简历版本名称</h3>
              <div v-if="!editModes.basic" class="text-gray-900">
                {{ currentResume.versionName }}
              </div>
              <input
                v-else
                v-model="currentResume.versionName"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="例如: Google - Software Engineer"
              />
            </div>

            <!-- 个人简介 -->
            <div>
              <h3 class="text-sm font-semibold text-gray-900 mb-2">个人简介</h3>
              <div v-if="!editModes.basic" v-html="renderMarkdown(currentResume.about)" class="prose max-w-none"></div>
              <textarea
                v-else
                v-model="currentResume.about"
                rows="6"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="个人简介 (支持 Markdown)"
              ></textarea>
            </div>

            <!-- 联系方式 -->
            <div>
              <h3 class="text-sm font-semibold text-gray-900 mb-2">联系方式</h3>
              <div v-if="!editModes.basic" class="grid grid-cols-2 gap-4">
                <div v-if="currentResume.email">
                  <label class="block text-sm font-medium text-gray-600 mb-1">邮箱</label>
                  <p class="text-gray-900">{{ currentResume.email }}</p>
                </div>
                <div v-if="currentResume.phone">
                  <label class="block text-sm font-medium text-gray-600 mb-1">电话</label>
                  <p class="text-gray-900">{{ currentResume.phone }}</p>
                </div>
                <div v-if="currentResume.linkedinUrl">
                  <label class="block text-sm font-medium text-gray-600 mb-1">LinkedIn</label>
                  <a :href="currentResume.linkedinUrl" target="_blank" class="text-blue-600 hover:underline">
                    {{ currentResume.linkedinUrl }}
                  </a>
                </div>
                <div v-if="currentResume.githubUrl">
                  <label class="block text-sm font-medium text-gray-600 mb-1">GitHub</label>
                  <a :href="currentResume.githubUrl" target="_blank" class="text-blue-600 hover:underline">
                    {{ currentResume.githubUrl }}
                  </a>
                </div>
                <!-- 其他链接 -->
                <div
                  v-for="link in currentResume.otherLinks"
                  :key="link.title"
                >
                  <label class="block text-sm font-medium text-gray-600 mb-1">{{ link.title }}</label>
                  <a :href="link.url" target="_blank" class="text-blue-600 hover:underline">
                    {{ link.url }}
                  </a>
                </div>
              </div>
              <div v-else class="space-y-4">
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">邮箱 *</label>
                    <input
                      v-model="currentResume.email"
                      type="email"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">电话</label>
                    <input
                      v-model="currentResume.phone"
                      type="tel"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">LinkedIn</label>
                    <input
                      v-model="currentResume.linkedinUrl"
                      type="url"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">GitHub</label>
                    <input
                      v-model="currentResume.githubUrl"
                      type="url"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>

                <!-- 其他链接 - 编辑模式 -->
                <div>
                  <div class="flex items-center justify-between mb-2">
                    <label class="block text-sm font-medium text-gray-700">其他链接</label>
                    <button
                      @click="showLinkModal = true"
                      class="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
                    >
                      添加链接
                    </button>
                  </div>
                  <div class="space-y-2">
                    <div
                      v-for="(link, index) in currentResume.otherLinks"
                      :key="index"
                      class="flex items-center gap-2 p-2 border border-gray-200 rounded-lg"
                    >
                      <div class="flex-1">
                        <p class="text-sm font-medium text-gray-900">{{ link.title }}</p>
                        <p class="text-xs text-gray-600 truncate">{{ link.url }}</p>
                      </div>
                      <button
                        @click="deleteLink(index)"
                        class="text-red-600 hover:text-red-700"
                        title="删除"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                      </button>
                    </div>
                    <div v-if="!currentResume.otherLinks?.length" class="text-sm text-gray-500 text-center py-2">
                      暂无其他链接
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 语言 -->
            <div v-if="currentResume.languages">
              <h3 class="text-sm font-semibold text-gray-900 mb-2">语言能力</h3>
              <div v-if="!editModes.basic" v-html="renderMarkdown(currentResume.languages)" class="prose max-w-none"></div>
              <textarea
                v-else
                v-model="currentResume.languages"
                rows="4"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="每行一条语言，支持 Markdown：&#10;- **English** (Fluent)&#10;- **Mandarin** (Native)"
              ></textarea>
            </div>
          </div>

          <!-- Tab 2: 技能 -->
          <div v-if="activeTab === 'skills'" class="bg-white rounded-lg shadow p-4 text-sm">
            <div class="flex justify-end gap-2 mb-3">
              <button
                v-if="!editModes.skills"
                @click="editModes.skills = true"
                class="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                编辑
              </button>
              <template v-else>
                <button
                  @click="showSkillModal = true"
                  class="px-3 py-1 text-sm bg-green-600 text-white rounded hover:bg-green-700"
                >
                  添加技能
                </button>
                <button
                  @click="editModes.skills = false"
                  class="px-3 py-1 text-sm bg-gray-600 text-white rounded hover:bg-gray-700"
                >
                  完成
                </button>
              </template>
            </div>

            <div class="space-y-2">
              <div
                v-for="(skillGroup, category) in groupedSkills"
                :key="category"
                class="border border-gray-200 rounded-lg p-3"
              >
                <h4 class="font-semibold text-gray-900 mb-2 text-sm">{{ category }}</h4>
                <div class="flex flex-wrap gap-1.5">
                  <div
                    v-for="skill in skillGroup"
                    :key="skill.id"
                    class="inline-flex items-center gap-1.5 px-2.5 py-1 bg-blue-50 border border-blue-200 rounded-full"
                  >
                    <span class="text-sm font-medium text-gray-900">{{ skill.skillName }}</span>
                    <span v-if="skill.proficiency" class="text-xs text-gray-600">
                      ({{ skill.proficiency }})
                    </span>
                    <button
                      v-if="editModes.skills"
                      @click="deleteSkill(skill.id)"
                      class="text-red-600 hover:text-red-700 ml-0.5"
                      title="删除"
                    >
                      <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
              <div v-if="!currentResume.skills?.length" class="text-gray-500 text-center py-8">
                暂无技能
              </div>
            </div>
          </div>

          <!-- Tab 3: 工作经历 -->
          <div v-if="activeTab === 'experience'" class="bg-white rounded-lg shadow p-4 text-sm">
            <div class="space-y-2">
              <div
                v-for="exp in currentResume.experiences"
                :key="exp.id"
                class="border border-gray-200 rounded-lg hover:border-gray-300 transition-colors"
              >
                <!-- 标题栏：始终显示，包含收缩/展开按钮 -->
                <div class="p-3 flex items-start justify-between">
                  <div class="flex-1 flex items-start gap-3">
                    <!-- 收缩/展开按钮 -->
                    <button
                      @click="toggleExperience(exp.id)"
                      class="text-gray-400 hover:text-gray-600 mt-0.5"
                    >
                      <svg class="w-4 h-4 transition-transform" :class="{ 'rotate-90': !collapsedExperiences[exp.id] }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                      </svg>
                    </button>

                    <!-- 基本信息 -->
                    <div class="flex-1 min-w-0">
                      <div class="flex items-baseline gap-2 flex-wrap">
                        <h4 class="font-medium text-gray-900 text-sm">{{ exp.position }}</h4>
                        <span class="text-gray-400">@</span>
                        <p class="text-gray-700 text-sm">{{ exp.companyName }}</p>
                        <span v-if="exp.location" class="text-gray-500 text-xs">· {{ exp.location }}</span>
                      </div>
                      <p class="text-xs text-gray-500 mt-0.5">
                        {{ formatDate(exp.startDate) }} - {{ exp.endDate ? formatDate(exp.endDate) : '至今' }}
                      </p>
                    </div>
                  </div>

                  <!-- 操作按钮 -->
                  <div v-if="editModes.experience" class="flex gap-1.5 ml-3">
                    <button
                      @click="openExperienceModal(exp)"
                      class="text-blue-600 hover:text-blue-700"
                      title="编辑"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button
                      @click="deleteExperience(exp.id)"
                      class="text-red-600 hover:text-red-700"
                      title="删除"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                  <button
                    v-else-if="exp.id === currentResume.experiences[0]?.id"
                    @click="editModes.experience = true"
                    class="px-2 py-1 text-xs bg-blue-600 text-white rounded hover:bg-blue-700 ml-3"
                  >
                    管理
                  </button>
                </div>

                <!-- 展开内容 -->
                <div v-if="!collapsedExperiences[exp.id]" class="px-3 pb-3 pt-0 space-y-2">
                  <!-- 工作职责 -->
                  <div v-if="exp.responsibilities" class="text-xs">
                    <div v-html="renderMarkdown(exp.responsibilities)" class="prose prose-sm max-w-none"></div>
                  </div>

                  <!-- 关联项目 - 同一行显示 -->
                  <div v-if="exp.projectNames && exp.projectNames.length > 0" class="flex items-start gap-2 pt-2 border-t border-gray-100">
                    <span class="text-xs font-medium text-gray-600 whitespace-nowrap pt-0.5">关联项目:</span>
                    <div class="flex flex-wrap gap-1.5">
                      <router-link
                        v-for="(projectName, index) in exp.projectNames"
                        :key="index"
                        :to="{ name: 'ProjectExperiences' }"
                        class="inline-flex items-center px-2 py-0.5 bg-blue-50 text-blue-700 rounded text-xs hover:bg-blue-100 transition-colors"
                      >
                        <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                        {{ projectName }}
                      </router-link>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 添加新经历按钮 -->
              <div v-if="editModes.experience" class="flex justify-between items-center pt-1">
                <button
                  @click="openExperienceModal()"
                  class="px-3 py-1.5 text-xs bg-green-600 text-white rounded hover:bg-green-700"
                >
                  + 添加工作经历
                </button>
                <button
                  @click="editModes.experience = false"
                  class="px-3 py-1.5 text-xs bg-gray-600 text-white rounded hover:bg-gray-700"
                >
                  完成
                </button>
              </div>

              <div v-if="!currentResume.experiences?.length" class="text-gray-500 text-center py-8 text-xs">
                暂无工作经历
              </div>
            </div>
          </div>

          <!-- Tab 4: 教育与培训 -->
          <div v-if="activeTab === 'education'" class="bg-white rounded-lg shadow p-4 space-y-4 text-sm">
            <!-- 教育背景 -->
            <div>
              <div class="flex justify-between items-start mb-3">
                <h3 class="text-sm font-semibold text-gray-900">教育背景</h3>
                <div class="flex gap-2">
                  <button
                    v-if="!editModes.education"
                    @click="editModes.education = true"
                    class="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
                  >
                    管理
                  </button>
                  <template v-else>
                    <button
                      @click="openEducationModal()"
                      class="px-3 py-1 text-sm bg-green-600 text-white rounded hover:bg-green-700"
                    >
                      添加教育背景
                    </button>
                    <button
                      @click="editModes.education = false"
                      class="px-3 py-1 text-sm bg-gray-600 text-white rounded hover:bg-gray-700"
                    >
                      完成
                    </button>
                  </template>
                </div>
              </div>
              <div class="space-y-3">
                <div
                  v-for="edu in currentResume.education"
                  :key="edu.id"
                  class="p-4 border border-gray-200 rounded-lg hover:border-gray-300 transition-colors"
                  :class="editModes.education ? 'bg-white' : 'bg-gray-50'"
                >
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <h4 class="font-semibold text-gray-900">{{ edu.schoolName }}</h4>
                      <p class="text-gray-700">{{ edu.degree }} - {{ edu.major }}</p>
                      <p class="text-sm text-gray-500 mt-1">
                        {{ formatDate(edu.startDate) }} - {{ formatDate(edu.endDate) }}
                      </p>
                    </div>
                    <div v-if="editModes.education" class="flex gap-2 ml-4">
                      <button
                        @click="openEducationModal(edu)"
                        class="text-blue-600 hover:text-blue-700"
                        title="编辑"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>
                      <button
                        @click="deleteEducation(edu.id)"
                        class="text-red-600 hover:text-red-700"
                        title="删除"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                      </button>
                    </div>
                  </div>
                </div>
                <div v-if="!currentResume.education?.length" class="text-gray-500 text-center py-4">
                  暂无教育背景
                </div>
              </div>
            </div>

            <!-- 证书与培训 -->
            <div>
              <div class="flex justify-between items-start mb-3">
                <h3 class="text-sm font-semibold text-gray-900">证书与培训</h3>
                <div class="flex gap-2">
                  <button
                    v-if="!editModes.certifications"
                    @click="editModes.certifications = true"
                    class="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
                  >
                    管理
                  </button>
                  <template v-else>
                    <button
                      @click="openCertificationModal()"
                      class="px-3 py-1 text-sm bg-green-600 text-white rounded hover:bg-green-700"
                    >
                      添加证书
                    </button>
                    <button
                      @click="editModes.certifications = false"
                      class="px-3 py-1 text-sm bg-gray-600 text-white rounded hover:bg-gray-700"
                    >
                      完成
                    </button>
                  </template>
                </div>
              </div>
              <div class="grid grid-cols-2 gap-3">
                <div
                  v-for="cert in currentResume.certifications"
                  :key="cert.id"
                  class="p-3 border border-gray-200 rounded-lg hover:border-gray-300 transition-colors"
                  :class="editModes.certifications ? 'bg-white' : 'bg-gray-50'"
                >
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <p class="font-medium text-gray-900">{{ cert.certName }}</p>
                      <p class="text-sm text-gray-600">{{ cert.issuer }}</p>
                      <p v-if="cert.issueDate" class="text-xs text-gray-500 mt-1">
                        {{ cert.issueDate }}
                      </p>
                    </div>
                    <div v-if="editModes.certifications" class="flex gap-2 ml-4">
                      <button
                        @click="openCertificationModal(cert)"
                        class="text-blue-600 hover:text-blue-700"
                        title="编辑"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>
                      <button
                        @click="deleteCertification(cert.id)"
                        class="text-red-600 hover:text-red-700"
                        title="删除"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                      </button>
                    </div>
                  </div>
                </div>
                <div v-if="!currentResume.certifications?.length" class="col-span-2 text-gray-500 text-center py-4">
                  暂无证书
                </div>
              </div>
            </div>
          </div>

          <!-- Tab 5: 兴趣爱好 -->
          <div v-if="activeTab === 'hobbies'" class="bg-white rounded-lg shadow p-4 text-sm">
            <div class="flex justify-end gap-2 mb-3">
              <button
                v-if="!editModes.hobbies"
                @click="editModes.hobbies = true"
                class="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                编辑
              </button>
              <template v-else>
                <button
                  @click="editModes.hobbies = false"
                  class="px-3 py-1 text-sm bg-gray-600 text-white rounded hover:bg-gray-700"
                >
                  取消
                </button>
                <button
                  @click="saveHobbies"
                  class="px-3 py-1 text-sm bg-green-600 text-white rounded hover:bg-green-700"
                >
                  保存
                </button>
              </template>
            </div>

            <div v-if="currentResume.hobbies">
              <div v-if="!editModes.hobbies" v-html="renderMarkdown(currentResume.hobbies)" class="prose max-w-none"></div>
              <textarea
                v-else
                v-model="currentResume.hobbies"
                rows="8"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="兴趣爱好 (支持 Markdown)"
              ></textarea>
            </div>
            <div v-else class="text-gray-500 text-center py-8">
              暂无兴趣爱好信息
            </div>
          </div>
        </div>
      </div>

      <div v-else class="flex-1 flex items-center justify-center">
        <div class="text-center text-gray-500">
          <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p class="text-lg">请选择或创建一个简历版本</p>
        </div>
      </div>
    </div>

    <!-- 创建简历 Modal -->
    <div v-if="showCreateModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-96">
        <h3 class="text-lg font-semibold mb-4">创建新简历</h3>
        <input
          v-model="newResumeName"
          type="text"
          placeholder="简历版本名称 (如: Google SRE 2026)"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg mb-4"
        />
        <div class="flex justify-end gap-2">
          <button
            @click="showCreateModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="createResume"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            创建
          </button>
        </div>
      </div>
    </div>

    <!-- 添加技能 Modal -->
    <div v-if="showSkillModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-96">
        <h3 class="text-lg font-semibold mb-4">添加技能</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">技能名称</label>
            <input
              v-model="newSkill.skillName"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">分类</label>
            <select
              v-model="newSkill.skillCategory"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            >
              <option value="Cloud & Infrastructure">Cloud & Infrastructure</option>
              <option value="Programming">Programming</option>
              <option value="Frameworks">Frameworks</option>
              <option value="DevOps">DevOps</option>
              <option value="Database">Database</option>
              <option value="Messaging">Messaging</option>
              <option value="Observability">Observability</option>
              <option value="Management">Management</option>
              <option value="__custom__">自定义分类...</option>
            </select>
            <!-- 自定义分类输入框 -->
            <input
              v-if="newSkill.skillCategory === '__custom__'"
              v-model="customCategory"
              type="text"
              placeholder="输入新的分类名称"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg mt-2"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">熟练程度</label>
            <select
              v-model="newSkill.proficiency"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            >
              <option value="">不指定</option>
              <option value="Beginner">Beginner</option>
              <option value="Intermediate">Intermediate</option>
              <option value="Advanced">Advanced</option>
              <option value="Expert">Expert</option>
            </select>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <button
            @click="showSkillModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="addSkill"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            添加
          </button>
        </div>
      </div>
    </div>

    <!-- 添加链接 Modal -->
    <div v-if="showLinkModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-96">
        <h3 class="text-lg font-semibold mb-4">添加链接</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">链接标题</label>
            <input
              v-model="newLink.title"
              type="text"
              placeholder="例如: Portfolio, Blog, Twitter"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">URL</label>
            <input
              v-model="newLink.url"
              type="url"
              placeholder="https://example.com"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <button
            @click="showLinkModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="addLink"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            添加
          </button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑工作经历 Modal -->
    <div v-if="showExperienceModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[900px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">{{ editingExperience?.id ? '编辑工作经历' : '添加工作经历' }}</h3>
        <div class="space-y-3">
          <div class="grid grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">公司名称 *</label>
              <input
                v-model="editingExperience.companyName"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">职位 *</label>
              <input
                v-model="editingExperience.position"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">地点</label>
              <input
                v-model="editingExperience.location"
                type="text"
                placeholder="例如: San Jose, CA"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
            </div>
          </div>

          <div class="grid grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">开始日期 *</label>
              <input
                v-model="editingExperience.startDate"
                type="month"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">结束日期</label>
              <input
                v-model="editingExperience.endDate"
                type="month"
                :disabled="editingExperience.isCurrent"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm disabled:bg-gray-100"
              />
            </div>
            <div class="flex items-end">
              <label class="flex items-center pb-2">
                <input
                  v-model="editingExperience.isCurrent"
                  type="checkbox"
                  class="mr-2"
                />
                <span class="text-sm text-gray-700">目前在职</span>
              </label>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">工作职责 (支持 Markdown)</label>
            <textarea
              v-model="editingExperience.responsibilities"
              rows="5"
              placeholder="每行一条职责，使用 Markdown 格式：&#10;- **Led team** to achieve...&#10;- **Developed** new features..."
              class="w-full px-3 py-2 border border-gray-300 rounded-lg font-mono text-sm"
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">主要成就 (支持 Markdown)</label>
            <textarea
              v-model="editingExperience.achievements"
              rows="3"
              placeholder="主要成就和亮点..."
              class="w-full px-3 py-2 border border-gray-300 rounded-lg font-mono text-sm"
            ></textarea>
          </div>

          <!-- 关联项目 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">关联项目</label>
            <div class="space-y-1 max-h-32 overflow-y-auto border border-gray-200 rounded-lg p-2">
              <div
                v-for="project in allProjects"
                :key="project.id"
                class="flex items-start p-1.5 hover:bg-gray-50 rounded cursor-pointer"
                @click="toggleProject(project.id)"
              >
                <input
                  type="checkbox"
                  :checked="editingExperience.projectIds?.includes(project.id)"
                  class="mt-0.5 mr-2"
                  @click.stop="toggleProject(project.id)"
                />
                <div class="flex-1">
                  <p class="font-medium text-gray-900 text-sm">{{ project.projectName }}</p>
                  <p class="text-xs text-gray-500">{{ project.projectType }} · {{ project.startDate || 'N/A' }}</p>
                </div>
              </div>
              <div v-if="!allProjects?.length" class="text-center text-gray-500 text-sm py-2">
                暂无项目，请先到项目经验库创建项目
              </div>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button
            @click="showExperienceModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveExperience"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            保存
          </button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑教育背景 Modal -->
    <div v-if="showEducationModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[600px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">{{ editingEducation?.id ? '编辑教育背景' : '添加教育背景' }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">学校名称 *</label>
            <input
              v-model="editingEducation.schoolName"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              required
            />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">学位 *</label>
              <input
                v-model="editingEducation.degree"
                type="text"
                placeholder="例如: Bachelor, Master"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">专业 *</label>
              <input
                v-model="editingEducation.major"
                type="text"
                placeholder="例如: Computer Science"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">开始日期 *</label>
              <input
                v-model="editingEducation.startDate"
                type="month"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">结束日期 *</label>
              <input
                v-model="editingEducation.endDate"
                type="month"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">GPA (选填)</label>
            <input
              v-model="editingEducation.gpa"
              type="text"
              placeholder="例如: 3.8/4.0"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">课程 (选填，支持 Markdown)</label>
            <textarea
              v-model="editingEducation.courses"
              rows="4"
              placeholder="主要课程列表..."
              class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm"
            ></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button
            @click="showEducationModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveEducation"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            保存
          </button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑证书 Modal -->
    <div v-if="showCertificationModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[500px]">
        <h3 class="text-lg font-semibold mb-4">{{ editingCertification?.id ? '编辑证书' : '添加证书' }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">证书名称 *</label>
            <input
              v-model="editingCertification.certName"
              type="text"
              placeholder="例如: AWS Certified Solutions Architect"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">颁发机构 *</label>
            <input
              v-model="editingCertification.issuer"
              type="text"
              placeholder="例如: Amazon Web Services"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">获得日期</label>
            <input
              v-model="editingCertification.issueDate"
              type="month"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">过期日期 (选填)</label>
            <input
              v-model="editingCertification.expiryDate"
              type="month"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">凭证URL (选填)</label>
            <input
              v-model="editingCertification.credentialUrl"
              type="url"
              placeholder="https://..."
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button
            @click="showCertificationModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveCertification"
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
import { useRoute } from 'vue-router'
import { resumeApi } from '@/api/resumeApi'
import { projectApi } from '@/api/projectApi'
import { aiJobAnalysisApi } from '@/api/aiJobAnalysisApi'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()
const route = useRoute()

const resumes = ref([])
const selectedResumeId = ref('')
const currentResume = ref(null)
const improvementSuggestions = ref([]) // AI分析改进建议
const showCreateModal = ref(false)
const showSkillModal = ref(false)
const showLinkModal = ref(false)
const showExperienceModal = ref(false)
const showEducationModal = ref(false)
const showCertificationModal = ref(false)
const newResumeName = ref('')
const activeTab = ref('basic')

// 每个Tab独立的编辑模式
const editModes = ref({
  basic: false,
  skills: false,
  experience: false,
  education: false,
  certifications: false,
  hobbies: false
})

const tabs = [
  { id: 'basic', name: '基本信息' },
  { id: 'skills', name: '技能' },
  { id: 'experience', name: '工作经历' },
  { id: 'education', name: '教育与培训' },
  { id: 'hobbies', name: '兴趣爱好' }
]

const newSkill = ref({
  skillName: '',
  skillCategory: 'Cloud & Infrastructure',
  proficiency: ''
})

const newLink = ref({
  title: '',
  url: ''
})

const customCategory = ref('')

const editingExperience = ref({
  companyName: '',
  position: '',
  location: '',
  startDate: '',
  endDate: '',
  isCurrent: false,
  responsibilities: '',
  achievements: '',
  projectIds: []  // 关联项目ID列表
})

const allProjects = ref([])  // 所有项目列表（用于选择）

// 工作经历收缩状态
const collapsedExperiences = ref({})

const editingEducation = ref({
  schoolName: '',
  degree: '',
  major: '',
  startDate: '',
  endDate: '',
  gpa: '',
  courses: ''
})

const editingCertification = ref({
  certName: '',
  issuer: '',
  issueDate: '',
  expiryDate: '',
  credentialUrl: ''
})

// 技能按大类分组
const groupedSkills = computed(() => {
  if (!currentResume.value?.skills) return {}

  const groups = {}
  currentResume.value.skills.forEach(skill => {
    const category = skill.skillCategory || 'Other'
    if (!groups[category]) {
      groups[category] = []
    }
    groups[category].push(skill)
  })
  return groups
})

// 日期格式化: 2023-09-01 -> 2023-09
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  // 如果是 YYYY-MM-DD 格式，截取前7位
  if (typeof dateStr === 'string' && dateStr.length >= 7) {
    return dateStr.substring(0, 7)
  }
  return dateStr
}

// Markdown渲染
const renderMarkdown = (text) => {
  if (!text) return ''
  // 如果不是字符串，转换为字符串
  if (typeof text !== 'string') {
    if (Array.isArray(text)) {
      // 数组转为逗号分隔的字符串
      text = text.map(item => {
        if (typeof item === 'object') {
          return item.name || JSON.stringify(item)
        }
        return item
      }).join(', ')
    } else if (typeof text === 'object') {
      text = JSON.stringify(text, null, 2)
    } else {
      text = String(text)
    }
  }
  return md.render(text)
}

// 监听"目前在职"复选框，自动清空结束日期
watch(() => editingExperience.value.isCurrent, (newValue) => {
  if (newValue) {
    editingExperience.value.endDate = ''
  }
})

// Watch for currentResume changes to auto-load AI suggestions
watch(() => currentResume.value?.jobApplicationId, async (jobApplicationId) => {
  if (jobApplicationId) {
    // This is a customized resume for a specific job - load AI analysis
    try {
      const analyses = await aiJobAnalysisApi.getByJobApplication(jobApplicationId)
      if (analyses && analyses.length > 0) {
        // Extract improvement suggestions from the latest analysis
        const latestAnalysis = analyses[0]
        const parsedAnalysis = JSON.parse(latestAnalysis.aiAnalysisResult)
        improvementSuggestions.value = parsedAnalysis.improvements || []
      } else {
        improvementSuggestions.value = []
      }
    } catch (error) {
      console.error('加载AI分析失败:', error)
      improvementSuggestions.value = []
    }
  } else {
    // This is a general resume (not customized for a job) - clear suggestions
    improvementSuggestions.value = []
  }
})

onMounted(async () => {
  // 先加载简历列表和项目列表
  await loadResumes()
  await loadProjects()

  // 如果URL包含resumeId参数，选中该简历并加载详情
  if (route.query.resumeId) {
    // Convert string to number for proper comparison with resume.id
    await selectResume(parseInt(route.query.resumeId))
  }
})

// 加载所有项目（用于关联选择）
const loadProjects = async () => {
  try {
    const data = await projectApi.getProjects()
    allProjects.value = data || []
  } catch (error) {
    console.error('加载项目列表失败:', error)
  }
}

// 切换项目选择
const toggleProject = (projectId) => {
  if (!editingExperience.value.projectIds) {
    editingExperience.value.projectIds = []
  }

  const index = editingExperience.value.projectIds.indexOf(projectId)
  if (index > -1) {
    editingExperience.value.projectIds.splice(index, 1)
  } else {
    editingExperience.value.projectIds.push(projectId)
  }
}

// 切换工作经历收缩/展开
const toggleExperience = (experienceId) => {
  collapsedExperiences.value[experienceId] = !collapsedExperiences.value[experienceId]
}

const loadResumes = async () => {
  try {
    const data = await resumeApi.getResumes()
    resumes.value = data || []
    // 只有在URL没有指定resumeId时才自动选择默认简历
    if (resumes.value.length > 0 && !route.query.resumeId) {
      const defaultResume = resumes.value.find(r => r.isDefault) || resumes.value[0]
      selectedResumeId.value = defaultResume.id
      await loadResumeDetails()
    }
  } catch (error) {
    console.error('加载简历列表失败:', error)
  }
}

const selectResume = async (resumeId) => {
  selectedResumeId.value = resumeId
  // 重置所有编辑模式
  Object.keys(editModes.value).forEach(key => {
    editModes.value[key] = false
  })
  await loadResumeDetails()
  // Note: watch on currentResume.jobApplicationId will auto-load AI suggestions
}

const loadResumeDetails = async () => {
  if (!selectedResumeId.value) {
    currentResume.value = null
    return
  }

  try {
    const data = await resumeApi.getResumeById(selectedResumeId.value)
    currentResume.value = data
  } catch (error) {
    console.error('加载简历详情失败:', error)
  }
}

const createResume = async () => {
  if (!newResumeName.value.trim()) {
    alert('请输入简历名称')
    return
  }

  try {
    await resumeApi.createResume({
      versionName: newResumeName.value,
      email: 'user@example.com' // 临时邮箱,后续更新
    })
    showCreateModal.value = false
    newResumeName.value = ''
    await loadResumes()
  } catch (error) {
    console.error('创建简历失败:', error)
    alert('创建失败')
  }
}

// 保存基本信息
const saveBasicInfo = async () => {
  try {
    await resumeApi.updateResume(selectedResumeId.value, currentResume.value)
    alert('保存成功')
    editModes.value.basic = false
    await loadResumeDetails()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

// 打开工作经历Modal
const openExperienceModal = (experience = null) => {
  if (experience) {
    // 编辑模式 - 转换日期格式为 YYYY-MM
    editingExperience.value = {
      ...experience,
      startDate: formatDate(experience.startDate),
      endDate: experience.endDate ? formatDate(experience.endDate) : '',
      projectIds: experience.projectIds || []  // 保留关联项目ID
    }
  } else {
    // 新增模式
    editingExperience.value = {
      companyName: '',
      position: '',
      location: '',
      startDate: '',
      endDate: '',
      isCurrent: false,
      responsibilities: '',
      achievements: '',
      projectIds: []  // 初始化为空数组
    }
  }
  showExperienceModal.value = true
}

// 保存工作经历
const saveExperience = async () => {
  if (!editingExperience.value.companyName || !editingExperience.value.position || !editingExperience.value.startDate) {
    alert('请填写必填项')
    return
  }

  try {
    // 转换日期格式：YYYY-MM -> YYYY-MM-DD (后端需要完整日期)
    const payload = {
      ...editingExperience.value,
      startDate: editingExperience.value.startDate ? `${editingExperience.value.startDate}-01` : null,
      endDate: editingExperience.value.endDate ? `${editingExperience.value.endDate}-01` : null
    }

    if (editingExperience.value.id) {
      // 更新
      await resumeApi.updateExperience(selectedResumeId.value, editingExperience.value.id, payload)
    } else {
      // 新增
      await resumeApi.createExperience(selectedResumeId.value, payload)
    }
    showExperienceModal.value = false
    await loadResumeDetails()
  } catch (error) {
    console.error('保存工作经历失败:', error)
    alert('保存失败')
  }
}

// 删除工作经历
const deleteExperience = async (experienceId) => {
  if (!confirm('确定要删除这条工作经历吗?')) return

  try {
    await resumeApi.deleteExperience(selectedResumeId.value, experienceId)
    await loadResumeDetails()
  } catch (error) {
    console.error('删除工作经历失败:', error)
    alert('删除失败')
  }
}

// 保存兴趣爱好
const saveHobbies = async () => {
  try {
    await resumeApi.updateResume(selectedResumeId.value, currentResume.value)
    alert('保存成功')
    editModes.value.hobbies = false
    await loadResumeDetails()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

const deleteResume = async () => {
  if (!confirm('确定要删除这个简历版本吗?')) return

  try {
    await resumeApi.deleteResume(selectedResumeId.value)
    alert('删除成功')
    selectedResumeId.value = ''
    currentResume.value = null
    await loadResumes()
  } catch (error) {
    console.error('删除简历失败:', error)
    alert('删除失败')
  }
}

const copyResume = async () => {
  try {
    await resumeApi.copyResume(selectedResumeId.value)
    alert('复制成功')
    await loadResumes()
  } catch (error) {
    console.error('复制简历失败:', error)
    alert('复制失败')
  }
}

const setAsDefault = async () => {
  try {
    await resumeApi.setDefaultResume(selectedResumeId.value)
    alert('设置成功')
    await loadResumes()
  } catch (error) {
    console.error('设置默认简历失败:', error)
    alert('设置失败')
  }
}

const addSkill = async () => {
  if (!newSkill.value.skillName.trim()) {
    alert('请输入技能名称')
    return
  }

  // 处理自定义分类
  if (newSkill.value.skillCategory === '__custom__') {
    if (!customCategory.value.trim()) {
      alert('请输入自定义分类名称')
      return
    }
    newSkill.value.skillCategory = customCategory.value.trim()
  }

  try {
    await resumeApi.createSkill(selectedResumeId.value, newSkill.value)
    showSkillModal.value = false
    newSkill.value = {
      skillName: '',
      skillCategory: 'Cloud & Infrastructure',
      proficiency: ''
    }
    customCategory.value = ''
    await loadResumeDetails()
  } catch (error) {
    console.error('添加技能失败:', error)
    alert('添加失败')
  }
}

const deleteSkill = async (skillId) => {
  if (!confirm('确定要删除这个技能吗?')) return

  try {
    await resumeApi.deleteSkill(selectedResumeId.value, skillId)
    await loadResumeDetails()
  } catch (error) {
    console.error('删除技能失败:', error)
    alert('删除失败')
  }
}

const addLink = () => {
  if (!newLink.value.title.trim() || !newLink.value.url.trim()) {
    alert('请输入链接标题和URL')
    return
  }

  // Initialize otherLinks if null
  if (!currentResume.value.otherLinks) {
    currentResume.value.otherLinks = []
  }

  currentResume.value.otherLinks.push({
    title: newLink.value.title,
    url: newLink.value.url
  })

  showLinkModal.value = false
  newLink.value = {
    title: '',
    url: ''
  }
}

const deleteLink = (index) => {
  if (!confirm('确定要删除这个链接吗?')) return
  currentResume.value.otherLinks.splice(index, 1)
}

// ==================== 教育背景管理 ====================

// 打开教育背景Modal
const openEducationModal = (education = null) => {
  if (education) {
    // 编辑模式 - 转换日期格式为 YYYY-MM
    editingEducation.value = {
      ...education,
      startDate: formatDate(education.startDate),
      endDate: formatDate(education.endDate)
    }
  } else {
    // 新增模式
    editingEducation.value = {
      schoolName: '',
      degree: '',
      major: '',
      startDate: '',
      endDate: '',
      gpa: '',
      courses: ''
    }
  }
  showEducationModal.value = true
}

// 保存教育背景
const saveEducation = async () => {
  if (!editingEducation.value.schoolName || !editingEducation.value.degree ||
      !editingEducation.value.major || !editingEducation.value.startDate ||
      !editingEducation.value.endDate) {
    alert('请填写必填项')
    return
  }

  try {
    // 转换日期格式：YYYY-MM -> YYYY-MM-DD (后端需要完整日期)
    const payload = {
      ...editingEducation.value,
      startDate: editingEducation.value.startDate ? `${editingEducation.value.startDate}-01` : null,
      endDate: editingEducation.value.endDate ? `${editingEducation.value.endDate}-01` : null
    }

    if (editingEducation.value.id) {
      // 更新
      await resumeApi.updateEducation(selectedResumeId.value, editingEducation.value.id, payload)
    } else {
      // 新增
      await resumeApi.createEducation(selectedResumeId.value, payload)
    }
    showEducationModal.value = false
    await loadResumeDetails()
  } catch (error) {
    console.error('保存教育背景失败:', error)
    alert('保存失败')
  }
}

// 删除教育背景
const deleteEducation = async (educationId) => {
  if (!confirm('确定要删除这条教育背景吗?')) return

  try {
    await resumeApi.deleteEducation(selectedResumeId.value, educationId)
    await loadResumeDetails()
  } catch (error) {
    console.error('删除教育背景失败:', error)
    alert('删除失败')
  }
}

// ==================== 证书管理 ====================

// 打开证书Modal
const openCertificationModal = (certification = null) => {
  if (certification) {
    // 编辑模式 - 转换日期格式为 YYYY-MM
    editingCertification.value = {
      ...certification,
      issueDate: formatDate(certification.issueDate),
      expiryDate: certification.expiryDate ? formatDate(certification.expiryDate) : ''
    }
  } else {
    // 新增模式
    editingCertification.value = {
      certName: '',
      issuer: '',
      issueDate: '',
      expiryDate: '',
      credentialUrl: ''
    }
  }
  showCertificationModal.value = true
}

// 保存证书
const saveCertification = async () => {
  if (!editingCertification.value.certName || !editingCertification.value.issuer) {
    alert('请填写必填项')
    return
  }

  try {
    // 转换日期格式：YYYY-MM -> YYYY-MM-DD (后端需要完整日期)
    const payload = {
      ...editingCertification.value,
      issueDate: editingCertification.value.issueDate ? `${editingCertification.value.issueDate}-01` : null,
      expiryDate: editingCertification.value.expiryDate ? `${editingCertification.value.expiryDate}-01` : null
    }

    if (editingCertification.value.id) {
      // 更新
      await resumeApi.updateCertification(selectedResumeId.value, editingCertification.value.id, payload)
    } else {
      // 新增
      await resumeApi.createCertification(selectedResumeId.value, payload)
    }
    showCertificationModal.value = false
    await loadResumeDetails()
  } catch (error) {
    console.error('保存证书失败:', error)
    alert('保存失败')
  }
}

// 删除证书
const deleteCertification = async (certificationId) => {
  if (!confirm('确定要删除这个证书吗?')) return

  try {
    await resumeApi.deleteCertification(selectedResumeId.value, certificationId)
    await loadResumeDetails()
  } catch (error) {
    console.error('删除证书失败:', error)
    alert('删除失败')
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
