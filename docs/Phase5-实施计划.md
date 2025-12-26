# Phase 5 实施计划 - 系统设计学习模块

> **版本**: v1.0
> **创建日期**: 2025-12-25
> **预计开始**: 待定
> **参考文档**:
> - [Phase5-设计文档.md](./Phase5-设计文档.md)
> - [Phase5-详细需求.md](../requirement/Phase5-详细需求.md)
> - [Phase5-系统设计学习模块.md](../prompt/Phase5-系统设计学习模块.md)

---

## 一、实施概述

### 1.1 目标

Phase 5 将实现**系统设计学习模块**，包含两个子模块：
1. **基础知识模块** - 复用现有Skill + Focus Area + Learning Content架构
2. **典型案例模块** - 新增独立的案例管理系统（5张新表）

### 1.2 核心价值

- 提供系统化的系统设计学习路径
- 支持基础知识学习（核心概念、关键技术、设计模式）
- 提供典型案例实战练习（设计Twitter、Instagram等）
- 标准化6步骤答题框架
- 支持多个参考答案方案
- 左右对比学习模式

### 1.3 实施策略

- **分阶段交付**: 基础知识模块 → 典型案例基础功能 → 用户答题功能 → 高级功能
- **复用优先**: 最大化复用Phase 2/4的架构和组件
- **质量优先**: 每个阶段充分测试后再进入下一阶段
- **文档同步**: 边实施边更新文档

---

## 二、实施阶段

### Phase 5.1: 基础知识模块（高优先级）

**预计工时**: 8-10小时

#### 任务清单

**后端开发** (4-5小时):
- [x] 创建"系统设计" Skill (已存在，ID=2)
- [x] 创建3个Major Categories
  - [x] 核心概念 (ID=13)
  - [x] 关键技术 (ID=14)
  - [x] 设计模式 (ID=15)
- [x] 等待用户提供Focus Area清单 (已有2个：Problem-Solving, Systems Design & Architecture)
- [ ] 批量导入Focus Areas（用户后续添加）
- [x] 复用Learning Contents表结构（无需修改）
- [x] 复用Learning Stages表结构（无需修改）

**前端开发** (3-4小时):
- [x] 创建管理员基础知识管理页面 `/admin/system-design-basics`
  - [x] 复用AlgorithmContentManagement.vue布局（两栏）
  - [x] 左侧：3个大分类Tab + Focus Area列表
  - [x] 右侧：学习资料列表（文章、视频）
  - [x] 支持CRUD学习资料
- [x] 创建用户基础知识学习页面 `/system-design/basics`
  - [x] 复用AlgorithmLearning.vue布局（两栏）
  - [x] 左侧：3个大分类Tab + Focus Area列表
  - [x] 右侧：单页整合显示学习资料（视频、文章分类）

**数据导入** (1小时):
- [x] 初始化系统设计Skill (已存在)
- [x] 初始化3个Major Categories
- [x] 导入Focus Areas（已有2个，用户后续继续添加）

**测试验证** (包含在开发中):
- [x] 管理员可以为Focus Area添加学习资料
- [x] 用户可以浏览3个大分类的基础知识
- [x] 学习资料正确显示（文章、视频）
- [x] 基础功能已实现，待用户手工测试

---

### Phase 5.2: 典型案例基础功能（高优先级）

**预计工时**: 12-15小时

#### 任务清单

**数据库设计** (2小时):
- [ ] 创建Migration V11: `V11__create_system_design_tables.sql`
  - [ ] `system_design_cases` - 案例主表
  - [ ] `case_resources` - 学习资源表
  - [ ] `case_solutions` - 参考答案表（支持多方案）
  - [ ] `solution_diagrams` - 配图表
  - [ ] `user_case_notes` - 用户答题记录表
- [ ] 验证表结构和索引
- [ ] 验证级联删除规则
- [ ] 更新 `database/schema.sql`（独立于Flyway）

**后端开发** (5-6小时):
- [ ] 创建实体类（5个）
  - [ ] `SystemDesignCase.java`
  - [ ] `CaseResource.java`
  - [ ] `CaseSolution.java`
  - [ ] `SolutionDiagram.java`
  - [ ] `UserCaseNote.java`
- [ ] 创建Repository层（5个）
  - [ ] `SystemDesignCaseRepository`
  - [ ] `CaseResourceRepository`
  - [ ] `CaseSolutionRepository`
  - [ ] `SolutionDiagramRepository`
  - [ ] `UserCaseNoteRepository`
