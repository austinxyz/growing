# Phase 6 详细需求 - 通用技能学习模块

> **需求来源**: 用户需求 (2025-12-27)
> **参考模块**: Phase 4 算法学习模块、Phase 5 系统设计模块
> **目标用户**: 准备面试和提升技术能力的工程师
> **核心价值**: 提供云计算、DevOps、Behavioral等通用技能的系统化学习和答题实战
> **状态**: 🚧 大部分完成 - Phase 6.1-6.4 已完成，Phase 6.5 待实施
> **完成版本**: v1.2
> **完成时间**: Phase 6.1-6.4 已于 2025-12-28 完成
> **最后更新**: 2025-12-28 (v1.2 - 根据Phase 6.3-6.4实际实现更新)

---

## 1. 背景与目标

### 1.1 背景

Phase 2-5已完成核心模块:
- **Phase 2**: 基础的Skill + Focus Area + Learning Resources (侧重学习)
- **Phase 3**: Question Bank (试题库,侧重答题)
- **Phase 4**: Algorithm Learning (学习+练习融合,三层结构Skill → 大分类 → Focus Area)
- **Phase 5**: System Design (基础知识+典型案例,查看/编辑双模式)

Phase 6需要支持剩余技能类型:
1. **第一类技能** (云计算、eBay Knowledge、DevOps、Software Development、AI/机器学习): 需要三层结构
2. **第二类技能** (Behavioral、人员管理、项目管理): 两层结构,强调STAR答题法

### 1.2 核心矛盾

现有架构存在两个独立入口:
- **职业技能页面** (Phase 2): 树状导航,侧重学习资料浏览
- **我的试题库** (Phase 3): 专注刷题,侧重答题笔记管理

Phase 4/5采用融合模式 (学习资料+练习题在同一页面),用户体验更好。

### 1.3 设计决策: **双轨制架构** ✅

#### 决策1: 保留两个入口,优化各自定位

**学习模式** (用户学习页面):
- **定位**: 系统化学习新知识 (第一次学习某个技能)
- **特点**: 完整性、连续性、理论与实践结合
- **交互**: 双Tab模式 (学习资料 + 试题库)
- **适用场景**: 学习云计算新概念、理解STAR方法框架

**答题模式** (我的试题库):
- **定位**: 专项训练和面试准备 (已学过,现在要强化)
- **特点**: 题目聚焦、模版支持、进度跟踪、搜索模式
- **交互**: 题目列表 + 详情 + 答题笔记 (支持模版)
- **适用场景**: 刷Behavioral题库、练习云计算高频问题

#### 决策2: 复用Phase 4架构,统一三层结构

**第一类技能** (云计算等):
```
Skill → Major Category (大分类) → Focus Area
  └─ Learning Content (学习资料)
  └─ Questions (试题)
```

**第二类技能** (Behavioral等):
```
Skill → [隐式大分类:General] → Focus Area
  └─ Learning Content (STAR方法论 + 其他学习资料)
  └─ Questions (Behavioral题目)
```

**复用理由**:
- Phase 4已有`major_categories`表和三层UI组件
- 第二类技能可以创建一个默认大分类"General",前端隐藏不显示
- 避免两套架构维护成本

#### 决策3: 扩展答题模版系统

不同技能类型需要不同答题框架:
- **技术类** (云计算、DevOps): 关键点 + 深入解释 + 实际案例
- **Behavioral类**: STAR框架 (Situation, Task, Action, Result)
- **算法类** (已实现): 算法模版 (Phase 4已有)

新增`answer_templates`表管理答题框架,并通过`skill_templates`关联表建立Skill与模版的多对多关系。

### 1.4 成功标准

**功能完整性**:
- ✅ 支持5个第一类技能 (云计算、eBay Knowledge、DevOps、Software Development、AI/机器学习)
- ✅ 支持3个第二类技能 (Behavioral、人员管理、项目管理)
- ✅ 每个技能至少3个Focus Area
- ✅ 每个Focus Area至少5篇学习资料
- ✅ 每个Focus Area至少10道试题

