# Phase 5 设计文档 - 系统设计学习模块

> **架构版本**: v1.1
> **状态**: ✅ 已完成 (2025-12-26)
> **创建时间**: 2025-12-25
> **更新时间**: 2025-12-26
> **需求文档**: [Phase5-详细需求.md](../requirement/Phase5-详细需求.md)
> **参考设计**: https://www.hellointerview.com/

---

## 1. 总体架构

### 1.1 设计理念

Phase 5 实现**两个子模块**的系统设计学习系统:
- **基础知识模块** - 复用现有Skill + Focus Area + Learning Content架构
- **典型案例模块** - 新增独立的案例管理系统(5张表)

### 1.2 核心特性

✅ **基础知识学习** - 复用Phase 2/4的架构,3个大分类(核心概念、关键技术、设计模式)
✅ **典型案例管理** - 独立的案例系统,支持多个参考答案
✅ **标准化答题框架** - 6个步骤(需求、实体、API、数据流、架构、深入)
✅ **多参考答案支持** - 每个案例可以有多个设计方案
✅ **左右对比模式** - 用户答题时可以左右对比参考答案
✅ **双轨制答题** - 6步骤（step1-6）+ 7个结构化关键点（kp_*）
✅ **学习总结视图** - 横向对比所有案例的关键要点

### 1.3 实际架构分层

```
┌──────────────────────────────────────────────────────────────┐
│                      前端 (Vue 3)                             │
├──────────────────────────────────────────────────────────────┤
│  管理端                        │  用户端                      │
│  - 基础知识管理(复用)           │  - 基础知识学习(复用)         │
│    * 大分类 + Focus Area树     │  - 典型案例浏览              │
│    * 学习资料CRUD              │    * 查看模式(案例详情)       │
│  - 典型案例管理(新增)           │    * 编辑模式(左右对比)       │
│    * 案例信息编辑              │  - 用户答题编辑器            │
│    * 学习资源管理              │    * 6个步骤Tab + 7个关键点Tab│
│    * 参考答案管理(6个Tab)      │    * 架构图上传              │
│    * 支持多个方案              │    * 要点总结                │
│                                │  - 学习总结页面(新增)         │
│                                │    * 横向对比所有案例         │
└──────────────────────────────────────────────────────────────┘
                           ↕ RESTful API
┌──────────────────────────────────────────────────────────────┐
│                   后端 (Spring Boot)                          │
├──────────────────────────────────────────────────────────────┤
│  Controller层                                                 │
│  - SystemDesignCaseController (案例管理API)                  │
│  - CaseResourceController (学习资源API)                      │
│  - CaseSolutionController (参考答案API)                      │
│  - UserSystemDesignController (用户答题API)                  │
├──────────────────────────────────────────────────────────────┤
│  Service层                                                    │
│  - SystemDesignCaseService (案例CRUD)                        │
│  - CaseResourceService (资源管理)                            │
│  - CaseSolutionService (答案管理,支持多方案)                │
│  - UserCaseNoteService (用户答题记录,双轨制)                │
├──────────────────────────────────────────────────────────────┤
│  Repository层 (Spring Data JPA)                               │
│  - SystemDesignCaseRepository                                 │
│  - CaseResourceRepository                                     │
│  - CaseSolutionRepository                                     │
│  - SolutionDiagramRepository                                  │
│  - UserCaseNoteRepository                                     │
└──────────────────────────────────────────────────────────────┘
                           ↕
┌──────────────────────────────────────────────────────────────┐
│                   数据库 (MySQL 8.0)                          │
│  新增:                                                        │
│  - system_design_cases (案例主表)                            │
│  - case_resources (学习资源表)                               │
│  - case_solutions (参考答案表,支持多方案)                    │
│  - solution_diagrams (配图表)                                │
│  - user_case_notes (用户答题记录表,双轨制)                   │
│  复用:                                                        │
│  - skills (系统设计Skill)                                    │
│  - major_categories (3个大分类)                              │
│  - focus_areas (基础知识Focus Areas)                         │
│  - learning_contents (基础知识学习资料)                      │
└──────────────────────────────────────────────────────────────┘
```

### 1.4 与现有架构的集成

**复用Phase 2/4的概念(基础知识模块)**:
- ✅ **Skill** - "系统设计"(id=2)
- ✅ **Major Category** - 3个大分类(核心概念、关键技术、设计模式)
- ✅ **Focus Area** - 具体知识点
- ✅ **Learning Content** - 学习资料(文章、视频)

**新增概念(典型案例模块)**:
- 🆕 **SystemDesignCase** - 典型案例(如设计Twitter)
- 🆕 **CaseResource** - 案例学习资源(视频、文章)
- 🆕 **CaseSolution** - 参考答案(支持多个方案,每个方案6个步骤)
- 🆕 **SolutionDiagram** - 参考答案配图
- 🆕 **UserCaseNote** - 用户答题记录(6个步骤 + 7个结构化关键点 + 架构图 + 要点总结)

