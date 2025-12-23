# Phase 4 设计文档 - 编程与数据结构学习模块

> **架构版本**: v2.2 (管理员UI改进 - 题目管理独立Tab)
> **状态**: ✅ 设计定稿 - 题目管理独立Tab + programming_question_details表
> **创建时间**: 2025-12-22
> **最后更新**: 2025-12-22
> **需求文档**: [Phase4-详细需求.md](../requirement/Phase4-详细需求.md)
> **修正说明**:
> - [Phase4-core_strategy-字段修正.md](./Phase4-core_strategy-字段修正.md)
> - [Phase4-管理员UI改进-题目管理独立Tab.md](./Phase4-管理员UI改进-题目管理独立Tab.md)
> **关键决策**: [Prompt.txt - Phase 4 Decisions](../prompt/Prompt.txt)

---

## 1. 总体架构

### 1.1 设计理念

Phase 4 实现**灵活的、Skill级别可定制**的学习路径系统：
- **不同Skill可以定义不同的学习阶段**（如：编程 = 原理→代码→题目，系统设计 = 案例→模版→权衡）
- **统一的内容管理框架**（文章、视频、代码、题目等）
- **三段式学习路径**（编程与数据结构专用）：基础原理 → 实现代码 → 实战题目

### 1.2 核心特性

✅ **Skill级别自定义学习阶段** - learning_stages表存储每个Skill的学习路径
✅ **统一内容管理** - learning_contents表管理所有类型的学习内容
✅ **题目管理独立** - programming_question_details表存储编程题专属字段
✅ **4大分类** - 数据结构、搜索、动规、其他（删除"核心刷题框架"和"基础篇"）
✅ **25个Focus Area** - 精简后的学习主题（从33个合并）
✅ **225篇文章** - labuladong算法笔记完整导入
✅ **算法模版库** - 独立板块，快速参考（不关联Focus Area）
✅ **用户笔记** - 复用Phase 3的user_question_notes表，扩展core_strategy字段

### 1.3 架构分层

```
┌──────────────────────────────────────────────────────────────┐
│                      前端 (Vue 3)                             │
├──────────────────────────────────────────────────────────────┤
│  管理端                        │  用户端                      │
│  - 分阶段内容管理（3个Tab）     │  - 大分类浏览（4个Tab）       │
│    * 基础原理、实现代码、       │  - Focus Area选择            │
│      实战题目                  │  - 学习阶段Tab切换           │
│  - 题目管理独立Tab（第4个）     │  - 内容查看（按阶段）         │
│    * CRUD编程题                │  - 题目详情Modal             │
│    * 通用字段+编程题扩展字段    │  - 算法模版查阅              │
│  - 算法模版管理                │  - 个人笔记管理              │
│                                │    * 核心思路+个人笔记        │
└──────────────────────────────────────────────────────────────┘
                           ↕ RESTful API
┌──────────────────────────────────────────────────────────────┐
│                   后端 (Spring Boot)                          │
├──────────────────────────────────────────────────────────────┤
│  Controller层                                                 │
│  - AdminLearningContentController (管理员API)                │
│  - LearningContentController (用户API)                       │
├──────────────────────────────────────────────────────────────┤
│  Service层                                                    │
│  - LearningStageService (学习阶段查询)                       │
│  - LearningContentService (内容CRUD)                         │
│  - QuestionService (题目关联 - 扩展现有)                     │
│  - UserQuestionNoteService (笔记 - 复用Phase 3)              │
├──────────────────────────────────────────────────────────────┤
│  Repository层 (Spring Data JPA)                               │
│  - LearningStageRepository                                    │
│  - LearningContentRepository                                  │
│  - QuestionRepository (扩展)                                  │
│  - UserQuestionNoteRepository (复用)                          │
└──────────────────────────────────────────────────────────────┘
                           ↕
┌──────────────────────────────────────────────────────────────┐
│                   数据库 (MySQL 8.0)                          │
│  新增：                                                       │
│  - learning_stages (学习阶段定义)                            │
│  - learning_contents (学习内容统一表)                        │
│  - programming_question_details (编程题专属字段)             │
│  修改：                                                       │
│  - questions (增加stage_id字段)                              │
│  - user_question_notes (增加core_strategy字段)               │
│  - major_categories (删除2个分类，保留4个)                   │
└──────────────────────────────────────────────────────────────┘
```

### 1.4 与现有架构的集成

**复用Phase 1-3的概念**:
- ✅ **Skill** - "编程与数据结构"（skills表，已存在）
- ✅ **Focus Area** - 25个学习主题（focus_areas表，已存在）
- ✅ **Question** - LeetCode题目（questions表，增加stage_id）
- ✅ **UserQuestionNote** - 用户笔记（user_question_notes表，完全复用）
- ✅ **MajorCategory** - 4个大分类（major_categories表，删除2个）

**新增概念**:
- 🆕 **LearningStage** - Skill级别的学习阶段定义（新表）
- 🆕 **LearningContent** - 统一的学习内容表（文章、视频、代码、模版等）
- 🆕 **ProgrammingQuestionDetails** - 编程题专属字段（LeetCode链接、算法标签等）

**架构优势**:
- 🎯 **灵活扩展** - 未来可为"系统设计"、"运维"等Skill定义不同的学习阶段
- 🎯 **统一管理** - 所有学习内容通过同一张表管理
- 🎯 **类型扩展** - content_data JSON字段支持不同类型内容的特殊数据

---

## 2. 数据模型设计

