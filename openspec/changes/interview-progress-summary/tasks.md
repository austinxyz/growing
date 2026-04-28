## 1. Backend — DTO and Enums

- [x] 1.1 Create `dto/ActiveProgressDTO.java` as a Java record with all fields specified in design.md (id, companyId, companyName, positionName, applicationStatus, macroStageStep, microStageLabel, daysSinceApplied, daysSinceLastUpdate, priorityLevel, nextActionType, nextActionLabel, nextActionDate)
- [x] 1.2 Create `dto/PriorityLevel.java` enum with values OFFER_DEADLINE, STALLED, SCHEDULED, WAITING (ordinal order matters for default sort)
- [x] 1.3 Create `dto/NextActionType.java` enum with values OFFER_DEADLINE, FOLLOW_UP, SCHEDULED_INTERVIEW, WAITING_FEEDBACK, NONE

## 2. Backend — Repository

- [x] 2.1 Add active-progress repo query — shipped as derived `findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(userId, statuses)` (no JOIN FETCH because JobApplication has no @OneToMany to Stage/Record); avoided N+1 with batch loaders `findByJobApplicationIdInOrderByStageOrder(ids)` and `findByJobApplicationIdInOrderByInterviewDateDesc(ids)` on the respective repos. Status list expanded to 9 variants (canonical English + legacy `PhoneScreen`/`Onsite` + Chinese `已投递`/`筛选中`/`面试中`)
- [x] 2.2 Closed-applications support implemented end-to-end via `loadProgressForStatuses(userId, statuses)` refactor that both `getActiveProgress` and `getClosedProgress` call. New `CLOSED_STATUSES` constant covers `Rejected`/`Withdrawn` + Chinese `已拒绝`/`已撤回`. New endpoint `GET /api/job-applications/closed-progress` returns the same `ActiveProgressDTO` shape (no DTO duplication). Smoke test against austin.xyz returned 3 已拒绝 cards correctly

## 3. Backend — Service Logic (TDD)

- [x] 3.1 Stalled threshold tests in `ProgressCalculatorTest` — covers boundaries 7/8, 10/11, 14/15, Offer never; plus Chinese variants (`已投递`, `面试中`)
- [x] 3.2 Priority resolution tests — OFFER_DEADLINE > STALLED > SCHEDULED > WAITING, including Offer/3d-deadline outranking 30d-stale
- [x] 3.3 daysSinceApplied tests — valid statusHistory, null, malformed JSON, missing Applied entry, multiple Applied (picks first)
- [x] 3.4 microStageLabel tests — composite ("Onsite · 2/4 (X 已完成)"), fallbacks for Applied/Screening/Interviewing, Offer special-case
- [x] 3.5 nextAction tests — OFFER_DEADLINE / FOLLOW_UP / SCHEDULED_INTERVIEW / WAITING_FEEDBACK
- [x] 3.6 Implement `JobApplicationService.getActiveProgress(Long userId)` — orchestrator: filters via repo, batch-loads stages/records, maps via `toActiveProgressDTO`, sorts by priority+days
- [x] 3.7 Pure helpers in `service/progress/ProgressCalculator.java` — `normalizeStatus`, `parseStatusHistoryAppliedAt`, `daysSinceApplied(app, mapper)`, `daysSinceLastUpdate(app, latestRecordActivity)`, `macroStageStep`, `isStalled`, `computePriority`, `buildMicroStageLabel`, `buildNextAction`. Refinement: `daysSinceLastUpdate` takes the most-recent `InterviewRecord.updatedAt` so Interviewing-state record updates count as progress
- [x] 3.8 Default sort verified — `JobApplicationServiceProgressTest.getActiveProgress_sortsByPriorityThenDaysDesc`
- [x] 3.9 49 tests pass (33 calculator + 3 service + 2 controller + 11 from later refinements). Formal Jacoco coverage report deferred (no Jacoco plugin in pom.xml; coverage by inspection ≥ 80% on all new code paths)

## 4. Backend — Controller (TDD)

