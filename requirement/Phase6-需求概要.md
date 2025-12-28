# Phase 6 需求概要 - 通用技能学习模块

## 背景

Phase 2-5已完成核心模块：
- **Phase 2**: 基础的Skill + Focus Area + Learning Resources（侧重学习）
- **Phase 3**: Question Bank（试题库，侧重答题）
- **Phase 4**: Algorithm Learning（学习+练习融合，三层结构Skill → 大分类 → Focus Area）
- **Phase 5**: System Design（基础知识+典型案例，查看/编辑双模式）

Phase 6需要支持剩余技能类型，分为两大类：
1. **第一类**（云计算、eBay Knowledge、DevOps、Software Development、AI/机器学习）：需要三层结构
2. **第二类**（Behavioral、人员管理、项目管理）：两层结构，强调STAR答题法

---

## 核心矛盾与设计决策

### 矛盾点
现有架构存在两个独立入口：
- **职业技能页面**（Phase 2）：树状导航，侧重学习资料浏览
- **我的试题库**（Phase 3）：专注刷题，侧重答题笔记管理

Phase 4/5采用融合模式（学习资料+练习题在同一页面），用户体验更好。

### 设计决策：**双轨制架构** ✅

#### 决策1: 保留两个入口，优化各自定位

**学习模式**（用户学习页面）：
- **定位**：系统化学习新知识（第一次学习某个技能）
- **特点**：完整性、连续性、理论与实践结合
- **交互**：双Tab模式（学习资料 + 试题库）
- **适用场景**：学习云计算新概念、理解STAR方法框架

**答题模式**（我的试题库）：
- **定位**：专项训练和面试准备（已学过，现在要强化）
- **特点**：题目聚焦、模版支持、进度跟踪、搜索模式
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
  └─ Learning Content（STAR方法论 + 其他学习资料）
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

新增`answer_templates`表管理答题框架，并通过新表或在`skills`表中加入`template_id`字段建立Skill与模版的关联关系。

---

## 功能需求

### 1. 第一类技能：云计算、eBay Knowledge、DevOps、Software Development、AI/机器学习

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

**管理界面**：复用"设置-内容-职业技能库"页面

**布局**：两栏

**左侧**（上下两栏树形结构）：
- **上栏树**：职业路径 → 技能（可展开/折叠）
- **下栏树**：大分类 → Focus Area（根据上栏选中的技能动态显示）

**右侧**：
- Learning Content管理（理论、代码、命令、架构图）
- Questions管理（CRUD）
- AI学习笔记导入功能（管理员导入AI生成的学习笔记）

#### 1.2 学习资料管理

**Learning Content类型**：
- 理论文档（Markdown）
- 代码示例（代码片段 + 语言标识）
- 命令示例（Shell、kubectl等）
- 架构图（图片URL）

<Austin> 需要是文档和录像，代码命令架构图可能不重要，会在文档中出现

**扩展learning_contents表**：
```sql
ALTER TABLE learning_contents
  ADD COLUMN content_type ENUM('theory', 'code', 'command', 'diagram', 'article', 'video', 'code_example', 'template', 'case_study') COMMENT '内容类型',
  ADD COLUMN code_language VARCHAR(50) COMMENT '代码语言（仅code/code_example类型）',
  ADD COLUMN diagram_url VARCHAR(500) COMMENT '图片链接（仅diagram类型）';
```

**AI学习笔记**（所有人可见）：
- 新增字段：`is_ai_generated BOOLEAN DEFAULT FALSE` - 标记是否为AI生成
- AI笔记用于整理学习内容和总结知识点
- 管理员可以导入或编辑AI笔记

#### 1.3 试题管理

**复用questions表**（已有question_type字段）：
- `question_type`：已有字段，包含'behavioral'、'technical'、'design'、'programming'
- `answer_requirement`：已有字段，用于存储答题重点
- 不需要新增`answer_key_points`和`knowledge_points`字段

