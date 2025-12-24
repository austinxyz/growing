# Phase 4 设计文档 - 算法与数据结构学习模块

> **架构版本**: v1.0 (基于实际实现)
> **状态**: ✅ 已实施完成
> **创建时间**: 2025-12-24
> **需求文档**: [Phase4-详细需求.md](../requirement/Phase4-详细需求.md)
> **关键决策**: [Prompt.txt - Phase 4 Decisions](../prompt/Prompt.txt)

---

## 1. 总体架构

### 1.1 设计理念

Phase 4 实现**灵活的、Skill级别可定制**的学习路径系统：
- **不同Skill可以定义不同的学习阶段**（算法 = 原理→代码→题目）
- **统一的内容管理框架**（文章、视频、代码、题目等）
- **三段式学习路径**（算法与数据结构专用）：基础原理 → 实现代码 → 实战题目

### 1.2 核心特性

✅ **Skill级别自定义学习阶段** - learning_stages表存储每个Skill的学习路径
✅ **统一内容管理** - learning_contents表管理所有类型的学习内容
✅ **题目管理独立** - programming_question_details表存储编程题专属字段
✅ **4个大分类** - 数据结构、搜索、动规、其他
✅ **52个Focus Area** - 算法学习主题
✅ **225篇文章** - labuladong算法笔记完整导入
✅ **10个算法模版** - 独立板块，快速参考（不关联Focus Area）
✅ **用户笔记** - 复用Phase 3的user_question_notes表，扩展core_strategy字段
✅ **学习总结** - 新增功能，272道编程题核心思路汇总

### 1.3 实际架构分层

```
┌──────────────────────────────────────────────────────────────┐
│                      前端 (Vue 3)                             │
├──────────────────────────────────────────────────────────────┤
│  管理端                        │  用户端                      │
│  - 分阶段内容管理（3个Tab）     │  - 大分类浏览（4个Tab）       │
│    * 基础原理、实现代码、       │  - Focus Area选择            │
│      实战题目                  │  - 单页整合显示所有学习阶段   │
│  - 题目管理独立Tab（第4个）     │  - 题目详情Modal             │
│    * CRUD编程题                │  - 算法模版查阅              │
│    * 通用字段+编程题扩展字段    │  - 个人笔记管理              │
│  - 算法模版管理                │    * 核心思路+个人笔记        │
│                                │  - 学习总结页面 (NEW!)       │
│                                │    * 272题核心思路快速浏览    │
└──────────────────────────────────────────────────────────────┘
                           ↕ RESTful API
┌──────────────────────────────────────────────────────────────┐
│                   后端 (Spring Boot)                          │
├──────────────────────────────────────────────────────────────┤
│  Controller层                                                 │
│  - AdminLearningContentController (管理员API)                │
│  - LearningContentController (用户API)                       │
│  - QuestionController (题目API - 扩展learning review)        │
├──────────────────────────────────────────────────────────────┤
│  Service层                                                    │
│  - LearningStageService (学习阶段查询)                       │
│  - LearningContentService (内容CRUD)                         │
│  - QuestionService (题目CRUD + learning review批量查询)      │
│  - UserQuestionNoteService (笔记 - 复用Phase 3)              │
│  - ProgrammingQuestionDetailsService (编程题详情)            │
├──────────────────────────────────────────────────────────────┤
│  Repository层 (Spring Data JPA)                               │
│  - LearningStageRepository                                    │
│  - LearningContentRepository                                  │
│  - QuestionRepository (扩展)                                  │
│  - UserQuestionNoteRepository (复用)                          │
│  - ProgrammingQuestionDetailsRepository (新增)               │
└──────────────────────────────────────────────────────────────┘
                           ↕
┌──────────────────────────────────────────────────────────────┐
│                   数据库 (MySQL 8.0)                          │
│  新增：                                                       │
│  - learning_stages (学习阶段定义) - 3条记录                  │
│  - learning_contents (学习内容统一表) - 235条记录            │
│  - programming_question_details (编程题专属字段) - 272条    │
│  修改：                                                       │
│  - questions (无结构变更，仅数据增加272道编程题)             │
│  - user_question_notes (增加core_strategy字段) - 272条      │
│  - major_categories (无变更，保留4个)                        │
└──────────────────────────────────────────────────────────────┘
```

