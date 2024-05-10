package com.fpt.fms.service;

import com.fpt.fms.domain.ApproveStatus;
import com.fpt.fms.domain.Farm;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.FarmRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.service.dto.FarmDTO;
import com.fpt.fms.service.search.SearchDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FarmServiceTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FarmService farmService;

    private SearchDTO searchDTO;

    private Farm farm1;
    private Farm farm2;
    private Farm farm3;

    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        farm1 = new Farm();
        farm1.setId(1L);
        farm1.setName("Farm 1");
        farm1.setStatus(ApproveStatus.APPROVED);
        User user1 = new User(); // Creating a sample User object
        user1.setFirstName("LH");
        user1.setLastName("Minh");
        farm1.setUser(user1);

        farm2 = new Farm();
        farm2.setId(2L);
        farm2.setName("Farm 2");
        farm2.setStatus(ApproveStatus.REJECT);
        User user2 = new User(); // Creating a sample User object
        user2.setFirstName("LH");
        user2.setLastName("Minh");
        farm2.setUser(user2);

        farm3 = new Farm();
        farm3.setId(3L);
        farm3.setName("Farm 3");
        farm2.setStatus(ApproveStatus.REQUEST);
        User user3 = new User(); // Creating a sample User object
        user3.setFirstName("LH");
        user3.setLastName("Minh");
        farm3.setUser(user3);
    }

    @Test
    public void testGetFarmWhenIdIsNull() {
        Long farmId = null;

        when(farmRepository.findById(farmId)).thenThrow(new EmailAlreadyUsedException("Không tìm thấy trang trại"));
        assertThatThrownBy(() -> farmService.getFarm(farmId))
            .isInstanceOf(EmailAlreadyUsedException.class)
            .hasMessageContaining("Không tìm thấy trang trại");
    }
    @Test
    public void testGetFarmWhenFarmExists() {
        Long farmId = 1L;

        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm1));

        Optional<FarmDTO> result = farmService.getFarm(farmId);

        assertThat(result).isPresent();

        assertThat(result.get().getName()).isEqualTo(farm1.getName());
        assertThat(result.get().getStatus()).isEqualTo(farm1.getStatus());
        verify(farmRepository).findById(farmId);
    }

    @Test
    public void testGetFarmWhenFarmDoesNotExist() {
        Long farmId = 4L;
        when(farmRepository.findById(farmId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> farmService.getFarm(farmId))
            .isInstanceOf(EmailAlreadyUsedException.class)
            .hasMessageContaining("Không tìm thấy trang trại");
    }

    @Test
    public void testDeleteFarmsWhenFarmsExistAndStatusNotApprovedThenDeleteFarms() {
        Set<Long> ids = new HashSet<>(Arrays.asList(2L, 3L));
        List<Farm> farms = Arrays.asList(farm2, farm3);
        farm2.setStatus(ApproveStatus.REJECT);
        farm3.setStatus(ApproveStatus.REQUEST);

        when(farmRepository.findFarmByIdIn(ids)).thenReturn(farms);
        farmService.deleteFarms(ids);

        // Verify that the deleteByIdIn method is called
        verify(farmRepository, times(1)).findFarmByIdIn(ids);
    }

    @Test
    public void testDeleteFarmsWhenFarmsExistAndStatusApprovedThenThrowBaseException() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L));
        List<Farm> farms = Arrays.asList(farm1);
        when(farmRepository.findFarmByIdIn(ids)).thenReturn(farms);
        assertThatThrownBy(() -> farmService.deleteFarms(ids))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining("Không thể xóa trang trại");
    }

    @Test
    public void testDeleteFarmsWhenFarmsNotExistThenNoDelete() {
        Set<Long> ids = new HashSet<>(Arrays.asList(4L, 5L));
        when(farmRepository.findFarmByIdIn(ids)).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> farmService.deleteFarms(ids))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining("Không tìm thấy trang trại");

    }
    @Test
    public void testDeleteFarmsWhenInputIsNullThenThrowException() {
        when(farmRepository.findFarmByIdIn(null)).thenThrow(new IllegalArgumentException("Farm ID cannot be null"));
        assertThatThrownBy(() -> farmService.deleteFarms(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Farm ID cannot be null");
    }
    // Existing test cases...
    @Test
    public void testDeleteFarmWhenInputIsValidThenReturnResult() {
        when(farmRepository.findById(2L)).thenReturn(Optional.of(farm2));
        farmService.deleteFarm(2L);
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    public void testDeleteFarmWhenInputIsValidAndStatusIsApprovedThenThrowException() {
        // Arrange
        when(farmRepository.findById(1L)).thenReturn(Optional.of(farm1));

        // Act & Assert
        assertThatThrownBy(() -> farmService.deleteFarm(1L))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining("Không thể xóa trang trại");
    }


    @Test
    public void testDeleteFarmWhenInputIsNullThenThrowException() {
        when(farmRepository.findById(null)).thenThrow(new IllegalArgumentException("Farm ID cannot be null"));
        assertThatThrownBy(() -> farmService.deleteFarm(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Farm ID cannot be null");
    }

    @Test
    public void testGetFarmsWhenPageNumberIsNegativeThenThrowIllegalArgumentException() {
        int page = -2;
        int size = 2;
        pageable = new Pageable() {
            @Override
            public int getPageNumber() {
                return page;
            }

            @Override
            public int getPageSize() {
                return size;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

        };

        assertThatThrownBy(() -> farmService.getFarms(pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid pagination parameters");
    }

    @Test
    public void testGetFarmsWhenPageSizeIsNegativeThenThrowIllegalArgumentException() {
        int page = 2;
        int size = -2;
        pageable = new Pageable() {
            @Override
            public int getPageNumber() {
                return page;
            }

            @Override
            public int getPageSize() {
                return size;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

        };

        assertThatThrownBy(() -> farmService.getFarms(pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid pagination parameters");
    }

    @Test
    public void testUpdateFarmWhenFarmExistsThenUpdateFarm() {
        FarmDTO farmDTO = new FarmDTO();
        farmDTO.setId(1L);
        farmDTO.setName("Farm Updated");
        farmDTO.setStatus(ApproveStatus.REJECT);
        when(farmRepository.findById(any(Long.class))).thenReturn(Optional.of(farm1));
        when(farmRepository.save(any(Farm.class))).thenReturn(farm1);
        farmService.updateFarm(farmDTO);
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    public void testUpdateFarmWhenFarmDoesNotExistThenThrowException() {
        FarmDTO farmDTO = new FarmDTO();
        farmDTO.setId(4L);
        farmDTO.setName("Farm Updated");
        farmDTO.setStatus(ApproveStatus.REQUEST);
        when(farmRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> farmService.updateFarm(farmDTO))
            .isInstanceOf(EmailAlreadyUsedException.class)
            .hasMessageContaining("Không tìm thấy trang trại");
    }

    @Test
    public void testSearchWhenFarmsMatchThenReturnPageOfFarms() {
        List<Farm> farms = Arrays.asList(farm1);
        Page<Farm> page1 = new PageImpl<>(farms);
        searchDTO = new SearchDTO();
        searchDTO.setApproveStatus("APPROVED");
        searchDTO.setFarmName("Farm 1");

        when(farmRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page1);

        int page = 0;
        int size = 10;
        pageable = PageRequest.of(page, size);

        Page<FarmDTO> result = farmService.search(searchDTO, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(farmRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testSearchWhenFarmsDoNotMatchThenReturnEmptyPage() {
        searchDTO = new SearchDTO();
        searchDTO.setApproveStatus("REJECTED");
        searchDTO.setFarmName("Farm 3");

        Page<Farm> page1 = Page.empty();
        when(farmRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page1);

        int page = 0;
        int size = 10;
        pageable = PageRequest.of(page, size);

        Page<FarmDTO> result = farmService.search(searchDTO, pageable);

        assertThat(result.getContent()).isEmpty();
        verify(farmRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testSearchWhenSearchCriteriaAreNullThenReturnAllFarms() {
        List<Farm> farms = Arrays.asList(farm1, farm2, farm3);
        Page<Farm> page1 = new PageImpl<>(farms);
        searchDTO = new SearchDTO();
        when(farmRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page1);

        int page = 0;
        int size = 10;
        pageable = PageRequest.of(page, size);

        Page<FarmDTO> result = farmService.search(searchDTO, pageable);

        assertThat(result.getContent()).hasSize(3);
        verify(farmRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testSearchWhenPageNumberIsNegativeThenThrowIllegalArgumentException() {
        searchDTO = new SearchDTO();
        int page = -2;
        int size = 2;
        pageable = new Pageable() {
            @Override
            public int getPageNumber() {
                return page;
            }

            @Override
            public int getPageSize() {
                return size;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

        };

        assertThatThrownBy(() -> farmService.search(searchDTO, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid pagination parameters");
    }

    @Test
    public void testSearchWhenPageSizeIsNegativeThenThrowIllegalArgumentException() {
        searchDTO = new SearchDTO();
        int page = 2;
        int size = -2;
        pageable = new Pageable() {
            @Override
            public int getPageNumber() {
                return page;
            }

            @Override
            public int getPageSize() {
                return size;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

        };

        assertThatThrownBy(() -> farmService.search(searchDTO, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid pagination parameters");
    }

    @Test
    public void testGetFarmsWhenFarmsExistThenReturnPageOfFarms() {
        List<Farm> farms = Arrays.asList(farm1, farm2, farm3);
        Page<Farm> page1 = new PageImpl<>(farms);
        when(farmRepository.findAll(any(Pageable.class))).thenReturn(page1);

        int page = 0;
        int size = 10;
        pageable = PageRequest.of(page, size);

        Page<FarmDTO> result = farmService.getFarms(pageable);

        assertThat(result.getContent()).hasSize(3);
        verify(farmRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetFarmsWhenNoFarmsThenReturnEmptyPage() {
        Page<Farm> page1 = Page.empty();
        when(farmRepository.findAll(any(Pageable.class))).thenReturn(page1);
        int page = 0;
        int size = 10;
        pageable = PageRequest.of(page, size);
        Page<FarmDTO> result = farmService.getFarms(pageable);

        assertThat(result.getContent()).isEmpty();
        verify(farmRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetFarmsWhenBothPageSizePageAndIsNegativeThenThrowIllegalArgumentException() {
        int page = -2;
        int size = -2;
        pageable = new Pageable() {
            @Override
            public int getPageNumber() {
                return page;
            }

            @Override
            public int getPageSize() {
                return size;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

        };

        assertThatThrownBy(() -> farmService.getFarms(pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid pagination parameters");
    }

    @Test
    public void testUpdateFarmWhenFarmExistsThenUpdateFarm4() {
        FarmDTO farmDTO = new FarmDTO();
        farmDTO.setId(1L);
        farmDTO.setName("Farm Updated");
        farmDTO.setStatus(ApproveStatus.REQUEST);
        when(farmRepository.findById(any(Long.class))).thenReturn(Optional.of(farm1));
        when(farmRepository.save(any(Farm.class))).thenReturn(farm1);
        farmService.updateFarm(farmDTO);
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    public void testUpdateFarmWhenFarmExistsThenUpdateFarm3() {
        FarmDTO farmDTO = new FarmDTO();
        farmDTO.setId(1L);
        farmDTO.setName("Farm Updated");
        farmDTO.setStatus(ApproveStatus.REQUEST);
        when(farmRepository.findById(any(Long.class))).thenReturn(Optional.of(farm1));
        when(farmRepository.save(any(Farm.class))).thenReturn(farm1);
        farmService.updateFarm(farmDTO);
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    public void testUpdateFarmWhenFarmExistsThenUpdateFarm1() {
        FarmDTO farmDTO = new FarmDTO();
        farmDTO.setId(1L);
        farmDTO.setName("Farm Updated");
        when(farmRepository.findById(any(Long.class))).thenReturn(Optional.of(farm1));
        when(farmRepository.save(any(Farm.class))).thenReturn(farm1);
        farmService.updateFarm(farmDTO);
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    public void testUpdateFarmWhenFarmExistsThenUpdateFarm2() {
        FarmDTO farmDTO = new FarmDTO();
        farmDTO.setId(1L);
        farmDTO.setStatus(ApproveStatus.REQUEST);
        when(farmRepository.findById(any(Long.class))).thenReturn(Optional.of(farm1));
        when(farmRepository.save(any(Farm.class))).thenReturn(farm1);
        farmService.updateFarm(farmDTO);
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    public void testSearchWhenFarmsMatchThenReturnPageOfFarms1() {
        searchDTO = new SearchDTO();
        searchDTO.setApproveStatus("REQUEST");

        List<Farm> farms = Arrays.asList(farm3);
        Page<Farm> page1 = new PageImpl<>(farms);
        when(farmRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page1);
        int page = 0;
        int size = 10;
        pageable = PageRequest.of(page, size);
        Page<FarmDTO> result = farmService.search(searchDTO, pageable);
        assertThat(result.getContent()).hasSize(1);
        verify(farmRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testSearchWhenSearchCriteriaAreNullThenReturnAllFarms1() {
        List<Farm> farms = Arrays.asList(farm1, farm2, farm3);
        Page<Farm> page1 = new PageImpl<>(farms);
        searchDTO = new SearchDTO();
        when(farmRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page1);

        int page = 0;
        int size = 2;
        pageable = PageRequest.of(page, size);

        Page<FarmDTO> result = farmService.search(searchDTO, pageable);

        assertThat(result.getContent()).hasSize(3);
        verify(farmRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testSearchWhenBothPageSizePageAndIsNegativeThenThrowIllegalArgumentException() {
        searchDTO = new SearchDTO();

        int page = -2;
        int size = -2;
        //pageable = PageRequest.of(page, size);
        pageable = new Pageable() {
            @Override
            public int getPageNumber() {
                return page;
            }

            @Override
            public int getPageSize() {
                return size;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

        };
        assertThatThrownBy(() -> farmService.search(searchDTO, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid pagination parameters");
    }

    @Test
    public void testGetFarmsWhenFarmsExistThenReturnPageOfFarms1() {
        List<Farm> farms = Arrays.asList(farm1, farm2, farm3);
        Page<Farm> page1 = new PageImpl<>(farms);
        when(farmRepository.findAll(any(Pageable.class))).thenReturn(page1);

        int page = 1;
        int size = 2;
        pageable = PageRequest.of(page, size);

        Page<FarmDTO> result = farmService.getFarms(pageable);

        assertThat(result.getContent()).hasSize(3);
        verify(farmRepository, times(1)).findAll(pageable);
    }

    // Existing test cases...
}
