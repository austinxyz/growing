# Growing App - Architecture Guide

> **Purpose**: Detailed technical architecture reference
> **For quick context recovery, see**: `/CLAUDE.md`
> **For UI design patterns, see**: `/docs/FRONTEND_UI_DESIGN_GUIDE.md`

## System Architecture

### Technology Stack

**Backend**:
- Java 17 + Spring Boot 3.2.0
- Spring Data JPA + Hibernate
- MySQL 8.0/9.4
- Flyway (database migrations)
- JWT Authentication (jjwt 0.12.3)
- BCrypt password hashing (strength 10)
- Google OAuth 2.0

**Frontend**:
- Vue 3 Composition API
- Vue Router 4
- Vite
- Tailwind CSS
- Axios (HTTP client)
- Google Identity Services

### Data Model Overview

**Phase 1-2 Foundation** (Users, Career Paths, Skills, Resources):
```
users (id, username, email, password_hash, role, ...)
  ↓ many-to-many via user_career_paths
career_paths (id, name, code, description, icon)
  ↓ many-to-many via career_path_skills
skills (id, name, description, is_important, icon, display_order)
  ├─ one-to-many → focus_areas (id, skill_id, name, description)
  │    └─ one-to-many → learning_resources (Phase 2)
  └─ one-to-many → learning_contents (Phase 4+)
```

**Phase 3 Question Bank**:
```
focus_areas
  └─ one-to-many → questions (id, focus_area_id, question_type, question_text, ...)
       └─ one-to-many → user_question_notes (id, question_id, user_id, note_content, core_strategy)
```

**Phase 4 Algorithm Learning** (Three-layer structure):
```
skills
  ↓ one-to-many
major_categories (id, skill_id, name)  ← 新增大分类层
  ↓ one-to-many (via focus_area_categories)
focus_areas
  ↓ one-to-many
learning_contents (id, focus_area_id, content_type, title, url, ...)
  ├─ programming_question_details (extends learning_contents)
  ├─ user_learning_content_notes (user_id, note_content)
  └─ user_learning_content_knowledge_points (user_id, kp_title, kp_content)
```

**Phase 5 System Design**:
```
skills (特殊skill: "系统设计")
  ├─ focus_areas (基础知识: CAP理论、缓存、消息队列等)
  │    └─ learning_contents (文章、视频)
  │         ├─ user_learning_content_notes
  │         └─ user_learning_content_knowledge_points
  └─ system_design_cases (典型案例: Instagram、TinyURL等)
       ├─ case_resources (学习资源)
       ├─ case_solutions (参考答案, 支持多方案)
       │    └─ solution_diagrams (架构图)
       └─ user_case_notes (用户答题: 6步骤 + 7个kp字段)
```

**Phase 6 General Skills** (通用技能: 云计算、Behavioral等):
```
skills (云计算、Behavioral、DevOps等)
  ├─ major_categories (第一类技能: Kubernetes、Network等 / 第二类技能: 隐藏"General"大分类)
  │    └─ focus_areas
  │         ├─ learning_contents (学习资料)
  │         │    ├─ user_learning_content_notes (user_id=-1表示AI笔记)
  │         │    └─ user_learning_content_knowledge_points (user_id=-1表示AI知识点)
  │         └─ questions (复用Phase 3表)
  │              └─ user_question_notes (新增related_knowledge_point_ids字段)
  └─ answer_templates (答题模版: STAR、Technical等)
       └─ skill_templates (多对多关联, is_default标记)
```

**Key Constraints**:
- username, email: unique indexes
- career_path_skills: unique(career_path_id, skill_id)
- user_question_notes: unique(question_id, user_id) - one note per user per question
- user_case_notes: unique(case_id, user_id) - one note per user per case
- skill_templates: composite PK (skill_id, template_id)
- CASCADE delete: career_paths → skills → focus_areas → learning_contents/questions

**Enums**:
- Resource Types: `BOOK`, `COURSE`, `WEBSITE`, `ARTICLE`, `VIDEO`, `DOCUMENT`, `BLOG`, `PRACTICE`, `TOOL`, `OTHER`
- Question Difficulty: `EASY`, `MEDIUM`, `HARD`
- Question Types: `behavioral`, `technical`, `design`, `programming`

