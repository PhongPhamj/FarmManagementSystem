package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.*;
import com.fpt.fms.repository.spec.TaskSpectificationBuilder;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.dto.TaskDTO;
import com.fpt.fms.service.search.SearchTaskDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {TaskService.class})
@ExtendWith(SpringExtension.class)
class TaskServiceTest {
    @MockBean
    private ModelMapper modelMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @MockBean
    private PlantRepo plantRepo;

    @MockBean
    private TaskRepository taskRepository;

    @Autowired
    @InjectMocks
    private TaskService taskService;

    @MockBean
    private ToolCategoryRepository toolCategoryRepository;

    @MockBean
    private ToolRepository toolRepository;

    @MockBean
    private UserRepository userRepository;

    @Mock
    private User user, user2;

    @Mock
    private Plant plant;

    @Mock
    private Farm farm;

    @Mock
    private Task task, task1;

    @Mock
    private TaskDTO taskDTO;
    @Mock
    private TaskSpectificationBuilder spectificationBuilder;

    @Mock
    private Authority userAuthority, userAuthority2;

    @BeforeEach
    public void setUp() {
        //Autho - User
        userAuthority2 = new Authority(); // Thay bằng cách tạo từ chuỗi thực tế
        userAuthority2.setName(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(userAuthority2); // Thêm Authority vào Set
        //Autho - Employee
        userAuthority = new Authority(); // Thay bằng cách tạo từ chuỗi thực tế
        userAuthority.setName(AuthoritiesConstants.EMPLOYEE);
        Set<Authority> authoritiesEm = new HashSet<>();
        authoritiesEm.add(userAuthority); // Thêm Authority vào Set
        //Oner
        user2 = new User();
        user2.setActivated(true);
        user2.setAuthorities(authorities);
        user2.setCreatedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setEmail("minh1812001@gmail.com");
        user2.setFarmRole(FarmRole.OWNER);
        user2.setCreatedBy("anonymousUser");
        user2.setFirstName("Le Huu");
        user2.setFullName("Le Huu Minh");
        user2.setId(1L);
        user2.setIdCard("038201012260");
        user2.setImageUrl("https://example.org/example");
        user2.setLastModifiedBy("System");
        user2.setLastModifiedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setLastName("Minh");
        user2.setOwner("Owner");
        user2.setPassword("123456");
        user2.setPhoneNumber("0343383101");
        user2.setDeleted(false);
        //farm
        farm = new Farm();
        farm.setCreatedBy(user2.getEmail());
        farm.setCreatedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        farm.setId(1L);
        farm.setLastModifiedBy("System");
        farm.setLastModifiedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        farm.setName("Test Farm Name");
        farm.setStatus(ApproveStatus.APPROVED);
        farm.setUser(user2); // set user cho farm
        // Employee
        user = new User();
        user.setActivated(true);
        user.setAuthorities(authoritiesEm);
        user.setCreatedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setFarmRole(FarmRole.EMPLOYEE);
        user.setFirstName("Jane");
        user.setFullName("Dr Jane Doe");
        user.setId(2L);
        user.setIdCard("038201012261");
        user.setImageUrl("https://example.org/example");
        user.setCreatedBy(user2.getEmail());
        user.setLastModifiedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setOwnerId(1L);
        user.setPassword("123456");
        user.setPhoneNumber("6625550144");
        user.setDeleted(false);
        ///////////////////////
        plant = new Plant();
        plant.setCreatedBy(user2.getEmail());
        plant.setCreatedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        plant.setDeleted(true);
        plant.setId(1L);
        plant.setLastModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        plant.setLastModifiedDate(LocalDate.of(2021, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        plant.setName("Test Plant");
        plant.setProvider("Test Provider");
        plant.setSource("Test Source");
        plant.setStatus(true);
        plant.setType(PlantCategory.FRUIT);
        plant.setDeleted(false);
        plant.setSource("VN");

        // task
        task = new Task();
        task.setCreatedBy("minh1812001@gmail.com");
        task.setCreatedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        task.setDescription("The characteristics of someone or something");
        Date dueDate = Date.from(LocalDateTime.of(2020, 1, 3, 16, 30).atZone(ZoneOffset.UTC).toInstant());
        task.setDueDate(dueDate);
        task.setId(1L);
        task.setLastModifiedBy(user2.getEmail());
        task.setLastModifiedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        task.setPlant(plant);
        task.setPriority(Priority.MEDIUM);
        task.setRepeat(1L);
        task.setRepeatStatus(RepeatStatus.DAILY);
        task.setRepeatUntil(Date.from(LocalDate.of(2020, 1, 2).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        Date startDate = Date.from(LocalDateTime.of(2020, 1, 1, 7, 30).atZone(ZoneOffset.UTC).toInstant());
        task.setStartDate(startDate);
        task.setStatusProcess(StatusProcess.TODO);
        task.setTitle("Test Task");
        task.setToolList(new ArrayList<>());
        task.setUser(user);
        /////////////////
        task1 = new Task();
        task1.setCreatedBy("minh1812001@gmail.com");
        task1.setCreatedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        task1.setDescription("The characteristics of someone or something");
        Date dueDate1 = Date.from(LocalDateTime.of(2020, 1, 3, 16, 30).atZone(ZoneOffset.UTC).toInstant());
        task1.setDueDate(dueDate1);
        task1.setId(2L);
        task1.setLastModifiedBy(user2.getEmail());
        task1.setLastModifiedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        task1.setPlant(plant);
        task1.setPriority(Priority.HIGHTEST);
        task1.setRepeat(1L);
        task1.setRepeatStatus(RepeatStatus.DAILY);
        task1.setRepeatUntil(Date.from(LocalDate.of(2020, 1, 2).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        Date startDate1 = Date.from(LocalDateTime.of(2020, 1, 1, 7, 30).atZone(ZoneOffset.UTC).toInstant());
        task1.setStartDate(startDate1);
        task1.setStatusProcess(StatusProcess.TODO);
        task1.setTitle("Test Task1");
        task1.setToolList(new ArrayList<>());
        task1.setUser(user);
        ////////////////

        taskDTO = new TaskDTO(); // Thay vì gán bằng new TaskDTO()
        taskDTO.setCreateBy("minh1812001@gmail.com");
        taskDTO.setDescription("The characteristics of someone or something");
        taskDTO.setDueDate(Date.from(LocalDate.of(2020, 1, 3).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setDueTimme(Date.from(LocalDate.of(2020, 1, 3).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setEndHourse(16L);
        taskDTO.setEndMinutes(30L);
        taskDTO.setId(1L);
        taskDTO.setPlantName("Test Plant");
        taskDTO.setPriority(Priority.HIGHTEST);
        taskDTO.setRepeat(1L);
        taskDTO.setRepeatStatus(RepeatStatus.DAILY);
        taskDTO.setRepeatUntil(Date.from(LocalDate.of(2020, 1, 2).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setStartDate(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setStartHourse(7L);
        taskDTO.setStartMinutes(30L);
        taskDTO.setStartTimme(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setStatus(true);
        taskDTO.setStatusProcess(StatusProcess.TODO);
        taskDTO.setTitle("Test Task");
        ArrayList<Long> toolIds = new ArrayList<>();
        taskDTO.setToolIds(toolIds);
        taskDTO.setUser(2L);
    }

    @Test
    public void testGetSearchTaskWhenIsAllIsNullThenReturnMatchingTasks() {
        // Given
        String isAll = null;
        SearchTaskDTO searchTaskDTO = new SearchTaskDTO();
        searchTaskDTO.setTaskName("Test Task");
        List<Task> tasks = Arrays.asList(task, task1);
        List<User> users = Arrays.asList(user, user2);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(plantRepo.findAll()).thenReturn(Collections.singletonList(plant));
        when(userRepository.findAll().stream()
            .filter(
                user ->
                    Objects.nonNull(user.getFarmRole()) &&
                        (Objects.isNull(user.getOwnerId()) || Objects.equals(user.getOwnerId(), user2.getId()))
            )
            .collect(Collectors.toList())).thenReturn(users);
      //  Specification<Task> specification = TaskSpectificationBuilder.buildQuery(searchTaskDTO, users, Collections.singletonList(plant));

        when(taskRepository.findAll((Specification<Task>) any())).thenReturn(Arrays.asList(task,task1));


        // When
        List<TaskDTO> result = taskService.getSearchTask(searchTaskDTO, isAll);

        // Then
        assertEquals(tasks.size(), result.size());
        verify(taskRepository).findAll((Specification<Task>) any());
    }


    @Test
    public void testGetSearchTaskWhenIsAllIsTrue() {
        // Given
        String isAll = "true";

        // Mocking necessary objects and setting up expectations
        SearchTaskDTO searchTaskDTO = new SearchTaskDTO();
        searchTaskDTO.setTaskName("Test Task");
        List<Task> tasks = Arrays.asList(task, task1);
        List<User> users = Arrays.asList(user, user2);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(plantRepo.findAll()).thenReturn(Collections.singletonList(plant));
        when(userRepository.findAll()).thenReturn(users);
      //  Specification<Task> specification = TaskSpectificationBuilder.buildQuery(searchTaskDTO, users, Collections.singletonList(plant));

        when(taskRepository.findAll((Specification<Task>) any()))
            .thenReturn(tasks);

        // When
        List<TaskDTO> result = taskService.getSearchTask(searchTaskDTO, isAll);

        // Then
        assertEquals(tasks.size(), result.size());
        verify(taskRepository).findAll((Specification<Task>) any());
    }


    @Test
    public void testGetSearchTaskWhenIsAllIsFalse() {
        // Given
        String isAll = "false";
        SearchTaskDTO searchTaskDTO = new SearchTaskDTO();
        searchTaskDTO.setTaskName("Test Task");
        List<Task> tasks = Arrays.asList(task, task1);
        List<User> users = Arrays.asList(user, user2);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(plantRepo.findAll()).thenReturn(Collections.singletonList(plant));
        when(userRepository.findAll().stream()
            .filter(
                user ->
                    Objects.nonNull(user.getFarmRole()) &&
                        (Objects.isNull(user.getOwnerId()) || Objects.equals(user.getOwnerId(), user2.getId()))
            )
            .collect(Collectors.toList())).thenReturn(users);
        //  Specification<Task> specification = TaskSpectificationBuilder.buildQuery(searchTaskDTO, users, Collections.singletonList(plant));

        when(taskRepository.findAll((Specification<Task>) any())).thenReturn(Arrays.asList(task,task1));


        // When
        List<TaskDTO> result = taskService.getSearchTask(searchTaskDTO, isAll);

        // Then
        assertEquals(tasks.size(), result.size());
        verify(taskRepository).findAll((Specification<Task>) any());
    }
    ///change Status
    @Test
    public void testChangeTaskStatusProcessWhenTaskExistsThenStatusChanged() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        // Given
        when(taskRepository.findByIdAndCreatedBy(taskDTO.getId(), user2.getEmail())).thenReturn(Optional.of(task));
        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);

        // When
        taskService.changeTaskStatusProcess(taskDTO);

        // Then
        verify(taskRepository).save(task);
        assertEquals(taskDTO.getStatusProcess(), task.getStatusProcess());
    }

    @Test
    public void testChangeTaskStatusProcessWhenTaskDoesNotExistThenExceptionThrown() {
        // Given
        when(taskRepository.findById(taskDTO.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BaseException.class, () -> taskService.changeTaskStatusProcess(taskDTO));
    }
//// get task calender
//    @Test
//    public void testGetAllTaskOrForUserWhenIsAllIsTrueThenReturnAllTasks() {
//        // Given
//        String isAll = "true";
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn(user2.getEmail());
//        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
//        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
//        List<Task> tasks = Arrays.asList(task1,task);
//        when(taskRepository.findByCreatedBy(user2.getCreatedBy())).thenReturn(tasks);
//
//        // When
//        List<TaskDTO> result = taskService.getAllTaskOrForUser(isAll);
//
//        // Then
//        assertEquals(0, result.size());
//        verify(taskRepository).findByCreatedBy(user2.getCreatedBy());
//    }

    @Test
    public void testGetAllTaskOrForUserWhenIsAllIsFalseThenReturnUserTasks() {
        // Given
        String isAll = "false";
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user));
        // List<Task> tasks = Collections.singletonList(task);
        List<Task> tasks = Arrays.asList(task, task1);
        when(taskRepository.findAllByUser(user)).thenReturn(tasks);

        // When
        List<TaskDTO> result = taskService.getAllTaskOrForUser(isAll);

        // Then
        assertEquals(tasks.size(), result.size());
        verify(taskRepository).findAllByUser(user);
    }

    //delete more
    @Test
    public void testDeleteTasksWhenIdsValidThenTasksDeleted() {
        // Given
        user2.setFarmRole(FarmRole.OWNER);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L));
        when(taskRepository.findTaskByIdInAndCreatedBy(ids,user2.getEmail())).thenReturn(Arrays.asList(task, task1));
        task1.setStatusProcess(StatusProcess.DONE);
        task.setStatusProcess(StatusProcess.MISSED);

        // When
        taskService.deleteTasks(ids);

        // Then
        assertTrue(task.getDeleted());
        assertTrue(task1.getDeleted());
        verify(taskRepository).saveAll(Arrays.asList(task, task1));
    }

    @Test
    public void testDeleteTasksWhenUserIsOwnerOrManagerAndTaskStatusIsDoneThenExceptionIsThrown1() {
        // Given
        user2.setFarmRole(FarmRole.OWNER);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L));
        when(taskRepository.findTaskByIdInAndCreatedBy(ids,user2.getEmail())).thenReturn(Arrays.asList(task, task1));
        task.setStatusProcess(StatusProcess.TODO);
        task1.setStatusProcess(StatusProcess.TODO);

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.deleteTasks(ids));

        // Then
        assertEquals("Không thể xóa nhiệm vụ đang hoạt động", exception.getMessage());
    }

    @Test
    public void testDeleteTasksWhenIdsInvalidThenExceptionThrown() {
        // Given
        user2.setFarmRole(FarmRole.EMPLOYEE);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L));

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.deleteTasks(ids));

        // Then
        assertEquals("Người dùng không đủ quyền hạn!", exception.getMessage());
    }

    @Test
    public void testDeleteTasksWhenUserIsOwnerOrManagerAndTaskStatusIsDoneThenExceptionIsThrown() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        Set<Long> ids = new HashSet<>(Arrays.asList(3L, 4L));

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.deleteTasks(ids));

        // Then
        assertEquals("Không tìm thấy nhiệm vụ", exception.getMessage());
    }


    // DELETE 1
    @Test
    public void testDeleteTaskWhenIdIsValidThenDeleteTask() {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        task.setDeleted(false);
        task.setStatusProcess(StatusProcess.DONE);

        // When
        taskService.deleteTask(1L);

        // Then
        assertTrue(task.getDeleted());
        verify(taskRepository).save(task);
    }

    @Test
    public void testDeleteTaskWhenUserIsOwnerOrManagerAndTaskStatusIsDoneThenExceptionIsThrown() {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        task.setStatusProcess(StatusProcess.TODO);

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.deleteTask(1L));

        // Then
        assertEquals("Không thể xóa nhiệm vụ đang hoạt động", exception.getMessage());
    }

    @Test
    public void testDeleteTaskWhenUserIsNotOwnerOrManagerThenExceptionIsThrown() {
        // Given
        user2.setFarmRole(FarmRole.EMPLOYEE);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.deleteTask(1L));

        // Then
        assertEquals("Người dùng không đủ quyền hạn!", exception.getMessage());
    }

