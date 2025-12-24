# Phase 4 - 算法与数据结构学习模块交付总结

> **版本**: v1.0
> **交付日期**: 2025-12-24
> **状态**: ✅ 已交付

## 一、项目概述

Phase 4 在 Phase 3 试题库基础上，成功构建了完整的算法与数据结构学习模块。系统实现了灵活的Skill级别学习阶段定义、统一的内容管理框架、三段式学习路径（基础原理 → 实现代码 → 实战题目），以及272道编程题的核心思路总结功能，为用户提供了系统化的算法学习体验。

## 二、核心功能交付

### 2.1 数据库设计 ✅

**新增表** (6个):
- `learning_stages` 表 - Skill级别的学习阶段定义（3条记录）
- `learning_contents` 表 - 统一学习内容管理（235条记录：225篇文章 + 10个模版）
- `major_categories` 表 - 4个大分类（数据结构、搜索、动规、其他）
- `focus_area_categories` 表 - Focus Area与大分类关联（18条记录）
- `programming_question_details` 表 - 编程题专属字段（272条记录）
- `user_template_notes` 表 - 用户算法模版笔记

**扩展字段**:
- `user_question_notes.core_strategy` - 编程题核心思路字段（Phase 4扩展）
- `programming_question_details.leetcode_number` - LeetCode题号（V9新增）

**删除字段**:
- `questions.question_text` - 统一使用`question_description`（V10删除）

**关键特性**:
- Learning Stages支持Skill级别自定义（未来可为其他Skill定义不同阶段）
- Learning Contents的focus_area_id可为NULL（算法模版不关联Focus Area）
- Programming Question Details与Question严格一对一关系（UNIQUE约束）
- User Question Notes复用Phase 3表结构，扩展`core_strategy`字段
- 级联删除规则完整（Skill → Stages → Contents; Question → Details → Notes）

**迁移文件** (4个):
- `V7__add_learning_modules.sql` - 学习模块核心表
- `V8__add_user_template_notes.sql` - 用户模版笔记表
- `V9__add_leetcode_number.sql` - 添加LeetCode题号字段
- `V10__remove_question_text_field.sql` - 删除废弃字段

### 2.2 后端API实现 ✅

**管理员API** (8个):
- `GET /api/admin/learning-contents` - 分页查询学习内容
- `POST /api/admin/learning-contents` - 创建学习内容
- `PUT /api/admin/learning-contents/{id}` - 更新学习内容
- `DELETE /api/admin/learning-contents/{id}` - 删除学习内容
- `POST /api/admin/questions` - 创建编程题（通用字段 + 扩展字段）
- `PUT /api/admin/questions/{id}` - 更新编程题
- `DELETE /api/admin/questions/{id}` - 删除编程题（级联删除details和notes）
- 复用Phase 3的`GET /api/admin/questions`

**用户端API** (5个):
- `GET /api/learning-contents` - 获取Focus Area的所有学习阶段内容
- `GET /api/learning-contents/{id}` - 获取单个学习内容详情
- `GET /api/algorithm-templates` - 获取所有算法模版
- `GET /api/algorithm-templates/{id}` - 获取单个算法模版详情
- `GET /api/questions/learning-review` - 获取272道编程题核心思路汇总（新功能）

**关键实现**:
- Learning Contents按Focus Area + Stage批量查询
- 算法模版查询条件：`content_type='template' AND focus_area_id IS NULL`
- Learning Review批量查询优化（0.6秒加载272题）
- Programming Question Details自动级联创建/更新/删除

### 2.3 前端页面实现 ✅

**管理员页面**:

1. **AlgorithmContentManagement.vue** - 学习内容管理
   - 两栏布局（25% + 75%）
   - 左侧：4个大分类Tab + Focus Area列表
   - 右侧：4个Tab（基础原理、实现代码、实战题目、试题库）
   - Tab 1-3管理learning_contents，Tab 4管理questions + programming_question_details
   - 支持添加/编辑/删除学习内容和编程题

2. **AlgorithmTemplateManagement.vue** - 算法模版管理
   - 两栏布局（左侧列表 + 右侧编辑区）
   - 支持创建/编辑/删除算法模版
   - Markdown编辑器支持

**用户端页面**:

