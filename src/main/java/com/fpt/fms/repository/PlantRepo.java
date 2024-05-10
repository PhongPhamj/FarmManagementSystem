package com.fpt.fms.repository;

import com.fpt.fms.domain.CropPlan;
import com.fpt.fms.domain.Plant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantRepo extends JpaRepository<Plant, Long> {
    @Query(
        value = "select p from Plant p where p.createdBy = :user and (:name is null or LOWER(p.name) LIKE CONCAT('%',LOWER(CAST(:name AS text)), '%') ) and p.status is true"
    )
    List<Plant> findAllByCreatedByAndStatusAndNamePlant(@Param("user") String createdBy, @Param("name") String namePlant);

    @Query("SELECT p FROM Plant p WHERE p.id = :id AND p.createdBy = :createdBy")
    Plant findByIdAndCreatedBy(@Param("id") Long id, @Param("createdBy") String createdBy);

    Plant findByNameAndCreatedBy(String name,String eString);
    @Query("SELECT p FROM Plant p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Plant> findPlantsByNameContaining(@Param("name") String name);

    @Query("SELECT p FROM Plant p WHERE LOWER(CONCAT(p.name, ' ', p.source)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Plant> findPlantsByNameAndSourceCombined(@Param("name") String name);

    List<Plant> findAllByIdIn(Set<Long> ids);


    List<Plant> findAllByCreatedBy(String createdBy);


}
