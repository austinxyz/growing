-- 添加leetcode_number字段到programming_question_details表
ALTER TABLE programming_question_details
ADD COLUMN leetcode_number INT NULL AFTER question_id;

-- 从questions表的title中提取LeetCode编号并更新
UPDATE programming_question_details pqd
JOIN questions q ON pqd.question_id = q.id
SET pqd.leetcode_number = CAST(SUBSTRING_INDEX(q.title, '.', 1) AS UNSIGNED)
WHERE q.title REGEXP '^[0-9]+\\.';

-- 为leetcode_number添加索引以提高查询性能
CREATE INDEX idx_leetcode_number ON programming_question_details(leetcode_number);
