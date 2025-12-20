#!/bin/bash

# Setup Java Environment and Load Database Credentials
# This script configures the development environment for the growing app

set -e  # Exit on error

echo "🔧 Setting up Java 17 environment..."

# Set Java 17 as JAVA_HOME
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || echo "")
    if [ -z "$JAVA_HOME" ]; then
        echo "❌ Java 17 not found. Install with: brew install openjdk@17"
        exit 1
    fi
else
    # Linux - try common locations
    for java_path in /usr/lib/jvm/java-17-openjdk* /usr/lib/jvm/java-17-oracle*; do
        if [ -d "$java_path" ]; then
            export JAVA_HOME="$java_path"
            break
        fi
    done
    if [ -z "$JAVA_HOME" ]; then
        echo "❌ Java 17 not found. Please install Java 17."
        exit 1
    fi
fi

export PATH="$JAVA_HOME/bin:$PATH"
echo "✅ JAVA_HOME: $JAVA_HOME"
echo "✅ Java version: $(java -version 2>&1 | head -n 1)"

# Load database credentials from backend/.env
if [ -f backend/.env ]; then
    echo "🔐 Loading database credentials from backend/.env..."

    # Read .env and export variables
    while IFS='=' read -r key value; do
        # Skip comments and empty lines
        [[ $key =~ ^#.*$ ]] && continue
        [[ -z $key ]] && continue

        # Remove quotes and export
        value=$(echo "$value" | sed -e 's/^"//' -e 's/"$//' -e "s/^'//" -e "s/'$//")
        export "$key=$value"

        # Show confirmation (mask password)
        if [[ $key == "DB_PASSWORD" ]]; then
            echo "  ✅ $key=****"
        elif [[ $key == "CLAUDE_API_KEY" ]]; then
            echo "  ✅ $key=****"
        else
            echo "  ✅ $key=$value"
        fi
    done < backend/.env

    echo "✅ Database credentials loaded"
else
    echo "⚠️  Warning: backend/.env not found"
    echo "   Create it with: DB_URL, DB_USERNAME, DB_PASSWORD"
    echo ""
    echo "   Example format:"
    echo "   DB_URL=jdbc:mysql://localhost:3306/growing?useSSL=false&serverTimezone=UTC"
    echo "   DB_USERNAME=your_username"
    echo "   DB_PASSWORD=your_password"
    exit 1
fi

echo ""
echo "✅ Environment setup complete!"
echo ""
echo "You can now run:"
echo "  cd backend && mvn spring-boot:run"
echo "  cd frontend && npm run dev"
