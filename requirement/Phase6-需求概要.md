# Phase 6 需求概要 - 通用技能学习模块

## 背景

Phase 2-5已完成核心模块：
- **Phase 2**: 基础的Skill + Focus Area + Learning Resources（侧重学习）
- **Phase 3**: Question Bank（试题库，侧重答题）
- **Phase 4**: Algorithm Learning（学习+练习融合，三层结构Skill → 大分类 → Focus Area）
- **Phase 5**: System Design（基础知识+典型案例，查看/编辑双模式）

Phase 6需要支持剩余技能类型，分为两大类：
1. **第一类**（云计算、eBay Knowledge、DevOps、Software Development）：需要三层结构
2. **第二类**（Behavioral、People Management、Project Management）：两层结构，强调STAR答题法

---

## 核心矛盾与设计决策

### 矛盾点
现有架构存在两个独立入口：
- **职业技能页面**（Phase 2）：树状导航，侧重学习资料浏览
- **我的试题库**（Phase 3）：专注刷题，侧重答题笔记管理

Phase 4/5采用融合模式（学习资料+练习题在同一页面），用户体验更好。

### 设计决策：**双轨制架构**

#### 决策1: 保留两个入口，优化各自定位

**学习模式**（用户学习页面）：
- **定位**：系统化学习新知识（第一次学习某个技能）
- **特点**：完整性、连续性、理论与实践结合
- **交互**：单页连续阅读（学习资料 → 示例代码 → 相关试题）
- **适用场景**：学习云计算新概念、理解STAR方法框架

**答题模式**（我的试题库）：
- **定位**：专项训练和面试准备（已学过，现在要强化）
- **特点**：题目聚焦、模版支持、进度跟踪
- **交互**：题目列表 + 详情 + 答题笔记（支持模版）
- **适用场景**：刷Behavioral题库、练习云计算高频问题

#### 决策2: 复用Phase 4架构，统一三层结构

**第一类技能**（云计算等）：
```
Skill → Major Category（大分类）→ Focus Area
  └─ Learning Content（学习资料）
  └─ Questions（试题）
```

**第二类技能**（Behavioral等）：
```
Skill → [隐式大分类：General] → Focus Area
  └─ Learning Content（STAR方法论文档）
  └─ Questions（Behavioral题目）
```

**复用理由**：
- Phase 4已有`major_categories`表和三层UI组件
- 第二类技能可以创建一个默认大分类"General"，前端隐藏不显示
- 避免两套架构维护成本

#### 决策3: 扩展答题模版系统

不同技能类型需要不同答题框架：
- **技术类**（云计算、DevOps）：关键点 + 深入解释 + 实际案例
- **Behavioral类**：STAR框架（Situation, Task, Action, Result）
- **算法类**（已实现）：算法模版（Phase 4已有）

新增`answer_templates`表管理答题框架。

---

## 功能需求

### 1. 第一类技能：云计算、eBay Knowledge、DevOps、Software Development

#### 1.1 三层结构管理（管理员）

**Skill → Major Category → Focus Area**

**示例**：
```
Skill: 云计算
  ├─ Major Category: Kubernetes
  │   ├─ Focus Area: Pod管理
  │   ├─ Focus Area: Service网络
  │   └─ Focus Area: 存储卷
  ├─ Major Category: Network
  │   ├─ Focus Area: VPC设计
  │   └─ Focus Area: 负载均衡
  └─ Major Category: Security
      ├─ Focus Area: IAM权限
      └─ Focus Area: 数据加密
```

**管理界面**：
- 三栏布局（复用Phase 4模式）
- 左侧Tab：Skill列表（云计算、DevOps等）
- 中间栏：Major Category列表 + Focus Area树
- 右侧栏：Learning Content管理（CRUD）

#### 1.2 学习资料管理

**Learning Content类型**：
- 理论文档（Markdown）
- 代码示例（代码片段 + 语言标识）
- 命令示例（Shell、kubectl等）
- 架构图（图片URL）

**字段设计**：
```sql
learning_content:
  - content_type: 'theory' | 'code' | 'command' | 'diagram'
  - code_language: 'bash' | 'yaml' | 'python' | ...（仅code类型）
  - diagram_url: 图片链接（仅diagram类型）
```

#### 1.3 试题管理

**复用Phase 3的questions表**，扩展字段：
```sql
questions:
  - skill_id: 关联Skill
  - focus_area_id: 关联Focus Area
  - question_type: 'technical' | 'behavioral' | 'algorithm'（新增）
  - answer_key_points: JSON数组（答题重点）
  - knowledge_points: JSON数组（关联知识点标签）
```

