package com.fpt.fms.web.rest;

import com.fpt.fms.domain.HarvestUnit;
import com.fpt.fms.service.HarvestPlanService;
import com.fpt.fms.service.dto.HarvestPlanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/harvestPlans")
public class HarvestController {

    @Autowired
    private HarvestPlanService harvestPlanService;

    @GetMapping("/harvestUnit")
    public List<HarvestUnit> lstHarvestUnit(){
        return Arrays.stream(HarvestUnit.values()).collect(Collectors.toList());
    }

    @PostMapping("/cropPlan/{cropPlanId}")
    public void createHarvestPlan(@PathVariable Long cropPlanId, @RequestBody HarvestPlanDTO harvestPlanDTO){
        harvestPlanService.createHarvestPlan(cropPlanId, harvestPlanDTO);
    }

    @GetMapping("/cropPlan/{cropPlanId}")
    public List<HarvestPlanDTO> lstHarvestPlanByCropPlan(@PathVariable Long cropPlanId){
        return harvestPlanService.lstAllByCropPlan(cropPlanId);
    }

    @PutMapping("/{harvestId}")
    public void updateHarvestPlanById(@PathVariable Long harvestId, @RequestBody HarvestPlanDTO harvestPlanDTO){
        harvestPlanService.updateHarvestbyId(harvestId, harvestPlanDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteHarvestPlanById(@PathVariable Long id){
        harvestPlanService.deleteHarvestPlan(id);
    }


}
