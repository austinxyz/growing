# Growing - Personal Growth Management System

A comprehensive personal growth management system built with Spring Boot 3.2 + Vue 3.

## Features

- **User Management**: User registration and authentication with Google OAuth
- **Career Path Tracking**: Track and manage multiple career paths
- **Skills Management**: Organize and monitor skill development

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- MySQL 8.0
- Maven

### Frontend
- Vue 3
- Vite
- Tailwind CSS
- Lucide Icons

## Prerequisites

- Java 17
- Node.js 16+
- MySQL 8.0
- Maven 3.6+

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/austinxyz/growing.git
cd growing
```

### 2. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE growing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Copy the environment template and configure your credentials:

```bash
cd backend
cp .env.example .env
```

Edit `backend/.env` with your actual database credentials:

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=growing
DB_USER=your_username
DB_PASSWORD=your_password
```

### 3. Start Backend

```bash
cd backend
mvn spring-boot:run
```

The backend will start on http://localhost:8080

API Documentation: http://localhost:8080/api/swagger-ui.html

### 4. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on http://localhost:3000

## Google OAuth Setup

To enable Google Sign-In, follow the guide in [docs/GOOGLE_OAUTH_SETUP.md](docs/GOOGLE_OAUTH_SETUP.md)

## Claude Code Skills

This project includes several Claude Code skills for development automation:

- **git-commit-push**: Automated git commit and push
- **docker-build-push**: Build and push Docker images
- **mysql-exec**: Execute MySQL queries
- **setup-java**: Setup Java environment

See [docs/SKILLS.md](docs/SKILLS.md) for details.

## Project Structure

```
growing/
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java source code
│   │   │   └── resources/  # Configuration files
│   │   └── test/           # Tests
│   ├── .env.example        # Environment template
│   └── pom.xml
├── frontend/               # Vue 3 frontend
│   ├── src/
│   │   ├── components/     # Vue components
│   │   ├── views/          # Page views
│   │   ├── api/            # API clients
│   │   └── composables/    # Vue composables
│   ├── public/
│   └── package.json
├── docs/                   # Documentation
├── .claude/                # Claude Code skills
└── README.md
```

## Development

### Backend Development

The backend uses Spring Boot DevTools for hot reload:

```bash
cd backend
mvn spring-boot:run
```

### Frontend Development

Vite provides HMR (Hot Module Reload):

```bash
cd frontend
npm run dev
```

### Database Migrations

The project uses Hibernate DDL auto-update. For production, consider using Flyway or Liquibase.

## Docker Deployment

Build and push Docker images:

```bash
./build-multiarch.sh v1.0.0
```

This will build and push:
- `xuaustin/growing-backend:v1.0.0` and `latest`
- `xuaustin/growing-frontend:v1.0.0` and `latest`

Both for `linux/amd64` and `linux/arm64` platforms.

## Security Notes

⚠️ **IMPORTANT**: Never commit sensitive information to git!

Files that are excluded from git (via `.gitignore`):
- `backend/.env` - Database credentials
- `frontend/.env*` - Frontend environment variables
- `target/` - Build outputs
- `node_modules/` - Dependencies
- `.idea/`, `.vscode/` - IDE files

Always use `.env.example` files as templates.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: add some amazing feature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is private and proprietary.

## Author

Austin Xu (@austinxyz)
