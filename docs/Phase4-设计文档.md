# Phase 4 设计文档 - 编程与数据结构学习模块

> **状态**: 设计阶段 🟡
> **创建时间**: 2025-12-21
> **需求文档**: [Phase4-详细需求.md](../requirement/Phase4-详细需求.md)

---

## 1. 总体架构

### 1.1 模块概览

Phase 4 在现有试题库基础上，为"编程与数据结构"技能提供专门的学习模块，整合LeetCode、labuladong算法笔记等外部资源。

**核心特性**:
- 基于labuladong的6大分类组织学习内容
- Focus Area级别的学习资料管理
- 题目扩展信息（LeetCode链接、关键思路、标签等）
- 常用算法模板库
- 用户个人笔记（复用Phase 3的user_question_notes表）

**架构分层**:
```
┌─────────────────────────────────────────────────────────┐
│                     前端 (Vue 3)                         │
├─────────────────────────────────────────────────────────┤
│  管理端                    │  用户端                     │
│  - 编程数据结构内容管理     │  - 算法笔记浏览             │
│  - 常用算法管理            │  - 常用算法查阅             │
│                           │  - 个人笔记管理             │
└─────────────────────────────────────────────────────────┘
                          ↕ RESTful API
┌─────────────────────────────────────────────────────────┐
│                  后端 (Spring Boot)                      │
├─────────────────────────────────────────────────────────┤
│  Controller层                                            │
│  - AdminAlgorithmController (管理员API)                 │
│  - AlgorithmController (用户API)                        │
├─────────────────────────────────────────────────────────┤
│  Service层                                               │
│  - MajorCategoryService (大分类管理)                    │
│  - FocusAreaLearningResourceService (学习资料)          │
│  - QuestionExtensionService (题目扩展信息)              │
│  - AlgorithmTemplateService (算法模板)                  │
├─────────────────────────────────────────────────────────┤
│  Repository层 (Spring Data JPA)                          │
│  - major_categories                                      │
│  - major_category_focus_areas                            │
│  - focus_area_learning_resources                         │
│  - question_extensions                                   │
│  - algorithm_templates                                   │
└─────────────────────────────────────────────────────────┘
                          ↕
┌─────────────────────────────────────────────────────────┐
│                  数据库 (MySQL 8.0)                      │
│  - 5张新表 + 复用现有表 (questions, user_question_notes) │
└─────────────────────────────────────────────────────────┘
```

### 1.2 与现有架构的集成

**复用Phase 1-3的概念**:
- **Skill** - "编程与数据结构"（skill_id = 已存在）
- **Focus Area** - 小分类（如"双指针"、"滑动窗口"）
- **Question** - LeetCode题目（复用questions表）
- **UserQuestionNote** - 用户笔记（复用user_question_notes表）

**新增概念**:
- **Major Category** - labuladong的6大分类
- **Focus Area Learning Resource** - Focus Area级别的学习资料
- **Question Extension** - 题目扩展信息（链接、思路、标签）
- **Algorithm Template** - 常用算法模板

---

## 2. 数据模型设计

### 2.1 ER图

```
┌─────────────────────┐
│  major_categories   │  (大分类 - 6条固定数据)
├─────────────────────┤
│ id (PK)             │
│ name                │  (基础篇、核心刷题框架...)
│ display_order       │
│ description         │
└─────────────────────┘
          │
          │ 1:N
          ↓
┌──────────────────────────────┐
│ major_category_focus_areas   │  (关联表)
├──────────────────────────────┤
│ id (PK)                      │
│ major_category_id (FK)       │
│ focus_area_id (FK)           │
│ display_order                │
└──────────────────────────────┘
          │
          │ N:1
          ↓
┌─────────────────────┐
│   focus_areas       │  (复用现有表 - 小分类)
├─────────────────────┤
│ id (PK)             │
│ skill_id (FK)       │
│ name                │
└─────────────────────┘
          │
          │ 1:N
          ↓
┌──────────────────────────────────┐
│ focus_area_learning_resources    │  (学习资料)
├──────────────────────────────────┤
│ id (PK)                          │
│ focus_area_id (FK)               │
│ title                            │
│ resource_type (ENUM)             │  ARTICLE/VIDEO/DOCUMENT
│ url                              │
│ author                           │
│ description (TEXT)               │
│ display_order                    │
│ created_at                       │
│ updated_at                       │
└──────────────────────────────────┘

┌─────────────────────┐
│    questions        │  (复用现有表 - 题目)
├─────────────────────┤
│ id (PK)             │
│ focus_area_id (FK)  │
│ title               │
│ difficulty          │
└─────────────────────┘
          │
          │ 1:1
          ↓
┌──────────────────────────────────┐
│      question_extensions         │  (题目扩展信息)
├──────────────────────────────────┤
│ id (PK)                          │
│ question_id (FK, UNIQUE)         │
│ leetcode_number                  │
│ leetcode_url                     │
│ labuladong_url                   │
│ hello_interview_url              │
│ other_urls (JSON)                │  ["url1", "url2"]
│ tags (JSON)                      │  ["数组", "双指针"]
│ similar_questions (JSON)         │  [26, 27, 283]
│ key_insights (TEXT, Markdown)    │
│ time_complexity                  │
│ space_complexity                 │
│ created_at                       │
│ updated_at                       │
└──────────────────────────────────┘

┌─────────────────────┐
│    questions        │  (复用现有表)
└─────────────────────┘
          │
          │ 1:N
          ↓
┌──────────────────────────────────┐
│    user_question_notes           │  (复用Phase 3)
├──────────────────────────────────┤
│ id (PK)                          │
│ question_id (FK)                 │
│ user_id (FK)                     │
│ note_content (TEXT, Markdown)    │
│ created_at                       │
│ updated_at                       │
│ UNIQUE(question_id, user_id)     │
└──────────────────────────────────┘

┌──────────────────────────────────┐
│     algorithm_templates          │  (算法模板 - 独立表)
├──────────────────────────────────┤
│ id (PK)                          │
│ name                             │
│ description                      │
│ template_code (TEXT, Markdown)   │
│ notes (TEXT, Markdown)           │
│ time_complexity                  │
│ space_complexity                 │
│ related_questions (JSON)         │  [704, 35, 33]
│ created_at                       │
│ updated_at                       │
└──────────────────────────────────┘
```

### 2.2 表结构详细设计

#### 2.2.1 major_categories (大分类表)

```sql
CREATE TABLE major_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '大分类名称',
    display_order INT NOT NULL COMMENT '显示顺序',
    description TEXT COMMENT '分类描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='编程与数据结构大分类';
```

**初始数据**（6条固定记录）:
```sql
INSERT INTO major_categories (name, display_order, description) VALUES
('基础篇', 1, '算法基础知识和编程思维'),
('核心刷题框架', 2, '双指针、滑动窗口、二分查找等核心技巧'),
('经典数据结构算法', 3, '数组、链表、二叉树、图等数据结构'),
('经典暴力搜索算法', 4, 'DFS、BFS、回溯等搜索算法'),
('经典动态规划算法', 5, '动态规划的各种经典问题'),
('其他算法', 6, '数学技巧、位运算等其他算法');
```

#### 2.2.2 major_category_focus_areas (关联表)

