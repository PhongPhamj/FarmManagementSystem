package com.fpt.fms.service;

import com.fpt.fms.domain.Blog;
import com.fpt.fms.domain.BlogCategory;
import com.fpt.fms.fileUtils.FileUtils;
import com.fpt.fms.repository.BlogCategoryRepository;
import com.fpt.fms.repository.BlogRepository;
import com.fpt.fms.service.dto.BlogDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BlogCategoryRepository blogCategoryRepository;

    @Mock
    private FileUtils fileUtils;

    @InjectMocks
    private BlogService blogService;

    private Blog blog;
    private BlogCategory blogCategory;
    private BlogDTO blogDTO;
    private MultipartFile imageFile;

    @BeforeEach
    public void setUp() {
        blogCategory = new BlogCategory();
        blogCategory.setId(1L);
        blogCategory.setName("Test BlogCategory");
        blogCategory.setStatus(true);
        blog = new Blog();
        blog.setId(1L);
        blog.setTitle("Test Blog");
        blog.setDescription("Test Description");
        blog.setContent("Test Content");
        blog.setStatus(true);
        blog.setBlogCategory(blogCategory);
        blogDTO = new BlogDTO();
        blogDTO.setId(blog.getId());
        blogDTO.setTitle(blog.getTitle());
        blogDTO.setDescription(blog.getDescription());
        blogDTO.setContent(blog.getContent());
        blogDTO.setStatus(blog.getStatus());
        blogDTO.setBlogCategory(blog.getBlogCategory().getName());
        modelMapper = new ModelMapper();

        imageFile = new MockMultipartFile("imageFile", "hello.png", "image/png", "some image".getBytes());
    }

