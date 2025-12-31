# Growing App - Docker Deployment Guide

## Quick Start

### 1. Prerequisites
- Docker 20.10+
- Docker Compose 2.0+

### 2. Setup Environment Variables

Copy the example environment file and configure:

```bash
cp .env.example .env
```

Edit `.env` and set your database credentials and JWT secret:

```bash
DB_USER=austinxu
DB_PASSWORD=helloworld
JWT_SECRET=your-super-secret-jwt-key-min-256-bits
```

### 3. Build and Run

#### Option A: Using Docker Compose (Recommended)

```bash
# Build and start all services
docker-compose up -d

# Check logs
docker-compose logs -f

# Stop services
docker-compose down
```

#### Option B: Build Images Separately

```bash
# Build backend image
cd backend
docker build -t xuaustin/growing-backend:latest .

# Build frontend image
cd ../frontend
docker build -t xuaustin/growing-frontend:latest .

# Run with docker-compose
cd ..
docker-compose up -d
```

### 4. Access the Application

- **Frontend**: http://localhost:3001
- **Backend API**: http://localhost:8082/api
- **Health Check**: http://localhost:8082/api/career-paths

### 5. Default Admin Account

```
Username: austinxu
Email: austin@example.com
Password: helloworld
Role: admin
```

## Architecture

```
┌─────────────────┐
│   Frontend      │
│  (Vue.js + Nginx)│
│   Port: 3001    │
└────────┬────────┘
         │
         │ /api/* → proxy
         │
┌────────▼────────┐
│   Backend       │
│ (Spring Boot)   │
│   Port: 8082    │
└────────┬────────┘
         │
         │ JDBC
         │
┌────────▼────────┐
│   Database      │
│    (MySQL)      │
│ 10.0.0.7:37719  │
└─────────────────┘
```

## Docker Images

- **Backend**: `xuaustin/growing-backend:latest`
  - Base: `eclipse-temurin:17-jre`
  - Size: ~300MB
  - Multi-stage build with Maven

- **Frontend**: `xuaustin/growing-frontend:latest`
  - Base: `nginx:1.25-alpine`
  - Size: ~50MB
  - Multi-stage build with Node.js

## Deployment

### Push to Docker Hub

```bash
# Login to Docker Hub
docker login

# Build and push backend
cd backend
docker build -t xuaustin/growing-backend:latest .
docker push xuaustin/growing-backend:latest

# Build and push frontend
cd ../frontend
docker build -t xuaustin/growing-frontend:latest .
docker push xuaustin/growing-frontend:latest
```

### Production Deployment

1. Copy `docker-compose.yml` and `.env` to your server
2. Update `.env` with production credentials
3. Run: `docker-compose up -d`

## Health Checks

Both services have built-in health checks:

- **Backend**: Checks `/api/career-paths` every 30s
- **Frontend**: Checks nginx root every 30s

View health status:

```bash
docker ps
docker inspect growing-backend | grep -A 10 Health
docker inspect growing-frontend | grep -A 10 Health
```

## Troubleshooting

### Backend won't start

```bash
# Check logs
docker-compose logs backend

# Common issues:
# 1. Database connection - verify DB_HOST, DB_PORT, DB_USER, DB_PASSWORD in .env
# 2. JWT_SECRET not set - check .env file
```

### Frontend shows blank page

```bash
# Check logs
docker-compose logs frontend

# Check backend connectivity
docker exec growing-frontend wget -O- http://backend:8082/api/career-paths
```

### Database connection issues

```bash
# Test database connection from backend container
docker exec growing-backend wget --spider http://10.0.0.7:37719
```

## Development

### Hot Reload (Not Recommended for Docker)

For development, use local setup with `npm run dev` and `./backend/start.sh`.

Docker is optimized for production deployment.

## Cleanup

```bash
# Stop and remove containers
docker-compose down

# Remove images
docker rmi xuaustin/growing-backend:latest
docker rmi xuaustin/growing-frontend:latest

# Remove all unused images and volumes
docker system prune -a --volumes
```

## Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | MySQL host | `10.0.0.7` | ✅ |
| `DB_PORT` | MySQL port | `37719` | ✅ |
| `DB_NAME` | Database name | `growing` | ✅ |
| `DB_USER` | Database user | - | ✅ |
| `DB_PASSWORD` | Database password | - | ✅ |
| `JWT_SECRET` | JWT signing key | - | ✅ |

## Security Notes

1. **NEVER commit `.env` file** - It's in `.gitignore`
2. **Change JWT_SECRET in production** - Use a strong, random key (256+ bits)
3. **Use HTTPS in production** - Add reverse proxy (nginx/traefik)
4. **Secure database credentials** - Use secrets management in production

## License

MIT License - See LICENSE file for details
