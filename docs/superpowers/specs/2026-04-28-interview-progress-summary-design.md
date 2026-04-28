# Interview Progress Summary — Design Spec

> **Date**: 2026-04-28
> **Author**: brainstormed with Claude Code (superpowers:brainstorming)
> **Status**: Approved — ready for implementation
> **OpenSpec change**: [`openspec/changes/interview-progress-summary/`](../../../openspec/changes/interview-progress-summary/)
> **Implementation tracking**: see `tasks.md` in the OpenSpec change

---

## 1. 问题与目标

求职者每天最高频的问题是"现在面试都进展到哪了，今天最该跟进什么"。Phase 7 提供的现有入口 `/job-search/dashboard` 只显示 6 张静态计数卡片（简历数、公司数、申请数等），价值低；用户必须进入 `JobApplicationList` 才能逐条查看 active 职位的状态，无法一眼看出 stalled、offer 临近、需要安排面试等信号。

**目标**：用一个聚合的"面试进展看板"替换现 dashboard，把"今天该处理什么"直接放到求职模块入口。

## 2. Brainstorming 过程中确定的关键决策

| # | 问题 | 选项 | 决定 |
|---|---|---|---|
| Q1 | "Active 职位" 范围 | A 进行中 / B 进行中+Offer / C 包含 NotApplied | **A + B**：`Applied` + `PhoneScreen` + `Onsite` + `Offer` |
| Q2 | 主视图布局 | Kanban / Table / 卡片+stepper | **C 卡片 + 4 步进度条**，整卡可点跳详情 |
| Q3 | 卡片信息密度 | 极简 / +next-action / +stalled / +micro-stage | **D 全选**：基础 + next-action + stalled 警告 + 微观 stage |
| Q4 | 默认排序 / 分组 | 按需关注 / 按 stage / 按时间 | **D**：默认 "Needs Attention"（offer 截止 → stalled → 其他），顶部 toggle 切换其他模式 |
| Q5 | 页面位置 | 替换 dashboard / 新路由 / 加 tab | **A 直接替换 `/job-search/dashboard`**；旧 `JobSearchDashboard.vue` 删除 |

## 3. 视觉设计

最终 mockup 文件保留在 `.superpowers/brainstorm/30588-1777408629/content/final-design.html`（已 gitignore），关键视觉元素：

- **页头**：标题 + 3 个统计 stat box（进行中 / 需要跟进 / Offer 待决定）+ 排序 segmented toggle + "显示已结案" 按钮
- **卡片网格**（lg 双列、移动端单列），每张卡含：
  - 左侧色条：🟢 Offer 临近 · 🔴 Stalled · 普通卡无色条
  - 顶部：公司名（粗）/ 职位 / micro stage 斜体小字 / 状态 pill（右上）
  - 中部：4 步水平进度条 `Apply · Phone · Onsite · Offer`，已完成步与当前步亮色，当前步带圆点
  - 底部：`已 N 天 · 上次更新 Xd` + 右侧 next-action chip
  - 右上角徽章：仅在 STALLED 或 OFFER_DEADLINE 时显示

## 4. 架构

```
┌─ 前端 ────────────────────────────────────────────────┐
│  /job-search/dashboard (路由 name 保留)               │
│    InterviewProgress.vue ←── 替换 JobSearchDashboard   │
│      ├─ ProgressSummaryHeader.vue                      │
│      └─ ProgressCard.vue (× N)                         │
└────────────────────────────────────────────────────────┘
                           │
                           ▼  GET /api/job-applications/active-progress
┌─ 后端 ────────────────────────────────────────────────┐
│  JobApplicationController.getActiveProgress(userId)    │
│    └─ JobApplicationService.getActiveProgress(userId)  │
│         ├─ Repo.findActiveByUserIdWithStagesAndRecords()
│         └─ computeProgress(app) — 派生所有字段         │
└────────────────────────────────────────────────────────┘
```

**复用**：`JobApplication` / `InterviewStage` / `InterviewRecord` / `Company` 实体，无 DB schema 变更。

**核心原则**：派生字段（stalled / priority / microStageLabel / nextActionLabel）全部在后端计算，前端只做展现 + 简单 sort 切换。

## 5. 后端契约

### 5.1 端点

```
GET /api/job-applications/active-progress
Authorization: Bearer <jwt>
→ 200 OK
[
  ActiveProgressDTO,
  ...
]
```

### 5.2 DTO

```java
public record ActiveProgressDTO(
    Long id, Long companyId, String companyName, String positionName,
    String applicationStatus,           // Applied / PhoneScreen / Onsite / Offer
    Integer macroStageStep,             // 1..4
    String microStageLabel,             // 已格式化, e.g. "Onsite · 2/4 (Tech Round 1 已完成)"
    Integer daysSinceApplied,
    Integer daysSinceLastUpdate,
    PriorityLevel priorityLevel,        // OFFER_DEADLINE / STALLED / SCHEDULED / WAITING
    NextActionType nextActionType,
    String nextActionLabel,             // e.g. "📅 Tech Round 2 · 5/2"
    LocalDate nextActionDate
) {}
```

### 5.3 业务规则

**`daysSinceApplied`**：解析 `statusHistory` JSON 找第一次 `Applied` 时间戳；fallback 到 `createdAt`。

**Stalled 阈值**（按 stage 不同）：
| Stage | `daysSinceLastUpdate >` |
|---|---|
| Applied | 7 |
| PhoneScreen / Screening | 10 |
| Onsite / Interviewing | 14 |
| Offer | 不参与 stalled（用 OFFER_DEADLINE 维度） |