---

## Phase 6 Architecture Details

### Data Model

**ER Diagram**:
```
skills
  ├─ 1:N → major_categories (第二类技能创建"General"隐藏)
  │         └─ 1:N → focus_areas
  │                   ├─ 1:N → learning_contents
  │                   │         ├─ 1:N → user_learning_content_notes (支持user_id=-1)
  │                   │         └─ 1:N → user_learning_content_knowledge_points (支持user_id=-1)
  │                   └─ 1:N → questions
  │                             └─ 1:N → user_question_notes (扩展related_knowledge_point_ids)
  └─ N:M → answer_templates (通过skill_templates关联)
             (STAR、Technical等模版)
```

**answer_templates表** (答题模版):
```sql
CREATE TABLE answer_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(50) UNIQUE NOT NULL COMMENT '模版名称 (STAR, Technical等)',
    template_fields JSON NOT NULL COMMENT '模版字段定义 (JSON数组)',
    description TEXT COMMENT '模版说明',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 示例数据: STAR模版
INSERT INTO answer_templates (template_name, template_fields, description) VALUES (
  'STAR',
  '[
    {"key": "situation", "label": "Situation (情境)", "placeholder": "描述项目背景..."},
    {"key": "task", "label": "Task (任务)", "placeholder": "你的具体职责..."},
    {"key": "action", "label": "Action (行动)", "placeholder": "你采取的行动..."},
    {"key": "result", "label": "Result (结果)", "placeholder": "可量化的成果..."}
  ]',
  'STAR答题框架,适用于Behavioral题目'
);
```

**skill_templates表** (Skill与模版多对多关联):
```sql
CREATE TABLE skill_templates (
    skill_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否为该Skill的默认模版',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (skill_id, template_id),
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES answer_templates(id) ON DELETE CASCADE
);
```

**user_question_notes表扩展** (支持知识点关联):
```sql
ALTER TABLE user_question_notes
    ADD COLUMN related_knowledge_point_ids JSON COMMENT '关联的知识点ID列表 (JSON数组)';
```

### Backend Architecture

**Controller层**:
```java
@RestController
@RequestMapping("/api")  // ⚠️ CRITICAL: 统一前缀在类级别
public class SkillTemplateController {

    // ✅ CORRECT: 不再重复/api前缀
    @GetMapping("/skills/{skillId}/templates")
    public ResponseEntity<List<SkillTemplateDTO>> getSkillTemplatesPublic(@PathVariable Long skillId) {
        // 公开API: 用户获取技能的所有模版
    }

    @GetMapping("/skills/{skillId}/templates/default")
    public ResponseEntity<SkillTemplateDTO> getDefaultTemplatePublic(@PathVariable Long skillId) {
        // 公开API: 用户获取技能的默认模版
    }

    @GetMapping("/admin/skill-templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SkillTemplateDTO>> getSkillTemplates(@RequestParam Long skillId) {
        // 管理员API: 获取技能关联的所有模版
    }

    @PostMapping("/admin/skill-templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> associateTemplate(@RequestBody SkillTemplateRequest request) {
        // 管理员API: 关联技能与模版
    }

    @DeleteMapping("/admin/skill-templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disassociateTemplate(@RequestParam Long skillId, @RequestParam Long templateId) {
        // 管理员API: 取消关联
    }
}
```

**Service层核心逻辑**:

1. **AI笔记处理** (user_id=-1标识):
```java
@Service
public class LearningContentService {

    @Transactional
    public void importAINote(Long contentId, String noteContent) {
        // 查找或创建AI笔记记录 (user_id=-1)
        UserLearningContentNote aiNote = noteRepository
            .findByLearningContentIdAndUserId(contentId, -1L)
            .orElse(new UserLearningContentNote());

        aiNote.setLearningContentId(contentId);
        aiNote.setUserId(-1L); // AI笔记标识
        aiNote.setNoteContent(noteContent);
        noteRepository.save(aiNote);
    }

    public Map<String, Object> getNotesWithAI(Long contentId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 查询AI笔记
        noteRepository.findByLearningContentIdAndUserId(contentId, -1L)
            .ifPresent(aiNote -> result.put("aiNote", toDTO(aiNote)));

        // 查询用户笔记
        noteRepository.findByLearningContentIdAndUserId(contentId, userId)
            .ifPresent(userNote -> result.put("userNote", toDTO(userNote)));

        return result;
    }
}
```

