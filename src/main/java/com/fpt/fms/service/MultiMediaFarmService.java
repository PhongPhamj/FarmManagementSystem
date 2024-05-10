package com.fpt.fms.service;

import com.fpt.fms.domain.LocationDetail;
import com.fpt.fms.domain.MultiMedia;
import com.fpt.fms.fileUtils.FileUtils;
import com.fpt.fms.repository.LocationDetailRepository;
import com.fpt.fms.repository.MultiMediaRepository;
import com.fpt.fms.service.baseservice.IMultiMediaFarmService;
import com.fpt.fms.service.dto.MultiMediaDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MultiMediaFarmService implements IMultiMediaFarmService {
    @Autowired
    private final MultiMediaRepository imageRepository;

    @Autowired
    private final LocationDetailRepository locationDetailRepository;

    private static final String FOLDER_IMAGE_LOCATION_DETAIL = "Image_LocationDetail";

    private static final String UPLOAD_DIR = "static/images";
    private final FileUtils fileUtils;

    public MultiMediaFarmService(MultiMediaRepository imageRepository, LocationDetailRepository locationDetailRepository, FileUtils fileUtils) {
        this.imageRepository = imageRepository;
        this.locationDetailRepository = locationDetailRepository;
        this.fileUtils = fileUtils;
    }


    @Override
    @Transactional
    public List<MultiMedia> uploadImage(List<MultipartFile> files, Long id) {

        if (getLocationDetailById(id) == null) {
            throw new BaseException(HttpStatus.FORBIDDEN.value(), "Không tìm thấy khu vực!");
        }
        // Tên thư mục lưu ảnh của location farm

        List<MultiMedia> uploadedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.getOriginalFilename() == null) {
                throw new BaseException(HttpStatus.FORBIDDEN.value(), "Không có tệp nào!");
            }
            String name = UUID.randomUUID() + file.getOriginalFilename();
            String url = fileUtils.uploadImageLocal(file, FOLDER_IMAGE_LOCATION_DETAIL, name, id);
            if (url == null) {
                throw new BaseException(HttpStatus.FORBIDDEN.value(), "Không tìm thấy đường đẫn");
            }
            MultiMedia multiMedia = new MultiMedia();
            multiMedia.setName(name);
            multiMedia.setImageUrl(url);
            uploadedImages.add(multiMedia);
        }

        return imageRepository.saveAll(uploadedImages);
    }

    public List<MultiMediaDTO> getImages(Long id) {
        Path uploadPath = Paths.get(UPLOAD_DIR, FOLDER_IMAGE_LOCATION_DETAIL, id.toString()).toAbsolutePath().normalize();
        return imageRepository.findAll().stream().filter(f -> {
            Path imageUrl = Path.of(f.getImageUrl()).getParent();
            return imageUrl.toString().contains(uploadPath.toString());
        }).map(image -> {
            MultiMediaDTO a = new MultiMediaDTO();
            a.setId(image.getId());
            a.setName(image.getName());
            a.setCreatedDate(Date.from(image.getCreatedDate()));
            return a;
        }).collect(Collectors.toList());
    }

        @Override
    public void deletImage(Long id) {
        MultiMedia farm = getImageLocationFarm(id);
        farm.setDeleted(Boolean.TRUE);
        imageRepository.save(farm);
    }

    private LocationDetail getLocationDetailById(Long id) {
        return locationDetailRepository.findById(id).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy khu vực sẵn có"));
    }

    private MultiMedia getImageLocationFarm(Long locationFarmId) {
        return imageRepository.findById(locationFarmId).orElseThrow(() -> new EntityNotFoundException("Không tìm thất ảnh"));
    }
}