- [ ] 创建Service层（5个）
  - [ ] `SystemDesignCaseService` - 案例CRUD
  - [ ] `CaseResourceService` - 资源管理
  - [ ] `CaseSolutionService` - 答案管理（支持多方案）
  - [ ] `SolutionDiagramService` - 配图管理
  - [ ] `UserCaseNoteService` - 用户答题记录
- [ ] 创建Controller层（4个）
  - [ ] `SystemDesignCaseController` - 管理员案例管理API
  - [ ] `CaseResourceController` - 学习资源API
  - [ ] `CaseSolutionController` - 参考答案API
  - [ ] `UserCaseNoteController` - 用户答题API
- [ ] 实现管理员API（12个端点）
  - [ ] `GET/POST/PUT/DELETE /api/admin/system-design-cases`
  - [ ] `POST /api/admin/system-design-cases/{caseId}/resources`
  - [ ] `DELETE /api/admin/case-resources/{id}`
  - [ ] `GET/POST/PUT/DELETE /api/admin/system-design-cases/{caseId}/solutions`
  - [ ] `POST /api/admin/case-solutions/{solutionId}/diagrams`
  - [ ] `DELETE /api/admin/solution-diagrams/{id}`
- [ ] 实现用户端API（5个端点）
  - [ ] `GET /api/system-design-cases` - 案例列表
  - [ ] `GET /api/system-design-cases/{id}` - 案例详情
  - [ ] `GET /api/system-design-cases/{caseId}/solutions` - 参考答案列表
  - [ ] `GET /api/case-solutions/{id}` - 参考答案详情
  - [ ] `GET/POST /api/system-design-cases/{caseId}/my-note` - 用户答题记录

**前端开发** (4-5小时):
- [ ] 创建管理员案例管理页面 `/admin/system-design-cases`
  - [ ] 两栏布局（左侧案例列表 + 右侧详情）
  - [ ] 右侧上部：案例详情编辑
    - [ ] 标题、描述、难度、公司标签、关联知识
    - [ ] 学习资源管理（视频、文章）
  - [ ] 右侧下部：参考答案管理
    - [ ] 支持多个参考答案（下拉选择）
    - [ ] 6个Tab（需求、实体、API、数据流、架构、深入）
    - [ ] Markdown编辑器 + 图片上传
- [ ] 创建用户案例浏览页面 `/system-design/cases`（查看模式）
  - [ ] 两栏布局（左侧案例列表 + 右侧详情）
  - [ ] 右侧上部：案例详情（标题、描述、难度、学习资源）
  - [ ] 右侧下部：参考答案
    - [ ] 支持选择不同方案（方案A、方案B等）
    - [ ] 6个Tab展示参考答案
    - [ ] Markdown渲染 + 图片展示
  - [ ] "编辑我的答案"按钮

**测试验证** (包含在开发中):
- [ ] 管理员可以创建案例（标题、描述、难度等）
- [ ] 管理员可以为案例添加学习资源（视频、文章）
- [ ] 管理员可以为案例添加参考答案（支持多个方案）
- [ ] 管理员可以为每个参考答案填写6个步骤内容
- [ ] 管理员可以为参考答案上传配图
- [ ] 用户可以浏览所有案例
- [ ] 用户可以查看案例详情（描述、学习资源）
- [ ] 用户可以查看多个参考答案（切换方案）
- [ ] 所有Markdown内容正确渲染

---

### Phase 5.3: 用户答题功能（中优先级）

**预计工时**: 8-10小时

#### 任务清单

**前端开发** (6-7小时):
- [ ] 创建用户答题编辑页面 `/system-design/cases/{id}/edit`（编辑模式）
  - [ ] 左右两栏对比布局
  - [ ] 左侧：参考答案（只读，6个Tab）
    - [ ] 支持选择不同方案
    - [ ] Markdown渲染 + 图片展示
  - [ ] 右侧：我的答案（可编辑，6个Tab）
    - [ ] 6个步骤Markdown编辑器
    - [ ] 架构图上传组件
    - [ ] 要点总结编辑器
  - [ ] 保存/取消按钮
  - [ ] 自动保存功能（可选）
- [ ] 实现架构图上传功能
  - [ ] 图片上传组件
  - [ ] 图片预览
  - [ ] 文件类型和大小验证
- [ ] 实现答题记录保存
  - [ ] UPSERT逻辑（一个用户对一个案例只能有一条记录）
  - [ ] 保存成功提示
  - [ ] 返回查看模式

