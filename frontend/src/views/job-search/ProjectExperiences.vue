<template>
  <div class="project-experiences p-6">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">项目经验库</h1>

    <div class="grid grid-cols-3 gap-6">
      <!-- 左侧: 项目列表 -->
      <div class="col-span-1 bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold">项目列表</h2>
          <button
            @click="showCreateModal = true"
            class="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
          >
            新建
          </button>
        </div>

        <div class="space-y-2">
          <div
            v-for="project in projects"
            :key="project.id"
            @click="selectedProjectId = project.id"
            class="p-3 border rounded cursor-pointer hover:bg-gray-50"
            :class="{ 'bg-blue-50 border-blue-500': selectedProjectId === project.id }"
          >
            <div class="font-medium">{{ project.projectName }}</div>
            <div class="text-sm text-gray-500">{{ project.projectType }}</div>
            <div class="text-xs text-gray-400">
              {{ project.startDate }} - {{ project.endDate || '进行中' }}
            </div>
          </div>
          <div v-if="!projects.length" class="text-gray-500 text-center py-8">
            暂无项目经验
          </div>
        </div>
      </div>

      <!-- 右侧: 项目详情 -->
      <div class="col-span-2 bg-white rounded-lg shadow p-6">
        <div v-if="currentProject">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-xl font-semibold">{{ currentProject.projectName }}</h2>
            <div class="flex gap-2">
              <button
                @click="showEditModal = true"
                class="px-3 py-1 bg-green-600 text-white text-sm rounded hover:bg-green-700"
              >
                编辑
              </button>
              <button
                @click="deleteProject"
                class="px-3 py-1 bg-red-600 text-white text-sm rounded hover:bg-red-700"
              >
                删除
              </button>
            </div>
          </div>

          <div class="space-y-4">
            <div>
              <h3 class="font-semibold text-gray-700 mb-2">What/When/Who/Why</h3>
              <div class="text-gray-600 whitespace-pre-wrap">{{ currentProject.whatDescription }}</div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-700 mb-2">Problem Statement</h3>
              <div class="text-gray-600 whitespace-pre-wrap">{{ currentProject.problemStatement }}</div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-700 mb-2">Solution</h3>
              <div class="text-gray-600 whitespace-pre-wrap">{{ currentProject.myContribution }}</div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-700 mb-2">Result</h3>
              <div class="text-gray-600 whitespace-pre-wrap">{{ currentProject.quantitativeResults }}</div>
            </div>
          </div>
        </div>

        <div v-else class="text-center text-gray-500 py-12">
          请选择一个项目查看详情
        </div>
      </div>
    </div>

    <!-- 创建/编辑项目 Modal (简化版) -->
    <div v-if="showCreateModal || showEditModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-2/3 max-h-[80vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">{{ showCreateModal ? '创建项目' : '编辑项目' }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">项目名称 *</label>
            <input v-model="formData.projectName" class="w-full px-4 py-2 border rounded-lg" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">项目类型</label>
            <select v-model="formData.projectType" class="w-full px-4 py-2 border rounded-lg">
              <option value="Feature">Feature Development</option>
              <option value="Optimization">Performance Optimization</option>
              <option value="Migration">System Migration</option>
              <option value="Infrastructure">Infrastructure</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">项目背景</label>
            <textarea v-model="formData.whatDescription" rows="3" class="w-full px-4 py-2 border rounded-lg"></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <button @click="closeModal" class="px-4 py-2 border rounded-lg hover:bg-gray-50">取消</button>
          <button @click="saveProject" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { projectApi } from '@/api/projectApi'

const projects = ref([])
const selectedProjectId = ref(null)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const formData = ref({
  projectName: '',
  projectType: 'Feature',
  whatDescription: '',
  problemStatement: '',
  myContribution: '',
  quantitativeResults: ''
})

const currentProject = computed(() =>
  projects.value.find(p => p.id === selectedProjectId.value)
)

onMounted(() => {
  loadProjects()
})

watch(() => showEditModal.value, (val) => {
  if (val && currentProject.value) {
    formData.value = { ...currentProject.value }
  }
})

const loadProjects = async () => {
  try {
    const data = await projectApi.getProjects()
    projects.value = data || []
  } catch (error) {
    console.error('加载项目列表失败:', error)
  }
}

const saveProject = async () => {
  try {
    if (showCreateModal.value) {
      await projectApi.createProject(formData.value)
    } else {
      await projectApi.updateProject(selectedProjectId.value, formData.value)
    }
    alert('保存成功')
    closeModal()
    await loadProjects()
  } catch (error) {
    console.error('保存项目失败:', error)
    alert('保存失败')
  }
}

const deleteProject = async () => {
  if (!confirm('确定要删除这个项目吗?')) return

  try {
    await projectApi.deleteProject(selectedProjectId.value)
    selectedProjectId.value = null
    await loadProjects()
  } catch (error) {
    console.error('删除项目失败:', error)
    alert('删除失败')
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  formData.value = {
    projectName: '',
    projectType: 'Feature',
    whatDescription: '',
    problemStatement: '',
    myContribution: '',
    quantitativeResults: ''
  }
}
</script>
