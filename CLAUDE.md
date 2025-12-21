# Growing App - Claude Code Guide

> **Context Recovery**: When resuming, read this file first.
> **Project Root**: `/Users/yanzxu/claude/growing/`

## Critical Guardrails

### Environment & Security

**NEVER commit `backend/.env`** - Contains DB credentials. Already in `.gitignore`.

**ALWAYS use `./backend/start.sh`** - Sources `.env` and runs `mvn spring-boot:run`.

**Passwords**: Always use `passwordEncoder.encode()` - BCrypt strength 10.

**JWT**: HS384, 24h expiration, secret in `backend/.env` as `JWT_SECRET`.

**Admin routes**: All `/api/users/*` require admin role check. Use `authService.isAdminByToken()`.

**CORS**: Only `http://localhost:3000` in development (see `SecurityConfig.java`).

### Database

**Connection**: Credentials in `backend/.env` (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD).

**Schema**:
```
users (id, username, email, password_hash, role, ...)
  ↓ many-to-many via user_career_paths
career_paths (id, name, code, description, icon)
```

**Indexes**: username (unique), email (unique), role.

**Foreign keys**: CASCADE delete on `user_career_paths`.

## Tech Stack

**Backend**: Java 17 + Spring Boot 3.2 + Spring Data JPA + MySQL 8.0
**Frontend**: Vue 3 Composition API + Vue Router 4 + Vite + Tailwind CSS
**Security**: JWT (jjwt 0.12.3) + BCrypt + Google OAuth 2.0

## Quick Start

### Backend
```bash
cd backend
./start.sh
# Runs on http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:3000
# Proxies /api to http://localhost:8080
```

### Test Admin Account
```
Username: austinxu
Email: austin@example.com
Password: helloworld
Role: admin
```

## Architecture Patterns

### JWT Authentication Flow

1. Frontend: `POST /api/auth/login` with credentials
2. Backend: validates password → generates JWT (username in subject)
3. Frontend: stores in `localStorage.auth_token`
4. Frontend: Axios interceptor adds `Authorization: Bearer {token}`
5. Backend: extracts username from JWT → loads User from DB

### Admin Permission Check

```java
// UserController.java pattern
private void requireAdmin(String authHeader) {
    if (!authService.isAdminByToken(authHeader.replace("Bearer ", ""))) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
    }
}
```

### Google OAuth Flow

1. Frontend: Google SDK initialized with client ID
2. User clicks Google button → gets credential (ID token)
3. Frontend: `POST /api/auth/google` with credential
4. Backend: verifies with Google API → creates/updates user → returns JWT
5. Same JWT flow as traditional login

## File Structure

```
backend/src/main/java/com/growing/app/
  controller/AuthController.java    # login, register, Google OAuth
  controller/UserController.java    # admin-only user CRUD
  service/AuthService.java          # JWT generation, password verify
  service/UserService.java          # user CRUD, DTO conversion
  model/User.java                   # JPA entity with @ManyToMany careerPaths
  config/SecurityConfig.java        # CORS, PasswordEncoder beans
  util/JwtUtil.java                 # JWT token util

frontend/src/
  composables/useAuth.js            # global auth state (currentUser, token, isAdmin)
  api/auth.js                       # login, register, Google OAuth APIs
  api/index.js                      # Axios instance with JWT interceptor
  router/index.js                   # routes with meta.requiresAdmin guard
  views/auth/Login.vue              # login page with Google button
  components/Sidebar.vue            # navigation with role-based menu
```

## API Reference

**Auth** (`/api/auth`):
- `POST /login` - username/password → JWT + user
- `POST /register` - create account → JWT + user
- `POST /google` - Google ID token → JWT + user
- `GET /me` - get current user (requires Bearer token)

**Users** (`/api/users`) - Admin only:
- `GET /` - list all users with career paths
- `POST /` - create user (can set role)
- `PUT /{id}` - update user (fullName, bio, role, careerPaths)
- `DELETE /{id}` - delete user

**Career Paths** (`/api/career-paths`):
- `GET /` - list all active paths (public)

## Troubleshooting

**401 Unauthorized**: Token expired (24h TTL) or JWT_SECRET mismatch between `.env` and backend.

**403 Forbidden**: User authenticated but missing admin role for `/api/users/*`.

**CORS errors**: Check frontend URL allowed in `SecurityConfig.corsConfigurationSource()`.

**Google OAuth fails**: Verify `google.client.id` in `application.properties` matches Google Cloud Console.

**Database connection fails**: Check all vars in `backend/.env` match MySQL server settings.

## Current Status

**Phase 1 完成** (2025-12-20)

✅ JWT auth + Google OAuth
✅ User management API (admin CRUD)
✅ Frontend login/register pages
✅ Route guards + Axios interceptor

🔄 Building Dashboard + Sidebar UI

## Additional Context

For complex JWT usage or Google OAuth errors, see `docs/Phase1-设计文档.md`.

For detailed requirements, see `requirement/Phase1-详细需求.md`.
