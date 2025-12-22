# Phase 3 - 试题库基础功能设计文档

> **版本**: v1.0
> **日期**: 2025-12-21
> **状态**: 设计阶段

## 一、总体架构

### 1.1 系统概述

Phase 3 在 Phase 2 技能体系管理的基础上，构建基于Focus Area的试题库系统。支持管理员导入公共试题、用户添加个人试题，并为试题添加个人笔记，为后续的求职准备和核心技能模块提供基础数据支撑。

### 1.2 技术栈

**后端**:
- Spring Boot 3.2.0
- Spring Data JPA + Hibernate
- MySQL 8.0/9.4
- Flyway (数据库迁移)
- Jackson (JSON处理，Red Flags数组序列化)

**前端**:
- Vue 3 Composition API
- Vue Router 4
- Vite
- Tailwind CSS
- Markdown编辑器: `marked` + `highlight.js`

### 1.3 核心概念层级

```
用户 (User) - Phase 1
  └─ 职业路径 (Career Path) - Phase 1
       └─ 技能 (Skill) - Phase 2
            └─ 专注领域 (Focus Area) - Phase 2
                 └─ 试题 (Question) - Phase 3 NEW
                      └─ 用户笔记 (User Question Note) - Phase 3 NEW
```

**关键设计决策**:
- **试题归属**: 试题直接归属于Focus Area（一个Focus Area有多个试题）
- **答案要求**: 1:1关系，存储在questions表的answer_requirement字段
- **Red Flags**: 存储为JSON数组，在questions表的red_flags字段
- **用户笔记**: 独立表user_question_notes，UNIQUE约束(question_id, user_id)
- **可见性**: 公共试题(is_official=true) + 用户私有试题(created_by_user_id)

## 二、数据模型设计

### 2.1 实体关系图 (ERD)

```
users (Phase 1)
  └─ one-to-many → questions (created_by_user_id)
       └─ one-to-many → user_question_notes

focus_areas (Phase 2)
  └─ one-to-many → questions

questions (Phase 3)
  └─ one-to-many → user_question_notes (Phase 3)

users (Phase 1)
  └─ one-to-many → user_question_notes
```

### 2.2 核心表设计

#### questions 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| focus_area_id | BIGINT | FK, NOT NULL, INDEX | 所属Focus Area |
| question_text | TEXT | NOT NULL | 问题描述（Markdown） |
| difficulty | ENUM('EASY','MEDIUM','HARD') | NOT NULL | 难度级别 |
| answer_requirement | TEXT | | 答案要求（Markdown）- 说明如何回答 |
| target_position | VARCHAR(100) | | 针对职位（如"软件工程师"） |
| target_level | VARCHAR(50) | | 针对级别（如"Senior"） |
| red_flags | TEXT | | Red Flags列表（JSON数组） |
| is_official | TINYINT(1) | DEFAULT 0, INDEX | 是否公共试题 |
| created_by_user_id | BIGINT | FK, INDEX | 创建者ID（admin创建则为null） |
| display_order | INT | DEFAULT 0, INDEX | 显示顺序 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**外键约束**:
- `focus_area_id` → `focus_areas(id)` ON DELETE CASCADE
- `created_by_user_id` → `users(id)` ON DELETE CASCADE

**索引**:
```sql
CREATE INDEX idx_focus_area_id ON questions(focus_area_id);
CREATE INDEX idx_is_official ON questions(is_official);
CREATE INDEX idx_created_by_user_id ON questions(created_by_user_id);
CREATE INDEX idx_difficulty ON questions(difficulty);
CREATE INDEX idx_target_position ON questions(target_position);
CREATE INDEX idx_target_level ON questions(target_level);
CREATE INDEX idx_display_order ON questions(display_order);
```

**字段说明**:
- `answer_requirement`: 与试题1:1关系，说明如何回答这个问题（如"1. 定义DP核心概念 2. 说明子问题重叠..."）
- `red_flags`: JSON数组格式，如 `["无法解释子问题重叠", "混淆贪心算法和动态规划"]`
- `target_position`/`target_level`: 可选，为空表示适用于所有职位/级别

