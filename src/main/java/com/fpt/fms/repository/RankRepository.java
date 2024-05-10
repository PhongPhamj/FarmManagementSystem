package com.fpt.fms.repository;

import com.fpt.fms.domain.Farm;
import com.fpt.fms.domain.Rank;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RankRepository extends JpaRepository<Rank,Long> , JpaSpecificationExecutor<Rank> {
    List<Rank> findRankByIdInAndAndCreatedBy(Set<Long> ids,String email);

    Rank findByIdAndCreatedBy(Long id, String email);
    Optional<Rank> findByName(String name);

    List<Rank> findByCreatedBy(String email);

}
