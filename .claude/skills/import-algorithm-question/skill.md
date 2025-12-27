# Import Algorithm Question Skill

智能生成LeetCode算法题的高质量学习笔记，严格遵循质量标准。

## 功能描述

从`import/算法题_整理.md`中读取题目基本信息（题号、标题、链接、核心思路），使用 LeetCode GraphQL API 获取题目详情，调用AI生成高质量笔记（严格遵循LeetCode笔记生成标准），然后保存到 `notes/` 目录。

**核心特性**：
- 使用 LeetCode GraphQL API 获取题目详情（比 WebFetch 更稳定快速）
- 严格遵循笔记生成标准（800字、40行代码、6个章节）
- 自动质量检查
- 保存到 `notes/` 目录（格式：`{leetcode_num}_{title}.md`）

## 使用方式

### 方式1：通过序号生成
```
/import-algorithm-question 276
```
生成算法题_整理.md中的第276题（2611. 老鼠和奶酪）笔记

### 方式2：通过LeetCode编号生成
```
/import-algorithm-question --leetcode 2611
```
生成LeetCode 2611题笔记

### 方式3：批量生成（范围）
```
/import-algorithm-question --range 101-110
```
生成第101-110题笔记（逐题确认）

## 工作流程

1. **提取原始信息**：从算法题_整理.md解析题目序号、LeetCode编号、标题、链接、核心思路
2. **调用 LeetCode GraphQL API**：获取题目描述、答案要求、难度、标签（优先 LeetCode.com → LeetCode.cn → WebSearch）
3. **生成高质量笔记**：调用AI，严格按照`prompt/LeetCode笔记生成标准Prompt.md`生成800字以上笔记
4. **Focus Area归类**：基于标签智能归类到18个Focus Area之一
5. **质量检查**：自动检查字数、代码行数、章节完整性、核心策略长度
6. **保存笔记**：保存到 `notes/{leetcode_num}_{title}.md`（格式如 `5_Longest_Palindromic_Substring.md`）

## 笔记质量标准

严格遵循`prompt/LeetCode笔记生成标准Prompt.md`：

✅ **结构要求**：6个章节（解题思路、解题步骤、代码示例、关键提示、复杂度分析、核心策略）
✅ **字数要求**：800字以上
✅ **代码要求**：40行以上（不含空行和注释），完整可运行
✅ **步骤要求**：每种方法3-7步，具体到操作细节
✅ **提示要求**：至少3-5条，包括边界条件、易错点、优化思路、相关题目
✅ **核心策略**：一句话总结（不超过30字），存入`core_strategy`字段

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

1. **笔记生成遵循标准**：完全按照`prompt/LeetCode笔记生成标准Prompt.md`的格式和质量要求
2. **核心策略来源**：优先使用算法题_整理.md中的原始核心思路，如果不符合30字要求则由AI优化
3. **质量自检**：生成笔记前自动检查是否满足质量标准（800字、40行代码、6个章节）
4. **预览模式**：默认先展示生成的笔记内容（包括核心策略、完整笔记、代码行数），需用户确认
5. **保存位置**：笔记保存到 `notes/` 目录，文件名格式：`{leetcode_num}_{title_snake_case}.md`
6. **数据库导入**：使用其他 skill 将笔记导入数据库（本 skill 不负责数据库操作）
7. **GraphQL API**：优先使用 LeetCode GraphQL API 获取题目信息，比 WebFetch 更稳定快速

## 示例输出

```
📋 题目分析完成：[2611] Mice and Cheese

✅ Focus Area归类: Greedy (50)
✅ 难度: MEDIUM
✅ 标签: ["Array", "Greedy", "Sorting"]
✅ 复杂度: 时间O(n log n), 空间O(n)

📝 核心策略（30字以内）:
按reward1[i]-reward2[i]排序，选前k个用reward1，其余用reward2求和

📖 笔记内容预览:
==================== 笔记预览 ====================
## 1. 解题思路
本题要求在两个数组中选择元素求和，有以下三种解法：...

[完整6个章节...]

**核心策略**: 按reward1[i]-reward2[i]排序，选前k个用reward1，其余用reward2求和
=================================================

✅ 质量检查通过:
  - 字数: 1024字 ✅
  - 代码行数: 58行 ✅
  - 章节完整性: 6/6 ✅
  - 核心策略长度: 28字 ✅

💾 保存路径: notes/2611_Mice_and_Cheese.md

确认保存? (yes/no)
```

**保存成功后**：
```
✅ 笔记已成功保存到 notes/2611_Mice_and_Cheese.md

📊 笔记统计:
  - 字数: 1024字
  - 代码行数: 58行
  - 核心策略: 按reward1[i]-reward2[i]排序，选前k个用reward1，其余用reward2求和

💡 下一步操作建议:
  1. 查看笔记: cat notes/2611_Mice_and_Cheese.md
  2. 导入数据库: 使用其他 skill 将笔记导入数据库
  3. 继续生成: /import-algorithm-question <题号>
```

## 错误处理

- ❌ 题目编号不存在：提示题号范围（1-276）
- ❌ LeetCode GraphQL API 无法访问：尝试 LeetCode.cn → WebSearch
- ❌ Focus Area 无法确定：提示用户手动选择
- ❌ 笔记文件已存在：询问是否覆盖
- ❌ 笔记质量不达标：重新生成或手动调整
- ❌ 题目不存在：提示用户检查题号或 LeetCode 链接

## 依赖

- Python 3.x
- backend/.env（数据库配置，用于读取题目信息）
- import/算法题_整理.md（题目源文件）
- prompt/LeetCode笔记生成标准Prompt.md（笔记生成标准）
- 网络访问 LeetCode GraphQL API

## 限制

- 每次只能生成一道题（批量模式逐题确认）
- 需要网络访问 LeetCode GraphQL API（获取题目详情）
- 笔记由AI生成，自动检查质量标准
- 批量生成时会逐题确认（避免错误生成）
- 本 skill 不负责数据库导入，需要使用其他 skill