### 2.1 ER图

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
                    │ stage_name          │  (基础原理、实现代码...)
                    │ stage_type          │  (theory, implementation...)
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
│ id (PK)      │───→│ id (PK)           │
│ skill_id     │ N:1│ name              │
│ name         │    │ sort_order        │
│ category_id  │    └───────────────────┘
└──────────────┘
     │
     │ 1:N
     ↓
┌──────────────────────────┐         ┌─────────────────────┐
│    questions             │────┐    │ learning_stages     │
├──────────────────────────┤    │    ├─────────────────────┤
│ id (PK)                  │    └───→│ id (PK)             │
│ focus_area_id            │   N:1   │ stage_id (FK)       │  ← 新增字段
│ stage_id (FK)            │         └─────────────────────┘
│ title                    │
│ difficulty               │
└──────────────────────────┘
     │                 │
     │ 1:N             │ 1:1
     ↓                 ↓
┌──────────────────────────────┐  ┌──────────────────────────────────┐
│ user_question_notes          │  │ programming_question_details     │
├──────────────────────────────┤  ├──────────────────────────────────┤
│ id (PK)                      │  │ id (PK)                          │
│ user_id (FK)                 │  │ question_id (FK, UNIQUE)         │
│ question_id (FK)             │  │ leetcode_url                     │
│ note_content                 │  │ labuladong_url                   │
│ core_strategy (新增)         │  │ hellointerview_url               │
│ created_at                   │  │ is_important                     │
│ updated_at                   │  │ tags (JSON)                      │
└──────────────────────────────┘  │ complexity                       │
                                  │ similar_questions (JSON)         │
                                  └──────────────────────────────────┘
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

**初始数据（编程与数据结构）**:
```sql
INSERT INTO learning_stages (skill_id, stage_name, stage_type, description, sort_order) VALUES
(1, '基础原理', 'theory', '数据结构的基本概念、特点、适用场景', 1),
(1, '实现代码', 'implementation', '数据结构的代码实现、API设计、算法技巧', 2),
(1, '实战题目', 'practice', 'LeetCode题目练习、算法应用', 3);
```

**字段说明**:
- `stage_type`: 前端识别符（用于不同类型的渲染方式）
- `sort_order`: 学习阶段顺序（从1开始）

**未来扩展示例**（系统设计Skill）:
```sql
INSERT INTO learning_stages (skill_id, stage_name, stage_type, description, sort_order) VALUES
(2, '案例分析', 'case_study', '真实系统的架构分析、需求理解', 1),
(2, '架构模版', 'architecture_template', '通用架构模式、设计模版', 2),
(2, '权衡讨论', 'tradeoff', '不同方案的优缺点、技术选型', 3),
(2, '延伸阅读', 'extended_reading', '论文、博客、技术文档', 4);
```

---

#### 表2: learning_contents (学习内容统一表)

**用途**: 统一管理所有类型的学习内容（文章、视频、代码、模版等）

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
- `article`: 文章/博客（如labuladong文章）
- `video`: 视频教程
- `code_example`: 代码示例
- `template`: 算法模版（focus_area_id = NULL）
- `case_study`: 案例分析（系统设计用）

**content_data JSON格式示例**:

```json
// content_type = 'article'
{
  "readingTime": 15,  // 阅读时长（分钟）
  "tags": ["双指针", "数组"],
  "relatedQuestions": [26, 27, 283]  // 关联题目ID
}

// content_type = 'code_example'
{
  "language": "java",
  "code": "public class ArrayList<E> {...}",
  "complexity": {"time": "O(1)", "space": "O(n)"},
  "notes": "动态扩容策略：每次扩容为原来的1.5倍"
}

// content_type = 'template'（算法模版）
{
  "language": "java",
  "template": "public int binarySearch(int[] nums, int target) {...}",
  "notes": "注意边界条件: left <= right",
  "complexity": {"time": "O(log n)", "space": "O(1)"},
  "relatedResources": [
    {"title": "二分搜索核心模板", "url": "https://..."}
  ],
  "practiceQuestions": [704, 35, 33]
}

// content_type = 'case_study'（系统设计用）
{
  "systemName": "Instagram Feed",
  "requirements": ["支持10亿用户", "Feed刷新延迟<500ms"],
  "architectureDiagramUrl": "https://...",
  "keyComponents": [
    {"name": "Feed Service", "description": "..."}
  ],
  "tradeoffs": [
    {"decision": "Push vs Pull", "chosen": "Hybrid", "reasoning": "..."}
  ]
}
```

**visibility字段使用**:
- `public`: 所有用户可见的学习资源（默认，管理员创建）
- `private`: 预留给未来用户自定义学习资源功能（Phase 4不使用）

**算法模版的存储**:
- `focus_area_id = NULL`（不关联Focus Area）
- `content_type = 'template'`
- 前端单独展示"算法模版"板块

---

#### 表3: questions (题目表 - 保持通用)

**说明**:
- questions表继续通过`focus_area_id`关联到Focus Area，**不关联**到`learning_stages`
- questions表保持通用字段，适用于所有Skill（编程、系统设计、运维等）
- 编程题的专属字段存储在独立表 `programming_question_details`

---

#### 表4: programming_question_details (编程题专属字段表)

**说明**:
- 存储编程题的公共元数据（题目链接、难度、标签等）
- **核心思路 (core_strategy) 不存储在此表**，而是存储在 user_question_notes 表中（每个用户有自己的解题思路）

