# Phase 3 - 试题库基础功能详细需求

> **版本**: v0.1 (草稿)
> **日期**: 2025-12-21
> **状态**: 待Review

## 一、需求概述

### 1.1 核心目标

构建基于Focus Area的试题库系统，支持管理员导入公共试题、用户添加个人试题，为后续的求职准备和核心技能模块提供基础数据支撑。

### 1.2 范围界定

**本Phase包含**:
- ✅ 基础试题CRUD（问题、难度、答案、Red Flags）
- ✅ 试题可见性控制（公共/私有）
- ✅ 按Focus Area组织试题
- ✅ 针对不同职位/Level的答案要点

**本Phase不包含**:
- ❌ 编程题的LeetCode链接、代码、复杂度（Phase 4 - 核心技能模块）
- ❌ 系统设计题的扩展字段（Phase 4）
- ❌ Behavior题的扩展字段（Phase 4）
- ❌ 公司特定试题和回答（Phase 5 - 求职模块）

## 二、功能需求

### 2.1 试题管理（Admin）

#### 功能描述
管理员可以创建、编辑、删除公共试题，这些试题对所有用户可见。

#### 用户故事
- 作为管理员，我希望能够按Focus Area浏览和添加试题，方便组织题库结构
- 作为管理员，我希望能够批量导入试题（未来功能），提高录入效率

#### 操作流程
1. 导航：Admin > 试题管理（新建页面）
2. 页面布局（类似技能管理页面）：
   - **左侧面板**：职业路径Tab → 技能列表 → Focus Area列表
   - **右侧面板**：选中Focus Area后显示该区域的所有试题列表
3. 点击"添加试题"按钮，弹出试题编辑窗口
4. 填写试题表单：
   - 所属Focus Area（必选，从下拉列表选择）
   - 问题描述（必填，富文本支持代码块）
   - 难度级别（必选：Easy/Medium/Hard）
   - 答案要求（富文本）- 这是给面试者的参考，说明如何回答这个问题
   - 针对职位（可选，单选：软件工程师/职业经理/等）
   - 针对级别（可选，单选：Junior/Mid/Senior/Staff）
   - Red Flags（可选，多条）
5. 保存后试题自动标记为`is_official = true`
6. 可以在试题列表中点击"编辑"或"删除"按钮，通过弹窗进行修改

#### 验收标准
- [ ] 管理员可以在试题管理页面通过Focus Area筛选试题
- [ ] 添加/编辑试题时可以选择Focus Area
- [ ] 试题支持富文本编辑（Markdown格式）
- [ ] 难度级别为必选项
- [ ] 可以添加多条Red Flags
- [ ] 答案要求为单一文本字段（1:1关系）

### 2.2 个人试题管理（User）

#### 功能描述
用户可以添加自己的私有试题，这些试题只有创建者本人可见。

#### 用户故事
- 作为用户，我希望能够添加自己收集的面试题，建立个人题库
- 作为用户，我希望我的私有试题不被其他用户看到，保护个人资料

#### 操作流程
1. 导航：学习 > 职业路径 > 试题库（新建页面）
2. 页面布局：
   - **顶部**：职业路径Tab（软件工程师、职业经理等）
   - **左侧面板**：技能-Focus Area树形结构
   - **右侧面板**：试题列表 + 试题详细页（点击试题后展开）
3. 选择Focus Area后，右侧显示该区域的试题列表（公共试题 + 我的私有试题）
4. 点击"添加我的试题"按钮，填写试题表单（字段同Admin）
5. 保存后试题自动标记为`is_official = false, created_by_user_id = 当前用户`
6. 在试题详细页，可以对任何试题（公共或私有）添加/编辑用户笔记
7. 只能编辑/删除自己创建的私有试题

#### 验收标准
- [ ] 用户只能在自己关联的职业路径下浏览和添加试题
- [ ] 用户添加的试题自动标记为私有
- [ ] 用户只能编辑/删除自己创建的试题
- [ ] 用户可以对任何试题（公共或私有）添加笔记

### 2.3 用户笔记管理（User）

#### 功能描述
用户可以对任何试题（公共或私有）添加个人笔记，包括自己的答案、思路、补充说明、心得等。笔记只有创建者本人可见。

#### 用户故事
- 作为用户，我希望能够在试题下记录自己的答案和思路，方便复习
- 作为用户，我希望能够对公共试题添加个人注释，补充自己的理解
- 作为用户，我希望我的笔记只有自己可见，保护隐私

