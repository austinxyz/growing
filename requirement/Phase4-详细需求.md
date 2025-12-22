# Phase 4 详细需求 - 编程与数据结构学习模块

> **需求来源**: 用户需求 (2025-12-22)
> **目标用户**: 准备算法面试的工程师
> **核心价值**: 提供系统化的编程与数据结构学习路径，支持多种skill的自定义学习阶段
> **状态**: v2.0 - 架构重构 (待Review)

---

## 1. 背景与目标

### 1.1 用户痛点

**现状**:
- 用户学习编程与数据结构时，资源分散在多个平台：
  - LeetCode - 刷题平台
  - labuladong的算法笔记 - 系统化学习
  - Hello Interview - 题解参考
  - 极客时间王铮的算法课 - 视频课程
- 缺乏统一的学习入口和进度跟踪
- 难以建立知识体系，容易遗忘关键思路
- **不同技能的学习路径差异大**（如：算法强调基础→实现→练习，系统设计强调案例→模版→权衡）

**目标**:
- 在Growing App中整合所有学习资源
- **支持每个Skill定义自己的学习阶段**（编程 vs 系统设计 vs 运维）
- 提供统一的内容管理框架，适配不同学习路径
- 提供题目总结和笔记功能
- 维护常用算法模板库

### 1.2 成功标准

**功能完整性**:
- ✅ 支持按4大分类浏览学习内容（数据结构、搜索、动规、其他）
- ✅ 支持自定义学习阶段（如：基础原理 → 实现代码 → 实战题目）
- ✅ 管理员可以维护学习内容（文章、视频、代码、案例、题目）
- ✅ 用户可以查看题目总结（LeetCode链接、关键思路等）
- ✅ 用户可以添加个人笔记
- ✅ 提供算法模版/快速参考功能

**用户体验**:
- ✅ 清晰的知识结构（大分类 → Focus Area → 学习阶段 → 学习内容）
- ✅ 三段式学习体验（基础原理 → 实现代码 → 实战题目）
- ✅ 一键跳转到LeetCode、labuladong等外部资源
- ✅ 支持Markdown格式的内容展示

---

## 2. 核心概念

### 2.1 架构设计思路

**设计原则**:
1. **灵活性**: 不同Skill可以定义不同的学习阶段
2. **统一性**: 所有学习内容（文章、视频、代码、案例、题目）统一管理
3. **扩展性**: 通过JSON字段存储特定类型的扩展数据
4. **复用性**: 复用现有的Skill、Focus Area、Question表结构

### 2.2 编程与数据结构的学习路径

**层级关系**:
```
Skill: 编程与数据结构
  ↓
大分类（4个固定分类）
  ├─ 数据结构
  ├─ 搜索
  ├─ 动规
  └─ 其他
      ↓
Focus Area（如"数组"、"链表"、"二叉树"）
      ↓
学习阶段（Skill级别定义）
  ├─ 阶段1: 基础原理 (theory)
  ├─ 阶段2: 实现代码 (implementation)
  └─ 阶段3: 实战题目 (practice)
      ↓
学习内容（文章、视频、代码示例、题目）
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

### 2.3 系统设计的学习路径（未来扩展）

**学习阶段定义**（示例）:
```
Skill: 系统设计
  ↓
Focus Area: 分布式存储
  ↓
学习阶段（系统设计特有）
  ├─ 阶段1: 案例分析 (case_study)
  ├─ 阶段2: 架构模版 (architecture_template)
  ├─ 阶段3: 权衡讨论 (tradeoff)
  └─ 阶段4: 延伸阅读 (extended_reading)
