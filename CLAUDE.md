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
- `src/api/index.js` - Axios instance with JWT interceptor
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

**Phase 3 设计完成** (2025-12-21):
- ✅ 详细需求文档（`requirement/Phase3-详细需求.md`）
- ✅ 设计文档（`docs/Phase3-设计文档.md`）
- 🔄 待实施：试题库基础功能
  - questions表 + user_question_notes表
  - 基于Focus Area的试题管理
  - 用户笔记功能（UPSERT逻辑）
  - 单页面三栏布局UI

## Documentation

**Quick Reference** (you are here): `/CLAUDE.md`
**Architecture Details**: `/docs/ARCHITECTURE.md` (data model, API design, security, performance)
**Phase Designs**: `/docs/Phase1-设计文档.md`, `/docs/Phase2-设计文档.md`
**Requirements**: `/requirement/Phase1-详细需求.md`, `/requirement/Phase2-详细需求.md`

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
