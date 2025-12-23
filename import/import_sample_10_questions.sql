-- =====================================================
-- 算法题导入实验脚本 (10道题目样例 - Phase 4 版本)
-- =====================================================
-- 生成时间: 2025-12-22
-- 数据来源: 算法题_整理.md
-- 用户: austin.xyz@gmail.com (测试账号)
-- =====================================================
-- Phase 4 表结构:
-- 1. questions - 题目基本信息 (focus_area_id, stage_id, title, description, difficulty, visibility, created_by)
-- 2. programming_question_details - 编程题专属字段 (leetcode_url, tags, complexity, is_important, similar_questions)
-- 3. user_question_notes - 用户笔记 (question_id, user_id, core_strategy, note_content)
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- STEP 1: 插入 questions 表数据
-- =====================================================
-- 说明:
-- - focus_area_id: 根据核心思路自动分类 (暂用示例值,需根据实际数据库调整)
-- - stage_id: 3 (实战题目阶段,根据V7 migration)
-- - difficulty: EASY/MEDIUM/HARD
-- - visibility: public (公共试题)
-- - created_by: 1 (管理员创建)
-- - description: 题目描述 (Markdown格式)
-- =====================================================

-- 假设 focus_area_id 映射 (需根据实际数据库调整):
-- 数组 -> focus_area_id = 1
-- 链表 -> focus_area_id = 2
-- 回溯算法 -> focus_area_id = 9
-- 二叉树 -> focus_area_id = 5
-- 动态规划基础 -> focus_area_id = 11
-- 贪心算法 -> focus_area_id = 16

-- 题目 1: [5] 5. Longest Palindromic Substring
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  1,  -- 数组 (双指针技巧)
  3,  -- 实战题目阶段
  '[5] Longest Palindromic Substring',
  '**题目描述**: 找到字符串中最长的回文子串。

**考察点**: 双指针扩展、中心扩散法

**示例**:
```
输入: s = "babad"
输出: "bab" 或 "aba"
```',
  'MEDIUM',
  'public',
  1,  -- 管理员创建
  NOW(),
  NOW()
);

-- 题目 2: [15] 3Sum
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  1,  -- 数组 (双指针技巧)
  3,
  '[15] 3Sum',
  '**题目描述**: 找到数组中所有和为0的三元组,不能重复。

**考察点**: 排序 + 双指针、去重逻辑

**示例**:
```
输入: nums = [-1,0,1,2,-1,-4]
输出: [[-1,-1,2],[-1,0,1]]
```',
  'MEDIUM',
  'public',
  1,
  NOW(),
  NOW()
);

-- 题目 3: [30] Substring with Concatenation of All Words
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  1,  -- 数组 (滑动窗口)
  3,
  '[30] Substring with Concatenation of All Words',
  '**题目描述**: 找到字符串中所有由给定单词数组拼接而成的子串起始位置。

**考察点**: 滑动窗口、哈希表计数

**示例**:
```
输入: s = "barfoothefoobarman", words = ["foo","bar"]
输出: [0,9]
```',
  'HARD',
  'public',
  1,
  NOW(),
  NOW()
);

-- 题目 4: [47] Permutations II
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  9,  -- 回溯算法
  3,
  '[47] Permutations II',
  '**题目描述**: 生成包含重复数字的数组的所有不重复排列。

**考察点**: 回溯算法、剪枝去重

**示例**:
```
输入: nums = [1,1,2]
输出: [[1,1,2],[1,2,1],[2,1,1]]
```',
  'MEDIUM',
  'public',
  1,
  NOW(),
  NOW()
);

-- 题目 5: [55] Jump Game
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  16,  -- 贪心算法
  3,
  '[55] Jump Game',
  '**题目描述**: 判断是否能从数组起点跳到终点。

**考察点**: 贪心算法、最远跳跃距离维护

**示例**:
```
输入: nums = [2,3,1,1,4]
输出: true
解释: 跳1步到索引1, 再跳3步到终点
```',
  'MEDIUM',
  'public',
  1,
  NOW(),
  NOW()
);

-- 题目 6: [61] Rotate List
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  2,  -- 链表
  3,
  '[61] Rotate List',
  '**题目描述**: 将链表向右旋转k个位置。