2. **答题模版渲染** (动态字段生成):
```java
@Service
public class SkillTemplateService {

    public AnswerTemplateDTO getDefaultTemplate(Long skillId) {
        // 查询默认模版关联
        SkillTemplate skillTemplate = skillTemplateRepository
            .findBySkillIdAndIsDefaultTrue(skillId)
            .orElse(null);

        if (skillTemplate == null) {
            return null;
        }

        // 获取模版详情
        AnswerTemplate template = templateRepository.findById(skillTemplate.getTemplateId())
            .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

        AnswerTemplateDTO dto = toDTO(template);

        // 解析JSON字段定义
        dto.setTemplateFields(
            objectMapper.readValue(template.getTemplateFields(), new TypeReference<List<TemplateFieldDTO>>() {})
        );

        return dto;
    }
}
```

### Frontend Architecture

**页面结构**:

1. **管理员 - 通用技能内容管理** (`/admin/general-skills`):
```vue
GeneralSkillContentManagement.vue
├─ 左侧 (上下两栏树形结构)
│   ├─ 上栏树: 职业路径 → 技能
│   └─ 下栏树:
│       ├─ 第一类技能: 大分类 → Focus Area
│       └─ 第二类技能: 直接显示Focus Area (隐藏"General"大分类)
└─ 右侧 (双Tab布局)
    ├─ Tab 1: 学习资料
    │   ├─ Learning Content列表 (CRUD)
    │   └─ AI笔记导入功能 (AIImportModal)
    └─ Tab 2: 试题库
        ├─ Questions列表 (CRUD)
        └─ 三模式系统:
            ├─ view (浏览模式): QuestionViewModal内联
            ├─ edit (编辑模式): QuestionEditModal内联
            └─ ai-answer (AI答题模式): 模版/自由切换
```

2. **管理员 - 技能模版库管理** (`/admin/skill-templates`):
```vue
SkillTemplateManagement.vue
├─ 左侧 30% - 职业路径与技能树
│   └─ 技能卡片 (选中状态高亮)
└─ 右侧 70% - 模版管理面板
    ├─ 技能信息头部 (蓝色背景)
    │   ├─ [新增模版] 按钮 → TemplateEditorModal
    │   └─ [关联已有模版] 按钮 → AssociateTemplateModal
    └─ 模版卡片列表 (Grid布局, 2列)
        ├─ 模版信息 (名称、描述、字段展示)
        ├─ 默认标记 (绿色Badge)
        └─ 操作按钮 (编辑、设为默认、取消关联)
```

3. **用户端 - 通用技能学习** (`/general-skills/learning`):
```vue
GeneralSkillLearning.vue
├─ 左侧 (上下两栏树形结构) - 同管理员页面
└─ 右侧 (双Tab布局)
    ├─ Tab 1: 学习资料
    │   ├─ 🤖 AI学习笔记卡片 (user_id=-1, 背景色标记)
    │   └─ 📝 我的学习笔记卡片 (可编辑)
    └─ Tab 2: 试题库
        ├─ 试题列表
        └─ 答题界面 (UserNoteEditor.vue)
            ├─ 模版模式: 动态渲染STAR/Technical字段
            └─ 自由模式: 自由文本输入
```

**核心组件 - STAR框架动态答题**:

