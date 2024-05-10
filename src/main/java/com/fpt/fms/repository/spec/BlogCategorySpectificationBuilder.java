package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.*;
import com.fpt.fms.service.search.SearchBlogCategoryDTO;
import com.fpt.fms.service.search.SearchDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.constraints.NotNull;

public class BlogCategorySpectificationBuilder {

    private static Specification<BlogCategory> hasApproveStatus(String status) {
        return (root, query, cb) -> {
            if (status != null && !status.isEmpty() ) {
                boolean statusBoolean = Boolean.parseBoolean(status);
                return cb.equal(root.get(BlogCategory_.status), statusBoolean);
            }
            return cb.conjunction();
        };
    }

    private static Specification<BlogCategory> hasName(String name) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(name)) {
                return cb.conjunction();
            }
            String searchTextLowerCase = "%" + name.toLowerCase().trim() + "%";
            return cb.or(
                cb.like(cb.lower(root.get(BlogCategory_.name)), searchTextLowerCase),
                cb.like(cb.lower(root.get(BlogCategory_.description)), searchTextLowerCase),
                cb.like(cb.function("TO_CHAR", String.class, root.get(BlogCategory_.createdDate), cb.literal("dd/mm/yyyy")), searchTextLowerCase)
            );
        };
    }

    public static Specification<BlogCategory> buildQuery(@NotNull SearchBlogCategoryDTO searchDTO) {
        return hasApproveStatus(searchDTO.getStatus()).and(hasName(searchDTO.getName()));
    }
}
