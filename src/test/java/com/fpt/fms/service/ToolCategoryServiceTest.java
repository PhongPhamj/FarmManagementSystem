package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.ToolCategoryRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.dto.ToolCategoryDTO;
import com.fpt.fms.service.search.SearchToolCategoryDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ToolCategoryService.class})
@ExtendWith(MockitoExtension.class)
public class ToolCategoryServiceTest {

    @MockBean
    private ModelMapper modelMapper;

    @Mock
    private ToolCategoryRepository toolCategoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ToolCategoryService toolCategoryService;

    private ToolCategory toolCategory;
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;
    @Mock
    private User user, user2;
    @Mock
    private Authority userAuthority, userAuthority2;

    @BeforeEach
    public void setUp() {
        //Autho - User
        userAuthority2 = new Authority(); // Thay bằng cách tạo từ chuỗi thực tế
        userAuthority2.setName(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(userAuthority2); // Thêm Authority vào Set
        //Oner
        user2 = new User();
        user2.setActivated(true);
        user2.setAuthorities(authorities);
        user2.setCreatedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setEmail("minh1812001@gmail.com");
        user2.setFarmRole(FarmRole.OWNER);
        user2.setCreatedBy("anonymousUser");
        user2.setFirstName("Le Huu");
        user2.setFullName("Le Huu Minh");
        user2.setId(1L);
        user2.setIdCard("038201012260");
        user2.setImageUrl("https://example.org/example");
        user2.setLastModifiedBy("System");
        user2.setLastModifiedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setLastName("Minh");
        user2.setOwner("Owner");
        user2.setPassword("123456");
        user2.setPhoneNumber("0343383101");
        user2.setDeleted(false);

        toolCategory = new ToolCategory();
        toolCategory.setId(1L);
        toolCategory.setName("Tool Category 1");
        toolCategory.setDescription("Description 1");
        toolCategory.setStatus(true);
        toolCategory.setCreatedBy("test@test.com");

        user = new User();
        user.setEmail("test@test.com");
        modelMapper = new ModelMapper();

        toolCategoryService = new ToolCategoryService(toolCategoryRepository, modelMapper, userRepository);
    }

    // Existing tests...

    @Test
    public void testRegisterToolCategoryWhenToolCategoryNotExistThenSaveToolCategory() {
        // Arrange
        user2.setFarmRole(FarmRole.OWNER);
        ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
        toolCategoryDTO.setName("New Tool Category");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findByName(toolCategoryDTO.getName())).thenReturn(null);

        // Act
        toolCategoryService.registerToolCategory(toolCategoryDTO);

        // Assert
        verify(toolCategoryRepository, times(1)).save(any(ToolCategory.class));
    }

