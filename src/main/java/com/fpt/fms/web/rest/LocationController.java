package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.service.baseservice.ILocationService;
import com.fpt.fms.service.dto.LocationDTO;
import net.logstash.logback.encoder.org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/location")
public class LocationController {
    @Value("fms")
    private String applicationName;
    private final ILocationService locationServices;

    public LocationController(ILocationService locationServices) {
        this.locationServices = locationServices;
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<LocationDTO>> getLocation() {
        List<LocationDTO> location = locationServices.getAllLocation();
        return new ResponseEntity<>(location, HttpStatus.OK);
    }
    @PostMapping()
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> registerLocation(@Valid @RequestBody LocationDTO locationDTO) {
        locationServices.registerLocation(locationDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Đăng kí vị trí thành công!");
    }
    @PatchMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<Void> updateLocation(@Valid @RequestBody LocationDTO locationDTO) {
        locationServices.updateLocation(locationDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> deleteLocation(@PathVariable(name = "id") Long id) {
        locationServices.deleteLocation(id);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @DeleteMapping()
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> deleteLocations(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        locationServices.deleteLocations(ids);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }
}
