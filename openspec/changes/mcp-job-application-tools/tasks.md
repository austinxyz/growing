## 1. Dependency + Configuration

- [ ] 1.1 Add `spring-ai-starter-mcp-server-webmvc` to `backend/pom.xml`. Verify the exact artifact ID and latest GA version against <https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html>; pick the version compatible with Spring Boot 3.2.0
- [ ] 1.2 Run `mvn -q dependency:tree | grep mcp` to confirm the starter pulled in `mcp-spring-webmvc`, `mcp-server`, and Jackson modules cleanly without dependency conflicts
- [ ] 1.3 Add to `backend/src/main/resources/application.properties`: `spring.ai.mcp.server.name=growing`, `spring.ai.mcp.server.version=1.0.0`, `spring.ai.mcp.server.transport=streamable-http`. Verify property names against the Spring AI starter docs (these are subject to change pre-1.0; adjust if the starter uses different keys)
- [ ] 1.4 Run `./backend/start.sh dev` and verify that hitting `POST /mcp` returns a non-404 (even if 401, that proves the route is mounted). Capture the actual default endpoint path the starter chose if different from `/mcp`

## 2. McpAuthService — TDD

- [ ] 2.1 RED — write `McpAuthServiceTest.getUserIdFromRequest_validJwt_returnsUserId`: stub a `MockHttpServletRequest` with `Authorization: Bearer <signed-token>`, mock `JwtUtil.getUsernameFromToken` and `AuthService.getUserIdByUsername`, assert returned userId
- [ ] 2.2 RED — `getUserIdFromRequest_missingHeader_throws401`: request with no Authorization header → expect a `ResponseStatusException` (or custom `McpAuthException`) with code 401 and message "Missing or invalid Authorization header"
- [ ] 2.3 RED — `getUserIdFromRequest_invalidSignature_throws401`: header present but JWT parse throws → 401
- [ ] 2.4 RED — `getUserIdFromRequest_expiredToken_throws401WithReloginMessage`: JWT parse throws expiration exception → 401 with "Token expired, please re-login to growing and copy a fresh token"
- [ ] 2.5 RED — `getUserIdFromRequest_unknownUser_throws401`: JWT valid but `AuthService.getUserIdByUsername` returns null/throws → 401
- [ ] 2.6 GREEN — implement `McpAuthService` (`@Service`) at `backend/src/main/java/com/growing/app/mcp/McpAuthService.java`. Inject `JwtUtil` + `AuthService` via constructor. Method `Long getUserIdFromRequest(HttpServletRequest req)` does: read `Authorization`, strip `"Bearer "` prefix, call `JwtUtil.getUsernameFromToken`, then `AuthService.getUserIdByUsername`; wrap exceptions with appropriate 401 messages
- [ ] 2.7 Verify all tests in 2.1-2.5 pass; run `mvn test -Dtest=McpAuthServiceTest`
- [ ] 2.8 Run **superpowers:requesting-code-review** on the diff for group 2; address CRITICAL/HIGH findings before moving on

## 3. McpJobTools — TDD per tool

- [ ] 3.1 RED — `McpJobToolsTest.listApplications_noFilter_returnsTrimmedSummaries`: mock `JobApplicationService.getAllApplicationsByUserId(3L)` to return 2 DTOs; invoke the tool method with userId=3 and `status=null`; assert response is JSON array of 2 items, each with the documented summary fields and NO `qualifications`/`responsibilities`/`notes`/`recruiterInsights`
- [ ] 3.2 RED — `listApplications_withStatusFilter_passesThrough`: invoke with `status="Interviewing"`; verify the tool calls `getApplicationsByStatus(3L, "Interviewing")` (not `getAll`)
- [ ] 3.3 RED — `listApplications_chineseStatusFilter_passesThrough`: invoke with `status="面试中"`; verify the tool calls `getApplicationsByStatus(3L, "面试中")` (service handles normalization downstream)
- [ ] 3.4 GREEN — implement `list_applications` method on `McpJobTools` (registered with `@Tool` or whatever annotation Spring AI starter uses)
- [ ] 3.5 RED — `getApplicationDetail_owned_returnsFullDto`: mock `JobApplicationService.getApplicationById(42L, 3L)` to return a fully-populated DTO with stages + interviews; assert response JSON has all fields including heavy text
- [ ] 3.6 RED — `getApplicationDetail_notOwned_returns403Error`: service throws `ResponseStatusException(403)` → tool response is `{"error":{"code":403,"message":"..."}}` text content
- [ ] 3.7 RED — `getApplicationDetail_notFound_returns404Error`: service throws `ResponseStatusException(404)` → tool response is `{"error":{"code":404,"message":"..."}}`
- [ ] 3.8 GREEN — implement `get_application_detail` method
- [ ] 3.9 RED — `getActiveProgress_default_callsGetActiveOnly`: invoke with no args; verify the tool calls `service.getActiveProgress(userId)` and NOT `getClosedProgress`; assert returned items have all derived fields
- [ ] 3.10 RED — `getActiveProgress_includeClosed_concatsBoth`: invoke with `include_closed=true`; verify both `getActiveProgress` and `getClosedProgress` are called and results concatenated
- [ ] 3.11 GREEN — implement `get_active_progress` method
- [ ] 3.12 Run all `McpJobToolsTest` cases; confirm GREEN
- [ ] 3.13 Run **superpowers:requesting-code-review** on the diff for group 3; address CRITICAL/HIGH findings before moving on

