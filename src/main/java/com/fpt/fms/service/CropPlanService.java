package com.fpt.fms.service;

import com.fpt.fms.domain.CropPlan;
import com.fpt.fms.domain.HarvestPlan;
import com.fpt.fms.domain.LocationDetail;
import com.fpt.fms.domain.Plant;
import com.fpt.fms.repository.*;
import com.fpt.fms.service.dto.*;
import com.fpt.fms.web.rest.errors.BaseException;

import java.util.*;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CropPlanService {

    @Autowired
    private CropPlanRepo cropPlanRepo;

    @Autowired
    private PlantRepo plantRepo;

    @Autowired
    private PlantDetailRepo plantDetailRepo;

    @Autowired
    private HarvestPlanRepo harvestPlanRepo;

    @Autowired
    private HarvestPlanService harvestPlanService;

    @Autowired
    private LocationDetailRepository locationDetailRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    public void deleteCropPlanById(Long cropPlanId) {
        Optional<CropPlan> cropPlan = cropPlanRepo.findById(cropPlanId);
        if (cropPlan.isPresent()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy kế hoạch");
        }

        List<HarvestPlan> harvestPlans = harvestPlanRepo.getAllByCropPlanId(cropPlanId);

        if (harvestPlans != null) {
            harvestPlanRepo.deleteAll(harvestPlans);
        }
        cropPlanRepo.delete(cropPlan.get());
    }

    @Transactional(rollbackFor = Exception.class)
    public CropPlanDTO createCropPlan(CropPlanDTO cropPlanDTO) {
       Optional<Plant> plant = plantRepo.findById(cropPlanDTO.getPlantId());
        if (!plant.isPresent()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy thông tin cây trồng");
        }

        Optional<LocationDetail> locationDetail = locationDetailRepository.findById(cropPlanDTO.getLocationDetailId());
        if (!locationDetail.isPresent()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy thông tin vị trí trồng");
        }

        CropPlan cropPlan = modelMapper.map(cropPlanDTO, CropPlan.class);
        cropPlan.setDateDone(null);
        cropPlan.setIsDone(false);
        cropPlan.setPlant(plant.get());
        cropPlan.setStatus(true);

        cropPlan.setLocationDetail(locationDetail.get());

        cropPlan = cropPlanRepo.save(cropPlan);
        cropPlanDTO.setId(cropPlan.getId());

        return cropPlanDTO;
    }

    public void updateCropPlan(CropPlanDTO cropPlanDTO) {
        Optional<CropPlan> cropPlan = cropPlanRepo.findById(cropPlanDTO.getId());
        if (!cropPlan.isPresent()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy kế hoạch cây trồng");
        }
        cropPlanRepo.save(
            cropPlan
                .map(
                    cropPlan1 -> {
                        cropPlan1.setStartMethod(cropPlanDTO.getStartMethod());
                        cropPlan1.setGrowthStage(cropPlanDTO.getGrowthStage());
                        cropPlan1.setCompleteAmount(cropPlanDTO.getCompleteAmount());
                        cropPlan1.setSeedlingDate(cropPlanDTO.getSeedlingDate());
                        cropPlan1.setGerminationDate(cropPlanDTO.getGerminationDate());
                        cropPlan1.setCompleteDate(cropPlanDTO.getCompleteDate());
                        cropPlan1.setRipeningDate(cropPlanDTO.getRipeningDate());
                        cropPlan1.setDateToHarvest(cropPlanDTO.getDateToHarvest());
                        cropPlan1.setFromDate(cropPlanDTO.getFromDate());
                        cropPlan1.setToDate(cropPlanDTO.getToDate());
                        cropPlan1.setSowAmount(cropPlanDTO.getSowAmount());
                        cropPlan1.setPlantDepth(cropPlanDTO.getPlantDepth());
                        cropPlan1.setPlantSpace(cropPlanDTO.getPlantSpace());
                        cropPlan1.setRowSpace(cropPlanDTO.getRowSpace());
                        cropPlan1.setSowDate(cropPlanDTO.getSowDate());
                        cropPlan1.setSession(cropPlanDTO.getSession());
                        cropPlan1.setExpectedAmount(cropPlanDTO.getExpectedAmount());
                        cropPlan1.setGerminationAmount(cropPlanDTO.getGerminationAmount());
                        cropPlan1.setFlowerAmount(cropPlanDTO.getFlowerAmount());
                        cropPlan1.setFlowerDate(cropPlanDTO.getFlowerDate());
                        cropPlan1.setCompleteAmount(cropPlanDTO.getCompleteAmount());
                        cropPlan1.setRipeningAmount(cropPlanDTO.getRipeningAmount());
                        cropPlan1.setSeedlingAmount(cropPlanDTO.getSeedlingAmount());
                        cropPlan1.setSeedStarted(cropPlanDTO.getSeedStarted());

                        return cropPlan1;
                    }
                )
                .get()
        );
    }

    @Transactional(readOnly = true)
    public List<CropPlanDTO> getLstPlanByUserCurrent(String curUser, Integer year, String name, Long status) {
        List<CropPlan> cropPlans = cropPlanRepo.findAllByCreatedByAndStatus(curUser, year);

        List<CropPlanDTO> cropPlanDTOS = new ArrayList<>();
        if((name != null && !name.trim().isEmpty()) && !cropPlans.isEmpty()){
            cropPlans = cropPlans.stream().filter(filter -> filter.getPlant().getName().toUpperCase(Locale.ROOT).contains(name.toUpperCase(Locale.ROOT))).collect(Collectors.toList());
        }
        if(status != null && status == 1){
            cropPlans = cropPlans.stream().filter(CropPlan::getIsDone).collect(Collectors.toList());
        }
        if(status != null && status == 0){
            cropPlans = cropPlans.stream().filter(cropPlan -> !cropPlan.getIsDone()).collect(Collectors.toList());
        }
        for (CropPlan cropPlan : cropPlans) {
            CropPlanDTO cropPlanDTO = modelMapper.map(cropPlan, CropPlanDTO.class);

            cropPlanDTO.setPlantDTO(modelMapper.map(cropPlan.getPlant(), PlantDTO.class));

            LocationDetailDTO locationDetailDTO = modelMapper.map(cropPlan.getLocationDetail(), LocationDetailDTO.class);
            locationDetailDTO.setAddress(cropPlan.getLocationDetail().getLocation().getAddress());

            cropPlanDTO.setLocationDetailDTO(locationDetailDTO);

            cropPlanDTOS.add(cropPlanDTO);
        }
        cropPlanDTOS.sort(Comparator.nullsFirst(Comparator.comparing(CropPlanDTO::getTotalHarvestAmount,
                                                Comparator.nullsFirst(Integer::compareTo))));
        return cropPlanDTOS;
    }

    public LocationDetailDTO getStaticCropPlanByPlantId(Long locationDetailId) {
        LocationDetail locationDetail = locationDetailRepository
            .findById(locationDetailId)
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy vị trí trồng"));

        List<CropPlan> cropPlans = cropPlanRepo.findAllByLocationDetail(locationDetail);
        LocationDetailDTO locationDetailDTO = modelMapper.map(locationDetail, LocationDetailDTO.class);
        ArrayList<CropPlanDTO> cropPlanDTOS = new ArrayList<>();

        for (CropPlan cropPlan : cropPlans) {
            Plant plant = cropPlan.getPlant();
            CropPlanDTO cropPlanDTO = modelMapper.map(cropPlan, CropPlanDTO.class);
            cropPlanDTO.setNamePlant(plant.getName());
            cropPlanDTO.setTotalHarvestAmount(harvestPlanRepo.countAllByCropPlanId(cropPlan.getId()));
            cropPlanDTOS.add(cropPlanDTO);
        }
        locationDetailDTO.setCropPlanDTO(cropPlanDTOS);

        return locationDetailDTO;
    }

    @Transactional(readOnly = true)
    public CropPlanDTO getCropPlanDetailById(Long cropPlanId) {
        Optional<CropPlan> cropPlan = cropPlanRepo.findById(cropPlanId);
        if (!cropPlan.isPresent()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy thông tin kế hoạch");
        }
        Plant plant = cropPlan.get().getPlant();
        LocationDetail locationDetail = cropPlan.get().getLocationDetail();

        CropPlanDTO cropPlanDTO = modelMapper.map(cropPlan.get(), CropPlanDTO.class);
        PlantDTO plantDTO = modelMapper.map(plant, PlantDTO.class);
        plantDTO.setPlantDetailDTO(modelMapper.map(plantDetailRepo.findPlantDetailByPlantId(plant.getId()).orElse(null), PlantDetailDTO.class));
        cropPlanDTO.setPlantDTO(plantDTO);

        LocationDetailDTO locationDetailDTO = (modelMapper.map(locationDetail, LocationDetailDTO.class));
        locationDetailDTO.setAddress(locationDetail.getLocation().getAddress());

        cropPlanDTO.setLocationDetailDTO(locationDetailDTO);
        cropPlanDTO.setTotalHarvestAmount(harvestPlanRepo.countAllByCropPlanId(cropPlanId));
        cropPlanDTO.setHarvestPlanDTOS(harvestPlanService.lstAllByCropPlan(cropPlanId));

        return cropPlanDTO;
    }

    public Map<Object, Object> getReportPlantById(Long plantId, List<String> year){
        List<HarvestPlan> harvestPlans = harvestPlanRepo.findAllByPlantId(plantId);
        Map<Object, Object> map1 = new HashMap<>();
        for (String y : year){
            List<HarvestPlan> harvestPlans1 = harvestPlans.stream().filter(filter -> {
                if(filter.getDateHarvest() == null){
                    return false;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(filter.getDateHarvest());

                return y.equals(calendar.get(Calendar.YEAR) + "");
            }).collect(Collectors.toList());
            Map<Object, Object> map = new HashMap<>();

            map.put("total", harvestPlans1.stream().mapToLong(value -> value.getAmount() == null ? 0 : value.getAmount()).sum());
            for (int i = 1; i <= 12 ; i ++){
                int finalI = i;
                List<HarvestPlan> harvestPlans2 = harvestPlans1.stream().filter(filter -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(filter.getDateHarvest());

                    return finalI == (calendar.get(Calendar.MONTH)+1);
                }).collect(Collectors.toList());
                if(harvestPlans2 == null){
                    map.put(finalI+"", 0);
                }else {
                    map.put(finalI+"", harvestPlans2.stream().mapToLong(value -> value.getAmount() == null ? 0 : value.getAmount()).sum());
                }
            }

            map1.put(y+"", map);

        }

        return map1;
    }


    public void updateStatusCropPlan(Integer status, Long cropPlanId) {
        CropPlan cropPlan = cropPlanRepo.findById(cropPlanId)
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy kế hoạch"));
        if(status == 0){
            cropPlan.setIsDone(false);
            cropPlan.setDateDone(null);
        }else {
            cropPlan.setIsDone(true);
            cropPlan.setDateDone(new Date());
        }
        cropPlanRepo.save(cropPlan);
    }

}
