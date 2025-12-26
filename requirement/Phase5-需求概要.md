# Phase 5: 系统设计学习模块 - 需求概要

> **设计参考**: https://www.hellointerview.com/
> **创建时间**: 2025-12-25
> **版本**: v1.0 概要版

---

## 一、模块概述

Phase 5 新增**系统设计学习模块**，帮助用户学习大规模分布式系统设计能力。模块分为两个子模块：

1. **基础知识模块**：学习系统设计的核心概念、关键技术、设计模式
2. **典型案例模块**：通过完整案例实战练习系统设计

---

## 二、整体架构

### 2.1 Skill定义

- **Skill名称**: 系统设计 (System Design)
- **技能图标**: 🏗️
- **描述**: 掌握大规模分布式系统设计的能力

### 2.2 大分类 (Major Categories)

建议设置4个Major Category：

1. **核心概念 (Core Concepts)** 📚
   - 描述：理论基础和通用概念
   - 目标：掌握分布式系统的基本理论

2. **关键技术 (Key Technologies)** ⚙️
   - 描述：常用中间件和基础设施
   - 目标：了解常见技术组件的特性和应用场景

3. **设计模式 (Design Patterns)** 🎨
   - 描述：常见问题的解决方案模式
   - 目标：掌握典型场景的设计模式

4. **典型案例 (Typical Cases)** 🎯
   - 描述：完整的系统设计案例实战
   - 目标：通过实际案例综合运用知识
   - **注意**：此分类不使用Focus Area结构，案例存储在独立的表中

<Austin> 典型案例不是大分类，不需要在数据库major_categories中体现。

### 2.3 Focus Area设计

**前3个大分类**（核心概念、关键技术、设计模式）下的Focus Area由用户提供建议。

**典型案例大分类**不设置Focus Area，案例存储在独立的 `system_design_cases` 表中。

---

## 三、数据模型设计

### 3.1 基础知识模块

**复用现有表结构**：
- `skills` - 系统设计Skill
- `major_categories` - 4个大分类
- `focus_areas` - 基础知识的Focus Areas（等待用户提供）
- `focus_area_major_categories` - Focus Area与Major Category的多对多关系
- `learning_contents` - 学习资料（文章、视频、文档）

**去掉学习阶段**：
- ❌ 不使用 `learning_stages` 表
- ✅ Focus Area下直接关联学习资料

### 3.2 典型案例模块

**新增独立表**：`system_design_cases`

```sql
CREATE TABLE system_design_cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id BIGINT NOT NULL,  -- 关联到系统设计skill

    -- 基本信息
    title VARCHAR(500) NOT NULL COMMENT '案例标题（如：设计Twitter）',
    case_description TEXT COMMENT '案例描述',
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,

    -- 学习资源链接
    problem_statement_url VARCHAR(500) COMMENT '问题描述文章链接',
    video_solution_url VARCHAR(500) COMMENT '视频讲解链接',
    article_solution_url VARCHAR(500) COMMENT '文章讲解链接（如HelloInterview）',

    <Austin> 问题描述已经case_description体现，不需要problem_statement_url，但是视频和文章讲解可能会有多个。

    -- 元数据
    difficulty_rating INT COMMENT '难度评分 1-10',
    estimated_time_minutes INT COMMENT '预计完成时间（分钟）',
    company_tags TEXT COMMENT '面试公司标签 (JSON数组)',

    <Austin> 预计完成时间暂不需要

    -- 关联基础知识
    related_focus_areas TEXT COMMENT '关联的Focus Area IDs (JSON数组)',

    -- 参考答案（8个步骤）
    template_requirements TEXT COMMENT '需求分析模板',
    template_non_functional_requirements TEXT COMMENT '非功能需求模板',
    template_core_entities TEXT COMMENT '核心实体模板',
    template_api_design TEXT COMMENT 'API设计模板',
    template_data_flow TEXT COMMENT '数据流模板',
    template_high_level_design TEXT COMMENT '高层设计模板',
    template_deep_dive TEXT COMMENT '深入探讨模板',
    template_trade_offs TEXT COMMENT '权衡考虑模板',
    <Austin> 参考答案部分有必要拆成一个专门的表，可能有多个参考答案。参考答案可能也会包含图。

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
COMMENT='系统设计案例表（独立于questions表）';
```

