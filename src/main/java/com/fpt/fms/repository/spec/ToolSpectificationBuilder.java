package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.*;
import com.fpt.fms.service.search.SearchToolCategoryDTO;
import com.fpt.fms.service.search.SearchToolDTO;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ToolSpectificationBuilder {

    private static Specification<Tool> hasApproveStatus(String status) {
        return (root, query, cb) -> {
            if (status != null && !status.isEmpty()) {
                boolean statusBoolean = Boolean.parseBoolean(status);
                return cb.equal(root.get(Tool_.status), statusBoolean);
            }
            return cb.conjunction();
        };
    }

    private static Specification<Tool> hasName(String name) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(name)) {
                return cb.conjunction();
            }
            String searchTextLowerCase = "%" + name.toLowerCase().trim() + "%";
            return cb.or(
                cb.like(cb.lower(root.get(Tool_.name)), searchTextLowerCase),
                cb.like(cb.lower(root.get(Tool_.source)), searchTextLowerCase),
                cb.like(cb.lower(root.get(Tool_.toolCategory).get(ToolCategory_.name)), searchTextLowerCase),
                cb.like(cb.function("TO_CHAR", String.class, root.get(Tool_.createdDate), cb.literal("dd/mm/yyyy")), searchTextLowerCase)
            );
        };
    }
    private static Specification<Tool> hasToolCategorys(List<ToolCategory> toolCates) {
        return (root, query, cb) -> {
            if (toolCates.isEmpty()) {
                return cb.conjunction();
            }
            return root.get(Tool_.toolCategory).in(toolCates);
        };
    }

    public static Specification<Tool> buildQuery(@NotNull SearchToolDTO searchDTO,List<ToolCategory> toolCates) {
        return hasApproveStatus(searchDTO.getStatustool()).and(hasName(searchDTO.getName())).and(hasToolCategorys(toolCates));
    }
}