### 1.4 与现有架构的集成

**复用Phase 1-3的概念**:
- ✅ **Skill** - "算法与数据结构"（skills表，已存在）
- ✅ **Focus Area** - 52个学习主题（focus_areas表，已存在）
- ✅ **Question** - LeetCode题目（questions表，增加272道编程题）
- ✅ **UserQuestionNote** - 用户笔记（user_question_notes表，扩展core_strategy字段）
- ✅ **MajorCategory** - 4个大分类（major_categories表，无变更）

**新增概念**:
- 🆕 **LearningStage** - Skill级别的学习阶段定义（3个阶段）
- 🆕 **LearningContent** - 统一的学习内容表（225篇文章 + 10个模版）
- 🆕 **ProgrammingQuestionDetails** - 编程题专属字段（LeetCode链接等，272条）

**架构优势**:
- 🎯 **灵活扩展** - 未来可为其他Skill定义不同的学习阶段
- 🎯 **统一管理** - 所有学习内容通过同一张表管理
- 🎯 **类型扩展** - content_data JSON字段支持不同类型内容的特殊数据

---

## 2. 数据模型设计

### 2.1 实际ER图

```
                    ┌───────────────────┐
                    │      skills       │
                    ├───────────────────┤
                    │ id (PK)           │
                    │ name              │
                    └───────────────────┘
                             │
                             │ 1:N
                             ↓
                    ┌─────────────────────┐
                    │  learning_stages    │  (学习阶段定义)
                    ├─────────────────────┤
                    │ id (PK)             │
                    │ skill_id (FK)       │
                    │ stage_name          │  (基础原理、实现代码、实战题目)
                    │ stage_type          │  (theory, implementation, practice)
                    │ description         │
                    │ sort_order          │
                    └─────────────────────┘
                             │
                             │ 1:N
                             ↓
                    ┌──────────────────────────┐
                    │  learning_contents       │  (学习内容统一表)
                    ├──────────────────────────┤
     ┌──────────────│ id (PK)                  │
     │              │ focus_area_id (FK, NULL) │  ← NULL for templates
     │              │ stage_id (FK)            │
     │              │ content_type             │  (article/video/code/template)
     │              │ title                    │
     │              │ description              │
     │              │ url                      │
     │              │ author                   │
     │              │ content_data (JSON)      │  ← 扩展数据
     │              │ sort_order               │
     │              │ visibility               │  (public/private)
     │              │ created_by (FK)          │
     │              │ created_at               │
     │              │ updated_at               │
     │              └──────────────────────────┘
     │                       ↑
     │                       │ N:1
     ↓                       │
┌──────────────┐    ┌───────────────────┐
│ focus_areas  │    │  major_categories │  (4个大分类)
├──────────────┤    ├───────────────────┤
│ id (PK)      │    │ id (PK)           │
│ skill_id     │    │ name              │
│ name         │    │ sort_order        │
│ category_id  │───→└───────────────────┘
└──────────────┘  N:1
     │
     │ 1:N
     ↓
┌──────────────────────────┐
│    questions             │
├──────────────────────────┤
│ id (PK)                  │
│ focus_area_id (FK)       │
│ title                    │
│ question_description     │
│ difficulty               │
│ question_type            │  (programming)
└──────────────────────────┘
     │                 │
     │ 1:N             │ 1:1
     ↓                 ↓
┌──────────────────────────────┐  ┌──────────────────────────────────┐
│ user_question_notes          │  │ programming_question_details     │
├──────────────────────────────┤  ├──────────────────────────────────┤
│ id (PK)                      │  │ id (PK)                          │
│ user_id (FK)                 │  │ question_id (FK, UNIQUE)         │
│ question_id (FK)             │  │ leetcode_number (INT)            │
│ note_content                 │  │ leetcode_url                     │
│ core_strategy (新增)         │  │ labuladong_url                   │
│ created_at                   │  │ hellointerview_url               │
│ updated_at                   │  └──────────────────────────────────┘
└──────────────────────────────┘
```

### 2.2 表结构详细设计

#### 表1: learning_stages (学习阶段定义表)

**用途**: 为每个Skill定义自定义的学习阶段序列

