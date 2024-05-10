package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.repository.spec.EmployeeSpectificationBuilder;
import com.fpt.fms.repository.spec.UserSpectificationBuilder;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.IUserService;
import com.fpt.fms.service.dto.*;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import com.fpt.fms.service.search.SearchUserDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.util.*;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagerService implements IUserService {

    private final UserRepository userRepository;

    public UserManagerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserDTO> getUser(Long userId) {
        User user = getUserById(userId);
        if (user != null && !user.getDeleted()) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setFullName(user.getFullName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setIdCard(user.getIdCard());

            // Check if authorities is not null before processing
            if (user.getAuthorities() != null) {
                // Assuming getAuthorities returns a Set<Authority> and each Authority has a getName method
                Set<String> authorityNames = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());

                // Convert authority names to a comma-separated string
                String authorities = String.join(",", authorityNames);
                userDTO.setAuthorities(authorities);
            }

            userDTO.setActivated(user.isActivated());
            // Map other properties of interest here.
            return Optional.of(userDTO);
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại");
        }
    }

    @Override
    @Transactional
    public Page<UserDTO> getUsers(Pageable pageable) {
        Page<User> userDTOPage = userRepository.findAll(pageable);
        return getUserDTOS(pageable, userDTOPage);
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        User user = getUserById(userDTO.getId());
        if (user.getAuthorities().stream().anyMatch(a -> a.getName().equals(AuthoritiesConstants.ADMIN))) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể thay đổi trạng thái");
        } else {
            // not admin, apply changes
            user.setFirstName(userDTO.getFirstName() != null ? userDTO.getFirstName() : user.getFirstName());
            user.setLastName(userDTO.getLastName() != null ? userDTO.getLastName() : user.getLastName());
            user.setFullName(userDTO.getFullName() != null ? userDTO.getFullName() : user.getFirstName() + " " + user.getLastName());
            user.setActivated(userDTO.isActivated());
            user.setWorkStatus(userDTO.isWorkStatus());
            userRepository.save(user);
        }
    }

    @Override
    public Page<UserDTO> searchUser(SearchUserDTO searchUserDTO, Pageable pageable) {
        Specification<User> specification = UserSpectificationBuilder.buildQuery(searchUserDTO);
        Page<User> employeeDTOPage = userRepository.findAll(specification, pageable);
        return getUserDTOS(pageable, employeeDTOPage);
    }

    @NotNull
    private Page<UserDTO> getUserDTOS(Pageable pageable, Page<User> employeeDTOPage) {
        List<UserDTO> employeeList = employeeDTOPage
            .getContent()
            .stream()
            .filter(emp -> Objects.nonNull(emp.getFarmRole()))
            .map(this::mapUserToUserDTO)
            .collect(Collectors.toList());
        return new PageImpl<>(employeeList, pageable, employeeDTOPage.getTotalElements());
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy người dùng"));
        if (user.getAuthorities().stream().anyMatch(authority -> authority.getName().equals(AuthoritiesConstants.ADMIN))) {
            throw new BaseException(400, "Không thể xóa người Admin");
        }
        checkUserStatus(user);
        user.setDeleted(Boolean.TRUE);
        userRepository.save(user);
    }

    public void deleteUsers(Set<Long> ids) {
        List<User> users = userRepository.findAllByIdIn(ids);
        if (users.isEmpty()) {
            throw new BaseException(400, "Không tìm thấy người dùng");
        }
        for (User user : users) {
            if (user.getAuthorities().stream().anyMatch(authority -> authority.getName().equals(AuthoritiesConstants.ADMIN))) {
                throw new BaseException(400, "Không thể xóa người Admin");
            }
            checkUserStatus(user);
            user.setDeleted(true);
        }

        userRepository.saveAll(users);
    }

    private static void checkUserStatus(User user) {
        if (user.isActivated()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa người dùng đang hoạt động");
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
    }

    public UserDTO mapUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setCreatedDate(Date.from(user.getCreatedDate()));
        userDTO.setEmail(user.getEmail());

        // Extract role names from authorities and concatenate into a single string
        String authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.joining(","));

        userDTO.setAuthorities(authorities);
        userDTO.setActivated(user.isActivated());

        String byRole;
        if (authorities.contains("ROLE_USER")) {
            byRole = "ADMIN";
        } else if (authorities.contains("ROLE_EMPLOYEE")) {
            byRole = "OWNER";
        } else {
            byRole = "OTHER";
        }
        userDTO.setCreateByRole(byRole);

        return userDTO;
    }

    private Set<Authority> convertAuthorityNamesToEntities(String authorities) {
        // Split the authorities string into a list of authority names
        List<String> authorityNames = Arrays.asList(authorities.split(","));

        // Convert authority names to Authority objects
        return authorityNames
            .stream()
            .map(
                name -> {
                    Authority authority = new Authority();
                    authority.setName(name.trim());
                    return authority;
                }
            )
            .collect(Collectors.toSet());
    }
}
