<template>
  <div class="management-experiences p-6">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">人员管理经验库</h1>

    <div class="grid grid-cols-3 gap-6">
      <!-- 左侧: 经验列表 -->
      <div class="col-span-1 bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold">管理经验</h2>
          <button
            @click="showCreateModal = true"
            class="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
          >
            新建
          </button>
        </div>

        <div class="space-y-4">
          <div v-for="type in experienceTypes" :key="type.value">
            <h3 class="text-sm font-semibold text-gray-700 mb-2">{{ type.label }}</h3>
            <div class="space-y-2">
              <div
                v-for="exp in filteredExperiences(type.value)"
                :key="exp.id"
                @click="selectedExpId = exp.id"
                class="p-3 border rounded cursor-pointer hover:bg-gray-50"
                :class="{ 'bg-blue-50 border-blue-500': selectedExpId === exp.id }"
              >
                <div class="font-medium text-sm">{{ exp.experienceName }}</div>
                <div class="text-xs text-gray-400">
                  {{ exp.startDate }} - {{ exp.endDate || '进行中' }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧: 经验详情 -->
      <div class="col-span-2 bg-white rounded-lg shadow p-6">
        <div v-if="currentExperience">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-xl font-semibold">{{ currentExperience.experienceName }}</h2>
            <div class="flex gap-2">
              <button
                @click="showEditModal = true"
                class="px-3 py-1 bg-green-600 text-white text-sm rounded hover:bg-green-700"
              >
                编辑
              </button>
              <button
                @click="deleteExperience"
                class="px-3 py-1 bg-red-600 text-white text-sm rounded hover:bg-red-700"
              >
                删除
              </button>
            </div>
          </div>

          <div class="space-y-4">
            <div>
              <h3 class="font-semibold text-gray-700 mb-2">背景和挑战</h3>
              <div class="text-gray-600 whitespace-pre-wrap">{{ currentExperience.background }}</div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-700 mb-2">采取的行动</h3>
              <div class="text-gray-600 whitespace-pre-wrap">{{ currentExperience.actionsTaken }}</div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-700 mb-2">结果和影响</h3>
              <div class="text-gray-600 whitespace-pre-wrap">{{ currentExperience.results }}</div>
            </div>
          </div>
        </div>

        <div v-else class="text-center text-gray-500 py-12">
          请选择一个经验查看详情
        </div>
      </div>
    </div>

    <!-- 创建/编辑 Modal (简化版) -->
    <div v-if="showCreateModal || showEditModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-2/3">
        <h3 class="text-lg font-semibold mb-4">{{ showCreateModal ? '创建经验' : '编辑经验' }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">经验名称 *</label>
            <input v-model="formData.experienceName" class="w-full px-4 py-2 border rounded-lg" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">经验类型 *</label>
            <select v-model="formData.experienceType" class="w-full px-4 py-2 border rounded-lg">
              <option value="ManageUp">Manage Up</option>
              <option value="CrossTeam">Cross-team Management</option>
              <option value="TeamGrowth">Team Growth</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">背景和挑战</label>
            <textarea v-model="formData.background" rows="3" class="w-full px-4 py-2 border rounded-lg"></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <button @click="closeModal" class="px-4 py-2 border rounded-lg hover:bg-gray-50">取消</button>
          <button @click="saveExperience" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { managementApi } from '@/api/managementApi'

const experiences = ref([])
const selectedExpId = ref(null)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const formData = ref({
  experienceName: '',
  experienceType: 'ManageUp',
  background: '',
  actionsTaken: '',
  results: ''
})

const experienceTypes = [
  { value: 'ManageUp', label: 'Manage Up' },
  { value: 'CrossTeam', label: 'Cross-team' },
  { value: 'TeamGrowth', label: 'Team Growth' }
]

const currentExperience = computed(() =>
  experiences.value.find(e => e.id === selectedExpId.value)
)

const filteredExperiences = (type) => {
  return experiences.value.filter(e => e.experienceType === type)
}

onMounted(() => {
  loadExperiences()
})

const loadExperiences = async () => {
  try {
    const data = await managementApi.getExperiences()
    experiences.value = data || []
  } catch (error) {
    console.error('加载管理经验失败:', error)
  }
}

const saveExperience = async () => {
  try {
    if (showCreateModal.value) {
      await managementApi.createExperience(formData.value)
    } else {
      await managementApi.updateExperience(selectedExpId.value, formData.value)
    }
    alert('保存成功')
    closeModal()
    await loadExperiences()
  } catch (error) {
    console.error('保存经验失败:', error)
    alert('保存失败')
  }
}

const deleteExperience = async () => {
  if (!confirm('确定要删除这个经验吗?')) return

  try {
    await managementApi.deleteExperience(selectedExpId.value)
    selectedExpId.value = null
    await loadExperiences()
  } catch (error) {
    console.error('删除经验失败:', error)
    alert('删除失败')
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  formData.value = {
    experienceName: '',
    experienceType: 'ManageUp',
    background: '',
    actionsTaken: '',
    results: ''
  }
}
</script>
