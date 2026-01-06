# Phase 7 Enhancements - 求职管理增强功能使用指南

> **创建时间**: 2026-01-04
> **功能**: 面试准备清单 + Focus Area关联 + Recruiter Insights

---

## 一、功能概述

### 1. 面试准备清单 (Interview Preparation Checklist)

**用途**: 为每个面试阶段创建个性化的准备清单，支持重点标记、分类管理、打印输出。

**特性**:
- ✅ 个性化清单：每个面试阶段独立的准备项
- ✅ 重点标记：标记重要项目，可单独提取打印成1-2页
- ✅ 分类管理：Study, Practice, Research, Review, Other
- ✅ 排序支持：自定义排序顺序
- ✅ Markdown备注：支持详细说明

**数据模型**:
```sql
CREATE TABLE `interview_preparation_checklist` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `interview_stage_id` bigint NOT NULL,         -- 关联的面试阶段
  `checklist_item` varchar(500) NOT NULL,       -- 准备项内容
  `is_priority` tinyint(1) DEFAULT 0,           -- 是否为重点项
  `category` varchar(50),                       -- 分类
  `notes` text,                                 -- 备注（Markdown）
  `sort_order` int DEFAULT 0,                   -- 排序
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. Focus Area 关联

**用途**: 将每个面试阶段关联到具体的Focus Areas，精准定位准备内容。

**数据模型**:
```sql
ALTER TABLE interview_stages
ADD COLUMN focus_area_ids JSON DEFAULT NULL;
```

**示例数据**:
```json
{
  "focus_area_ids": [12, 25, 38]  // 对应 "动态规划", "系统设计-缓存", "Behavioral-Leadership"
}
```

### 3. Recruiter Insights

**用途**: 记录从Recruiter处获取的结构化信息（团队、文化、技术栈、面试重点、流程tips）。

**数据模型**:
```sql
ALTER TABLE job_applications
ADD COLUMN recruiter_insights JSON DEFAULT NULL;
```

**示例数据**:
```json
{
  "teamSize": "10-15人",
  "teamCulture": "扁平化管理，鼓励创新，code review文化强",
  "techStackPreference": ["Java", "Spring Boot", "Kubernetes", "Microservices"],
  "interviewFocus": "系统设计能力和编程基础，会有2轮算法，1轮系统设计",
  "processTips": "第一轮技术面试会很深入，建议准备LeetCode Medium难度算法题。系统设计重点看架构思维而非具体技术栈。"
}
```

---

## 二、Backend API

### 1. 面试准备清单 API

#### 获取阶段的所有准备清单项
```http
GET /api/interview-preparation-checklist/stage/{stageId}
Authorization: Bearer <token>
```

**响应示例**:
```json
[
  {
    "id": 1,
    "interviewStageId": 10,
    "checklistItem": "复习动态规划模版（背包问题、最长子序列）",
    "isPriority": true,
    "category": "Study",
    "notes": "参考labuladong动态规划专题",
    "sortOrder": 1,
    "createdAt": "2026-01-04T10:00:00",
    "updatedAt": "2026-01-04T10:00:00"
  },
  {
    "id": 2,
    "interviewStageId": 10,
    "checklistItem": "刷10道LeetCode Medium难度题",
    "isPriority": true,
    "category": "Practice",
    "notes": null,
    "sortOrder": 2
  }
]
```

#### 获取重点准备清单项（用于打印）
```http
GET /api/interview-preparation-checklist/stage/{stageId}/priority
Authorization: Bearer <token>
```

**使用场景**: 面试前打印1-2页重点材料，只包含 `isPriority=true` 的项目。

#### 创建准备清单项
```http
POST /api/interview-preparation-checklist
Authorization: Bearer <token>
Content-Type: application/json

{
  "interviewStageId": 10,
  "checklistItem": "准备项目A的STAR回答",
  "isPriority": true,
  "category": "Study",
  "notes": "重点强调技术难点和business impact",
  "sortOrder": 1
}
```

#### 批量创建准备清单项
```http
POST /api/interview-preparation-checklist/batch
Authorization: Bearer <token>
Content-Type: application/json

