package com.fpt.fms.repository;

import com.fpt.fms.domain.BlogCategory;
import com.fpt.fms.domain.Rank;
import com.fpt.fms.domain.ToolCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ToolCategoryRepository extends JpaRepository<ToolCategory, Long>, JpaSpecificationExecutor<ToolCategory> {
    List<ToolCategory> findToolCategoryByIdIn(Set<Long> ids);

    ToolCategory findByName(String name);

    List<ToolCategory> findAllByCreatedBy(String email, Pageable pageable);
    List<ToolCategory> findByCreatedBy(String email);

    List<ToolCategory>findToolCategoryByIdInAndCreatedBy(Set<Long> ids,String email);

    ToolCategory findByIdAndCreatedBy(Long id, String email);

}
