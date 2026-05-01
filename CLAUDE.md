# Growing App - Claude Code Guide

> **Context Recovery**: Read this first when resuming work.
> **Project Root**: `<your-clone-of-growing>/`

## Behavior Guidelines

**Think before coding.** State assumptions explicitly. If multiple interpretations exist, present them — don't pick silently. If something is unclear, stop and ask before implementing.

**Simplicity first.** Write the minimum code that solves the problem. No speculative features, no abstractions for single-use code, no "flexibility" that wasn't requested. If it can be 50 lines, don't write 200.

**Surgical changes.** Touch only what the request requires. Don't "improve" adjacent code, comments, or formatting. Match existing style. Remove only imports/variables made unused by *your* changes — not pre-existing dead code.

**Goal-driven execution.** Transform tasks into verifiable goals before starting:
- "Fix the bug" → write a test that reproduces it, then make it pass
- "Add validation" → write tests for invalid inputs, then make them pass
- "Refactor X" → ensure tests pass before and after

---

## Critical Guardrails

### 🚨 NEVER Do This

1. **NEVER commit `backend/.env` or `.env`** — Contains DB credentials, already in `.gitignore`
2. **NEVER run backend without sourcing `.env`** — Use `./backend/start.sh`, not `mvn spring-boot:run` directly
3. **NEVER use dev config in production** — Local: `./start.sh prod`, Docker: `./deploy.sh` (auto-uses prod mode)
4. **NEVER store plaintext passwords** — Always use `passwordEncoder.encode()` (BCrypt strength 10)
5. **NEVER skip admin role check on `/api/users/*`** — Use `authService.isAdminByToken()`
6. **NEVER modify user resources without ownership check** — Users can only delete their own resources
7. **NEVER hardcode JWT secret** — Must be in `backend/.env` as `JWT_SECRET`
8. **NEVER allow CORS from all origins** — Only `http://localhost:3001` in development
9. **NEVER use `response.data` when axios interceptor is configured** — Interceptor already returns `response.data`
   ```javascript
   const data = await questionApi.getQuestions()  // ✅ correct
   items.value = response.data || []              // ❌ undefined!
   ```
10. **NEVER add `/api` prefix in API calls** — Axios baseURL is already `/api`
    ```javascript
    apiClient.get('/major-categories')   // ✅ correct
    apiClient.get('/api/major-categories') // ❌ results in /api/api/...
    ```
11. **NEVER forget to populate nested objects in Service DTOs** — Always populate all fields including nested collections
12. **ALWAYS use GlobalExceptionHandler for consistent error responses** — `ResponseStatusException` reason must reach response body

---

### ⚠️ Code Patterns — Recurring Mistakes

**Frontend API calls** — Before writing any API call:
- Read `/frontend/src/api/index.js` lines 4 (baseURL=`/api`) and 27-29 (interceptor unwraps `response.data`)
- Open the target API file, copy-paste method names — never hand-type
- Pattern: `const data = await api.method()` — never `response.data`

**Frontend routing** — Before any `router.push()`:
- Use named routes: `router.push({ name: 'X' })` not hardcoded paths
- When sending query params, verify the destination imports `useRoute` AND reads `route.query.<param>`
- Auto-select logic: always check URL state first (`URL > Default > First item`)

**Backend Controller paths**:
- Check if class has `@RequestMapping` — if yes, method paths are relative; if no, they must be absolute (including `/api`)
- Never duplicate prefix — results in `/api/api/...`

**Backend DTOs**:
- Before adding a field: grep ALL service methods returning this DTO, verify every one populates it
- Copy field names from the Java DTO file — never hand-type to avoid case/spelling mismatch

**Clone/copy operations**:
- List ALL fields in source entity, mark `@Column(nullable=false)` fields
- Copy every non-nullable field; update FK references (e.g., `resume_id`) to new parent

**Business logic**:
- List ALL conditions that affect the guard, not just the "protect" path
- Write a reverse test: what SHOULDN'T trigger the logic must also work

**MCP `@Tool` methods** — Spring AI 1.0.x specifics:
- Transport is **SSE only** (not Streamable HTTP): `GET /mcp/sse` + `POST /mcp/message?sessionId=X`
- `@Tool` methods run on a Reactor thread — `ThreadLocal` and `RequestContextHolder` are **always null**
- Correct userId pattern: `McpAuthFilter` registers `sessionId→userId` in `McpSessionStore`; tools call `McpRequestContext.requireUserId(toolContext, mcpSessionStore)`
- `McpServerSession.getId()` ≠ external sessionId in POST URL — use `McpRequestContext.extractSessionId(exchange)` (walks field chain to transport)
- `@Tool` method params: only `@ToolParam` business params + `ToolContext` — `HttpServletRequest` cannot be injected
- Tools need explicit `MethodToolCallbackProvider` bean — check startup log: `Registered tools: N` (N > 0)
- `jwt.expiration` must be ≥ 2592000000 (30 days) — MCP clients don't auto-refresh tokens

---

### 🚦 Pre-Development Checklist

