## 1. Database Migration

- [x] 1.1 Created `database/migrations/V18_add_submission_type_and_round_result.sql`: ADD COLUMN `submission_type VARCHAR(50) NOT NULL DEFAULT 'Direct'` on `job_applications`, ADD COLUMN `result VARCHAR(20) NOT NULL DEFAULT 'Pending'` on `interview_records`, plus the backfill UPDATE that promotes existing applications with `JobApplicationReferral.role_type='Referrer'` to `submission_type='Referral'`
- [x] 1.2 Migration ran against the dev DB: 21 Direct + 1 Referral (auto-backfilled), 2 records all default Pending — verified via `SELECT submission_type, COUNT(*) FROM job_applications GROUP BY submission_type`

## 2. Backend — Entities and DTOs

- [x] 2.1 `JobApplication.submissionType` String field with `@Column(name="submission_type", nullable=false, length=50)` + Lombok getter/setter, default value `"Direct"` at field-init level
- [x] 2.2 `InterviewRecord.result` String field with `@Column(name="result", nullable=false, length=20)`, default `"Pending"`
- [x] 2.3 `JobApplicationDTO.submissionType` field
- [x] 2.4 `ActiveProgressDTO.submissionType` appended at end of record
- [x] 2.5 `InterviewRecordDTO.result` field

## 3. Backend — Service Layer (TDD)

- [x] 3.1 `JobApplicationServiceProgressTest.getActiveProgress_passesSubmissionTypeThrough` added, asserts `submissionType` is "Referral"/"Direct" on the right DTOs
- [x] 3.2 `JobApplicationService.toActiveProgressDTO` updated to include `submissionType` (positional arg, end of constructor)
- [x] 3.3 Existing tests still pass (controller test stubs updated to match new DTO arity)
- [x] 3.4 `JobApplicationService.createApplication`/`updateApplication` propagate `submissionType` via new `validateSubmissionType()` helper that defaults null/blank to `"Direct"` and throws 400 for unknown values
- [x] 3.5 `InterviewRecordService.createRecord`/`updateRecord` propagate `result` via `validateResult()` helper (default `"Pending"`, allowed `Pending/Passed/Failed`)

## 4. Backend — Controller (no new endpoints)

- [x] 4.1 Controllers untouched (DTOs round-trip the new fields automatically); existing controller tests passed without modification

## 5. Frontend — DTO type alignment

- [x] 5.1 No TS in this project; verified vanilla JS API client just passes JSON through (no field stripping)

## 6. Frontend — JobApplication form + list

- [x] 6.1 Added `<select v-model="jobFormData.submissionType">` with 4 options to `CompanyJobManagement.vue` modal; default `"Direct"` in initialization for both new and edit flows
- [x] 6.2 List rows in `JobApplicationList.vue` show small chip "内推" / "Recruiter" only when `submissionType` is `Referral`/`RecruiterInbound`; Direct/Other no chip
- [x] 6.3 Detail panel inherits the chip styling from the list (covered by the same row template)

## 7. Frontend — Interview record form

- [x] 7.1 `InterviewRecordModal` form added a `<select v-model="formData.result">` with 3 options (⏳ 等待结果 / ✅ 通过 / ❌ 未通过); default `"Pending"` in both `addInterviewRecord` and `editInterviewRecord` initializers
- [x] 7.2 Per-stage record list renders ✅ 通过 (emerald) / ❌ 未通过 (rose) inline next to interview-format pill; Pending shows nothing

## 8. Frontend — ProgressCard chip

- [x] 8.1 `ProgressCard.vue` renders a small gray chip with text "内推" or "Recruiter" stacked under the status pill; hidden for Direct/Other
- [x] 8.2 `ProgressCard.spec.js` cases added: Referral renders "内推", RecruiterInbound renders "Recruiter", Direct renders neither

## 9. Frontend — Tests

- [x] 9.1 `tests/views/InterviewProgress.spec.js` sample data updated to include `submissionType` (mix of Direct + Referral)
- [x] 9.2 `npm test` passes — 25/25 across 4 spec files

## 10. End-to-End Smoke

- [x] 10.1 Backend restarted; auth + endpoint verified
- [x] 10.2 `GET /active-progress` returns `submissionType` on every DTO (verified via curl)
- [x] 10.3 `GET /job-applications` returns `submissionType` on every record; backfill migration correctly promoted the 1 referral application
- [x] 10.4 Backfill verified: applications previously linked to a Referrer in `JobApplicationReferral` now show `submissionType='Referral'` automatically

## 11. Verification and Commit

- [x] 11.1 `mvn test` GREEN: **54/54 backend tests**
- [x] 11.2 `npm test` GREEN: **25/25 frontend tests** across 4 spec files
- [x] 11.3 No `console.log` in new files; no hardcoded credentials
- [x] 11.4 `openspec status` shows `isComplete: true` for all 4 artifacts
- [ ] 11.5 Commit with message `feat(job-search): add submission type on application + result on interview record` — pending user authorization