UserNoteEditor.vue (位于GeneralSkillLearning.vue:1176-1486):
```vue
<script setup>
// 1. 获取技能默认模版
const loadAnswerTemplate = async () => {
  const skillId = currentSkillId.value
  const template = await skillTemplateApi.getDefaultTemplatePublic(skillId)

  if (template && template.templateFields) {
    answerTemplate.value = template
    answerMode.value = 'template' // 模版模式

    // 解析templateFields (支持JSON字符串/对象双格式)
    const fields = parseTemplateFields(template.templateFields)
    templateFields.value = fields
  } else {
    answerMode.value = 'free' // 自由模式
  }
}

// 2. 解析已保存笔记 (正则匹配: ## Label\nContent)
const parseNoteToTemplate = (noteContent) => {
  const parsedValues = {}

  templateFields.value.forEach(field => {
    const pattern = new RegExp(`## ${field.label}\\s*\\n([\\s\\S]*?)(?=\\n## |$)`, 'i')
    const match = noteContent.match(pattern)
    if (match) {
      parsedValues[field.key] = match[1].trim()
    }
  })

  return parsedValues
}

// 3. 保存笔记 (将模版字段合并为Markdown)
const saveNote = async () => {
  let noteContent = ''

  if (answerMode.value === 'template') {
    // 模版模式: 合并字段为Markdown格式
    const parts = templateFields.value.map(field => {
      return `## ${field.label}\n${answerData.value[field.key] || ''}`
    })
    noteContent = parts.join('\n\n')
  } else {
    // 自由模式: 保存纯文本
    noteContent = answerData.value.freeAnswer || ''
  }

  await questionApi.saveMyNote(questionId.value, {
    noteContent,
    coreStrategy: answerData.value.coreStrategy
  })
}
</script>

<template>
  <!-- 模版/自由模式切换 -->
  <div v-if="answerTemplate" class="mb-4 flex gap-2">
    <button @click="answerMode = 'template'" :class="{'bg-blue-600': answerMode === 'template'}">
      模版模式
    </button>
    <button @click="answerMode = 'free'" :class="{'bg-blue-600': answerMode === 'free'}">
      自由模式
    </button>
  </div>

  <!-- 模版模式: 动态渲染字段 -->
  <div v-if="answerMode === 'template'" class="grid grid-cols-2 gap-4">
    <div v-for="(field, idx) in templateFields" :key="field.key"
         :class="getFieldColorClass(idx)">
      <label class="font-semibold">{{ field.label }}</label>
      <textarea v-model="answerData[field.key]" :placeholder="field.placeholder" rows="4"></textarea>
    </div>
  </div>

  <!-- 自由模式: 自由文本 -->
  <div v-else>
    <textarea v-model="answerData.freeAnswer" rows="12" placeholder="在这里自由回答..."></textarea>
  </div>

  <!-- 核心思路 (两种模式都有) -->
  <div class="mt-4">
    <label>核心思路 (可选)</label>
    <textarea v-model="answerData.coreStrategy" rows="2"></textarea>
  </div>

  <button @click="saveNote" class="mt-4 bg-green-600 text-white px-4 py-2">保存笔记</button>
</template>
```

**API Client**:
```javascript
// frontend/src/api/skillTemplateApi.js

// 公开API (用户端)
export const getSkillTemplatesPublic = (skillId) =>
  apiClient.get(`/skills/${skillId}/templates`)

export const getDefaultTemplatePublic = (skillId) =>
  apiClient.get(`/skills/${skillId}/templates/default`)

// 管理员API
export const getSkillTemplates = (skillId) =>
  apiClient.get('/admin/skill-templates', { params: { skillId } })

export const associateTemplate = (skillId, templateId, isDefault = false) =>
  apiClient.post('/admin/skill-templates', { skillId, templateId, isDefault })

export const setDefaultTemplate = (skillId, templateId) =>
  apiClient.put('/admin/skill-templates/default', null, { params: { skillId, templateId } })

export const disassociateTemplate = (skillId, templateId) =>
  apiClient.delete('/admin/skill-templates', { params: { skillId, templateId } })
