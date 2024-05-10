package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.*;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.constraints.NotNull;

public class EmployeeSpectificationBuilder {

    private static Specification<User> hasApproveStatus(String role) {
        return (root, query, cb) -> {
            if (role != null) {
                FarmRole statusEnum = FarmRole.fromString(role); // Use a method to map the string to enum
                if (statusEnum != null) {
                    return cb.equal(root.get(User_.farmRole), statusEnum);
                }
            }
            return cb.conjunction();
        };
    }
    private static Specification<User> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status != null && !status.isEmpty()) {
                boolean statusBoolean = Boolean.parseBoolean(status);
                return cb.equal(root.get(User_.activated), statusBoolean);
            }
            return cb.conjunction();
        };
    }

    public static Specification<User> hasFullText(String searchText) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(searchText)) {
                return cb.conjunction();
            }
            String searchTextLowerCase = "%" + searchText.toLowerCase().trim() + "%";
            return cb.or(
                cb.like(cb.lower(root.get(User_.fullName)), searchTextLowerCase),
                cb.like(cb.lower(root.get(User_.email)), searchTextLowerCase),
                cb.like(cb.function("TO_CHAR", String.class, root.get(User_.createdDate), cb.literal("dd/mm/yyyy")), searchTextLowerCase)
            );
        };
    }
    public static Specification<User> buildQuery(@NotNull SearchEmployeeDTO searchUserDTO) {
        return hasApproveStatus(searchUserDTO.getFarmRole())
            .and(hasFullText(searchUserDTO.getFullText())).and(hasStatus(searchUserDTO.getActivated()));
    }
}
