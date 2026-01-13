#!/bin/bash

# Growing App - Production Deployment Script
# This script deploys the application using Docker Compose in production mode

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "========================================="
echo "Growing App - Production Deployment"
echo "========================================="

# Check if .env exists
if [ ! -f .env ]; then
    echo "❌ Error: .env file not found!"
    echo ""
    echo "Please create .env file from template:"
    echo "  cp .env.production.example .env"
    echo "  vim .env  # Edit with your actual credentials"
    exit 1
fi

# Load .env to validate required variables
source .env

# Validate required environment variables
REQUIRED_VARS=("DB_USER" "DB_PASSWORD" "JWT_SECRET")
MISSING_VARS=()

for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
        MISSING_VARS+=("$var")
    fi
done

if [ ${#MISSING_VARS[@]} -gt 0 ]; then
    echo "❌ Error: Missing required environment variables:"
    for var in "${MISSING_VARS[@]}"; do
        echo "  - $var"
    done
    echo ""
    echo "Please update your .env file with these values."
    exit 1
fi

# Check JWT_SECRET strength
if [ ${#JWT_SECRET} -lt 32 ]; then
    echo "⚠️  Warning: JWT_SECRET is too short (< 32 characters)"
    echo "For better security, generate a strong secret:"
    echo "  openssl rand -base64 64"
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo ""
echo "✅ Environment variables validated"
echo ""
echo "Configuration:"
echo "  - Spring Profile: prod (production mode)"
echo "  - Database: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
echo "  - Frontend Port: 3001"
echo "  - Backend Port: 8082"
echo ""

# Ask for confirmation
read -p "Deploy to production? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Deployment cancelled."
    exit 0
fi

# Pull latest images
echo ""
echo "📥 Pulling latest Docker images..."
docker-compose pull

# Stop existing containers
echo ""
echo "🛑 Stopping existing containers..."
docker-compose down

# Start containers
echo ""
echo "🚀 Starting containers in production mode..."
docker-compose up -d

# Wait for services to be healthy
echo ""
echo "⏳ Waiting for services to be healthy..."
sleep 5

# Check container status
echo ""
echo "📊 Container Status:"
docker-compose ps

# Check logs for errors
echo ""
echo "📋 Recent logs:"
docker-compose logs --tail=20

echo ""
echo "========================================="
echo "✅ Deployment Complete!"
echo "========================================="
echo ""
echo "Access your application:"
echo "  - Frontend: http://localhost:3001"
echo "  - Backend API: http://localhost:8082"
echo ""
echo "Check logs:"
echo "  docker-compose logs -f          # All services"
echo "  docker-compose logs -f backend  # Backend only"
echo "  docker-compose logs -f frontend # Frontend only"
echo ""
echo "Verify production mode:"
echo "  docker-compose logs backend | grep 'profiles are active'"
echo "  # Should show: The following profiles are active: prod"
echo ""
