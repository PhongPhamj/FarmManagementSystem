package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.PlantService;
import com.fpt.fms.service.dto.PlantDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plants")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
public class PlantController {

    @Value("fms")
    private String applicationName;

    @Autowired
    private PlantService plantService;

    @GetMapping("")
    public List<PlantDTO> getAllPlantByCurrentUser(@RequestParam(required = false) String name) {
        String curUser = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(
                () -> {
                    throw new BaseException(HttpStatus.UNAUTHORIZED.value(), "không tìm thấy thông tin người dùng trong hệ thống");
                }
            );
        return plantService.getListPlantByUserCurrent(curUser, name);
    }

    @PostMapping("")
    public PlantDTO createPlant(@RequestBody PlantDTO plantDTO) {
        return plantService.createPlant(plantDTO);
    }

    @PutMapping("")
    public void updatePlant(@RequestBody PlantDTO plantDTO) {
        plantService.updatePlant(plantDTO);
    }

    @DeleteMapping("/{plantId}")
    public void deletePlant(@PathVariable Long plantId) {
        plantService.deletePlant(plantId);
    }

    @DeleteMapping
    public ResponseEntity<PlantDTO> deletePlants(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        plantService.deletePlants(ids);

        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(HeaderUtil.createAlert(applicationName, "Xóa cây trồng thành công", applicationName))
            .build();
    }

    @GetMapping("/totalReport")
    public ResponseEntity<?> listStaticPlantByCurrentUser(@RequestParam Long year){
        String curUser = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(
                () -> {
                    throw new BaseException(HttpStatus.UNAUTHORIZED.value(), "không tìm thấy thông tin người dùng trong hệ thống");
                }
            );
        if(!Pattern.matches("^\\d{4}$", year.toString())){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "year không hợp lệ");
        }
        return ResponseEntity.ok(plantService.listStaticPlantByCurrentUser2(curUser, year));
    }

    @PostMapping("/statisticProductPlant/{plantId}")
    public ResponseEntity<?> getStaticProductPlant(@PathVariable Long plantId, @RequestBody List<String> year){
        return ResponseEntity.ok(plantService.getStaticProductPlant(plantId, year));
    }
}