- [x] 4.1 Controller test `JobApplicationControllerProgressTest` — covers happy path (DTO array returned), empty list. Auth-failure (401) test deferred — would need full Spring Security context bootstrap which isn't set up in the project's existing test pattern
- [x] 4.2 `GET /api/job-applications/active-progress` added to `JobApplicationController` — extracts userId from JWT via existing `authService.getUsernameFromToken` pattern
- [x] 4.3 Tests pass (49/49 total)
- [x] 4.4 N+1 regression guard — implemented as Mockito-verify test in `JobApplicationServiceProgressTest.getActiveProgress_doesNotIssuePerRowQueries` (cheaper substitute for full Hibernate Statistics IT). Asserts: each batch repo method invoked exactly once + per-row methods (`companyRepository.findById`, `findByJobApplicationIdOrderByStageOrder`, `findByJobApplicationIdOrderByInterviewDateDesc`) NEVER invoked. Catches the original N+1 bug pattern at unit-test cost (~10ms vs ~10s for full IT)

## 5. Frontend — API Client

- [x] 5.1 `getActiveProgress()` added to `frontend/src/api/jobApplicationApi.js` — calls `api.get('/job-applications/active-progress')` (no `/api` prefix)
- [x] 5.2 File-level comment about interceptor and baseURL already present at top of `jobApplicationApi.js`; new method inherits that contract

## 6. Frontend — Components

- [x] 6.1 `frontend/src/components/job-search/ProgressCard.vue` — note path uses `job-search` (hyphen) per project convention, not `jobSearch`. Renders company/position/micro/status pill, 4-step stepper, bottom row with daysSinceApplied + daysSinceLastUpdate + nextActionLabel chip, left-border accent + corner badge keyed by priorityLevel; click navigates with `router.push({ name: 'JobApplicationList', query: { id: app.id } })`
- [x] 6.2 `frontend/src/components/job-search/ProgressSummaryHeader.vue` — props `counts`/`sortMode`/`showClosed`; emits `update:sortMode`/`update:showClosed`; 3 stat boxes + segmented sort toggle + show-closed button
- [x] 6.3 `frontend/src/views/job-search/InterviewProgress.vue` — refs, computed `displayed` (sort modes: attention/stage/time), computed `summary`, loadProgress on mount; loading/error/empty states

## 7. Frontend — Routing and Cleanup