**考察点**: 链表反转、双指针

**示例**:
```
输入: head = [1,2,3,4,5], k = 2
输出: [4,5,1,2,3]
```',
  'MEDIUM',
  'public',
  1,
  NOW(),
  NOW()
);

-- 题目 7: [64] Minimum Path Sum
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  11,  -- 动态规划基础
  3,
  '[64] Minimum Path Sum',
  '**题目描述**: 找到从左上角到右下角的最小路径和 (只能向下或向右移动)。

**考察点**: 二维DP、状态转移方程

**示例**:
```
输入: grid = [[1,3,1],[1,5,1],[4,2,1]]
输出: 7
解释: 路径 1→3→1→1→1
```',
  'MEDIUM',
  'public',
  1,
  NOW(),
  NOW()
);

-- 题目 8: [82] Remove Duplicates from Sorted List II
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  2,  -- 链表
  3,
  '[82] Remove Duplicates from Sorted List II',
  '**题目描述**: 删除排序链表中所有重复节点 (只保留出现一次的节点)。

**考察点**: 快慢指针、哑节点

**示例**:
```
输入: head = [1,2,3,3,4,4,5]
输出: [1,2,5]
```',
  'MEDIUM',
  'public',
  1,
  NOW(),
  NOW()
);

-- 题目 9: [92] Reverse Linked List II
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  2,  -- 链表
  3,
  '[92] Reverse Linked List II',
  '**题目描述**: 反转链表的第m到第n个节点 (一次遍历)。

**考察点**: 链表反转、递归

**示例**:
```
输入: head = [1,2,3,4,5], left = 2, right = 4
输出: [1,4,3,2,5]
```',
  'MEDIUM',
  'public',
  1,
  NOW(),
  NOW()
);

-- 题目 10: [96] Unique Binary Search Trees
INSERT INTO questions (
  focus_area_id,
  stage_id,
  title,
  question_description,
  difficulty,
  visibility,
  created_by,
  created_at,
  updated_at
) VALUES (
  5,  -- 二叉树
  3,
  '[96] Unique Binary Search Trees',
  '**题目描述**: 计算给定n个节点能构成多少种不同的BST。

**考察点**: 卡特兰数、动态规划

**示例**:
```
输入: n = 3
输出: 5
```',
  'MEDIUM',
  'public',
  1,
  NOW(),
  NOW()
);

-- =====================================================
-- STEP 2: 插入 programming_question_details 表数据
-- =====================================================
-- 说明: Phase 4 新增表，存储编程题专属字段
-- - leetcode_url: LeetCode题目链接
-- - is_important: 是否为重要题目
-- - tags: 算法技巧标签 (JSON数组)
-- - complexity: 复杂度描述
-- - similar_questions: 类似题目 (JSON数组)
-- =====================================================

-- 编程题详情 1: [5] Longest Palindromic Substring
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity)
SELECT id, 'https://leetcode.com/problems/longest-palindromic-substring/', TRUE, JSON_ARRAY('双指针', '中心扩展'), '时间O(n²), 空间O(1)'
FROM questions WHERE title = '[5] Longest Palindromic Substring';

-- 编程题详情 2: [15] 3Sum
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity, similar_questions)
SELECT id, 'https://leetcode.com/problems/3sum/', TRUE, JSON_ARRAY('双指针', '排序', '去重'), '时间O(n²), 空间O(1)',
JSON_ARRAY(
  JSON_OBJECT('id', 1, 'title', 'Two Sum'),
  JSON_OBJECT('id', 18, 'title', '4Sum')
)
FROM questions WHERE title = '[15] 3Sum';

-- 编程题详情 3: [30] Substring with Concatenation of All Words
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity)
SELECT id, 'https://leetcode.com/problems/substring-with-concatenation-of-all-words/', FALSE, JSON_ARRAY('滑动窗口', '哈希表'), '时间O(n × wordLen), 空间O(m)'
FROM questions WHERE title = '[30] Substring with Concatenation of All Words';

-- 编程题详情 4: [47] Permutations II
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity)
SELECT id, 'https://leetcode.com/problems/permutations-ii/', TRUE, JSON_ARRAY('回溯', '剪枝', '去重'), '时间O(n × n!), 空间O(n)'
FROM questions WHERE title = '[47] Permutations II';

