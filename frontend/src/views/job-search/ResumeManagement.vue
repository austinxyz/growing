<template>
  <div class="resume-management h-full flex">
    <!-- 左侧：简历版本列表 -->
    <div class="w-80 bg-white border-r border-gray-200 flex flex-col">
      <div class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-900 mb-3">简历版本</h2>
        <button
          @click="showCreateModal = true"
          class="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center justify-center gap-2"
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
              'p-3 mb-2 rounded-lg cursor-pointer transition-colors',
              selectedResumeId === resume.id
                ? 'bg-blue-50 border-2 border-blue-500'
                : 'bg-gray-50 border border-gray-200 hover:bg-gray-100'
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
    <div class="flex-1 flex flex-col bg-gray-50">
      <div v-if="currentResume" class="flex-1 flex flex-col">
        <!-- 顶部操作栏 -->
        <div class="bg-white border-b border-gray-200 p-4 flex items-center justify-between">
          <h1 class="text-xl font-bold text-gray-900">{{ currentResume.versionName }}</h1>
          <div class="flex gap-2">
            <button
              @click="copyResume"
              class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
            >
              复制简历
            </button>
            <button
              @click="setAsDefault"
              :disabled="currentResume.isDefault"
              class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50"
            >
              设为默认
            </button>
            <button
              @click="deleteResume"
              :disabled="currentResume.isDefault"
              class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50"
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
                'px-6 py-3 font-medium transition-colors border-b-2',
                activeTab === tab.id
                  ? 'text-blue-600 border-blue-600'
                  : 'text-gray-600 border-transparent hover:text-gray-900'
              ]"
            >
              {{ tab.name }}
            </button>
          </div>
        </div>

        <!-- Tab内容 -->
        <div class="flex-1 overflow-y-auto p-6">
          <!-- Tab 1: 基本信息 -->
          <div v-if="activeTab === 'basic'" class="bg-white rounded-lg shadow p-6 space-y-6">
            <!-- Tab操作按钮 -->
            <div class="flex justify-between items-center border-b border-gray-200 pb-4">
              <h2 class="text-lg font-semibold text-gray-900">基本信息</h2>
              <div class="flex gap-2">
                <button
                  v-if="!editModes.basic"
                  @click="editModes.basic = true"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  编辑
                </button>
                <template v-else>
                  <button
                    @click="editModes.basic = false"
                    class="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                  >
                    取消
                  </button>
                  <button
                    @click="saveBasicInfo"
                    class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                  >
                    保存
                  </button>
                </template>
              </div>
            </div>

            <!-- 个人简介 -->
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-3">个人简介</h3>
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
              <h3 class="text-lg font-semibold text-gray-900 mb-3">联系方式</h3>
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
              <h3 class="text-lg font-semibold text-gray-900 mb-3">语言能力</h3>
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
          <div v-if="activeTab === 'skills'" class="bg-white rounded-lg shadow p-6">
            <!-- Tab操作按钮 -->
            <div class="flex justify-between items-center border-b border-gray-200 pb-4 mb-4">
              <h2 class="text-lg font-semibold text-gray-900">技能</h2>
              <div class="flex gap-2">
                <button
                  v-if="!editModes.skills"
                  @click="editModes.skills = true"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  编辑
                </button>
                <template v-else>
                  <button
                    @click="showSkillModal = true"
                    class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                  >
                    添加技能
                  </button>
                  <button
                    @click="editModes.skills = false"
                    class="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                  >
                    完成
                  </button>
                </template>
              </div>
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
          <div v-if="activeTab === 'experience'" class="bg-white rounded-lg shadow p-6">
            <!-- Tab操作按钮 -->
            <div class="flex justify-between items-center border-b border-gray-200 pb-4 mb-4">
              <h2 class="text-lg font-semibold text-gray-900">工作经历</h2>
              <div class="flex gap-2">
                <button
                  v-if="!editModes.experience"
                  @click="editModes.experience = true"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  管理
                </button>
                <template v-else>
                  <button
                    @click="openExperienceModal()"
                    class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                  >
                    添加工作经历
                  </button>
                  <button
                    @click="editModes.experience = false"
                    class="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                  >
                    完成
                  </button>
                </template>
              </div>
            </div>

            <div class="space-y-4">
              <div
                v-for="exp in currentResume.experiences"
                :key="exp.id"
                class="p-4 border border-gray-200 rounded-lg hover:border-gray-300 transition-colors"
              >
                <div class="flex items-start justify-between mb-2">
                  <div class="flex-1">
                    <h4 class="font-semibold text-gray-900 text-lg">{{ exp.position }}</h4>
                    <p class="text-gray-700 font-medium">{{ exp.companyName }}</p>
                    <p v-if="exp.location" class="text-sm text-gray-600">{{ exp.location }}</p>
                    <p class="text-sm text-gray-500 mt-1">
                      {{ formatDate(exp.startDate) }} - {{ exp.endDate ? formatDate(exp.endDate) : '至今' }}
                    </p>
                  </div>
                  <div v-if="editModes.experience" class="flex gap-2 ml-4">
                    <button
                      @click="openExperienceModal(exp)"
                      class="text-blue-600 hover:text-blue-700"
                      title="编辑"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button
                      @click="deleteExperience(exp.id)"
                      class="text-red-600 hover:text-red-700"
                      title="删除"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                </div>
                <div v-if="exp.responsibilities" class="mt-3">
                  <div v-html="renderMarkdown(exp.responsibilities)" class="prose max-w-none"></div>
                </div>
              </div>
              <div v-if="!currentResume.experiences?.length" class="text-gray-500 text-center py-8">
                暂无工作经历
              </div>
            </div>
          </div>

          <!-- Tab 4: 教育与培训 -->
          <div v-if="activeTab === 'education'" class="bg-white rounded-lg shadow p-6 space-y-6">
            <!-- 教育背景 -->
            <div>
              <div class="flex justify-between items-center mb-4">
                <h3 class="text-lg font-semibold text-gray-900">教育背景</h3>
                <div class="flex gap-2">
                  <button
                    v-if="!editModes.education"
                    @click="editModes.education = true"
                    class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    管理
                  </button>
                  <template v-else>
                    <button
                      @click="openEducationModal()"
                      class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                    >
                      添加教育背景
                    </button>
                    <button
                      @click="editModes.education = false"
                      class="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
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
              <div class="flex justify-between items-center mb-4">
                <h3 class="text-lg font-semibold text-gray-900">证书与培训</h3>
                <div class="flex gap-2">
                  <button
                    v-if="!editModes.certifications"
                    @click="editModes.certifications = true"
                    class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    管理
                  </button>
                  <template v-else>
                    <button
                      @click="openCertificationModal()"
                      class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                    >
                      添加证书
                    </button>
                    <button
                      @click="editModes.certifications = false"
                      class="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
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
          <div v-if="activeTab === 'hobbies'" class="bg-white rounded-lg shadow p-6">
            <!-- Tab操作按钮 -->
            <div class="flex justify-between items-center border-b border-gray-200 pb-4 mb-4">
              <h2 class="text-lg font-semibold text-gray-900">兴趣爱好</h2>
              <div class="flex gap-2">
                <button
                  v-if="!editModes.hobbies"
                  @click="editModes.hobbies = true"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  编辑
                </button>
                <template v-else>
                  <button
                    @click="editModes.hobbies = false"
                    class="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
                  >
                    取消
                  </button>
                  <button
                    @click="saveHobbies"
                    class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                  >
                    保存
                  </button>
                </template>
              </div>
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
      <div class="bg-white rounded-lg p-6 w-[600px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">{{ editingExperience?.id ? '编辑工作经历' : '添加工作经历' }}</h3>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">公司名称 *</label>
              <input
                v-model="editingExperience.companyName"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">职位 *</label>
              <input
                v-model="editingExperience.position"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">地点</label>
            <input
              v-model="editingExperience.location"
              type="text"
              placeholder="例如: San Jose, CA"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">开始日期 *</label>
              <input
                v-model="editingExperience.startDate"
                type="month"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">结束日期</label>
              <input
                v-model="editingExperience.endDate"
                type="month"
                :disabled="editingExperience.isCurrent"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg disabled:bg-gray-100"
              />
              <label class="flex items-center mt-2">
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
              rows="8"
              placeholder="每行一条职责，使用 Markdown 格式：&#10;- **Led team** to achieve...&#10;- **Developed** new features..."
              class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm"
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">主要成就 (支持 Markdown)</label>
            <textarea
              v-model="editingExperience.achievements"
              rows="4"
              placeholder="主要成就和亮点..."
              class="w-full px-4 py-2 border border-gray-300 rounded-lg font-mono text-sm"
            ></textarea>
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
import { resumeApi } from '@/api/resumeApi'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()

