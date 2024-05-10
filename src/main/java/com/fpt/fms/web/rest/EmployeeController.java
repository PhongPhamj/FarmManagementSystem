package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.domain.User;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.EmployeeService;
import com.fpt.fms.service.UserService;
import com.fpt.fms.service.baseservice.IEmployeeService;
import com.fpt.fms.service.dto.EmployeeDTO;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
public class EmployeeController {

    @Value("fms")
    private String applicationName;

    private final EmployeeService employeeService;

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<EmployeeDTO>> getEmployees(@PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER,
        size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable) {
        Page<EmployeeDTO> employeeDTOPage = employeeService.getAllEmployee(getLoginUser(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), employeeDTOPage);
        return new ResponseEntity<>(employeeDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable(name = "id") Long id) {
        return ResponseUtil.wrapOrNotFound(employeeService.getEmployee(getLoginUser(), id));
    }

    @PostMapping("/search")
    public ResponseEntity<List<EmployeeDTO>> getEmployeeSearch(@RequestBody(required = false) SearchEmployeeDTO searchEmployeeDTO,
                                                               @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable) {
        Page<EmployeeDTO> employeeDTOPage = employeeService.searchEmployee(searchEmployeeDTO, pageable, getLoginUser());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), employeeDTOPage);
        return new ResponseEntity<>(employeeDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<Void> updateEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        employeeService.updateEmployee(getLoginUser(), employeeDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmployeeDTO> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(getLoginUser(),id);
        return ResponseEntity.status(HttpStatus.OK).headers(HeaderUtil.createAlert(applicationName, "Xóa nhân viên thành công", applicationName)).build();
    }

    @DeleteMapping()
    public ResponseEntity<EmployeeDTO> deleteEmployees(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays.stream(idInString.split(Constants.SYMBOL_COMMA)).map(String::trim).filter(NumberUtils::isCreatable).map(Long::valueOf).collect(Collectors.toSet());
        employeeService.deleteEmployees(getLoginUser(),ids);
        return ResponseEntity.status(HttpStatus.OK).headers(HeaderUtil.createAlert(applicationName, "Xóa nhân viên thành công", applicationName)).build();
    }

    private String getLoginUser() {
        return SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
    }
}
