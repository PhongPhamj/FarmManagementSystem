package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.*;
import com.fpt.fms.service.search.SearchRankDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.constraints.NotNull;

public class RankSpectificationBuilder {

    private static Specification<Rank> hasName(String name) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(name)) {
                return cb.conjunction();
            }
            String searchTextLowerCase = "%" + name.toLowerCase().trim() + "%";
            return cb.or(
                cb.like(cb.lower(root.get(Rank_.name)), searchTextLowerCase),
                cb.like(cb.lower(root.get(Rank_.description)), searchTextLowerCase),
                cb.like(cb.lower(root.get(Rank_.rankDetail)), searchTextLowerCase),
                cb.like(cb.function("TO_CHAR", String.class, root.get(Rank_.createdDate), cb.literal("dd/mm/yyyy")), searchTextLowerCase)
            );
        };
    }
    private static Specification<Rank> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status != null && !status.isEmpty()) {
                boolean statusBoolean = Boolean.parseBoolean(status);
                return cb.equal(root.get(Rank_.status), statusBoolean);
            }
            return cb.conjunction();
        };
    }
    public static Specification<Rank> buildQuery(@NotNull SearchRankDTO searchDTO) {
        return hasName(searchDTO.getRankName()).and(hasStatus(searchDTO.getStatus()));
    }
}
