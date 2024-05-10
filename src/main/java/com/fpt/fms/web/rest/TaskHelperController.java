package com.fpt.fms.web.rest;

import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.PlantService;
import com.fpt.fms.service.baseservice.*;
import com.fpt.fms.service.dto.EmployeeDTO;
import com.fpt.fms.service.dto.PlantDTO;
import com.fpt.fms.service.dto.ToolDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/taskhelper")
@PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
public class TaskHelperController {

    private final IPriorityService iPriorityService;

    private final IStatusProcessService iStatusProcessService;
    private final IUserService userService;
    private final IEmployeeService iEmployeeService;
    private final IToolService toolService;
    private final PlantService iPlantService;
    private final IRepeatStatusService iRepeatStatusService;

    public TaskHelperController(
        IPriorityService iPriorityService,
        IStatusProcessService iStatusProcessService,
        IUserService userService,
        IEmployeeService iEmployeeService, IToolService toolService,
        PlantService iPlantService,
        IRepeatStatusService iRepeatStatusService
    ) {
        this.iPriorityService = iPriorityService;
        this.iStatusProcessService = iStatusProcessService;
        this.userService = userService;
        this.iEmployeeService = iEmployeeService;
        this.toolService = toolService;

        this.iPlantService = iPlantService;
        this.iRepeatStatusService = iRepeatStatusService;
    }

    @GetMapping("/priority")
    public ResponseEntity<List<String>> listPriority() {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
        return new ResponseEntity<>(iPriorityService.getListPriority(), HttpStatus.OK);
    }

    @GetMapping("/repeatstatus")
    public ResponseEntity<List<String>> listRepeatStatus() {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
        return new ResponseEntity<>(iRepeatStatusService.getListRepeatStatus(), HttpStatus.OK);
    }

    @GetMapping("/statusprocess")
    public ResponseEntity<List<String>> listStatusProcess() {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
        return new ResponseEntity<>(iStatusProcessService.getListStatusProcess(), HttpStatus.OK);
    }

    @GetMapping("/assigneduser")
    public ResponseEntity<List<EmployeeDTO>> listAssignedUser() {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
        return new ResponseEntity<>(iEmployeeService.getEmployeesWithStatusTrue(getLoginUser()), HttpStatus.OK);
    }

    @GetMapping("/tool")
    public ResponseEntity<List<ToolDTO>> listTool() {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
        return new ResponseEntity<>(toolService.getListTool(), HttpStatus.OK);
    }

    @GetMapping("/plant")
    public ResponseEntity<List<PlantDTO>> listPlant(@RequestParam(required = false) String name) {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
        return new ResponseEntity<>(iPlantService.getListPlant(name), HttpStatus.OK);
    }
    private String getLoginUser() {
        return SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
    }
}