```sql
CREATE TABLE learning_stages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  skill_id BIGINT NOT NULL COMMENT '所属技能',
  stage_name VARCHAR(50) NOT NULL COMMENT '阶段名称，如"基础原理"',
  stage_type VARCHAR(50) NOT NULL COMMENT '阶段类型标识，如"theory"',
  description TEXT COMMENT '阶段说明',
  sort_order INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
  UNIQUE KEY uk_skill_stage (skill_id, stage_name),
  INDEX idx_skill_order (skill_id, sort_order)
) COMMENT='学习阶段定义表（Skill级别配置）';
```

**实际数据（算法与数据结构）**:
```sql
INSERT INTO learning_stages (skill_id, stage_name, stage_type, description, sort_order) VALUES
(1, '基础原理', 'theory', '数据结构的基本概念、特点、适用场景', 1),
(1, '实现代码', 'implementation', '数据结构的代码实现、API设计、算法技巧', 2),
(1, '实战题目', 'practice', 'LeetCode题目练习、算法应用', 3);
```

---

#### 表2: learning_contents (学习内容统一表)

**用途**: 统一管理所有类型的学习内容（文章、视频、代码、模版等）

**数据统计**:
- 总计: 235条
- 文章: ~225条（labuladong算法笔记）
- 模版: 10条（算法模版）

```sql
CREATE TABLE learning_contents (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  focus_area_id BIGINT COMMENT 'Focus Area ID（算法模版为NULL）',
  stage_id BIGINT NOT NULL COMMENT '所属学习阶段',
  content_type ENUM('article', 'video', 'code_example', 'template', 'case_study') NOT NULL,

  -- 通用字段
  title VARCHAR(500) NOT NULL,
  description TEXT,
  url VARCHAR(1000) COMMENT '外部资源链接',
  author VARCHAR(100),

  -- 扩展内容（JSON存储，根据content_type不同）
  content_data JSON COMMENT '扩展数据，如代码、模版、案例详情等',

  -- 元信息
  sort_order INT DEFAULT 0,
  visibility ENUM('public', 'private') DEFAULT 'public',
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
  FOREIGN KEY (stage_id) REFERENCES learning_stages(id) ON DELETE CASCADE,
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_focus_stage (focus_area_id, stage_id, sort_order),
  INDEX idx_stage_type (stage_id, content_type)
) COMMENT='学习内容统一表';
```

**content_type说明**:
- `article`: 文章/博客（如labuladong文章）- 主要类型
- `video`: 视频教程（预留）
- `code_example`: 代码示例（预留）
- `template`: 算法模版（focus_area_id = NULL，10个模版）
- `case_study`: 案例分析（预留，系统设计用）

**算法模版的存储**:
- `focus_area_id = NULL`（不关联Focus Area）
- `content_type = 'template'`
- 前端单独展示"算法模版"板块

---

#### 表3: questions (题目表 - 保持通用)

**说明**:
- questions表继续通过`focus_area_id`关联到Focus Area
- questions表保持通用字段，适用于所有Skill（编程、系统设计、运维等）
- 编程题的专属字段存储在独立表 `programming_question_details`

**数据统计**:
- 总计: 379题
- behavioral: 107题（Phase 3）
- programming: 272题（Phase 4新增）

---

#### 表4: programming_question_details (编程题专属字段表)

**说明**:
- 存储编程题的公共元数据（LeetCode链接等）
- **核心思路 (core_strategy) 不存储在此表**，而是存储在 user_question_notes 表中

**数据统计**: 272条（对应272道编程题）

```sql
CREATE TABLE programming_question_details (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  question_id BIGINT NOT NULL UNIQUE COMMENT '关联的题目ID',

  -- LeetCode题号（整数，用于排序和过滤）
  leetcode_number INT COMMENT 'LeetCode题号',

  -- 外部资源链接
  leetcode_url VARCHAR(500) COMMENT 'LeetCode题目链接',
  labuladong_url VARCHAR(500) COMMENT 'labuladong题解链接',
  hellointerview_url VARCHAR(500) COMMENT 'Hello Interview题解链接',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
  INDEX idx_leetcode_number (leetcode_number)
) COMMENT='编程题专属字段表（仅用于算法与数据结构Skill）';
```

