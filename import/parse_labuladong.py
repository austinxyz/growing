#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
解析 labuladong_phase4_v2_staged.md 文件并生成SQL导入脚本
"""

import re
from datetime import datetime

# 大分类名称映射(去除标号)
CATEGORY_MAP = {
    '大分类 1: 数据结构': 'Data Structure',
    '大分类 2: 搜索': 'Search',
    '大分类 3: 动规': 'Dynamic Programming',
    '大分类 4: 其他': 'Others'
}

# Focus Area名称映射(去除标号,转换为英文)
FOCUS_AREA_MAP = {
    'Focus Area 1.1: 数组': 'Array',
    'Focus Area 1.2: 链表': 'Linked List',
    'Focus Area 1.3: 栈与队列': 'Stack and Queue',
    'Focus Area 1.4: 哈希表': 'Hash Table',
    'Focus Area 1.5: 二叉树': 'Binary Tree',
    'Focus Area 1.6: 图': 'Graph',
    'Focus Area 1.7: 排序算法': 'Sorting',
    'Focus Area 1.8: 数据结构设计': 'Data Structure Design',
    'Focus Area 2.1: 回溯算法': 'Backtracking',
    'Focus Area 2.2: BFS算法': 'BFS',
    'Focus Area 3.1: 动态规划基础': 'DP Fundamentals',
    'Focus Area 3.2: 子序列问题': 'Subsequence Problems',
    'Focus Area 3.3: 背包问题': 'Knapsack Problems',
    'Focus Area 3.4: 博弈与游戏': 'Game Theory',
    'Focus Area 3.5: 经典DP问题': 'Classic DP',
    'Focus Area 3.6: 贪心算法': 'Greedy',
    'Focus Area 4.1: 数学技巧': 'Math Tricks',
    'Focus Area 4.2: 经典面试题': 'Classic Interview'
}

# 学习阶段类型映射
STAGE_MAP = {
    '阶段1: 基础原理 (theory)': 'theory',
    '阶段2: 实现代码 (implementation)': 'implementation',
    '阶段3: 实战题目 (practice)': 'practice'
}


def parse_markdown(file_path):
    """解析Markdown文件,提取Focus Area和学习内容"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 存储结果
    categories = []
    focus_areas = []
    contents = []

    current_category = None
    current_focus_area = None
    current_stage = None

    lines = content.split('\n')

    for line in lines:
        # 匹配大分类 (## 大分类 1: 数据结构)
        if line.startswith('## 大分类'):
            category_match = re.match(r'## (大分类 \d+: .+)', line)
            if category_match:
                category_name = category_match.group(1)
                if category_name in CATEGORY_MAP:
                    current_category = {
                        'name': category_name,
                        'english_name': CATEGORY_MAP[category_name]
                    }
                    if current_category not in categories:
                        categories.append(current_category)

        # 匹配Focus Area (### Focus Area 1.1: 数组)
        elif line.startswith('### Focus Area'):
            fa_match = re.match(r'### (Focus Area \d+\.\d+: .+)', line)
            if fa_match:
                fa_name = fa_match.group(1)
                if fa_name in FOCUS_AREA_MAP:
                    current_focus_area = {
                        'name': fa_name,
                        'english_name': FOCUS_AREA_MAP[fa_name],
                        'category': current_category['english_name'] if current_category else None,
                        'contents': {
                            'theory': [],
                            'implementation': [],
                            'practice': []
                        }
                    }
                    focus_areas.append(current_focus_area)
                    current_stage = None

        # 匹配学习阶段 (#### 阶段1: 基础原理 (theory))
        elif line.startswith('#### 阶段'):
            stage_match = re.match(r'#### (阶段\d+: .+ \(.+\))', line)
            if stage_match:
                stage_name = stage_match.group(1)
                if stage_name in STAGE_MAP:
                    current_stage = STAGE_MAP[stage_name]

        # 匹配学习内容 (- [标题](URL))
        elif line.startswith('- [') and current_focus_area and current_stage:
            content_match = re.match(r'- \[(.+?)\]\((.+?)\)', line)
            if content_match:
                title = content_match.group(1)
                url = content_match.group(2)

                # 判断内容类型
                content_type = 'article'  # 默认为文章
                if '【游戏】' in title:
                    content_type = 'code_example'
                elif '【练习】' in title:
                    content_type = 'article'  # 练习题当作文章

                current_focus_area['contents'][current_stage].append({
                    'title': title,
                    'url': url,
                    'content_type': content_type,
                    'stage': current_stage
                })

    return categories, focus_areas