#### 操作流程
1. 在试题详情页（QuestionDetail.vue）
2. 查看"我的笔记"区域
3. 如果已有笔记，显示笔记内容 + [编辑]按钮
4. 如果无笔记，显示[添加笔记]按钮
5. 点击后弹出笔记编辑器（支持Markdown）
6. 保存后笔记关联到当前试题和用户

#### 笔记内容
- 我的答案/思路
- 补充说明
- 个人心得
- 练习记录
- 任何想记录的内容（自由格式）

#### 验收标准
- [ ] 用户可以对任何试题添加笔记（公共或私有试题）
- [ ] 笔记只有创建者本人可见
- [ ] 一个用户对一个试题只能有一条笔记（可编辑更新）
- [ ] 支持Markdown格式
- [ ] 删除试题时级联删除关联笔记

### 2.4 试题搜索（未来功能）
_暂不实现_

## 三、数据模型设计

### 3.1 核心表设计

#### questions 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| focus_area_id | BIGINT | FK, NOT NULL, INDEX | 所属Focus Area |
| question_text | TEXT | NOT NULL | 问题描述（支持Markdown） |
| difficulty | ENUM | NOT NULL | 难度: EASY, MEDIUM, HARD |
| answer_requirement | TEXT | | 答案要求（Markdown）- 给面试者的参考 |
| target_position | VARCHAR(100) | | 针对职位（如"软件工程师"） |
| target_level | VARCHAR(50) | | 针对级别（如"Senior"） |
| red_flags | TEXT | | Red Flags列表（JSON数组格式） |
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

**说明**:
- 答案要求 (answer_requirement): 与试题1:1关系，说明如何回答这个问题
- Red Flags: 存储为JSON数组，如 `["不要提及具体公司名称", "避免过于简单的回答"]`
- 针对职位/级别: 可选字段，为空表示适用于所有职位/级别

#### user_question_notes 表 (NEW - Phase 3)

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

### 3.2 枚举定义

**Difficulty (难度)**:
```sql
ENUM('EASY', 'MEDIUM', 'HARD')
```

**Position Types (职位类型，VARCHAR字段参考值)**:
- 软件工程师 (Software Engineer)
- 职业经理 (Engineering Manager)
- 产品经理 (Product Manager)
- 等（可扩展）

**Level Types (级别类型，参考值)**:
- Junior (初级)
- Mid (中级)
- Senior (高级)
- Staff (资深)
- Principal (首席)

### 3.3 数据关系图

```
focus_areas (Phase 2)
  ↓ one-to-many
questions (包含answer_requirement和red_flags字段)
  ↓ one-to-many
user_question_notes (NEW - Phase 3)

users (Phase 1)
  ├─ one-to-many → questions (created_by_user_id)
  └─ one-to-many → user_question_notes
```

### 3.4 可见性规则

**查询逻辑** (类似Learning Resource):

```sql
-- 用户视角
SELECT * FROM questions
WHERE focus_area_id = ?
  AND (is_official = true OR created_by_user_id = ?)
ORDER BY difficulty ASC, display_order ASC, created_at DESC

-- 管理员视角（可看到所有试题）
SELECT * FROM questions
WHERE focus_area_id = ?
ORDER BY difficulty ASC, display_order ASC, created_at DESC
```

### 3.5 级联删除规则

```
删除 Focus Area
  └─ CASCADE → questions
       └─ CASCADE → user_question_notes

删除 Question
  └─ CASCADE → user_question_notes

删除 User
  ├─ CASCADE → questions (created_by_user_id)
  │    └─ CASCADE → user_question_notes
  └─ CASCADE → user_question_notes (用户创建的笔记)
```

## 四、API设计

### 4.1 用户端API

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

### 4.2 管理员API

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

## 五、前端UI设计

### 5.1 路由设计

#### 用户端路由

| 路径 | 组件 | 说明 |
|------|------|------|
| `/my-questions` | MyQuestionBank.vue | 我的题库（单页面三栏布局） |

#### 管理员路由

| 路径 | 组件 | 说明 |
|------|------|------|
| `/admin/questions` | QuestionManagement.vue | 试题管理（两栏布局） |

### 5.2 核心页面设计

#### MyQuestionBank.vue (用户题库 - 单页面三栏布局)

