-- =====================================================
-- Labuladong Phase 4 数据导入脚本
-- 生成时间: 2025-12-22 21:52:21
-- 数据来源: labuladong_phase4_v2_staged.md
-- =====================================================

-- 1. 插入大分类 (major_categories表已在V7 migration中创建)
-- 注意: major_categories已在V7中初始化,这里只做确认

-- 2. 插入Focus Area (focus_areas表)
-- 假设skill_id=1为'编程与数据结构'
SET @skill_id = 1;
SET @admin_user_id = 1;  -- 假设管理员用户ID为1

SET @category_1_id = (SELECT id FROM major_categories WHERE name = '数据结构' LIMIT 1);
SET @category_2_id = (SELECT id FROM major_categories WHERE name = '搜索' LIMIT 1);
SET @category_3_id = (SELECT id FROM major_categories WHERE name = '动规' LIMIT 1);
SET @category_4_id = (SELECT id FROM major_categories WHERE name = '其他' LIMIT 1);

-- 获取学习阶段ID
SET @stage_theory_id = (SELECT id FROM learning_stages WHERE skill_id = @skill_id AND stage_type = 'theory' LIMIT 1);
SET @stage_implementation_id = (SELECT id FROM learning_stages WHERE skill_id = @skill_id AND stage_type = 'implementation' LIMIT 1);
SET @stage_practice_id = (SELECT id FROM learning_stages WHERE skill_id = @skill_id AND stage_type = 'practice' LIMIT 1);

-- 插入Focus Area
INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Array', 'Array - 来自labuladong算法笔记', NOW());
SET @fa_1_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_1_id, @category_1_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Linked List', 'Linked List - 来自labuladong算法笔记', NOW());
SET @fa_2_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_2_id, @category_1_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Stack and Queue', 'Stack and Queue - 来自labuladong算法笔记', NOW());
SET @fa_3_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_3_id, @category_1_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Hash Table', 'Hash Table - 来自labuladong算法笔记', NOW());
SET @fa_4_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_4_id, @category_1_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Binary Tree', 'Binary Tree - 来自labuladong算法笔记', NOW());
SET @fa_5_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_5_id, @category_1_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Graph', 'Graph - 来自labuladong算法笔记', NOW());
SET @fa_6_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_6_id, @category_1_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Sorting', 'Sorting - 来自labuladong算法笔记', NOW());
SET @fa_7_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_7_id, @category_1_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Data Structure Design', 'Data Structure Design - 来自labuladong算法笔记', NOW());
SET @fa_8_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_8_id, @category_1_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Backtracking', 'Backtracking - 来自labuladong算法笔记', NOW());
SET @fa_9_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_9_id, @category_2_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'BFS', 'BFS - 来自labuladong算法笔记', NOW());
SET @fa_10_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_10_id, @category_2_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'DP Fundamentals', 'DP Fundamentals - 来自labuladong算法笔记', NOW());
SET @fa_11_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_11_id, @category_3_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Subsequence Problems', 'Subsequence Problems - 来自labuladong算法笔记', NOW());
SET @fa_12_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_12_id, @category_3_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Knapsack Problems', 'Knapsack Problems - 来自labuladong算法笔记', NOW());
SET @fa_13_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_13_id, @category_3_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Game Theory', 'Game Theory - 来自labuladong算法笔记', NOW());
SET @fa_14_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_14_id, @category_3_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Classic DP', 'Classic DP - 来自labuladong算法笔记', NOW());
SET @fa_15_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_15_id, @category_3_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Greedy', 'Greedy - 来自labuladong算法笔记', NOW());
SET @fa_16_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_16_id, @category_3_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Math Tricks', 'Math Tricks - 来自labuladong算法笔记', NOW());
SET @fa_17_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_17_id, @category_4_id);

INSERT INTO focus_areas (skill_id, name, description, created_at)
VALUES (@skill_id, 'Classic Interview', 'Classic Interview - 来自labuladong算法笔记', NOW());
SET @fa_18_id = LAST_INSERT_ID();

INSERT INTO focus_area_categories (focus_area_id, category_id)
VALUES (@fa_18_id, @category_4_id);

-- 3. 插入学习内容 (learning_contents表)

