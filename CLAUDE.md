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
7. **NEVER allow CORS from all origins** - Only `http://localhost:3001` in development (see `SecurityConfig.java`)
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
11. **ALWAYS use GlobalExceptionHandler for consistent error responses** - `ResponseStatusException` reason must be included in response body (see `GlobalExceptionHandler.java:24`)

### ⚠️ Common Mistakes from Previous Prompts

**📂 Category 1: Documentation Management Mistakes**

**Mistake #1**: Asking for "归纳到CLAUDE.md文件中去，但保持这个文件简洁"
- **Problem**: You asked me to add architecture details to CLAUDE.md, making it bloated
- **Fix**: Architecture details belong in `docs/ARCHITECTURE.md`, CLAUDE.md is for guardrails only
- **🛡️ How to Avoid**: Before asking for docs updates, specify EXACTLY which file gets what (e.g., "Update CLAUDE.md with [error pattern X], keep under 10 lines, focus on prevention checklist")

**Mistake #2**: Not specifying what CLAUDE.md should contain
- **Problem**: Generic "summarize architecture" leads to verbose output
- **Fix**: Explicitly state "only critical guardrails and quick start commands"
- **🛡️ How to Avoid**: Use template: "Update CLAUDE.md with [specific error], show [prevention checklist/code example]"

**Mistake #3**: Asking to "review" design documents without specific criteria
- **Problem**: I'll just parrot back what's in the doc without value-add
- **Fix**: Ask specific questions like "what security issues exist?" or "what's missing?"
- **🛡️ How to Avoid**: Provide review criteria: security, performance, edge cases, UX, or missing validations

---

**🌐 Category 2: Frontend Axios Mistakes** ⚠️ **HIGHEST FREQUENCY - 2 mistakes in 2 phases!**

**Root Cause**: Not checking `/frontend/src/api/index.js` before writing API code

