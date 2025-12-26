# Growing App - Architecture Guide

> **Purpose**: Detailed technical architecture reference
> **For quick context recovery, see**: `/CLAUDE.md`
> **For UI design patterns, see**: `/docs/FRONTEND_UI_DESIGN_GUIDE.md`

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
  │    └─ one-to-many → questions (id, focus_area_id, question_text, difficulty, answer_requirement, red_flags, is_official, created_by_user_id)
  │         └─ one-to-many → user_question_notes (id, question_id, user_id, note_content)
  └─ one-to-many → learning_resources (id, skill_id, resource_type, title, url, is_official, created_by_user_id)
```

**Key Constraints**:
- username, email: unique indexes
- career_path_skills: unique(career_path_id, skill_id)
- user_question_notes: unique(question_id, user_id) - one note per user per question
- CASCADE delete:
  - career_paths → career_path_skills
  - skills → focus_areas/learning_resources
  - focus_areas → questions → user_question_notes
  - users → learning_resources, questions, user_question_notes

**Enums**:
- Resource Types: `BOOK`, `COURSE`, `WEBSITE`, `ARTICLE`, `VIDEO`, `DOCUMENT`, `BLOG`, `PRACTICE`, `TOOL`, `OTHER`
- Question Difficulty: `EASY`, `MEDIUM`, `HARD`

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

**Resource Visibility Control**:

Learning Resources:
- Users see: official resources + their own resources
- SQL: `WHERE skill_id = ? AND (is_official = true OR created_by_user_id = ?)`
- Users can only delete their own resources
- Admins can see and manage all resources

Questions (Phase 3):
- Users see: official questions + their own questions
- SQL: `WHERE focus_area_id = ? AND (is_official = true OR created_by_user_id = ?)`
- Users can only edit/delete their own questions
- Admins can see and manage all questions

User Notes:
- Users see only their own notes
- Unique constraint ensures one note per user per question
- Admins cannot see other users' notes (privacy protection)

### Backend Architecture

**Layer Structure**:
```
Controller Layer
  ├─ AuthController         (login, register, Google OAuth)
  ├─ UserController         (admin-only user CRUD)
  ├─ SkillController        (user skill queries)
  ├─ AdminSkillController   (admin skill/focus/resource CRUD)
  ├─ QuestionController     (user question queries, user notes)
  └─ AdminQuestionController (admin question CRUD)

Service Layer
  ├─ AuthService            (JWT generation, password verify, token validation)
  ├─ UserService            (user CRUD, DTO conversion)
  ├─ SkillService           (skill CRUD, visibility filtering)
  ├─ FocusAreaService       (focus area CRUD)
  ├─ LearningResourceService (resource CRUD with ownership check)
  ├─ QuestionService        (question CRUD, visibility filtering)
  └─ UserQuestionNoteService (note CRUD with UPSERT logic)

Repository Layer
  ├─ UserRepository
  ├─ CareerPathRepository
  ├─ SkillRepository
  ├─ FocusAreaRepository
  ├─ LearningResourceRepository
  ├─ QuestionRepository
  └─ UserQuestionNoteRepository

Model Layer
  ├─ User                 (@ManyToMany careerPaths)
  ├─ CareerPath           (@ManyToMany skills)
  ├─ Skill                (@ManyToMany careerPaths, @OneToMany focusAreas/resources)
  ├─ FocusArea            (@ManyToOne skill, @OneToMany questions)
  ├─ LearningResource     (@ManyToOne skill, @ManyToOne createdByUser)
  ├─ Question             (@ManyToOne focusArea, @ManyToOne createdByUser, @OneToMany userNotes)
  └─ UserQuestionNote     (@ManyToOne question, @ManyToOne user)
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

**Phase 3 Patterns**:

