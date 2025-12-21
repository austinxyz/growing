# Growing App - Architecture Guide

> **Purpose**: Detailed technical architecture reference
> **For quick context recovery, see**: `/CLAUDE.md`

## System Architecture

### Technology Stack

**Backend**:
- Java 17 + Spring Boot 3.2.0
- Spring Data JPA + Hibernate
- MySQL 8.0/9.4
- Flyway (database migrations)
- JWT Authentication (jjwt 0.12.3)
- BCrypt password hashing (strength 10)
- Google OAuth 2.0

**Frontend**:
- Vue 3 Composition API
- Vue Router 4
- Vite
- Tailwind CSS
- Axios (HTTP client)
- Google Identity Services

### Data Model

```
users (id, username, email, password_hash, role, ...)
  ↓ many-to-many via user_career_paths
career_paths (id, name, code, description, icon)
  ↓ many-to-many via career_path_skills
skills (id, name, description, is_important, icon, display_order)
  ├─ one-to-many → focus_areas (id, skill_id, name, description)
  └─ one-to-many → learning_resources (id, skill_id, resource_type, title, url, is_official, created_by_user_id)
```

**Key Constraints**:
- username, email: unique indexes
- career_path_skills: unique(career_path_id, skill_id)
- CASCADE delete: career_paths → career_path_skills, skills → focus_areas/learning_resources, users → learning_resources

**Resource Types ENUM**: `BOOK`, `COURSE`, `WEBSITE`, `ARTICLE`, `VIDEO`, `DOCUMENT`, `BLOG`, `PRACTICE`, `TOOL`, `OTHER`

### Authentication Flow

**JWT Authentication**:
1. Frontend: `POST /api/auth/login` with credentials
2. Backend: validates password → generates JWT (username in subject)
3. Frontend: stores in `localStorage.auth_token`
4. Frontend: Axios interceptor adds `Authorization: Bearer {token}`
5. Backend: extracts username from JWT → loads User from DB

**Google OAuth**:
1. Frontend: Google SDK initialized with client ID
2. User clicks Google button → gets credential (ID token)
3. Frontend: `POST /api/auth/google` with credential
4. Backend: verifies with Google API → creates/updates user → returns JWT
5. Same JWT flow as traditional login

**Token Details**:
- Algorithm: HS384
- Expiration: 24 hours
- Secret: stored in `backend/.env` as `JWT_SECRET`
- Payload: subject (username), issuedAt, expiration

### Authorization Patterns

**Admin Permission Check**:
```java
private void requireAdmin(String authHeader) {
    if (!authService.isAdminByToken(authHeader.replace("Bearer ", ""))) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
    }
}
```

**Resource Visibility Control** (Learning Resources):
- Users see: official resources + their own resources
- SQL: `WHERE skill_id = ? AND (is_official = true OR created_by_user_id = ?)`
- Users can only delete their own resources
- Admins can see and manage all resources

### Backend Architecture

**Layer Structure**:
```
Controller Layer
  ├─ AuthController       (login, register, Google OAuth)
  ├─ UserController       (admin-only user CRUD)
  ├─ SkillController      (user skill queries)
  └─ AdminSkillController (admin skill/focus/resource CRUD)

Service Layer
  ├─ AuthService          (JWT generation, password verify, token validation)
  ├─ UserService          (user CRUD, DTO conversion)
  ├─ SkillService         (skill CRUD, visibility filtering)
  ├─ FocusAreaService     (focus area CRUD)
  └─ LearningResourceService (resource CRUD with ownership check)

Repository Layer
  ├─ UserRepository
  ├─ CareerPathRepository
  ├─ SkillRepository
  ├─ FocusAreaRepository
  └─ LearningResourceRepository

Model Layer
  ├─ User                 (@ManyToMany careerPaths)
  ├─ CareerPath           (@ManyToMany skills)
  ├─ Skill                (@ManyToMany careerPaths, @OneToMany focusAreas/resources)
  ├─ FocusArea            (@ManyToOne skill)
  └─ LearningResource     (@ManyToOne skill, @ManyToOne createdByUser)
```

**JPA Patterns**:

Cascading deletes:
```java
@OneToMany(mappedBy = "skill", cascade = CascadeType.ALL,
           orphanRemoval = true, fetch = FetchType.LAZY)
private List<FocusArea> focusAreas = new ArrayList<>();
```

Resource visibility query:
```java
@Query("SELECT lr FROM LearningResource lr " +
       "WHERE lr.skill.id = :skillId " +
       "AND (lr.isOfficial = true OR lr.createdByUser.id = :userId) " +
       "ORDER BY lr.displayOrder ASC, lr.createdAt ASC")
List<LearningResource> findBySkillIdAndVisibleToUser(
    @Param("skillId") Long skillId,
    @Param("userId") Long userId);
```

### Frontend Architecture

**State Management**:
- Global auth state: `composables/useAuth.js`
- Provides: `currentUser`, `token`, `isAdmin`, `login()`, `logout()`, `updateUser()`
- Reactive state shared across components