#### user_question_notes 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| question_id | BIGINT | FK, NOT NULL | 所属问题ID |
| user_id | BIGINT | FK, NOT NULL | 创建者ID |
| note_content | TEXT | NOT NULL | 笔记内容（Markdown） |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**外键约束**:
- `question_id` → `questions(id)` ON DELETE CASCADE
- `user_id` → `users(id)` ON DELETE CASCADE

**唯一约束**:
```sql
UNIQUE KEY uk_question_user (question_id, user_id)
```

**说明**:
- 一个用户对一个试题只能有一条笔记记录
- 用户可以随时编辑更新笔记内容（通过UPSERT逻辑实现）
- `updated_at`字段记录最后修改时间

**索引**:
```sql
CREATE INDEX idx_question_id ON user_question_notes(question_id);
CREATE INDEX idx_user_id ON user_question_notes(user_id);
CREATE UNIQUE INDEX uk_question_user ON user_question_notes(question_id, user_id);
```

### 2.3 枚举定义

**Difficulty (难度)**:
```sql
ENUM('EASY', 'MEDIUM', 'HARD')
```

**Position Types (职位类型)**:
- 值来源于 Phase 1 的 `career_paths` 表记录
- 用户在 `target_position` 字段中引用职业路径名称（如"软件工程师"、"职业经理"）
- 前端通过查询 `career_paths` 表填充下拉选项

**Level Types (级别类型，参考值)**:
- Junior (初级)
- Senior (高级)
- Staff (资深)
- Principal (首席)
- **说明**: 以后在求职模块（Phase 5）可能会和具体公司的level做映射

### 2.4 级联删除规则

```
删除 Focus Area
  └─ CASCADE → questions
       └─ CASCADE → user_question_notes

删除 Question
  └─ CASCADE → user_question_notes

删除 User (暂不实现)
  ├─ CASCADE → questions (created_by_user_id)
  │    └─ CASCADE → user_question_notes
  └─ CASCADE → user_question_notes (用户创建的笔记)
```

**说明**: Phase 3暂不实现用户删除功能，级联删除规则在数据库层已定义，未来启用时自动生效。

### 2.5 可见性规则

**查询逻辑**:

```sql
-- 用户视角（只看公共试题 + 自己的私有试题）
SELECT * FROM questions
WHERE focus_area_id = ?
  AND (is_official = true OR created_by_user_id = ?)
ORDER BY difficulty ASC, display_order ASC, created_at DESC

-- 管理员视角（可看到所有试题）
SELECT * FROM questions
WHERE focus_area_id = ?
ORDER BY difficulty ASC, display_order ASC, created_at DESC
```

## 三、后端API设计

### 3.1 用户端API

#### GET /api/questions/focus-areas/{focusAreaId}
**功能**: 获取Focus Area下的试题列表（公共 + 用户自己的）

**请求参数**:
- `difficulty` (可选): 难度筛选 (EASY/MEDIUM/HARD)
- `position` (可选): 职位筛选
- `level` (可选): 级别筛选

**响应**:
```json
[
  {
    "id": 1,
    "focusAreaId": 5,
    "questionText": "解释什么是动态规划...",
    "difficulty": "MEDIUM",
    "targetPosition": "软件工程师",
    "targetLevel": "Senior",
    "isOfficial": true,
    "createdByUserId": null,
    "createdAt": "2025-12-20T10:00:00Z"
  }
]
```

#### GET /api/questions/{id}
**功能**: 获取试题详情（包含答案要求、Red Flags和用户笔记）

**响应**:
```json
{
  "id": 1,
  "focusAreaId": 5,
  "focusAreaName": "动态规划",
  "questionText": "解释什么是动态规划，并给出一个例子",
  "difficulty": "MEDIUM",
  "answerRequirement": "1. 定义动态规划的核心概念\n2. 说明子问题重叠和最优子结构\n3. 给出斐波那契数列的例子",
  "targetPosition": "软件工程师",
  "targetLevel": "Senior",
  "redFlags": ["无法解释子问题重叠", "无法解释最优子结构", "混淆贪心算法和动态规划"],
  "isOfficial": true,
  "userNote": {
    "id": 1,
    "noteContent": "我的答案思路：\n1. 使用动态规划\n2. 状态转移方程...",
    "createdAt": "2025-12-21T10:00:00Z",
    "updatedAt": "2025-12-21T11:00:00Z"
  },
  "createdAt": "2025-12-20T10:00:00Z"
}
```

