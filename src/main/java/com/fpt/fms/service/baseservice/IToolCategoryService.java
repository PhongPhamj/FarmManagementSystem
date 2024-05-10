package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.BlogCategoryDTO;
import com.fpt.fms.service.dto.ToolCategoryDTO;
import com.fpt.fms.service.search.SearchBlogCategoryDTO;
import com.fpt.fms.service.search.SearchToolCategoryDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IToolCategoryService {
    Optional<ToolCategoryDTO> getToolCategory(Long toolCategoryDTO);

    Page<ToolCategoryDTO> getToolCategorys(Pageable pageable);

    Page<ToolCategoryDTO> searchToolCategory(SearchToolCategoryDTO searchDTO, Pageable pageable);
    void registerToolCategory(ToolCategoryDTO toolCategoryDTO);

    void updateToolCategory(ToolCategoryDTO toolCategoryDTO);

    void deleteToolCategory(Long farmId);

    void deleteToolCategorys(Set<Long> ids);
    List<ToolCategoryDTO> listToolCateWithStatus();
}
