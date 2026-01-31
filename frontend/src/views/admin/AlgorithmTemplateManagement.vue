<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <!-- 顶部标题栏 -->
    <div class="bg-white border-b border-gray-200">
      <div class="px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">算法模版管理</h1>
        <p class="text-sm text-gray-600 mt-1">管理常用算法模版和快速参考代码</p>
      </div>
    </div>

    <!-- 两栏布局 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：模版列表 -->
      <div class="w-96 bg-white border-r border-gray-200 flex flex-col">
        <!-- 搜索栏 + 新建按钮 -->
        <div class="p-4 border-b border-gray-200">
          <div class="relative mb-3">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索模版..."
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            />
          </div>
          <button
            @click="createNewTemplate"
            class="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 flex items-center justify-center space-x-2 text-sm"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span>新建模版</span>
          </button>
        </div>

        <!-- 模版列表 -->
        <div class="flex-1 overflow-y-auto">
          <!-- Loading状态 -->
          <div v-if="loading" class="flex items-center justify-center py-12">
            <div class="text-center">
              <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <p class="mt-2 text-sm text-gray-500">加载中...</p>
            </div>
          </div>

          <!-- 模版列表项 -->
          <div v-else-if="filteredTemplates.length > 0">
            <div
              v-for="template in filteredTemplates"
              :key="template.id"
              @click="selectTemplate(template)"
              :class="[
                'p-4 border-b border-gray-200 cursor-pointer hover:bg-gray-50 transition-colors',
                selectedTemplate?.id === template.id ? 'bg-blue-50 border-l-4 border-l-blue-600' : ''
              ]"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1 min-w-0">
                  <h3 class="text-sm font-medium text-gray-900 truncate">{{ template.title }}</h3>
                  <p class="text-xs text-gray-500 mt-1 line-clamp-2">{{ template.description || '无描述' }}</p>
                  <div class="flex items-center space-x-2 mt-2">
                    <span class="px-2 py-0.5 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                      {{ template.contentData?.language || 'N/A' }}
                    </span>
                    <span class="text-xs text-gray-400">{{ formatDate(template.updatedAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="flex flex-col items-center justify-center py-12 px-4">
            <svg class="h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <h3 class="mt-2 text-sm font-medium text-gray-900">暂无算法模版</h3>
            <p class="mt-1 text-xs text-gray-500 text-center">点击上方按钮创建第一个模版</p>
          </div>
        </div>
      </div>

      <!-- 右侧：详情编辑面板 -->
      <div class="flex-1 overflow-y-auto bg-gray-50">
        <!-- 未选中任何模版 -->
        <div v-if="!selectedTemplate" class="flex items-center justify-center h-full">
          <div class="text-center text-gray-400">
            <svg class="mx-auto h-16 w-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="mt-4 text-sm">请从左侧选择一个模版</p>
          </div>
        </div>

        <!-- 模版详情编辑区域 -->
        <div v-else class="p-6">
          <div class="max-w-4xl mx-auto">
            <!-- 操作按钮栏 -->
            <div class="flex justify-between items-center mb-6">
              <h2 class="text-xl font-bold text-gray-900">
                {{ isEditing ? '编辑模版' : '查看模版' }}
              </h2>
              <div class="flex space-x-3">
                <button
                  v-if="!isEditing"
                  @click="startEdit"
                  class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 text-sm"
                >
                  编辑
                </button>
                <template v-else>
                  <button
                    @click="cancelEdit"
                    class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 text-sm"
                  >
                    取消
                  </button>
                  <button
                    @click="saveTemplate"
                    :disabled="saving"
                    class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-blue-300 text-sm"
                  >
                    {{ saving ? '保存中...' : '保存' }}
                  </button>
                </template>
                <button
                  @click="confirmDelete"
                  class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 text-sm"
                >
                  删除
                </button>
              </div>
            </div>

            <!-- 表单 -->
            <div class="bg-white rounded-lg shadow p-6 space-y-6">
              <!-- 标题 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">模版名称</label>
                <input
                  v-model="editForm.title"
                  :disabled="!isEditing"
                  type="text"
                  placeholder="例如：滑动窗口算法模版"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
                />
              </div>

              <!-- 描述 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">描述</label>
                <textarea
                  v-model="editForm.description"
                  :disabled="!isEditing"
                  rows="2"
                  placeholder="简短描述这个算法模版的用途"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
                ></textarea>
              </div>

              <!-- 语言 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">编程语言</label>
                <select
                  v-model="editForm.contentData.language"
                  :disabled="!isEditing"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
                >
                  <option value="java">Java</option>
                  <option value="python">Python</option>
                  <option value="javascript">JavaScript</option>
                  <option value="cpp">C++</option>
                  <option value="go">Go</option>
                </select>
              </div>

              <!-- 代码 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">模版代码</label>
                <textarea
                  v-model="editForm.contentData.code"
                  :disabled="!isEditing"
                  rows="15"
                  placeholder="粘贴算法模版代码..."
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm disabled:bg-gray-50 disabled:text-gray-500"
                ></textarea>
              </div>

              <!-- 复杂度 -->
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">时间复杂度</label>
                  <input
                    v-model="editForm.contentData.complexity.time"
                    :disabled="!isEditing"
                    type="text"
                    placeholder="例如：O(N)"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">空间复杂度</label>
                  <input
                    v-model="editForm.contentData.complexity.space"
                    :disabled="!isEditing"
                    type="text"
                    placeholder="例如：O(1)"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
                  />
                </div>
              </div>

              <!-- 关键点 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">关键注意点</label>
                <div class="space-y-2">
                  <div
                    v-for="(point, index) in editForm.contentData.keyPoints"
                    :key="index"
                    class="flex items-start space-x-2"
                  >
                    <span class="text-blue-600 mt-2">•</span>
                    <input
                      v-model="editForm.contentData.keyPoints[index]"
                      :disabled="!isEditing"
                      type="text"
                      placeholder="输入关键点"
                      class="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
                    />
                    <button
                      v-if="isEditing"
                      @click="removeKeyPoint(index)"
                      class="text-red-600 hover:text-red-800 mt-2"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                  <button
                    v-if="isEditing"
                    @click="addKeyPoint"
                    class="text-sm text-blue-600 hover:text-blue-800 flex items-center space-x-1"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                    </svg>
                    <span>添加关键点</span>
                  </button>
                </div>
              </div>

              <!-- 来源链接 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">来源链接</label>
                <!-- 编辑模式：输入框 -->
                <input
                  v-if="isEditing"
                  v-model="editForm.url"
                  type="url"
                  placeholder="https://..."
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <!-- 查看模式：可点击链接 -->
                <div v-else class="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50">
                  <a
                    v-if="editForm.url"
                    :href="editForm.url"
                    target="_blank"
                    rel="noopener noreferrer"
                    class="text-blue-600 hover:text-blue-800 hover:underline flex items-center space-x-1"
                  >
                    <span>{{ editForm.url }}</span>
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                    </svg>
                  </a>
                  <span v-else class="text-gray-400">未设置</span>
                </div>
              </div>

              <!-- 关联试题 -->
              <div>
                <div class="flex items-center justify-between mb-3">
                  <label class="block text-sm font-medium text-gray-700">关联试题</label>
                  <button
                    @click="showAddQuestionDialog = true"
                    class="px-3 py-1.5 bg-blue-600 text-white rounded-md hover:bg-blue-700 text-sm flex items-center space-x-1"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                    </svg>
                    <span>添加试题</span>
                  </button>
                </div>

                <!-- 关联试题列表 -->
                <div v-if="loadingQuestions" class="text-center py-4">
                  <svg class="animate-spin h-6 w-6 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                </div>
                <div v-else-if="relatedQuestions.length === 0" class="text-center py-8 border-2 border-dashed border-gray-300 rounded-md">
                  <svg class="mx-auto h-10 w-10 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <p class="mt-2 text-sm text-gray-500">暂无关联试题</p>
                  <p class="text-xs text-gray-400 mt-1">点击上方按钮添加相关编程题</p>
                </div>
                <div v-else class="space-y-2">
                  <div
                    v-for="question in relatedQuestions"
                    :key="question.id"
                    class="flex items-center justify-between p-3 border border-gray-200 rounded-md hover:bg-gray-50"
                  >
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center space-x-2">
                        <span v-if="question.leetcodeNumber" class="text-xs font-semibold text-gray-500">
                          #{{ question.leetcodeNumber }}
                        </span>
                        <h4 class="text-sm font-medium text-gray-900 truncate">{{ question.questionTitle }}</h4>
                      </div>
                      <div class="flex items-center space-x-2 mt-1">
                        <span
                          v-if="question.difficulty"
                          :class="[
                            'px-2 py-0.5 text-xs font-semibold rounded',
                            question.difficulty === 'EASY' ? 'bg-green-100 text-green-800' :
                            question.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-red-100 text-red-800'
                          ]"
                        >
                          {{ question.difficulty === 'EASY' ? '简单' : question.difficulty === 'MEDIUM' ? '中等' : '困难' }}
                        </span>
                      </div>
                    </div>
                    <button
                      @click="removeQuestion(question.questionId)"
                      class="ml-3 text-red-600 hover:text-red-800"
                      title="移除关联"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <ConfirmDialog
      v-if="showDeleteConfirm"
      title="删除算法模版"
      :message="`确定要删除模版「${selectedTemplate?.title}」吗？此操作不可恢复。`"
      confirmText="删除"
      cancelText="取消"
      @confirm="handleDelete"
      @cancel="showDeleteConfirm = false"
    />

    <!-- 添加试题对话框 -->
    <div
      v-if="showAddQuestionDialog"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click.self="showAddQuestionDialog = false"
    >
      <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[80vh] flex flex-col">
        <!-- 对话框标题 -->
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">添加关联试题</h3>
        </div>

        <!-- 搜索栏 -->
        <div class="px-6 py-4 border-b border-gray-200">
          <input
            v-model="questionSearchQuery"
            type="text"
            placeholder="搜索试题标题或LeetCode题号..."
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            @input="searchQuestions"
          />
        </div>

        <!-- 试题列表 -->
        <div class="flex-1 overflow-y-auto px-6 py-4">
          <div v-if="searchingQuestions" class="text-center py-8">
            <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-2 text-sm text-gray-500">搜索中...</p>
          </div>
          <div v-else-if="availableQuestions.length === 0" class="text-center py-8">
            <p class="text-sm text-gray-500">没有找到相关试题</p>
          </div>
          <div v-else class="space-y-2">
            <div
              v-for="question in availableQuestions"
              :key="question.id"
              @click="addQuestion(question.id)"
              class="p-3 border border-gray-200 rounded-md hover:bg-blue-50 hover:border-blue-300 cursor-pointer transition-colors"
            >
              <div class="flex items-center justify-between">
                <div class="flex-1 min-w-0">
                  <div class="flex items-center space-x-2">
                    <span v-if="question.leetcodeNumber" class="text-xs font-semibold text-gray-500">
                      #{{ question.leetcodeNumber }}
                    </span>
                    <h4 class="text-sm font-medium text-gray-900 truncate">{{ question.title }}</h4>
                  </div>
                  <div class="flex items-center space-x-2 mt-1">
                    <span
                      v-if="question.difficulty"
                      :class="[
                        'px-2 py-0.5 text-xs font-semibold rounded',
                        question.difficulty === 'EASY' ? 'bg-green-100 text-green-800' :
                        question.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' :
                        'bg-red-100 text-red-800'
                      ]"
                    >
                      {{ question.difficulty === 'EASY' ? '简单' : question.difficulty === 'MEDIUM' ? '中等' : '困难' }}
                    </span>
                  </div>
                </div>
                <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- 对话框按钮 -->
        <div class="px-6 py-4 border-t border-gray-200 flex justify-end">
          <button
            @click="showAddQuestionDialog = false"
            class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
          >
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import learningContentApi from '@/api/learningContentApi'
import templateQuestionApi from '@/api/templateQuestionApi'
import questionApi from '@/api/questionApi'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

// 状态
const loading = ref(false)
const saving = ref(false)
const templates = ref([])
const searchQuery = ref('')
const selectedTemplate = ref(null)
const isEditing = ref(false)
const showDeleteConfirm = ref(false)

// 关联试题相关状态
const showAddQuestionDialog = ref(false)
const loadingQuestions = ref(false)
const searchingQuestions = ref(false)
const relatedQuestions = ref([])
const availableQuestions = ref([])
const questionSearchQuery = ref('')

// 编辑表单
const editForm = ref({
  title: '',
  description: '',
  url: '',
  contentData: {
    language: 'java',
    code: '',
    complexity: {
      time: '',
      space: ''
    },
    keyPoints: []
  }
})

// 计算属性
const filteredTemplates = computed(() => {
  if (!searchQuery.value) return templates.value
  const query = searchQuery.value.toLowerCase()
  return templates.value.filter(t =>
    t.title.toLowerCase().includes(query) ||
    t.description?.toLowerCase().includes(query)
  )
})

// 加载模版列表
const loadTemplates = async () => {
  try {
    loading.value = true
    const data = await learningContentApi.getTemplates()
    templates.value = data || []
  } catch (error) {
    console.error('加载模版列表失败:', error)
    alert('加载模版列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 选择模版
const selectTemplate = (template) => {
  selectedTemplate.value = template
  isEditing.value = false
  loadEditForm(template)
  loadRelatedQuestions(template.id)
}

// 加载编辑表单
const loadEditForm = (template) => {
  editForm.value = {
    title: template.title || '',
    description: template.description || '',
    url: template.url || '',
    contentData: {
      language: template.contentData?.language || 'java',
      code: template.contentData?.code || '',
      complexity: {
        time: template.contentData?.complexity?.time || '',
        space: template.contentData?.complexity?.space || ''
      },
      keyPoints: template.contentData?.keyPoints || []
    }
  }
}

// 创建新模版
const createNewTemplate = () => {
  selectedTemplate.value = { id: null }
  isEditing.value = true
  editForm.value = {
    title: '',
    description: '',
    url: '',
    contentData: {
      language: 'java',
      code: '',
      complexity: {
        time: '',
        space: ''
      },
      keyPoints: []
    }
  }
}

// 开始编辑
const startEdit = () => {
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  if (selectedTemplate.value?.id) {
    // 已存在的模版，恢复原数据
    loadEditForm(selectedTemplate.value)
    isEditing.value = false
  } else {
    // 新建模版，取消选择
    selectedTemplate.value = null
    isEditing.value = false
  }
}

// 保存模版
const saveTemplate = async () => {
  try {
    // 验证
    if (!editForm.value.title.trim()) {
      alert('请输入模版名称')
      return
    }

    saving.value = true

    const templateData = {
      contentType: 'template',
      title: editForm.value.title,
      description: editForm.value.description,
      url: editForm.value.url,
      author: 'admin',
      contentData: JSON.stringify(editForm.value.contentData),
      visibility: 'public',
      sortOrder: 0
    }

    if (selectedTemplate.value?.id) {
      // 更新
      await learningContentApi.updateContent(selectedTemplate.value.id, templateData)
    } else {
      // 创建
      await learningContentApi.createTemplate(templateData)
    }

    await loadTemplates()
    isEditing.value = false

    // 重新选中更新后的模版
    if (selectedTemplate.value?.id) {
      const updated = templates.value.find(t => t.id === selectedTemplate.value.id)
      if (updated) selectTemplate(updated)
    } else {
      // 新建的模版，选中第一个
      if (templates.value.length > 0) {
        selectTemplate(templates.value[0])
      }
    }
  } catch (error) {
    console.error('保存模版失败:', error)
    alert('保存模版失败: ' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

// 确认删除
const confirmDelete = () => {
  showDeleteConfirm.value = true
}

// 删除模版
const handleDelete = async () => {
  try {
    await learningContentApi.deleteContent(selectedTemplate.value.id)
    await loadTemplates()
    selectedTemplate.value = null
    showDeleteConfirm.value = false
  } catch (error) {
    console.error('删除模版失败:', error)
    alert('删除模版失败: ' + (error.message || '未知错误'))
  }
}

// 关键点管理
const addKeyPoint = () => {
  editForm.value.contentData.keyPoints.push('')
}

const removeKeyPoint = (index) => {
  editForm.value.contentData.keyPoints.splice(index, 1)
}

// 加载关联试题
const loadRelatedQuestions = async (templateId) => {
  if (!templateId) {
    relatedQuestions.value = []
    return
  }

  try {
    loadingQuestions.value = true
    const data = await templateQuestionApi.getQuestionsByTemplate(templateId)
    relatedQuestions.value = data || []
  } catch (error) {
    console.error('加载关联试题失败:', error)
    relatedQuestions.value = []
  } finally {
    loadingQuestions.value = false
  }
}

// 搜索可用试题
const searchQuestions = async () => {
  const query = questionSearchQuery.value.trim()
  if (!query) {
    availableQuestions.value = []
    return
  }

  try {
    searchingQuestions.value = true
    // 搜索所有编程题（PROGRAMMING类型）
    const allQuestions = await questionApi.getQuestions()

    // 过滤编程题并排除已关联的
    const relatedIds = new Set(relatedQuestions.value.map(q => q.questionId))
    const queryLower = query.toLowerCase()

    availableQuestions.value = allQuestions
      .filter(q => q.type === 'PROGRAMMING')
      .filter(q => !relatedIds.has(q.id))
      .filter(q => {
        const titleMatch = q.title.toLowerCase().includes(queryLower)
        const numberMatch = q.leetcodeNumber && q.leetcodeNumber.toString().includes(query)
        return titleMatch || numberMatch
      })
      .slice(0, 20) // 限制显示20个
  } catch (error) {
    console.error('搜索试题失败:', error)
    availableQuestions.value = []
  } finally {
    searchingQuestions.value = false
  }
}

// 添加试题关联
const addQuestion = async (questionId) => {
  if (!selectedTemplate.value) return

  try {
    await templateQuestionApi.addTemplateQuestion(
      selectedTemplate.value.id,
      questionId
    )

    // 重新加载关联试题
    await loadRelatedQuestions(selectedTemplate.value.id)

    // 关闭对话框并重置搜索
    showAddQuestionDialog.value = false
    questionSearchQuery.value = ''
    availableQuestions.value = []
  } catch (error) {
    console.error('添加试题关联失败:', error)
    alert('添加失败: ' + (error.message || '未知错误'))
  }
}

// 移除试题关联
const removeQuestion = async (questionId) => {
  if (!selectedTemplate.value) return

  if (!confirm('确定要移除这个试题的关联吗？')) {
    return
  }

  try {
    await templateQuestionApi.removeTemplateQuestion(
      selectedTemplate.value.id,
      questionId
    )

    // 重新加载关联试题
    await loadRelatedQuestions(selectedTemplate.value.id)
  } catch (error) {
    console.error('移除试题关联失败:', error)
    alert('移除失败: ' + (error.message || '未知错误'))
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    month: '2-digit',
    day: '2-digit'
  })
}

// 初始化
onMounted(() => {
  loadTemplates()
})
</script>
