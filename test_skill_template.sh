#!/bin/bash
TOKEN=$(curl -s -X POST "http://localhost:8080/api/auth/login" -H 'Content-Type: application/json' -d '{"username": "austinxu", "password": "helloworld"}' | jq -r '.token')

echo "测试获取Skill的所有模版..."
curl -s -X GET "http://localhost:8080/api/admin/skill-templates?skillId=1" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
