#!/bin/bash

# Build and push multi-platform Docker images (AMD64 + ARM64)
# Usage: ./build-multiarch.sh [version]

set -e

# Default version if not provided
VERSION=${1:-v1.0.0}
DOCKER_USERNAME="xuaustin"

echo "🐳 Building multi-platform Docker images for version: $VERSION"
echo "   Platforms: linux/amd64, linux/arm64"
echo ""

# Create buildx builder if it doesn't exist
if ! docker buildx ls | grep -q multiarch; then
    echo "📦 Creating buildx builder..."
    docker buildx create --name multiarch --use
fi

# Use the multiarch builder
docker buildx use multiarch

# Login to Docker Hub (will prompt for credentials if not logged in)
echo "🔐 Logging in to Docker Hub..."
docker login

# Build and push backend image
echo ""
echo "🏗️ Building backend image for linux/amd64 and linux/arm64..."
cd backend
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t ${DOCKER_USERNAME}/growing-backend:${VERSION} \
  -t ${DOCKER_USERNAME}/growing-backend:latest \
  --push \
  .
cd ..

# Build and push frontend image
echo ""
echo "🏗️ Building frontend image for linux/amd64 and linux/arm64..."
cd frontend
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t ${DOCKER_USERNAME}/growing-frontend:${VERSION} \
  -t ${DOCKER_USERNAME}/growing-frontend:latest \
  --push \
  .
cd ..

# Build and push backup image
echo ""
echo "🏗️ Building backup image for linux/amd64 and linux/arm64..."
cd backup
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t ${DOCKER_USERNAME}/growing-backup:${VERSION} \
  -t ${DOCKER_USERNAME}/growing-backup:latest \
  --push \
  .
cd ..

echo ""
echo "✅ Multi-platform images built and pushed successfully!"
echo ""
echo "Backend images:"
echo "  - ${DOCKER_USERNAME}/growing-backend:${VERSION} (amd64 + arm64)"
echo "  - ${DOCKER_USERNAME}/growing-backend:latest (amd64 + arm64)"
echo ""
echo "Frontend images:"
echo "  - ${DOCKER_USERNAME}/growing-frontend:${VERSION} (amd64 + arm64)"
echo "  - ${DOCKER_USERNAME}/growing-frontend:latest (amd64 + arm64)"
echo ""
echo "Backup images:"
echo "  - ${DOCKER_USERNAME}/growing-backup:${VERSION} (amd64 + arm64)"
echo "  - ${DOCKER_USERNAME}/growing-backup:latest (amd64 + arm64)"
echo ""
echo "🔍 To verify platforms:"
echo "   docker buildx imagetools inspect ${DOCKER_USERNAME}/growing-backend:latest"
echo "   docker buildx imagetools inspect ${DOCKER_USERNAME}/growing-frontend:latest"
echo "   docker buildx imagetools inspect ${DOCKER_USERNAME}/growing-backup:latest"