**设计优势**:
- ✅ questions表保持简洁，适用于所有Skill类型
- ✅ 编程题的额外信息独立管理，避免NULL字段浪费
- ✅ 通过question_id的UNIQUE约束确保一对一关系
- ✅ CASCADE删除确保数据一致性
- ✅ 核心思路存储在user_question_notes，支持用户个性化解题方法
- ✅ leetcode_number字段支持按题号排序和过滤

---

#### 表5: user_question_notes (用户笔记表 - 扩展编程题字段)

**说明**:
- 复用 Phase 3 的 user_question_notes 表
- 为编程题扩展 `core_strategy` 字段（其他类型题目该字段为空）
- 支持用户个性化的解题思路记录

**数据统计**: 272条用户笔记（对应272道编程题）

**Migration V9 扩展**:
```sql
ALTER TABLE user_question_notes
  ADD COLUMN core_strategy TEXT COMMENT '核心思路（Markdown格式，仅编程题使用）';
```

**完整表结构** (包含Phase 3字段 + Phase 4扩展):
```sql
CREATE TABLE user_question_notes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  question_id BIGINT NOT NULL COMMENT '关联的题目ID',
  user_id BIGINT NOT NULL COMMENT '创建者ID',

  -- Phase 3 字段
  note_content TEXT COMMENT '笔记内容（Markdown格式）',

  -- Phase 4 扩展字段（编程题专用）
  core_strategy TEXT COMMENT '核心思路（Markdown格式，用户提供的解题核心思路）',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY uk_question_user (question_id, user_id),
  INDEX idx_question_id (question_id),
  INDEX idx_user_id (user_id)
) COMMENT='用户题目笔记表（Phase 3 + Phase 4扩展）';
```

**字段使用说明**:
- **note_content** - 通用笔记内容（所有题目类型都可以使用）
  - 补充说明、心得体会、注意事项等
  - Phase 3 已有字段，保持向后兼容
- **core_strategy** - 编程题核心思路（仅编程题使用）
  - 用户提供的解题核心思路
  - 不同用户对同一道题可以有不同的解法
  - 数据结构题、系统设计题等其他类型题目该字段为 NULL

**设计理由**:
- ✅ **用户个性化** - 核心思路是用户个人的解题方法，不是题目的公共属性
- ✅ **多样性** - 不同用户对同一道题可能有不同的解法和思路
- ✅ **隐私性** - 用户笔记只有创建者本人可见
- ✅ **复用性** - 复用Phase 3的表结构，避免冗余
- ✅ **向后兼容** - 不影响现有Phase 3的笔记功能

---

#### 表6: major_categories (大分类表 - 保持4个)

**说明**: Phase 4无变更，保留4个大分类

**数据**:
1. 数据结构
2. 搜索
3. 动规
4. 其他

---

### 2.3 数据关联规则

```
skills (算法与数据结构)
  ↓ 1:N
learning_stages (基础原理、实现代码、实战题目) - 3条
  ↓ 1:N
learning_contents (文章、视频、代码、模版) - 235条
  ↑ N:1 (可选)
focus_areas (数组、链表、二叉树...) - 52个
  ↑ N:1
major_categories (数据结构、搜索、动规、其他) - 4个

questions (题目表) - 379题 (107 behavioral + 272 programming)
  ↓ 1:1
programming_question_details (编程题专属字段) - 272条

  ↓ 1:N
user_question_notes (用户笔记) - 272条
```

**级联删除规则**:
- 删除Skill → 级联删除所有learning_stages → 级联删除所有learning_contents
- 删除Focus Area → 级联删除关联的learning_contents（但不影响算法模版）
- 删除LearningStage → 级联删除该阶段的所有learning_contents
- 删除Question → 级联删除programming_question_details和user_question_notes

---

## 3. API设计

### 3.1 管理端API

#### 3.1.1 学习内容管理

**GET /api/admin/learning-contents**
- 功能: 分页查询学习内容（按Focus Area和Stage过滤）
- 权限: 管理员
- 参数:
  - `focusAreaId` (optional): Focus Area ID
  - `stageId` (optional): 学习阶段ID
  - `contentType` (optional): 内容类型
  - `page`, `size`: 分页参数

**POST /api/admin/learning-contents**
- 功能: 创建学习内容
- 权限: 管理员