UPSERT logic (user notes):
```java
@Transactional
public UserQuestionNoteDTO saveOrUpdateNote(Long questionId, String noteContent, Long userId) {
    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    // Check if note already exists
    Optional<UserQuestionNote> existingNote =
        noteRepository.findByQuestionIdAndUserId(questionId, userId);

    UserQuestionNote note;
    if (existingNote.isPresent()) {
        // Update existing note
        note = existingNote.get();
        note.setNoteContent(noteContent);
    } else {
        // Create new note
        note = new UserQuestionNote();
        note.setQuestion(question);
        note.setUser(user);
        note.setNoteContent(noteContent);
    }

    return convertToDTO(noteRepository.save(note));
}
```

JSON serialization (Red Flags array):
```java
@Entity
@Table(name = "questions")
public class Question {
    @Column(name = "red_flags", columnDefinition = "TEXT")
    private String redFlags; // Stored as JSON array string

    // Transient helper methods
    public List<String> getRedFlagsList() {
        if (redFlags == null || redFlags.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(redFlags, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public void setRedFlagsList(List<String> redFlagsList) throws JsonProcessingException {
        this.redFlags = objectMapper.writeValueAsString(redFlagsList);
    }
}
```

### Frontend Architecture

**State Management**:
- Global auth state: `composables/useAuth.js`
- Provides: `currentUser`, `token`, `isAdmin`, `login()`, `logout()`, `updateUser()`
- Reactive state shared across components

**API Organization**:
```
api/
  ├─ index.js          (Axios instance with JWT interceptor + response unwrapper)
  ├─ auth.js           (login, register, Google OAuth, getCurrentUser)
  ├─ users.js          (admin user management)
  ├─ careerPaths.js    (career path queries)
  ├─ skills.js         (user skill queries)
  ├─ adminSkills.js    (admin skill management)
  ├─ questionApi.js    (user question queries, user notes)
  └─ adminQuestionApi.js (admin question CRUD)
```

**Axios Interceptors**:

Request interceptor (JWT injection):
```javascript
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

⚠️ **Response interceptor** (auto-unwraps response.data):
```javascript
apiClient.interceptors.response.use(
  (response) => {
    return response.data  // Auto-unwraps!
  },
  (error) => {
    // error handling...
  }
)
```

**CRITICAL**: Because the response interceptor returns `response.data`, all API calls should use:
```javascript
// ✅ CORRECT
const data = await questionApi.getQuestions()
items.value = data || []

// ❌ WRONG - response.data is undefined!
const response = await questionApi.getQuestions()
items.value = response.data || []  // undefined!
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

Three-column layout (MyQuestionBank.vue - Phase 3):
- Top: Career Path tabs
- Left (25%): Skill-Focus Area tree (collapsible)
- Middle (35%): Question list + difficulty filters
- Right (40%): Question details + user notes
- Pattern: Click Focus Area → load questions → click question → show details + notes

Context-aware modal props (Phase 3):
```javascript
// Pass current context to auto-fill form fields
<QuestionEditModal
  :question="editingQuestion"
  :current-focus-area-id="selectedFocusAreaId"
  :current-focus-area-name="selectedFocusAreaName"
/>

// In modal: conditionally render read-only vs editable
<div v-if="currentFocusAreaName">
  <div class="px-3 py-2 bg-gray-50 border rounded-md">
    {{ currentFocusAreaName }}
  </div>
</div>
<div v-else>
  <select v-model="form.focusAreaId" required>
    <!-- options -->
  </select>
</div>
```

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

**Markdown Rendering** (Phase 3):
```javascript
import { marked } from 'marked'

const renderMarkdown = (text) => {
  if (!text) return ''
  try {
    return marked(text, { breaks: true, gfm: true })
  } catch (error) {
    console.error('Markdown rendering error:', error)
    return text
  }
}
```

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