[
  {
    "interviewStageId": 10,
    "checklistItem": "复习算法模版",
    "isPriority": true,
    "category": "Study",
    "sortOrder": 1
  },
  {
    "interviewStageId": 10,
    "checklistItem": "刷题10道",
    "isPriority": false,
    "category": "Practice",
    "sortOrder": 2
  }
]
```

#### 更新准备清单项
```http
PUT /api/interview-preparation-checklist/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "checklistItem": "复习动态规划模版（已完成）",
  "isPriority": true,
  "category": "Study",
  "notes": "已复习完成",
  "sortOrder": 1
}
```

#### 删除准备清单项
```http
DELETE /api/interview-preparation-checklist/{id}
Authorization: Bearer <token>
```

---

### 2. 面试阶段 API（支持新字段）

#### 创建面试阶段（包含Focus Areas）
```http
POST /api/job-applications/{jobId}/stages
Authorization: Bearer <token>
Content-Type: application/json

{
  "stageName": "Technical Interview Round 1",
  "stageOrder": 1,
  "skillIds": [1, 2],              // 编程与数据结构, 系统设计
  "focusAreaIds": [12, 25, 38],    // 动态规划, 缓存, Leadership
  "preparationNotes": "# 准备重点\n- 算法：动态规划\n- 系统设计：缓存策略"
}
```

#### 更新面试阶段
```http
PUT /api/interview-stages/{stageId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "stageName": "Technical Interview Round 1",
  "stageOrder": 1,
  "skillIds": [1, 2],
  "focusAreaIds": [12, 25, 38, 42],  // 新增了一个Focus Area
  "preparationNotes": "# 准备重点（已更新）\n- 算法：动态规划 + 贪心\n- 系统设计：缓存策略 + 分布式系统"
}
```

---

### 3. 职位申请 API（支持Recruiter Insights）

#### 创建职位申请（包含Recruiter Insights）
```http
POST /api/job-applications
Authorization: Bearer <token>
Content-Type: application/json

{
  "companyId": 1,
  "positionName": "Senior Software Engineer",
  "positionLevel": "Senior",
  "qualifications": "5+ years Java experience...",
  "responsibilities": "Design and develop microservices...",
  "recruiterInsights": {
    "teamSize": "10-15人",
    "teamCulture": "扁平化管理，鼓励创新",
    "techStackPreference": ["Java", "Spring Boot", "Kubernetes"],
    "interviewFocus": "系统设计和编程基础",
    "processTips": "第一轮技术面试会很深入，建议准备常见算法题"
  }
}
```

#### 更新职位申请（更新Recruiter Insights）
```http
PUT /api/job-applications/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "recruiterInsights": {
    "teamSize": "12-18人（最新信息）",
    "teamCulture": "扁平化管理，鼓励创新，remote-friendly",
    "techStackPreference": ["Java", "Spring Boot", "Kubernetes", "AWS"],
    "interviewFocus": "系统设计能力和编程基础，会有2轮算法，1轮系统设计",
    "processTips": "新增：Behavioral面试会问leadership经验，准备STAR回答"
  }
}
```

---

## 三、Frontend 使用示例

### 1. 导入API

```javascript
import interviewPreparationChecklistApi from '@/api/interviewPreparationChecklistApi'
import { interviewStageApi } from '@/api/interviewStageApi'
import { jobApplicationApi } from '@/api/jobApplicationApi'
```

### 2. 面试准备清单组件示例

```vue
<template>
  <div class="checklist-manager">
    <h3>{{ stageName }} - 准备清单</h3>

    <!-- 显示所有清单项 -->
    <div class="checklist-items">
      <div
        v-for="item in checklistItems"
        :key="item.id"
        :class="['checklist-item', { priority: item.isPriority }]"
      >
        <span class="category-badge">{{ item.category }}</span>
        <span class="item-text">{{ item.checklistItem }}</span>
        <button @click="editItem(item)">编辑</button>
        <button @click="deleteItem(item.id)">删除</button>
      </div>
    </div>

    <!-- 添加新项目 -->
    <button @click="showAddForm = true">添加准备项</button>

    <!-- 打印重点材料 -->
    <button @click="printPriorityItems">打印重点材料（1-2页）</button>

    <!-- 添加表单 -->
    <div v-if="showAddForm" class="add-form">
      <input v-model="newItem.checklistItem" placeholder="准备项内容" />
      <select v-model="newItem.category">
        <option value="Study">Study</option>
        <option value="Practice">Practice</option>
        <option value="Research">Research</option>
        <option value="Review">Review</option>
        <option value="Other">Other</option>
      </select>
      <label>
        <input type="checkbox" v-model="newItem.isPriority" />
        标记为重点
      </label>
      <textarea v-model="newItem.notes" placeholder="备注（Markdown）"></textarea>
      <button @click="addItem">保存</button>
      <button @click="showAddForm = false">取消</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import interviewPreparationChecklistApi from '@/api/interviewPreparationChecklistApi'

