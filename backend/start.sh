#!/bin/bash

# Growing Backend Start Script
# Loads environment variables from .env and starts Spring Boot
#
# Usage:
#   ./start.sh           # Development mode (default)
#   ./start.sh prod      # Production mode
#   ./start.sh dev       # Development mode (explicit)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Set Java 17 for Maven (auto-detect by OS)
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home
elif [[ "$OSTYPE" == "msys"* || "$OSTYPE" == "cygwin"* || "$OSTYPE" == "win32" ]]; then
    # Windows (Git Bash / MSYS2)
    export JAVA_HOME="/c/Program Files/Java/jdk-17"
    export PATH="/c/Users/lorra/tools/apache-maven-3.9.6/bin:$PATH"
fi

# Get profile from argument (default: dev)
PROFILE="${1:-dev}"

# Validate profile
if [[ "$PROFILE" != "dev" && "$PROFILE" != "prod" ]]; then
    echo "Error: Invalid profile '$PROFILE'. Use 'dev' or 'prod'."
    exit 1
fi

# Load environment variables from .env if it exists
if [ -f "$SCRIPT_DIR/.env" ]; then
    echo "Loading environment variables from .env"
    export $(grep -v '^#' "$SCRIPT_DIR/.env" | xargs)
else
    echo "Warning: .env file not found. Using default configuration."
fi

# Set Spring profile
export SPRING_PROFILES_ACTIVE="$PROFILE"

# Start Spring Boot
echo "========================================="
echo "Starting Growing backend in $PROFILE mode..."
echo "Profile: $PROFILE"
echo "Port: 8082"
echo "========================================="
cd "$SCRIPT_DIR"
mvn spring-boot:run -Dmaven.test.skip=true
