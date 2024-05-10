package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.ToolCategory;
import com.fpt.fms.domain.ToolCategory_;
import com.fpt.fms.domain.Tool_;
import com.fpt.fms.domain.User_;
import com.fpt.fms.service.search.SearchToolCategoryDTO;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ToolCategorySpectificationBuilder {

    private static Specification<ToolCategory> hasApproveStatus(String status) {
        return (root, query, cb) -> {
            if (status != null && !status.isEmpty()) {
                boolean statusBoolean = Boolean.parseBoolean(status);
                return cb.equal(root.get(ToolCategory_.status), statusBoolean);
            }
            return cb.conjunction();
        };
    }

    private static Specification<ToolCategory> hasName(String name) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(name)) {
                return cb.conjunction();
            }
            String searchTextLowerCase = "%" + name.toLowerCase().trim() + "%";
            return cb.or(
                cb.like(cb.lower(root.get(ToolCategory_.name)), searchTextLowerCase),
                cb.like(cb.lower(root.get(ToolCategory_.description)), searchTextLowerCase),
                cb.like(cb.function("TO_CHAR", String.class, root.get(ToolCategory_.createdDate), cb.literal("dd/mm/yyyy")), searchTextLowerCase)
            );
        };
    }

    public static Specification<ToolCategory> buildQuery(@NotNull SearchToolCategoryDTO searchDTO) {
        return hasApproveStatus(searchDTO.getStatus()).and(hasName(searchDTO.getName()));
    }
}
