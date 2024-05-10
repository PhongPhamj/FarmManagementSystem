package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.ApproveStatus;
import com.fpt.fms.domain.Farm;
import com.fpt.fms.domain.Farm_;
import com.fpt.fms.service.search.SearchDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.constraints.NotNull;

public class FarmSpectificationBuilder {

    private static Specification<Farm> hasApproveStatus(String approveStatus) {
        return (root, query, cb) -> {
            if (approveStatus != null) {
                ApproveStatus statusEnum = ApproveStatus.fromString(approveStatus); // Use a method to map the string to enum
                if (statusEnum != null) {
                    return cb.equal(root.get(Farm_.status), statusEnum);
                }
            }
            return cb.conjunction();
        };
    }

    private static Specification<Farm> hasName(String name) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(name)) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get(Farm_.NAME)), ("%" + name + "%").toLowerCase());
        };
    }

    public static Specification<Farm> buildQuery(@NotNull SearchDTO searchDTO) {
        return hasApproveStatus(searchDTO.getApproveStatus()).and(hasName(searchDTO.getFarmName()));
    }
}
