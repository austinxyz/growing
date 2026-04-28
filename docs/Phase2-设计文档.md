# Phase 2 - 技能体系管理设计文档

> **版本**: v1.0
> **日期**: 2025-12-20
> **状态**: ✅ 已实施完成

## 一、总体架构

### 1.1 系统概述

Phase 2 在 Phase 1 JWT认证系统的基础上,构建完整的技能体系管理功能。支持管理员配置技能树和学习资源,用户查看个人职业路径下的技能并添加个人学习资源。

### 1.2 技术栈

**后端**:
- Spring Boot 3.2.0
- Spring Data JPA + Hibernate
- MySQL 8.0/9.4
- Flyway (数据库迁移)

**前端**:
- Vue 3 Composition API
- Vue Router 4
- Vite
- Tailwind CSS

### 1.3 核心概念层级

```
用户 (User)
  └─ 职业路径 (Career Path) - Phase 1已实现
       └─ 技能 (Skill) - Phase 2新增
            ├─ 专注领域 (Focus Area) - Phase 2新增
            └─ 学习资源 (Learning Resource) - Phase 2新增
```

**关键设计决策**:
- **Focus Area**: 纯展示性质,只包含名称和描述,不关联其他数据
- **Learning Resource**: 直接归属于技能,不归属于Focus Area
- **资源可见性**: 用户添加的资源只有自己可见,不影响其他用户

## 二、数据模型设计

### 2.1 实体关系图 (ERD)

```
users (Phase 1)
  ↓ many-to-many via user_career_paths
career_paths (Phase 1)
  ↓ many-to-many via career_path_skills (Phase 2)
skills (Phase 2)
  ├─ one-to-many → focus_areas (Phase 2)
  └─ one-to-many → learning_resources (Phase 2)
                      ↓ many-to-one
                    users (created_by_user_id)
```

### 2.2 核心表设计

#### skills 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| name | VARCHAR(100) | NOT NULL | 技能名称,如"编程" |
| description | TEXT | | 技能描述 |
| is_important | TINYINT(1) | DEFAULT 0 | 是否重要技能(⭐标记) |
| icon | VARCHAR(50) | | Emoji图标 |
| display_order | INT | DEFAULT 0, INDEX | 显示顺序 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### career_path_skills 表 (关联表)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| career_path_id | BIGINT | FK, INDEX | 职业路径ID |
| skill_id | BIGINT | FK, INDEX | 技能ID |
| created_at | TIMESTAMP | | 创建时间 |

**唯一约束**: `UNIQUE (career_path_id, skill_id)`

**外键约束**:
- `career_path_id` → `career_paths(id)` ON DELETE CASCADE
- `skill_id` → `skills(id)` ON DELETE CASCADE

#### focus_areas 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| skill_id | BIGINT | FK, NOT NULL, INDEX | 所属技能ID |
| name | VARCHAR(200) | NOT NULL | 领域名称,如"动态规划" |
| description | TEXT | | 描述 |
| display_order | INT | DEFAULT 0, INDEX | 显示顺序 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**外键约束**:
- `skill_id` → `skills(id)` ON DELETE CASCADE

#### learning_resources 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| skill_id | BIGINT | FK, NOT NULL, INDEX | 所属技能ID |
| resource_type | ENUM | NOT NULL | 资源类型 |
| title | VARCHAR(200) | NOT NULL | 资源标题 |
| url | VARCHAR(500) | | 资源链接 |
| author | VARCHAR(100) | | 作者/来源 |
| description | TEXT | | 资源描述 |
| is_official | TINYINT(1) | DEFAULT 0, INDEX | 是否官方资源 |
| created_by_user_id | BIGINT | FK, INDEX | 创建者ID |
| display_order | INT | DEFAULT 0, INDEX | 显示顺序 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**资源类型枚举**:
```sql
ENUM('BOOK','COURSE','WEBSITE','ARTICLE','VIDEO','DOCUMENT','BLOG','PRACTICE','TOOL','OTHER')
```

**外键约束**:
- `skill_id` → `skills(id)` ON DELETE CASCADE
- `created_by_user_id` → `users(id)` ON DELETE CASCADE

### 2.3 级联删除规则

