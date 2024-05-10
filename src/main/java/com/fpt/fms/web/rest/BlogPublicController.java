package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.service.BlogService;
import com.fpt.fms.service.baseservice.IBlogCategoryService;
import com.fpt.fms.service.baseservice.IBlogService;
import com.fpt.fms.service.dto.BlogCategoryDTO;
import com.fpt.fms.service.dto.BlogDTO;
import com.fpt.fms.service.search.SearchBlogDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/blog/public")
public class BlogPublicController {

    @Value("fms")
    private String applicationName;

    private final IBlogService blogService;
    private final IBlogCategoryService blogCategoryService;

    public BlogPublicController(BlogService blogService, IBlogCategoryService blogCategoryService) {
        this.blogService = blogService;
        this.blogCategoryService = blogCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<BlogDTO>> getBlogPublics(
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<BlogDTO> blogDTOS = blogService.getBlogPublics(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), blogDTOS);
        return new ResponseEntity<>(blogDTOS.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/blogcategory/status")
    public ResponseEntity<List<BlogCategoryDTO>> getBlogCategorysWithStatus() {
        List<BlogCategoryDTO> blogCategoryDTOPage = blogCategoryService.listBlogcateWithStatus();
        return new ResponseEntity<>(blogCategoryDTOPage, HttpStatus.OK);
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<BlogDTO> getBlogPublic(@PathVariable(name = "blogId") Long blogId) {
        return ResponseUtil.wrapOrNotFound(blogService.getBlogPublic(blogId));
    }

    @PostMapping("/search")
    public ResponseEntity<List<BlogDTO>> getBlogSearch(
        @RequestBody(required = false) SearchBlogDTO searchDTO,
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<BlogDTO> blogDTOPage = blogService.searchBlogPublic(searchDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), blogDTOPage);
        return new ResponseEntity<>(blogDTOPage.getContent(), headers, HttpStatus.OK);
    }
}
