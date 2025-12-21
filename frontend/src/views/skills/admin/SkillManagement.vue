<template>
  <div class="flex h-screen bg-background">
    <!-- 左侧：职业路径Tab + 技能列表 -->
    <div class="w-96 border-r border-border flex flex-col bg-card">
      <!-- Header -->
      <div class="p-6 border-b border-border">
        <h1 class="text-2xl font-bold text-foreground mb-2">技能管理</h1>
        <p class="text-sm text-muted-foreground">按职业路径管理技能</p>
      </div>

      <!-- 职业路径 Tabs -->
      <div class="border-b border-border">
        <div class="flex flex-col">
          <button
            v-for="careerPath in careerPaths"
            :key="careerPath.id"
            @click="activeCareerPathId = careerPath.id"
            :class="[
              'px-6 py-3 text-left font-medium text-sm transition-colors border-l-4',
              activeCareerPathId === careerPath.id
                ? 'border-primary text-primary bg-primary/5'
                : 'border-transparent text-muted-foreground hover:text-foreground hover:bg-accent/50'
            ]"
          >
            <div class="flex items-center justify-between">
              <span>{{ careerPath.name }}</span>
              <span class="px-2 py-0.5 text-xs rounded-full bg-primary/10 text-primary">
                {{ getSkillCount(careerPath.id) }}
              </span>
            </div>
          </button>
        </div>
      </div>

      <!-- 技能列表 -->
      <div class="flex-1 overflow-y-auto">
        <div v-if="loadingSkills" class="p-6 text-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
        </div>

        <div v-else-if="currentSkills.length === 0" class="p-6 text-center text-muted-foreground">
          该职业路径暂无技能
        </div>

        <div v-else class="p-4 space-y-2">
          <button
            v-for="skill in currentSkills"
            :key="skill.id"
            @click="selectedSkillId = skill.id"
            :class="[
              'w-full text-left p-4 rounded-lg border transition-all',
              selectedSkillId === skill.id
                ? 'border-primary bg-primary/5 shadow-sm'
                : 'border-border hover:border-primary/50 hover:bg-accent/50'
            ]"
          >
            <div class="flex items-center space-x-3">
              <div class="text-2xl flex-shrink-0">{{ skill.icon || '📚' }}</div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center space-x-2 mb-1">
                  <h3 class="text-sm font-semibold text-foreground truncate">{{ skill.name }}</h3>
                  <span v-if="skill.isImportant" class="flex-shrink-0 text-xs bg-destructive/10 text-destructive px-1.5 py-0.5 rounded">
                    重要
                  </span>
                </div>
                <p class="text-xs text-muted-foreground line-clamp-2">{{ skill.description }}</p>
              </div>
            </div>
          </button>
        </div>
      </div>

      <!-- 创建技能按钮 -->
      <div class="p-4 border-t border-border">
        <button
          @click="showCreateModal = true"
          class="w-full px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium"
        >
          + 创建新技能
        </button>
      </div>
    </div>

    <!-- 右侧：技能详情 -->
    <div class="flex-1 overflow-y-auto">
      <div v-if="!selectedSkillId" class="h-full flex items-center justify-center">
        <div class="text-center">
          <div class="text-6xl mb-4">📚</div>
          <p class="text-lg text-muted-foreground">选择一个技能查看详情</p>
        </div>
      </div>

      <div v-else-if="loadingDetail" class="p-12 text-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
      </div>

      <div v-else-if="selectedSkill" class="p-8 space-y-6 max-w-5xl">
        <!-- 基本信息 Card -->
        <div class="bg-card border border-border rounded-lg p-6">
          <div class="flex items-start justify-between mb-6">
            <div class="flex items-center space-x-4">
              <div class="text-5xl">{{ selectedSkill.icon || '📚' }}</div>
              <div>
                <h2 class="text-2xl font-bold text-foreground mb-1">{{ selectedSkill.name }}</h2>
                <div class="flex items-center space-x-2 text-sm text-muted-foreground">
                  <span v-if="selectedSkill.isImportant" class="px-2 py-1 bg-destructive/10 text-destructive rounded text-xs font-medium">
                    重要技能
                  </span>
                  <span>ID: {{ selectedSkill.id }}</span>
                </div>
              </div>
            </div>
            <div class="flex items-center space-x-2">
              <button
                @click="editSkill"
                class="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
              >
                编辑
              </button>
              <button
                @click="deleteSkill"
                class="px-4 py-2 bg-destructive text-destructive-foreground rounded-lg hover:bg-destructive/90 transition-colors"
              >
                删除
              </button>
            </div>
          </div>

          <div class="space-y-4">
            <div>
              <label class="text-sm font-medium text-muted-foreground">描述</label>
              <p class="mt-1 text-foreground">{{ selectedSkill.description || '暂无描述' }}</p>
            </div>

            <div>
              <label class="text-sm font-medium text-muted-foreground">关联职业路径</label>
              <div class="mt-2 flex flex-wrap gap-2">
                <span
                  v-for="cp in selectedSkill.careerPaths"
                  :key="cp.id"
                  class="px-3 py-1 bg-primary/10 text-primary rounded-full text-sm"
                >
                  {{ cp.name }}
                </span>
                <span v-if="!selectedSkill.careerPaths || selectedSkill.careerPaths.length === 0" class="text-muted-foreground text-sm">
                  未关联职业路径
                </span>
              </div>
            </div>

            <div class="grid grid-cols-3 gap-4 pt-4 border-t border-border">
              <div>
                <label class="text-xs text-muted-foreground">专注领域</label>
                <p class="text-2xl font-bold text-foreground">{{ selectedSkill.focusAreaCount || 0 }}</p>
              </div>
              <div>
                <label class="text-xs text-muted-foreground">学习资源</label>
                <p class="text-2xl font-bold text-foreground">{{ selectedSkill.resourceCount || 0 }}</p>
              </div>
              <div>
                <label class="text-xs text-muted-foreground">显示顺序</label>
                <p class="text-2xl font-bold text-foreground">{{ selectedSkill.displayOrder || 0 }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 专注领域 Card -->
        <div class="bg-card border border-border rounded-lg p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-bold text-foreground">专注领域 ({{ selectedSkill.focusAreas?.length || 0 }})</h3>
            <button
              @click="addFocusArea"
              class="px-3 py-1.5 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors text-sm"
            >
              + 添加领域
            </button>
          </div>

          <div v-if="!selectedSkill.focusAreas || selectedSkill.focusAreas.length === 0" class="text-center py-8 text-muted-foreground">
            暂无专注领域
          </div>

          <div v-else class="space-y-3">
            <div
              v-for="(area, index) in selectedSkill.focusAreas"
              :key="area.id"
              class="p-4 border border-border rounded-lg hover:border-primary/50 transition-colors"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <div class="flex items-center space-x-2 mb-1">
                    <span class="text-sm font-medium text-muted-foreground">#{{ index + 1 }}</span>
                    <h4 class="font-semibold text-foreground">{{ area.name }}</h4>
                  </div>
                  <p class="text-sm text-muted-foreground">{{ area.description }}</p>
                </div>
                <div class="flex items-center space-x-2 ml-4">
                  <button
                    @click="editFocusArea(area)"
                    class="text-sm text-primary hover:text-primary/80"
                  >
                    编辑
                  </button>
                  <button
                    @click="handleDeleteFocusArea(area.id)"
                    class="text-sm text-destructive hover:text-destructive/80"
                  >
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 学习资源 Card -->
        <div class="bg-card border border-border rounded-lg p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-bold text-foreground">学习资源 ({{ selectedSkill.learningResources?.length || 0 }})</h3>
            <button
              @click="addResource"
              class="px-3 py-1.5 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors text-sm"
            >
              + 添加资源
            </button>
          </div>

          <div v-if="!selectedSkill.learningResources || selectedSkill.learningResources.length === 0" class="text-center py-8 text-muted-foreground">
            暂无学习资源
          </div>

          <div v-else class="space-y-3">
            <div
              v-for="resource in selectedSkill.learningResources"
              :key="resource.id"
              class="p-4 border border-border rounded-lg hover:border-primary/50 transition-colors"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <div class="flex items-center space-x-2 mb-1">
                    <span class="px-2 py-0.5 bg-accent text-accent-foreground rounded text-xs font-medium">
                      {{ getResourceTypeLabel(resource.resourceType) }}
                    </span>
                    <span v-if="resource.isOfficial" class="px-2 py-0.5 bg-primary/10 text-primary rounded text-xs font-medium">
                      官方
                    </span>
                  </div>
                  <h4 class="font-semibold text-foreground mb-1">{{ resource.title }}</h4>
                  <p v-if="resource.description" class="text-sm text-muted-foreground mb-2">{{ resource.description }}</p>
                  <div class="flex items-center space-x-3 text-xs text-muted-foreground">
                    <span v-if="resource.author">作者: {{ resource.author }}</span>
                    <a v-if="resource.url" :href="resource.url" target="_blank" class="text-primary hover:underline">
                      查看链接 →
                    </a>
                  </div>
                </div>
                <div class="flex items-center space-x-2 ml-4">
                  <button
                    @click="editResource(resource)"
                    class="text-sm text-primary hover:text-primary/80"
                  >
                    编辑
                  </button>
                  <button
                    @click="handleDeleteResource(resource.id)"
                    class="text-sm text-destructive hover:text-destructive/80"
                  >
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建技能Modal -->
    <SkillEditModal
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @success="handleSkillCreated"
    />

    <!-- 编辑技能Modal -->
    <SkillEditModal
      v-if="showEditModal"
      :skill="selectedSkill"
      @close="showEditModal = false"
      @success="handleSkillUpdated"
    />

    <!-- 添加/编辑专注领域Modal -->
    <FocusAreaEditModal
      v-if="showFocusAreaModal"
      :skill-id="selectedSkillId"
      :focus-area="editingFocusArea"
      @close="closeFocusAreaModal"
      @success="handleFocusAreaSaved"
    />

    <!-- 添加/编辑学习资源Modal -->
    <LearningResourceEditModal
      v-if="showResourceModal"
      :skill-id="selectedSkillId"
      :resource="editingResource"
      @close="closeResourceModal"
      @success="handleResourceSaved"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getAllCareerPaths } from '@/api/careerPaths'
