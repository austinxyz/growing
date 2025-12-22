# Phase 3 - 试题库基础功能详细需求

> **版本**: v1.0 (正式版)
> **日期**: 2025-12-21
> **状态**: ✅ 已完成

## 一、需求概述

### 1.1 核心目标

构建基于Focus Area的试题库系统，支持管理员管理公共试题、用户添加个人试题，为后续的求职准备和核心技能模块提供基础数据支撑。

### 1.2 范围界定

**本Phase包含**:
- ✅ 基础试题CRUD（问题文本、难度、答案要求、Red Flags）
- ✅ 试题可见性控制（公共/私有）
- ✅ 按Focus Area组织试题
- ✅ 针对不同职位/级别的答案要点
- ✅ 用户笔记系统（支持对任何试题添加个人笔记）
- ✅ 试题数据导入（通过Python脚本从Markdown导入）

**本Phase不包含**:
- ❌ 编程题的LeetCode链接、代码实现、复杂度分析（Phase 4 - 核心技能模块）
- ❌ 系统设计题的架构图、组件设计（Phase 4）
- ❌ Behavior题的STAR框架结构化（Phase 4）
- ❌ 公司特定试题和面经（Phase 5 - 求职模块）
- ❌ 试题练习记录、正确率统计（Phase 6 - 学习进度追踪）

---

## 二、功能需求

### 2.1 试题管理（管理员）

#### 功能描述
管理员可以创建、编辑、删除公共试题，这些试题对所有用户可见。

#### 用户故事
```
作为管理员，我希望能够按Focus Area浏览和添加试题，方便组织题库结构
作为管理员，我希望能够编辑任何试题（包括用户创建的），维护题库质量
作为管理员，我希望能够批量导入试题，提高录入效率
```

#### 操作流程

**页面入口**: 设置 → 内容管理 → 试题管理

**页面布局（两栏布局）**:
- **左侧面板（25%）**：
  - 职业路径选择（Tab形式）
  - 技能列表
  - 专注领域列表（点击技能展开）

- **右侧面板（75%）**：
  - 试题列表（选中专注领域后显示）
  - 添加试题按钮
  - 试题卡片（显示问题预览、难度、创建者）

**添加试题流程**:
1. 选择职业路径 → 技能 → 专注领域
2. 点击"添加试题"按钮，弹出编辑窗口
3. 填写试题表单：
   - **所属专注领域**（自动填充当前选中的Focus Area，不可编辑）
   - **问题描述**（必填，Markdown格式，支持代码块）
   - **难度级别**（必选：Easy/Medium/Hard，Radio按钮）
   - **答案要求**（可选，Markdown格式）- 说明如何回答这个问题的参考答案框架
   - **针对职位**（可选，下拉选择：软件工程师/职业经理/基础架构工程师）
   - **针对级别**（可选，下拉选择：Junior/Mid/Senior/Staff/Principal）
   - **Red Flags**（可选，多行输入，每行一条）
4. 点击"保存"，试题自动标记为 `is_official = true, created_by_user_id = NULL`
5. 成功后刷新试题列表

**编辑/删除试题**:
- 在试题卡片上点击"编辑"按钮，弹出编辑窗口
- 在试题卡片上点击"删除"按钮，确认后删除
- 管理员可以编辑/删除任何试题（包括用户创建的）

#### 验收标准
- [x] 管理员可以在试题管理页面通过Focus Area筛选试题
- [x] 添加试题时Focus Area自动设置为当前选中的区域
- [x] 试题支持Markdown编辑（问题描述、答案要求）
- [x] 难度级别为必选项（EASY/MEDIUM/HARD）
- [x] 可以添加多条Red Flags
- [x] 答案要求为可选字段
- [x] 管理员可以编辑/删除任何试题

---

### 2.2 个人试题管理（用户）

#### 功能描述
用户可以添加自己的私有试题，这些试题只有创建者本人可见。

#### 用户故事
```
作为用户，我希望能够添加自己收集的面试题，建立个人题库
作为用户，我希望我的私有试题不被其他用户看到，保护个人资料
作为用户，我希望能够看到公共试题和我的私有试题，统一管理
```

#### 操作流程

**页面入口**: 学习 → 学习模块 → 我的试题库

**页面布局（三栏布局）**:
- **顶部**：职业路径Tab（软件工程师、职业经理、基础架构工程师等）
- **左侧面板（25%）**：技能-专注领域树形结构
- **中间面板（35%）**：
  - 难度筛选器（全部/Easy/Medium/Hard）
  - "添加我的试题"按钮
  - 试题列表（卡片形式）
- **右侧面板（40%）**：
  - 试题详情（问题、答案要求、Red Flags）
  - 我的笔记区域

