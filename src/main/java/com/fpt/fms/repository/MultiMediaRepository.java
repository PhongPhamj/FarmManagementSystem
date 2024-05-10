package com.fpt.fms.repository;

import com.fpt.fms.domain.MultiMedia;
import com.fpt.fms.service.dto.MultiMediaDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MultiMediaRepository extends JpaRepository<MultiMedia,Long> {
    List<MultiMediaDTO> findAllByImageUrl(String url);
}
