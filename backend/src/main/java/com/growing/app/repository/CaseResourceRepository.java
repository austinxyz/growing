package com.growing.app.repository;

import com.growing.app.model.CaseResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseResourceRepository extends JpaRepository<CaseResource, Long> {

    /**
     * 查询某个案例的所有学习资源
     * 按display_order排序
     */
    List<CaseResource> findBySystemDesignCaseIdOrderByDisplayOrderAsc(Long caseId);
}