**架构优势**:
- 🎯 **独立性** - 典型案例模块独立于Focus Area,不依赖learning_stages
- 🎯 **灵活性** - 每个案例支持多个参考答案方案
- 🎯 **扩展性** - JSON字段支持公司标签、关联知识点等扩展信息
- 🎯 **双轨制** - 同时支持6步骤完整答案和结构化关键点记录

---

## 2. 数据模型设计

### 2.1 实际ER图

```
                    ┌───────────────────┐
                    │      skills       │
                    ├───────────────────┤
                    │ id=2 (PK)         │
                    │ name="系统设计"    │
                    └───────────────────┘
                             │
                 ┌───────────┴───────────┐
                 │                       │
                 │ 1:N (基础知识)         │ 1:N (典型案例)
                 ↓                       ↓
    ┌─────────────────────┐    ┌──────────────────────────┐
    │ major_categories    │    │ system_design_cases      │
    ├─────────────────────┤    ├──────────────────────────┤
    │ id (PK)             │    │ id (PK)                  │
    │ name                │    │ skill_id (FK) = 2        │
    │ (核心概念、         │    │ title                    │
    │  关键技术、         │    │ case_description (TEXT)  │
    │  设计模式)          │    │ difficulty (ENUM)        │
    └─────────────────────┘    │ difficulty_rating (1-10) │
             │                 │ company_tags (JSON)      │
             │ 1:N             │ related_focus_areas (JSON)│
             ↓                 │ is_official              │
    ┌─────────────────────┐    │ created_by_user_id       │
    │   focus_areas       │    │ display_order            │
    ├─────────────────────┤    └──────────────────────────┘
    │ id (PK)             │             │
    │ skill_id (FK) = 2   │             │ 1:N
    │ name                │             ↓
    │ major_category_id   │    ┌──────────────────────────┐
    └─────────────────────┘    │   case_resources         │
             │                 ├──────────────────────────┤
             │ 1:N             │ id (PK)                  │
             ↓                 │ case_id (FK)             │
    ┌─────────────────────┐    │ resource_type (ENUM)     │ (VIDEO/ARTICLE)
    │ learning_contents   │    │ title                    │
    ├─────────────────────┤    │ url                      │
    │ id (PK)             │    │ description              │
    │ focus_area_id (FK)  │    │ display_order            │
    │ content_type        │    └──────────────────────────┘
    │ title, url, author  │
    └─────────────────────┘    system_design_cases (续)
                                        │
                                        │ 1:N
                                        ↓
                               ┌──────────────────────────┐
                               │   case_solutions         │ (多方案)
                               ├──────────────────────────┤
                               │ id (PK)                  │
                               │ case_id (FK)             │
                               │ solution_name            │ (方案A、方案B等)
                               │ author                   │
                               │ step1_requirements (TEXT)│
                               │ step2_entities (TEXT)    │
                               │ step3_api (TEXT)         │
                               │ step4_data_flow (TEXT)   │
                               │ step5_architecture (TEXT)│
                               │ step6_deep_dive (TEXT)   │
                               │ display_order            │
                               └──────────────────────────┘
                                        │
                                        │ 1:N
                                        ↓
                               ┌──────────────────────────┐
                               │  solution_diagrams       │
                               ├──────────────────────────┤
                               │ id (PK)                  │
                               │ solution_id (FK)         │
                               │ diagram_type (ENUM)      │ (ARCHITECTURE/DATA_FLOW/ENTITY/OTHER)
                               │ title                    │
                               │ image_url                │
                               │ description              │
                               │ display_order            │
                               └──────────────────────────┘

system_design_cases (续)
         │
         │ 1:N
         ↓
┌──────────────────────────────────────────┐
│  user_case_notes (双轨制)                 │
├──────────────────────────────────────────┤
│ id (PK)                                  │
│ case_id (FK)                             │
│ user_id (FK)                             │
│                                          │
│ -- 6步骤字段 (step1-6)                   │
│ step1_requirements (TEXT)                │
│ step2_entities (TEXT)                    │
│ step3_api (TEXT)                         │
│ step4_data_flow (TEXT)                   │
│ step5_architecture (TEXT)                │
│ step6_deep_dive (TEXT)                   │
│                                          │
│ -- 7个结构化关键点 (kp_*)                 │
│ kp_requirement (TEXT)                    │
│ kp_nfr (TEXT)                            │
│ kp_entity (TEXT)                         │
│ kp_components (TEXT)                     │
│ kp_api (TEXT)                            │
│ kp_object1 (TEXT)                        │
│ kp_object2 (TEXT)                        │
│                                          │
│ -- 附件和总结                            │
│ architecture_diagram_url                 │
│ key_points (TEXT)                        │
│                                          │
│ UNIQUE(case_id, user_id)                 │
└──────────────────────────────────────────┘
```

### 2.2 表结构详细设计

#### 表1: system_design_cases (案例主表)