**知识点关联**：
- 用户自己在`user_learning_content_knowledge_points`表中总结知识点
- 可关联AI总结的知识点或用户自己的知识点
<Austin> AI生成的可以认为是特殊用户AI 生成的学习总结，和知识点总结，还是复用user_learning_content_notes，user_learning_content_knowledge_points表

#### 1.4 用户学习笔记

**复用现有表**：

**A. 整体笔记**（`user_learning_content_notes`表）
- 已有表结构，不需要新增

**B. 知识点笔记**（`user_learning_content_knowledge_points`表）
- 已有表结构，不需要新增
- 知识点是用户自己总结的

**AI笔记功能**（新增）：
- 在`learning_contents`表中新增字段标记AI生成的笔记
- AI笔记所有人可见，帮助用户整理学习内容和知识点 

<Austin> 不需要，可以固定一个特定的用户id，比如-1来表示AI生成比较，就不要在learning_contents表加字段

#### 1.5 用户答题笔记

**复用user_question_notes表**：
- 已有`core_strategy`字段：用于技术类答题的核心思路
- 已有`note_content`字段：用于Behavioral类的STAR答题笔记
- 不需要新增`key_points`和`related_knowledge_points`字段
- 可关联AI总结的知识点或用户的知识点（通过应用逻辑实现）

<Austin> - 可关联AI总结的知识点或用户的知识点（通过应用逻辑实现）, 这个最好加一个字段

---

### 2. 第二类技能：Behavioral、人员管理、项目管理

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

**管理界面**：
- 复用"设置-内容-职业技能库"页面
- 左侧下栏：直接显示Focus Area列表（隐藏大分类层级）
- 右侧：学习资料和试题管理

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
  template_name VARCHAR(50) UNIQUE NOT NULL COMMENT '模版名称（STAR, Technical等）',
  template_fields JSON NOT NULL COMMENT '模版字段定义',
  description TEXT COMMENT '模版说明',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '答题模版表';
```

**Skill与模版关联**：
- **方案A**：在`skills`表中新增`template_id`字段
- **方案B**：创建新的关联表`skill_templates`

**STAR模版字段定义**：
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
<Austin> 用方案B吧，更灵活，对于一个skill的问题，答题模版，可能可以用skill默认模版，但也用所有问题的通用模版，就是自己写markdown内容。


**模版管理**：
- 答题模版是针对Skill的
- 需要专门的管理页面或在现有设置页面中管理

#### 2.3 Behavioral题目管理

**questions表**（已有字段）：
- `question_type: 'behavioral'`
- `answer_requirement`: 存储答题要求

**示例题目**：
```
题目: "Describe a time when you had to resolve a conflict within your team."
question_type: behavioral
```

#### 2.4 用户STAR答题笔记

**user_question_notes表**（复用已有字段）：
- 使用已有的`note_content`字段存储STAR答题笔记
- 前端根据Skill关联的模版动态渲染输入界面
- 不需要新增`key_points`字段

**存储示例**（使用note_content字段）：
```markdown
**Situation（情境）**
在eBay搜索团队，两位工程师对架构设计有分歧...

**Task（任务）**
作为Tech Lead，需要在2周内解决分歧并推进项目...

**Action（行动）**
1. 组织技术评审会议
2. 建立决策框架
3. 促进双方对话