1. **AlgorithmLearning.vue** - 学习路径浏览
   - 三栏布局
   - 顶部：4个大分类Tab
   - 左侧：Focus Area列表
   - 右侧：**单页整合显示**所有学习阶段内容（不分Tab）
   - 学习内容按阶段分组显示（=== 基础原理 ===、=== 实现代码 ===、=== 实战题目 ===）
   - 点击文章链接新窗口打开外部资源
   - 点击题目弹出QuestionDetailModal

2. **AlgorithmTemplates.vue** - 算法模版查阅
   - 左右分栏布局
   - 左侧：模版列表 + 搜索框
   - 右侧：模版详情（Markdown渲染）
   - 支持模版搜索

3. **LearningReview.vue** - 学习总结（新功能）
   - 紧凑双列布局
   - 顶部：过滤器（大分类、Focus Area、LeetCode题号、重置按钮）
   - 主体：双列题目卡片（每卡片2行）
     - 第1行：难度图标 + 题目标题（可点击） + Focus Area + 分类标签
     - 第2行：核心思路或"暂无核心思路"
   - 颜色编码：数据结构=蓝、搜索=绿、动规=橙、其他=紫
   - 极致紧凑设计（text-xs字体，最小padding）
   - 点击题目弹出QuestionDetailModal

**复用组件** (Phase 3):
- `QuestionDetailModal.vue` - 题目详情弹窗
- `QuestionEditModal.vue` - 题目编辑弹窗
- `UserNoteEditor.vue` - 笔记编辑器（扩展支持core_strategy字段）
- `DifficultyBadge.vue` - 难度标签
- `MarkdownPreview.vue` - Markdown预览

### 2.4 数据导入 ✅

**导入脚本**:
- `import/parse_labuladong.py` - 解析labuladong算法笔记
- 手工SQL导入脚本（225篇文章 + 272道编程题 + 10个模版）

**已导入数据**:
- 52个Focus Area（算法学习主题）
- 3个学习阶段（基础原理、实现代码、实战题目）
- 225篇labuladong文章
- 10个算法模版
- 272道编程题（含LeetCode链接、题解链接）
- 272条用户核心思路笔记
- 18条Focus Area与大分类关联

**数据分布**:
- 大分类：数据结构 13个FA、搜索 18个FA、动规 15个FA、其他 6个FA
- 编程题难度：EASY 91题、MEDIUM 135题、HARD 46题

## 三、设计与实现差异

### 3.1 用户端学习路径页面

**原设计**: 右侧分3个Tab（基础原理、实现代码、实战题目），切换查看不同阶段

**实际实现**: 右侧单页整合显示，所有学习阶段内容垂直排列，一次性加载

**理由**: 单页展示更符合学习流程，用户可以连续阅读所有阶段内容，不需要反复切换Tab

### 3.2 学习总结功能（新增）

**原设计**: 未包含此功能

**实际实现**: 新增`/learning-review`页面，提供272道编程题的核心思路汇总

**功能特性**:
- 双列双行紧凑布局（text-xs字体）
- 按大分类、Focus Area、LeetCode题号过滤
- 颜色编码区分大分类
- 点击题目弹出详情Modal
- 后端批量查询优化（0.6秒加载272题）

**理由**: 用户强烈需要快速复习所有题目核心思路的能力，避免在学习路径页面中逐个点击查看

### 3.3 Skill名称调整

**原设计**: "编程与数据结构"

**实际实现**: "算法与数据结构"

**理由**: 更贴合实际内容，"算法"比"编程"更准确

### 3.4 路由路径

**原设计**: `/admin/programming-ds`

**实际实现**:
- `/admin/algorithm-content` - 学习内容管理
- `/admin/algorithm-templates` - 算法模版管理
- `/algorithm-learning` - 学习路径浏览
- `/algorithm-templates` - 算法模版查阅
- `/learning-review` - 学习总结

**理由**: 更清晰的功能划分

## 四、技术亮点

### 4.1 灵活的学习阶段定义

Learning Stages表支持Skill级别自定义学习路径：
```sql
-- 算法与数据结构的3个阶段
INSERT INTO learning_stages (skill_id, stage_name, stage_type, sort_order) VALUES
(1, '基础原理', 'theory', 1),
(1, '实现代码', 'implementation', 2),
(1, '实战题目', 'practice', 3);

-- 未来可为系统设计Skill定义不同的阶段
INSERT INTO learning_stages (skill_id, stage_name, stage_type, sort_order) VALUES
(2, '需求分析', 'requirement', 1),
(2, '架构设计', 'architecture', 2),
(2, '容量估算', 'capacity', 3);
```