**用途**: 存储典型案例的基本信息

**实际DDL** (V15 migration):
```sql
CREATE TABLE system_design_cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id BIGINT NOT NULL COMMENT '关联到系统设计skill (固定为2)',

    -- 基本信息
    title VARCHAR(500) NOT NULL COMMENT '案例标题(如:设计Twitter)',
    case_description TEXT COMMENT '案例描述(Markdown格式)',
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    difficulty_rating INT COMMENT '难度评分 1-10',

    -- 元数据
    company_tags TEXT COMMENT '面试公司标签 (JSON数组: ["Google", "Meta"])',
    related_focus_areas TEXT COMMENT '关联的Focus Area IDs (JSON数组: [1, 5, 12])',

    -- 管理字段
    is_official BOOLEAN DEFAULT TRUE COMMENT '是否官方案例',
    created_by_user_id BIGINT COMMENT '创建人ID',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_skill_id (skill_id),
    INDEX idx_difficulty (difficulty_rating),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='系统设计案例表';
```

---

#### 表2: case_resources (案例学习资源表)

**用途**: 存储案例的学习资源(视频、文章)

**实际DDL** (V15 migration):
```sql
CREATE TABLE case_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL COMMENT '关联的案例ID',

    -- 资源信息
    resource_type ENUM('VIDEO', 'ARTICLE') NOT NULL COMMENT '资源类型',
    title VARCHAR(500) NOT NULL COMMENT '资源标题',
    url VARCHAR(1000) NOT NULL COMMENT '资源URL',
    description TEXT COMMENT '资源描述',

    -- 管理字段
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (case_id) REFERENCES system_design_cases(id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='案例学习资源表';
```

---

#### 表3: case_solutions (案例参考答案表)

**用途**: 存储案例的参考答案(支持多个方案)

**实际DDL** (V15 migration):
```sql
CREATE TABLE case_solutions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL COMMENT '关联的案例ID',

    -- 方案信息
    solution_name VARCHAR(200) NOT NULL COMMENT '方案名称(如:方案A-基础版本,方案B-高级版本)',
    author VARCHAR(200) COMMENT '作者',

    -- 6个步骤内容（Markdown格式）
    step1_requirements TEXT COMMENT '步骤1:需求澄清与功能列表',
    step2_entities TEXT COMMENT '步骤2:核心实体定义',
    step3_api TEXT COMMENT '步骤3:API设计',
    step4_data_flow TEXT COMMENT '步骤4:数据流设计',
    step5_architecture TEXT COMMENT '步骤5:高层架构设计',
    step6_deep_dive TEXT COMMENT '步骤6:深入讨论',

    -- 管理字段
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (case_id) REFERENCES system_design_cases(id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='案例参考答案表(支持多方案)';
```

---

#### 表4: solution_diagrams (参考答案图片表)

**用途**: 存储参考答案的配图

**实际DDL** (V15 migration):
```sql
CREATE TABLE solution_diagrams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    solution_id BIGINT NOT NULL COMMENT '关联的参考答案ID',

    -- 配图信息
    diagram_type ENUM('ARCHITECTURE', 'DATA_FLOW', 'ENTITY', 'OTHER') NOT NULL COMMENT '配图类型',
    title VARCHAR(200) COMMENT '配图标题',
    image_url VARCHAR(1000) NOT NULL COMMENT '图片URL',
    description TEXT COMMENT '配图说明',

    -- 管理字段
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (solution_id) REFERENCES case_solutions(id) ON DELETE CASCADE,
    INDEX idx_solution_id (solution_id),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='参考答案配图表';
```

---

#### 表5: user_case_notes (用户答题记录表 - 双轨制)

**用途**: 存储用户的答题内容（6步骤 + 7个结构化关键点）

**实际DDL** (V15 + V16 migration):
```sql
-- V15: 创建基础表
CREATE TABLE user_case_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL COMMENT '关联的案例ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',

    -- 用户答案（6个步骤，Markdown格式）
    step1_requirements TEXT COMMENT '步骤1:需求澄清与功能列表',
    step2_entities TEXT COMMENT '步骤2:核心实体定义',
    step3_api TEXT COMMENT '步骤3:API设计',
    step4_data_flow TEXT COMMENT '步骤4:数据流设计',
    step5_architecture TEXT COMMENT '步骤5:高层架构设计',
    step6_deep_dive TEXT COMMENT '步骤6:深入讨论',

    -- 用户架构图
    architecture_diagram_url VARCHAR(1000) COMMENT '用户上传的架构图URL',

    -- 要点总结
    key_points TEXT COMMENT '要点总结(Markdown格式)',

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (case_id) REFERENCES system_design_cases(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_case (case_id, user_id) COMMENT '一个用户对一个案例只能有一条记录',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='用户答题记录表';

-- V16: 添加结构化关键点字段
ALTER TABLE user_case_notes
    ADD COLUMN kp_requirement TEXT COMMENT '需求分析(Requirement)' AFTER user_id,
    ADD COLUMN kp_nfr TEXT COMMENT '非功能性需求(Non-Functional Requirements)' AFTER kp_requirement,
    ADD COLUMN kp_entity TEXT COMMENT '核心实体定义(Entity)' AFTER kp_nfr,
    ADD COLUMN kp_components TEXT COMMENT '关键组件(Key Components)' AFTER kp_entity,
    ADD COLUMN kp_api TEXT COMMENT 'API设计(API)' AFTER kp_components,
    ADD COLUMN kp_object1 TEXT COMMENT '核心对象1设计(Object1)' AFTER kp_api,
    ADD COLUMN kp_object2 TEXT COMMENT '核心对象2设计(Object2)' AFTER kp_object1;
```

