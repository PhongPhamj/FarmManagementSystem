package com.fpt.fms.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fpt.fms.domain.FarmRole;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.service.dto.EmployeeDTO;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private User employee, employee2, employee3;

    @BeforeEach
    public void setUp() {
        employee = new User();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setCreatedBy("lehuuminh2001@gmail.com");
        employee.setEmail("john.doe@example.com");
        employee.setDeleted(false);

        employee2 = new User();
        employee2.setId(2L);
        employee2.setFirstName("Alice");
        employee2.setLastName("Smith");
        employee2.setCreatedBy("lehuuminh2001@gmail.com");
        employee2.setEmail("alice.smith@example.com");
        employee2.setDeleted(false);

        employee3 = new User();
        employee3.setId(3L);
        employee3.setFirstName("Bob");
        employee3.setLastName("Johnson");
        employee3.setCreatedBy("lehuuminh2001@gmail.com");
        employee3.setEmail("bob.johnson@example.com");
        employee3.setDeleted(false);
    }

    // Existing test cases...

    @Test
    public void testGetAllEmployeeWhenValidParametersThenReturnPageOfEmployeeDTO() {
        String createBy = "lehuuminh2001@gmail.com";
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> userPage = new PageImpl<>(Arrays.asList(employee, employee2, employee3), pageable, 3);

        when(userRepository.findAllByCreatedBy(createBy, FarmRole.EMPLOYEE, pageable)).thenReturn(userPage);

        Page<EmployeeDTO> result = employeeService.getAllEmployee(createBy, pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
    }

    @Test
    public void testGetAllEmployeeWhenNonExistingCreateByThenReturnEmptyPage() {
        String createBy = "non-existing@gmail.com";
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> userPage = Page.empty(pageable);

        when(userRepository.findAllByCreatedBy(createBy, FarmRole.EMPLOYEE, pageable)).thenReturn(userPage);

        Page<EmployeeDTO> result = employeeService.getAllEmployee(createBy, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void testGetAllEmployeeWhenNullCreateByThenThrowNullPointerException() {
        Pageable pageable = PageRequest.of(0, 5);

        assertThrows(NullPointerException.class, () -> employeeService.getAllEmployee(null, pageable));
    }

    @Test
    public void testDeleteEmployeesWhenUserFoundAndNotActivatedThenDeleteUser() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        employee.setActivated(false);
        employee2.setActivated(false);
        employee3.setActivated(false);

        when(userRepository.findAllByIdIn(ids)).thenReturn(Arrays.asList(employee, employee2, employee3));

        employeeService.deleteEmployees("lehuuminh2001@gmail.com", ids);

        assertTrue(employee.getDeleted());
        assertTrue(employee2.getDeleted());
        assertTrue(employee3.getDeleted());
        verify(userRepository, times(1)).saveAll(Arrays.asList(employee, employee2, employee3));
    }

    @Test
    public void testDeleteEmployeesWhenUserFoundAndActivatedThenNotDeleteUser() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        employee.setActivated(true);
        employee2.setActivated(true);
        employee3.setActivated(true);

        when(userRepository.findAllByIdIn(ids)).thenReturn(Arrays.asList(employee, employee2, employee3));

        assertThrows(BaseException.class, () -> employeeService.deleteEmployees("lehuuminh2001@gmail.com", ids));

        assertFalse(employee.getDeleted());
        assertFalse(employee2.getDeleted());
        assertFalse(employee3.getDeleted());
        verify(userRepository, times(0)).saveAll(Arrays.asList(employee, employee2, employee3));
    }

    @Test
    public void testDeleteEmployeesWhenUserNotFoundThenNotDeleteUser() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));

        when(userRepository.findAllByIdIn(ids)).thenReturn(Arrays.asList());

        employeeService.deleteEmployees("lehuuminh2001@gmail.com", ids);

        verify(userRepository, times(0)).saveAll(Arrays.asList(employee, employee2, employee3));
    }

    @Test
    public void testDeleteEmployeeWhenUserIsFoundThenDeleteUser() {
        when(userRepository.findUserByIdAndCreatedBy(1L, "lehuuminh2001@gmail.com")).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee("lehuuminh2001@gmail.com", 1L);

        assertTrue(employee.getDeleted());
        verify(userRepository, times(1)).save(employee);
    }

    @Test
    public void testDeleteEmployeeWhenUserIsNotFoundThenThrowException() {
        when(userRepository.findUserByIdAndCreatedBy(1L, "lehuuminh2001@gmail.com")).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> employeeService.deleteEmployee("lehuuminh2001@gmail.com", 1L));

        assertEquals("Không tìm thấy người dùng", exception.getMessage());
        assertEquals(400, exception.getCode());
    }

    @Test
    public void testUpdateEmployeeWhenUserFoundThenEmployeeUpdated() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(1L);
        employeeDTO.setFirstName("Updated John");
        employeeDTO.setLastName("Updated Doe");
        employeeDTO.setEmail("updated.john.doe@example.com");

        when(userRepository.findUserByIdAndCreatedBy(1L, "lehuuminh2001@gmail.com")).thenReturn(Optional.of(employee));
        when(userRepository.save(any(User.class))).thenReturn(null);

        employeeService.updateEmployee("lehuuminh2001@gmail.com", employeeDTO);

        verify(userRepository).findUserByIdAndCreatedBy(1L, "lehuuminh2001@gmail.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testUpdateEmployeeWhenUserNotFoundThenExceptionThrown() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(4L);
        employeeDTO.setFirstName("Updated John");
        employeeDTO.setLastName("Updated Doe");
        employeeDTO.setEmail("updated.john.doe@example.com");

        when(userRepository.findUserByIdAndCreatedBy(4L, "lehuuminh2001@gmail.com")).thenReturn(Optional.empty());

        BaseException exception = assertThrows(
            BaseException.class,
            () -> employeeService.updateEmployee("lehuuminh2001@gmail.com", employeeDTO)
        );

        verify(userRepository).findUserByIdAndCreatedBy(4L, "lehuuminh2001@gmail.com");
        assertEquals("Không tìm thấy người dùng", exception.getMessage());
        assertEquals(400, exception.getCode());
    }

    @Test
    public void testGetEmployeeWhenUserIsFoundThenReturnEmployee() {
        when(userRepository.findUserByIdAndCreatedBy(1L, "lehuuminh2001@gmail.com")).thenReturn(Optional.of(employee));

        Optional<EmployeeDTO> result = employeeService.getEmployee("lehuuminh2001@gmail.com", 1L);

        verify(userRepository).findUserByIdAndCreatedBy(1L, "lehuuminh2001@gmail.com");
        verifyNoMoreInteractions(userRepository);

        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertEquals("john.doe@example.com", result.get().getEmail());
    }

    @Test
    public void testGetEmployeeWhenUserIsNotFoundThenThrowException() {
        when(userRepository.findUserByIdAndCreatedBy(1L, "lehuuminh2001@gmail.com")).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> employeeService.getEmployee("lehuuminh2001@gmail.com", 1L));

        verify(userRepository).findUserByIdAndCreatedBy(1L, "lehuuminh2001@gmail.com");
        verifyNoMoreInteractions(userRepository);

        assertEquals("Không tìm thấy người dùng", exception.getMessage());
        assertEquals(400, exception.getCode());
    }

    @Test
    public void testSearchEmployeeWhenSearchEmployeeDTOIsNullThenThrowNullPointerException() {
        Pageable pageable = PageRequest.of(0, 5);

        assertThrows(NullPointerException.class, () -> employeeService.searchEmployee(null, pageable, "lehuuminh2001@gmail.com"));
    }

    @Test
    public void testSearchEmployeeWhenCreateByIsNullThenThrowNullPointerException() {
        Pageable pageable = PageRequest.of(0, 5);
        SearchEmployeeDTO searchEmployeeDTO = new SearchEmployeeDTO();

        assertThrows(NullPointerException.class, () -> employeeService.searchEmployee(searchEmployeeDTO, pageable, null));
    }

    @Test
    public void testSearchEmployeeWhenPageableIsNullThenThrowNullPointerException() {
        SearchEmployeeDTO searchEmployeeDTO = new SearchEmployeeDTO();

        assertThrows(NullPointerException.class, () -> employeeService.searchEmployee(searchEmployeeDTO, null, "lehuuminh2001@gmail.com"));
    }

    @Test
    public void testSearchEmployeeWithValidSearchEmployeeDTOAndMatchingUsersThenReturnPageOfEmployeeDTOs() {
        Pageable pageable = PageRequest.of(0, 5);
        SearchEmployeeDTO searchEmployeeDTO = new SearchEmployeeDTO();
        Page<User> userPage = new PageImpl<>(Arrays.asList(employee, employee2, employee3), pageable, 3);

        when(userRepository.findAll((Specification<User>) any(), eq(pageable))).thenReturn(userPage);

        Page<EmployeeDTO> result = employeeService.searchEmployee(searchEmployeeDTO, pageable, "lehuuminh2001@gmail.com");

        verify(userRepository).findAll((Specification<User>) any(), eq(pageable));
        verifyNoMoreInteractions(userRepository);

        assertEquals(3, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getFirstName());
        assertEquals("Doe", result.getContent().get(0).getLastName());
        assertEquals("john.doe@example.com", result.getContent().get(0).getEmail());
    }

    @Test
    public void testSearchEmployeeWithValidSearchEmployeeDTOButNoMatchingUsersThenReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 5);
        SearchEmployeeDTO searchEmployeeDTO = new SearchEmployeeDTO();
        Page<User> userPage = Page.empty(pageable);

        when(userRepository.findAll((Specification<User>) any(), eq(pageable))).thenReturn(userPage);

        Page<EmployeeDTO> result = employeeService.searchEmployee(searchEmployeeDTO, pageable, "lehuuminh2001@gmail.com");

        verify(userRepository).findAll((Specification<User>) any(), eq(pageable));
        verifyNoMoreInteractions(userRepository);

        assertEquals(0, result.getTotalElements());
    }
}
