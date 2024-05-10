package com.fpt.fms.service;

import com.fpt.fms.domain.Authority;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.service.dto.UserDTO;
import com.fpt.fms.service.search.SearchUserDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserManagerServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManagerService userManagerService;

    private User user, user1, user2, user3;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCreatedBy("lehuuminh2001@gmail.com");
        user.setEmail("john.doe@example.com");
        user.setDeleted(false);

        user1 = new User();
        user1.setId(2L);
        user1.setFirstName("Alice");
        user1.setLastName("Smith");
        user1.setCreatedBy("lehuuminh2001@gmail.com");
        user1.setEmail("alice.smith@example.com");
        user1.setDeleted(false);

        user2 = new User();
        user2.setId(3L);
        user2.setFirstName("Bob");
        user2.setLastName("Johnson");
        user2.setCreatedBy("lehuuminh2001@gmail.com");
        user2.setEmail("bob.johnson@example.com");
        user2.setDeleted(false);
        user3 = new User();
        user3.setId(4L);
        user3.setFirstName("Roke");
        user3.setLastName("Johnson");
        user3.setCreatedBy("lehuuminh2001@gmail.com");
        user3.setEmail("lehuuminh2001@gmail.com");
        Authority adminAuthority = new Authority();
        adminAuthority.setName(AuthoritiesConstants.ADMIN);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(adminAuthority);
        user3.setAuthorities(authorities);
        user3.setDeleted(false);

    }
    @Test
    public void testSearchUserWhenValidSearchUserDTOAndPageableThenReturnPageOfUserDTO() {
        SearchUserDTO searchUserDTO = new SearchUserDTO();
        searchUserDTO.setFullText("John");
        Pageable pageable = PageRequest.of(0, 1);
        List<User> users = Arrays.asList(user);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll((Specification<User>) any(), eq(pageable))).thenReturn(userPage);

        Page<UserDTO> userDTOPage = userManagerService.searchUser(searchUserDTO, pageable);

        assertEquals(users.size(), userDTOPage.getContent().size());
        assertEquals(userPage.getTotalElements(), userDTOPage.getTotalElements());
        assertEquals(userPage.getNumber(), userDTOPage.getNumber());
        assertEquals(userPage.getSize(), userDTOPage.getSize());
    }

    @Test
    public void testSearchUserWhenNoMatchingUsersThenReturnEmptyPage() {
        SearchUserDTO searchUserDTO = new SearchUserDTO();
        searchUserDTO.setFullText("Alice");
        Pageable pageable = PageRequest.of(0, 1);
        List<User> users = Arrays.asList();
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll((Specification<User>) any(), eq(pageable))).thenReturn(userPage);

        Page<UserDTO> userDTOPage = userManagerService.searchUser(searchUserDTO, pageable);

        assertTrue(userDTOPage.getContent().isEmpty());
        assertEquals(userPage.getTotalElements(), userDTOPage.getTotalElements());
        assertEquals(userPage.getNumber(), userDTOPage.getNumber());
        assertEquals(userPage.getSize(), userDTOPage.getSize());
    }

    @Test
    public void testSearchUserWhenNullSearchUserDTOThenThrowNullPointerException() {
        Pageable pageable = PageRequest.of(0, 1);

        assertThrows(NullPointerException.class, () -> userManagerService.searchUser(null, pageable));
    }

    @Test
    public void testSearchUserWhenCriteriaMatchThenReturnUsers() {
        SearchUserDTO searchUserDTO = new SearchUserDTO();
        searchUserDTO.setFullText("John");
        Pageable pageable = PageRequest.of(0, 1);
        List<User> users = Arrays.asList(user);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll((Specification<User>) any(), eq(pageable))).thenReturn(userPage);

        Page<UserDTO> userDTOPage = userManagerService.searchUser(searchUserDTO, pageable);

        assertEquals(users.size(), userDTOPage.getContent().size());
        assertEquals(userPage.getTotalElements(), userDTOPage.getTotalElements());
        assertEquals(userPage.getNumber(), userDTOPage.getNumber());
        assertEquals(userPage.getSize(), userDTOPage.getSize());
    }

    @Test
    public void testSearchUserWhenCriteriaDoNotMatchThenReturnEmptyPage() {
        SearchUserDTO searchUserDTO = new SearchUserDTO();
        searchUserDTO.setFullText("Alice");
        Pageable pageable = PageRequest.of(0, 1);
        List<User> users = Arrays.asList();
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll((Specification<User>) any(), eq(pageable))).thenReturn(userPage);

        Page<UserDTO> userDTOPage = userManagerService.searchUser(searchUserDTO, pageable);

        assertTrue(userDTOPage.getContent().isEmpty());
        assertEquals(userPage.getTotalElements(), userDTOPage.getTotalElements());
        assertEquals(userPage.getNumber(), userDTOPage.getNumber());
        assertEquals(userPage.getSize(), userDTOPage.getSize());
    }


    // Existing tests...

    @Test
    public void testDeleteUsersWhenUsersFoundAndDeletedThenSuccess() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        List<User> users = Arrays.asList(user, user1, user2);

        when(userRepository.findAllByIdIn(ids)).thenReturn(users);

        userManagerService.deleteUsers(ids);

        users.forEach(user -> assertTrue(user.getDeleted()));
        verify(userRepository, times(1)).saveAll(users);
    }

    @Test
    public void testDeleteUsersWhenUserNotFoundThenBaseException() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        List<User> users = new ArrayList<>();

        when(userRepository.findAllByIdIn(ids)).thenReturn(users);

        assertThrows(BaseException.class, () -> userManagerService.deleteUsers(ids));

        users.forEach(user -> assertFalse(user.getDeleted()));
        verify(userRepository, times(0)).saveAll(users);

    }

    @Test
    public void testDeleteUsersWhenUserIsAdminThenBaseException() {
        Set<Long> ids = new HashSet<>(Arrays.asList(3L));
        List<User> users = Arrays.asList(user3);

        when(userRepository.findAllByIdIn(ids)).thenReturn(users);

        BaseException exception = assertThrows(BaseException.class, () -> userManagerService.deleteUsers(ids));

        assertEquals(400, exception.getCode());
        assertEquals("Không thể xóa người Admin", exception.getMessage());
        assertFalse(user3.getDeleted());
        verify(userRepository, times(0)).saveAll(users);
    }

    @Test
    public void testDeleteUsersWhenUserFoundThenDeleteUser() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        List<User> users = Arrays.asList(user, user1, user2);

        when(userRepository.findAllByIdIn(ids)).thenReturn(users);

        userManagerService.deleteUsers(ids);

        users.forEach(user -> assertTrue(user.getDeleted()));
        verify(userRepository, times(1)).saveAll(users);
    }

    @Test
    public void testDeleteUserWhenUserExistsAndNotActivatedThenDeleteUser() {
        user.setActivated(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userManagerService.deleteUser(1L);

        assertTrue(user.getDeleted());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUserWhenUserExistsAndActivatedThenNotDeleteUser() {
        user.setActivated(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        BaseException exception = assertThrows(BaseException.class, () -> userManagerService.deleteUser(1L));

        assertEquals(400, exception.getCode());
        assertEquals("Không thể xóa người dùng đang hoạt động", exception.getMessage());
        assertFalse(user.getDeleted());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void testDeleteUserWhenUserDoesNotExistThenNotDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        BaseException exception = assertThrows(BaseException.class, () -> userManagerService.deleteUser(1L));
        assertEquals("Không tìm thấy người dùng", exception.getMessage());
        assertEquals(400, exception.getCode());
    }

    @Test
    public void testGetUserWhenUserFoundAndNotDeletedThenReturnUserDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserDTO> userDTOOptional = userManagerService.getUser(1L);

        assertTrue(userDTOOptional.isPresent());
        UserDTO userDTO = userDTOOptional.get();
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getEmail(), userDTO.getEmail());
    }

    @Test
    public void testGetUserWhenUserNotFoundThenThrowBaseException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EmailAlreadyUsedException exception = assertThrows(EmailAlreadyUsedException.class, () -> userManagerService.getUser(1L));
        assertEquals("Không tìm thấy người dùng", exception.getMessage());
    }

    @Test
    public void testGetUserWhenUserFoundButDeletedThenThrowBaseException() {
        user.setDeleted(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        BaseException exception = assertThrows(BaseException.class, () -> userManagerService.getUser(1L));

        assertEquals(400, exception.getCode());
        assertEquals("không thể xác định thông tin người dùng hiện tại", exception.getMessage());
    }

    @Test
    public void testGetUsersWhenUsersExistThenReturnPageOfUserDTOs() {
        List<User> users = Arrays.asList(user, user1, user2);
        Pageable pageable = PageRequest.of(0, 3);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDTO> userDTOPage = userManagerService.getUsers(pageable);

        assertEquals(users.size(), userDTOPage.getContent().size());
        assertEquals(userPage.getTotalElements(), userDTOPage.getTotalElements());
        assertEquals(userPage.getNumber(), userDTOPage.getNumber());
        assertEquals(userPage.getSize(), userDTOPage.getSize());
    }

    @Test
    public void testGetUsersWhenNoUsersThenReturnEmptyPage() {
        List<User> users = Arrays.asList();
        Pageable pageable = PageRequest.of(0, 3);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDTO> userDTOPage = userManagerService.getUsers(pageable);

        assertTrue(userDTOPage.getContent().isEmpty());
        assertEquals(userPage.getTotalElements(), userDTOPage.getTotalElements());
        assertEquals(userPage.getNumber(), userDTOPage.getNumber());
        assertEquals(userPage.getSize(), userDTOPage.getSize());
    }

    @Test
    public void testGetUsersWhenMoreUsersThanPageSizeThenReturnPageWithCorrectSize() {
        List<User> users = Arrays.asList(user, user1, user2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> userPage = new PageImpl<>(users.subList(0, 2), pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDTO> userDTOPage = userManagerService.getUsers(pageable);

        assertEquals(pageable.getPageSize(), userDTOPage.getContent().size());
        assertEquals(userPage.getTotalElements(), userDTOPage.getTotalElements());
        assertEquals(userPage.getNumber(), userDTOPage.getNumber());
        assertEquals(userPage.getSize(), userDTOPage.getSize());
    }

    @Test
    public void testUpdateUserWhenNotAdminThenUpdateUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("UpdatedFirstName");
        userDTO.setLastName("UpdatedLastName");
        userDTO.setEmail("updated.email@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userManagerService.updateUser(userDTO);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUserWhenAdminThenThrowException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(4L);
        userDTO.setFirstName("UpdatedFirstName");
        userDTO.setLastName("UpdatedLastName");
        userDTO.setEmail("updated.email@example.com");

        when(userRepository.findById(4L)).thenReturn(Optional.of(user3));

        BaseException exception = assertThrows(BaseException.class, () -> userManagerService.updateUser(userDTO));

        assertEquals(400, exception.getCode());
        assertEquals("Không thể thay đổi trạng thái", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testUpdateUserWhenUserNotFoundThenThrowException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(5L);
        userDTO.setFirstName("UpdatedFirstName");
        userDTO.setLastName("UpdatedLastName");
        userDTO.setEmail("updated.email@example.com");

        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        EmailAlreadyUsedException exception = assertThrows(EmailAlreadyUsedException.class, () -> userManagerService.updateUser(userDTO));
        assertEquals("Không tìm thấy người dùng", exception.getMessage());
    }

    @Test
    public void testDeleteUsersWhenUserFoundThenDeleted() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        List<User> users = Arrays.asList(user, user1, user2);

        when(userRepository.findAllByIdIn(ids)).thenReturn(users);

        userManagerService.deleteUsers(ids);

        users.forEach(user -> assertTrue(user.getDeleted()));
        verify(userRepository, times(1)).saveAll(users);
    }
}
