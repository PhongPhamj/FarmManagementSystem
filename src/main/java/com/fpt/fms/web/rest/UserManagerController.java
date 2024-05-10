package com.fpt.fms.web.rest;


import com.fpt.fms.config.Constants;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.IUserService;
import com.fpt.fms.service.dto.*;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import com.fpt.fms.service.search.SearchUserDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/api/usermanager")
@PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.USER + "\")")
public class UserManagerController {
    @Value("fms")
    private String applicationName;


    private final IUserService userManagerService;


    public UserManagerController(IUserService userManagerService) {
        this.userManagerService = userManagerService;
    }


    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsers(
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<UserDTO> userDTOPage = userManagerService.getUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), userDTOPage);
        return new ResponseEntity<>(userDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "userId") Long userId) {
        return ResponseUtil.wrapOrNotFound(userManagerService.getUser(userId));
    }

    @PatchMapping("/users")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UserDTO userDTO) {
        userManagerService.updateUser(userDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }

    @PostMapping("/users/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersSearch(
        @RequestBody(required = false) SearchUserDTO searchUserDTO,
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<UserDTO> userDTOPage = userManagerService.searchUser(searchUserDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), userDTOPage);
        return new ResponseEntity<>(userDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<EmployeeDTO> deleteUser(@PathVariable("id") Long id) {
        userManagerService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).headers(HeaderUtil.createAlert(applicationName, "Xóa nhân viên thành công", applicationName)).build();
    }

    @DeleteMapping()
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeDTO> deleteUsers(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays.stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim).filter(NumberUtils::isCreatable).map(Long::valueOf).collect(Collectors.toSet());
        userManagerService.deleteUsers(ids);
        return ResponseEntity.status(HttpStatus.OK).headers(HeaderUtil.createAlert(applicationName, "Xóa nhân viên thành công", applicationName)).build();
    }

    private String getLoginUser() {
        return SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
    }
}
