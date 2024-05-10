package com.fpt.fms.repository;

import com.fpt.fms.domain.ApproveStatus;
import com.fpt.fms.domain.Farm;
import com.fpt.fms.service.dto.FarmDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long>, JpaSpecificationExecutor<Farm> {

    Optional<Farm> findFarmByCreatedByAndStatusNot(String createdBy, ApproveStatus status);

    @Query(value = "select * from farm f where f.user_id = :id and is_deleted = false f.status != :status", nativeQuery = true)
    Optional<Farm> findFarmByUserIdAndStatusNot(@Param("id") Long id, @Param("status") ApproveStatus status);

    Optional<Farm> findFarmByCreatedBy(String createdBy);

    List<Farm> findFarmByIdIn(Set<Long> ids);

    @Query(value = "select * from farm f where f.user_id = :id", nativeQuery = true)
    Optional<Farm> findFarmByUserId(@Param("id") Long id);
}