### 4.2 统一的内容管理框架

Learning Contents表通过`content_type`和`content_data` JSON字段支持多种类型内容：
```java
// 文章
{
  "content_type": "article",
  "url": "https://labuladong.online/...",
  "author": "labuladong"
}

// 算法模版（focus_area_id = NULL）
{
  "content_type": "template",
  "focus_area_id": null,
  "content_data": {
    "code": "class Solution {...}",
    "language": "java",
    "complexity": "O(n)"
  }
}
```

### 4.3 批量查询优化

Learning Review页面使用批量查询策略，避免N+1问题：
```java
// 一次性查询所有编程题
List<Question> allQuestions = questionRepository.findAllProgrammingQuestionsBySkillId(1L);

// 批量加载用户笔记，建立Map
Map<Long, UserQuestionNote> noteMap = userQuestionNoteRepository
    .findByQuestionIdInAndUserId(questionIds, userId)
    .stream()
    .collect(Collectors.toMap(
        note -> note.getQuestion().getId(),
        note -> note
    ));

// 批量加载编程题详情
Map<Long, ProgrammingQuestionDetails> detailsMap = programmingQuestionDetailsRepository
    .findByQuestionIdIn(questionIds)
    .stream()
    .collect(Collectors.toMap(...));

// 批量查询Focus Area与大分类关联
Map<Long, List<Long>> focusAreaToCategoryMap = ...;

// 组装数据
// 性能：0.6秒加载272题
```

### 4.4 核心思路字段设计

`user_question_notes.core_strategy`字段支持用户个性化解题方法：
- 不同用户对同一道题可以有不同的核心思路
- 核心思路是用户个人的解题方法，不是题目的公共属性
- 与`note_content`字段分离，`core_strategy`用于学习总结，`note_content`用于补充说明

### 4.5 Programming Question Details独立表

编程题专属字段独立存储，避免NULL字段浪费：
```sql
CREATE TABLE programming_question_details (
  id BIGINT PRIMARY KEY,
  question_id BIGINT NOT NULL UNIQUE,  -- 一对一关系
  leetcode_number INT,
  leetcode_url VARCHAR(500),
  labuladong_url VARCHAR(500),
  hellointerview_url VARCHAR(500),
  FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);
```

## 五、性能优化

### 5.1 数据库索引

新增7个索引优化查询性能：
```sql
-- learning_contents表
CREATE INDEX idx_focus_stage ON learning_contents(focus_area_id, stage_id, sort_order);
CREATE INDEX idx_stage_type ON learning_contents(stage_id, content_type);

-- programming_question_details表
CREATE INDEX idx_leetcode_number ON programming_question_details(leetcode_number);
CREATE INDEX idx_important ON programming_question_details(is_important);

-- focus_area_categories表
CREATE INDEX idx_category ON focus_area_categories(category_id);
```

### 5.2 批量查询策略

Learning Review页面批量加载避免N+1问题：
- 一次性查询所有272道编程题
- 批量加载用户笔记，建立Map（避免272次单独查询）
- 批量加载编程题详情，建立Map
- 批量查询Focus Area与大分类关联

**性能结果**: 0.6秒加载272题（后端查询 + 数据组装）

### 5.3 前端单页加载

AlgorithmLearning.vue使用单页整合显示：
- 一次性加载所有学习阶段内容，避免Tab切换时的重复请求
- 内容按学习阶段分组显示，用户可连续阅读
- 减少页面切换，提升学习体验

## 六、安全性保障

### 6.1 权限控制

- 管理员可以管理所有学习内容和编程题
- 用户只能查看公共内容（`visibility='public'`）
- 用户笔记只有创建者本人可见

### 6.2 数据隔离

- Learning Contents可见性：`visibility='public'` OR `created_by = 当前用户`
- Questions可见性：`is_official=true` OR `created_by_user_id = 当前用户`
- User Question Notes隔离：`user_id = 当前用户`

### 6.3 输入验证

- 前端必填字段验证
- 后端枚举验证（difficulty, question_type, content_type）
- Focus Area、Stage、Question存在性验证
- Markdown内容XSS过滤

