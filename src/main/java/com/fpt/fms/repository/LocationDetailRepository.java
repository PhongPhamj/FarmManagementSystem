package com.fpt.fms.repository;

import com.fpt.fms.domain.Location;
import com.fpt.fms.domain.LocationDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LocationDetailRepository extends JpaRepository<LocationDetail, Long>, JpaSpecificationExecutor<LocationDetail> {
//    Page<LocationDetail> findAllByLocation (Location location, Pageable pageable);
    List<LocationDetail> findLocationDetailByIdIn(Set<Long> ids);
    List<LocationDetail> findAllByCreatedBy(Pageable pageable, String email);

    @Query(value = "select r.* from location_detail r, crop_plan c where c.location_detail_id = r.id and c.plant_id = ?1", nativeQuery = true)
    List<LocationDetail> findAllByPlantId(Long id);


    List<LocationDetail> getAllByCreatedBy(String createdBy);
    Optional<LocationDetail> findByIdAndCreatedBy(Long id, String eString);
    LocationDetail findByNameAndCreatedBy(String name,String email);
}
