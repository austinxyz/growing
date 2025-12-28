<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-full px-6 py-4">
        <h1 class="text-2xl font-bold text-gray-900">技能架构管理</h1>
        <p class="text-sm text-gray-600 mt-1">管理职业路径、技能、大分类和Focus Area</p>
      </div>
    </div>

    <!-- 三栏布局 -->
    <div class="flex h-[calc(100vh-9rem)]">
      <!-- 左侧栏 (25%) - 职业路径 -->
      <div class="w-1/4 bg-white border-r border-gray-200 overflow-hidden flex flex-col">
        <div class="p-3 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
          <h3 class="text-xs font-semibold text-gray-700 uppercase tracking-wider">职业路径</h3>
          <button
            @click="handleAddCareerPath"
            class="px-2 py-1 bg-blue-600 text-white rounded text-xs hover:bg-blue-700"
            title="新增职业路径"
          >
            + 新增
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-2">
          <!-- 加载中 -->
          <div v-if="loadingCareerPaths" class="text-center py-8">
            <svg class="animate-spin h-6 w-6 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-2 text-xs text-gray-500">加载中...</p>
          </div>

          <!-- 职业路径列表 -->
          <div v-else-if="careerPaths.length > 0" class="space-y-1">
            <div
              v-for="careerPath in careerPaths"
              :key="careerPath.id"
              @click="selectCareerPath(careerPath.id)"
              :class="[
                'flex items-center justify-between px-3 py-2 rounded cursor-pointer transition-colors',
                selectedCareerPathId === careerPath.id
                  ? 'bg-blue-50 text-blue-700 font-medium'
                  : 'text-gray-700 hover:bg-gray-50'
              ]"
            >
              <div class="flex items-center space-x-2 flex-1 min-w-0">
                <span class="text-base">{{ careerPath.icon }}</span>
                <span class="text-sm truncate">{{ careerPath.name }}</span>
              </div>
              <div class="flex items-center space-x-1 ml-2">
                <button
                  @click.stop="handleEditCareerPath(careerPath)"
                  class="p-1 text-gray-400 hover:text-blue-600"
                  title="编辑"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
                <button
                  @click.stop="handleDeleteCareerPath(careerPath.id)"
                  class="p-1 text-gray-400 hover:text-red-600"
                  title="删除"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            </div>

            <!-- 未关联职业路径的技能 -->
            <div class="border-t border-gray-200 my-2"></div>
            <div
              @click="selectCareerPath('unassociated')"
              :class="[
                'flex items-center justify-between px-3 py-2 rounded cursor-pointer transition-colors',
                selectedCareerPathId === 'unassociated'
                  ? 'bg-orange-50 text-orange-700 font-medium'
                  : 'text-gray-600 hover:bg-gray-50'
              ]"
            >
              <div class="flex items-center space-x-2 flex-1 min-w-0">
                <span class="text-base">🔗</span>
                <span class="text-sm">未关联职业路径</span>
              </div>
              <span class="text-xs text-gray-400" v-if="unassociatedSkillsCount > 0">
                {{ unassociatedSkillsCount }}
              </span>
            </div>
          </div>

          <!-- 无职业路径 -->
          <div v-else class="text-center text-gray-400 py-8">
            <p class="text-xs">暂无职业路径</p>
          </div>
        </div>
      </div>

      <!-- 中间栏 (35%) - 技能列表 + 大分类 -->
      <div class="w-[35%] bg-white border-r border-gray-200 overflow-hidden flex flex-col">
        <!-- 未选择职业路径时的提示 -->
        <div v-if="!selectedCareerPathId" class="flex-1 flex items-center justify-center text-gray-400">
          <div class="text-center p-4">
            <p class="text-xs">请先从左侧选择一个职业路径</p>
          </div>
        </div>

        <!-- 选中职业路径后显示技能和大分类 -->
        <template v-else>
          <!-- 技能列表部分 (50%) -->
          <div class="h-1/2 border-b border-gray-200 overflow-hidden flex flex-col">
            <div class="p-3 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
              <h3 class="text-xs font-semibold text-gray-700 uppercase tracking-wider">技能列表</h3>
              <button
                @click="handleAddSkill"
                class="px-2 py-1 bg-blue-600 text-white rounded text-xs hover:bg-blue-700"
                title="新增技能"
              >
                + 新增
              </button>
            </div>

            <div class="flex-1 overflow-y-auto p-2">
              <!-- 加载中 -->
              <div v-if="loadingSkills" class="text-center py-8">
                <svg class="animate-spin h-6 w-6 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <p class="mt-2 text-xs text-gray-500">加载中...</p>
              </div>

              <!-- 技能列表 -->
              <div v-else-if="currentSkills.length > 0" class="space-y-1">
                <div
                  v-for="skill in currentSkills"
                  :key="skill.id"
                  @click="selectSkill(skill.id)"
                  :class="[
                    'flex items-center justify-between px-3 py-2 rounded cursor-pointer transition-colors',
                    selectedSkillId === skill.id
                      ? 'bg-blue-50 text-blue-700 font-medium'
                      : 'text-gray-700 hover:bg-gray-50'
                  ]"
                >
                  <div class="flex items-center space-x-2 flex-1 min-w-0">
                    <span class="text-base">{{ skill.icon || '📚' }}</span>
                    <span class="text-sm truncate">{{ skill.name }}</span>
                  </div>
                  <div class="flex items-center space-x-1 ml-2">
                    <button
                      @click.stop="handleEditSkill(skill)"
                      class="p-1 text-gray-400 hover:text-blue-600"
                      title="编辑"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button
                      @click.stop="handleDeleteSkill(skill.id)"
                      class="p-1 text-gray-400 hover:text-red-600"
                      title="删除"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>

              <!-- 无技能 -->
              <div v-else class="text-center text-gray-400 py-8">
                <p class="text-xs">该职业路径暂无技能</p>
              </div>
            </div>
          </div>

          <!-- 大分类部分 (50%) -->
          <div class="h-1/2 overflow-hidden flex flex-col">
            <div class="p-3 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
              <h3 class="text-xs font-semibold text-gray-700 uppercase tracking-wider">大分类</h3>
              <button
                v-if="selectedSkillId && !isSecondTypeSkill"
                @click="handleAddCategory"
                class="px-2 py-1 bg-blue-600 text-white rounded text-xs hover:bg-blue-700"
                title="新增大分类"
              >
                + 新增
              </button>
            </div>

            <!-- 未选择技能 -->
            <div v-if="!selectedSkillId" class="flex-1 flex items-center justify-center text-gray-400">
              <div class="text-center p-4">
                <p class="text-xs">请先从上方选择一个技能</p>
              </div>
            </div>

            <!-- 通用分类技能提示 -->
            <div v-else-if="isSecondTypeSkill" class="flex-1 flex items-center justify-center text-gray-500">
              <div class="text-center p-4">
                <svg class="mx-auto h-10 w-10 text-gray-400 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <p class="text-xs font-medium">📋 通用分类模式</p>
                <p class="text-xs text-gray-400 mt-1">该技能无需大分类</p>
                <p class="text-xs text-gray-400">直接在右侧管理Focus Area</p>
              </div>
            </div>

            <!-- 加载中 -->
            <div v-else-if="loadingCategories" class="flex-1 flex items-center justify-center">
              <div class="text-center">
                <svg class="animate-spin h-6 w-6 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <p class="mt-2 text-xs text-gray-500">加载中...</p>
              </div>
            </div>

            <!-- 大分类列表 -->
            <div v-else class="flex-1 overflow-y-auto p-2">
              <div class="space-y-1">
                <!-- 实际的大分类 -->
                <div
                  v-for="category in categories"
                  :key="category.id"
                  @click="selectCategory(category.id)"
                  @dragover.prevent="handleDragOver($event, category.id)"
                  @drop="handleDrop($event, category.id)"
                  :class="[
                    'flex items-center justify-between px-3 py-2 rounded cursor-pointer transition-colors relative',
                    selectedCategoryId === category.id
                      ? 'bg-blue-50 text-blue-700 font-medium'
                      : 'text-gray-700 hover:bg-gray-50',
                    dragOverCategoryId === category.id
                      ? 'ring-2 ring-blue-400 bg-blue-50'
                      : ''
                  ]"
                >
                  <span class="text-sm truncate flex-1">{{ category.name }}</span>
                  <div class="flex items-center space-x-1 ml-2">
                    <button
                      @click.stop="handleEditCategory(category)"
                      class="p-1 text-gray-400 hover:text-blue-600"
                      title="编辑"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button
                      @click.stop="handleDeleteCategory(category.id)"
                      class="p-1 text-gray-400 hover:text-red-600"
                      title="删除"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                </div>

                <!-- 未分类虚拟分类 -->
                <div v-if="categories.length > 0" class="border-t border-gray-200 my-2"></div>
                <div
                  @click="selectCategory('uncategorized')"
                  @dragover.prevent="handleDragOver($event, 'uncategorized')"
                  @drop="handleDrop($event, 'uncategorized')"
                  :class="[
                    'flex items-center justify-between px-3 py-2 rounded cursor-pointer transition-colors',
                    selectedCategoryId === 'uncategorized'
                      ? 'bg-orange-50 text-orange-700 font-medium'
                      : 'text-gray-600 hover:bg-gray-50',
                    dragOverCategoryId === 'uncategorized'
                      ? 'ring-2 ring-orange-400 bg-orange-50'
                      : ''
                  ]"
                >
                  <div class="flex items-center space-x-2 flex-1">
                    <span class="text-base">📂</span>
                    <span class="text-sm">未分类</span>
                  </div>
                  <span class="text-xs text-gray-400" v-if="uncategorizedCount > 0">
                    {{ uncategorizedCount }}
                  </span>
                </div>

                <!-- 无大分类时的提示 -->
                <div v-if="categories.length === 0" class="text-center text-gray-400 py-8">
                  <p class="text-xs">该技能暂无大分类</p>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 右侧栏 (40%) - Focus Area列表 -->
      <div class="w-[40%] bg-white overflow-hidden flex flex-col">
        <div class="p-3 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
          <h3 class="text-xs font-semibold text-gray-700 uppercase tracking-wider">Focus Area</h3>
          <button
            v-if="canManageFocusAreas"
            @click="handleAddFocusArea"
            class="px-2 py-1 bg-blue-600 text-white rounded text-xs hover:bg-blue-700"
            title="新增Focus Area"
          >
            + 新增
          </button>
        </div>

        <!-- 未选择技能或大分类 -->
        <div v-if="!canManageFocusAreas" class="flex-1 flex items-center justify-center text-gray-400">
          <div class="text-center p-4">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 20l4-16m2 16l4-16M6 9h14M4 15h14" />
            </svg>
            <p class="text-xs mt-2">请先选择技能{{ isSecondTypeSkill ? '' : '或大分类' }}</p>
          </div>
        </div>

        <!-- 加载中 -->
        <div v-else-if="loadingFocusAreas" class="flex-1 flex items-center justify-center">
          <div class="text-center">
            <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="mt-2 text-sm text-gray-500">加载中...</p>
          </div>
        </div>

        <!-- Focus Area列表 -->
        <div v-else class="flex-1 overflow-y-auto p-4">
          <!-- 未分类提示 -->
          <div v-if="selectedCategoryId === 'uncategorized' && focusAreas.length > 0" class="mb-3 p-2 bg-orange-50 border border-orange-200 rounded text-xs text-orange-700">
            <div class="flex items-start space-x-2">
              <svg class="w-4 h-4 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div>
                <p class="font-medium">未分类的 Focus Area</p>
                <p class="mt-1">这些 Focus Area 尚未关联到任何大分类。您可以拖拽它们到左侧的大分类中。</p>
              </div>
            </div>
          </div>

          <div v-if="focusAreas.length > 0" class="space-y-2">
            <div
              v-for="(focusArea, index) in focusAreas"
              :key="focusArea.id"
              draggable="true"
              @dragstart="handleDragStart($event, focusArea)"
              @dragend="handleDragEnd"
              :class="[
                'p-3 border border-gray-200 rounded-lg transition-all cursor-move',
                draggingFocusAreaId === focusArea.id
                  ? 'opacity-50 border-blue-400'
                  : 'hover:border-blue-300 hover:shadow-sm'
              ]"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1 min-w-0">
                  <div class="flex items-center space-x-2 mb-1">
                    <!-- 拖拽图标 -->
                    <svg class="w-4 h-4 text-gray-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8h16M4 16h16" />
                    </svg>
                    <span class="text-xs text-gray-500">#{{ index + 1 }}</span>
                    <h4 class="text-sm font-medium text-gray-900 truncate">{{ focusArea.name }}</h4>
                  </div>
                  <p v-if="focusArea.description" class="text-xs text-gray-500 line-clamp-2 ml-6">{{ focusArea.description }}</p>

                  <!-- 显示当前所属大分类 -->
                  <div v-if="!isSecondTypeSkill && focusArea.categoryNames && focusArea.categoryNames.length > 0" class="mt-1 ml-6">
                    <div class="flex flex-wrap gap-1">
                      <span
                        v-for="categoryName in focusArea.categoryNames"
                        :key="categoryName"
                        class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800"
                      >
                        {{ categoryName }}
                      </span>
                    </div>
                  </div>
                </div>
                <div class="flex items-center space-x-1 ml-2">
                  <button
                    @click="handleEditFocusArea(focusArea)"
                    class="p-1 text-gray-400 hover:text-blue-600"
                    title="编辑"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button
                    @click="handleDeleteFocusArea(focusArea.id)"
                    class="p-1 text-gray-400 hover:text-red-600"
                    title="删除"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="text-center text-gray-400 py-12">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="text-sm mt-2">暂无Focus Area</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 职业路径编辑Modal -->
    <CareerPathEditModal
      v-if="showCareerPathModal"
      :career-path="editingCareerPath"
      @close="closeCareerPathModal"
      @success="handleCareerPathSaved"
    />

    <!-- 技能编辑Modal -->
    <SkillEditModal
      v-if="showSkillModal"
      :skill="editingSkill"
      :career-path-id="selectedCareerPathId"
      @close="closeSkillModal"
      @success="handleSkillSaved"
    />

    <!-- 大分类编辑Modal -->
    <CategoryEditModal
      v-if="showCategoryModal"
      :category="editingCategory"
      :skill-id="selectedSkillId"
      @close="closeCategoryModal"
      @success="handleCategorySaved"
    />

    <!-- Focus Area编辑Modal -->
    <FocusAreaEditModal
      v-if="showFocusAreaModal"
      :focus-area="editingFocusArea"
      :skill-id="selectedSkillId"
      :category-id="selectedCategoryId"
      @close="closeFocusAreaModal"
      @success="handleFocusAreaSaved"
    />
  </div>