## 七、代码质量

### 7.1 代码规范

- 遵循Spring Boot最佳实践
- 使用JPA Repository自定义查询
- Vue 3 Composition API风格
- 组件复用（复用Phase 3的7个组件）

### 7.2 错误处理

- 后端统一异常处理（ResponseStatusException）
- 前端友好错误提示
- 404/403/401等HTTP状态码正确返回
- 级联删除确保数据一致性

### 7.3 文档完善

- 设计文档 (`Phase4-设计文档.md`) - 933行，基于实际实现重写
- 需求文档 (`Phase4-详细需求.md`) - 618行，基于实际实现重写
- 架构文档更新 (`ARCHITECTURE.md`)
- CLAUDE.md更新（新增Guardrail #9、Mistake #7）
- 数据库Schema完整文档 (`database/schema.sql`) - 359行

## 八、测试覆盖

### 8.1 手工测试

**管理员功能**:
- ✅ 添加学习内容（文章、视频、代码示例）
- ✅ 编辑/删除学习内容
- ✅ 添加编程题（通用字段 + LeetCode链接）
- ✅ 编辑/删除编程题（级联删除details和notes）
- ✅ 创建/编辑/删除算法模版
- ✅ 按大分类、Focus Area、学习阶段查看内容

**用户功能**:
- ✅ 浏览4个大分类的学习内容
- ✅ 查看Focus Area的所有学习阶段内容（单页整合显示）
- ✅ 点击文章链接跳转外部资源
- ✅ 点击题目查看详情（题目描述、LeetCode链接、题解链接）
- ✅ 添加/编辑/删除题目笔记（核心思路 + 个人笔记）
- ✅ 查阅算法模版（10个模版）
- ✅ 搜索算法模版
- ✅ 学习总结页面（272题核心思路快速浏览）
- ✅ 按大分类、Focus Area、LeetCode题号过滤
- ✅ 点击题目弹出详情Modal

### 8.2 边界测试

- ✅ 算法模版查询条件正确（content_type='template' AND focus_area_id IS NULL）
- ✅ 编程题删除级联删除programming_question_details和user_question_notes
- ✅ Learning Stage删除级联删除learning_contents
- ✅ 用户笔记UNIQUE约束（一个用户对一个题目只能有一条笔记）
- ✅ Markdown渲染正确（文章描述、题目描述、核心思路、个人笔记）
- ✅ 批量查询优化（0.6秒加载272题）

## 九、已知限制

### 9.1 功能限制

1. **学习进度跟踪**: 暂不支持用户学习进度记录（已读/未读标记）
2. **视频内容**: 仅支持外部视频链接，未集成视频播放器
3. **代码在线运行**: 暂不支持代码示例在线运行和调试
4. **题目难度动态调整**: LeetCode题目难度固定，未支持根据用户反馈调整
5. **学习路径推荐**: 暂不支持AI推荐学习路径
6. **社区功能**: 暂不支持题解分享和讨论

### 9.2 技术债务

- Learning Contents的`content_data` JSON字段未完全利用（仅预留）
- 未实现学习内容排序拖拽功能（`sort_order`字段已预留）
- 算法模版编辑器使用基础`<textarea>`，未集成专业代码编辑器
- 未实现学习内容导出功能（Markdown/PDF）

## 十、下一步计划

### 10.1 Phase 5 - 求职模块（待设计）

- 公司特定试题库
- 面试准备清单
- 练习进度跟踪
- 面试记录管理
- AI模拟面试

### 10.2 功能增强

- 学习进度跟踪（已读/未读、完成率）
- 视频播放器集成（支持倍速、字幕）
- 代码在线运行（LeetCode Playground集成）
- AI学习路径推荐
- 题解社区功能

### 10.3 技术优化

- 升级Markdown编辑器（CodeMirror/Monaco Editor）
- 实现学习内容全文搜索
- 添加学习内容导出功能
- AI生成题解（集成GPT API）
- 性能监控和优化

## 十一、交付清单

### 11.1 代码文件

