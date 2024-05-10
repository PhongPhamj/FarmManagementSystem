package com.fpt.fms.service.baseservice;

import com.fpt.fms.domain.MultiMedia;
import com.fpt.fms.service.dto.MultiMediaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IMultiMediaFarmService {
    List<MultiMedia> uploadImage(List<MultipartFile> file,Long id) throws IOException;
    List<MultiMediaDTO> getImages(Long id);

    void deletImage(Long id);
}
