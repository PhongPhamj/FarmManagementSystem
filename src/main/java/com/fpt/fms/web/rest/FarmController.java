package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.service.baseservice.IFarmService;
import com.fpt.fms.service.dto.FarmDTO;
import com.fpt.fms.service.search.SearchDTO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class FarmController {

    private final IFarmService farmService;
    @Value("fms")
    private String applicationName;

    public FarmController(IFarmService farmService) {
        this.farmService = farmService;
    }

    @PostMapping("/api/register-farm")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public void registerFarm(@Valid @RequestBody FarmDTO farmDTO) {
        farmService.registerFarm(farmDTO);
    }

    @GetMapping("/api/farm")
    public FarmDTO farmOfCurrentUser() {
        return farmService.getFarmOfUserCurrent();
    }

    @PutMapping("/api/farm/{farmId}")
    @PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.EMPLOYEE + "\", \"" + AuthoritiesConstants.USER + "\")")
    public FarmDTO updateFarm(@PathVariable Long farmId, @RequestBody FarmDTO farmDTO) {
        return farmService.updateFarm(farmId, farmDTO);
    }

    @GetMapping("/api/farm/findallfarm")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<FarmDTO>> getFarms(
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<FarmDTO> farmDTOPage = farmService.getFarms(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), farmDTOPage);
        return new ResponseEntity<>(farmDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/api/farm/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<FarmDTO>> getSearchFarms(
        @RequestBody(required = false) SearchDTO searchDTO,
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<FarmDTO> farmDTOPage = farmService.search(searchDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), farmDTOPage);
        return new ResponseEntity<>(farmDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/api/farm/{farmId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN' ,'ROLE_EMPLOYEE')")
    public ResponseEntity<FarmDTO> getFarm(@PathVariable(name = "farmId") Long farmId) {
        return ResponseUtil.wrapOrNotFound(farmService.getFarm(farmId));
    }

    @PatchMapping("/api/farm")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> updateFarm(@Valid @RequestBody FarmDTO farmDTO) {
        farmService.updateFarm(farmDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }

    @DeleteMapping("/api/farm/{farmId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteFarm(@PathVariable(name = "farmId") Long farmId) {
        farmService.deleteFarm(farmId);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @DeleteMapping("/api/farm")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteFarms(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        farmService.deleteFarms(ids);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }
}
