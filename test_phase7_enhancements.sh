#!/bin/bash

# Phase 7 Enhancements - API测试脚本
# 用途：验证面试准备清单、Focus Area关联、Recruiter Insights功能

set -e

BASE_URL="http://localhost:8082/api"
TOKEN=""
JOB_APP_ID=""
STAGE_ID=""
CHECKLIST_ID=""

echo "========================================="
echo "Phase 7 Enhancements API 测试"
echo "========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}➜ $1${NC}"
}

# Step 1: 登录获取Token
echo "Step 1: 登录获取Token"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "austinxu",
    "password": "helloworld"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')

if [ -z "$TOKEN" ]; then
    print_error "登录失败，请检查后端是否运行"
    echo "Response: $LOGIN_RESPONSE"
    exit 1
fi

print_success "登录成功，Token已获取"
echo ""

# Step 2: 创建测试用的Job Application（包含Recruiter Insights）
echo "Step 2: 创建Job Application（包含Recruiter Insights）"
JOB_RESPONSE=$(curl -s -X POST "$BASE_URL/job-applications" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "companyId": 1,
    "positionName": "Senior Software Engineer - Test",
    "positionLevel": "Senior",
    "qualifications": "5+ years Java experience",
    "responsibilities": "Design and develop microservices",
    "recruiterInsights": {
      "teamSize": "10-15人",
      "teamCulture": "扁平化管理，鼓励创新",
      "techStackPreference": ["Java", "Spring Boot", "Kubernetes"],
      "interviewFocus": "系统设计和编程基础",
      "processTips": "第一轮技术面试会很深入，建议准备常见算法题"
    }
  }')