**说明**: 如果用户没有为该试题添加笔记，`userNote`字段为`null`

#### POST /api/questions
**功能**: 用户添加个人试题

**请求体**:
```json
{
  "focusAreaId": 5,
  "questionText": "我的面试题...",
  "difficulty": "EASY",
  "answerRequirement": "关键点1\n关键点2\n关键点3",
  "targetPosition": "软件工程师",
  "targetLevel": "Mid",
  "redFlags": ["不要说xxx", "避免提到yyy"]
}
```

**响应**: 201 Created + 试题对象

**权限**: 需登录，`isOfficial`自动设为false，`createdByUserId`自动设为当前用户

#### PUT /api/questions/{id}
**功能**: 更新试题（只能更新自己创建的）

**权限**: 只能更新`created_by_user_id = 当前用户`的试题

#### DELETE /api/questions/{id}
**功能**: 删除试题（只能删除自己创建的）

**权限**: 只能删除`created_by_user_id = 当前用户`的试题

#### GET /api/questions/{id}/note
**功能**: 获取用户对某试题的笔记

**响应**:
```json
{
  "id": 1,
  "questionId": 10,
  "noteContent": "我的答案思路：\n1. ...\n2. ...",
  "createdAt": "2025-12-21T10:00:00Z",
  "updatedAt": "2025-12-21T11:00:00Z"
}
```

如果无笔记，返回 `404 Not Found`

**权限**: 需登录

#### POST /api/questions/{id}/note
**功能**: 为试题添加笔记（如果已有笔记则更新）

**请求体**:
```json
{
  "noteContent": "我的答案思路：\n1. 使用动态规划\n2. 状态转移方程..."
}
```

**响应**: 201 Created (新建) 或 200 OK (更新) + 笔记对象

**权限**: 需登录，自动关联当前用户

**说明**: 使用 UPSERT 逻辑（如果存在则更新，不存在则插入）

#### DELETE /api/questions/{id}/note
**功能**: 删除用户对某试题的笔记

**权限**: 需登录，只能删除自己的笔记

### 3.2 管理员API

#### GET /api/admin/questions
**功能**: 获取所有试题（支持分页和筛选）

**请求参数**:
- `focusAreaId` (可选): Focus Area筛选
- `skillId` (可选): Skill筛选
- `difficulty` (可选): 难度筛选
- `isOfficial` (可选): 是否公共试题

**权限**: 需admin角色

#### POST /api/admin/questions
**功能**: 管理员添加公共试题

**请求体**: 同用户端，但`isOfficial`自动设为true

**权限**: 需admin角色

#### PUT /api/admin/questions/{id}
**功能**: 更新任意试题

**权限**: 需admin角色

#### DELETE /api/admin/questions/{id}
**功能**: 删除任意试题

**权限**: 需admin角色

## 四、前端路由设计

### 4.1 用户端路由

| 路径 | 组件 | 说明 |
|------|------|------|
| `/my-questions` | MyQuestionBank.vue | 我的题库（单页面三栏布局） |

**MyQuestionBank.vue 布局特点**:
- 职业路径Tab（顶部）
- 左侧面板 (25%): 技能-Focus Area树
- 中间面板 (35%): 试题列表 + 难度筛选
- 右侧面板 (40%): 试题详情 + 我的笔记

### 4.2 管理员路由

| 路径 | 组件 | 说明 |
|------|------|------|
| `/admin/questions` | QuestionManagement.vue | 试题管理（两栏布局） |

**QuestionManagement.vue 布局特点**:
- 左侧面板 (384px): 技能列表 → Focus Area列表
- 右侧面板 (flex-1): 试题列表 + CRUD操作

## 五、前端组件设计

### 5.1 核心组件

#### MyQuestionBank.vue
**功能**: 单页面三栏布局的用户题库

