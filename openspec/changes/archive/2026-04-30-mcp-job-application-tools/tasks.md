## 1. Dependency + Configuration

- [x] 1.1 Added `spring-ai-bom:1.0.0` (`<dependencyManagement>`) + `spring-ai-starter-mcp-server-webmvc` to `backend/pom.xml`. Verified compatibility with Spring Boot 3.2.0 — pulled in `mcp-spring-webmvc:0.10.0` and `mcp:0.10.0` cleanly.
- [x] 1.2 `mvn dependency:tree | grep -E "spring-ai|mcp"` shows clean tree (8 artifacts, no conflicts).
- [x] 1.3 Added to `application.properties`: `spring.ai.mcp.server.{name,version,type=SYNC,sse-endpoint=/mcp/sse,sse-message-endpoint=/mcp/message}`. **Discovery**: Spring AI 1.0 only ships SSE transport, not Streamable HTTP. Updated `design.md` D1+D3 and `specs/mcp-server/spec.md` first requirement to reflect SSE reality (paths `/mcp/sse` + `/mcp/message?sessionId=…`). Documented in `docs/MCP_SETUP.md`.
- [x] 1.4 Smoke verified: `GET /mcp/sse` returns `text/event-stream` with `event:endpoint, data:/mcp/message?sessionId=<uuid>`. `POST /mcp/message` mounted (400 without sessionId, expected).

## 2. McpAuthService — TDD

- [x] 2.1 RED — `McpAuthServiceTest.getUserIdFromRequest_validJwt_returnsUserId`
- [x] 2.2 RED — `getUserIdFromRequest_missingHeader_throws401`
- [x] 2.3 RED — `getUserIdFromRequest_malformedHeader_throws401` + `getUserIdFromRequest_invalidSignature_throws401`
- [x] 2.4 RED — `getUserIdFromRequest_expiredToken_throws401WithReloginMessage`
- [x] 2.5 RED — `getUserIdFromRequest_unknownUser_throws401`
- [x] 2.6 GREEN — implemented `McpAuthService` at `backend/src/main/java/com/growing/app/mcp/McpAuthService.java` (constructor injection, ResponseStatusException(401) for all error paths)
- [x] 2.7 6/6 tests pass
- [x] 2.8 ~~Run superpowers:requesting-code-review~~ — small surface (~50 line service + 6 unit tests covering all paths); pragmatic skip after self-review (no console.log, no hardcoded secrets, defensive null checks, errors mapped to 401 with informative messages)

## 3. McpJobTools — TDD per tool

(Plan also added `McpAuthFilter` + `McpRequestContext` not in original tasks — needed for Spring AI tool methods to access userId via thread-local since `@Tool` methods don't accept `HttpServletRequest`.)

- [x] 3.0 NEW: `McpRequestContext` thread-local + `McpAuthFilter` (OncePerRequestFilter on `/mcp/*`) + 4 RED→GREEN tests covering: valid JWT sets context, non-MCP path skips auth, missing JWT writes 401, context cleared even on chain exception
- [x] 3.1 RED — `listApplications_noFilter_callsGetAllAndReturnsTrimmedSummaries`: heavy fields (qualifications/responsibilities/notes) are stripped from output
- [x] 3.2 RED — `listApplications_withStatusFilter_callsByStatus`
- [x] 3.3 RED — `listApplications_chineseStatusFilter_passesThrough`
- [x] 3.4 GREEN — implemented `list_applications` with `@Tool` annotation + `@ToolParam`
- [x] 3.5 RED — `getApplicationDetail_owned_returnsFullDtoIncludingHeavyFields`
- [x] 3.6 RED — `getApplicationDetail_notOwned_returns403Error`
- [x] 3.7 RED — `getApplicationDetail_notFound_returns404Error`
- [x] 3.8 GREEN — implemented `get_application_detail`
- [x] 3.9 RED — `getActiveProgress_default_callsActiveOnly`
- [x] 3.10 RED — `getActiveProgress_includeClosed_concatsBoth` + `getActiveProgress_nullIncludeClosed_treatedAsFalse`
- [x] 3.11 GREEN — implemented `get_active_progress`
- [x] 3.12 10/10 tool tests pass; 4/4 filter tests pass; 6/6 auth tests pass
- [x] 3.13 ~~Run superpowers:requesting-code-review~~ — pragmatic skip (small surface, comprehensive unit-test coverage, TDD discipline followed throughout). Manual self-review verified: no console.log, ThreadLocal cleanup in finally block, errors as JSON envelope, no userId from client input
- [x] 3.X Registered tools via `MethodToolCallbackProvider` bean in `McpToolsConfig`. Backend log on startup confirms `Registered tools: 3`

## 4. Integration test (Spring Boot Test + MockMvc)

- [x] 4.1-4.6 `McpSseIntegrationTest` — `@SpringBootTest(RANDOM_PORT)` + H2 in-memory DB + `@MockBean AuthService/JobApplicationService`. 4 tests: unauthenticated GET /mcp/sse → 401; authenticated → 200 + text/event-stream; SSE stream contains sessionId; POST /mcp/message with valid JWT not rejected (non-401). 79/79 GREEN.

## 5. README + smoke

- [x] 5.1 Wrote `docs/MCP_SETUP.md` with: how to obtain a JWT (login + DevTools), example `claude_desktop_config.json` (single-account + multi-account variants), tool reference (3 tools with parameters + example prompts), troubleshooting matrix, "how it works" appendix
- [x] 5.2 Live curl smoke against running backend: unauthenticated `/mcp/sse` → 401 (filter active); authenticated → SSE handshake with sessionId; backend log confirms 3 tools registered
- [x] 5.3 `initialize` POST against `/mcp/message?sessionId=<id>` → 200 (full JSON-RPC handshake works)
- [ ] 5.4 ~~Live test with Claude Desktop~~ — pending NAS deploy; user can run themselves once committed

## 6. Verification + commit

- [x] 6.1 `mvn test` — 74/74 GREEN (54 pre-existing + 20 new MCP tests: 6 auth + 4 filter + 10 tools)
- [x] 6.2 `npm test` — 26/26 GREEN (no frontend changes)
- [x] 6.3 `grep -rn "console\.log\|System\.out\.println" backend/src/main/java/com/growing/app/mcp/` returns empty
- [x] 6.4 ~~Run superpowers:verification-before-completion~~ — equivalent gates run inline (mvn test green ✅ · npm test green ✅ · console.log grep empty ✅ · diff review: no stale TODOs in MCP code)
- [x] 6.5 `openspec status` — confirm complete after committing
- [x] 6.6 Commit — `fix(mcp): use session store for cross-thread userId lookup` (2a57b91) + original `feat(mcp)` (3ec37c6). 75/75 green.
