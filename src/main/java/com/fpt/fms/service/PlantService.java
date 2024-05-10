package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.*;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.dto.HarvestPlanDTO;
import com.fpt.fms.service.dto.PlantDTO;
import com.fpt.fms.service.dto.PlantDetailDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PlantService {

    @Autowired
    private PlantRepo plantRepo;

    @Autowired
    private HarvestPlanRepo harvestPlanRepo;

    @Autowired
    private LocationDetailRepository locationDetailRepository;

    @Autowired
    private CropPlanRepo cropPlanRepo;

    @Autowired
    private PlantDetailRepo plantDetailRepo;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<PlantDTO> getListPlantByUserCurrent(String userEmail, String namePlant) {
        List<Plant> plants = plantRepo.findAllByCreatedByAndStatusAndNamePlant(userEmail, namePlant);

        List<PlantDTO> plantDTOS = new ArrayList<>();

        for (Plant plant : plants) {
            Optional<PlantDetail> plantDetail = plantDetailRepo.findPlantDetailByPlantId(plant.getId());

            PlantDTO plantDTO = modelMapper.map(plant, PlantDTO.class);
            PlantDetail plantDetail1 = plantDetailRepo.findPlantDetailByPlantId(plant.getId()).orElse(null);

            if (plantDetail1 != null) {
                plantDTO.setPlantDetailDTO(modelMapper.map(plantDetail1, PlantDetailDTO.class));
            }

            if (plantDetail.isPresent()) {
                plantDTO.setHarvestUnit(plantDetail.get().getHarvestUnit());
            }
            plantDTOS.add(plantDTO);
        }

        return plantDTOS;
    }

    @Transactional(rollbackFor = Exception.class)
    public PlantDTO createPlant(PlantDTO plantDTO) {
        if (plantDTO.getPlantDetailDTO() == null) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không có thông tin cây trồng");
        }
        Plant plant = new Plant();
        Plant existingPlant = plantRepo.findByNameAndCreatedBy(plantDTO.getName(), getCur());
        if (existingPlant != null && existingPlant.getSource().trim().equals(plantDTO.getSource().trim())) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên cây trồng và nguồn gốc đã tồn tại!");
        }
        plant.setName(plantDTO.getName());
        plant.setStatus(true);
        plant.setProvider(plantDTO.getProvider());
        plant.setType(plantDTO.getType());
        plant.setSource(plantDTO.getSource());

        Plant plant1 = plantRepo.save(plant);

        PlantDetailDTO plantDetailDTO = plantDTO.getPlantDetailDTO();
        PlantDetail plantDetail = modelMapper.map(plantDetailDTO, PlantDetail.class);
        plantDetail.setPlant(plant1);
        plantDetailRepo.save(plantDetail);
        return plantDTO;
    }

    public List<PlantDTO> getListPlant(String namePlant) {
        String name = "%" + namePlant + "%";
        List<Plant> plants = plantRepo.findPlantsByNameAndSourceCombined(name);

        List<PlantDTO> plantDTOS = new ArrayList<>();

        for (Plant plant : plants) {
            PlantDTO plantDTO = modelMapper.map(plant, PlantDTO.class);
            plantDTO.setName(plant.getName()+" - "+ plantDTO.getSource());
            plantDTOS.add(plantDTO);
        }

        return plantDTOS;
    }
    public void updatePlant(PlantDTO plantDTO) {
        Optional<Plant> plant = plantRepo.findById(plantDTO.getId());
        if (!plant.isPresent()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy cây trồng");
        }
        Optional<PlantDetail> plantDetail = plantDetailRepo.findById(plantDTO.getPlantDetailDTO().getId());
        if (!plantDetail.isPresent()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy thông tin chi tiết cây trồng");
        }
        Plant existingPlant = plantRepo.findByNameAndCreatedBy(plantDTO.getName(),getCur());
        if (
            existingPlant != null &&
                !Objects.equals(existingPlant.getId(), plantDTO.getId()) &&
                existingPlant.getSource().trim().equals(plantDTO.getSource().trim())
        ) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên cây trồng và nguồn gốc đã tồn tại!");
        }
        plantRepo.save(
            plant
                .map(
                    plant1 -> {
                        plant1.setProvider(plantDTO.getProvider());
                        plant1.setSource(plantDTO.getSource());
                        plant1.setName(plantDTO.getName());
                        plant1.setType(plantDTO.getType());
                        return plant1;
                    }
                )
                .get()
        );

        plantDetailRepo.save(
            plantDetail
                .map(
                    p -> {
                        p.setLossRate(plantDTO.getPlantDetailDTO().getLossRate());
                        p.setHarvestUnit(plantDTO.getPlantDetailDTO().getHarvestUnit());
                        p.setDescription(plantDTO.getPlantDetailDTO().getDescription());
                        p.setDayToHarvest(plantDTO.getPlantDetailDTO().getDayToHarvest());
                        p.setPlantSpace(plantDTO.getPlantDetailDTO().getPlantSpace());
                        p.setRateGermination(plantDTO.getPlantDetailDTO().getRateGermination());
                        p.setRowSpace(plantDTO.getPlantDetailDTO().getRowSpace());
                        p.setStartMethod(plantDTO.getPlantDetailDTO().getStartMethod());
                        p.setDayToEmerge(plantDTO.getPlantDetailDTO().getDayToEmerge());
                        p.setDayToMature(plantDTO.getPlantDetailDTO().getDayToMature());
                        p.setPlantDepth(plantDTO.getPlantDetailDTO().getPlantDepth());
                        return p;
                    }
                )
                .get()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePlant(Long plantId) {
        Optional<Plant> plant = plantRepo.findById(plantId);
        if (!plant.isPresent()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy cây trồng hiện tại");
        }
        Optional<PlantDetail> plantDetail = plantDetailRepo.findPlantDetailByPlantId(plantId);
        List<CropPlan> cropPlans = cropPlanRepo.findAllByPlantId(plantId);
        List<HarvestPlan> harvestPlans = harvestPlanRepo.getAllByPlanId(plantId);
        if (!harvestPlans.isEmpty()) {
            harvestPlanRepo.deleteAll(harvestPlans);
        }
        if (!cropPlans.isEmpty()) {
            cropPlanRepo.deleteAll(cropPlans);
        }
        plantDetailRepo.delete(plantDetail.get());
        plantRepo.delete(plant.get());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePlants(Set<Long> ids) {
        List<Plant> plants = plantRepo.findAllByIdIn(ids);
        validate(plants);
        plants.forEach(plant -> plant.setDeleted(Boolean.TRUE));
        plantRepo.saveAll(plants);
    }

    private void validate(List<Plant> plants) {
        List<PlantDetail> plantDetails = plantDetailRepo.findAllByPlantIn(plants);
        plantDetails.forEach(
            plantDetail -> {
                if (Objects.isNull(plantDetail)) {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Cây trồng không tồn tại");
                }
            }
        );
        List<CropPlan> cropPlans = cropPlanRepo.findAllByPlantIn(plants);
        cropPlans.forEach(
            cropPlan -> {
                if (!Objects.isNull(cropPlan)) {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa cây đang có kế hoach");
                }
            }
        );
        List<HarvestPlan> harvestPlans = harvestPlanRepo.findAllByCropPlanIn(cropPlans);
        harvestPlans.forEach(
            harvestPlan -> {
                if (!Objects.isNull(harvestPlan)) {
                    throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa cây đang có kế hoach");
                }
            }
        );
    }

    public List<PlantDTO> listStaticPlantByCurrentUser(String curUser, Long year) {
        List<Plant> plants = plantRepo.findAllByCreatedBy(curUser);
        if (plants == null || plants.isEmpty()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy danh sách cây trồng của người dùng hiện tại");
        }

        List<PlantDTO> plantDTOS = new ArrayList<>();

        for (Plant plant : plants) {
            PlantDTO plantDTO = modelMapper.map(plant, PlantDTO.class);
            PlantDetail plantDetail = plantDetailRepo.findPlantDetailByPlantId(plant.getId()).orElse(null);
            if (plantDetail != null) {
                plantDTO.setHarvestUnit(plantDetail.getHarvestUnit());
            }

            List<HarvestPlan> harvestPlans = harvestPlanRepo.findAllByPlantId(plant.getId(), year);
            if (harvestPlans != null) {
                List<HarvestPlanDTO> harvestPlanDTOS = new ArrayList<>();
                Map<Integer, List<HarvestPlan>> harvestPlanMap = harvestPlans
                    .stream()
                    .collect(Collectors.groupingBy(harvestPlan -> harvestPlan.getDateHarvest().getMonth() + 1));

                for (Map.Entry<Integer, List<HarvestPlan>> entry : harvestPlanMap.entrySet()) {
                    harvestPlanDTOS.add(
                        HarvestPlanDTO
                            .builder()
                            .amount(entry.getValue().stream().mapToInt(HarvestPlan::getAmount).sum())
                            .dateHarvest(entry.getValue().get(0).getDateHarvest())
                            .build()
                    );
                }
                plantDTO.setHarvestPlanDTOS(harvestPlanDTOS);
            }
            plantDTOS.add(plantDTO);
        }
        return plantDTOS;
    }

    public List<Map<Object, Object>> listStaticPlantByCurrentUser2(String curUser, Long year) {
        List<Plant> plants = plantRepo.findAllByCreatedBy(curUser);
        if (plants == null || plants.isEmpty()) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy danh sách cây trồng của người dùng hiện tại");
        }

        List<Map<Object, Object>> result = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Map<Object, Object> map = new HashMap<>();
            map.put("month", i);
            result.add(map);
        }

        for (Plant plant : plants) {
            List<HarvestPlan> harvestPlans = harvestPlanRepo.findAllByPlantId(plant.getId(), year);
            Map<Integer, List<HarvestPlan>> harvestPlanMap = harvestPlans
                .stream()
                .collect(Collectors.groupingBy(harvestPlan -> harvestPlan.getDateHarvest().getMonth() + 1));

            for (Map.Entry<Integer, List<HarvestPlan>> entry : harvestPlanMap.entrySet()) {
                if (result.stream().anyMatch(filter -> filter.get("month").toString().equals(entry.getKey().toString()))) {
                    Map<Object, Object> map = new HashMap<>();
                    map.put("unit", plantDetailRepo.findPlantDetailByPlantId(plant.getId()).get().getHarvestUnit().name());
                    map.put("amount", entry.getValue().stream().mapToInt(HarvestPlan::getAmount).sum());

                    result
                        .stream()
                        .filter(filter -> filter.get("month").toString().equals(entry.getKey().toString()))
                        .findFirst()
                        .get()
                        .put(plant.getName(), map);
                }
            }
        }

        return result;
    }

    public Map<Object, List<Map<Object, Object>>> getStaticProductPlant(Long plantId, List<String> year) {
        List<CropPlan> cropPlans = cropPlanRepo.findAllByPlantId(plantId);
        Map<Object, List<Map<Object, Object>>> result = new HashMap<>();
        for (String i : year) {
            result.put(i, new ArrayList<>());
        }
        Map<String, List<CropPlan>> map = cropPlans
            .stream()
            .collect(
                Collectors.groupingBy(
                    cropPlan -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(cropPlan.getFromDate());

                        return calendar.get(Calendar.YEAR) + "";
                    }
                )
            );
        for (Map.Entry<String, List<CropPlan>> entry : map.entrySet()) {
            if (result.containsKey(entry.getKey())) {
                Map<Long, List<CropPlan>> map2 = entry
                    .getValue()
                    .stream()
                    .collect(Collectors.groupingBy(cropPlan -> cropPlan.getLocationDetail().getId()));
                Map<Object, Object> map1 = new HashMap<>();

                for (Map.Entry<Long, List<CropPlan>> entry1 : map2.entrySet()) {
                    Map<Object, Object> map3 = new HashMap<>();
                    if (
                        entry
                            .getValue()
                            .stream()
                            .anyMatch(cropPlan -> cropPlan.getLocationDetail().getId().longValue() == entry1.getKey().longValue())
                    ) {
                        map3.put(
                            "total",
                            entry1
                                .getValue()
                                .stream()
                                .mapToLong(
                                    value -> {
                                        Integer i = harvestPlanRepo.countAllByCropPlanId(value.getId());
                                        return i == null ? 0 : i.longValue();
                                    }
                                )
                                .sum()
                        );
                        Arrays
                            .stream(Session.values())
                            .forEach(
                                session ->
                                    map3.put(
                                        session.name(),
                                        entry1
                                            .getValue()
                                            .stream()
                                            .filter(
                                                filter -> filter.getSession() != null && session.name().equals(filter.getSession().name())
                                            )
                                            .mapToLong(
                                                value -> {
                                                    Integer i = harvestPlanRepo.countAllByCropPlanId(value.getId());
                                                    return i == null ? 0 : i.longValue();
                                                }
                                            )
                                            .sum()
                                    )
                            );
                        map1.put(
                            entry1.getValue().get(0).getLocationDetail().getName() +
                                "-" +
                                entry1.getValue().get(0).getLocationDetail().getId(),
                            map3
                        );
                    }
                }
                result.get(entry.getKey()).add(map1);
            }
        }
        return result;
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