**添加我的试题流程**:
1. 选择职业路径 → 展开技能 → 点击专注领域
2. 点击"添加我的试题"按钮
3. 填写试题表单（字段同管理员，但专注领域自动设置）
4. 保存后试题自动标记为 `is_official = false, created_by_user_id = 当前用户ID`
5. 成功后刷新试题列表

**查看试题详情**:
1. 在试题列表中点击试题卡片
2. 右侧面板显示完整的试题详情：
   - 问题描述（Markdown渲染）
   - 难度标签（Easy绿色/Medium黄色/Hard红色）
   - 公共试题/我的试题标签
   - 答案要求（蓝色背景高亮）
   - Red Flags列表（红色警告图标）
   - 我的笔记区域

**编辑/删除我的试题**:
- 只能编辑/删除自己创建的私有试题
- 公共试题不显示编辑/删除按钮

#### 验收标准
- [x] 用户可以在我的试题库页面浏览公共试题和自己的私有试题
- [x] 用户添加的试题自动标记为私有（`is_official = false`）
- [x] 用户只能编辑/删除自己创建的试题
- [x] 试题列表支持按难度筛选
- [x] 试题详情支持Markdown渲染

---

### 2.3 用户笔记管理（用户）

#### 功能描述
用户可以对任何试题（公共或私有）添加个人笔记，包括自己的答案、思路、补充说明、心得等。笔记只有创建者本人可见。

#### 用户故事
```
作为用户，我希望能够在试题下记录自己的答案和思路，方便复习
作为用户，我希望能够对公共试题添加个人注释，补充自己的理解
作为用户，我希望我的笔记只有自己可见，保护隐私
```

#### 操作流程

**添加/编辑笔记**:
1. 在试题详情页，点击"添加笔记"或"编辑笔记"按钮
2. 弹出笔记编辑器（Markdown支持）
3. 输入笔记内容
4. 点击"保存"按钮
5. 笔记内容立即显示在试题详情页的笔记区域

**删除笔记**:
1. 在试题详情页的笔记区域，点击"删除笔记"按钮
2. 确认后删除笔记
3. 笔记区域恢复为空状态

#### 数据模型
- **唯一约束**: (question_id, user_id) - 一个用户对一个试题只能有一条笔记
- **UPSERT语义**: 如果笔记已存在则更新，否则创建新笔记
- **级联删除**:
  - 删除试题 → 级联删除该试题的所有用户笔记
  - 删除用户 → 级联删除该用户的所有笔记

#### 验收标准
- [x] 用户可以对任何试题（公共或私有）添加笔记
- [x] 每个用户对每个试题只能有一条笔记（UPSERT逻辑）
- [x] 笔记支持Markdown编辑和渲染
- [x] 笔记只有创建者本人可见
- [x] 删除试题时自动级联删除相关笔记

---

## 三、数据模型

### 3.1 questions 表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY | 主键 |
| focus_area_id | BIGINT | NOT NULL, FK | 所属Focus Area（外键关联focus_areas.id） |
| question_text | TEXT | NOT NULL | 问题描述（支持Markdown） |
| difficulty | ENUM | NOT NULL | 难度级别（EASY/MEDIUM/HARD） |
| answer_requirement | TEXT | NULL | 答案要求（Markdown格式） |
| target_position | VARCHAR(100) | NULL | 针对职位（如"软件工程师"） |
| target_level | VARCHAR(50) | NULL | 针对级别（如"Senior"） |
| red_flags | TEXT | NULL | Red Flags列表（JSON数组格式） |
| is_official | TINYINT(1) | DEFAULT 0 | 是否公共试题（1=公共，0=私有） |
| created_by_user_id | BIGINT | NULL, FK | 创建者ID（管理员创建则为NULL） |
| display_order | INT | DEFAULT 0 | 显示顺序 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | ON UPDATE | 更新时间 |

**索引**:
- `idx_focus_area_id` - 按Focus Area查询
- `idx_is_official` - 按可见性查询
- `idx_created_by_user_id` - 按创建者查询
- `idx_difficulty` - 按难度筛选
- `idx_target_position` - 按职位筛选
- `idx_target_level` - 按级别筛选
- `idx_display_order` - 按顺序排序

**外键约束**:
- `focus_area_id` → `focus_areas(id)` ON DELETE CASCADE
- `created_by_user_id` → `users(id)` ON DELETE CASCADE

---

### 3.2 user_question_notes 表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY | 主键 |
| question_id | BIGINT | NOT NULL, FK | 所属问题ID |
| user_id | BIGINT | NOT NULL, FK | 创建者ID |
| note_content | TEXT | NOT NULL | 笔记内容（Markdown格式） |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | ON UPDATE | 更新时间 |

**唯一约束**:
- `uk_question_user` (question_id, user_id) - 一个用户对一个试题只能有一条笔记

