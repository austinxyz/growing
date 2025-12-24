# Phase 4 详细需求 - 算法与数据结构学习模块

> **需求来源**: 用户需求 (2025-12-22)
> **目标用户**: 准备算法面试的工程师
> **核心价值**: 提供系统化的算法与数据结构学习路径，支持多种skill的自定义学习阶段
> **状态**: ✅ Phase 4 已完成 (2025-12-24)
> **实施版本**: v1.0 - 基于实际实现

---

## 1. 背景与目标

### 1.1 用户痛点

**现状**:
- 用户学习算法与数据结构时，资源分散在多个平台：
  - LeetCode - 刷题平台
  - labuladong的算法笔记 - 系统化学习
  - Hello Interview - 题解参考
  - 极客时间王铮的算法课 - 视频课程
- 缺乏统一的学习入口和进度跟踪
- 难以建立知识体系，容易遗忘关键思路
- 不同技能的学习路径差异大（如：算法强调基础→实现→练习）

**目标**:
- 在Growing App中整合所有学习资源
- 支持每个Skill定义自己的学习阶段（算法: 基础原理→实现代码→实战题目）
- 提供统一的内容管理框架，适配不同学习路径
- 提供题目总结和笔记功能
- 维护常用算法模板库

### 1.2 成功标准

**功能完整性**:
- ✅ 支持按4大分类浏览学习内容（数据结构、搜索、动规、其他）
- ✅ 支持自定义学习阶段（基础原理 → 实现代码 → 实战题目）
- ✅ 管理员可以维护学习内容（文章、视频、代码、案例、题目）
- ✅ 用户可以查看题目详情（LeetCode链接、题目描述、题解链接）
- ✅ 用户可以添加个人笔记（通用笔记 + 核心思路）
- ✅ 提供算法模版/快速参考功能
- ✅ 提供学习总结页面（272道编程题的核心思路汇总）

**用户体验**:
- ✅ 清晰的知识结构（大分类 → Focus Area → 学习阶段 → 学习内容）
- ✅ 单页整合展示（所有学习阶段的内容在一个页面显示）
- ✅ 一键跳转到LeetCode、labuladong等外部资源
- ✅ 支持Markdown格式的内容展示
- ✅ 紧凑的学习总结界面（双列双行布局，快速浏览题目和核心思路）

---

## 2. 核心概念

### 2.1 架构设计思路

**设计原则**:
1. **灵活性**: 不同Skill可以定义不同的学习阶段
2. **统一性**: 所有学习内容（文章、视频、代码、案例、题目）统一管理
3. **扩展性**: 通过JSON字段存储特定类型的扩展数据
4. **复用性**: 复用现有的Skill、Focus Area、Question表结构

### 2.2 算法与数据结构的学习路径

**层级关系**:
```
Skill: 算法与数据结构
  ↓
大分类（4个固定分类）
  ├─ 数据结构
  ├─ 搜索
  ├─ 动规
  └─ 其他
      ↓
Focus Area（如"数组"、"链表"、"二叉树"）- 52个
      ↓
学习阶段（Skill级别定义）
  ├─ 阶段1: 基础原理 (theory)
  ├─ 阶段2: 实现代码 (implementation)
  └─ 阶段3: 实战题目 (practice)
      ↓
学习内容（文章、视频、代码示例）+ 编程题目
```