```
Frontend API Calls:
[ ] Read /frontend/src/api/index.js (lines 4, 27-29)?
[ ] Open target API file, copy-paste method names?
[ ] Endpoints start with "/" WITHOUT "/api" prefix?
[ ] Response pattern: `const data = await api.method()`?

Frontend Router:
[ ] Using named routes (not hardcoded paths)?
[ ] If sending query params: destination reads route.query.<param> AND has a watch on it?
[ ] Auto-select checks URL state first?

Backend:
[ ] Controller @RequestMapping vs absolute path — no duplicate /api?
[ ] Field names copied from DTO file (not typed by hand)?
[ ] All service methods for this DTO populated?

Clone/Copy:
[ ] All @Column(nullable=false) fields copied?
[ ] FK fields updated to new parent?

Business Logic:
[ ] Listed all conditions (both protect AND allow paths)?
[ ] Reverse case tested?

MCP (when touching backend/src/main/java/com/growing/app/mcp/):
[ ] Spring AI 1.0.x → SSE only (not Streamable HTTP)?
[ ] @Tool method uses ToolContext + McpSessionStore (not ThreadLocal)?
[ ] McpToolsConfig has MethodToolCallbackProvider bean?
[ ] Startup log: "Registered tools: N > 0"?
[ ] jwt.expiration >= 2592000000 (30 days)?
```

---

## Quick Start

### 🚨 CRITICAL: Port Configuration
**Frontend**: http://localhost:3001 (NOT 3000!)
**Backend**: http://localhost:8082 (NOT 8080!)

### Backend
```bash
cd backend
./start.sh       # Development mode
./start.sh prod  # Production mode (minimal logs)
# Runs on http://localhost:8082
```

### Frontend
```bash
cd frontend && npm install && npm run dev
# Runs on http://localhost:3001 — proxies /api → http://localhost:8082
```

### Docker (生产环境)
```bash
cp .env.production.example .env
vim .env          # 填入 DB 凭据和 JWT 密钥
./deploy.sh       # 自动使用 prod 模式
# 验证: docker-compose logs backend | grep "profiles are active"  → prod
```

### Database
Credentials in `backend/.env` — see `.env.example`. No values committed.

### Database Backup
Local dev: backup service optional (frontend shows connection error — normal).
Production: included in `docker-compose.yml`, starts with `./deploy.sh`.
```bash
curl http://localhost:5001/health          # verify running
curl http://localhost:5001/backup/list     # list backups
# See docs/BACKUP_SYSTEM.md
```

---

## Tech Stack

**Backend**: Java 17 + Spring Boot 3.2 + Spring Data JPA + MySQL 8.0 + JWT
**Frontend**: Vue 3 + Vue Router 4 + Vite + Tailwind CSS
**Auth**: JWT (HS512, 30d TTL for MCP / 24h for web) + BCrypt + Google OAuth 2.0

## Key Files

**Backend**:
- `backend/.env` — DB credentials, JWT secret (DO NOT COMMIT)
- `backend/start.sh` — Startup script (sources `.env`)
- `src/main/java/com/growing/app/config/SecurityConfig.java` — CORS, password encoder
- `src/main/java/com/growing/app/mcp/` — MCP server (auth filter, session store, tools)

**Frontend**:
- `src/composables/useAuth.js` — Global auth state (currentUser, isAdmin, token)
- `src/api/index.js` — Axios (⚠️ baseURL=`/api`, interceptor unwraps `response.data`)
- `src/router/index.js` — Routes with `meta.requiresAdmin` guards

**Database**:
- `database/schema.sql` — Complete schema (no Flyway — use `mysql-exec` skill for migrations)
- `database/init_data.sql` — Seed data

---

## When Things Break

**401 Unauthorized**:
- Token expired → re-login; for MCP clients update `claude_desktop_config.json` with fresh JWT
- JWT_SECRET mismatch → check `backend/.env`

**403 Forbidden**:
- Not admin → check `role` column in `users` table
- Deleting another user's resource → ownership check failed

**CORS errors** → check `SecurityConfig.corsConfigurationSource()` (only `http://localhost:3001` whitelisted in dev)

**Google OAuth fails** → verify `google.client.id` in `application.properties` + redirect URI in Google Console

**Database connection fails** → verify vars in `backend/.env` match running MySQL

**Frontend shows `undefined`** → check axios pattern (`const data = await api.x()`, not `response.data`) and DTO field population

**MCP tools return 401** → token expired (30d) — get fresh JWT from growing UI → DevTools → Local Storage → `token`

**MCP `tools/list` empty** → check `McpToolsConfig` has `MethodToolCallbackProvider` bean; startup log must show `Registered tools: N > 0`

---

## Current Status

**已完成**: Phases 1–8 (用户管理 → 技能 → 题库 → 算法 → 系统设计 → 通用技能 → 求职管理 → 面试进展 + MCP server)

详见 `/docs/Phase{N}-设计文档.md` 和 `/docs/ARCHITECTURE.md`

## Documentation

| File | Content |
|------|---------|
| `/CLAUDE.md` | Guardrails + quick start (you are here) |
| `/docs/ARCHITECTURE.md` | Data model, API design, security, all phases |
| `/docs/MCP_SETUP.md` | Claude Desktop integration, token setup, troubleshooting |
| `/docs/BACKUP_SYSTEM.md` | Backup service setup and operations |
| `/docs/DOCKER_DEPLOYMENT.md` | Production Docker deployment |
| `/docs/Phase{N}-设计文档.md` | Per-phase design decisions |
| `/requirement/Phase{N}-详细需求.md` | Per-phase requirements |
