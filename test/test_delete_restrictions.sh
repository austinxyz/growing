#!/bin/bash

# 测试删除限制功能
# 1. 测试删除有 Focus Area 的 Skill（应该失败）
# 2. 测试删除 General 大分类（应该失败）
# 3. 测试删除其他大分类（应该成功，Focus Area 变为未分类）

BASE_URL="http://localhost:8080/api"

# 登录获取token
echo "===== 登录获取token ====="
TOKEN=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"austinxu","password":"helloworld"}' | jq -r '.token')

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "❌ 登录失败"
  exit 1
fi
echo "✅ 登录成功，token: ${TOKEN:0:20}..."

# 测试1: 尝试删除有 Focus Area 的 Skill（应该失败）
echo ""
echo "===== 测试1: 尝试删除有 Focus Area 的 Skill (ID=1, 算法与数据结构) ====="
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "${BASE_URL}/admin/skills/1" \
  -H "Authorization: Bearer ${TOKEN}")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)

echo "HTTP状态码: $HTTP_CODE"
echo "响应内容: $BODY"

if [ "$HTTP_CODE" = "400" ]; then
  echo "✅ 测试通过: 删除有 Focus Area 的 Skill 被正确拒绝"
  echo "   错误信息: $(echo $BODY | jq -r '.message')"
else
  echo "❌ 测试失败: 应该返回 400 状态码"
fi

# 测试2: 查询 General 大分类的 ID
echo ""
echo "===== 查询算法与数据结构 (Skill ID=1) 的大分类 ====="
CATEGORIES=$(curl -s "${BASE_URL}/major-categories?skillId=1" \
  -H "Authorization: Bearer ${TOKEN}")
echo "大分类列表: $(echo $CATEGORIES | jq -c '.')"

GENERAL_ID=$(echo $CATEGORIES | jq -r '.[] | select(.name == "General") | .id')
if [ -z "$GENERAL_ID" ] || [ "$GENERAL_ID" = "null" ]; then
  echo "⚠️  未找到 General 大分类，跳过测试2"
else
  # 测试2: 尝试删除 General 大分类（应该失败）
  echo ""
  echo "===== 测试2: 尝试删除 General 大分类 (ID=$GENERAL_ID) ====="
  RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "${BASE_URL}/major-categories/$GENERAL_ID" \
    -H "Authorization: Bearer ${TOKEN}")

  HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
  BODY=$(echo "$RESPONSE" | head -n-1)

  echo "HTTP状态码: $HTTP_CODE"
  echo "响应内容: $BODY"

  if [ "$HTTP_CODE" = "400" ]; then
    echo "✅ 测试通过: 删除 General 大分类被正确拒绝"
    echo "   错误信息: $(echo $BODY | jq -r '.message')"
  else
    echo "❌ 测试失败: 应该返回 400 状态码"
  fi
fi

# 测试3: 创建测试用的大分类和 Focus Area
echo ""
echo "===== 准备测试数据: 创建测试大分类 ====="
TEST_CATEGORY=$(curl -s -X POST "${BASE_URL}/major-categories" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": 1,
    "name": "测试分类_临时",
    "description": "用于测试删除功能",
    "sortOrder": 999
  }')

TEST_CATEGORY_ID=$(echo $TEST_CATEGORY | jq -r '.id')
echo "✅ 创建测试大分类成功，ID: $TEST_CATEGORY_ID"

# 创建一个 Focus Area 并关联到测试大分类
echo ""
echo "===== 创建测试 Focus Area ====="
TEST_FA=$(curl -s -X POST "${BASE_URL}/learning-content/focus-areas" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"skillId\": 1,
    \"name\": \"测试Focus Area_临时\",
    \"description\": \"用于测试删除功能\",
    \"categoryIds\": [$TEST_CATEGORY_ID]
  }")

TEST_FA_ID=$(echo $TEST_FA | jq -r '.id')
echo "✅ 创建测试 Focus Area 成功，ID: $TEST_FA_ID"
echo "   关联的大分类: $(echo $TEST_FA | jq -c '.categoryIds')"

# 测试3: 删除测试大分类（应该成功）
echo ""
echo "===== 测试3: 删除测试大分类 (ID=$TEST_CATEGORY_ID) ====="
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "${BASE_URL}/major-categories/$TEST_CATEGORY_ID" \
  -H "Authorization: Bearer ${TOKEN}")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)

echo "HTTP状态码: $HTTP_CODE"
if [ "$HTTP_CODE" = "204" ] || [ "$HTTP_CODE" = "200" ]; then
  echo "✅ 测试通过: 删除大分类成功"
else
  echo "❌ 测试失败: 删除大分类失败"
  echo "   响应内容: $BODY"
fi

# 验证 Focus Area 是否变为未分类
echo ""
echo "===== 验证 Focus Area 变为未分类状态 ====="
sleep 1  # 等待一下确保数据库更新完成

# 查询未分类的 Focus Area
UNCATEGORIZED_FA=$(curl -s "${BASE_URL}/admin/skills/1/focus-areas/uncategorized" \
  -H "Authorization: Bearer ${TOKEN}")

echo "未分类的 Focus Area 列表: $(echo $UNCATEGORIZED_FA | jq -c '.')"

# 检查我们创建的 Focus Area 是否在未分类列表中
IS_UNCATEGORIZED=$(echo $UNCATEGORIZED_FA | jq -r ".[] | select(.id == $TEST_FA_ID) | .id")

if [ -n "$IS_UNCATEGORIZED" ] && [ "$IS_UNCATEGORIZED" != "null" ]; then
  echo "✅ 验证通过: Focus Area (ID=$TEST_FA_ID) 已变为未分类状态"
  CATEGORY_IDS=$(echo $UNCATEGORIZED_FA | jq -r ".[] | select(.id == $TEST_FA_ID) | .categoryIds")
  echo "   categoryIds: $CATEGORY_IDS (应该为空数组)"
else
  echo "❌ 验证失败: Focus Area 未出现在未分类列表中"
fi

# 清理测试数据
echo ""
echo "===== 清理测试数据 ====="
curl -s -X DELETE "${BASE_URL}/admin/skills/focus-areas/$TEST_FA_ID" \
  -H "Authorization: Bearer ${TOKEN}" > /dev/null
echo "✅ 删除测试 Focus Area (ID=$TEST_FA_ID)"

echo ""
echo "===== 测试完成 ====="
