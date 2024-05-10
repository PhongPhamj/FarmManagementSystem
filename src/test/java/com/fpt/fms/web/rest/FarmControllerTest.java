package com.fpt.fms.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.fms.domain.ApproveStatus;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.service.FarmService;
import com.fpt.fms.service.dto.FarmDTO;
import com.fpt.fms.service.search.SearchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FarmControllerTest {
    private FarmController farmController;
    private FarmService farmService;
    private Pageable pageable;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        farmService = mock(FarmService.class);
        farmController = new FarmController(farmService);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(farmController)
            .build();
    }

    // Existing tests...


    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void testGetSearchFarmsWhenSearchReturnsNonEmptyPageThenReturnFarmDTOs() throws Exception {
        List<FarmDTO> farmDTOList = new ArrayList<>();
        farmDTOList.add(new FarmDTO());
        Page<FarmDTO> farmDTOPage = new PageImpl<>(farmDTOList);
        SearchDTO searchDTO = new SearchDTO();
        pageable = PageRequest.of(0, 10);
        when(farmService.search(eq(searchDTO), eq(pageable))).thenReturn(farmDTOPage);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/farm/search")
                .content(objectMapper.writeValueAsString(searchDTO))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())))
            .andExpect(status().isOk())
            .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        List<FarmDTO> responseList = objectMapper.readValue(responseContent, new TypeReference<>() {
        });

        ResponseEntity<List<FarmDTO>> response = new ResponseEntity<>(responseList, HttpStatus.OK);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        for (int i = 0; i < farmDTOList.size(); i++) {
            FarmDTO expectedFarmDTO = farmDTOList.get(i);
            FarmDTO actualFarmDTO = responseList.get(i);

            assertThat(expectedFarmDTO.getId()).isEqualTo(actualFarmDTO.getId());
            assertThat(expectedFarmDTO.getName()).isEqualTo(actualFarmDTO.getName());
            assertThat(expectedFarmDTO.getOwner()).isEqualTo(actualFarmDTO.getOwner());
            assertThat(expectedFarmDTO.getEmail()).isEqualTo(actualFarmDTO.getEmail());
            assertThat(expectedFarmDTO.getStatus()).isEqualTo(actualFarmDTO.getStatus());
            assertThat(expectedFarmDTO.getCreatedDate()).isEqualTo(actualFarmDTO.getCreatedDate());
        }
        verify(farmService, times(1)).search(searchDTO, pageable);
    }

//    @Test
//    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
//    void testGetSearchFarmsWhenSearchReturnsEmptyPageThenReturnEmptyList() throws Exception {
//        List<FarmDTO> farmDTOList = new ArrayList<>();
//        Page<FarmDTO> farmDTOPage = new PageImpl<>(farmDTOList);
//        SearchDTO searchDTO = new SearchDTO();
//        when(farmService.search(searchDTO, pageable)).thenReturn(farmDTOPage);
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/farm/search")
//                .param("page", String.valueOf(pageable.getPageNumber()))
//                .param("size", String.valueOf(pageable.getPageSize()))
//                .content(objectMapper.writeValueAsString(searchDTO))
//                .contentType("application/json"))
//            .andExpect(status().isOk())
//            .andReturn();
//
//        String responseContent = result.getResponse().getContentAsString();
//        List<FarmDTO> responseList = objectMapper.readValue(responseContent, new TypeReference<>() {
//        });
//
//        ResponseEntity<List<FarmDTO>> response = new ResponseEntity<>(responseList, HttpStatus.OK);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isEqualTo(farmDTOList);
//
//        verify(farmService, times(1)).search(searchDTO, pageable);
//    }

//    @Test
//    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
//    void testGetSearchFarmsWhenSearchThrowsExceptionThenReturnBadRequest() throws Exception {
//        SearchDTO searchDTO = new SearchDTO();
//        when(farmService.search(searchDTO, pageable)).thenThrow(new RuntimeException());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/farm/search")
//                .param("page", String.valueOf(pageable.getPageNumber()))
//                .param("size", String.valueOf(pageable.getPageSize()))
//                .content(objectMapper.writeValueAsString(searchDTO))
//                .contentType("application/json"))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//    }

