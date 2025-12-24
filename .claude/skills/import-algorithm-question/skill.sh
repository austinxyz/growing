#!/bin/bash
# Import Algorithm Question Skill - Entry Point

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
    exit 1
fi

# 提取题目信息
cd "$PROJECT_ROOT"

echo "🔍 正在从算法题_整理.md中提取题目信息..."

# 调用Python脚本提取题目原始信息
QUESTION_JSON=$(python3 "$SKILL_DIR/skill.py" "$@")

if [ $? -ne 0 ]; then
    echo "❌ 题目提取失败"
    exit 1
fi

# 解析JSON（最后一行是JSON）
QUESTION_DATA=$(echo "$QUESTION_JSON" | tail -n 1)

echo ""
echo "✅ 题目信息已提取"
echo ""
echo "📌 原始数据:"
echo "$QUESTION_DATA" | jq '.'

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🤖 接下来将调用AI Agent分析并扩展题目信息..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 输出JSON供Claude使用
echo "$QUESTION_DATA"