```sql
CREATE TABLE major_category_focus_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    major_category_id BIGINT NOT NULL COMMENT '大分类ID',
    focus_area_id BIGINT NOT NULL COMMENT 'Focus Area ID',
    display_order INT NOT NULL DEFAULT 0 COMMENT '在该大分类下的显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (major_category_id) REFERENCES major_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
    UNIQUE KEY uk_category_focus (major_category_id, focus_area_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大分类与Focus Area关联表';
```

**索引设计**:
- `uk_category_focus`: 防止重复关联
- `idx_major_category_id`: 加速按大分类查询

#### 2.2.3 focus_area_learning_resources (学习资料表)

```sql
CREATE TABLE focus_area_learning_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    focus_area_id BIGINT NOT NULL COMMENT 'Focus Area ID',
    title VARCHAR(255) NOT NULL COMMENT '资料标题',
    resource_type ENUM('ARTICLE', 'VIDEO', 'DOCUMENT', 'OTHER') NOT NULL COMMENT '资料类型',
    url VARCHAR(500) COMMENT '资料链接',
    author VARCHAR(100) COMMENT '作者/来源',
    description TEXT COMMENT '资料简介',
    display_order INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
    INDEX idx_focus_area (focus_area_id, display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Focus Area学习资料';
```

**字段说明**:
- `resource_type`: 区分文章、视频、文档等类型
- `url`: 可选，支持离线资料（无链接）
- `display_order`: 支持管理员自定义排序

#### 2.2.4 question_extensions (题目扩展信息表)

```sql
CREATE TABLE question_extensions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL COMMENT '题目ID',
    leetcode_number INT COMMENT 'LeetCode题号',
    leetcode_url VARCHAR(500) COMMENT 'LeetCode链接',
    labuladong_url VARCHAR(500) COMMENT 'labuladong链接',
    hello_interview_url VARCHAR(500) COMMENT 'Hello Interview链接',
    other_urls JSON COMMENT '其他相关链接数组',
    tags JSON COMMENT '标签数组 ["数组", "双指针"]',
    similar_questions JSON COMMENT '类似题目ID数组 [26, 27, 283]',
    key_insights TEXT COMMENT '关键思路 (Markdown格式)',
    time_complexity VARCHAR(50) COMMENT '时间复杂度',
    space_complexity VARCHAR(50) COMMENT '空间复杂度',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    UNIQUE KEY uk_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目扩展信息';
```

**JSON字段示例**:
```json
{
  "other_urls": [
    "https://visualgo.net/en/sorting",
    "https://cp-algorithms.com/data_structures/sparse-table.html"
  ],
  "tags": ["数组", "双指针", "原地修改"],
  "similar_questions": [26, 27, 283]
}
```

**索引设计**:
- `uk_question`: 确保一个题目只有一条扩展信息
- `idx_leetcode_number`: 支持按LeetCode题号搜索

#### 2.2.5 algorithm_templates (算法模板表)

```sql
CREATE TABLE algorithm_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '算法名称',
    description TEXT COMMENT '算法简介',
    template_code TEXT NOT NULL COMMENT '模板代码 (Markdown格式)',
    notes TEXT COMMENT '注意事项 (Markdown格式)',
    time_complexity VARCHAR(50) COMMENT '时间复杂度',
    space_complexity VARCHAR(50) COMMENT '空间复杂度',
    related_questions JSON COMMENT '相关题目ID数组',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常用算法模板';
```

**字段说明**:
- 不包含`category`字段（根据需求反馈，算法无需分类）
- `template_code`和`notes`使用Markdown格式，支持代码高亮
- `related_questions`存储题目ID数组，方便跳转

### 2.3 数据关联规则

#### 级联删除规则

```
major_categories (大分类)
  └─ ON DELETE CASCADE → major_category_focus_areas
       └─ ON DELETE CASCADE → focus_areas
            ├─ ON DELETE CASCADE → focus_area_learning_resources
            └─ ON DELETE CASCADE → questions
                 ├─ ON DELETE CASCADE → question_extensions
                 └─ ON DELETE CASCADE → user_question_notes
```

**说明**:
- 删除大分类 → 删除关联关系（不删除Focus Area本身）
- 删除Focus Area → 删除学习资料 + 题目 + 扩展信息 + 笔记
- 删除题目 → 删除扩展信息 + 用户笔记
- Algorithm Templates独立存在，不受级联影响

**注意**: 实际使用中，大分类和Focus Area通常不会被删除，这些级联规则主要是数据库层面的保护机制

#### 可见性规则

| 资源类型 | 可见性 | 规则 |
|---------|--------|------|
| major_categories | 全部用户 | 6条固定数据 |
| focus_area_learning_resources | 全部用户 | 管理员添加，所有用户可见 |
| questions | 公共+私有 | is_official=true或created_by_user_id=当前用户 |
| question_extensions | 全部用户 | 管理员添加，所有用户可见（扩展公共题目） |
| algorithm_templates | 全部用户 | 管理员添加，所有用户可见 |
| user_question_notes | 仅本人 | 只有创建者可见 |

---

## 3. 后端API设计

### 3.1 管理员API (AdminAlgorithmController)

#### 3.1.1 大分类管理

```java
// GET /api/admin/algorithm/major-categories
// 获取所有大分类（6条固定数据）
public ResponseEntity<List<MajorCategoryDTO>> getMajorCategories()

// POST /api/admin/algorithm/major-categories/{id}/focus-areas
// 为大分类添加Focus Area关联
public ResponseEntity<Void> addFocusAreaToCategory(
    @PathVariable Long id,
    @RequestBody FocusAreaAssignmentDTO dto  // {focusAreaId, displayOrder}
)

// DELETE /api/admin/algorithm/major-categories/{categoryId}/focus-areas/{focusAreaId}
// 移除大分类的Focus Area关联
public ResponseEntity<Void> removeFocusAreaFromCategory(
    @PathVariable Long categoryId,
    @PathVariable Long focusAreaId
)

// PUT /api/admin/algorithm/major-categories/{id}/focus-areas/order
// 批量更新Focus Area在大分类下的排序
public ResponseEntity<Void> updateFocusAreaOrder(
    @PathVariable Long id,
    @RequestBody List<FocusAreaOrderDTO> orders  // [{focusAreaId, displayOrder}]
)
```

#### 3.1.2 学习资料管理

```java
// POST /api/admin/algorithm/focus-areas/{id}/resources
// 为Focus Area添加学习资料
public ResponseEntity<LearningResourceDTO> createLearningResource(
    @PathVariable Long id,
    @RequestBody LearningResourceCreateDTO dto
)

// PUT /api/admin/algorithm/resources/{id}
// 更新学习资料
public ResponseEntity<LearningResourceDTO> updateLearningResource(
    @PathVariable Long id,
    @RequestBody LearningResourceUpdateDTO dto
)

// DELETE /api/admin/algorithm/resources/{id}
// 删除学习资料
public ResponseEntity<Void> deleteLearningResource(@PathVariable Long id)

// PUT /api/admin/algorithm/focus-areas/{id}/resources/order
// 批量更新资料排序
public ResponseEntity<Void> updateResourceOrder(
    @PathVariable Long id,
    @RequestBody List<ResourceOrderDTO> orders
)
```

#### 3.1.3 题目扩展信息管理

