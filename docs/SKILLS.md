# Claude Code Skills for Growing Project

This document describes the available Claude Code skills for the Growing project. These skills automate common development tasks.

## Available Skills

### 1. git-commit-push

**Purpose**: Automatically stage, commit, and push changes to GitHub.

**When to use**:
- User asks to "commit", "push", or "save changes to git"
- After completing a feature or bug fix
- When you want to save your work to the repository

**Location**: `.claude/skills/git-commit-push/`

**What it does**:
1. Runs `git status` to show what will be committed
2. Runs `git add .` to stage all changes
3. Creates a commit with a descriptive message (following conventional commits format)
4. Pushes to `origin/main`

**Example usage**:
```
User: "Please commit and push these changes"
Claude: [Uses git-commit-push skill to commit and push]
```

### 2. docker-build-push

**Purpose**: Build and push multi-architecture Docker images to Docker Hub.

**When to use**:
- User asks to "build docker images"
- User asks to "push to docker hub"
- When deploying the application

**Location**: `.claude/skills/docker-build-push/`

**What it does**:
1. Creates/uses Docker buildx builder for multi-platform support
2. Logs into Docker Hub (xuaustin account)
3. Builds backend image: `xuaustin/growing-backend:VERSION` + `latest`
4. Builds frontend image: `xuaustin/growing-frontend:VERSION` + `latest`
5. Pushes to Docker Hub with both amd64 and arm64 platforms

**Example usage**:
```bash
# Build with specific version
./build-multiarch.sh v1.0.0

# Build with default version
./build-multiarch.sh
```

### 3. mysql-exec

**Purpose**: Execute MySQL queries with automatic credential loading.

**When to use**:
- User asks to "query the database"
- User asks to "execute SQL"
- User asks to "check database schema"
- User asks to "open mysql shell"

**Location**: `.claude/skills/mysql-exec/`

**Supported modes**:
1. **Execute SQL file**: Run migration scripts
2. **Execute inline query**: Run ad-hoc SQL queries
3. **Interactive shell**: Open MySQL command-line interface

**How it works**:
1. Locates MySQL client (installed via `brew install mysql-client`)
2. Loads credentials from `backend/.env`
3. Parses connection details from `DB_URL`
4. Executes the SQL command

**Example queries**:
```sql
-- List all tables
SHOW TABLES;

-- View all users
SELECT * FROM users;

-- View users with career paths
SELECT u.username, u.email, cp.name as career_path
FROM users u
LEFT JOIN user_career_paths ucp ON u.id = ucp.user_id
LEFT JOIN career_paths cp ON ucp.career_path_id = cp.id;
```

### 4. setup-java

**Purpose**: Configure Java 17 environment and load database credentials.

**When to use**:
- **At session start** - Before any backend development
- User says "start the backend"
- User says "setup the environment"
- When you see "URL must start with 'jdbc'" error
- When environment variables are not loaded

**Location**: `.claude/skills/setup-java/`

**What it does**:
1. Locates and sets `JAVA_HOME` to Java 17
2. Adds Java 17 to `PATH`
3. Loads all environment variables from `backend/.env`
4. Validates the setup

**After running this skill**:
```bash
# Environment variables are now available in the current shell session
cd backend && mvn spring-boot:run
cd backend && mvn test
cd frontend && npm run dev
```

**Important notes**:
- This skill exports environment variables to the current shell session
- For background Maven tasks, use `backend/start.sh` instead (loads .env internally)
- Java 17 is required for Spring Boot 3.2

## Prerequisites

### For all skills:
- Must run from project root directory: `/Users/yanzxu/claude/growing/`

### For git-commit-push:
- Git installed and configured
- GitHub repository set up: https://github.com/austinxyz/growing.git
- Branch: `main`

### For docker-build-push:
- Docker installed
- Docker buildx support
- Docker Hub credentials (xuaustin account)
- `build-multiarch.sh` script in project root

### For mysql-exec:
- MySQL client: `brew install mysql-client`
- Database credentials in `backend/.env`:
  ```
  DB_URL=jdbc:mysql://10.0.0.7:37719/growing?useSSL=false&serverTimezone=UTC
  DB_USERNAME=root
  DB_PASSWORD=your_password
  ```

### For setup-java:
- Java 17 installed
  - macOS: `brew install openjdk@17`
  - Linux: `sudo apt install openjdk-17-jdk`
- Database credentials in `backend/.env`

## Skill Files Structure

Each skill directory contains:
- `SKILL.md` - Skill documentation and metadata
- `skill.sh` - Main skill execution script (for setup-java)
- `setup-env.sh` - Environment setup script (for setup-java)

## Conventional Commits Format

All git commits should follow this format:

- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `refactor:` - Code refactoring
- `style:` - Code style changes
- `test:` - Test updates
- `chore:` - Build/tool updates

Example commit message:
```
feat: add user authentication with Google OAuth

- Implement login and registration pages
- Add Google Sign-In integration
- Create user state management with localStorage
- Add logout functionality in sidebar

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Database Schema

Current tables:
- `users` - User accounts with username, email, password, full name
- `career_paths` - Available career paths
- `user_career_paths` - Many-to-many relationship between users and career paths

## Docker Images

- Backend: `xuaustin/growing-backend:latest`
- Frontend: `xuaustin/growing-frontend:latest`
- Platforms: linux/amd64, linux/arm64

## Notes

- These skills are adapted from the finance project
- Database name changed from `finance` to `growing`
- GitHub repository changed to `growing.git`
- Docker image names changed to `growing-backend` and `growing-frontend`
- Git branch changed from `master` to `main`