**示例**：
```json
{
  "question_text": "解释Kubernetes Pod的生命周期",
  "answer_key_points": [
    "Pending阶段：调度和镜像拉取",
    "Running阶段：容器启动和健康检查",
    "Succeeded/Failed：终止状态",
    "重启策略的影响"
  ],
  "knowledge_points": ["Pod管理", "容器生命周期", "健康检查"]
}
```

#### 1.4 用户学习笔记

**两种笔记类型**：

**A. 整体笔记**（针对整个Learning Content）
```sql
user_learning_notes:
  - user_id
  - content_id（关联learning_content）
  - overall_note: 整体理解笔记（Markdown）
```

**B. 知识点笔记**（针对Learning Content中的具体知识点）
```sql
user_knowledge_point_notes:
  - user_id
  - content_id
  - knowledge_point_tag: 知识点标签（如"Pod调度策略"）
  - note: 知识点笔记（Markdown）
```

**交互设计**：
- 学习资料页面底部：整体笔记编辑区
- 知识点笔记：点击正文中的知识点标签，弹出笔记编辑器

#### 1.5 用户答题笔记

**扩展Phase 3的user_question_notes表**：
```sql
user_question_notes:
  - template_type: 'technical' | 'behavioral' | 'star' | null（新增）
  - key_points: JSON数组（结构化答题要点，按模版字段存储）
  - related_knowledge_points: JSON数组（关联的知识点标签）
```

**技术类答题模版示例**：
```json
{
  "template_type": "technical",
  "key_points": {
    "core_concept": "Kubernetes Pod是最小调度单元...",
    "deep_dive": "调度过程涉及Scheduler组件...",
    "real_world_example": "我在eBay项目中遇到Pod调度失败..."
  },
  "related_knowledge_points": ["Pod管理", "Scheduler"]
}
```

---

### 2. 第二类技能：Behavioral、People Management、Project Management

#### 2.1 简化结构（Skill → Focus Area）

**示例**：
```
Skill: Behavioral
  ├─ Focus Area: Leadership（领导力）
  ├─ Focus Area: Conflict Resolution（冲突解决）
  ├─ Focus Area: Teamwork（团队协作）
  └─ Focus Area: Communication（沟通能力）
```

**实现方式**：
- 后端：为每个第二类Skill创建默认Major Category "General"
- 前端：检测到"General"分类时，隐藏大分类层级，直接显示Focus Area列表

#### 2.2 STAR答题模版

**STAR框架**：
- **S**ituation（情境）：背景和挑战
- **T**ask（任务）：你的具体职责
- **A**ction（行动）：你采取的行动
- **R**esult（结果）：可量化的成果

**答题模版表**：
```sql
CREATE TABLE answer_templates (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  template_name VARCHAR(50) UNIQUE NOT NULL, -- 'STAR', 'Technical', 'Algorithm'
  template_fields JSON NOT NULL, -- 模版字段定义
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**STAR模版字段**：
```json
{
  "template_name": "STAR",
  "template_fields": [
    {"key": "situation", "label": "Situation（情境）", "placeholder": "描述项目背景和挑战..."},
    {"key": "task", "label": "Task（任务）", "placeholder": "你的具体职责是什么..."},
    {"key": "action", "label": "Action（行动）", "placeholder": "你采取了哪些行动..."},
    {"key": "result", "label": "Result（结果）", "placeholder": "可量化的成果（如提升X%）..."}
  ]
}
```

#### 2.3 Behavioral题目管理

**questions表扩展**：
```sql
questions:
  - question_type: 'behavioral'
  - suggested_template: 'STAR'（推荐使用的答题模版）
