package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.*;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import com.fpt.fms.service.search.SearchUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IUserService {

    Optional<UserDTO> getUser(Long userId);

    Page<UserDTO> getUsers(Pageable pageable);

    void updateUser(UserDTO userDTO);

    Page<UserDTO> searchUser(SearchUserDTO searchUserDTO, Pageable pageable);


    void deleteUser(Long id);

    void deleteUsers(Set<Long> ids);
}
