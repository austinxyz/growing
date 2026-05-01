## ADDED Requirements

### Requirement: Application Submission Type

`JobApplication` SHALL carry a `submissionType` field that records how the user submitted the application, with allowed values `Direct`, `Referral`, `RecruiterInbound`, and `Other`. The field is required and defaults to `Direct` for new records.

#### Scenario: New application defaults to Direct
- **WHEN** a user creates a new `JobApplication` without specifying `submissionType`
- **THEN** the persisted record has `submissionType = "Direct"`

#### Scenario: User marks application as Referral
- **WHEN** a user updates `submissionType` to `Referral` on an existing application
- **THEN** subsequent reads return `submissionType = "Referral"`
- **AND** the existing `JobApplicationReferral` rows for that application are unaffected

#### Scenario: Invalid submission type is rejected
- **WHEN** a request body contains `submissionType = "InvalidValue"`
- **THEN** the response status is 400 Bad Request

### Requirement: Submission Type Surfaced in Progress DTO

The `ActiveProgressDTO` returned by `GET /api/job-applications/active-progress` SHALL include a `submissionType` field carrying the underlying application's value verbatim.

#### Scenario: Progress card receives submission type
- **WHEN** an active application has `submissionType = "Referral"`
- **THEN** the corresponding `ActiveProgressDTO` has `submissionType = "Referral"`

### Requirement: Decoupled From Referral Junction

The `submissionType` field SHALL be authoritative independent of `JobApplicationReferral` rows. The system SHALL NOT auto-derive or auto-overwrite `submissionType` based on the presence or absence of referral junction rows after the initial migration.

#### Scenario: User can mark Referral without a contact
- **WHEN** a user sets `submissionType = "Referral"` on an application that has no `JobApplicationReferral` rows
- **THEN** the persisted record has `submissionType = "Referral"`
- **AND** the application is not rejected for missing referral junction

#### Scenario: Existing referral junction does not override user choice
- **WHEN** an application has `JobApplicationReferral` rows with `roleType = "Referrer"` AND user sets `submissionType = "Direct"`
- **THEN** the persisted record has `submissionType = "Direct"`

### Requirement: Migration Backfills Submission Type

The database migration introducing `submission_type` SHALL backfill existing records by setting `submission_type = "Referral"` for any application id that already has a `JobApplicationReferral` row with `role_type = "Referrer"`, and `submission_type = "Direct"` for all other existing applications.

#### Scenario: Application with existing Referrer link gets backfilled to Referral
- **WHEN** the migration runs against a database where application id 42 has a `JobApplicationReferral` row with `role_type = "Referrer"`
- **THEN** application id 42 has `submission_type = "Referral"` after migration

#### Scenario: Application without referrer link defaults to Direct
- **WHEN** the migration runs against a database where application id 99 has no `JobApplicationReferral` rows
- **THEN** application id 99 has `submission_type = "Direct"` after migration

### Requirement: Interview Round Result

`InterviewRecord` SHALL carry a `result` field with allowed values `Pending`, `Passed`, and `Failed`. The field is required and defaults to `Pending` for new records.

#### Scenario: New interview record defaults to Pending
- **WHEN** a user creates a new `InterviewRecord` without specifying `result`
- **THEN** the persisted record has `result = "Pending"`

#### Scenario: User marks round as Passed
- **WHEN** a user updates `result` to `Passed` on an existing record
- **THEN** subsequent reads return `result = "Passed"`

#### Scenario: User marks round as Failed
- **WHEN** a user updates `result` to `Failed` on an existing record
- **THEN** subsequent reads return `result = "Failed"`

#### Scenario: Invalid result is rejected
- **WHEN** a request body contains `result = "InvalidValue"`
- **THEN** the response status is 400 Bad Request

### Requirement: Result Migration Defaults

The database migration introducing `result` on `interview_records` SHALL set `result = "Pending"` for all existing rows.

#### Scenario: Existing records default to Pending
- **WHEN** the migration runs against existing interview records
- **THEN** every row has `result = "Pending"` after migration

### Requirement: Result Does Not Auto-Update Application Status

Setting `InterviewRecord.result = "Failed"` SHALL NOT automatically change the parent `JobApplication.applicationStatus`. The user controls macro status independently.

#### Scenario: Failed round leaves application status unchanged
- **WHEN** a user sets `result = "Failed"` on a record whose application has `applicationStatus = "Interviewing"`
- **THEN** the application's `applicationStatus` remains `"Interviewing"`