const resumes = ref([])
const selectedResumeId = ref('')
const currentResume = ref(null)
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
  achievements: ''
})

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

onMounted(() => {
  loadResumes()
})

const loadResumes = async () => {
  try {
    const data = await resumeApi.getResumes()
    resumes.value = data || []
    // 默认选择第一个简历
    if (resumes.value.length > 0) {
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
      endDate: experience.endDate ? formatDate(experience.endDate) : ''
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
      achievements: ''
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
    if (editingExperience.value.id) {
      // 更新
      await resumeApi.updateExperience(selectedResumeId.value, editingExperience.value.id, editingExperience.value)
    } else {
      // 新增
      await resumeApi.createExperience(selectedResumeId.value, editingExperience.value)
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
    if (editingEducation.value.id) {
      // 更新
      await resumeApi.updateEducation(selectedResumeId.value, editingEducation.value.id, editingEducation.value)
    } else {
      // 新增
      await resumeApi.createEducation(selectedResumeId.value, editingEducation.value)
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
    if (editingCertification.value.id) {
      // 更新
      await resumeApi.updateCertification(selectedResumeId.value, editingCertification.value.id, editingCertification.value)
    } else {
      // 新增
      await resumeApi.createCertification(selectedResumeId.value, editingCertification.value)
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