**字段说明**:
- **step1-6字段**: 完整的6步骤系统设计答案（Markdown长文本）
- **kp_*字段**: 快速记录关键要点（Markdown短文本）
- **双轨制**: 用户可选择使用哪种方式或同时使用

---

### 2.3 数据关联规则

```
skills (系统设计, id=2)
  ↓ 1:N
major_categories (核心概念、关键技术、设计模式) - 3个
  ↓ 1:N
focus_areas
  ↓ 1:N
learning_contents (学习资料)

skills (系统设计, id=2)
  ↓ 1:N
system_design_cases (典型案例)
  ↓ 1:N
case_resources (学习资源)
  ↓ 1:N
case_solutions (参考答案,支持多个方案)
  ↓ 1:N
solution_diagrams (配图)

system_design_cases
  ↓ 1:N
user_case_notes (用户答题记录,UNIQUE(case_id, user_id))
```

**关键规则**:
- 每个案例可以有多个参考答案(Case Solutions)
- 每个参考答案可以有多张配图(Solution Diagrams)
- 每个用户对每个案例只能有一条答题记录(User Case Notes)
- 用户答题记录不关联特定的参考答案(用户可以参考多个方案)
- 用户答题记录支持双轨制：6步骤（step1-6）+ 7个结构化关键点（kp_*）

**级联删除规则**:
- 删除Skill → 级联删除所有Major Categories → 级联删除所有Focus Areas → 级联删除所有Learning Contents
- 删除Case → 级联删除Case Resources、Case Solutions、User Case Notes
- 删除Case Solution → 级联删除Solution Diagrams

---

## 3. API设计

### 3.1 管理端API

#### 3.1.1 案例管理 (SystemDesignCaseController)

**GET /api/admin/system-design-cases**
- 功能: 分页查询案例列表
- 权限: 管理员
- 参数: page, size, difficulty
- 返回: Page<SystemDesignCaseDTO>

**POST /api/admin/system-design-cases**
- 功能: 创建新案例
- 权限: 管理员
- Request Body: SystemDesignCaseDTO

**PUT /api/admin/system-design-cases/{id}**
- 功能: 更新案例
- 权限: 管理员
- Request Body: SystemDesignCaseDTO

**DELETE /api/admin/system-design-cases/{id}**
- 功能: 删除案例(级联删除resources、solutions、user_notes)
- 权限: 管理员

#### 3.1.2 学习资源管理 (CaseResourceController)

**POST /api/admin/system-design-cases/{caseId}/resources**
- 功能: 为案例添加学习资源
- 权限: 管理员
- Request Body: CaseResourceDTO

**DELETE /api/admin/case-resources/{id}**
- 功能: 删除学习资源
- 权限: 管理员

#### 3.1.3 参考答案管理 (CaseSolutionController)

**GET /api/admin/system-design-cases/{caseId}/solutions**
- 功能: 获取案例的所有参考答案
- 权限: 管理员
- 返回: List<CaseSolutionDTO>

**POST /api/admin/system-design-cases/{caseId}/solutions**
- 功能: 为案例添加参考答案
- 权限: 管理员
- Request Body: CaseSolutionDTO

**PUT /api/admin/case-solutions/{id}**
- 功能: 更新参考答案
- 权限: 管理员
- Request Body: CaseSolutionDTO

**DELETE /api/admin/case-solutions/{id}**
- 功能: 删除参考答案(级联删除配图)
- 权限: 管理员

#### 3.1.4 配图管理

**POST /api/admin/case-solutions/{solutionId}/diagrams**
- 功能: 为参考答案添加配图
- 权限: 管理员
- Request Body: multipart/form-data

**DELETE /api/admin/solution-diagrams/{id}**
- 功能: 删除配图
- 权限: 管理员

---

### 3.2 用户端API

#### 3.2.1 案例浏览 (SystemDesignCaseController)

**GET /api/system-design-cases**
- 功能: 获取所有案例列表
- 权限: 已登录用户
- 参数: difficulty, page, size
- 返回: List<SystemDesignCaseDTO>

**GET /api/system-design-cases/{id}**
- 功能: 获取案例详情(包含学习资源)
- 权限: 已登录用户
- 返回: SystemDesignCaseDTO

