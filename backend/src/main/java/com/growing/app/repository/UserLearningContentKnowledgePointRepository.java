package com.growing.app.repository;

import com.growing.app.model.UserLearningContentKnowledgePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLearningContentKnowledgePointRepository extends JpaRepository<UserLearningContentKnowledgePoint, Long> {

    /**
     * 根据学习资料ID和用户ID查找所有知识点，按display_order排序
     */
    List<UserLearningContentKnowledgePoint> findByLearningContentIdAndUserIdOrderByDisplayOrderAsc(Long learningContentId, Long userId);

    /**
     * 删除指定学习资料和用户的所有知识点
     */
    void deleteByLearningContentIdAndUserId(Long learningContentId, Long userId);
}
