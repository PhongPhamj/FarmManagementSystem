package com.fpt.fms.web.rest;

import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.IApproveStatusService;
import com.fpt.fms.service.baseservice.IAuthorityService;
import com.fpt.fms.service.baseservice.IFarrmRoleService;
import com.fpt.fms.service.baseservice.IPlantFormatService;
import com.fpt.fms.service.dto.ApproveStatusDTO;
import com.fpt.fms.service.dto.AuthorityDTO;
import com.fpt.fms.service.dto.FarmRoleDTO;
import com.fpt.fms.service.dto.PlantFormatDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final IAuthorityService iAuthorityService;
    private final IFarrmRoleService iFarrmRoleService;
    private final IPlantFormatService iPlantFormatService;
    private final IApproveStatusService iApproveStatusService;

    public RoleController(
        IAuthorityService iAuthorityService,
        IFarrmRoleService iFarrmRoleService,
        IPlantFormatService iPlantFormatService,
        IApproveStatusService iApproveStatusService
    ) {
        this.iAuthorityService = iAuthorityService;
        this.iFarrmRoleService = iFarrmRoleService;
        this.iPlantFormatService = iPlantFormatService;
        this.iApproveStatusService = iApproveStatusService;
    }

    @GetMapping("/authority")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AuthorityDTO>> listAuthority() {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại"));
        return new ResponseEntity<>(iAuthorityService.listAuthority(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/farmrole")
    public ResponseEntity<List<FarmRoleDTO>> listFarmRoles() {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(
                () -> {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại");
                }
            );
        List<FarmRoleDTO> farmRoleDTOs = iFarrmRoleService.mapFarmRolesToDTO();

        return new ResponseEntity<>(farmRoleDTOs, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/plantformat")
    public ResponseEntity<List<PlantFormatDTO>> listPlantFormat() {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(
                () -> {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại");
                }
            );
        List<PlantFormatDTO> farmRoleDTOs = iPlantFormatService.mapPlantFormatToDTO();

        return new ResponseEntity<>(farmRoleDTOs, HttpStatus.OK);
    }

    @GetMapping("/approvestatus")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<ApproveStatusDTO>> listApproveStatus() {
        SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(
                () -> {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không thể xác định thông tin người dùng hiện tại");
                }
            );
        List<ApproveStatusDTO> approveStatusDTOS = iApproveStatusService.listApproveStatus();

        return new ResponseEntity<>(approveStatusDTOS, HttpStatus.OK);
    }
}
