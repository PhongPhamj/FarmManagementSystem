package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.*;
import com.fpt.fms.repository.spec.TaskSpectificationBuilder;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.ITaskService;
import com.fpt.fms.service.dto.TaskDTO;
import com.fpt.fms.service.search.SearchTaskDTO;
import com.fpt.fms.web.rest.errors.BaseException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService implements ITaskService {

    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final ToolRepository toolRepository;
    private final PlantRepo platRepo;

    private final UserRepository userRepository;

    public TaskService(ModelMapper modelMapper, TaskRepository taskRepository, ToolRepository toolRepository, ToolCategoryRepository toolCategoryRepository, PlantRepo platRepo, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.taskRepository = taskRepository;
        this.toolRepository = toolRepository;
        this.platRepo = platRepo;
        this.userRepository = userRepository;
    }

    @Override
    public List<TaskDTO> getAllTaskOrForUser(String isAll) {
        boolean showAllTasks = false;
        if (isAll != null && !isAll.trim().isEmpty()) {
            showAllTasks = Boolean.parseBoolean(isAll);
        }

        List<Task> taskList;

        if (showAllTasks) {
            taskList = taskRepository.findAllByUserIn(getUsers());
        } else {
            taskList = taskRepository.findAllByUser(getUser());

        }

        return taskList.stream().map(taskDTO -> {
            TaskDTO task = new TaskDTO();

            task.setId(taskDTO.getId());
            task.setTitle(taskDTO.getTitle());
            task.setUser(taskDTO.getUser().getId());
            task.setUserName(taskDTO.getUser().getFullName());
            task.setStartDate(taskDTO.getStartDate());
            task.setDueDate(taskDTO.getDueDate());
            task.setStartTimme(taskDTO.getStartDate());
            task.setDueTimme(taskDTO.getDueDate());

            return task;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getSearchTask(SearchTaskDTO searchTaskDTO, String isAll) {
        boolean showAllTasks = false;
        if (isAll != null && !isAll.trim().isEmpty()) {
            showAllTasks = Boolean.parseBoolean(isAll);
        }
        Specification<Task> specification = TaskSpectificationBuilder.buildQuery(searchTaskDTO, getUsers(), getPlants());
        List<Task> taskList;
        if (showAllTasks) {
            taskList = taskRepository.findAll(specification.and((root, query, cb) -> cb.equal(root.get("createdBy"), getCur())));

        } else {
            taskList = taskRepository.findAll(specification.and((root, query, cb) -> cb.equal(root.get("user"), getUser())));
        }

        // Loại bỏ bản ghi trùng lặp dựa trên ID của Task

        return taskList.stream().distinct().map(taskDTO -> {
            TaskDTO task = new TaskDTO();
            task.setId(taskDTO.getId());
            task.setTitle(taskDTO.getTitle());
            task.setUser(taskDTO.getUser().getId());

            task.setUserName(taskDTO.getUser().getFullName());
            task.setDueDate(taskDTO.getDueDate());
            task.setCreateBy(taskDTO.getCreatedBy());
            task.setPriority(taskDTO.getPriority());
            task.setStatusProcess(taskDTO.getStatusProcess());

            return task;
        }).collect(Collectors.toList());
    }

    @Override
    public void registerTask(TaskDTO taskDto) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            Task taskDTO = modelMapper.map(taskDto, Task.class);
            if (taskDTO == null) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Hãy nhập thông tin nhiệm vụ");
            }
            taskDTO.setToolList(findToolById(taskDto.getToolIds()));

            if (taskDto.getUser() == null) {
                taskDTO.setUser(getUser());
            } else {
                taskDTO.setUser(userRepository.findById(taskDto.getUser()).get());
            }
            if (Objects.nonNull(taskDto.getPlantId())) {
                Plant a = findPlantById(taskDto.getPlantId());
                if (a == null) {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Loại cây không tồn tại trong trang trại!");
                }
                taskDTO.setPlant(a);
            }
            if (taskDto.getStartDate() == null || taskDto.getDueDate() == null) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Ngày bắt đầu hoặc kết thúc không được trống");
            }

            taskDTO.setStartDate(convertDate(taskDto.getStartDate(), taskDto.getStartHourse(), taskDto.getStartMinutes()));
            taskDTO.setDueDate(convertDate(taskDto.getDueDate(), taskDto.getEndHourse(), taskDto.getEndMinutes()));
            LocalDate startDate = convertDateToLocalDate(taskDTO.getStartDate());
            LocalDate dueDate = convertDateToLocalDate(taskDTO.getDueDate());
            if (startDate.isAfter(dueDate)) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Ngày bắt đầu phải trước ngày kết thúc!");
            }
            if (!taskDto.getRepeatStatus().equals(RepeatStatus.NOTREPEAT)) {
                if (taskDto.getRepeatUntil() == null) {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Ngày lặp không được trống");
                }
                taskDTO.setRepeatUntil(taskDto.getRepeatUntil());
                taskDTO.setRepeat(taskDto.getRepeat());
                LocalDate repeatDate = convertDateToLocalDate(taskDTO.getRepeatUntil());
                if (repeatDate.isBefore(startDate) || repeatDate.isAfter(dueDate)) {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Ngày lặp phải nằm trong khoảng từ ngày bắt đầu đến ngày kết thúc!");
                }
            } else {
                taskDTO.setRepeatUntil(null);
                taskDTO.setRepeat(null);
            }
            taskRepository.save(taskDTO);
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }
    }

    @Override
    public void updateTask(TaskDTO taskDto) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            Task existingTask = findTaskById(taskDto.getId()).get();
            modelMapper.map(taskDto, existingTask);
            existingTask.setToolList(findToolById(taskDto.getToolIds()));
            if (taskDto.getUser() == null) {
                existingTask.setUser(getUser());
            } else {
                existingTask.setUser(userRepository.findById(taskDto.getUser()).get());
            }
            if (Objects.nonNull(taskDto.getPlantId())) {
                Plant a = findPlantById(taskDto.getPlantId());
                if (a == null) {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Loại cây không tồn tại trong trang trại!");
                }
                existingTask.setPlant(a);
            }
            if (taskDto.getStartDate() == null || taskDto.getDueDate() == null) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Ngày bắt đầu hoặc kết thúc không được trống");
            }
            existingTask.setStartDate(convertDate(taskDto.getStartDate(), taskDto.getStartHourse(), taskDto.getStartMinutes()));
            existingTask.setDueDate(convertDate(taskDto.getDueDate(), taskDto.getEndHourse(), taskDto.getEndMinutes()));
            LocalDate startDate = convertDateToLocalDate(existingTask.getStartDate());
            LocalDate dueDate = convertDateToLocalDate(existingTask.getDueDate());
            if (startDate.isAfter(dueDate)) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Ngày bắt đầu phải trước ngày kết thúc!");
            }
            if (!taskDto.getRepeatStatus().equals(RepeatStatus.NOTREPEAT)) {
                if (taskDto.getRepeatUntil() == null) {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Ngày lặp không được trống");
                }
                existingTask.setRepeatUntil(taskDto.getRepeatUntil());
                existingTask.setRepeat(taskDto.getRepeat());
                LocalDate repeatDate = convertDateToLocalDate(existingTask.getRepeatUntil());
                if (repeatDate.isBefore(startDate) || repeatDate.isAfter(dueDate)) {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Ngày lặp phải nằm trong khoảng từ ngày bắt đầu đến ngày kết thúc!");
                }
            } else {
                existingTask.setRepeatUntil(null);
                existingTask.setRepeat(null);
            }

            taskRepository.save(existingTask);
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }
    }

    @Override
    public void changeTaskStatusProcess(TaskDTO taskDto) {
        Task taskDTO = findTaskById(taskDto.getId()).get();
        taskDTO.setStatusProcess(taskDto.getStatusProcess());
        taskRepository.save(taskDTO);
    }

    @Override
    @Transactional
    public void changeTaskStatusProcessDueDate() {
        List<Task> listTask = taskRepository.findAll();
        LocalDate currentDateTime = LocalDate.now();
        for (Task task : listTask) {
            LocalDate dueDate = convertDateToLocalDate(task.getDueDate());
            if (dueDate.isBefore(currentDateTime) && task.getStatusProcess().equals(StatusProcess.TODO)) {
                task.setStatusProcess(StatusProcess.MISSED);
            }
        }
        taskRepository.saveAll(listTask);
    }

    @Override
    public List<TaskDTO> getListAllTaskOrForUser(String isAll) {
        boolean showAllTasks = false;
        if (isAll != null && !isAll.trim().isEmpty()) {
            showAllTasks = Boolean.parseBoolean(isAll);
        }

        List<Task> taskList;

        if (showAllTasks) {
            taskList = taskRepository.findAllByUserIn(getUsers());
        } else {
            taskList = taskRepository.findAllByUser(getUser());
        }
        return taskList.stream().distinct().map(taskDTO -> {
            TaskDTO task = new TaskDTO();
            task.setId(taskDTO.getId());
            task.setTitle(taskDTO.getTitle());
            task.setUser(taskDTO.getUser().getId());

            task.setUserName(taskDTO.getUser().getFullName());
            task.setDueDate(taskDTO.getDueDate());
            task.setCreateBy(taskDTO.getCreatedBy());
            task.setPriority(taskDTO.getPriority());
            task.setStatusProcess(taskDTO.getStatusProcess());

            return task;
        }).collect(Collectors.toList());
    }

    private List<User> getUsers() {
        return userRepository.findAllByCreatedByOrId(getCur(), findUserByEmail().getId());

    }

    private List<Plant> getPlants() {
        return platRepo.findAll();
    }

    @Override
    public Optional<TaskDTO> getTask(Long id) {
        return findTaskById(id).map(task -> {
            ZoneId yourDesiredZoneId = ZoneId.of("+0"); // Replace "YourDesiredZone" with your desired zone
            Hibernate.initialize(task.getToolList());
            TaskDTO taskDTO = new TaskDTO();
            taskDTO.setTitle(task.getTitle());
            taskDTO.setStatusProcess(task.getStatusProcess());
            taskDTO.setRepeatStatus(task.getRepeatStatus());
            taskDTO.setPriority(task.getPriority());
            taskDTO.setUser(task.getUser().getId());
            taskDTO.setDescription(task.getDescription());
            taskDTO.setUserName(task.getUser().getFullName());
            Instant startInstant = task.getStartDate().toInstant();
            ZonedDateTime startDateTime = startInstant.atZone(ZoneId.systemDefault());
            ZonedDateTime startInTargetZone = startDateTime.withZoneSameInstant(yourDesiredZoneId);
            taskDTO.setStartHourse((long) startInTargetZone.getHour());
            taskDTO.setStartMinutes((long) startInTargetZone.getMinute());

            // Chuyển đổi thời gian kết thúc (dueDate)
            Instant endInstant = task.getDueDate().toInstant();
            ZonedDateTime endDateTime = endInstant.atZone(ZoneId.systemDefault());
            ZonedDateTime endInTargetZone = endDateTime.withZoneSameInstant(yourDesiredZoneId);
            taskDTO.setEndHourse((long) endInTargetZone.getHour());
            taskDTO.setEndMinutes((long) endInTargetZone.getMinute());
            taskDTO.setStartDate(task.getStartDate());
            taskDTO.setDueDate(task.getDueDate());
            List<Long> toolIds = task.getToolList().stream().map(Tool::getId).collect(Collectors.toList());
            taskDTO.setToolIds(toolIds);
            taskDTO.setPlantName(task.getPlant().getName() + " - " + task.getPlant().getSource());
            taskDTO.setRepeat(task.getRepeat());
            taskDTO.setRepeatUntil(task.getRepeatUntil());
            taskDTO.setRepeatStatus(task.getRepeatStatus());
            return taskDTO;
        });
    }

    @Override
    public void deleteTask(Long id) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            Task task = taskRepository.findById(id).orElseThrow(() -> new BaseException(400, "Không tìm thấy nhiệm vụ"));
            checkTaskStatus(task);
            task.setDeleted(true);
            taskRepository.save(task);
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }
    }

    @Override
    public void deleteTasks(Set<Long> ids) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            List<Task> tasks = taskRepository.findTaskByIdInAndCreatedBy(ids, getCur());
            if (tasks.isEmpty()) {
                throw new BaseException(400, "Không tìm thấy nhiệm vụ");
            }
            tasks.forEach(task -> {
                checkTaskStatus(task);
                task.setDeleted(Boolean.TRUE);
            });
            taskRepository.saveAll(tasks);
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }
    }

    public User getUser() {
        String currentUser = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUser != null) {
            Optional<User> userOptional = userRepository.findOneByLogin(currentUser);
            if (userOptional.isPresent()) {
                return userOptional.get();
            } else {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng chưa xác thực");
            }
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng chưa xác thực");
        }
    }

    public User findUserByEmail() {
        return userRepository.findOneByLogin(getCur()).get();
    }

    private List<Tool> findToolById(List<Long> ids) {
        return toolRepository.findAllById(ids);
    }

    private static void checkTaskStatus(Task task) {
        if (task.getStatusProcess().equals(StatusProcess.TODO)) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa nhiệm vụ đang hoạt động");
        }
    }

    private Optional<Task> findTaskById(Long id) {
        return Optional.ofNullable(taskRepository.findByIdAndCreatedBy(id, getCur()).orElseThrow(() -> new BaseException(400, "Không tìm thấy thông tin nhiệm vụ")));
    }

    private Plant findPlantById(Long name) {
        return platRepo.findByIdAndCreatedBy(name, getCur());
    }

    private Plant findPlantByName(String name) {
        return platRepo.findByNameAndCreatedBy(name, getCur());
    }

    private User findUserInFarm(String name) {
        return userRepository.findByFullNameAndCreatedBy(name, getCur());
    }

    private Date convertDate(Date a, Long b, Long c) {
        if (b != null && c != null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("+0"));
            calendar.setTime(a);

            // Đặt giờ và phút từ b và c
            calendar.set(Calendar.HOUR_OF_DAY, b.intValue());
            calendar.set(Calendar.MINUTE, c.intValue());

            return calendar.getTime();
        }
        return null;
    }

    private String getCur() {
        String createBy = getUser().getCreatedBy();
        if (createBy.equals("anonymousUser")) {
            createBy = getUser().getEmail();
        } else {
            createBy = getUser().getCreatedBy();
        }
        return createBy;
    }

    private LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
