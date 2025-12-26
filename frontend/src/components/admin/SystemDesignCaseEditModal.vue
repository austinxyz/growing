<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- Backdrop -->
    <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="close"></div>

    <!-- Modal -->
    <div class="flex min-h-screen items-center justify-center p-4">
      <div class="relative bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-hidden">
        <!-- Header -->
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h2 class="text-xl font-semibold text-gray-800">
            {{ isEditMode ? '编辑案例' : '新建案例' }}
          </h2>
          <button @click="close" class="text-gray-400 hover:text-gray-600">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Body -->
        <div class="px-6 py-4 overflow-y-auto max-h-[calc(90vh-140px)]">
          <div class="space-y-4">
            <!-- Title -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                案例标题 <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.title"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="例如: 设计Twitter"
              />
            </div>

            <!-- Difficulty & Rating -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  难度级别 <span class="text-red-500">*</span>
                </label>
                <select
                  v-model="form.difficulty"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="EASY">简单</option>
                  <option value="MEDIUM">中等</option>
                  <option value="HARD">困难</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  难度评分 (1-10)
                </label>
                <input
                  v-model.number="form.difficultyRating"
                  type="number"
                  min="1"
                  max="10"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="1-10"
                />
              </div>
            </div>

            <!-- Company Tags -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                公司标签 (逗号分隔)
              </label>
              <input
                v-model="companyTagsInput"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="例如: Google, Meta, Amazon"
              />
              <div v-if="form.companyTags && form.companyTags.length > 0" class="mt-2 flex flex-wrap gap-2">
                <span
                  v-for="tag in form.companyTags"
                  :key="tag"
                  class="px-2 py-1 text-xs bg-blue-100 text-blue-700 rounded"
                >
                  {{ tag }}
                </span>
              </div>
            </div>

            <!-- Related Focus Areas -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                相关知识领域
              </label>

              <!-- Add Focus Area -->
              <div class="mb-3 p-3 bg-gray-50 rounded-lg">
                <div class="flex gap-3 items-end">
                  <!-- Category Selector -->
                  <div class="flex-1">
                    <label class="block text-xs font-medium text-gray-600 mb-1">选择大分类</label>
                    <select
                      v-model="selectedCategoryId"
                      class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    >
                      <option :value="null">请选择分类</option>
                      <option v-for="category in categories" :key="category.id" :value="category.id">
                        {{ category.name }}
                      </option>
                    </select>
                  </div>

                  <!-- Focus Area Selector -->
                  <div class="flex-1">
                    <label class="block text-xs font-medium text-gray-600 mb-1">选择知识领域</label>
                    <select
                      v-model="selectedFocusAreaId"
                      :disabled="!selectedCategoryId || filteredFocusAreas.length === 0"
                      class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:bg-gray-100"
                    >
                      <option :value="null">请选择领域</option>
                      <option v-for="fa in filteredFocusAreas" :key="fa.id" :value="fa.id">
                        {{ fa.name }}
                      </option>
                    </select>
                  </div>

                  <!-- Add Button -->
                  <button
                    @click="addFocusArea"
                    :disabled="!selectedFocusAreaId || isAlreadyAdded(selectedFocusAreaId)"
                    class="px-4 py-1.5 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors whitespace-nowrap"
                  >
                    {{ isAlreadyAdded(selectedFocusAreaId) ? '已添加' : '+ 添加' }}
                  </button>
                </div>
              </div>

              <!-- Focus Areas List -->
              <div v-if="form.relatedFocusAreas && form.relatedFocusAreas.length > 0" class="flex flex-wrap gap-2">
                <div
                  v-for="faId in form.relatedFocusAreas"
                  :key="faId"
                  class="flex items-center gap-2 px-3 py-1.5 bg-blue-50 rounded-lg"
                >
                  <span class="text-sm text-gray-700">
                    {{ getFocusAreaName(faId) }}
                  </span>
                  <button
                    @click="removeFocusArea(faId)"
                    class="text-red-600 hover:text-red-700 text-xs"
                  >
                    ✕
                  </button>
                </div>
              </div>
              <p v-else class="text-sm text-gray-400">暂无相关知识领域</p>
            </div>

            <!-- Display Order -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                显示顺序
              </label>
              <input
                v-model.number="form.displayOrder"
                type="number"
                min="0"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="0"
              />
            </div>

            <!-- Is Official -->
            <div class="flex items-center">
              <input
                v-model="form.isOfficial"
                type="checkbox"
                id="isOfficial"
                class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <label for="isOfficial" class="ml-2 text-sm text-gray-700">
                官方案例
              </label>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="px-6 py-4 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="close"
            class="px-4 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
          >
            取消
          </button>
          <button
            @click="handleSave"
            :disabled="!isFormValid"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
          >
            {{ isEditMode ? '保存' : '创建' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import majorCategoryApi from '@/api/majorCategoryApi'

const props = defineProps({
  isOpen: Boolean,
  caseData: Object // 编辑时传入
})

const emit = defineEmits(['close', 'save'])

const isEditMode = computed(() => !!props.caseData)

const form = ref({
  title: '',
  caseDescription: '',
  difficulty: 'MEDIUM',
  difficultyRating: 5,
  companyTags: [],
  relatedFocusAreas: [],
  displayOrder: 0,
  isOfficial: true
})

const companyTagsInput = ref('')

// Focus Area management
const categories = ref([])
const allFocusAreas = ref([])
const selectedCategoryId = ref(null)
const selectedFocusAreaId = ref(null)

// Computed: filtered focus areas by selected category
const filteredFocusAreas = computed(() => {
  if (!selectedCategoryId.value) return []
  return allFocusAreas.value.filter(fa =>
    fa.categoryIds && fa.categoryIds.includes(selectedCategoryId.value)
  )
})

// Load categories and focus areas
const loadData = async () => {
  try {
    const SYSTEM_DESIGN_SKILL_ID = 2
    categories.value = await majorCategoryApi.getAllMajorCategories(SYSTEM_DESIGN_SKILL_ID)
    allFocusAreas.value = await majorCategoryApi.getFocusAreasWithCategories(SYSTEM_DESIGN_SKILL_ID)
  } catch (error) {
    console.error('加载分类和知识领域失败:', error)
  }
}

// Focus area helper functions
const getFocusAreaName = (faId) => {
  const fa = allFocusAreas.value.find(f => f.id === faId)
  return fa ? fa.name : `ID: ${faId}`
}

const isAlreadyAdded = (faId) => {
  return form.value.relatedFocusAreas.includes(faId)
}

const addFocusArea = () => {
  if (selectedFocusAreaId.value && !isAlreadyAdded(selectedFocusAreaId.value)) {
    form.value.relatedFocusAreas.push(selectedFocusAreaId.value)
    // Reset selectors
    selectedCategoryId.value = null
    selectedFocusAreaId.value = null
  }
}

const removeFocusArea = (faId) => {
  form.value.relatedFocusAreas = form.value.relatedFocusAreas.filter(id => id !== faId)
}

// Watch for input changes to update arrays
watch(companyTagsInput, (newValue) => {
  form.value.companyTags = newValue
    ? newValue.split(',').map(tag => tag.trim()).filter(tag => tag)
    : []
})

// Watch for caseData changes (when editing)
watch(() => props.caseData, (newData) => {
  if (newData) {
    form.value = {
      title: newData.title || '',
      caseDescription: newData.caseDescription || '',
      difficulty: newData.difficulty || 'MEDIUM',
      difficultyRating: newData.difficultyRating || 5,
      companyTags: newData.companyTags || [],
      relatedFocusAreas: newData.relatedFocusAreas || [],
      displayOrder: newData.displayOrder || 0,
      isOfficial: newData.isOfficial !== false
    }
    companyTagsInput.value = (newData.companyTags || []).join(', ')
  }
}, { immediate: true })

const isFormValid = computed(() => {
  return form.value.title && form.value.title.trim().length > 0
})

const close = () => {
  emit('close')
  // Reset form
  form.value = {
    title: '',
    caseDescription: '',
    difficulty: 'MEDIUM',
    difficultyRating: 5,
    companyTags: [],
    relatedFocusAreas: [],
    displayOrder: 0,
    isOfficial: true
  }
  companyTagsInput.value = ''
  selectedCategoryId.value = null
  selectedFocusAreaId.value = null
}

const handleSave = () => {
  if (!isFormValid.value) return

  emit('save', {
    ...form.value,
    id: props.caseData?.id
  })
  close()
}

onMounted(() => {
  loadData()
})
</script>