**组件结构**:
```vue
<template>
  <div class="my-question-bank">
    <!-- 顶部：职业路径Tab -->
    <div class="career-path-tabs">
      <button v-for="cp in careerPaths" :key="cp.id">
        {{ cp.icon }} {{ cp.name }}
      </button>
    </div>

    <!-- 三栏布局 -->
    <div class="three-column-layout">
      <!-- 左侧：技能-Focus Area树 -->
      <aside class="left-panel w-1/4">
        <SkillFocusAreaTree
          :careerPathId="selectedCareerPathId"
          @select="onFocusAreaSelected"
        />
      </aside>

      <!-- 中间：试题列表 -->
      <section class="middle-panel w-1/3">
        <div class="filters">
          <button @click="filterByDifficulty('ALL')">全部</button>
          <button @click="filterByDifficulty('EASY')">Easy</button>
          <button @click="filterByDifficulty('MEDIUM')">Medium</button>
          <button @click="filterByDifficulty('HARD')">Hard</button>
        </div>
        <button @click="showAddQuestionModal">+ 添加我的试题</button>
        <QuestionCard
          v-for="q in filteredQuestions"
          :key="q.id"
          :question="q"
          @click="selectQuestion(q)"
        />
      </section>

      <!-- 右侧：试题详情 + 笔记 -->
      <section class="right-panel flex-1">
        <QuestionDetail
          v-if="selectedQuestion"
          :question="selectedQuestion"
          :userNote="userNote"
          @edit-note="showNoteEditor"
        />
      </section>
    </div>
  </div>
</template>
```

**状态管理**:
```javascript
const selectedCareerPathId = ref(null)
const selectedFocusAreaId = ref(null)
const selectedQuestion = ref(null)
const questions = ref([])
const userNote = ref(null)
const difficulty = ref('ALL')

const filteredQuestions = computed(() => {
  if (difficulty.value === 'ALL') return questions.value
  return questions.value.filter(q => q.difficulty === difficulty.value)
})
```

#### QuestionManagement.vue
**功能**: 管理员试题管理（两栏布局）

**组件结构**:
```vue
<template>
  <div class="question-management">
    <!-- 左侧：技能-Focus Area选择 -->
    <aside class="left-panel w-96">
      <CareerPathTabs vertical />
      <SkillList @select="onSkillSelected" />
      <FocusAreaList
        v-if="selectedSkill"
        :skillId="selectedSkill.id"
        @select="onFocusAreaSelected"
      />
    </aside>

    <!-- 右侧：试题列表 + CRUD -->
    <section class="right-panel flex-1">
      <div class="header">
        <h2>{{ selectedFocusArea?.name }} - 试题列表</h2>
        <button @click="showAddQuestionModal">+ 添加试题</button>
      </div>
      <QuestionCard
        v-for="q in questions"
        :key="q.id"
        :question="q"
        showCreator
        @edit="editQuestion(q)"
        @delete="deleteQuestion(q)"
      />
    </section>

    <!-- Modal -->
    <QuestionEditModal
      v-if="showModal"
      :question="editingQuestion"
      @save="saveQuestion"
      @cancel="showModal = false"
    />
  </div>
</template>
```

### 5.2 可复用组件

#### QuestionCard.vue
**功能**: 试题卡片（列表展示）

**Props**:
- `question` (Object): 试题对象
- `showCreator` (Boolean): 是否显示创建者（管理员视图）

**Emits**:
- `click`: 点击卡片
- `edit`: 编辑试题（管理员）
- `delete`: 删除试题（管理员）

**模板**:
```vue
<template>
  <div class="question-card" @click="$emit('click')">
    <div class="header">
      <DifficultyBadge :difficulty="question.difficulty" />
      <span v-if="question.isOfficial" class="badge">公共</span>
      <span v-else class="badge">我的</span>
    </div>
    <h3>{{ truncate(question.questionText, 100) }}</h3>
    <div class="meta">
      <span v-if="question.targetPosition">{{ question.targetPosition }}</span>
      <span v-if="question.targetLevel">({{ question.targetLevel }})</span>
      <span v-if="showCreator" class="creator">
        创建者: {{ question.createdByUserId ? 'user' + question.createdByUserId : 'Admin' }}
      </span>
    </div>
    <div v-if="showActions" class="actions">
      <button @click.stop="$emit('edit')">编辑</button>
      <button @click.stop="$emit('delete')">删除</button>
    </div>
  </div>
</template>
```

#### QuestionEditModal.vue
**功能**: 试题编辑弹窗

**Props**:
- `question` (Object, optional): 编辑模式时传入已有试题

**Emits**:
- `save`: 保存试题
- `cancel`: 取消

