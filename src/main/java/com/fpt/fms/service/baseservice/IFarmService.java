package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.FarmDTO;
import com.fpt.fms.service.search.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface IFarmService {
    void registerFarm(FarmDTO farmDTO);

    FarmDTO getFarmOfUserCurrent();

    FarmDTO updateFarm(Long farmId, FarmDTO farmDTO);

    Optional<FarmDTO> getFarm(Long farmId);

    Page<FarmDTO> getFarms(Pageable pageable);

    Page<FarmDTO> search(SearchDTO searchDTO, Pageable pageable);

    void updateFarm(FarmDTO farmDTO);

    void deleteFarm(Long farmId);

    void deleteFarms(Set<Long> ids);

}
