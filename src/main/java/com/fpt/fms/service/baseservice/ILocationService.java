package com.fpt.fms.service.baseservice;

import com.fpt.fms.domain.Location;
import com.fpt.fms.service.dto.LocationDTO;
import com.fpt.fms.service.dto.LocationDetailDTO;
import com.fpt.fms.service.search.SearchLocationDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ILocationService {
  List<LocationDTO> getAllLocation();

//    Page<LocationDTO> getLocationsFarm(Pageable pageable);
//    Page<LocationDetailDTO> searchLocationFarm(SearchLocationDetailDTO searchLocationFarmDTO, Pageable pageable);
//
//    Optional<LocationDetailDTO> getLocationDetail(Long locationDetailId);
    void registerLocation(LocationDTO locationFarmDTO);

    void deleteLocation(Long locationId);

    void deleteLocations(Set<Long> ids);

    void updateLocation(LocationDTO locationDTO);
}
