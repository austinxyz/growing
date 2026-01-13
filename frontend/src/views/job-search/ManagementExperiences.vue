<template>
  <div class="management-experiences h-full flex bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 左侧：按Focus Area分组的经验列表 -->
    <div class="w-80 bg-white border-r border-gray-200 flex flex-col shadow-lg">
      <div class="bg-gradient-to-r from-blue-600 to-purple-600 p-4 border-b border-purple-700">
        <h2 class="text-lg font-bold text-white mb-3">👥 人员管理经验</h2>
        <button
          @click="showCreateModal = true"
          class="w-full px-4 py-2.5 bg-gradient-to-r from-green-500 to-emerald-500 text-white rounded-lg hover:from-green-600 hover:to-emerald-600 hover:shadow-lg transition-all flex items-center justify-center gap-2 font-semibold shadow-md text-sm"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          新建经验
        </button>
      </div>

      <div class="flex-1 overflow-y-auto">
        <div class="p-2">
          <!-- 树形结构：Focus Area → 经验名称 -->
          <div v-for="focusArea in focusAreasWithExperiences" :key="focusArea.id" class="mb-1">
            <!-- 第一层：Focus Area -->
            <div class="flex items-center px-2 py-1.5 rounded hover:bg-gray-100 transition-colors group">
              <!-- 展开/折叠图标 -->
              <svg
                @click="toggleFocusArea(focusArea.id)"
                class="w-4 h-4 mr-1 text-gray-500 flex-shrink-0 transition-transform cursor-pointer"
                :class="{ 'rotate-90': expandedFocusAreas.has(focusArea.id) }"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
              <!-- Focus Area 名称 -->
              <span
                @click="toggleFocusArea(focusArea.id)"
                class="text-sm font-medium text-gray-700 truncate flex-1 cursor-pointer"
              >
                {{ focusArea.name }}
                <span class="ml-1 text-xs text-gray-500">({{ focusArea.experiences.length }})</span>
              </span>
            </div>

            <!-- 第二层：经验列表（子节点） -->
            <div v-if="expandedFocusAreas.has(focusArea.id)" class="ml-5 mt-1 space-y-1">
              <div
                v-for="exp in focusArea.experiences"
                :key="exp.id"
                @click.stop="selectExperience(exp.id)"
                :class="[
                  'flex items-center px-2 py-1.5 rounded cursor-pointer transition-all duration-200',
                  selectedExpId === exp.id
                    ? 'bg-gradient-to-r from-blue-50 to-purple-50 border-l-4 border-l-blue-600 shadow-md text-blue-700 font-medium'
                    : 'text-gray-600 hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-sm'
                ]"
              >
                <!-- 经验图标 -->
                <svg class="w-3.5 h-3.5 mr-1.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <!-- 经验名称 -->
                <div class="flex-1 min-w-0">
                  <div class="text-sm truncate">{{ exp.experienceName }}</div>
                  <div class="text-xs text-gray-500 mt-0.5">
                    {{ formatDate(exp.startDate) }} - {{ exp.endDate ? formatDate(exp.endDate) : '进行中' }}
                  </div>
                </div>
              </div>

              <!-- 无经验提示 -->
              <div v-if="!focusArea.experiences || focusArea.experiences.length === 0" class="text-xs text-gray-400 px-2 py-2">
                暂无经验
              </div>
            </div>
          </div>

          <!-- 无数据提示 -->
          <div v-if="!focusAreasWithExperiences.length" class="text-center text-gray-500 py-8 text-sm">
            暂无经验数据
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧：经验详情 (STARL格式) -->
    <div class="flex-1 flex flex-col">
      <div v-if="currentExperience" class="flex-1 flex flex-col">
        <!-- 顶部表头：标题 + 基本信息 -->
        <div class="bg-gradient-to-r from-blue-600 to-purple-600 border-b border-purple-700 p-4 shadow-lg">
          <div class="flex items-center justify-between mb-3">
            <h1 class="text-xl font-bold text-white">{{ currentExperience.experienceName }}</h1>
            <div class="flex gap-2">
              <button
                @click="toggleEdit('basicInfo')"
                class="px-3 py-1.5 text-sm bg-gradient-to-r from-indigo-500 to-purple-500 text-white rounded hover:from-indigo-600 hover:to-purple-600 hover:shadow-lg transition-all font-semibold shadow-md"
              >
                {{ editModes.basicInfo ? '取消编辑' : '编辑基本信息' }}
              </button>
              <button
                @click="deleteExperience"
                class="px-3 py-1.5 text-sm bg-gradient-to-r from-red-500 to-pink-500 text-white rounded hover:from-red-600 hover:to-pink-600 hover:shadow-lg transition-all font-semibold shadow-md"
              >
                删除
              </button>
            </div>
          </div>

          <!-- 基本信息 - 浏览模式 -->
          <div v-if="!editModes.basicInfo" class="grid grid-cols-3 gap-4 text-sm">
            <div>
              <span class="text-blue-100">Focus Area:</span>
              <span class="ml-2 font-semibold text-white">{{ getFocusAreaName(currentExperience.focusAreaId) }}</span>
            </div>
            <div>
              <span class="text-blue-100">时间:</span>
              <span class="ml-2 font-semibold text-white">
                {{ formatDate(currentExperience.startDate) }} - {{ currentExperience.endDate ? formatDate(currentExperience.endDate) : '进行中' }}
              </span>
            </div>
            <div v-if="currentExperience.teamGrowthSubtype">
              <span class="text-blue-100">类型:</span>
              <span class="ml-2 font-semibold text-white">{{ currentExperience.teamGrowthSubtype }}</span>
            </div>
          </div>

          <!-- 基本信息 - 编辑模式 -->
          <div v-else class="grid grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">经验名称</label>
              <input
                v-model="editBasicInfo.experienceName"
                type="text"
                class="w-full px-3 py-2 text-sm border border-gray-300 rounded"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">开始时间</label>
              <input
                v-model="editBasicInfo.startDate"
                type="month"
                class="w-full px-3 py-2 text-sm border border-gray-300 rounded"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">结束时间</label>
              <input
                v-model="editBasicInfo.endDate"
                type="month"
                class="w-full px-3 py-2 text-sm border border-gray-300 rounded"
              />
            </div>
            <div class="col-span-3 flex justify-end">
              <button
                @click="saveBasicInfo"
                class="px-4 py-2 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
              >
                保存
              </button>
            </div>
          </div>
        </div>

        <!-- STARL卡片区域 - 2x2 布局 -->
        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-2 gap-4">
            <!-- 卡片1: ST - 背景和挑战 -->
            <STARLCard
              title="ST - 背景和挑战"
              :content="currentExperience.background"
              :is-editing="editModes.background"
              @toggle-edit="toggleEdit('background')"
              @save="saveField('background', $event)"
              color="blue"
            />

            <!-- 卡片2: A - 采取的行动 -->
            <STARLCard
              title="A - 采取的行动"
              :content="currentExperience.actionsTaken"
              :is-editing="editModes.actionsTaken"
              @toggle-edit="toggleEdit('actionsTaken')"
              @save="saveField('actionsTaken', $event)"
              color="green"
            />

            <!-- 卡片3: R - 结果和影响 -->
            <STARLCard
              title="R - 结果和影响"
              :content="currentExperience.results"
              :is-editing="editModes.results"
              @toggle-edit="toggleEdit('results')"
              @save="saveField('results', $event)"
              color="orange"
            />

            <!-- 卡片4: L - 经验教训 -->
            <STARLCard
              title="L - 经验教训"
              :content="currentExperience.lessonsLearned"
              :is-editing="editModes.lessonsLearned"
              @toggle-edit="toggleEdit('lessonsLearned')"
              @save="saveField('lessonsLearned', $event)"
              color="pink"
            />
          </div>
        </div>
      </div>

      <div v-else class="flex-1 flex items-center justify-center">
        <div class="text-center text-gray-500">
          <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p class="text-lg">请选择一个经验查看详情</p>
        </div>
      </div>
    </div>

    <!-- 创建经验 Modal -->
    <div v-if="showCreateModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-[700px] max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">新建管理经验</h3>
        <div class="space-y-3">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">经验名称 *</label>
            <input
              v-model="formData.experienceName"
              type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              placeholder="例如: 提升团队士气的季度团建"
              required
            />
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Focus Area *</label>
              <select
                v-model="formData.focusAreaId"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
                required
              >
                <option value="">请选择</option>
                <option v-for="fa in focusAreas" :key="fa.id" :value="fa.id">
                  {{ fa.name }}
                </option>
              </select>
            </div>

            <div v-if="needsTeamGrowthSubtype">
              <label class="block text-sm font-medium text-gray-700 mb-1">Team Growth类型</label>
              <select
                v-model="formData.teamGrowthSubtype"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              >
                <option value="">请选择</option>
                <option value="Hiring">Hiring</option>
                <option value="HighPerformer">High Performer</option>
                <option value="LowPerformer">Low Performer</option>
              </select>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">开始时间</label>
              <input
                v-model="formData.startDate"
                type="month"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">结束时间</label>
              <input
                v-model="formData.endDate"
                type="month"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">ST - 背景和挑战</label>
            <textarea
              v-model="formData.background"
              rows="4"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              placeholder="描述当时的背景、面临的挑战和问题..."
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">A - 采取的行动</label>
            <textarea
              v-model="formData.actionsTaken"
              rows="4"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              placeholder="描述你采取的具体行动和措施..."
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">R - 结果和影响</label>
            <textarea
              v-model="formData.results"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              placeholder="描述最终的结果、数据和影响..."
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">L - 经验教训</label>
            <textarea
              v-model="formData.lessonsLearned"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              placeholder="总结你学到的经验和教训..."
            ></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button
            @click="closeCreateModal"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="createExperience"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
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
import { managementApi } from '@/api/managementApi'
import skillApi from '@/api/skillApi'
import STARLCard from '@/components/STARLCard.vue'

