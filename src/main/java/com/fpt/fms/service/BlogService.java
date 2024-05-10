package com.fpt.fms.service;

import com.fpt.fms.domain.Blog;
import com.fpt.fms.domain.BlogCategory;
import com.fpt.fms.fileUtils.FileUtils;
import com.fpt.fms.repository.BlogCategoryRepository;
import com.fpt.fms.repository.BlogRepository;
import com.fpt.fms.repository.spec.BlogSpectificationBuilder;
import com.fpt.fms.service.baseservice.IBlogService;
import com.fpt.fms.service.dto.BlogDTO;
import com.fpt.fms.service.search.SearchBlogDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BlogService implements IBlogService {

    private final BlogCategoryRepository blogCategoryRepository;
    private final ModelMapper modelMapper;

    private final BlogRepository blogRepository;
    private final FileUtils fileUtils;

    public BlogService(
        BlogCategoryRepository blogCategoryRepository,
        ModelMapper modelMapper,
        BlogRepository blogRepository,
        FileUtils fileUtils
    ) {
        this.blogCategoryRepository = blogCategoryRepository;
        this.modelMapper = modelMapper;
        this.blogRepository = blogRepository;
        this.fileUtils = fileUtils;
    }

    @Override
    public Page<BlogDTO> getBlogs(Pageable pageable) {
        List<BlogDTO> blogDTOList = blogRepository
            .findBlogByBlogCategoryIn(pageable,getBlogCategorys())
            .stream()
            .filter(
                blog ->
                    blog.getBlogCategory() != null && // Kiểm tra xem BlogCategory không phải là null
                    !blog.getBlogCategory().getDeleted() &&
                    blog.getBlogCategory().getStatus()
            )
            .distinct()
            .map(
                blog -> {
                    BlogDTO blogList = new BlogDTO();
                    blogList.setId(blog.getId());
                    blogList.setTitle(blog.getTitle());
                    blogList.setImageUrl(blog.getImageUrl());
                    blogList.setDescription(blog.getDescription());
                    blogList.setStatus(blog.getStatus());
                    blogList.setBlogCategory(blog.getBlogCategory() != null ? blog.getBlogCategory().getName() : "Loại hình đã bị xóa"); // Xử lý trường hợp null
                    blogList.setCreatedDate(Date.from(blog.getCreatedDate()));
                    return blogList;
                }
            )
            .collect(Collectors.toList());

        return new PageImpl<>(blogDTOList, pageable, blogDTOList.size());
    }

    @Override
    public Page<BlogDTO> searchBlog(SearchBlogDTO searchDTO, Pageable pageable) {
        Specification<Blog> specification = BlogSpectificationBuilder.buildQuery(searchDTO,getBlogCategorys());

        List<BlogDTO> blogDTOList = blogRepository
            .findAll(specification, pageable)
            .stream()
            .filter(blog -> blog.getBlogCategory() != null && !blog.getBlogCategory().getDeleted() && blog.getBlogCategory().getStatus())
            .distinct()
            .map(
                blog -> {
                    BlogDTO blogList = new BlogDTO();
                    blogList.setId(blog.getId());
                    blogList.setTitle(blog.getTitle());
                    blogList.setImageUrl(blog.getImageUrl());
                    blogList.setDescription(blog.getDescription());
                    blogList.setStatus(blog.getStatus());
                    blogList.setBlogCategory(blog.getBlogCategory().getName());
                    blogList.setCreatedDate(Date.from(blog.getCreatedDate()));
                    return blogList;
                }
            )
            .collect(Collectors.toList());

        return new PageImpl<>(blogDTOList, pageable, blogDTOList.size());
    }

    @Override
    public Page<BlogDTO> searchBlogPublic(SearchBlogDTO searchDTO, Pageable pageable) {
        Specification<Blog> specification = BlogSpectificationBuilder.buildQuery(searchDTO,getBlogCategorys());

        List<BlogDTO> blogDTOList = blogRepository
            .findAll(specification, pageable)
            .stream()
            .filter(
                blog ->
                    blog.getBlogCategory() != null &&
                    !blog.getBlogCategory().getDeleted() &&
                    blog.getBlogCategory().getStatus() &&
                    blog.getStatus()
            )
            .distinct()
            .map(
                blog -> {
                    BlogDTO blogList = new BlogDTO();
                    blogList.setId(blog.getId());
                    blogList.setTitle(blog.getTitle());
                    blogList.setImageUrl(blog.getImageUrl());
                    blogList.setDescription(blog.getDescription());
                    blogList.setStatus(blog.getStatus());
                    blogList.setBlogCategory(blog.getBlogCategory().getName());
                    blogList.setCreatedDate(Date.from(blog.getCreatedDate()));
                    return blogList;
                }
            )
            .collect(Collectors.toList());

        return new PageImpl<>(blogDTOList, pageable, blogDTOList.size());
    }

    @Override
    public void registerBlog(
        String title,
        String description,
        String content,
        String blogCategory,
        Boolean status,
        MultipartFile imageFile
    ) {
        BlogDTO blog = new BlogDTO();
        blog.setTitle(title);
        blog.setDescription(description);
        blog.setContent(content);
        blog.setStatus(status);
        Blog createBlog = modelMapper.map(blog, Blog.class);
        Blog savedBlog = blogRepository.save(createBlog);
        String folder = "Blog_Image";
        if(imageFile == null){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Chọn ảnh cho bài viết!");
        }
        String imageUrl = fileUtils.uploadImageLocal(imageFile, folder, imageFile.getOriginalFilename(), savedBlog.getId());
        blog.setImageUrl(imageUrl);
        savedBlog.setBlogCategory(getBlogCategoryByName(blogCategory));
        savedBlog.setImageUrl(imageUrl);
        blogRepository.save(savedBlog);
    }

    @Override
    public Optional<BlogDTO> getBlog(Long blogId) {
        return blogRepository
            .findById(blogId)
            .map(
                blog -> {
                    BlogDTO detail = new BlogDTO();
                    detail.setId((blog.getId()));
                    detail.setTitle(blog.getTitle());
                    detail.setDescription(blog.getDescription());
                    detail.setImageUrl(blog.getImageUrl());
                    detail.setContent(blog.getContent());
                    detail.setStatus(blog.getStatus());
                    detail.setBlogCategory(blog.getBlogCategory().getName());
                    detail.setCreateBy(blog.getCreatedBy());
                    detail.setLastModifiedBy(blog.getLastModifiedBy());
                    detail.setCreatedDate(Date.from(blog.getCreatedDate()));
                    detail.setLastModifiedDate(Date.from(blog.getLastModifiedDate()));
                    return detail;
                }
            );
    }

    @Override
    @Transactional
    public void updateBlog(
        Long id,
        String title,
        String description,
        String content,
        String blogCategory,
        Boolean status,
        MultipartFile imageFile
    ) {
        Blog existingBlog = getBlogById(id);

        BlogDTO blog = new BlogDTO();
        blog.setId(id);
        blog.setTitle(title);
        blog.setDescription(description);
        blog.setContent(content);
        blog.setStatus(status);

        // Kiểm tra xem file ảnh mới có được cung cấp không
        if (imageFile != null && !imageFile.isEmpty()) {
            String folder = "Blog_Image";
            String imageUrl = fileUtils.uploadImageLocal(imageFile, folder, imageFile.getOriginalFilename(), id);
            blog.setImageUrl(imageUrl);
        } else if (imageFile == null) {
            // Nếu không có file ảnh mới, giữ nguyên URL ảnh cũ
            blog.setImageUrl(existingBlog.getImageUrl());
        }

        blog.setLastModifiedDate(Date.from(Instant.now()));

        Blog mapBlog = modelMapper.map(blog, Blog.class);
        BlogCategory category = getBlogCategoryByName(blogCategory);

        // Check if the category is null before setting it
        if (category != null) {
            mapBlog.setBlogCategory(category);
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Loại danh mục bài viết không tồn tại!");
        }
        blogRepository.save(mapBlog);
    }

    @Override
    public void deleteBlog(Long id) {
        Blog blog = getBlogById(id);
        checkBlogStatus(blog);
        blog.setDeleted(Boolean.TRUE);
        blogRepository.save(blog);
    }

    @Override
    public void deleteBlogs(Set<Long> ids) {
        List<Blog> blogs = blogRepository.findBlogByIdIn(ids);
        if (blogs.isEmpty()) {
            throw new BaseException(400,"Không tìm thấy bài viết");
        }
        blogs.forEach(
            blog -> {
                checkBlogStatus(blog);
                blog.setDeleted(Boolean.TRUE);
            }
        );
        blogRepository.saveAll(blogs);
    }

    @Override
    public Page<BlogDTO> getBlogPublics(Pageable pageable) {


        List<BlogDTO> blogDTOList = blogRepository
            .findBlogByBlogCategoryIn(pageable,getBlogCategorys())
            .stream()
            .filter(
                blog ->
                    blog.getBlogCategory() != null && // Kiểm tra xem BlogCategory không phải là null
                        !blog.getBlogCategory().getDeleted() &&
                        blog.getBlogCategory().getStatus()
            )
            .distinct()
            .map(
                blog -> {
                    BlogDTO blogDTO = new BlogDTO();
                    blogDTO.setId(blog.getId());
                    blogDTO.setTitle(blog.getTitle());
                    blogDTO.setImageUrl(blog.getImageUrl());
                    blogDTO.setDescription(blog.getDescription());
                    blogDTO.setBlogCategory(blog.getBlogCategory().getName());
                    blogDTO.setCreatedDate(Date.from(blog.getCreatedDate()));
                    return blogDTO;
                }
            )
            .collect(Collectors.toList());

        return new PageImpl<>(blogDTOList, pageable, blogDTOList.size());

    }

    @Override
    public Optional<BlogDTO> getBlogPublic(Long blogId) {
        return Optional.ofNullable(
            blogRepository
                .findById(blogId)
                .filter(
                    blog ->
                        blog.getBlogCategory() != null &&
                        !blog.getBlogCategory().getDeleted() &&
                        blog.getBlogCategory().getStatus() &&
                        blog.getStatus()
                )
                .map(
                    blog -> {
                        BlogDTO detail = new BlogDTO();
                        detail.setId((blog.getId()));
                        detail.setTitle(blog.getTitle());
                        detail.setDescription(blog.getDescription());
                        detail.setImageUrl(blog.getImageUrl());
                        detail.setContent(blog.getContent());
                        detail.setStatus(blog.getStatus());
                        detail.setBlogCategory(blog.getBlogCategory().getName());
                        detail.setCreateBy(blog.getCreatedBy());
                        return detail;
                    }
                )
                .stream()
                .findFirst()
                .orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy blog"))
        );
    }

    private List<BlogCategory> getBlogCategorys(){
        return blogCategoryRepository.findAll();
    }
    private Blog getBlogById(Long id) {
        return blogRepository.findById(id).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy blog"));
    }

    private BlogCategory getBlogCategoryByName(String name) {
        return blogCategoryRepository.findBlogCategoryByName(name);
    }

    private static void checkBlogStatus(Blog blog) {
        if (Boolean.TRUE.equals(blog.getStatus())) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa Blog đang hoạt động");
        }
    }
}