    @Test
    public void testDeleteTaskWhenIdDoesNotExistThenExceptionIsThrown() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        // Given
        when(taskRepository.findById(3L)).thenReturn(Optional.empty());

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.deleteTask(3L));

        // Then
        assertEquals(400, exception.getCode());
        assertEquals("Không tìm thấy nhiệm vụ", exception.getMessage());
    }

    ///Register
    @Test
    public void testRegisterTaskWhenUserIsOwnerOrManagerThenTaskIsRegistered() {
        // Given
        user2.setFarmRole(FarmRole.OWNER);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(userRepository.findById(taskDTO.getUser())).thenReturn(Optional.of(user));
        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        when(plantRepo.findByIdAndCreatedBy(plant.getId(),user2.getEmail())).thenReturn(plant);

        // When
        taskService.registerTask(taskDTO);

        // Then
        verify(taskRepository).save(task);
    }

    @Test
    public void testRegisterTaskWhenStartDateOrDueDateIsNullThenBaseExceptionIsThrown() {
        // Given
        taskDTO = new TaskDTO();
        taskDTO.setStartDate(null); // Thiết lập startDate là null
        taskDTO.setDueDate(Date.from(LocalDate.of(2023, 12, 31).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        // When và Then
        BaseException exception = assertThrows(BaseException.class, () -> taskService.registerTask(taskDTO));

        assertEquals("Ngày bắt đầu hoặc kết thúc không được trống", exception.getMessage());
    }

    @Test
    public void testRegisterTaskWhenPlantNotFoundThenBaseExceptionIsThrown() {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        when(userRepository.findById(taskDTO.getUser())).thenReturn(Optional.of(user));
        taskDTO.setPlantId(3l);
        when(plantRepo.findByIdAndCreatedBy(taskDTO.getPlantId(),user2.getEmail())).thenReturn(null);


        // When và Then
        BaseException exception = assertThrows(BaseException.class, () -> taskService.registerTask(taskDTO));

        assertEquals("Loại cây không tồn tại trong trang trại!", exception.getMessage());
    }

    @Test
    public void testRegisterTaskWhenTaskDtoIsNullThenBaseExceptionIsThrown() {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        BaseException exception = assertThrows(BaseException.class, () -> taskService.registerTask(taskDTO));
        assertEquals("Hãy nhập thông tin nhiệm vụ", exception.getMessage());
    }

    @Test
    public void testRegisterTaskWhenUserIsNotOwnerOrManagerThenExceptionIsThrown() {
        // Given
        user2.setFarmRole(FarmRole.EMPLOYEE);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.registerTask(taskDTO));

        // Then
        assertEquals("Người dùng không đủ quyền hạn!", exception.getMessage());
    }

    @Test
    public void testRegisterTaskWhenStartDateIsAfterDueDateThenExceptionIsThrown() {
        // Given
        user2.setFarmRole(FarmRole.OWNER);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(userRepository.findById(taskDTO.getUser())).thenReturn(Optional.of(user));
        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        when(plantRepo.findByIdAndCreatedBy(plant.getId(),user2.getEmail())).thenReturn(plant);
        taskDTO.setStartDate(Date.from(LocalDate.of(2020, 1, 3).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setDueDate(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.registerTask(taskDTO));
        assertEquals("Ngày bắt đầu phải trước ngày kết thúc!", exception.getMessage());
    }

    @Test
    public void testRegisterTaskWhenRepeatDateIsNotWithinStartAndDueDateThenExceptionIsThrown() {
        // Given
        taskDTO.setStartDate(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setDueDate(Date.from(LocalDate.of(2020, 1, 3).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setRepeatUntil(Date.from(LocalDate.of(2020, 1, 4).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        user2.setFarmRole(FarmRole.OWNER);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(userRepository.findById(taskDTO.getUser())).thenReturn(Optional.of(user));

        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        when(plantRepo.findByIdAndCreatedBy(plant.getId(),user2.getEmail())).thenReturn(plant);

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.registerTask(taskDTO));

        // Then
        assertEquals("Ngày lặp phải nằm trong khoảng từ ngày bắt đầu đến ngày kết thúc!", exception.getMessage());
    }

    ///Update
    @Test
    public void testUpdateTaskWhenUserIsOwnerOrManagerThenTaskIsRegistered() {
        // Given
        user2.setFarmRole(FarmRole.OWNER);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(userRepository.findById(taskDTO.getUser())).thenReturn(Optional.of(user));

        when(taskRepository.findById(taskDTO.getId())).thenReturn(Optional.of(task));
        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        when(plantRepo.findByIdAndCreatedBy(plant.getId(),user2.getEmail())).thenReturn(plant);

        // When
        taskService.registerTask(taskDTO);

        // Then
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    public void testUpdateTaskWhenStartDateOrDueDateIsNullThenBaseExceptionIsThrown() {
        // Given
        taskDTO = new TaskDTO();
        taskDTO.setStartDate(null); // Thiết lập startDate là null
        taskDTO.setDueDate(Date.from(LocalDate.of(2023, 12, 31).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(taskRepository.findById(taskDTO.getId())).thenReturn(Optional.of(task));

        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        // When và Then
        BaseException exception = assertThrows(BaseException.class, () -> taskService.registerTask(taskDTO));

        assertEquals("Ngày bắt đầu hoặc kết thúc không được trống", exception.getMessage());
    }

    @Test
    public void testUpdateTaskWhenPlantNotFoundThenBaseExceptionIsThrown() {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(taskRepository.findById(taskDTO.getId())).thenReturn(Optional.of(task));
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(userRepository.findById(taskDTO.getUser())).thenReturn(Optional.of(user));

        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        taskDTO.setPlantId(3l);
        when(plantRepo.findByIdAndCreatedBy(taskDTO.getPlantId(),user2.getEmail())).thenReturn(null);

        // When và Then
        BaseException exception = assertThrows(BaseException.class, () -> taskService.registerTask(taskDTO));

        assertEquals("Loại cây không tồn tại trong trang trại!", exception.getMessage());
    }

    @Test
    public void testUpdateTaskWhenTaskDtoIsNullThenBaseExceptionIsThrown() {
        // Given
        taskDTO.setId(3L);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));

        BaseException exception = assertThrows(BaseException.class, () -> taskService.updateTask(taskDTO));
        assertEquals("Không tìm thấy thông tin nhiệm vụ", exception.getMessage());
    }

    @Test
    public void testUpdateTaskWhenUserIsNotOwnerOrManagerThenExceptionIsThrown() {
        // Given
        user2.setFarmRole(FarmRole.EMPLOYEE);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.updateTask(taskDTO));

        // Then
        assertEquals("Người dùng không đủ quyền hạn!", exception.getMessage());
    }

    @Test
    public void testUpdateTaskWhenStartDateIsAfterDueDateThenExceptionIsThrown() {
        // Given
        user2.setFarmRole(FarmRole.OWNER);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(userRepository.findById(taskDTO.getUser())).thenReturn(Optional.of(user));

        when(taskRepository.findByIdAndCreatedBy(taskDTO.getId(), user2.getEmail())).thenReturn(Optional.of(task));
        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        when(plantRepo.findByIdAndCreatedBy(plant.getId(),user2.getEmail())).thenReturn(plant);
        taskDTO.setStartDate(Date.from(LocalDate.of(2020, 1, 3).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setDueDate(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.updateTask(taskDTO));

        // Then
        assertEquals("Ngày bắt đầu phải trước ngày kết thúc!", exception.getMessage());
    }

    @Test
    public void testUpdateTaskWhenRepeatDateIsNotWithinStartAndDueDateThenExceptionIsThrown() {
        // Given
        taskDTO.setStartDate(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setDueDate(Date.from(LocalDate.of(2020, 1, 3).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        taskDTO.setRepeatUntil(Date.from(LocalDate.of(2020, 1, 4).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        user2.setFarmRole(FarmRole.OWNER);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(taskRepository.findByIdAndCreatedBy(taskDTO.getId(), user2.getEmail())).thenReturn(Optional.of(task));
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(userRepository.findById(taskDTO.getUser())).thenReturn(Optional.of(user));

        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        when(plantRepo.findByIdAndCreatedBy(plant.getId(),user2.getEmail())).thenReturn(plant);


        // When
        BaseException exception = assertThrows(BaseException.class, () -> taskService.updateTask(taskDTO));

        // Then
        assertEquals("Ngày lặp phải nằm trong khoảng từ ngày bắt đầu đến ngày kết thúc!", exception.getMessage());
    }

    //Get
//    @Test
//    public void testGetTaskWhenIdIsInvalidThenReturnEmpty() {
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn(user2.getEmail());
//        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
//        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
//        // Given
//        when(taskRepository.findByIdAndCreatedBy(3L, user2.getEmail())).thenReturn(Optional.of(task));
//
//        // When
//        BaseException exception = assertThrows(BaseException.class, () -> taskService.getTask(3L));
//
//        // Then
//        assertEquals(400, exception.getCode());
//        assertEquals("Không tìm thấy thông tin nhiệm vụ", exception.getMessage());
//    }

    @Test
    public void testGetTaskWhenIdIsValidThenReturnsCorrectTaskDTO() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        // Given
        when(taskRepository.findByIdAndCreatedBy(1L, user2.getEmail())).thenReturn(Optional.of(task));

        // When
        Optional<TaskDTO> result = taskService.getTask(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(taskDTO.getTitle(), result.get().getTitle());
    }

}
