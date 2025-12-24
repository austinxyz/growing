# Import Algorithm Question Skill

智能导入算法题到数据库的skill。从`import/算法题_整理.md`中提取题目，自动分析并扩展信息后插入数据库。

## 功能描述

从算法题_整理.md文件中读取指定题目，使用AI分析题目内容，获取完整的LeetCode信息，自动归类到正确的Focus Area，然后插入到数据库的三个表中：
- `questions` - 题目基本信息
- `programming_question_details` - 编程题详情
- `user_question_notes` - 用户笔记（austin.xyz用户）

## 使用方式

### 方式1：通过题号导入
```
/import-algorithm-question 276
```
导入第276题（2611. 老鼠和奶酪）

### 方式2：通过LeetCode编号导入
```
/import-algorithm-question --leetcode 2611
```
导入LeetCode 2611题

### 方式3：通过题目名称导入（模糊匹配）
```
/import-algorithm-question "老鼠和奶酪"
```

### 方式4：批量导入（范围）
```
/import-algorithm-question --range 101-150
```
导入第101-150题

## 工作流程

1. **提取原始信息**：从算法题_整理.md中解析题目的序号、LeetCode编号、标题、链接、核心思路
2. **AI分析扩展**：调用general-purpose agent访问LeetCode获取完整信息（题目描述、难度、标签等）
3. **Focus Area归类**：基于题目类型和标签，智能归类到18个Focus Area之一
4. **生成笔记内容**：基于原始核心思路，扩展生成完整的解题步骤、复杂度分析、注意事项
5. **预览确认**：展示即将插入的完整数据，等待用户确认
6. **插入数据库**：依次插入questions、programming_question_details、user_question_notes三个表

## 可用的Focus Area映射

- Array (35)
- Linked List (36)
- Stack and Queue (37)
- Hash Table (38)
- Binary Tree (39)
- Graph (40)
- Sorting (41)
- Data Structure Design (42)
- Backtracking (43)
- BFS (44)
- DP Fundamentals (45)
- Subsequence Problems (46)
- Knapsack Problems (47)
- Game Theory (48)
- Classic DP (49)
- Greedy (50)
- Math Tricks (51)
- Classic Interview (52)

## 重要说明

1. **核心思路保持原样**：user_question_notes表的`core_strategy`字段直接使用算法题_整理.md中的原始核心思路
2. **笔记内容扩展**：`note_content`字段包含核心思路+解题步骤+复杂度分析+注意事项
3. **用户归属**：所有笔记都关联到austin.xyz用户（user_id=1）
4. **去重检查**：导入前检查题目是否已存在（基于LeetCode编号）
5. **预览模式**：默认先展示数据，需要用户确认后才实际导入

## 示例输出

```
📋 题目分析完成：[2611] Mice and Cheese

✅ Focus Area归类: Greedy (50)
✅ 难度: MEDIUM
✅ 标签: ["Array", "Greedy", "Sorting"]
✅ 复杂度: 时间O(n log n), 空间O(n)

📝 核心思路（原始）:
order reward1[i]-reward2[i], find k max, use reward1, else reward2, sum.

📖 扩展笔记长度: 523字符

🔍 数据库检查: 题目不存在，可以导入

确认导入? (yes/no)
```

## 错误处理

- ❌ 题目编号不存在：提示题号范围（1-276）
- ❌ LeetCode链接无法访问：使用WebSearch搜索题目信息
- ❌ Focus Area无法确定：提示用户手动选择
- ❌ 题目已存在：提示是否更新现有数据
- ❌ 数据库连接失败：显示详细错误信息

## 依赖

- Python 3.x
- mysql-connector-python
- backend/.env（数据库配置）
- import/算法题_整理.md（题目源文件）

## 限制

- 每次只能导入一道题（批量模式除外）
- 需要网络访问LeetCode
- 笔记内容由AI生成，可能需要人工审核
- 批量导入时会逐题确认（避免错误导入）
