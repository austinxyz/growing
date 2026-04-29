## Why

求职者需要区分通过内推还是网站直投投递的职位，以便：(a) 在卡片/列表上一眼看出投递渠道，(b) 跟踪具体内推人是谁，(c) 未来做"内推 vs 直投" 成功率分析。当前 `JobApplication` 实体没有 submission source 字段，只有 `JobApplicationReferral` 这个 junction 表把申请关联到 network 里的具体联系人——但用户可能知道是内推却暂时没把内推人加进 network，导致信息缺失。

同时，每一轮面试需要能记录"通过 / 未通过 / 等待反馈"。当前 `InterviewRecord` 只有 1-5 分制的 performance 自评（overallPerformance、technicalDepth 等），没有简单的 pass/fail 结果字段。这导致用户回顾时无法快速看出"哪几轮通过了，卡在哪一轮"，复盘成本高。

## What Changes

- 在 `JobApplication` 实体加 `submissionType` 字段（枚举：`Direct` / `Referral` / `RecruiterInbound` / `Other`）
- `submissionType=Referral` 时，复用现有 `JobApplicationReferral` 关联表记录具体内推人（不新增字段，避免重复）
- 在 `InterviewRecord` 实体加 `result` 字段（枚举：`Pending` / `Passed` / `Failed`），默认 `Pending`
- 后端：DTO 加新字段；service/controller 透传；新增数据库 migration（V1 schema 新增列，已有数据默认 `Direct` / `Pending`）
- 前端：申请编辑表单加 submission type 选择器；面试记录编辑表单加 result 选择器；申请列表卡片显示 submission source 标签
- 面试进展看板（`/job-search/dashboard`）卡片也显示 submission source 小标签（"内推" / "直投"），让最高频视图也能看到

## Capabilities

### New Capabilities

- `application-tracking`: 关于 `JobApplication` 和 `InterviewRecord` 携带的元数据契约——submission source（投递渠道）、内推人关联、per-round result（每轮面试结果）。提供数据模型契约让 UI 与未来的成功率分析共享一致定义。

### Modified Capabilities

- `interview-progress`: 已有 dashboard capability。本次变更为 `ActiveProgressDTO` 加 `submissionType` 字段，让卡片显示投递渠道。

## Impact

**数据库**:
- 新 migration `V18_add_submission_type_and_round_result.sql`：
  - `ALTER TABLE job_applications ADD COLUMN submission_type VARCHAR(50) NOT NULL DEFAULT 'Direct'`
  - `ALTER TABLE interview_records ADD COLUMN result VARCHAR(20) NOT NULL DEFAULT 'Pending'`
- 已有数据：`submission_type` 全部回填 `Direct`，`result` 全部回填 `Pending`（后续用户手动改）

**后端**:
- `JobApplication` entity + JPA mapping 新字段
- `InterviewRecord` entity + JPA mapping 新字段
- `JobApplicationDTO` / `ActiveProgressDTO` / `InterviewRecordDTO` 加新字段
- `JobApplicationService.toActiveProgressDTO` 透传 `submissionType`
- 不需要新 endpoint：现有 PUT `/api/job-applications/{id}` 和 PUT `/api/interview-records/{id}` 已经支持整个 DTO 更新，只需 DTO 加字段

**前端**:
- `frontend/src/views/job-search/JobApplicationList.vue`：申请编辑表单加 submission type 下拉选择 + 列表卡片 submission source 标签
- `frontend/src/components/job-search/ProgressCard.vue`：卡片右上角 status pill 旁边加小 chip "内推" / "直投"
- 面试记录编辑表单（位于 JobApplicationList.vue 同一文件内的 modal）加 result 下拉

**测试**:
- 后端：service 测试加 cases (submission type roundtrip、result roundtrip)；controller 测试加字段验证
- 前端：ProgressCard.spec.js 加 submission type chip 渲染测试

**风险**:
- DB migration 默认值兼容性：所有现存数据强制 `Direct` / `Pending` — 准确性取决于用户手动修正。可接受。
- 老数据中 `JobApplicationReferral` 表已经有 referrer 关联但 `submissionType=Direct` 的不一致情况：可在 migration 里加 `UPDATE job_applications SET submission_type='Referral' WHERE id IN (SELECT DISTINCT job_application_id FROM job_application_referrals)` 自动同步。

**非目标（V2）**:
- "内推 vs 直投 成功率" 报表/统计页面：本次只加数据，分析视图留作后续 change（数据积累后再做）
- "Result=Failed 时自动把 applicationStatus 推进到 Rejected" 的联动逻辑：留给用户手动控制，避免误操作
