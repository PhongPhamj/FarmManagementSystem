package com.fpt.fms.service.baseservice;

import com.fpt.fms.service.dto.RankDTO;
import com.fpt.fms.service.dto.TaskDTO;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import com.fpt.fms.service.search.SearchRankDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IRankService {
    List<RankDTO> getAllRank();
    List<RankDTO> getAllRankWithSratusTrue();
    List<RankDTO> getSearchRank(SearchRankDTO searchRankDTO);

    void registerRank(RankDTO rankDTO);
    void updateRank(RankDTO rankDTO);

    RankDTO getRank(Long id);
    void deleteRank(Long id);

    void deleteRanks(Set<Long> ids);
}
