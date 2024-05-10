package com.fpt.fms.service;

import com.fpt.fms.domain.PlantCategory;
import com.fpt.fms.service.dto.PlantDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class PlantServiceTest {

    private PlantDTO plantDTO;

    @Mock
    private PlantService plantService;

    @BeforeEach
    public void setUp() {
        plantDTO = PlantDTO.builder()
            .id(1L)
            .name("plant")
            .provider("provider")
            .source("source")
            .status(true)
            .type(PlantCategory.FLOWER)
            .build();
    }

    @Test
    public void testCreatePlantWhenTypePlantNullNotThrowException(){
        plantDTO.setProvider(null);
        plantDTO.setName(null);
        plantDTO.setSource(null);
        assertThatThrownBy(() -> plantService.createPlant(plantDTO))
            .isNotInstanceOf(BaseException.class);
    }

    @Test
    public void testCreatePlantWhenFieldsIsNull(){
        assertThatThrownBy(() -> plantService.createPlant(plantDTO))
            .isInstanceOf(BaseException.class);
    }

    @Test
    public void testUpdatePlantWhenNotFoundIdThenThrowException(){
        plantDTO.setId(0L);
        assertThatThrownBy(() -> plantService.createPlant(plantDTO))
            .isInstanceOf(BaseException.class);
    }

    @Test
    public void testDeletePlantWhenNotFoundIdThenThrowException(){
        assertThatThrownBy(() -> plantService.deletePlant(1L))
            .isInstanceOf(BaseException.class);
    }

    @Test
    public void testDeleteMultiplePlantWhenNotFoundIdThenThrowException(){
        assertThatThrownBy(() -> plantService.deletePlants(Set.of(0L, -1L,-2L)))
            .isInstanceOf(BaseException.class);
    }
    @Test
    public void testSearchListPlantByNameWhenNullHaveReturn(){
        assertThat(plantService.getListPlant(null)).isNotNull();
    }
    @Test
    public void testSearchListPlantByNameWhenEmptyHaveReturn(){
        assertThat(plantService.getListPlant(null)).isNotNull();
    }

    @Test
    public void listStaticPlantByCurrentUserWhenUserNullAndYearNullThrowException(){
        assertThatThrownBy(() -> plantService.listStaticPlantByCurrentUser2(null, null))
            .isInstanceOf(BaseException.class);
    }
    @Test
    public void listStaticPlantByCurrentUserWhenUserNullAndYearNotNullThrowException(){
        assertThatThrownBy(() -> plantService.listStaticPlantByCurrentUser2(null, 2022L))
            .isInstanceOf(BaseException.class);
    }
    @Test
    public void listStaticPlantByCurrentUserWhenUserNotNullAndYearNullThrowException(){
        assertThatThrownBy(() -> plantService.listStaticPlantByCurrentUser2("anh", 2022L))
            .isInstanceOf(BaseException.class);
    }

    @Test
    public void getStaticProductPlantWhenPlantIdNullThrowException(){
        assertThatThrownBy(() -> plantService.getStaticProductPlant(null, Arrays.asList("2022")))
            .isInstanceOf(BaseException.class);
    }
    @Test
    public void getStaticProductPlantWhenPlantIdIncorrectThenThrowException(){
        assertThatThrownBy(() -> plantService.getStaticProductPlant(0L, Arrays.asList("2022")))
            .isInstanceOf(BaseException.class);
    }
}