**索引**:
- `idx_question_id` - 按试题查询
- `idx_user_id` - 按用户查询

**外键约束**:
- `question_id` → `questions(id)` ON DELETE CASCADE
- `user_id` → `users(id)` ON DELETE CASCADE

---

## 四、业务规则

### 4.1 试题可见性规则

**用户查询试题时的SQL逻辑**:
```sql
SELECT * FROM questions
WHERE focus_area_id = ?
  AND (is_official = 1 OR created_by_user_id = ?)
ORDER BY display_order ASC, created_at ASC
```

- 公共试题（`is_official = 1`）对所有用户可见
- 私有试题（`is_official = 0`）只对创建者可见
- 管理员查询时无需 `created_by_user_id` 过滤条件

### 4.2 权限控制规则

**管理员权限**:
- ✅ 查看所有试题（公共 + 所有用户的私有试题）
- ✅ 创建公共试题
- ✅ 编辑任何试题
- ✅ 删除任何试题

**普通用户权限**:
- ✅ 查看公共试题 + 自己的私有试题
- ✅ 创建私有试题
- ✅ 编辑自己创建的私有试题
- ✅ 删除自己创建的私有试题
- ❌ 编辑/删除公共试题
- ❌ 查看/编辑其他用户的私有试题

**笔记权限**:
- ✅ 用户可以对任何试题（公共或私有）添加笔记
- ✅ 笔记只有创建者本人可见
- ✅ 用户只能编辑/删除自己的笔记

### 4.3 级联删除规则

- 删除 Focus Area → CASCADE 删除该区域的所有试题 → CASCADE 删除相关笔记
- 删除 User → CASCADE 删除该用户创建的试题 + 该用户的所有笔记
- 删除 Question → CASCADE 删除该试题的所有用户笔记

### 4.4 数据验证规则

**必填字段**:
- `focus_area_id` - 必须关联到有效的Focus Area
- `question_text` - 不能为空
- `difficulty` - 必须为 EASY/MEDIUM/HARD 之一

**可选字段**:
- `answer_requirement` - 可以为空
- `target_position` - 可以为空
- `target_level` - 可以为空
- `red_flags` - 可以为空JSON数组

**JSON字段格式**:
```json
{
  "red_flags": [
    "没有考虑边界情况",
    "没有分析时间复杂度",
    "代码缺少注释"
  ]
}
```

---

## 五、API设计概要

### 5.1 管理员API

**端点**: `/api/admin/questions`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/questions?focusAreaId={id}` | 获取指定Focus Area的所有试题 |
| GET | `/api/admin/questions/{id}` | 获取试题详情 |
| POST | `/api/admin/questions` | 创建公共试题 |
| PUT | `/api/admin/questions/{id}` | 更新试题 |
| DELETE | `/api/admin/questions/{id}` | 删除试题 |

### 5.2 用户API

**端点**: `/api/questions`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/questions/focus-areas/{id}` | 获取指定Focus Area的试题（公共+我的） |
| GET | `/api/questions/{id}` | 获取试题详情（含用户笔记） |
| POST | `/api/questions` | 创建私有试题 |
| PUT | `/api/questions/{id}` | 更新自己的私有试题 |
| DELETE | `/api/questions/{id}` | 删除自己的私有试题 |

### 5.3 用户笔记API

