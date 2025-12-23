-- =====================================================
-- 算法题导入脚本 (10道题目 - 修正focus_area_id)
-- =====================================================
-- 修改记录:
-- 1. 修正 focus_area_id 映射为当前数据库正确的ID
-- 2. Array: 35, Linked List: 36, Binary Tree: 39
-- 3. Backtracking: 43, Greedy: 50, DP Fundamentals: 45
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 清空测试数据（可选，如果需要重新导入）
DELETE FROM user_question_notes WHERE question_id IN (SELECT id FROM questions WHERE title LIKE '[%]%');
DELETE FROM programming_question_details WHERE question_id IN (SELECT id FROM questions WHERE title LIKE '[%]%');
DELETE FROM questions WHERE title LIKE '[%]%';

-- =====================================================
-- STEP 1: 插入 questions 表数据
-- =====================================================

-- 题目 1: [5] Longest Palindromic Substring
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  35,  -- Array (修正: 1 → 35)
  '[5] Longest Palindromic Substring',
  '**题目描述**: 找到字符串中最长的回文子串。

**考察点**: 双指针扩展、中心扩散法

**示例**:
```
输入: s = "babad"
输出: "bab" 或 "aba"
```',
  'MEDIUM',
  1,  -- 公共试题
  1,  -- 管理员创建
  NOW(),
  NOW()
);

-- 题目 2: [15] 3Sum
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  35,  -- Array (修正: 1 → 35)
  '[15] 3Sum',
  '**题目描述**: 找到数组中所有和为0的三元组,不能重复。

**考察点**: 排序 + 双指针、去重逻辑

**示例**:
```
输入: nums = [-1,0,1,2,-1,-4]
输出: [[-1,-1,2],[-1,0,1]]
```',
  'MEDIUM',
  1,
  1,
  NOW(),
  NOW()
);

-- 题目 3: [30] Substring with Concatenation of All Words
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  35,  -- Array (修正: 1 → 35, 滑动窗口也属于数组题)
  '[30] Substring with Concatenation of All Words',
  '**题目描述**: 找到字符串中所有由给定单词数组拼接而成的子串起始位置。

**考察点**: 滑动窗口、哈希表计数

**示例**:
```
输入: s = "barfoothefoobarman", words = ["foo","bar"]
输出: [0,9]
```',
  'HARD',
  1,
  1,
  NOW(),
  NOW()
);

-- 题目 4: [47] Permutations II
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  43,  -- Backtracking (修正: 9 → 43)
  '[47] Permutations II',
  '**题目描述**: 生成包含重复数字的数组的所有不重复排列。

**考察点**: 回溯算法、剪枝去重

**示例**:
```
输入: nums = [1,1,2]
输出: [[1,1,2],[1,2,1],[2,1,1]]
```',
  'MEDIUM',
  1,
  1,
  NOW(),
  NOW()
);

-- 题目 5: [55] Jump Game
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  50,  -- Greedy (修正: 16 → 50)
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
  1,
  1,
  NOW(),
  NOW()
);

-- 题目 6: [61] Rotate List
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  36,  -- Linked List (修正: 2 → 36)
  '[61] Rotate List',
  '**题目描述**: 将链表向右旋转k个位置。

**考察点**: 链表反转、双指针

**示例**:
```
输入: head = [1,2,3,4,5], k = 2
输出: [4,5,1,2,3]
```',
  'MEDIUM',
  1,
  1,
  NOW(),
  NOW()
);

-- 题目 7: [64] Minimum Path Sum
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  45,  -- DP Fundamentals (修正: 11 → 45)
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
  1,
  1,
  NOW(),
  NOW()
);

-- 题目 8: [82] Remove Duplicates from Sorted List II
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  36,  -- Linked List (修正: 2 → 36)
  '[82] Remove Duplicates from Sorted List II',
  '**题目描述**: 删除排序链表中所有重复节点 (只保留出现一次的节点)。

**考察点**: 快慢指针、哑节点

**示例**:
```
输入: head = [1,2,3,3,4,4,5]
输出: [1,2,5]
```',
  'MEDIUM',
  1,
  1,
  NOW(),
  NOW()
);

-- 题目 9: [92] Reverse Linked List II
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  36,  -- Linked List (修正: 2 → 36)
  '[92] Reverse Linked List II',
  '**题目描述**: 反转链表的第m到第n个节点 (一次遍历)。

