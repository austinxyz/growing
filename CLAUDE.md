# Growing App - Claude Code Guide

> **Context Recovery**: Read this first when resuming work.
> **Project Root**: `/Users/yanzxu/claude/growing/`

## Critical Guardrails

### 🚨 NEVER Do This

1. **NEVER commit `backend/.env`** - Contains DB credentials, already in `.gitignore`
2. **NEVER run backend without sourcing `.env`** - Use `./backend/start.sh`, not `mvn spring-boot:run` directly
3. **NEVER store plaintext passwords** - Always use `passwordEncoder.encode()` (BCrypt strength 10)
4. **NEVER skip admin role check on `/api/users/*`** - Use `authService.isAdminByToken()`
5. **NEVER modify user resources without ownership check** - Users can only delete their own resources
6. **NEVER hardcode JWT secret** - Must be in `backend/.env` as `JWT_SECRET`
7. **NEVER allow CORS from all origins** - Only `http://localhost:3000` in development (see `SecurityConfig.java`)
8. **NEVER use `response.data` when axios interceptor is configured** - Interceptor already returns `response.data`, use `response` directly
   ```javascript
   // ❌ WRONG - response.data is undefined!
   const response = await questionApi.getQuestions()
   items.value = response.data || []

   // ✅ CORRECT
   const data = await questionApi.getQuestions()
   items.value = data || []
   ```
9. **NEVER add `/api` prefix in API calls** - Axios baseURL is already `/api` (see `/frontend/src/api/index.js:4`)
   ```javascript
   // ❌ WRONG - results in /api/api/major-categories
   apiClient.get('/api/major-categories')

   // ✅ CORRECT
   apiClient.get('/major-categories')
   ```
10. **NEVER forget to populate nested objects in Service DTOs** - Check `SkillService.getSkillsByCareerPathId()` includes `focusAreas`

### ⚠️ Common Mistakes from Previous Prompts

**Mistake #1**: Asking for "归纳到CLAUDE.md文件中去，但保持这个文件简洁"
- **Problem**: You asked me to add architecture details to CLAUDE.md, making it bloated
- **Fix**: Architecture details belong in `docs/ARCHITECTURE.md`, CLAUDE.md is for guardrails only

**Mistake #2**: Not specifying what CLAUDE.md should contain
- **Problem**: Generic "summarize architecture" leads to verbose output
- **Fix**: Explicitly state "only critical guardrails and quick start commands"

**Mistake #3**: Asking to "review" design documents without specific criteria
- **Problem**: I'll just parrot back what's in the doc without value-add
- **Fix**: Ask specific questions like "what security issues exist?" or "what's missing?"