```sql
CREATE TABLE programming_question_details (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  question_id BIGINT NOT NULL UNIQUE COMMENT '关联的题目ID',

  -- 外部资源链接
  leetcode_url VARCHAR(500) COMMENT 'LeetCode题目链接',
  labuladong_url VARCHAR(500) COMMENT 'labuladong题解链接',
  hellointerview_url VARCHAR(500) COMMENT 'Hello Interview题解链接',

  -- 重要性标记
  is_important BOOLEAN DEFAULT FALSE COMMENT '是否为重要算法题（⭐标记）',

  -- 算法技巧标签
  tags JSON COMMENT '算法技巧标签，如["双指针","滑动窗口"]',

  -- 复杂度和相关题目
  complexity VARCHAR(100) COMMENT '算法复杂度，如"时间O(n), 空间O(1)"',
  similar_questions JSON COMMENT '类似题目列表，如[{"id": 15, "title": "3Sum"}]',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
  INDEX idx_important (is_important),
  INDEX idx_tags ((CAST(tags AS CHAR(255) ARRAY)))
) COMMENT='编程题专属字段表（仅用于编程与数据结构Skill）';
```

**设计优势**:
- ✅ questions表保持简洁，适用于所有Skill类型
- ✅ 编程题的额外信息独立管理，避免NULL字段浪费
- ✅ 未来如需为其他Skill（如系统设计）添加专属字段，可创建类似的独立表
- ✅ 通过question_id的UNIQUE约束确保一对一关系
- ✅ CASCADE删除确保数据一致性
- ✅ 核心思路存储在user_question_notes，支持用户个性化解题方法

**tags字段示例**:
```json
["双指针", "滑动窗口"]
```

**similar_questions字段示例**:
```json
[
  {"id": 15, "title": "3Sum"},
  {"id": 18, "title": "4Sum"}
]
```

---

#### 表5: user_question_notes (用户笔记表 - 扩展编程题字段)

**说明**:
- 复用 Phase 3 的 user_question_notes 表
- 为编程题扩展 `core_strategy` 字段（其他类型题目该字段为空）
- 支持用户个性化的解题思路记录

**Migration V7 扩展**:
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
  note_content TEXT NOT NULL COMMENT '笔记内容（Markdown格式）',

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

**前端展示示例**（编程题）:
```
┌────────────────────────────────────────┐
│ 题目详情                                │
├────────────────────────────────────────┤
│ [26] 删除有序数组中的重复项             │
│ 难度: EASY                              │
│                                        │
│ ━━━ 核心思路 (我的解法) ━━━           │
│ 使用双指针法:                          │
│ 1. 快指针遍历数组                      │
│ 2. 慢指针指向不重复元素的末尾          │
│ 3. 遇到不同元素时移动慢指针            │
│                                        │
│ ━━━ 个人笔记 ━━━                      │
│ 注意边界情况:                          │
│ - 数组为空时返回0                      │
│ - 数组长度为1时直接返回1               │
│ 复杂度: O(n) 时间, O(1) 空间           │
└────────────────────────────────────────┘
```

---

#### 表6: major_categories (大分类表 - 调整为4个)

**修改**: 删除"核心刷题框架"和"基础篇"

```sql
-- Migration V7中执行
DELETE FROM major_categories WHERE name IN ('核心刷题框架', '基础篇');

-- 保留4个大分类：
-- 1. 数据结构
-- 2. 搜索
-- 3. 动规
-- 4. 其他
```

---

### 2.3 数据关联规则

```
skills (编程与数据结构)
  ↓ 1:N
learning_stages (基础原理、实现代码、实战题目)
  ↓ 1:N
learning_contents (文章、视频、代码、模版)
  ↑ N:1 (可选)
focus_areas (数组、链表、二叉树...)
  ↑ N:1
major_categories (数据结构、搜索、动规、其他)

questions (题目表)
  ↓ N:1
learning_stages (关联到"实战题目"阶段)
```

**级联删除规则**:
- 删除Skill → 级联删除所有learning_stages → 级联删除所有learning_contents
- 删除Focus Area → 级联删除关联的learning_contents（但不影响算法模版）
- 删除LearningStage → 级联删除该阶段的所有learning_contents
- 删除Question → 不影响learning_contents（题目独立存在）

**注意**: 实际场景中很少删除大分类和Focus Area，这些规则主要用于数据清理和测试。

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
- 返回:
  ```json
  {
    "content": [
      {
        "id": 1,
        "focusAreaId": 5,
        "focusAreaName": "数组",
        "stageId": 1,
        "stageName": "基础原理",
        "contentType": "article",
        "title": "数组（顺序存储）基本原理",
        "url": "https://labuladong.online/...",
        "author": "labuladong",
        "sortOrder": 1,
        "visibility": "public"
      }
    ],
    "totalElements": 50,
    "totalPages": 5
  }
  ```

**POST /api/admin/learning-contents**
- 功能: 创建学习内容
- 权限: 管理员
- 请求体:
  ```json
  {
    "focusAreaId": 5,  // 算法模版时为null
    "stageId": 1,
    "contentType": "article",
    "title": "数组（顺序存储）基本原理",
    "description": "介绍数组的基本特性...",
    "url": "https://labuladong.online/algo/data-structure-basic/array-basic/",
    "author": "labuladong",
    "contentData": null,  // 可选，JSON格式
    "sortOrder": 1,
    "visibility": "public"
  }
  ```
- 返回: 创建的learning_content对象

**PUT /api/admin/learning-contents/{id}**
- 功能: 更新学习内容
- 权限: 管理员
- 请求体: 同创建
- 返回: 更新后的对象