**API Organization**:
```
api/
  ├─ index.js          (Axios instance with JWT interceptor)
  ├─ auth.js           (login, register, Google OAuth, getCurrentUser)
  ├─ users.js          (admin user management)
  ├─ careerPaths.js    (career path queries)
  ├─ skills.js         (user skill queries)
  └─ adminSkills.js    (admin skill management)
```

**Axios Interceptor** (JWT injection):
```javascript
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

**Route Guards**:
```javascript
router.beforeEach((to, from, next) => {
  const { isAdmin } = useAuth()
  if (to.meta.requiresAdmin && !isAdmin.value) {
    next('/login')
  } else {
    next()
  }
})
```

**Component Patterns**:

Modal-based CRUD (SkillManagement.vue):
- Two-column layout (left: list, right: detail cards)
- Modal components for create/edit operations
- No page navigation, all operations in-place

Caching strategy (MyCareerPaths.vue):
```javascript
const careerPathSkills = ref({}) // indexed by careerPathId
const loadSkillsForCareerPath = async (careerPathId) => {
  if (careerPathSkills.value[careerPathId]) {
    return // already cached
  }
  // fetch and cache...
}
```

**Emoji Handling**:
- Database: `VARCHAR(50)` stores unicode characters
- Frontend: emoji picker with 50 common emojis
- Fallback: manual text input

### API Design

**RESTful Endpoints**:

```
Auth:
  POST   /api/auth/login
  POST   /api/auth/register
  POST   /api/auth/google
  GET    /api/auth/me

Users (Admin):
  GET    /api/users
  POST   /api/users
  PUT    /api/users/{id}
  DELETE /api/users/{id}

Career Paths:
  GET    /api/career-paths

Skills (User):
  GET    /api/skills/career-paths/{id}
  GET    /api/skills/{id}
  POST   /api/skills/{skillId}/resources
  DELETE /api/skills/resources/{id}

Admin Skills:
  GET    /api/admin/skills
  POST   /api/admin/skills
  PUT    /api/admin/skills/{id}
  DELETE /api/admin/skills/{id}
  POST   /api/admin/skills/{skillId}/focus-areas
  PUT    /api/admin/skills/focus-areas/{id}
  DELETE /api/admin/skills/focus-areas/{id}
  POST   /api/admin/skills/{skillId}/resources
  PUT    /api/admin/skills/resources/{id}
  DELETE /api/admin/skills/resources/{id}
```

**Response Patterns**:
- Success: 200 OK (GET/PUT), 201 Created (POST), 204 No Content (DELETE)
- Client errors: 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found
- Server errors: 500 Internal Server Error

**DTO Pattern**:
- All API responses use DTOs (Data Transfer Objects)
- Prevents JPA lazy-loading issues in JSON serialization
- Hides internal entity structure

### Security Design

**CORS Configuration**:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    // ...
}
```

**Password Storage**:
- BCrypt hashing with strength 10
- Always use `passwordEncoder.encode()` before saving
- Never store plaintext passwords

**Token Security**:
- JWT stored in localStorage (frontend)
- Token sent via Authorization header (not URL params)
- Server validates signature and expiration on every request

**Data Isolation**:
- Users only see official resources + their own resources
- Ownership check before resource deletion
- Admin role required for user management

### Performance Optimizations

**Database Indexes**:
```sql
-- skills
CREATE INDEX idx_display_order ON skills(display_order);
CREATE INDEX idx_is_important ON skills(is_important);

-- focus_areas
CREATE INDEX idx_skill_id ON focus_areas(skill_id);

-- learning_resources
CREATE INDEX idx_skill_id ON learning_resources(skill_id);
CREATE INDEX idx_is_official ON learning_resources(is_official);
CREATE INDEX idx_created_by_user_id ON learning_resources(created_by_user_id);

-- career_path_skills
CREATE UNIQUE INDEX uk_career_skill ON career_path_skills(career_path_id, skill_id);
```

**Lazy Loading**:
```java
@OneToMany(mappedBy = "skill", fetch = FetchType.LAZY)
private List<FocusArea> focusAreas;
```

Only load associations when needed, avoiding N+1 queries.

**Frontend Caching**:
- Skills cached by careerPathId
- User data refreshed only on mount
- Avoid duplicate API calls

### Development Workflow

**Backend**:
```bash
cd backend
./start.sh  # Sources .env and runs mvn spring-boot:run
```

**Frontend**:
```bash
cd frontend
npm install
npm run dev  # Runs on http://localhost:3000, proxies /api to :8080
```

**Database Migrations**:
- Flyway manages schema versions
- Migration files: `backend/src/main/resources/db/migration/V{N}__{description}.sql`
- Auto-runs on application startup

### Testing Strategy

**Backend Testing**:
- Unit tests: Service layer logic
- Integration tests: Repository queries
- Controller tests: API endpoints with MockMvc

**Frontend Testing**:
- Component tests: Vue Test Utils
- E2E tests: Cypress (future)

---

**Document Version**: v1.0
**Last Updated**: 2025-12-21
**Maintainer**: Austin Xu
