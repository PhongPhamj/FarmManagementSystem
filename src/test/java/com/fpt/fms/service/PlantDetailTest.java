package com.fpt.fms.service;

import com.fpt.fms.domain.HarvestUnit;
import com.fpt.fms.domain.PlantCategory;
import com.fpt.fms.service.dto.PlantDTO;
import com.fpt.fms.service.dto.PlantDetailDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class PlantDetailTest {

    private PlantDTO plantDTO;

    @Mock
    private PlantDetailService plantDetailService;

    @BeforeEach
    public void setUp() {
        plantDTO = PlantDTO.builder()
            .id(1L)
            .name("plant")
            .provider("provider")
            .source("source")
            .status(true)
            .type(PlantCategory.FLOWER)
            .plantDetailDTO(PlantDetailDTO.builder()
                .dayToHarvest(40)
                .dayToMature(20)
                .description("escription")
                .lossRate(29)
                .harvestUnit(HarvestUnit.BALE)
                .rateGermination(20)
                .rowSpace(20)
                .rateGermination(20)
                .build())
            .build();
    }
    @Test
    public void testCreatePlantDetailWhenNameIsNullThenThrowException(){
        plantDTO.setName(null);
        assertThatThrownBy(() -> plantDetailService.createPlantDetail(plantDTO))
            .isInstanceOf(BaseException.class);
    }
    @Test
    public void testCreatePlantDetailWhenPlantDetailIsNullThenThrowException(){
        plantDTO.setName(null);
        assertThatThrownBy(() -> plantDetailService.createPlantDetail(plantDTO))
            .isInstanceOf(BaseException.class);
    }

    @Test
    public void testUpdatePlantDetailWhenIdPlantDetailNotExistThenThrowException(){
        plantDTO.setName("anh");
        assertThatThrownBy(() -> plantDetailService.updatePlantDetail(0L, plantDTO.getPlantDetailDTO()))
            .isInstanceOf(BaseException.class);
    }
}
