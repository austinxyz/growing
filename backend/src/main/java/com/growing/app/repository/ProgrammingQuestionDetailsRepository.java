package com.growing.app.repository;

import com.growing.app.model.ProgrammingQuestionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 编程题详情Repository
 */
@Repository
public interface ProgrammingQuestionDetailsRepository extends JpaRepository<ProgrammingQuestionDetails, Long> {

    /**
     * 通过question_id查询
     */
    Optional<ProgrammingQuestionDetails> findByQuestionId(Long questionId);

    /**
     * 查询所有重要题目
     */
    List<ProgrammingQuestionDetails> findByIsImportantTrue();

    /**
     * 删除question关联的details
     */
    void deleteByQuestionId(Long questionId);
}