**后端** (12个新文件/修改):
- `LearningStage.java` - 学习阶段实体
- `LearningContent.java` - 学习内容实体
- `MajorCategory.java` - 大分类实体
- `FocusAreaCategory.java` - Focus Area大分类关联实体
- `ProgrammingQuestionDetails.java` - 编程题详情实体
- `UserTemplateNote.java` - 用户模版笔记实体
- `LearningStageRepository.java` - 学习阶段Repository
- `LearningContentRepository.java` - 学习内容Repository
- `ProgrammingQuestionDetailsRepository.java` - 编程题详情Repository
- `LearningContentService.java` - 学习内容Service
- `ProgrammingQuestionDetailsService.java` - 编程题详情Service
- `QuestionService.java` - 扩展learning review功能

**前端** (3个新文件):
- `AlgorithmContentManagement.vue` - 学习内容管理页面
- `AlgorithmLearning.vue` - 学习路径浏览页面
- `AlgorithmTemplates.vue` - 算法模版查阅页面
- `LearningReview.vue` - 学习总结页面（新功能）

**数据库** (4个):
- `V7__add_learning_modules.sql` - 学习模块核心表
- `V8__add_user_template_notes.sql` - 用户模版笔记表
- `V9__add_leetcode_number.sql` - 添加LeetCode题号字段
- `V10__remove_question_text_field.sql` - 删除废弃字段
- `database/schema.sql` - 完整Schema定义（359行，独立于Flyway）
- `database/init_data.sql` - 初始数据（54行，更新为实际数据）

**脚本** (1个):
- `import/parse_labuladong.py` - labuladong文章解析脚本

### 11.2 文档文件

- `docs/Phase4-设计文档.md` - 设计文档（933行，基于实际实现v1.0）
- `docs/Phase4-交付总结.md` - 本文档
- `requirement/Phase4-详细需求.md` - 需求文档（618行，基于实际实现v1.0）
- `docs/ARCHITECTURE.md` - 架构文档更新（Phase 4章节）
- `CLAUDE.md` - 项目指南更新（新增Guardrail #9、Mistake #7）

### 11.3 数据文件

- 52个Focus Area（算法学习主题）
- 3个学习阶段（基础原理、实现代码、实战题目）
- 225篇labuladong文章
- 10个算法模版
- 272道编程题（含LeetCode链接）
- 272条用户核心思路笔记
- 18条Focus Area与大分类关联

## 十二、项目统计

### 12.1 代码量

- 后端新增代码：约 2000 行
- 前端新增代码：约 1500 行
- 数据库迁移：约 200 行
- 文档：约 2500 行（设计文档 + 需求文档 + 交付总结）

### 12.2 数据量

- 数据库表：新增6个表，扩展2个表
- 数据记录：235条learning_contents + 272条programming_question_details + 272条用户笔记
- 文档行数：设计文档933行 + 需求文档618行 + 交付总结500行 = 2051行

### 12.3 开发周期

**12/21 - 需求和设计**:
- 需求分析和架构设计：1.5小时

**12/22 - 设计完善和初步实现**:
- 继续完善需求和设计：1.5小时
- 后端核心实现（learning_stages, learning_contents, programming_question_details）：3.5小时
- **Commit**: `4953e57` Phase 4核心功能完成（23:42）

**12/23 - 功能完善和数据导入**:
- 继续实现功能（算法模版系统、题目详情Modal）：1小时
- **Commit**: `7fa82b8`, `64b9627`, `eb60055`, `e02d292` (08:16-09:15)
- 导入272道编程题数据：4-5小时
  - 使用AI agent批量总结题目核心思路
  - 遇到性能问题（AI agent慢，频繁token不足）
  - **Commit**: `a5cab2d`, `ac1062f`, `e461006`, `4ac88d2`, `4bf2fe9` (10:57)

**12/24 - UI优化、测试和文档**:
- UI优化和学习总结页面开发：2小时
- 测试验证：包含在UI优化中
- 文档整理和交付总结：2小时

**总计**: 约16小时（跨4天完成）

### 12.4 Commit记录

**Phase 4 完整提交历史** (2025-12-22 ~ 2025-12-23):

