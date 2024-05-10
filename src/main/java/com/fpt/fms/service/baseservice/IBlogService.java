package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.BlogDTO;
import com.fpt.fms.service.search.SearchBlogDTO;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IBlogService {
    Page<BlogDTO> getBlogs(Pageable pageable);
    Page<BlogDTO> searchBlog(SearchBlogDTO searchDTO, Pageable pageable);
    Page<BlogDTO> searchBlogPublic(SearchBlogDTO searchDTO, Pageable pageable);
    void registerBlog(String title, String description, String content, String blogCategory, Boolean status, MultipartFile imageFile);
    Optional<BlogDTO> getBlog(Long blogId);

    void updateBlog(
        Long id,
        String title,
        String description,
        String content,
        String blogCategory,
        Boolean status,
        MultipartFile imageFile
    );

    void deleteBlog(Long farmId);

    void deleteBlogs(Set<Long> ids);

    Page<BlogDTO> getBlogPublics(Pageable pageable);

    Optional<BlogDTO> getBlogPublic(Long blogId);
}
