## Context

Phase 7 (求职管理) 已上线了完整的 `JobApplication` / `InterviewStage` / `InterviewRecord` / `JobApplicationReferral` 数据链。其中 `JobApplicationReferral` 是 junction 表，把一个申请关联到 network 里的具体 person（roleType=Recruiter/Referrer）。但目前 `JobApplication` 本身没有"投递方式"字段——用户区分内推/直投只能间接通过 referral 表是否有 Referrer 类型的关联。这有两个问题：

1. 用户可能知道某个职位是内推但没把内推人加进 network 表，导致 referral junction 为空 → 系统误判为直投
2. 列表/看板上要判断渠道，每张卡片都要再 join 一次 referral 表，性能 + 复杂度都不必要

`InterviewRecord` 有 `overallPerformance` 等 1-5 分自评字段，但没有"通过/未通过"这种二元结果。复盘时用户得读自评数字推断结果，效率低。

约束：
- 兼容现有数据（不能让现存的 25 条 JobApplication 和 N 条 InterviewRecord 失效）
- 不引入新 endpoint，复用现有 PUT 接口（用户已经习惯）
- 中英文 status 兼容性（Phase 8 已经做了 normalization）— 这里 submissionType / result 一开始就只用英文 canonical，UI 层做中文展示

## Goals / Non-Goals

**Goals:**
- `JobApplication.submissionType` 显式记录投递渠道，独立于 referral 表是否有关联人
- `InterviewRecord.result` 二元/三态结果，方便快速复盘和未来分析
- 现有数据平滑迁移：default 值合理（Direct / Pending）+ 自动从 `JobApplicationReferral` 推断 submissionType=Referral 的数据
- 在面试进展看板和申请列表两处 UI 显示 submission source（高频视图也能看到）

**Non-Goals:**
- 成功率统计/报表（数据积累后另起 change）
- 自动联动：result=Failed 不自动推 applicationStatus 到 Rejected（用户手动控制）
- 多个内推人的优先级或权重（junction 表已支持多个 referrer，UI V1 平铺展示即可）
- 新 REST endpoint（复用现有 PUT/POST 即可承载新字段）

## Decisions

### D1: `submissionType` 在 `JobApplication` 上加字段，不依赖 referral junction
**选项**：
- A. 不加字段，从 `JobApplicationReferral` 表里 `EXISTS roleType='Referrer'` 派生
- B. 加 `submissionType` 字符串字段（**采纳**）

**理由**：派生方案在用户没把内推人加进 network 时会误判（典型场景：朋友说"我推你"，用户立刻投了，但还没把朋友加到联系人）。显式字段让用户输入意图与系统判断一致，且避免每次查询都 join。

**值集**：`Direct` / `Referral` / `RecruiterInbound` / `Other`。Other 兜底（招聘会、social media 等）。

### D2: `result` 在 `InterviewRecord` 上，三值枚举，默认 Pending
**选项**：
- A. 二值 (Passed / Failed)，无默认 → 必须填
- B. 三值 (Pending / Passed / Failed)，默认 Pending（**采纳**）

**理由**：用户面试当天就创建 InterviewRecord，但结果可能 1-2 周后才知道。三态让"已记录但还没结果"成为合法状态，符合实际工作流。`WAITING_FEEDBACK` 是面试进展看板的 priority enum，已有；这里的 `result` 是 record 自身属性，语义不同，独立命名。

### D3: 复用现有 PUT 接口，不开新端点
**理由**：`PUT /api/job-applications/{id}` 已经接收完整 DTO，加字段即可。同理 InterviewRecord 的 PUT。新 endpoint 没有价值，只增加测试和文档负担。

### D4: 数据库 migration 自动推断已有 referral 关联
**理由**：用户在 Phase 7 已经手动维护了 `JobApplicationReferral` 关联，那部分数据应该回填 `submissionType='Referral'`。Migration 包含：

```sql
UPDATE job_applications
SET submission_type = 'Referral'
WHERE id IN (
  SELECT DISTINCT job_application_id
  FROM job_application_referrals
  WHERE role_type = 'Referrer'
);
```

不需要 user 手动整理已有数据。其他记录默认 `Direct`，用户后续修正即可。

### D5: 面试进展卡片用小 chip 展示 submission source
**理由**：现有 `ProgressCard` 已经很密集（公司、职位、micro stage、stepper、days、next-action chip）。加 chip 不能挤掉关键信息。位置：top-right 状态 pill 左侧，灰色小 chip "内推" / "直投"。`Direct` / `Other` / 默认值不显示 chip，避免视觉噪声——只在 `Referral` 和 `RecruiterInbound` 时显示。

### D6: result 在 UI 上用 emoji + 中文标签
**理由**：与现有面试卡片风格一致（next-action 已经用 emoji）。

| Result | UI 显示 |
|---|---|
| Pending | （不显示标签，默认） |
| Passed | ✅ 通过 |
| Failed | ❌ 未通过 |

未来如果加 `WaitingFeedback` 可显示 ⏳ 等待反馈（但 V1 暂不需要，等 user 反馈再加）。

## Risks / Trade-offs

- **风险**：DB migration 把已有数据强制设为 `Direct`，对早期通过内推但还没维护 `JobApplicationReferral` 关联的申请会误判。
  → **Mitigation**：D4 的反向回填语句覆盖大多数情况；用户列表上看到错误后可一键改。

- **风险**：result 字段默认 `Pending` 后，老数据中"明显已经过了的面试"全显示 Pending 会造成困惑。
  → **Mitigation**：UI 不为 Pending 显示标签——视觉上和加字段前没区别；用户主动改时才会出现 ✅/❌。

- **风险**：`submissionType` 与 `JobApplicationReferral` 数据不一致（用户改了一边没改另一边）。
  → **Trade-off**：接受。两边语义不同——前者是"我自己的标记"，后者是"具体内推人是谁"。允许 `submissionType=Direct` 但有 `JobApplicationReferral` 行（可能是 Recruiter 关联，roleType=Recruiter 不是 Referrer）。UI 不强制同步。

- **风险**：未来"内推 vs 直投 成功率" 分析需要更细的成功定义（拿到 onsite？拿到 offer？接 offer？）。
  → **不在本次范围**：V1 只确保数据正确入库，分析视图另起 change。

## Migration Plan

1. 部署后端代码 + 运行 migration `V18`
2. Migration 自动回填 已有数据
3. 用户在前端逐步修正（如有错判）
4. 回滚：单条 SQL 即可
   ```sql
   ALTER TABLE job_applications DROP COLUMN submission_type;
   ALTER TABLE interview_records DROP COLUMN result;
   ```
   配合 `git revert` 即可。

## Open Questions

- 是否要在创建新申请时强制选择 submissionType（默认 `Direct` 但禁止留空）？V1 选 默认 `Direct`，不强制弹窗。
- 是否提供"批量修正" 工具（如：一键把所有 referral junction 有 Referrer 关联的设为 `Referral`）？V1 不做——migration 已经处理；后续手动维护即可。
- `Other` 是否应该展开成 `JobFair` / `SocialMedia` / `ColdEmail` 等具体值？V1 只 `Other`，未来按需求拆。
