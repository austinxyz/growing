#!/usr/bin/env python3
"""
Import Algorithm Question Skill - Implementation
从算法题_整理.md智能导入题目到数据库
"""

import sys
import re
import json
import mysql.connector
from pathlib import Path

# 数据库配置文件路径
ENV_FILE = Path(__file__).parent.parent.parent.parent / "backend" / ".env"
QUESTIONS_FILE = Path(__file__).parent.parent.parent.parent / "import" / "算法题_整理.md"

# Focus Area映射
FOCUS_AREA_MAP = {
    'Array': 35,
    'Linked List': 36,
    'Stack and Queue': 37,
    'Hash Table': 38,
    'Binary Tree': 39,
    'Graph': 40,
    'Sorting': 41,
    'Data Structure Design': 42,
    'Backtracking': 43,
    'BFS': 44,
    'DP Fundamentals': 45,
    'Subsequence Problems': 46,
    'Knapsack Problems': 47,
    'Game Theory': 48,
    'Classic DP': 49,
    'Greedy': 50,
    'Math Tricks': 51,
    'Classic Interview': 52
}

AUSTIN_USER_ID = 3  # austin.xyz user

def load_db_config():
    """从.env文件加载数据库配置"""
    config = {}
    with open(ENV_FILE, 'r') as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith('#') and '=' in line:
                key, value = line.split('=', 1)
                config[key] = value

    return {
        'host': config.get('DB_HOST'),
        'port': int(config.get('DB_PORT', 3306)),
        'user': config.get('DB_USER'),
        'password': config.get('DB_PASSWORD'),
        'database': config.get('DB_NAME')
    }

def parse_question_from_md(seq_num=None, leetcode_num=None, title_keyword=None):
    """从算法题_整理.md中解析题目信息"""
    with open(QUESTIONS_FILE, 'r', encoding='utf-8') as f:
        content = f.read()

    # 正则匹配题目
    pattern = r'### (\d+)\. \[(\d+)\] (.+?)\n\n\*\*LeetCode链接\*\*: (.+?)\n\n\*\*核心思路\*\*: (.+?)(?=\n\n---|\Z)'
    matches = re.findall(pattern, content, re.DOTALL)

    for match in matches:
        q_seq = int(match[0])
        q_leetcode = int(match[1])
        q_title_raw = match[2].strip()
        q_url = match[3].strip()
        q_strategy = match[4].strip()

        # 清理标题（去除标记）
        q_title = q_title_raw.replace('⭐', '').replace('🟠', '').strip()
        is_important = '⭐' in q_title_raw

        # 匹配条件
        if seq_num and q_seq == seq_num:
            return {
                'seq_num': q_seq,
                'leetcode_num': q_leetcode,
                'title': q_title,
                'url': q_url,
                'core_strategy': q_strategy,
                'is_important': is_important
            }
        elif leetcode_num and q_leetcode == leetcode_num:
            return {
                'seq_num': q_seq,
                'leetcode_num': q_leetcode,
                'title': q_title,
                'url': q_url,
                'core_strategy': q_strategy,
                'is_important': is_important
            }
        elif title_keyword and title_keyword.lower() in q_title.lower():
            return {
                'seq_num': q_seq,
                'leetcode_num': q_leetcode,
                'title': q_title,
                'url': q_url,
                'core_strategy': q_strategy,
                'is_important': is_important
            }

    return None

def check_question_exists(cursor, leetcode_num):
    """检查题目是否已存在"""
    cursor.execute("""
        SELECT q.id, q.title
        FROM questions q
        JOIN programming_question_details pqd ON q.id = pqd.question_id
        WHERE q.display_order = %s
    """, (leetcode_num,))
    return cursor.fetchone()

def insert_question(cursor, question_data):
    """插入题目到数据库"""

    # 1. 插入 questions 表
    insert_question_sql = """
    INSERT INTO questions (
        focus_area_id, title, difficulty, question_type,
        is_official, created_by_user_id, display_order
    ) VALUES (%s, %s, %s, %s, %s, %s, %s)
    """

    cursor.execute(insert_question_sql, (
        question_data['focus_area_id'],
        question_data['title'],
        question_data['difficulty'],
        'programming',
        1,
        None,
        question_data['leetcode_num']
    ))

    question_id = cursor.lastrowid

    # 2. 插入 programming_question_details 表
    insert_details_sql = """
    INSERT INTO programming_question_details (
        question_id, leetcode_url, is_important, tags, complexity
    ) VALUES (%s, %s, %s, %s, %s)
    """

    cursor.execute(insert_details_sql, (
        question_id,
        question_data['url'],
        question_data.get('is_important', False),
        json.dumps(question_data['tags'], ensure_ascii=False),
        question_data['complexity']
    ))

    # 3. 插入 user_question_notes 表
    insert_note_sql = """
    INSERT INTO user_question_notes (
        question_id, user_id, note_content, core_strategy
    ) VALUES (%s, %s, %s, %s)
    """

    cursor.execute(insert_note_sql, (
        question_id,
        AUSTIN_USER_ID,
        question_data['note_content'],
        question_data['core_strategy']
    ))

    return question_id

def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("❌ 用法: python skill.py <题号|--leetcode <编号>|--range <开始-结束>>")
        print("示例: python skill.py 276")
        print("示例: python skill.py --leetcode 2611")
        print("示例: python skill.py --range 101-110")
        sys.exit(1)

    # 解析参数
    arg = sys.argv[1]

    if arg == '--leetcode' and len(sys.argv) >= 3:
        leetcode_num = int(sys.argv[2])
        question = parse_question_from_md(leetcode_num=leetcode_num)
    elif arg.isdigit():
        seq_num = int(arg)
        question = parse_question_from_md(seq_num=seq_num)
    else:
        print(f"❌ 无效的参数: {arg}")
        sys.exit(1)

    if not question:
        print(f"❌ 未找到题目")
        sys.exit(1)

    # 显示题目基本信息
    print(f"\n📋 找到题目: #{question['seq_num']} [{question['leetcode_num']}] {question['title']}")
    print(f"🔗 LeetCode链接: {question['url']}")
    print(f"💡 核心思路: {question['core_strategy']}")
    print(f"⭐ 重点题目: {'是' if question['is_important'] else '否'}")
    print("\n" + "="*80)

    # 这里需要调用Claude Code的Task tool来分析题目
    # 由于这是Python脚本，无法直接调用Task tool
    # 所以这个脚本只负责提取原始信息，实际的AI分析和导入需要在Claude Code中完成

    print("\n✅ 题目信息提取完成")
    print(f"\n接下来请让Claude Code调用Task tool分析并导入此题")
    print(f"题目数据已准备好，等待AI分析...")

    # 输出JSON供Claude Code使用
    print("\n" + json.dumps(question, ensure_ascii=False, indent=2))

if __name__ == '__main__':
    main()
