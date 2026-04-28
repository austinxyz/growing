## ADDED Requirements

### Requirement: Active Job Aggregation Endpoint

The system SHALL expose `GET /api/job-applications/active-progress` that returns the authenticated user's job applications whose `applicationStatus` is one of `Applied`, `PhoneScreen`, `Onsite`, `Offer`, enriched with derived progress fields.

#### Scenario: Authenticated user retrieves active applications
- **WHEN** an authenticated user calls `GET /api/job-applications/active-progress`
- **THEN** the response status is 200
- **AND** the response body is a JSON array of `ActiveProgressDTO` objects
- **AND** every returned item has `applicationStatus` in `{Applied, PhoneScreen, Onsite, Offer}`
- **AND** items belonging to other users are not included

#### Scenario: Unauthenticated request is rejected
- **WHEN** a request without a valid JWT token calls `GET /api/job-applications/active-progress`
- **THEN** the response status is 401

#### Scenario: User has no active applications
- **WHEN** an authenticated user has zero applications in active statuses
- **THEN** the response status is 200
- **AND** the response body is an empty array `[]`

### Requirement: Stage-Aware Stalled Detection

The system SHALL flag an application as stalled when `daysSinceLastUpdate` exceeds a stage-specific threshold:
- `Applied`: > 7 days
- `PhoneScreen`: > 10 days
- `Onsite`: > 14 days
- `Offer`: never marked stalled (offer-deadline priority handles urgency)

#### Scenario: Applied state stalls after 7 days
- **WHEN** an application has `applicationStatus=Applied` and `statusUpdatedAt` was 8 days ago
- **THEN** `priorityLevel` is `STALLED`

#### Scenario: Applied state at exactly 7 days is not stalled
- **WHEN** an application has `applicationStatus=Applied` and `statusUpdatedAt` was 7 days ago
- **THEN** `priorityLevel` is not `STALLED`

#### Scenario: Onsite state stalls after 14 days
- **WHEN** an application has `applicationStatus=Onsite` and `statusUpdatedAt` was 15 days ago
- **THEN** `priorityLevel` is `STALLED`

#### Scenario: Offer state is never stalled
- **WHEN** an application has `applicationStatus=Offer` and `statusUpdatedAt` was 30 days ago and no offer deadline within 14 days
- **THEN** `priorityLevel` is `WAITING`, not `STALLED`

### Requirement: Priority Level Calculation

The system SHALL compute `priorityLevel` for each active application using the first matching rule (top to bottom): `OFFER_DEADLINE` → `STALLED` → `SCHEDULED` → `WAITING`.

#### Scenario: Offer deadline within 14 days takes top priority
- **WHEN** an application has `applicationStatus=Offer` and `offerDeadline` is 5 days from today
- **THEN** `priorityLevel` is `OFFER_DEADLINE`

#### Scenario: Offer deadline beats stalled flag when both apply
- **WHEN** an application has `applicationStatus=Offer`, `offerDeadline` 3 days away, AND `daysSinceLastUpdate=20`
- **THEN** `priorityLevel` is `OFFER_DEADLINE`

#### Scenario: Scheduled interview within 7 days outranks waiting
- **WHEN** an application has `applicationStatus=Onsite`, no stalled condition, AND has an `InterviewRecord` with `interviewDate` 3 days in the future
- **THEN** `priorityLevel` is `SCHEDULED`

#### Scenario: Default to waiting when no other rule matches
- **WHEN** an application has `applicationStatus=Applied`, `daysSinceLastUpdate=3`, no scheduled interviews
- **THEN** `priorityLevel` is `WAITING`

### Requirement: Days Since Applied Calculation

The system SHALL compute `daysSinceApplied` from the timestamp of the first `Applied` entry in `statusHistory`. When `statusHistory` is null, empty, malformed, or contains no `Applied` entry, the system SHALL fall back to `createdAt`.

#### Scenario: Status history contains Applied entry
- **WHEN** an application has `statusHistory` with first Applied entry at 10 days ago
- **THEN** `daysSinceApplied` is 10

#### Scenario: Status history is null
- **WHEN** an application has `statusHistory=null` and `createdAt` 6 days ago
- **THEN** `daysSinceApplied` is 6

