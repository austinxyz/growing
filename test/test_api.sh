#!/bin/bash

# Login and get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"austinxu","password":"helloworld"}' | \
  grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Token obtained: ${TOKEN:0:20}..."
echo ""

# Test getMyCareerPaths API
echo "Testing GET /api/career-paths/my"
curl -s http://localhost:8080/api/career-paths/my \
  -H "Authorization: Bearer $TOKEN"
echo ""