### 3.3 用户答题记录

**新增表**：`user_case_notes`

```sql
CREATE TABLE user_case_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    -- 用户的答题内容（8个步骤）
    user_requirements TEXT COMMENT '用户的需求分析',
    user_non_functional_requirements TEXT COMMENT '用户的非功能需求',
    user_core_entities TEXT COMMENT '用户的核心实体',
    user_api_design TEXT COMMENT '用户的API设计',
    user_data_flow TEXT COMMENT '用户的数据流',
    user_high_level_design TEXT COMMENT '用户的高层设计',
    user_deep_dive TEXT COMMENT '用户的深入探讨',
    user_trade_offs TEXT COMMENT '用户的权衡考虑',

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

## 四、核心功能

<Austin> 核心功能和侧边栏结合起来
管理员功能
1. 设置-内容管理-系统设计基础，类似设置-内容管理-算法与数据结构页面布局，左边显示大分类-focus area，选focus area后，不需要有多tab，只需要可以新建/修改学习资料
2. 设置-内容管理-典型案例，两栏结构，左边显示案例list，右边显示案例详情。分上下，上面详情，下面参考答案，参考答案分为6个tab，将需求和非功能需求合为一个tab，deep dive和tradeoff合为一个，其余对应参考答案的字段。可以允许多个参考答案。可以增删改案例，也可以编辑案例信息。
用户功能
1. 学习-系统设计-基础知识，类似学习-算法与数据结构-学习路径页面布局，左边显示大分类-focus area，选focus area后，不需要有多tab，只需要可以看到学习资料列表，查看学习资料
2. 学习-系统设计-典型案例，两栏结构，左边显示案例list，右边显示案例详情。分上下，上面详情，下面参考答案，参考答案分为6个tab，将需求和非功能需求合为一个tab，deep dive和tradeoff合为一个，其余对应参考答案的字段。可以允许多个参考答案。只是显示，但是用户可以选择编辑案例笔记。当编辑笔记时，变成左右两栏，左边案例信息和参考答案，右边是用户答案，也分类6个tab。

### 4.1 基础知识学习模块

#### 管理员功能
- 创建/编辑 Focus Area（核心概念、关键技术、设计模式）
- 添加学习资料到 Focus Area（文章、视频、文档）

#### 用户功能
- 浏览 Focus Area（按 Major Category 分类）
- 查看学习资料
- 对知识点做笔记和总结

**学习路径示例**：
```
用户选择 "核心概念" Category
→ 选择 "分布式理论" Focus Area
→ 查看学习资料列表
→ 阅读文章、观看视频
→ 做笔记总结
```

### 4.2 典型案例实战模块

#### 管理员功能
- 创建/编辑系统设计案例
- 填写案例的8步参考答案
- 上传学习资源链接（问题描述、视频讲解、文章讲解）
- 设置案例元数据（难度、预计时间、公司标签）
- 关联案例到基础知识点（related_focus_areas）

**案例创建示例**：
```
案例: 设计Twitter
难度: HARD (7/10)
预计时间: 45分钟
公司标签: ["Meta", "Twitter", "ByteDance"]
关联知识点: [API设计, 缓存系统, 消息队列, 数据分片]