```

**示例题目**：
```
题目: "Describe a time when you had to resolve a conflict within your team."
推荐模版: STAR
```

#### 2.4 用户STAR答题笔记

**user_question_notes表存储示例**：
```json
{
  "question_id": 123,
  "template_type": "star",
  "key_points": {
    "situation": "在eBay搜索团队，两位工程师对架构设计有分歧...",
    "task": "作为Tech Lead，需要在2周内解决分歧并推进项目...",
    "action": "1. 组织技术评审会议；2. 建立决策框架；3. 促进双方对话...",
    "result": "最终采纳混合方案，项目按时交付，团队满意度提升20%"
  }
}
```

---

## 页面设计

### 1. 管理员页面

#### 1.1 通用技能管理（云计算、DevOps等）

**布局**：三栏（复用Phase 4 AlgorithmLearning.vue）

**左侧Tab**：
- 云计算
- eBay Knowledge
- DevOps
- Software Development

**中间栏**：
- Major Category列表（可折叠）
- Focus Area树（每个大分类下）

**右侧栏**：
- Learning Content管理（理论、代码、命令、架构图）
- Questions管理（CRUD）

**新增功能**：
- 批量导入学习资料（Markdown文件）
- 知识点标签管理（自动提取或手动添加）

#### 1.2 Behavioral技能管理

**布局**：两栏（简化版）

**左侧**：
- Focus Area列表（Leadership, Conflict Resolution等）

**右侧**：
- Learning Content（STAR方法论文档）
- Questions管理（题库维护）

**特殊功能**：
- 答题模版编辑器（STAR框架字段定义）

---

### 2. 用户学习页面

#### 2.1 学习模式（新页面：GeneralSkillLearning.vue）

**布局**：左右分栏

**左侧导航**（30%宽度）：
```
[Skill Tabs]
  └─ 云计算 | DevOps | Behavioral

[知识树]
  云计算
    ├─ Kubernetes（大分类）
    │   ├─ Pod管理（Focus Area）✓
    │   └─ Service网络
    └─ Network
        └─ VPC设计

[进度显示]
  已学习: 15/30 Focus Areas
```

**右侧内容区**（70%宽度）：
```
[Focus Area标题] Pod管理

[学习资料]
  📄 理论文档（Markdown渲染）
  💻 代码示例（语法高亮）
  📊 架构图

  [我的整体笔记]
  （可展开/折叠的笔记编辑区）

[知识点笔记]
  #Pod调度策略 [编辑笔记]
  #生命周期管理 [编辑笔记]

[相关试题]
  Q1: 解释Pod的生命周期
  Q2: 如何调试Pod启动失败

  [查看答题笔记] → 跳转到"我的试题库"
```

**交互特性**：
- 单页连续阅读（滚动到底自动加载下一个Focus Area）
- 知识点高亮（点击跳转到笔记编辑）
- 学习进度自动保存

#### 2.2 Behavioral学习模式

**布局差异**：
- 左侧：直接显示Focus Area列表（隐藏大分类层级）
- 右侧：STAR方法论文档 + 典型题目示例

**示例内容**：
```
[Focus Area] Conflict Resolution

[STAR方法论]
  1. Situation: 如何描述冲突背景
  2. Task: 明确你的角色和职责
  3. Action: 具体行动步骤（建议3-5个）
  4. Result: 可量化的成果

[典型题目]
  Q: "Describe a time when you resolved a conflict..."
  [使用STAR模版回答] → 跳转到答题模式
```

---

### 3. 答题模式（扩展MyQuestionBank.vue）

#### 3.1 新增功能

**A. 答题模版选择**

题目详情页新增：
```
[选择答题框架]
  ○ 自由作答
  ● STAR框架
  ○ 技术答题模版

（选中后，下方显示结构化输入框）
```

**B. STAR框架输入界面**

```
[我的回答]

Situation（情境）
┌────────────────────────────────────┐
│ 描述项目背景和挑战...              │
│                                    │
└────────────────────────────────────┘

Task（任务）
┌────────────────────────────────────┐
│ 你的具体职责是什么...              │
└────────────────────────────────────┘

Action（行动）
┌────────────────────────────────────┐
│ 1. ...                             │
│ 2. ...                             │
└────────────────────────────────────┘

Result（结果）
┌────────────────────────────────────┐
│ 可量化的成果...                    │
└────────────────────────────────────┘

[保存笔记]
```

**C. 技术答题模版**

```
[我的回答]

核心概念
┌────────────────────────────────────┐
│ 简明扼要解释核心概念...            │
└────────────────────────────────────┘

深入解析
┌────────────────────────────────────┐
│ 技术细节、工作原理...              │
└────────────────────────────────────┘

实际案例
┌────────────────────────────────────┐
│ 我在XX项目中的实践...              │
└────────────────────────────────────┘

关联知识点
[ ] Pod管理  [ ] Scheduler  [ ] 健康检查