**DELETE /api/admin/learning-contents/{id}**
- 功能: 删除学习内容
- 权限: 管理员
- 返回: 204 No Content

**PUT /api/admin/learning-contents/reorder**
- 功能: 批量调整排序（拖拽后）
- 权限: 管理员
- 请求体:
  ```json
  {
    "contentIds": [3, 1, 2, 5, 4],  // 新的排序
    "stageId": 1
  }
  ```
- 返回: 200 OK

---

#### 3.1.2 编程题管理 (Questions + Programming Question Details)

**GET /api/admin/questions**
- 功能: 查询题目列表（支持按Focus Area过滤）
- 权限: 管理员
- 参数:
  - `focusAreaId` (optional): Focus Area ID
  - `difficulty` (optional): EASY/MEDIUM/HARD
  - `page`, `size`: 分页参数
- 返回:
  ```json
  {
    "content": [
      {
        "id": 26,
        "title": "[26] 删除有序数组中的重复项",
        "difficulty": "EASY",
        "focusAreaId": 5,
        "focusAreaName": "数组",
        "programmingDetails": {
          "leetcodeUrl": "https://leetcode.com/problems/...",
          "isImportant": true,
          "tags": ["双指针", "数组"],
          "complexity": "时间O(n), 空间O(1)"
        }
      }
    ],
    "totalElements": 100
  }
  ```

**POST /api/admin/questions**
- 功能: 创建新题目（通用字段 + 编程题扩展字段）
- 权限: 管理员
- 请求体:
  ```json
  {
    "title": "[26] 删除有序数组中的重复项",
    "questionDescription": "给你一个有序数组...",
    "difficulty": "EASY",
    "focusAreaId": 5,
    "answerRequirement": "要求原地删除...",
    "redFlags": ["忘记处理空数组", "修改了原数组长度"],
    "programmingDetails": {
      "leetcodeUrl": "https://leetcode.com/problems/...",
      "labuladongUrl": "https://labuladong.online/...",
      "hellointerviewUrl": "https://...",
      "isImportant": true,
      "tags": ["双指针", "数组"],
      "complexity": "时间O(n), 空间O(1)",
      "similarQuestions": [
        {"id": 27, "title": "移除元素"}
      ]
    }
  }
  ```
- 返回: 创建的question对象（包含programmingDetails）

**PUT /api/admin/questions/{id}**
- 功能: 更新题目（通用字段 + 编程题扩展字段）
- 权限: 管理员
- 请求体: 同创建
- 返回: 更新后的question对象

**DELETE /api/admin/questions/{id}**
- 功能: 删除题目
- 权限: 管理员
- 说明:
  - 级联删除 programming_question_details
  - 级联删除用户笔记 (user_question_notes)
- 返回: 204 No Content

---

### 3.2 用户端API

#### 3.2.1 学习内容浏览

**GET /api/learning-contents**
- 功能: 获取Focus Area的所有学习阶段内容
- 权限: 已登录用户
- 参数:
  - `focusAreaId`: Focus Area ID (required)
- 返回:
  ```json
  {
    "focusArea": {
      "id": 5,
      "name": "数组",
      "categoryName": "数据结构"
    },
    "stages": [
      {
        "id": 1,
        "stageName": "基础原理",
        "stageType": "theory",
        "description": "数据结构的基本概念...",
        "contents": [
          {
            "id": 1,
            "contentType": "article",
            "title": "数组（顺序存储）基本原理",
            "url": "https://...",
            "author": "labuladong"
          },
          {
            "id": 2,
            "contentType": "article",
            "title": "动态数组代码实现",
            "url": "https://...",
            "author": "labuladong"
          }
        ]
      },
      {
        "id": 2,
        "stageName": "实现代码",
        "stageType": "implementation",
        "description": "数据结构的代码实现...",
        "contents": [...]
      },
      {
        "id": 3,
        "stageName": "实战题目",
        "stageType": "practice",
        "description": "LeetCode题目练习...",
        "contents": [
          {
            "id": 3,
            "contentType": "question",
            "title": "[26] 删除有序数组中的重复项",
            "difficulty": "EASY",
            "questionId": 26  // 链接到questions表
          }
        ]
      }
    ]
  }
  ```

**GET /api/learning-contents/{id}**
- 功能: 获取单个学习内容详情
- 权限: 已登录用户
- 返回:
  ```json
  {
    "id": 1,
    "focusAreaId": 5,
    "stageId": 1,
    "contentType": "article",
    "title": "数组（顺序存储）基本原理",
    "description": "...",
    "url": "https://...",
    "author": "labuladong",
    "contentData": {...},  // 扩展数据
    "createdAt": "2025-12-22T10:00:00"
  }
  ```

---

#### 3.2.2 算法模版查阅

**GET /api/algorithm-templates**
- 功能: 获取所有算法模版
- 权限: 已登录用户
- 参数:
  - `search` (optional): 搜索关键词
- 返回:
  ```json
  [
    {
      "id": 100,
      "title": "二分查找",
      "contentData": {
        "language": "java",
        "template": "public int binarySearch(...) {...}",
        "notes": "注意边界条件...",
        "complexity": {"time": "O(log n)", "space": "O(1)"},
        "relatedResources": [...],
        "practiceQuestions": [704, 35, 33]
      }
    }
  ]
  ```

**GET /api/algorithm-templates/{id}**
- 功能: 获取单个算法模版详情
- 权限: 已登录用户
- 返回: 模版详情（同上）

---

