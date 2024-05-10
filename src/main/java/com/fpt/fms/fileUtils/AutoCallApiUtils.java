package com.fpt.fms.fileUtils;

import com.fpt.fms.service.TaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoCallApiUtils {
    private final TaskService taskService;

    public AutoCallApiUtils(TaskService taskService) {
        this.taskService = taskService;
    }

    // Thiết lập lịch trình tự động gọi API
    @Scheduled(cron = "0 00 0 * * ?")
    public void autoChangeStatus() {
        taskService.changeTaskStatusProcessDueDate();
    }
}
