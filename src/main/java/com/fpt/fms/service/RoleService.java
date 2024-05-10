package com.fpt.fms.service;

import com.fpt.fms.domain.ApproveStatus;
import com.fpt.fms.domain.Authority;
import com.fpt.fms.domain.FarmRole;
import com.fpt.fms.domain.PlantFormat;
import com.fpt.fms.repository.AuthorityRepository;
import com.fpt.fms.service.baseservice.IApproveStatusService;
import com.fpt.fms.service.baseservice.IAuthorityService;
import com.fpt.fms.service.baseservice.IFarrmRoleService;
import com.fpt.fms.service.baseservice.IPlantFormatService;
import com.fpt.fms.service.dto.ApproveStatusDTO;
import com.fpt.fms.service.dto.AuthorityDTO;
import com.fpt.fms.service.dto.FarmRoleDTO;
import com.fpt.fms.service.dto.PlantFormatDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements IAuthorityService, IFarrmRoleService, IPlantFormatService, IApproveStatusService {

    private final AuthorityRepository authorityRepository;

    public RoleService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public List<AuthorityDTO> listAuthority() {
        List<Authority> authorities = authorityRepository.findAll();
        List<AuthorityDTO> authorityDTOList = new ArrayList<>();
        for (int i = 0; i < authorities.size(); i++) {
            Authority authority = authorities.get(i);
            AuthorityDTO authorityDTO = new AuthorityDTO(i + 1, authority.getName()); // i + 1 là id là số thứ tự
            authorityDTOList.add(authorityDTO);
        }

        return authorityDTOList;
    }

    @Override
    public List<PlantFormatDTO> mapPlantFormatToDTO() {
        PlantFormat[] plantFormat = PlantFormat.values();
        List<PlantFormatDTO> plantFormatDTO = new ArrayList<>();

        for (int i = 0; i < plantFormat.length; i++) {
            PlantFormat planFormatDTO = plantFormat[i];
            PlantFormatDTO planformat = new PlantFormatDTO(i + 1, planFormatDTO.name());
            plantFormatDTO.add(planformat);
        }
        return plantFormatDTO;
    }

    @Override
    public List<FarmRoleDTO> mapFarmRolesToDTO() {
        FarmRole[] farmRoles = FarmRole.values();
        List<FarmRoleDTO> farmRoleDTOs = new ArrayList<>();

        for (int i = 0; i < farmRoles.length; i++) {
            FarmRole farmRole = farmRoles[i];
            FarmRoleDTO farmRoleDTO = new FarmRoleDTO(i + 1, farmRole.name());
            farmRoleDTOs.add(farmRoleDTO);
        }
        return farmRoleDTOs;
    }

    @Override
    public List<ApproveStatusDTO> listApproveStatus() {
        ApproveStatus[] approveStatuses = ApproveStatus.values();
        List<ApproveStatusDTO> listApproveStatus = new ArrayList<>();
        for (int i = 0; i < approveStatuses.length; i++) {
            ApproveStatus appStatus = approveStatuses[i];
            ApproveStatusDTO approveStatusDTO = new ApproveStatusDTO(i + 1, appStatus.name());
            listApproveStatus.add(approveStatusDTO);
        }
        return listApproveStatus;
    }
}
