## Why

求职者日常最高频的问题是"现在面试都进展到哪了，今天最该跟进什么"。现有 `/job-search/dashboard` 只显示 6 张静态计数卡片（简历数、公司数、申请数等），这些信息在各子页头部已可见，价值低；用户必须进入 `JobApplicationList` 才能逐条查看 active 职位的状态，无法一眼看出 stalled、offer 临近、需要安排面试等信号。需要一个聚合的进展看板，把"今天该处理什么"直接放到求职模块入口。

## What Changes

- 新增**面试进展看板**作为 `/job-search/dashboard` 的内容（替换原 `JobSearchDashboard.vue`）
- 卡片网格 + 4 步阶段进度条（Apply / Phone / Onsite / Offer）展示 active 职位（`applicationStatus` ∈ Applied / PhoneScreen / Onsite / Offer）
- 每张卡片显示：公司、职位、micro stage（如 "Onsite · 2/4"）、累计天数、上次更新天数、next-action chip、stalled / offer-deadline flag
- 新增后端聚合端点 `GET /api/job-applications/active-progress`，返回 `ActiveProgressDTO[]`，由后端计算所有派生字段（stalled 判定、priority、microStageLabel、nextActionLabel）
- 前端排序切换：`Needs Attention`（默认，后端给序）/ `By Stage` / `By Time`，以及 "显示已结案" toggle 用于复盘
- **BREAKING（仅 UI）**：原 `JobSearchDashboard.vue`（6 张计数卡）删除；统计计数功能并入 `JobApplicationList` 顶部
- 不修改数据库 schema，复用现有 `JobApplication` / `InterviewStage` / `InterviewRecord` / `Company` 实体

## Capabilities

### New Capabilities
- `interview-progress`: 求职模块的进展看板能力——把当前所有 active 申请按"需要关注"的优先级展示在一个页面，包含 stalled 检测、offer deadline 倒计时、micro stage 进度，并允许从卡片直接跳转到对应的 `JobApplicationList` 详情视图。

### Modified Capabilities
<!-- 无：本项目此前未在 openspec/specs/ 下建立任何 capability，本次为首个 spec。 -->

## Impact

**前端**:
- 删除 `frontend/src/views/job-search/JobSearchDashboard.vue`
- 新增 `frontend/src/views/job-search/InterviewProgress.vue`（页面）
- 新增 `frontend/src/components/jobSearch/ProgressSummaryHeader.vue`、`ProgressCard.vue`
- `frontend/src/api/jobApplicationApi.js` 加 `getActiveProgress()` 方法
- `frontend/src/router/index.js` 把 `/job-search/dashboard` 的 component 改成 `InterviewProgress.vue`

**后端**:
- `JobApplicationController` 加 `GET /api/job-applications/active-progress`
- `JobApplicationService` 加 `getActiveProgress(userId)` 与 `computeProgress(application)` 私有方法（含 stalled / priority / microStage / nextAction 计算）
- `JobApplicationRepository` 加 `findActiveByUserIdWithStagesAndRecords(userId)` join-fetch 查询，避免 N+1
- 新增 DTO `ActiveProgressDTO`（record 类型）+ 两个 enum（`PriorityLevel`、`NextActionType`）

**数据库**: 无 schema 变更。

**测试**: 后端 service 单测 + controller 集成测；前端组件测 + API 客户端测。目标 80%+ 覆盖。

**依赖 / 风险**:
- 解析 `statusHistory` JSON 字段的健壮性（已在 stalled 边界条件中覆盖）
- N+1 查询风险（用 join-fetch 仓库方法规避）
- 时区一致性（沿用现有模块的服务器时区策略，不引入 ZoneId）