```java
// POST /api/admin/algorithm/questions/{id}/extension
// 为题目创建/更新扩展信息（UPSERT逻辑）
public ResponseEntity<QuestionExtensionDTO> saveQuestionExtension(
    @PathVariable Long id,
    @RequestBody QuestionExtensionDTO dto
)

// DELETE /api/admin/algorithm/questions/{id}/extension
// 删除题目扩展信息
public ResponseEntity<Void> deleteQuestionExtension(@PathVariable Long id)
```

#### 3.1.4 算法模板管理

```java
// POST /api/admin/algorithm/templates
// 创建算法模板
public ResponseEntity<AlgorithmTemplateDTO> createTemplate(
    @RequestBody AlgorithmTemplateCreateDTO dto
)

// PUT /api/admin/algorithm/templates/{id}
// 更新算法模板
public ResponseEntity<AlgorithmTemplateDTO> updateTemplate(
    @PathVariable Long id,
    @RequestBody AlgorithmTemplateUpdateDTO dto
)

// DELETE /api/admin/algorithm/templates/{id}
// 删除算法模板
public ResponseEntity<Void> deleteTemplate(@PathVariable Long id)
```

### 3.2 用户API (AlgorithmController)

#### 3.2.1 算法笔记浏览

```java
// GET /api/algorithm/major-categories
// 获取所有大分类
public ResponseEntity<List<MajorCategoryDTO>> getMajorCategories()

// GET /api/algorithm/major-categories/{id}/focus-areas
// 获取大分类下的所有Focus Area
public ResponseEntity<List<FocusAreaDTO>> getFocusAreasByCategory(
    @PathVariable Long id
)

// GET /api/algorithm/focus-areas/{id}/resources
// 获取Focus Area的学习资料
public ResponseEntity<List<LearningResourceDTO>> getLearningResources(
    @PathVariable Long id
)

// GET /api/algorithm/focus-areas/{id}/questions
// 获取Focus Area的题目列表（含扩展信息）
public ResponseEntity<List<QuestionWithExtensionDTO>> getQuestionsByFocusArea(
    @PathVariable Long id
)

// GET /api/algorithm/questions/{id}/detail
// 获取题目详情（含扩展信息 + 用户笔记）
public ResponseEntity<QuestionDetailDTO> getQuestionDetail(
    @PathVariable Long id,
    @RequestHeader("Authorization") String token
)
```

#### 3.2.2 常用算法查阅

```java
// GET /api/algorithm/templates
// 获取所有算法模板
public ResponseEntity<List<AlgorithmTemplateDTO>> getAllTemplates()

// GET /api/algorithm/templates/{id}
// 获取算法模板详情
public ResponseEntity<AlgorithmTemplateDTO> getTemplateDetail(
    @PathVariable Long id
)
```

### 3.3 DTO设计

#### 3.3.1 MajorCategoryDTO

```java
public class MajorCategoryDTO {
    private Long id;
    private String name;
    private Integer displayOrder;
    private String description;
    private List<FocusAreaSimpleDTO> focusAreas;  // 该大分类下的Focus Area列表
}
```

#### 3.3.2 LearningResourceDTO

```java
public class LearningResourceDTO {
    private Long id;
    private Long focusAreaId;
    private String title;
    private String resourceType;  // ARTICLE/VIDEO/DOCUMENT/OTHER
    private String url;
    private String author;
    private String description;
    private Integer displayOrder;
}
```

#### 3.3.3 QuestionExtensionDTO

```java
public class QuestionExtensionDTO {
    private Long id;
    private Long questionId;
    private Integer leetcodeNumber;
    private String leetcodeUrl;
    private String labuladongUrl;
    private String helloInterviewUrl;
    private List<String> otherUrls;  // JSON解析后
    private List<String> tags;       // JSON解析后
    private List<Integer> similarQuestions;  // JSON解析后
    private String keyInsights;      // Markdown格式
    private String timeComplexity;
    private String spaceComplexity;
}
```

#### 3.3.4 QuestionWithExtensionDTO

```java
public class QuestionWithExtensionDTO {
    // 基础信息（来自Question）
    private Long id;
    private String title;
    private String difficulty;

    // 扩展信息（来自QuestionExtension）
    private QuestionExtensionDTO extension;  // 可能为null
}
```

#### 3.3.5 QuestionDetailDTO

```java
public class QuestionDetailDTO {
    // 基础信息
    private Long id;
    private String title;
    private String difficulty;
    private String description;
    private String answerRequirement;
    private List<String> redFlags;

    // 扩展信息
    private QuestionExtensionDTO extension;

    // 用户笔记
    private UserQuestionNoteDTO userNote;  // 可能为null
}
```

#### 3.3.6 AlgorithmTemplateDTO

```java
public class AlgorithmTemplateDTO {
    private Long id;
    private String name;
    private String description;
    private String templateCode;    // Markdown格式
    private String notes;            // Markdown格式
    private String timeComplexity;
    private String spaceComplexity;
    private List<Integer> relatedQuestions;  // JSON解析后
}
```

---

## 4. 前端路由和组件设计

### 4.1 路由配置

```javascript
// src/router/index.js

// 管理员路由
{
  path: '/admin',
  component: AdminLayout,
  meta: { requiresAdmin: true },
  children: [
    // 编程与数据结构内容管理
    {
      path: 'algorithm-content',
      name: 'AlgorithmContentManagement',
      component: () => import('@/views/admin/AlgorithmContentManagement.vue')
    },
    // 常用算法管理
    {
      path: 'algorithm-templates',
      name: 'AlgorithmTemplateManagement',
      component: () => import('@/views/admin/AlgorithmTemplateManagement.vue')
    }
  ]
},

// 用户路由
{
  path: '/learning',
  component: LearningLayout,
  meta: { requiresAuth: true },
  children: [
    // 算法笔记
    {
      path: 'algorithm-notes',
      name: 'AlgorithmNotes',
      component: () => import('@/views/learning/AlgorithmNotes.vue')
    },
    // 常用算法
    {
      path: 'algorithm-templates',
      name: 'AlgorithmTemplates',
      component: () => import('@/views/learning/AlgorithmTemplates.vue')
    }
  ]
}
```

### 4.2 管理员页面组件

#### 4.2.1 AlgorithmContentManagement.vue (编程与数据结构内容管理)

**布局**: 三栏布局（Tab导航 + 小分类列表 + 内容编辑）

```vue
<template>
  <div class="algorithm-content-management">
    <!-- 顶部: 6个大分类Tab -->
    <div class="category-tabs">
      <button
        v-for="category in majorCategories"
        :key="category.id"
        :class="{ active: selectedCategoryId === category.id }"
        @click="selectCategory(category.id)"
      >
        {{ category.name }}
      </button>
    </div>

    <div class="content-area">
      <!-- 左侧: Focus Area列表 (25%) -->
      <div class="focus-area-panel">
        <div class="panel-header">
          <h3>专注领域</h3>
          <button @click="showAddFocusAreaModal">添加专注领域</button>
        </div>
        <draggable
          v-model="focusAreas"
          @end="updateFocusAreaOrder"
        >
          <div
            v-for="fa in focusAreas"
            :key="fa.id"
            :class="{ active: selectedFocusAreaId === fa.id }"
            @click="selectFocusArea(fa.id)"
          >
            {{ fa.name }}
          </div>
        </draggable>
      </div>

      <!-- 右侧: 学习资料 + 题目列表 (75%) -->
      <div class="content-panel">
        <!-- 学习资料区 -->
        <section class="resources-section">
          <div class="section-header">
            <h3>学习资料</h3>
            <button @click="showAddResourceModal">添加资料</button>
          </div>
          <draggable
            v-model="learningResources"
            @end="updateResourceOrder"
          >
            <LearningResourceCard
              v-for="resource in learningResources"
              :key="resource.id"
              :resource="resource"
              @edit="editResource"
              @delete="deleteResource"
            />
          </draggable>
        </section>

        <!-- 题目列表区 -->
        <section class="questions-section">
          <div class="section-header">
            <h3>题目集</h3>
            <button @click="showAddQuestionModal">添加题目</button>
          </div>
          <QuestionCard
            v-for="question in questions"
            :key="question.id"
            :question="question"
            @edit="editQuestion"
            @delete="deleteQuestion"
          />
        </section>
      </div>
    </div>

    <!-- Modals -->
    <AddFocusAreaModal v-if="showAddFAModal" ... />
    <AddResourceModal v-if="showAddResModal" ... />
    <QuestionEditModal v-if="showQuestionModal" ... />
  </div>
</template>
```

