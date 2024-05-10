package com.fpt.fms.service;

import com.fpt.fms.domain.Tool;
import com.fpt.fms.domain.ToolCategory;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.ToolCategoryRepository;
import com.fpt.fms.repository.ToolRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.dto.ToolDTO;
import com.fpt.fms.service.search.SearchToolDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ToolServiceTest {

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private ToolCategoryRepository toolCategoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ToolService toolService;

    private Tool tool;
    private ToolDTO toolDTO;
    private User user;
    private ToolCategory toolCategory;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setActivated(true);

        toolCategory = new ToolCategory();
        toolCategory.setId(1L);
        toolCategory.setName("Tool Category Name");
        toolCategory.setStatus(true);

        tool = new Tool();
        tool.setId(1L);
        tool.setName("Tool Name");
        tool.setDescription("Tool Description");
        tool.setSource("Tool Source");
        tool.setStatus(true);
        tool.setToolCategory(toolCategory);
        tool.setCreatedBy(user.getEmail());

        toolDTO = new ToolDTO();
        toolDTO.setId(tool.getId());
        toolDTO.setName(tool.getName());
        toolDTO.setDescription(tool.getDescription());
        toolDTO.setSource(tool.getSource());
        toolDTO.setStatus(tool.getStatus());
        toolDTO.setToolCategory(tool.getToolCategory().getName());
    }

    // Existing tests...

    @Test
    public void testSearchToolWhenMatchedThenReturnPageOfToolDTO() {
        SearchToolDTO searchDTO = new SearchToolDTO();
        searchDTO.setName("Tool Name");
        Pageable pageable = PageRequest.of(0, 10);
        List<Tool> tools = Collections.singletonList(tool);
        when(toolRepository.findAll((Specification<Tool>) any(), eq(pageable))).thenReturn(new PageImpl<>(tools));

        Page<ToolDTO> result = toolService.searchTool(searchDTO, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo(toolDTO.getName());
    }

    @Test
    public void testSearchToolWhenNotMatchedThenReturnEmptyPage() {
        SearchToolDTO searchDTO = new SearchToolDTO();
        searchDTO.setName("Non-existing Tool Name");
        Pageable pageable = PageRequest.of(0, 10);
        when(toolRepository.findAll((Specification<Tool>) any(), eq(pageable))).thenReturn(Page.empty());

        Page<ToolDTO> result = toolService.searchTool(searchDTO, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    public void testSearchToolWhenSearchCriteriaNullThenThrowNullPointerException() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(NullPointerException.class, () -> toolService.searchTool(null, pageable));
    }

    @Test
    public void testDeleteToolsWhenValidIdsThenToolsDeleted() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L));
        List<Tool> tools = new ArrayList<>();
        tool.setStatus(false);
        tools.add(tool);
        when(toolRepository.findToolByIdInAndCreatedBy(ids,user.getEmail())).thenReturn(tools);

        toolService.deleteTools(ids);

        verify(toolRepository, times(1)).saveAll(tools);
        assertThat(tool.getDeleted()).isTrue();
    }

    @Test
    public void testDeleteToolsWhenStatusIsTrue() {
        // Arrange
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L));
        List<Tool> tools = new ArrayList<>();
        tool.setStatus(true);
        tools.add(tool);
        when(toolRepository.findToolByIdInAndCreatedBy(ids,user.getEmail())).thenReturn(tools);

        // Act
        BaseException exception = assertThrows(BaseException.class, () -> toolService.deleteTools(ids));

        assertEquals(400, exception.getCode());
        assertEquals("Không thể xóa công cụ đang hoạt động", exception.getMessage());
    }

    @Test
    public void testDeleteToolsWhenEmptyIdsThenNoAction() {
        // Arrange
        Set<Long> ids = new HashSet<>();

        when(toolRepository.findToolByIdInAndCreatedBy(ids,user.getEmail())).thenReturn(Collections.emptyList());

        // Act
        BaseException exception = assertThrows(BaseException.class, () -> toolService.deleteTools(ids));

        assertEquals(400, exception.getCode());
        assertEquals("Không tìm thấy công cụ", exception.getMessage());

        // Assert
    }

    @Test
    public void testGetToolWhenToolExistsThenReturnToolDTO() {
        when(toolRepository.findById(tool.getId())).thenReturn(Optional.of(tool));
        Optional<ToolDTO> result = toolService.getTool(tool.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(toolDTO.getName());
        assertThat(result.get().getDescription()).isEqualTo(toolDTO.getDescription());
    }

    @Test
    public void testGetToolWhenToolDoesNotExistThenReturnEmptyOptional() {
        when(toolRepository.findById(tool.getId())).thenReturn(Optional.empty());

        Optional<ToolDTO> result = toolService.getTool(tool.getId());

        assertThat(result).isNotPresent();
    }

    @Test
    public void testRegisterToolWhenToolWithSameNameExistsThenThrowBaseException() {
        when(toolRepository.findByName(toolDTO.getName())).thenReturn(tool);

        assertThatThrownBy(() -> toolService.registerTool(toolDTO))
            .isInstanceOf(BaseException.class)
            .hasMessage("Tên công đã tồn tại!")
            .hasFieldOrPropertyWithValue("code", HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testUpdateToolWhenToolExistsThenThrowBaseException() {
        when(toolRepository.findById(toolDTO.getId())).thenReturn(Optional.of(tool));
        when(toolRepository.findByName(toolDTO.getName())).thenReturn(new Tool());

        assertThatThrownBy(() -> toolService.updateTool(toolDTO))
            .isInstanceOf(BaseException.class)
            .hasMessage("Tên công đã tồn tại!")
            .hasFieldOrPropertyWithValue("code", HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testUpdateToolWhenToolNotFoundThenThrowBaseException() {
        when(toolRepository.findById(toolDTO.getId())).thenReturn(Optional.empty());

        assertThrows(EmailAlreadyUsedException.class, () -> toolService.updateTool(toolDTO));
        verify(toolCategoryRepository, never()).save(any(ToolCategory.class));
    }

    @Test
    public void testUpdateToolWhenToolIsUpdatedThenSuccess() {
        when(toolRepository.findById(toolDTO.getId())).thenReturn(Optional.of(tool));
        when(toolRepository.findByName(toolDTO.getName())).thenReturn(null);
        when(toolCategoryRepository.findByName(toolDTO.getToolCategory())).thenReturn(toolCategory);

        toolService.updateTool(toolDTO);

        verify(toolRepository, times(1)).save(tool);
    }

    @Test
    public void testDeleteToolWhenToolExistsAndNotActiveThenDeleteTool() {
        tool.setStatus(false);
        when(toolRepository.findById(tool.getId())).thenReturn(Optional.of(tool));

        toolService.deleteTool(tool.getId());

        verify(toolRepository, times(1)).save(tool);
        assertThat(tool.getDeleted()).isTrue();
    }

    @Test
    public void testDeleteToolWhenToolExistsAndActiveThenThrowException() {
        tool.setStatus(true);
        when(toolRepository.findById(tool.getId())).thenReturn(Optional.of(tool));

        assertThatThrownBy(() -> toolService.deleteTool(tool.getId()))
            .isInstanceOf(BaseException.class)
            .hasMessage("Không thể xóa công cụ đang hoạt động")
            .hasFieldOrPropertyWithValue("code", HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testDeleteToolWhenToolDoesNotExistThenThrowException() {
        when(toolRepository.findById(tool.getId())).thenReturn(Optional.empty());

        assertThrows(EmailAlreadyUsedException.class, () -> toolService.deleteTool(tool.getId()));
    }
}