</template>

<script>
import { getAllCareerPaths, deleteCareerPath } from '@/api/careerPaths'
import { getAllSkills, getUnassociatedSkills, deleteSkill } from '@/api/skills'
import majorCategoryApi from '@/api/majorCategoryApi'
import CareerPathEditModal from '@/components/skills/admin/CareerPathEditModal.vue'
import SkillEditModal from '@/components/skills/admin/SkillEditModal.vue'
import CategoryEditModal from '@/components/skills/admin/CategoryEditModal.vue'
import FocusAreaEditModal from '@/components/skills/admin/FocusAreaEditModal.vue'

export default {
  name: 'SkillManagement',
  components: {
    CareerPathEditModal,
    SkillEditModal,
    CategoryEditModal,
    FocusAreaEditModal
  },
  data() {
    return {
      // 职业路径相关
      careerPaths: [],
      loadingCareerPaths: false,
      selectedCareerPathId: null,

      // 技能相关
      allSkills: [],
      unassociatedSkills: [],
      loadingSkills: false,
      selectedSkillId: null,

      // 大分类相关
      categories: [],
      loadingCategories: false,
      selectedCategoryId: null,

      // Focus Area相关
      focusAreas: [],
      loadingFocusAreas: false,
      uncategorizedCount: 0, // 未分类Focus Area数量

      // 拖拽相关
      draggingFocusAreaId: null,
      draggingFocusArea: null,
      dragOverCategoryId: null,

      // Modal状态
      showCareerPathModal: false,
      editingCareerPath: null,
      showSkillModal: false,
      editingSkill: null,
      showCategoryModal: false,
      editingCategory: null,
      showFocusAreaModal: false,
      editingFocusArea: null
    }
  },
  computed: {
    // 当前职业路径的技能列表
    currentSkills() {
      if (!this.selectedCareerPathId) return []
      if (this.selectedCareerPathId === 'unassociated') {
        return this.unassociatedSkills.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0))
      }
      return this.allSkills.filter(skill =>
        skill.careerPaths?.some(cp => cp.id === this.selectedCareerPathId)
      ).sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0))
    },
    // 未关联技能数量
    unassociatedSkillsCount() {
      return this.unassociatedSkills.length
    },
    // 判断是否为通用分类技能（不需要大分类，直接管理Focus Area）
    isSecondTypeSkill() {
      if (!this.selectedSkillId) {
        return false
      }
      const selectedSkill = this.currentSkills.find(s => s.id === this.selectedSkillId)
      return selectedSkill?.isGeneralOnly === true
    },
    // 是否可以管理Focus Area
    canManageFocusAreas() {
      // 第二类技能：选中技能即可
      if (this.isSecondTypeSkill && this.selectedSkillId) {
        return true
      }
      // 第一类技能：需要选中大分类（包括"未分类"虚拟分类）
      return !this.isSecondTypeSkill && this.selectedCategoryId
    }
  },
  async mounted() {
    await this.loadCareerPaths()
  },
  watch: {
    // 监听职业路径切换
    async selectedCareerPathId(newVal) {
      this.selectedSkillId = null
      this.selectedCategoryId = null
      this.categories = []
      this.focusAreas = []
      if (newVal) {
        await this.loadSkills()
      }
    },
    // 监听技能切换
    async selectedSkillId(newVal) {
      this.selectedCategoryId = null
      this.categories = []
      this.focusAreas = []
      if (newVal) {
        await this.loadCategories()
        // 第二类技能自动加载Focus Area
        if (this.isSecondTypeSkill) {
          await this.loadFocusAreas()
        }
      }
    },
    // 监听大分类切换
    async selectedCategoryId(newVal) {
      this.focusAreas = []
      if (newVal) {
        await this.loadFocusAreas()
      }
    }
  },
  methods: {
    // ===== 职业路径相关方法 =====
    async loadCareerPaths() {
      this.loadingCareerPaths = true
      try {
        const data = await getAllCareerPaths()
        this.careerPaths = data || []
        // 自动选中第一个职业路径
        if (this.careerPaths.length > 0 && !this.selectedCareerPathId) {
          this.selectedCareerPathId = this.careerPaths[0].id
        }
      } catch (error) {
        console.error('加载职业路径失败:', error)
        this.careerPaths = []
      } finally {
        this.loadingCareerPaths = false
      }
    },
    selectCareerPath(id) {
      this.selectedCareerPathId = id
    },
    handleAddCareerPath() {
      this.editingCareerPath = null
      this.showCareerPathModal = true
    },
    handleEditCareerPath(careerPath) {
      this.editingCareerPath = careerPath
      this.showCareerPathModal = true
    },
    async handleDeleteCareerPath(id) {
      if (!confirm('确定要删除这个职业路径吗？删除后该职业路径下的技能将变为未关联状态。')) {
        return
      }
      try {
        await deleteCareerPath(id)
        if (this.selectedCareerPathId === id) {
          this.selectedCareerPathId = null
        }
        await this.loadCareerPaths()
      } catch (error) {
        console.error('删除职业路径失败:', error)
        alert('删除失败: ' + (error.message || '未知错误'))
      }
    },
    closeCareerPathModal() {
      this.showCareerPathModal = false
      this.editingCareerPath = null
    },
    async handleCareerPathSaved() {
      this.closeCareerPathModal()
      await this.loadCareerPaths()
    },

    // ===== 技能相关方法 =====
    async loadSkills() {
      this.loadingSkills = true
      try {
        const data = await getAllSkills()
        this.allSkills = data || []

        // 同时加载未关联技能
        const unassociatedData = await getUnassociatedSkills()
        this.unassociatedSkills = unassociatedData || []
      } catch (error) {
        console.error('加载技能失败:', error)
        this.allSkills = []
        this.unassociatedSkills = []
      } finally {
        this.loadingSkills = false
      }
    },
    selectSkill(id) {
      this.selectedSkillId = id
    },
    handleAddSkill() {
      this.editingSkill = null
      this.showSkillModal = true
    },
    handleEditSkill(skill) {
      this.editingSkill = skill
      this.showSkillModal = true
    },
    async handleDeleteSkill(id) {
      if (!confirm('确定要删除这个技能吗？')) {
        return
      }
      try {
        await deleteSkill(id)
        if (this.selectedSkillId === id) {
          this.selectedSkillId = null
        }
        await this.loadSkills()
        alert('删除成功')
      } catch (error) {
        console.error('删除技能失败:', error)
        // 显示后端返回的错误信息
        const errorMessage = error.response?.data?.message || error.message || '未知错误'
        alert('删除失败: ' + errorMessage)
      }
    },
    closeSkillModal() {
      this.showSkillModal = false
      this.editingSkill = null
    },
    async handleSkillSaved() {
      this.closeSkillModal()
      await this.loadSkills()
    },

    // ===== 大分类相关方法 =====
    async loadCategories() {
      if (!this.selectedSkillId) {
        return
      }
      this.loadingCategories = true
      try {
        const data = await majorCategoryApi.getAllMajorCategories(this.selectedSkillId)
        this.categories = data || []

        // 加载未分类Focus Area数量
        await this.loadUncategorizedCount()
      } catch (error) {
        console.error('加载大分类失败:', error)
        this.categories = []
      } finally {
        this.loadingCategories = false
      }
    },
    async loadUncategorizedCount() {
      if (!this.selectedSkillId || this.isSecondTypeSkill) {
        this.uncategorizedCount = 0
        return
      }
      try {
        const data = await majorCategoryApi.getUncategorizedFocusAreas(this.selectedSkillId)
        this.uncategorizedCount = (data || []).length
      } catch (error) {
        console.error('加载未分类Focus Area数量失败:', error)
        this.uncategorizedCount = 0
      }
    },
    selectCategory(id) {
      this.selectedCategoryId = id
    },
    handleAddCategory() {
      this.editingCategory = null
      this.showCategoryModal = true
    },
    handleEditCategory(category) {
      this.editingCategory = category
      this.showCategoryModal = true
    },
    async handleDeleteCategory(id) {
      // 检查是否是 General 分类
      const category = this.categories.find(c => c.id === id)
      if (category && category.name === 'General') {
        alert('不允许删除 General 大分类')
        return
      }

      if (!confirm('确定要删除这个大分类吗？删除后该大分类下的Focus Area将变为未分类状态，可以重新关联到其他大分类。')) {
        return
      }
      try {
        await majorCategoryApi.deleteMajorCategory(id)
        if (this.selectedCategoryId === id) {
          this.selectedCategoryId = null
        }
        await this.loadCategories()
        await this.loadFocusAreas() // 重新加载Focus Area列表
        alert('删除成功')
      } catch (error) {
        console.error('删除大分类失败:', error)
        // 显示后端返回的错误信息
        const errorMessage = error.response?.data?.message || error.message || '未知错误'
        alert('删除失败: ' + errorMessage)
      }
    },
    closeCategoryModal() {
      this.showCategoryModal = false
      this.editingCategory = null
    },
    async handleCategorySaved() {
      this.closeCategoryModal()
      await this.loadCategories()
    },

    // ===== Focus Area相关方法 =====
    async loadFocusAreas() {
      if (!this.selectedSkillId) {
        return
      }
      this.loadingFocusAreas = true
      try {
        // 如果选中"未分类"，查询未分类的 Focus Area
        if (this.selectedCategoryId === 'uncategorized') {
          const data = await majorCategoryApi.getUncategorizedFocusAreas(this.selectedSkillId)
          this.focusAreas = data || []
          return
        }

        const data = await majorCategoryApi.getFocusAreasWithCategories(this.selectedSkillId)

        // 根据技能类型筛选Focus Area
        if (this.isSecondTypeSkill) {
          // 第二类技能：显示所有Focus Area
          this.focusAreas = data || []
        } else if (this.selectedCategoryId) {
          // 第一类技能：只显示选中大分类下的Focus Area
          this.focusAreas = (data || []).filter(fa =>
            fa.categoryIds && fa.categoryIds.includes(this.selectedCategoryId)
          )
        } else {
          this.focusAreas = []
        }
      } catch (error) {
        console.error('加载Focus Area失败:', error)
        this.focusAreas = []
      } finally {
        this.loadingFocusAreas = false
      }
    },
    handleAddFocusArea() {
      this.editingFocusArea = null
      this.showFocusAreaModal = true
    },
    handleEditFocusArea(focusArea) {
      this.editingFocusArea = focusArea
      this.showFocusAreaModal = true
    },
    async handleDeleteFocusArea(id) {
      if (!confirm('确定要删除这个Focus Area吗？删除后该Focus Area下的所有学习资料和试题也将被删除。')) {
        return
      }
      try {
        await majorCategoryApi.deleteFocusArea(id)
        await this.loadFocusAreas()
      } catch (error) {
        console.error('删除Focus Area失败:', error)
        alert('删除失败: ' + (error.message || '未知错误'))
      }
    },
    closeFocusAreaModal() {
      this.showFocusAreaModal = false
      this.editingFocusArea = null
    },
    async handleFocusAreaSaved() {
      this.closeFocusAreaModal()
      await this.loadFocusAreas()
      await this.loadUncategorizedCount() // 更新未分类数量
    },

    // ===== 拖拽相关方法 =====
    handleDragStart(event, focusArea) {
      this.draggingFocusAreaId = focusArea.id
      this.draggingFocusArea = focusArea
      event.dataTransfer.effectAllowed = 'move'
    },
    handleDragEnd() {
      this.draggingFocusAreaId = null
      this.draggingFocusArea = null
      this.dragOverCategoryId = null
    },
    handleDragOver(event, categoryId) {
      this.dragOverCategoryId = categoryId
    },
    async handleDrop(event, targetCategoryId) {
      event.preventDefault()
      this.dragOverCategoryId = null

      if (!this.draggingFocusArea) {
        return
      }

      const focusArea = this.draggingFocusArea
      const sourceCategoryId = this.selectedCategoryId

      // 如果拖到同一个分类，不做处理
      if (sourceCategoryId === targetCategoryId) {
        return
      }

      try {
        // 准备新的 categoryIds
        let newCategoryIds = []

        if (targetCategoryId === 'uncategorized') {
          // 拖到"未分类"：清空所有大分类关联
          newCategoryIds = []
        } else {
          // 拖到具体大分类：获取现有的大分类关联
          newCategoryIds = focusArea.categoryIds ? [...focusArea.categoryIds] : []

          // 如果从"未分类"拖过来，直接添加新分类
          if (sourceCategoryId === 'uncategorized') {
            newCategoryIds.push(targetCategoryId)
          } else {
            // 从其他分类拖过来，移除原分类，添加新分类
            newCategoryIds = newCategoryIds.filter(id => id !== sourceCategoryId)
            if (!newCategoryIds.includes(targetCategoryId)) {
              newCategoryIds.push(targetCategoryId)
            }
          }
        }

        // 更新 Focus Area 的大分类关联
        await majorCategoryApi.updateFocusArea(focusArea.id, {
          ...focusArea,
          categoryIds: newCategoryIds
        })

        // 刷新列表
        await this.loadFocusAreas()
        await this.loadUncategorizedCount()

        // 显示成功提示
        const targetName = targetCategoryId === 'uncategorized'
          ? '未分类'
          : this.categories.find(c => c.id === targetCategoryId)?.name || '目标分类'
        alert(`已将 "${focusArea.name}" 移动到 "${targetName}"`)
      } catch (error) {
        console.error('移动Focus Area失败:', error)
        const errorMessage = error.response?.data?.message || error.message || '未知错误'
        alert('移动失败: ' + errorMessage)
      } finally {
        this.draggingFocusAreaId = null
        this.draggingFocusArea = null
      }
    }
  }
}
</script>
