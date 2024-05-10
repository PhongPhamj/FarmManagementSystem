package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.EmployeeDTO;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IEmployeeService {
    Optional<EmployeeDTO> getEmployee(String createBy, Long userId);

    List<EmployeeDTO> getEmployeesWithStatusTrue(String createdBy);

    Page<EmployeeDTO> searchEmployee(SearchEmployeeDTO searchEmployeeDTO, Pageable pageable, String createBy);

    void updateEmployee(String createBy, EmployeeDTO employeeDTO);

    void deleteEmployee(String createBy, Long userId);

    void deleteEmployees(String createBy, Set<Long> ids);

    Page<EmployeeDTO> getAllEmployee(String createBy,Pageable pageable);

}
