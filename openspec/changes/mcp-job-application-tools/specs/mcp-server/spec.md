## ADDED Requirements

### Requirement: MCP Streamable HTTP Endpoint

The system SHALL expose a streamable-http MCP transport endpoint at `POST /mcp` on the existing Spring Boot HTTP listener (port 8082). The endpoint MUST conform to the Model Context Protocol streamable-http transport specification.

#### Scenario: MCP client receives initialize response
- **WHEN** an MCP client POSTs an `initialize` request to `/mcp` with valid JSON-RPC envelope and a valid Bearer JWT
- **THEN** the response status is 200
- **AND** the response body is a JSON-RPC initialize response containing the server capabilities

#### Scenario: MCP endpoint advertises three tools
- **WHEN** an MCP client POSTs a `tools/list` request to `/mcp` with a valid Bearer JWT
- **THEN** the response includes exactly three tools: `list_applications`, `get_application_detail`, `get_active_progress`

### Requirement: Bearer JWT Authentication

The MCP endpoint SHALL extract the Bearer token from the `Authorization` HTTP header, validate it via the existing `JwtUtil`, resolve the username to a userId via the existing `AuthService`, and pass the userId to tool implementations. Requests with missing, malformed, or invalid Bearer tokens MUST be rejected with a 401 error.

#### Scenario: Missing Authorization header is rejected
- **WHEN** an MCP request arrives at `/mcp` with no `Authorization` header
- **THEN** the MCP response contains an error block with `code: 401` and a message indicating "Missing or invalid Authorization header"

#### Scenario: Invalid JWT signature is rejected
- **WHEN** an MCP request arrives at `/mcp` with `Authorization: Bearer <invalid>` (signature mismatch)
- **THEN** the MCP response contains an error block with `code: 401`

#### Scenario: Expired JWT is rejected with helpful message
- **WHEN** an MCP request arrives at `/mcp` with a JWT whose `exp` claim has passed
- **THEN** the MCP response contains an error block with `code: 401`
- **AND** the message indicates the token expired and prompts re-login

#### Scenario: Valid JWT resolves to userId for downstream calls
- **WHEN** an MCP request arrives at `/mcp` with a valid JWT for username `austin.xyz` (id 3)
- **THEN** the tool implementation receives `userId = 3`
- **AND** all service calls within the tool are scoped to userId 3

### Requirement: Tool — list_applications

The MCP server SHALL expose a tool named `list_applications` with an optional string parameter `status` that returns a JSON array of summary objects for the authenticated user's applications. When `status` is omitted, all applications are returned. When provided, the value SHALL be passed through to `JobApplicationService` filter (which accepts both English and Chinese variants via existing `normalizeStatus` logic).

The summary object schema MUST include: `id`, `companyName`, `positionName`, `applicationStatus`, `submissionType`, `statusUpdatedAt`, `daysSinceApplied`, `interviewStageCount`, `interviewRecordCount`. The summary MUST NOT include heavy text fields (`qualifications`, `responsibilities`, `notes`, `recruiterInsights`) to conserve LLM context.

#### Scenario: List without filter returns all user's applications
- **WHEN** the user invokes `list_applications` with no arguments
- **THEN** the response contains a JSON array of all the user's applications
- **AND** items belonging to other users are not included

#### Scenario: Status filter accepts canonical English
- **WHEN** the user invokes `list_applications` with `status="Interviewing"`
- **THEN** only applications whose `applicationStatus` matches Interviewing (or its Chinese variant `面试中`) are returned

#### Scenario: Status filter accepts Chinese variant
- **WHEN** the user invokes `list_applications` with `status="已投递"`
- **THEN** only applications whose `applicationStatus` matches Applied (or `已投递`) are returned

#### Scenario: Heavy text fields are omitted
- **WHEN** the user invokes `list_applications`
- **THEN** the returned items do NOT contain `qualifications`, `responsibilities`, `notes`, or `recruiterInsights` fields

### Requirement: Tool — get_application_detail

The MCP server SHALL expose a tool named `get_application_detail` with a required integer parameter `id` that returns the full `JobApplicationDTO` for the specified application — including nested `stages`, `interviews` (records), `recruiterInsights`, and all heavy text fields. The application MUST belong to the authenticated user; otherwise the tool returns a 403 error.

#### Scenario: Owner can fetch own application detail
- **WHEN** the user (id 3) invokes `get_application_detail` with `id` of an application they own
- **THEN** the response contains the full `JobApplicationDTO` including `stages`, `interviews`, `qualifications`, `responsibilities`, `recruiterInsights`

#### Scenario: Cannot fetch another user's application
- **WHEN** the user (id 3) invokes `get_application_detail` with `id` of an application owned by user id 1
- **THEN** the response contains an error block with `code: 403`

#### Scenario: Non-existent id returns 404
- **WHEN** the user invokes `get_application_detail` with an `id` that does not exist
- **THEN** the response contains an error block with `code: 404`

### Requirement: Tool — get_active_progress

The MCP server SHALL expose a tool named `get_active_progress` with an optional boolean parameter `include_closed` (default `false`) that returns the same `ActiveProgressDTO[]` shape as the existing `GET /api/job-applications/active-progress` endpoint. The tool reuses `JobApplicationService.getActiveProgress(userId)` and, when `include_closed=true`, also calls `getClosedProgress(userId)` and concatenates.

The returned items MUST include all derived fields: `priorityLevel`, `microStageLabel`, `daysSinceApplied`, `daysSinceLastUpdate`, `nextActionType`, `nextActionLabel`, `nextActionDate`, `submissionType`, `macroStageStep`, `applicationStatus` (canonical normalized).

#### Scenario: Default invocation returns active applications only
- **WHEN** the user invokes `get_active_progress` with no arguments
- **THEN** the response contains active applications (statuses Applied / Screening / Interviewing / Offer plus their Chinese variants)
- **AND** rejected/withdrawn applications are not included

#### Scenario: include_closed=true adds rejected and withdrawn
- **WHEN** the user invokes `get_active_progress` with `include_closed=true`
- **THEN** the response includes both active and closed (Rejected / Withdrawn / 已拒绝 / 已撤回) applications

#### Scenario: Sort order matches dashboard
- **WHEN** the user has applications spanning all priority levels
- **THEN** the response is sorted by `priorityLevel` ordinal ASC, then `daysSinceApplied` DESC (matching the existing `JobApplicationService.getActiveProgress` order)

### Requirement: Tool Output Format

All tool responses SHALL be returned as a single MCP `text/plain` content block whose body is a JSON-stringified payload. Errors SHALL be returned in the same content-block envelope with body `{"error": {"code": <int>, "message": <string>}}`.

#### Scenario: Successful tool returns text content with JSON body
- **WHEN** any tool succeeds
- **THEN** the MCP response has a single content block of type `text` whose `text` field is a valid JSON string

#### Scenario: Errors use the same envelope
- **WHEN** any tool fails (auth / not found / forbidden / internal)
- **THEN** the response is a single text content block whose JSON body has the form `{"error": {"code": <int>, "message": <string>}}`

### Requirement: User Scoping Across All Tools

Every tool implementation SHALL pass the userId resolved from the JWT to every `JobApplicationService` method call. No tool SHALL accept a userId argument from the MCP client. No tool SHALL bypass the existing service-layer ownership checks.

#### Scenario: Tool implementations cannot bypass user scoping
- **WHEN** a tool implementation is reviewed
- **THEN** it does NOT accept a userId parameter from the input schema
- **AND** every call to `JobApplicationService` passes the JWT-resolved userId
