package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.service.BlogService;
import com.fpt.fms.service.baseservice.IBlogService;
import com.fpt.fms.service.dto.BlogDTO;
import com.fpt.fms.service.search.SearchBlogDTO;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blogmanager")
public class BlogController {
    @Value("fms")
    private String applicationName;
    private final IBlogService blogService;


    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/blog")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BlogDTO>> getBlogs(
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<BlogDTO> blogDTOS = blogService.getBlogs(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), blogDTOS);
        return new ResponseEntity<>(blogDTOS.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/blog/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BlogDTO>> getBlogSearch(
        @RequestBody(required = false) SearchBlogDTO searchDTO,
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable) {
        Page<BlogDTO> blogDTOPage = blogService.searchBlog(searchDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), blogDTOPage);
        return new ResponseEntity<>(blogDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/blog")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> registerBlog(
        @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("content") String content,
        @RequestParam("blogCategory") String blogCategory, @RequestParam("status") Boolean status, @RequestParam(value = "file", required = false) MultipartFile file) {
        blogService.registerBlog(title, description, content, blogCategory, status, file);
        return ResponseEntity.status(HttpStatus.OK).body("Đăng blog thành công!");
    }

    @GetMapping("/blog/{blogId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BlogDTO> getBlog(@PathVariable(name = "blogId") Long blogId) {
        return ResponseUtil.wrapOrNotFound(blogService.getBlog(blogId));
    }

    @PatchMapping("/blog/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateBlog(@PathVariable("id") Long id, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("content") String content,
                                           @RequestParam("blogCategory") String blogCategory, @RequestParam("status") Boolean status,     @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        blogService.updateBlog(id, title, description, content, blogCategory, status, file);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }

    @DeleteMapping("/blog/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBlog(@PathVariable(name = "id") Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @DeleteMapping("/blog")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBlogs(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        blogService.deleteBlogs(ids);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

}