#### 3.2.2 参考答案查看 (CaseSolutionController)

**GET /api/system-design-cases/{caseId}/solutions**
- 功能: 获取案例的所有参考答案
- 权限: 已登录用户
- 返回: List<CaseSolutionDTO>

**GET /api/case-solutions/{id}**
- 功能: 获取单个参考答案详情(包含配图)
- 权限: 已登录用户
- 返回: CaseSolutionDTO

#### 3.2.3 用户答题 (UserSystemDesignController)

**GET /api/system-design-cases/{caseId}/my-note**
- 功能: 获取当前用户对该案例的答题记录
- 权限: 已登录用户
- 返回: UserCaseNoteDTO (包含step1-6和kp_*字段)

**POST /api/system-design-cases/{caseId}/my-note**
- 功能: 保存/更新用户答题记录
- 权限: 已登录用户
- Request Body:
```json
{
  "step1Requirements": "...",
  "step2Entities": "...",
  "step3Api": "...",
  "step4DataFlow": "...",
  "step5Architecture": "...",
  "step6DeepDive": "...",
  "kpRequirement": "...",
  "kpNfr": "...",
  "kpEntity": "...",
  "kpComponents": "...",
  "kpApi": "...",
  "kpObject1": "...",
  "kpObject2": "...",
  "architectureDiagramUrl": "https://...",
  "keyPoints": "..."
}
```

**GET /api/system-design/my-notes**
- 功能: 获取当前用户的所有答题记录（用于学习总结页面）
- 权限: 已登录用户
- 返回: List<UserCaseNoteWithCaseDTO>

#### 3.2.4 架构图上传

**POST /api/system-design-cases/{caseId}/upload-diagram**
- 功能: 上传架构图
- 权限: 已登录用户
- Request: multipart/form-data (file)
- Response: { "url": "https://..." }

---

## 4. 前端设计

### 4.1 管理端页面

#### 4.1.1 基础知识管理页面

**页面**: `/admin/system-design-basics`
**组件**: SystemDesignBasicsManagement.vue
**复用**: Phase 4的AlgorithmContentManagement.vue布局
**功能**: 管理3个大分类下的Focus Area和学习资料

**关键实现**:
- 左侧大分类Tab切换
- Focus Area列表展示
- 右侧学习资料CRUD
- Markdown编辑器

#### 4.1.2 典型案例管理页面

**页面**: `/admin/system-design-cases`
**组件**: SystemDesignCaseManagement.vue

**组件结构**:
```vue
SystemDesignCaseManagement.vue
├─ 左侧: 案例列表
│   ├─ 案例标题
│   ├─ 难度标签 (EASY/MEDIUM/HARD + 评分)
│   └─ 选中状态
├─ 右侧上部: 案例详情编辑
│   ├─ 基本信息 (标题、描述、难度、公司、关联知识)
│   └─ 学习资源管理 (视频、文章)
└─ 右侧下部: 参考答案管理
    ├─ 答案选择器 (方案A、方案B等)
    ├─ 6个Tab (需求、实体、API、数据流、架构、深入)
    └─ Markdown编辑器 + 图片上传
```

**关键实现**:
- 案例CRUD操作
- 学习资源管理
- 多参考答案管理
- 6步骤Tab切换
- Markdown编辑器集成

---

### 4.2 用户端页面

#### 4.2.1 基础知识学习页面

**页面**: `/system-design/basics`
**组件**: SystemDesignBasics.vue
**复用**: Phase 4的AlgorithmLearning.vue布局
**功能**: 浏览3个大分类下的Focus Area和学习资料

#### 4.2.2 典型案例实战页面

**页面**: `/system-design/cases`
**组件**: SystemDesignCases.vue

**两种模式**:

**查看模式**:
```vue
SystemDesignCases.vue (查看模式)
├─ 左侧: 案例列表
│   ├─ 案例标题
│   ├─ 难度标签
│   └─ 答题状态标记 (✅ 已答题)
├─ 右侧上部: 案例详情
│   ├─ 基本信息 (标题、描述、难度、公司、关联知识)
│   └─ 学习资源 (视频、文章链接)
└─ 右侧下部: 参考答案
    ├─ 答案选择器 (方案A、方案B等)
    ├─ 6个Tab
    └─ Markdown渲染 + 图片展示
```

**编辑模式**:
```vue
SystemDesignCases.vue (编辑模式)
├─ 左侧: 参考答案 (可切换显示/隐藏)
│   ├─ 显示/隐藏按钮
│   ├─ 答案选择器
│   ├─ 6个Tab
│   └─ Markdown渲染 (只读)
└─ 右侧: 我的答案 (可编辑)
    ├─ 14个Tab区域
    │   ├─ 6个步骤Tab (step1-6)
    │   ├─ 7个关键点Tab (Requirement、NFR、Entity、Components、API、Object1、Object2)
    │   └─ 架构图Tab (上传架构图)
    ├─ Markdown编辑器 (每个Tab)
    └─ 保存/取消按钮
```

