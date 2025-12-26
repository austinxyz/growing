# Phase 5 设计文档 - 系统设计学习模块

> **架构版本**: v1.0
> **状态**: 🚧 待实施
> **创建时间**: 2025-12-25
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
│    * 学习资源管理              │    * 6个步骤Tab              │
│    * 参考答案管理(6个Tab)      │    * 架构图上传              │
│    * 支持多个方案              │    * 要点总结                │
└──────────────────────────────────────────────────────────────┘
                           ↕ RESTful API
┌──────────────────────────────────────────────────────────────┐
│                   后端 (Spring Boot)                          │
├──────────────────────────────────────────────────────────────┤
│  Controller层                                                 │
│  - SystemDesignCaseController (案例管理API)                  │
│  - CaseResourceController (学习资源API)                      │
│  - CaseSolutionController (参考答案API)                      │
│  - UserCaseNoteController (用户答题API)                      │
├──────────────────────────────────────────────────────────────┤
│  Service层                                                    │
│  - SystemDesignCaseService (案例CRUD)                        │
│  - CaseResourceService (资源管理)                            │
│  - CaseSolutionService (答案管理,支持多方案)                │
│  - SolutionDiagramService (配图管理)                         │
│  - UserCaseNoteService (用户答题记录)                        │
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
│  - user_case_notes (用户答题记录表)                          │
│  复用:                                                        │
│  - skills (系统设计Skill)                                    │
│  - major_categories (3个大分类)                              │
│  - focus_areas (基础知识Focus Areas)                         │
│  - learning_contents (基础知识学习资料)                      │
└──────────────────────────────────────────────────────────────┘
```

### 1.4 与现有架构的集成

**复用Phase 2/4的概念(基础知识模块)**:
- ✅ **Skill** - "系统设计"(新建)
- ✅ **Major Category** - 3个大分类(核心概念、关键技术、设计模式)
- ✅ **Focus Area** - 具体知识点(待用户提供清单)
- ✅ **Learning Content** - 学习资料(文章、视频)

**新增概念(典型案例模块)**:
- 🆕 **SystemDesignCase** - 典型案例(如设计Twitter)
- 🆕 **CaseResource** - 案例学习资源(视频、文章)
- 🆕 **CaseSolution** - 参考答案(支持多个方案,每个方案6个步骤)
- 🆕 **SolutionDiagram** - 参考答案配图
- 🆕 **UserCaseNote** - 用户答题记录(6个步骤 + 架构图 + 要点总结)

**架构优势**:
- 🎯 **独立性** - 典型案例模块独立于Focus Area,不依赖learning_stages
- 🎯 **灵活性** - 每个案例支持多个参考答案方案
- 🎯 **扩展性** - JSON字段支持公司标签、关联知识点等扩展信息

---

## 2. 数据模型设计

### 2.1 实际ER图

```
                    ┌───────────────────┐
                    │      skills       │
                    ├───────────────────┤
                    │ id (PK)           │
                    │ name              │  (系统设计)
                    └───────────────────┘
                             │
                 ┌───────────┴───────────┐
                 │                       │
                 │ 1:N (基础知识)         │ 1:N (典型案例)
                 ↓                       ↓
    ┌─────────────────────┐    ┌──────────────────────────┐
    │ major_categories    │    │ system_design_cases      │ (案例主表)
    ├─────────────────────┤    ├──────────────────────────┤
    │ id (PK)             │    │ id (PK)                  │
    │ name                │    │ skill_id (FK)            │
    │ (核心概念、         │    │ title                    │
    │  关键技术、         │    │ case_description (TEXT)  │
    │  设计模式)          │    │ difficulty               │
    └─────────────────────┘    │ difficulty_rating (1-10) │
             │                 │ company_tags (JSON)      │
             │ 1:N             │ related_focus_areas (JSON)│
             ↓                 │ is_official              │
    ┌─────────────────────┐    │ created_by_user_id       │
    │   focus_areas       │    │ display_order            │
    ├─────────────────────┤    └──────────────────────────┘
    │ id (PK)             │             │
    │ skill_id (FK)       │             │ 1:N
    │ name                │             ↓
    │ major_category_id   │    ┌──────────────────────────┐
    └─────────────────────┘    │   case_resources         │ (学习资源)
             │                 ├──────────────────────────┤
             │ 1:N             │ id (PK)                  │
             ↓                 │ case_id (FK)             │
    ┌─────────────────────┐    │ resource_type            │ (video/article)
    │ learning_contents   │    │ resource_url             │
    ├─────────────────────┤    │ resource_title           │
    │ id (PK)             │    │ display_order            │
    │ focus_area_id (FK)  │    └──────────────────────────┘
    │ stage_id (FK)       │
    │ content_type        │    system_design_cases (续)
    │ title, url, author  │             │
    └─────────────────────┘             │ 1:N
                                        ↓
                               ┌──────────────────────────┐
                               │   case_solutions         │ (参考答案,支持多方案)
                               ├──────────────────────────┤
                               │ id (PK)                  │
                               │ case_id (FK)             │
                               │ solution_title           │ (方案A、方案B等)
                               │ author_name              │
                               │ is_primary               │ (主要答案标记)
                               │ step1_requirements_and_nfr (TEXT)│
                               │ step2_core_entities (TEXT)│
                               │ step3_api_design (TEXT)  │
                               │ step4_data_flow (TEXT)   │
                               │ step5_high_level_design (TEXT)│
                               │ step6_deep_dive_and_tradeoffs (TEXT)│
                               │ display_order            │
                               └──────────────────────────┘
                                        │
                                        │ 1:N
                                        ↓
                               ┌──────────────────────────┐
                               │  solution_diagrams       │ (配图)
                               ├──────────────────────────┤
                               │ id (PK)                  │
                               │ solution_id (FK)         │
                               │ diagram_url              │
                               │ diagram_title            │
                               │ diagram_description      │
                               │ step_number (1-6)        │
                               │ display_order            │
                               └──────────────────────────┘