import { getAllSkills, getSkillByIdAdmin, deleteSkill as deleteSkillApi, deleteFocusArea, deleteResource } from '@/api/skills'
import SkillEditModal from '@/components/skills/admin/SkillEditModal.vue'
import FocusAreaEditModal from '@/components/skills/admin/FocusAreaEditModal.vue'
import LearningResourceEditModal from '@/components/skills/admin/LearningResourceEditModal.vue'

const router = useRouter()

const careerPaths = ref([])
const allSkills = ref([])
const activeCareerPathId = ref(null)
const selectedSkillId = ref(null)
const selectedSkill = ref(null)

const loadingSkills = ref(false)
const loadingDetail = ref(false)

const showCreateModal = ref(false)
const showEditModal = ref(false)
const showFocusAreaModal = ref(false)
const showResourceModal = ref(false)

const editingFocusArea = ref(null)
const editingResource = ref(null)

// 资源类型映射（英文枚举 -> 中文显示）
const resourceTypeMap = {
  'DOCUMENT': '文档',
  'VIDEO': '视频',
  'COURSE': '课程',
  'BOOK': '书籍',
  'BLOG': '博客',
  'PRACTICE': '练习',
  'TOOL': '工具',
  'ARTICLE': '文章',
  'WEBSITE': '网站',
  'OTHER': '其他'
}

