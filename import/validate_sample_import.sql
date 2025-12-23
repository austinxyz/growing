-- =====================================================
-- 数据验证脚本 - 导入后检查
-- =====================================================
-- 使用方法: 在导入 import_sample_10_questions.sql 后运行此脚本
-- =====================================================

-- 1️⃣ 检查导入的题目总数
SELECT '题目总数' AS check_item, COUNT(*) AS count
FROM questions
WHERE title LIKE '[%]%';

-- 2️⃣ 检查题目按难度分布
SELECT '难度分布' AS check_item, difficulty, COUNT(*) AS count
FROM questions
WHERE title LIKE '[%]%'
GROUP BY difficulty
ORDER BY FIELD(difficulty, 'EASY', 'MEDIUM', 'HARD');

-- 3️⃣ 检查题目按 Focus Area 分布 (需要关联 focus_areas 表)
SELECT
  '按Focus Area分布' AS check_item,
  f.name AS focus_area,
  COUNT(*) AS question_count
FROM questions q
JOIN focus_areas f ON q.focus_area_id = f.id
WHERE q.title LIKE '[%]%'
GROUP BY f.id, f.name
ORDER BY question_count DESC;

-- 4️⃣ 检查用户笔记数量
SELECT '用户笔记总数' AS check_item, COUNT(*) AS count
FROM user_question_notes
WHERE user_id = 2;

-- 5️⃣ 检查题目和笔记的对应关系 (应该是1对1)
SELECT
  '题目-笔记匹配检查' AS check_item,
  q.title,
  CASE
    WHEN n.id IS NOT NULL THEN '✅ 有笔记'
    ELSE '❌ 缺少笔记'
  END AS note_status
FROM questions q
LEFT JOIN user_question_notes n ON q.id = n.question_id AND n.user_id = 2
WHERE q.title LIKE '[%]%'
ORDER BY q.display_order;

-- 6️⃣ 检查笔记内容预览 (前50个字符)
SELECT
  '笔记内容预览' AS check_item,
  q.title,
  LEFT(n.note_content, 50) AS note_preview
FROM questions q
JOIN user_question_notes n ON q.id = n.question_id
WHERE n.user_id = 2 AND q.title LIKE '[%]%'
ORDER BY q.display_order;

-- 7️⃣ 检查 stage_id 是否正确 (应该都是3 - 实战题目阶段)
SELECT
  '学习阶段检查' AS check_item,
  s.stage_name,
  COUNT(*) AS question_count
FROM questions q
JOIN learning_stages s ON q.stage_id = s.id
WHERE q.title LIKE '[%]%'
GROUP BY s.id, s.stage_name;

-- 8️⃣ 检查是否有孤立的题目 (没有关联 focus_area 或 stage)
SELECT '孤立题目检查' AS check_item, title, focus_area_id, stage_id
FROM questions
WHERE title LIKE '[%]%'
  AND (focus_area_id IS NULL OR stage_id IS NULL);

-- 9️⃣ 检查 learning_contents 中是否创建了对应的案例分析
SELECT
  '学习内容录入检查' AS check_item,
  COUNT(*) AS case_study_count
FROM learning_contents
WHERE content_type = 'case_study'
  AND title LIKE '[%]%';

-- 🔟 完整数据展示 (前3条)
SELECT
  '完整数据示例' AS check_item,
  q.id,
  q.title,
  q.difficulty,
  f.name AS focus_area,
  s.stage_name,
  LEFT(q.description, 100) AS description_preview,
  LEFT(n.note_content, 100) AS note_preview
FROM questions q
LEFT JOIN focus_areas f ON q.focus_area_id = f.id
LEFT JOIN learning_stages s ON q.stage_id = s.id
LEFT JOIN user_question_notes n ON q.id = n.question_id AND n.user_id = 2
WHERE q.title LIKE '[%]%'
ORDER BY q.display_order
LIMIT 3;

-- =====================================================
-- 预期结果:
-- =====================================================
-- 1️⃣ 题目总数: 10
-- 2️⃣ 难度分布: MEDIUM (9), HARD (1)
-- 3️⃣ Focus Area分布: 数组、链表、回溯算法、动态规划、二叉树、贪心算法
-- 4️⃣ 用户笔记总数: 10
-- 5️⃣ 所有题目都有对应笔记 ✅
-- 6️⃣ 笔记内容包含"核心思路"
-- 7️⃣ 所有题目的 stage_id = 3 (实战题目)
-- 8️⃣ 没有孤立题目
-- 9️⃣ learning_contents 有1条案例分析 (仅示例)
-- 🔟 完整数据展示正常
-- =====================================================
