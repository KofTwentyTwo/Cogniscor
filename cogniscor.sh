#!/bin/bash

#
# Cogniscor - Company Research & Analysis Tool
# Shell script wrapper - UI handled by Java TUI
#

set -e

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_PATH="${SCRIPT_DIR}/target/cogniscor-1.0-SNAPSHOT.jar"

# Colors for errors only
RED='\033[0;31m'
NC='\033[0m'

print_error() {
    echo -e "${RED}❌ Error: $1${NC}" >&2
}

# Check if JAR exists
check_jar() {
    if [ ! -f "$JAR_PATH" ]; then
        print_error "JAR file not found: $JAR_PATH"
        echo "Building the project..."
        cd "$SCRIPT_DIR"
        if mvn clean package -DskipTests -q; then
            echo "✓ Build complete"
        else
            print_error "Build failed"
            exit 1
        fi
    fi
}

# Check Java version
check_java() {
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        print_error "Java 17+ required (found Java $JAVA_VERSION)"
        exit 1
    fi
}

# Main execution
check_java
check_jar

# Run Java TUI - it handles ALL output
# Suppress Jansi native access warnings on Java 21+
cd "$SCRIPT_DIR"
java --add-opens=java.base/java.lang=ALL-UNNAMED \
     -Djansi.force=true \
     -jar "$JAR_PATH" "$@" 2>&1 | grep -v "WARNING.*java.lang.System"
exit ${PIPESTATUS[0]}
