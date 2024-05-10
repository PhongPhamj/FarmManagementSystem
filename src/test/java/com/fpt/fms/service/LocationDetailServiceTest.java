package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.LocationDetailRepository;
import com.fpt.fms.repository.LocationRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.dto.LocationDTO;
import com.fpt.fms.service.dto.LocationDetailDTO;
import com.fpt.fms.service.dto.LocationFarmDTO;
import com.fpt.fms.service.search.SearchLocationDetailDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationDetailServiceTest {
    @Mock
    private UserDetails userDetails;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private LocationDetailRepository locationDetailRepository;

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LocationDetailService locationDetailService;

    private LocationDetail locationDetail;
    private Location location;
    private LocationDTO locationDTO;
    private LocationDetailDTO locationDetailDTO;
    @Mock
    private User user2;
    @Mock
    private Authority userAuthority2;

    @BeforeEach
    public void setUp() {
//Autho - User
        userAuthority2 = new Authority(); // Thay bằng cách tạo từ chuỗi thực tế
        userAuthority2.setName(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(userAuthority2); // Thêm Authority vào Set
        user2 = new User();
        user2.setActivated(true);
        user2.setAuthorities(authorities);
        user2.setCreatedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setEmail("minh1812001@gmail.com");
        user2.setFarmRole(FarmRole.OWNER);
        user2.setCreatedBy("anonymousUser");
        user2.setFirstName("Le Huu");
        user2.setFullName("Le Huu Minh");
        user2.setId(1L);
        user2.setIdCard("038201012260");
        user2.setImageUrl("https://example.org/example");
        user2.setLastModifiedBy("System");
        user2.setLastModifiedDate(LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setLastName("Minh");
        user2.setOwner("Owner");
        user2.setPassword("123456");
        user2.setPhoneNumber("0343383101");
        user2.setDeleted(false);
        locationDetail = new LocationDetail();
        locationDetail.setId(1L);
        locationDetail.setName("Test Location Detail");
        locationDetail.setStatus(true);
        locationDetail.setCreatedBy(user2.getEmail());

        location = new Location();
        location.setId(1L);
        location.setAddress("Test Address");
        location.setStatus(true);
        location.setCreatedBy(user2.getEmail());

        locationDTO = new LocationDTO();
        locationDTO.setId(1L);
        locationDTO.setAddress("Test Address");
        locationDTO.setStatus(true);

        locationDetail.setLocation(location);
        locationDetailDTO = new LocationDetailDTO();
        locationDetailDTO.setId(1L);
        locationDetailDTO.setName("Test Location Detail");
        locationDetailDTO.setStatus(true);

    }

    // Existing tests...



//    @Test
//    @DisplayName("Test 'getLocationDetail' method with a valid location detail id.")
//    public void testGetLocationDetailWhenValidIdThenReturnLocationDetail() {
//        when(locationDetailRepository.findById(locationDetailDTO.getId())).thenReturn(Optional.of(locationDetail));
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn(user2.getEmail());
//        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
//        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
//        when(locationDetailRepository.findByIdAndCreatedBy(1L,user2.getEmail())).thenReturn(Optional.of(locationDetail));
//        Optional<LocationDetailDTO> result = locationDetailService.getLocationDetail(1L);
//        assertTrue(result.isPresent());
//        assertEquals(locationDetailDTO.getName(), result.get().getName());
//    }    @Test
//    @DisplayName("Test 'getLocationDetail' method with a valid location detail id.")
//    public void testGetLocationDetailWhenValidIdThenReturnLocationDetail() {
//        when(locationDetailRepository.findById(locationDetailDTO.getId())).thenReturn(Optional.of(locationDetail));
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn(user2.getEmail());
//        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
//        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
//        when(locationDetailRepository.findByIdAndCreatedBy(1L,user2.getEmail())).thenReturn(Optional.of(locationDetail));
//        Optional<LocationDetailDTO> result = locationDetailService.getLocationDetail(1L);
//        assertTrue(result.isPresent());
//        assertEquals(locationDetailDTO.getName(), result.get().getName());
//    }

//    @Test
//    @DisplayName("Test 'registerLocationDetailFarm' method when the location detail with the given name already exists in the database.")
//    public void testRegisterLocationDetailFarmWhenLocationDetailExistsThenThrowBaseException() {
//        // Arrange
//        LocationFarmDTO locationFarmDTO = new LocationFarmDTO();
//        locationFarmDTO.setLocationDTO(locationDTO);
//        locationFarmDTO.setLocationDetailDTO(locationDetailDTO);
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn(user2.getEmail());
//        when(userRepository.findOneByLogin(anyString())).thenReturn(Optional.of(user2));
//        when(locationDetailRepository.findByIdAndCreatedBy(locationDetailDTO.getId(),user2.getEmail())).thenReturn(Optional.of(locationDetail));
//        when(locationDetailRepository.findByNameAndCreatedBy(locationDetailDTO.getName(), user2.getEmail())).thenReturn(locationDetail);
//        assertThatThrownBy(() -> locationDetailService.registerLocationDetailFarm(locationFarmDTO))
//            .isInstanceOf(BaseException.class)
//            .hasMessage("Tên vị trí đã tồn tại!");
//    }


    @Test
    @DisplayName("Test 'updateLocationFarm' method when the location detail exists and the name is not duplicated.")
    public void testUpdateLocationFarmWhenLocationDetailExistsAndNameNotDuplicatedThenUpdateLocationDetail() {
        LocationFarmDTO locationFarmDTO = new LocationFarmDTO();
        locationFarmDTO.setLocationDTO(locationDTO);
        locationFarmDTO.setLocationDetailDTO(locationDetailDTO);
        when(locationDetailRepository.findById(locationDetailDTO.getId())).thenReturn(Optional.of(locationDetail));
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(locationDetailRepository.findById(locationDetailDTO.getId())).thenReturn(Optional.of(locationDetail));
         when(locationDetailRepository.findByNameAndCreatedBy(locationDetailDTO.getName(),user2.getEmail())).thenReturn(null);
        locationDetailService.updateLocationFarm(locationFarmDTO);
    }

    @Test
    @DisplayName("Test 'updateLocationFarm' method when the location detail does not exist.")
    public void testUpdateLocationFarmWhenLocationDetailDoesNotExistThenThrowBaseException() {
        LocationFarmDTO locationFarmDTO = new LocationFarmDTO();
        locationFarmDTO.setLocationDTO(locationDTO);
        locationFarmDTO.setLocationDetailDTO(locationDetailDTO);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(locationDetailRepository.findById(locationDetailDTO.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> locationDetailService.updateLocationFarm(locationFarmDTO))
            .isInstanceOf(BaseException.class)
            .hasMessage("Không tìm thấy khu vực trồng");
    }

    @Test
    @DisplayName("Test 'updateLocationFarm' method when the location detail name is duplicated.")
    public void testUpdateLocationFarmWhenLocationDetailNameDuplicatedThenThrowBaseException() {
        LocationFarmDTO locationFarmDTO = new LocationFarmDTO();
        locationFarmDTO.setLocationDTO(locationDTO);
        locationFarmDTO.setLocationDetailDTO(locationDetailDTO);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(locationDetailRepository.findById(locationDetailDTO.getId())).thenReturn(Optional.of(locationDetail));
        when(locationDetailRepository.findByNameAndCreatedBy(locationDetailDTO.getName(),user2.getEmail())).thenReturn(new LocationDetail());
        assertThatThrownBy(() -> locationDetailService.updateLocationFarm(locationFarmDTO))
            .isInstanceOf(BaseException.class)
            .hasMessage("Tên vị trí đã tồn tại!");
    }

//    @Test
//    @DisplayName("Test 'deleteLocationDetails' method when the provided set of location detail ids is valid and all the location details are found in the database.")
//    public void testDeleteLocationDetailsWhenIdsAreValidAndAllLocationDetailsAreFoundThenDeleteLocationDetails() {
//        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
//
//        List<LocationDetail> locationDetails = Arrays.asList(locationDetail, new LocationDetail(), new LocationDetail());
//        when(locationDetailRepository.findLocationDetailByIdIn(ids)).thenReturn(locationDetails);
//        locationDetailService.deleteLocationDetails(ids);
//        verify(locationDetailRepository, times(1)).saveAll(locationDetails);
//        verify(locationRepository, times(1)).saveAll(anyList());
//    }

//    @Test
//    @DisplayName("Test 'deleteLocationDetails' method when the provided set of location detail ids is valid but some of the location details are not found in the database.")
//    public void testDeleteLocationDetailsWhenIdsAreValidButSomeLocationDetailsAreNotFoundThenDeleteLocationDetails() {
//        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn(user2.getEmail());
//        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
//        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
//        List<LocationDetail> locationDetails = Arrays.asList(locationDetail, null, null);
//        when(locationDetailRepository.findLocationDetailByIdIn(ids)).thenReturn(locationDetails);
//        assertThatThrownBy(() -> locationDetailService.deleteLocationDetails(ids))
//            .isInstanceOf(BaseException.class)
//            .hasMessage("Không tìm thất vị trí trồng trọt!");
//    }

//    @Test
//    @DisplayName("Test 'deleteLocationDetails' method when the provided set of location detail ids is empty.")
//    public void testDeleteLocationDetailsWhenIdsAreEmptyThenDoNothing() {
//        Set<Long> ids = new HashSet<>();
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn(user2.getEmail());
//        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
//        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
//        locationDetailService.deleteLocationDetails(ids);
//        verify(locationDetailRepository, Mockito.never()).saveAll(anyList());
//        verify(locationRepository, never()).saveAll(anyList());
//
//
//    }
//    @Test
//    @DisplayName("Test 'deleteLocationDetail' method when the user is OWNER or MANAGER.")
//    public void testDeleteLocationDetailWhenUserIsOwnerOrManagerThenSuccess1() {
//        // Arrange
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn(user2.getEmail());
//        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
//        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
//        when(locationDetailRepository.findByIdAndCreatedBy(1L, user2.getEmail())).thenReturn(Optional.of(locationDetail));
//
//        // Act
//        locationDetailService.deleteLocationDetail(1L);
//
//        // Assert
//        verify(locationDetailRepository, times(1)).save(locationDetail);
//        verify(locationRepository, times(1)).save(location);
//    }

//    @Test
//    @DisplayName("Test 'deleteLocationDetail' method when the user is not OWNER or MANAGER.")
//    public void testDeleteLocationDetailWhenUserIsNotOwnerOrManagerThenThrowBaseException1() {
//        // Arrange
//        user2.setFarmRole(FarmRole.EMPLOYEE);
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn(user2.getEmail());
//        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
//        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
//        when(locationDetailRepository.findByIdAndCreatedBy(1L, user2.getEmail())).thenReturn(Optional.of(locationDetail));
//
//        // Act and Assert
//        assertThatThrownBy(() -> locationDetailService.deleteLocationDetail(1L))
//            .isInstanceOf(BaseException.class)
//            .hasMessage("Người dùng không đủ quyền hạn!");
//    }

    @Test
    @DisplayName("Test 'searchLocationFarm' method when valid search criteria and pageable parameters are provided.")
    public void testSearchLocationFarmWhenValidSearchCriteriaAndPageableThenReturnPageOfLocationDetailDTO() {
        // Arrange
        SearchLocationDetailDTO searchLocationFarmDTO = new SearchLocationDetailDTO();
        searchLocationFarmDTO.setPlanFormat("Test Plan Format");
        searchLocationFarmDTO.setNameLocation("Test Location Detail");
        searchLocationFarmDTO.setStatus("true");
        Pageable pageable = PageRequest.of(0, 10);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        when(userRepository.findOneByLogin(anyString())).thenReturn(Optional.of(user2));
        List<LocationDetail> locationDetails = Collections.singletonList(locationDetail);
        when(locationDetailRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(locationDetails));
        Page<LocationDetailDTO> result = locationDetailService.searchLocationFarm(searchLocationFarmDTO, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Test 'searchLocationFarm' method when null search criteria are provided.")
    public void testSearchLocationFarmWhenNullSearchCriteriaThenThrowException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        // Act and Assert
        assertThatThrownBy(() -> locationDetailService.searchLocationFarm(null, pageable))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Test 'searchLocationFarm' method when null pageable parameter is provided.")
    public void testSearchLocationFarmWhenNullPageableThenThrowException() {
        // Arrange
        SearchLocationDetailDTO searchLocationFarmDTO = new SearchLocationDetailDTO();
        searchLocationFarmDTO.setPlanFormat("Test Plan Format");
        searchLocationFarmDTO.setNameLocation("Test Location Detail");
        searchLocationFarmDTO.setStatus("true");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        // Act and Assert
        assertThatThrownBy(() -> locationDetailService.searchLocationFarm(searchLocationFarmDTO, null))
            .isInstanceOf(NullPointerException.class);
    }
}