**Result（结果）**
最终采纳混合方案，项目按时交付，团队满意度提升20%
```

---

## 页面设计

### 1. 管理员页面

#### 1.1 职业技能库管理（统一页面）

**路径**：设置 → 内容 → 职业技能库

**布局**：两栏

**左侧**（上下两栏树形结构）：
- **上栏树**：职业路径 → 技能（树形展开）
- **下栏树**：
  - **第一类技能**：大分类 → Focus Area
  - **第二类技能**：直接显示Focus Area（隐藏大分类）

**右侧**：
- Learning Content管理（CRUD）
  - 理论文档
  - 代码示例（带语言标识）
  - 命令示例
  - 架构图
  - AI学习笔记导入
- Questions管理（CRUD）

**特殊功能**：
- AI学习笔记导入（管理员可导入AI生成的学习资料）
- 试题没有知识点字段，知识点由用户自己总结

<Austin> AI学习笔记是针对学习内容的，右侧是两个tab，一个是学习内容tab，对于学习内容tab，编辑内容，对于某个内容，可以导入AI学习笔记，一个是试题库tab，试题库，显示试题列表，可以新增修改试题


#### 1.2 答题模版管理

**位置**：待定（可以是专门页面或在职业技能库页面中管理）

**功能**：
- 模版CRUD（创建、编辑、删除STAR、Technical等模版）
- 模版字段定义（JSON配置）
- Skill与模版关联管理

---

### 2. 用户学习页面

#### 2.1 统一学习页面（复用现有页面）

**路径**：学习 → 职业路径 → 职业技能

**布局**：两栏

**左侧**：
- 复用"设置-内容-职业技能库"页面的左侧布局
- **上栏树**：职业路径 → 技能
- **下栏树**：
  - **第一类技能**：大分类 → Focus Area
  - **第二类技能**：直接显示Focus Area
- 不显示学习进度

**右侧内容区**（双Tab布局，类似Phase 4算法学习）：

**Tab 1: 学习资料**
```
[Focus Area标题] Pod管理

[学习资料列表]
  📄 理论文档（Markdown渲染）
  💻 代码示例（语法高亮）
  🖼️ 架构图

  🤖 AI学习笔记（标记AI生成）

[我的整体笔记]
  （可展开/折叠的笔记编辑区）

[知识点笔记]
  用户自己总结的知识点列表
  - 点击可编辑
```
<Austin> 对于每个学习资料，可以显示学习资料内容，然后AI的学习笔记和我的笔记（很多card，显示整体笔记和知识点笔记），如果AI和我的知识点标题重合了，可以合并在一起。

**Tab 2: 试题库**
```
[试题列表]
  Q1: 解释Pod的生命周期
  Q2: 如何调试Pod启动失败

[浏览模式 / 答题模式]
  - 浏览模式：查看题目和要求
  - 答题模式：根据Skill关联的模版呈现答案输入界面
    - Behavioral类：STAR框架输入
    - 技术类：自由答题或结构化输入
```

#### 2.2 Behavioral学习模式

**无需单独页面**，和2.1相同：
- 左侧：直接显示Focus Area（隐藏大分类）
- 右侧：
  - 学习资料Tab：STAR方法论文档 + 其他学习资料
  - 试题库Tab：
    - 浏览模式：查看题目
    - 答题模式：STAR框架结构化输入（根据Skill模版动态渲染）

---

### 3. 答题模式（改写MyQuestionBank.vue）

#### 3.1 搜索模式

**新增筛选条件**：
```
[技能类型] 云计算 ▼
[大分类] Kubernetes ▼（仅第一类技能显示）
[Focus Area] Pod管理 ▼
[题目类型] 全部 | Behavioral | 技术 | 设计 | 编程
[答题状态] 全部 | 已答 | 未答
```

#### 3.2 答题界面

**根据Skill模版动态渲染**：

**Behavioral类（STAR模版）**：
```
[我的回答]

Situation（情境）
┌────────────────────────────────────┐
│ 描述项目背景和挑战...              │
└────────────────────────────────────┘

Task（任务）
┌────────────────────────────────────┐
│ 你的具体职责是什么...              │
└────────────────────────────────────┘

Action（行动）
┌────────────────────────────────────┐
│ 你采取的具体行动...                │
└────────────────────────────────────┘

Result（结果）
┌────────────────────────────────────┐
│ 可量化的成果...                    │
└────────────────────────────────────┘