```
删除 Career Path
  └─ CASCADE → career_path_skills (关联技能不删除)

删除 Skill
  ├─ CASCADE → career_path_skills
  ├─ CASCADE → focus_areas
  └─ CASCADE → learning_resources

删除 User
  └─ CASCADE → learning_resources (created_by_user_id)
```

## 三、后端API设计

### 3.1 用户端API

#### GET /api/skills/career-paths/{careerPathId}
**功能**: 获取职业路径下的所有技能(包含统计信息)

**响应**:
```json
[
  {
    "id": 1,
    "name": "编程",
    "description": "...",
    "isImportant": true,
    "icon": "💻",
    "displayOrder": 0,
    "focusAreaCount": 5,
    "resourceCount": 10,
    "careerPaths": [...]
  }
]
```

#### GET /api/skills/{id}
**功能**: 获取技能详情(包含专注领域和用户可见的学习资源)

**响应**:
```json
{
  "id": 1,
  "name": "编程",
  "focusAreas": [
    {"id": 1, "name": "动态规划", "description": "..."}
  ],
  "learningResources": [
    {
      "id": 1,
      "title": "LeetCode",
      "resourceType": "WEBSITE",
      "isOfficial": true
    },
    {
      "id": 2,
      "title": "我的笔记",
      "resourceType": "DOCUMENT",
      "isOfficial": false,
      "createdByUserId": 1
    }
  ]
}
```

**资源可见性规则**:
```sql
SELECT * FROM learning_resources
WHERE skill_id = ?
  AND (is_official = true OR created_by_user_id = ?)
ORDER BY display_order ASC, created_at ASC
```

#### POST /api/skills/{skillId}/resources
**功能**: 用户添加个人学习资源

**请求体**:
```json
{
  "title": "我的学习笔记",
  "resourceType": "DOCUMENT",
  "url": "https://...",
  "author": "我自己",
  "description": "..."
}
```

**响应**: 201 Created + 资源对象

**权限**: 需登录,`isOfficial`自动设为false,`createdByUserId`自动设为当前用户

#### DELETE /api/skills/resources/{resourceId}
**功能**: 删除个人学习资源

**权限**: 只能删除自己添加的资源(`created_by_user_id = 当前用户`)

### 3.2 管理员API

#### GET /api/admin/skills
**功能**: 获取所有技能(包含职业路径关联)

**权限**: 需admin角色

#### POST /api/admin/skills
**功能**: 创建技能并关联到职业路径

**请求体**:
```json
{
  "name": "编程",
  "description": "...",
  "isImportant": true,
  "icon": "💻",
  "careerPathIds": [1, 2]
}
```

#### PUT /api/admin/skills/{id}
**功能**: 更新技能信息

#### DELETE /api/admin/skills/{id}
**功能**: 删除技能(级联删除Focus Area和Learning Resource)

#### POST /api/admin/skills/{skillId}/focus-areas
**功能**: 创建专注领域

**请求体**:
```json
{
  "name": "动态规划",
  "description": "..."
}
```

#### PUT /api/admin/skills/focus-areas/{id}
**功能**: 更新专注领域

#### DELETE /api/admin/skills/focus-areas/{id}
**功能**: 删除专注领域

#### POST /api/admin/skills/{skillId}/resources
**功能**: 管理员添加官方学习资源

**请求体**:
```json
{
  "title": "LeetCode",
  "resourceType": "WEBSITE",
  "url": "https://leetcode.com",
  "author": "LeetCode",
  "description": "...",
  "isOfficial": true
}
```

#### PUT /api/admin/skills/resources/{id}
**功能**: 更新学习资源

#### DELETE /api/admin/skills/resources/{id}
**功能**: 删除学习资源

## 四、前端路由设计

### 4.1 用户端路由

| 路径 | 组件 | 说明 |
|------|------|------|
| `/skills/career-paths` | MyCareerPaths.vue | 职业路径下的技能列表 |
| `/skills/:id` | SkillDetail.vue | 技能详情(专注领域+学习资源) |

### 4.2 管理员路由

| 路径 | 组件 | 说明 |
|------|------|------|
| `/admin/skills` | SkillManagement.vue | 技能管理(两栏布局) |

**SkillManagement.vue 布局**:
- **左侧面板**(384px): 职业路径Tab + 技能列表
- **右侧面板**(flex-1): 3个Card
  - Card 1: 基本信息
  - Card 2: 专注领域
  - Card 3: 学习资源

