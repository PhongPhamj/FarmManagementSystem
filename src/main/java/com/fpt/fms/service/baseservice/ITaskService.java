package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.TaskDTO;
import com.fpt.fms.service.search.SearchTaskDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ITaskService {
    List<TaskDTO> getAllTaskOrForUser(String isAll);
    List<TaskDTO> getSearchTask(SearchTaskDTO searchTaskDTO, String isAll);
    void registerTask(TaskDTO taskDto);
    void updateTask(TaskDTO taskDto);
    void changeTaskStatusProcess(TaskDTO taskDto);
    void changeTaskStatusProcessDueDate();
    List<TaskDTO> getListAllTaskOrForUser(String isAll);
    Optional<TaskDTO> getTask(Long id);
    void deleteTask(Long id);
    void deleteTasks(Set<Long> ids);
}
