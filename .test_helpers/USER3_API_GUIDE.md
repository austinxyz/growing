# User ID=3 API测试指南

## 用户信息

- **User ID**: 3
- **Username**: austin.xyz
- **Password**: helloworld
- **Email**: austin.xyz@gmail.com
- **Role**: admin

## 职位申请

| ID | 公司 | 职位 | 状态 |
|----|------|------|------|
| 3  | Intuit | Group Manager - AI | NotApplied |
| 13 | LinkedIn | Sr. Manager, Software Engineering, Compute Infrastructure | 未申请 |

## 定制简历

| Resume ID | 职位申请ID | 简历名称 |
|-----------|-----------|---------|
| 6 | 3 (Intuit) | Intuit - Group Manager - AI |
| 5 | 13 (LinkedIn) | LinkedIn - Sr. Manager, Software Engineering, Compute Infrastructure |

## 快速API测试命令

### 1. 获取JWT Token

```bash
# 方法1: 使用辅助脚本
source .test_helpers/api_test_helpers.sh
TOKEN=$(get_token "austin.xyz" "helloworld")
echo $TOKEN

# 方法2: 直接curl
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"austin.xyz","password":"helloworld"}' \
  | jq -r '.token'
```

### 2. 测试获取职位申请列表

```bash
# 设置token（先运行上面的命令获取）
TOKEN='<你的token>'

# 获取所有职位申请
curl -s http://localhost:8082/api/job-applications \
  -H "Authorization: Bearer $TOKEN" \
  | python3 -m json.tool
```

### 3. 测试获取定制简历

```bash
# 获取Intuit职位的定制简历 (job_application_id=3)
curl -s http://localhost:8082/api/resumes/by-job/3 \
  -H "Authorization: Bearer $TOKEN" \
  | python3 -m json.tool

# 获取LinkedIn职位的定制简历 (job_application_id=13)
curl -s http://localhost:8082/api/resumes/by-job/13 \
  -H "Authorization: Bearer $TOKEN" \
  | python3 -m json.tool
```

### 4. 测试创建定制简历（如果不存在）

```bash
# 注意：job_application_id=3和13已经有简历了，不需要再创建
# 如果要测试创建，需要先删除现有简历或创建新的职位申请

curl -X POST http://localhost:8082/api/resumes/clone-for-job/<job_id> \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

## 常见JWT错误及解决方法

### ❌ 错误1: "Invalid compact JWT string"

**原因**: Shell变量传递时，引号处理不当导致token被分割

**错误示例**:
```bash
TOKEN="eyJhbG..."
curl -H "Authorization: Bearer $TOKEN"  # ❌ token被shell分割
```

**正确做法**:
```bash
# 方法1: 单引号包裹token
TOKEN='eyJhbG...'

# 方法2: 直接嵌入命令
curl -H 'Authorization: Bearer eyJhbG...'

# 方法3: 使用辅助脚本
source .test_helpers/api_test_helpers.sh
api_get "/job-applications" "austin.xyz"
```

### ❌ 错误2: Token过期

**原因**: JWT有24小时有效期

**解决**: 重新登录获取新token

```bash
# 清除缓存的token
unset TOKEN

# 重新获取
TOKEN=$(curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"austin.xyz","password":"helloworld"}' \
  | jq -r '.token')
```

### ❌ 错误3: 权限不足 (403 Forbidden)

**原因**: 尝试访问其他用户的资源

**解决**: 确保使用正确的用户登录

## 完整测试示例

```bash
#!/bin/bash
# 完整的API测试流程

# Step 1: 登录获取token
echo "Step 1: 登录..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"austin.xyz","password":"helloworld"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
USER_ID=$(echo $LOGIN_RESPONSE | jq -r '.user.id')

echo "✅ 登录成功！User ID: $USER_ID"
echo "Token: ${TOKEN:0:50}..."

# Step 2: 获取职位申请
echo -e "\nStep 2: 获取职位申请..."
JOBS=$(curl -s http://localhost:8082/api/job-applications \
  -H "Authorization: Bearer $TOKEN")

echo "$JOBS" | jq '.[] | {id, position_name: .positionName, company: .companyName}'

# Step 3: 获取第一个职位的定制简历
JOB_ID=$(echo "$JOBS" | jq -r '.[0].id')
echo -e "\nStep 3: 获取职位 $JOB_ID 的定制简历..."

RESUME=$(curl -s http://localhost:8082/api/resumes/by-job/$JOB_ID \
  -H "Authorization: Bearer $TOKEN")

if [ "$(echo $RESUME | jq -r '.id')" != "null" ]; then
  echo "✅ 找到定制简历！"
  echo "$RESUME" | jq '{id, versionName, experiencesCount: (.experiences | length)}'
else
  echo "⚠️  该职位还没有定制简历"
fi
```

保存为 `test_user3_api.sh` 并运行：
```bash
bash test_user3_api.sh
```