**数据流**:
1. 加载6个大分类（mounted）
2. 选择大分类 → 加载该分类下的Focus Area列表
3. 选择Focus Area → 加载学习资料 + 题目列表
4. 拖拽排序 → 调用API批量更新`display_order`

#### 4.2.2 AlgorithmTemplateManagement.vue (常用算法管理)

**布局**: 两栏布局（算法列表 + 详情编辑）

```vue
<template>
  <div class="algorithm-template-management">
    <!-- 左侧: 算法列表 (30%) -->
    <div class="template-list">
      <div class="panel-header">
        <h3>常用算法</h3>
        <button @click="createTemplate">新建算法</button>
      </div>
      <input
        v-model="searchKeyword"
        placeholder="搜索算法..."
        class="search-input"
      />
      <div
        v-for="template in filteredTemplates"
        :key="template.id"
        :class="{ active: selectedTemplateId === template.id }"
        @click="selectTemplate(template.id)"
      >
        {{ template.name }}
      </div>
    </div>

    <!-- 右侧: 算法详情编辑 (70%) -->
    <div class="template-detail" v-if="selectedTemplate">
      <form @submit.prevent="saveTemplate">
        <label>算法名称</label>
        <input v-model="selectedTemplate.name" required />

        <label>简介</label>
        <textarea v-model="selectedTemplate.description" rows="3" />

        <label>模板代码</label>
        <MarkdownEditor v-model="selectedTemplate.templateCode" />

        <label>注意事项</label>
        <MarkdownEditor v-model="selectedTemplate.notes" />

        <label>时间复杂度</label>
        <input v-model="selectedTemplate.timeComplexity" />

        <label>空间复杂度</label>
        <input v-model="selectedTemplate.spaceComplexity" />

        <label>相关题目</label>
        <QuestionSelector v-model="selectedTemplate.relatedQuestions" />

        <div class="actions">
          <button type="submit">保存</button>
          <button type="button" @click="deleteTemplate">删除</button>
        </div>
      </form>
    </div>
  </div>
</template>
```

### 4.3 用户页面组件

#### 4.3.1 AlgorithmNotes.vue (算法笔记)

**布局**: 三栏布局（Tab导航 + 小分类列表 + 学习内容）

```vue
<template>
  <div class="algorithm-notes">
    <!-- 顶部: 6个大分类Tab -->
    <div class="category-tabs">
      <button
        v-for="category in majorCategories"
        :key="category.id"
        :class="{ active: selectedCategoryId === category.id }"
        @click="selectCategory(category.id)"
      >
        {{ category.name }}
      </button>
    </div>

    <div class="content-area">
      <!-- 左侧: Focus Area列表 (25%) -->
      <div class="focus-area-panel">
        <h3>专注领域</h3>
        <div
          v-for="fa in focusAreas"
          :key="fa.id"
          :class="{ active: selectedFocusAreaId === fa.id }"
          @click="selectFocusArea(fa.id)"
        >
          {{ fa.name }}
        </div>
      </div>

      <!-- 右侧: 学习资料 + 题目总结 (75%) -->
      <div class="content-panel">
        <!-- 学习资料区 (只读) -->
        <section class="resources-section">
          <h3>学习资料</h3>
          <div v-for="resource in learningResources" :key="resource.id">
            <span class="resource-type-icon">{{ getTypeIcon(resource.type) }}</span>
            <a :href="resource.url" target="_blank">{{ resource.title }}</a>
            <span class="author">{{ resource.author }}</span>
          </div>
        </section>

        <!-- 题目总结区 -->
        <section class="questions-section">
          <h3>题目总结</h3>
          <div
            v-for="question in questions"
            :key="question.id"
            class="question-item"
            @click="openQuestionDetail(question.id)"
          >
            <span class="question-title">
              [{{ question.extension?.leetcodeNumber }}] {{ question.title }}
            </span>
            <DifficultyBadge :difficulty="question.difficulty" />
            <div class="quick-links">
              <a v-if="question.extension?.leetcodeUrl" :href="question.extension.leetcodeUrl" target="_blank">LeetCode</a>
              <a v-if="question.extension?.labuladongUrl" :href="question.extension.labuladongUrl" target="_blank">labuladong</a>
            </div>
          </div>
        </section>
      </div>
    </div>

    <!-- 题目详情Modal -->
    <QuestionDetailModal
      v-if="showDetailModal"
      :questionId="selectedQuestionId"
      @close="showDetailModal = false"
    />
  </div>
</template>
```

**数据流**:
1. 加载6个大分类
2. 选择大分类 → 加载Focus Area列表
3. 选择Focus Area → 加载学习资料 + 题目列表
4. 点击题目 → 弹出QuestionDetailModal

#### 4.3.2 AlgorithmTemplates.vue (常用算法)

**布局**: 两栏布局（算法列表 + 算法详情）

```vue
<template>
  <div class="algorithm-templates">
    <!-- 左侧: 算法列表 (30%) -->
    <div class="template-list">
      <h3>常用算法</h3>
      <input
        v-model="searchKeyword"
        placeholder="搜索算法..."
        class="search-input"
      />
      <div
        v-for="template in filteredTemplates"
        :key="template.id"
        :class="{ active: selectedTemplateId === template.id }"
        @click="selectTemplate(template.id)"
      >
        {{ template.name }}
      </div>
    </div>

    <!-- 右侧: 算法详情 (70%) -->
    <div class="template-detail" v-if="selectedTemplate">
      <h2>{{ selectedTemplate.name }}</h2>
      <p class="description">{{ selectedTemplate.description }}</p>

      <div class="complexity">
        <span>时间复杂度: {{ selectedTemplate.timeComplexity }}</span>
        <span>空间复杂度: {{ selectedTemplate.spaceComplexity }}</span>
      </div>

      <section class="template-code">
        <h3>模板代码</h3>
        <MarkdownRenderer :content="selectedTemplate.templateCode" />
      </section>

      <section class="notes">
        <h3>注意事项</h3>
        <MarkdownRenderer :content="selectedTemplate.notes" />
      </section>

      <section class="related-questions" v-if="selectedTemplate.relatedQuestions?.length">
        <h3>相关题目</h3>
        <div
          v-for="qid in selectedTemplate.relatedQuestions"
          :key="qid"
          @click="jumpToQuestion(qid)"
        >
          [{{ qid }}] {{ getQuestionTitle(qid) }}
        </div>
      </section>
    </div>
  </div>
</template>
```