[保存笔记]
```

#### 3.2 筛选和导航

**新增筛选条件**：
```
[技能类型] 云计算 ▼
[大分类] Kubernetes ▼
[Focus Area] Pod管理 ▼
[答题状态] 全部 | 已答 | 未答
[模版类型] 全部 | STAR | 技术类
```

---

## 数据模型设计

### 新增表

#### 1. answer_templates（答题模版）
```sql
CREATE TABLE answer_templates (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  template_name VARCHAR(50) UNIQUE NOT NULL COMMENT '模版名称（STAR, Technical等）',
  template_fields JSON NOT NULL COMMENT '模版字段定义',
  description TEXT COMMENT '模版说明',
  skill_types JSON COMMENT '适用的技能类型（如["Behavioral", "People Management"]）',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '答题模版表';
```

#### 2. user_learning_notes（整体笔记）
```sql
CREATE TABLE user_learning_notes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  content_id BIGINT NOT NULL COMMENT '关联learning_content.id',
  overall_note TEXT COMMENT '整体理解笔记（Markdown）',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_content (user_id, content_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (content_id) REFERENCES learning_content(id) ON DELETE CASCADE
) COMMENT '用户学习整体笔记';
```

#### 3. user_knowledge_point_notes（知识点笔记）
```sql
CREATE TABLE user_knowledge_point_notes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  content_id BIGINT NOT NULL COMMENT '关联learning_content.id',
  knowledge_point_tag VARCHAR(100) NOT NULL COMMENT '知识点标签',
  note TEXT COMMENT '知识点笔记（Markdown）',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_content_kp (user_id, content_id, knowledge_point_tag),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (content_id) REFERENCES learning_content(id) ON DELETE CASCADE
) COMMENT '用户知识点笔记';
```

### 扩展表

#### 1. learning_content（扩展）
```sql
ALTER TABLE learning_content
  ADD COLUMN content_type ENUM('theory', 'code', 'command', 'diagram') DEFAULT 'theory' COMMENT '内容类型',
  ADD COLUMN code_language VARCHAR(50) COMMENT '代码语言（仅code类型）',
  ADD COLUMN diagram_url VARCHAR(500) COMMENT '图片链接（仅diagram类型）',
  ADD COLUMN knowledge_points JSON COMMENT '知识点标签数组';
```

#### 2. questions（扩展）
```sql
ALTER TABLE questions
  ADD COLUMN question_type ENUM('technical', 'behavioral', 'algorithm') DEFAULT 'technical' COMMENT '题目类型',
  ADD COLUMN answer_key_points JSON COMMENT '答题重点（JSON数组）',
  ADD COLUMN knowledge_points JSON COMMENT '关联知识点标签',
  ADD COLUMN suggested_template VARCHAR(50) COMMENT '推荐答题模版（关联answer_templates.template_name）';
```

#### 3. user_question_notes（扩展）
```sql
ALTER TABLE user_question_notes
  ADD COLUMN template_type VARCHAR(50) COMMENT '使用的答题模版（star, technical等）',
  ADD COLUMN key_points JSON COMMENT '结构化答题要点（按模版字段存储）',
  ADD COLUMN related_knowledge_points JSON COMMENT '关联的知识点标签';
```

---

## 技术方案

### 1. 复用Phase 4架构

**复用组件**：
- `major_categories`表：存储云计算、Kubernetes等大分类
- `focus_area_categories`关联表：Focus Area归属大分类
- 三栏布局UI组件：管理员页面

**差异处理**：
- Skill类型字段：`skills.skill_category` ENUM('career_path', 'algorithm', 'system_design', 'general_technical', 'general_soft')
- 第二类技能（Behavioral）：创建隐式"General"大分类，前端UI隐藏

### 2. 知识点标签系统

**自动提取**：
- 管理员创建Learning Content时，自动提取Markdown标题作为知识点标签
- 示例：`## Pod调度策略` → 标签 "Pod调度策略"

**手动编辑**：
- 管理员可手动添加/删除标签
- 标签存储在`learning_content.knowledge_points` JSON字段

**用户笔记关联**：
- 用户点击正文中的知识点标签 → 弹出笔记编辑器
- 笔记保存到`user_knowledge_point_notes`表

### 3. 答题模版渲染引擎

**前端动态表单生成**：
```javascript
// 根据template_fields JSON动态生成表单
const renderTemplateForm = (template) => {
  return template.template_fields.map(field => ({
    key: field.key,
    label: field.label,
    component: 'textarea',
    placeholder: field.placeholder
  }))
}
```

**后端验证**：
```java
// 验证用户提交的key_points是否符合模版结构
public boolean validateKeyPoints(String templateName, Map<String, String> keyPoints) {
  AnswerTemplate template = templateRepository.findByName(templateName);
  List<String> requiredFields = template.getTemplateFields().stream()
    .map(field -> field.get("key"))
    .collect(Collectors.toList());

  return keyPoints.keySet().containsAll(requiredFields);
}
```

