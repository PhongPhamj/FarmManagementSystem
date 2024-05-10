package com.fpt.fms.web.rest;

import com.fpt.fms.domain.GrowthStage;
import com.fpt.fms.domain.StartMethod;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.CropPlanService;
import com.fpt.fms.service.dto.CropPlanDTO;
import com.fpt.fms.service.dto.LocationDetailDTO;
import com.fpt.fms.service.dto.PlantDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/crop-plan")
public class CropPlanController {

    @Autowired
    private CropPlanService cropPlanService;

    @GetMapping("/lst-plan-method")
    public List<String> lstPlantMethod() {
        return Arrays.stream(StartMethod.values()).map(startMethod -> startMethod.name()).collect(Collectors.toList());
    }

    @DeleteMapping("/{cropPlanId}")
    public void deleteCropPlan(@PathVariable Long cropPlanId){
        cropPlanService.deleteCropPlanById(cropPlanId);
    }

    @GetMapping("/lst-grow-stage")
    public List<String> lstGrowStage() {
        return Arrays.stream(GrowthStage.values()).map(growthStage -> growthStage.name()).collect(Collectors.toList());
    }

    @PostMapping("")
    public CropPlanDTO createPlan(@RequestBody CropPlanDTO cropPlanDTO) {
        return cropPlanService.createCropPlan(cropPlanDTO);
    }

    @PutMapping("")
    public void updatePlan(@RequestBody CropPlanDTO cropPlanDTO) {
        cropPlanService.updateCropPlan(cropPlanDTO);
    }

    @PatchMapping("/{cropPlanId}/{status}")
    public void updateStatusCropPlan(@PathVariable Integer status, @PathVariable Long cropPlanId) {

        cropPlanService.updateStatusCropPlan(status, cropPlanId);
    }

    @GetMapping("")
    public List<CropPlanDTO> getListPlanByCurrentUser(@RequestParam Integer year,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false) Long status) {
        String curUser = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(
                () -> {
                    throw new BaseException(HttpStatus.UNAUTHORIZED.value(), "không tìm thấy thông tin người dùng trong hệ thống");
                }
            );
        return cropPlanService.getLstPlanByUserCurrent(curUser, year, name, status);
    }

    @GetMapping("/{cropPlanId}")
    public CropPlanDTO getCropPlanDetailById(@PathVariable Long cropPlanId) {
        return cropPlanService.getCropPlanDetailById(cropPlanId);
    }

    @GetMapping("/locationDetail/{id}")
    public LocationDetailDTO lstAllByPlantId(@PathVariable Long id) {
        return cropPlanService.getStaticCropPlanByPlantId(id);
    }

    @PostMapping("/statisticHarvestByPlantId/{plantId}")
    public ResponseEntity<?> statisticHarvestByPlantId(@PathVariable Long plantId, @RequestBody List<String> years) {
        return ResponseEntity.ok(cropPlanService.getReportPlantById(plantId, years));
    }
}