### 4.4 可复用组件

#### 4.4.1 LearningResourceCard.vue

```vue
<template>
  <div class="learning-resource-card">
    <span class="type-icon">{{ typeIcon }}</span>
    <div class="content">
      <h4>{{ resource.title }}</h4>
      <p class="author">{{ resource.author }}</p>
      <p class="description">{{ resource.description }}</p>
      <a :href="resource.url" target="_blank">查看资料</a>
    </div>
    <div class="actions">
      <button @click="$emit('edit', resource)">编辑</button>
      <button @click="$emit('delete', resource.id)">删除</button>
    </div>
  </div>
</template>
```

#### 4.4.2 QuestionDetailModal.vue

**关键功能**:
- 显示题目基础信息 + 扩展信息
- 内嵌UserNoteEditor（复用Phase 3组件）
- 支持跳转到类似题目

```vue
<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>[{{ question.extension?.leetcodeNumber }}] {{ question.title }}</h2>
        <DifficultyBadge :difficulty="question.difficulty" />
        <button @click="$emit('close')">✕</button>
      </div>

      <div class="modal-body">
        <!-- 外部链接 -->
        <section class="external-links">
          <h3>外部链接</h3>
          <a v-if="extension?.leetcodeUrl" :href="extension.leetcodeUrl" target="_blank">LeetCode</a>
          <a v-if="extension?.labuladongUrl" :href="extension.labuladongUrl" target="_blank">labuladong</a>
          <a v-if="extension?.helloInterviewUrl" :href="extension.helloInterviewUrl" target="_blank">Hello Interview</a>
          <a
            v-for="(url, idx) in extension?.otherUrls"
            :key="idx"
            :href="url"
            target="_blank"
          >
            其他链接{{ idx + 1 }}
          </a>
        </section>

        <!-- 关键思路 -->
        <section class="key-insights" v-if="extension?.keyInsights">
          <h3>关键思路</h3>
          <MarkdownRenderer :content="extension.keyInsights" />
        </section>

        <!-- 标签 -->
        <section class="tags" v-if="extension?.tags?.length">
          <h3>标签</h3>
          <span v-for="tag in extension.tags" :key="tag" class="tag">{{ tag }}</span>
        </section>

        <!-- 复杂度 -->
        <section class="complexity">
          <span>时间复杂度: {{ extension?.timeComplexity || 'N/A' }}</span>
          <span>空间复杂度: {{ extension?.spaceComplexity || 'N/A' }}</span>
        </section>

        <!-- 类似题目 -->
        <section class="similar-questions" v-if="extension?.similarQuestions?.length">
          <h3>类似题目</h3>
          <div
            v-for="qid in extension.similarQuestions"
            :key="qid"
            @click="jumpToSimilarQuestion(qid)"
          >
            [{{ qid }}] {{ getSimilarQuestionTitle(qid) }}
          </div>
        </section>

        <!-- 我的笔记 -->
        <section class="user-note">
          <h3>我的笔记</h3>
          <UserNoteEditor :questionId="questionId" />
        </section>
      </div>
    </div>
  </div>
</template>
```

#### 4.4.3 QuestionSelector.vue

**用途**: 在管理员页面选择相关题目（用于算法模板的`relatedQuestions`）

```vue
<template>
  <div class="question-selector">
    <input
      v-model="searchKeyword"
      placeholder="搜索题目（题号或标题）..."
      @input="searchQuestions"
    />
    <div class="selected-questions">
      <span
        v-for="qid in modelValue"
        :key="qid"
        class="selected-tag"
      >
        [{{ qid }}] {{ getQuestionTitle(qid) }}
        <button @click="removeQuestion(qid)">×</button>
      </span>
    </div>
    <div class="search-results" v-if="searchResults.length">
      <div
        v-for="q in searchResults"
        :key="q.id"
        @click="addQuestion(q.id)"
      >
        [{{ q.id }}] {{ q.title }}
      </div>
    </div>
  </div>
</template>
```

---

## 5. 后端Service层设计

### 5.1 MajorCategoryService

```java
@Service
public class MajorCategoryService {

    @Autowired
    private MajorCategoryRepository majorCategoryRepository;

    @Autowired
    private MajorCategoryFocusAreaRepository mcfaRepository;

    // 获取所有大分类（带Focus Area列表）
    public List<MajorCategoryDTO> getAllCategories() {
        List<MajorCategory> categories = majorCategoryRepository.findAllByOrderByDisplayOrder();
        return categories.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private MajorCategoryDTO convertToDTO(MajorCategory category) {
        MajorCategoryDTO dto = new MajorCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setDescription(category.getDescription());

        // 加载该大分类下的Focus Area列表（按display_order排序）
        List<MajorCategoryFocusArea> mappings = mcfaRepository
            .findByMajorCategoryIdOrderByDisplayOrder(category.getId());

        dto.setFocusAreas(mappings.stream()
            .map(m -> convertToFocusAreaSimpleDTO(m.getFocusArea()))
            .collect(Collectors.toList())
        );

        return dto;
    }

    // 为大分类添加Focus Area关联
    @Transactional
    public void addFocusAreaToCategory(Long categoryId, Long focusAreaId, Integer displayOrder) {
        MajorCategoryFocusArea mapping = new MajorCategoryFocusArea();
        mapping.setMajorCategoryId(categoryId);
        mapping.setFocusAreaId(focusAreaId);
        mapping.setDisplayOrder(displayOrder);
        mcfaRepository.save(mapping);
    }

    // 批量更新Focus Area排序
    @Transactional
    public void updateFocusAreaOrder(Long categoryId, List<FocusAreaOrderDTO> orders) {
        for (FocusAreaOrderDTO order : orders) {
            mcfaRepository.updateDisplayOrder(
                categoryId,
                order.getFocusAreaId(),
                order.getDisplayOrder()
            );
        }
    }
}
```

### 5.2 FocusAreaLearningResourceService

```java
@Service
public class FocusAreaLearningResourceService {

    @Autowired
    private FocusAreaLearningResourceRepository resourceRepository;

    // 创建学习资料
    @Transactional
    public LearningResourceDTO createResource(Long focusAreaId, LearningResourceCreateDTO dto) {
        FocusAreaLearningResource resource = new FocusAreaLearningResource();
        resource.setFocusAreaId(focusAreaId);
        resource.setTitle(dto.getTitle());
        resource.setResourceType(dto.getResourceType());
        resource.setUrl(dto.getUrl());
        resource.setAuthor(dto.getAuthor());
        resource.setDescription(dto.getDescription());

        // 自动设置display_order为最大值+1
        Integer maxOrder = resourceRepository.findMaxDisplayOrderByFocusAreaId(focusAreaId);
        resource.setDisplayOrder(maxOrder == null ? 0 : maxOrder + 1);

        FocusAreaLearningResource saved = resourceRepository.save(resource);
        return convertToDTO(saved);
    }

    // 获取Focus Area的学习资料（按display_order排序）
    public List<LearningResourceDTO> getResourcesByFocusArea(Long focusAreaId) {
        List<FocusAreaLearningResource> resources = resourceRepository
            .findByFocusAreaIdOrderByDisplayOrder(focusAreaId);

        return resources.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // 批量更新资料排序
    @Transactional
    public void updateResourceOrder(Long focusAreaId, List<ResourceOrderDTO> orders) {
        for (ResourceOrderDTO order : orders) {
            resourceRepository.updateDisplayOrder(order.getResourceId(), order.getDisplayOrder());
        }
    }
}
```

