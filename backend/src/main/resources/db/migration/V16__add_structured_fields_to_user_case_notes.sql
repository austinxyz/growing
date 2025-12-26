-- Phase 5.2: 为user_case_notes表添加结构化字段
-- 拆分用户答案为更细粒度的字段，便于分项编辑和展示

ALTER TABLE user_case_notes
    ADD COLUMN kp_requirement TEXT COMMENT '需求分析(Requirement)' AFTER user_id,
    ADD COLUMN kp_nfr TEXT COMMENT '非功能性需求(Non-Functional Requirements)' AFTER kp_requirement,
    ADD COLUMN kp_entity TEXT COMMENT '核心实体定义(Entity)' AFTER kp_nfr,
    ADD COLUMN kp_components TEXT COMMENT '关键组件(Key Components)' AFTER kp_entity,
    ADD COLUMN kp_api TEXT COMMENT 'API设计(API)' AFTER kp_components,
    ADD COLUMN kp_object1 TEXT COMMENT '核心对象1设计(Object1)' AFTER kp_api,
    ADD COLUMN kp_object2 TEXT COMMENT '核心对象2设计(Object2)' AFTER kp_object1;

-- 注释：
-- 1. 这些kp_*字段与原有的step1-6字段共存，用户可以选择使用哪种方式记录答案
-- 2. kp_requirement: 功能需求分析
-- 3. kp_nfr: 非功能性需求（如可扩展性、性能、可用性等）
-- 4. kp_entity: 核心实体定义（如User, Tweet, Feed等）
-- 5. kp_components: 关键组件（如LoadBalancer, Cache, MessageQueue等）
-- 6. kp_api: API接口设计
-- 7. kp_object1/object2: 核心对象的详细设计（如缓存策略、数据库schema等）
