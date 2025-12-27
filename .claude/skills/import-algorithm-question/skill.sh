#!/bin/bash
# Import Algorithm Question Skill - Entry Point
# 严格遵循 prompt/LeetCode笔记生成标准Prompt.md 生成高质量笔记

set -e

SKILL_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SKILL_DIR/../../.." && pwd)"

# 检查参数
if [ $# -eq 0 ]; then
    echo "❌ 请提供题号或LeetCode编号"
    echo ""
    echo "用法示例:"
    echo "  /import-algorithm-question 276              # 导入第276题"
    echo "  /import-algorithm-question --leetcode 2611  # 导入LeetCode 2611"
    echo "  /import-algorithm-question --range 101-110  # 批量导入"
    exit 1
fi

cd "$PROJECT_ROOT"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 LeetCode算法题智能导入系统"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Step 1: 提取题目信息
echo "🔍 Step 1/6: 从算法题_整理.md中提取题目信息..."
QUESTION_JSON=$(python3 "$SKILL_DIR/skill.py" "$@")

if [ $? -ne 0 ]; then
    echo "❌ 题目提取失败"
    exit 1
fi

# 解析JSON（最后一行是JSON）
QUESTION_DATA=$(echo "$QUESTION_JSON" | tail -n 1)

echo "✅ 题目信息已提取"
echo ""
echo "📌 原始数据:"
echo "$QUESTION_DATA" | jq '.'
echo ""

# 输出JSON供Claude使用（这是skill的返回值，Claude会读取）
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📋 题目基本信息已准备完毕，等待Claude Code接管后续步骤..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "接下来Claude Code将执行:"
echo "  Step 2: 调用 LeetCode GraphQL API 获取题目详情"
echo "  Step 3: 使用标准Prompt生成800字以上高质量笔记"
echo "  Step 4: Focus Area智能归类"
echo "  Step 5: 质量检查（字数、代码行数、章节完整性）"
echo "  Step 6: 保存笔记到 notes/{leetcode_num}_{title}.md"
echo ""

# 输出JSON供Claude Code使用
echo "$QUESTION_DATA"