    @Test
    public void testRegisterToolCategoryWhenToolCategoryExistThenThrowBaseException() {
        // Arrange
        ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
        toolCategoryDTO.setName("Existing Tool Category");

        ToolCategory existingToolCategory = new ToolCategory();
        existingToolCategory.setName("Existing Tool Category");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findByName(toolCategoryDTO.getName())).thenReturn(existingToolCategory);

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> toolCategoryService.registerToolCategory(toolCategoryDTO));
        assertEquals(400, exception.getCode());
        assertEquals("Tên loại công đã tồn tại!", exception.getMessage());
        verify(toolCategoryRepository, never()).save(any(ToolCategory.class));
    }

    @Test
    public void testDeleteToolCategorysWhenValidIdsThenToolCategoriesDeleted() {
        // Arrange
        toolCategory.setStatus(false);
        Set<Long> ids = new HashSet<>();
        ids.add(toolCategory.getId());
        ids.add(toolCategory.getId());
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findToolCategoryByIdInAndCreatedBy(ids,user2.getEmail())).thenReturn(Collections.singletonList(toolCategory));

        // Act
        toolCategoryService.deleteToolCategorys(ids);

        // Assert
        verify(toolCategoryRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testDeleteToolCategorysWhenStatusIsTrue() {
        // Arrange
        Set<Long> ids = new HashSet<>();
        ids.add(toolCategory.getId());
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findToolCategoryByIdInAndCreatedBy(ids,user2.getEmail())).thenReturn(Collections.singletonList(toolCategory));

        // Act
        BaseException exception = assertThrows(BaseException.class, () -> toolCategoryService.deleteToolCategorys(ids));

        assertEquals(400, exception.getCode());
        assertEquals("Không thể xóa loại công cụ đang hoạt động", exception.getMessage());
    }

    @Test
    public void testDeleteToolCategorysWhenEmptyIdsThenNoAction() {
        // Arrange
        Set<Long> ids = new HashSet<>();
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findToolCategoryByIdInAndCreatedBy(ids,user2.getEmail())).thenReturn(Collections.emptyList());

        // Act
        BaseException exception = assertThrows(BaseException.class, () -> toolCategoryService.deleteToolCategorys(ids));

        assertEquals(400, exception.getCode());
        assertEquals("Không tìm thấy danh mục công cụ", exception.getMessage());

        // Assert
    }

    @Test
    public void testDeleteToolCategoryWhenToolCategoryExistsAndNotActiveThenDeleteToolCategory() {
        // Arrange
        toolCategory.setStatus(false);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findByIdAndCreatedBy(toolCategory.getId(),user2.getEmail())).thenReturn(toolCategory);

        // Act
        toolCategoryService.deleteToolCategory(toolCategory.getId());

        // Assert

        verify(toolCategoryRepository, times(1)).save(any(ToolCategory.class));
    }

    @Test
    public void testDeleteToolCategoryWhenToolCategoryDoesNotExistThenThrowException() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findByIdAndCreatedBy(2L,user2.getEmail())).thenReturn(null);

        // Act & Assert
        assertThrows(EmailAlreadyUsedException.class, () -> toolCategoryService.deleteToolCategory(2L));
        verify(toolCategoryRepository, never()).save(any(ToolCategory.class));
    }

    @Test
    public void testDeleteToolCategoryWhenToolCategoryExistsAndActiveThenThrowException() {
        // Arrange
        toolCategory.setStatus(true);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findByIdAndCreatedBy(toolCategory.getId(),user2.getEmail())).thenReturn(toolCategory);

        // Act & Assert
        assertThrows(BaseException.class, () -> toolCategoryService.deleteToolCategory(toolCategory.getId()));
        verify(toolCategoryRepository, never()).save(any(ToolCategory.class));
    }

    @Test
    public void testUpdateToolCategoryWhenToolCategoryExistsAndNameNotDuplicatedThenUpdateToolCategory() {
        // Arrange
        ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
        toolCategoryDTO.setId(1L);
        toolCategoryDTO.setName("Updated Tool Category");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findByIdAndCreatedBy(toolCategoryDTO.getId(),user2.getEmail())).thenReturn(toolCategory);
        when(toolCategoryRepository.findByName(toolCategoryDTO.getName())).thenReturn(null);

        // Act
        toolCategoryService.updateToolCategory(toolCategoryDTO);

        // Assert
        verify(toolCategoryRepository, times(1)).save(any(ToolCategory.class));
    }

    @Test
    public void testUpdateToolCategoryWhenToolExistsThenThrowBaseException() {
        ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
        toolCategoryDTO.setId(2L);
        toolCategoryDTO.setName("Tool Category 1");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(toolCategoryRepository.findByIdAndCreatedBy(toolCategoryDTO.getId(),user2.getEmail())).thenReturn(toolCategory);
        when(toolCategoryRepository.findByName(toolCategoryDTO.getName())).thenReturn(new ToolCategory());

        assertThatThrownBy(() -> toolCategoryService.updateToolCategory(toolCategoryDTO))
            .isInstanceOf(BaseException.class)
            .hasMessage("Tên loại công đã tồn tại!")
            .hasFieldOrPropertyWithValue("code", HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testUpdateToolCategoryWhenToolCategoryNotExistsThenDoNothing() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
        toolCategoryDTO.setId(2L);
        toolCategoryDTO.setName("Non-Existing Tool Category");

        when(toolCategoryRepository.findByIdAndCreatedBy(toolCategoryDTO.getId(),user2.getEmail())).thenReturn(null);

        // Act & Assert
        assertThrows(EmailAlreadyUsedException.class, () -> toolCategoryService.updateToolCategory(toolCategoryDTO));
        verify(toolCategoryRepository, never()).save(any(ToolCategory.class));
    }

    @Test
    public void testRegisterToolCategory_WithNonExistingToolCategory_ShouldSaveToolCategory() {

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        // Arrange
        ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
        toolCategoryDTO.setName("New Tool Category");

        when(toolCategoryRepository.findByName(toolCategoryDTO.getName())).thenReturn(null);

        // Act
        toolCategoryService.registerToolCategory(toolCategoryDTO);

        // Assert
        verify(toolCategoryRepository, times(1)).save(any(ToolCategory.class));
    }

    @Test
    public void testRegisterToolCategory_WithExistingToolCategory_ShouldThrowException() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
        toolCategoryDTO.setName("Existing Tool Category");

        ToolCategory existingToolCategory = new ToolCategory();
        existingToolCategory.setName("Existing Tool Category");

        when(toolCategoryRepository.findByName(toolCategoryDTO.getName())).thenReturn(existingToolCategory);

        // Act & Assert
        assertThrows(BaseException.class, () -> toolCategoryService.registerToolCategory(toolCategoryDTO));
        verify(toolCategoryRepository, never()).save(any(ToolCategory.class));
    }

    @Test
    public void testGetToolCategoryWhenToolCategoryExistsThenReturnToolCategoryDTO() {
        when(toolCategoryRepository.findById(1L)).thenReturn(Optional.of(toolCategory));

        Optional<ToolCategoryDTO> result = toolCategoryService.getToolCategory(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(toolCategory.getId());
        assertThat(result.get().getName()).isEqualTo(toolCategory.getName());
        assertThat(result.get().getDescription()).isEqualTo(toolCategory.getDescription());
        assertThat(result.get().getStatus()).isEqualTo(toolCategory.getStatus());
    }

    @Test
    public void testGetToolCategoryWhenToolCategoryDoesNotExistThenReturnEmptyOptional() {
        when(toolCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ToolCategoryDTO> result = toolCategoryService.getToolCategory(1L);

        assertThat(result).isNotPresent();
    }

    @Test
    public void testSearchToolCategoryWhenToolCategoriesMatchThenReturnPageOfToolCategories() {
        // Prepare test data
        SearchToolCategoryDTO searchDTO = new SearchToolCategoryDTO();
        searchDTO.setStatus("true");
        searchDTO.setName("Tool Category 1");
        Pageable pageable = PageRequest.of(0, 1);

        // Mocking the toolCategoryRepository
        when(toolCategoryRepository.findAll(any(Specification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(Collections.singletonList(toolCategory)));

        // Invoke the method being tested
        Page<ToolCategoryDTO> result = toolCategoryService.searchToolCategory(searchDTO, pageable);

        // Assertions
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getId()).isEqualTo(toolCategory.getId());
        assertThat(result.getContent().get(0).getName()).isEqualTo(toolCategory.getName());
        assertThat(result.getContent().get(0).getDescription()).isEqualTo(toolCategory.getDescription());
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(toolCategory.getStatus());
    }


    @Test
    public void testSearchToolCategoryWhenNoToolCategoriesMatchThenReturnEmptyPage() {
        SearchToolCategoryDTO searchDTO = new SearchToolCategoryDTO();
        searchDTO.setStatus("false");
        searchDTO.setName("Tool Category 2");
        Pageable pageable = PageRequest.of(0, 1);

        when(toolCategoryRepository.findAll((Specification<ToolCategory>) any(), eq(pageable)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ToolCategoryDTO> result = toolCategoryService.searchToolCategory(searchDTO, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    public void testSearchToolCategoryWhenMatchedCriteriaThenReturnPageOfToolCategories() {
        SearchToolCategoryDTO searchDTO = new SearchToolCategoryDTO();
        searchDTO.setStatus("true");
        searchDTO.setName("Tool Category 1");
        Pageable pageable = PageRequest.of(0, 1);

        when(toolCategoryRepository.findAll((Specification<ToolCategory>) any(), eq(pageable)))
            .thenReturn(new PageImpl<>(Collections.singletonList(toolCategory)));

        Page<ToolCategoryDTO> result = toolCategoryService.searchToolCategory(searchDTO, pageable);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getId()).isEqualTo(toolCategory.getId());
        assertThat(result.getContent().get(0).getName()).isEqualTo(toolCategory.getName());
        assertThat(result.getContent().get(0).getDescription()).isEqualTo(toolCategory.getDescription());
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(toolCategory.getStatus());
    }

    @Test
    public void testSearchToolCategoryWhenNoMatchedCriteriaThenReturnEmptyPage() {
        SearchToolCategoryDTO searchDTO = new SearchToolCategoryDTO();
        searchDTO.setStatus("false");
        searchDTO.setName("Tool Category 2");
        Pageable pageable = PageRequest.of(0, 1);

        when(toolCategoryRepository.findAll((Specification<ToolCategory>) any(), eq(pageable)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ToolCategoryDTO> result = toolCategoryService.searchToolCategory(searchDTO, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    public void testSearchToolCategoryWhenNullCriteriaThenReturnAllToolCategories() {
        SearchToolCategoryDTO searchDTO = new SearchToolCategoryDTO();
        Pageable pageable = PageRequest.of(0, 1);

        when(toolCategoryRepository.findAll((Specification<ToolCategory>) any(), eq(pageable)))
            .thenReturn(new PageImpl<>(Collections.singletonList(toolCategory)));

        Page<ToolCategoryDTO> result = toolCategoryService.searchToolCategory(searchDTO, pageable);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getId()).isEqualTo(toolCategory.getId());
        assertThat(result.getContent().get(0).getName()).isEqualTo(toolCategory.getName());
        assertThat(result.getContent().get(0).getDescription()).isEqualTo(toolCategory.getDescription());
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(toolCategory.getStatus());
    }

    // Other tests...
}