```

### Key Design Decisions

**1. 为什么第二类技能使用"General"大分类而不是两套架构?**
- 复用Phase 4的三层结构UI组件和后端逻辑
- 避免维护两套架构 (isSecondTypeSkill检测逻辑简单)
- 前端隐藏"General"大分类层级，用户无感知

**2. 为什么AI笔记使用user_id=-1而不是新表?**
- 复用现有表结构 (user_learning_content_notes, user_learning_content_knowledge_points)
- 无需新增AI专用表
- 查询简单: `WHERE user_id IN (?, -1)`

**3. 为什么答题模版采用多对多关联?**
- 一个Skill可以关联多个模版 (默认模版 + 通用模版)
- 用户可选择使用默认模版或通用模版
- 灵活扩展: 未来可添加更多模版类型

**4. 为什么答题笔记使用Markdown格式存储?**
- 模版字段合并为`## Label\nContent`格式
- 易于解析和展示 (正则匹配)
- 可随时切换模版/自由模式 (同一字段)

### Implementation Lessons (Phase 6)

**✅ 成功经验**:
1. **零axios bug** - 遵循CLAUDE.md Guardrails #8-9，预防性检查清单有效
2. **零DTO bug** - 遵循Guardrail #10，完整填充DTO字段
3. **代码复用率80%** - 最大化复用Phase 4架构，开发效率提升
4. **超预期交付** - 3个亮点功能 (三模式系统、STAR动态答题、技能模版库管理)

**❌ 犯过的错误**:

**错误1: Controller路径重复** (commit 8e04ecb)
- **问题**: `@GetMapping("/api/skills/{id}/templates")` - 方法上包含完整`/api`前缀
- **表现**: 实际URL变成`/api/api/skills/...` (404错误)
- **根本原因**: Controller缺少类级别`@RequestMapping("/api")`注解，但方法上写了完整路径
- **影响**: 所有8个API端点都无法访问
- **修复**:
  ```java
  // ❌ WRONG
  @RestController
  public class SkillTemplateController {
      @GetMapping("/api/skills/{id}/templates")  // 重复/api
  }

  // ✅ CORRECT
  @RestController
  @RequestMapping("/api")  // 类级别统一前缀
  public class SkillTemplateController {
      @GetMapping("/skills/{id}/templates")  // 方法级别不重复
  }
  ```
- **如何避免**: 见CLAUDE.md新增Guardrail #11

**错误2: 前后端字段名不一致** (commit 9243698)
- **问题**: 前端使用`questionText`，后端DTO是`questionDescription`
- **表现**: UI显示空白
- **根本原因**: 前端直接使用了表字段名，未检查DTO定义
- **修复**: 统一使用`questionDescription` (后端DTO为准)
- **如何避免**: 见CLAUDE.md新增Guardrail #12

**⚠️ 技术债务** (待Phase 6.5解决):
- 知识点关联功能UI未实现 (`related_knowledge_point_ids`字段)
- MyQuestionBank.vue搜索模式未完善 (筛选条件)
- AI笔记批量导入工具缺失

**对比Phase 3-5**:
| 指标 | Phase 3 | Phase 4 | Phase 5 | Phase 6 |
|------|---------|---------|---------|---------|
| Axios Bug | 2 | 1 | 0 | **0** ✅ |
| DTO Bug | 1 | 1 | 0 | **0** ✅ |
| Controller Bug | 0 | 0 | 0 | **1** ⚠️ |
| 代码复用率 | 30% | 50% | 70% | **80%** ✅ |

**新增错误类型**: Controller路径配置错误 - Phase 1-5未出现，Phase 6新增

---

## Authentication Flow

**JWT Authentication**:
1. Frontend: `POST /api/auth/login` with credentials
2. Backend: validates password → generates JWT (username in subject)
3. Frontend: stores in `localStorage.auth_token`
4. Frontend: Axios interceptor adds `Authorization: Bearer {token}`
5. Backend: extracts username from JWT → loads User from DB

**Google OAuth**:
1. Frontend: Google SDK initialized with client ID
2. User clicks Google button → gets credential (ID token)
3. Frontend: `POST /api/auth/google` with credential
4. Backend: verifies with Google API → creates/updates user → returns JWT
5. Same JWT flow as traditional login

**Token Details**:
- Algorithm: HS384
- Expiration: 24 hours
- Secret: stored in `backend/.env` as `JWT_SECRET`
- Payload: subject (username), issuedAt, expiration

---

## Authorization Patterns