**表单字段**:
```vue
<template>
  <Modal>
    <form @submit.prevent="handleSubmit">
      <!-- 所属Focus Area -->
      <select v-model="form.focusAreaId" required>
        <option v-for="fa in focusAreas" :key="fa.id" :value="fa.id">
          {{ fa.name }}
        </option>
      </select>

      <!-- 问题描述 -->
      <textarea v-model="form.questionText" placeholder="问题描述" required />
      <MarkdownPreview :content="form.questionText" />

      <!-- 难度级别 -->
      <div class="difficulty-selector">
        <label><input type="radio" v-model="form.difficulty" value="EASY" required> Easy</label>
        <label><input type="radio" v-model="form.difficulty" value="MEDIUM"> Medium</label>
        <label><input type="radio" v-model="form.difficulty" value="HARD"> Hard</label>
      </div>

      <!-- 答案要求 -->
      <textarea v-model="form.answerRequirement" placeholder="答案要求（如何回答这个问题）" />
      <MarkdownPreview :content="form.answerRequirement" />

      <!-- 针对职位（动态从career_paths加载） -->
      <select v-model="form.targetPosition">
        <option value="">不限职位</option>
        <option v-for="cp in careerPaths" :key="cp.id" :value="cp.name">
          {{ cp.icon }} {{ cp.name }}
        </option>
      </select>

      <!-- 针对级别 -->
      <select v-model="form.targetLevel">
        <option value="">不限级别</option>
        <option value="Junior">Junior</option>
        <option value="Senior">Senior</option>
        <option value="Staff">Staff</option>
        <option value="Principal">Principal</option>
      </select>

      <!-- Red Flags -->
      <RedFlagList v-model="form.redFlags" />

      <div class="actions">
        <button type="button" @click="$emit('cancel')">取消</button>
        <button type="submit">保存</button>
      </div>
    </form>
  </Modal>
</template>
```

#### RedFlagList.vue
**功能**: Red Flag列表编辑组件

**Props**:
- `modelValue` (Array): Red Flags数组

**Emits**:
- `update:modelValue`: 更新Red Flags

**模板**:
```vue
<template>
  <div class="red-flag-list">
    <h4>⚠️ Red Flags</h4>
    <div v-for="(flag, index) in redFlags" :key="index" class="flag-item">
      <input v-model="redFlags[index]" placeholder="Red Flag描述" />
      <button @click="removeFlag(index)">删除</button>
    </div>
    <button @click="addFlag">+ 添加Red Flag</button>
  </div>
</template>

<script setup>
const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const redFlags = ref(props.modelValue || [])

const addFlag = () => {
  redFlags.value.push('')
}

const removeFlag = (index) => {
  redFlags.value.splice(index, 1)
  emit('update:modelValue', redFlags.value)
}

watch(redFlags, (newVal) => {
  emit('update:modelValue', newVal)
}, { deep: true })
</script>
```

#### UserNoteEditor.vue
**功能**: 用户笔记编辑器（Markdown支持）

**Props**:
- `questionId` (Number): 试题ID
- `initialNote` (Object, optional): 已有笔记

**Emits**:
- `save`: 保存笔记
- `cancel`: 取消

**模板**:
```vue
<template>
  <Modal>
    <h3>📝 我的笔记</h3>
    <textarea
      v-model="noteContent"
      placeholder="记录你的答案思路、补充说明、心得..."
      rows="15"
    />
    <MarkdownPreview :content="noteContent" />
    <div class="actions">
      <button @click="$emit('cancel')">取消</button>
      <button @click="handleSave">保存</button>
    </div>
  </Modal>
</template>

<script setup>
const props = defineProps(['questionId', 'initialNote'])
const emit = defineEmits(['save', 'cancel'])

const noteContent = ref(props.initialNote?.noteContent || '')

const handleSave = async () => {
  await questionApi.saveOrUpdateNote(props.questionId, { noteContent: noteContent.value })
  emit('save')
}
</script>
```

#### DifficultyBadge.vue
**功能**: 难度标签（不同颜色）

**Props**:
- `difficulty` (String): EASY/MEDIUM/HARD