system_design_cases (续)
         │
         │ 1:N
         ↓
┌──────────────────────────┐
│  user_case_notes         │ (用户答题记录)
├──────────────────────────┤
│ id (PK)                  │
│ case_id (FK)             │
│ user_id (FK)             │
│ user_step1_requirements_and_nfr (TEXT)│
│ user_step2_core_entities (TEXT)│
│ user_step3_api_design (TEXT)│
│ user_step4_data_flow (TEXT)│
│ user_step5_high_level_design (TEXT)│
│ user_step6_deep_dive_and_tradeoffs (TEXT)│
│ architecture_diagram_url │
│ key_takeaways (TEXT)     │
│ UNIQUE(case_id, user_id) │
└──────────────────────────┘
```

### 2.2 表结构详细设计

#### 表1: system_design_cases (案例主表)

**用途**: 存储典型案例的基本信息

```sql
CREATE TABLE system_design_cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id BIGINT NOT NULL COMMENT '关联到系统设计skill',

    -- 基本信息
    title VARCHAR(500) NOT NULL COMMENT '案例标题(如:设计Twitter)',
    case_description TEXT COMMENT '案例描述(Markdown格式)',
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    difficulty_rating INT COMMENT '难度评分 1-10',

    -- 元数据
    company_tags TEXT COMMENT '面试公司标签 (JSON数组: ["Google", "Meta"])',
    related_focus_areas TEXT COMMENT '关联的Focus Area IDs (JSON数组: [1, 5, 12])',

    -- 管理字段
    is_official BOOLEAN DEFAULT TRUE,
    created_by_user_id BIGINT,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_skill_id (skill_id),
    INDEX idx_difficulty (difficulty_rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='系统设计案例表';
```

---

#### 表2: case_resources (案例学习资源表)

**用途**: 存储案例的学习资源(视频、文章)

```sql
CREATE TABLE case_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,

    -- 资源信息
    resource_type ENUM('video', 'article') NOT NULL COMMENT '资源类型:视频/文章',
    resource_url VARCHAR(500) NOT NULL COMMENT '资源链接',
    resource_title VARCHAR(200) COMMENT '资源标题',
    display_order INT DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (case_id) REFERENCES system_design_cases(id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='案例学习资源(视频、文章)';
```

---

#### 表3: case_solutions (案例参考答案表)

**用途**: 存储案例的参考答案(支持多个方案)

```sql
CREATE TABLE case_solutions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,

    -- 参考答案标识
    solution_title VARCHAR(200) COMMENT '答案标题(如:方案A - 基于MySQL)',
    author_name VARCHAR(100) COMMENT '作者名称',
    is_primary BOOLEAN DEFAULT FALSE COMMENT '是否为主要参考答案',

    -- 6个步骤(合并后)
    step1_requirements_and_nfr TEXT COMMENT '步骤1:需求分析 + 非功能需求',
    step2_core_entities TEXT COMMENT '步骤2:核心实体',
    step3_api_design TEXT COMMENT '步骤3:API设计',
    step4_data_flow TEXT COMMENT '步骤4:数据流',
    step5_high_level_design TEXT COMMENT '步骤5:高层设计',
    step6_deep_dive_and_tradeoffs TEXT COMMENT '步骤6:深入探讨 + 权衡考虑',

    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (case_id) REFERENCES system_design_cases(id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_primary (is_primary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='案例参考答案(支持多个参考答案)';
```

---

#### 表4: solution_diagrams (参考答案图片表)

**用途**: 存储参考答案的配图

```sql
CREATE TABLE solution_diagrams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    solution_id BIGINT NOT NULL,

    -- 图片信息
    diagram_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    diagram_title VARCHAR(200) COMMENT '图片标题',
    diagram_description TEXT COMMENT '图片说明',
    step_number INT COMMENT '关联的步骤(1-6)',
    display_order INT DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (solution_id) REFERENCES case_solutions(id) ON DELETE CASCADE,
    INDEX idx_solution_id (solution_id),
    INDEX idx_step (step_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='参考答案配图';
```

---

#### 表5: user_case_notes (用户答题记录表)

**用途**: 存储用户的答题内容

```sql
CREATE TABLE user_case_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    -- 用户的答题内容(6个步骤)
    user_step1_requirements_and_nfr TEXT COMMENT '用户步骤1:需求分析 + 非功能需求',
    user_step2_core_entities TEXT COMMENT '用户步骤2:核心实体',
    user_step3_api_design TEXT COMMENT '用户步骤3:API设计',
    user_step4_data_flow TEXT COMMENT '用户步骤4:数据流',
    user_step5_high_level_design TEXT COMMENT '用户步骤5:高层设计',
    user_step6_deep_dive_and_tradeoffs TEXT COMMENT '用户步骤6:深入探讨 + 权衡考虑',

    -- 附件和总结
    architecture_diagram_url VARCHAR(500) COMMENT '架构图URL',
    key_takeaways TEXT COMMENT '要点总结',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (case_id) REFERENCES system_design_cases(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_case_user (case_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='用户系统设计案例答题记录';
```

---

### 2.3 数据关联规则

```
skills (系统设计)
  ↓ 1:N
major_categories (核心概念、关键技术、设计模式) - 3个
  ↓ 1:N
focus_areas (待用户提供)
  ↓ 1:N
learning_contents (学习资料)

skills (系统设计)
  ↓ 1:N
system_design_cases (典型案例)
  ↓ 1:N
case_resources (学习资源)

system_design_cases
  ↓ 1:N
case_solutions (参考答案,支持多个方案)
  ↓ 1:N
solution_diagrams (配图)

system_design_cases
  ↓ 1:N
user_case_notes (用户答题记录,UNIQUE(case_id, user_id))
```

**级联删除规则**:
- 删除Skill → 级联删除所有Major Categories → 级联删除所有Focus Areas → 级联删除所有Learning Contents
- 删除Case → 级联删除Case Resources、Case Solutions、User Case Notes
- 删除Case Solution → 级联删除Solution Diagrams

---

## 3. API设计

### 3.1 管理端API

#### 3.1.1 案例管理

**GET /api/admin/system-design-cases**
- 功能: 分页查询案例列表
- 权限: 管理员
- 参数: page, size, difficulty

**POST /api/admin/system-design-cases**
- 功能: 创建新案例
- 权限: 管理员

**PUT /api/admin/system-design-cases/{id}**
- 功能: 更新案例
- 权限: 管理员

**DELETE /api/admin/system-design-cases/{id}**
- 功能: 删除案例(级联删除resources、solutions、user_notes)
- 权限: 管理员

#### 3.1.2 学习资源管理

**POST /api/admin/system-design-cases/{caseId}/resources**
- 功能: 为案例添加学习资源
- 权限: 管理员

**DELETE /api/admin/case-resources/{id}**
- 功能: 删除学习资源
- 权限: 管理员

#### 3.1.3 参考答案管理

**GET /api/admin/system-design-cases/{caseId}/solutions**
- 功能: 获取案例的所有参考答案
- 权限: 管理员

**POST /api/admin/system-design-cases/{caseId}/solutions**
- 功能: 为案例添加参考答案
- 权限: 管理员

**PUT /api/admin/case-solutions/{id}**
- 功能: 更新参考答案
- 权限: 管理员

**DELETE /api/admin/case-solutions/{id}**
- 功能: 删除参考答案(级联删除配图)
- 权限: 管理员

#### 3.1.4 配图管理

**POST /api/admin/case-solutions/{solutionId}/diagrams**
- 功能: 为参考答案添加配图
- 权限: 管理员

**DELETE /api/admin/solution-diagrams/{id}**
- 功能: 删除配图
- 权限: 管理员

---

### 3.2 用户端API

#### 3.2.1 案例浏览

**GET /api/system-design-cases**
- 功能: 获取所有案例列表
- 权限: 已登录用户
- 参数: difficulty, page, size

**GET /api/system-design-cases/{id}**
- 功能: 获取案例详情(包含学习资源)
- 权限: 已登录用户

#### 3.2.2 参考答案查看

**GET /api/system-design-cases/{caseId}/solutions**
- 功能: 获取案例的所有参考答案
- 权限: 已登录用户

**GET /api/case-solutions/{id}**
- 功能: 获取单个参考答案详情(包含配图)
- 权限: 已登录用户

#### 3.2.3 用户答题

**GET /api/system-design-cases/{caseId}/my-note**
- 功能: 获取当前用户对该案例的答题记录
- 权限: 已登录用户

**POST /api/system-design-cases/{caseId}/my-note**
- 功能: 保存/更新用户答题记录
- 权限: 已登录用户
- Request Body:
```json
{
  "userStep1RequirementsAndNfr": "...",
  "userStep2CoreEntities": "...",
  "userStep3ApiDesign": "...",
  "userStep4DataFlow": "...",
  "userStep5HighLevelDesign": "...",
  "userStep6DeepDiveAndTradeoffs": "...",
  "architectureDiagramUrl": "https://...",
  "keyTakeaways": "..."
}
```

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
**复用**: Phase 4的AlgorithmContentManagement.vue布局
**功能**: 管理3个大分类下的Focus Area和学习资料

#### 4.1.2 典型案例管理页面

**页面**: `/admin/system-design-cases`

**组件结构**:
```vue
SystemDesignCaseManagement.vue
├─ CaseList.vue (左侧案例列表)
├─ CaseDetailPanel.vue (右侧上部:案例详情)
│   ├─ 基本信息编辑
│   ├─ 学习资源管理
└─ CaseSolutionPanel.vue (右侧下部:参考答案管理)
    ├─ 答案列表(选择器)
    ├─ 6个Tab(需求、实体、API、数据流、架构、深入)
    └─ Markdown编辑器 + 图片上传
```

---

### 4.2 用户端页面

#### 4.2.1 基础知识学习页面

**页面**: `/system-design/basics`
**复用**: Phase 4的AlgorithmLearning.vue布局
**功能**: 浏览3个大分类下的Focus Area和学习资料

#### 4.2.2 典型案例实战页面

**页面**: `/system-design/cases`

**两种模式**:

**查看模式**:
```vue
SystemDesignCases.vue (查看模式)
├─ CaseList.vue (左侧案例列表)
├─ CaseDetailPanel.vue (右侧上部:案例详情)
│   ├─ 基本信息
│   ├─ 学习资源
└─ ReferenceSolutionPanel.vue (右侧下部:参考答案)
    ├─ 答案选择器(方案A、方案B等)
    ├─ 6个Tab(需求、实体、API、数据流、架构、深入)
    └─ Markdown渲染 + 图片展示
```

**编辑模式**:
```vue
SystemDesignCaseEditor.vue (编辑模式)
├─ ReferenceSolutionPanel.vue (左侧:参考答案,只读)
│   ├─ 6个Tab
│   └─ Markdown渲染
└─ UserAnswerEditor.vue (右侧:我的答案,可编辑)
    ├─ 6个Tab + Markdown编辑器
    ├─ 架构图上传
    └─ 要点总结
```

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
        // 2. 解析company_tags和related_focus_areas的JSON
        // 3. 保存到数据库
    }

    /**
     * 获取案例详情(包含学习资源)
     */
    public SystemDesignCaseDTO getCaseDetail(Long caseId) {
        // 1. 获取案例基本信息
        // 2. 获取学习资源列表
        // 3. 组装DTO
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
        // 2. 按display_order排序
        // 3. 标记主要答案
    }

    /**
     * 创建参考答案
     */
    @Transactional
    public CaseSolutionDTO createSolution(Long caseId, CaseSolutionDTO dto) {
        // 1. 创建答案
        // 2. 保存6个步骤内容
        // 3. 如果is_primary=true,取消其他答案的主要标记
    }

    /**
     * 获取答案详情(包含配图)
     */
    public CaseSolutionDTO getSolutionDetail(Long solutionId) {
        // 1. 获取答案基本信息
        // 2. 获取配图列表(按step_number分组)
        // 3. 组装DTO
    }
}
```

#### 5.1.3 UserCaseNoteService

```java
@Service
public class UserCaseNoteService {

    /**
     * 保存/更新用户答题记录
     */
    @Transactional
    public UserCaseNoteDTO saveOrUpdateNote(Long caseId, Long userId, UserCaseNoteDTO dto) {
        // 1. 查找现有记录(UNIQUE约束)
        // 2. 如果存在则更新,否则创建
        // 3. 保存6个步骤内容
        // 4. 保存架构图URL和要点总结
    }

    /**
     * 获取用户的答题记录
     */
    public UserCaseNoteDTO getUserNote(Long caseId, Long userId) {
        // 1. 查询用户的答题记录
        // 2. 返回DTO(如果不存在返回null)
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

    // 获取主要参考答案
    Optional<CaseSolution> findByCaseIdAndIsPrimaryTrue(Long caseId);
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

### 6.1 Migration V11 (Phase 5表创建)

```sql
-- V11__create_system_design_tables.sql

-- 案例主表
CREATE TABLE system_design_cases (...);

-- 学习资源表
CREATE TABLE case_resources (...);

-- 参考答案表
CREATE TABLE case_solutions (...);

-- 配图表
CREATE TABLE solution_diagrams (...);

-- 用户答题记录表
CREATE TABLE user_case_notes (...);
```

**初始数据导入**:
- 系统设计Skill
- 3个Major Categories(核心概念、关键技术、设计模式)
- Focus Areas(待用户提供清单)

---

## 7. 性能优化

### 7.1 数据库索引

```sql
-- system_design_cases表索引
CREATE INDEX idx_skill_id ON system_design_cases(skill_id);
CREATE INDEX idx_difficulty ON system_design_cases(difficulty_rating);

-- case_solutions表索引
CREATE INDEX idx_case_id ON case_solutions(case_id);
CREATE INDEX idx_primary ON case_solutions(is_primary);

-- solution_diagrams表索引
CREATE INDEX idx_solution_id ON solution_diagrams(solution_id);
CREATE INDEX idx_step ON solution_diagrams(step_number);

-- user_case_notes表索引
UNIQUE KEY uk_case_user (case_id, user_id);
INDEX idx_user_id (user_id);
```

### 7.2 查询优化

**案例列表查询**:
- 分页加载(Page<SystemDesignCase>)
- 只查询必要字段(不加载6个步骤内容)

**参考答案切换**:
- 按需加载(切换方案时才查询对应答案)
- 配图懒加载

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

(待实施后更新)

---

## 10. 附录

### 10.1 术语表

| 术语 | 英文 | 说明 |
|------|------|------|
| 案例 | Case | 典型系统设计案例(如设计Twitter) |
| 参考答案 | Solution | 案例的设计方案(支持多个方案) |
| 步骤 | Step | 答题框架的6个步骤 |
| 配图 | Diagram | 参考答案的架构图 |

### 10.2 参考文档

- [Phase 5 详细需求 v1.0](../requirement/Phase5-详细需求.md)
- [Phase 5 Prompt](../prompt/Phase5-系统设计学习模块.md)
- [HelloInterview](https://www.hellointerview.com/)
- [ARCHITECTURE.md](./ARCHITECTURE.md)

---

**文档版本**: v1.0
**创建时间**: 2025-12-25
**状态**: 🚧 待实施
**更新说明**:
- 基于Phase 5 prompt创建设计文档
- 参考Phase 4设计文档的格式和结构
- 明确5张新表的设计
- 定义6步骤答题框架
- 支持多参考答案方案