-- Focus Area: Array
-- 基础原理 (4篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_theory_id, 'article', '数组（顺序存储）基本原理', 'https://labuladong.online/algo/data-structure-basic/array-basic/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_theory_id, 'article', '动态数组代码实现', 'https://labuladong.online/algo/data-structure-basic/array-implement/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_theory_id, 'article', '环形数组技巧及实现', 'https://labuladong.online/algo/data-structure-basic/cycle-array/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_theory_id, 'article', '位图原理及实现', 'https://labuladong.online/algo/data-structure-basic/bitmap/', 'labuladong', 4, 'public', @admin_user_id, NOW());
-- 实现代码 (9篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_implementation_id, 'article', '双指针技巧秒杀七道数组题目', 'https://labuladong.online/algo/essential-technique/array-two-pointers-summary/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_implementation_id, 'article', '滑动窗口算法核心代码模板', 'https://labuladong.online/algo/essential-technique/sliding-window-framework/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_implementation_id, 'article', '二分搜索算法核心代码模板', 'https://labuladong.online/algo/essential-technique/binary-search-framework/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_implementation_id, 'article', '实际运用二分搜索时的思维框架', 'https://labuladong.online/algo/frequency-interview/binary-search-in-action/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_implementation_id, 'article', '小而美的算法技巧：前缀和数组', 'https://labuladong.online/algo/data-structure/prefix-sum/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_implementation_id, 'article', '小而美的算法技巧：差分数组', 'https://labuladong.online/algo/data-structure/diff-array/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_implementation_id, 'article', '滑动窗口延伸：Rabin Karp 字符匹配算法', 'https://labuladong.online/algo/practice-in-action/rabinkarp/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_implementation_id, 'article', '带权重的随机选择算法', 'https://labuladong.online/algo/frequency-interview/random-pick-with-weight/', 'labuladong', 8, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_implementation_id, 'article', '田忌赛马背后的算法决策', 'https://labuladong.online/algo/practice-in-action/advantage-shuffle/', 'labuladong', 9, 'public', @admin_user_id, NOW());
-- 实战题目 (8篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_practice_id, 'code_example', '【游戏】消消乐游戏', 'https://labuladong.online/algo/game/match-three/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_practice_id, 'article', '二维数组的花式遍历技巧', 'https://labuladong.online/algo/practice-in-action/2d-array-traversal-summary/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_practice_id, 'article', '【练习】数组双指针经典习题', 'https://labuladong.online/algo/problem-set/array-two-pointers/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_practice_id, 'code_example', '【游戏】生命游戏', 'https://labuladong.online/algo/game/life-game/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_practice_id, 'article', '一个方法团灭 nSum 问题', 'https://labuladong.online/algo/practice-in-action/nsum/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_practice_id, 'article', '【练习】前缀和技巧经典习题', 'https://labuladong.online/algo/problem-set/perfix-sum/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_practice_id, 'article', '【练习】滑动窗口算法经典习题', 'https://labuladong.online/algo/problem-set/sliding-window/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_1_id, @stage_practice_id, 'article', '【练习】二分搜索算法经典习题', 'https://labuladong.online/algo/problem-set/binary-search/', 'labuladong', 8, 'public', @admin_user_id, NOW());

-- Focus Area: Linked List
-- 基础原理 (3篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_2_id, @stage_theory_id, 'article', '链表（链式存储）基本原理', 'https://labuladong.online/algo/data-structure-basic/linkedlist-basic/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_2_id, @stage_theory_id, 'article', '链表代码实现', 'https://labuladong.online/algo/data-structure-basic/linkedlist-implement/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_2_id, @stage_theory_id, 'article', '跳表核心原理', 'https://labuladong.online/algo/data-structure-basic/skip-list-basic/', 'labuladong', 3, 'public', @admin_user_id, NOW());
-- 实现代码 (3篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_2_id, @stage_implementation_id, 'article', '双指针技巧秒杀七道链表题目', 'https://labuladong.online/algo/essential-technique/linked-list-skills-summary/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_2_id, @stage_implementation_id, 'article', '单链表的花式反转方法汇总', 'https://labuladong.online/algo/data-structure/reverse-linked-list-recursion/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_2_id, @stage_implementation_id, 'article', '如何判断回文链表', 'https://labuladong.online/algo/data-structure/palindrome-linked-list/', 'labuladong', 3, 'public', @admin_user_id, NOW());
-- 实战题目 (2篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_2_id, @stage_practice_id, 'code_example', '【游戏】实现贪吃蛇', 'https://labuladong.online/algo/game/snake/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_2_id, @stage_practice_id, 'article', '【练习】链表双指针经典习题', 'https://labuladong.online/algo/problem-set/linkedlist-two-pointers/', 'labuladong', 2, 'public', @admin_user_id, NOW());

-- Focus Area: Stack and Queue
-- 基础原理 (4篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_theory_id, 'article', '队列/栈基本原理', 'https://labuladong.online/algo/data-structure-basic/queue-stack-basic/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_theory_id, 'article', '用链表实现队列/栈', 'https://labuladong.online/algo/data-structure-basic/linked-queue-stack/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_theory_id, 'article', '用数组实现队列/栈', 'https://labuladong.online/algo/data-structure-basic/array-queue-stack/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_theory_id, 'article', '双端队列（Deque）原理及实现', 'https://labuladong.online/algo/data-structure-basic/deque-implement/', 'labuladong', 4, 'public', @admin_user_id, NOW());
-- 实现代码 (3篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_implementation_id, 'article', '队列实现栈以及栈实现队列', 'https://labuladong.online/algo/data-structure/stack-queue/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_implementation_id, 'article', '单调栈算法模板解决三道例题', 'https://labuladong.online/algo/data-structure/monotonic-stack/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_implementation_id, 'article', '单调队列结构解决滑动窗口问题', 'https://labuladong.online/algo/data-structure/monotonic-queue/', 'labuladong', 3, 'public', @admin_user_id, NOW());
-- 实战题目 (5篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_practice_id, 'article', '【练习】栈的经典习题', 'https://labuladong.online/algo/problem-set/stack/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_practice_id, 'article', '【练习】括号类问题汇总', 'https://labuladong.online/algo/problem-set/parentheses/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_practice_id, 'article', '【练习】队列的经典习题', 'https://labuladong.online/algo/problem-set/queue/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_practice_id, 'article', '【练习】单调栈的几种变体及经典习题', 'https://labuladong.online/algo/problem-set/monotonic-stack/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_3_id, @stage_practice_id, 'article', '【练习】单调队列的通用实现及经典习题', 'https://labuladong.online/algo/problem-set/monotonic-queue/', 'labuladong', 5, 'public', @admin_user_id, NOW());

-- Focus Area: Hash Table
-- 基础原理 (5篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_4_id, @stage_theory_id, 'article', '哈希表核心原理', 'https://labuladong.online/algo/data-structure-basic/hashmap-basic/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_4_id, @stage_theory_id, 'article', '用拉链法实现哈希表', 'https://labuladong.online/algo/data-structure-basic/hashtable-chaining/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_4_id, @stage_theory_id, 'article', '线性探查法的两个难点', 'https://labuladong.online/algo/data-structure-basic/linear-probing-key-point/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_4_id, @stage_theory_id, 'article', '线性探查法的两种代码实现', 'https://labuladong.online/algo/data-structure-basic/linear-probing-code/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_4_id, @stage_theory_id, 'article', '哈希集合的原理及代码实现', 'https://labuladong.online/algo/data-structure-basic/hash-set/', 'labuladong', 5, 'public', @admin_user_id, NOW());
-- 实现代码 (3篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_4_id, @stage_implementation_id, 'article', '用链表加强哈希表（LinkedHashMap）', 'https://labuladong.online/algo/data-structure-basic/hashtable-with-linked-list/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_4_id, @stage_implementation_id, 'article', '用数组加强哈希表（ArrayHashMap）', 'https://labuladong.online/algo/data-structure-basic/hashtable-with-array/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_4_id, @stage_implementation_id, 'article', '布隆过滤器原理及实现', 'https://labuladong.online/algo/data-structure-basic/bloom-filter/', 'labuladong', 3, 'public', @admin_user_id, NOW());
-- 实战题目 (1篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_4_id, @stage_practice_id, 'article', '【练习】哈希表更多习题', 'https://labuladong.online/algo/problem-set/hash-table/', 'labuladong', 1, 'public', @admin_user_id, NOW());

-- Focus Area: Binary Tree
-- 基础原理 (11篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', '二叉树基础及常见类型', 'https://labuladong.online/algo/data-structure-basic/binary-tree-basic/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', '二叉树的递归/层序遍历', 'https://labuladong.online/algo/data-structure-basic/binary-tree-traverse-basic/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', 'DFS 和 BFS 的适用场景', 'https://labuladong.online/algo/data-structure-basic/use-case-of-dfs-bfs/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', '多叉树的递归/层序遍历', 'https://labuladong.online/algo/data-structure-basic/n-ary-tree-traverse-basic/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', '二叉搜索树的应用及可视化', 'https://labuladong.online/algo/data-structure-basic/tree-map-basic/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', '红黑树的完美平衡及可视化', 'https://labuladong.online/algo/data-structure-basic/rbtree-basic/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', 'Trie/字典树/前缀树原理及可视化', 'https://labuladong.online/algo/data-structure-basic/trie-map-basic/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', '二叉堆核心原理及可视化', 'https://labuladong.online/algo/data-structure-basic/binary-heap-basic/', 'labuladong', 8, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', '二叉堆/优先级队列代码实现', 'https://labuladong.online/algo/data-structure-basic/binary-heap-implement/', 'labuladong', 9, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', '线段树核心原理及可视化', 'https://labuladong.online/algo/data-structure-basic/segment-tree-basic/', 'labuladong', 10, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_theory_id, 'article', '数据压缩和霍夫曼树', 'https://labuladong.online/algo/data-structure-basic/huffman-tree/', 'labuladong', 11, 'public', @admin_user_id, NOW());
-- 实现代码 (15篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '二叉树系列算法核心纲领', 'https://labuladong.online/algo/essential-technique/binary-tree-summary/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '二叉树心法（思路篇）', 'https://labuladong.online/algo/data-structure/binary-tree-part1/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '二叉树心法（构造篇）', 'https://labuladong.online/algo/data-structure/binary-tree-part2/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '二叉树心法（后序篇）', 'https://labuladong.online/algo/data-structure/binary-tree-part3/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '二叉树心法（序列化篇）', 'https://labuladong.online/algo/data-structure/serialize-and-deserialize-binary-tree/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '二叉搜索树心法（特性篇）', 'https://labuladong.online/algo/data-structure/bst-part1/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '二叉搜索树心法（基操篇）', 'https://labuladong.online/algo/data-structure/bst-part2/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '二叉搜索树心法（构造篇）', 'https://labuladong.online/algo/data-structure/bst-part3/', 'labuladong', 8, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '二叉搜索树心法（后序篇）', 'https://labuladong.online/algo/data-structure/bst-part4/', 'labuladong', 9, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', 'TreeMap/TreeSet 代码实现', 'https://labuladong.online/algo/data-structure-basic/tree-map-implement/', 'labuladong', 10, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '基本线段树的代码实现', 'https://labuladong.online/algo/data-structure/segment-tree-implement/', 'labuladong', 11, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '优化：实现动态线段树', 'https://labuladong.online/algo/data-structure/segment-tree-dynamic/', 'labuladong', 12, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '优化：实现懒更新线段树', 'https://labuladong.online/algo/data-structure/segment-tree-lazy-update/', 'labuladong', 13, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', 'Trie 树代码实现', 'https://labuladong.online/algo/data-structure/trie-implement/', 'labuladong', 14, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_implementation_id, 'article', '实现霍夫曼编码压缩算法', 'https://labuladong.online/algo/data-structure/huffman-tree-implementation/', 'labuladong', 15, 'public', @admin_user_id, NOW());
-- 实战题目 (21篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '本章导读', 'https://labuladong.online/algo/intro/binary-tree-practice/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】用「遍历」思维解题 I', 'https://labuladong.online/algo/problem-set/binary-tree-traverse-i/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】用「遍历」思维解题 II', 'https://labuladong.online/algo/problem-set/binary-tree-traverse-ii/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】用「遍历」思维解题 III', 'https://labuladong.online/algo/problem-set/binary-tree-traverse-iii/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】用「分解问题」思维解题 I', 'https://labuladong.online/algo/problem-set/binary-tree-divide-i/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】用「分解问题」思维解题 II', 'https://labuladong.online/algo/problem-set/binary-tree-divide-ii/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】同时运用两种思维解题', 'https://labuladong.online/algo/problem-set/binary-tree-combine-two-view/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】利用后序位置解题 I', 'https://labuladong.online/algo/problem-set/binary-tree-post-order-i/', 'labuladong', 8, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】利用后序位置解题 II', 'https://labuladong.online/algo/problem-set/binary-tree-post-order-ii/', 'labuladong', 9, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】利用后序位置解题 III', 'https://labuladong.online/algo/problem-set/binary-tree-post-order-iii/', 'labuladong', 10, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】运用层序遍历解题 I', 'https://labuladong.online/algo/problem-set/binary-tree-level-i/', 'labuladong', 11, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】运用层序遍历解题 II', 'https://labuladong.online/algo/problem-set/binary-tree-level-ii/', 'labuladong', 12, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】二叉搜索树经典例题 I', 'https://labuladong.online/algo/problem-set/bst1/', 'labuladong', 13, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】二叉搜索树经典例题 II', 'https://labuladong.online/algo/problem-set/bst2/', 'labuladong', 14, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】线段树经典习题', 'https://labuladong.online/algo/problem-set/segment-tree/', 'labuladong', 15, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】Trie 树算法习题', 'https://labuladong.online/algo/problem-set/trie/', 'labuladong', 16, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '【练习】优先级队列经典习题', 'https://labuladong.online/algo/problem-set/binary-heap/', 'labuladong', 17, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '拓展：最近公共祖先系列解题框架', 'https://labuladong.online/algo/practice-in-action/lowest-common-ancestor-summary/', 'labuladong', 18, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '拓展：如何计算完全二叉树的节点数', 'https://labuladong.online/algo/data-structure/count-complete-tree-nodes/', 'labuladong', 19, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '拓展：惰性展开多叉树', 'https://labuladong.online/algo/data-structure/flatten-nested-list-iterator/', 'labuladong', 20, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_5_id, @stage_practice_id, 'article', '拓展：用栈模拟递归迭代遍历二叉树', 'https://labuladong.online/algo/data-structure/iterative-traversal-binary-tree/', 'labuladong', 21, 'public', @admin_user_id, NOW());

-- Focus Area: Graph
-- 基础原理 (7篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_theory_id, 'article', '图论中的基本术语', 'https://labuladong.online/algo/data-structure-basic/graph-terminology/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_theory_id, 'article', '图结构的通用代码实现', 'https://labuladong.online/algo/data-structure-basic/graph-basic/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_theory_id, 'article', '图结构的 DFS/BFS 遍历', 'https://labuladong.online/algo/data-structure-basic/graph-traverse-basic/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_theory_id, 'article', '欧拉图和一笔画游戏', 'https://labuladong.online/algo/data-structure-basic/eulerian-graph/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_theory_id, 'article', '图结构最短路径算法概览', 'https://labuladong.online/algo/data-structure-basic/graph-shortest-path/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_theory_id, 'article', '最小生成树算法概览', 'https://labuladong.online/algo/data-structure-basic/graph-minimum-spanning-tree/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_theory_id, 'article', 'Union Find 并查集原理', 'https://labuladong.online/algo/data-structure-basic/union-find-basic/', 'labuladong', 7, 'public', @admin_user_id, NOW());
-- 实现代码 (8篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_implementation_id, 'article', '二分图判定算法', 'https://labuladong.online/algo/data-structure/bipartite-graph/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_implementation_id, 'article', 'Hierholzer 算法寻找欧拉路径', 'https://labuladong.online/algo/data-structure/eulerian-graph-hierholzer/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_implementation_id, 'article', '环检测及拓扑排序算法', 'https://labuladong.online/algo/data-structure/topological-sort/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_implementation_id, 'article', 'Union-Find 并查集算法', 'https://labuladong.online/algo/data-structure/union-find/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_implementation_id, 'article', 'Dijkstra 算法核心原理及实现', 'https://labuladong.online/algo/data-structure/dijkstra/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_implementation_id, 'article', 'Dijkstra 拓展：带限制的最短路问题', 'https://labuladong.online/algo/data-structure/dijkstra-follow-up/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_implementation_id, 'article', 'Kruskal 最小生成树算法', 'https://labuladong.online/algo/data-structure/kruskal/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_implementation_id, 'article', 'Prim 最小生成树算法', 'https://labuladong.online/algo/data-structure/prim/', 'labuladong', 8, 'public', @admin_user_id, NOW());
-- 实战题目 (3篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_practice_id, 'article', '【练习】欧拉路径经典习题', 'https://labuladong.online/algo/problem-set/eulerian-path/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_practice_id, 'article', '【练习】并查集经典习题', 'https://labuladong.online/algo/problem-set/union-find/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_6_id, @stage_practice_id, 'article', '【练习】Dijkstra 算法经典习题', 'https://labuladong.online/algo/problem-set/dijkstra/', 'labuladong', 3, 'public', @admin_user_id, NOW());

-- Focus Area: Sorting
-- 基础原理 (12篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '本章导读', 'https://labuladong.online/algo/intro/sorting/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '排序算法的关键指标', 'https://labuladong.online/algo/data-structure-basic/sort-basic/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '选择排序所面临的问题', 'https://labuladong.online/algo/data-structure-basic/select-sort/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '拥有稳定性：冒泡排序', 'https://labuladong.online/algo/data-structure-basic/bubble-sort/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '运用逆向思维：插入排序', 'https://labuladong.online/algo/data-structure-basic/insertion-sort/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '突破 O(N^2)：希尔排序', 'https://labuladong.online/algo/data-structure-basic/shell-sort/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '妙用二叉树前序位置：快速排序', 'https://labuladong.online/algo/data-structure-basic/quick-sort/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '妙用二叉树后序位置：归并排序', 'https://labuladong.online/algo/data-structure-basic/merge-sort/', 'labuladong', 8, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '二叉堆结构的运用：堆排序', 'https://labuladong.online/algo/data-structure-basic/heap-sort/', 'labuladong', 9, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '全新的排序原理：计数排序', 'https://labuladong.online/algo/data-structure-basic/counting-sort/', 'labuladong', 10, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '博采众长：桶排序', 'https://labuladong.online/algo/data-structure-basic/bucket-sort/', 'labuladong', 11, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_theory_id, 'article', '基数排序（Radix Sort）', 'https://labuladong.online/algo/data-structure-basic/radix-sort/', 'labuladong', 12, 'public', @admin_user_id, NOW());
-- 实现代码 (2篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_implementation_id, 'article', '拓展：归并排序详解及应用', 'https://labuladong.online/algo/practice-in-action/merge-sort/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_7_id, @stage_implementation_id, 'article', '拓展：快速排序详解及应用', 'https://labuladong.online/algo/practice-in-action/quick-sort/', 'labuladong', 2, 'public', @admin_user_id, NOW());

-- Focus Area: Data Structure Design
-- 实现代码 (8篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_8_id, @stage_implementation_id, 'article', '算法就像搭乐高：手撸 LRU 算法', 'https://labuladong.online/algo/data-structure/lru-cache/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_8_id, @stage_implementation_id, 'article', '算法就像搭乐高：手撸 LFU 算法', 'https://labuladong.online/algo/frequency-interview/lfu/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_8_id, @stage_implementation_id, 'article', '常数时间删除/查找数组中的任意元素', 'https://labuladong.online/algo/data-structure/random-set/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_8_id, @stage_implementation_id, 'article', '设计朋友圈时间线功能', 'https://labuladong.online/algo/data-structure/design-twitter/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_8_id, @stage_implementation_id, 'article', '设计考场座位分配算法', 'https://labuladong.online/algo/frequency-interview/exam-room/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_8_id, @stage_implementation_id, 'article', '拓展：如何实现一个计算器', 'https://labuladong.online/algo/data-structure/implement-calculator/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_8_id, @stage_implementation_id, 'article', '拓展：两个二叉堆实现中位数算法', 'https://labuladong.online/algo/practice-in-action/find-median-from-data-stream/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_8_id, @stage_implementation_id, 'article', '拓展：数组去重问题（困难版）', 'https://labuladong.online/algo/frequency-interview/remove-duplicate-letters/', 'labuladong', 8, 'public', @admin_user_id, NOW());
-- 实战题目 (1篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_8_id, @stage_practice_id, 'article', '【练习】更多经典设计习题', 'https://labuladong.online/algo/problem-set/ds-design/', 'labuladong', 1, 'public', @admin_user_id, NOW());

-- Focus Area: Backtracking
-- 基础原理 (3篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_theory_id, 'article', '回溯算法解题套路框架', 'https://labuladong.online/algo/essential-technique/backtrack-framework/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_theory_id, 'article', '回溯算法秒杀所有排列/组合/子集问题', 'https://labuladong.online/algo/essential-technique/permutation-combination-subset-all-in-one/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_theory_id, 'article', '解答回溯算法/DFS算法的若干疑问', 'https://labuladong.online/algo/essential-technique/backtrack-vs-dfs/', 'labuladong', 3, 'public', @admin_user_id, NOW());
-- 实现代码 (5篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_implementation_id, 'article', '回溯算法实践：数独和 N 皇后问题', 'https://labuladong.online/algo/practice-in-action/sudoku-nqueue/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_implementation_id, 'article', '一文秒杀所有岛屿题目', 'https://labuladong.online/algo/frequency-interview/island-dfs-summary/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_implementation_id, 'article', '球盒模型：回溯算法穷举的两种视角', 'https://labuladong.online/algo/practice-in-action/two-views-of-backtrack/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_implementation_id, 'article', '回溯算法实践：括号生成', 'https://labuladong.online/algo/practice-in-action/generate-parentheses/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_implementation_id, 'article', '回溯算法实践：集合划分', 'https://labuladong.online/algo/practice-in-action/partition-to-k-equal-sum-subsets/', 'labuladong', 5, 'public', @admin_user_id, NOW());
-- 实战题目 (5篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_practice_id, 'code_example', '【游戏】实现数独作弊器', 'https://labuladong.online/algo/game/sudoku/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_practice_id, 'code_example', '【游戏】扫雷 II', 'https://labuladong.online/algo/game/minesweeper-ii/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_practice_id, 'article', '【练习】回溯算法经典习题 I', 'https://labuladong.online/algo/problem-set/backtrack-i/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_practice_id, 'article', '【练习】回溯算法经典习题 II', 'https://labuladong.online/algo/problem-set/backtrack-ii/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_9_id, @stage_practice_id, 'article', '【练习】回溯算法经典习题 III', 'https://labuladong.online/algo/problem-set/backtrack-iii/', 'labuladong', 5, 'public', @admin_user_id, NOW());

-- Focus Area: BFS
-- 基础原理 (1篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_10_id, @stage_theory_id, 'article', 'BFS 算法解题套路框架', 'https://labuladong.online/algo/essential-technique/bfs-framework/', 'labuladong', 1, 'public', @admin_user_id, NOW());
-- 实战题目 (5篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_10_id, @stage_practice_id, 'code_example', '【游戏】求解迷宫', 'https://labuladong.online/algo/game/maze/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_10_id, @stage_practice_id, 'code_example', '【游戏】华容道游戏', 'https://labuladong.online/algo/game/huarong-road/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_10_id, @stage_practice_id, 'code_example', '【游戏】连连看游戏', 'https://labuladong.online/algo/game/connect-two/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_10_id, @stage_practice_id, 'article', '【练习】BFS 经典习题 I', 'https://labuladong.online/algo/problem-set/bfs/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_10_id, @stage_practice_id, 'article', '【练习】BFS 经典习题 II', 'https://labuladong.online/algo/problem-set/bfs-ii/', 'labuladong', 5, 'public', @admin_user_id, NOW());

-- Focus Area: DP Fundamentals
-- 基础原理 (7篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_11_id, @stage_theory_id, 'article', '动态规划解题套路框架', 'https://labuladong.online/algo/essential-technique/dynamic-programming-framework/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_11_id, @stage_theory_id, 'article', '动态规划设计：最长递增子序列', 'https://labuladong.online/algo/dynamic-programming/longest-increasing-subsequence/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_11_id, @stage_theory_id, 'article', 'base case 和备忘录的初始值怎么定？', 'https://labuladong.online/algo/dynamic-programming/memo-fundamental/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_11_id, @stage_theory_id, 'article', '动态规划穷举的两种视角', 'https://labuladong.online/algo/dynamic-programming/two-views-of-dp/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_11_id, @stage_theory_id, 'article', '动态规划和回溯算法的思维转换', 'https://labuladong.online/algo/dynamic-programming/word-break/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_11_id, @stage_theory_id, 'article', '对动态规划进行空间压缩', 'https://labuladong.online/algo/dynamic-programming/space-optimization/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_11_id, @stage_theory_id, 'article', '最优子结构原理和 dp 数组遍历方向', 'https://labuladong.online/algo/dynamic-programming/faq-summary/', 'labuladong', 7, 'public', @admin_user_id, NOW());
-- 实战题目 (2篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_11_id, @stage_practice_id, 'article', '【练习】动态规划经典习题 I', 'https://labuladong.online/algo/problem-set/dynamic-programming-i/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_11_id, @stage_practice_id, 'article', '【练习】动态规划经典习题 II', 'https://labuladong.online/algo/problem-set/dynamic-programming-ii/', 'labuladong', 2, 'public', @admin_user_id, NOW());

-- Focus Area: Subsequence Problems
-- 基础原理 (4篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_12_id, @stage_theory_id, 'article', '经典动态规划：编辑距离', 'https://labuladong.online/algo/dynamic-programming/edit-distance/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_12_id, @stage_theory_id, 'article', '动态规划设计：最大子数组', 'https://labuladong.online/algo/dynamic-programming/maximum-subarray/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_12_id, @stage_theory_id, 'article', '经典动态规划：最长公共子序列', 'https://labuladong.online/algo/dynamic-programming/longest-common-subsequence/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_12_id, @stage_theory_id, 'article', '动态规划之子序列问题解题模板', 'https://labuladong.online/algo/dynamic-programming/subsequence-problem/', 'labuladong', 4, 'public', @admin_user_id, NOW());

-- Focus Area: Knapsack Problems
-- 基础原理 (4篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_13_id, @stage_theory_id, 'article', '经典动态规划：0-1 背包问题', 'https://labuladong.online/algo/dynamic-programming/knapsack1/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_13_id, @stage_theory_id, 'article', '经典动态规划：子集背包问题', 'https://labuladong.online/algo/dynamic-programming/knapsack2/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_13_id, @stage_theory_id, 'article', '经典动态规划：完全背包问题', 'https://labuladong.online/algo/dynamic-programming/knapsack3/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_13_id, @stage_theory_id, 'article', '背包问题的变体：目标和', 'https://labuladong.online/algo/dynamic-programming/target-sum/', 'labuladong', 4, 'public', @admin_user_id, NOW());
-- 实战题目 (1篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_13_id, @stage_practice_id, 'article', '【练习】背包问题经典习题', 'https://labuladong.online/algo/problem-set/knapsack/', 'labuladong', 1, 'public', @admin_user_id, NOW());

-- Focus Area: Game Theory
-- 基础原理 (7篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_14_id, @stage_theory_id, 'article', '动态规划之最小路径和', 'https://labuladong.online/algo/dynamic-programming/minimum-path-sum/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_14_id, @stage_theory_id, 'article', '旅游省钱大法：加权最短路径', 'https://labuladong.online/algo/dynamic-programming/cheap-travel/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_14_id, @stage_theory_id, 'article', '多源最短路径：Floyd 算法', 'https://labuladong.online/algo/data-structure/floyd/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_14_id, @stage_theory_id, 'article', '经典动态规划：正则表达式', 'https://labuladong.online/algo/dynamic-programming/regular-expression-matching/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_14_id, @stage_theory_id, 'article', '经典动态规划：高楼扔鸡蛋', 'https://labuladong.online/algo/dynamic-programming/egg-drop/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_14_id, @stage_theory_id, 'article', '经典动态规划：戳气球', 'https://labuladong.online/algo/dynamic-programming/burst-balloons/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_14_id, @stage_theory_id, 'article', '经典动态规划：博弈问题', 'https://labuladong.online/algo/dynamic-programming/game-theory/', 'labuladong', 7, 'public', @admin_user_id, NOW());
-- 实现代码 (2篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_14_id, @stage_implementation_id, 'article', '动态规划帮我通关了《魔塔》', 'https://labuladong.online/algo/dynamic-programming/magic-tower/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_14_id, @stage_implementation_id, 'article', '动态规划帮我通关了《辐射4》', 'https://labuladong.online/algo/dynamic-programming/freedom-trail/', 'labuladong', 2, 'public', @admin_user_id, NOW());

-- Focus Area: Classic DP
-- 基础原理 (2篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_15_id, @stage_theory_id, 'article', '一个方法团灭 LeetCode 打家劫舍问题', 'https://labuladong.online/algo/dynamic-programming/house-robber/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_15_id, @stage_theory_id, 'article', '一个方法团灭 LeetCode 股票买卖问题', 'https://labuladong.online/algo/dynamic-programming/stock-problem-summary/', 'labuladong', 2, 'public', @admin_user_id, NOW());
-- 实战题目 (1篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_15_id, @stage_practice_id, 'article', '【练习】打家劫舍问题模式', 'https://labuladong.online/algo/problem-set/rob-house/', 'labuladong', 1, 'public', @admin_user_id, NOW());

-- Focus Area: Greedy
-- 基础原理 (1篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_16_id, @stage_theory_id, 'article', '贪心算法解题套路框架', 'https://labuladong.online/algo/essential-technique/greedy/', 'labuladong', 1, 'public', @admin_user_id, NOW());
-- 实现代码 (4篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_16_id, @stage_implementation_id, 'article', '老司机加油算法', 'https://labuladong.online/algo/frequency-interview/gas-station-greedy/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_16_id, @stage_implementation_id, 'article', '贪心算法之区间调度问题', 'https://labuladong.online/algo/frequency-interview/interval-scheduling/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_16_id, @stage_implementation_id, 'article', '扫描线技巧：安排会议室', 'https://labuladong.online/algo/frequency-interview/scan-line-technique/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_16_id, @stage_implementation_id, 'article', '剪视频剪出一个贪心算法', 'https://labuladong.online/algo/frequency-interview/cut-video/', 'labuladong', 4, 'public', @admin_user_id, NOW());

-- Focus Area: Math Tricks
-- 基础原理 (8篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_theory_id, 'article', '一行代码就能解决的算法题', 'https://labuladong.online/algo/frequency-interview/one-line-solutions/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_theory_id, 'article', '常用的位操作', 'https://labuladong.online/algo/frequency-interview/bitwise-operation/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_theory_id, 'article', '必知必会数学技巧', 'https://labuladong.online/algo/essential-technique/math-techniques-summary/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_theory_id, 'article', '谈谈游戏中的随机算法', 'https://labuladong.online/algo/frequency-interview/random-algorithm/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_theory_id, 'article', '讲两道常考的阶乘算法题', 'https://labuladong.online/algo/frequency-interview/factorial-problems/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_theory_id, 'article', '如何高效寻找素数', 'https://labuladong.online/algo/frequency-interview/print-prime-number/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_theory_id, 'article', '如何同时寻找缺失和重复的元素', 'https://labuladong.online/algo/frequency-interview/mismatch-set/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_theory_id, 'article', '几个反直觉的概率问题', 'https://labuladong.online/algo/frequency-interview/probability-problem/', 'labuladong', 8, 'public', @admin_user_id, NOW());
-- 实现代码 (1篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_implementation_id, 'code_example', '【游戏】扫雷游戏地图生成器', 'https://labuladong.online/algo/game/minesweeper/', 'labuladong', 1, 'public', @admin_user_id, NOW());
-- 实战题目 (1篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_17_id, @stage_practice_id, 'article', '【练习】数学技巧相关习题', 'https://labuladong.online/algo/problem-set/math-tricks/', 'labuladong', 1, 'public', @admin_user_id, NOW());

-- Focus Area: Classic Interview
-- 基础原理 (3篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_theory_id, 'article', '如何高效解决接雨水问题', 'https://labuladong.online/algo/frequency-interview/trapping-rain-water/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_theory_id, 'article', '一文秒杀所有丑数系列问题', 'https://labuladong.online/algo/frequency-interview/ugly-number-summary/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_theory_id, 'article', '一个方法解决三道区间问题', 'https://labuladong.online/algo/practice-in-action/interval-problem-summary/', 'labuladong', 3, 'public', @admin_user_id, NOW());
-- 实现代码 (4篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_implementation_id, 'article', '谁能想到，斗地主也能玩出算法', 'https://labuladong.online/algo/practice-in-action/split-array-into-consecutive-subsequences/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_implementation_id, 'article', '烧饼排序算法', 'https://labuladong.online/algo/frequency-interview/pancake-sorting/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_implementation_id, 'article', '字符串乘法计算', 'https://labuladong.online/algo/practice-in-action/multiply-strings/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_implementation_id, 'article', '如何判定完美矩形', 'https://labuladong.online/algo/frequency-interview/perfect-rectangle/', 'labuladong', 4, 'public', @admin_user_id, NOW());
-- 实战题目 (16篇)
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '本章导读', 'https://labuladong.online/algo/intro/core-intro/', 'labuladong', 1, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '学习数据结构和算法的框架思维', 'https://labuladong.online/algo/essential-technique/algorithm-summary/', 'labuladong', 2, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '一个视角 + 两种思维模式搞定递归', 'https://labuladong.online/algo/essential-technique/understand-recursion/', 'labuladong', 3, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '本章导读', 'https://labuladong.online/algo/intro/data-structure-basic/', 'labuladong', 4, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '时间空间复杂度入门', 'https://labuladong.online/algo/intro/complexity-basic/', 'labuladong', 5, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '算法时空复杂度分析实用指南', 'https://labuladong.online/algo/essential-technique/complexity-analysis/', 'labuladong', 6, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '双指针技巧秒杀七道链表题目', 'https://labuladong.online/algo/essential-technique/linked-list-skills-summary/', 'labuladong', 7, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '双指针技巧秒杀七道数组题目', 'https://labuladong.online/algo/essential-technique/array-two-pointers-summary/', 'labuladong', 8, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '滑动窗口算法核心代码模板', 'https://labuladong.online/algo/essential-technique/sliding-window-framework/', 'labuladong', 9, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '二分搜索算法核心代码模板', 'https://labuladong.online/algo/essential-technique/binary-search-framework/', 'labuladong', 10, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '回溯算法解题套路框架', 'https://labuladong.online/algo/essential-technique/backtrack-framework/', 'labuladong', 11, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '回溯算法秒杀所有排列/组合/子集问题', 'https://labuladong.online/algo/essential-technique/permutation-combination-subset-all-in-one/', 'labuladong', 12, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', 'BFS 算法解题套路框架', 'https://labuladong.online/algo/essential-technique/bfs-framework/', 'labuladong', 13, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '动态规划解题套路框架', 'https://labuladong.online/algo/essential-technique/dynamic-programming-framework/', 'labuladong', 14, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '贪心算法解题套路框架', 'https://labuladong.online/algo/essential-technique/greedy/', 'labuladong', 15, 'public', @admin_user_id, NOW());
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, sort_order, visibility, created_by, created_at)
VALUES (@fa_18_id, @stage_practice_id, 'article', '分治算法解题套路框架', 'https://labuladong.online/algo/essential-technique/divide-and-conquer/', 'labuladong', 16, 'public', @admin_user_id, NOW());

-- =====================================================
-- 导入完成统计
-- =====================================================
-- 大分类数量: 4
-- Focus Area数量: 18
-- Array: 21篇
-- Linked List: 8篇
-- Stack and Queue: 12篇
-- Hash Table: 9篇
-- Binary Tree: 47篇
-- Graph: 18篇
-- Sorting: 14篇
-- Data Structure Design: 9篇
-- Backtracking: 13篇
-- BFS: 6篇
-- DP Fundamentals: 9篇
-- Subsequence Problems: 4篇
-- Knapsack Problems: 5篇
-- Game Theory: 9篇
-- Classic DP: 3篇
-- Greedy: 5篇
-- Math Tricks: 10篇
-- Classic Interview: 23篇
-- 学习内容总数: 225
-- =====================================================