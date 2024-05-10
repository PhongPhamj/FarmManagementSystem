package com.fpt.fms.repository;

import com.fpt.fms.domain.CropPlan;
import com.fpt.fms.domain.LocationDetail;
import com.fpt.fms.domain.Plant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CropPlanRepo extends JpaRepository<CropPlan, Long> {
    List<CropPlan> findAllByPlantId(Long plantId);

    @Query(value = "select * from crop_plan c where c.created_by = ?1 and c.status is true and EXTRACT(YEAR FROM c.seed_started) = ?2 and c.is_deleted = false ", nativeQuery = true)
    List<CropPlan> findAllByCreatedByAndStatus(String createdBy, Integer year);

    List<CropPlan> findAllByPlantIn(List<Plant> plants);

    List<CropPlan> findAllByLocationDetail(LocationDetail locationDetail);

    Optional<CropPlan> findTopByBedContainingIgnoreCase(String bed);

    @Query(value = "select * from crop_plan c where c.location_detail_id = ?1", nativeQuery = true)
    List<CropPlan> getAllByLocationDetail(LocationDetail locationDetail);

    List<CropPlan> getAllByPlant(Plant plant);

}
