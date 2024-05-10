package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.*;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.service.search.SearchEmployeeDTO;
import com.fpt.fms.service.search.SearchUserDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserSpectificationBuilder {

    private static Specification<User> hasApproveStatus(String authority) {
        return (root, query, cb) -> {
            if (authority != null && !authority.isEmpty()) {
                if (AuthoritiesConstants.ADMIN.contains(authority) ||
                    AuthoritiesConstants.USER.contains(authority) ||
                    AuthoritiesConstants.EMPLOYEE.contains(authority) ||
                    AuthoritiesConstants.ANONYMOUS.contains(authority)) {
                    Join<User, Authority> authorityJoin = root.join("authorities", JoinType.LEFT);
                    Expression<String> authorityName = authorityJoin.get("name");

                    return cb.equal(authorityName, authority);
                }
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
    private static Specification<User> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status != null && !status.isEmpty()) {
                boolean statusBoolean = Boolean.parseBoolean(status);
                return cb.equal(root.get(User_.activated), statusBoolean);
            }
            return cb.conjunction();
        };
    }
    public static Specification<User> buildQuery(@NotNull SearchUserDTO searchUserDTO) {
        return hasApproveStatus(String.valueOf(searchUserDTO.getAuthorities()))
            .and(hasFullText(searchUserDTO.getFullText())).and(hasStatus(searchUserDTO.getActivated()));
    }

}