**后端开发** (2-3小时):
- [ ] 实现架构图上传API
  - [ ] `POST /api/system-design-cases/{caseId}/upload-diagram`
  - [ ] 文件类型验证（图片格式）
  - [ ] 文件大小限制
  - [ ] 云存储集成或本地存储

**测试验证** (包含在开发中):
- [ ] 用户可以进入编辑模式
- [ ] 左侧参考答案正确显示（只读）
- [ ] 右侧我的答案可以编辑（6个步骤）
- [ ] 架构图上传成功
- [ ] 要点总结保存成功
- [ ] 答题记录正确保存（UPSERT）
- [ ] 保存后返回查看模式
- [ ] 再次进入编辑模式，答案正确加载

---

### Phase 5.4: 高级功能（低优先级）

**预计工时**: 6-8小时

#### 任务清单

**功能增强** (4-5小时):
- [ ] 关联知识点跳转
  - [ ] 案例详情页显示关联的Focus Area
  - [ ] 点击Focus Area跳转到基础知识页面
- [ ] 学习进度追踪（可选）
  - [ ] 记录用户查看的案例
  - [ ] 记录用户完成答题的案例
  - [ ] 学习进度统计
- [ ] 案例推荐（可选）
  - [ ] 基于用户学习进度推荐案例
  - [ ] 基于难度推荐案例
  - [ ] 基于关联知识推荐案例

**UI/UX优化** (2-3小时):
- [ ] 优化案例列表显示（卡片式布局）
- [ ] 优化参考答案切换交互
- [ ] 优化编辑模式布局（响应式）
- [ ] 添加快捷键支持（保存、切换Tab等）

**测试验证** (包含在开发中):
- [ ] 关联知识点跳转正确
- [ ] 学习进度正确记录
- [ ] 案例推荐算法准确
- [ ] UI/UX优化符合预期

---

## 三、技术实施细节

### 3.1 数据库Migration策略

**Migration V11**: `V11__create_system_design_tables.sql`

```sql
-- 1. 案例主表
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

-- 2-5: 其他4个表...
```

**注意事项**:
- ✅ 所有外键使用CASCADE删除
- ✅ JSON字段使用TEXT类型（MySQL 5.7+兼容）
- ✅ 添加必要索引（skill_id, difficulty_rating, case_id, solution_id）
- ✅ 设置合适的字符集（utf8mb4）

### 3.2 API设计规范

**管理员API前缀**: `/api/admin/system-design-cases`

**用户端API前缀**: `/api/system-design-cases`

**关键规则**:
- ✅ RESTful风格
- ✅ 分页参数：`page`, `size`（默认20）
- ✅ 级联删除：删除案例时自动删除resources、solutions、diagrams、user_notes
- ✅ UPSERT逻辑：user_case_notes使用UNIQUE约束

### 3.3 前端组件复用

**复用Phase 2/4组件**:
- `MarkdownEditor.vue` - Markdown编辑器（用于6个步骤）
- `MarkdownPreview.vue` - Markdown渲染（用于查看模式）
- `DifficultyBadge.vue` - 难度标签
- `TagInput.vue` - 标签输入（公司标签、关联知识）

**新增组件**:
- `CaseDetailPanel.vue` - 案例详情面板
- `CaseSolutionPanel.vue` - 参考答案面板（6个Tab）
- `UserAnswerEditor.vue` - 用户答案编辑器（6个Tab + 架构图 + 要点总结）
- `SolutionSelector.vue` - 参考答案选择器（下拉列表）
- `DiagramUpload.vue` - 架构图上传组件

### 3.4 性能优化策略

**后端优化**:
- [ ] 案例列表分页加载（避免一次加载所有案例）
- [ ] 参考答案按需加载（切换方案时才查询）
- [ ] 配图懒加载
- [ ] 使用索引优化查询（idx_skill_id, idx_case_id, idx_solution_id）

**前端优化**:
- [ ] 参考答案切换时显示加载状态
- [ ] Tab切换时避免重复请求
- [ ] 图片懒加载
- [ ] Markdown渲染缓存

---

## 四、质量保证

### 4.1 测试计划

**单元测试**:
- [ ] Service层测试（5个Service，每个3-5个测试用例）
- [ ] Repository层测试（CRUD + 级联删除验证）

**集成测试**:
- [ ] API端点测试（17个端点，每个2-3个测试用例）
- [ ] 级联删除测试（删除案例 → 验证resources、solutions、diagrams、user_notes也被删除）
- [ ] UNIQUE约束测试（user_case_notes的UNIQUE(case_id, user_id)）