// 获取资源类型的中文显示
const getResourceTypeLabel = (type) => {
  return resourceTypeMap[type] || type
}

// 获取当前职业路径的技能数量
const getSkillCount = (careerPathId) => {
  return allSkills.value.filter(skill =>
    skill.careerPaths?.some(cp => cp.id === careerPathId)
  ).length
}

// 当前职业路径的技能列表
const currentSkills = computed(() => {
  if (!activeCareerPathId.value) return []
  return allSkills.value.filter(skill =>
    skill.careerPaths?.some(cp => cp.id === activeCareerPathId.value)
  ).sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0))
})

// 加载职业路径
const loadCareerPaths = async () => {
  try {
    careerPaths.value = await getAllCareerPaths()
    if (careerPaths.value.length > 0) {
      activeCareerPathId.value = careerPaths.value[0].id
    }
  } catch (error) {
    console.error('加载职业路径失败:', error)
  }
}

// 加载所有技能
const loadSkills = async () => {
  loadingSkills.value = true
  try {
    const response = await getAllSkills()
    allSkills.value = response.data || response
  } catch (error) {
    console.error('加载技能失败:', error)
  } finally {
    loadingSkills.value = false
  }
}

// 加载技能详情
const loadSkillDetail = async (skillId) => {
  loadingDetail.value = true
  try {
    const response = await getSkillByIdAdmin(skillId)
    selectedSkill.value = response.data || response
  } catch (error) {
    console.error('加载技能详情失败:', error)
  } finally {
    loadingDetail.value = false
  }
}