[保存笔记] → 保存到note_content字段（Markdown格式）
```
<Austin> 仍然可以有核心思路字段，也可以关联知识点。

**技术类**：
```
[核心思路]（core_strategy字段）
┌────────────────────────────────────┐
│ 核心概念、技术细节...              │
└────────────────────────────────────┘

[详细回答]（note_content字段）
┌────────────────────────────────────┐
│ 深入解析、实际案例...              │
└────────────────────────────────────┘

[关联知识点]
  可选择AI总结的知识点或用户自己的知识点

[保存笔记]
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
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '答题模版表';
```

**Skill与模版关联**（待定方案）：

**方案A**：在skills表添加字段
```sql
ALTER TABLE skills
  ADD COLUMN template_id BIGINT COMMENT '关联的答题模版ID',
  ADD CONSTRAINT fk_skill_template FOREIGN KEY (template_id) REFERENCES answer_templates(id);
```

**方案B**：创建关联表
```sql
CREATE TABLE skill_templates (
  skill_id BIGINT NOT NULL,
  template_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (skill_id, template_id),
  FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
  FOREIGN KEY (template_id) REFERENCES answer_templates(id) ON DELETE CASCADE
) COMMENT 'Skill与答题模版关联表';
```

### 扩展表

#### 1. learning_contents（扩展）
```sql
ALTER TABLE learning_contents
  ADD COLUMN code_language VARCHAR(50) COMMENT '代码语言（仅code/code_example类型）',
  ADD COLUMN diagram_url VARCHAR(500) COMMENT '图片链接（仅diagram类型）',
  ADD COLUMN is_ai_generated BOOLEAN DEFAULT FALSE COMMENT '是否为AI生成的笔记';
```

**说明**：
- `content_type`已有enum，可支持'article'、'video'、'code_example'等
- 不需要新增`knowledge_points`字段（知识点由用户总结，存在`user_learning_content_knowledge_points`表）

#### 2. questions（无需扩展）

**已有字段满足需求**：
- `question_type`：已有'behavioral'、'technical'、'design'、'programming'
- `answer_requirement`：已有，用于答题重点

**不需要新增**：
- ~~answer_key_points~~（使用answer_requirement）
- ~~knowledge_points~~（知识点由用户总结）
- ~~suggested_template~~（通过Skill关联模版）

#### 3. user_question_notes（无需扩展）

**已有字段满足需求**：
- `note_content`：用于存储STAR答题笔记或技术类详细回答
- `core_strategy`：用于技术类核心思路

**不需要新增**：
- ~~template_type~~（通过question关联的Skill获取模版）
- ~~key_points~~（使用note_content + Markdown格式）
- ~~related_knowledge_points~~（通过应用逻辑关联）

---

## 技术方案

### 1. 复用Phase 4架构

**复用组件**：
- `major_categories`表：存储云计算、Kubernetes等大分类（关联到skill_id）
- `focus_area_categories`关联表：Focus Area归属大分类
- 两栏布局UI组件：管理员和用户页面

**差异处理**：
- 第二类技能（Behavioral）：创建隐式"General"大分类，前端UI隐藏

### 2. 知识点系统

**用户知识点**（`user_learning_content_knowledge_points`表）：
- 用户自己总结的知识点
- 关联到learning_content_id

**AI知识点**（learning_contents表）：
- 管理员导入的AI生成学习笔记
- `is_ai_generated = TRUE`标记
- 所有用户可见

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
// 验证笔记内容是否包含必需的模版字段（可选）
public boolean validateNoteContent(String templateName, String noteContent) {
  AnswerTemplate template = templateRepository.findByName(templateName);
  List<String> requiredLabels = template.getTemplateFields().stream()
    .map(field -> field.get("label"))
    .collect(Collectors.toList());

  // 检查noteContent是否包含所有必需的标签（如"Situation（情境）"）
  return requiredLabels.stream().allMatch(noteContent::contains);
}
```

---

## 实施路径

