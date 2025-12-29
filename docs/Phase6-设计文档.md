# Phase 6 设计文档 - 通用技能学习模块

> **架构版本**: v1.1
> **状态**: 🚧 部分完成 - Phase 6.1-6.2 已实现
> **创建时间**: 2025-12-27
> **最后更新**: 2025-12-28 (v1.1 - 根据实际实现更新)
> **需求文档**: [Phase6-详细需求.md](../requirement/Phase6-详细需求.md)
> **参考模块**: Phase 4 算法学习模块、Phase 5 系统设计模块

---

## 1. 总体架构

### 1.1 设计理念

Phase 6 实现**双轨制学习系统**:
- **学习模式** - 系统化学习新知识 (学习资料 + 试题库双Tab)
- **答题模式** - 专项训练和面试准备 (题库刷题 + 模版支持)

### 1.2 核心特性

✅ **复用Phase 4三层架构** - Skill → Major Category → Focus Area
✅ **第二类技能简化** - 隐藏"General"大分类,呈现两层结构
✅ **AI学习笔记** - 使用user_id=-1标识,对所有用户可见
✅ **答题模版系统** - 支持STAR、Technical等多种模版
✅ **知识点关联** - 答题笔记可关联AI知识点或用户知识点
✅ **双Tab学习模式** - 学习资料 + 试题库融合

### 1.3 架构分层

```
┌──────────────────────────────────────────────────────────────┐
│                      前端 (Vue 3)                             │
├──────────────────────────────────────────────────────────────┤
│  管理端                        │  用户端                      │
│  - 职业技能库管理(复用+扩展)    │  - 学习页面(双Tab)           │
│    * 上下两栏树形结构          │    * 学习资料Tab             │
│    * 支持第一类/第二类技能     │    * 试题库Tab               │
│    * AI笔记导入功能            │  - 答题模式页面              │
│  - 答题模版管理(新增)          │    * STAR框架输入            │
│    * 模版CRUD                 │    * 技术类输入              │
│    * Skill关联管理            │    * 知识点关联              │
└──────────────────────────────────────────────────────────────┘
                           ↕ RESTful API
┌──────────────────────────────────────────────────────────────┐
│                   后端 (Spring Boot)                          │
├──────────────────────────────────────────────────────────────┤
│  Controller层                                                 │
│  - AnswerTemplateController (模版管理API)                    │
│  - SkillTemplateController (Skill-模版关联API)               │
│  - LearningContentController (学习资料API,扩展AI笔记)        │
│  - QuestionController (试题API,扩展知识点关联)               │
├──────────────────────────────────────────────────────────────┤
│  Service层                                                    │
│  - AnswerTemplateService (模版CRUD)                          │
│  - SkillTemplateService (模版关联管理)                       │
│  - LearningContentService (学习资料管理,AI笔记导入)          │
│  - QuestionService (试题管理)                                │
│  - UserQuestionNoteService (答题笔记,支持模版渲染)           │
├──────────────────────────────────────────────────────────────┤
│  Repository层 (Spring Data JPA)                               │
│  - AnswerTemplateRepository                                   │
│  - SkillTemplateRepository                                    │
│  - UserLearningContentNoteRepository (支持user_id=-1查询)    │
│  - UserLearningContentKnowledgePointRepository               │
│  - UserQuestionNoteRepository                                 │
└──────────────────────────────────────────────────────────────┘
                           ↕
┌──────────────────────────────────────────────────────────────┐
│                   数据库 (MySQL 8.0)                          │
│  新增:                                                        │
│  - answer_templates (答题模版表)                             │
│  - skill_templates (Skill-模版关联表,多对多)                │
│  扩展:                                                        │
│  - user_question_notes (新增related_knowledge_point_ids)     │
│  复用:                                                        │
│  - skills, major_categories, focus_areas                     │
│  - learning_contents                                         │
│  - questions                                                 │
│  - user_learning_content_notes (支持user_id=-1)             │
│  - user_learning_content_knowledge_points (支持user_id=-1)  │
└──────────────────────────────────────────────────────────────┘
```

### 1.4 与现有架构的集成

**复用Phase 4的概念**:
- ✅ **Skill** - 云计算、Behavioral等技能
- ✅ **Major Category** - 大分类 (第二类技能使用"General"隐藏)
- ✅ **Focus Area** - 具体知识点
- ✅ **Learning Content** - 学习资料 (文章、视频)

**新增概念**:
- 🆕 **AnswerTemplate** - 答题模版 (STAR、Technical等)
- 🆕 **SkillTemplate** - Skill与模版的多对多关联
- 🆕 **AI笔记** - 使用user_id=-1标识的学习笔记和知识点

**架构优势**:
- 🎯 **复用性** - 最大化复用Phase 4的三层架构和UI组件
- 🎯 **灵活性** - 第二类技能可隐藏大分类层级,呈现两层结构
- 🎯 **扩展性** - 答题模版系统支持多种答题框架
- 🎯 **AI增强** - AI笔记对所有用户可见,提升学习效率

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
                 ┌───────────┴───────────┐
                 │                       │
                 │ 1:N                   │ N:M (skill_templates)
                 ↓                       ↓
    ┌─────────────────────┐    ┌──────────────────────────┐
    │ major_categories    │    │   answer_templates       │
    ├─────────────────────┤    ├──────────────────────────┤
    │ id (PK)             │    │ id (PK)                  │
    │ skill_id (FK)       │    │ template_name (UNIQUE)   │
    │ name                │    │ template_fields (JSON)   │
    │ (第二类技能:        │    │ description              │
    │  "General" 隐藏)    │    └──────────────────────────┘
    └─────────────────────┘             ↕
             │                          │ N:M
             │ 1:N                      │
             ↓                          ↓
    ┌─────────────────────┐    ┌──────────────────────────┐
    │   focus_areas       │    │   skill_templates        │
    ├─────────────────────┤    ├──────────────────────────┤
    │ id (PK)             │    │ skill_id (FK)            │
    │ skill_id (FK)       │    │ template_id (FK)         │
    │ major_category_id   │    │ is_default (BOOLEAN)     │
    │ name                │    │ PRIMARY KEY (skill, tpl) │
    └─────────────────────┘    └──────────────────────────┘
             │
             │ 1:N
             ↓
    ┌─────────────────────┐
    │ learning_contents   │
    ├─────────────────────┤
    │ id (PK)             │
    │ focus_area_id (FK)  │
    │ content_type        │
    │ title, url, author  │
    └─────────────────────┘
             │
             │ 1:N
             ↓
    ┌──────────────────────────────────────┐
    │ user_learning_content_notes          │
    ├──────────────────────────────────────┤
    │ id (PK)                              │
    │ learning_content_id (FK)             │
    │ user_id (FK) [支持-1标识AI笔记]      │
    │ note_content (TEXT)                  │
    │ UNIQUE(content_id, user_id)          │
    └──────────────────────────────────────┘

    learning_contents (续)
             │
             │ 1:N
             ↓
    ┌──────────────────────────────────────┐
    │ user_learning_content_knowledge_pts  │
    ├──────────────────────────────────────┤
    │ id (PK)                              │
    │ learning_content_id (FK)             │
    │ user_id (FK) [支持-1标识AI知识点]    │
    │ kp_title (VARCHAR)                   │
    │ kp_content (TEXT)                    │
    └──────────────────────────────────────┘

    focus_areas (续)
             │
             │ 1:N
             ↓
    ┌─────────────────────┐
    │     questions       │
    ├─────────────────────┤
    │ id (PK)             │
    │ focus_area_id (FK)  │
    │ question_type       │ (behavioral/technical/design/programming)
    │ question_text       │
    │ answer_requirement  │
    └─────────────────────┘
             │
             │ 1:N
             ↓
    ┌──────────────────────────────────────┐
    │  user_question_notes                 │
    ├──────────────────────────────────────┤
    │ id (PK)                              │
    │ question_id (FK)                     │
    │ user_id (FK)                         │
    │ note_content (TEXT)                  │ [用于STAR答题笔记]
    │ core_strategy (TEXT)                 │ [技术类核心思路]
    │ related_knowledge_point_ids (JSON)   │ [新增]
    │ UNIQUE(question_id, user_id)         │
    └──────────────────────────────────────┘