// 人员管理 Skill ID (从数据库查询得知是9)
const PEOPLE_MANAGEMENT_SKILL_ID = 9

const experiences = ref([])
const focusAreas = ref([])
const selectedExpId = ref(null)
const showCreateModal = ref(false)
const expandedFocusAreas = ref(new Set())

const editModes = ref({
  basicInfo: false,
  background: false,
  actionsTaken: false,
  results: false,
  lessonsLearned: false
})

const cardStates = ref({
  basicInfo: true,
  st: true,
  a: true,
  r: true,
  l: true
})

const editBasicInfo = ref({
  experienceName: '',
  startDate: '',
  endDate: ''
})

const formData = ref({
  experienceName: '',
  focusAreaId: '',
  teamGrowthSubtype: '',
  startDate: '',
  endDate: '',
  background: '',
  actionsTaken: '',
  results: '',
  lessonsLearned: ''
})

// 当前选中的经验
const currentExperience = computed(() => {
  return experiences.value.find(e => e.id === selectedExpId.value) || null
})

// Focus Areas with experiences grouped
const focusAreasWithExperiences = computed(() => {
  return focusAreas.value.map(fa => ({
    ...fa,
    experiences: experiences.value
      .filter(exp => exp.focusAreaId === fa.id)
      .sort((a, b) => {
        // 按时间倒序
        const dateA = new Date(b.startDate || '1900-01-01')
        const dateB = new Date(a.startDate || '1900-01-01')
        return dateB - dateA
      })
  }))
})