**模板**:
```vue
<template>
  <span :class="['difficulty-badge', difficultyClass]">
    {{ difficulty }}
  </span>
</template>

<script setup>
const props = defineProps(['difficulty'])

const difficultyClass = computed(() => {
  switch (props.difficulty) {
    case 'EASY': return 'bg-green-500 text-white'
    case 'MEDIUM': return 'bg-yellow-500 text-white'
    case 'HARD': return 'bg-red-500 text-white'
    default: return ''
  }
})
</script>
```

## 六、后端Service层设计

### 6.1 QuestionService

**核心方法**:

```java
// 获取Focus Area下的试题（用户可见）
public List<QuestionDTO> getQuestionsByFocusAreaId(Long focusAreaId, Long userId) {
    return questionRepository.findByFocusAreaIdAndVisibleToUser(focusAreaId, userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

// 获取试题详情（含用户笔记）
public QuestionDTO getQuestionByIdForUser(Long id, Long userId) {
    Question question = questionRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

    QuestionDTO dto = convertToDTO(question);

    // 加载用户笔记
    Optional<UserQuestionNote> note = noteRepository.findByQuestionIdAndUserId(id, userId);
    note.ifPresent(n -> dto.setUserNote(convertNoteToDTO(n)));

    return dto;
}

// 创建试题
@Transactional
public QuestionDTO createQuestion(QuestionDTO dto, Long userId) {
    Question question = new Question();
    question.setQuestionText(dto.getQuestionText());
    question.setDifficulty(dto.getDifficulty());
    question.setAnswerRequirement(dto.getAnswerRequirement());
    question.setTargetPosition(dto.getTargetPosition());
    question.setTargetLevel(dto.getTargetLevel());

    // Red Flags存储为JSON数组字符串
    if (dto.getRedFlags() != null && !dto.getRedFlags().isEmpty()) {
        question.setRedFlags(objectMapper.writeValueAsString(dto.getRedFlags()));
    }

    // 关联Focus Area
    FocusArea focusArea = focusAreaRepository.findById(dto.getFocusAreaId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "专注领域不存在"));
    question.setFocusArea(focusArea);

    // 设置创建者（用户创建）或is_official（管理员创建）
    if (userId != null) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        question.setCreatedByUser(user);
        question.setIsOfficial(false);
    } else {
        question.setIsOfficial(true);
    }

    return convertToDTO(questionRepository.save(question));
}
```

**Repository查询**:

```java
@Query("SELECT q FROM Question q " +
       "WHERE q.focusArea.id = :focusAreaId " +
       "AND (q.isOfficial = true OR q.createdByUser.id = :userId) " +
       "ORDER BY q.difficulty ASC, q.displayOrder ASC")
List<Question> findByFocusAreaIdAndVisibleToUser(
    @Param("focusAreaId") Long focusAreaId,
    @Param("userId") Long userId);
```

### 6.2 UserQuestionNoteService

**核心方法**:

```java
@Transactional
public UserQuestionNoteDTO saveOrUpdateNote(Long questionId,
                                            String noteContent,
                                            Long userId) {
    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

    // 查找是否已存在笔记
    Optional<UserQuestionNote> existingNote =
        noteRepository.findByQuestionIdAndUserId(questionId, userId);

    UserQuestionNote note;
    if (existingNote.isPresent()) {
        // 更新现有笔记
        note = existingNote.get();
        note.setNoteContent(noteContent);
    } else {
        // 创建新笔记
        note = new UserQuestionNote();
        note.setQuestion(question);
        note.setUser(user);
        note.setNoteContent(noteContent);
    }

    return convertToDTO(noteRepository.save(note));
}

// Repository方法
@Query("SELECT n FROM UserQuestionNote n " +
       "WHERE n.question.id = :questionId AND n.user.id = :userId")
Optional<UserQuestionNote> findByQuestionIdAndUserId(
    @Param("questionId") Long questionId,
    @Param("userId") Long userId);
```

## 七、关键技术实现

### 7.1 Red Flags JSON序列化

**实体类**:
```java
@Entity
@Table(name = "questions")
public class Question {
    // ... other fields

    @Column(name = "red_flags", columnDefinition = "TEXT")
    private String redFlags; // JSON数组字符串

    // Getter/Setter
    public List<String> getRedFlagsList() {
        if (redFlags == null || redFlags.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(redFlags, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public void setRedFlagsList(List<String> redFlagsList) throws JsonProcessingException {
        this.redFlags = objectMapper.writeValueAsString(redFlagsList);
    }
}
```

