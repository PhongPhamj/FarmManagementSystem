package com.fpt.fms.service;

import com.fpt.fms.domain.FarmRole;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.repository.spec.EmployeeSpectificationBuilder;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.IEmployeeService;
import com.fpt.fms.service.dto.EmployeeDTO;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final UserRepository userRepository;

    @Override
    public Optional<EmployeeDTO> getEmployee(String createBy, Long userId) {
        return Optional.ofNullable(
            userRepository
                .findUserByIdAndCreatedBy(userId, createBy)
                .map(EmployeeDTO::new)
                .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy người dùng"))
        );
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithStatusTrue(String createdBy) {
        List<User> employees = userRepository.findAllByCreatedByAndActivatedTrue(createdBy);
        return employees.stream().map(EmployeeDTO::new).collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeDTO> getAllEmployee(String createBy, Pageable pageable) {
        return userRepository.findAllByCreatedBy(createBy, FarmRole.EMPLOYEE, pageable).map(EmployeeDTO::new);
    }

    @Override
    public Page<EmployeeDTO> searchEmployee(SearchEmployeeDTO searchEmployeeDTO, Pageable pageable, String createBy) {
        Specification<User> specification = EmployeeSpectificationBuilder.buildQuery(searchEmployeeDTO);
        return userRepository
            .findAll(
                specification
                    .and((root, query, cb) -> cb.equal(root.get("createdBy"), createBy))
                    .and((root, query, cb) -> cb.equal(root.get("farmRole"), FarmRole.EMPLOYEE)),
                pageable
            )
            .map(EmployeeDTO::new);
    }

    @Override
    public void updateEmployee(String createBy, EmployeeDTO employeeDTO) {
        User employee = userRepository
            .findUserByIdAndCreatedBy(employeeDTO.getId(), createBy)
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy người dùng"));
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setFullName(employeeDTO.getFullName());
        employee.setFarmRole(employeeDTO.getFarmrole());
        employee.setActivated(employeeDTO.isActivated());
        employee.setWorkStatus(employeeDTO.isWorkStatus());
        userRepository.save(employee);
    }

    @Override
    public void deleteEmployee(String createBy, Long userId) {
        User employee = userRepository
            .findUserByIdAndCreatedBy(userId, createBy)
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy người dùng"));
        employee.setDeleted(Boolean.TRUE);
        checkUserStatus(employee);
        userRepository.save(employee);
    }

    @Override
    public void deleteEmployees(String createBy, Set<Long> ids) {
        List<User> employees = userRepository.findAllByIdIn(ids);
        employees.forEach(
            employee -> {
                checkUserStatus(employee);
                employee.setDeleted(Boolean.TRUE);
            }
        );
        userRepository.saveAll(employees);
    }

    private static void checkUserStatus(User user) {
        if (user.isActivated()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa người dùng đang hoạt động");
        }
    }

    private User getUser() {
        String curUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
        return userRepository.findOneByLogin(curUser).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
    }
}