def generate_sql(categories, focus_areas):
    """生成SQL导入脚本"""
    sql_lines = []

    # SQL头部
    sql_lines.append("-- =====================================================")
    sql_lines.append("-- Labuladong Phase 4 数据导入脚本")
    sql_lines.append(f"-- 生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    sql_lines.append("-- 数据来源: labuladong_phase4_v2_staged.md")
    sql_lines.append("-- =====================================================")
    sql_lines.append("")

    # 1. 插入大分类
    sql_lines.append("-- 1. 插入大分类 (major_categories表已在V7 migration中创建)")
    sql_lines.append("-- 注意: major_categories已在V7中初始化,这里只做确认")
    sql_lines.append("")

    # 2. 插入Focus Area并建立关联
    sql_lines.append("-- 2. 插入Focus Area (focus_areas表)")
    sql_lines.append("-- 假设skill_id=1为'编程与数据结构'")
    sql_lines.append("SET @skill_id = 1;")
    sql_lines.append("SET @admin_user_id = 1;  -- 假设管理员用户ID为1")
    sql_lines.append("")

    # 为每个大分类创建变量
    for i, category in enumerate(categories, 1):
        sql_lines.append(f"SET @category_{i}_id = (SELECT id FROM major_categories WHERE name = '{category['name'].split(': ')[1]}' LIMIT 1);")
    sql_lines.append("")

    # 为每个学习阶段创建变量
    sql_lines.append("-- 获取学习阶段ID")
    sql_lines.append("SET @stage_theory_id = (SELECT id FROM learning_stages WHERE skill_id = @skill_id AND stage_type = 'theory' LIMIT 1);")
    sql_lines.append("SET @stage_implementation_id = (SELECT id FROM learning_stages WHERE skill_id = @skill_id AND stage_type = 'implementation' LIMIT 1);")
    sql_lines.append("SET @stage_practice_id = (SELECT id FROM learning_stages WHERE skill_id = @skill_id AND stage_type = 'practice' LIMIT 1);")
    sql_lines.append("")

    # 插入Focus Area
    sql_lines.append("-- 插入Focus Area")
    for i, fa in enumerate(focus_areas, 1):
        # 找到对应的大分类ID变量
        category_id_var = None
        for j, cat in enumerate(categories, 1):
            if cat['english_name'] == fa['category']:
                category_id_var = f"@category_{j}_id"
                break

        sql_lines.append(f"INSERT INTO focus_areas (skill_id, name, description, created_at)")
        sql_lines.append(f"VALUES (@skill_id, '{fa['english_name']}', '{fa['english_name']} - 来自labuladong算法笔记', NOW());")
        sql_lines.append(f"SET @fa_{i}_id = LAST_INSERT_ID();")
        sql_lines.append("")

        # 建立Focus Area与大分类的关联
        if category_id_var:
            sql_lines.append(f"INSERT INTO focus_area_categories (focus_area_id, category_id)")
            sql_lines.append(f"VALUES (@fa_{i}_id, {category_id_var});")
            sql_lines.append("")

    # 3. 插入学习内容
    sql_lines.append("-- 3. 插入学习内容 (learning_contents表)")
    sql_lines.append("")

    for i, fa in enumerate(focus_areas, 1):
        sql_lines.append(f"-- Focus Area: {fa['english_name']}")

        # 遍历3个阶段
        for stage_type, stage_var in [
            ('theory', '@stage_theory_id'),
            ('implementation', '@stage_implementation_id'),
            ('practice', '@stage_practice_id')
        ]:
            stage_contents = fa['contents'][stage_type]
            if stage_contents:
                stage_name = {'theory': '基础原理', 'implementation': '实现代码', 'practice': '实战题目'}[stage_type]
                sql_lines.append(f"-- {stage_name} ({len(stage_contents)}篇)")

                for j, content in enumerate(stage_contents, 1):
                    # 转义单引号
                    title = content['title'].replace("'", "''")
                    url = content['url'].replace("'", "''")

                    sql_lines.append(f"INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)")
                    sql_lines.append(f"VALUES (@fa_{i}_id, {stage_var}, '{content['content_type']}', '{title}', '{url}', 'labuladong', {j}, 'public', @admin_user_id, NOW());")

        sql_lines.append("")

    # SQL尾部
    sql_lines.append("-- =====================================================")
    sql_lines.append("-- 导入完成统计")
    sql_lines.append("-- =====================================================")
    sql_lines.append(f"-- 大分类数量: {len(categories)}")
    sql_lines.append(f"-- Focus Area数量: {len(focus_areas)}")

    # 统计每个Focus Area的内容数量
    total_contents = 0
    for fa in focus_areas:
        fa_total = sum(len(fa['contents'][stage]) for stage in ['theory', 'implementation', 'practice'])
        total_contents += fa_total
        sql_lines.append(f"-- {fa['english_name']}: {fa_total}篇")

    sql_lines.append(f"-- 学习内容总数: {total_contents}")
    sql_lines.append("-- =====================================================")

    return '\n'.join(sql_lines)


def main():
    """主函数"""
    input_file = '/Users/yanzxu/claude/growing/import/labuladong_phase4_v2_staged.md'
    output_file = '/Users/yanzxu/claude/growing/import/import_labuladong_contents.sql'

    print(f"📖 读取文件: {input_file}")
    categories, focus_areas = parse_markdown(input_file)

    print(f"✅ 解析完成:")
    print(f"   - 大分类: {len(categories)}")
    print(f"   - Focus Area: {len(focus_areas)}")

    # 统计内容
    total_contents = 0
    for fa in focus_areas:
        fa_total = sum(len(fa['contents'][stage]) for stage in ['theory', 'implementation', 'practice'])
        total_contents += fa_total
        print(f"   - {fa['english_name']}: {fa_total}篇 (理论:{len(fa['contents']['theory'])}, 实现:{len(fa['contents']['implementation'])}, 实战:{len(fa['contents']['practice'])})")

    print(f"   - 学习内容总数: {total_contents}")

    print(f"\n📝 生成SQL脚本...")
    sql = generate_sql(categories, focus_areas)

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(sql)

    print(f"✅ SQL脚本已生成: {output_file}")
    print(f"   总行数: {len(sql.split(chr(10)))}")


if __name__ == '__main__':
    main()