**Mistake #4 (Phase 3)**: Not understanding axios interceptor behavior
- **Problem**: Axios response interceptor returns `response.data`, but code used `response.data` again → `undefined`
- **What Happened**: Bug appeared TWICE in Phase 3 (QuestionManagement.vue, MyQuestionBank.vue)
- **Root Cause**: `/frontend/src/api/index.js:27-29` has interceptor that auto-unwraps
- **Fix**: Always use `response` directly, not `response.data` (see Guardrail #8)
- **Lesson**: When debugging "data not showing", check interceptors first

**Mistake #5 (Phase 3)**: Incomplete DTO conversion in Service layer
- **Problem**: `SkillService.getSkillsByCareerPathId()` didn't populate `focusAreas` list
- **What Happened**: Frontend couldn't display Focus Area tree because backend returned empty array
- **Root Cause**: DTO conversion only set `focusAreaCount`, forgot to add actual `focusAreas` list
- **Fix**: Added `dto.setFocusAreas(focusAreaRepository.find...)`
- **Lesson**: When adding new fields to DTOs, update ALL methods that return that DTO

**Mistake #6 (Phase 3)**: Poor UX from not considering user context
- **Problem**: User already viewing Focus Area X, but modal still asked "which Focus Area?"
- **What Happened**: Required user to re-select Focus Area when adding question
- **Root Cause**: Modal didn't receive current context as prop
- **Fix**: Pass `currentFocusAreaId` and `currentFocusAreaName` props, conditionally render read-only field
- **Lesson**: Before implementing forms, think about user's current context and pre-fill what you can

**Mistake #7 (Phase 4)**: Adding `/api` prefix when axios baseURL already includes it
- **Problem**: API calls like `apiClient.get('/api/major-categories')` resulted in `/api/api/major-categories` (404)
- **What Happened**: Created `majorCategoryApi.js` with `/api/` prefix, but axios baseURL is already `/api`
- **Root Cause**: Forgot that `/frontend/src/api/index.js:4` sets `baseURL: '/api'`
- **Fix**: API calls should NEVER include `/api` prefix - just use `apiClient.get('/major-categories')`
- **Lesson**: ALWAYS check axios config before writing API calls. Frontend axios baseURL = `/api`, so endpoint paths start with `/` (e.g., `/users`, `/skills`)

## Quick Start

### Backend
```bash
cd backend
./start.sh  # Sources .env and runs Spring Boot
# Runs on http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:3000
# Proxies /api → http://localhost:8080
```

### Database
- **Host**: 10.0.0.7:37719
- **Database**: `growing`
- **User**: `austinxu` / `helloworld`
- **Credentials**: In `backend/.env` (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD)

### Test Admin Account
```
Username: austinxu
Email: austin@example.com
Password: helloworld
Role: admin
```

## Tech Stack

**Backend**: Java 17 + Spring Boot 3.2 + Spring Data JPA + MySQL 8.0 + JWT
**Frontend**: Vue 3 + Vue Router 4 + Vite + Tailwind CSS
**Auth**: JWT (HS384, 24h TTL) + BCrypt + Google OAuth 2.0

## Key Files

**Backend**:
- `backend/.env` - Database credentials, JWT secret (DO NOT COMMIT)
- `backend/start.sh` - Startup script that sources `.env`
- `src/main/java/com/growing/app/config/SecurityConfig.java` - CORS, password encoder
- `src/main/java/com/growing/app/util/JwtUtil.java` - JWT token utilities

**Frontend**:
- `src/composables/useAuth.js` - Global auth state (currentUser, isAdmin, token)
- `src/api/index.js` - Axios instance with JWT interceptor + response unwrapper (⚠️ returns `response.data`)
- `src/router/index.js` - Routes with `meta.requiresAdmin` guards

**Database**:
- `backend/src/main/resources/db/migration/` - Flyway migration files
- Migrations run automatically on app startup

## When Things Break

**401 Unauthorized**:
- Token expired (24h TTL) → user needs to re-login
- JWT_SECRET mismatch → check `backend/.env` matches running backend

**403 Forbidden**:
- User authenticated but not admin → check `role` column in `users` table
- Deleting someone else's resource → ownership check failed

**CORS errors**:
- Frontend URL not allowed → check `SecurityConfig.corsConfigurationSource()`
- Only `http://localhost:3000` is whitelisted in dev

**Google OAuth fails**:
- Check `google.client.id` in `application.properties` matches Google Cloud Console
- Verify redirect URI configured in Google Console

**Database connection fails**:
- Check all vars in `backend/.env` match MySQL server settings
- Verify MySQL is running on 10.0.0.7:37719

**Frontend data not showing** (Phase 3 bug):
- Check browser console for `undefined` errors
- Verify axios response handling: use `response` not `response.data` (interceptor already unwrapped)
- Check backend Service methods populate all DTO fields (especially nested objects like `focusAreas`)

## Current Status

**Phase 1 完成** (2025-12-20):
- ✅ JWT auth + Google OAuth
- ✅ User management (admin CRUD)
- ✅ Login/register pages
- ✅ Route guards + JWT interceptor

**Phase 2 完成** (2025-12-20):
- ✅ Skills management (admin CRUD)
- ✅ Focus areas management
- ✅ Learning resources with visibility control
- ✅ User skill browsing
- ✅ Personal resource management

**Phase 3 完成** (2025-12-21):
- ✅ 试题库基础功能
  - questions表 + user_question_notes表 (V5 migration)
  - 基于Focus Area的试题管理（公共试题 + 用户私有试题）
  - 用户笔记功能（UPSERT逻辑 - 一个用户对一个试题只能有一条笔记）
  - 管理员页面：两栏布局 (QuestionManagement.vue)
  - 用户页面：三栏布局 (MyQuestionBank.vue) - 职业路径Tab + 技能树 + 试题列表 + 详情+笔记
  - Markdown渲染支持（问题描述、答案要求、笔记）
  - Red Flags JSON序列化存储
  - 8道初始试题导入（Python脚本）
  - 4个bug修复（axios响应处理 × 2、DTO不完整、UX优化）

## Documentation

**Quick Reference** (you are here): `/CLAUDE.md`
**Architecture Details**: `/docs/ARCHITECTURE.md` (data model, API design, security, performance, Phase 3 patterns)
**Phase Designs**: `/docs/Phase1-设计文档.md`, `/docs/Phase2-设计文档.md`, `/docs/Phase3-设计文档.md`
**Requirements**: `/requirement/Phase1-详细需求.md`, `/requirement/Phase2-详细需求.md`, `/requirement/Phase3-详细需求.md`

## Prompt Writing Tips for User

When asking Claude Code to update this project:

✅ **Good Prompts**:
- "Add X feature following Phase 2 patterns"
- "Check if Y violates any security guardrails"
- "Create Phase 3 design doc, don't update CLAUDE.md yet"

❌ **Bad Prompts**:
- "Summarize everything into CLAUDE.md" (too vague, makes it bloated)
- "Make the code better" (no specific criteria)
- "Add architecture to CLAUDE.md" (wrong file, use ARCHITECTURE.md)

**Key Principle**: CLAUDE.md = guardrails + quick start. Architecture details = separate docs.
