package com.fpt.fms.service;

import com.fpt.fms.domain.BlogCategory;
import com.fpt.fms.domain.ToolCategory;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.BlogCategoryRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.service.dto.BlogCategoryDTO;
import com.fpt.fms.service.search.SearchBlogCategoryDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {BlogCategoryService.class})
@ExtendWith(MockitoExtension.class)
public class BlogCategoryServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BlogCategoryRepository blogCategoryRepository;

    @InjectMocks
    private BlogCategoryService blogCategoryService;

    private BlogCategory blogCategory,blogCategory1,blogCategory2;

    @BeforeEach
    public void setUp() {
        blogCategory = new BlogCategory();
        blogCategory.setId(1L);
        blogCategory.setName("Test Category");
        blogCategory.setDescription("Test Description");
        blogCategory.setStatus(true);

        blogCategory1 = new BlogCategory();
        blogCategory1.setId(2L);
        blogCategory1.setName("Test Category 1");
        blogCategory1.setDescription("Test Description 1");
        blogCategory1.setStatus(true);

        blogCategory2 = new BlogCategory();
        blogCategory2.setId(3L);
        blogCategory2.setName("Test Category");
        blogCategory2.setDescription("Test Description 1");
        blogCategory2.setStatus(true);
    }
    @Test
    @DisplayName("Test the 'searchBlogCategory' method when the search criteria match some blog categories")
    public void testSearchBlogCategoryWhenMatchedThenReturnPageOfBlogCategories() {
        // Arrange
        SearchBlogCategoryDTO searchDTO = new SearchBlogCategoryDTO();
        searchDTO.setName("Test Category");
        Pageable pageable = PageRequest.of(0, 10);

        List<BlogCategory> blogCategories = Collections.singletonList(blogCategory);
        Page<BlogCategory> page = new PageImpl<>(blogCategories, pageable, blogCategories.size());

        when(blogCategoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // Act
        Page<BlogCategoryDTO> result = blogCategoryService.searchBlogCategory(searchDTO, pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(blogCategory.getName(), result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Test the 'searchBlogCategory' method when the search criteria do not match any blog categories")
    public void testSearchBlogCategoryWhenNotMatchedThenReturnEmptyPage() {
        // Arrange
        SearchBlogCategoryDTO searchDTO = new SearchBlogCategoryDTO();
        searchDTO.setName("Non-existing Category");
        Pageable pageable = PageRequest.of(0, 10);

        List<BlogCategory> blogCategories = Collections.emptyList();
        Page<BlogCategory> page = new PageImpl<>(blogCategories, pageable, blogCategories.size());

        when(blogCategoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // Act
        Page<BlogCategoryDTO> result = blogCategoryService.searchBlogCategory(searchDTO, pageable);

        // Assert
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Test the 'searchBlogCategory' method when an exception occurs during the search")
    public void testSearchBlogCategoryWhenExceptionThenThrowException() {
        // Arrange
        SearchBlogCategoryDTO searchDTO = new SearchBlogCategoryDTO();
        searchDTO.setName("Test Category");
        Pageable pageable = PageRequest.of(0, 10);

        when(blogCategoryRepository.findAll(any(Specification.class), eq(pageable))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> blogCategoryService.searchBlogCategory(searchDTO, pageable));
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    public void testDeleteBlogCategorysWhenValidIdsThenToolCategoriesDeleted() {
        // Arrange
        blogCategory.setStatus(false);
        Set<Long> ids = new HashSet<>();
        ids.add(blogCategory.getId());
        when(blogCategoryRepository.findBlogCategoryByIdIn(ids)).thenReturn(Collections.singletonList(blogCategory));

        // Act
        blogCategoryService.deleteBlogCategorys(ids);

        // Assert
        verify(blogCategoryRepository, times(1)).saveAll(anyList());
    }
    /*    @Test
        @WithMockUser(username = "test@test.com", roles = {"ROLE_USER"})
        public void testRegisterBlogCategoryWhenCategoryDoesNotExistThenSaveCategory() {
            BlogCategoryDTO blogCategoryDTO = new BlogCategoryDTO();

            blogCategoryDTO.setName("Test Category");

            SecurityUtils securityUtils = Mockito.mock(SecurityUtils.class);
            // Mock một user cụ thể với email tương ứng
            User user = new User();
            user.setEmail("test@test.com");

            // Định nghĩa phương thức findBlogCategoryByName trả về null (không tìm thấy)
            when(blogCategoryRepository.findBlogCategoryByName(blogCategoryDTO.getName())).thenReturn(null);
            when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("test@test.com"));
            // Định nghĩa phương thức findOneByLogin với email tương ứng trả về Optional rỗng
            when(userRepository.findOneByLogin(eq("test@test.com"))).thenReturn(Optional.empty());

            // Gọi phương thức cần kiểm thử
            blogCategoryService.registerBlogCategory(blogCategoryDTO);

            // Xác nhận rằng phương thức save của blogCategoryRepository được gọi với một đối tượng BlogCategory bất kỳ
            verify(blogCategoryRepository).save(any(BlogCategory.class));
        }*/
    @Test
    public void testDeleteBlogCategorysWhenStatusIsTrue() {
        // Arrange
        Set<Long> ids = new HashSet<>();
        ids.add(blogCategory.getId());
        when(blogCategoryRepository.findBlogCategoryByIdIn(ids)).thenReturn(Collections.singletonList(blogCategory));

        // Act
        BaseException exception = assertThrows(BaseException.class, () -> blogCategoryService.deleteBlogCategorys(ids));

        assertEquals(400, exception.getCode());
        assertEquals("Không thể xóa loaị Blog đang hoạt động", exception.getMessage());
    }

    @Test
    public void testDeleteBlogCategorysWhenEmptyIdsThenNoAction() {
        // Arrange
        Set<Long> ids = new HashSet<>();

        when(blogCategoryRepository.findBlogCategoryByIdIn(ids)).thenReturn(Collections.emptyList());

        // Act
        BaseException exception = assertThrows(BaseException.class, () -> blogCategoryService.deleteBlogCategorys(ids));

        assertEquals(400, exception.getCode());
        assertEquals("Không tìm thấy danh mục bài viết", exception.getMessage());

        // Assert
    }

    @Test
    public void testDeleteBlogCategoryWhenToolCategoryExistsAndNotActiveThenDeleteToolCategory() {
        // Arrange
        blogCategory.setStatus(false);
        when(blogCategoryRepository.findById(blogCategory.getId())).thenReturn(Optional.of(blogCategory));

        // Act
        blogCategoryService.deleteBlogCategory(blogCategory.getId());

        // Assert

        verify(blogCategoryRepository, times(1)).save(any(BlogCategory.class));
    }

    @Test
    public void testDeleteToolCategoryWhenToolCategoryDoesNotExistThenThrowException() {
        // Arrange
        when(blogCategoryRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EmailAlreadyUsedException.class, () -> blogCategoryService.deleteBlogCategory(2L));
        verify(blogCategoryRepository, never()).save(any(BlogCategory.class));
    }

    @Test
    public void testDeleteToolCategoryWhenToolCategoryExistsAndActiveThenThrowException() {
        // Arrange
        blogCategory.setStatus(true);
        when(blogCategoryRepository.findById(blogCategory.getId())).thenReturn(Optional.of(blogCategory));

        // Act & Assert
        assertThrows(BaseException.class, () -> blogCategoryService.deleteBlogCategory(blogCategory.getId()));
        verify(blogCategoryRepository, never()).save(any(BlogCategory.class));
    }

    @Test
    public void testGetBlogCategoryWhenCategoryExistsThenReturnCategory() {
        when(blogCategoryRepository.findById(1L)).thenReturn(Optional.of(blogCategory));

        Optional<BlogCategoryDTO> result = blogCategoryService.getBlogCategory(1L);

        assertTrue(result.isPresent());
        assertEquals(blogCategory.getId(), result.get().getId());
        assertEquals(blogCategory.getName(), result.get().getName());
        assertEquals(blogCategory.getDescription(), result.get().getDescription());
        assertEquals(blogCategory.getStatus(), result.get().getStatus());
    }

    @Test
    public void testGetBlogCategoryWhenCategoryDoesNotExistThenReturnEmptyOptional() {
        when(blogCategoryRepository.findById(4L)).thenReturn(Optional.empty());

        Optional<BlogCategoryDTO> result = blogCategoryService.getBlogCategory(4L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testRegisterBlogCategoryWhenCategoryAlreadyExistsThenThrowException1() {
        BlogCategoryDTO blogCategoryDTO = new BlogCategoryDTO();
        blogCategoryDTO.setName("Test Category");
        User user = new User();
        user.setEmail("test@test.com");

        lenient().when(userRepository.findOneByLogin("test@test.com")).thenReturn(Optional.of(user));

        when(blogCategoryRepository.findBlogCategoryByName(blogCategoryDTO.getName())).thenReturn(blogCategory);

        assertThrows(BaseException.class, () -> blogCategoryService.registerBlogCategory(blogCategoryDTO));
    }

    @Test
    public void testRegisterBlogCategoryWhenCurrentUserNotFoundThenThrowException() {
        BlogCategoryDTO blogCategoryDTO = new BlogCategoryDTO();
        blogCategoryDTO.setName("Test Category");

        assertThrows(EmailAlreadyUsedException.class, () -> blogCategoryService.registerBlogCategory(blogCategoryDTO));
    }

    @Test
    public void testUpdateBlogCategoryWhenNameDuplicatedThenThrowException() {
        BlogCategoryDTO blogCategoryDTO = new BlogCategoryDTO();
        blogCategoryDTO.setId(1L);
        blogCategoryDTO.setName("Test Category");


        when(blogCategoryRepository.findBlogCategoryByName(blogCategoryDTO.getName())).thenReturn(blogCategory2);
        when(blogCategoryRepository.findById(blogCategoryDTO.getId())).thenReturn(Optional.of(blogCategory));

        assertThrows(BaseException.class, () -> blogCategoryService.updateBlogCategory(blogCategoryDTO));
        verify(blogCategoryRepository, never()).save(any(BlogCategory.class));
    }

    @Test
    public void testUpdateBlogCategoryWhenCategoryNotExistsThenThrowException() {
        BlogCategoryDTO blogCategoryDTO = new BlogCategoryDTO();
        blogCategoryDTO.setId(2L);
        blogCategoryDTO.setName("Test Category");

        when(blogCategoryRepository.findById(blogCategoryDTO.getId())).thenReturn(Optional.empty());

        assertThrows(EmailAlreadyUsedException.class, () -> blogCategoryService.updateBlogCategory(blogCategoryDTO));
    }

    @Test
    public void testUpdateBlogCategoryWhenNameNotDuplicatedThenSuccess() {
        BlogCategoryDTO blogCategoryDTO = new BlogCategoryDTO();
        blogCategoryDTO.setId(1L);
        blogCategoryDTO.setName("Test Category");

        when(blogCategoryRepository.findBlogCategoryByName(blogCategoryDTO.getName())).thenReturn(null);
        when(blogCategoryRepository.findById(blogCategoryDTO.getId())).thenReturn(Optional.of(blogCategory));

        blogCategoryService.updateBlogCategory(blogCategoryDTO);

        verify(blogCategoryRepository, times(1)).save(any(BlogCategory.class));
    }
}
