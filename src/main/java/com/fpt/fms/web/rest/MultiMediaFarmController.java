package com.fpt.fms.web.rest;

import com.fpt.fms.service.baseservice.IMultiMediaFarmService;
import com.fpt.fms.service.dto.MultiMediaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/images")
public class MultiMediaFarmController {
    @Value("fms")
    private String applicationName;
    @Autowired
    private final IMultiMediaFarmService imageUploadService;

    public MultiMediaFarmController(IMultiMediaFarmService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestPart("file") List<MultipartFile> files, @RequestParam("id") Long id) throws IOException {
        imageUploadService.uploadImage(files, id);
        return new ResponseEntity<>("Tải lên ảnh thành công", HttpStatus.OK);

    }

    @GetMapping("/upload/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<MultiMediaDTO>> getFarms(@PathVariable Long id) {
        List<MultiMediaDTO> farmDTOList = imageUploadService.getImages(id);
        return ResponseEntity.ok(farmDTOList);
    }
    @DeleteMapping("/upload/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> deleteImage(@PathVariable(name = "id") Long id) {
        imageUploadService.deletImage(id);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }
}