**Mistake #4 (Phase 3)**: Using `response.data` when interceptor already unwrapped it
- **Problem**: Axios interceptor returns `response.data`, but code used `response.data` again → `undefined`
- **What Happened**: Bug appeared TWICE (QuestionManagement.vue, MyQuestionBank.vue)
- **Root Cause**: `/frontend/src/api/index.js:27-29` auto-unwraps `response.data`
- **Fix**: Always use `response` directly (see Guardrail #8)
- **🛡️ Prevention Checklist**:
  1. [ ] Read `/frontend/src/api/index.js:27-29` BEFORE writing any API call
  2. [ ] Add comment in new API files: `// Note: interceptor returns response.data already`
  3. [ ] Use pattern: `const data = await api.method()` (NOT `const response = ...`)
  4. [ ] Test with browser console to verify data structure

**Mistake #7 (Phase 4)**: Adding `/api` prefix when baseURL already includes it
- **Problem**: `apiClient.get('/api/major-categories')` → `/api/api/major-categories` (404)
- **What Happened**: Created `majorCategoryApi.js` with duplicate `/api/` prefix
- **Root Cause**: Forgot `/frontend/src/api/index.js:4` sets `baseURL: '/api'`
- **Fix**: Endpoints NEVER include `/api` - just `apiClient.get('/major-categories')`
- **🛡️ Prevention Checklist**:
  1. [ ] Read `/frontend/src/api/index.js:4` to confirm baseURL
  2. [ ] Memorize: `baseURL = '/api'` → endpoints start with `/` (e.g., `/users`)
  3. [ ] Review existing API file (e.g., `questionApi.js`) as reference
  4. [ ] Add template comment:
     ```javascript
     // ⚠️ baseURL is '/api', do NOT add '/api' prefix!
     // ❌ WRONG: apiClient.get('/api/users')
     // ✅ CORRECT: apiClient.get('/users')
     ```

**🚨 MANDATORY Checklist Before Writing ANY Frontend API Code**:
- [ ] Opened `/frontend/src/api/index.js` and checked lines 4 (baseURL) and 27-29 (interceptor)?
- [ ] Reviewed at least ONE existing API file for reference?
- [ ] API endpoints start with `/` WITHOUT `/api` prefix?
- [ ] API response pattern is `const data = await api.method()`?
- [ ] Added template comment warning about axios config?

---

**🔧 Category 3: Backend Service Layer Mistakes**

**Mistake #5 (Phase 3)**: Incomplete DTO conversion
- **Problem**: `SkillService.getSkillsByCareerPathId()` didn't populate `focusAreas` list
- **What Happened**: Frontend got empty array, couldn't display Focus Area tree
- **Root Cause**: DTO only set `focusAreaCount`, forgot `focusAreas` list itself
- **Fix**: Added `dto.setFocusAreas(focusAreaRepository.find...)`
- **🛡️ Prevention Checklist** (when adding DTO fields):
  1. [ ] Grep ALL Service methods returning this DTO
  2. [ ] For each method: verify new field is populated
  3. [ ] Add unit test checking DTO completeness
  4. [ ] Use code comment:
     ```java
     // DTO Completeness Checklist:
     // [ ] Primitives set? [ ] Nested collections? [ ] Computed fields?
     ```

---

**🎨 Category 4: UX Design Mistakes**

**Mistake #6 (Phase 3)**: Not pre-filling user context in forms
- **Problem**: User viewing Focus Area X, but modal asked "which Focus Area?" again
- **What Happened**: Required re-selection of known context
- **Root Cause**: Modal didn't receive `currentFocusAreaId` prop
- **Fix**: Pass context props, conditionally render read-only vs editable
- **🛡️ Prevention Checklist** (before implementing forms/modals):
  1. [ ] Asked: "What context does the user ALREADY have?"
  2. [ ] Pre-filled ALL inferrable fields from current context
  3. [ ] Used pattern:
     ```vue
     <!-- Pass context -->
     <Modal :current-id="selectedId" :current-name="selectedName" />
     <!-- Conditionally render -->
     <div v-if="currentName">{{ currentName }}</div> <!-- read-only -->
     <select v-else v-model="form.id">...</select> <!-- editable -->
     ```
  4. [ ] Tested with real user workflow: "Would I be annoyed?"

---

**🎯 Category 5: Phase 6 New Error Patterns**

**Mistake #8 (Phase 6)**: Backend API路径包含重复的`/api`前缀
- **Problem**: Controller无`@RequestMapping("/api")`注解，但路径写成`@GetMapping("/api/skills/...")`
- **What Happened**: 实际URL变成`/api/api/skills/...`导致404（commit 8e04ecb）
- **Root Cause**: 复制粘贴其他Controller代码，没注意路径配置差异
- **Fix**: Controller路径只写业务路径，不含`/api`（Spring Boot自动添加）
- **🛡️ Prevention Checklist**:
  1. [ ] 新Controller检查是否有`@RequestMapping`类级注解？
  2. [ ] 如果有：方法路径只写业务路径（如`/skills/{id}`）
  3. [ ] 如果无：方法路径写完整路径（如`/api/skills/{id}`）
  4. [ ] 参考同项目其他Controller的路径配置模式

**Mistake #9 (Phase 6)**: 前后端字段名不一致
- **Problem**: 前端用`questionText`，后端DTO是`questionDescription`（commit 9243698）
- **What Happened**: 数据无法正确渲染，显示undefined
- **Root Cause**: 没有查看后端DTO定义就写前端代码
- **Fix**: 使用后端DTO的精确字段名`questionDescription`
- **🛡️ Prevention Checklist**:
  1. [ ] 写前端代码前，先读后端DTO文件（如`QuestionDTO.java`）
  2. [ ] 复制后端字段名到前端，避免手打导致typo
  3. [ ] 测试时检查浏览器Network tab的实际响应JSON
  4. [ ] 添加注释标注字段来源：`// From QuestionDTO.questionDescription`

**Mistake #10 (Phase 6)**: 业务逻辑判断不完整
- **Problem**: General分类保护逻辑无条件阻止删除，忽略`isGeneralOnly`标志（commit ad804fc）
- **What Happened**: `isGeneralOnly=false`技能无法删除错误创建的General分类
- **Root Cause**: 业务规则只考虑了"保护General"，没考虑"哪些技能需要保护"
- **Fix**: 检查`skill.isGeneralOnly`，只保护需要General的技能
- **🛡️ Prevention Checklist**:
  1. [ ] 写if判断前，先列出所有应该检查的条件
  2. [ ] 业务规则添加反向测试：不该保护的场景能正常操作？
  3. [ ] 添加注释说明判断逻辑：`// Only protect General for isGeneralOnly=true`
  4. [ ] 查看相关实体的所有布尔字段，是否影响判断？

---

**📊 Quality Metrics (Updated)**:
| Metric | Phase 3 | Phase 4 | Phase 5 | Phase 6 |
|--------|---------|---------|---------|---------|
| Axios bugs | 2 | 1 | **0** ✅ | **0** ✅ |
| DTO bugs | 1 | 1 | **0** ✅ | **0** ✅ |
| **Backend API bugs** | 0 | 0 | 0 | **1** ⚠️ |
| **Field name bugs** | 0 | 0 | 0 | **1** ⚠️ |
| **Logic bugs** | 0 | 0 | 0 | **1** ⚠️ |
| Code reuse | 30% | 50% | 70% | **80%** |

**Phase 6 Lessons**:
- ✅ Axios/DTO bugs成功避免（预防性检查清单有效）
- ⚠️ 出现3个新类型错误（后端API路径、字段名、业务逻辑）
- **教训**: 检查清单需要扩展到后端API和业务逻辑

**🚨 UPDATED Pre-Development Checklist**:
```
Frontend:
[ ] Read /frontend/src/api/index.js (lines 4, 27-29)?
[ ] Review existing API file as pattern?
[ ] Endpoints start with "/" WITHOUT "/api" prefix?
[ ] Response: "const data = await api.method()" pattern?

Backend:
[ ] Check Controller @RequestMapping annotation exists?
[ ] Method paths match annotation style (relative or absolute)?
[ ] Read target DTO file before using field names?
[ ] Copy field names from DTO, don't hand-type?

DTOs:
[ ] Grep all Service methods returning this DTO?
[ ] Verify ALL fields populated (primitives, nested, computed)?

Business Logic:
[ ] List ALL conditions that should affect this logic?
[ ] Test reverse case: what SHOULDN'T trigger this logic?
[ ] Check related entity boolean fields that may affect logic?
```

## Quick Start

### 🚨 CRITICAL: Port Configuration
**Frontend**: http://localhost:3001 (NOT 3000!)
**Backend**: http://localhost:8082 (NOT 8080!)
**NEVER assume default ports** - Always check these ports before debugging

### Backend
```bash
cd backend
./start.sh  # Sources .env and runs Spring Boot
# Runs on http://localhost:8082
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:3001
# Proxies /api → http://localhost:8082
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
- `database/schema.sql` - Complete database schema (不使用 Flyway)
- `database/init_data.sql` - Initial data
- **数据库迁移方式**: 使用 `mysql-exec` skill 执行 SQL 文件，不使用 Flyway

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

**已完成Phases**: 1-6 (用户管理 → 技能学习 → 试题库 → 算法学习 → 系统设计 → 通用技能)

**详细信息**: 查看各Phase设计文档 (`/docs/Phase{N}-设计文档.md`)

**Phase 6 Quality Metrics** (最新):
| Metric | Phase 3 | Phase 4 | Phase 5 | Phase 6 |
|--------|---------|---------|---------|---------|
| Axios bugs | 2 | 1 | **0** ✅ | **0** ✅ |
| DTO bugs | 1 | 1 | **0** ✅ | **0** ✅ |
| Backend API bugs | 0 | 0 | 0 | **1** ⚠️ |
| Code reuse | 30% | 50% | 70% | **80%** |

## Documentation

**Quick Reference** (you are here): `/CLAUDE.md`
**Architecture Details**: `/docs/ARCHITECTURE.md` (data model, API design, security, performance, all phases)
**Phase Designs**:
- `/docs/Phase1-设计文档.md` - User management + JWT auth + Google OAuth
- `/docs/Phase2-设计文档.md` - Skills + Focus Areas + Learning Resources
- `/docs/Phase3-设计文档.md` - Question bank + User notes
- `/docs/Phase4-设计文档.md` - Algorithm learning + Templates + Learning stages
- `/docs/Phase5-设计文档.md` - System design learning (basics + cases)
- `/docs/Phase6-设计文档.md` - General skills (cloud, behavioral) + Answer templates
**Requirements**:
- `/requirement/Phase1-详细需求.md`
- `/requirement/Phase2-详细需求.md`
- `/requirement/Phase3-详细需求.md`
- `/requirement/Phase4-详细需求.md`
- `/requirement/Phase5-详细需求.md`
- `/requirement/Phase6-详细需求.md`

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