//    @Test
//    public void testSearchBlogPublicWhenBlogRepositoryReturnsListThenReturnPageWithBlogDTOs() {
//        BlogRepository blogRepository = Mockito.mock(BlogRepository.class);
//
//// Creating a single blog
//        Blog blog = new Blog(); // Replace this with your actual Blog object
//
//// Mocking the behavior of blogRepository.findAll() method to return a list containing a single blog
//        Mockito.when(blogRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
//            .thenReturn((Page) Collections.singletonList(blog));
//        when(modelMapper.map(blog, BlogDTO.class)).thenReturn(blogDTO);
//
//        Pageable pageable = PageRequest.of(0, 1);
//        Page<BlogDTO> result = blogService.searchBlogPublic(null, pageable);
//
//        assertEquals(1, result.getContent().size());
//        assertEquals(blogDTO, result.getContent().get(0));
//    }
//
//    @Test
//    public void testSearchBlogPublicWhenBlogRepositoryReturnsEmptyListThenReturnEmptyPage() {
//        BlogRepository blogRepository = Mockito.mock(BlogRepository.class);
//
//// Creating a single blog
//        Blog blog = new Blog(); // Replace this with your actual Blog object
//
//// Mocking the behavior of blogRepository.findAll() method to return a list containing a single blog
//        Mockito.when(blogRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
//            .thenReturn((Page) Collections.emptyList());
//
//        Pageable pageable = PageRequest.of(0, 1);
//        Page<BlogDTO> result = blogService.searchBlogPublic(null, pageable);
//
//        assertTrue(result.getContent().isEmpty());
//    }
//
//    @Test
//    public void testSearchBlogPublicWhenBlogRepositoryReturnsListWithSizeLessThanPageSizeThenReturnPageWithCorrectSize() {
//        when(blogRepository.findAll((Specification<Blog>) any(), any(Pageable.class))).thenReturn((Page<Blog>) Collections.singletonList(blog));
//        when(modelMapper.map(blog, BlogDTO.class)).thenReturn(blogDTO);
//
//        Pageable pageable = PageRequest.of(0, 2);
//        Page<BlogDTO> result = blogService.searchBlogPublic(null, pageable);
//
//        assertEquals(1, result.getContent().size());
//    }
//
//    @Test
//    public void testSearchBlogPublicWhenBlogRepositoryReturnsListInCorrectOrderThenReturnPageWithCorrectOrder() {
//        Blog blog2 = new Blog();
//        blog2.setId(2L);
//        blog2.setTitle("Test Blog 2");
//        blog2.setDescription("Test Description 2");
//        blog2.setContent("Test Content 2");
//        blog2.setStatus(true);
//        blog2.setBlogCategory(blogCategory);
//        BlogDTO blogDTO2 = new BlogDTO();
//        blogDTO2.setId(blog2.getId());
//        blogDTO2.setTitle(blog2.getTitle());
//        blogDTO2.setDescription(blog2.getDescription());
//        blogDTO2.setContent(blog2.getContent());
//        blogDTO2.setStatus(blog2.getStatus());
//        blogDTO2.setBlogCategory(blog2.getBlogCategory().getName());
//
//        Page<Blog> blogPage = new PageImpl<>(Arrays.asList(blog, blog2));
//        when(blogRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
//            .thenReturn(blogPage);
//        when(modelMapper.map(blog, BlogDTO.class)).thenReturn(blogDTO);
//        when(modelMapper.map(blog2, BlogDTO.class)).thenReturn(blogDTO2);
//
//        Pageable pageable = PageRequest.of(0, 2);
//        Page<BlogDTO> result = blogService.searchBlogPublic(null, pageable);
//
//        assertEquals(2, result.getContent().size());
//        assertEquals(blogDTO, result.getContent().get(0));
//        assertEquals(blogDTO2, result.getContent().get(1));
//    }

    @Test
    public void testUpdateBlogWhenBlogDoesNotExistThenThrowException() {
        when(blogRepository.findById(blog.getId())).thenReturn(Optional.empty());

        assertThrows(EmailAlreadyUsedException.class, () -> blogService.updateBlog(blog.getId(), blog.getTitle(), blog.getDescription(),
            blog.getContent(), blog.getBlogCategory().getName(), blog.getStatus(), imageFile));
    }

    @Test
    public void testUpdateBlogWhenBlogCategoryDoesNotExistThenThrowException() {
        when(blogRepository.findById(blog.getId())).thenReturn(Optional.of(blog));
        when(blogCategoryRepository.findBlogCategoryByName(blog.getBlogCategory().getName())).thenReturn(null);

        assertThrows(BaseException.class, () -> blogService.updateBlog(blog.getId(), blog.getTitle(), blog.getDescription(),
            blog.getContent(), blog.getBlogCategory().getName(), blog.getStatus(), imageFile));
    }

    @Test
    public void testRegisterBlogWhenSaveMethodThrowsExceptionThenThrowException() {
        when(blogRepository.save(any(Blog.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> blogService.registerBlog(blog.getTitle(), blog.getDescription(),
            blog.getContent(), blog.getBlogCategory().getName(), blog.getStatus(), imageFile));
    }

    @Test
    public void testRegisterBlogWhenUploadImageLocalMethodThrowsExceptionThenThrowException() {
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(fileUtils.uploadImageLocal(any(MultipartFile.class), anyString(), anyString(), anyLong()))
            .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> blogService.registerBlog(blog.getTitle(), blog.getDescription(),
            blog.getContent(), blog.getBlogCategory().getName(), blog.getStatus(), imageFile));
    }

    @Test
    public void testGetBlogWhenBlogExistsThenReturnBlogDTO() {
        when(blogRepository.findById(blog.getId())).thenReturn(Optional.of(blog));

        Optional<BlogDTO> result = blogService.getBlog(blog.getId());

        assertTrue(result.isPresent());
        assertEquals(blogDTO.getId(), result.get().getId());
        assertEquals(blogDTO.getTitle(), result.get().getTitle());
        assertEquals(blogDTO.getDescription(), result.get().getDescription());
        assertEquals(blogDTO.getContent(), result.get().getContent());
        assertEquals(blogDTO.getStatus(), result.get().getStatus());
        assertEquals(blogDTO.getBlogCategory(), result.get().getBlogCategory());
    }

    @Test
    public void testGetBlogWhenBlogDoesNotExistThenReturnEmptyOptional() {
        when(blogRepository.findById(blog.getId())).thenReturn(Optional.empty());

        Optional<BlogDTO> result = blogService.getBlog(blog.getId());

        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteBlogsWhenStatusIsTrue() {
        // Arrange
        Set<Long> ids = new HashSet<>();
        ids.add(blog.getId());
        when(blogRepository.findBlogByIdIn(ids)).thenReturn(Collections.singletonList(blog));

        // Act
        BaseException exception = assertThrows(BaseException.class, () -> blogService.deleteBlogs(ids));

        assertEquals(400, exception.getCode());
        assertEquals("Không thể xóa Blog đang hoạt động", exception.getMessage());
    }

    @Test
    public void testDeleteBlogsWhenEmptyIdsThenNoAction() {
        // Arrange
        Set<Long> ids = new HashSet<>();

        when(blogRepository.findBlogByIdIn(ids)).thenReturn(Collections.emptyList());

        // Act
        BaseException exception = assertThrows(BaseException.class, () -> blogService.deleteBlogs(ids));

        assertEquals(400, exception.getCode());
        assertEquals("Không tìm thấy bài viết", exception.getMessage());

        // Assert
    }

    @Test
    public void testDeleteBlogCategoryWhenToolCategoryExistsAndNotActiveThenDeleteToolCategory() {
        // Arrange
        blog.setStatus(false);
        when(blogRepository.findById(blogCategory.getId())).thenReturn(Optional.of(blog));

        // Act
        blogService.deleteBlog(blog.getId());

        // Assert

        verify(blogRepository, times(1)).save(any(Blog.class));
    }

    @Test
    public void testDeleteToolCategoryWhenToolCategoryDoesNotExistThenThrowException() {
        // Arrange
        when(blogRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EmailAlreadyUsedException.class, () -> blogService.deleteBlog(2L));
        verify(blogRepository, never()).save(any(Blog.class));
    }

    @Test
    public void testDeleteToolCategoryWhenToolCategoryExistsAndActiveThenThrowException() {
        // Arrange
        blog.setStatus(true);
        when(blogRepository.findById(blog.getId())).thenReturn(Optional.of(blog));

        // Act & Assert
        assertThrows(BaseException.class, () -> blogService.deleteBlog(blog.getId()));
        verify(blogRepository, never()).save(any(Blog.class));
    }
}
