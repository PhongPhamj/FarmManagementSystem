package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.repository.RankRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.repository.spec.RankSpectificationBuilder;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.IRankService;
import com.fpt.fms.service.dto.EmployeeDTO;
import com.fpt.fms.service.dto.RankDTO;
import com.fpt.fms.service.search.SearchRankDTO;
import com.fpt.fms.web.rest.errors.BaseException;

import java.util.*;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RankService implements IRankService {

    private final RankRepository rankRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public RankService(RankRepository rankRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.rankRepository = rankRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<RankDTO> getAllRank() {
        return rankRepository.findByCreatedBy(getCur()).stream().map(RankDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<RankDTO> getAllRankWithSratusTrue() {
        return  rankRepository.findByCreatedBy(getCur()).stream().filter(Rank::getStatus).map(RankDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<RankDTO> getSearchRank(SearchRankDTO searchRankDTO) {
        Specification<Rank> specification = RankSpectificationBuilder.buildQuery(searchRankDTO);
        return rankRepository.findAll(specification.and((root, query, cb) -> cb.equal(root.get("createdBy"),getCur()))).stream()
            .map(RankDTO::new)
            .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void registerRank(RankDTO rankDTO) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            rankDTO.setStatus(true);
            Rank rank = modelMapper.map(rankDTO, Rank.class);
            Optional<Rank> existingRank = rankRepository.findByName(rankDTO.getName());
            if (existingRank.isPresent()) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên xếp hạng đã tồn tại!");
            }
            rankRepository.save(rank);
        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }

    }

    @Override
    public void updateRank(RankDTO rankDTO) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            Rank rank = getRankByIdAndCreateBy(rankDTO.getId(),getCur());
            Optional<Rank> existingRank = rankRepository.findByName(rankDTO.getName());
            if (existingRank.isPresent() && !Objects.equals(existingRank.get().getId(), rankDTO.getId())) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên xếp hạng đã tồn tại!");
            }
            rank.setId(rankDTO.getId());
            rank.setName(rankDTO.getName());
            rank.setDescription(rankDTO.getDescription());
            rank.setRankDetail(rankDTO.getRankDetail());
            rank.setStatus(rankDTO.getStatus());
            rankRepository.save(rank);
        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }

    }

    @Override
    public RankDTO getRank(Long id) {
        Rank rank = getRankByIdAndCreateBy(id, getCur());
        return new RankDTO(rank);
    }

    @Override
    public void deleteRank(Long id) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            Rank rank = getRankByIdAndCreateBy(id,getCur());
            checkRankStatus(rank);
            rank.setDeleted(true);
            rankRepository.save(rank);
        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }

    }

    @Override
    public void deleteRanks(Set<Long> ids) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            List<Rank> ranks = rankRepository.findRankByIdInAndAndCreatedBy(ids,getCur());
            if (ranks.size() != ids.size()) {
                Set<Long> existingIds = ranks.stream().map(Rank::getId).collect(Collectors.toSet());
                ids.removeAll(existingIds);
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Có xếp hạng không tồn tại");
            }
            ranks.forEach(
                rank -> {
                    checkRankStatus(rank);
                    rank.setDeleted(Boolean.TRUE);
                }
            );
            rankRepository.saveAll(ranks);
        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }


    }

    private User getUser() {
        String curUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
        return userRepository.findOneByLogin(curUser).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
    }
    private String getCur() {
        String createBy =getUser().getCreatedBy();
        if(createBy.equals("anonymousUser")){
            createBy = getUser().getEmail();
        }
        return createBy;
    }
    private Rank getRankByIdAndCreateBy(Long id , String email){
        Rank rank = rankRepository.findByIdAndCreatedBy(id, email);
        if (rank == null) {
            throw new BaseException(400, "Xếp hạng không tồn tại");
        }
        return rank;
    }
    private static void checkRankStatus(Rank rank) {
        if (Boolean.TRUE.equals(rank.getStatus())) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa xếp hạng đang hoạt động");
        }
    }
}