### 5.3 QuestionExtensionService

```java
@Service
public class QuestionExtensionService {

    @Autowired
    private QuestionExtensionRepository extensionRepository;

    @Autowired
    private ObjectMapper objectMapper;  // Jackson for JSON serialization

    // 创建/更新题目扩展信息（UPSERT逻辑）
    @Transactional
    public QuestionExtensionDTO saveExtension(Long questionId, QuestionExtensionDTO dto) {
        QuestionExtension extension = extensionRepository
            .findByQuestionId(questionId)
            .orElse(new QuestionExtension());

        extension.setQuestionId(questionId);
        extension.setLeetcodeNumber(dto.getLeetcodeNumber());
        extension.setLeetcodeUrl(dto.getLeetcodeUrl());
        extension.setLabuladongUrl(dto.getLabuladongUrl());
        extension.setHelloInterviewUrl(dto.getHelloInterviewUrl());

        // JSON字段序列化
        try {
            extension.setOtherUrls(objectMapper.writeValueAsString(dto.getOtherUrls()));
            extension.setTags(objectMapper.writeValueAsString(dto.getTags()));
            extension.setSimilarQuestions(objectMapper.writeValueAsString(dto.getSimilarQuestions()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON fields", e);
        }

        extension.setKeyInsights(dto.getKeyInsights());
        extension.setTimeComplexity(dto.getTimeComplexity());
        extension.setSpaceComplexity(dto.getSpaceComplexity());

        QuestionExtension saved = extensionRepository.save(extension);
        return convertToDTO(saved);
    }

    // 获取题目扩展信息
    public QuestionExtensionDTO getExtension(Long questionId) {
        return extensionRepository.findByQuestionId(questionId)
            .map(this::convertToDTO)
            .orElse(null);
    }

    // DTO转换（JSON反序列化）
    private QuestionExtensionDTO convertToDTO(QuestionExtension extension) {
        QuestionExtensionDTO dto = new QuestionExtensionDTO();
        dto.setId(extension.getId());
        dto.setQuestionId(extension.getQuestionId());
        dto.setLeetcodeNumber(extension.getLeetcodeNumber());
        dto.setLeetcodeUrl(extension.getLeetcodeUrl());
        dto.setLabuladongUrl(extension.getLabuladongUrl());
        dto.setHelloInterviewUrl(extension.getHelloInterviewUrl());

        // JSON字段反序列化
        try {
            dto.setOtherUrls(objectMapper.readValue(
                extension.getOtherUrls(),
                new TypeReference<List<String>>() {}
            ));
            dto.setTags(objectMapper.readValue(
                extension.getTags(),
                new TypeReference<List<String>>() {}
            ));
            dto.setSimilarQuestions(objectMapper.readValue(
                extension.getSimilarQuestions(),
                new TypeReference<List<Integer>>() {}
            ));
        } catch (JsonProcessingException e) {
            // 如果JSON解析失败，返回空列表
            dto.setOtherUrls(Collections.emptyList());
            dto.setTags(Collections.emptyList());
            dto.setSimilarQuestions(Collections.emptyList());
        }

        dto.setKeyInsights(extension.getKeyInsights());
        dto.setTimeComplexity(extension.getTimeComplexity());
        dto.setSpaceComplexity(extension.getSpaceComplexity());

        return dto;
    }
}
```

### 5.4 AlgorithmTemplateService

```java
@Service
public class AlgorithmTemplateService {

    @Autowired
    private AlgorithmTemplateRepository templateRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 创建算法模板
    @Transactional
    public AlgorithmTemplateDTO createTemplate(AlgorithmTemplateCreateDTO dto) {
        AlgorithmTemplate template = new AlgorithmTemplate();
        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        template.setTemplateCode(dto.getTemplateCode());
        template.setNotes(dto.getNotes());
        template.setTimeComplexity(dto.getTimeComplexity());
        template.setSpaceComplexity(dto.getSpaceComplexity());

        // 序列化relatedQuestions
        try {
            template.setRelatedQuestions(objectMapper.writeValueAsString(dto.getRelatedQuestions()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize relatedQuestions", e);
        }

        AlgorithmTemplate saved = templateRepository.save(template);
        return convertToDTO(saved);
    }

    // 获取所有算法模板
    public List<AlgorithmTemplateDTO> getAllTemplates() {
        List<AlgorithmTemplate> templates = templateRepository.findAllByOrderByNameAsc();
        return templates.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}
```

---

## 6. 关键技术实现

### 6.1 JSON字段处理

**问题**: MySQL的JSON类型需要在Java中进行序列化/反序列化。

**方案**: 使用Jackson的`ObjectMapper`处理。

**实体类定义**:
```java
@Entity
public class QuestionExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "JSON")
    private String otherUrls;  // 存储为JSON字符串

    @Column(columnDefinition = "JSON")
    private String tags;

    @Column(columnDefinition = "JSON")
    private String similarQuestions;

    // getters and setters
}
```

**Service层处理**:
```java
// 保存时：List → JSON字符串
List<String> tags = Arrays.asList("数组", "双指针");
String tagsJson = objectMapper.writeValueAsString(tags);  // ["数组","双指针"]
extension.setTags(tagsJson);

// 查询时：JSON字符串 → List
String tagsJson = extension.getTags();
List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
```

### 6.2 拖拽排序实现

**前端使用`vuedraggable`**:
```vue
<template>
  <draggable
    v-model="items"
    @end="onDragEnd"
    item-key="id"
  >
    <template #item="{ element }">
      <div>{{ element.name }}</div>
    </template>
  </draggable>
</template>

<script>
import draggable from 'vuedraggable'

export default {
  components: { draggable },
  data() {
    return {
      items: []
    }
  },
  methods: {
    async onDragEnd() {
      // 生成新的display_order数组
      const orders = this.items.map((item, index) => ({
        id: item.id,
        displayOrder: index
      }))

      // 批量更新后端
      await api.updateOrder(orders)
    }
  }
}
</script>
```

**后端批量更新**:
```java
@Transactional
public void updateOrder(List<OrderDTO> orders) {
    for (OrderDTO order : orders) {
        repository.updateDisplayOrder(order.getId(), order.getDisplayOrder());
    }
}
```

**Repository自定义查询**:
```java
@Modifying
@Query("UPDATE FocusAreaLearningResource r SET r.displayOrder = :displayOrder WHERE r.id = :id")
void updateDisplayOrder(@Param("id") Long id, @Param("displayOrder") Integer displayOrder);
```

### 6.3 Markdown渲染

**前端使用`marked`库**:
```bash
npm install marked highlight.js
```

**MarkdownRenderer组件**:
```vue
<template>
  <div class="markdown-content" v-html="renderedHtml"></div>
</template>

<script setup>
import { computed } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const props = defineProps({
  content: String
})

// 配置marked使用highlight.js
marked.setOptions({
  highlight: (code, lang) => {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  }
})

const renderedHtml = computed(() => {
  return marked(props.content || '')
})
</script>

<style>
.markdown-content {
  /* GitHub-style markdown */
  line-height: 1.6;
}
.markdown-content pre {
  background: #f6f8fa;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
}
.markdown-content code {
  font-family: 'Monaco', 'Courier New', monospace;
}
</style>
```

