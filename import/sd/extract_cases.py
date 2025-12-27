#!/usr/bin/env python3
"""
从HelloInterview的practice.html中提取系统设计案例列表
"""
import re

def extract_cases_from_html(html_file):
    """从HTML文件中提取案例名称和难度"""
    with open(html_file, 'r', encoding='utf-8') as f:
        content = f.read()

    cases = []

    # 查找所有案例名称的模式
    # 案例名称在 <p class="MuiTypography-root MuiTypography-body1 mui-sgdv9t"> 中
    name_pattern = r'<p class="MuiTypography-root MuiTypography-body1 mui-sgdv9t">(.*?)</p>'
    # 难度在 <p class="MuiTypography-root MuiTypography-body2 mui-..."> 中
    difficulty_pattern = r'<p class="MuiTypography-root MuiTypography-body2 mui-[^"]*">(Easy|Medium|Hard)</p>'

    names = re.findall(name_pattern, content)
    difficulties = re.findall(difficulty_pattern, content)

    # 组合案例名称和难度
    for i, name in enumerate(names):
        difficulty = difficulties[i] if i < len(difficulties) else 'Unknown'
        cases.append({
            'name': name,
            'difficulty': difficulty,
            'slug': name.lower().replace(' ', '-').replace('design-', '')
        })

    return cases

def generate_markdown(cases, output_file):
    """生成markdown文档"""
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('# HelloInterview 系统设计典型案例\n\n')
        f.write('> 来源: https://www.hellointerview.com/learn/system-design/practice\n\n')
        f.write(f'共 {len(cases)} 个案例\n\n')

        # 按难度分组
        easy_cases = [c for c in cases if c['difficulty'] == 'Easy']
        medium_cases = [c for c in cases if c['difficulty'] == 'Medium']
        hard_cases = [c for c in cases if c['difficulty'] == 'Hard']

        f.write('## 统计\n\n')
        f.write(f'- Easy: {len(easy_cases)} 个\n')
        f.write(f'- Medium: {len(medium_cases)} 个\n')
        f.write(f'- Hard: {len(hard_cases)} 个\n\n')

        f.write('---\n\n')

        # Easy难度
        if easy_cases:
            f.write('## Easy (简单)\n\n')
            f.write('| # | 案例名称 | URL |\n')
            f.write('|---|---------|-----|\n')
            for idx, case in enumerate(easy_cases, 1):
                url = f"https://www.hellointerview.com/learn/system-design/answer-keys/{case['slug']}"
                f.write(f"| {idx} | {case['name']} | {url} |\n")
            f.write('\n')

        # Medium难度
        if medium_cases:
            f.write('## Medium (中等)\n\n')
            f.write('| # | 案例名称 | URL |\n')
            f.write('|---|---------|-----|\n')
            for idx, case in enumerate(medium_cases, 1):
                url = f"https://www.hellointerview.com/learn/system-design/answer-keys/{case['slug']}"
                f.write(f"| {idx} | {case['name']} | {url} |\n")
            f.write('\n')

        # Hard难度
        if hard_cases:
            f.write('## Hard (困难)\n\n')
            f.write('| # | 案例名称 | URL |\n')
            f.write('|---|---------|-----|\n')
            for idx, case in enumerate(hard_cases, 1):
                url = f"https://www.hellointerview.com/learn/system-design/answer-keys/{case['slug']}"
                f.write(f"| {idx} | {case['name']} | {url} |\n")
            f.write('\n')

if __name__ == '__main__':
    html_file = 'import/sd/pratice.html'
    output_file = 'import/sd/典型案例.md'

    print(f'从 {html_file} 提取案例...')
    cases = extract_cases_from_html(html_file)

    print(f'找到 {len(cases)} 个案例')
    print('生成markdown文档...')

    generate_markdown(cases, output_file)
    print(f'✓ 已生成: {output_file}')