### Phase 6.1: 数据模型和基础架构 - 1周

**Week 1**:
- [ ] 创建`answer_templates`表
- [ ] 扩展`learning_contents`表（code_language, diagram_url, is_ai_generated）
- [ ] Skill与模版关联（方案A或B）
- [ ] 为第二类技能创建"General"大分类数据
- [ ] STAR和Technical模版预置数据

### Phase 6.2: 管理员页面 - 1周

**Week 2**:
- [ ] 修改"设置-内容-职业技能库"页面
  - [ ] 左侧上下两栏树形结构
  - [ ] 支持第一类/第二类技能的大分类显示逻辑
  - [ ] AI学习笔记导入功能
- [ ] 答题模版管理页面（或集成到现有页面）

### Phase 6.3: 用户学习页面 - 1周

**Week 3**:
- [ ] 修改"学习-职业路径-职业技能"页面
  - [ ] 左侧复用管理页面布局
  - [ ] 右侧双Tab（学习资料 + 试题库）
  - [ ] AI笔记展示（标记AI生成）
  - [ ] 知识点笔记功能

### Phase 6.4: 答题模式 - 1周

**Week 4**:
- [ ] 改写MyQuestionBank.vue
  - [ ] 搜索模式（新增筛选条件）
  - [ ] 根据Skill模版动态渲染答题界面
  - [ ] STAR框架输入界面
  - [ ] 技术类答题界面
  - [ ] 知识点关联功能

### Phase 6.5: 数据导入和测试 - 0.5周

**Week 5**:
- [ ] 云计算、DevOps等技能数据导入
- [ ] Behavioral题库导入（50道典型题）
- [ ] AI学习笔记导入
- [ ] 全流程测试

---

## 成功指标

### 功能完整性
- [ ] 支持5个第一类技能（云计算、eBay Knowledge、DevOps、Software Development、AI/机器学习）
- [ ] 支持3个第二类技能（Behavioral、人员管理、项目管理）
- [ ] 每个技能至少3个Focus Area
- [ ] 每个Focus Area至少5篇学习资料
- [ ] 每个Focus Area至少10道试题

### 用户体验
- [ ] 学习模式双Tab切换流畅
- [ ] AI笔记清晰标记，易于识别
- [ ] STAR答题模版输入友好（清晰的字段分隔）
- [ ] 知识点笔记编辑流畅

### 技术质量
- [ ] 零axios bug（遵循CLAUDE.md Guardrails #8-9）
- [ ] 零DTO bug（遵循Guardrail #10完整性检查）
- [ ] API响应时间 < 200ms（带索引优化）
- [ ] 前端打包体积 < 500KB（代码分割）

---

## 待决策问题

### 1. Skill与模版的关联方式

**方案A**：在`skills`表中新增`template_id`字段
- 优点：简单直接，查询效率高
- 缺点：一个Skill只能关联一个模版

**方案B**：创建`skill_templates`关联表
- 优点：支持多对多关系（未来扩展）
- 缺点：增加表关联复杂度

**建议**：暂用方案A（一个Skill一个模版），若后续需要多模版支持，再迁移到方案B
<Austin> 用方案B

### 2. 答题模版管理位置

**方案A**：专门的模版管理页面
- 优点：功能独立，便于维护
- 缺点：增加菜单复杂度

**方案B**：集成在"设置-内容"页面
- 优点：减少菜单层级
- 缺点：页面功能可能过于复杂

**建议**：方案A（专门页面），位置：设置 → 内容 → 答题模版管理
---
<Austin> 方案A

## 参考资料

- **Phase 4设计文档**：`/docs/Phase4-设计文档.md`（大分类架构）
- **Phase 3设计文档**：`/docs/Phase3-设计文档.md`（试题库架构）
- **STAR方法论**：https://www.themuse.com/advice/star-interview-method
- **Kubernetes官方文档**：https://kubernetes.io/docs/（学习资料参考）