Questions (User - Phase 3):
  GET    /api/questions/focus-areas/{focusAreaId}  (get questions by Focus Area)
  GET    /api/questions/{id}                        (get question detail with user note)
  POST   /api/questions                             (create user question)
  PUT    /api/questions/{id}                        (update user's own question)
  DELETE /api/questions/{id}                        (delete user's own question)
  GET    /api/questions/{id}/note                   (get user note)
  POST   /api/questions/{id}/note                   (create/update user note - UPSERT)
  DELETE /api/questions/{id}/note                   (delete user note)

Admin Questions (Phase 3):
  GET    /api/admin/questions?focusAreaId={id}      (get all questions, optional filter)
  POST   /api/admin/questions                       (create official question)
  PUT    /api/admin/questions/{id}                  (update any question)
  DELETE /api/admin/questions/{id}                  (delete any question)
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

-- questions (Phase 3)
CREATE INDEX idx_focus_area_id ON questions(focus_area_id);
CREATE INDEX idx_is_official ON questions(is_official);
CREATE INDEX idx_created_by_user_id ON questions(created_by_user_id);
CREATE INDEX idx_difficulty ON questions(difficulty);
CREATE INDEX idx_target_position ON questions(target_position);
CREATE INDEX idx_target_level ON questions(target_level);
CREATE INDEX idx_display_order ON questions(display_order);

-- user_question_notes (Phase 3)
CREATE INDEX idx_question_id ON user_question_notes(question_id);
CREATE INDEX idx_user_id ON user_question_notes(user_id);
CREATE UNIQUE INDEX uk_question_user ON user_question_notes(question_id, user_id);

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

## Phase 4 Architecture (Algorithm Learning System)

### Overview

Phase 4 introduced a flexible, Skill-level learning path system for the "算法与数据结构" skill. Key innovation: **different skills can define different learning stages** (e.g., algorithms use 3 stages: theory → implementation → practice).

### New Data Model

**Phase 4 additions**:
```
major_categories (id, name, sort_order)  - 4 fixed categories
  ↑ many-to-many via focus_area_categories
focus_areas (extended with category mapping)
  ↑ one-to-many
learning_contents (id, focus_area_id, stage_id, content_type, title, url, ...)
  ↑ many-to-one
learning_stages (id, skill_id, stage_name, stage_type, sort_order)  - 3 stages
  ↑ many-to-one
skills (算法与数据结构, skill_id=1)

questions (extended with programming_question_details)
  ↓ one-to-one
programming_question_details (id, question_id, leetcode_number, leetcode_url, ...)

user_question_notes (extended with core_strategy field for programming questions)
```

**Data Statistics**:
- Major Categories: 4 (数据结构、搜索、动规、其他)
- Focus Areas: 52 (algorithm topics)
- Learning Stages: 3 (基础原理、实现代码、实战题目)
- Learning Contents: 235 (225 articles + 10 templates)
- Programming Questions: 272 (with detailed metadata)
- User Notes: 272 (with core_strategy field)

### Architecture Patterns

**1. Skill-Level Learning Stages**:
```
Skill ("算法与数据结构")
  → Learning Stages (3 custom stages)
    → Learning Contents (articles, videos, code, templates)
```

Different skills can define different stages:
- Algorithms: theory → implementation → practice
- System Design (future): requirements → architecture → scalability
- DevOps (future): setup → automation → monitoring

**2. Algorithm Templates**:
- Stored in `learning_contents` with `focus_area_id = NULL`
- `content_type = 'template'`
- Independent from Focus Areas, displayed in separate section
- 10 templates for quick reference

**3. Programming Question Details** (one-to-one extension):
```java
// questions table (generic, all question types)
Question {
  id, focus_area_id, title, question_description,
  difficulty, question_type, is_official, ...
}

// programming_question_details table (programming-specific)
ProgrammingQuestionDetails {
  id, question_id (UNIQUE),
  leetcode_number, leetcode_url,
  labuladong_url, hellointerview_url
}
```

**Design Benefits**:
- `questions` table stays generic (supports behavioral, technical, programming, design)
- Programming-specific fields isolated, no NULL waste for other question types
- UNIQUE constraint ensures strict one-to-one relationship

**4. User Notes Extension** (core_strategy field):
```java
// Phase 3 base structure
UserQuestionNote {
  id, question_id, user_id,
  note_content,           // General notes (all question types)
  created_at, updated_at
}

// Phase 4 extension
+ core_strategy TEXT     // Core algorithm strategy (programming questions only)
```

**Why store in user_question_notes, not programming_question_details?**
- Core strategy is USER-SPECIFIC, not question metadata
- Different users may have different solving approaches
- Supports user privacy (only owner can see their strategy)

### API Design

**Admin APIs** (learning content management):
```
GET    /api/admin/learning-contents?focusAreaId={id}&stageId={id}
POST   /api/admin/learning-contents
PUT    /api/admin/learning-contents/{id}
DELETE /api/admin/learning-contents/{id}

GET    /api/admin/questions?focusAreaId={id}
POST   /api/admin/questions (with programming_question_details)
PUT    /api/admin/questions/{id}
DELETE /api/admin/questions/{id} (cascade delete details and notes)
```

**User APIs**:
```
GET    /api/major-categories
GET    /api/focus-areas-with-categories?skillId={id}
GET    /api/learning-contents?focusAreaId={id}  (returns all stages in one call)
GET    /api/algorithm-templates?search={keyword}
GET    /api/questions/learning-review?majorCategoryId={id}&focusAreaId={id}&leetcodeNumber={num}
```

**Learning Review API** (new feature):
- Endpoint: `GET /api/questions/learning-review`
- Purpose: Batch query all 272 programming questions with core strategies
- Performance: 0.6 seconds (batch query optimization)
- Optimization technique:
  ```java
  // Single query for all programming questions
  List<Question> allQuestions = questionRepository.findAllProgrammingQuestionsBySkillId(1L);

  // Batch load user notes, build Map<questionId, UserQuestionNote>
  Map<Long, UserQuestionNote> noteMap = buildNoteMap(allQuestions, userId);

  // Batch load programming details, build Map<questionId, ProgrammingQuestionDetailsDTO>
  Map<Long, ProgrammingQuestionDetailsDTO> detailsMap = buildDetailsMap(allQuestions);

  // Batch query Focus Area to Major Category mapping
  Map<Long, List<Long>> focusAreaToCategoryMap = buildCategoryMap(allQuestions);
  ```

### Frontend Architecture

**Admin Pages**:
1. `/admin/algorithm-content` - Two-column layout
   - Left: Major Category tabs + Focus Area list
   - Right: 4 tabs (基础原理、实现代码、实战题目、试题库)
   - Tab 1-3: Manage learning_contents
   - Tab 4: Manage questions + programming_question_details

2. `/admin/algorithm-templates` - Two-column layout
   - Left: Template list
   - Right: Markdown editor

**User Pages**:
1. `/algorithm-learning` - Three-column layout
   - Top: 4 Major Category tabs
   - Left: Focus Area list
   - Right: **Single-page integrated display** (all learning stages vertically stacked)
   - Design change from original: Originally 3 tabs (theory/implementation/practice), now single page for continuous reading

2. `/algorithm-templates` - Two-column layout
   - Left: Template list + search
   - Right: Markdown preview

3. `/learning-review` - Compact dual-column layout (NEW)
   - Top: Filters (major category, focus area, LeetCode number)
   - Body: Dual-column card grid (272 questions)
   - Card format (2 rows):
     - Row 1: Difficulty icon + Title (clickable) + Focus Area + Category tag
     - Row 2: Core strategy (or "暂无核心思路")
   - Color coding by major category (blue/green/orange/purple)
   - Performance: 0.6s load time (backend batch query)

### Service Layer Patterns

**Pattern 1: Batch Query Optimization**
```java
// QuestionService.getLearningReview()
public Map<String, Object> getLearningReview(Long userId) {
    // Single query for all programming questions
    List<Question> allProgrammingQuestions =
        questionRepository.findAllProgrammingQuestionsBySkillId(ALGORITHM_SKILL_ID);

    // Batch load associations (avoid N+1 queries)
    Map<Long, UserQuestionNote> noteMap = batchLoadNotes(allProgrammingQuestions, userId);
    Map<Long, ProgrammingQuestionDetailsDTO> detailsMap = batchLoadDetails(allProgrammingQuestions);
    Map<Long, List<Long>> focusAreaToCategoryMap = batchLoadCategoryMapping(allProgrammingQuestions);

    // Assemble response
    return assembleResponse(allProgrammingQuestions, noteMap, detailsMap, focusAreaToCategoryMap);
}
```

**Pattern 2: Single-Page Integrated Display** (frontend)
```javascript
// AlgorithmLearning.vue
async function loadFocusAreaContent(focusAreaId) {
  // Single API call returns all learning stages
  const data = await learningContentApi.getContentsByFocusArea(focusAreaId)

  // Backend returns: { focusArea, stageContents: [{ stage, contents }, ...] }
  // Frontend displays all stages vertically in one page
  allStages.value = data.stageContents
}
```

### Database Migrations

**V9: Add leetcode_number field**
```sql
ALTER TABLE programming_question_details
ADD COLUMN leetcode_number INT COMMENT 'LeetCode题号',
ADD INDEX idx_leetcode_number (leetcode_number);
```

**V10: Remove deprecated question_text field**
```sql
ALTER TABLE questions
DROP COLUMN question_text;
```

**Initial Data Import**:
- 3 learning stages (V8_create_learning_stages.sql)
- 225 labuladong articles (import/labuladong_phase4_v2_staged.md → Python script)
- 10 algorithm templates (manual creation via admin panel)
- 272 programming questions + details (import/programming_questions_with_details.sql)
- 272 user notes with core_strategy (import/notes_only_import.sql)

### Performance Optimizations

**Database Indexes**:
```sql
-- learning_contents
CREATE INDEX idx_focus_stage ON learning_contents(focus_area_id, stage_id, sort_order);
CREATE INDEX idx_stage_type ON learning_contents(stage_id, content_type);

-- programming_question_details
CREATE INDEX idx_leetcode_number ON programming_question_details(leetcode_number);

-- focus_area_categories
CREATE INDEX idx_focus_area_id ON focus_area_categories(focus_area_id);
CREATE INDEX idx_category_id ON focus_area_categories(major_category_id);
```

**Query Optimization**:
- Learning review: Batch query (0.6s for 272 questions)
- Focus Area content: Single API call for all stages
- Template search: Indexed on title field

### Key Design Decisions

**1. Why separate programming_question_details table?**
- `questions` table is generic (supports all question types)
- Programming-specific fields (LeetCode links) only needed for programming questions
- Avoids NULL fields for 107 behavioral questions
- Easier to extend with other question types (system design, DevOps, etc.)

**2. Why store core_strategy in user_question_notes?**
- Core strategy is user-specific, not question metadata
- Different users have different solving approaches
- Supports privacy (only owner sees their strategy)
- Reuses Phase 3 infrastructure (UPSERT logic, ownership control)

**3. Why single-page display instead of tabs?**
- Better learning flow: users read theory → code → practice continuously
- Reduces UI friction (no tab switching)
- Matches reading patterns: top-to-bottom scroll

**4. Why add learning review page?**
- User need: Quickly review all 272 questions' core strategies
- Avoids clicking 272 individual question details
- Performance: Batch query optimization (0.6s load time)
- Compact design: Dual-column, minimal padding, text-xs fonts

---

**Document Version**: v2.0
**Last Updated**: 2025-12-24 (added Phase 4 architecture)
**Maintainer**: Austin Xu
