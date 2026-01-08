#!/bin/bash
# API测试辅助函数集合
# 使用方法: source .test_helpers/api_test_helpers.sh

API_BASE="http://localhost:8082/api"

# ============ JWT Token 管理 ============

# 全局变量存储常用用户的token
declare -A USER_TOKENS

# 获取JWT Token（带缓存）
# 用法: get_token [username] [password]
get_token() {
  local USERNAME=${1:-"austin.xyz"}
  local PASSWORD=${2:-"helloworld"}
  local CACHE_KEY="${USERNAME}:${PASSWORD}"

  # 检查缓存
  if [ -n "${USER_TOKENS[$CACHE_KEY]}" ]; then
    echo "${USER_TOKENS[$CACHE_KEY]}"
    return 0
  fi

  # 登录获取新token
  local RESPONSE=$(curl -s -X POST "$API_BASE/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

  local TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

  if [ -z "$TOKEN" ]; then
    echo "❌ 登录失败 (用户: $USERNAME)！响应: $RESPONSE" >&2
    return 1
  fi

  # 缓存token
  USER_TOKENS[$CACHE_KEY]=$TOKEN
  echo "$TOKEN"
}

# 预定义用户token获取函数
get_token_user1() { get_token "austinxu" "helloworld"; }
get_token_user3() { get_token "austin.xyz" "helloworld"; }

# ============ API 调用辅助函数 ============

# GET请求
# 用法: api_get <endpoint> [username]
api_get() {
  local ENDPOINT=$1
  local USERNAME=${2:-"austin.xyz"}
  local TOKEN=$(get_token "$USERNAME")

  curl -s -X GET "$API_BASE$ENDPOINT" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json"
}

# POST请求
# 用法: api_post <endpoint> <json_data> [username]
api_post() {
  local ENDPOINT=$1
  local DATA=$2
  local USERNAME=${3:-"austin.xyz"}
  local TOKEN=$(get_token "$USERNAME")

  curl -s -X POST "$API_BASE$ENDPOINT" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "$DATA"
}

# PUT请求
# 用法: api_put <endpoint> <json_data> [username]
api_put() {
  local ENDPOINT=$1
  local DATA=$2
  local USERNAME=${3:-"austin.xyz"}
  local TOKEN=$(get_token "$USERNAME")

  curl -s -X PUT "$API_BASE$ENDPOINT" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "$DATA"
}

# DELETE请求
# 用法: api_delete <endpoint> [username]
api_delete() {
  local ENDPOINT=$1
  local USERNAME=${2:-"austin.xyz"}
  local TOKEN=$(get_token "$USERNAME")

  curl -s -X DELETE "$API_BASE$ENDPOINT" \
    -H "Authorization: Bearer $TOKEN"
}

# ============ 简历相关API ============

# 获取职位的定制简历
# 用法: get_job_resume <job_application_id> [username]
get_job_resume() {
  local JOB_ID=$1
  local USERNAME=${2:-"austin.xyz"}
  api_get "/resumes/by-job/$JOB_ID" "$USERNAME"
}

# 创建定制简历
# 用法: clone_resume_for_job <job_application_id> [username]
clone_resume_for_job() {
  local JOB_ID=$1
  local USERNAME=${2:-"austin.xyz"}
  api_post "/resumes/clone-for-job/$JOB_ID" "" "$USERNAME"
}

# 获取所有职位申请
# 用法: get_job_applications [username]
get_job_applications() {
  local USERNAME=${1:-"austin.xyz"}
  api_get "/job-applications" "$USERNAME"
}

# ============ 使用示例 ============
# source .test_helpers/api_test_helpers.sh
# get_job_applications | jq '.[0]'
# clone_resume_for_job 1 | jq '.'
# get_job_resume 1 | jq '.experiences'