### 6.4 UPSERT逻辑实现

**场景**: 题目扩展信息可能存在或不存在，需要统一的保存接口。

**实现**:
```java
@Transactional
public QuestionExtensionDTO saveExtension(Long questionId, QuestionExtensionDTO dto) {
    // 查询是否已存在
    QuestionExtension extension = extensionRepository
        .findByQuestionId(questionId)
        .orElse(new QuestionExtension());  // 不存在则创建新对象

    // 设置字段（新建和更新通用逻辑）
    extension.setQuestionId(questionId);
    extension.setLeetcodeNumber(dto.getLeetcodeNumber());
    // ... 其他字段

    // 保存（JPA自动判断是INSERT还是UPDATE）
    QuestionExtension saved = extensionRepository.save(extension);
    return convertToDTO(saved);
}
```

**关键点**:
- 使用`Optional.orElse()`优雅处理存在/不存在情况
- JPA的`save()`方法自动判断是新增还是更新（根据`id`字段）
- 前端无需区分创建和更新，统一调用同一个API

---

## 7. 安全性设计

### 7.1 权限控制

| 操作 | 权限要求 | 实现方式 |
|-----|---------|---------|
| 管理大分类关联 | 管理员 | `@PreAuthorize("hasRole('ADMIN')")` |
| 添加学习资料 | 管理员 | Spring Security拦截器 |
| 编辑题目扩展信息 | 管理员 | Controller方法级权限注解 |
| 管理算法模板 | 管理员 | 同上 |
| 浏览学习内容 | 登录用户 | JWT token验证 |
| 添加个人笔记 | 登录用户 | 复用Phase 3逻辑 |

**Controller权限注解示例**:
```java
@RestController
@RequestMapping("/api/admin/algorithm")
@PreAuthorize("hasRole('ADMIN')")  // 类级别：所有方法需要管理员权限
public class AdminAlgorithmController {

    @PostMapping("/resources/{id}")
    public ResponseEntity<LearningResourceDTO> createResource(...) {
        // 只有管理员可访问
    }
}
```

### 7.2 输入验证

**JSON字段验证**:
```java
public class QuestionExtensionDTO {
    @NotNull(message = "Question ID is required")
    private Long questionId;

    @Size(max = 500, message = "LeetCode URL too long")
    private String leetcodeUrl;

    @Valid  // 嵌套验证
    private List<@NotBlank String> tags;

    @Pattern(regexp = "O\\(.*\\)", message = "Invalid time complexity format")
    private String timeComplexity;
}
```

**Markdown内容过滤**:
```java
// 使用jsoup防止XSS攻击
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public String sanitizeMarkdown(String markdown) {
    // 允许Markdown常用标签，禁止<script>等危险标签
    return Jsoup.clean(markdown, Safelist.relaxed());
}
```

---

## 8. 测试策略

### 8.1 后端单元测试

**QuestionExtensionServiceTest.java**:
```java
@SpringBootTest
class QuestionExtensionServiceTest {

    @Autowired
    private QuestionExtensionService extensionService;

    @Autowired
    private QuestionExtensionRepository extensionRepository;

    @Test
    @Transactional
    void testUpsertLogic_Create() {
        // Arrange
        Long questionId = 1L;
        QuestionExtensionDTO dto = new QuestionExtensionDTO();
        dto.setLeetcodeNumber(26);
        dto.setTags(Arrays.asList("数组", "双指针"));

        // Act
        QuestionExtensionDTO saved = extensionService.saveExtension(questionId, dto);

        // Assert
        assertNotNull(saved.getId());
        assertEquals(26, saved.getLeetcodeNumber());
        assertEquals(2, saved.getTags().size());
    }

    @Test
    @Transactional
    void testUpsertLogic_Update() {
        // Arrange - 先创建
        Long questionId = 1L;
        QuestionExtensionDTO dto1 = new QuestionExtensionDTO();
        dto1.setLeetcodeNumber(26);
        extensionService.saveExtension(questionId, dto1);

        // Act - 更新同一个题目
        QuestionExtensionDTO dto2 = new QuestionExtensionDTO();
        dto2.setLeetcodeNumber(27);  // 修改题号
        extensionService.saveExtension(questionId, dto2);

        // Assert - 应该只有一条记录
        long count = extensionRepository.count();
        assertEquals(1, count);

        QuestionExtension updated = extensionRepository.findByQuestionId(questionId).get();
        assertEquals(27, updated.getLeetcodeNumber());
    }
}
```

### 8.2 前端组件测试

**QuestionDetailModal.test.js**:
```javascript
import { mount } from '@vue/test-utils'
import QuestionDetailModal from '@/components/QuestionDetailModal.vue'

describe('QuestionDetailModal', () => {
  it('displays question extension info', async () => {
    const question = {
      id: 1,
      title: '删除有序数组中的重复项',
      difficulty: 'EASY',
      extension: {
        leetcodeNumber: 26,
        leetcodeUrl: 'https://leetcode.com/problems/...',
        tags: ['数组', '双指针'],
        keyInsights: '## 快慢指针法\n\n...'
      }
    }

    const wrapper = mount(QuestionDetailModal, {
      props: { questionId: 1 }
    })

    // Mock API response
    await wrapper.vm.$nextTick()

    expect(wrapper.text()).toContain('[26]')
    expect(wrapper.text()).toContain('数组')
    expect(wrapper.find('a[href*="leetcode"]').exists()).toBe(true)
  })
})
```

---

## 9. 部署配置

### 9.1 Flyway迁移文件