//    @Test
//    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
//    void testGetFarmsWhenFarmServiceReturnsNonEmptyPageOfFarmsThenReturnFarms() throws Exception {
//        List<FarmDTO> farmDTOList = new ArrayList<>();
//        farmDTOList.add(new FarmDTO(1L, "Farm 1", "First", "Owner1", null, "owner1@example.com",Date.from(Instant.now()), ApproveStatus.APPROVED, null));
//        farmDTOList.add(new FarmDTO(2L, "Farm 2", "Second", "Owner2", null, "owner2@example.com", new Date(), ApproveStatus.REJECT, null));
//        farmDTOList.add(new FarmDTO(3L, "Farm 3", "Third", "Owner3", null, "owner3@example.com", new Date(), ApproveStatus.REJECT, null));
//        Page<FarmDTO> farmDTOPage = new PageImpl<>(farmDTOList);
//        pageable = PageRequest.of(0, 10);
//        when(farmService.getFarms(pageable)).thenReturn(farmDTOPage);
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/farm/findallfarm")
//                .param("page", String.valueOf(pageable.getPageNumber()))
//                .param("size", String.valueOf(pageable.getPageSize())))
//            .andExpect(status().isOk())
//            .andReturn();
//
//        String responseContent = result.getResponse().getContentAsString();
//        List<FarmDTO> responseList = objectMapper.readValue(responseContent, new TypeReference<>() {
//        });
//
//        ResponseEntity<List<FarmDTO>> response = new ResponseEntity<>(responseList, HttpStatus.OK);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        // Kiểm tra từng trường dữ liệu của FarmDTO
//        for (int i = 0; i < farmDTOList.size(); i++) {
//            FarmDTO expectedFarmDTO = farmDTOList.get(i);
//            FarmDTO actualFarmDTO = responseList.get(i);
//
//            assertThat(expectedFarmDTO.getId()).isEqualTo(actualFarmDTO.getId());
//            assertThat(expectedFarmDTO.getName()).isEqualTo(actualFarmDTO.getName());
//            assertThat(expectedFarmDTO.getOwner()).isEqualTo(actualFarmDTO.getOwner());
//            assertThat(expectedFarmDTO.getEmail()).isEqualTo(actualFarmDTO.getEmail());
//            assertThat(expectedFarmDTO.getStatus()).isEqualTo(actualFarmDTO.getStatus());
//            assertThat(expectedFarmDTO.getCreatedDate()).isEqualTo(actualFarmDTO.getCreatedDate());
//        }
//        verify(farmService, times(1)).getFarms(pageable);
//    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void testGetFarmsWhenFarmServiceReturnsLimitPageOfFarmsThenReturnFarms() throws Exception {
        List<FarmDTO> farmDTOList = new ArrayList<>();
        farmDTOList.add(new FarmDTO());
        Page<FarmDTO> farmDTOPage = new PageImpl<>(farmDTOList);
        pageable = PageRequest.of(1, 1);
        when(farmService.getFarms(pageable)).thenReturn(farmDTOPage);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/farm/findallfarm")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())))
            .andExpect(status().isOk())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<FarmDTO> responseList = objectMapper.readValue(responseContent, new TypeReference<>() {
        });

        ResponseEntity<List<FarmDTO>> response = new ResponseEntity<>(responseList, HttpStatus.OK);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Kiểm tra từng trường dữ liệu của FarmDTO
        for (int i = 0; i < farmDTOList.size(); i++) {
            FarmDTO expectedFarmDTO = farmDTOList.get(i);
            FarmDTO actualFarmDTO = responseList.get(i);

            assertThat(expectedFarmDTO.getId()).isEqualTo(actualFarmDTO.getId());
            assertThat(expectedFarmDTO.getName()).isEqualTo(actualFarmDTO.getName());
            assertThat(expectedFarmDTO.getOwner()).isEqualTo(actualFarmDTO.getOwner());
            assertThat(expectedFarmDTO.getEmail()).isEqualTo(actualFarmDTO.getEmail());
            assertThat(expectedFarmDTO.getStatus()).isEqualTo(actualFarmDTO.getStatus());
            assertThat(expectedFarmDTO.getCreatedDate()).isEqualTo(actualFarmDTO.getCreatedDate());
        }
        verify(farmService, times(1)).getFarms(pageable);
    }


    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void testGetFarmsWhenCalledWithInvalidPageAndSizeParametersThenReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/farm/findallfarm")
                .param("page", "-2")
                .param("size", "2"))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void testGetFarmsWhenCalledWithPageAndInvalidSizeParametersThenReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/farm/findallfarm")
                .param("page", "2")
                .param("size", "-2"))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void testGetFarmsWhenCalledWithInvalidPageAndInvalidSizeParametersThenReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/farm/findallfarm")
                .param("page", "-2")
                .param("size", "-2"))
            .andExpect(status().isBadRequest())
            .andReturn();
    }
}
