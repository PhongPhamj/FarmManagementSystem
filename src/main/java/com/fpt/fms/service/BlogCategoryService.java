package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.BlogCategoryRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.repository.spec.BlogCategorySpectificationBuilder;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.IBlogCategoryService;
import com.fpt.fms.service.dto.BlogCategoryDTO;
import com.fpt.fms.service.search.SearchBlogCategoryDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BlogCategoryService implements IBlogCategoryService {

    private final BlogCategoryRepository blogCategoryRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public BlogCategoryService(BlogCategoryRepository blogCategoryRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.blogCategoryRepository = blogCategoryRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<BlogCategoryDTO> getBlogCategory(Long blogCategoryId) {
        return blogCategoryRepository
            .findById(blogCategoryId)
            .map(
                blog -> {
                    BlogCategoryDTO blogCategory = new BlogCategoryDTO();
                    blogCategory.setId((blog.getId()));
                    blogCategory.setName(blog.getName());
                    blogCategory.setDescription(blog.getDescription());
                    blogCategory.setStatus(blog.getStatus());
                    blogCategory.setCreatedDate(Date.from(blog.getCreatedDate()));
                    blogCategory.setLastModifiedDate(Date.from(blog.getLastModifiedDate()));
                    blogCategory.setCreateBy(blog.getCreatedBy());
                    blogCategory.setLastModifiedBy(blog.getLastModifiedBy());
                    return blogCategory;
                }
            );
    }

    @Override
    public Page<BlogCategoryDTO> getBlogCategorys(Pageable pageable) {
        List<BlogCategoryDTO> blogDTOList = blogCategoryRepository
            .findAll(pageable)
            .stream()
            .filter(blogcate -> !blogcate.getDeleted())
            .map(
                blog -> {
                    BlogCategoryDTO blogCategory = new BlogCategoryDTO();
                    blogCategory.setId((blog.getId()));
                    blogCategory.setName(blog.getName());
                    blogCategory.setDescription(blog.getDescription());
                    blogCategory.setStatus(blog.getStatus());
                    blogCategory.setCreatedDate(Date.from(blog.getCreatedDate()));
                    return blogCategory;
                }
            )
            .collect(Collectors.toList());

        return new PageImpl<>(blogDTOList, pageable, blogDTOList.size());
    }

    @Override
    public Page<BlogCategoryDTO> searchBlogCategory(SearchBlogCategoryDTO searchDTO, Pageable pageable) {
        Specification<BlogCategory> specification = BlogCategorySpectificationBuilder.buildQuery(searchDTO);
        List<BlogCategoryDTO> blogDTOList = blogCategoryRepository
            .findAll(specification, pageable)
            .stream()
            .filter(blogcate -> !blogcate.getDeleted())
            .map(
                blog -> {
                    BlogCategoryDTO blogCategory = new BlogCategoryDTO();
                    blogCategory.setId((blog.getId()));
                    blogCategory.setName(blog.getName());
                    blogCategory.setDescription(blog.getDescription());
                    blogCategory.setStatus(blog.getStatus());
                    blogCategory.setCreatedDate(Date.from(blog.getCreatedDate()));
                    return blogCategory;
                }
            )
            .collect(Collectors.toList());

        return new PageImpl<>(blogDTOList, pageable, blogDTOList.size());
    }

    @Override
    public void registerBlogCategory(BlogCategoryDTO blogCategoryDTO) {
        BlogCategory existingBlogCate = blogCategoryRepository.findBlogCategoryByName(blogCategoryDTO.getName());
        if (existingBlogCate !=null) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Loại danh mục bài viết đã tồn tại!");
        }

        BlogCategory blogCategory = modelMapper.map(blogCategoryDTO, BlogCategory.class);
        blogCategory.setCreatedBy(getUser().getEmail());
        blogCategoryRepository.save(blogCategory);
    }

    @Override
    public List<BlogCategoryDTO> listBlogcateWithStatus() {
        return blogCategoryRepository
            .findAll()
            .stream()
            .filter(blogcate -> blogcate.getStatus() == true)
            .map(
                blog -> {
                    BlogCategoryDTO a = new BlogCategoryDTO();
                    a.setId(blog.getId());
                    a.setName(blog.getName());
                    return a;
                }
            )
            .collect(Collectors.toList());
    }

    @Override
    public void updateBlogCategory(BlogCategoryDTO blogCategoryDTO) {
        BlogCategory blogCategory = getBlogCategoryById(blogCategoryDTO.getId());
        BlogCategory existingBlogCate = blogCategoryRepository.findBlogCategoryByName(blogCategoryDTO.getName());
        if (existingBlogCate !=null && !Objects.equals(existingBlogCate.getId(), blogCategoryDTO.getId())) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Loại danh mục bài viết đã tồn tại!");
        }
        blogCategory.setName(blogCategoryDTO.getName());
        blogCategory.setDescription(blogCategoryDTO.getDescription());
        blogCategory.setLastModifiedDate(Instant.now());
        blogCategory.setStatus(blogCategoryDTO.getStatus());
        blogCategoryRepository.save(blogCategory);
    }

    @Override
    public void deleteBlogCategory(Long id) {
        BlogCategory blogCategory = getBlogCategoryById(id);
        checkBlogCategoryStatus(blogCategory);
        blogCategory.setDeleted(Boolean.TRUE);
        blogCategoryRepository.save(blogCategory);
    }

    @Override
    public void deleteBlogCategorys(Set<Long> ids) {
        List<BlogCategory> blogCategories = blogCategoryRepository.findBlogCategoryByIdIn(ids);
        if (blogCategories.isEmpty()) {
            throw new BaseException(400,"Không tìm thấy danh mục bài viết");
        }
        blogCategories.forEach(
            blog -> {
                checkBlogCategoryStatus(blog);
                blog.setDeleted(Boolean.TRUE);
            }
        );
        blogCategoryRepository.saveAll(blogCategories);
    }

    private User getUser() {
        String curUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
        return userRepository.findOneByLogin(curUser).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
    }

    private BlogCategory getBlogCategoryById(Long id) {
        return blogCategoryRepository.findById(id).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy loại Blog"));
    }

    private static void checkBlogCategoryStatus(BlogCategory blogCategory) {
        if (Boolean.TRUE.equals(blogCategory.getStatus())) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa loaị Blog đang hoạt động");
        }
    }
}