// 监听选中的技能ID
watch(selectedSkillId, (newId) => {
  if (newId) {
    loadSkillDetail(newId)
  } else {
    selectedSkill.value = null
  }
})

// 编辑技能
const editSkill = () => {
  showEditModal.value = true
}

// 删除技能
const deleteSkill = async () => {
  if (!confirm(`确定要删除技能"${selectedSkill.value.name}"吗？`)) return

  try {
    await deleteSkillApi(selectedSkillId.value)
    alert('删除成功')
    selectedSkillId.value = null
    await loadSkills()
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请重试')
  }
}

// 添加专注领域
const addFocusArea = () => {
  editingFocusArea.value = null
  showFocusAreaModal.value = true
}

// 编辑专注领域
const editFocusArea = (area) => {
  editingFocusArea.value = area
  showFocusAreaModal.value = true
}

// 删除专注领域
const handleDeleteFocusArea = async (id) => {
  if (!confirm('确定要删除这个专注领域吗？')) return

  try {
    await deleteFocusArea(id)
    alert('删除成功')
    await loadSkillDetail(selectedSkillId.value)
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请重试')
  }
}

// 关闭专注领域弹窗
const closeFocusAreaModal = () => {
  showFocusAreaModal.value = false
  editingFocusArea.value = null
}

// 专注领域保存成功
const handleFocusAreaSaved = async () => {
  closeFocusAreaModal()
  await loadSkillDetail(selectedSkillId.value)
}

// 添加学习资源
const addResource = () => {
  editingResource.value = null
  showResourceModal.value = true
}

// 编辑学习资源
const editResource = (resource) => {
  editingResource.value = resource
  showResourceModal.value = true
}

// 删除学习资源
const handleDeleteResource = async (id) => {
  if (!confirm('确定要删除这个学习资源吗？')) return

  try {
    await deleteResource(id)
    alert('删除成功')
    await loadSkillDetail(selectedSkillId.value)
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请重试')
  }
}

// 关闭学习资源弹窗
const closeResourceModal = () => {
  showResourceModal.value = false
  editingResource.value = null
}

// 学习资源保存成功
const handleResourceSaved = async () => {
  closeResourceModal()
  await loadSkillDetail(selectedSkillId.value)
}

// 技能创建成功
const handleSkillCreated = async () => {
  showCreateModal.value = false
  await loadSkills()
}

// 技能更新成功
const handleSkillUpdated = async () => {
  showEditModal.value = false
  await loadSkills()
  if (selectedSkillId.value) {
    await loadSkillDetail(selectedSkillId.value)
  }
}

onMounted(async () => {
  await loadCareerPaths()
  await loadSkills()
})
</script>
