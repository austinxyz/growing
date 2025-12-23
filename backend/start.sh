#!/bin/bash

# Growing Backend Start Script
# Loads environment variables from .env and starts Spring Boot

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Load environment variables from .env if it exists
if [ -f "$SCRIPT_DIR/.env" ]; then
    echo "Loading environment variables from .env"
    export $(grep -v '^#' "$SCRIPT_DIR/.env" | xargs)
else
    echo "Warning: .env file not found. Using default configuration."
fi

# Start Spring Boot
echo "Starting Growing backend..."
cd "$SCRIPT_DIR"
mvn spring-boot:run -Dmaven.test.skip=true
