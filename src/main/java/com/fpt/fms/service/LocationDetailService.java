package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.*;
import com.fpt.fms.repository.spec.LocationSpectificationBuilder;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.ILocationDetailService;
import com.fpt.fms.service.dto.LocationDTO;
import com.fpt.fms.service.dto.LocationDetailDTO;
import com.fpt.fms.service.dto.LocationFarmDTO;
import com.fpt.fms.service.search.SearchLocationDetailDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationDetailService implements ILocationDetailService {

    private final LocationDetailRepository locationDetailRepository;
    private final LocationRepository locationRepository;
    private final CropPlanRepo cropPlanRepo;
    private final ModelMapper modelMapper;
    private final HarvestPlanRepo harvestPlanRepo;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public Page<LocationDetailDTO> getLocationsFarm(Pageable pageable) {
        List<LocationDetailDTO> locationDetailDTOList = locationDetailRepository
            .findAllByCreatedBy(pageable, getCur())
            .stream()
//            .filter(locationDetail -> !locationDetail.getLocation().getDeleted() && locationDetail.getLocation().getStatus())
            .distinct()
            .map(
                location -> {
                    LocationDetailDTO locationFarm = new LocationDetailDTO();
                    locationFarm.setId((location.getId()));
                    locationFarm.setName(location.getName());
                    locationFarm.setPlantFormat(location.getPlantFormat());
                    locationFarm.setCreatedDate(Date.from(location.getCreatedDate()));
                    locationFarm.setStatus(location.getStatus());
                    return locationFarm;
                }
            )
            .collect(Collectors.toList());
        return new PageImpl<>(locationDetailDTOList, pageable, locationDetailDTOList.size());
    }

    private List<Location> getLocations() {
        return locationRepository.findAllByCreatedBy(getCur());
    }

    @Override
    @Transactional
    public Page<LocationDetailDTO> searchLocationFarm(SearchLocationDetailDTO searchLocationFarmDTO, Pageable pageable) {
        Specification<LocationDetail> specification = LocationSpectificationBuilder.buildQuery(searchLocationFarmDTO, getLocations());
        List<LocationDetailDTO> locationDetailDTOList = locationDetailRepository
            .findAll(specification.and((root, query, cb) -> cb.equal(root.get("createdBy"), getCur())), pageable)
            .stream()
//            .filter(locationDetail -> !locationDetail.getLocation().getDeleted() && locationDetail.getLocation().getStatus())
            .distinct()
            .map(
                location -> {
                    LocationDetailDTO locationFarm = new LocationDetailDTO();
                    locationFarm.setId((location.getId()));
                    locationFarm.setName(location.getName());
                    locationFarm.setPlantFormat(location.getPlantFormat());
                    locationFarm.setCreatedDate(Date.from(location.getCreatedDate()));
                    locationFarm.setStatus(location.getStatus());
                    return locationFarm;
                }
            )
            .collect(Collectors.toList());
        return new PageImpl<>(locationDetailDTOList, pageable, locationDetailDTOList.size());
    }

    @Override
    public Optional<LocationDetailDTO> getLocationDetail(Long locationDetailId) {
        LocationDetail locationFarmDTO = getLocationDetailById(locationDetailId);
        LocationDetailDTO locationDetail = new LocationDetailDTO();
        locationDetail.setNumberOfBeds(locationFarmDTO.getNumberOfBeds());
        locationDetail.setName(locationFarmDTO.getName());
        locationDetail.setPlantFormat(locationFarmDTO.getPlantFormat());
        locationDetail.setArea(locationFarmDTO.getArea());
        locationDetail.setStatus(locationFarmDTO.getStatus());
        locationDetail.setDescription(locationFarmDTO.getDescription());
        LocationDTO locationDTO = modelMapper.map(locationFarmDTO.getLocation(), LocationDTO.class);
        locationDetail.setLocationDTO(locationDTO);
        // Ánh xạ các thuộc tính khác mà bạn quan tâm tại đây.
        return Optional.of(locationDetail);
    }

    @Override
    @Transactional
    public void registerLocationDetailFarm(LocationFarmDTO locationFarmDTO) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            LocationDetail existingLocationDetail = locationDetailRepository.findByNameAndCreatedBy(locationFarmDTO.getLocationDetailDTO().getName(),getCur());
            if (existingLocationDetail != null) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên vị trí đã tồn tại!");
            }
            LocationDetail locationDetail = modelMapper.map(locationFarmDTO.getLocationDetailDTO(), LocationDetail.class);
            Location location = modelMapper.map(locationFarmDTO.getLocationDTO(), Location.class);
            locationDetail.setStatus(true);
            location.setStatus(true);
            Location x = locationRepository.save(location);
            locationDetail.setLocation(x);
            locationDetailRepository.save(locationDetail);
        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }
    }

    @Override
    public void deleteLocationDetail(Long locationId) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            LocationDetail locationDetail = getLocationDetailById(locationId);
            deleteCropPLan(locationDetail);

            checkLocationDetailStatus(locationDetail);
            Location location = locationDetail.getLocation();
            location.setDeleted(Boolean.TRUE);
            locationDetail.setDeleted(Boolean.TRUE);
            locationDetailRepository.save(locationDetail);
            locationRepository.save(location);
        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }

    }

    private void deleteCropPLan(LocationDetail locationDetail) {
        List<CropPlan> cropPlans = cropPlanRepo.getAllByLocationDetail(locationDetail);
        cropPlans.forEach(cropPlan -> cropPlan.setDeleted(Boolean.TRUE));

        Set<Long> cropPlanId = cropPlans.stream().map(CropPlan::getId).collect(Collectors.toSet());
        List<HarvestPlan> harvestPlans = harvestPlanRepo.findHarvestPlansByCropPlanIdIn(cropPlanId);
        harvestPlans.forEach(harvestPlan -> harvestPlan.setDeleted(Boolean.TRUE));
        cropPlanRepo.saveAll(cropPlans);
        harvestPlanRepo.deleteAll(harvestPlans);
    }

    @Transactional
    @Override
    public void deleteLocationDetails(Set<Long> ids) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            List<LocationDetail> locationDetails = locationDetailRepository.findLocationDetailByIdIn(ids);
            if (locationDetails == null) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy vị trí trồng trọt!");
            }
            List<Location> relatedLocations = new ArrayList<>();
            locationDetails.forEach(
                locationDetail -> {
                    deleteCropPLan(locationDetail);
                    checkLocationDetailStatus(locationDetail);
                    locationDetail.setDeleted(true);

                    Location location = locationDetail.getLocation();
                    if (location != null) {
                        location.setDeleted(true);
                        relatedLocations.add(location);
                    }
                }
            );
            locationDetailRepository.saveAll(locationDetails);
            locationRepository.saveAll(relatedLocations);
        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }

    }

    @Override
    public void updateLocationFarm(LocationFarmDTO locationFarmDTO) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            Long locationDetailIdToUpdate = locationFarmDTO.getLocationDetailDTO().getId();
            LocationDetail locationDetail = locationDetailRepository
                .findById(locationDetailIdToUpdate)
                .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy khu vực trồng"));
            LocationDetail existingLocationDetail = locationDetailRepository.findByNameAndCreatedBy(locationFarmDTO.getLocationDetailDTO().getName(),getCur());
            if (existingLocationDetail != null && !Objects.equals(existingLocationDetail.getId(), locationDetailIdToUpdate)) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên vị trí đã tồn tại!");
            }
            Location location = locationDetail.getLocation();
            location.setAddress(locationFarmDTO.getLocationDTO().getAddress());
            location.setLatitude(locationFarmDTO.getLocationDTO().getLatitude());
            location.setLongitude(locationFarmDTO.getLocationDTO().getLongitude());
            location.setStatus(locationFarmDTO.getLocationDetailDTO().getStatus());
            locationRepository.save(location);
            //////////////////////////////////////////////////////////////////////////////////////////
            locationDetail.setNumberOfBeds(locationFarmDTO.getLocationDetailDTO().getNumberOfBeds());
            locationDetail.setName(locationFarmDTO.getLocationDetailDTO().getName());
            locationDetail.setPlantFormat(locationFarmDTO.getLocationDetailDTO().getPlantFormat());
            locationDetail.setArea(locationFarmDTO.getLocationDetailDTO().getArea());
            locationDetail.setStatus(locationFarmDTO.getLocationDetailDTO().getStatus());
            locationDetail.setDescription(locationFarmDTO.getLocationDetailDTO().getDescription());

            locationDetailRepository.save(locationDetail);
        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }

    }

    @Override
    public List<LocationDetailDTO> lstLocationDetailByUserCreate(String curUser) {
        List<LocationDetail> locationDetails = locationDetailRepository.getAllByCreatedBy(curUser);
        List<LocationDetailDTO> locationDetailDTOS = new ArrayList<>();


        for (LocationDetail locationDetail : locationDetails) {
            List<CropPlan> cropPlans = cropPlanRepo.getAllByLocationDetail(locationDetail);

            locationDetailDTOS.add(
                LocationDetailDTO
                    .builder()
                    .address(locationDetail.getLocation().getAddress())
                    .id(locationDetail.getId())
                    .name(locationDetail.getName())
                    .numberOfBeds(locationDetail.getNumberOfBeds())
                    .beds((ArrayList<String>) getListBed(locationDetail.getNumberOfBeds(), null))
                    .build()
            );
        }
        return locationDetailDTOS;
    }

    private List<String> getListBed(Integer numberBed, List<String> lists) {
        if (numberBed == null || numberBed == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= numberBed; i++) {
            list.add("Luống " + i);
        }
        return list;
    }

    private static void checkLocationDetailStatus(LocationDetail locationDetail) {
        if (Boolean.TRUE.equals(locationDetail.getStatus())) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa Kkhu vực đang hoạt động");
        }
    }

    private LocationDetail getLocationDetailById(Long id) {
        return locationDetailRepository.findByIdAndCreatedBy(id, getCur()).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy khu vực sẵn có"));
    }

    private User getUser() {
        String curUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
        return userRepository.findOneByLogin(curUser).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
    }

    private String getCur() {
        String createBy = getUser().getCreatedBy();
        if (createBy.equals("anonymousUser")) {
            createBy = getUser().getEmail();
        }
        return createBy;
    }
}
