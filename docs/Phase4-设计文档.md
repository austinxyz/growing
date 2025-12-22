# Phase 4 设计文档 - 编程与数据结构学习模块

> **架构版本**: v2.0 (learning_stages + learning_contents)
> **状态**: 设计阶段 🟡
> **创建时间**: 2025-12-22
> **需求文档**: [Phase4-详细需求.md](../requirement/Phase4-详细需求.md)
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
✅ **4大分类** - 数据结构、搜索、动规、其他（删除"核心刷题框架"和"基础篇"）
✅ **25个Focus Area** - 精简后的学习主题（从33个合并）
✅ **225篇文章** - labuladong算法笔记完整导入
✅ **算法模版库** - 独立板块，快速参考（不关联Focus Area）
✅ **用户笔记** - 复用Phase 3的user_question_notes表

### 1.3 架构分层

```
┌──────────────────────────────────────────────────────────────┐
│                      前端 (Vue 3)                             │
├──────────────────────────────────────────────────────────────┤
│  管理端                        │  用户端                      │
│  - 分阶段内容管理               │  - 大分类浏览（4个Tab）       │
│    * 按Tab组织内容             │  - Focus Area选择            │
│    * 文章、视频、代码、题目     │  - 学习阶段Tab切换           │
│  - 题目关联到阶段               │  - 内容查看（按阶段）         │
│  - 算法模版管理                │  - 算法模版查阅              │
│                                │  - 个人笔记管理              │
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
│  修改：                                                       │
│  - questions (增加stage_id字段)                              │
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
┌──────────────────┐         ┌─────────────────────┐
│    questions     │────┐    │ learning_stages     │
├──────────────────┤    │    ├─────────────────────┤
│ id (PK)          │    └───→│ id (PK)             │
│ focus_area_id    │   N:1   │ stage_id (FK)       │  ← 新增字段
│ stage_id (FK)    │         └─────────────────────┘
│ title            │
│ difficulty       │
└──────────────────┘
     │
     │ 1:N
     ↓
┌─────────────────────────┐
│ user_question_notes     │  (复用Phase 3)
├─────────────────────────┤
│ id (PK)                 │
│ user_id (FK)            │
│ question_id (FK)        │
│ note_content            │
│ created_at              │
│ updated_at              │
└─────────────────────────┘
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

#### 表3: questions (题目表 - 增加stage_id字段)

**修改**: 为questions表增加stage_id关联

```sql
ALTER TABLE questions
ADD COLUMN stage_id BIGINT COMMENT '所属学习阶段（实战题目阶段）',
ADD FOREIGN KEY (stage_id) REFERENCES learning_stages(id) ON DELETE SET NULL;
```

**用途**: 将题目关联到"实战题目"阶段

**示例**:
```sql
-- 将题目关联到"实战题目"阶段（stage_id = 3）
UPDATE questions
SET stage_id = 3
WHERE focus_area_id IN (SELECT id FROM focus_areas WHERE name = '数组');
```

---

#### 表4: major_categories (大分类表 - 调整为4个)

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

#### 3.1.2 题目关联到阶段

**POST /api/admin/questions/{questionId}/link-stage**
- 功能: 将题目关联到学习阶段
- 权限: 管理员
- 请求体:
  ```json
  {
    "stageId": 3  // "实战题目"阶段ID
  }
  ```
- 返回: 更新后的question对象

**DELETE /api/admin/questions/{questionId}/unlink-stage**
- 功能: 取消题目的阶段关联
- 权限: 管理员
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

**布局**: 两栏布局
```
┌─────────────────────────────────────────────────────────┐
│  【编程与数据结构 - 内容管理】                             │
├─────────────┬───────────────────────────────────────────┤
│             │  【选中: 数组 - 基础原理】                  │
│  左侧树形   │  ┌─────────────────────────────────────┐  │
│  结构       │  │ Tab: 基础原理 | 实现代码 | 实战题目   │  │
│             │  ├─────────────────────────────────────┤  │
│  - 数据结构 │  │ [+ 添加内容]                        │  │
│    - 数组   │  │                                     │  │
│    - 链表   │  │ 1. [📄] 数组（顺序存储）基本原理      │  │
│    - 栈队列 │  │    作者: labuladong [编辑] [删除]    │  │
│  - 搜索     │  │                                     │  │
│    - 回溯   │  │ 2. [📄] 动态数组代码实现             │  │
│    - BFS    │  │    作者: labuladong [编辑] [删除]    │  │
│  - 动规     │  │                                     │  │
│    - 基础   │  │ [拖拽排序]                          │  │
│             │  └─────────────────────────────────────┘  │
└─────────────┴───────────────────────────────────────────┘
```

**功能点**:
1. **左侧树**: 4个大分类 → 25个Focus Area
2. **右侧Tab**: 3个学习阶段（基础原理、实现代码、实战题目）
3. **添加内容**: Modal弹窗，选择类型（article/video/code_example）
4. **编辑/删除**: 内联操作
5. **拖拽排序**: Vue Draggable实现

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
3. 修改 `questions` 表（增加stage_id字段）
4. 删除 `major_categories` 表中的2个分类
5. 初始化"编程与数据结构"的3个学习阶段

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

-- 3. 修改questions表
ALTER TABLE questions
ADD COLUMN stage_id BIGINT COMMENT '所属学习阶段',
ADD FOREIGN KEY (stage_id) REFERENCES learning_stages(id) ON DELETE SET NULL;

-- 4. 删除major_categories中的2个分类
DELETE FROM major_categories WHERE name IN ('核心刷题框架', '基础篇');

-- 5. 初始化"编程与数据结构"的学习阶段
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
- `LearningStageRepository.java`
- `LearningContentRepository.java`
- `LearningContentService.java`
- `AdminLearningContentController.java`
- `LearningContentController.java`

**修改文件**:
- `Question.java` (增加stageId字段)
- `QuestionService.java` (增加linkToStage方法)

---

### 10.3 前端部署

**新增页面**:
- `AlgorithmContentManagement.vue` (管理端)
- `AlgorithmTemplateManagement.vue` (管理端)
- `AlgorithmLearning.vue` (用户端)
- `AlgorithmTemplates.vue` (用户端)

**新增组件**:
- `LearningStageTab.vue` (学习阶段Tab切换)
- `LearningContentList.vue` (内容列表)
- `AddLearningContentModal.vue` (添加内容Modal)

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

**文档版本**: v2.0
**最后更新**: 2025-12-22
**状态**: 待Review ✅
**下一步**: 编写Migration V7脚本
