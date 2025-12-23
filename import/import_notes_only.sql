-- =====================================================
-- 仅导入笔记 (austin.xyz@gmail.com, user_id=3)
-- =====================================================

SET NAMES utf8mb4;

-- 笔记 1: [5] Longest Palindromic Substring
INSERT INTO user_question_notes (question_id, user_id, core_strategy, note_content, created_at, updated_at)
SELECT id, 3,
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
SELECT id, 3,
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
SELECT id, 3,
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
SELECT id, 3,
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
SELECT id, 3,
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
SELECT id, 3,
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
SELECT id, 3,
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
SELECT id, 3,
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
SELECT id, 3,
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
SELECT id, 3,
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