**`daysSinceLastUpdate` 计算细节**：取 `max(statusUpdatedAt, latestInterviewRecord.updatedAt)`。这样在 Interviewing 阶段，每次面试记录的更新都算"进展"，不会因为 macro 状态没变就误判 stalled。

**Priority 优先级**（top-down，第一个匹配胜出）：
1. `OFFER_DEADLINE`（绿）— `applicationStatus=Offer` AND `offerDeadline ≤ 14d`
2. `STALLED`（红）— 见上面阈值表
3. `SCHEDULED`（橙）— 存在 `InterviewRecord` 有未来 ≤ 7d 的日期
4. `WAITING`（灰）— 其他

**默认排序**：`priorityLevel.ordinal()` ASC，然后 `daysSinceApplied` DESC。

**N+1 规避**：用 `JOIN FETCH` 单次查询 application + stages + records。

## 6. 前端

### 6.1 组件树

```
InterviewProgress.vue (页面)
├── ProgressSummaryHeader.vue
└── ProgressCard.vue (× N)
```

### 6.2 状态

```js
const apps = ref([])
const loading = ref(true)
const error = ref(null)
const sortMode = ref('attention')  // 'attention' | 'stage' | 'time'
const showClosed = ref(false)

const displayed = computed(() => {
  // 'attention' 用后端给的顺序
  // 'stage' / 'time' 在前端简单排序
})
const summary = computed(() => ({
  inProgress: apps.value.length,
  stalled: apps.value.filter(a => a.priorityLevel === 'STALLED').length,
  offerPending: apps.value.filter(a => a.priorityLevel === 'OFFER_DEADLINE').length
}))
```

### 6.3 API 客户端

```js
// frontend/src/api/jobApplicationApi.js
// baseURL='/api', 不加 /api 前缀; 拦截器已 unwrap response.data
getActiveProgress() {
  return apiClient.get('/job-applications/active-progress')
}
```

### 6.4 路由

```js
// frontend/src/router/index.js
{
  path: '/job-search/dashboard',
  name: 'JobSearchDashboard',     // 保留 name
  component: () => import('../views/job-search/InterviewProgress.vue')
}
```

### 6.5 卡片点击行为

```js
router.push({ name: 'JobApplicationList', query: { id: app.id } })
```

## 7. 边界情况

| 情况 | 处理 |
|---|---|
| 用户没有 active 职位 | 空状态：插画 + "暂无进行中的申请" + "去添加" 按钮跳 `/job-search/applications` |
| `statusHistory` null / 损坏 | 后端 catch + log warn，fallback 到 `createdAt` |
| status=Offer 但 `offerDeadline` 缺失 | priority 降级 `WAITING`，不显示截止 chip |
| `InterviewStage[]` 空 | `microStageLabel` 用按 status 的固定文案 |
| `InterviewRecord[]` 都没未来日期 | `nextActionType` = `WAITING_FEEDBACK` 或 `NONE` |
| 网络/500 | 顶部红条 + 重试按钮，不清空已有数据 |
| 时区 | 沿用现有模块策略，服务器时区 |

## 8. 测试策略

**后端**（JUnit5 + Mockito + AssertJ）：
- `JobApplicationServiceProgressTest` 表驱动覆盖 stalled 阈值边界、priority 优先级、`statusHistory` 解析三种情况、`microStageLabel` 三种情况
- `JobApplicationControllerActiveProgressIT` 用 MockMvc 测 401 / 200 / status 过滤 / 用户隔离 / 单次 SELECT（Hibernate Statistics 断言）

**前端**（Vitest + Vue Test Utils）：
- `ProgressCard.spec.js` 4 种 priority 渲染 / stepper 位置 / 点击 router.push 路径正确（防 Phase 7 Mistake #13）
- `InterviewProgress.spec.js` mock api 测 sortMode 切换 / 空状态 / loading/error
- `jobApplicationApi.spec.js` 验证调 `/job-applications/active-progress`（无 `/api` 前缀，防 Mistake #7）

**E2E**（如有 Playwright）：登录 → dashboard → 看到卡片 → 点击 → 跳转 detail 页 query 含 id。

**目标**：80%+ 覆盖（项目硬性要求）。

## 9. 迁移与回滚

无数据迁移。代码部署：
1. 后端合并新 endpoint + service + DTO + repo
2. 前端合并新组件 + 更新 router；`git rm JobSearchDashboard.vue`
3. 部署后人工 smoke：登录 → `/job-search` → 看板 → 点卡片跳详情
4. 回滚：单次 `git revert` 即可（无 schema 变更）

## 10. 实施

实施任务清单见 OpenSpec change：
- `openspec/changes/interview-progress-summary/proposal.md` — Why & What
- `openspec/changes/interview-progress-summary/design.md` — 技术决策（D1-D7）
- `openspec/changes/interview-progress-summary/specs/interview-progress/spec.md` — 16 项 Requirements
- `openspec/changes/interview-progress-summary/tasks.md` — 11 组 ~40 个 checkbox 任务

运行 `/opsx:apply` 进入实施。

## 11. 待解决问题

- 是否要在 header 加 "快速添加申请" 按钮？V1 不加，看用户反馈。
- 是否要为长期保留的 "Offer 已 declined" 提供专门归档视图？V1 用 toggle 看。
- stalled 阈值是否要做用户级配置？V1 全局硬编码常量，未来可移到 `application.properties`。
