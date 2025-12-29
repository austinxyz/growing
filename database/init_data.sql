-- =====================================================
-- Growing App - Initial Data
-- 数据库初始数据（配合schema.sql使用）
-- 不使用Flyway，通过mysql-exec执行SQL迁移
-- Last Updated: 2025-12-28 (Phase 6 完成)
-- =====================================================

-- =====================================================
-- Phase 1: 职业路径初始数据
-- =====================================================

-- 1. 职业路径 (Career Paths)
INSERT INTO career_paths (id, name, code, description, icon, is_active) VALUES
(1, '软件工程师', 'software-engineer', '全栈开发、系统设计、算法优化', '💻', 1),
(2, '职业经理', 'engineering-manager', '团队管理、项目交付、技术领导力', '👔', 1),
(3, '基础架构工程师', 'infrastructure-engineer', '负责设计、构建和维护大规模分布式系统基础架构，包括云平台、容器编排、网络架构等', '🏛️', 1);

-- =====================================================
-- Phase 2: 技能体系初始数据
-- =====================================================

-- 2. 核心技能 (Skills)
-- Phase 6新增字段: is_general_only (Behavioral等通用技能无大分类，直接管理Focus Area)
INSERT INTO skills (id, name, description, is_important, display_order, is_general_only) VALUES
(1, '算法与数据结构', '算法、数据结构、编程基础，面试必备核心技能', 1, 1, 0),
(2, '系统设计', '系统架构、设计模式、可扩展性设计', 1, 2, 0),
(3, 'Behavioral', '行为面试、领导力原则、软技能', 1, 3, 1),
(4, '云计算', '云服务、云架构、容器编排', 1, 4, 0),
(5, 'AI/机器学习', '机器学习算法、深度学习、AI应用', 1, 5, 0),
(6, 'Infrastructure', '基础设施、网络、运维', 0, 6, 0),
(7, 'DevOps', 'CI/CD、自动化运维、监控', 0, 7, 0),
(8, '项目管理', '项目计划、执行、风险控制', 0, 8, 1),
(9, '人员管理', '团队管理、人才发展、绩效管理', 1, 9, 1);

-- =====================================================
-- Phase 4: 算法与数据结构模块初始数据
-- =====================================================

-- 3. 大分类 (Major Categories)
-- 算法与数据结构的大分类 (skill_id = 1)
INSERT INTO major_categories (id, name, description, sort_order, skill_id) VALUES
(1, '数据结构', '数组、链表、树、图等数据结构', 1, 1),
(2, '搜索', '二分搜索、DFS、BFS等搜索算法', 2, 1),
(3, '动规', '动态规划相关算法', 3, 1),
(4, '其他', '其他算法和技巧', 4, 1);

-- =====================================================
-- Phase 6: 通用技能学习模块初始数据
-- =====================================================

-- 4. 答题模版 (Answer Templates)
INSERT INTO answer_templates (id, template_name, description, template_fields) VALUES
(1, 'STAR', 'STAR方法论答题模版，适用于Behavioral题目',
 JSON_ARRAY(
   JSON_OBJECT('key', 'situation', 'label', 'Situation (情境)', 'placeholder', '描述当时的情境和背景'),
   JSON_OBJECT('key', 'task', 'label', 'Task (任务)', 'placeholder', '说明你需要完成的任务或目标'),
   JSON_OBJECT('key', 'action', 'label', 'Action (行动)', 'placeholder', '详细描述你采取的具体行动'),
   JSON_OBJECT('key', 'result', 'label', 'Result (结果)', 'placeholder', '说明最终的结果和影响')
 )),
(2, 'Technical', '技术题答题模版，适用于技术类问题',
 JSON_ARRAY(
   JSON_OBJECT('key', 'core_concept', 'label', '核心概念', 'placeholder', '解释关键技术概念'),
   JSON_OBJECT('key', 'implementation', 'label', '实现方式', 'placeholder', '描述具体实现方法'),
   JSON_OBJECT('key', 'tradeoffs', 'label', '权衡考虑', 'placeholder', '分析优缺点和适用场景')
 ));

-- 5. 技能-模版关联 (Skill Templates)
-- Behavioral技能默认使用STAR模版
INSERT INTO skill_templates (skill_id, template_id, is_default) VALUES
(3, 1, 1);  -- Behavioral → STAR (默认)

-- =====================================================
-- 注意事项
-- =====================================================

-- 1. 本文件包含数据库的初始数据，用于配合schema.sql初始化新数据库
-- 2. 包含3个职业路径、9个核心技能、4个大分类（算法）、2个答题模版
-- 3. 如需完整的Focus Area、Learning Stages、Learning Contents等数据，
--    需要额外导入或通过管理界面添加
-- 4. 不使用Flyway，数据库迁移通过mysql-exec skill执行SQL文件
-- 5. ID值已固定，确保与应用代码中的硬编码ID保持一致（如：ALGORITHM_SKILL_ID = 1）
-- 6. Phase 6注意:
--    - is_general_only=1的技能: Behavioral(3), 项目管理(8), 人员管理(9)
--    - STAR模版(id=1)已关联到Behavioral技能(id=3)作为默认模版
