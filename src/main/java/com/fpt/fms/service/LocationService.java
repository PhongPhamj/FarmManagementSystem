package com.fpt.fms.service;

import com.fpt.fms.domain.CropPlan;
import com.fpt.fms.domain.HarvestPlan;
import com.fpt.fms.domain.Location;
import com.fpt.fms.domain.PlantDetail;
import com.fpt.fms.repository.CropPlanRepo;
import com.fpt.fms.repository.HarvestPlanRepo;
import com.fpt.fms.repository.LocationDetailRepository;
import com.fpt.fms.repository.LocationRepository;
import com.fpt.fms.service.baseservice.ILocationService;
import com.fpt.fms.service.dto.LocationDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService implements ILocationService {

    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<LocationDTO> getAllLocation() {
        return locationRepository
            .findAll()
            .stream()
            .map(
                location -> {
                    LocationDTO locationDTO = new LocationDTO();
                    locationDTO.setId(location.getId()); // Gán giá trị id cho locationDTO
                    locationDTO.setAddress(location.getAddress());
                    return locationDTO;
                }
            )
            .collect(Collectors.toList());
    }

    @Override
    public void registerLocation(LocationDTO locationDTO) {
        Location location = modelMapper.map(locationDTO, Location.class);
        locationRepository.save(location);
    }

    @Override
    public void deleteLocation(Long locationId) {
        Location locationfarm = getLocationlById(locationId);
        checkLocationStatus(locationfarm);
        locationfarm.setDeleted(Boolean.TRUE);
        locationRepository.save(locationfarm);
    }

    @Override
    public void deleteLocations(Set<Long> ids) {
        List<Location> locationfarms = locationRepository.findLocationByIdIn(ids);
        locationfarms.forEach(
            locations -> {
                checkLocationStatus(locations);
                locations.setDeleted(Boolean.TRUE);
            }
        );
        locationRepository.saveAll(locationfarms);
    }

    @Override
    public void updateLocation(LocationDTO locationDTO) {
        Location location = getLocationlById(locationDTO.getId());
        location.setAddress(locationDTO.getAddress());
        location.setLongitude(locationDTO.getLongitude());
        location.setLatitude(locationDTO.getLatitude());
        location.setStatus(locationDTO.getStatus());

        locationRepository.save(location);
    }

    private static void checkLocationStatus(Location location) {
        if (Boolean.TRUE.equals(location.getStatus())) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa vị trí đang hoạt động");
        }

    }

    private Location getLocationlById(Long id) {
        return locationRepository.findById(id).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy vị trí sẵn có"));
    }
}
