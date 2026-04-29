## MODIFIED Requirements

### Requirement: Active Job Aggregation Endpoint

The system SHALL expose `GET /api/job-applications/active-progress` that returns the authenticated user's job applications whose `applicationStatus` is one of `Applied`, `PhoneScreen`, `Onsite`, `Offer`, enriched with derived progress fields and the underlying `submissionType`.

#### Scenario: Authenticated user retrieves active applications
- **WHEN** an authenticated user calls `GET /api/job-applications/active-progress`
- **THEN** the response status is 200
- **AND** the response body is a JSON array of `ActiveProgressDTO` objects
- **AND** every returned item has `applicationStatus` in `{Applied, PhoneScreen, Onsite, Offer}`
- **AND** every returned item has a non-null `submissionType` value
- **AND** items belonging to other users are not included

#### Scenario: Unauthenticated request is rejected
- **WHEN** a request without a valid JWT token calls `GET /api/job-applications/active-progress`
- **THEN** the response status is 401

#### Scenario: User has no active applications
- **WHEN** an authenticated user has zero applications in active statuses
- **THEN** the response status is 200
- **AND** the response body is an empty array `[]`

#### Scenario: DTO carries submission type
- **WHEN** the response includes an application whose underlying record has `submissionType = "Referral"`
- **THEN** the corresponding DTO entry has `submissionType = "Referral"`