**端点**: `/api/questions/{questionId}/note`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/questions/{questionId}/note` | 获取用户对该试题的笔记 |
| POST | `/api/questions/{questionId}/note` | 添加/更新笔记（UPSERT） |
| DELETE | `/api/questions/{questionId}/note` | 删除笔记 |

---

## 六、UI/UX设计要点

### 6.1 管理员页面（试题管理）

**布局**: 两栏布局
- **左侧（25%）**: 职业路径 → 技能 → 专注领域（树形导航）
- **右侧（75%）**: 试题列表 + 添加按钮

**试题卡片显示**:
- 问题预览（前100字符 + "..."）
- 难度标签（带颜色：绿/黄/红）
- 创建者信息（公共试题 / 用户名）
- 操作按钮（编辑/删除）

**编辑弹窗**:
- 标题："添加试题" / "编辑试题"
- 表单字段垂直排列
- 专注领域只读显示（灰色背景）
- Markdown编辑器（问题描述、答案要求）
- Red Flags多行输入（每行一条）

### 6.2 用户页面（我的试题库）

**布局**: 三栏布局
- **左侧（25%）**: 技能-专注领域树（可折叠）
- **中间（35%）**: 试题列表 + 筛选器
- **右侧（40%）**: 试题详情 + 我的笔记

**试题列表筛选器**:
- 难度筛选（全部/Easy/Medium/Hard）- Pill按钮
- "添加我的试题"按钮（禁用状态：未选择Focus Area）

**试题详情显示**:
- 问题描述（Markdown渲染）
- 答案要求（蓝色背景）
- Red Flags（红色图标 + 列表）
- 我的笔记区域（灰色背景 + 编辑/删除按钮）

**笔记编辑器**:
- Markdown文本框
- 实时预览（可选）
- 保存/取消按钮

---

## 七、测试场景

### 7.1 管理员测试场景

| 场景 | 操作 | 预期结果 |
|------|------|----------|
| 添加公共试题 | 选择Focus Area → 点击添加 → 填写表单 → 保存 | 试题创建成功，`is_official = true` |
| 编辑公共试题 | 点击编辑 → 修改内容 → 保存 | 试题更新成功 |
| 删除公共试题 | 点击删除 → 确认 | 试题删除成功，相关笔记也被删除 |
| 编辑用户试题 | 在试题列表中编辑用户创建的试题 | 允许编辑并保存成功 |
| 按Focus Area筛选 | 切换不同的Focus Area | 试题列表正确更新 |

### 7.2 用户测试场景

| 场景 | 操作 | 预期结果 |
|------|------|----------|
| 查看公共试题 | 选择Focus Area | 显示所有公共试题 |
| 添加私有试题 | 点击"添加我的试题" → 填写 → 保存 | 试题创建成功，`is_official = false` |
| 编辑私有试题 | 编辑自己创建的试题 | 允许编辑并保存成功 |
| 删除私有试题 | 删除自己创建的试题 | 试题删除成功 |
| 编辑公共试题 | 尝试编辑公共试题 | 不显示编辑按钮 |
| 查看他人试题 | 尝试查看其他用户的私有试题 | 不在列表中显示 |
| 添加笔记 | 对任意试题点击"添加笔记" | 笔记创建成功 |
| 更新笔记 | 编辑已有笔记 | 笔记更新成功（UPSERT） |
| 删除笔记 | 删除笔记 | 笔记删除成功 |
| 按难度筛选 | 点击不同难度标签 | 试题列表正确过滤 |

---

## 八、数据导入

### 8.1 初始数据导入

**导入工具**: Python脚本 (`import/import_questions.py`)

**数据源**: `import/Infrastructure Engineering Interview Q&A Guide.md`

**导入内容**:
- ✅ 8道精选面试题
- ✅ 覆盖5个不同Focus Area
- ✅ 包含完整的答案要求和Red Flags
- ✅ 自动映射到正确的Focus Area

**导入结果**:
- 3道 Behavioral 题 → Customer Centricity
- 2道 Kubernetes 题 → Containerization & Kubernetes
- 1道 Linux 题 → Linux Kernel & OS Internals
- 1道 System Design 题 → Systems Design & Architecture
- 1道 Coding 题 → Programming Proficiency

详见: `import/IMPORT_SUMMARY.md`

---

## 九、实施状态

### 9.1 已完成功能

- [x] 数据库Schema创建（V5 migration）
- [x] 后端API实现（QuestionController + AdminQuestionController）
- [x] 前端管理员页面（QuestionManagement.vue）
- [x] 前端用户页面（MyQuestionBank.vue）
- [x] 试题编辑组件（QuestionEditModal.vue）
- [x] 用户笔记组件（UserNoteEditor.vue）
- [x] 试题卡片组件（QuestionCard.vue）
- [x] 难度标签组件（DifficultyBadge.vue）
- [x] Red Flags组件（RedFlagList.vue）
- [x] 数据导入脚本（import_questions.py）
- [x] 8道初始试题导入

### 9.2 Bug修复

- [x] Focus Area列表不显示问题（后端未返回focusAreas）
- [x] 试题列表不显示问题（前端response.data解析错误）
- [x] 添加试题时Focus Area重复选择问题（自动填充当前选中区域）
- [x] 中文显示优化（"Focus Area" → "专注领域"）

### 9.3 待优化功能（Phase 4）

- [ ] Markdown编辑器增强（实时预览、语法高亮）
- [ ] 试题标签系统（添加自定义标签）
- [ ] 试题搜索功能（全文搜索）
- [ ] 批量导入UI界面
- [ ] 试题导出功能（导出为PDF/Markdown）
- [ ] 试题练习模式（计时、正确率统计）

---

## 十、总结

Phase 3成功构建了基于Focus Area的试题库基础系统，实现了：

1. **完整的CRUD功能** - 管理员和用户都可以管理试题
2. **灵活的可见性控制** - 公共试题vs私有试题
3. **用户笔记系统** - 支持对任何试题添加个人笔记
4. **数据导入能力** - 通过Python脚本快速导入试题
5. **良好的用户体验** - 三栏布局、难度筛选、Markdown支持

为后续的核心技能模块（Phase 4）和求职模块（Phase 5）奠定了坚实的数据基础。
