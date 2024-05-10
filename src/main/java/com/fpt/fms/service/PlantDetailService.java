package com.fpt.fms.service;

import com.fpt.fms.domain.Plant;
import com.fpt.fms.domain.PlantDetail;
import com.fpt.fms.repository.PlantDetailRepo;
import com.fpt.fms.repository.PlantRepo;
import com.fpt.fms.service.dto.HarvestDTO;
import com.fpt.fms.service.dto.PlantDTO;
import com.fpt.fms.service.dto.PlantDetailDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PlantDetailService {

    @Autowired
    private PlantDetailRepo plantDetailRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PlantRepo plantRepo;

    @Transactional(rollbackFor = Exception.class)
    public void createPlantDetail(PlantDTO plantDTO){
        if(plantDTO.getName() == null || plantDTO.getName().isBlank()){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên cây trông không được trống");
        }if(plantDTO.getPlantDetailDTO() == null){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Thông tin chi tiết cây trông không được trống");
        }
        PlantDetailDTO plantDetailDTO = plantDTO.getPlantDetailDTO();
        Plant plant = Plant.builder()
            .name(plantDTO.getName())
            .provider(plantDTO.getProvider())
            .source(plantDTO.getSource())
            .status(true)
            .type(plantDTO.getType())
            .build();
        Plant plant1 = plantRepo.save(plant);

        PlantDetail plantDetail = modelMapper.map(plantDetailDTO, PlantDetail.class);
        plantDetail.setPlant(plant1);

        plantDetailRepo.save(plantDetail);
    }

    public PlantDetailDTO getPlantDetailByPlantId(Long plantId){
        Optional<Plant> plant = plantRepo.findById(plantId);
        if(!plant.isPresent()){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy thông tin cây trồng");
        }
        Optional<PlantDetail>  plantDetail = plantDetailRepo.findPlantDetailByPlantId(plantId);

        PlantDetailDTO plantDetailDTO = modelMapper.map(plantDetail.get(), PlantDetailDTO.class);
        plantDetailDTO.setPlantDTO(modelMapper.map(plant.get(), PlantDTO.class));

        return plantDetailDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePlantDetail(Long id, PlantDetailDTO plantDetailDTO){
        Optional<PlantDetail> opDetail = plantDetailRepo.findById(id);
        if(!opDetail.isPresent()){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy thông tin chi tiết cây trồng");
        }
        PlantDetail plantDetail = opDetail.get();

        plantDetail.setPlantDepth(plantDetailDTO.getPlantDepth());
        plantDetail.setPlantSpace(plantDetailDTO.getPlantSpace());
        plantDetail.setDescription(plantDetail.getDescription());
        plantDetail.setDayToEmerge(plantDetailDTO.getDayToEmerge());
        plantDetail.setRowSpace(plantDetailDTO.getRowSpace());
        plantDetail.setRateGermination(plantDetailDTO.getRateGermination());
        plantDetail.setStartMethod(plantDetailDTO.getStartMethod());
        plantDetail.setDayToHarvest(plantDetailDTO.getDayToHarvest());
        plantDetail.setDescription(plantDetailDTO.getDescription());
        plantDetail.setHarvestUnit(plantDetailDTO.getHarvestUnit());
        plantDetail.setLossRate(plantDetailDTO.getLossRate());

        plantDetailRepo.save(plantDetail);
    }
}
