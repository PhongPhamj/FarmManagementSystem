package com.fpt.fms.repository;

import com.fpt.fms.domain.Location;
import com.fpt.fms.domain.LocationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {
    List<Location> findLocationByIdIn(Set<Long> ids);

    List<Location> findAllByCreatedBy(String eString);

    Location findByAddress(String address);
}