**DTO转换**:
```java
private QuestionDTO convertToDTO(Question question) {
    QuestionDTO dto = new QuestionDTO();
    // ... map other fields
    dto.setRedFlags(question.getRedFlagsList()); // 自动反序列化JSON
    return dto;
}
```

### 7.2 富文本编辑（Markdown）

**前端组件**:
```vue
<template>
  <div class="markdown-editor">
    <textarea v-model="content" @input="handleInput" />
    <div class="preview" v-html="renderedHtml"></div>
  </div>
</template>

<script setup>
import { marked } from 'marked'
import hljs from 'highlight.js'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const content = ref(props.modelValue || '')

const renderedHtml = computed(() => {
  return marked(content.value, {
    highlight: (code, lang) => {
      if (lang && hljs.getLanguage(lang)) {
        return hljs.highlight(code, { language: lang }).value
      }
      return hljs.highlightAuto(code).value
    }
  })
})

const handleInput = () => {
  emit('update:modelValue', content.value)
}
</script>
```

### 7.3 权限控制

**后端Service层**:
```java
public void deleteQuestion(Long id, Long userId, boolean isAdmin) {
    Question question = questionRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

    // 权限检查：管理员可删除所有，用户只能删除自己的
    if (!isAdmin && !question.getCreatedByUser().getId().equals(userId)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此试题");
    }

    questionRepository.delete(question);
}
```

**Controller层**:
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteQuestion(@PathVariable Long id,
                                            @RequestHeader("Authorization") String authHeader) {
    String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    boolean isAdmin = authService.isAdminByToken(authHeader.replace("Bearer ", ""));
    questionService.deleteQuestion(id, user.getId(), isAdmin);

    return ResponseEntity.noContent().build();
}
```

### 7.4 性能优化

**数据库索引**:
```sql
-- 组合索引用于常见查询
CREATE INDEX idx_focus_official_user ON questions(focus_area_id, is_official, created_by_user_id);

-- 单列索引用于筛选
CREATE INDEX idx_difficulty ON questions(difficulty);
CREATE INDEX idx_target_position ON questions(target_position);
CREATE INDEX idx_target_level ON questions(target_level);
```

**前端缓存**:
```javascript
const questionCache = ref({}) // { focusAreaId: [questions] }

const loadQuestions = async (focusAreaId) => {
  if (questionCache.value[focusAreaId]) {
    return questionCache.value[focusAreaId]
  }

  const response = await questionApi.getQuestionsByFocusArea(focusAreaId)
  questionCache.value[focusAreaId] = response.data
  return response.data
}
```

## 八、安全性设计

### 8.1 数据隔离

- 用户只能看到公共试题 + 自己的私有试题
- 用户只能删除自己创建的试题
- 用户笔记只有创建者本人可见
- 管理员可以看到和管理所有试题

### 8.2 输入验证

**后端验证**:
```java
@PostMapping
public ResponseEntity<QuestionDTO> createQuestion(@Valid @RequestBody QuestionDTO dto,
                                                    @RequestHeader("Authorization") String authHeader) {
    // 验证Focus Area存在
    if (!focusAreaRepository.existsById(dto.getFocusAreaId())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "专注领域不存在");
    }

    // 验证难度值
    if (!Arrays.asList("EASY", "MEDIUM", "HARD").contains(dto.getDifficulty())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "无效的难度值");
    }

    // ...
}
```

**前端验证**:
```vue
<script setup>
const validateForm = () => {
  if (!form.questionText.trim()) {
    alert('问题描述不能为空')
    return false
  }
  if (!form.difficulty) {
    alert('请选择难度级别')
    return false
  }
  return true
}

const handleSubmit = () => {
  if (!validateForm()) return
  emit('save', form)
}
</script>
```

## 九、测试策略

### 9.1 后端单元测试

```java
@Test
void testGetQuestionsByFocusAreaForUser() {
    // given
    Long focusAreaId = 1L;
    Long userId = 1L;

    // when
    List<QuestionDTO> questions = questionService.getQuestionsByFocusAreaId(focusAreaId, userId);

    // then
    assertFalse(questions.isEmpty());
    // 应该只看到公共试题 + 自己的私有试题
    assertTrue(questions.stream()
        .allMatch(q -> q.isOfficial() || q.getCreatedByUserId().equals(userId)));
}