## 4. Integration test (Spring Boot Test + MockMvc)

- [ ] 4.1 RED — `McpEndpointIT.initialize_withValidJwt_returns200`: full Spring context boot, POST `/mcp` with `Content-Type: application/json` body `{"jsonrpc":"2.0","method":"initialize",...}` and `Authorization: Bearer <signed-test-token>`; expect 200 and a JSON-RPC initialize response shape
- [ ] 4.2 RED — `McpEndpointIT.toolsList_returnsThreeTools`: POST `/mcp` with `tools/list`; assert the three tool names are present in the response
- [ ] 4.3 RED — `McpEndpointIT.callTool_listApplications_returnsTextContent`: seed a test JobApplication via repository, then POST `/mcp` with `tools/call` for `list_applications`; assert the text content body contains the seeded application
- [ ] 4.4 RED — `McpEndpointIT.missingAuth_returns401InEnvelope`: POST `/mcp` with no Authorization header; assert 401 (or 200 with `{"error":{"code":401}}` depending on Spring AI starter convention — adapt the assertion to whichever the starter chose)
- [ ] 4.5 GREEN — fix any wiring issues that surface from the IT (typically: dependency-injection misses, transport config, Jackson serialization)
- [ ] 4.6 Run **superpowers:requesting-code-review** on the diff for group 4; address CRITICAL/HIGH findings before moving on

## 5. README + smoke

- [ ] 5.1 Add a section to `README.md` (or new `docs/MCP_SETUP.md`): "Connecting Claude Desktop to growing MCP". Cover: how to get a JWT (login to growing UI, DevTools → Application → localStorage → `token`), example Claude Desktop `claude_desktop_config.json` snippet with two server entries (one per account), expected URL `http://nas:8082/mcp`, troubleshooting (token expired = re-login)
- [ ] 5.2 SMOKE — Restart backend on local machine (`./backend/start.sh dev`); from a separate terminal, `curl -X POST http://localhost:8082/mcp -H "Content-Type: application/json" -H "Authorization: Bearer $(get_jwt)" -d '{"jsonrpc":"2.0","id":1,"method":"tools/list"}'` — verify all three tools are listed
- [ ] 5.3 SMOKE — Same curl but `tools/call` for `list_applications` — verify a real application from your DB comes back
- [ ] 5.4 SMOKE — Configure Claude Desktop's `claude_desktop_config.json` with the local URL + your real JWT; restart Claude Desktop; ask Claude "what jobs am I currently interviewing for?" — verify it picks up `get_active_progress` and returns a sensible answer

## 6. Verification + commit

- [ ] 6.1 Run `mvn test` — all backend tests GREEN (existing 54 + new ~12)
- [ ] 6.2 Run `npm test` — frontend tests still GREEN (no frontend changes expected, but verify nothing accidentally broke)
- [ ] 6.3 `grep -rn "console\.log\|System\.out\.println" backend/src/main/java/com/growing/app/mcp/ frontend/src/` — must be empty for production code paths
- [ ] 6.4 Run **superpowers:verification-before-completion** as final gate (mvn test + npm test + console.log grep + diff review for stale TODOs and commented-out blocks)
- [ ] 6.5 Run `openspec status --change mcp-job-application-tools` and confirm `isComplete: true`
- [ ] 6.6 Commit with message `feat(mcp): add MCP server with 3 read-only job-application tools`