**PUT /api/admin/learning-contents/{id}**
- 功能: 更新学习内容
- 权限: 管理员

**DELETE /api/admin/learning-contents/{id}**
- 功能: 删除学习内容
- 权限: 管理员

#### 3.1.2 编程题管理

**GET /api/admin/questions**
- 功能: 查询题目列表（支持按Focus Area过滤）
- 权限: 管理员

**POST /api/admin/questions**
- 功能: 创建新题目（通用字段 + 编程题扩展字段）
- 权限: 管理员

**PUT /api/admin/questions/{id}**
- 功能: 更新题目
- 权限: 管理员

**DELETE /api/admin/questions/{id}**
- 功能: 删除题目（级联删除programming_question_details和user_question_notes）
- 权限: 管理员

---

### 3.2 用户端API

#### 3.2.1 学习内容浏览

**GET /api/learning-contents**
- 功能: 获取Focus Area的所有学习阶段内容
- 权限: 已登录用户
- 参数: `focusAreaId` (required)

**GET /api/learning-contents/{id}**
- 功能: 获取单个学习内容详情
- 权限: 已登录用户

#### 3.2.2 算法模版查阅

**GET /api/algorithm-templates**
- 功能: 获取所有算法模版
- 权限: 已登录用户
- 参数: `search` (optional): 搜索关键词

**GET /api/algorithm-templates/{id}**
- 功能: 获取单个算法模版详情
- 权限: 已登录用户

#### 3.2.3 学习总结（新增功能）

**GET /api/questions/learning-review**
- 功能: 获取所有编程题的核心思路汇总
- 权限: 已登录用户
- 参数:
  - `majorCategoryId` (optional): 按大分类过滤
  - `focusAreaId` (optional): 按Focus Area过滤
  - `leetcodeNumber` (optional): 按LeetCode题号过滤
- 返回: 包含272道编程题的详细信息（题目、难度、Focus Area、大分类、用户核心思路等）
- 性能: 批量查询优化，0.6秒加载272题

---

## 4. 前端设计

### 4.1 管理端页面

#### 4.1.1 学习内容管理页面 (AlgorithmContentManagement.vue)

**布局**: 两栏布局 + 4个Tab

```
┌────────────────────────────────────────────────────────────┐
│  算法与数据结构 - 内容管理                                  │
├───────────────┬────────────────────────────────────────────┤
│ 左侧面板(25%) │ 右侧面板(75%)                               │
│               │                                            │
│ [数据结构Tab] │ ┌──────────────────────────────────────┐ │
│ [搜索Tab]     │ │ 数组 - 内容管理                       │ │
│ [动规Tab]     │ ├──────────────────────────────────────┤ │
│ [其他Tab]     │ │ Tab1 │ Tab2 │ Tab3 │ [试题库]    │    │ │
│               │ │ 基础原理 实现代码 实战题目  (第4个Tab) │ │
│ Focus Area列表│ ├──────────────────────────────────────┤ │
│ ☑ 数组        │ │                                      │ │
│ □ 链表        │ │ [+ 添加内容] 或 [+ 新建题目]         │ │
│ □ 栈和队列    │ │                                      │ │
│ □ 哈希表      │ │ 内容/题目列表                        │ │
│ ...           │ │                                      │ │
└───────────────┴──┴──────────────────────────────────────┴─┘
```

**右侧面板Tab说明**:
1. **Tab 1-3: 学习阶段Tab** (基础原理、实现代码、实战题目)
   - 管理 learning_contents 表的数据
   - 添加文章、视频、代码示例等
2. **Tab 4: 试题库Tab**
   - 管理 questions + programming_question_details 表的数据
   - 显示当前Focus Area的所有题目

#### 4.1.2 算法模版管理页面 (AlgorithmTemplateManagement.vue)

**布局**: 两栏布局（左侧模版列表 + 右侧编辑区）

---

### 4.2 用户端页面

#### 4.2.1 学习内容浏览页面 (AlgorithmLearning.vue)

**实际布局**: 三栏布局 + **单页整合显示**（不分Tab）

