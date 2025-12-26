-- Create user_learning_content_notes table
CREATE TABLE user_learning_content_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    learning_content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    note_content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_note_learning_content FOREIGN KEY (learning_content_id) REFERENCES learning_contents(id) ON DELETE CASCADE,
    CONSTRAINT fk_note_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_learning_content_user UNIQUE (learning_content_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create index for faster queries
CREATE INDEX idx_user_learning_content_notes_user_id ON user_learning_content_notes(user_id);
CREATE INDEX idx_user_learning_content_notes_content_id ON user_learning_content_notes(learning_content_id);
