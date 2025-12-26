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

**🎯 Category 5: Phase 5 Best Practices - What Finally Worked**

**Context**: Phase 5 had ZERO axios bugs and ZERO DTO bugs - first clean phase!

**Success Factor #1: Clear Requirements Upfront**
- **What User Did**: Provided reference design (HelloInterview) at start
- **What User Did**: Explicitly stated architecture decisions ("reuse Skill-FocusArea for basics, separate tables for cases")
- **Result**: No architectural rework needed
- **🛡️ Lesson**: Reference designs save 10x time vs describing from scratch

**Success Factor #2: Iterative Review Process**
- **What User Did**: "Give me requirements outline first, I'll review, then we do UI design"
- **What User Did**: Reviewed outline before implementation started
- **Result**: Caught issues early, no mid-implementation pivots
- **🛡️ Lesson**: Review in 2 stages (requirements → UI) catches more issues than single review

**Success Factor #3: Prevention Checklists Actually Work**
- **What Changed**: After Phase 3/4, added MANDATORY axios checklist (Guardrails #8-9)
- **What Happened**: Phase 5 had zero `response.data` bugs, zero `/api/api` bugs
- **Result**: First clean phase without rework
- **🛡️ Lesson**: Checklists MUST be mandatory (block development until checked)

**Success Factor #4: Accepting Design Evolution**
- **What Happened**: V16 migration added kp_* fields mid-development
- **Why It Worked**: User said "add these fields" without asking "why didn't we plan this?"
- **Result**: Quick iteration, no blame, feature improved
- **🛡️ Lesson**: Some features emerge during development - this is OK, not a failure

**🚨 NEW MANDATORY Pre-Development Checklist (Based on Phase 5)**:
```
Before starting ANY new phase:
[ ] User provided reference design? (website, app, example)
[ ] User explicitly stated architecture decisions? (reuse vs new tables)
[ ] Requirements outline created and reviewed by user?
[ ] UI design reviewed by user before coding?
[ ] Axios prevention checklist reviewed? (Guardrails #8-9)
[ ] DTO completeness checklist reviewed? (Guardrail #10)
```

**Comparison: Phase 3 → Phase 4 → Phase 5**:
| Metric | Phase 3 | Phase 4 | Phase 5 |
|--------|---------|---------|---------|
| Axios bugs | 2 | 1 | **0** ✅ |
| DTO bugs | 1 | 1 | **0** ✅ |
| Reference design | ❌ | ❌ | ✅ HelloInterview |
| Requirements review | ❌ | ❌ | ✅ 2-stage |
| Design evolution | ❌ Seen as failure | ❌ Seen as failure | ✅ Accepted V16 |

---

**📊 Meta-Analysis: Why Axios Mistakes Repeat**

**Pattern**: Axios config mistakes in Phase 3 AND Phase 4
**Why**: Guardrails #8 and #9 are REACTIVE (written AFTER bugs), not PROACTIVE

**Breaking the Pattern**:
- ✅ Add prevention checklists (done above)
- ✅ Create `/frontend/src/api/README.md` with axios config summary (TODO)
- ✅ Update CLAUDE.md after EVERY bug fix with "How to Avoid" section
- ⚠️ Consider ESLint rule to detect `/api/api` paths (investigate feasibility)
- **✅ Phase 5 Result: Zero axios bugs - checklists work!**

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

**Phase 4 完成** (2025-12-23 至 2025-12-24):
- ✅ 算法与数据结构学习模块
  - major_categories + focus_area_categories多对多关系 (V8-V12 migrations)
  - learning_stages（基础、进阶、强化）三阶段学习体系
  - programming_question_details表（LeetCode链接、代码片段、复杂度）
  - user_template_notes表（算法模版笔记）
  - 管理员页面：三栏布局（大分类Tab + Focus Area + 学习资料）
  - 用户学习页面：单页显示（理论 → 代码 → 练习连续阅读）
  - 算法模版库：模版管理 + 题目关联
  - 学习总结页面：272道题的核心策略批量展示
  - 225篇labuladong文章导入 + 272道编程题导入
  - 1个axios bug修复（/api前缀重复）+ 1个DTO bug修复（focusAreas未填充）

**Phase 5 完成** (2025-12-25 至 2025-12-26):
- ✅ 系统设计学习模块（参考HelloInterview）
  - **基础知识模块**：复用Skill + Focus Area + Learning Content架构（3大分类：核心概念、关键技术、设计模式）
  - **典型案例模块**：5张独立表（V15 + V16 migrations）
    - system_design_cases（案例主表：标题、难度、公司标签、关联知识）
    - case_resources（学习资源：视频、文章）
    - case_solutions（参考答案：支持多方案，6步骤框架）
    - solution_diagrams（配图：架构图、数据流图、实体图）
    - user_case_notes（用户答题：双轨制 - 6步骤 + 7个结构化关键点）
  - 管理员页面：三栏布局（案例列表 + 详情+资源 + 参考答案）
  - 用户案例页面：双模式（查看模式 + 左右对比编辑模式，14个Tab）
  - 学习总结页面：横向对比所有案例的关键要点
  - 答案可见性控制：编辑时可隐藏参考答案，鼓励独立思考
  - Focus Area关联和导航：从案例跳转到基础知识学习
  - **零axios bug，零DTO bug** - 成功应用Phase 3/4的教训

## Documentation

**Quick Reference** (you are here): `/CLAUDE.md`
**Architecture Details**: `/docs/ARCHITECTURE.md` (data model, API design, security, performance, all phases)
**Phase Designs**:
- `/docs/Phase1-设计文档.md` - User management + JWT auth + Google OAuth
- `/docs/Phase2-设计文档.md` - Skills + Focus Areas + Learning Resources
- `/docs/Phase3-设计文档.md` - Question bank + User notes
- `/docs/Phase4-设计文档.md` - Algorithm learning + Templates + Learning stages
- `/docs/Phase5-设计文档.md` - System design learning (basics + cases)
**Requirements**:
- `/requirement/Phase1-详细需求.md`
- `/requirement/Phase2-详细需求.md`
- `/requirement/Phase3-详细需求.md`
- `/requirement/Phase4-详细需求.md`
- `/requirement/Phase5-详细需求.md`

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