```

### 2.2 表结构详细设计

#### 表1: answer_templates (答题模版表 - 新增)

**用途**: 存储答题框架模版 (STAR、Technical等)

**DDL** (V17 migration):
```sql
CREATE TABLE answer_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- 模版信息
    template_name VARCHAR(50) UNIQUE NOT NULL COMMENT '模版名称 (STAR, Technical等)',
    template_fields JSON NOT NULL COMMENT '模版字段定义 (JSON数组)',
    description TEXT COMMENT '模版说明',

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_template_name (template_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='答题模版表';
```

**template_fields字段示例** (STAR模版):
```json
[
  {
    "key": "situation",
    "label": "Situation (情境)",
    "placeholder": "描述项目背景和挑战..."
  },
  {
    "key": "task",
    "label": "Task (任务)",
    "placeholder": "你的具体职责是什么..."
  },
  {
    "key": "action",
    "label": "Action (行动)",
    "placeholder": "你采取了哪些行动..."
  },
  {
    "key": "result",
    "label": "Result (结果)",
    "placeholder": "可量化的成果 (如提升X%)..."
  }
]
```

---

#### 表2: skill_templates (Skill-模版关联表 - 新增,多对多)

**用途**: 管理Skill与答题模版的多对多关联

**DDL** (V17 migration):
```sql
CREATE TABLE skill_templates (
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    template_id BIGINT NOT NULL COMMENT '模版ID',

    -- 关联信息
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否为该Skill的默认模版',

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (skill_id, template_id),
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES answer_templates(id) ON DELETE CASCADE,
    INDEX idx_skill_id (skill_id),
    INDEX idx_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Skill与答题模版关联表 (多对多)';
```

**说明**:
- 一个Skill可以关联多个模版 (默认模版 + 通用模版)
- `is_default`字段标记该Skill的默认模版
- 用户可选择使用默认模版或通用模版

---

#### 表3: user_question_notes (用户答题笔记表 - 扩展)

**用途**: 存储用户的答题笔记 (支持模版渲染和知识点关联)

**扩展DDL** (V17 migration):
```sql
-- 已有表结构,新增字段
ALTER TABLE user_question_notes
    ADD COLUMN related_knowledge_point_ids JSON COMMENT '关联的知识点ID列表 (JSON数组)' AFTER core_strategy;
```

**字段说明**:
- `note_content`: 用于存储STAR答题笔记或技术类详细回答
- `core_strategy`: 用于技术类核心思路 (Behavioral类也可使用)
- `related_knowledge_point_ids`: 关联的知识点ID列表 (可以是AI生成的或用户自己的)

**示例数据**:
```json
{
  "note_content": "**Situation (情境)**\n在eBay搜索团队,两位工程师对架构设计有分歧...\n\n**Task (任务)**\n作为Tech Lead,需要在2周内解决分歧并推进项目...\n\n**Action (行动)**\n1. 组织技术评审会议\n2. 建立决策框架\n3. 促进双方对话\n\n**Result (结果)**\n最终采纳混合方案,项目按时交付,团队满意度提升20%",
  "core_strategy": "冲突解决的关键在于建立客观评估标准",
  "related_knowledge_point_ids": [123, 456, 789]
}
```

---

#### 表4: user_learning_content_notes (学习笔记表 - 已有,支持AI笔记)

**用途**: 存储用户的学习笔记 (支持user_id=-1标识AI笔记)

**已有表结构** (Phase 2):
```sql
CREATE TABLE user_learning_content_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    learning_content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL COMMENT '用户ID (-1表示AI生成的笔记)',

    -- 笔记内容
    note_content TEXT COMMENT '笔记内容 (Markdown)',

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (learning_content_id) REFERENCES learning_contents(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_content (learning_content_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**AI笔记处理**:
- 使用特殊用户ID `user_id=-1` 表示AI生成的笔记
- 前端查询时需要同时查询用户笔记和AI笔记:
  ```sql
  SELECT * FROM user_learning_content_notes
  WHERE learning_content_id = ? AND user_id IN (?, -1)
  ORDER BY user_id DESC; -- AI笔记排在前面
  ```

---

#### 表5: user_learning_content_knowledge_points (知识点表 - 已有,支持AI知识点)

**用途**: 存储用户总结的知识点 (支持user_id=-1标识AI知识点)

**已有表结构** (Phase 2):
```sql
CREATE TABLE user_learning_content_knowledge_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    learning_content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL COMMENT '用户ID (-1表示AI生成的知识点)',

    -- 知识点内容
    kp_title VARCHAR(500) NOT NULL COMMENT '知识点标题',
    kp_content TEXT COMMENT '知识点内容 (Markdown)',

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (learning_content_id) REFERENCES learning_contents(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_learning_content_id (learning_content_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**AI知识点处理**:
- 使用特殊用户ID `user_id=-1` 表示AI生成的知识点
- 前端查询时需要同时查询用户知识点和AI知识点:
  ```sql
  SELECT * FROM user_learning_content_knowledge_points
  WHERE learning_content_id = ? AND user_id IN (?, -1)
  ORDER BY user_id DESC, created_at ASC;
  ```

---

### 2.3 数据关联规则

```
skills
  ↓ 1:N
major_categories (第二类技能创建"General"隐藏)
  ↓ 1:N
focus_areas
  ↓ 1:N
learning_contents
  ↓ 1:N
user_learning_content_notes (支持user_id=-1)
  ↓ 1:N
user_learning_content_knowledge_points (支持user_id=-1)

focus_areas
  ↓ 1:N
questions
  ↓ 1:N
user_question_notes (扩展related_knowledge_point_ids)

skills
  ↓ N:M (skill_templates)
answer_templates
```

**关键规则**:
- 第二类技能 (Behavioral等) 创建默认Major Category "General",前端隐藏
- AI笔记和AI知识点使用user_id=-1标识,对所有用户可见
- 每个Skill可以关联多个答题模版 (通过skill_templates表)
- 用户答题笔记可以关联AI知识点或用户自己的知识点 (related_knowledge_point_ids)

**级联删除规则**:
- 删除Skill → 级联删除Major Categories → 级联删除Focus Areas → 级联删除Learning Contents、Questions
- 删除AnswerTemplate → 级联删除skill_templates关联

---

## 3. API设计

### 3.1 管理端API

#### 3.1.1 答题模版管理 (AnswerTemplateController)

**GET /api/admin/answer-templates**
- 功能: 获取所有答题模版
- 权限: 管理员
- 返回: List<AnswerTemplateDTO>

**POST /api/admin/answer-templates**
- 功能: 创建答题模版
- 权限: 管理员
- Request Body: AnswerTemplateDTO
  ```json
  {
    "templateName": "STAR",
    "templateFields": [
      {"key": "situation", "label": "Situation (情境)", "placeholder": "..."},
      {"key": "task", "label": "Task (任务)", "placeholder": "..."},
      {"key": "action", "label": "Action (行动)", "placeholder": "..."},
      {"key": "result", "label": "Result (结果)", "placeholder": "..."}
    ],
    "description": "STAR答题框架"
  }
  ```

**PUT /api/admin/answer-templates/{id}**
- 功能: 更新答题模版
- 权限: 管理员
- Request Body: AnswerTemplateDTO

**DELETE /api/admin/answer-templates/{id}**
- 功能: 删除答题模版 (级联删除skill_templates关联)
- 权限: 管理员

#### 3.1.2 Skill-模版关联管理 (SkillTemplateController)

**GET /api/admin/skills/{skillId}/templates**
- 功能: 获取Skill关联的所有模版
- 权限: 管理员
- 返回: List<SkillTemplateDTO>

**POST /api/admin/skills/{skillId}/templates/{templateId}**
- 功能: 关联Skill与模版
- 权限: 管理员
- Request Param: isDefault (boolean)

**DELETE /api/admin/skills/{skillId}/templates/{templateId}**
- 功能: 取消Skill与模版的关联
- 权限: 管理员

**PUT /api/admin/skills/{skillId}/templates/{templateId}/set-default**
- 功能: 设置默认模版
- 权限: 管理员

#### 3.1.3 AI笔记管理 (LearningContentController扩展)

**POST /api/admin/learning-contents/{contentId}/ai-note**
- 功能: 为学习资料导入AI整体笔记
- 权限: 管理员
- Request Body:
  ```json
  {
    "noteContent": "AI生成的学习笔记 (Markdown)"
  }
  ```
- 实现: 创建user_id=-1的笔记记录

**POST /api/admin/learning-contents/{contentId}/ai-knowledge-points**
- 功能: 为学习资料导入AI知识点
- 权限: 管理员
- Request Body:
  ```json
  {
    "knowledgePoints": [
      {"kpTitle": "知识点1", "kpContent": "内容1"},
      {"kpTitle": "知识点2", "kpContent": "内容2"}
    ]
  }
  ```
- 实现: 创建user_id=-1的知识点记录

---

### 3.2 用户端API

#### 3.2.1 学习资料查看 (LearningContentController扩展)

**GET /api/learning-contents/{contentId}/notes**
- 功能: 获取学习资料的笔记 (包含AI笔记和用户笔记)
- 权限: 已登录用户
- 返回:
  ```json
  {
    "aiNote": {
      "noteContent": "AI生成的学习笔记"
    },
    "userNote": {
      "noteContent": "用户的学习笔记"
    }
  }
  ```

**GET /api/learning-contents/{contentId}/knowledge-points**
- 功能: 获取学习资料的知识点 (包含AI知识点和用户知识点)
- 权限: 已登录用户
- 返回:
  ```json
  {
    "aiKnowledgePoints": [
      {"id": 1, "kpTitle": "AI知识点1", "kpContent": "内容1"},
      {"id": 2, "kpTitle": "AI知识点2", "kpContent": "内容2"}
    ],
    "userKnowledgePoints": [
      {"id": 3, "kpTitle": "用户知识点1", "kpContent": "内容1"}
    ]
  }
  ```

#### 3.2.2 答题模版查看 (SkillTemplateController)

**GET /api/skills/{skillId}/answer-template**
- 功能: 获取Skill的默认答题模版
- 权限: 已登录用户
- 返回: AnswerTemplateDTO

**GET /api/answer-templates**
- 功能: 获取所有可用的答题模版
- 权限: 已登录用户
- 返回: List<AnswerTemplateDTO>

#### 3.2.3 答题笔记保存 (UserQuestionNoteController扩展)

**POST /api/questions/{questionId}/my-note**
- 功能: 保存/更新用户答题笔记 (支持知识点关联)
- 权限: 已登录用户
- Request Body:
  ```json
  {
    "noteContent": "STAR答题笔记或技术类详细回答",
    "coreStrategy": "核心思路",
    "relatedKnowledgePointIds": [123, 456, 789]
  }
  ```

**GET /api/questions/{questionId}/my-note**
- 功能: 获取用户答题笔记
- 权限: 已登录用户
- 返回: UserQuestionNoteDTO (包含relatedKnowledgePointIds)

---

## 4. 前端设计

### 4.1 管理端页面

#### 4.1.1 职业技能库管理页面 (扩展)

**页面**: `/admin/skill-management`
**组件**: SkillManagement.vue (扩展)

**组件结构**:
```vue
SkillManagement.vue
├─ 左侧 (上下两栏树形结构)
│   ├─ 上栏树: 职业路径 → 技能
│   └─ 下栏树:
│       ├─ 第一类技能: 大分类 → Focus Area
│       └─ 第二类技能: 直接显示Focus Area (隐藏大分类)
└─ 右侧 (双Tab布局)
    ├─ Tab 1: 学习内容
    │   ├─ Learning Content列表 (CRUD)
    │   └─ AI笔记导入按钮
    │       ├─ 导入AI整体笔记
    │       └─ 导入AI知识点列表
    └─ Tab 2: 试题库
        └─ Questions列表 (CRUD)
```

**关键实现**:
- 下栏树检测大分类名称是否为"General",是则隐藏大分类层级
- AI笔记导入功能: 调用`POST /api/admin/learning-contents/{contentId}/ai-note`
- AI知识点导入功能: 调用`POST /api/admin/learning-contents/{contentId}/ai-knowledge-points`

#### 4.1.2 技能模版库管理 (新增) ✅ 已实现

**路径**: `/admin/skill-templates` ✅ 路由已注册
**组件**: `SkillTemplateManagement.vue`

**组件结构**:
```vue
SkillTemplateManagement.vue (左右两栏布局)
├─ 左侧 30% - 职业路径与技能树
│   ├─ 职业路径节点 (可展开/折叠)
│   │   └─ 技能列表 (子节点)
│   │       ├─ 技能卡片 (选中状态高亮)
│   │       └─ 点击切换选中技能
│   └─ 自动展开所有职业路径
│
└─ 右侧 70% - 模版管理面板
    ├─ 未选择技能: 提示卡片
    ├─ 已选择技能: 显示模版管理
    │   ├─ 技能信息头部 (蓝色背景)
    │   │   ├─ 技能名称
    │   │   ├─ [新增模版] 按钮
    │   │   └─ [关联已有模版] 按钮
    │   │
    │   ├─ 模版卡片列表 (Grid布局,2列)
    │   │   ├─ 模版信息卡片
    │   │   │   ├─ 模版名称 + 默认标记 (绿色Badge)
    │   │   │   ├─ 模版描述
    │   │   │   ├─ 模版字段展示
    │   │   │   └─ 操作按钮组
    │   │   │       ├─ [编辑] - 打开TemplateEditorModal
    │   │   │       ├─ [设为默认] - 调用API设置默认
    │   │   │       └─ [取消关联] - 调用API删除关联
    │   │   │
    │   │   └─ 空状态提示 (无关联模版时)
    │   │
    │   └─ 加载状态 (Spinner)
```

**核心组件**:

**A. TemplateEditorModal.vue (模版编辑器)**
```vue
TemplateEditorModal
├─ 模式检测: 新增 or 编辑
├─ 基础信息输入
│   ├─ 模版名称 (required)
│   └─ 模版描述
├─ 动态字段编辑器
│   ├─ 字段卡片列表 (灰色背景)
│   │   ├─ key输入框 (字段键名,required)
│   │   ├─ label输入框 (显示标签,required)
│   │   ├─ placeholder输入框 (提示文本)
│   │   └─ [删除字段] 按钮 (红色)
│   └─ [+ 添加字段] 按钮
├─ 表单验证
│   ├─ 模版名称必填
│   ├─ 至少一个字段
│   └─ 字段key和label必填
└─ 操作按钮
    ├─ [取消] - 关闭Modal
    └─ [保存] - 调用API保存
        ├─ 新增: POST /api/admin/answer-templates
        └─ 编辑: PUT /api/admin/answer-templates/{id}
```

**JSON序列化逻辑**:
```javascript
// templateFields存储为JSON字符串
template.templateFields = JSON.stringify(fields)

// 示例存储格式:
'[{"key":"situation","label":"Situation (情境)","placeholder":"描述项目背景..."}]'
```

**B. AssociateTemplateModal.vue (模版关联器)**
```vue
AssociateTemplateModal
├─ 加载所有可用模版 (GET /api/admin/answer-templates)
├─ 过滤已关联模版 (从列表中排除)
├─ 单选界面
│   ├─ 模版卡片 (边框、悬停效果)
│   │   ├─ 圆形选择框 (蓝色高亮)
│   │   ├─ 模版名称
│   │   ├─ 模版描述
│   │   └─ 模版字段数量
│   └─ 点击选择模版
└─ 操作按钮
    ├─ [取消] - 关闭Modal
    └─ [关联] - 调用API关联
        └─ POST /api/admin/skill-templates
            {skillId, templateId, isDefault: false}
```

**关键实现**:
- ✅ 动态字段编辑器: 可添加/删除字段,实时预览
- ✅ JSON序列化: templateFields存储为JSON字符串
- ✅ 模版卡片展示: Grid布局,默认标记用绿色Badge
- ✅ API完整实现: SkillTemplateController (公开+管理员)
- ✅ 反向查询支持: 可查询某个模版关联了哪些技能

**API端点** (SkillTemplateController.java):
```java
// 公开API (供用户答题使用)
GET /api/skills/{skillId}/templates - 获取技能所有模版
GET /api/skills/{skillId}/templates/default - 获取默认模版 (含templateFields)

// 管理员API
GET /api/admin/skill-templates?skillId=X - 获取技能模版
GET /api/admin/skill-templates/default?skillId=X - 获取默认模版
POST /api/admin/skill-templates - 关联技能与模版
PUT /api/admin/skill-templates/default - 设置默认模版
DELETE /api/admin/skill-templates?skillId=X&templateId=Y - 取消关联
GET /api/admin/skill-templates/by-template?templateId=X - 反向查询
```

#### 4.1.3 AI答题模式 (新增) ✅ 已实现

**组件**: GeneralSkillContentManagement.vue
**位置**: 试题库Tab右栏的第三种模式

**三模式系统**:
```
questionViewMode: 'view' | 'edit' | 'ai-answer'
├─ view (浏览模式): QuestionViewModal 组件 (inline)
├─ edit (编辑模式): QuestionEditModal 组件 (inline)
└─ ai-answer (AI答题模式): 自定义内联模版渲染
```

**AI答题模式布局**:
```vue
AI答题模式 (questionViewMode === 'ai-answer')
├─ 模式说明卡片 (紫色提示: 为试题添加AI参考答案)
├─ 题目描述 (只读,Markdown渲染)
├─ 答题模式切换 (如果有模版)
│   ├─ [模版模式] - 使用STAR/Technical等模版
│   └─ [自由模式] - 自由文本输入
├─ 模版答题 (aiAnswerMode === 'template')
│   ├─ 模版说明卡片
│   ├─ 动态渲染模版字段 (两栏卡片布局,彩色区分)
│   │   ├─ Situation (蓝色渐变卡片)
│   │   ├─ Task (绿色渐变卡片)
│   │   ├─ Action (橙色渐变卡片)
│   │   └─ Result (紫色渐变卡片)
│   ├─ 核心思路输入框 (黄色边框,支持Markdown)
│   └─ [清空] [保存AI答案]
└─ 自由答题 (aiAnswerMode === 'free')
    ├─ AI参考答案 (Textarea, 12行)
    ├─ 核心思路输入框 (同上)
    └─ [清空] [保存AI答案]
```

**模版加载逻辑** (GeneralSkillContentManagement.vue:1304-1406):
```javascript
async loadAIAnswer() {
  // 1. 获取AI笔记 (user_id = -1)
  let aiNote = await adminQuestionApi.getAINote(questionId)

  // 2. 通过Focus Area获取skill_id
  let skillId = this.selectedSkillId || focusArea.skillId

  // 3. 加载Skill的默认答题模版
  const template = await api.get(`/skills/${skillId}/templates/default`)

  // 4. 如果有模版,解析AI笔记到模版字段
  if (template && template.templateFields) {
    this.aiAnswerTemplate = template
    this.aiAnswerMode = 'template'

    // 尝试解析Markdown格式: ## Label\nContent
    for (const field of templateFields) {
      const pattern = /## ${field.label}\s*\n([\s\S]*?)(?=\n## |$)/i
      const match = aiNote.noteContent.match(pattern)
      if (match) {
        parsedValues[field.key] = match[1].trim()
      }
    }
  } else {
    // 无模版,使用自由模式
    this.aiAnswerTemplate = null
    this.aiAnswerMode = 'free'
    this.aiAnswerData = {
      freeAnswer: aiNote?.noteContent || '',
      coreStrategy: aiNote?.coreStrategy || ''
    }
  }
}
```

**保存AI答案逻辑** (GeneralSkillContentManagement.vue:1431-1482):
```javascript
async saveAIAnswer() {
  let noteContent = ''
  const coreStrategy = this.aiAnswerData.coreStrategy || ''

  // 模版模式: 将模版字段合并为Markdown格式
  if (this.aiAnswerMode === 'template') {
    const parts = templateFields.map(field => {
      return `## ${field.label}\n${this.aiAnswerData[field.key] || ''}`
    })
    noteContent = parts.join('\n\n')
  } else {
    // 自由模式: 保存纯文本
    noteContent = this.aiAnswerData.freeAnswer || ''
  }

  // 调用API保存 (user_id = -1 在后端处理)
  await adminQuestionApi.saveOrUpdateAINote(questionId, {
    noteContent,
    coreStrategy
  })
}
```

**关键特性**:
- ✅ **模版字段动态渲染**: 根据template.templateFields JSON动态生成表单
- ✅ **Markdown格式存储**: 模版字段存储为`## Label\nContent`格式,便于解析和展示
- ✅ **核心思路字段**: 无论模版模式还是自由模式都支持coreStrategy字段
- ✅ **模版与自由切换**: 用户可自由选择使用模版或自由文本
- ✅ **彩色卡片UI**: 模版字段使用渐变色卡片区分,提升视觉体验
- ✅ **实时加载**: 切换到AI答题模式时自动加载模版和已有AI笔记

**API端点**:
- `GET /api/admin/questions/{id}/ai-note` - 获取AI笔记 (user_id=-1)
- `POST /api/admin/questions/{id}/ai-note` - 保存AI笔记
- `GET /api/skills/{skillId}/templates/default` - 获取Skill的默认模版

---

### 4.2 用户端页面

#### 4.2.1 通用技能学习页面 ✅ 已实现

**路径**: `/general-skills/learning` 或 `/general-skills/learning/:skillId`
**组件**: `GeneralSkillLearning.vue`

**组件结构**:
```vue
GeneralSkillLearning.vue
├─ 左侧 (上下两栏树形结构)
│   ├─ 上栏树: 职业路径 → 技能
│   └─ 下栏树:
│       ├─ 第一类技能: 大分类 → Focus Area
│       └─ 第二类技能: 直接显示Focus Area
└─ 右侧内容区 (双Tab布局)
    ├─ Tab 1: 学习资料
    │   └─ 学习资料列表
    │       ├─ 学习资料内容 (Markdown渲染/视频播放)
    │       ├─ 🤖 AI学习笔记卡片
    │       │   ├─ AI整体笔记 (折叠/展开)
    │       │   └─ AI知识点列表
    │       └─ 📝 我的学习笔记卡片
    │           ├─ 我的整体笔记 (可编辑)
    │           └─ 我的知识点列表 (可编辑)
    │
    │   💡 知识点合并逻辑:
    │     如果AI知识点和用户知识点标题相同,则在UI上合并显示
    │     (底层仍是两条记录,但UI上显示为一个卡片)
    │
    └─ Tab 2: 试题库
        ├─ 试题列表
        └─ 试题详情 + 答题笔记
            ├─ 浏览模式: 查看题目和要求
            └─ 答题模式: 根据Skill模版渲染输入界面
                ├─ Behavioral类: STAR框架输入
                └─ 技术类: 自由答题输入
```

**关键实现**:
- AI笔记卡片使用特殊样式标记 (如背景色、图标)
- 知识点合并逻辑:
  ```javascript
  const mergedKnowledgePoints = [];
  const aiKps = aiKnowledgePoints; // user_id=-1
  const userKps = userKnowledgePoints; // user_id=currentUser.id

  // 合并同名知识点
  aiKps.forEach(aiKp => {
    const userKp = userKps.find(kp => kp.kpTitle === aiKp.kpTitle);
    mergedKnowledgePoints.push({
      title: aiKp.kpTitle,
      aiContent: aiKp.kpContent,
      userContent: userKp?.kpContent || null,
      isMerged: !!userKp
    });
  });

  // 添加只有用户笔记的知识点
  userKps.forEach(userKp => {
    if (!aiKps.find(kp => kp.kpTitle === userKp.kpTitle)) {
      mergedKnowledgePoints.push({
        title: userKp.kpTitle,
        aiContent: null,
        userContent: userKp.kpContent,
        isMerged: false
      });
    }
  });
  ```

#### 4.2.2 独立答题页面 ⏭️ 已删除，功能已整合

**注**: `MyQuestionBank.vue`已删除，答题功能已整合到`GeneralSkillLearning.vue`的试题库Tab中

**原设计 (已过时)**:

**新增筛选条件**:
```vue
<template>
  <div class="filter-bar">
    <select v-model="filters.skillId">
      <option value="">全部技能</option>
      <option v-for="skill in skills" :value="skill.id">{{ skill.name }}</option>
    </select>

    <!-- 仅第一类技能显示大分类筛选 -->
    <select v-if="isFirstClassSkill" v-model="filters.majorCategoryId">
      <option value="">全部大分类</option>
      <option v-for="cat in majorCategories" :value="cat.id">{{ cat.name }}</option>
    </select>

    <select v-model="filters.focusAreaId">
      <option value="">全部Focus Area</option>
      <option v-for="fa in focusAreas" :value="fa.id">{{ fa.name }}</option>
    </select>

    <select v-model="filters.questionType">
      <option value="">全部题目类型</option>
      <option value="behavioral">Behavioral</option>
      <option value="technical">技术</option>
      <option value="design">设计</option>
      <option value="programming">编程</option>
    </select>

    <select v-model="filters.answerStatus">
      <option value="">全部答题状态</option>
      <option value="answered">已答</option>
      <option value="unanswered">未答</option>
    </select>
  </div>
</template>
```

**答题界面 (根据Skill模版动态渲染)**:

**Behavioral类 (STAR模版)**:
```vue
<template>
  <div class="answer-editor">
    <h3>我的回答</h3>

    <!-- 根据模版字段动态渲染 -->
    <div v-for="field in template.templateFields" :key="field.key" class="field-group">
      <label>{{ field.label }}</label>
      <textarea
        v-model="answer[field.key]"
        :placeholder="field.placeholder"
        rows="4"
      ></textarea>
    </div>

    <!-- 核心思路 (可选) -->
    <div class="field-group">
      <label>核心思路 (可选)</label>
      <textarea v-model="answer.coreStrategy" rows="2"></textarea>
    </div>

    <!-- 关联知识点 -->
    <div class="field-group">
      <label>关联知识点</label>
      <KnowledgePointSelector
        v-model="answer.relatedKnowledgePointIds"
        :ai-knowledge-points="aiKnowledgePoints"
        :user-knowledge-points="userKnowledgePoints"
      />
    </div>

    <button @click="saveAnswer">保存笔记</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      answer: {
        situation: '',
        task: '',
        action: '',
        result: '',
        coreStrategy: '',
        relatedKnowledgePointIds: []
      },
      template: null // STAR模版
    }
  },
  methods: {
    async saveAnswer() {
      // 将answer对象转换为Markdown格式
      const noteContent = this.template.templateFields.map(field => {
        return `**${field.label}**\n${this.answer[field.key] || ''}\n`;
      }).join('\n');

      // 调用API保存
      await questionApi.saveMyNote(this.questionId, {
        noteContent,
        coreStrategy: this.answer.coreStrategy,
        relatedKnowledgePointIds: this.answer.relatedKnowledgePointIds
      });
    }
  }
}
</script>
```

**技术类**:
```vue
<template>
  <div class="answer-editor">
    <h3>我的回答</h3>

    <!-- 核心思路 -->
    <div class="field-group">
      <label>核心思路</label>
      <textarea v-model="answer.coreStrategy" rows="3"></textarea>
    </div>

    <!-- 详细回答 -->
    <div class="field-group">
      <label>详细回答</label>
      <MarkdownEditor v-model="answer.noteContent" />
    </div>

    <!-- 关联知识点 -->
    <div class="field-group">
      <label>关联知识点</label>
      <KnowledgePointSelector
        v-model="answer.relatedKnowledgePointIds"
        :ai-knowledge-points="aiKnowledgePoints"
        :user-knowledge-points="userKnowledgePoints"
      />
    </div>

    <button @click="saveAnswer">保存笔记</button>
  </div>
</template>
```

---

## 5. 业务逻辑设计

### 5.1 Service层核心逻辑

#### 5.1.1 AnswerTemplateService

```java
@Service
public class AnswerTemplateService {

    /**
     * 创建答题模版
     */
    @Transactional
    public AnswerTemplateDTO createTemplate(AnswerTemplateDTO dto) {
        // 1. 创建模版
        AnswerTemplate template = new AnswerTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setDescription(dto.getDescription());

        // 2. 保存字段定义 (JSON)
        template.setTemplateFields(objectMapper.writeValueAsString(dto.getTemplateFields()));

        // 3. 保存
        template = templateRepository.save(template);
        return toDTO(template);
    }

    /**
     * 获取模版详情
     */
    public AnswerTemplateDTO getTemplate(Long templateId) {
        AnswerTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

        AnswerTemplateDTO dto = toDTO(template);

        // 解析JSON字段
        dto.setTemplateFields(objectMapper.readValue(
            template.getTemplateFields(),
            new TypeReference<List<TemplateFieldDTO>>() {}
        ));

        return dto;
    }
}
```

#### 5.1.2 SkillTemplateService

```java
@Service
public class SkillTemplateService {

    /**
     * 关联Skill与模版
     */
    @Transactional
    public void associateTemplate(Long skillId, Long templateId, boolean isDefault) {
        // 1. 检查Skill和Template是否存在
        skillRepository.findById(skillId)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        templateRepository.findById(templateId)
            .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

        // 2. 如果设置为默认,则取消该Skill的其他默认模版
        if (isDefault) {
            skillTemplateRepository.findBySkillId(skillId)
                .forEach(st -> st.setIsDefault(false));
        }

        // 3. 创建关联
        SkillTemplate skillTemplate = new SkillTemplate();
        skillTemplate.setSkillId(skillId);
        skillTemplate.setTemplateId(templateId);
        skillTemplate.setIsDefault(isDefault);

        skillTemplateRepository.save(skillTemplate);
    }

    /**
     * 获取Skill的默认模版
     */
    public AnswerTemplateDTO getDefaultTemplate(Long skillId) {
        // 1. 查询默认模版关联
        SkillTemplate skillTemplate = skillTemplateRepository
            .findBySkillIdAndIsDefaultTrue(skillId)
            .orElse(null);

        if (skillTemplate == null) {
            return null;
        }

        // 2. 返回模版详情
        return templateService.getTemplate(skillTemplate.getTemplateId());
    }
}
```

#### 5.1.3 LearningContentService (扩展AI笔记)

```java
@Service
public class LearningContentService {

    /**
     * 导入AI整体笔记
     */
    @Transactional
    public void importAINote(Long contentId, String noteContent) {
        // 1. 检查学习资料是否存在
        learningContentRepository.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        // 2. 查找或创建AI笔记记录 (user_id=-1)
        UserLearningContentNote aiNote = noteRepository
            .findByLearningContentIdAndUserId(contentId, -1L)
            .orElse(new UserLearningContentNote());

        aiNote.setLearningContentId(contentId);
        aiNote.setUserId(-1L); // AI笔记标识
        aiNote.setNoteContent(noteContent);

        noteRepository.save(aiNote);
    }

    /**
     * 导入AI知识点列表
     */
    @Transactional
    public void importAIKnowledgePoints(Long contentId, List<KnowledgePointDTO> kps) {
        // 1. 检查学习资料是否存在
        learningContentRepository.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        // 2. 删除旧的AI知识点
        knowledgePointRepository.deleteByLearningContentIdAndUserId(contentId, -1L);

        // 3. 创建新的AI知识点
        for (KnowledgePointDTO kpDTO : kps) {
            UserLearningContentKnowledgePoint kp = new UserLearningContentKnowledgePoint();
            kp.setLearningContentId(contentId);
            kp.setUserId(-1L); // AI知识点标识
            kp.setKpTitle(kpDTO.getKpTitle());
            kp.setKpContent(kpDTO.getKpContent());

            knowledgePointRepository.save(kp);
        }
    }

    /**
     * 获取学习资料的笔记 (包含AI笔记和用户笔记)
     */
    public Map<String, UserLearningContentNoteDTO> getNotes(Long contentId, Long userId) {
        Map<String, UserLearningContentNoteDTO> result = new HashMap<>();

        // 1. 查询AI笔记
        noteRepository.findByLearningContentIdAndUserId(contentId, -1L)
            .ifPresent(aiNote -> result.put("aiNote", toDTO(aiNote)));

        // 2. 查询用户笔记
        noteRepository.findByLearningContentIdAndUserId(contentId, userId)
            .ifPresent(userNote -> result.put("userNote", toDTO(userNote)));

        return result;
    }

    /**
     * 获取学习资料的知识点 (包含AI知识点和用户知识点)
     */
    public Map<String, List<KnowledgePointDTO>> getKnowledgePoints(Long contentId, Long userId) {
        Map<String, List<KnowledgePointDTO>> result = new HashMap<>();

        // 1. 查询AI知识点
        List<UserLearningContentKnowledgePoint> aiKps =
            knowledgePointRepository.findByLearningContentIdAndUserId(contentId, -1L);
        result.put("aiKnowledgePoints", aiKps.stream().map(this::toDTO).collect(Collectors.toList()));

        // 2. 查询用户知识点
        List<UserLearningContentKnowledgePoint> userKps =
            knowledgePointRepository.findByLearningContentIdAndUserId(contentId, userId);
        result.put("userKnowledgePoints", userKps.stream().map(this::toDTO).collect(Collectors.toList()));

        return result;
    }
}
```

#### 5.1.4 UserQuestionNoteService (扩展知识点关联)

```java
@Service
public class UserQuestionNoteService {

    /**
     * 保存/更新用户答题笔记 (支持知识点关联)
     */
    @Transactional
    public UserQuestionNoteDTO saveOrUpdateNote(Long questionId, Long userId, UserQuestionNoteDTO dto) {
        // 1. 查找现有记录
        Optional<UserQuestionNote> existingNote = noteRepository.findByQuestionIdAndUserId(questionId, userId);

        UserQuestionNote note;
        if (existingNote.isPresent()) {
            note = existingNote.get();
        } else {
            note = new UserQuestionNote();
            note.setQuestionId(questionId);
            note.setUserId(userId);
        }

        // 2. 保存内容
        note.setNoteContent(dto.getNoteContent());
        note.setCoreStrategy(dto.getCoreStrategy());

        // 3. 保存关联的知识点ID列表 (JSON)
        if (dto.getRelatedKnowledgePointIds() != null) {
            note.setRelatedKnowledgePointIds(
                objectMapper.writeValueAsString(dto.getRelatedKnowledgePointIds())
            );
        }

        note = noteRepository.save(note);
        return toDTO(note);
    }

    /**
     * 获取用户答题笔记
     */
    public UserQuestionNoteDTO getUserNote(Long questionId, Long userId) {
        Optional<UserQuestionNote> note = noteRepository.findByQuestionIdAndUserId(questionId, userId);

        if (note.isEmpty()) {
            return null;
        }

        UserQuestionNoteDTO dto = toDTO(note.get());

        // 解析JSON字段
        if (note.get().getRelatedKnowledgePointIds() != null) {
            dto.setRelatedKnowledgePointIds(
                objectMapper.readValue(
                    note.get().getRelatedKnowledgePointIds(),
                    new TypeReference<List<Long>>() {}
                )
            );
        }

        return dto;
    }
}
```

---

### 5.2 数据访问层

#### 5.2.1 AnswerTemplateRepository

```java
@Repository
public interface AnswerTemplateRepository extends JpaRepository<AnswerTemplate, Long> {

    // 按名称查询模版
    Optional<AnswerTemplate> findByTemplateName(String templateName);
}
```

#### 5.2.2 SkillTemplateRepository

```java
@Repository
public interface SkillTemplateRepository extends JpaRepository<SkillTemplate, SkillTemplateId> {

    // 获取Skill关联的所有模版
    List<SkillTemplate> findBySkillId(Long skillId);

    // 获取Skill的默认模版
    Optional<SkillTemplate> findBySkillIdAndIsDefaultTrue(Long skillId);
}
```

#### 5.2.3 UserLearningContentNoteRepository (扩展)

```java
@Repository
public interface UserLearningContentNoteRepository extends JpaRepository<UserLearningContentNote, Long> {

    // 查询笔记 (支持user_id=-1)
    Optional<UserLearningContentNote> findByLearningContentIdAndUserId(Long contentId, Long userId);

    // 删除AI笔记
    void deleteByLearningContentIdAndUserId(Long contentId, Long userId);
}
```

#### 5.2.4 UserLearningContentKnowledgePointRepository (扩展)

```java
@Repository
public interface UserLearningContentKnowledgePointRepository extends JpaRepository<UserLearningContentKnowledgePoint, Long> {

    // 查询知识点 (支持user_id=-1)
    List<UserLearningContentKnowledgePoint> findByLearningContentIdAndUserId(Long contentId, Long userId);

    // 删除AI知识点
    void deleteByLearningContentIdAndUserId(Long contentId, Long userId);
}
```

---

## 6. 数据迁移

### 6.1 Migration V17 (Phase 6表创建)

**文件**: `V17__create_phase6_tables.sql`

```sql
-- 1. 创建答题模版表
CREATE TABLE answer_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(50) UNIQUE NOT NULL COMMENT '模版名称 (STAR, Technical等)',
    template_fields JSON NOT NULL COMMENT '模版字段定义 (JSON数组)',
    description TEXT COMMENT '模版说明',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_template_name (template_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='答题模版表';

-- 2. 创建Skill-模版关联表
CREATE TABLE skill_templates (
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    template_id BIGINT NOT NULL COMMENT '模版ID',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否为该Skill的默认模版',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (skill_id, template_id),
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES answer_templates(id) ON DELETE CASCADE,
    INDEX idx_skill_id (skill_id),
    INDEX idx_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Skill与答题模版关联表 (多对多)';

-- 3. 扩展user_question_notes表
ALTER TABLE user_question_notes
    ADD COLUMN related_knowledge_point_ids JSON COMMENT '关联的知识点ID列表 (JSON数组)' AFTER core_strategy;

-- 4. 插入STAR模版预置数据
INSERT INTO answer_templates (template_name, template_fields, description) VALUES (
  'STAR',
  '[
    {"key": "situation", "label": "Situation (情境)", "placeholder": "描述项目背景和挑战..."},
    {"key": "task", "label": "Task (任务)", "placeholder": "你的具体职责是什么..."},
    {"key": "action", "label": "Action (行动)", "placeholder": "你采取了哪些行动..."},
    {"key": "result", "label": "Result (结果)", "placeholder": "可量化的成果 (如提升X%)..."}
  ]',
  'STAR答题框架,适用于Behavioral题目'
);

-- 5. 插入Technical模版预置数据
INSERT INTO answer_templates (template_name, template_fields, description) VALUES (
  'Technical',
  '[
    {"key": "core_concept", "label": "核心概念", "placeholder": "核心技术概念和原理..."},
    {"key": "implementation", "label": "技术实现", "placeholder": "具体实现方式和细节..."},
    {"key": "case_study", "label": "实际案例", "placeholder": "真实项目中的应用案例..."}
  ]',
  'Technical答题框架,适用于技术类题目'
);

-- 6. 为第二类技能创建"General"大分类
-- 示例: Behavioral技能 (假设skill_id=3)
INSERT INTO major_categories (skill_id, name, display_order) VALUES
  (3, 'General', 0); -- Behavioral技能的隐式大分类
```

---

## 7. 性能优化

### 7.1 数据库索引

```sql
-- answer_templates表索引
CREATE INDEX idx_template_name ON answer_templates(template_name);

-- skill_templates表索引
CREATE INDEX idx_skill_id ON skill_templates(skill_id);
CREATE INDEX idx_template_id ON skill_templates(template_id);

-- user_learning_content_notes表索引 (支持user_id=-1查询)
CREATE INDEX idx_user_id ON user_learning_content_notes(user_id);

-- user_learning_content_knowledge_points表索引 (支持user_id=-1查询)
CREATE INDEX idx_user_id ON user_learning_content_knowledge_points(user_id);
```

### 7.2 查询优化

**AI笔记查询优化**:
```sql
-- 一次性查询AI笔记和用户笔记
SELECT * FROM user_learning_content_notes
WHERE learning_content_id = ? AND user_id IN (?, -1)
ORDER BY user_id DESC;
-- 使用IN查询 + 索引,避免两次查询
```

**模版查询优化**:
- 缓存答题模版数据 (Redis)
- 前端缓存模版字段定义

---

## 8. 安全设计

### 8.1 权限控制

**管理端API**:
- 所有 `/api/admin/answer-templates/**` 需要管理员权限
- 所有 `/api/admin/skills/{skillId}/templates/**` 需要管理员权限
- AI笔记导入功能需要管理员权限

**用户端API**:
- 用户只能查看和编辑自己的笔记
- AI笔记 (user_id=-1) 对所有用户可见

### 8.2 数据验证

```java
@NotNull(message = "Template name is required")
@Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Invalid template name")
private String templateName;

@NotNull(message = "Template fields are required")
@Size(min = 1, message = "At least one field is required")
private List<TemplateFieldDTO> templateFields;

@Pattern(regexp = "^\\[\\d+(,\\d+)*\\]$", message = "Invalid knowledge point IDs format")
private String relatedKnowledgePointIds;
```

---

## 9. 实现与设计的差异说明

### 9.1 已实现内容 (Phase 6.1-6.4)

#### Phase 6.1: 数据模型 ✅ 完全按设计实施
- ✅ `answer_templates`表 - 答题模版存储
- ✅ `skill_templates`表 - Skill与模版多对多关联
- ✅ `user_question_notes`表扩展 - 新增`related_knowledge_point_ids`字段
- ✅ STAR和Technical模版预置数据

#### Phase 6.2: 管理员页面 ✅ 完全按设计实施
- ✅ GeneralSkillContentManagement.vue - 通用技能内容管理
  - 左右两栏布局,上下树形结构
  - 双Tab模式 (学习资料 + 试题库)
  - AI笔记导入功能 (AIImportModal组件)
  - **新增**: AI答题模式 (三模式系统: view/edit/ai-answer)

#### Phase 6.3: 技能模版管理 ✅ 超预期实现
**设计文档未明确要求,实际完整实现**:
- ✅ SkillTemplateManagement.vue - 技能模版库管理页面
  - 左侧30%: 职业路径 → 技能树
  - 右侧70%: 模版卡片列表 (默认标记、操作按钮)
- ✅ TemplateEditorModal.vue - 模版编辑器
  - 动态字段编辑器 (添加/删除字段)
  - JSON序列化存储
- ✅ AssociateTemplateModal.vue - 模版关联器
- ✅ SkillTemplateController API完整实现
  - 公开API: `/api/skills/{skillId}/templates` (供用户使用)
  - 管理员API: `/api/admin/skill-templates` (含反向查询)

#### Phase 6.4: 问题浏览模式 ✅ 超预期实现
**设计文档未明确要求,实际完整实现**:
- ✅ QuestionViewModal.vue - 两列布局
  - 左列: 问题详情 (描述、答案要求)
  - 右列: 答题笔记/AI答案
  - 三种模式: view/edit/ai-answer
  - 紧凑prose模式 (prose-sm)
- ✅ UserNoteEditor.vue - 用户笔记编辑器
  - **核心功能**: STAR框架动态答题界面 ✅
  - 模版/自由两种答题模式切换
  - 根据`answerTemplate.templateFields` JSON动态渲染输入框
  - 自动解析已保存的模版格式笔记 (正则匹配`## Label`)
  - 实时预览完整答案 (Markdown渲染)
  - API调用: `skillTemplateApi.getDefaultTemplatePublic(skillId)`

### 9.2 设计方案优势

#### AI笔记实现方式
**设计方案**: 使用user_id=-1标识AI笔记
**优势**:
- ✅ 复用现有表结构
- ✅ 避免新增AI专用表
- ✅ 查询逻辑简单

#### 第二类技能的大分类处理
**设计方案**: 创建"General"大分类,前端隐藏
**优势**:
- ✅ 复用Phase 4的三层架构
- ✅ 避免两套UI组件维护
- ✅ 后端逻辑统一

#### 答题模版的关联方式
**设计方案**: 采用多对多关联 (skill_templates表)
**优势**:
- ✅ 一个Skill可以关联多个模版
- ✅ 支持默认模版标记
- ✅ 用户可选择使用不同模版

### 9.3 实施亮点 (超预期功能)

#### 亮点1: 三模式系统 (GeneralSkillContentManagement.vue)
**原设计**: 只有查看/编辑两种模式
**实际实现**: 三模式系统
- `view` - 浏览模式 (QuestionViewModal内联)
- `edit` - 编辑模式 (QuestionEditModal内联)
- `ai-answer` - AI答题模式 (新增,支持模版/自由切换)

**价值**:
- 管理员可为试题添加AI参考答案
- 支持STAR/Technical等模版框架
- 模版答题卡片彩色区分 (蓝/绿/橙/紫渐变)

#### 亮点2: STAR框架动态答题界面 (UserNoteEditor.vue)
**原设计**: 简要提及"根据Skill模版动态渲染"
**实际实现**: 完整的模版渲染引擎
```javascript
// 1. 获取技能默认模版 (GeneralSkillLearning.vue:1803)
const template = await skillTemplateApi.getDefaultTemplatePublic(skillId)

// 2. 动态解析templateFields (支持JSON字符串/对象双格式)
const fields = parseTemplateFields(template.templateFields)

// 3. 自动解析已保存笔记 (正则匹配: ## Label\nContent)
const pattern = new RegExp(`## ${field.label}\\s*\\n([\\s\\S]*?)(?=\\n## |$)`, 'i')

// 4. 实时预览 (Markdown渲染)
const preview = marked(templateMarkdown)
```

**价值**:
- 用户无需手动输入STAR标签
- 自动保存/加载为Markdown格式
- 可随时切换模版/自由模式

#### 亮点3: 完整的技能模版库管理系统
**原设计**: 未明确设计
**实际实现**: 三个独立组件协同工作
- SkillTemplateManagement.vue - 主页面 (左右两栏)
- TemplateEditorModal.vue - 模版编辑器 (动态字段管理)
- AssociateTemplateModal.vue - 模版关联器 (单选界面)

**价值**:
- 管理员可视化管理模版字段
- 支持多模版关联 (默认模版 + 通用模版)
- 反向查询支持 (查询模版被哪些技能使用)

### 9.4 待完成功能 (Phase 6.5)

- ⏭️ 知识点关联功能 (`related_knowledge_point_ids`字段在答题笔记中的应用)
- ⏭️ 独立题库刷题页面 (如需要，重新实现MyQuestionBank.vue，含搜索筛选)
- ⏭️ 数据导入 (云计算、DevOps、Behavioral题库)
- ⏭️ AI笔记批量导入工具

---

## 10. 附录

### 10.1 术语表

| 术语 | 英文 | 说明 |
|------|------|------|
| 第一类技能 | First Class Skills | 云计算、DevOps等需要三层结构的技能 |
| 第二类技能 | Second Class Skills | Behavioral等两层结构的技能 |
| AI笔记 | AI Note | 使用user_id=-1标识的AI生成笔记 |
| 答题模版 | Answer Template | STAR、Technical等答题框架 |
| 知识点 | Knowledge Point | 用户或AI总结的知识点 |

### 10.2 参考文档

- [Phase 6 详细需求 v1.0](../requirement/Phase6-详细需求.md)
- [Phase 4 设计文档](./Phase4-设计文档.md)
- [ARCHITECTURE.md](./ARCHITECTURE.md)

---

## 11. 版本历史

### v1.1 (2025-12-28)
**更新内容**:
- ✅ 更新实施进度: Phase 6.1-6.4已完成
- ✅ 新增4.1.3章节: AI答题模式实现细节
- ✅ 新增第9章: 实现与设计的差异说明
- ✅ 新增实施亮点章节: 三模式系统、STAR动态答题、技能模版库管理
- ✅ 更新验收标准: 标记已完成项

### v1.0 (2025-12-27)
**初始版本**:
- 📋 完整设计文档 (数据模型、API、前端、实施路径)

---

**文档版本**: v1.1
**创建时间**: 2025-12-27
**最后更新**: 2025-12-28 (v1.1 - 根据实际实现更新)
**状态**: 🚧 部分完成 - Phase 6.1-6.4 已实现，Phase 6.5 待实施

**实施进度**:
- ✅ Phase 6.1: 数据模型和基础架构 (100%)
- ✅ Phase 6.2: 管理员页面 (100% - 含AI答题模式)
- ✅ Phase 6.3: 技能模版管理 (100% - 完整实现三组件系统)
- ✅ Phase 6.4: 问题浏览模式重新设计 (100% - 含STAR动态答题界面)
- ⏭️ Phase 6.5: 数据导入和测试 (待实施)

**核心成果**:
1. ✅ STAR框架动态答题界面完整实现 (UserNoteEditor.vue)
2. ✅ 三模式系统 (view/edit/ai-answer) 完整实现
3. ✅ 技能模版库管理系统完整实现 (SkillTemplateManagement.vue + 2个Modal组件)
4. ✅ 公开API支持用户端答题 (SkillTemplateController)

**待完成功能**:
1. ⏭️ 知识点关联功能 (related_knowledge_point_ids字段在答题笔记中的应用)
2. ⏭️ 独立题库刷题页面 (如需要，重新实现MyQuestionBank.vue)
3. ⏭️ 数据导入 (云计算、DevOps、Behavioral题库)
4. ⏭️ AI笔记批量导入工具
