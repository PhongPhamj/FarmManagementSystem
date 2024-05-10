package com.fpt.fms.web.rest;


import com.fpt.fms.domain.PlantCategory;
import com.fpt.fms.service.PlantDetailService;
import com.fpt.fms.service.dto.PlantDTO;
import com.fpt.fms.service.dto.PlantDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plant-detail")
public class PlantDetailController {

    @Autowired
    private PlantDetailService plantDetailService;

    @GetMapping("/category")
    public List<PlantCategory> getAllPlantCategory(){
        return Arrays.stream(PlantCategory.values()).collect(Collectors.toList());
    }

    @PostMapping("")
    public void createPlantDetail(@RequestBody PlantDTO plantDTO){
       plantDetailService.createPlantDetail(plantDTO);
    }

    @GetMapping("/{plantId}")
    public ResponseEntity<PlantDetailDTO> getPlantDetailById(@PathVariable Long plantId){
        return ResponseEntity.ok(plantDetailService.getPlantDetailByPlantId(plantId));
    }

    @PutMapping("/{plantDetailId}")
    public void updatePlantDetail(@PathVariable Long plantDetailId, @RequestBody PlantDetailDTO plantDetailDTO){
        plantDetailService.updatePlantDetail(plantDetailId, plantDetailDTO);
    }
}
