package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.ILocationDetailService;
import com.fpt.fms.service.dto.LocationDTO;
import com.fpt.fms.service.dto.LocationDetailDTO;
import com.fpt.fms.service.dto.LocationFarmDTO;
import com.fpt.fms.service.search.SearchLocationDetailDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import net.logstash.logback.encoder.org.apache.commons.lang3.math.NumberUtils;
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

@RestController
@RequestMapping("/api/farmmanager/locationfarm")
public class LocationDetailController {

    @Value("fms")
    private String applicationName;

    private final ILocationDetailService locationFarmService;

    public LocationDetailController(ILocationDetailService locationFarmService) {
        this.locationFarmService = locationFarmService;
    }

    @GetMapping("/lstLocationDetailByUser")
    public List<LocationDetailDTO> getListByUserCurrent() {
        String curUser = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(
                () -> {
                    throw new BaseException(HttpStatus.UNAUTHORIZED.value(), "không tìm thấy thông tin người dùng trong hệ thống");
                }
            );
        return locationFarmService.lstLocationDetailByUserCreate(curUser);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<List<LocationDetailDTO>> getLocationsFarm(
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<LocationDetailDTO> locationsFarmPage = locationFarmService.getLocationsFarm(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            locationsFarmPage
        );
        return new ResponseEntity<>(locationsFarmPage.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<List<LocationDetailDTO>> getLocationsSearch(
        @RequestBody(required = false) SearchLocationDetailDTO searchLocationFarmDTO,
        @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.PAGE_SIZE_DEFAULT) Pageable pageable
    ) {
        Page<LocationDetailDTO> locationFarmDTOPage = locationFarmService.searchLocationFarm(searchLocationFarmDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            locationFarmDTOPage
        );
        return new ResponseEntity<>(locationFarmDTOPage.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<String> registerLocationFarm(@RequestBody LocationFarmDTO locationFarmDTO) {
        locationFarmService.registerLocationDetailFarm(locationFarmDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Đăng ký khu vực thành công!");
    }

    @GetMapping("/locationdetail/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<LocationDetailDTO> getLocation(@PathVariable(name = "id") Long id) {
        return ResponseUtil.wrapOrNotFound(locationFarmService.getLocationDetail(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<Void> deleteLocationDetail(@PathVariable(name = "id") Long id) {
        locationFarmService.deleteLocationDetail(id);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<Void> deleteLocationDetails(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        locationFarmService.deleteLocationDetails(ids);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @PatchMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<Void> updateLocationFarm(@Valid @RequestBody LocationFarmDTO locationFarmDTO) {
        locationFarmService.updateLocationFarm(locationFarmDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }


}