## 五、前端组件设计

### 5.1 核心组件

#### MyCareerPaths.vue
**功能**: 显示用户职业路径下的技能

**特性**:
- 职业路径Tab切换
- 技能卡片网格布局(MD:2列,LG:3列)
- "全部" / "重要技能"过滤
- 显示专注领域数量和资源数量
- 点击卡片跳转到技能详情

**关键实现**:
```javascript
onMounted(async () => {
  // 刷新用户信息以获取最新的职业路径数据(包括icon)
  const response = await authApi.getCurrentUser()
  const userData = response.data || response
  updateUser(userData)

  // 为每个职业路径加载技能
  for (const careerPath of userCareerPaths.value) {
    await loadSkillsForCareerPath(careerPath.id)
  }
})
```

#### SkillDetail.vue
**功能**: 显示技能详情

**Tab结构**:
- Tab 1: 专注领域(列表展示)
- Tab 2: 学习资源(按类型分组,显示官方+个人资源)

**添加资源功能**:
- "添加我的资源"按钮
- 资源类型下拉选择(10种类型)
- 只能删除自己添加的资源

#### SkillManagement.vue
**功能**: 管理员技能管理

**布局特点**:
- 两栏布局(left-right split)
- 左侧:职业路径垂直Tab + 技能列表
- 右侧:技能详情3个Card
- 使用Modal进行CRUD操作(不跳转页面)

**Modal组件**:
- `SkillEditModal`: 创建/编辑技能(含Emoji选择器)
- `FocusAreaEditModal`: 创建/编辑专注领域
- `LearningResourceEditModal`: 创建/编辑学习资源

### 5.2 可复用组件

#### SkillEditModal.vue
**功能**: 技能创建/编辑弹窗

**Emoji选择器**:
```javascript
const commonEmojis = [
  '💻', '📱', '🖥️', '⌨️', '🖱️',
  '🎯', '🚀', '⚡', '🔥', '✨',
  // ... 共50个常用emoji
]
```

**表单字段**:
- 技能名称(必填)
- 描述
- 是否重要(复选框)
- 图标Emoji(选择器 + 手动输入)
- 关联职业路径(多选复选框)

#### FocusAreaEditModal.vue
**功能**: 专注领域创建/编辑弹窗

**表单字段**:
- 领域名称(必填)
- 描述

#### LearningResourceEditModal.vue
**功能**: 学习资源创建/编辑弹窗

**表单字段**:
- 资源标题(必填)
- 资源类型(下拉选择,10种类型)
- 描述
- 链接URL
- 作者/来源
- 是否官方(管理员添加时默认勾选)

**资源类型映射**:
```javascript
const resourceTypeMap = {
  'DOCUMENT': '文档',
  'VIDEO': '视频',
  'COURSE': '课程',
  'BOOK': '书籍',
  'BLOG': '博客',
  'PRACTICE': '练习',
  'TOOL': '工具',
  'ARTICLE': '文章',
  'WEBSITE': '网站',
  'OTHER': '其他'
}
```

## 六、后端Service层设计

### 6.1 SkillService

**核心方法**:

```java
// 获取职业路径下的技能(含统计信息)
public List<SkillDTO> getSkillsByCareerPathId(Long careerPathId) {
    return skillRepository.findByCareerPathId(careerPathId).stream()
            .map(skill -> {
                SkillDTO dto = convertToDTO(skill);
                dto.setFocusAreaCount(focusAreaRepository.countBySkillId(skill.getId()));
                dto.setResourceCount(learningResourceRepository.countBySkillId(skill.getId()));
                return dto;
            })
            .collect(Collectors.toList());
}

// 获取技能详情(含用户可见资源)
public SkillDTO getSkillByIdForUser(Long id, Long userId) {
    Skill skill = skillRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    SkillDTO dto = convertToDTO(skill);
    dto.setFocusAreas(focusAreaRepository.findBySkillId(id));
    dto.setLearningResources(learningResourceRepository
        .findBySkillIdAndVisibleToUser(id, userId)); // 官方 + 用户自己的
    return dto;
}
```

### 6.2 FocusAreaService

