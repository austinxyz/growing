#!/bin/bash

# Growing App - Docker Build and Push Script
# This script builds both frontend and backend images and pushes them to Docker Hub

set -e

echo "=========================================="
echo "Growing App - Docker Build & Push"
echo "=========================================="

# Check if logged into Docker Hub
if ! docker info | grep -q "Username"; then
    echo "Please login to Docker Hub first:"
    docker login
fi

# Build backend
echo ""
echo "📦 Building backend image..."
cd backend
docker build -t xuaustin/growing-backend:latest .
echo "✅ Backend image built"

# Build frontend
echo ""
echo "📦 Building frontend image..."
cd ../frontend
docker build -t xuaustin/growing-frontend:latest .
echo "✅ Frontend image built"

cd ..

# Push images
echo ""
echo "🚀 Pushing images to Docker Hub..."
docker push xuaustin/growing-backend:latest
docker push xuaustin/growing-frontend:latest

echo ""
echo "=========================================="
echo "✅ All images built and pushed successfully!"
echo "=========================================="
echo ""
echo "To deploy, run:"
echo "  docker-compose up -d"
echo ""
