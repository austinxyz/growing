<template>
  <div class="p-8">
    <div class="mb-8 flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold text-foreground">用户管理</h1>
        <p class="text-muted-foreground mt-2">管理系统用户和权限</p>
      </div>
      <button
        @click="showCreateDialog = true"
        class="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors"
      >
        + 创建用户
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="bg-card border border-border rounded-lg p-12 text-center">
      <p class="text-muted-foreground">加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="bg-destructive/10 border border-destructive rounded-lg p-6">
      <p class="text-destructive">{{ error }}</p>
      <button
        @click="loadUsers"
        class="mt-4 px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90"
      >
        重试
      </button>
    </div>

    <!-- 用户列表 -->
    <div v-else class="bg-card border border-border rounded-lg overflow-hidden">
      <table class="w-full">
        <thead class="bg-muted/50 border-b border-border">
          <tr>
            <th class="text-left p-4 font-semibold text-foreground">ID</th>
            <th class="text-left p-4 font-semibold text-foreground">用户名</th>
            <th class="text-left p-4 font-semibold text-foreground">邮箱</th>
            <th class="text-left p-4 font-semibold text-foreground">全名</th>
            <th class="text-left p-4 font-semibold text-foreground">职业路径</th>
            <th class="text-left p-4 font-semibold text-foreground">创建时间</th>
            <th class="text-right p-4 font-semibold text-foreground">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="user in users"
            :key="user.id"
            class="border-b border-border hover:bg-accent/50 transition-colors"
          >
            <td class="p-4 text-muted-foreground">{{ user.id }}</td>
            <td class="p-4 text-foreground font-medium">{{ user.username }}</td>
            <td class="p-4 text-muted-foreground">{{ user.email }}</td>
            <td class="p-4 text-foreground">{{ user.fullName || '-' }}</td>
            <td class="p-4">
              <div class="flex flex-wrap gap-1">
                <span
                  v-for="path in user.careerPaths"
                  :key="path.id"
                  class="px-2 py-1 bg-primary/10 text-primary text-xs rounded-md"
                >
                  {{ path.name }}
                </span>
                <span v-if="!user.careerPaths || user.careerPaths.length === 0" class="text-muted-foreground text-sm">
                  未设置
                </span>
              </div>
            </td>
            <td class="p-4 text-muted-foreground text-sm">
              {{ formatDate(user.createdAt) }}
            </td>
            <td class="p-4">
              <div class="flex justify-end gap-2">
                <button
                  @click="viewUser(user)"
                  class="px-3 py-1 text-sm bg-secondary text-secondary-foreground rounded-md hover:bg-secondary/80"
                  title="查看详情"
                >
                  查看
                </button>
                <button
                  @click="editUser(user)"
                  class="px-3 py-1 text-sm bg-accent text-accent-foreground rounded-md hover:bg-accent/80"
                  title="编辑"
                >
                  编辑
                </button>
                <button
                  @click="confirmDeleteUser(user)"
                  class="px-3 py-1 text-sm bg-destructive text-destructive-foreground rounded-md hover:bg-destructive/80"
                  title="删除"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="users.length === 0">
            <td colspan="7" class="p-12 text-center text-muted-foreground">
              暂无用户数据
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 创建/编辑用户对话框 -->
    <div
      v-if="showCreateDialog || showEditDialog"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
      @click.self="closeDialog"
    >
      <div class="bg-card border border-border rounded-lg p-6 w-full max-w-md">
        <h2 class="text-xl font-bold text-foreground mb-4">
          {{ showCreateDialog ? '创建用户' : '编辑用户' }}
        </h2>
        <form @submit.prevent="showCreateDialog ? handleCreate() : handleUpdate()">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">用户名 *</label>
              <input
                v-model="formData.username"
                type="text"
                required
                :disabled="showEditDialog"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground"
                :class="{ 'opacity-50 cursor-not-allowed': showEditDialog }"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">邮箱 *</label>
              <input
                v-model="formData.email"
                type="email"
                required
                :disabled="showEditDialog"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground"
                :class="{ 'opacity-50 cursor-not-allowed': showEditDialog }"
              />
            </div>
            <div v-if="showCreateDialog">
              <label class="block text-sm font-medium text-foreground mb-1">密码 *</label>
              <input
                v-model="formData.password"
                type="password"
                required
                minlength="6"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">全名</label>
              <input
                v-model="formData.fullName"
                type="text"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">个人简介</label>
              <textarea
                v-model="formData.bio"
                rows="3"
                class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground"
              ></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">职业路径</label>
              <div class="space-y-2">
                <label
                  v-for="path in careerPaths"
                  :key="path.id"
                  class="flex items-center space-x-2 cursor-pointer"
                >
                  <input
                    type="checkbox"
                    :value="path.id"
                    v-model="formData.careerPathIds"
                    class="rounded border-border"
                  />
                  <span class="text-foreground">{{ path.name }}</span>
                  <span class="text-xs text-muted-foreground">{{ path.description }}</span>
                </label>
              </div>
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-6">
            <button
              type="button"
              @click="closeDialog"
              class="px-4 py-2 bg-secondary text-secondary-foreground rounded-md hover:bg-secondary/80"
            >
              取消
            </button>
            <button
              type="submit"
              :disabled="submitting"
              class="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 disabled:opacity-50"
            >
              {{ submitting ? '提交中...' : (showCreateDialog ? '创建' : '更新') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 用户详情对话框 -->
    <div
      v-if="showDetailDialog"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
      @click.self="showDetailDialog = false"
    >
      <div class="bg-card border border-border rounded-lg p-6 w-full max-w-2xl">
        <h2 class="text-xl font-bold text-foreground mb-4">用户详情</h2>
        <div v-if="selectedUser" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-muted-foreground mb-1">ID</label>
              <p class="text-foreground">{{ selectedUser.id }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-muted-foreground mb-1">用户名</label>
              <p class="text-foreground font-medium">{{ selectedUser.username }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-muted-foreground mb-1">邮箱</label>
              <p class="text-foreground">{{ selectedUser.email }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-muted-foreground mb-1">全名</label>
              <p class="text-foreground">{{ selectedUser.fullName || '-' }}</p>
            </div>
            <div class="col-span-2">
              <label class="block text-sm font-medium text-muted-foreground mb-1">个人简介</label>
              <p class="text-foreground">{{ selectedUser.bio || '-' }}</p>
            </div>
            <div class="col-span-2">
              <label class="block text-sm font-medium text-muted-foreground mb-1">职业路径</label>
              <div class="flex flex-wrap gap-2 mt-1">
                <span
                  v-for="path in selectedUser.careerPaths"
                  :key="path.id"
                  class="px-3 py-1 bg-primary/10 text-primary rounded-md"
                >
                  {{ path.name }}
                </span>
                <span v-if="!selectedUser.careerPaths || selectedUser.careerPaths.length === 0" class="text-muted-foreground">
                  未设置
                </span>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-muted-foreground mb-1">创建时间</label>
              <p class="text-foreground">{{ formatDate(selectedUser.createdAt) }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-muted-foreground mb-1">更新时间</label>
              <p class="text-foreground">{{ formatDate(selectedUser.updatedAt) }}</p>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button
            @click="showDetailDialog = false"
            class="px-4 py-2 bg-secondary text-secondary-foreground rounded-md hover:bg-secondary/80"
          >
            关闭
          </button>
          <button
            @click="editUser(selectedUser)"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90"
          >
            编辑
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div
      v-if="showDeleteDialog"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
      @click.self="showDeleteDialog = false"
    >
      <div class="bg-card border border-border rounded-lg p-6 w-full max-w-md">
        <h2 class="text-xl font-bold text-foreground mb-4">确认删除</h2>
        <p class="text-muted-foreground mb-6">
          确定要删除用户 <span class="font-semibold text-foreground">{{ userToDelete?.username }}</span> 吗？
          此操作无法撤销。
        </p>
        <div class="flex justify-end gap-3">
          <button
            @click="showDeleteDialog = false"
            class="px-4 py-2 bg-secondary text-secondary-foreground rounded-md hover:bg-secondary/80"
          >
            取消
          </button>
          <button
            @click="handleDelete"
            :disabled="submitting"
            class="px-4 py-2 bg-destructive text-destructive-foreground rounded-md hover:bg-destructive/80 disabled:opacity-50"
          >
            {{ submitting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import userApi from '@/api/user'
import careerPathApi from '@/api/careerPath'

// 状态
const users = ref([])
const careerPaths = ref([])
const loading = ref(false)
const error = ref(null)
const submitting = ref(false)

// 对话框状态
const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const showDetailDialog = ref(false)
const showDeleteDialog = ref(false)

// 选中的用户
const selectedUser = ref(null)
const userToDelete = ref(null)

// 表单数据
const formData = ref({
  username: '',
  email: '',
  password: '',
  fullName: '',
  bio: '',
  careerPathIds: []
})

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await userApi.getUsers()
    users.value = response.data || []
  } catch (err) {
    error.value = err.response?.data?.message || '加载用户列表失败'
    console.error('加载用户列表失败:', err)
  } finally {
    loading.value = false
  }
}

// 加载职业路径
const loadCareerPaths = async () => {
  try {
    const response = await careerPathApi.getCareerPaths()
    careerPaths.value = response.data || []
  } catch (err) {
    console.error('加载职业路径失败:', err)
  }
}

// 查看用户详情
const viewUser = (user) => {
  selectedUser.value = user
  showDetailDialog.value = true
}

// 编辑用户
const editUser = (user) => {
  selectedUser.value = user
  formData.value = {
    username: user.username,
    email: user.email,
    password: '',
    fullName: user.fullName || '',
    bio: user.bio || '',
    careerPathIds: user.careerPaths?.map(p => p.id) || []
  }
  showDetailDialog.value = false
  showEditDialog.value = true
}

// 确认删除
const confirmDeleteUser = (user) => {
  userToDelete.value = user
  showDeleteDialog.value = true
}

// 创建用户
const handleCreate = async () => {
  submitting.value = true
  try {
    await userApi.createUser(formData.value)
    closeDialog()
    await loadUsers()
  } catch (err) {
    alert(err.response?.data?.message || '创建用户失败')
    console.error('创建用户失败:', err)
  } finally {
    submitting.value = false
  }
}

// 更新用户
const handleUpdate = async () => {
  submitting.value = true
  try {
    const updateData = {
      fullName: formData.value.fullName,
      bio: formData.value.bio,
      careerPathIds: formData.value.careerPathIds
    }
    await userApi.updateUser(selectedUser.value.id, updateData)
    closeDialog()
    await loadUsers()
  } catch (err) {
    alert(err.response?.data?.message || '更新用户失败')
    console.error('更新用户失败:', err)
  } finally {
    submitting.value = false
  }
}

// 删除用户
const handleDelete = async () => {
  submitting.value = true
  try {
    await userApi.deleteUser(userToDelete.value.id)
    showDeleteDialog.value = false
    userToDelete.value = null
    await loadUsers()
  } catch (err) {
    alert(err.response?.data?.message || '删除用户失败')
    console.error('删除用户失败:', err)
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const closeDialog = () => {
  showCreateDialog.value = false
  showEditDialog.value = false
  selectedUser.value = null
  formData.value = {
    username: '',
    email: '',
    password: '',
    fullName: '',
    bio: '',
    careerPathIds: []
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadUsers()
  loadCareerPaths()
})
</script>