```
┌────────────────────────────────────────────────────────────┐
│  【算法与数据结构】                                           │
│  Tab: 数据结构 | 搜索 | 动规 | 其他                          │
├─────────────┬──────────────────────────────────────────────┤
│  Focus Area │  【选中: 数组】                                │
│  列表       │  ┌────────────────────────────────────────┐  │
│             │  │ === 基础原理 ===                        │  │
│ - 数组      │  │                                        │  │
│ - 链表      │  │ 📄 数组（顺序存储）基本原理              │  │
│ - 栈队列    │  │    作者: labuladong                     │  │
│ - 哈希表    │  │    → labuladong.online                 │  │
│ - 二叉树    │  │                                        │  │
│ - 图        │  │ 📄 动态数组代码实现                     │  │
│ - 排序      │  │    作者: labuladong                     │  │
│             │  │                                        │  │
│             │  │ === 实现代码 ===                        │  │
│             │  │                                        │  │
│             │  │ 📄 双指针技巧总结                       │  │
│             │  │                                        │  │
│             │  │ === 实战题目 ===                        │  │
│             │  │                                        │  │
│             │  │ 🎯 [26] 删除有序数组中的重复项           │  │
│             │  │ 🎯 [27] 移除元素                        │  │
│             │  └────────────────────────────────────────┘  │
└─────────────┴──────────────────────────────────────────────┘
```

**关键差异**:
- ❌ **原设计**: 右侧分3个Tab（基础原理、实现代码、实战题目），切换查看不同阶段
- ✅ **实际实现**: 右侧单页整合显示，所有学习阶段内容垂直排列，一次性加载

**理由**: 单页展示更符合学习流程，用户可以连续阅读所有阶段内容，不需要反复切换Tab

---

#### 4.2.2 算法模版查阅页面 (AlgorithmTemplates.vue)

**布局**: 两栏布局（左侧列表 + 右侧Markdown渲染）

---

#### 4.2.3 学习总结页面 (LearningReview.vue) - 新增功能

**布局**: 紧凑双列布局

```
┌─────────────────────────────────────────────────────────┐
│  【学习总结 - 核心思路快速浏览】                         │
│  [过滤器: 所有分类 ▼] [所有Focus Area ▼] [题号: ___]   │
│  [重置]                                                 │
├─────────────────────────────────────────────────────────┤
│  ┌────────────────────┐  ┌────────────────────┐        │
│  │ ● [26] 删除有序数组 │  │ ● [27] 移除元素     │        │
│  │   数组 | 数据结构   │  │   数组 | 数据结构   │        │
│  │ 核心思路：双指针法  │  │ 核心思路：快慢指针  │        │
│  └────────────────────┘  └────────────────────┘        │
│  ┌────────────────────┐  ┌────────────────────┐        │
│  │ ● [283] 移动零      │  │ ● [167] 两数之和II  │        │
│  │   数组 | 数据结构   │  │   数组 | 搜索       │        │
│  │ 核心思路：双指针   │  │ 核心思路：对撞指针  │        │
│  └────────────────────┘  └────────────────────┘        │
└─────────────────────────────────────────────────────────┘
```

**功能特性**:
- ✅ 显示所有272道编程题的核心思路总结
- ✅ 按大分类过滤（数据结构、搜索、动规、其他）
- ✅ 按Focus Area过滤
- ✅ 按LeetCode题号过滤
- ✅ 颜色编码区分大分类（蓝色=数据结构、绿色=搜索、橙色=动规、紫色=其他）
- ✅ 点击题目标题，弹出QuestionDetailModal
- ✅ 极致紧凑设计（text-xs字体，最小padding，双列布局）
- ✅ 性能优化：后端批量查询（0.6秒加载272题）

**后端批量查询优化**:
```java
// QuestionService.getLearningReview()方法
// 1. 批量查询所有编程题
List<Question> allProgrammingQuestions =
    questionRepository.findAllProgrammingQuestionsBySkillId(ALGORITHM_SKILL_ID);

// 2. 批量获取用户笔记，建立Map
Map<Long, UserQuestionNote> noteMap = ...;

// 3. 批量加载编程题详情
Map<Long, ProgrammingQuestionDetailsDTO> detailsMap = ...;

// 4. 批量查询Focus Area与大分类关联
Map<Long, List<Long>> focusAreaToCategoryMap = ...;

// 性能: 0.6秒加载272题
```