**前端测试**:
- [ ] 组件渲染测试
- [ ] 用户交互测试（点击、输入、提交）
- [ ] Markdown渲染测试

**手工测试**:
- [ ] 管理员功能完整测试（按Phase 5.2清单）
- [ ] 用户功能完整测试（按Phase 5.2-5.3清单）
- [ ] 边界测试（空数据、长文本、特殊字符等）
- [ ] 性能测试（加载时间、响应时间）

### 4.2 代码审查

**审查要点**:
- [ ] API前缀正确（无`/api`重复）
- [ ] axios响应处理正确（无`response.data.data`）
- [ ] DTO字段完整（特别是嵌套对象）
- [ ] 级联删除规则正确
- [ ] Markdown XSS过滤
- [ ] 文件上传验证

### 4.3 文档更新

**需要更新的文档**:
- [ ] `ARCHITECTURE.md` - 添加Phase 5章节
- [ ] `CLAUDE.md` - 添加Phase 5相关guardrails（如果有新的坑）
- [ ] `database/schema.sql` - 更新完整Schema（独立于Flyway）
- [ ] `database/init_data.sql` - 更新初始数据

---

## 五、风险管理

### 5.1 已知风险

**技术风险**:
- **风险1**: 6个步骤字段TEXT类型可能存储超大内容
  - **缓解**: 添加前端字数限制（如每个步骤最多5000字）
  - **缓解**: 后端验证字段长度

- **风险2**: 图片上传文件大小和格式验证不足
  - **缓解**: 前后端双重验证（文件类型、文件大小）
  - **缓解**: 设置合理的文件大小限制（如5MB）

- **风险3**: 多个参考答案切换时可能性能问题
  - **缓解**: 按需加载（切换方案时才查询）
  - **缓解**: 前端缓存已加载的答案

**业务风险**:
- **风险4**: Focus Area清单未提供，基础知识模块无法完成
  - **缓解**: 提前与用户确认Focus Area清单
  - **缓解**: 准备默认的Focus Area清单备用

- **风险5**: 用户答题记录数据量可能很大
  - **缓解**: 添加分页和过滤功能
  - **缓解**: 定期清理长期未访问的数据

### 5.2 应对策略

- **策略1**: 分阶段交付，每个阶段充分测试后再进入下一阶段
- **策略2**: 复用Phase 2/4的架构和组件，降低风险
- **策略3**: 及时更新文档，避免实现与设计脱节
- **策略4**: 遇到问题及时记录到CLAUDE.md

---

## 六、交付标准

### 6.1 功能完整性

**Phase 5.1 交付标准**:
- [ ] 系统设计Skill创建完成
- [ ] 3个Major Categories创建完成
- [ ] Focus Areas导入完成
- [ ] 管理员基础知识管理页面可用
- [ ] 用户基础知识学习页面可用
- [ ] 学习资料CRUD功能正常

**Phase 5.2 交付标准**:
- [ ] 5个新表创建完成，Migration V11执行成功
- [ ] 管理员案例管理页面可用（案例CRUD + 学习资源 + 参考答案）
- [ ] 用户案例浏览页面可用（查看模式）
- [ ] 参考答案多方案支持正常
- [ ] 6个Tab正确显示
- [ ] Markdown渲染正确

**Phase 5.3 交付标准**:
- [ ] 用户答题编辑页面可用（编辑模式）
- [ ] 左右对比布局正常
- [ ] 6个步骤编辑器正常
- [ ] 架构图上传功能正常
- [ ] 要点总结保存成功
- [ ] UPSERT逻辑正确（一个用户对一个案例只能有一条记录）

**Phase 5.4 交付标准**:
- [ ] 关联知识点跳转正常
- [ ] 学习进度追踪正常（如果实现）
- [ ] 案例推荐算法准确（如果实现）
- [ ] UI/UX优化完成

### 6.2 性能标准

- [ ] 案例列表加载时间 < 1秒
- [ ] 参考答案切换响应时间 < 200ms
- [ ] 架构图上传时间 < 3秒（5MB以内）
- [ ] Markdown渲染流畅（无明显卡顿）

### 6.3 安全标准

- [ ] 管理员API需要管理员权限
- [ ] 用户只能管理自己的答题记录
- [ ] Markdown内容XSS过滤
- [ ] 图片上传文件类型和大小验证
- [ ] JSON字段正确序列化/反序列化

---

## 七、时间估算

### 7.1 总体估算

