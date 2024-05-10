package com.fpt.fms.repository;

import com.fpt.fms.domain.CropPlan;
import com.fpt.fms.domain.HarvestPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface HarvestPlanRepo extends JpaRepository<HarvestPlan, Long> {
    @Query(value = "select sum(h.amount) from harvest_plan h, crop_plan c where h.crop_plan_id = c.id and c.status is true and h.crop_plan_id = ?1", nativeQuery = true)
    Integer countAllByCropPlanId(Long cropPlanId);

    List<HarvestPlan> getAllByCropPlanId(Long cropPlanId);

    @Query(value = "select h.* from harvest_plan h, crop_plan c where c.id = h.crop_plan_id and c.plant_id = ?1 ", nativeQuery = true)
    List<HarvestPlan> getAllByPlanId(Long plantId);

    List<HarvestPlan> findAllByCropPlanIn(List<CropPlan> cropPlans);

    @Query(value = "select h.* from plant p, crop_plan c, harvest_plan h where p.id = c.plant_id" +
        " and h.crop_plan_id = c.id and EXTRACT(YEAR FROM date_harvest) = ?2 and p.id = ?1", nativeQuery = true)
    List<HarvestPlan> findAllByPlantId(Long plantId, Long year);

    @Query(value = "select h.* from plant p, crop_plan c, harvest_plan h where p.id = c.plant_id" +
        " and h.crop_plan_id = c.id and p.id = ?1", nativeQuery = true)
    List<HarvestPlan> findAllByPlantId(Long plantId);
    @Query(value = "SELECT hp.* FROM public.harvest_plan hp WHERE hp.crop_plan_id IN ?1", nativeQuery = true)
    List<HarvestPlan> findHarvestPlansByCropPlanIdIn(Set<Long> cropPLanId);
}