**考察点**: 链表反转、递归

**示例**:
```
输入: head = [1,2,3,4,5], left = 2, right = 4
输出: [1,4,3,2,5]
```',
  'MEDIUM',
  1,
  1,
  NOW(),
  NOW()
);

-- 题目 10: [96] Unique Binary Search Trees
INSERT INTO questions (
  focus_area_id,
  title,
  question_text,
  difficulty,
  is_official,
  created_by_user_id,
  created_at,
  updated_at
) VALUES (
  39,  -- Binary Tree (修正: 5 → 39)
  '[96] Unique Binary Search Trees',
  '**题目描述**: 计算给定n个节点能构成多少种不同的BST。

**考察点**: 卡特兰数、动态规划

**示例**:
```
输入: n = 3
输出: 5
```',
  'MEDIUM',
  1,
  1,
  NOW(),
  NOW()
);

-- =====================================================
-- STEP 2: 插入 programming_question_details 表数据
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

-- 编程题详情 3-10
INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity)
SELECT id, 'https://leetcode.com/problems/substring-with-concatenation-of-all-words/', FALSE, JSON_ARRAY('滑动窗口', '哈希表'), '时间O(n × wordLen), 空间O(m)'
FROM questions WHERE title = '[30] Substring with Concatenation of All Words';

INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity)
SELECT id, 'https://leetcode.com/problems/permutations-ii/', TRUE, JSON_ARRAY('回溯', '剪枝', '去重'), '时间O(n × n!), 空间O(n)'
FROM questions WHERE title = '[47] Permutations II';

INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity, similar_questions)
SELECT id, 'https://leetcode.com/problems/jump-game/', TRUE, JSON_ARRAY('贪心算法', '动态规划'), '时间O(n), 空间O(1)',
JSON_ARRAY(JSON_OBJECT('id', 45, 'title', 'Jump Game II'))
FROM questions WHERE title = '[55] Jump Game';

INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity)
SELECT id, 'https://leetcode.com/problems/rotate-list/', FALSE, JSON_ARRAY('链表', '双指针', '反转'), '时间O(n), 空间O(1)'
FROM questions WHERE title = '[61] Rotate List';

INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity, similar_questions)
SELECT id, 'https://leetcode.com/problems/minimum-path-sum/', TRUE, JSON_ARRAY('动态规划', '二维DP'), '时间O(m × n), 空间O(1)',
JSON_ARRAY(
  JSON_OBJECT('id', 62, 'title', 'Unique Paths'),
  JSON_OBJECT('id', 174, 'title', 'Dungeon Game')
)
FROM questions WHERE title = '[64] Minimum Path Sum';

INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity)
SELECT id, 'https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/', FALSE, JSON_ARRAY('链表', '双指针', '哑节点'), '时间O(n), 空间O(1)'
FROM questions WHERE title = '[82] Remove Duplicates from Sorted List II';

INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity, similar_questions)
SELECT id, 'https://leetcode.com/problems/reverse-linked-list-ii/', TRUE, JSON_ARRAY('链表', '递归', '反转'), '时间O(n), 空间O(1)',
JSON_ARRAY(JSON_OBJECT('id', 206, 'title', 'Reverse Linked List'))
FROM questions WHERE title = '[92] Reverse Linked List II';

INSERT INTO programming_question_details (question_id, leetcode_url, is_important, tags, complexity, similar_questions)
SELECT id, 'https://leetcode.com/problems/unique-binary-search-trees/', TRUE, JSON_ARRAY('动态规划', '卡特兰数', 'BST'), '时间O(n²), 空间O(n)',
JSON_ARRAY(JSON_OBJECT('id', 95, 'title', 'Unique Binary Search Trees II'))
FROM questions WHERE title = '[96] Unique Binary Search Trees';

SET FOREIGN_KEY_CHECKS = 1;

SELECT '✅ 导入完成! 已成功插入10道算法题及其编程详情' as status;
SELECT COUNT(*) as '新增试题数量' FROM questions WHERE title LIKE '[%]%';
SELECT COUNT(*) as '新增编程详情数量' FROM programming_question_details WHERE question_id IN (SELECT id FROM questions WHERE title LIKE '[%]%');