| 阶段 | 预计工时 | 累计工时 |
|------|---------|---------|
| Phase 5.1 - 基础知识模块 | 8-10小时 | 8-10小时 |
| Phase 5.2 - 典型案例基础功能 | 12-15小时 | 20-25小时 |
| Phase 5.3 - 用户答题功能 | 8-10小时 | 28-35小时 |
| Phase 5.4 - 高级功能 | 6-8小时 | 34-43小时 |
| **总计** | **34-43小时** | - |

### 7.2 详细分解

**后端开发**: 15-18小时
- 数据库设计和Migration: 2小时
- 实体类和Repository: 3-4小时
- Service层: 5-6小时
- Controller层: 4-5小时
- 架构图上传API: 1-2小时

**前端开发**: 15-18小时
- 管理员页面: 7-8小时
  - 基础知识管理: 3-4小时
  - 典型案例管理: 4-5小时
- 用户页面: 8-10小时
  - 基础知识学习: 3-4小时
  - 案例浏览（查看模式）: 2-3小时
  - 答题编辑（编辑模式）: 3-4小时

**数据导入**: 2-3小时
- 初始化Skill和Major Categories: 0.5小时
- 导入Focus Areas: 1-1.5小时
- 导入示例案例（可选）: 0.5-1小时

**测试和文档**: 2-4小时
- 单元测试和集成测试: 1-2小时
- 手工测试: 0.5-1小时
- 文档更新: 0.5-1小时

---

## 八、依赖和前置条件

### 8.1 外部依赖

- [ ] 用户提供Focus Area清单（3个大分类下的具体知识点）
- [ ] 用户确认6步骤答题框架是否需要调整
- [ ] 用户确认架构图存储方案（本地 vs 云存储）

### 8.2 技术依赖

- [ ] MySQL 8.0（已有）
- [ ] Spring Boot 3.2+（已有）
- [ ] Vue 3 + Vite（已有）
- [ ] Markdown编辑器组件（已有，Phase 4）
- [ ] 图片上传组件（需新增或集成第三方）

### 8.3 内部依赖

- [ ] Phase 2/4的Learning Stages和Learning Contents表结构（已有）
- [ ] Phase 2/4的MarkdownEditor和MarkdownPreview组件（已有）
- [ ] Phase 3的用户认证和权限控制（已有）

---

## 九、实施检查清单

### 9.1 开始前检查

- [ ] 阅读Phase5设计文档和详细需求
- [ ] 确认用户提供了Focus Area清单
- [ ] 确认架构图存储方案
- [ ] 确认6步骤答题框架无需调整
- [ ] 准备好开发环境（后端、前端、数据库）

### 9.2 每个阶段结束后检查

- [ ] 功能测试通过
- [ ] 代码审查通过
- [ ] 文档更新完成
- [ ] Git commit提交
- [ ] 与用户确认交付成果

### 9.3 最终交付检查

- [ ] 所有功能测试通过
- [ ] 性能标准达标
- [ ] 安全标准达标
- [ ] 文档完整（设计文档、需求文档、交付总结）
- [ ] database/schema.sql更新（独立于Flyway）
- [ ] CLAUDE.md更新（如果有新的guardrails）
- [ ] Git所有commits推送到远程仓库

---

## 十、附录

### 10.1 参考资料

- **HelloInterview**: https://www.hellointerview.com/
- **System Design Primer**: https://github.com/donnemartin/system-design-primer
- **Grokking System Design**: https://www.designgurus.io/
- **ByteByteGo**: https://bytebytego.com/

### 10.2 相关文档

- [Phase5-设计文档.md](./Phase5-设计文档.md) - 详细设计
- [Phase5-详细需求.md](../requirement/Phase5-详细需求.md) - 功能需求
- [Phase5-系统设计学习模块.md](../prompt/Phase5-系统设计学习模块.md) - 原始Prompt
- [Phase4-设计文档.md](./Phase4-设计文档.md) - Phase4参考
- [ARCHITECTURE.md](./ARCHITECTURE.md) - 整体架构

### 10.3 数据库表清单

**新增表** (5个):
1. `system_design_cases` - 案例主表
2. `case_resources` - 学习资源表
3. `case_solutions` - 参考答案表
4. `solution_diagrams` - 配图表
5. `user_case_notes` - 用户答题记录表

**复用表** (4个):
1. `skills` - 系统设计Skill
2. `major_categories` - 3个大分类
3. `focus_areas` - 基础知识Focus Areas
4. `learning_contents` - 基础知识学习资料

---

**文档版本**: v1.0
**创建时间**: 2025-12-25
**创建人**: Austin Xu
**审核状态**: 待审核
