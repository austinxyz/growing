# 面试准备笔记 TODO 功能设计

## 1. 功能概述

在求职申请列表页面，用户可以为每个职位的面试阶段创建准备笔记（TODO list），帮助用户系统化地准备面试。

### 1.1 用户故事

> 作为求职者，我希望能够为每个面试阶段创建详细的准备清单，记录需要完成的事项，包括学习材料、刷题记录、项目经验回顾等，并能跟踪完成进度和时间，以便系统化地准备面试。

### 1.2 核心功能

1. **TODO项管理**: 创建、编辑、删除、标记完成
2. **关联准备清单**: TODO项可以关联到面试阶段的准备清单
3. **资源链接**: 支持添加学习材料、刷题链接、相关资料
4. **完成跟踪**: 标记完成状态，记录完成时间
5. **进度展示**: 显示整体准备进度

## 2. 数据模型设计

### 2.1 interview_preparation_todos 表

```sql
CREATE TABLE `interview_preparation_todos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_stage_id` bigint NOT NULL COMMENT '面试阶段ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',

  -- TODO基本信息
  `title` varchar(500) NOT NULL COMMENT 'TODO标题',
  `description` text COMMENT 'TODO描述（Markdown）',
  `todo_type` varchar(50) NOT NULL DEFAULT 'General' COMMENT 'TODO类型：General, StudyMaterial, Practice, ProjectReview, Checklist',
  `priority` int DEFAULT 0 COMMENT '优先级（0-5，数字越大优先级越高）',
  `order_index` int NOT NULL DEFAULT 0 COMMENT '排序索引',

  -- 关联资源
  `checklist_item_id` bigint DEFAULT NULL COMMENT '关联的准备清单项ID（如果有）',
  `resource_links` json DEFAULT NULL COMMENT '资源链接（JSON数组：[{title, url, type}]）',

  -- 完成状态
  `is_completed` boolean NOT NULL DEFAULT false COMMENT '是否完成',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `completion_notes` text COMMENT '完成备注（如：刷了哪些题、学了什么）',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_stage` (`interview_stage_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_stage_order` (`interview_stage_id`, `order_index`),
  KEY `idx_checklist` (`checklist_item_id`),
  CONSTRAINT `interview_preparation_todos_ibfk_1` FOREIGN KEY (`interview_stage_id`) REFERENCES `interview_stages` (`id`) ON DELETE CASCADE,
  CONSTRAINT `interview_preparation_todos_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `interview_preparation_todos_ibfk_3` FOREIGN KEY (`checklist_item_id`) REFERENCES `interview_preparation_checklists` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试准备TODO清单';
```

### 2.2 interview_preparation_checklists 表（新增）

用于存储准备清单模板项（面试阶段的标准准备项）

```sql
CREATE TABLE `interview_preparation_checklists` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_stage_id` bigint NOT NULL COMMENT '面试阶段ID',

  `item_title` varchar(500) NOT NULL COMMENT '清单项标题',
  `item_description` text COMMENT '清单项描述（Markdown）',
  `item_type` varchar(50) NOT NULL COMMENT '类型：SkillReview, PracticeQuestions, ProjectReview, CompanyResearch, Other',
  `order_index` int NOT NULL DEFAULT 0 COMMENT '排序索引',

  -- 关联资源
  `skill_ids` json DEFAULT NULL COMMENT '关联技能ID（JSON数组）',
  `question_ids` json DEFAULT NULL COMMENT '关联试题ID（JSON数组）',
  `project_ids` json DEFAULT NULL COMMENT '关联项目ID（JSON数组）',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_stage` (`interview_stage_id`),
  KEY `idx_stage_order` (`interview_stage_id`, `order_index`),
  CONSTRAINT `interview_preparation_checklists_ibfk_1` FOREIGN KEY (`interview_stage_id`) REFERENCES `interview_stages` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试准备清单模板';
```

### 2.3 字段说明

**interview_preparation_todos**:
- `todo_type`: TODO类型
  - `General`: 一般任务（文字描述）
  - `StudyMaterial`: 学习材料（看了什么资料）
  - `Practice`: 练习刷题（刷了什么题）
  - `ProjectReview`: 项目经验回顾
  - `Checklist`: 关联到准备清单项

- `resource_links`: JSON格式示例
  ```json
  [
    {"title": "LeetCode 200", "url": "https://leetcode.com/problems/number-of-islands", "type": "practice"},
    {"title": "System Design Primer", "url": "https://github.com/donnemartin/system-design-primer", "type": "study"}
  ]
  ```

## 3. API设计

### 3.1 PreprationChecklistController

**获取面试阶段的准备清单**
- `GET /api/interview-stages/{stageId}/preparation-checklists`
- 返回: `List<PreparationChecklistDTO>`

**创建准备清单项**
- `POST /api/interview-stages/{stageId}/preparation-checklists`
- 请求体: `PreparationChecklistDTO`
- 返回: `PreparationChecklistDTO`

**更新准备清单项**
- `PUT /api/preparation-checklists/{id}`
- 请求体: `PreparationChecklistDTO`
- 返回: `PreparationChecklistDTO`

**删除准备清单项**
- `DELETE /api/preparation-checklists/{id}`
- 返回: `void`

### 3.2 PreparationTodoController

**获取面试阶段的TODO列表**
- `GET /api/interview-stages/{stageId}/todos`
- 返回: `List<PreparationTodoDTO>`

**创建TODO**
- `POST /api/interview-stages/{stageId}/todos`
- 请求体: `PreparationTodoDTO`
- 返回: `PreparationTodoDTO`

**更新TODO**
- `PUT /api/todos/{id}`
- 请求体: `PreparationTodoDTO`
- 返回: `PreparationTodoDTO`

**标记TODO完成/未完成**
- `PATCH /api/todos/{id}/complete`
- 请求体: `{"isCompleted": true, "completionNotes": "..."}`
- 返回: `PreparationTodoDTO`

**删除TODO**
- `DELETE /api/todos/{id}`
- 返回: `void`

**批量更新TODO顺序**
- `PATCH /api/interview-stages/{stageId}/todos/reorder`
- 请求体: `[{id: 1, orderIndex: 0}, {id: 2, orderIndex: 1}]`
- 返回: `void`

## 4. 前端界面设计

### 4.1 页面入口

在"求职申请列表页面"，点击某个职位申请，进入职位详情页面，在"面试流程"标签页中：

```
职位详情
├── 概览
├── 简历
├── 面试流程 ← 这里
│   ├── [阶段1: Phone Screen]
│   │   ├── 准备清单（标准模板）
│   │   └── 我的准备笔记（TODO list） ← 新增
│   ├── [阶段2: Technical Interview]
│   └── [阶段3: Onsite]
└── 记录
```

### 4.2 界面布局

```
┌─────────────────────────────────────────────────┐
│ 面试阶段: Phone Screen                           │
├─────────────────────────────────────────────────┤
│ 准备清单（标准模板）                              │
│ □ 复习Java并发编程                                │
│ □ 练习LeetCode Medium难度题                       │
│ □ 准备项目经验：支付系统项目                       │
├─────────────────────────────────────────────────┤
│ 我的准备笔记                          [+ 新建TODO] │
│                                                   │
│ 进度: ████░░░░░░ 40% (2/5 完成)                  │
│                                                   │
│ ┌───────────────────────────────────────────┐   │
│ │ ✓ [高] 复习Java并发知识                    │   │
│ │     📚 学习材料: Java并发编程实战          │   │
│ │     🔗 https://...                        │   │
│ │     完成时间: 2026-01-10 15:30            │   │
│ │     备注: 复习了线程池、锁优化            │   │
│ └───────────────────────────────────────────┘   │
│                                                   │
│ ┌───────────────────────────────────────────┐   │
│ │ ✓ [中] 刷题：LeetCode 200, 146, 23         │   │
│ │     🏆 练习题目:                           │   │
│ │     - Number of Islands (Medium) ✓        │   │
│ │     - LRU Cache (Medium) ✓                │   │
│ │     - Merge K Sorted Lists (Hard) ✓       │   │
│ │     完成时间: 2026-01-11 20:00            │   │
│ └───────────────────────────────────────────┘   │
│                                                   │
│ ┌───────────────────────────────────────────┐   │
│ │ □ [高] 准备支付系统项目讲解                │   │
│ │     📁 项目经验: 微信支付系统             │   │
│ │     📝 准备STAR回答：                     │   │
│ │        - Situation: ...                   │   │
│ │        - Task: ...                        │   │
│ └───────────────────────────────────────────┘   │
│                                                   │
│ ┌───────────────────────────────────────────┐   │
│ │ □ [中] 了解公司产品和技术栈                │   │
│ │     🔗 https://company.com/tech-blog      │   │
│ └───────────────────────────────────────────┘   │
│                                                   │
│ ┌───────────────────────────────────────────┐   │
│ │ □ [低] 准备行为面试问题                    │   │
│ │     ☑ 关联清单项: 准备Behavioral问题      │   │
│ └───────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

### 4.3 TODO卡片组件

每个TODO卡片包含：
- 复选框（完成状态）
- 优先级标签（高/中/低）
- 标题
- 描述（Markdown）
- 类型图标（学习材料/练习/项目回顾等）
- 资源链接列表
- 完成时间（如果已完成）
- 完成备注（如果已完成）
- 操作按钮（编辑、删除）

### 4.4 创建/编辑TODO对话框

```
┌─────────────────────────────────────────┐
│ 创建准备笔记                             │
├─────────────────────────────────────────┤
│ TODO类型: [下拉选择]                     │
│   ○ 一般任务                             │
│   ○ 学习材料                             │
│   ● 练习刷题                             │
│   ○ 项目回顾                             │
│   ○ 关联准备清单                         │
│                                          │
│ 优先级: [滑块] 低 ━━●━━━ 高              │
│                                          │
│ 标题*: [输入框]                          │
│ 刷LeetCode Medium难度题                  │
│                                          │
│ 描述: [Markdown编辑器]                   │
│ - LeetCode 200: Number of Islands        │
│ - LeetCode 146: LRU Cache                │
│                                          │
│ 资源链接: [+ 添加链接]                   │
│ 📌 LeetCode 200                          │
│    https://leetcode.com/problems/...     │
│    [删除]                                │
│                                          │
│         [取消]  [保存]                   │
└─────────────────────────────────────────┘
```

## 5. 实施计划

### 5.1 数据库迁移
- [ ] 创建 `interview_preparation_checklists` 表
- [ ] 创建 `interview_preparation_todos` 表

### 5.2 后端开发
- [ ] PreparationChecklist Entity/DTO/Repository
- [ ] PreparationTodo Entity/DTO/Repository
- [ ] PreparationChecklistService
- [ ] PreparationTodoService
- [ ] PreparationChecklistController
- [ ] PreparationTodoController

### 5.3 前端开发
- [ ] preparationChecklistApi.js
- [ ] preparationTodoApi.js
- [ ] TodoList.vue（TODO列表组件）
- [ ] TodoItem.vue（TODO卡片组件）
- [ ] TodoForm.vue（创建/编辑对话框）
- [ ] ChecklistManagement.vue（准备清单管理）
- [ ] 集成到面试流程页面

## 6. 验收标准

- [ ] 可为每个面试阶段创建准备清单模板
- [ ] 可创建、编辑、删除TODO项
- [ ] TODO可标记完成/未完成，记录完成时间
- [ ] TODO可添加资源链接（学习材料、刷题链接）
- [ ] TODO可关联到准备清单项
- [ ] TODO可设置优先级
- [ ] TODO列表可拖拽排序
- [ ] 显示整体准备进度（完成百分比）
- [ ] 完成的TODO显示完成时间和备注
- [ ] 支持Markdown格式描述