#### 3.2.3 学习阶段查询

**GET /api/learning-stages**
- 功能: 获取Skill的所有学习阶段
- 权限: 已登录用户
- 参数:
  - `skillId`: Skill ID (required)
- 返回:
  ```json
  [
    {
      "id": 1,
      "stageName": "基础原理",
      "stageType": "theory",
      "description": "数据结构的基本概念、特点、适用场景",
      "sortOrder": 1
    },
    {
      "id": 2,
      "stageName": "实现代码",
      "stageType": "implementation",
      "description": "数据结构的代码实现、API设计、算法技巧",
      "sortOrder": 2
    },
    {
      "id": 3,
      "stageName": "实战题目",
      "stageType": "practice",
      "description": "LeetCode题目练习、算法应用",
      "sortOrder": 3
    }
  ]
  ```

---

## 4. 前端设计

### 4.1 管理端页面

#### 4.1.1 学习内容管理页面 (AlgorithmContentManagement.vue)

**布局**: 两栏布局 + 4个Tab
```
┌────────────────────────────────────────────────────────────┐
│  编程与数据结构 - 内容管理                                  │
├───────────────┬────────────────────────────────────────────┤
│ 左侧面板(25%) │ 右侧面板(75%)                               │
│               │                                            │
│ [数据结构Tab] │ ┌──────────────────────────────────────┐ │
│ [搜索Tab]     │ │ 数组 - 内容管理                       │ │
│ [动规Tab]     │ ├──────────────────────────────────────┤ │
│ [其他Tab]     │ │ Tab1 │ Tab2 │ Tab3 │ [题目管理] │    │ │
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
   - 添加文章、视频、代码示例、题解链接等
   - 点击"添加内容"弹出 LearningContentModal

2. **Tab 4: 题目管理Tab**
   - 管理 questions + programming_question_details 表的数据
   - 显示当前Focus Area的所有题目
   - 点击"新建题目"弹出 QuestionEditModal (包含通用字段 + 编程题扩展字段)

**Tab切换逻辑**:
- Tab 1-3切换时，加载对应阶段的learning_contents数据
- 切换到Tab 4时，加载当前Focus Area的questions数据
- Tab状态独立，不互相干扰

**功能点**:
1. **左侧树**: 4个大分类 → 25个Focus Area
2. **右侧Tab 1-3**: 3个学习阶段（基础原理、实现代码、实战题目）的学习内容管理
3. **右侧Tab 4**: 题目CRUD（通用字段 + programming_question_details扩展字段）
4. **添加内容**: Modal弹窗，选择类型（article/video/code_example）
5. **新建题目**: Modal弹窗，填写通用字段 + 编程题专属字段
6. **编辑/删除**: 内联操作
7. **拖拽排序**: Vue Draggable实现

---

#### 4.1.2 算法模版管理页面 (AlgorithmTemplateManagement.vue)

**布局**: 两栏布局
```
┌─────────────────────────────────────────────────────────┐
│  【算法模版管理】                                         │
├─────────────┬───────────────────────────────────────────┤
│  模版列表   │  【选中: 二分查找】                         │
│             │  ┌─────────────────────────────────────┐  │
│ [+ 新建]    │  │ 标题: 二分查找                      │  │
│             │  │ 语言: Java                          │  │
│ - 二分查找  │  │ 模版代码:                           │  │
│ - 快速排序  │  │ ```java                             │  │
│ - 双指针    │  │ public int binarySearch(...) {...}  │  │
│ - 滑动窗口  │  │ ```                                 │  │
│             │  │                                     │  │
│ [搜索...]   │  │ 注意事项:                           │  │
│             │  │ - 数组必须有序                       │  │
│             │  │ - 注意边界条件                       │  │
│             │  │                                     │  │
│             │  │ 练习题目: [704, 35, 33]             │  │
│             │  │                                     │  │
│             │  │ [保存] [删除]                        │  │
│             │  └─────────────────────────────────────┘  │
└─────────────┴───────────────────────────────────────────┘
```

---

### 4.2 用户端页面

#### 4.2.1 学习内容浏览页面 (AlgorithmLearning.vue)

**布局**: 三栏布局 + Tab
```
┌────────────────────────────────────────────────────────────┐
│  【编程与数据结构】                                           │
│  Tab: 数据结构 | 搜索 | 动规 | 其他                          │
├─────────────┬──────────────────────────────────────────────┤
│  Focus Area │  【选中: 数组】                                │
│  列表       │  ┌────────────────────────────────────────┐  │
│             │  │ Tab: 基础原理 | 实现代码 | 实战题目     │  │
│ - 数组      │  ├────────────────────────────────────────┤  │
│ - 链表      │  │                                        │  │
│ - 栈队列    │  │ 📄 数组（顺序存储）基本原理              │  │
│ - 哈希表    │  │    作者: labuladong                     │  │
│ - 二叉树    │  │    → labuladong.online                 │  │
│ - 图        │  │                                        │  │
│ - 排序      │  │ 📄 动态数组代码实现                     │  │
│ - 数据结构  │  │    作者: labuladong                     │  │
│   设计      │  │    → labuladong.online                 │  │
│             │  │                                        │  │
│             │  │ 📄 环形数组技巧及实现                   │  │
│             │  │    作者: labuladong                     │  │
│             │  │    → labuladong.online                 │  │
│             │  │                                        │  │
│             │  └────────────────────────────────────────┘  │
└─────────────┴──────────────────────────────────────────────┘
```

**功能点**:
1. **顶部大分类Tab**: 4个Tab（数据结构、搜索、动规、其他）
2. **左侧Focus Area**: 当前大分类的所有Focus Area
3. **右侧学习阶段Tab**: 3个Tab（基础原理、实现代码、实战题目）
4. **内容列表**:
   - 文章/视频：显示标题、作者，点击跳转外部链接
   - 题目：点击打开详情Modal（包含笔记功能）
5. **Tab默认激活**: 第一个阶段（基础原理）

---

#### 4.2.2 算法模版查阅页面 (AlgorithmTemplates.vue)

**布局**: 两栏布局
```
┌─────────────────────────────────────────────────────────┐
│  【算法模版】                                             │
├─────────────┬───────────────────────────────────────────┤
│  模版列表   │  【选中: 二分查找】                         │
│             │  ┌─────────────────────────────────────┐  │
│ [搜索...]   │  │ # 二分查找                          │  │
│             │  │                                     │  │
│ - 二分查找  │  │ ## 模版代码                         │  │
│ - 快速排序  │  │ ```java                             │  │
│ - 双指针    │  │ public int binarySearch(...) {...}  │  │
│ - 滑动窗口  │  │ ```                                 │  │
│ - 回溯算法  │  │                                     │  │
│ - BFS       │  │ ## 注意事项                         │  │
│ - 动态规划  │  │ - 数组必须有序                       │  │
│             │  │ - 注意边界条件 (left <= right)       │  │
│             │  │                                     │  │
│             │  │ ## 复杂度                           │  │
│             │  │ - 时间: O(log n)                    │  │
│             │  │ - 空间: O(1)                        │  │
│             │  │                                     │  │
│             │  │ ## 练习题目                         │  │
│             │  │ - [704] 二分查找                    │  │
│             │  │ - [35] 搜索插入位置                  │  │
│             │  └─────────────────────────────────────┘  │
└─────────────┴───────────────────────────────────────────┘
```

**功能点**:
1. **左侧列表**: 所有算法模版，支持搜索
2. **右侧详情**: Markdown渲染，代码语法高亮
3. **练习题目**: 点击跳转到题目详情Modal

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

                // 如果是"实战题目"阶段，还需要获取关联的题目
                if ("practice".equals(stage.getStageType())) {
                    List<Question> questions = questionRepository
                        .findByFocusAreaIdAndStageId(focusAreaId, stage.getId());
                    // 合并contents和questions
                }

                return new StageContentDTO(stage, contents);
            })
            .collect(Collectors.toList());

        return new FocusAreaLearningDTO(focusArea, stageContents);
    }

    /**
     * 创建学习内容
     */
    @Transactional
    public LearningContent createContent(CreateLearningContentDTO dto) {
        // 1. 验证阶段存在
        LearningStage stage = learningStageRepository.findById(dto.getStageId())
            .orElseThrow(() -> new ResourceNotFoundException("Stage not found"));

        // 2. 如果是算法模版，focus_area_id必须为null
        if ("template".equals(dto.getContentType()) && dto.getFocusAreaId() != null) {
            throw new BusinessException("Template content should not link to focus area");
        }

        // 3. 如果不是模版，必须关联Focus Area
        if (!"template".equals(dto.getContentType()) && dto.getFocusAreaId() == null) {
            throw new BusinessException("Non-template content must link to a focus area");
        }

        // 4. 创建内容
        LearningContent content = new LearningContent();
        BeanUtils.copyProperties(dto, content);
        content.setCreatedBy(getCurrentUser().getId());

        return learningContentRepository.save(content);
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

---

#### 5.1.2 QuestionService (扩展)

```java
@Service
public class QuestionService {