#### Scenario: Status history is malformed JSON
- **WHEN** an application has `statusHistory="{not valid json"` and `createdAt` 4 days ago
- **THEN** `daysSinceApplied` is 4
- **AND** the system logs a warning but does not throw

### Requirement: Micro Stage Label Derivation

The system SHALL produce a human-readable `microStageLabel` for each application by combining the count of completed `InterviewRecord` entries against the count of defined `InterviewStage` entries plus the latest record's `stageName`. When stages or records are absent, a fallback label appropriate to the macro stage SHALL be used.

#### Scenario: Onsite with stages and records produces composite label
- **WHEN** an application has 4 `InterviewStage` entries and 2 completed `InterviewRecord` entries (latest stage name "Tech Round 1")
- **THEN** `microStageLabel` is `"Onsite · 2/4 (Tech Round 1 已完成)"`

#### Scenario: Applied state without stages uses fallback
- **WHEN** an application has `applicationStatus=Applied` and no `InterviewStage` entries
- **THEN** `microStageLabel` is `"等待初步筛选"`

#### Scenario: Phone screen without records uses fallback
- **WHEN** an application has `applicationStatus=PhoneScreen` and no `InterviewRecord` entries
- **THEN** `microStageLabel` is `"Phone Screen · 待安排"`

### Requirement: Next Action Calculation

The system SHALL determine `nextActionType`, `nextActionLabel`, and optional `nextActionDate` for each application based on `applicationStatus`, presence of upcoming `InterviewRecord` dates, and `offerDeadline`.

#### Scenario: Offer with deadline shows deadline countdown
- **WHEN** an application has `applicationStatus=Offer` and `offerDeadline=2026-05-05`
- **THEN** `nextActionType` is `OFFER_DEADLINE`
- **AND** `nextActionLabel` contains the formatted deadline (e.g., `"⏰ 截止 5/5"`)
- **AND** `nextActionDate` is `2026-05-05`

#### Scenario: Upcoming scheduled interview shows interview info
- **WHEN** an application has an `InterviewRecord` with `interviewDate=2026-05-02` and stage name "Tech Round 2"
- **THEN** `nextActionType` is `SCHEDULED_INTERVIEW`
- **AND** `nextActionLabel` is `"📅 Tech Round 2 · 5/2"`

#### Scenario: Stalled application suggests follow-up
- **WHEN** an application is stalled (`priorityLevel=STALLED`)
- **THEN** `nextActionType` is `FOLLOW_UP`
- **AND** `nextActionLabel` is `"📩 跟进 recruiter"`

#### Scenario: Active application with nothing scheduled shows waiting
- **WHEN** an application has `applicationStatus=PhoneScreen`, `daysSinceLastUpdate=4`, no scheduled records
- **THEN** `nextActionType` is `WAITING_FEEDBACK`
- **AND** `nextActionLabel` is `"⏳ 等待反馈"`

### Requirement: Default Sort Order

The endpoint SHALL return applications sorted by `priorityLevel` ordinal ascending (`OFFER_DEADLINE` first), with `daysSinceApplied` descending as the tie-breaker.

#### Scenario: Offer-deadline cards come before stalled cards
- **WHEN** the response includes one `OFFER_DEADLINE` and one `STALLED` application
- **THEN** the `OFFER_DEADLINE` application appears first

#### Scenario: Within same priority, oldest applications first
- **WHEN** two applications both have `priorityLevel=WAITING` and `daysSinceApplied` of 5 and 12 respectively
- **THEN** the application with `daysSinceApplied=12` appears first

### Requirement: Macro Stage Step

The system SHALL set `macroStageStep` to an integer 1-4 mapping `applicationStatus` to a fixed step:
- `Applied` → 1
- `PhoneScreen` → 2
- `Onsite` → 3
- `Offer` → 4

#### Scenario: Mapping covers all four active statuses
- **WHEN** an application has `applicationStatus=Onsite`
- **THEN** `macroStageStep` is 3

### Requirement: Avoidance of N+1 Queries

The endpoint SHALL fetch applications, their `InterviewStage` entries, and their `InterviewRecord` entries in a single database query (via `JOIN FETCH` or `@EntityGraph`).

