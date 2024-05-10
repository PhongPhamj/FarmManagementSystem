package com.fpt.fms.repository;

import com.fpt.fms.domain.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long>, JpaSpecificationExecutor<BlogCategory> {
    List<BlogCategory> findBlogCategoryByIdIn(Set<Long> ids);

    BlogCategory findBlogCategoryByName(String name);


}