// 是否需要Team Growth子类型
const needsTeamGrowthSubtype = computed(() => {
  const selectedFA = focusAreas.value.find(fa => fa.id === formData.value.focusAreaId)
  if (!selectedFA) return false
  // Team Growth相关的Focus Areas需要子类型
  const teamGrowthAreas = ['High Performer', 'Low Performer', 'Hiring']
  return teamGrowthAreas.includes(selectedFA.name)
})

onMounted(async () => {
  await loadFocusAreas()
  await loadExperiences()
  // 默认展开所有Focus Areas
  expandedFocusAreas.value = new Set(focusAreas.value.map(fa => fa.id))
})

// 加载Focus Areas
const loadFocusAreas = async () => {
  try {
    const skill = await skillApi.getSkillById(PEOPLE_MANAGEMENT_SKILL_ID)
    focusAreas.value = skill.focusAreas || []
  } catch (error) {
    console.error('加载Focus Areas失败:', error)
  }
}

// 加载所有经验
const loadExperiences = async () => {
  try {
    const data = await managementApi.getExperiences()
    experiences.value = data || []
  } catch (error) {
    console.error('加载管理经验失败:', error)
  }
}

// 切换Focus Area展开/折叠
const toggleFocusArea = (focusAreaId) => {
  if (expandedFocusAreas.value.has(focusAreaId)) {
    expandedFocusAreas.value.delete(focusAreaId)
  } else {
    expandedFocusAreas.value.add(focusAreaId)
  }
}

