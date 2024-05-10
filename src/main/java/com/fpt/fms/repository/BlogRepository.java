package com.fpt.fms.repository;

import com.fpt.fms.domain.Blog;
import com.fpt.fms.domain.BlogCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {
    List<Blog> findBlogByIdIn(Set<Long> ids);
    List<Blog> findBlogByBlogCategoryIn(Pageable pageable,List<BlogCategory>blogCategories);

    Blog findBlogByTitle(String title);

}