**关键实现**:
- 查看/编辑模式切换
- 答案可见性控制（显示/隐藏参考答案）
- 14个Tab管理（6步骤 + 7关键点 + 1架构图）
- 左右对比布局
- Markdown编辑器集成
- 架构图上传

#### 4.2.3 学习总结页面

**页面**: `/system-design/summary`
**组件**: SystemDesignSummary.vue

**布局**: 全屏表格视图

**组件结构**:
```vue
SystemDesignSummary.vue
├─ Header (渐变背景)
│   ├─ 标题: 系统设计 - 学习总结
│   └─ 副标题: 系统设计案例关键点总结
└─ Main Content (表格)
    ├─ 表头
    │   ├─ 案例名 (固定列)
    │   ├─ 📋 Requirement
    │   ├─ ⚡ NFR
    │   ├─ 📦 Entity
    │   ├─ 🧩 Components
    │   ├─ 🔌 API
    │   ├─ 🎯 Object1
    │   └─ 🎯 Object2
    └─ 表格行 (每个案例一行)
        ├─ 案例名 (可点击跳转到案例详情)
        └─ 7个关键点单元格 (Markdown渲染)
```

**关键实现**:
- 横向对比所有案例
- 案例名固定列，可点击跳转
- 表格内Markdown渲染
- 渐变背景和悬停效果
- 自动加载所有用户笔记

---

## 5. 业务逻辑设计

### 5.1 Service层核心逻辑

#### 5.1.1 SystemDesignCaseService

```java
@Service
public class SystemDesignCaseService {

    /**
     * 创建案例(包含基本信息)
     */
    @Transactional
    public SystemDesignCaseDTO createCase(SystemDesignCaseDTO dto, Long userId) {
        // 1. 创建案例
        SystemDesignCase case = new SystemDesignCase();
        case.setSkillId(2L); // 固定为系统设计skill
        case.setTitle(dto.getTitle());
        case.setCaseDescription(dto.getCaseDescription());
        case.setDifficulty(dto.getDifficulty());
        case.setDifficultyRating(dto.getDifficultyRating());

        // 2. 解析company_tags和related_focus_areas的JSON
        case.setCompanyTags(objectMapper.writeValueAsString(dto.getCompanyTags()));
        case.setRelatedFocusAreas(objectMapper.writeValueAsString(dto.getRelatedFocusAreas()));

        case.setCreatedByUserId(userId);
        case.setIsOfficial(true);

        // 3. 保存到数据库
        case = caseRepository.save(case);
        return toDTO(case);
    }

    /**
     * 获取案例详情(包含学习资源)
     */
    public SystemDesignCaseDTO getCaseDetail(Long caseId) {
        // 1. 获取案例基本信息
        SystemDesignCase case = caseRepository.findById(caseId)
            .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        // 2. 获取学习资源列表
        List<CaseResource> resources = resourceRepository.findByCaseIdOrderByDisplayOrder(caseId);

        // 3. 组装DTO
        SystemDesignCaseDTO dto = toDTO(case);
        dto.setResources(resources.stream().map(this::toResourceDTO).collect(Collectors.toList()));

        return dto;
    }
}
```

#### 5.1.2 CaseSolutionService

```java
@Service
public class CaseSolutionService {

    /**
     * 获取案例的所有参考答案
     */
    public List<CaseSolutionDTO> getSolutionsByCase(Long caseId) {
        // 1. 查询所有答案
        List<CaseSolution> solutions = solutionRepository.findByCaseIdOrderByDisplayOrder(caseId);

        // 2. 转换为DTO
        return solutions.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 创建参考答案
     */
    @Transactional
    public CaseSolutionDTO createSolution(Long caseId, CaseSolutionDTO dto) {
        // 1. 创建答案
        CaseSolution solution = new CaseSolution();
        solution.setCaseId(caseId);
        solution.setSolutionName(dto.getSolutionName());
        solution.setAuthor(dto.getAuthor());

        // 2. 保存6个步骤内容
        solution.setStep1Requirements(dto.getStep1Requirements());
        solution.setStep2Entities(dto.getStep2Entities());
        solution.setStep3Api(dto.getStep3Api());
        solution.setStep4DataFlow(dto.getStep4DataFlow());
        solution.setStep5Architecture(dto.getStep5Architecture());
        solution.setStep6DeepDive(dto.getStep6DeepDive());

        // 3. 保存
        solution = solutionRepository.save(solution);
        return toDTO(solution);
    }

    /**
     * 获取答案详情(包含配图)
     */
    public CaseSolutionDTO getSolutionDetail(Long solutionId) {
        // 1. 获取答案基本信息
        CaseSolution solution = solutionRepository.findById(solutionId)
            .orElseThrow(() -> new ResourceNotFoundException("Solution not found"));

        // 2. 获取配图列表
        List<SolutionDiagram> diagrams = diagramRepository.findBySolutionIdOrderByDisplayOrder(solutionId);

        // 3. 组装DTO
        CaseSolutionDTO dto = toDTO(solution);
        dto.setDiagrams(diagrams.stream().map(this::toDiagramDTO).collect(Collectors.toList()));

        return dto;
    }
}
```

