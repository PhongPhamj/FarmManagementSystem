package com.fpt.fms.repository;

import com.fpt.fms.domain.Plant;
import com.fpt.fms.domain.PlantDetail;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantDetailRepo extends JpaRepository<PlantDetail, Long> {
    Optional<PlantDetail> findPlantDetailByPlantId(Long plantId);

    List<PlantDetail> findAllByPlantIn(List<Plant> plants);
}