const props = defineProps({
  stageId: { type: Number, required: true },
  stageName: { type: String, required: true }
})

const checklistItems = ref([])
const showAddForm = ref(false)
const newItem = ref({
  interviewStageId: props.stageId,
  checklistItem: '',
  isPriority: false,
  category: 'Study',
  notes: '',
  sortOrder: 0
})

// 加载清单项
const loadChecklistItems = async () => {
  try {
    const data = await interviewPreparationChecklistApi.getChecklistByStageId(props.stageId)
    checklistItems.value = data || []
  } catch (error) {
    console.error('加载准备清单失败:', error)
  }
}

// 添加清单项
const addItem = async () => {
  try {
    newItem.value.sortOrder = checklistItems.value.length + 1
    await interviewPreparationChecklistApi.createChecklist(newItem.value)
    showAddForm.value = false
    await loadChecklistItems()
    // 重置表单
    newItem.value = {
      interviewStageId: props.stageId,
      checklistItem: '',
      isPriority: false,
      category: 'Study',
      notes: '',
      sortOrder: 0
    }
  } catch (error) {
    console.error('添加准备项失败:', error)
  }
}

// 删除清单项
const deleteItem = async (id) => {
  if (!confirm('确定删除此准备项？')) return
  try {
    await interviewPreparationChecklistApi.deleteChecklist(id)
    await loadChecklistItems()
  } catch (error) {
    console.error('删除准备项失败:', error)
  }
}

// 打印重点材料
const printPriorityItems = async () => {
  try {
    const priorityItems = await interviewPreparationChecklistApi.getPriorityChecklistByStageId(props.stageId)
    // 生成打印页面
    const printWindow = window.open('', '_blank')
    printWindow.document.write(`
      <html>
      <head>
        <title>${props.stageName} - 重点准备材料</title>
        <style>
          body { font-family: Arial, sans-serif; padding: 20px; }
          h1 { border-bottom: 2px solid #333; }
          .item { margin: 15px 0; padding: 10px; border-left: 3px solid #007bff; }
          .category { color: #666; font-size: 0.9em; }
        </style>
      </head>
      <body>
        <h1>${props.stageName} - 重点准备材料</h1>
        ${priorityItems.map(item => `
          <div class="item">
            <div class="category">[${item.category}]</div>
            <div>${item.checklistItem}</div>
            ${item.notes ? `<div style="margin-top:5px;color:#666;">${item.notes}</div>` : ''}
          </div>
        `).join('')}
      </body>
      </html>
    `)
    printWindow.document.close()
    printWindow.print()
  } catch (error) {
    console.error('打印重点材料失败:', error)
  }
}

onMounted(() => {
  loadChecklistItems()
})
</script>