```java
public FocusAreaDTO createFocusArea(Long skillId, FocusAreaDTO dto) {
    Skill skill = skillRepository.findById(skillId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    FocusArea focusArea = new FocusArea();
    focusArea.setSkill(skill);
    focusArea.setName(dto.getName());
    focusArea.setDescription(dto.getDescription());

    return convertToDTO(focusAreaRepository.save(focusArea));
}
```

### 6.3 LearningResourceService

**资源可见性查询**:

```java
@Query("SELECT lr FROM LearningResource lr " +
       "WHERE lr.skill.id = :skillId " +
       "AND (lr.isOfficial = true OR lr.createdByUser.id = :userId) " +
       "ORDER BY lr.displayOrder ASC, lr.createdAt ASC")
List<LearningResource> findBySkillIdAndVisibleToUser(
    @Param("skillId") Long skillId,
    @Param("userId") Long userId);
```

**用户添加资源**:

```java
public LearningResourceDTO createUserResource(Long skillId,
                                                LearningResourceDTO dto,
                                                Long userId) {
    Skill skill = skillRepository.findById(skillId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    LearningResource resource = new LearningResource();
    resource.setSkill(skill);
    resource.setCreatedByUser(user);
    resource.setIsOfficial(false); // 用户资源必定非官方
    // ... 其他字段

    return convertToDTO(learningResourceRepository.save(resource));
}
```

## 七、关键技术实现

### 7.1 JPA实体关联

**Skill.java**:
```java
@ManyToMany
@JoinTable(
    name = "career_path_skills",
    joinColumns = @JoinColumn(name = "skill_id"),
    inverseJoinColumns = @JoinColumn(name = "career_path_id")
)
private Set<CareerPath> careerPaths = new HashSet<>();

@OneToMany(mappedBy = "skill", cascade = CascadeType.ALL,
           orphanRemoval = true, fetch = FetchType.LAZY)
private List<FocusArea> focusAreas = new ArrayList<>();

@OneToMany(mappedBy = "skill", cascade = CascadeType.ALL,
           orphanRemoval = true, fetch = FetchType.LAZY)
private List<LearningResource> learningResources = new ArrayList<>();
```

### 7.2 Vue Composition API状态管理

**MyCareerPaths.vue**:
```javascript
const userCareerPaths = ref([])
const careerPathSkills = ref({}) // 按careerPathId索引的技能列表
const loadingSkills = ref({})    // 按careerPathId索引的loading状态

const loadSkillsForCareerPath = async (careerPathId) => {
  if (careerPathSkills.value[careerPathId]) {
    return // 已加载,避免重复请求
  }

  loadingSkills.value[careerPathId] = true
  const response = await getSkillsByCareerPath(careerPathId)
  careerPathSkills.value[careerPathId] = response.data || response
  loadingSkills.value[careerPathId] = false
}
```

### 7.3 Emoji图标存储

**数据库字段**:
```sql
icon VARCHAR(50) -- 存储emoji unicode字符
```

**前端选择器**:
```vue
<div class="grid grid-cols-10 gap-2">
  <button
    v-for="emoji in commonEmojis"
    :key="emoji"
    @click="form.icon = emoji"
    class="text-2xl hover:bg-gray-100 p-2 rounded"
  >
    {{ emoji }}
  </button>
</div>
```

### 7.4 资源类型枚举同步

**后端枚举**:
```java
public enum ResourceType {
    BOOK, COURSE, WEBSITE, ARTICLE, VIDEO,
    DOCUMENT, BLOG, PRACTICE, TOOL, OTHER
}
```

**前端下拉**:
```vue
<select v-model="form.resourceType" required>
  <option value="">请选择类型</option>
  <option value="DOCUMENT">文档</option>
  <option value="VIDEO">视频</option>
  <!-- ... -->
</select>
```

**前端显示映射**:
```javascript
const resourceTypeMap = {
  'DOCUMENT': '文档',
  'VIDEO': '视频',
  // ...
}
```

## 八、安全性设计

### 8.1 权限控制

**管理员接口**:
```java
private void requireAdmin(String authHeader) {
    if (!authService.isAdminByToken(authHeader.replace("Bearer ", ""))) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
    }
}
```

**用户资源权限**:
```java
// 删除资源时检查ownership
if (!resource.getCreatedByUser().getId().equals(userId)) {
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此资源");
}
```

### 8.2 数据隔离

- 用户只能看到官方资源和自己添加的资源
- 用户只能删除自己添加的资源
- 管理员可以看到和管理所有资源

