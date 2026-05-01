# OpenSpec Capabilities

> Phase 8+ 的需求变更通过 OpenSpec 工作流管理。
> 每个 capability spec 是可测试的 BDD 行为规范（SHALL + Scenario）。
>
> **查看方式**：
> - CLI：`openspec show <capability-name>`
> - 文件：`openspec/specs/<capability>/spec.md`

---

## Capabilities

### `interview-progress`
**功能**：面试进展看板 — 替换旧仪表盘，聚合所有活跃申请的优先级、进度标签和下一步行动。

**核心行为**：
- `GET /api/job-applications/active-progress` 返回带派生字段的 DTO（priorityLevel / microStageLabel / nextActionLabel 等）
- 优先级规则：OFFER_DEADLINE → STALLED（超时按阶段判断）→ SCHEDULED → WAITING
- 前端路由 `/job-search/dashboard` 渲染 `InterviewProgress.vue`
- 支持三种本地排序（Needs Attention / By Stage / By Time），不触发网络请求

**来自变更**：[`2026-04-28-interview-progress-summary`](../changes/archive/2026-04-28-interview-progress-summary/proposal.md)

---

### `application-tracking`
**功能**：投递来源标记 + 面试轮次结果记录。

**核心行为**：
- `JobApplication.submissionType`：Direct / Referral / RecruiterInbound / Other（默认 Direct）
- 已有记录通过迁移自动回填：有 Referrer 内推联系人 → Referral，其余 → Direct
- `InterviewRecord.result`：Pending / Passed / Failed（默认 Pending）
- Result 不自动修改 `applicationStatus`，两者独立管理
- `ActiveProgressDTO` 包含 `submissionType` 字段（进展看板可见渠道信息）

**来自变更**：[`2026-04-30-application-source-and-round-results`](../changes/archive/2026-04-30-application-source-and-round-results/proposal.md)

---

### `mcp-server` *(spec 待同步)*
**功能**：MCP Server — Claude Desktop / Claude Code 直接查询求职数据。

**核心行为**：
- 3 个只读工具：`list_applications` / `get_application_detail` / `get_active_progress`
- 认证复用 growing JWT（Bearer token），有效期 30 天
- 传输协议：SSE（Spring AI 1.0.x）— `GET /mcp/sse` + `POST /mcp/message?sessionId=X`
- 用户隔离：每个 tool 调用自动 scoped 到 token 对应的 userId

**来自变更**：[`mcp-job-application-tools`](../changes/mcp-job-application-tools/proposal.md)（活跃，尚未归档）

**配置文档**：[`docs/MCP_SETUP.md`](../../docs/MCP_SETUP.md)

---

## 变更索引

| 归档名 | 影响 Capability | 时间 |
|--------|----------------|------|
| [`2026-04-28-interview-progress-summary`](../changes/archive/2026-04-28-interview-progress-summary/) | interview-progress | 2026-04-28 |
| [`2026-04-30-application-source-and-round-results`](../changes/archive/2026-04-30-application-source-and-round-results/) | application-tracking + interview-progress | 2026-04-30 |
| [`mcp-job-application-tools`](../changes/mcp-job-application-tools/) *(活跃)* | mcp-server | 2026-04-30 |

---

## 查询指南

**"这个功能是怎么设计的？"** → 看 `spec.md`（行为契约）

**"为什么要做这个功能？"** → 看对应变更的 `proposal.md`（Why + 背景）

**"技术方案怎么选的？"** → 看对应变更的 `design.md`（Decisions 章节）

**"具体实现了哪些步骤？"** → 看对应变更的 `tasks.md`（TDD 任务列表）