<style scoped>
.checklist-item.priority {
  background-color: #fff3cd;
  border-left: 3px solid #ffc107;
}
.category-badge {
  background-color: #007bff;
  color: white;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 0.85em;
  margin-right: 10px;
}
</style>
```

### 3. Recruiter Insights 表单示例

```vue
<template>
  <div class="recruiter-insights-form">
    <h3>Recruiter提供的信息</h3>

    <div class="form-group">
      <label>团队规模</label>
      <input v-model="insights.teamSize" placeholder="如: 10-15人" />
    </div>

    <div class="form-group">
      <label>团队文化</label>
      <textarea
        v-model="insights.teamCulture"
        placeholder="扁平化管理，鼓励创新..."
        rows="3"
      ></textarea>
    </div>

    <div class="form-group">
      <label>技术栈偏好</label>
      <input
        v-model="techStackInput"
        placeholder="Java, Spring Boot, Kubernetes (逗号分隔)"
        @blur="updateTechStack"
      />
    </div>

    <div class="form-group">
      <label>面试重点</label>
      <textarea
        v-model="insights.interviewFocus"
        placeholder="系统设计和编程基础..."
        rows="3"
      ></textarea>
    </div>

    <div class="form-group">
      <label>内部流程Tips</label>
      <textarea
        v-model="insights.processTips"
        placeholder="第一轮技术面试会很深入..."
        rows="4"
      ></textarea>
    </div>

    <button @click="saveInsights">保存</button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { jobApplicationApi } from '@/api/jobApplicationApi'

const props = defineProps({
  jobApplicationId: { type: Number, required: true },
  initialInsights: { type: Object, default: () => ({}) }
})

const insights = ref({
  teamSize: '',
  teamCulture: '',
  techStackPreference: [],
  interviewFocus: '',
  processTips: '',
  ...props.initialInsights
})

const techStackInput = ref(
  props.initialInsights.techStackPreference?.join(', ') || ''
)

// 更新技术栈数组
const updateTechStack = () => {
  insights.value.techStackPreference = techStackInput.value
    .split(',')
    .map(s => s.trim())
    .filter(s => s.length > 0)
}

// 保存Recruiter Insights
const saveInsights = async () => {
  try {
    await jobApplicationApi.updateJobApplication(props.jobApplicationId, {
      recruiterInsights: insights.value
    })
    alert('Recruiter信息已保存！')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请重试')
  }
}

// 监听初始数据变化
watch(() => props.initialInsights, (newVal) => {
  insights.value = { ...insights.value, ...newVal }
  techStackInput.value = newVal.techStackPreference?.join(', ') || ''
}, { deep: true })
</script>
```

---

## 四、完整使用流程示例

### 场景：为Google软件工程师职位准备面试

#### Step 1: 创建职位申请并记录Recruiter信息

```javascript
const jobData = {
  companyId: 1,  // Google
  positionName: 'Senior Software Engineer',
  positionLevel: 'L5',
  recruiterInsights: {
    teamSize: '12-15人',
    teamCulture: 'Google典型文化：创新驱动，20%自由时间，code review严格',
    techStackPreference: ['C++', 'Java', 'Python', 'Distributed Systems'],
    interviewFocus: '系统设计占40%，算法占40%，Behavioral占20%',
    processTips: '算法题难度Medium-Hard，系统设计注重scalability和trade-offs分析'
  }
}

const job = await jobApplicationApi.createJobApplication(jobData)
```

#### Step 2: 创建面试阶段（Phone Screen）

```javascript
const phoneScreenStage = {
  stageName: 'Phone Screen',
  stageOrder: 1,
  skillIds: [1],  // 编程与数据结构
  focusAreaIds: [12, 15, 20],  // 动态规划、树、图
  preparationNotes: `
# Phone Screen准备重点
- 算法：动态规划、树、图
- 难度：LeetCode Medium
- 时长：45分钟（30分钟coding + 15分钟Q&A）
  `
}