-- 编程题详情 5: [55] Jump Game
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity, similar_questions)
SELECT id, 'https://leetcode.com/problems/jump-game/', TRUE, JSON_ARRAY('贪心算法', '动态规划'), '时间O(n), 空间O(1)',
JSON_ARRAY(
  JSON_OBJECT('id', 45, 'title', 'Jump Game II')
)
FROM questions WHERE title = '[55] Jump Game';

-- 编程题详情 6: [61] Rotate List
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity)
SELECT id, 'https://leetcode.com/problems/rotate-list/', FALSE, JSON_ARRAY('链表', '双指针', '反转'), '时间O(n), 空间O(1)'
FROM questions WHERE title = '[61] Rotate List';

-- 编程题详情 7: [64] Minimum Path Sum
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity, similar_questions)
SELECT id, 'https://leetcode.com/problems/minimum-path-sum/', TRUE, JSON_ARRAY('动态规划', '二维DP'), '时间O(m × n), 空间O(1)',
JSON_ARRAY(
  JSON_OBJECT('id', 62, 'title', 'Unique Paths'),
  JSON_OBJECT('id', 174, 'title', 'Dungeon Game')
)
FROM questions WHERE title = '[64] Minimum Path Sum';

-- 编程题详情 8: [82] Remove Duplicates from Sorted List II
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity)
SELECT id, 'https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/', FALSE, JSON_ARRAY('链表', '双指针', '哑节点'), '时间O(n), 空间O(1)'
FROM questions WHERE title = '[82] Remove Duplicates from Sorted List II';

-- 编程题详情 9: [92] Reverse Linked List II
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity, similar_questions)
SELECT id, 'https://leetcode.com/problems/reverse-linked-list-ii/', TRUE, JSON_ARRAY('链表', '递归', '反转'), '时间O(n), 空间O(1)',
JSON_ARRAY(
  JSON_OBJECT('id', 206, 'title', 'Reverse Linked List')
)
FROM questions WHERE title = '[92] Reverse Linked List II';

-- 编程题详情 10: [96] Unique Binary Search Trees
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity, similar_questions)
SELECT id, 'https://leetcode.com/problems/unique-binary-search-trees/', TRUE, JSON_ARRAY('动态规划', '卡特兰数', 'BST'), '时间O(n²), 空间O(n)',
JSON_ARRAY(
  JSON_OBJECT('id', 95, 'title', 'Unique Binary Search Trees II')
)
FROM questions WHERE title = '[96] Unique Binary Search Trees';

-- =====================================================
-- STEP 3: 插入 user_question_notes 表数据
-- =====================================================
-- 说明: Phase 4 扩展字段
-- - core_strategy: 核心思路 (用户的解题方法)
-- - note_content: 个人笔记 (补充说明、心得)
-- =====================================================

-- 笔记 1: [5] Longest Palindromic Substring
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'找到i，j往外扩展的palindrom string，然后遍历数组。',
'## 解题步骤

1. 遍历每个字符作为中心点
2. 从中心向两侧扩展，判断是否为回文
3. 需要考虑奇数长度和偶数长度两种情况
4. 维护最长回文串的起始位置和长度

## 复杂度分析

- 时间复杂度: O(n²) - 遍历n个中心点，每次扩展最多n次
- 空间复杂度: O(1) - 只需常数空间

## 注意事项

- 注意边界情况: 单字符和空字符串
- 需要分别处理奇数长度和偶数长度的回文串',
NOW(), NOW()
FROM questions WHERE title = '[5] Longest Palindromic Substring';

-- 笔记 2: [15] 3Sum
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'排序，去除重复的，转化为2sum的问题。',
'## 解题步骤

1. 先对数组排序
2. 固定第一个数 nums[i]
3. 使用双指针在剩余数组中查找和为 -nums[i] 的两个数
4. 左指针从 i+1 开始，右指针从末尾开始
5. 去重：跳过重复的 nums[i]、nums[left]、nums[right]

## 复杂度分析

- 时间复杂度: O(n²) - 排序O(nlogn) + 遍历O(n) × 双指针O(n)
- 空间复杂度: O(1) - 不计结果数组

## 去重技巧