---

## 实施路径

### Phase 6.1: 第一类技能（云计算等）- 2周

**Week 1**:
- [ ] 数据模型扩展（learning_content, questions, user_learning_notes, user_knowledge_point_notes）
- [ ] 复用Phase 4大分类架构（major_categories）
- [ ] 管理员三栏界面（Learning Content CRUD + 知识点标签管理）

**Week 2**:
- [ ] 用户学习页面（GeneralSkillLearning.vue）
- [ ] 整体笔记 + 知识点笔记功能
- [ ] 学习资料类型支持（theory, code, command, diagram）
- [ ] 知识点标签自动提取

### Phase 6.2: 第二类技能（Behavioral）- 1.5周

**Week 3**:
- [ ] 答题模版系统（answer_templates表 + 管理界面）
- [ ] STAR模版预置数据
- [ ] user_question_notes扩展（template_type, key_points）

**Week 4-5**:
- [ ] MyQuestionBank.vue扩展（答题模版选择 + 结构化输入）
- [ ] STAR答题界面（动态表单渲染）
- [ ] Behavioral题库导入（50道典型题）

### Phase 6.3: 整合与优化 - 0.5周

- [ ] 学习模式与答题模式导航联动
- [ ] 进度跟踪（Focus Area学习完成度）
- [ ] 批量导入工具（学习资料 + 题目）

---

## 成功指标

### 功能完整性
- [ ] 支持4个第一类技能（云计算、eBay Knowledge、DevOps、Software Development）
- [ ] 支持3个第二类技能（Behavioral、People Management、Project Management）
- [ ] 每个技能至少3个Focus Area
- [ ] 每个Focus Area至少5篇学习资料
- [ ] 每个Focus Area至少10道试题

### 用户体验
- [ ] 学习模式支持单页连续阅读
- [ ] 知识点笔记编辑流畅（点击标签即开即编）
- [ ] STAR答题模版输入友好（清晰的字段分隔）
- [ ] 学习进度可视化（进度条、已学/未学标记）

### 技术质量
- [ ] 零axios bug（遵循CLAUDE.md Guardrails #8-9）
- [ ] 零DTO bug（遵循Guardrail #10完整性检查）
- [ ] API响应时间 < 200ms（带索引优化）
- [ ] 前端打包体积 < 500KB（代码分割）

---

## 待决策问题

### 1. 知识点标签的存储方式
**选项A**：JSON数组存储在learning_content表
```json
["Pod调度策略", "生命周期管理", "健康检查"]
```
- 优点：简单，无需额外表
- 缺点：难以统计标签使用频率，无法聚合查询

**选项B**：独立knowledge_points表（多对多关系）
```sql
CREATE TABLE knowledge_points (
  id BIGINT PRIMARY KEY,
  tag VARCHAR(100) UNIQUE,
  usage_count INT DEFAULT 0
)
```
- 优点：支持标签聚合查询、热门标签统计
- 缺点：增加表关联复杂度

**建议**：暂用选项A（JSON存储），若后续需要标签推荐功能，再迁移到选项B

### 2. 第二类技能是否需要Learning Content？
**问题**：Behavioral类技能主要是刷题，是否需要STAR方法论文档？

**建议**：需要，理由：
- 新手不了解STAR框架，需要学习文档
- 每个Focus Area有独特的答题技巧（如Leadership vs Conflict Resolution）
- Learning Content可以包含优秀回答示例

### 3. 学习进度如何定义？
**选项A**：完成Focus Area下的所有试题 → 标记为"已学习"
**选项B**：用户点击"标记为已学习"按钮 → 手动标记
**选项C**：查看过学习资料 + 回答过至少1道题 → 自动标记

**建议**：选项C（最灵活），新增`user_focus_area_progress`表：
```sql
CREATE TABLE user_focus_area_progress (
  user_id BIGINT,
  focus_area_id BIGINT,
  viewed_content BOOLEAN DEFAULT FALSE,
  answered_questions INT DEFAULT 0,
  status ENUM('not_started', 'in_progress', 'completed'),
  PRIMARY KEY (user_id, focus_area_id)
)
```

---

## 参考资料

- **Phase 4设计文档**：`/docs/Phase4-设计文档.md`（大分类架构）
- **Phase 3设计文档**：`/docs/Phase3-设计文档.md`（试题库架构）
- **STAR方法论**：https://www.themuse.com/advice/star-interview-method
- **Kubernetes官方文档**：https://kubernetes.io/docs/（学习资料参考）
