#!/bin/bash
# JWT Token获取工具
# 用法: ./get_jwt_token.sh [username] [password]
# 默认: username=austin.xyz, password=helloworld

USERNAME=${1:-"austin.xyz"}
PASSWORD=${2:-"helloworld"}
API_BASE="http://localhost:8082/api"

# 登录获取token
RESPONSE=$(curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

# 提取token
TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ 登录失败！响应: $RESPONSE" >&2
  exit 1
fi

echo "$TOKEN"
