-- Phase 2: 技能体系管理 - 初始化核心技能数据
-- 创建时间: 2025-12-20

-- 插入9个核心技能
INSERT INTO skills (name, description, is_important, icon, display_order) VALUES
('编程和数据结构', '算法、数据结构、编程基础，面试必备核心技能', TRUE, 'code', 1),
('系统设计', '系统架构、设计模式、可扩展性设计', TRUE, 'layers', 2),
('Behavioral技能', '行为面试、领导力原则、软技能', TRUE, 'users', 3),
('云计算', '云服务、云架构、容器编排', TRUE, 'cloud', 4),
('AI/机器学习', '机器学习算法、深度学习、AI应用', TRUE, 'brain', 5),
('Infrastructure', '基础设施、网络、运维', FALSE, 'server', 6),
('DevOps', 'CI/CD、自动化运维、监控', FALSE, 'tool', 7),
('项目管理', '项目计划、执行、风险控制', FALSE, 'briefcase', 8),
('人员管理', '团队管理、人才发展、绩效管理', TRUE, 'user-check', 9);

-- 关联技能到职业路径
-- 软件工程师路径 (假设ID=1)
INSERT INTO career_path_skills (career_path_id, skill_id) VALUES
(1, 1),  -- 编程和数据结构
(1, 2),  -- 系统设计
(1, 3),  -- Behavioral
(1, 4),  -- 云计算
(1, 5);  -- AI/机器学习

-- 职业经理路径 (假设ID=2)
INSERT INTO career_path_skills (career_path_id, skill_id) VALUES
(2, 1),  -- 编程和数据结构（职业经理也需要技术背景）
(2, 2),  -- 系统设计
(2, 3),  -- Behavioral
(2, 8),  -- 项目管理
(2, 9);  -- 人员管理