**完整页面布局**:
```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│ 我的题库                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│ 职业路径Tab: [软件工程师] [职业经理] [产品经理]                                      │
├────────────────┬──────────────────────────┬─────────────────────────────────────────┤
│ 左侧面板 (25%) │ 中间面板 (35%)            │ 右侧面板 (40%)                           │
│                │                          │                                         │
│ 技能-Focus     │ 试题列表                  │ 试题详情 + 我的笔记                      │
│ Area树形结构   │                          │                                         │
│                │                          │                                         │
│ ☑ 💻 编程       │ 筛选: [全部][Easy][Med]  │ ┌─────────────────────────────────┐   │
│   • 动态规划   │ [+ 添加我的试题]          │ │ 问题 [MEDIUM] [公共试题]         │   │
│   • 贪心算法   │                          │ │                                 │   │
│   • 图算法     │ ┌───────────────────────┐│ │ 解释什么是动态规划，并给出       │   │
│                │ │[MEDIUM] 解释什么是     ││ │ 一个经典例子。                   │   │
│ ☐ 🎯 系统设计  │ │动态规划... [公共]      ││ │                                 │   │
│   • CAP理论    │ │软件工程师(Senior)      ││ │ (Markdown渲染，代码高亮)        │   │
│   • 缓存策略   │ │创建: 2025-12-20        ││ │                                 │   │
│                │ └───────────────────────┘│ ├─────────────────────────────────┤   │
│ ☐ 💼 Behavior  │                          │ │ 答案要求                         │   │
│   • STAR       │ ┌───────────────────────┐│ │ 1. 定义动态规划的核心概念        │   │
│   • Leadership │ │[EASY] LeetCode 70:    ││ │ 2. 说明子问题重叠和最优子结构    │   │
│                │ │爬楼梯 [我的] [编辑]    ││ │ 3. 给出斐波那契数列的例子        │   │
│ (23道题)       │ │软件工程师(Mid)         ││ │                                 │   │
│                │ │创建: 2025-12-21        ││ ├─────────────────────────────────┤   │
│                │ └───────────────────────┘│ │ ⚠️ Red Flags                     │   │
│                │                          │ │ • 无法解释子问题重叠             │   │
│                │ (更多试题...)            │ │ • 无法解释最优子结构             │   │
│                │                          │ │                                 │   │
│                │                          │ ├─────────────────────────────────┤   │
│                │                          │ │ 📝 我的笔记       [编辑笔记]    │   │
│                │                          │ │                                 │   │
│                │                          │ │ 我的答案思路：                   │   │
│                │                          │ │ 1. 使用动态规划解决斐波那契问题  │   │
│                │                          │ │ 2. 状态转移方程: f(n)=f(n-1)+.. │   │
│                │                          │ │                                 │   │
│                │                          │ │ (Markdown渲染)                  │   │
│                │                          │ │                                 │   │
│                │                          │ │ 最后编辑: 2025-12-21 11:00      │   │
│                │                          │ │                          [删除] │   │
│                │                          │ └─────────────────────────────────┘   │
└────────────────┴──────────────────────────┴─────────────────────────────────────────┘
```

**交互说明**:
1. **左侧面板** - 技能Focus Area树
   - 可折叠/展开技能节点
   - 点击Focus Area → 中间面板加载对应试题列表
   - 显示当前选中职业路径下的所有技能

2. **中间面板** - 试题列表
   - 显示选中Focus Area的试题（公共 + 我的私有）
   - 难度筛选按钮
   - [+ 添加我的试题] 按钮 → 弹出编辑窗口
   - 点击试题卡片 → 右侧面板显示详情

3. **右侧面板** - 试题详情 + 我的笔记
   - 显示选中试题的完整内容
   - 答案要求、Red Flags
   - 我的笔记区域（可编辑）
   - 如果是我的私有试题，显示[编辑][删除]按钮

**优点**:
- ✅ 无页面跳转，所有操作在一个页面完成
- ✅ 类似邮件客户端的三栏布局，用户熟悉
- ✅ 左侧导航，中间列表，右侧详情，符合信息架构

#### QuestionEditModal.vue (试题编辑弹窗)