---

## 5. 业务逻辑设计

### 5.1 Service层核心逻辑

#### 5.1.1 LearningContentService

```java
@Service
public class LearningContentService {

    /**
     * 获取Focus Area的所有学习阶段内容
     */
    public FocusAreaLearningDTO getContentsByFocusArea(Long focusAreaId) {
        // 1. 获取Focus Area基本信息
        FocusArea focusArea = focusAreaRepository.findById(focusAreaId)
            .orElseThrow(() -> new ResourceNotFoundException("Focus Area not found"));

        // 2. 获取该Skill的所有学习阶段
        List<LearningStage> stages = learningStageRepository
            .findBySkillIdOrderBySortOrder(focusArea.getSkill().getId());

        // 3. 为每个阶段获取内容
        List<StageContentDTO> stageContents = stages.stream()
            .map(stage -> {
                List<LearningContent> contents = learningContentRepository
                    .findByFocusAreaIdAndStageIdOrderBySortOrder(focusAreaId, stage.getId());

                return new StageContentDTO(stage, contents);
            })
            .collect(Collectors.toList());

        return new FocusAreaLearningDTO(focusArea, stageContents);
    }

    /**
     * 获取所有算法模版
     */
    public List<LearningContent> getAlgorithmTemplates(String searchKeyword) {
        if (StringUtils.hasText(searchKeyword)) {
            return learningContentRepository
                .findByContentTypeAndTitleContainingOrderBySortOrder(
                    "template", searchKeyword);
        }
        return learningContentRepository
            .findByContentTypeAndFocusAreaIdIsNullOrderBySortOrder("template");
    }
}
```

#### 5.1.2 QuestionService (扩展learning review功能)

```java
@Service
public class QuestionService {

    /**
     * 获取学习总结（批量查询优化）
     * 性能: 0.6秒加载272道编程题
     */
    public Map<String, Object> getLearningReview(Long userId) {
        final Long ALGORITHM_SKILL_ID = 1L;

        // 批量查询所有编程题
        List<Question> allProgrammingQuestions =
            questionRepository.findAllProgrammingQuestionsBySkillId(ALGORITHM_SKILL_ID);

        // 批量获取用户笔记，建立Map
        Map<Long, UserQuestionNote> noteMap = ...;

        // 批量加载编程题详情
        Map<Long, ProgrammingQuestionDetailsDTO> detailsMap = ...;

        // 批量查询Focus Area与大分类关联
        Map<Long, List<Long>> focusAreaToCategoryMap = ...;

        // 组装数据并返回
        // ...
    }
}
```

---

### 5.2 数据访问层

#### 5.2.1 LearningContentRepository

```java
@Repository
public interface LearningContentRepository extends JpaRepository<LearningContent, Long> {

    // 查询Focus Area + Stage的内容
    List<LearningContent> findByFocusAreaIdAndStageIdOrderBySortOrder(
        Long focusAreaId, Long stageId);

    // 查询算法模版
    List<LearningContent> findByContentTypeAndFocusAreaIdIsNullOrderBySortOrder(
        String contentType);

    // 搜索模版
    List<LearningContent> findByContentTypeAndTitleContainingOrderBySortOrder(
        String contentType, String keyword);

    // 按阶段查询
    List<LearningContent> findByStageIdOrderBySortOrder(Long stageId);
}
```

#### 5.2.2 QuestionRepository (扩展)

```java
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 批量查询所有编程题（learning review用）
    @Query("SELECT q FROM Question q WHERE q.focusArea.skill.id = :skillId " +
           "AND q.questionType = 'programming' ORDER BY q.displayOrder")
    List<Question> findAllProgrammingQuestionsBySkillId(@Param("skillId") Long skillId);
}
```

---

## 6. 数据迁移

### 6.1 Migration V9 & V10

**V9**: 增加leetcode_number字段
```sql
ALTER TABLE programming_question_details
ADD COLUMN leetcode_number INT COMMENT 'LeetCode题号',
ADD INDEX idx_leetcode_number (leetcode_number);
```

**V10**: 删除question_text字段（已废弃，改用question_description）
```sql
ALTER TABLE questions
DROP COLUMN question_text;
```

