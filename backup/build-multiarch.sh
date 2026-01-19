#!/bin/bash

# Multi-architecture Docker build script for Growing backup service
# Builds for both amd64 and arm64 platforms

set -e

VERSION=${1:-latest}

echo "Building Growing backup service multi-architecture image..."
echo "Version: $VERSION"
echo "Platforms: linux/amd64, linux/arm64"

# Ensure buildx is available
if ! docker buildx version > /dev/null 2>&1; then
    echo "Error: docker buildx not found"
    echo "Please install: docker buildx install"
    exit 1
fi

# Create/use multi-platform builder
BUILDER_NAME="growing-multiarch-builder"
if ! docker buildx inspect "$BUILDER_NAME" > /dev/null 2>&1; then
    echo "Creating builder: $BUILDER_NAME"
    docker buildx create --name "$BUILDER_NAME" --use
else
    echo "Using existing builder: $BUILDER_NAME"
    docker buildx use "$BUILDER_NAME"
fi

# Login to Docker Hub
echo "Logging in to Docker Hub..."
docker login

# Build and push multi-arch image
echo "Building and pushing multi-architecture image..."
docker buildx build \
    --platform linux/amd64,linux/arm64 \
    --tag xuaustin/growing-backup:${VERSION} \
    --tag xuaustin/growing-backup:latest \
    --push \
    .

echo "Build complete!"
echo "Images pushed:"
echo "  - xuaustin/growing-backup:${VERSION}"
echo "  - xuaustin/growing-backup:latest"

# Verify platforms
echo ""
echo "Verifying platforms..."
docker buildx imagetools inspect xuaustin/growing-backup:${VERSION}