JOB_APP_ID=$(echo $JOB_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')

if [ -z "$JOB_APP_ID" ]; then
    print_error "创建Job Application失败"
    echo "Response: $JOB_RESPONSE"
    exit 1
fi

print_success "Job Application创建成功，ID: $JOB_APP_ID"
echo ""

# Step 3: 验证Recruiter Insights已保存
echo "Step 3: 验证Recruiter Insights"
JOB_DETAIL=$(curl -s -X GET "$BASE_URL/job-applications/$JOB_APP_ID" \
  -H "Authorization: Bearer $TOKEN")

RECRUITER_INSIGHTS=$(echo $JOB_DETAIL | grep -o '"recruiterInsights":{[^}]*}')

if [ -z "$RECRUITER_INSIGHTS" ]; then
    print_error "Recruiter Insights未保存"
    echo "Response: $JOB_DETAIL"
    exit 1
fi

print_success "Recruiter Insights验证成功"
echo "  - 内容: $(echo $RECRUITER_INSIGHTS | cut -c1-80)..."
echo ""

# Step 4: 创建Interview Stage（包含Focus Area IDs）
echo "Step 4: 创建Interview Stage（包含Focus Area IDs）"
STAGE_RESPONSE=$(curl -s -X POST "$BASE_URL/job-applications/$JOB_APP_ID/stages" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "stageName": "Technical Interview Round 1",
    "stageOrder": 1,
    "skillIds": [1, 2],
    "focusAreaIds": [1, 2, 3],
    "preparationNotes": "# 准备重点\n- 算法：动态规划\n- 系统设计：缓存策略"
  }')

STAGE_ID=$(echo $STAGE_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')

if [ -z "$STAGE_ID" ]; then
    print_error "创建Interview Stage失败"
    echo "Response: $STAGE_RESPONSE"
    exit 1
fi

print_success "Interview Stage创建成功，ID: $STAGE_ID"
echo ""

# Step 5: 验证Focus Area IDs已保存
echo "Step 5: 验证Focus Area IDs"
STAGE_DETAIL=$(curl -s -X GET "$BASE_URL/job-applications/$JOB_APP_ID/stages" \
  -H "Authorization: Bearer $TOKEN")

FOCUS_AREA_IDS=$(echo $STAGE_DETAIL | grep -o '"focusAreaIds":\[[0-9,]*\]')

if [ -z "$FOCUS_AREA_IDS" ]; then
    print_error "Focus Area IDs未保存"
    echo "Response: $STAGE_DETAIL"
    exit 1
fi

print_success "Focus Area IDs验证成功"
echo "  - 内容: $FOCUS_AREA_IDS"
echo ""

# Step 6: 创建Preparation Checklist（单个）
echo "Step 6: 创建Preparation Checklist（单个）"
CHECKLIST_RESPONSE=$(curl -s -X POST "$BASE_URL/interview-preparation-checklist" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"interviewStageId\": $STAGE_ID,
    \"checklistItem\": \"复习动态规划模版（背包问题、最长子序列）\",
    \"isPriority\": true,
    \"category\": \"Study\",
    \"notes\": \"参考labuladong动态规划专题\",
    \"sortOrder\": 1
  }")

CHECKLIST_ID=$(echo $CHECKLIST_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')

if [ -z "$CHECKLIST_ID" ]; then
    print_error "创建Preparation Checklist失败"
    echo "Response: $CHECKLIST_RESPONSE"
    exit 1
fi

print_success "Preparation Checklist创建成功，ID: $CHECKLIST_ID"
echo ""

# Step 7: 批量创建Preparation Checklist
echo "Step 7: 批量创建Preparation Checklist"
BATCH_RESPONSE=$(curl -s -X POST "$BASE_URL/interview-preparation-checklist/batch" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "[
    {
      \"interviewStageId\": $STAGE_ID,
      \"checklistItem\": \"刷LeetCode Medium难度题20道\",
      \"isPriority\": true,
      \"category\": \"Practice\",
      \"notes\": null,
      \"sortOrder\": 2
    },
    {
      \"interviewStageId\": $STAGE_ID,
      \"checklistItem\": \"准备项目经验STAR回答\",
      \"isPriority\": true,
      \"category\": \"Study\",
      \"notes\": \"强调技术难点和business impact\",
      \"sortOrder\": 3
    },
    {
      \"interviewStageId\": $STAGE_ID,
      \"checklistItem\": \"模拟面试1次\",
      \"isPriority\": false,
      \"category\": \"Practice\",
      \"notes\": null,
      \"sortOrder\": 4
    }
  ]")

BATCH_COUNT=$(echo $BATCH_RESPONSE | grep -o '"id":' | wc -l)

if [ "$BATCH_COUNT" -lt 3 ]; then
    print_error "批量创建失败"
    echo "Response: $BATCH_RESPONSE"
    exit 1
fi

print_success "批量创建成功，创建了 $BATCH_COUNT 个清单项"
echo ""

# Step 8: 获取所有Preparation Checklist
echo "Step 8: 获取所有Preparation Checklist"
ALL_CHECKLIST=$(curl -s -X GET "$BASE_URL/interview-preparation-checklist/stage/$STAGE_ID" \
  -H "Authorization: Bearer $TOKEN")

TOTAL_COUNT=$(echo $ALL_CHECKLIST | grep -o '"id":' | wc -l)

if [ "$TOTAL_COUNT" -lt 4 ]; then
    print_error "获取准备清单失败，预期至少4个"
    echo "Response: $ALL_CHECKLIST"
    exit 1
fi

print_success "获取所有准备清单成功，共 $TOTAL_COUNT 个"
echo ""

# Step 9: 获取重点Preparation Checklist（用于打印）
echo "Step 9: 获取重点Preparation Checklist"
PRIORITY_CHECKLIST=$(curl -s -X GET "$BASE_URL/interview-preparation-checklist/stage/$STAGE_ID/priority" \
  -H "Authorization: Bearer $TOKEN")

PRIORITY_COUNT=$(echo $PRIORITY_CHECKLIST | grep -o '"id":' | wc -l)

if [ "$PRIORITY_COUNT" -lt 3 ]; then
    print_error "获取重点清单失败，预期至少3个"
    echo "Response: $PRIORITY_CHECKLIST"
    exit 1
fi

print_success "获取重点准备清单成功，共 $PRIORITY_COUNT 个（可用于打印1-2页）"
echo ""

# Step 10: 更新Preparation Checklist
echo "Step 10: 更新Preparation Checklist"
UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/interview-preparation-checklist/$CHECKLIST_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"checklistItem\": \"复习动态规划模版（已完成）\",
    \"isPriority\": true,
    \"category\": \"Study\",
    \"notes\": \"已复习完成，掌握背包问题、最长子序列、股票问题\",
    \"sortOrder\": 1
  }")

UPDATED_ITEM=$(echo $UPDATE_RESPONSE | grep -o '"checklistItem":"[^"]*已完成')

if [ -z "$UPDATED_ITEM" ]; then
    print_error "更新准备清单失败"
    echo "Response: $UPDATE_RESPONSE"
    exit 1
fi

print_success "更新准备清单成功"
echo "  - 更新内容: $UPDATED_ITEM"
echo ""

# Step 11: 清理测试数据
echo "Step 11: 清理测试数据"
print_info "删除Preparation Checklist: $CHECKLIST_ID"
curl -s -X DELETE "$BASE_URL/interview-preparation-checklist/$CHECKLIST_ID" \
  -H "Authorization: Bearer $TOKEN" > /dev/null

print_info "删除Interview Stage: $STAGE_ID"
curl -s -X DELETE "$BASE_URL/interview-stages/$STAGE_ID" \
  -H "Authorization: Bearer $TOKEN" > /dev/null

print_info "删除Job Application: $JOB_APP_ID"
curl -s -X DELETE "$BASE_URL/job-applications/$JOB_APP_ID" \
  -H "Authorization: Bearer $TOKEN" > /dev/null

print_success "清理完成"
echo ""

# 测试总结
echo "========================================="
echo "✅ 所有测试通过！"
echo "========================================="
echo ""
echo "测试覆盖："
echo "  ✓ Recruiter Insights 创建和读取"
echo "  ✓ Focus Area IDs 关联"
echo "  ✓ Preparation Checklist 单个创建"
echo "  ✓ Preparation Checklist 批量创建"
echo "  ✓ Preparation Checklist 获取（全部/重点）"
echo "  ✓ Preparation Checklist 更新"
echo "  ✓ 数据清理"
echo ""
echo "Phase 7 Enhancements功能验证完成！ 🎉"