```

### 2.4 与现有结构的映射

**复用现有概念**:
- **Skill** = "编程与数据结构" / "系统设计" / "Infrastructure Engineering"
- **Focus Area** = 小分类（如"数组"、"链表"、"分布式存储"）
- **Question** = LeetCode题目（增加stage_id字段关联到"实战题目"阶段）

**新增概念**:
- **大分类 (Major Category)** - 4个主分类（数据结构、搜索、动规、其他）
- **学习阶段 (Learning Stage)** - Skill级别定义，支持不同技能自定义阶段
- **学习内容 (Learning Content)** - 统一管理文章、视频、代码、案例、题目等
- **算法模版** - 不作为大分类，而是作为"快速参考"独立板块

---

## 3. 用户故事

### 3.1 管理员视角

#### 故事1: 配置Skill的学习阶段

**角色**: 管理员
**目标**: 为"编程与数据结构"Skill定义学习阶段

**场景**:
1. 管理员登录，进入"设置 - 内容管理 - 编程与数据结构"
2. 点击"配置学习阶段"按钮
3. 看到当前已定义的3个阶段：
   - 基础原理 (theory)
   - 实现代码 (implementation)
   - 实战题目 (practice)
4. 可以添加、编辑、删除、排序学习阶段
5. 每个阶段包含：名称、类型标识、描述、排序

**验收标准**:
- 支持CRUD学习阶段
- 阶段类型标识用于前端识别渲染方式
- 阶段支持拖拽排序
- 删除阶段时检查是否有内容关联

#### 故事2: 为Focus Area添加分阶段学习内容

**角色**: 管理员
**目标**: 为"数组"Focus Area添加三个阶段的学习内容

**场景**:
1. 在左侧选中"数组"Focus Area
2. 右侧显示三个阶段的Tab：
   - 基础原理
   - 实现代码
   - 实战题目
3. 点击"基础原理"Tab
4. 点击"添加内容"按钮
5. 选择内容类型：
   - 文章 (article)
   - 视频 (video)
   - 代码示例 (code_example)
6. 填写内容信息：
   - 标题: "数组（顺序存储）基本原理"
   - URL: https://labuladong.online/algo/data-structure-basic/array-basic/
   - 作者: labuladong
   - 描述: 介绍数组的基本特性、时间复杂度等
7. 保存后，内容出现在"基础原理"阶段列表中
8. 切换到"实现代码"Tab，重复上述步骤添加代码示例
9. 切换到"实战题目"Tab，从题库中选择题目关联

**验收标准**:
- 支持按学习阶段组织内容
- 每个阶段的内容类型可以不同
- 支持拖拽排序内容
- 题目关联时自动设置stage_id

#### 故事3: 创建算法模版内容

**角色**: 管理员
**目标**: 创建"二分查找"算法模版作为快速参考

**场景**:
1. 进入"设置 - 内容管理 - 算法模版"
2. 点击"新建模版"按钮
3. 填写模版信息：
   - 标题: 二分查找
   - 内容类型: template
   - 内容数据（JSON）:
     ```json
     {
       "language": "java",
       "template": "public int binarySearch(int[] nums, int target) { ... }",
       "notes": "注意边界条件...",
       "complexity": {"time": "O(log n)", "space": "O(1)"}
     }
     ```
   - 相关学习资源链接（可选）
   - 练习题目ID列表（可选）
4. 保存后，模版出现在列表中

**验收标准**:
- 算法模版作为独立板块，不属于大分类
- 支持Markdown格式的代码和注释
- 可以关联练习题目
- 可以关联学习资源

### 3.2 用户视角

#### 故事4: 三段式学习体验

**角色**: 普通用户
**目标**: 系统化学习"数组"这个Focus Area

**场景**:
1. 用户登录，进入"学习 - 编程与数据结构"
2. 顶部显示4个Tab（数据结构、搜索、动规、其他）
3. 点击"数据结构"Tab
4. 左侧显示Focus Area列表，点击"数组"
5. 右侧显示三个学习阶段卡片：

   **阶段1: 基础原理**
   - 说明: 数据结构的基本概念、特点、适用场景
   - 内容列表:
     - 📄 [数组（顺序存储）基本原理](https://...)
     - 📄 [动态数组代码实现](https://...)

   **阶段2: 实现代码**
   - 说明: 数据结构的代码实现、API设计
   - 内容列表:
     - 💻 [双指针技巧总结](https://...)
     - 💻 [滑动窗口算法模板](https://...)

   **阶段3: 实战题目**
   - 说明: LeetCode题目练习、算法应用
   - 内容列表:
     - 🎯 [26] 删除有序数组中的重复项 (EASY)
     - 🎯 [27] 移除元素 (EASY)

6. 点击文章链接，新窗口打开labuladong文章
7. 点击题目，弹出题目详情Modal（包含关键思路、笔记区）

**验收标准**:
- 三个阶段按顺序展示，清晰分隔
- 每个阶段显示描述和内容列表
- 不同类型内容有不同图标
- 支持点击跳转
- 实战题目支持Modal查看详情

#### 故事5: 查看算法模版

**角色**: 普通用户
**目标**: 快速查阅"二分查找"算法模版

**场景**:
1. 进入"学习 - 编程与数据结构 - 算法模版"
2. 左侧显示算法列表（支持搜索）
3. 点击"二分查找"
4. 右侧显示：
   ```
   # 二分查找

   ## 模版代码
   ```java
   public int binarySearch(int[] nums, int target) {
       int left = 0, right = nums.length - 1;
       while (left <= right) {
           int mid = left + (right - left) / 2;
           if (nums[mid] == target) return mid;
           else if (nums[mid] < target) left = mid + 1;
           else right = mid - 1;
       }
       return -1;
   }
   ```

   ## 注意事项
   - 数组必须有序
   - 注意边界条件 (left <= right)
   - 防止溢出: mid = left + (right - left) / 2

   ## 复杂度
   - 时间: O(log n)
   - 空间: O(1)

   ## 相关学习资源
   - [二分搜索算法核心代码模板](https://...)
   - [实际运用二分搜索时的思维框架](https://...)

   ## 练习题目
   - [704] 二分查找
   - [35] 搜索插入位置
   ```

**验收标准**:
- 代码支持语法高亮
- Markdown正确渲染
- 相关资源可点击跳转
- 练习题目可点击跳转到题目详情

---

## 4. 功能需求

### 4.1 管理端功能

#### F1: 学习阶段配置

**功能描述**: 为编程与数据结构Skill配置学习阶段（通过数据库初始化，暂不提供UI配置界面）

**实施方式**:
- 在Migration V7中直接插入3个学习阶段：
  - 基础原理 (theory)
  - 实现代码 (implementation)
  - 实战题目 (practice)
- 未来如需要其他Skill（如系统设计）的阶段配置，可在后续Phase添加UI管理界面

#### F2: 分阶段内容管理

**功能描述**: 管理员可以为Focus Area的每个学习阶段添加内容

**子功能**:
- F2.1 浏览分阶段内容
  - 按Tab切换学习阶段
  - 每个阶段显示内容列表

- F2.2 添加学习内容
  - 选择内容类型（article/video/code_example/template/case_study）
  - 填写通用字段（标题、URL、作者、描述）
  - 填写扩展数据（JSON，根据类型不同）
  - 设置可见性（public/private）

- F2.3 编辑学习内容
  - 修改所有字段
  - 调整排序顺序（拖拽）

- F2.4 删除学习内容
  - 二次确认
  - 级联删除用户笔记（如果是题目）

#### F3: 题目关联到学习阶段

**功能描述**: 管理员可以将题目关联到"实战题目"阶段

**子功能**:
- F3.1 关联现有题目
  - 从题库中搜索题目
  - 选择后自动设置stage_id为"实战题目"阶段ID

- F3.2 创建新题目并关联
  - 直接在界面创建题目
  - 自动关联到当前Focus Area和"实战题目"阶段

- F3.3 批量关联题目
  - 支持批量选择题目
  - 一键关联到当前阶段

#### F4: 算法模版管理

**功能描述**: 管理员可以维护算法模版库

**子功能**:
- F4.1 创建算法模版
  - 作为learning_contents表的一条记录
  - content_type = 'template'
  - 不关联到Focus Area（focus_area_id = NULL）
  - 填写content_data JSON（包含代码、注释、复杂度）

- F4.2 编辑算法模版
  - 修改模版代码和注释
  - 关联学习资源和练习题目

- F4.3 删除算法模版
  - 二次确认
  - 不影响题目数据

### 4.2 用户端功能

#### F5: 分阶段学习浏览

**功能描述**: 用户可以按学习阶段浏览内容

**子功能**:
- F5.1 大分类浏览
  - 顶部Tab显示4个大分类
  - 切换Tab加载对应Focus Area列表

- F5.2 Focus Area选择
  - 左侧列表显示Focus Area
  - 点击加载该Focus Area的所有学习阶段内容

- F5.3 学习阶段展示
  - 右侧显示学习阶段Tab切换（基础原理、实现代码、实战题目）
  - 点击Tab加载该阶段的内容列表
  - 内容列表显示：类型图标、标题、作者
  - Tab默认激活第一个阶段（基础原理）

- F5.4 内容查看
  - 点击文章/视频链接，新窗口打开
  - 点击题目，弹出详情Modal
  - 点击代码示例，展开显示代码块


#### F6: 算法模版查阅

**功能描述**: 用户可以查阅算法模版

**子功能**:
- F6.1 模版列表浏览
  - 左侧显示所有算法模版
  - 支持搜索模版名称

- F6.2 模版详情查看
  - 显示模版代码（语法高亮）
  - 显示注意事项（Markdown渲染）
  - 显示复杂度
  - 显示相关学习资源链接
  - 显示练习题目链接

#### F7: 个人笔记管理

**功能描述**: 用户可以为题目添加笔记

**子功能**:
- F7.1 添加笔记
  - 在题目详情Modal中编辑笔记
  - 支持Markdown格式
  - 一个用户对一道题只能有一条笔记（UPSERT）

- F7.2 查看笔记
  - 在题目详情Modal中显示笔记
  - Markdown正确渲染

- F7.3 编辑/删除笔记
  - 支持编辑已有笔记
  - 支持删除笔记

---

## 5. 数据需求

### 5.1 数据库设计

#### 表1: learning_stages (学习阶段定义表)

```sql
CREATE TABLE learning_stages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  skill_id BIGINT NOT NULL COMMENT '所属技能',
  stage_name VARCHAR(50) NOT NULL COMMENT '阶段名称，如"基础原理"',
  stage_type VARCHAR(50) NOT NULL COMMENT '阶段类型标识，如"theory"',
  description TEXT COMMENT '阶段说明',
  sort_order INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
  UNIQUE KEY uk_skill_stage (skill_id, stage_name),
  INDEX idx_skill_order (skill_id, sort_order)
) COMMENT='学习阶段定义表（Skill级别配置）';
```

**初始数据（编程与数据结构）**:
```sql
INSERT INTO learning_stages (skill_id, stage_name, stage_type, description, sort_order) VALUES
(1, '基础原理', 'theory', '数据结构的基本概念、特点、适用场景', 1),
(1, '实现代码', 'implementation', '数据结构的代码实现、API设计、算法技巧', 2),
(1, '实战题目', 'practice', 'LeetCode题目练习、算法应用', 3);
```

#### 表2: learning_contents (学习内容统一表)

```sql
CREATE TABLE learning_contents (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  focus_area_id BIGINT COMMENT 'Focus Area ID（算法模版为NULL）',
  stage_id BIGINT NOT NULL COMMENT '所属学习阶段',
  content_type ENUM('article', 'video', 'code_example', 'template', 'case_study') NOT NULL,

  -- 通用字段
  title VARCHAR(500) NOT NULL,
  description TEXT,
  url VARCHAR(1000) COMMENT '外部资源链接',
  author VARCHAR(100),

  -- 扩展内容（JSON存储，根据content_type不同）
  content_data JSON COMMENT '扩展数据，如代码、模版、案例详情等',

  -- 元信息
  sort_order INT DEFAULT 0,
  visibility ENUM('public', 'private') DEFAULT 'public',
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
  FOREIGN KEY (stage_id) REFERENCES learning_stages(id) ON DELETE CASCADE,
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_focus_stage (focus_area_id, stage_id, sort_order),
  INDEX idx_stage_type (stage_id, content_type)
) COMMENT='学习内容统一表';
```

**content_data JSON示例**:

```json
// content_type = 'code_example'
{
  "language": "java",
  "code": "public class ArrayList<E> {...}",
  "complexity": {"time": "O(1)", "space": "O(n)"},
  "notes": "动态扩容策略：每次扩容为原来的1.5倍"
}

// content_type = 'template'
{
  "language": "java",
  "template": "public int binarySearch(...) {...}",
  "notes": "注意边界条件...",
  "complexity": {"time": "O(log n)", "space": "O(1)"},
  "relatedResources": [
    {"title": "二分搜索核心模板", "url": "https://..."}
  ],
  "practiceQuestions": [704, 35, 33]
}

// content_type = 'case_study'（系统设计用）
{
  "systemName": "Instagram Feed",
  "requirements": ["支持10亿用户", "Feed刷新延迟<500ms"],
  "architectureDiagramUrl": "https://...",
  "keyComponents": [
    {"name": "Feed Service", "description": "..."}
  ],
  "tradeoffs": [
    {"decision": "Push vs Pull", "chosen": "Hybrid", "reasoning": "..."}
  ]
}
```

#### 表3: questions (题目表 - 增加stage_id)

```sql
ALTER TABLE questions
ADD COLUMN stage_id BIGINT COMMENT '所属学习阶段（实战题目阶段）',
ADD FOREIGN KEY (stage_id) REFERENCES learning_stages(id) ON DELETE SET NULL;
```

#### 表4: major_categories (大分类表 - 调整为4个)

```sql
-- Migration中删除"核心刷题框架"和"基础篇"
DELETE FROM major_categories WHERE name IN ('核心刷题框架', '基础篇');

-- 保留4个大分类
-- 1. 数据结构
-- 2. 搜索
-- 3. 动规
-- 4. 其他
```

### 5.2 数据关联

```
skills (编程与数据结构)
  ↓ 1:N
learning_stages (基础原理、实现代码、实战题目)
  ↓ 1:N
learning_contents (文章、视频、代码、模版)
  ↑ N:1
focus_areas (数组、链表、二叉树...)
  ↑ N:1
major_categories (数据结构、搜索、动规、其他)

questions (题目表)
  ↓ N:1
learning_stages (关联到"实战题目"阶段)
```

### 5.3 数据迁移

**Migration V7 需要完成**:
1. 创建 `learning_stages` 表
2. 创建 `learning_contents` 表
3. `questions` 表增加 `stage_id` 字段
4. 删除 `major_categories` 表中的"核心刷题框架"和"基础篇"
5. 初始化"编程与数据结构"的3个学习阶段
6. （可选）将现有的 `resources` 数据迁移到 `learning_contents`

---

## 6. 非功能需求

### 6.1 性能要求

- 学习内容列表分页加载（每页50条）
- 学习阶段列表缓存（很少变化）
- Markdown渲染性能优化（大文档分块渲染）

### 6.2 安全要求

- 管理端所有API需要管理员权限
- 用户只能管理自己的笔记
- 防止XSS攻击（Markdown内容过滤）

### 6.3 兼容性要求

- 不影响现有的试题库功能（Phase 3）
- Focus Area表的扩展字段对现有数据兼容
- 支持主流浏览器（Chrome、Safari、Firefox）

### 6.4 可维护性要求

- 大分类固定为4个（数据结构、搜索、动规、其他）
- 学习阶段支持Skill级别自定义
- 扩展信息采用JSON存储，灵活扩展

---

## 7. UI/UX需求

### 7.1 视觉设计

- 延续现有风格（Tailwind CSS）
- 大分类Tab使用蓝色高亮
- 学习阶段卡片使用渐变色区分
- 不同类型内容使用不同图标（📄文章、🎥视频、💻代码、🎯题目）
- 难度标签颜色区分（EASY绿色、MEDIUM黄色、HARD红色）

### 7.2 交互设计

- 学习阶段卡片默认展开，可折叠
- 拖拽排序提供视觉反馈
- 外部链接点击在新窗口打开
- Markdown编辑器提供实时预览

### 7.3 响应式设计

- 支持桌面端（最小宽度1280px）
- 三栏布局在小屏幕上自适应为上下布局

---

## 8. 验收标准

### 8.1 管理端验收

- ✅ 管理员可以为"编程与数据结构"配置学习阶段
- ✅ 管理员可以为"数组"Focus Area的"基础原理"阶段添加文章
- ✅ 管理员可以为"实现代码"阶段添加代码示例
- ✅ 管理员可以为"实战题目"阶段关联LeetCode题目
- ✅ 管理员可以创建"二分查找"算法模版
- ✅ 所有Markdown内容正确渲染

### 8.2 用户端验收

- ✅ 用户可以浏览4个大分类的学习内容
- ✅ 用户可以看到"数组"Focus Area的三个学习阶段
- ✅ 用户可以点击文章链接跳转到labuladong网站
- ✅ 用户可以查看题目的关键思路（Markdown渲染）
- ✅ 用户可以添加个人笔记（Markdown格式）
- ✅ 用户可以查阅算法模版

### 8.3 性能验收

- ✅ 学习内容列表加载时间 < 1秒（50条/页）
- ✅ Markdown渲染流畅（无明显卡顿）
- ✅ Tab切换响应时间 < 200ms

---

## 9. 架构优势

### 9.1 与v1.0的对比

**v1.0设计（被替换）**:
- 6个固定大分类（基础篇、核心框架、数据结构、搜索、动规、其他）
- 学习资料和题目分开管理
- 不支持自定义学习阶段

**v2.0设计（当前）**:
- ✅ 4个大分类（数据结构、搜索、动规、其他）
- ✅ 支持Skill级别自定义学习阶段
- ✅ 统一管理所有学习内容（文章、视频、代码、题目）
- ✅ 灵活的JSON扩展字段
- ✅ 适配不同技能的学习路径（算法 vs 系统设计 vs 运维）

### 9.2 未来扩展性

**系统设计Skill扩展示例**:
```sql
-- 为"系统设计"Skill定义4个学习阶段
INSERT INTO learning_stages (skill_id, stage_name, stage_type, description, sort_order) VALUES
(2, '案例分析', 'case_study', '真实系统的架构分析、需求理解', 1),
(2, '架构模版', 'architecture_template', '通用架构模式、设计模版', 2),
(2, '权衡讨论', 'tradeoff', '不同方案的优缺点、技术选型', 3),
(2, '延伸阅读', 'extended_reading', '论文、博客、技术文档', 4);

-- 添加系统设计案例
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, content_data) VALUES
(10, 4, 'case_study', 'Instagram Feed架构设计', '{"systemName": "Instagram Feed", ...}');
```

---

## 10. 设计决策

以下设计问题已确认：

1. **算法模版的组织方式** ✅ 已确认
   - **决策**: 选项B - 作为独立板块，不关联Focus Area（focus_area_id = NULL）
   - **理由**: 算法模版是快速参考工具，不属于某个具体Focus Area，独立组织更清晰
   - **实现**: learning_contents表中，content_type = 'template'且focus_area_id = NULL的记录

2. **学习进度跟踪** ✅ 已确认
   - **决策**: Phase 4 不实现学习进度功能
   - **理由**: 学习进度需要复杂的状态管理和业务逻辑，不在Phase 4范围内
   - **未来计划**: 可在后续Phase实现用户学习进度、完成状态、学习时长等功能

3. **内容可见性控制** ✅ 已确认
   - **决策**: Phase 4 区分管理员内容和用户笔记
   - **管理员内容** (learning_contents表):
     - visibility = 'public' - 所有用户可见的学习资源
     - created_by = admin用户ID
   - **用户笔记** (user_question_notes表):
     - 用户为题目添加的个人笔记
     - 只有笔记所有者可见
     - 不通过visibility字段控制，通过user_id关联实现私有性

---

**文档版本**: v2.0
**创建时间**: 2025-12-21
**最后更新**: 2025-12-22
**状态**: v2.0架构重构 - 待Review ✅
**下一步**: 撰写设计文档 (Phase4-设计文档.md)