    /**
     * 将题目关联到学习阶段
     */
    @Transactional
    public Question linkQuestionToStage(Long questionId, Long stageId) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // 验证阶段存在且类型为"practice"
        LearningStage stage = learningStageRepository.findById(stageId)
            .orElseThrow(() -> new ResourceNotFoundException("Stage not found"));

        if (!"practice".equals(stage.getStageType())) {
            throw new BusinessException("Only practice stage can link questions");
        }

        question.setStageId(stageId);
        return questionRepository.save(question);
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

    // 统计
    long countByFocusAreaIdAndStageId(Long focusAreaId, Long stageId);
}
```

---

#### 5.2.2 LearningStageRepository

```java
@Repository
public interface LearningStageRepository extends JpaRepository<LearningStage, Long> {

    // 查询Skill的所有阶段（按sort_order排序）
    List<LearningStage> findBySkillIdOrderBySortOrder(Long skillId);

    // 查询特定类型的阶段
    Optional<LearningStage> findBySkillIdAndStageType(Long skillId, String stageType);
}
```

---

## 6. 数据迁移 (Migration V7)

### 6.1 迁移脚本概览

**文件**: `V7__add_learning_stages_and_contents.sql`

**执行内容**:
1. 创建 `learning_stages` 表
2. 创建 `learning_contents` 表
3. 创建 `programming_question_details` 表
4. 修改 `questions` 表（增加stage_id字段）
5. 修改 `user_question_notes` 表（增加core_strategy字段）
6. 删除 `major_categories` 表中的2个分类
7. 初始化"编程与数据结构"的3个学习阶段

---

### 6.2 完整SQL脚本

```sql
-- =====================================================
-- Migration V7: Learning Stages + Learning Contents
-- =====================================================

