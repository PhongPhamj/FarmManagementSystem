package com.fpt.fms.service;

import com.fpt.fms.domain.ToolCategory;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.ToolCategoryRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.service.dto.ToolCategoryDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToolCategoryServiceTest {

    @Mock
    private ToolCategoryRepository toolCategoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ToolCategoryService toolCategoryService;

    private ToolCategory toolCategory;
    private User user;

    @BeforeEach
    public void setUp() {
        toolCategory = new ToolCategory();
        toolCategory.setId(1L);
        toolCategory.setName("Tool Category 1");
        toolCategory.setDescription("Description 1");
        toolCategory.setStatus(true);

        user = new User();
        user.setEmail("test@test.com");
    }

    @Test
    public void testRegisterToolCategoryWhenToolCategoryNotExistThenSaveToolCategory() {
        ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
        toolCategoryDTO.setName("Tool Category 1");
        toolCategoryDTO.setCreateBy(user.getEmail());

        when(toolCategoryRepository.findByName(toolCategoryDTO.getName())).thenReturn(null);
        when(userRepository.findOneByLogin(anyString())).thenReturn(Optional.of(user));
        when(modelMapper.map(toolCategoryDTO, ToolCategory.class)).thenReturn(toolCategory);

        toolCategoryService.registerToolCategory(toolCategoryDTO);

        verify(toolCategoryRepository, times(1)).save(any(ToolCategory.class));
    }

    @Test
    public void testRegisterToolCategoryWhenToolCategoryExistThenThrowException() {
        ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
        toolCategoryDTO.setName("Tool Category 1");
        toolCategoryDTO.setCreateBy(user.getEmail());

        when(toolCategoryRepository.findByName(toolCategoryDTO.getName())).thenReturn(toolCategory);

        assertThatThrownBy(() -> toolCategoryService.registerToolCategory(toolCategoryDTO))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining("Tên loại công đã tồn tại!");

        verify(toolCategoryRepository, never()).save(any(ToolCategory.class));
    }
}