- `while (i > 0 && nums[i] == nums[i-1]) i++;`
- 注意排序后才能用双指针优化',
NOW(), NOW()
FROM questions WHERE title = '[15] 3Sum';

-- 笔记 3: [30] Substring with Concatenation of All Words
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'记录单词长度，总长度（单词长度×word length），从0-word 长度都可以开始，用一个hashmap记录target word count，slide window map，如果发现当前的word count大于target word count，缩小window（left+word count），如果count == target count，记录left，否则增加right长度，如果当前word没在target中，那么left = right，清除slide window map，count为0。',
'## 解题步骤

1. 计算单词长度 wordLen 和总长度 totalLen = wordLen × words.length
2. 用HashMap存储目标单词频次
3. 从0到wordLen-1分别作为起点，避免遗漏
4. 使用滑动窗口，每次移动wordLen距离
5. 维护窗口内单词计数，与目标对比

## 复杂度分析

- 时间复杂度: O(n × wordLen) - 外层循环wordLen次，内层遍历字符串
- 空间复杂度: O(m) - HashMap存储m个单词

## 注意事项

- 需要从每个可能的起点开始滑动（0 ~ wordLen-1）
- HashMap优化计数查找',
NOW(), NOW()
FROM questions WHERE title = '[30] Substring with Concatenation of All Words';

-- 笔记 4: [47] Permutations II
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'1，n，通过i>0 && nums[i-1]==nums[i] && !used[i-1]来剪枝，用used来剪枝，visit',
'## 关键剪枝条件

```java
if (i > 0 && nums[i] == nums[i-1] && !used[i-1]) {
    continue; // 剪枝：同层相同元素只选第一个
}
```

## 解题步骤

1. 先对数组排序（为了剪枝）
2. 使用 used[] 数组标记已使用的元素
3. 回溯过程中，如果当前元素和前一个相同，且前一个未使用，跳过（剪枝）
4. 这样可以避免生成重复的排列

## 复杂度分析

- 时间复杂度: O(n × n!) - 生成n!个排列，每个排列需要O(n)时间
- 空间复杂度: O(n) - used数组和递归栈

## 注意事项

- 排序是剪枝的前提
- used数组避免同一元素被重复使用',
NOW(), NOW()
FROM questions WHERE title = '[47] Permutations II';

-- 笔记 5: [55] Jump Game
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'确保 i < max(nums[i] + i)',
'## 解题步骤

1. 维护一个变量 maxReach 表示当前能到达的最远位置
2. 遍历数组，更新 maxReach = max(maxReach, i + nums[i])
3. 如果 maxReach >= n-1，返回 true
4. 如果 i > maxReach，说明无法继续前进，返回 false

## 贪心策略

每一步都尽可能扩展能到达的最远距离，不需要考虑具体跳跃路径。

## 复杂度分析

- 时间复杂度: O(n) - 只需一次遍历
- 空间复杂度: O(1) - 只需常数空间

## 注意事项

- 贪心算法的经典应用
- 不需要记录跳跃路径，只需判断可达性',
NOW(), NOW()
FROM questions WHERE title = '[55] Jump Game';

-- 笔记 6: [61] Rotate List
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'先reverse整个list，然后找到分别reverse前K个，和后K个，链接起来',
'## 解题步骤

1. 计算链表长度 len
2. 处理 k % len (避免重复旋转)
3. 找到新的头节点位置 (len - k)
4. 将链表首尾连接成环
5. 在新头节点前断开

## 优化技巧

- 可以一次遍历完成：先成环，再断开
- 注意边界情况：k=0 或 k=len 时不需要旋转

## 复杂度分析

- 时间复杂度: O(n) - 两次遍历（计算长度 + 找断点）
- 空间复杂度: O(1) - 只需常数空间

## 注意事项

- 成环技巧简化实现',
NOW(), NOW()
FROM questions WHERE title = '[61] Rotate List';

-- 笔记 7: [64] Minimum Path Sum
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'dp[i][j] = min(dp[i-1][j], dp[i][j-1]) + grid[i][j]',
'## 状态定义

`dp[i][j]` 表示从 (0,0) 到 (i,j) 的最小路径和。

## 状态转移方程

```java
dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1])
```

## 初始化