**表单字段**:
1. **所属Focus Area** (必选，下拉选择)
2. **问题描述** (必填，Textarea + Markdown预览)
3. **难度级别** (必选，Radio: Easy/Medium/Hard)
4. **答案要求** (可选，Textarea + Markdown预览) - 说明如何回答这个问题
5. **针对职位** (可选，单选下拉: 软件工程师/职业经理/等)
6. **针对级别** (可选，单选下拉: Junior/Mid/Senior/Staff)
7. **Red Flags** (可选，可添加多条):
   - Red Flag描述 (Input)
   - [+ 添加Red Flag] [删除]

**操作按钮**: [取消] [保存]

#### QuestionManagement.vue (管理员题库管理)

**两栏布局** (类似SkillManagement.vue):

**左侧面板** (384px):
```
┌─────────────────┐
│ 技能列表         │
│ ───────────────│
│ ☑ 全部技能      │
│ ☐ 💻 编程        │
│ ☐ 🎯 系统设计   │
│ ☐ 💼 Behavior   │
│                 │
│ Focus Area列表   │
│ (选中技能后显示) │
│ • 动态规划 (23) │
│ • 贪心算法 (15) │
└─────────────────┘
```

**右侧面板** (flex-1):
```
┌─────────────────────────────────────────────┐
│ 动态规划 - 试题列表              [+ 添加试题]│
├─────────────────────────────────────────────┤
│ ┌─────────────────────────────────────────┐ │
│ │ [MEDIUM] 解释什么是动态规划...    [编辑]│ │
│ │ 公共试题 | 软件工程师(Senior)     [删除]│ │
│ │ 创建者: Admin                           │ │
│ └─────────────────────────────────────────┘ │
│                                             │
│ ┌─────────────────────────────────────────┐ │
│ │ [EASY] LeetCode 70: 爬楼梯        [编辑]│ │
│ │ 私有试题 | 软件工程师(Mid)        [删除]│ │
│ │ 创建者: user123                         │ │
│ └─────────────────────────────────────────┘ │
│                                             │
│ (更多试题...)                               │
└─────────────────────────────────────────────┘
```

显示选中Focus Area的所有试题（公共 + 所有用户的私有试题）
- 试题卡片显示创建者信息
- 点击[编辑]弹出QuestionEditModal
- 点击[删除]确认删除

### 5.3 组件复用

**可复用组件**:
- `QuestionCard.vue` - 试题卡片（中间面板列表展示）
- `QuestionEditModal.vue` - 试题编辑弹窗（创建/编辑试题）
- `RedFlagList.vue` - Red Flag列表编辑组件（支持添加/删除多条）
- `DifficultyBadge.vue` - 难度标签（Easy/Medium/Hard，不同颜色）
- `UserNoteEditor.vue` - 用户笔记编辑器（Markdown支持，UPSERT逻辑）

## 六、技术实现要点

### 6.1 富文本编辑

**Markdown编辑器**:
- 前端使用轻量级Markdown编辑器（如`marked` + `highlight.js`）
- 支持代码块语法高亮
- 实时预览

### 6.2 权限控制

**后端Service层**:
```java
public List<QuestionDTO> getQuestionsByFocusAreaId(Long focusAreaId, Long userId) {
    return questionRepository.findByFocusAreaIdAndVisibleToUser(focusAreaId, userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

@Query("SELECT q FROM Question q " +
       "WHERE q.focusArea.id = :focusAreaId " +
       "AND (q.isOfficial = true OR q.createdByUser.id = :userId) " +
       "ORDER BY q.difficulty ASC, q.displayOrder ASC")
List<Question> findByFocusAreaIdAndVisibleToUser(
    @Param("focusAreaId") Long focusAreaId,
    @Param("userId") Long userId);
```

### 6.3 试题创建

