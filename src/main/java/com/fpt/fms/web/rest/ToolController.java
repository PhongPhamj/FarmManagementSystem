package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.domain.Tool;
import com.fpt.fms.service.baseservice.IToolService;
import com.fpt.fms.service.dto.BlogDTO;
import com.fpt.fms.service.dto.ToolCategoryDTO;
import com.fpt.fms.service.dto.ToolDTO;
import com.fpt.fms.service.search.SearchToolCategoryDTO;
import com.fpt.fms.service.search.SearchToolDTO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tool")
public class ToolController {
    @Value("fms")
    private String applicationName;
    private final IToolService toolService;


    public ToolController(IToolService toolService) {
        this.toolService = toolService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ToolDTO>> getTools(
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable) {
        Page<ToolDTO> toolDTOPage = toolService.getTools(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), toolDTOPage);
        return new ResponseEntity<>(toolDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ToolDTO>> getSearchTools(
        @RequestBody(required = false) SearchToolDTO searchDTO,
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable) {
        Page<ToolDTO> toolDTOPage = toolService.searchTool(searchDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), toolDTOPage);
        return new ResponseEntity<>(toolDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<ToolDTO> getBlog(@PathVariable(name = "id") Long id) {
        return ResponseUtil.wrapOrNotFound(toolService.getTool(id));
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<String> registerTool(@Valid @RequestBody ToolDTO toolDTO) {
        toolService.registerTool(toolDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Đăng kí công cụ thành công!");
    }

    @PatchMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> updateTool(@Valid @RequestBody ToolDTO toolDTO) {
        toolService.updateTool(toolDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deleteTool(@PathVariable(name = "id") Long id) {
        toolService.deleteTool(id);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deleteTools(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        toolService.deleteTools(ids);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }
}