#### 5.1.3 UserCaseNoteService（双轨制）

```java
@Service
public class UserCaseNoteService {

    /**
     * 保存/更新用户答题记录（双轨制：6步骤 + 7个关键点）
     */
    @Transactional
    public UserCaseNoteDTO saveOrUpdateNote(Long caseId, Long userId, UserCaseNoteDTO dto) {
        // 1. 查找现有记录(UNIQUE约束)
        Optional<UserCaseNote> existingNote = noteRepository.findByCaseIdAndUserId(caseId, userId);

        UserCaseNote note;
        if (existingNote.isPresent()) {
            // 2. 如果存在则更新
            note = existingNote.get();
        } else {
            // 3. 否则创建
            note = new UserCaseNote();
            note.setCaseId(caseId);
            note.setUserId(userId);
        }

        // 4. 保存6个步骤内容
        note.setStep1Requirements(dto.getStep1Requirements());
        note.setStep2Entities(dto.getStep2Entities());
        note.setStep3Api(dto.getStep3Api());
        note.setStep4DataFlow(dto.getStep4DataFlow());
        note.setStep5Architecture(dto.getStep5Architecture());
        note.setStep6DeepDive(dto.getStep6DeepDive());

        // 5. 保存7个结构化关键点
        note.setKpRequirement(dto.getKpRequirement());
        note.setKpNfr(dto.getKpNfr());
        note.setKpEntity(dto.getKpEntity());
        note.setKpComponents(dto.getKpComponents());
        note.setKpApi(dto.getKpApi());
        note.setKpObject1(dto.getKpObject1());
        note.setKpObject2(dto.getKpObject2());

        // 6. 保存架构图URL和要点总结
        note.setArchitectureDiagramUrl(dto.getArchitectureDiagramUrl());
        note.setKeyPoints(dto.getKeyPoints());

        note = noteRepository.save(note);
        return toDTO(note);
    }

    /**
     * 获取用户的答题记录
     */
    public UserCaseNoteDTO getUserNote(Long caseId, Long userId) {
        // 1. 查询用户的答题记录
        Optional<UserCaseNote> note = noteRepository.findByCaseIdAndUserId(caseId, userId);

        // 2. 返回DTO(如果不存在返回null)
        return note.map(this::toDTO).orElse(null);
    }

    /**
     * 获取用户的所有答题记录（用于学习总结页面）
     */
    public List<UserCaseNoteWithCaseDTO> getUserNotes(Long userId) {
        // 1. 查询用户的所有答题记录
        List<UserCaseNote> notes = noteRepository.findByUserIdOrderByUpdatedAtDesc(userId);

        // 2. 转换为DTO并包含案例信息
        return notes.stream()
            .map(note -> {
                UserCaseNoteWithCaseDTO dto = new UserCaseNoteWithCaseDTO();
                dto.setNote(toDTO(note));

                // 获取案例信息
                SystemDesignCase case = caseRepository.findById(note.getCaseId()).orElse(null);
                if (case != null) {
                    dto.setCaseTitle(case.getTitle());
                    dto.setCaseId(case.getId());
                }

                return dto;
            })
            .collect(Collectors.toList());
    }
}
```

---

### 5.2 数据访问层

#### 5.2.1 SystemDesignCaseRepository

```java
@Repository
public interface SystemDesignCaseRepository extends JpaRepository<SystemDesignCase, Long> {

    // 按Skill查询案例
    List<SystemDesignCase> findBySkillIdOrderByDisplayOrder(Long skillId);

    // 按难度过滤
    List<SystemDesignCase> findBySkillIdAndDifficultyOrderByDisplayOrder(
        Long skillId, String difficulty);
}
```

#### 5.2.2 CaseSolutionRepository

```java
@Repository
public interface CaseSolutionRepository extends JpaRepository<CaseSolution, Long> {

    // 获取案例的所有参考答案
    List<CaseSolution> findByCaseIdOrderByDisplayOrder(Long caseId);
}
```

#### 5.2.3 UserCaseNoteRepository

```java
@Repository
public interface UserCaseNoteRepository extends JpaRepository<UserCaseNote, Long> {

    // 获取用户的答题记录
    Optional<UserCaseNote> findByCaseIdAndUserId(Long caseId, Long userId);

    // 获取用户的所有答题记录
    List<UserCaseNote> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
```

---

## 6. 数据迁移

### 6.1 Migration V15 (Phase 5表创建)

**文件**: `V15__create_system_design_tables.sql`

