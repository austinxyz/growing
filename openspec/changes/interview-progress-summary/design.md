## Context

Phase 7（求职管理模块）已上线，包含简历、项目经验、公司、职位申请和面试 stage 全流程的数据模型。当前求职模块入口 `/job-search/dashboard` 只是 6 张静态计数卡片，对实际"今天该跟进什么"几乎无帮助。用户必须进入 `JobApplicationList` 才能看每个职位的状态，且没有 stalled / offer 截止 / 即将到来面试 等聚合视图。

现有数据已经足够支撑一个面试进展看板：
- `JobApplication.applicationStatus` 状态机（NotApplied / Applied / PhoneScreen / Onsite / Offer / Rejected）
- `JobApplication.statusHistory`（JSON）记录状态变迁时间戳
- `JobApplication.statusUpdatedAt` 上次状态变化时间
- `JobApplication.offerDeadline` offer 截止日期
- `InterviewStage[]` 每个职位的自定义阶段列表（含 stageOrder）
- `InterviewRecord[]` 已发生 / 已安排的面试记录

约束：
- 不引入新依赖；不变更数据库 schema
- 沿用项目现有模式（Spring Boot + JPA + Lombok + JSON 字段、Vue 3 + Composition API + Tailwind + axios 拦截器自动 unwrap response.data）
- 复杂业务逻辑放后端，前端仅做展现和简单排序

## Goals / Non-Goals

**Goals:**
- 一眼看出当前所有 active 申请的状态分布
- 突出"需要关注"的项目：offer 临近、stalled、即将到来的面试
- 从看板单点跳到具体职位详情，不复制详情视图功能
- 后端聚合 + 派生字段计算，避免前后端逻辑漂移
- 单次 join-fetch 查询，避免 N+1

**Non-Goals:**
- 不在看板上做 inline 编辑（如修改状态、添加面试记录）—— 这些走详情页
- 不实现拖拽改 stage（Kanban 视觉化已被否决，选了 stepper 卡片）
- 不引入新的实体或字段；不引入用户级偏好持久化（排序模式只在 session 内保留）
- 不实现"已关闭" 历史归档视图——只通过 toggle 临时显示 Rejected/Declined
- 不引入 i18n —— 所有文案中文硬编码（与项目现有风格一致）

## Decisions

### D1: 派生字段在后端计算，不在前端
**选项**：
- A. 后端只返回原始字段，前端算 stalled / priority / microStageLabel
- B. 后端返回完全派生好的 DTO，前端纯渲染（**采纳**）

**理由**：B 让 stalled 阈值、priority 优先级、文案格式可测试 + 集中维护。前端只做 sort 切换和渲染，避免规则漂移。代价：响应负载略大（含格式化字符串），但都是短字符串，可忽略。

### D2: 新建专用端点 `GET /api/job-applications/active-progress`，不复用 `GET /api/job-applications`
**理由**：
- 列表端点要回所有字段（含 qualifications / responsibilities / notes 等大文本），看板用不到，浪费带宽
- 派生字段（stalled、priority）需要 controller 层后处理，混在通用端点会污染；专用端点单一职责

**代价**：多一个接口面。Mitigation：DTO 用 `record` 类型，体量小。

### D3: stage-aware stalled 阈值（7 / 10 / 14 天），而非统一阈值
**理由**：不同阶段等待是不同的——recruiter 7 天没回 vs onsite 后 14 天没收到反馈，含义和紧迫度不一样。统一阈值会误报或漏报。

**阈值依据**（行业经验值，可后续做成 `application.properties` 配置）：
- Applied → PhoneScreen：7 天（recruiter 通常 1 周内首次响应）
- PhoneScreen → Onsite：10 天（hiring manager + scheduler 走流程时间）
- Onsite → Offer/Reject：14 天（debrief + offer approval）
- Offer 状态：不用 stalled 维度，用 OFFER_DEADLINE 优先级