**Admin Permission Check**:
```java
private void requireAdmin(String authHeader) {
    if (!authService.isAdminByToken(authHeader.replace("Bearer ", ""))) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
    }
}
```

**Resource Visibility Control**:

Learning Resources:
- Users see: official resources + their own resources
- SQL: `WHERE skill_id = ? AND (is_official = true OR created_by_user_id = ?)`
- Users can only delete their own resources
- Admins can see and manage all resources

Questions (Phase 3):
- Users see: official questions + their own questions
- SQL: `WHERE focus_area_id = ? AND (is_official = true OR created_by_user_id = ?)`
- Users can only edit/delete their own questions
- Admins can see and manage all questions

User Notes:
- Users see only their own notes
- Unique constraint ensures one note per user per question/case
- Admins cannot see other users' notes (privacy protection)

AI Notes (Phase 6):
- AI笔记使用user_id=-1标识
- 所有用户可见AI笔记
- 管理员可导入/编辑AI笔记

---

## Frontend Architecture

**State Management**:
- Global auth state: `composables/useAuth.js`
- Provides: `currentUser`, `token`, `isAdmin`, `login()`, `logout()`, `updateUser()`
- Reactive state shared across components

**API Organization**:
```
api/
  ├─ index.js          (Axios instance with JWT interceptor + response unwrapper)
  ├─ auth.js           (login, register, Google OAuth, getCurrentUser)
  ├─ users.js          (admin user management)
  ├─ careerPaths.js    (career path queries)
  ├─ skills.js         (user skill queries)
  ├─ adminSkills.js    (admin skill management)
  ├─ questionApi.js    (user question queries, user notes)
  ├─ adminQuestionApi.js (admin question CRUD)
  ├─ skillTemplateApi.js (Phase 6: 答题模版管理)
  └─ systemDesignCaseApi.js (Phase 5: 系统设计案例)
```

**Axios Interceptors**:

Request interceptor (JWT injection):
```javascript
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

⚠️ **Response interceptor** (auto-unwraps response.data):
```javascript
apiClient.interceptors.response.use(
  (response) => {
    return response.data  // Auto-unwraps!
  },
  (error) => {
    // error handling...
  }
)
```

**CRITICAL**: Because the response interceptor returns `response.data`, all API calls should use:
```javascript
// ✅ CORRECT
const data = await questionApi.getQuestions()
items.value = data || []

// ❌ WRONG - response.data is undefined!
const response = await questionApi.getQuestions()
items.value = response.data || []  // undefined!
```

**Route Guards**:
```javascript
router.beforeEach((to, from, next) => {
  const { isAdmin } = useAuth()
  if (to.meta.requiresAdmin && !isAdmin.value) {
    next('/login')
  } else {
    next()
  }
})
```

---

## Performance

**Database Indexes** (across all phases):
```sql
-- Core tables
INDEX idx_username ON users(username)
INDEX idx_email ON users(email)
INDEX idx_skill_id ON focus_areas(skill_id)
INDEX idx_focus_area_id ON questions(focus_area_id)

-- Phase 4: Algorithm learning
INDEX idx_skill_id ON major_categories(skill_id)
INDEX idx_major_category_id ON focus_area_categories(major_category_id)
INDEX idx_learning_content_id ON programming_question_details(learning_content_id)

-- Phase 5: System design
INDEX idx_skill_id ON system_design_cases(skill_id)
INDEX idx_case_id ON case_resources(case_id)
INDEX idx_case_id ON case_solutions(case_id)

-- Phase 6: General skills
INDEX idx_skill_id ON skill_templates(skill_id)
INDEX idx_template_id ON skill_templates(template_id)
UNIQUE KEY unique_user_content ON user_learning_content_notes(learning_content_id, user_id)
```

**Query Optimization**:
- Resource visibility: Single query with OR condition (not two separate queries)
- User notes: UPSERT pattern (check existence before insert/update)
- Learning summary: Batch load, only fetch kp_* fields (not step1-6)
- AI notes: `WHERE user_id IN (?, -1)` - uses index, fast

---

**Document Version**: v4.0
**Last Updated**: 2025-12-28 (added Phase 6 architecture)
**Maintainer**: Austin Xu
