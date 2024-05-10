package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.service.BlogCategoryService;
import com.fpt.fms.service.baseservice.IBlogCategoryService;
import com.fpt.fms.service.dto.BlogCategoryDTO;
import com.fpt.fms.service.search.SearchBlogCategoryDTO;
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
@RequestMapping("/api/blogmanager")
public class BlogCategoryController {

    @Value("fms")
    private String applicationName;

    private final IBlogCategoryService blogCategoryService;

    public BlogCategoryController(BlogCategoryService blogCategoryService) {
        this.blogCategoryService = blogCategoryService;
    }

    @GetMapping("/blogcategory")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BlogCategoryDTO>> getBlogCategorys(
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<BlogCategoryDTO> blogCategoryDTOPage = blogCategoryService.getBlogCategorys(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            blogCategoryDTOPage
        );
        return new ResponseEntity<>(blogCategoryDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/blogcategory/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BlogCategoryDTO>> getBlogCategorysWithStatus() {
        List<BlogCategoryDTO> blogCategoryDTOPage = blogCategoryService.listBlogcateWithStatus();
        return new ResponseEntity<>(blogCategoryDTOPage, HttpStatus.OK);
    }

    @PostMapping("/blogcategory/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BlogCategoryDTO>> getSearchCategorys(
        @RequestBody(required = false) SearchBlogCategoryDTO searchDTO,
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<BlogCategoryDTO> blogCategoryDTOPage = blogCategoryService.searchBlogCategory(searchDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            blogCategoryDTOPage
        );
        return new ResponseEntity<>(blogCategoryDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/blogcategory/{blogCategoryId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BlogCategoryDTO> getBlogCategory(@PathVariable(name = "blogCategoryId") Long blogCategoryId) {
        return ResponseUtil.wrapOrNotFound(blogCategoryService.getBlogCategory(blogCategoryId));
    }

    @PostMapping("/blogcategory")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> registerBlogCategory(@Valid @RequestBody BlogCategoryDTO blogCategoryDTO) {
        blogCategoryService.registerBlogCategory(blogCategoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Đăng loai blog thành công!");
    }

    @PatchMapping("/blogcategory")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateBlogCategory(@Valid @RequestBody BlogCategoryDTO blogCategoryDTO) {
        blogCategoryService.updateBlogCategory(blogCategoryDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }

    @DeleteMapping("/blogcategory/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBlogCategory(@PathVariable(name = "id") Long id) {
        blogCategoryService.deleteBlogCategory(id);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @DeleteMapping("/blogcategory")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBlogCategorys(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        blogCategoryService.deleteBlogCategorys(ids);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }
}
