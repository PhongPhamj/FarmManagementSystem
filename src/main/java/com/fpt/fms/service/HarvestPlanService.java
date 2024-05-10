package com.fpt.fms.service;

import com.fpt.fms.domain.CropPlan;
import com.fpt.fms.domain.HarvestPlan;
import com.fpt.fms.repository.CropPlanRepo;
import com.fpt.fms.repository.HarvestPlanRepo;
import com.fpt.fms.service.dto.HarvestPlanDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HarvestPlanService {

    @Autowired
    private HarvestPlanRepo harvestPlanRepo;

    @Autowired
    private CropPlanRepo cropPlanRepo;

    @Autowired
    private ModelMapper modelMapper;

    public void createHarvestPlan(Long cropPlanId, HarvestPlanDTO harvestPlanDTO){
        Optional<CropPlan> cropPlan = cropPlanRepo.findById(cropPlanId);
        if(!cropPlan.isPresent()){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy kế hoạch cho tạo thu hoạch");
        }
        if (cropPlan.get().getIsDone()){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "kế hoạch đã được hoàn thành không thể thao tác");
        }
        if(cropPlan.get().getCompleteDate() == null || new Date().before(cropPlan.get().getCompleteDate()) || new Date().equals(cropPlan.get().getCompleteDate())){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể thu hoạch khi chưa đến ngày thu hoạch");
        }
        HarvestPlan harvestPlan = modelMapper.map(harvestPlanDTO, HarvestPlan.class);
        harvestPlan.setCropPlan(cropPlan.get());

        harvestPlanRepo.save(harvestPlan);
    }

    public List<HarvestPlanDTO> lstAllByCropPlan(Long cropPlanId){
        Optional<CropPlan> cropPlan = cropPlanRepo.findById(cropPlanId);
        if(!cropPlan.isPresent()){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy kế hoạch cho tạo thu hoạch");
        }

        List<HarvestPlan> harvestPlans = harvestPlanRepo.getAllByCropPlanId(cropPlanId);
        List<HarvestPlanDTO> harvestPlanDTOS = new ArrayList<>();

        for (HarvestPlan harvestPlan : harvestPlans){
            harvestPlanDTOS.add(HarvestPlanDTO.builder()
                .amount(harvestPlan.getAmount())
                .dateHarvest(harvestPlan.getDateHarvest())
                .createdBy(harvestPlan.getCreatedBy())
                .id(harvestPlan.getId())
                .note(harvestPlan.getNote())
                .quality(harvestPlan.getQuality())
                .build());
        }
        Collections.sort(harvestPlanDTOS, Comparator.comparing(HarvestPlanDTO::getDateHarvest));
        return harvestPlanDTOS;
    }

    public void deleteHarvestPlan(Long id){
        Optional<HarvestPlan> harvestPlan = harvestPlanRepo.findById(id);
        if(!harvestPlan.isPresent()){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy kế hoạch cho tạo thu hoạch");
        }
        harvestPlanRepo.delete(harvestPlan.get());
    }

    public void updateHarvestbyId(Long harvestId, HarvestPlanDTO harvestPlanDTO) {
        HarvestPlan harvestPlan = harvestPlanRepo.findById(harvestId)
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy id kế hoạch thu hoạch"));

        harvestPlan.setDateHarvest(harvestPlanDTO.getDateHarvest());
        harvestPlan.setNote(harvestPlanDTO.getNote());
        harvestPlan.setAmount(harvestPlanDTO.getAmount());
        harvestPlan.setQuality(harvestPlanDTO.getQuality());

        HarvestPlan harvestPlan1 = harvestPlanRepo.save(harvestPlan);

    }
}
