package com.growing.app.repository;

import com.growing.app.model.SolutionDiagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionDiagramRepository extends JpaRepository<SolutionDiagram, Long> {

    /**
     * 查询某个参考答案的所有配图
     * 按display_order排序
     */
    List<SolutionDiagram> findByCaseSolutionIdOrderByDisplayOrderAsc(Long solutionId);
}