**示例**:
- 大分类: "数据结构"
- Focus Area: "数组"
- 学习阶段1 (基础原理):
  - [数组（顺序存储）基本原理](https://labuladong.online/algo/data-structure-basic/array-basic/)
  - [动态数组代码实现](https://labuladong.online/algo/data-structure-basic/array-implement/)
- 学习阶段2 (实现代码):
  - [双指针技巧总结](https://labuladong.online/algo/essential-technique/array-two-pointers-summary/)
  - [滑动窗口算法模板](https://labuladong.online/algo/essential-technique/sliding-window-framework/)
- 学习阶段3 (实战题目):
  - [26] 删除有序数组中的重复项
  - [27] 移除元素
  - [283] 移动零

### 2.3 与现有结构的映射

**复用现有概念**:
- **Skill** = "算法与数据结构"
- **Focus Area** = 小分类（如"数组"、"链表"）- 52个
- **Question** = LeetCode题目（增加了programming_question_details关联表）

**新增概念**:
- **大分类 (Major Category)** - 4个主分类（数据结构、搜索、动规、其他）
- **学习阶段 (Learning Stage)** - Skill级别定义，支持不同技能自定义阶段
- **学习内容 (Learning Content)** - 统一管理文章、视频、代码、案例、模版等（235条）
- **编程题详情 (Programming Question Details)** - 扩展编程题的LeetCode链接、算法标签等（272条）
- **算法模版** - 作为learning_contents的特殊类型，focus_area_id=NULL（10个模版）

---

## 3. 已实现功能清单

### 3.1 管理端功能

#### F1: 学习内容管理 (Learning Contents)

**页面**: `/admin/algorithm-content`

**布局**: 两栏布局
- 左侧：大分类Tab + Focus Area列表
- 右侧：学习阶段Tab（基础原理、实现代码、实战题目）+ 试题库Tab

**功能**:
- ✅ 按大分类浏览Focus Area
- ✅ 按Focus Area查看学习内容（分3个学习阶段Tab）
- ✅ 为每个学习阶段添加学习内容（文章、视频、代码示例）
  - 内容类型: article / video / code_example
  - 必填字段: 标题、URL、作者
  - 可选字段: 描述、扩展数据(JSON)
- ✅ 编辑/删除学习内容
- ✅ 拖拽排序学习内容

#### F2: 题目管理 (Questions + Programming Question Details)

**页面**: `/admin/algorithm-content` - 试题库Tab

**功能**:
- ✅ 查看当前Focus Area下的所有题目
- ✅ 创建新题目（包含通用字段 + 编程题扩展字段）
  - **通用字段** (questions表):
    - 题目标题（必填）
    - 问题描述（Markdown，可选）
    - 难度: EASY/MEDIUM/HARD（必填）
    - 答案要求（可选）
    - Red Flags（可选）
  - **编程题扩展字段** (programming_question_details表):
    - LeetCode题号（整数，用于排序和过滤）
    - LeetCode链接（必填）
    - labuladong题解链接（可选）
    - Hello Interview题解链接（可选）
- ✅ 编辑/删除题目（级联删除programming_question_details和user_question_notes）
- ✅ 题目自动关联到当前Focus Area

#### F3: 算法模版管理

**页面**: `/admin/algorithm-templates`

**功能**:
- ✅ 创建算法模版（content_type='template', focus_area_id=NULL）
- ✅ 填写模版代码（支持Markdown）
- ✅ 编辑/删除算法模版
- ✅ 当前已有10个算法模版

**数据**:
- 235条learning_contents（225篇文章 + 10个模版）

### 3.2 用户端功能

#### F4: 学习路径浏览

**页面**: `/algorithm-learning`

**布局**: 三栏布局
- 顶部：4个大分类Tab（数据结构、搜索、动规、其他）
- 左侧：Focus Area列表
- 右侧：**单页整合展示**所有学习阶段的内容

**功能**:
- ✅ 按大分类切换Focus Area列表
- ✅ 选择Focus Area后，右侧显示该Focus Area的所有内容
- ✅ 内容按学习阶段分组显示（基础原理、实现代码、实战题目）
- ✅ 点击文章/视频链接，新窗口打开外部资源
- ✅ 点击题目，弹出QuestionDetailModal（显示题目详情、LeetCode链接、用户笔记）
- ✅ Markdown渲染支持

**实现差异**:
- **设计**: 右侧分3个Tab，切换查看不同学习阶段
- **实际**: 右侧单页显示，所有学习阶段内容垂直排列，一次性加载

#### F5: 算法模版查阅

**页面**: `/algorithm-templates`

**布局**: 左右分栏
- 左侧：算法模版列表（支持搜索）
- 右侧：模版详情（Markdown渲染）

**功能**:
- ✅ 浏览所有算法模版
- ✅ 搜索模版名称
- ✅ 查看模版代码（语法高亮）
- ✅ Markdown正确渲染
- ✅ 当前已有10个模版

#### F6: 个人笔记管理

**页面**: 题目详情Modal（在/algorithm-learning中点击题目触发）

**功能**:
- ✅ 为题目添加笔记（复用Phase 3的user_question_notes表）
- ✅ 支持两类笔记字段：
  - **核心思路 (core_strategy)** - 编程题专用，用于学习总结
  - **个人笔记 (note_content)** - 补充说明、心得体会
- ✅ Markdown格式支持
- ✅ UPSERT逻辑（一个用户对一道题只能有一条笔记）
- ✅ 查看/编辑/删除笔记
- ✅ 当前已有272条用户笔记

#### F7: 学习总结（新增功能）

**页面**: `/learning-review`

**布局**: 紧凑的双列双行卡片布局
- 顶部：过滤器（所有分类、所有Focus Area、LeetCode题号、重置按钮）
- 主体：题目卡片列表（每个卡片2行）
  - 第1行：难度图标 + 题目标题（可点击） + Focus Area + 分类标签
  - 第2行：核心思路（如果有）或"暂无核心思路"

**功能**:
- ✅ 显示所有272道编程题的核心思路总结
- ✅ 按大分类过滤（数据结构、搜索、动规、其他）
- ✅ 按Focus Area过滤
- ✅ 按LeetCode题号过滤
- ✅ 颜色编码区分大分类（蓝色=数据结构、绿色=算法、橙色=设计、紫色=数学）
- ✅ 点击题目标题，弹出QuestionDetailModal
- ✅ 极致紧凑设计（text-xs字体，最小padding，双列布局）
- ✅ 性能优化：后端批量查询（0.6秒加载272题）

**设计差异**:
- **设计**: 未包含此功能
- **实际**: 新增独立页面，提供快速浏览所有编程题核心思路的能力

---

## 4. 数据需求

### 4.1 数据实体概览

#### 实体1: learning_stages (学习阶段定义)

**用途**: 为每个Skill定义自定义的学习阶段序列

**字段**:
- skill_id - 所属技能
- stage_name - 阶段名称（如"基础原理"）
- stage_type - 阶段类型标识（如"theory"）
- description - 阶段说明
- sort_order - 排序

**数据** (算法与数据结构):
- 阶段1: 基础原理 (theory)
- 阶段2: 实现代码 (implementation)
- 阶段3: 实战题目 (practice)

#### 实体2: learning_contents (学习内容统一表)

**用途**: 统一管理所有类型的学习内容

**字段**:
- focus_area_id - 所属Focus Area（算法模版时为NULL）
- stage_id - 所属学习阶段
- content_type - 内容类型（article/video/code_example/template/case_study）
- title - 标题
- description - 描述
- url - 外部资源链接
- author - 作者
- content_data - 扩展数据（JSON格式）
- sort_order - 排序
- visibility - 可见性（public/private）
- created_by - 创建者ID

**数据统计**:
- 总计: 235条
- 文章: ~225条
- 模版: 10条

#### 实体3: questions (题目表 - 通用)

**用途**: 存储所有类型的题目

**字段**:
- focus_area_id - 关联Focus Area
- title - 题目标题
- question_description - 问题描述（Markdown）
- difficulty - 难度（EASY/MEDIUM/HARD）
- question_type - 题型（behavioral/technical/design/programming）
- answer_requirement - 答案要求
- target_position - 目标岗位
- target_level - 目标级别
- red_flags - Red Flags（JSON数组字符串）
- is_official - 是否官方题目
- created_by_user_id - 创建者ID
- display_order - 显示顺序

**数据统计**:
- 总计: 379题
- behavioral: 107题
- programming: 272题

#### 实体4: programming_question_details (编程题专属字段)

**用途**: 存储编程题的专属信息

**字段**:
- question_id - 关联的题目ID（一对一关系）
- leetcode_number - LeetCode题号（整数，用于排序）
- leetcode_url - LeetCode题目链接
- labuladong_url - labuladong题解链接
- hellointerview_url - Hello Interview题解链接

**数据统计**:
- 总计: 272条（对应272道编程题）

#### 实体5: user_question_notes (用户笔记)

**用途**: 用户为题目添加的个人笔记

**字段**:
- question_id - 关联的题目ID
- user_id - 创建者ID
- note_content - 笔记内容（Markdown）
- core_strategy - 核心思路（编程题专用，Markdown）
- created_at - 创建时间
- updated_at - 更新时间

**字段使用说明**:
- **note_content** - 补充说明、心得体会、注意事项（所有题目类型）
- **core_strategy** - 解题核心思路（仅编程题使用，用于学习总结）

**数据统计**:
- 总计: 272条用户笔记

#### 实体6: major_categories (大分类)

**用途**: 组织Focus Area的顶层分类

**数据**:
- 数据结构
- 搜索
- 动规
- 其他

#### 实体7: focus_area_categories (Focus Area与大分类关联表)

**用途**: 多对多关系，一个Focus Area可属于多个大分类

**数据统计**:
- 18条关联记录（52个Focus Area分布在4个大分类中）

### 4.2 数据关联规则

**层级关系**:
```
Skill (算法与数据结构)
  → Learning Stages (3个阶段)
    → Learning Contents (235条：225篇文章 + 10个模版)
      ← Focus Areas (52个主题)
        ← Major Categories (4个大分类)

Questions (379题：107 behavioral + 272 programming)
  → Focus Areas (关联到具体主题)
  → Programming Question Details (272条，1:1关系)
  → User Question Notes (272条，1:N关系)
```

**关键规则**:
- 每个Skill可以有多个Learning Stages（当前算法有3个）
- Learning Contents可以关联Focus Area（文章、代码）或不关联（算法模版）
- Questions同时关联Focus Area（通过focus_area_id）
- Programming Question Details与Question是严格一对一关系
- User Question Notes与Question是一对多关系（每个用户对每道题只能有一条笔记）

---

## 5. 非功能需求

### 5.1 性能要求

**已达成**:
- ✅ 学习总结页面加载时间: 0.6秒（后端批量查询272题）
- ✅ 学习阶段内容加载: <1秒
- ✅ Markdown渲染流畅

**性能优化措施**:
- 后端批量查询（questionRepository.findAllProgrammingQuestionsBySkillId）
- 一次性加载所有用户笔记，建立Map
- 批量加载编程题详情，建立Map
- 批量查询Focus Area与大分类关联关系
- 前端扁平化数据结构，避免嵌套循环

### 5.2 安全要求

- ✅ 管理端所有API需要管理员权限
- ✅ 用户只能管理自己的笔记
- ✅ Markdown内容XSS过滤（使用marked库）

### 5.3 兼容性要求

- ✅ 不影响现有的试题库功能（Phase 3）
- ✅ Focus Area表的扩展字段对现有数据兼容
- ✅ 支持主流浏览器（Chrome、Safari、Firefox）

### 5.4 可维护性要求

- ✅ 大分类固定为4个（数据结构、搜索、动规、其他）
- ✅ 学习阶段支持Skill级别自定义（当前3个，未来可扩展）
- ✅ 扩展信息采用JSON存储，灵活扩展
- ✅ 编程题专属字段独立表存储，避免NULL字段浪费

---

## 6. UI/UX需求

### 6.1 管理员页面布局

**路径**: `/admin/algorithm-content`

**布局**: 两栏布局（25% + 75%）

```
┌────────────────────────────────────────────────────────────┐
│  算法与数据结构 - 内容管理                                  │
├───────────────┬────────────────────────────────────────────┤
│ 左侧面板(25%) │ 右侧面板(75%)                               │
│               │                                            │
│ [数据结构]    │ ┌──────────────────────────────────────┐ │
│ [搜索]        │ │ 数组 - 内容管理                       │ │
│ [动规]        │ ├──────────────────────────────────────┤ │
│ [其他]        │ │ Tab: 基础原理 实现代码 实战题目 试题库│ │
│               │ ├──────────────────────────────────────┤ │
│ Focus Area列表│ │                                      │ │
│ ☑ 数组        │ │ [+ 添加内容] 或 [+ 新建题目]         │ │
│ □ 链表        │ │                                      │ │
│ □ 栈和队列    │ │ 内容/题目列表                        │ │
│ ...           │ │                                      │ │
└───────────────┴──┴──────────────────────────────────────┴─┘
```

**Tab说明**:
- Tab 1-3: 学习阶段Tab（基础原理、实现代码、实战题目）- 管理learning_contents
- Tab 4: 试题库Tab - 管理questions + programming_question_details

### 6.2 用户页面布局

**页面1**: `/algorithm-learning` (学习路径)

**布局**: 三栏布局
- 顶部：4个大分类Tab
- 左侧：Focus Area列表
- 右侧：**单页整合显示**所有学习阶段内容（不分Tab）

**页面2**: `/algorithm-templates` (算法模版)

**布局**: 左右分栏
- 左侧：模版列表 + 搜索框
- 右侧：模版详情（Markdown渲染）

**页面3**: `/learning-review` (学习总结)

**布局**: 紧凑双列布局
- 顶部：过滤器行（大分类、Focus Area、LeetCode题号、重置）
- 主体：双列题目卡片（每卡片2行）

### 6.3 视觉需求

**颜色规范**:
- 大分类Tab: 蓝色高亮（激活状态）
- 学习阶段Tab: 蓝色高亮
- 试题库Tab: 蓝色高亮
- 难度标签: EASY绿点 / MEDIUM黄点 / HARD红点
- 分类标签: 数据结构=蓝 / 算法=绿 / 设计=橙 / 数学=紫

**图标规范**:
- 📄 文章
- 🎥 视频
- 💻 代码示例
- 📚 算法模版
- 🎯 题目

### 6.4 交互需求

**Tab切换**:
- Tab切换时显示加载状态
- 切换不同Tab时加载对应数据
- Tab状态独立，互不干扰

**列表操作**:
- 支持拖拽排序（保存后更新sort_order）
- 外部链接在新窗口打开
- 题目列表支持按难度、标签筛选

**表单交互**:
- Markdown编辑器提供实时预览
- 题目创建/编辑支持Markdown

### 6.5 响应式需求

- 支持桌面端（最小宽度1280px）
- 两栏布局在小屏幕(<1024px)自适应为上下布局
- Modal在移动端全屏显示

---

## 7. 验收标准

### 7.1 管理端验收

- ✅ 管理员可以为"算法与数据结构"的Focus Area添加学习内容
- ✅ 管理员可以为"数组"的"基础原理"阶段添加文章
- ✅ 管理员可以为"实现代码"阶段添加代码示例
- ✅ 管理员可以在"试题库"Tab创建编程题（包含通用字段 + LeetCode链接）
- ✅ 管理员可以创建算法模版（10个已完成）
- ✅ 所有Markdown内容正确渲染

### 7.2 用户端验收

- ✅ 用户可以浏览4个大分类的学习内容
- ✅ 用户可以看到"数组"Focus Area的所有学习内容（单页整合显示）
- ✅ 用户可以点击文章链接跳转到labuladong网站
- ✅ 用户可以查看题目详情（题目描述、LeetCode链接、题解链接）
- ✅ 用户可以为题目添加个人笔记（核心思路 + 个人笔记）
- ✅ 用户可以查阅算法模版（10个模版）
- ✅ 用户可以在学习总结页面快速浏览所有272道编程题的核心思路

### 7.3 性能验收

- ✅ 学习总结页面加载时间: 0.6秒（272题）
- ✅ 学习内容列表加载时间 < 1秒
- ✅ Markdown渲染流畅（无明显卡顿）
- ✅ Tab切换响应时间 < 200ms

---

## 8. 实现与设计的主要差异

### 8.1 用户端学习路径页面

**设计**: 右侧分3个Tab（基础原理、实现代码、实战题目），切换查看不同阶段
**实际**: 右侧单页整合显示，所有学习阶段内容垂直排列，一次性加载

**理由**: 单页展示更符合学习流程，用户可以连续阅读所有阶段内容，不需要反复切换Tab

### 8.2 学习总结功能（新增）

**设计**: 未包含此功能
**实际**: 新增`/learning-review`页面，提供272道编程题的核心思路汇总

**功能**:
- 双列双行紧凑布局
- 按大分类、Focus Area、LeetCode题号过滤
- 颜色编码区分大分类
- 点击题目弹出详情Modal
- 后端批量查询优化（0.6秒加载）

**理由**: 用户强烈需要快速复习所有题目核心思路的能力，避免在学习路径页面中逐个点击查看

### 8.3 Skill名称

**设计**: "编程与数据结构"
**实际**: "算法与数据结构"

**理由**: 更贴合实际内容，"算法"比"编程"更准确

### 8.4 路由路径

**设计**: `/admin/programming-ds`
**实际**: `/admin/algorithm-content` + `/admin/algorithm-templates` + `/algorithm-learning` + `/algorithm-templates` + `/learning-review`

**理由**: 更清晰的功能划分，算法模版独立管理页面

---

## 9. 数据统计

### 9.1 当前数据量

- 大分类: 4个
- Focus Areas: 52个
- 学习阶段: 3个（算法与数据结构）
- 学习内容: 235条（225篇文章 + 10个模版）
- 题目总数: 379题
  - Behavioral题目: 107题
  - 编程题: 272题
- 编程题详情: 272条
- 用户笔记: 272条
- Focus Area与大分类关联: 18条

### 9.2 数据来源

- 学习内容文章: 来自labuladong算法笔记
- 编程题: 来自LeetCode
- 算法模版: 手工创建

---

**文档版本**: v1.0 (基于实际实现)
**创建时间**: 2025-12-24
**状态**: ✅ Phase 4 已完成
**重要更新**:
- 基于实际实现重写整个需求文档
- 新增学习总结功能章节
- 更新用户端学习路径页面布局（单页整合显示）
- 统一Skill名称为"算法与数据结构"
- 更新数据统计（反映实际数据量）
