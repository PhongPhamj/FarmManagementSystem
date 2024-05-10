package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.BlogDTO;
import com.fpt.fms.service.dto.ToolDTO;
import com.fpt.fms.service.search.SearchBlogDTO;
import com.fpt.fms.service.search.SearchToolDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IToolService {
    Page<ToolDTO> getTools(Pageable pageable);
    Page<ToolDTO> searchTool(SearchToolDTO searchDTO, Pageable pageable);
    void registerTool(ToolDTO toolDTO);
    Optional<ToolDTO> getTool(Long id);

    void updateTool(ToolDTO toolDTO);

    void deleteTool(Long id);

    void deleteTools(Set<Long> ids);
    List<ToolDTO> getListTool();
}