const stage1 = await jobApplicationApi.createInterviewStage(job.id, phoneScreenStage)
```

#### Step 3: 为Phone Screen创建准备清单

```javascript
const checklistItems = [
  {
    interviewStageId: stage1.id,
    checklistItem: '复习动态规划模版（背包、最长子序列、股票问题）',
    isPriority: true,
    category: 'Study',
    notes: '参考labuladong DP专题',
    sortOrder: 1
  },
  {
    interviewStageId: stage1.id,
    checklistItem: '刷LeetCode Medium难度题20道（DP + 树 + 图）',
    isPriority: true,
    category: 'Practice',
    notes: '优先刷Google高频题',
    sortOrder: 2
  },
  {
    interviewStageId: stage1.id,
    checklistItem: '准备项目经验STAR回答（2-3个技术难点项目）',
    isPriority: true,
    category: 'Study',
    notes: '强调scalability和impact',
    sortOrder: 3
  },
  {
    interviewStageId: stage1.id,
    checklistItem: '模拟面试1次（与朋友或LeetCode mock interview）',
    isPriority: false,
    category: 'Practice',
    sortOrder: 4
  }
]

await interviewPreparationChecklistApi.batchCreateChecklist(checklistItems)
```

#### Step 4: 创建Onsite阶段

```javascript
const onsiteStage = {
  stageName: 'Onsite Round 1 - System Design',
  stageOrder: 2,
  skillIds: [2],  // 系统设计
  focusAreaIds: [25, 28, 30],  // 缓存、数据库、分布式系统
  preparationNotes: `
# 系统设计面试准备
- 时长：60分钟
- 重点：Scalability, Trade-offs, Availability
- 常见题型：设计Twitter、设计URL Shortener、设计Rate Limiter
  `
}

const stage2 = await jobApplicationApi.createInterviewStage(job.id, onsiteStage)
```

#### Step 5: 为Onsite创建准备清单

```javascript
const onsiteChecklist = [
  {
    interviewStageId: stage2.id,
    checklistItem: '复习系统设计6步法（需求澄清→实体设计→API设计→数据流→架构→深入讨论）',
    isPriority: true,
    category: 'Study',
    sortOrder: 1
  },
  {
    interviewStageId: stage2.id,
    checklistItem: '准备3个经典案例（Twitter, URL Shortener, Rate Limiter）',
    isPriority: true,
    category: 'Study',
    notes: '每个案例准备多种方案（基础版、高级版）',
    sortOrder: 2
  },
  {
    interviewStageId: stage2.id,
    checklistItem: '研究Google技术栈（Bigtable, Spanner, GFS）',
    isPriority: false,
    category: 'Research',
    sortOrder: 3
  }
]

await interviewPreparationChecklistApi.batchCreateChecklist(onsiteChecklist)
```

#### Step 6: 面试前一天 - 打印重点材料

```javascript
// 获取Phone Screen重点材料
const phoneScreenPriority = await interviewPreparationChecklistApi
  .getPriorityChecklistByStageId(stage1.id)

// 获取Onsite重点材料
const onsitePriority = await interviewPreparationChecklistApi
  .getPriorityChecklistByStageId(stage2.id)

// 生成打印页面（参考上面的Vue组件示例）
```

---

## 五、最佳实践

### 1. 准备清单的层次规划

**推荐结构**:
```
Phone Screen (1轮)
  └─ 重点准备项（3-5个，isPriority=true）
      ├─ 复习算法模版
      ├─ 刷题20道
      └─ 准备项目经验
  └─ 次要准备项（2-3个，isPriority=false）
      ├─ 模拟面试
      └─ 公司研究

Onsite (3-4轮)
  ├─ Round 1 - System Design
  │   ├─ 重点：系统设计6步法 + 3个经典案例
  │   └─ 次要：技术栈研究
  ├─ Round 2 - Coding
  │   ├─ 重点：Hard难度算法
  │   └─ 次要：边界条件测试
  └─ Round 3 - Behavioral
      ├─ 重点：Leadership STAR案例
      └─ 次要：公司文化契合度问题