**创建试题（简化版 - 无级联表）**:
```java
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

### 6.4 用户笔记UPSERT逻辑

**创建或更新笔记**:
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

### 6.5 性能优化

**分页查询**:
- 试题列表支持分页（默认20条/页）
- 答案要求和Red Flags存储在questions表，无需额外关联查询

**索引优化**:
- `focus_area_id` + `is_official` + `created_by_user_id` 组合索引
- `difficulty` 索引用于筛选
- `target_position` 和 `target_level` 索引用于职位/级别筛选

## 七、验收标准

### 7.1 功能验收

**基本功能**:
- [ ] 管理员可以创建公共试题
- [ ] 用户可以创建私有试题
- [ ] 用户只能看到公共试题 + 自己的私有试题
- [ ] 用户只能编辑/删除自己创建的试题
- [ ] 管理员可以编辑/删除所有试题

**用户笔记功能**:
- [ ] 用户可以对任何试题（公共或私有）添加笔记
- [ ] 用户笔记只有创建者本人可见
- [ ] 一个用户对一个试题只能有一条笔记
- [ ] 用户可以编辑/更新自己的笔记
- [ ] 用户可以删除自己的笔记
- [ ] 笔记支持Markdown格式

**数据完整性**:
- [ ] 试题必须关联到Focus Area
- [ ] 删除Focus Area时级联删除试题
- [ ] 删除试题时级联删除关联的笔记
- [ ] 删除用户时级联删除其创建的试题和笔记
- [ ] 一个用户对一个试题只能有一条笔记（唯一约束）

**UI体验**:
- [ ] 试题列表按难度筛选
- [ ] 试题详情支持Markdown渲染
- [ ] 笔记编辑支持Markdown预览
- [ ] 代码块语法高亮
- [ ] 响应式布局（移动端友好）

### 7.2 性能验收

- [ ] 试题列表加载时间 < 1秒
- [ ] 支持分页查询，单页最多20条
- [ ] 数据库查询使用索引

### 7.3 安全验收

- [ ] 用户无法访问其他用户的私有试题（API级别校验）
- [ ] 用户无法修改/删除其他用户的试题（API级别校验）
- [ ] 管理员权限校验

## 八、未来扩展规划

### 8.1 Phase 4 - 核心技能模块扩展

**编程题扩展** (question_code_details表):
- LeetCode链接
- 我的代码（多语言）
- 时间复杂度
- 空间复杂度
- 核心要点

**系统设计题扩展** (question_design_details表):
- 架构图（图片上传）
- 容量估算
- 关键技术选型
- Trade-offs分析

**Behavior题扩展** (question_behavior_details表):
- STAR格式示例
- 针对不同Leadership Principles的答案

### 8.2 Phase 5 - 求职模块

**公司特定试题** (company_questions表):
- 关联公司ID
- 公司特定的回答要点
- 其他人的回答（社区分享）

**面试准备清单**:
- 按公司/职位生成推荐题库
- 练习进度跟踪

### 8.3 其他功能

- [ ] 试题标签系统（跨Focus Area搜索）
- [ ] 试题收藏功能
- [ ] 试题练习记录（做题次数、正确率）
- [ ] AI生成试题（基于Focus Area自动生成）
- [ ] 批量导入/导出（CSV/JSON格式）

## 九、待讨论问题

### 9.1 数据模型相关

**问题1**: 答案内容存储方式
- **方案A**: 所有答案存在同一个`question_answers`表，通过`target_position`和`target_level`区分
- **方案B**: 分拆为多个表（`standard_answers`, `key_points`）
- **建议**: 方案A（简单，扩展性好）

**问题2**: Red Flags是否需要分级？
- 例如：Critical Red Flag vs Warning Red Flag
- **建议**: Phase 3先不分级，保持简单

**问题3**: 难度级别是否需要更细粒度？
- 当前：Easy/Medium/Hard
- 是否需要：LC Easy/LC Medium/LC Hard + 设计题难度 + Behavior难度
- **建议**: Phase 3统一用Easy/Medium/Hard，Phase 4针对不同技能类型可以有不同的难度体系

### 9.2 UI/UX相关

**问题1**: 试题列表默认展开还是折叠？
- **方案A**: 默认折叠，点击展开查看详情
- **方案B**: 卡片显示问题前100字，点击进入详情页
- **建议**: 方案B（参考GitHub Issues列表）

**问题2**: 是否支持试题排序？
- 按难度、按创建时间、按更新时间、手动拖拽排序
- **建议**: Phase 3先支持按难度+创建时间，手动排序留到Phase 4

**问题3**: Markdown编辑器选型？
- 轻量级：`marked` + `textarea`
- 重量级：`TinyMCE`, `Quill`
- **建议**: 先用`marked`（轻量，满足基本需求）

### 9.3 功能范围相关

**问题1**: 是否需要试题版本历史？
- 记录每次修改历史，可以回滚
- **建议**: Phase 3不实现，后续根据需求评估

**问题2**: 是否需要试题评论/讨论功能？
- 用户可以在试题下留言讨论
- **建议**: Phase 3不实现，留到社区功能

---

**文档版本**: v0.1 (草稿)
**下一步**: 等待Review和反馈，完善后生成设计文档