### D4: 4 步 macro stepper + 文字 micro-stage，不是动态步数
**理由**：尽管 `InterviewStage[]` 每个职位的阶段数量不同（3-7 步常见），固定 4 步 macro stepper（Apply / Phone / Onsite / Offer）保证不同卡片视觉一致。具体细节用文字 micro stage 表达（"Onsite · 2/4 (Prashanth 已完成)"）。代价：损失部分进度精度，但换来扫视效率。

### D5: 时间起点用 `statusHistory[Applied].timestamp`，fallback `createdAt`
**理由**：用户可能先把 NotApplied 状态的职位收藏好几天才投，"已花时间"应该从真正投递起算。但 `statusHistory` 是手动维护的 JSON 字段，可能空或损坏，必须有 fallback。

### D6: 默认排序由后端给出
**理由**：默认 `Needs Attention` 涉及 priority 优先级 + 二级 daysSinceApplied，后端一次算清，前端别再排（避免规则不一致）。`By Stage` / `By Time` 是简单单字段排序，前端做即可。

### D7: 替换原 `JobSearchDashboard.vue`，不并存
**理由**：原页 6 张计数卡（resumeCount / projectCount / companyCount / managementCount / jobApplicationCount / 快捷操作）：
- 前 5 个计数已在各子页头部存在（用户访问子页就能看到）
- "快捷操作" 卡是 3 个跳转链接，价值低
- 并存会让用户困惑为什么有两个 dashboard

并 `git rm` 旧组件文件；保留 router 中 `/job-search/dashboard` 路径和 `name: 'JobSearchDashboard'`，仅替换 component 引用，避免破坏外部 named-route 调用。

## Risks / Trade-offs

- **风险**：`statusHistory` JSON 字段历史数据可能不规范（不同时期写入格式不统一）。
  → **Mitigation**：解析时 try/catch + fallback 到 `createdAt`；后端 log warn 但不抛异常。

- **风险**：N+1 查询（每个 application 单独查 stages 和 records）。
  → **Mitigation**：在 Repository 加 `findActiveByUserIdWithStagesAndRecords()` 用 `@EntityGraph` 或 JPQL `JOIN FETCH`。集成测验证发出的 SQL 数量。

- **风险**：用户切换 sortMode 时如果重新调接口会闪烁。
  → **Mitigation**：仅在初次加载和"显示已结案"切换时调接口；sortMode 切换在前端缓存的数组上排序。

- **风险**：4 步 macro stepper 对阶段数极少（如 startup 简化流程：Apply → Onsite 直接）的职位显得不准确。
  → **Trade-off**：接受。这种边界情况下 stepper 仍能用，文字 micro-stage 可以补充准确细节。

- **风险**：stalled 阈值硬编码不灵活。
  → **后续工作**：先硬编码为常量，记下 TODO，未来可移到 `application.properties`。

- **风险**：删除 `JobSearchDashboard.vue` 移除 5 个计数指标（resumeCount 等）。
  → **Mitigation**：在 `JobApplicationList`、`ResumeManagement` 等子页顶部已有等价计数。如用户反馈缺失，单独 issue 跟进，不阻塞本 change。

## Migration Plan

无数据迁移。代码部署步骤：
1. 后端：合并新 endpoint + service 方法 + DTO + repository 查询
2. 前端：合并新组件 + 更新 router；移除 `JobSearchDashboard.vue`
3. 部署后人工 smoke：登录 → 进 `/job-search` → 看到看板而非旧 dashboard → 点一张卡 → 跳转到对应详情
4. 回滚：单次 git revert 即可（无 schema 变更，无破坏性改动）

## Open Questions

- 是否要在 header 加"快速添加申请" 按钮？（V1 不加，看用户反馈）
- 是否要为长期保留的"Offer 已 declined" 提供专门的归档视图？（V1 用 toggle 看，未来可加专门页面）
- stalled 阈值是否要做用户级配置？（V1 全局硬编码）
