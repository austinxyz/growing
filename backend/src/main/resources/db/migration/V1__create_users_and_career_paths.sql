-- Phase 1: 用户和职业路径 - 数据库表创建
-- 创建时间: 2025-12-20

-- 1. 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    full_name VARCHAR(100) COMMENT '全名',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色: user, admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 职业路径表
CREATE TABLE career_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '职业路径名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '唯一标识代码',
    description TEXT COMMENT '描述',
    icon VARCHAR(50) COMMENT '图标',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_code (code),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职业路径表';

-- 3. 用户-职业路径关联表
CREATE TABLE user_career_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    career_path_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (career_path_id) REFERENCES career_paths(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_career (user_id, career_path_id),
    INDEX idx_user_id (user_id),
    INDEX idx_career_path_id (career_path_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-职业路径关联表';

-- 初始化职业路径数据
INSERT INTO career_paths (name, code, description, icon, is_active) VALUES
('软件工程师', 'software-engineer', '全栈开发、系统设计、算法优化', 'code', TRUE),
('职业经理', 'engineering-manager', '团队管理、项目交付、技术领导力', 'users', TRUE);

-- 初始化测试管理员账号
INSERT INTO users (username, email, password_hash, full_name, role) VALUES
('austinxu', 'austin@example.com', '$2a$10$wP3UnBbGMpZ7BRmqbucM9uYpnflgV5du/6Qb3Ez7/ewI4Sa1jfbdW', 'Austin Xu', 'admin');
-- 密码: helloworld (BCrypt hash)