@Test
void testSaveOrUpdateNote_Create() {
    // given
    Long questionId = 1L;
    Long userId = 1L;
    String noteContent = "我的笔记";

    // when
    UserQuestionNoteDTO note = noteService.saveOrUpdateNote(questionId, noteContent, userId);

    // then
    assertNotNull(note.getId());
    assertEquals(noteContent, note.getNoteContent());
}

@Test
void testSaveOrUpdateNote_Update() {
    // given
    Long questionId = 1L;
    Long userId = 1L;
    noteService.saveOrUpdateNote(questionId, "原始笔记", userId);

    // when
    UserQuestionNoteDTO updated = noteService.saveOrUpdateNote(questionId, "更新后的笔记", userId);

    // then
    assertEquals("更新后的笔记", updated.getNoteContent());
}
```

### 9.2 前端组件测试

```javascript
describe('QuestionCard', () => {
  it('should display difficulty badge', () => {
    const wrapper = mount(QuestionCard, {
      props: {
        question: {
          id: 1,
          questionText: 'Test question',
          difficulty: 'MEDIUM'
        }
      }
    })
    expect(wrapper.find('.difficulty-badge').text()).toBe('MEDIUM')
  })

  it('should emit click event', async () => {
    const wrapper = mount(QuestionCard, {
      props: { question: { id: 1, questionText: 'Test' } }
    })
    await wrapper.trigger('click')
    expect(wrapper.emitted('click')).toBeTruthy()
  })
})

describe('UserNoteEditor', () => {
  it('should save note with UPSERT logic', async () => {
    const wrapper = mount(UserNoteEditor, {
      props: {
        questionId: 1,
        initialNote: null
      }
    })
    await wrapper.find('textarea').setValue('My note')
    await wrapper.find('button[type="submit"]').trigger('click')
    expect(wrapper.emitted('save')).toBeTruthy()
  })
})
```

## 十、部署配置

### 10.1 数据库迁移

**Flyway迁移文件**:
```
backend/src/main/resources/db/migration/
  V5__create_questions_tables.sql
```

**V5__create_questions_tables.sql**:
```sql
-- questions表
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    focus_area_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    answer_requirement TEXT,
    target_position VARCHAR(100),
    target_level VARCHAR(50),
    red_flags TEXT,
    is_official TINYINT(1) DEFAULT 0,
    created_by_user_id BIGINT,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_focus_area_id ON questions(focus_area_id);
CREATE INDEX idx_is_official ON questions(is_official);
CREATE INDEX idx_created_by_user_id ON questions(created_by_user_id);
CREATE INDEX idx_difficulty ON questions(difficulty);
CREATE INDEX idx_target_position ON questions(target_position);
CREATE INDEX idx_target_level ON questions(target_level);
CREATE INDEX idx_display_order ON questions(display_order);

-- user_question_notes表
CREATE TABLE user_question_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    note_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_question_user (question_id, user_id)
);

-- 索引
CREATE INDEX idx_question_id ON user_question_notes(question_id);
CREATE INDEX idx_user_id ON user_question_notes(user_id);
```

### 10.2 环境变量

`backend/.env`: 无需新增环境变量，使用已有配置

## 十一、已知限制和未来优化

### 11.1 已知限制

1. **试题搜索**: Phase 3暂不实现全文搜索（Phase 5考虑）
2. **试题版本历史**: 暂不支持修改历史回溯
3. **批量导入**: 暂不支持CSV/JSON批量导入（Phase 4考虑）
4. **试题标签**: 暂不支持跨Focus Area的标签系统
5. **协作功能**: 暂不支持试题评论/讨论

### 11.2 未来优化方向

1. **Phase 4 - 核心技能模块**:
   - 编程题扩展（LeetCode链接、代码、复杂度）
   - 系统设计题扩展（架构图、容量估算）
   - Behavior题扩展（STAR格式示例）

2. **Phase 5 - 求职模块**:
   - 公司特定试题
   - 面试准备清单
   - 练习进度跟踪

3. **其他功能**:
   - 试题收藏功能
   - AI生成试题
   - 资源评分和评论
   - 智能推荐

---

**文档版本**: v1.0
**最后更新**: 2025-12-21
**维护人**: Austin Xu