**初始数据导入**:
- 3个学习阶段
- 225篇labuladong文章
- 10个算法模版
- 272道编程题 + programming_question_details
- 272条用户笔记（core_strategy字段）

---

## 7. 性能优化

### 7.1 批量查询优化

**学习总结页面**:
- 问题: 272道题，如果逐个查询会很慢
- 解决: QuestionService.getLearningReview()使用批量查询
  - 一次性查询所有编程题
  - 批量加载用户笔记，建立Map
  - 批量加载编程题详情，建立Map
  - 批量查询Focus Area与大分类关联
- 性能: 0.6秒加载272题（后端查询 + 数据组装）

### 7.2 数据库索引

```sql
-- learning_contents表索引
CREATE INDEX idx_focus_stage ON learning_contents(focus_area_id, stage_id, sort_order);
CREATE INDEX idx_stage_type ON learning_contents(stage_id, content_type);

-- programming_question_details表索引
CREATE INDEX idx_leetcode_number ON programming_question_details(leetcode_number);
```

---

## 8. 安全设计

### 8.1 权限控制

**管理端API**:
- 所有 `/api/admin/learning-contents/**` 需要管理员权限
- 使用 `@PreAuthorize("hasRole('ADMIN')")` 注解

**用户端API**:
- 所有 `/api/learning-contents/**` 需要登录
- 所有 `/api/questions/learning-review` 需要登录

### 8.2 数据验证

**输入验证**:
```java
@NotNull(message = "Stage ID is required")
private Long stageId;

@NotBlank(message = "Title is required")
@Size(max = 500, message = "Title must not exceed 500 characters")
private String title;

@URL(message = "Invalid URL format")
private String url;
```

---

## 9. 实现与设计的主要差异

### 9.1 用户端学习路径页面

**设计**: 右侧分3个Tab（基础原理、实现代码、实战题目），切换查看不同阶段
**实际**: 右侧单页整合显示，所有学习阶段内容垂直排列，一次性加载

**理由**: 单页展示更符合学习流程，用户可以连续阅读所有阶段内容

### 9.2 学习总结功能（新增）

**设计**: 未包含此功能
**实际**: 新增`/learning-review`页面，提供272道编程题的核心思路汇总

**功能**:
- 双列双行紧凑布局
- 按大分类、Focus Area、LeetCode题号过滤
- 颜色编码区分大分类
- 点击题目弹出详情Modal
- 后端批量查询优化（0.6秒加载）

**理由**: 用户强烈需要快速复习所有题目核心思路的能力

### 9.3 Skill名称

**设计**: "编程与数据结构"
**实际**: "算法与数据结构"

**理由**: 更贴合实际内容，"算法"比"编程"更准确

### 9.4 路由路径

**设计**: `/admin/programming-ds`
**实际**: `/admin/algorithm-content` + `/admin/algorithm-templates` + `/algorithm-learning` + `/algorithm-templates` + `/learning-review`

**理由**: 更清晰的功能划分

---

## 10. 附录

### 10.1 术语表

| 术语 | 英文 | 说明 |
|------|------|------|
| 学习阶段 | Learning Stage | Skill级别定义的学习路径阶段 |
| 学习内容 | Learning Content | 统一管理的各类学习资源 |
| 算法模版 | Algorithm Template | 快速参考的代码模版（不关联Focus Area） |
| 内容类型 | Content Type | article/video/code_example/template/case_study |
| 可见性 | Visibility | public（所有用户）/ private（预留） |

### 10.2 参考文档

- [Phase 4 详细需求 v1.0](../requirement/Phase4-详细需求.md)
- [Labuladong 三段式结构](../import/labuladong_phase4_v2_staged.md)
- [Phase 4 Design Decisions](../prompt/Prompt.txt)
- [ARCHITECTURE.md](./ARCHITECTURE.md)

---

**文档版本**: v1.0 (基于实际实现)
**创建时间**: 2025-12-24
**状态**: ✅ 已实施完成
**更新说明**:
- 基于实际实现重写整个设计文档
- 更新Skill名称为"算法与数据结构"
- 更新用户端学习路径页面布局（单页整合显示）
- 新增学习总结功能章节
- 更新数据统计（反映实际数据量）
- 文档版本从v2.2（设计）更新为v1.0（实际实现）
