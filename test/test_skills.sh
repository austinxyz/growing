#!/bin/bash

# Login and get token
TOKEN=$(curl -s http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"austinxu","password":"helloworld"}' | \
  python3 -c "import sys,json; print(json.load(sys.stdin)['token'])" 2>/dev/null)

if [ -z "$TOKEN" ]; then
  echo "Failed to login"
  exit 1
fi

echo "=== Career Paths ==="
curl -s http://localhost:8080/api/career-paths \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool

echo ""
echo "=== Skills for Career Path 1 (软件工程师) ==="
curl -s "http://localhost:8080/api/skills/career-path/1" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool

echo ""
echo "=== Skills for Career Path 2 (职业经理) ==="
curl -s "http://localhost:8080/api/skills/career-path/2" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