-- 1. 创建learning_stages表
CREATE TABLE learning_stages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  skill_id BIGINT NOT NULL COMMENT '所属技能',
  stage_name VARCHAR(50) NOT NULL COMMENT '阶段名称',
  stage_type VARCHAR(50) NOT NULL COMMENT '阶段类型标识',
  description TEXT COMMENT '阶段说明',
  sort_order INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
  UNIQUE KEY uk_skill_stage (skill_id, stage_name),
  INDEX idx_skill_order (skill_id, sort_order)
) COMMENT='学习阶段定义表';

-- 2. 创建learning_contents表
CREATE TABLE learning_contents (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  focus_area_id BIGINT COMMENT 'Focus Area ID（算法模版为NULL）',
  stage_id BIGINT NOT NULL COMMENT '所属学习阶段',
  content_type ENUM('article', 'video', 'code_example', 'template', 'case_study') NOT NULL,

  title VARCHAR(500) NOT NULL,
  description TEXT,
  url VARCHAR(1000) COMMENT '外部资源链接',
  author VARCHAR(100),
  content_data JSON COMMENT '扩展数据',

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

-- 3. 创建programming_question_details表
CREATE TABLE programming_question_details (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  question_id BIGINT NOT NULL UNIQUE COMMENT '关联的题目ID',

  -- 外部资源链接
  leetcode_url VARCHAR(500) COMMENT 'LeetCode题目链接',
  labuladong_url VARCHAR(500) COMMENT 'labuladong题解链接',
  hellointerview_url VARCHAR(500) COMMENT 'Hello Interview题解链接',

  -- 重要性标记
  is_important BOOLEAN DEFAULT FALSE COMMENT '是否为重要算法题（⭐标记）',

  -- 算法标签和相关题目
  tags JSON COMMENT '算法技巧标签，如["双指针","滑动窗口"]',
  complexity VARCHAR(100) COMMENT '算法复杂度，如"时间O(n), 空间O(1)"',
  similar_questions JSON COMMENT '类似题目列表，如[{"id": 15, "title": "3Sum"}]',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
  INDEX idx_important (is_important),
  INDEX idx_tags ((CAST(tags AS CHAR(255) ARRAY)))
) COMMENT='编程题专属字段表（仅用于编程与数据结构Skill）';

-- 4. 修改questions表
ALTER TABLE questions
ADD COLUMN stage_id BIGINT COMMENT '所属学习阶段',
ADD FOREIGN KEY (stage_id) REFERENCES learning_stages(id) ON DELETE SET NULL;

-- 5. 修改user_question_notes表
ALTER TABLE user_question_notes
ADD COLUMN core_strategy TEXT COMMENT '核心思路（Markdown格式，仅编程题使用）';

-- 6. 删除major_categories中的2个分类
DELETE FROM major_categories WHERE name IN ('核心刷题框架', '基础篇');

-- 7. 初始化"编程与数据结构"的学习阶段
-- 假设skill_id=1为"编程与数据结构"
INSERT INTO learning_stages (skill_id, stage_name, stage_type, description, sort_order) VALUES
(1, '基础原理', 'theory', '数据结构的基本概念、特点、适用场景', 1),
(1, '实现代码', 'implementation', '数据结构的代码实现、API设计、算法技巧', 2),
(1, '实战题目', 'practice', 'LeetCode题目练习、算法应用', 3);
```

---

## 7. 测试计划

### 7.1 单元测试

#### 7.1.1 Service层测试

```java
@SpringBootTest
class LearningContentServiceTest {

    @Test
    void testGetContentsByFocusArea() {
        // 测试获取Focus Area的三个阶段内容
        FocusAreaLearningDTO dto = service.getContentsByFocusArea(1L);

        assertEquals(3, dto.getStages().size());
        assertEquals("基础原理", dto.getStages().get(0).getStageName());
    }

    @Test
    void testCreateTemplate() {
        // 测试创建算法模版（focus_area_id = null）
        CreateLearningContentDTO dto = new CreateLearningContentDTO();
        dto.setContentType("template");
        dto.setFocusAreaId(null);  // 必须为null
        dto.setStageId(2L);

        LearningContent content = service.createContent(dto);
        assertNotNull(content.getId());
        assertNull(content.getFocusAreaId());
    }

    @Test
    void testCreateArticle() {
        // 测试创建文章（必须关联Focus Area）
        CreateLearningContentDTO dto = new CreateLearningContentDTO();
        dto.setContentType("article");
        dto.setFocusAreaId(5L);  // 必须有值
        dto.setStageId(1L);

        LearningContent content = service.createContent(dto);
        assertNotNull(content.getId());
        assertEquals(5L, content.getFocusAreaId());
    }
}
```

---

### 7.2 集成测试

#### 7.2.1 API集成测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LearningContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateContent() throws Exception {
        String requestBody = """
            {
              "focusAreaId": 5,
              "stageId": 1,
              "contentType": "article",
              "title": "数组基本原理",
              "url": "https://labuladong.online/...",
              "author": "labuladong"
            }
            """;

        mockMvc.perform(post("/api/admin/learning-contents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("数组基本原理"));
    }

    @Test
    @WithMockUser
    void testGetContentsByFocusArea() throws Exception {
        mockMvc.perform(get("/api/learning-contents?focusAreaId=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stages").isArray())
            .andExpect(jsonPath("$.stages.length()").value(3));
    }
}
```

---

### 7.3 数据导入测试

**测试数据**: 使用`labuladong_phase4_v2_staged.md`中的数据

**测试步骤**:
1. 导入Focus Area 1.1 "数组"的所有内容
2. 验证3个阶段各自的内容数量
3. 验证内容排序正确
4. 验证算法模版（focus_area_id = NULL）导入成功

---

## 8. 性能优化

### 8.1 数据库优化

#### 8.1.1 索引策略

```sql
-- learning_contents表索引
CREATE INDEX idx_focus_stage ON learning_contents(focus_area_id, stage_id, sort_order);
CREATE INDEX idx_stage_type ON learning_contents(stage_id, content_type);
CREATE INDEX idx_template ON learning_contents(content_type, focus_area_id, sort_order);

-- questions表索引
CREATE INDEX idx_question_stage ON questions(stage_id, focus_area_id);
```

---

#### 8.1.2 查询优化

**分页查询**:
```java
// 管理端内容列表分页
Page<LearningContent> findByStageIdOrderBySortOrder(
    Long stageId, Pageable pageable);
```

**JSON字段查询优化**:
- 使用MySQL 8.0的JSON函数（JSON_EXTRACT）
- 避免在WHERE子句中使用JSON字段过滤

---

### 8.2 缓存策略

**Redis缓存**:
```java
@Cacheable(value = "learning_stages", key = "#skillId")
public List<LearningStage> getStagesBySkillId(Long skillId) {
    return learningStageRepository.findBySkillIdOrderBySortOrder(skillId);
}

@Cacheable(value = "focus_area_contents", key = "#focusAreaId")
public FocusAreaLearningDTO getContentsByFocusArea(Long focusAreaId) {
    // ... 查询逻辑
}
```

**缓存失效**:
- 管理员修改内容时，清除对应的缓存
- TTL设置为1小时

---

## 9. 安全设计

### 9.1 权限控制

**管理端API**:
- 所有 `/api/admin/learning-contents/**` 需要管理员权限
- 使用 `@PreAuthorize("hasRole('ADMIN')")` 注解

**用户端API**:
- 所有 `/api/learning-contents/**` 需要登录
- visibility='public'的内容对所有用户可见

---

### 9.2 数据验证

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

**业务规则验证**:
- 算法模版不能关联Focus Area
- 非模版内容必须关联Focus Area
- 只有"实战题目"阶段可以关联Question

---

## 10. 部署清单

### 10.1 数据库变更

- ✅ 执行 Migration V7
- ✅ 验证3个学习阶段已插入
- ✅ 验证major_categories只剩4个

---

### 10.2 后端部署

**新增文件**:
- `LearningStage.java` (Entity)
- `LearningContent.java` (Entity)
- `ProgrammingQuestionDetails.java` (Entity)
- `LearningStageRepository.java`
- `LearningContentRepository.java`
- `ProgrammingQuestionDetailsRepository.java`
- `LearningContentService.java`
- `AdminLearningContentController.java`
- `LearningContentController.java`

**修改文件**:
- `Question.java` (增加stageId字段)
- `UserQuestionNote.java` (增加coreStrategy字段)
- `QuestionService.java` (支持创建/更新programming_question_details)
- `UserQuestionNoteService.java` (支持core_strategy的CRUD)

---

### 10.3 前端部署

**新增页面**:
- `AlgorithmContentManagement.vue` (管理端 - 4个Tab)
- `AlgorithmTemplateManagement.vue` (管理端)
- `AlgorithmLearning.vue` (用户端)
- `AlgorithmTemplates.vue` (用户端)

**新增组件**:
- `LearningStageTab.vue` (学习阶段Tab切换 - 3个Tab)
- `QuestionManagementTab.vue` (题目管理Tab - 第4个Tab)
- `LearningContentList.vue` (内容列表)
- `AddLearningContentModal.vue` (添加内容Modal)
- `QuestionEditModal.vue` (题目编辑Modal - 支持通用字段 + programming_question_details)

---

## 11. 风险与应对

### 11.1 技术风险

**风险1**: JSON字段查询性能问题
- **应对**: 避免在WHERE子句中过滤JSON，改用外键关联查询

**风险2**: 算法模版content_data字段过大
- **应对**: 限制template字段最大长度为10KB，超长代码提供外部链接

---

### 11.2 数据迁移风险

**风险**: Migration V7失败导致表结构不一致
- **应对**:
  - 在测试环境验证Migration脚本
  - 准备回滚脚本
  - 生产环境执行前备份数据库

---

## 12. 附录

### 12.1 术语表

| 术语 | 英文 | 说明 |
|------|------|------|
| 学习阶段 | Learning Stage | Skill级别定义的学习路径阶段 |
| 学习内容 | Learning Content | 统一管理的各类学习资源 |
| 算法模版 | Algorithm Template | 快速参考的代码模版（不关联Focus Area） |
| 内容类型 | Content Type | article/video/code_example/template/case_study |
| 可见性 | Visibility | public（所有用户）/ private（预留） |

---

### 12.2 参考文档

- [Phase 4 详细需求 v2.0](../requirement/Phase4-详细需求.md)
- [Labuladong 三段式结构](../import/labuladong_phase4_v2_staged.md)
- [Phase 4 Design Decisions](../prompt/Prompt.txt)
- [ARCHITECTURE.md](./ARCHITECTURE.md)

---

**文档版本**: v2.2
**最后更新**: 2025-12-22
**状态**: ✅ 设计定稿
**更新说明**:
- v2.0: 初始设计 - 学习阶段 + 学习内容统一表
- v2.1: core_strategy字段修正 - 从programming_question_details移至user_question_notes
- v2.2: 管理员UI改进 - 题目管理独立Tab + programming_question_details表
**下一步**: 实施Migration V7
