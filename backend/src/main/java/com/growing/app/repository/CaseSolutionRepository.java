package com.growing.app.repository;

import com.growing.app.model.CaseSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseSolutionRepository extends JpaRepository<CaseSolution, Long> {

    /**
     * 查询某个案例的所有参考答案（支持多方案）
     * 按display_order排序
     */
    List<CaseSolution> findBySystemDesignCaseIdOrderByDisplayOrderAsc(Long caseId);
}
