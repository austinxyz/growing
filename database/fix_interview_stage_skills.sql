-- 修复interview_stages表中skill_ids和focus_area_ids的数据一致性问题
-- 问题：某些阶段的focus_area_ids包含了不属于当前skill_ids的Focus Area
-- 解决方案：更新skill_ids，使其包含所有focus_area_ids对应的技能

-- 阶段7: 系统设计
-- 当前: skill_ids=[2]
-- 应该: skill_ids=[2,3,4,7,9] (系统设计, Behavioral, 云计算, DevOps, 人员管理)
UPDATE interview_stages
SET skill_ids = '[2, 3, 4, 7, 9]'
WHERE id = 7;

-- 阶段6: Behavioral (已一致，无需修改)
-- 当前: skill_ids=[3]
-- 应该: skill_ids=[3]

-- 阶段8: Phone Screen
-- 当前: skill_ids=[3, 9, 8]
-- 应该: skill_ids=[3,4,9] (移除8-项目管理，添加4-云计算)
UPDATE interview_stages
SET skill_ids = '[3, 4, 9]'
WHERE id = 8;

-- 阶段9: 算法与数据结构
-- 当前: skill_ids=[1]
-- 应该: skill_ids=[1,3,7,9] (算法与数据结构, Behavioral, DevOps, 人员管理)
UPDATE interview_stages
SET skill_ids = '[1, 3, 7, 9]'
WHERE id = 9;

-- 阶段10: Infra知识
-- 当前: skill_ids=[6, 7]
-- 应该: skill_ids=[3,4,7,9] (移除6-Linux系统，添加3-Behavioral, 4-云计算, 9-人员管理)
UPDATE interview_stages
SET skill_ids = '[3, 4, 7, 9]'
WHERE id = 10;

-- 阶段11: 管理
-- 当前: skill_ids=[8, 9]
-- 应该: skill_ids=[3,9] (移除8-项目管理，添加3-Behavioral)
UPDATE interview_stages
SET skill_ids = '[3, 9]'
WHERE id = 11;

-- 阶段12: Bar Raiser
-- 当前: skill_ids=[9, 4, 3, 8]
-- 应该: skill_ids=[3,4] (只保留3-Behavioral, 4-云计算，移除8-项目管理, 9-人员管理)
UPDATE interview_stages
SET skill_ids = '[3, 4]'
WHERE id = 12;

-- 验证修复结果
SELECT
  id,
  stage_name,
  skill_ids,
  CONCAT('应包含',
    CASE id
      WHEN 7 THEN ' [2,3,4,7,9]'
      WHEN 6 THEN ' [3]'
      WHEN 8 THEN ' [3,4,9]'
      WHEN 9 THEN ' [1,3,7,9]'
      WHEN 10 THEN ' [3,4,7,9]'
      WHEN 11 THEN ' [3,9]'
      WHEN 12 THEN ' [3,4]'
      ELSE ' (无需修改)'
    END
  ) as expected_skills
FROM interview_stages
WHERE id IN (6, 7, 8, 9, 10, 11, 12)
ORDER BY id;
