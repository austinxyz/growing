-- Create user_learning_content_knowledge_points table
CREATE TABLE user_learning_content_knowledge_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    learning_content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_kp_learning_content FOREIGN KEY (learning_content_id) REFERENCES learning_contents(id) ON DELETE CASCADE,
    CONSTRAINT fk_kp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for faster queries
CREATE INDEX idx_user_learning_content_kp_user_id ON user_learning_content_knowledge_points(user_id);
CREATE INDEX idx_user_learning_content_kp_content_id ON user_learning_content_knowledge_points(learning_content_id);
CREATE INDEX idx_user_learning_content_kp_order ON user_learning_content_knowledge_points(learning_content_id, user_id, display_order);