## 九、性能优化

### 9.1 数据库索引

```sql
-- skills表
CREATE INDEX idx_display_order ON skills(display_order);
CREATE INDEX idx_is_important ON skills(is_important);

-- focus_areas表
CREATE INDEX idx_skill_id ON focus_areas(skill_id);
CREATE INDEX idx_display_order ON focus_areas(display_order);

-- learning_resources表
CREATE INDEX idx_skill_id ON learning_resources(skill_id);
CREATE INDEX idx_is_official ON learning_resources(is_official);
CREATE INDEX idx_created_by_user_id ON learning_resources(created_by_user_id);
CREATE INDEX idx_display_order ON learning_resources(display_order);

-- career_path_skills表
CREATE INDEX idx_career_path_id ON career_path_skills(career_path_id);
CREATE INDEX idx_skill_id ON career_path_skills(skill_id);
CREATE UNIQUE INDEX uk_career_skill ON career_path_skills(career_path_id, skill_id);
```

### 9.2 前端缓存策略

**技能列表缓存**:
```javascript
const careerPathSkills = ref({})

const loadSkillsForCareerPath = async (careerPathId) => {
  if (careerPathSkills.value[careerPathId]) {
    return // 已缓存,不重复加载
  }
  // ... fetch and cache
}
```

**用户数据刷新**:
```javascript
onMounted(async () => {
  // 刷新用户信息以获取最新的职业路径icon
  const response = await authApi.getCurrentUser()
  updateUser(userData)
})
```

### 9.3 懒加载

```java
@OneToMany(mappedBy = "skill", fetch = FetchType.LAZY)
private List<FocusArea> focusAreas;
```

只在需要时加载关联数据,避免N+1查询问题。

## 十、错误处理

### 10.1 后端异常处理

```java
@GetMapping("/{id}")
public ResponseEntity<SkillDTO> getSkill(@PathVariable Long id) {
    Skill skill = skillRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "技能不存在"));
    return ResponseEntity.ok(convertToDTO(skill));
}
```

### 10.2 前端错误处理

```javascript
try {
  await createFocusArea(skillId, formData)
  emit('success')
} catch (error) {
  console.error('Failed to save focus area:', error)
  alert('保存失败,请重试')
}
```

## 十一、测试策略

### 11.1 后端单元测试

```java
@Test
void testGetSkillsByCareerPath() {
    // given
    Long careerPathId = 1L;
    // when
    List<SkillDTO> skills = skillService.getSkillsByCareerPathId(careerPathId);
    // then
    assertFalse(skills.isEmpty());
    assertTrue(skills.get(0).getFocusAreaCount() >= 0);
}
```

### 11.2 前端组件测试

```javascript
describe('SkillEditModal', () => {
  it('should emit success on form submit', async () => {
    const wrapper = mount(SkillEditModal, {
      props: { skillId: 1 }
    })
    await wrapper.find('form').trigger('submit')
    expect(wrapper.emitted('success')).toBeTruthy()
  })
})
```

## 十二、部署配置

### 12.1 数据库迁移

使用Flyway自动管理:
```
backend/src/main/resources/db/migration/
  V1__create_users_and_career_paths.sql
  V2__create_skills_tables.sql
  V3__init_skills_data.sql
```

### 12.2 环境变量

`backend/.env`:
```
DB_HOST=your-db-host
DB_PORT=3306
DB_NAME=growing
DB_USER=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=...
```

## 十三、已知问题和未来优化

### 13.1 已知限制

1. **Focus Area暂不支持难度级别**: 未来版本可能添加
2. **题库未关联到Focus Area**: 题库功能将在Phase 3实现
3. **资源审核机制**: 用户添加的资源不需要审核,直接可见(仅自己可见)
4. **资源排序**: 暂不支持拖拽排序,使用`display_order`字段预留
5. **技能树可视化**: 暂不支持,列表展示即可

### 13.2 未来优化方向

1. 添加技能学习进度跟踪
2. 支持资源评分和评论
3. 智能推荐学习资源
4. 技能树图形化展示
5. Focus Area关联题库(Phase 3)
6. 资源标签系统
7. 学习路径推荐

---

**文档版本**: v1.0
**最后更新**: 2025-12-20
**维护人**: Austin Xu
