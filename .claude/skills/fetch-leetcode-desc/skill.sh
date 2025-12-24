#!/bin/bash
# Skill: fetch-leetcode-desc
# Description: Fetch LeetCode problem descriptions and update database

set -e

# Color output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "Fetching LeetCode problem descriptions..."

# Run the Python script from import directory
cd "$(dirname "$0")/../../.."
python3 import/fetch_leetcode_web.py