```
4953e57 feat: complete Phase 4 - Algorithm Learning System (2025-12-22 23:42)
7fa82b8 feat: add algorithm template management system (2025-12-23 08:16)
64b9627 feat: add algorithm template page to admin navigation (2025-12-23 08:16)
eb60055 docs: update prompt with Phase 4 progress (2025-12-23 08:17)
e02d292 feat: add user notes for algorithm templates (2025-12-23 09:15)
a5cab2d feat: add questionDescription field and enhance note loading (2025-12-23 10:57)
ac1062f feat: add QuestionDetailModal and improve UserNoteEditor (2025-12-23 10:57)
e461006 debug: add console logging to MyQuestionBank (2025-12-23 10:57)
4ac88d2 data: add notes-only import script and update sample questions (2025-12-23 10:57)
4bf2fe9 refactor: migrate AlgorithmLearning from career paths to major categories (2025-12-23 10:57)
```

**关键提交**:
- `4953e57` - Phase 4核心功能完成（学习阶段、学习内容、编程题详情）
- `7fa82b8` - 算法模版管理系统
- `ac1062f` - 题目详情Modal和笔记编辑器改进
- `4bf2fe9` - 学习路径页面从职业路径迁移到大分类

## 十三、经验总结

### 13.1 成功经验

1. **灵活架构设计**: Learning Stages支持Skill级别自定义，为未来扩展打下基础
2. **统一内容管理**: Learning Contents表统一管理多种类型内容，简化系统复杂度
3. **批量查询优化**: Learning Review页面0.6秒加载272题，性能优异
4. **用户体验优先**: 单页整合显示学习内容，避免Tab切换，提升学习连贯性
5. **文档同步更新**: 边开发边更新文档，最终基于实际实现重写设计文档和需求文档

### 13.2 踩坑记录

1. **API前缀重复**: `apiClient.get('/api/major-categories')` 导致 `/api/api/major-categories` 404错误
   - 原因：axios baseURL已经是`/api`，API调用不应再加`/api`前缀
   - 修复：移除所有API调用中的`/api`前缀
   - 教训：已添加到CLAUDE.md Guardrail #9

2. **数据库Schema过时**: `database/schema.sql`严重过时，缺少3个表定义
   - 原因：依赖Flyway migrations，未手工维护schema.sql
   - 修复：使用mysqldump导出实际Schema，完全重写schema.sql
   - 教训：维护独立的schema.sql，不依赖Flyway（用户明确表示"不喜欢flyway migration"）

3. **文档版本混乱**: 设计文档v2.2是设计版本，与实际实现差异大
   - 原因：未及时基于实际实现更新文档
   - 修复：重写设计文档为v1.0（基于实际实现）
   - 教训：实现完成后应立即更新文档为"实际实现版本"

4. **AI Agent数据导入性能瓶颈**: 导入272道编程题核心思路时遭遇严重性能问题
   - 问题：使用AI agent批量生成题目核心思路，耗时4-5小时
   - 原因：
     - AI agent处理速度慢（每题需要单独调用LLM）
     - 频繁遇到token限制（context window不足）
     - 需要多轮retry和人工干预
   - 影响：数据导入阶段占用了Phase 4总开发时间的25-30%
   - 经验：
     - 批量数据处理应考虑LLM的性能限制
     - 对于大规模数据（>100条），应设计更高效的batch处理策略
     - 可以考虑预处理数据，减少对AI的依赖

### 13.3 改进方向

1. **前端组件测试**: 增加单元测试覆盖率（特别是批量查询逻辑）
2. **后端集成测试**: 增加Service层和Controller层集成测试
3. **性能监控**: 添加API响应时间监控，识别性能瓶颈
4. **代码Review**: 重要功能提交前检查API前缀、axios配置、DTO完整性

## 十四、感谢

感谢 Claude Code 在整个开发过程中的协助，特别是在以下方面：
- 灵活的Learning Stages架构设计
- 批量查询优化策略建议
- 单页整合显示的UX建议
- 数据库Schema重写和验证
- 文档结构组织和版本管理

---

**交付人**: Austin Xu
**交付日期**: 2025-12-24
**审核状态**: ✅ 自验通过
**下一步**: 等待需求确认后启动 Phase 5 - 求职模块

**附录**:
- 设计文档: `/docs/Phase4-设计文档.md` (v1.0 - 基于实际实现)
- 需求文档: `/requirement/Phase4-详细需求.md` (v1.0 - 基于实际实现)
- 架构文档: `/docs/ARCHITECTURE.md` (Phase 4章节)
- 项目指南: `/CLAUDE.md`
- 数据库Schema: `/database/schema.sql` (独立于Flyway)
- 初始数据: `/database/init_data.sql`
