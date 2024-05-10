package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.service.baseservice.ITaskService;
import com.fpt.fms.service.dto.*;
import com.fpt.fms.service.search.SearchTaskDTO;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Value("fms")
    private String applicationName;

    private final ITaskService taskServie;

    public TaskController(ITaskService taskServie) {
        this.taskServie = taskServie;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    @PostMapping("/calender")
    public ResponseEntity<List<TaskDTO>> getListOfTasksCalender(@RequestParam(required = false) String isAll) {
        List<TaskDTO> taskDTOList = taskServie.getAllTaskOrForUser(isAll);
        return new ResponseEntity<>(taskDTOList, HttpStatus.OK);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<List<TaskDTO>> getSearchTasks(
        @RequestBody(required = false) SearchTaskDTO searchDTO,
        @RequestParam(required = false) String isAll
    ) {
        List<TaskDTO> taskDTOList = taskServie.getSearchTask(searchDTO, isAll);
        return new ResponseEntity<>(taskDTOList, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    @PostMapping("list")
    public ResponseEntity<List<TaskDTO>> getListOfTasks(@RequestParam(required = false) String isAll) {
        List<TaskDTO> taskDTOList = taskServie.getListAllTaskOrForUser(isAll);
        return new ResponseEntity<>(taskDTOList, HttpStatus.OK);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<String> registerTask(@Valid @RequestBody TaskDTO taskDTO) {
        taskServie.registerTask(taskDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Đăng kí nhiệm vụ thành công!");
    }

    @PatchMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Void> updateTask(@Valid @RequestBody TaskDTO TaskDTO) {
        taskServie.updateTask(TaskDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }

    @PatchMapping("/changestatus")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<String> changeStatusTask(@Valid @RequestBody TaskDTO taskDTO) {
        taskServie.changeTaskStatusProcess(taskDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Cập nhật trạng thái thành công!");
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable(name = "taskId") Long taskId) {
        return ResponseUtil.wrapOrNotFound(taskServie.getTask(taskId));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deleteTask(@PathVariable(name = "taskId") Long taskId) {
        taskServie.deleteTask(taskId);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deleteTasks(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        taskServie.deleteTasks(ids);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }
}
