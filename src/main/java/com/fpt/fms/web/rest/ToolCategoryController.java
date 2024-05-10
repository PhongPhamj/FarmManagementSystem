package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.service.baseservice.IToolCategoryService;
import com.fpt.fms.service.dto.BlogCategoryDTO;
import com.fpt.fms.service.dto.ToolCategoryDTO;
import com.fpt.fms.service.search.SearchBlogCategoryDTO;
import com.fpt.fms.service.search.SearchToolCategoryDTO;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
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

@RestController
@RequestMapping("/api/toolcategory")
public class ToolCategoryController {

    @Value("fms")
    private String applicationName;

    private final IToolCategoryService toolCategoryService;

    public ToolCategoryController(IToolCategoryService toolCategoryService) {
        this.toolCategoryService = toolCategoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ToolCategoryDTO>> getToolCategorys(
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<ToolCategoryDTO> toolCategoryDTOS = toolCategoryService.getToolCategorys(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            toolCategoryDTOS
        );
        return new ResponseEntity<>(toolCategoryDTOS.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ToolCategoryDTO>> getToolCategorysWithStatus() {
        List<ToolCategoryDTO> toolCategoryDTOS = toolCategoryService.listToolCateWithStatus();
        return new ResponseEntity<>(toolCategoryDTOS, HttpStatus.OK);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ToolCategoryDTO>> getSearchToolCategorys(
        @RequestBody(required = false) SearchToolCategoryDTO searchDTO,
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<ToolCategoryDTO> toolCategoryDTOS = toolCategoryService.searchToolCategory(searchDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            toolCategoryDTOS
        );
        return new ResponseEntity<>(toolCategoryDTOS.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{toolCategoryId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<ToolCategoryDTO> getToolCategory(@PathVariable(name = "toolCategoryId") Long toolCategoryId) {
        return ResponseUtil.wrapOrNotFound(toolCategoryService.getToolCategory(toolCategoryId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<String> registerToolCategory(@Valid @RequestBody ToolCategoryDTO toolCategoryDTO) {
        toolCategoryService.registerToolCategory(toolCategoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Đăng kí loại công cụ thành công!");
    }

    @PatchMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> updateToolCategory(@Valid @RequestBody ToolCategoryDTO toolCategoryDTO) {
        toolCategoryService.updateToolCategory(toolCategoryDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deleteToolCategory(@PathVariable(name = "id") Long id) {
        toolCategoryService.deleteToolCategory(id);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deleteToolCategorys(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        toolCategoryService.deleteToolCategorys(ids);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }
}