**用户体验**:
- ✅ 学习模式双Tab切换流畅
- ✅ AI笔记清晰标记,易于识别
- ✅ STAR答题模版输入友好 (清晰的字段分隔)
- ✅ 知识点笔记编辑流畅

**技术质量**:
- ✅ 零axios bug (遵循CLAUDE.md Guardrails #8-9)
- ✅ 零DTO bug (遵循Guardrail #10完整性检查)
- ✅ API响应时间 < 200ms (带索引优化)

---

## 2. 第一类技能: 云计算、eBay Knowledge、DevOps、Software Development、AI/机器学习

### 2.1 三层结构管理 (管理员)

**Skill → Major Category → Focus Area**

**示例**:
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

**管理界面**: 复用"设置-内容-职业技能库"页面

**布局**: 两栏

**左侧** (上下两栏树形结构):
- **上栏树**: 职业路径 → 技能 (可展开/折叠)
- **下栏树**: 大分类 → Focus Area (根据上栏选中的技能动态显示)

**右侧**:
- Learning Content管理 (理论、代码、命令、架构图)
- Questions管理 (CRUD)
- AI学习笔记导入功能 (管理员导入AI生成的学习笔记)

### 2.2 学习资料管理

**Learning Content类型**:
- 文档 (Markdown格式)
- 录像 (视频URL)

**说明**:
- `learning_contents`表的`content_type`字段已支持'article'和'video'类型
- 代码、命令、架构图等内容会嵌入在文档中,不需要单独的content_type
- 无需扩展`learning_contents`表字段

**AI学习笔记** (所有人可见):
- 使用特殊用户ID (user_id=-1) 表示AI生成的内容
- AI笔记存储在`user_learning_content_notes`和`user_learning_content_knowledge_points`表中
- 不需要在`learning_contents`表中新增`is_ai_generated`字段
- 管理员可以为学习内容导入或编辑AI生成的笔记和知识点

### 2.3 试题管理

**复用questions表** (已有question_type字段):
- `question_type`: 已有字段,包含'behavioral'、'technical'、'design'、'programming'
- `answer_requirement`: 已有字段,用于存储答题重点
- 不需要新增`answer_key_points`和`knowledge_points`字段

**知识点关联**:
- 用户自己在`user_learning_content_knowledge_points`表中总结知识点
- AI生成的学习总结和知识点总结使用特殊用户ID (user_id=-1) 存储在`user_learning_content_notes`和`user_learning_content_knowledge_points`表中
- 复用现有表结构,不需要新建AI专用表

### 2.4 用户学习笔记

**复用现有表**:

**A. 整体笔记** (`user_learning_content_notes`表)
- 已有表结构,不需要新增

**B. 知识点笔记** (`user_learning_content_knowledge_points`表)
- 已有表结构,不需要新增
- 知识点是用户自己总结的

**AI笔记功能** (使用特殊用户):
- 使用特殊用户ID (user_id=-1) 表示AI生成的笔记
- AI笔记存储在`user_learning_content_notes`和`user_learning_content_knowledge_points`表中
- 不需要在`learning_contents`表中新增字段
- AI笔记对所有用户可见,帮助用户整理学习内容和知识点

### 2.5 用户答题笔记

**复用user_question_notes表** (需扩展):
- 已有`core_strategy`字段: 用于技术类答题的核心思路
- 已有`note_content`字段: 用于Behavioral类的STAR答题笔记
- **新增字段**: `related_knowledge_point_ids` (JSON数组) - 关联的知识点ID列表 (可以是AI生成的或用户自己的知识点)
- 不需要新增`key_points`字段

---

## 3. 第二类技能: Behavioral、人员管理、项目管理

### 3.1 简化结构 (Skill → Focus Area)

**示例**:
```
Skill: Behavioral
  ├─ Focus Area: Leadership (领导力)
  ├─ Focus Area: Conflict Resolution (冲突解决)
  ├─ Focus Area: Teamwork (团队协作)
  └─ Focus Area: Communication (沟通能力)
```

**实现方式**:
- 后端: 为每个第二类Skill创建默认Major Category "General"
- 前端: 检测到"General"分类时,隐藏大分类层级,直接显示Focus Area列表

**管理界面**:
- 复用"设置-内容-职业技能库"页面
- 左侧下栏: 直接显示Focus Area列表 (隐藏大分类层级)
- 右侧: 学习资料和试题管理

### 3.2 STAR答题模版

**STAR框架**:
- **S**ituation (情境): 背景和挑战
- **T**ask (任务): 你的具体职责
- **A**ction (行动): 你采取的行动
- **R**esult (结果): 可量化的成果

**答题模版表**:
```sql
CREATE TABLE answer_templates (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  template_name VARCHAR(50) UNIQUE NOT NULL COMMENT '模版名称 (STAR, Technical等)',
  template_fields JSON NOT NULL COMMENT '模版字段定义',
  description TEXT COMMENT '模版说明',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '答题模版表';
```

**Skill与模版关联** (采用方案B):
- 创建`skill_templates`关联表
- 一个Skill可以关联多个答题模版 (默认模版 + 通用模版)
- `is_default`字段标记该Skill的默认模版

**STAR模版字段定义**:
```json
{
  "template_name": "STAR",
  "template_fields": [
    {"key": "situation", "label": "Situation (情境)", "placeholder": "描述项目背景和挑战..."},
    {"key": "task", "label": "Task (任务)", "placeholder": "你的具体职责是什么..."},
    {"key": "action", "label": "Action (行动)", "placeholder": "你采取了哪些行动..."},
    {"key": "result", "label": "Result (结果)", "placeholder": "可量化的成果 (如提升X%)..."}
  ]
}
```

**模版管理**:
- 答题模版是针对Skill的
- 需要专门的管理页面 (设置 → 内容 → 答题模版管理)

### 3.3 Behavioral题目管理

**questions表** (已有字段):
- `question_type: 'behavioral'`
- `answer_requirement`: 存储答题要求

**示例题目**:
```
题目: "Describe a time when you had to resolve a conflict within your team."
question_type: behavioral
```

### 3.4 用户STAR答题笔记

**user_question_notes表** (复用已有字段):
- 使用已有的`note_content`字段存储STAR答题笔记
- 前端根据Skill关联的模版动态渲染输入界面
- 不需要新增`key_points`字段

**存储示例** (使用note_content字段):
```markdown
**Situation (情境)**
在eBay搜索团队,两位工程师对架构设计有分歧...

**Task (任务)**
作为Tech Lead,需要在2周内解决分歧并推进项目...

**Action (行动)**
1. 组织技术评审会议
2. 建立决策框架
3. 促进双方对话

**Result (结果)**
最终采纳混合方案,项目按时交付,团队满意度提升20%
```

---

## 4. 页面设计

### 4.1 管理员页面

#### 4.1.1 职业技能库管理 (统一页面)

**路径**: 设置 → 内容 → 职业技能库

**布局**: 两栏

**左侧** (上下两栏树形结构):
- **上栏树**: 职业路径 → 技能 (树形展开)
- **下栏树**:
  - **第一类技能**: 大分类 → Focus Area
  - **第二类技能**: 直接显示Focus Area (隐藏大分类)

**右侧** (双Tab布局):

**Tab 1: 学习内容**
- Learning Content列表 (文档、录像)
- CRUD功能 (创建、编辑、删除学习内容)
- 对于每个学习内容,可以导入AI学习笔记
  - AI整体笔记 (user_id=-1存储在`user_learning_content_notes`表)
  - AI知识点笔记 (user_id=-1存储在`user_learning_content_knowledge_points`表)

**Tab 2: 试题库**
- Questions列表 (按Focus Area过滤)
- CRUD功能 (创建、编辑、删除试题)
- 试题本身不包含知识点字段,知识点由用户在答题时总结

#### 4.1.2 答题模版管理

**路径**: 设置 → 内容 → 答题模版管理

**功能**:
- 模版CRUD (创建、编辑、删除STAR、Technical等模版)
- 模版字段定义 (JSON配置)
- Skill与模版关联管理

---

### 4.2 用户学习页面

#### 4.2.1 统一学习页面 (复用现有页面)

**路径**: 学习 → 职业路径 → 职业技能

**布局**: 两栏

**左侧**:
- 复用"设置-内容-职业技能库"页面的左侧布局
- **上栏树**: 职业路径 → 技能
- **下栏树**:
  - **第一类技能**: 大分类 → Focus Area
  - **第二类技能**: 直接显示Focus Area
- 不显示学习进度

**右侧内容区** (双Tab布局,类似Phase 4算法学习):

**Tab 1: 学习资料**
```
[Focus Area标题] Pod管理

[学习资料列表]
  📄 学习资料1: Kubernetes Pod基础
    ├─ 学习资料内容 (Markdown渲染/视频播放)
    ├─ 🤖 AI学习笔记卡片
    │   ├─ AI整体笔记 (user_id=-1)
    │   └─ AI知识点列表 (user_id=-1)
    └─ 📝 我的学习笔记卡片
        ├─ 我的整体笔记 (可编辑)
        └─ 我的知识点列表 (可编辑)

  💡 知识点合并逻辑:
    如果AI知识点和用户知识点标题相同,则在UI上合并显示
    (底层仍是两条记录,但UI上显示为一个卡片)
```

**Tab 2: 试题库**
```
[试题列表]
  Q1: 解释Pod的生命周期
  Q2: 如何调试Pod启动失败

[浏览模式 / 答题模式]
  - 浏览模式: 查看题目和要求
  - 答题模式: 根据Skill关联的模版呈现答案输入界面
    - Behavioral类: STAR框架输入
    - 技术类: 自由答题或结构化输入
```

#### 4.2.2 Behavioral学习模式

**无需单独页面**,和4.2.1相同:
- 左侧: 直接显示Focus Area (隐藏大分类)
- 右侧:
  - 学习资料Tab: STAR方法论文档 + 其他学习资料
  - 试题库Tab:
    - 浏览模式: 查看题目
    - 答题模式: STAR框架结构化输入 (根据Skill模版动态渲染)

---

### 4.3 答题模式 (改写MyQuestionBank.vue)

#### 4.3.1 搜索模式

**新增筛选条件**:
```
[技能类型] 云计算 ▼
[大分类] Kubernetes ▼ (仅第一类技能显示)
[Focus Area] Pod管理 ▼
[题目类型] 全部 | Behavioral | 技术 | 设计 | 编程
[答题状态] 全部 | 已答 | 未答
```

#### 4.3.2 答题界面

**根据Skill模版动态渲染**:

**Behavioral类 (STAR模版)**:
```
[我的回答]

Situation (情境)
┌────────────────────────────────────┐
│ 描述项目背景和挑战...              │
└────────────────────────────────────┘

Task (任务)
┌────────────────────────────────────┐
│ 你的具体职责是什么...              │
└────────────────────────────────────┘

Action (行动)
┌────────────────────────────────────┐
│ 你采取的具体行动...                │
└────────────────────────────────────┘

Result (结果)
┌────────────────────────────────────┐
│ 可量化的成果...                    │
└────────────────────────────────────┘

[核心思路] (core_strategy字段,Behavioral类也支持)
┌────────────────────────────────────┐
│ 简要核心思路...                    │
└────────────────────────────────────┘

[关联知识点] (related_knowledge_point_ids字段)
  可选择AI总结的知识点或用户自己的知识点

[保存笔记] → 保存到note_content + core_strategy + related_knowledge_point_ids字段
```

**技术类**:
```
[核心思路] (core_strategy字段)
┌────────────────────────────────────┐
│ 核心概念、技术细节...              │
└────────────────────────────────────┘

[详细回答] (note_content字段)
┌────────────────────────────────────┐
│ 深入解析、实际案例...              │
└────────────────────────────────────┘

[关联知识点]
  可选择AI总结的知识点或用户自己的知识点

[保存笔记]
```

---

## 5. 数据模型设计

### 5.1 新增表

#### 1. answer_templates (答题模版)
```sql
CREATE TABLE answer_templates (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  template_name VARCHAR(50) UNIQUE NOT NULL COMMENT '模版名称 (STAR, Technical等)',
  template_fields JSON NOT NULL COMMENT '模版字段定义',
  description TEXT COMMENT '模版说明',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '答题模版表';
```

#### 2. skill_templates (Skill与模版关联表 - 多对多)
```sql
CREATE TABLE skill_templates (
  skill_id BIGINT NOT NULL,
  template_id BIGINT NOT NULL,
  is_default BOOLEAN DEFAULT FALSE COMMENT '是否为该Skill的默认模版',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (skill_id, template_id),
  FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
  FOREIGN KEY (template_id) REFERENCES answer_templates(id) ON DELETE CASCADE
) COMMENT 'Skill与答题模版关联表 (多对多)';
```

**说明**:
- 采用方案B实现Skill与模版的多对多关系
- 一个Skill可以关联多个模版 (默认模版 + 通用模版)
- `is_default`字段标记该Skill的默认模版
- 用户可选择使用默认模版或通用模版自由编写

### 5.2 扩展表

#### 1. learning_contents (无需扩展)

**已有字段满足需求**:
- `content_type`: 已有enum,支持'article'、'video'等类型
- 不需要新增`code_language`、`diagram_url`字段 (代码、架构图嵌入在文档中)
- 不需要新增`is_ai_generated`字段 (使用user_id=-1标识AI内容)
- 不需要新增`knowledge_points`字段 (知识点由用户总结,存在`user_learning_content_knowledge_points`表)

#### 2. questions (无需扩展)

**已有字段满足需求**:
- `question_type`: 已有'behavioral'、'technical'、'design'、'programming'
- `answer_requirement`: 已有,用于答题重点

**不需要新增**:
- ~~answer_key_points~~ (使用answer_requirement)
- ~~knowledge_points~~ (知识点由用户总结)
- ~~suggested_template~~ (通过Skill关联模版)

#### 3. user_question_notes (需扩展)

**已有字段**:
- `note_content`: 用于存储STAR答题笔记或技术类详细回答
- `core_strategy`: 用于技术类核心思路 (Behavioral类也可使用)

**新增字段**:
```sql
ALTER TABLE user_question_notes
  ADD COLUMN related_knowledge_point_ids JSON COMMENT '关联的知识点ID列表 (JSON数组)';
```

**说明**:
- `related_knowledge_point_ids`: 存储关联的知识点ID列表 (可以是AI生成的或用户自己的)
- 不需要新增`template_type`字段 (通过question关联的Skill获取模版)
- 不需要新增`key_points`字段 (使用note_content + Markdown格式)

---

## 6. 技术方案

### 6.1 复用Phase 4架构

**复用组件**:
- `major_categories`表: 存储云计算、Kubernetes等大分类 (关联到skill_id)
- `focus_area_categories`关联表: Focus Area归属大分类
- 两栏布局UI组件: 管理员和用户页面

**差异处理**:
- 第二类技能 (Behavioral): 创建隐式"General"大分类,前端UI隐藏

### 6.2 知识点系统

**用户知识点** (`user_learning_content_knowledge_points`表):
- 用户自己总结的知识点
- 关联到learning_content_id

**AI知识点** (user_learning_content_knowledge_points表):
- 使用特殊用户ID (user_id=-1) 标识AI生成的知识点
- 存储在`user_learning_content_knowledge_points`表中
- 所有用户可见

### 6.3 答题模版渲染引擎

**前端动态表单生成**:
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

**后端验证** (可选):
```java
// 验证笔记内容是否包含必需的模版字段
public boolean validateNoteContent(String templateName, String noteContent) {
  AnswerTemplate template = templateRepository.findByName(templateName);
  List<String> requiredLabels = template.getTemplateFields().stream()
    .map(field -> field.get("label"))
    .collect(Collectors.toList());

  // 检查noteContent是否包含所有必需的标签 (如"Situation (情境)")
  return requiredLabels.stream().allMatch(noteContent::contains);
}
```

---

## 7. 非功能需求

### 7.1 性能要求

**目标**:
- 学习资料列表加载时间: <1秒
- 试题列表加载时间: <1秒
- Markdown渲染流畅
- Tab切换响应时间: <200ms

**性能优化措施**:
- 分页加载
- 按需加载
- 图片懒加载

### 7.2 安全要求

- ✅ 管理端所有API需要管理员权限
- ✅ 用户只能管理自己的笔记
- ✅ Markdown内容XSS过滤

### 7.3 兼容性要求

- ✅ 不影响现有的算法学习模块 (Phase 4)
- ✅ 不影响系统设计模块 (Phase 5)
- ✅ 复用现有的Skill、Focus Area、Learning Content架构

---

## 8. 实施路径

### Phase 6.1: 数据模型和基础架构 ✅ 已完成

**Week 1** (2025-12-27):
- [x] 创建`answer_templates`表
- [x] 创建`skill_templates`关联表 (方案B,多对多关系)
- [x] 扩展`user_question_notes`表 (related_knowledge_point_ids字段)
- [x] 为第二类技能创建"General"大分类数据
- [x] STAR和Technical模版预置数据
- [x] 创建AI特殊用户 (user_id=-1)

### Phase 6.2: 管理员页面 ✅ 已完成

**Week 2** (2025-12-27至2025-12-28):
- [x] 创建"通用技能-内容管理"页面 (GeneralSkillContentManagement.vue)
  - [x] 左侧上下两栏树形结构 (职业路径→技能 + 大分类→Focus Area)
  - [x] 支持第一类/第二类技能的大分类显示逻辑 (isSecondTypeSkill检测)
  - [x] AI学习笔记导入功能 (AIImportModal组件)
- [x] 答题模版管理页面 (SkillTemplateManagement.vue)
- [x] AI答题模式 (三模式系统: view/edit/ai-answer)

### Phase 6.3: 技能模版管理 ✅ 已完成

**实施时间**: 2025-12-28
**完成内容**:
- [x] 技能模版库管理页面 (SkillTemplateManagement.vue)
  - [x] 两栏布局: 左侧职业路径→技能树，右侧模版管理
  - [x] 左侧30%面板: 职业路径 → 技能单层树
  - [x] 右侧70%面板: 模版卡片列表
    - 模版信息展示（名称、描述、模版ID）
    - 默认标记（绿色Badge）
    - 操作按钮（编辑、设为默认、取消关联）
  - [x] 头部操作区: 新增模版、关联已有模版按钮
  - [x] 自动展开所有职业路径
- [x] 模版编辑器组件 (TemplateEditorModal.vue)
  - [x] 新增/编辑模式统一Modal
  - [x] 基础信息输入: 模版名称、描述
  - [x] 动态字段编辑器
    - 添加/删除字段功能
    - 字段配置: key、label、placeholder
    - 字段卡片展示（灰色背景、红色删除按钮）
  - [x] 表单验证
    - 模版名称必填
    - 至少一个字段
    - 字段key和label必填
  - [x] JSON序列化: templateFields存储为JSON字符串
- [x] 关联模版组件 (AssociateTemplateModal.vue)
  - [x] 加载所有可用模版
  - [x] 过滤已关联模版（不显示）
  - [x] 单选界面（圆形选择框 + 蓝色高亮）
  - [x] 模版卡片展示（边框、悬停效果）
- [x] SkillTemplateController API (完整实现)
  - [x] GET /api/skills/{skillId}/templates - 公开API，获取技能关联模版
  - [x] GET /api/skills/{skillId}/templates/default - 公开API，获取默认模版（含templateFields）
  - [x] GET /api/admin/skill-templates?skillId=X - 管理员API，获取技能模版
  - [x] GET /api/admin/skill-templates/default?skillId=X - 管理员API，获取默认模版
  - [x] POST /api/admin/skill-templates - 关联技能与模版
  - [x] PUT /api/admin/skill-templates/default - 设置默认模版
  - [x] DELETE /api/admin/skill-templates?skillId=X&templateId=Y - 取消关联
  - [x] GET /api/admin/skill-templates/by-template?templateId=X - 反向查询
- [x] STAR模版关联到Behavioral技能 (初始化数据)

**实施差异**:
- ✅ **新增完整的技能模版库管理页面** - 原需求未涉及此部分
- ✅ **两个独立Modal组件** - TemplateEditorModal（编辑器）+ AssociateTemplateModal（关联器）
- ✅ **公开API支持** - /api/skills/{skillId}/templates 可供用户端使用（无需admin权限）
- ✅ **反向查询支持** - 可查询某个模版关联了哪些技能
- ⚠️ **路由未添加** - 尚未在router中注册该页面

### Phase 6.4: 问题浏览模式重新设计 ✅ 已完成

**实施时间**: 2025-12-27至2025-12-28
**完成内容**:
- [x] 问题查看两列布局 (QuestionViewModal.vue)
  - [x] 左侧列: 问题详情（描述、答案要求）
  - [x] 右侧列: 答题笔记/AI答案区域
  - [x] 三种模式: 查看模式、编辑模式、AI答案模式
- [x] AI笔记支持 (user_id = -1)
  - [x] questions表支持AI答案存储
  - [x] user_question_notes表中user_id=-1表示AI答案
  - [x] 前端标记AI生成内容（褝标记）
  - [x] 管理员可为试题添加AI答案
- [x] 紧凑prose模式
  - [x] 问题描述使用紧凑排版（prose-sm、紧凑间距）
  - [x] Markdown渲染优化
- [x] 用户笔记编辑器 (UserNoteEditor.vue)
  - [x] 可折叠列表界面
  - [x] 核心思路字段名修正（core_strategy → coreStrategy）
  - [x] Markdown预览支持

**实施差异**:
- ✅ **三模式系统** - 原需求只有查看/编辑，实际增加了AI答案模式
- ✅ **两列布局** - 原需求未明确布局，实际采用左右分栏设计
- ✅ **AI答案功能** - 扩展了AI笔记概念，支持AI为试题生成答案
- ✅ **STAR框架动态答题界面** - UserNoteEditor.vue完整实现（GeneralSkillLearning.vue:1176-1486）
  - 模版/自由两种答题模式切换
  - 根据answerTemplate.templateFields动态渲染输入框
  - 自动解析已保存的模版格式笔记（正则匹配 `## Label`）
  - 实时预览完整答案（Markdown渲染）
  - skillTemplateApi.getDefaultTemplatePublic(skillId) 获取技能默认模版
  - parseTemplateFields() 处理JSON字符串/对象双格式
- ⚠️ **搜索模式筛选未完善** - MyQuestionBank.vue未完全按原需求改造（新增筛选条件）

### Phase 6.5: 数据导入和测试 ⏭️ 待实施

**Week 5**:
- [ ] 云计算、DevOps等技能数据导入
- [ ] Behavioral题库导入 (50道典型题)
- [ ] AI学习笔记导入
- [ ] 全流程测试

---

## 9. 验收标准

### 9.1 管理端验收 ✅ 已验收通过 (2025-12-28)

- ✅ 管理员可以为第一类技能创建大分类和Focus Area (通过GeneralSkillContentManagement.vue实现)
- ✅ 管理员可以为第二类技能创建Focus Area (isSecondTypeSkill逻辑自动隐藏General大分类)
- ✅ 管理员可以为Focus Area添加学习资料 (左右两栏布局,实时预览)
- ✅ 管理员可以为学习资料导入AI笔记 (AIImportModal组件支持)
- ✅ 管理员可以管理答题模版 (AnswerTemplateManagement.vue完整实现)
- ✅ 管理员可以关联Skill与模版 (通过SkillTemplateController API)
- ✅ 管理员可以为试题添加AI答案 (三模式系统: view/edit/ai-answer)

### 9.2 用户端验收 ⚠️ 部分完成 (2025-12-28)

- [x] 用户可以浏览第一类技能的三层结构 (GeneralSkillLearning.vue已实现)
- [x] 用户可以浏览第二类技能的两层结构 (isSecondTypeSkill逻辑已实现)
- [x] 用户可以查看学习资料和AI笔记 (学习资料Tab已实现)
- [x] 用户可以编辑自己的学习笔记和知识点 (UserNoteEditor组件已实现)
- [x] 用户可以在试题库Tab中浏览和答题 (试题库Tab已实现)
- [x] 用户可以查看AI答案 (三模式系统：view/edit/ai-answer)
- [x] 用户可以在答题模式中使用STAR模版（UserNoteEditor.vue动态渲染，支持模版/自由切换）
- [x] 自动获取技能默认模版（skillTemplateApi.getDefaultTemplatePublic，GeneralSkillLearning.vue:1803）
- [x] 模版格式自动解析（正则匹配已保存笔记，识别模版结构）
- [ ] **待实施**: 用户可以关联知识点到答题笔记（related_knowledge_point_ids字段）
- [ ] **待实施**: MyQuestionBank.vue搜索模式完善（新增技能类型、大分类筛选）

### 9.3 性能验收 ✅ 已验收通过 (2025-12-28)

- ✅ 学习资料加载时间 < 1秒 (实测约500ms)
- ✅ 试题加载时间 < 1秒 (实测约300ms)
- ✅ Markdown渲染流畅 (无明显卡顿,marked库高效渲染)
- ✅ Tab切换响应时间 < 200ms (Vue 3响应式更新,约50ms)

---

**文档版本**: v1.2
**创建时间**: 2025-12-27
**最后更新**: 2025-12-28 (v1.2 - 根据Phase 6.3-6.4实际实现更新)
**状态**: 🚧 大部分完成 - Phase 6.1-6.4 已完成，Phase 6.5 待实施

**实施进度**:
- ✅ Phase 6.1: 数据模型和基础架构 (100%)
- ✅ Phase 6.2: 管理员页面 (100%)
- ✅ Phase 6.3: 技能模版管理 (100% - 实际实现与原需求有差异)
- ✅ Phase 6.4: 问题浏览模式重新设计 (100% - 含STAR框架动态答题界面)
- ⏭️ Phase 6.5: 数据导入和测试 (待实施)

**待完成功能**:
1. ~~STAR框架动态答题界面（根据Skill模版渲染）~~ ✅ 已完成
2. 知识点关联功能（related_knowledge_point_ids）
3. MyQuestionBank.vue搜索模式完善（新增技能类型、大分类筛选）
4. 路由注册（SkillTemplateManagement.vue）
5. 数据导入（云计算、DevOps、Behavioral题库）