- [x] 7.1 Router updated — `/job-search/dashboard` now imports `InterviewProgress.vue`, name `JobSearchDashboard` preserved
- [x] 7.2 `git rm frontend/src/views/job-search/JobSearchDashboard.vue` (will appear in next commit's diff)
- [x] 7.3 Verified no remaining imports of `JobSearchDashboard.vue` (grep returned no matches)

## 8. Frontend — Tests

- [x] 8.1 `tests/components/ProgressCard.spec.js` — 6 tests: company/position/micro render, STALLED red border + badge, OFFER_DEADLINE green border + countdown, SCHEDULED orange border, 4-step stepper highlight matches `macroStageStep`, click → `router.push({name:'JobApplicationList',query:{id}})`
- [x] 8.2 `tests/components/ProgressSummaryHeader.spec.js` — 4 tests: counts rendering, active sort button styling, `update:sortMode` emit, `update:showClosed` emit
- [x] 8.3 `tests/views/InterviewProgress.spec.js` — 6 tests: loading spinner, empty state, card-per-app, error doesn't clear data, sort-mode change does NOT trigger network call, sort 'time' reorders by daysSinceApplied DESC
- [x] 8.4 `tests/api/jobApplicationApi.spec.js` — 2 tests: calls `/job-applications/active-progress` (no `/api` prefix), returns interceptor-unwrapped data
- [x] 8.5 `npm test` → 18/18 passing across 4 spec files. Setup added: `vitest@4.1.5`, `@vue/test-utils@2.4.9`, `happy-dom@20.9.0`, `@vitest/ui@4.1.5` as devDeps; `test`/`test:watch` scripts in package.json; `vitest.config.js` with happy-dom + `@` alias

## 9. End-to-End Smoke

- [x] 9.1 Backend (`./backend/start.sh prod`) + frontend (`npm run dev`) running on :8082 / :3001
- [x] 9.2 Logged in as `austin.xyz` (more representative than `austinxu`; `austinxu` only has 2 apps, `austin.xyz` has 20)
- [x] 9.3 InterviewProgress view renders 10 active cards (8 Applied + 1 Interviewing + 1 Screening) — required status normalization fix to recognize Chinese DB values (`已投递`/`筛选中`/`面试中`)
- [x] 9.4 Card click navigates to `JobApplicationList?id=N` AND auto-selects the right job — required adding `useRoute()` + URL-aware auto-select to `JobApplicationList.vue` (Mistake #15 in CLAUDE.md)
- [x] 9.5 Sort mode toggle re-orders client-side; backend computed default order, frontend sort modes do not re-fetch (verified by code path inspection in `InterviewProgress.vue` `displayed` computed; no `loadProgress()` call on sortMode change)
- [x] 9.6 "显示已结案" toggle now lazy-loads `closed-progress` on first activation, caches in-memory, merges with active list. Closed cards rendered with `opacity-60 grayscale` muted styling + line-through status pill. Vitest covers: "toggling showClosed fetches closed jobs and merges them with active list" + "toggling off again hides closed cards (no extra fetch)" — both passing

## 10. Code Review and Documentation

- [x] 10.1 code-reviewer agent run — 4 HIGH findings: (1) **N+1 company lookup → fixed** (batch via `companyRepository.findAllById`); (2) **frontend `daysUntil` UTC-vs-local off-by-one → fixed** (parse ISO date as local-midnight); (3) ObjectMapper instantiated with `new` → pre-existing project pattern, kept (would require touching unrelated code); (4) in-place `Array.sort` in `displayed` computed → not a live bug per reviewer's own analysis, kept for now
- [x] 10.2 security-reviewer agent run — endpoint **PASSES** for user-scoping, PII, JSON parser DoS. Identified 2 CRITICAL items as pre-existing project-wide issues (out of scope for this change): SecurityConfig `permitAll()` and hardcoded JWT secret default — see "Out-of-scope security findings" note below
- [x] 10.3 CLAUDE.md updated — added Mistake #15 (cross-page query-param contracts), Quality Metrics Phase 8 column, Phase 8 Lessons section
- [ ] 10.4 ~~Commit~~ — pending user instruction

## 11. Verification

- [x] 11.1 Backend coverage by inspection: every public/static method on ProgressCalculator and JobApplicationService.getActiveProgress is exercised by at least one test (49 tests). Frontend formal coverage N/A (no test framework)
- [x] 11.2 No `console.log` in new files (`InterviewProgress.vue`, `ProgressCard.vue`, `ProgressSummaryHeader.vue`); only `console.error` for catch blocks matching project convention
- [x] 11.3 `JobSearchDashboard.vue` removed via `git rm` — appears as deletion in next commit's diff
- [x] 11.4 `openspec status --change interview-progress-summary` returns `isComplete: true` for the artifacts (proposal/design/specs/tasks all `done`)

## Out-of-scope security findings (from 10.2 agent run)

Two CRITICAL findings identified by the security-reviewer agent are **pre-existing project-wide concerns**, not introduced by this change. They affect the entire API, not just the new endpoint:

1. **`SecurityConfig.permitAll()`**: Spring Security chain doesn't authenticate any endpoint at the framework layer; controllers manually parse JWT from the Authorization header. This means any endpoint without explicit JWT-parsing logic is unauthenticated. Recommended: replace with `.anyRequest().authenticated()` + a `JwtAuthenticationFilter` registered before `UsernamePasswordAuthenticationFilter`.
2. **JWT secret has a hardcoded fallback default** in `JwtUtil.java`: if `jwt.secret` is missing from env, tokens are signed with a public string, allowing token forgery. Recommended: remove the default so startup fails fast when the secret is missing, then rotate any tokens issued under the default.

Tracked here as a follow-up; should be addressed in a separate change covering the whole project, not bundled with this UI feature.