// 选择经验
const selectExperience = async (expId) => {
  selectedExpId.value = expId
  // 重置所有编辑模式
  Object.keys(editModes.value).forEach(key => {
    editModes.value[key] = false
  })
}

// 获取Focus Area名称
const getFocusAreaName = (focusAreaId) => {
  const fa = focusAreas.value.find(f => f.id === focusAreaId)
  return fa ? fa.name : '未知'
}

// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  if (typeof dateStr === 'string' && dateStr.length >= 7) {
    return dateStr.substring(0, 7) // YYYY-MM
  }
  return dateStr
}

// 切换编辑模式
const toggleEdit = (field) => {
  if (field === 'basicInfo') {
    if (!editModes.value[field]) {
      // 进入编辑模式，初始化编辑数据
      editBasicInfo.value = {
        experienceName: currentExperience.value.experienceName,
        startDate: formatDate(currentExperience.value.startDate),
        endDate: currentExperience.value.endDate ? formatDate(currentExperience.value.endDate) : ''
      }
    }
  }
  editModes.value[field] = !editModes.value[field]
}

// 保存单个字段
const saveField = async (field, newValue) => {
  try {
    const updateData = {
      ...currentExperience.value,
      [field]: newValue
    }
    await managementApi.updateExperience(selectedExpId.value, updateData)

    // 更新本地数据
    const index = experiences.value.findIndex(e => e.id === selectedExpId.value)
    if (index !== -1) {
      experiences.value[index] = { ...experiences.value[index], [field]: newValue }
    }

    editModes.value[field] = false
    alert('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

// 保存基本信息
const saveBasicInfo = async () => {
  try {
    const updateData = {
      ...currentExperience.value,
      experienceName: editBasicInfo.value.experienceName,
      startDate: editBasicInfo.value.startDate ? `${editBasicInfo.value.startDate}-01` : null,
      endDate: editBasicInfo.value.endDate ? `${editBasicInfo.value.endDate}-01` : null
    }
    await managementApi.updateExperience(selectedExpId.value, updateData)

    // 更新本地数据
    const index = experiences.value.findIndex(e => e.id === selectedExpId.value)
    if (index !== -1) {
      experiences.value[index] = {
        ...experiences.value[index],
        experienceName: editBasicInfo.value.experienceName,
        startDate: updateData.startDate,
        endDate: updateData.endDate
      }
    }

    editModes.value.basicInfo = false
    alert('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

// 创建新经验
const createExperience = async () => {
  if (!formData.value.experienceName || !formData.value.focusAreaId) {
    alert('请填写经验名称和Focus Area')
    return
  }

  try {
    // 转换日期格式
    const payload = {
      ...formData.value,
      startDate: formData.value.startDate ? `${formData.value.startDate}-01` : null,
      endDate: formData.value.endDate ? `${formData.value.endDate}-01` : null
    }

    const newExperience = await managementApi.createExperience(payload)
    alert('创建成功')
    closeCreateModal()
    await loadExperiences()
    // 自动选中新创建的经验
    if (newExperience && newExperience.id) {
      selectedExpId.value = newExperience.id
    }
  } catch (error) {
    console.error('创建经验失败:', error)
    alert('创建失败')
  }
}

// 删除经验
const deleteExperience = async () => {
  if (!confirm('确定要删除这个经验吗?')) return

  try {
    await managementApi.deleteExperience(selectedExpId.value)
    selectedExpId.value = null
    await loadExperiences()
    alert('删除成功')
  } catch (error) {
    console.error('删除经验失败:', error)
    alert('删除失败')
  }
}

// 关闭创建Modal
const closeCreateModal = () => {
  showCreateModal.value = false
  formData.value = {
    experienceName: '',
    focusAreaId: '',
    teamGrowthSubtype: '',
    startDate: '',
    endDate: '',
    background: '',
    actionsTaken: '',
    results: '',
    lessonsLearned: ''
  }
}
</script>
