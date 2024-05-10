package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.BlogCategoryDTO;
import com.fpt.fms.service.search.SearchBlogCategoryDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBlogCategoryService {
    Optional<BlogCategoryDTO> getBlogCategory(Long blogCategoryId);

    Page<BlogCategoryDTO> getBlogCategorys(Pageable pageable);

    Page<BlogCategoryDTO> searchBlogCategory(SearchBlogCategoryDTO searchDTO, Pageable pageable);
    void registerBlogCategory(BlogCategoryDTO blogCategoryDTO);

    List<BlogCategoryDTO> listBlogcateWithStatus();

    void updateBlogCategory(BlogCategoryDTO blogCategoryDTO);

    void deleteBlogCategory(Long farmId);

    void deleteBlogCategorys(Set<Long> ids);
}