```

### 2. Recruiter Insights 记录技巧

**分阶段记录**:
- **初次联系**: 团队规模、职位级别
- **第一次沟通**: 技术栈、面试流程
- **深入沟通**: 团队文化、面试重点、内部tips
- **Offer阶段**: 薪资范围（不在recruiterInsights中，用baseSalary等字段）

**信息来源标注**:
```json
{
  "processTips": "[Recruiter Mary, 2026-01-03] 第一轮技术面试会很深入，建议准备常见算法题。[内推同事John, 2026-01-04] 面试官Joe喜欢问trade-offs问题。"
}
```

### 3. Focus Area 选择策略

**精准定位**:
- ❌ 错误：`skillIds: [1]` - 只选Skill（太宽泛）
- ✅ 正确：`skillIds: [1], focusAreaIds: [12, 15, 20]` - Skill + 具体Focus Areas

**覆盖完整**:
- Phone Screen → 2-3个Focus Areas
- Onsite每轮 → 3-5个Focus Areas
- Behavioral → 选择Leadership/Communication等软技能Focus Areas

---

## 六、常见问题 (FAQ)

### Q1: 准备清单的重点项应该控制在多少个？
**A**: 建议每个面试阶段的重点项控制在 **3-7个**，确保打印成1-2页。如果超过10个，说明划分不够精准。

### Q2: Recruiter Insights 和 notes 字段有什么区别？
**A**:
- `recruiterInsights` (job_applications表): 结构化的Recruiter提供的信息
- `notes` (job_applications表): 非结构化的个人备注
- `preparationNotes` (interview_stages表): 面试阶段的准备说明

### Q3: Focus Area IDs 从哪里获取？
**A**: 通过 `/api/focus-areas` API获取所有Focus Areas列表，或者通过 `/api/skills/{skillId}/focus-areas` 获取某个Skill下的Focus Areas。

### Q4: 如何批量导入已有的准备清单？
**A**: 使用 `POST /api/interview-preparation-checklist/batch` 批量创建接口。

---

## 七、错误处理

### 常见错误

**1. 403 Forbidden - 无权访问**
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "无权访问此面试阶段的准备清单"
}
```
**原因**: 准备清单所属的面试阶段不属于当前用户
**解决**: 检查 `interviewStageId` 是否正确

**2. 404 Not Found - 面试阶段不存在**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "面试阶段不存在"
}
```
**原因**: `interviewStageId` 无效
**解决**: 先调用 `/api/job-applications/{jobId}/stages` 确认阶段存在

**3. 400 Bad Request - JSON字段格式错误**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "JSON字段格式错误: ..."
}
```
**原因**: `recruiterInsights` 或 `focusAreaIds` 格式错误
**解决**: 确保JSON格式正确，`focusAreaIds` 是数组

---

## 八、后续扩展建议

### 可选功能（未实现）

1. **准备清单模板**
   - 为常见面试类型（算法面试、系统设计面试、Behavioral面试）提供模板
   - 用户可以基于模板快速生成清单

2. **完成进度追踪**
   - 添加 `is_completed` 字段到 `interview_preparation_checklist` 表
   - 统计准备进度百分比

3. **AI辅助生成**
   - 根据 `focusAreaIds` 自动推荐准备清单项
   - 基于 `recruiterInsights` 调整准备重点

4. **Recruiter通信历史**
   - 将 `recruiterInsights` 关联到 `recruiter_communications` 表
   - 追踪信息来源和更新时间

---

## 九、相关文件

**Backend**:
- Entity: `InterviewPreparationChecklist.java`, `InterviewStage.java`, `JobApplication.java`
- DTO: `InterviewPreparationChecklistDTO.java`, `RecruiterInsightsDTO.java`, `InterviewStageDTO.java`, `JobApplicationDTO.java`
- Repository: `InterviewPreparationChecklistRepository.java`
- Service: `InterviewPreparationChecklistService.java`, `InterviewStageService.java`, `JobApplicationService.java`
- Controller: `InterviewPreparationChecklistController.java`

**Frontend**:
- API: `interviewPreparationChecklistApi.js`, `interviewStageApi.js`, `jobApplicationApi.js`

**Database**:
- Migration: `database/migrations/phase7_enhancements.sql`

---

**文档版本**: 1.0
**最后更新**: 2026-01-04