- `dp[0][0] = grid[0][0]`
- 第一行：`dp[0][j] = dp[0][j-1] + grid[0][j]`
- 第一列：`dp[i][0] = dp[i-1][0] + grid[i][0]`

## 复杂度分析

- 时间复杂度: O(m × n) - 遍历整个网格
- 空间复杂度: O(m × n) - DP数组

## 空间优化

- 可以使用一维数组滚动优化，空间复杂度降至 O(n)
- 可以直接在原grid上修改，空间O(1)',
NOW(), NOW()
FROM questions WHERE title = '[64] Minimum Path Sum';

-- 笔记 8: [82] Remove Duplicates from Sorted List II
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'快慢指针，记得要记下prev，或者用两个list，一个dup，一个noDup',
'## 解题步骤

1. 使用哑节点 dummy 处理头节点被删除的情况
2. 使用 prev 指针指向上一个不重复的节点
3. 使用 curr 指针遍历链表
4. 如果 curr.val == curr.next.val，持续跳过相同值的节点
5. 否则，将 prev.next 指向 curr

## 关键技巧

- 哑节点避免特殊处理头节点
- 需要判断"是否有重复"，而不仅仅是"去重"

## 复杂度分析

- 时间复杂度: O(n) - 一次遍历
- 空间复杂度: O(1) - 只需常数空间

## 注意事项

- 注意与"去重保留一个"的区别',
NOW(), NOW()
FROM questions WHERE title = '[82] Remove Duplicates from Sorted List II';

-- 笔记 9: [92] Reverse Linked List II
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'用递归，先前进到m，触发反转逻辑',
'## 方法一：递归

1. 递归前进到第 m 个节点
2. 反转从 m 到 n 的节点
3. 返回时重新连接链表

## 方法二：迭代

1. 找到第 m-1 个节点 (prev)
2. 反转从 m 到 n 的部分
3. 重新连接三段：前段 → 反转段 → 后段

## 复杂度分析

- 时间复杂度: O(n) - 一次遍历
- 空间复杂度: O(1) - 迭代方法，O(n) - 递归方法（栈空间）

## 注意事项

- m=1 时需要特殊处理（反转从头开始）
- 使用哑节点简化边界情况',
NOW(), NOW()
FROM questions WHERE title = '[92] Reverse Linked List II';

-- 笔记 10: [96] Unique Binary Search Trees
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 2,
'构造[left, right]的树list，然后 lefts × rights',
'## 数学推导

这是**卡特兰数**问题：

```
C(n) = C(0)×C(n-1) + C(1)×C(n-2) + ... + C(n-1)×C(0)
```

## 动态规划解法

1. `dp[i]` 表示 i 个节点能构成的 BST 数量
2. 枚举根节点 j，左子树有 j-1 个节点，右子树有 i-j 个节点
3. `dp[i] = sum(dp[j-1] × dp[i-j])` for j in 1..i

## 初始化

- `dp[0] = 1` (空树)
- `dp[1] = 1` (单节点)

## 复杂度分析

- 时间复杂度: O(n²) - 外层循环n次，内层累加n次
- 空间复杂度: O(n) - DP数组

## 注意事项

- 卡特兰数的经典应用
- 可以用公式直接计算: C(n) = C(2n,n)/(n+1)',
NOW(), NOW()
FROM questions WHERE title = '[96] Unique Binary Search Trees';

-- =====================================================
-- STEP 4: 数据验证查询
-- =====================================================
-- 使用 validate_sample_import.sql 验证导入结果
-- =====================================================

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 导入完成 (Phase 4 版本)
-- =====================================================
-- 更新说明:
-- 1. ✅ 移除 display_order 字段 (Phase 4 设计中不存在)
-- 2. ✅ 新增 programming_question_details 表数据插入
-- 3. ✅ 分离 core_strategy 和 note_content 字段
-- 4. ✅ 使用 question_description 字段 (不是 description)
-- =====================================================
-- 下一步:
-- 1. 检查 focus_area_id 映射是否正确 (需根据实际数据库调整)
-- 2. 验证 user_id = 2 对应 austin.xyz@gmail.com
-- 3. 验证 stage_id = 3 对应"实战题目"阶段
-- 4. 执行 validate_sample_import.sql 检查数据完整性
-- =====================================================
