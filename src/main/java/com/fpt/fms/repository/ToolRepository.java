package com.fpt.fms.repository;

import com.fpt.fms.domain.Tool;
import com.fpt.fms.domain.ToolCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ToolRepository extends JpaRepository<Tool,Long>, JpaSpecificationExecutor<Tool> {
    List<Tool> findToolByIdInAndCreatedBy(Set<Long> ids,String email);
    List<Tool> findAllByCreatedBy(String email);
    Optional<Tool> findToolByIdAndCreatedBy(Long id, String email);

    List<Tool> findAllByToolCategoryIn(List<ToolCategory> toolCate);

    List<Tool> findToolByToolCategoryInAndAndCreatedBy(Pageable pageable,List<ToolCategory> toolCate,String email);
    Tool findByName(String name);
}