**V6__create_algorithm_tables.sql**:
```sql
-- Phase 4: 编程与数据结构学习模块

-- 1. 大分类表
CREATE TABLE major_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '大分类名称',
    display_order INT NOT NULL COMMENT '显示顺序',
    description TEXT COMMENT '分类描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='编程与数据结构大分类';

-- 初始化6个大分类
INSERT INTO major_categories (name, display_order, description) VALUES
('基础篇', 1, '算法基础知识和编程思维'),
('核心刷题框架', 2, '双指针、滑动窗口、二分查找等核心技巧'),
('经典数据结构算法', 3, '数组、链表、二叉树、图等数据结构'),
('经典暴力搜索算法', 4, 'DFS、BFS、回溯等搜索算法'),
('经典动态规划算法', 5, '动态规划的各种经典问题'),
('其他算法', 6, '数学技巧、位运算等其他算法');

-- 2. 大分类与Focus Area关联表
CREATE TABLE major_category_focus_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    major_category_id BIGINT NOT NULL COMMENT '大分类ID',
    focus_area_id BIGINT NOT NULL COMMENT 'Focus Area ID',
    display_order INT NOT NULL DEFAULT 0 COMMENT '在该大分类下的显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (major_category_id) REFERENCES major_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
    UNIQUE KEY uk_category_focus (major_category_id, focus_area_id),
    INDEX idx_major_category (major_category_id, display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大分类与Focus Area关联表';

-- 3. Focus Area学习资料表
CREATE TABLE focus_area_learning_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    focus_area_id BIGINT NOT NULL COMMENT 'Focus Area ID',
    title VARCHAR(255) NOT NULL COMMENT '资料标题',
    resource_type ENUM('ARTICLE', 'VIDEO', 'DOCUMENT', 'OTHER') NOT NULL COMMENT '资料类型',
    url VARCHAR(500) COMMENT '资料链接',
    author VARCHAR(100) COMMENT '作者/来源',
    description TEXT COMMENT '资料简介',
    display_order INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
    INDEX idx_focus_area (focus_area_id, display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Focus Area学习资料';

-- 4. 题目扩展信息表
CREATE TABLE question_extensions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL COMMENT '题目ID',
    leetcode_number INT COMMENT 'LeetCode题号',
    leetcode_url VARCHAR(500) COMMENT 'LeetCode链接',
    labuladong_url VARCHAR(500) COMMENT 'labuladong链接',
    hello_interview_url VARCHAR(500) COMMENT 'Hello Interview链接',
    other_urls JSON COMMENT '其他相关链接数组',
    tags JSON COMMENT '标签数组',
    similar_questions JSON COMMENT '类似题目ID数组',
    key_insights TEXT COMMENT '关键思路 (Markdown格式)',
    time_complexity VARCHAR(50) COMMENT '时间复杂度',
    space_complexity VARCHAR(50) COMMENT '空间复杂度',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    UNIQUE KEY uk_question (question_id),
    INDEX idx_leetcode_number (leetcode_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目扩展信息';

-- 5. 算法模板表
CREATE TABLE algorithm_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '算法名称',
    description TEXT COMMENT '算法简介',
    template_code TEXT NOT NULL COMMENT '模板代码 (Markdown格式)',
    notes TEXT COMMENT '注意事项 (Markdown格式)',
    time_complexity VARCHAR(50) COMMENT '时间复杂度',
    space_complexity VARCHAR(50) COMMENT '空间复杂度',
    related_questions JSON COMMENT '相关题目ID数组',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常用算法模板';
```

### 9.2 初始化数据脚本

**init_algorithm_data.sql** (可选，用于开发环境快速初始化):
```sql
-- 示例：为"编程与数据结构"技能创建Focus Area
INSERT INTO focus_areas (skill_id, name, description) VALUES
((SELECT id FROM skills WHERE name = '编程与数据结构'), '双指针', '双指针技巧总结'),
((SELECT id FROM skills WHERE name = '编程与数据结构'), '滑动窗口', '滑动窗口算法'),
((SELECT id FROM skills WHERE name = '编程与数据结构'), '二分查找', '二分查找及其变种');

-- 将Focus Area关联到大分类
INSERT INTO major_category_focus_areas (major_category_id, focus_area_id, display_order) VALUES
((SELECT id FROM major_categories WHERE name = '核心刷题框架'), (SELECT id FROM focus_areas WHERE name = '双指针'), 0),
((SELECT id FROM major_categories WHERE name = '核心刷题框架'), (SELECT id FROM focus_areas WHERE name = '滑动窗口'), 1),
((SELECT id FROM major_categories WHERE name = '核心刷题框架'), (SELECT id FROM focus_areas WHERE name = '二分查找'), 2);

-- 示例：添加学习资料
INSERT INTO focus_area_learning_resources (focus_area_id, title, resource_type, url, author, description, display_order) VALUES
((SELECT id FROM focus_areas WHERE name = '双指针'), '双指针技巧总结', 'ARTICLE', 'https://labuladong.online/algo/essential-technique/array-two-pointers-summary/', 'labuladong', '从双指针的经典场景入手，讲解快慢指针和左右指针', 0);

-- 示例：添加算法模板
INSERT INTO algorithm_templates (name, description, template_code, notes, time_complexity, space_complexity, related_questions) VALUES
('二分查找', '在有序数组中查找目标值的经典算法',
'```java\npublic int binarySearch(int[] nums, int target) {\n    int left = 0, right = nums.length - 1;\n    while (left <= right) {\n        int mid = left + (right - left) / 2;\n        if (nums[mid] == target) return mid;\n        else if (nums[mid] < target) left = mid + 1;\n        else right = mid - 1;\n    }\n    return -1;\n}\n```',
'- 数组必须有序\n- 注意边界条件 (left <= right)\n- 防止溢出: mid = left + (right - left) / 2',
'O(log n)', 'O(1)', '[704, 35, 33]');
```

---

## 10. 已知限制和未来优化

### 10.1 当前限制

1. **大分类固定为6个** - 硬编码在迁移文件中，无法动态增删
2. **算法模板无分类** - 可能导致算法列表过长时查找不便
3. **题目完成状态未实现** - 学习进度跟踪留待未来Phase
4. **学习资料不支持离线附件** - 只能存储URL链接

### 10.2 未来优化方向

#### 10.2.1 学习进度跟踪 (Phase 5)

**需求**:
- 记录用户完成的题目
- 显示每个Focus Area的完成进度
- 提供学习路径推荐

**数据模型**:
```sql
CREATE TABLE user_question_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    status ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'NOT_STARTED',
    last_practiced_at TIMESTAMP,
    completed_at TIMESTAMP,
    practice_count INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_question (user_id, question_id)
);
```

#### 10.2.2 智能推荐系统

**功能**:
- 根据用户已完成题目推荐类似题目
- 基于标签和难度推荐学习路径

**实现思路**:
- 使用`similar_questions`字段构建题目关系图
- 基于`tags`进行协同过滤
- 结合`difficulty`梯度推荐

#### 10.2.3 学习资料离线存储

**需求**: 支持上传PDF、视频等离线资料

**实现方案**:
- 新增`file_path`字段存储本地文件路径
- 使用MinIO或OSS存储大文件
- 前端支持文件上传和预览

---

## 11. 实施计划

### 11.1 实施阶段

**Phase 4.1 - 后端实现** (预计3小时):
1. 创建Flyway迁移文件V6
2. 实现5个实体类（Entity）
3. 实现5个Repository接口
4. 实现4个Service类
5. 实现2个Controller（Admin + User）
6. 单元测试覆盖

**Phase 4.2 - 前端实现** (预计4小时):
1. 创建API客户端（axios）
2. 实现4个页面组件
3. 实现5个可复用组件
4. 配置路由和导航菜单
5. 集成Markdown渲染和拖拽排序

**Phase 4.3 - 数据导入** (预计1小时):
1. 准备初始数据脚本
2. 导入示例学习资料
3. 导入示例算法模板
4. 为部分题目添加扩展信息

**Phase 4.4 - 测试和优化** (预计1小时):
1. 端到端测试
2. UI/UX优化
3. 性能调优
4. 文档更新

**总预计时间**: 9小时

### 11.2 验收标准

- ✅ 管理员可以为6个大分类配置Focus Area关联
- ✅ 管理员可以添加学习资料并排序
- ✅ 管理员可以为题目添加扩展信息（链接、思路、标签）
- ✅ 管理员可以创建常用算法模板
- ✅ 用户可以浏览6个大分类的学习内容
- ✅ 用户可以点击题目查看详情（弹窗显示）
- ✅ 用户可以添加个人笔记（复用Phase 3组件）
- ✅ 用户可以查阅常用算法模板
- ✅ 所有Markdown内容正确渲染
- ✅ 拖拽排序功能正常

---

**文档版本**: v1.0
**创建时间**: 2025-12-21
**状态**: 设计阶段 🟡
**下一步**: 用户Review后，开始实施
