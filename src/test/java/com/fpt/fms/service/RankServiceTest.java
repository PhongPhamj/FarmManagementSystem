package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.RankRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.dto.RankDTO;
import com.fpt.fms.service.search.SearchRankDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class RankServiceTest {

    @Mock
    private RankRepository rankRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private RankService rankService;
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;
    private Rank rank;
    private RankDTO rankDTO;
    @Mock
    private User user, user2;
    @Mock
    private Authority userAuthority, userAuthority2;
    @BeforeEach
    public void setUp() {
        //Autho - User
        userAuthority2 = new Authority(); // Thay bằng cách tạo từ chuỗi thực tế
        userAuthority2.setName(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(userAuthority2); // Thêm Authority vào Set
        //Oner
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
        rank = new Rank();
        rank.setId(1L);
        rank.setName("Rank 1");
        rank.setDescription("Description 1");
        rank.setRankDetail("Rank Detail 1");
        rank.setStatus(true);
        rank.setCreatedBy(user2.getCreatedBy());

        rankDTO = new RankDTO(rank);
        modelMapper = new ModelMapper();
    }

    // Existing tests...

    @Test
    public void testGetSearchRankWhenRanksMatchSearchCriteriaThenReturnMatchingRanks() {
        SearchRankDTO searchRankDTO = new SearchRankDTO();
        searchRankDTO.setRankName("Rank 1");
        searchRankDTO.setStatus("true");


        when(rankRepository.findAll((Specification<Rank>) any())).thenReturn(Arrays.asList(rank));

        assertThat(rankService.getSearchRank(searchRankDTO)).isNotEmpty();
    }

    @Test
    public void testGetSearchRankWhenNoRanksMatchSearchCriteriaThenReturnEmptyList() {
        SearchRankDTO searchRankDTO = new SearchRankDTO();
        searchRankDTO.setRankName("Rank 2");
        searchRankDTO.setStatus("false");

        Specification<Rank> specification = Specification.where(null);

        when(rankRepository.findAll((Specification<Rank>) any())).thenReturn(Arrays.asList());

        assertThat(rankService.getSearchRank(searchRankDTO)).isEmpty();
    }

    @Test
    public void testGetSearchRankWhenSearchRankDtoIsNullThenThrowNullPointerException() {
        assertThatThrownBy(() -> rankService.getSearchRank(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testDeleteRanksWhenAllRanksExistThenDeleteRanks() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L));
        Rank rank2 = new Rank();
        rank2.setId(2L);
        rank2.setStatus(false);
        rank.setStatus(false);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(rankRepository.findRankByIdInAndAndCreatedBy(ids,user2.getEmail())).thenReturn(Arrays.asList(rank, rank2));

        rankService.deleteRanks(ids);

        verify(rankRepository).saveAll(anyList());
    }

    @Test
    public void testDeleteRanksWhenSomeRanksDoNotExistThenThrowBaseException() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(rankRepository.findRankByIdInAndAndCreatedBy(ids,user2.getEmail())).thenReturn(Arrays.asList(rank));

        assertThatThrownBy(() -> rankService.deleteRanks(ids))
            .isInstanceOf(BaseException.class)
            .hasMessage("Có xếp hạng không tồn tại");
    }

    @Test
    public void testDeleteRanksWhenOneRankIsActiveThenThrowBaseException() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L));
        Rank rank2 = new Rank();
        rank2.setId(2L);
        rank2.setStatus(true);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(rankRepository.findRankByIdInAndAndCreatedBy(ids,user2.getEmail())).thenReturn(Arrays.asList(rank, rank2));

        assertThatThrownBy(() -> rankService.deleteRanks(ids))
            .isInstanceOf(BaseException.class)
            .hasMessage("Không thể xóa xếp hạng đang hoạt động");
    }

    @Test
    public void testRegisterRankWhenRankExistsThenThrowBaseException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(rankRepository.findByName(rankDTO.getName())).thenReturn(Optional.of(rank));

        assertThatThrownBy(() -> {
            rankService.registerRank(rankDTO);
        }).isInstanceOf(BaseException.class)
            .hasMessage("Tên xếp hạng đã tồn tại!");
    }

    @Test
    public void testGetRankWhenRankExistsThenReturnRankDTO() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(rankRepository.findByIdAndCreatedBy(rankDTO.getId(),user2.getEmail())).thenReturn(rank);

        RankDTO result = rankService.getRank(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(rankDTO.getId());
        assertThat(result.getName()).isEqualTo(rankDTO.getName());
        assertThat(result.getDescription()).isEqualTo(rankDTO.getDescription());
        assertThat(result.getRankDetail()).isEqualTo(rankDTO.getRankDetail());
        assertThat(result.getStatus()).isEqualTo(rankDTO.getStatus());
    }

    @Test
    public void testGetRankWhenRankDoesNotExistThenThrowBaseException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
       // when(rankRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rankService.getRank(3L))
            .isInstanceOf(BaseException.class)
            .hasMessage("Xếp hạng không tồn tại");
    }

    @Test
    public void testUpdateRankWhenRankExistsAndNewNameIsNotUsedThenUpdateRank() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(rankRepository.findByIdAndCreatedBy(rankDTO.getId(),user2.getEmail())).thenReturn(rank);
        when(rankRepository.findByName(rankDTO.getName())).thenReturn(Optional.empty());

        rankService.updateRank(rankDTO);

        verify(rankRepository).save(any(Rank.class));
    }

    @Test
    public void testUpdateRankWhenRankDoesNotExistThenThrowBaseException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        //when(rankRepository.findById(rankDTO.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rankService.updateRank(rankDTO))
            .isInstanceOf(BaseException.class)
            .hasMessage("Xếp hạng không tồn tại");
    }

    @Test
    public void testUpdateRankWhenNewNameIsUsedThenThrowBaseException() {
        Rank existingRank = new Rank();
        existingRank.setId(2L);
        existingRank.setName(rankDTO.getName());

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(rankRepository.findByIdAndCreatedBy(rankDTO.getId(),user2.getEmail())).thenReturn(rank);
        when(rankRepository.findByName(rankDTO.getName())).thenReturn(Optional.of(existingRank));

        assertThatThrownBy(() -> rankService.updateRank(rankDTO))
            .isInstanceOf(BaseException.class)
            .hasMessage("Tên xếp hạng đã tồn tại!");
    }

    @Test
    public void testDeleteRankWhenRankExistsThenDeleteRankFalse() {
        rank.setStatus(false);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(rankRepository.findByIdAndCreatedBy(rankDTO.getId(),user2.getEmail())).thenReturn(rank);

        rankService.deleteRank(rankDTO.getId());

        verify(rankRepository).save(any(Rank.class));
    }

    @Test
    public void testDeleteRankWhenRankExistsThenDeleteRankTrue() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        when(rankRepository.findByIdAndCreatedBy(rankDTO.getId(),user2.getEmail())).thenReturn(rank);

        assertThatThrownBy(() -> rankService.deleteRank(rankDTO.getId()))
            .isInstanceOf(BaseException.class)
            .hasMessage("Không thể xóa xếp hạng đang hoạt động");
    }

    @Test
    public void testDeleteRankWhenRankDoesNotExistThenThrowBaseException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user2.getEmail());
        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();
        when(userRepository.findOneByLogin(actualLogin.get())).thenReturn(Optional.of(user2));
        //when(rankRepository.findByIdAndCreatedBy(rankDTO.getId(),user2.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rankService.deleteRank(rankDTO.getId()))
            .isInstanceOf(BaseException.class)
            .hasMessage("Xếp hạng không tồn tại");
    }

    // Rest of the tests...
}
