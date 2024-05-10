package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.FarmDTO;
import com.fpt.fms.service.dto.LocationDTO;
import com.fpt.fms.service.dto.LocationDetailDTO;
import com.fpt.fms.service.dto.LocationFarmDTO;
import com.fpt.fms.service.search.SearchLocationDetailDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ILocationDetailService {
    Page<LocationDetailDTO> getLocationsFarm(Pageable pageable);
    Page<LocationDetailDTO> searchLocationFarm(SearchLocationDetailDTO searchLocationFarmDTO, Pageable pageable);

    Optional<LocationDetailDTO> getLocationDetail(Long locationDetailId);
    void registerLocationDetailFarm(LocationFarmDTO locationFarmDTO);

    void deleteLocationDetail(Long locationId);

    void deleteLocationDetails(Set<Long> ids);

    void updateLocationFarm(LocationFarmDTO locationFarmDTO);

    List<LocationDetailDTO> lstLocationDetailByUserCreate(String curUser);
}
