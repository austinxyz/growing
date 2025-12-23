#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
解析算法题.md文件，提取题目标题、链接和核心思路
【】标记表示重点题目
支持多题号共用思路格式：378/373 思路说明
"""

import re

def parse_markdown_file(file_path):
    """解析markdown文件"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 存储题目信息
    questions_dict = {}

    # 步骤1: 提取所有题目链接
    link_pattern = r'\[([^\]]+)\]\((https://leetcode\.com/problems/[^\)]+)\)'
    matches = re.findall(link_pattern, content)

    for title, url in matches:
        # 清理标题
        title = title.strip('*').strip()

        # 提取题目编号
        number_match = re.match(r'(\d+)\.', title)
        if number_match:
            number = number_match.group(1)
            questions_dict[number] = {
                'number': number,
                'title': title,
                'link': url,
                'thought': '',
                'is_important': False  # 是否重点题目
            }

    print(f"找到 {len(questions_dict)} 道题目链接")

    # 步骤2: 利用思路前的题号进行精确匹配，同时识别【】标记
    # 支持多题号格式：378/373 或 378,373
    lines = content.split('\n')

    for line in lines:
        # 跳过空行和标题
        if not line.strip() or line.startswith('#'):
            continue

        # 模式1: 匹配多题号格式 "【】?数字[/,]数字[/,]数字... 思路"
        # 例如: "378/373 都是K的有序数组" 或 "【】82 快慢指针"
        # 使用空格或中文逗号作为分隔符
        pattern_multi = r'(【】)?([\d/,]+)[，\s]\s*([^|【\n]+?)(?=(?:\s+【】?[\d/,]+[，\s])|$|【|\|)'
        matches_multi = re.findall(pattern_multi, line)

        for is_important_marker, nums_str, thought in matches_multi:
            # 解析题号列表（支持 / 和 , 分隔）
            nums_list = re.split(r'[/,]', nums_str)
            nums_list = [n.strip() for n in nums_list if n.strip().isdigit()]

            thought = thought.strip()

            if nums_list and thought and len(thought) > 3:
                # 清理思路文本
                thought = re.sub(r'\s+\d+$', '', thought)

                # 为所有题号关联同一个思路
                for num in nums_list:
                    if num in questions_dict and not questions_dict[num]['thought']:
                        questions_dict[num]['thought'] = thought

                        # 标记是否是重点题目
                        if is_important_marker == '【】':
                            questions_dict[num]['is_important'] = True
                            if len(nums_list) > 1:
                                print(f"✓ 【重点】题号 {num} (共{len(nums_list)}题): {thought[:50]}...")
                            else:
                                print(f"✓ 【重点】题号 {num}: {thought[:50]}...")
                        else:
                            if len(nums_list) > 1:
                                print(f"✓ 题号 {num} (共{len(nums_list)}题): {thought[:50]}...")
                            else:
                                print(f"✓ 题号 {num}: {thought[:50]}...")

    # 转换为列表并排序
    questions_list = list(questions_dict.values())
    questions_list.sort(key=lambda x: int(x['number']) if x['number'].isdigit() else 9999)

    return questions_list

def format_output(questions):
    """格式化输出为markdown"""
    output = "# 算法题整理\n\n"
    output += f"共 {len(questions)} 道题目\n\n"

    # 统计有思路的题目
    with_thought = sum(1 for q in questions if q['thought'])
    important_count = sum(1 for q in questions if q['is_important'])

    output += f"其中 {with_thought} 道题目有核心思路说明\n\n"
    output += f"其中 {important_count} 道为重点题目 ⭐\n\n"
    output += "---\n\n"

    # 先输出重点题目
    output += "## 重点题目 ⭐\n\n"
    important_questions = [q for q in questions if q['is_important']]

    if important_questions:
        for i, q in enumerate(important_questions, 1):
            output += f"### {i}. [{q['number']}] {q['title']}\n\n"
            output += f"**LeetCode链接**: {q['link']}\n\n"
            if q['thought']:
                output += f"**核心思路**: {q['thought']}\n\n"
            else:
                output += f"**核心思路**: 待补充\n\n"
            output += "---\n\n"
    else:
        output += "暂无重点题目标记\n\n---\n\n"

    # 输出所有题目
    output += "## 所有题目\n\n"

    for i, q in enumerate(questions, 1):
        # 重点题目添加⭐标记
        star = " ⭐" if q['is_important'] else ""
        output += f"### {i}. [{q['number']}] {q['title']}{star}\n\n"
        output += f"**LeetCode链接**: {q['link']}\n\n"
        if q['thought']:
            output += f"**核心思路**: {q['thought']}\n\n"
        else:
            output += f"**核心思路**: 待补充\n\n"
        output += "---\n\n"

    return output

