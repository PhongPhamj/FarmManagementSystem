package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.service.baseservice.IPriorityService;
import com.fpt.fms.service.baseservice.IRepeatStatusService;
import com.fpt.fms.service.baseservice.IStatusProcessService;
import com.fpt.fms.service.dto.PlantFormatDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskHelperService implements IPriorityService, IStatusProcessService, IRepeatStatusService {

    @Override
    public List<String> getListPriority() {
        List<String> priorities = new ArrayList<>();
        for (Priority priority : Priority.values()) {
            priorities.add(priority.getValue());
        }
        return priorities;
    }

    @Override
    public List<String> getListStatusProcess() {
        List<String> listStatusProcess = new ArrayList<>();
        for (StatusProcess statusProcess : StatusProcess.values()) {
            listStatusProcess.add(statusProcess.getValue());
        }
        return listStatusProcess;
    }

    @Override
    public List<String> getListRepeatStatus() {
        List<String> listRepeatStatus = new ArrayList<>();
        for (RepeatStatus repeatStatus : RepeatStatus.values()) {
            listRepeatStatus.add(repeatStatus.getValue());
        }
        return listRepeatStatus;
    }
}