参考答案包含8个步骤：
1. 需求分析
2. 非功能需求
3. 核心实体
4. API设计
5. 数据流
6. 高层设计
7. 深入探讨
8. 权衡考虑
```

#### 用户功能
- 浏览系统设计案例列表
- 查看案例详情（问题描述、参考答案、学习资源）
- 填写自己的答案（8个步骤）
- 上传架构图
- 保存要点总结
- 对比自己的答案和参考答案
- 查看自己的答题历史

**答题流程示例**：
```
用户选择 "设计Twitter" 案例
→ 查看问题描述和学习资源
→ 点击"开始答题"
→ 填写8个步骤（支持Markdown）
→ 绘制架构图并上传
→ 填写要点总结
→ 保存答案
→ 对比参考答案，查看差距
```

---

## 五、与算法模块的对比

| 功能 | 算法模块 | 系统设计模块 |
|-----|---------|------------|
| **数据结构** | questions表 + programming_question_details | system_design_cases表（独立） |
| **学习阶段** | 有（概念理解、刷题实战、总结复盘） | 无（Focus Area下直接关联学习资料） |
| **典型案例** | 无 | 有（独立的cases表） |
| **笔记表** | user_question_notes | user_case_notes |
| **核心字段** | leetcodeUrl, tags, complexity | 8个template_*字段, related_focus_areas |
| **用户答题** | coreStrategy, noteContent | 8个user_*字段 + architectureDiagramUrl + keyTakeaways |
| **学习资源** | LeetCode链接 | 视频讲解、文章讲解、问题描述 |

---

## 六、权限设计

### 管理员 (Admin)
- 管理所有 Focus Areas 和学习资料
- 创建/编辑系统设计案例（含8步参考答案）
- 设置案例元数据和关联知识点

### 普通用户 (User)
- 浏览学习资料
- 对基础知识做笔记
- 查看系统设计案例（含参考答案）
- 填写自己的答案（8个步骤）
- 上传架构图
- 保存要点总结
- 对比自己的答案和参考答案
- 查看自己的答题历史

---

## 七、实现阶段划分

### Phase 5.1: 基础知识模块（优先级：高）
1. 创建系统设计 Skill
2. 创建 4 个 Major Categories
3. 等待用户提供 Focus Area 建议，创建 Focus Areas
4. 添加学习资料（复用现有 LearningContent 功能）

**预期产出**：用户可以浏览系统设计基础知识，按 Major Category 和 Focus Area 学习

### Phase 5.2: 典型案例基础功能（优先级：高）
1. 创建 `system_design_cases` 表
2. 创建 `user_case_notes` 表
3. 实现后端 Service 层（CaseService, UserCaseNoteService）
4. 实现管理员端创建/编辑案例功能
5. 实现案例列表展示

**预期产出**：管理员可以创建系统设计案例（含8步参考答案和学习资源）

### Phase 5.3: 用户答题功能（优先级：中）
1. 实现用户答题编辑器（8个步骤）
2. 实现架构图上传功能
3. 实现答案保存和更新
4. 实现答题历史查看

**预期产出**：用户可以对案例进行答题，保存自己的解答

### Phase 5.4: 高级功能（优先级：低）
1. 答案对比功能（左侧参考答案，右侧用户答案）
2. 关联知识点跳转
3. 学习进度追踪
4. 案例推荐（根据用户掌握的基础知识推荐案例）

---

## 八、待确认事项

### ✅ 已确认
- 典型案例使用独立的 `system_design_cases` 表（方案B）
- 基础知识不使用学习阶段（Focus Area 下直接关联学习资料）
- 大分类设计（4个 Major Category）

### ❓ 待提供
- 前3个大分类（核心概念、关键技术、设计模式）下的 Focus Area 清单

### ❓ 待决定
- 是否需要为系统设计案例添加其他元数据（如预估数据量、QPS等）？
- 架构图上传使用什么存储方案（本地存储 vs 云存储）？
- 8个答题步骤是否完整？是否需要增减？

---

## 九、参考资料

- **HelloInterview**: https://www.hellointerview.com/
- **System Design Primer**: https://github.com/donnemartin/system-design-primer
- **Grokking System Design**: https://www.designgurus.io/
- **ByteByteGo**: https://bytebytego.com/

---

**下一步**：
1. 等待用户确认需求概要
2. 等待用户提供 Focus Area 建议
3. 确认后开始 Phase 5.1 实现