def format_csv_output(questions):
    """格式化输出为CSV"""
    import csv
    import io

    output = io.StringIO()
    writer = csv.writer(output)

    # 写入表头
    writer.writerow(['题号', '标题', '链接', '核心思路', '是否重点'])

    # 写入数据
    for q in questions:
        writer.writerow([
            q['number'],
            q['title'],
            q['link'],
            q['thought'] if q['thought'] else '待补充',
            '是' if q['is_important'] else '否'
        ])

    return output.getvalue()

def main():
    input_file = '/Users/yanzxu/claude/growing/import/算法题.md'
    output_md = '/Users/yanzxu/claude/growing/import/算法题_整理.md'
    output_csv = '/Users/yanzxu/claude/growing/import/算法题_整理.csv'

    print(f"正在解析文件: {input_file}\n")
    print("=" * 60)
    print("开始提取核心思路...")
    print("说明: 【】表示重点题目, 支持多题号共用思路(378/373)")
    print("=" * 60)

    questions = parse_markdown_file(input_file)

    print("\n" + "=" * 60)
    print(f"共整理 {len(questions)} 道题目")

    # 统计有思路的题目
    with_thought = sum(1 for q in questions if q['thought'])
    important_count = sum(1 for q in questions if q['is_important'])

    print(f"其中 {with_thought} 道题目有核心思路")
    print(f"其中 {important_count} 道为重点题目 ⭐")

    # 生成markdown输出
    md_output = format_output(questions)
    with open(output_md, 'w', encoding='utf-8') as f:
        f.write(md_output)
    print(f"\n✅ Markdown文件输出到: {output_md}")

    # 生成CSV输出
    csv_output = format_csv_output(questions)
    with open(output_csv, 'w', encoding='utf-8') as f:
        f.write(csv_output)
    print(f"✅ CSV文件输出到: {output_csv}")

    # 打印重点题目
    important_questions = [q for q in questions if q['is_important']]
    if important_questions:
        print("\n" + "=" * 60)
        print(f"重点题目列表 (共{len(important_questions)}道):")
        print("=" * 60)
        for i, q in enumerate(important_questions[:30], 1):  # 只显示前30道
            print(f"\n{i}. [{q['number']}] {q['title']}")
            if q['thought']:
                print(f"   思路: {q['thought']}")
            else:
                print(f"   思路: 待补充")

    # 统计信息
    print(f"\n" + "=" * 60)
    print("统计信息")
    print("=" * 60)
    print(f"总题目数: {len(questions)}")
    print(f"有思路: {with_thought} ({with_thought/len(questions)*100:.1f}%)")
    print(f"无思路: {len(questions)-with_thought} ({(len(questions)-with_thought)/len(questions)*100:.1f}%)")
    print(f"重点题目: {important_count} ({important_count/len(questions)*100:.1f}%)")

    # 查找共用思路的题目组
    thought_groups = {}
    for q in questions:
        if q['thought']:
            if q['thought'] not in thought_groups:
                thought_groups[q['thought']] = []
            thought_groups[q['thought']].append(q['number'])

    shared_thoughts = {k: v for k, v in thought_groups.items() if len(v) > 1}
    if shared_thoughts:
        print(f"\n共用思路的题目组: {len(shared_thoughts)} 组")
        print("(前5组示例):")
        for i, (thought, nums) in enumerate(list(shared_thoughts.items())[:5], 1):
            print(f"\n{i}. 题号 {', '.join(nums)}")
            print(f"   思路: {thought[:60]}...")

if __name__ == '__main__':
    main()