**创建的表**:
1. system_design_cases - 案例主表
2. case_resources - 学习资源表
3. case_solutions - 参考答案表
4. solution_diagrams - 配图表
5. user_case_notes - 用户答题记录表（基础版本）

### 6.2 Migration V16 (添加结构化关键点)

**文件**: `V16__add_structured_fields_to_user_case_notes.sql`

**修改**: 为user_case_notes表添加7个结构化关键点字段
- kp_requirement
- kp_nfr
- kp_entity
- kp_components
- kp_api
- kp_object1
- kp_object2

---

## 7. 性能优化

### 7.1 数据库索引

```sql
-- system_design_cases表索引
CREATE INDEX idx_skill_id ON system_design_cases(skill_id);
CREATE INDEX idx_difficulty ON system_design_cases(difficulty_rating);
CREATE INDEX idx_display_order ON system_design_cases(display_order);

-- case_resources表索引
CREATE INDEX idx_case_id ON case_resources(case_id);
CREATE INDEX idx_display_order ON case_resources(display_order);

-- case_solutions表索引
CREATE INDEX idx_case_id ON case_solutions(case_id);
CREATE INDEX idx_display_order ON case_solutions(display_order);

-- solution_diagrams表索引
CREATE INDEX idx_solution_id ON solution_diagrams(solution_id);
CREATE INDEX idx_display_order ON solution_diagrams(display_order);

-- user_case_notes表索引
UNIQUE KEY unique_user_case (case_id, user_id);
INDEX idx_user_id (user_id);
```

### 7.2 查询优化

**案例列表查询**:
- 分页加载(Page<SystemDesignCase>)
- 只查询必要字段(不加载6个步骤内容)

**参考答案切换**:
- 按需加载(切换方案时才查询对应答案)
- 配图懒加载

**学习总结页面**:
- 一次性加载所有用户笔记
- 只加载kp_*字段，不加载step1-6字段（减少数据传输）

---

## 8. 安全设计

### 8.1 权限控制

**管理端API**:
- 所有 `/api/admin/system-design-cases/**` 需要管理员权限
- 使用 `@PreAuthorize("hasRole('ADMIN')")` 注解

**用户端API**:
- 所有 `/api/system-design-cases/**` 需要登录
- 用户只能查看和编辑自己的答题记录

### 8.2 数据验证

```java
@NotNull(message = "Case ID is required")
private Long caseId;

@NotBlank(message = "Title is required")
@Size(max = 500, message = "Title must not exceed 500 characters")
private String title;

@Pattern(regexp = "^(EASY|MEDIUM|HARD)$", message = "Invalid difficulty")
private String difficulty;
```

---

## 9. 实现与设计的主要差异

### 9.1 数据模型差异

**设计文档**: 参考答案表有`is_primary`字段标记主要答案
**实际实现**: 取消了`is_primary`字段，所有答案平等展示

**原因**: 简化管理，用户可以自由选择参考哪个方案

### 9.2 用户答题字段差异

**设计文档**: 只有6个步骤字段（step1-6）
**实际实现**: 双轨制 - 6个步骤字段 + 7个结构化关键点字段（kp_*）

**原因**:
- V16 migration新增了kp_*字段
- 提供更灵活的知识点管理方式
- 支持学习总结页面的横向对比

### 9.3 新增功能

**设计文档**: 未包含学习总结页面
**实际实现**: 新增`/system-design/summary`页面和`SystemDesignSummary.vue`组件

**原因**:
- 用户需要横向对比所有案例的关键要点
- 利用kp_*字段实现结构化展示

### 9.4 答案可见性控制

**设计文档**: 编辑模式下左右对比，参考答案始终显示
**实际实现**: 编辑模式下可切换显示/隐藏参考答案

**原因**:
- 用户希望先独立思考再查看参考答案
- 提升学习效果

---

## 10. 附录

### 10.1 术语表

| 术语 | 英文 | 说明 |
|------|------|------|
| 案例 | Case | 典型系统设计案例(如设计Twitter) |
| 参考答案 | Solution | 案例的设计方案(支持多个方案) |
| 步骤 | Step | 答题框架的6个步骤 |
| 关键点 | Key Point (KP) | 结构化关键点（kp_*字段） |
| 配图 | Diagram | 参考答案的架构图 |
| 双轨制 | Dual-track | 6步骤 + 7个结构化关键点 |

### 10.2 参考文档

- [Phase 5 详细需求 v1.1](../requirement/Phase5-详细需求.md)
- [HelloInterview](https://www.hellointerview.com/)
- [ARCHITECTURE.md](./ARCHITECTURE.md)

---

**文档版本**: v1.1
**创建时间**: 2025-12-25
**更新时间**: 2025-12-26
**状态**: ✅ Phase 5 已完成
**更新说明**:
- 基于Phase 5实际实现更新设计文档
- 补充V16 migration（结构化关键点字段）
- 新增学习总结页面设计
- 新增答案可见性控制
- 补充实现与设计的主要差异
- 更新所有ER图和表结构为实际DDL