#### Scenario: Single query loads all related data
- **WHEN** the endpoint is invoked for a user with 5 active applications, each with 4 stages and 2 records
- **THEN** the database receives at most one SELECT statement (excluding any independent `Company` lookup, which may be a separate batched query)

### Requirement: Frontend Replaces Job Search Dashboard

The frontend route `/job-search/dashboard` SHALL render the new `InterviewProgress` view in place of the deprecated `JobSearchDashboard`. The route name `JobSearchDashboard` SHALL be preserved to avoid breaking named-route navigation.

#### Scenario: Dashboard route renders interview progress
- **WHEN** an authenticated user navigates to `/job-search/dashboard`
- **THEN** the rendered component is `InterviewProgress.vue`

#### Scenario: Old dashboard component is removed
- **WHEN** the codebase is searched after the change is applied
- **THEN** `frontend/src/views/job-search/JobSearchDashboard.vue` does not exist

### Requirement: Card Click Navigation

Each `ProgressCard` SHALL navigate to the existing application detail view when clicked, passing the application id.

#### Scenario: Click opens detail
- **WHEN** the user clicks a `ProgressCard` for application id 42
- **THEN** the router navigates to the `JobApplicationList` route with query `id=42`

### Requirement: Sort Mode Toggle

The frontend SHALL allow the user to switch among three sort modes within the page session: `Needs Attention` (default, server order), `By Stage` (group by `applicationStatus` in order Offer → Onsite → PhoneScreen → Applied), `By Time` (`daysSinceApplied` descending). Sort mode changes SHALL NOT trigger a network request; they SHALL re-order the in-memory list.

#### Scenario: By Stage groups applications
- **WHEN** the user selects `By Stage` and the list contains 2 Onsite, 1 Offer, and 1 Applied
- **THEN** the rendered order is Offer card, both Onsite cards, then the Applied card

#### Scenario: Sort change is local
- **WHEN** the user changes sort mode
- **THEN** no HTTP request is sent to the backend

### Requirement: Show Closed Toggle

The frontend SHALL provide a toggle that, when enabled, includes applications with `applicationStatus` in `{Rejected}` and offer outcomes in `{Declined}` in the displayed list. Enabling the toggle MAY trigger a network request to fetch closed entries.

#### Scenario: Toggle includes rejected applications
- **WHEN** the user enables `显示已结案` and there is one Rejected application
- **THEN** the Rejected application appears in the rendered list with appropriate visual de-emphasis

#### Scenario: Default state hides closed
- **WHEN** the page first loads
- **THEN** Rejected and Declined applications are not displayed

### Requirement: Empty State

The frontend SHALL display an empty-state message with a call-to-action when no active applications exist.

#### Scenario: User with no active applications sees CTA
- **WHEN** the endpoint returns an empty array
- **THEN** the page displays a message indicating no active applications and a button that routes to `/job-search/applications`

### Requirement: Error State

When the endpoint returns an error or the network call fails, the frontend SHALL display an error banner with a retry action and SHALL NOT clear any previously loaded data.

#### Scenario: 500 error shows banner
- **WHEN** the endpoint responds with HTTP 500
- **THEN** an error banner is displayed
- **AND** previously loaded cards remain visible

### Requirement: Stalled and Priority Visual Indicators

`ProgressCard` SHALL render distinct visual indicators for `priorityLevel`:
- `OFFER_DEADLINE`: green left border + deadline badge
- `STALLED`: red left border + stalled badge with day count
- `SCHEDULED`: orange left border (no badge)
- `WAITING`: no left border accent

#### Scenario: Stalled card shows red bar and badge
- **WHEN** an application's `priorityLevel` is `STALLED` and `daysSinceLastUpdate` is 9
- **THEN** the card has a red left border accent
- **AND** the card displays a badge containing `"9d"`

#### Scenario: Offer-deadline card shows green bar and countdown
- **WHEN** an application's `priorityLevel` is `OFFER_DEADLINE` and `nextActionDate` is 7 days away
- **THEN** the card has a green left border accent
- **AND** the card displays a countdown badge
