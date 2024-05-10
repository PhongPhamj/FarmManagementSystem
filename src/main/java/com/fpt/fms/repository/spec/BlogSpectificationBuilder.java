package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.*;
import com.fpt.fms.service.search.SearchBlogCategoryDTO;
import com.fpt.fms.service.search.SearchBlogDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.validation.constraints.NotNull;
import java.util.List;

public class BlogSpectificationBuilder {

    private static Specification<Blog> hasApproveStatus(String status) {
        return (root, query, cb) -> {
            if (status != null && !status.isEmpty() ) {
                boolean statusBoolean = Boolean.parseBoolean(status);
                return cb.equal(root.get(Blog_.status), statusBoolean);
            }
            return cb.conjunction();
        };
    }

    private static Specification<Blog> hasTitle(String title) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(title)) {
                return cb.conjunction();
            }
            String searchTextLowerCase = "%" + title.toLowerCase().trim() + "%";
            return cb.or(
                cb.like(cb.lower(root.get(Blog_.title)), searchTextLowerCase),
                cb.like(cb.lower(root.get(Blog_.description)), searchTextLowerCase),
                cb.like(cb.lower(root.get(Blog_.blogCategory).get(BlogCategory_.name)), searchTextLowerCase),
                cb.like(cb.function("TO_CHAR", String.class, root.get(Blog_.createdDate), cb.literal("dd/mm/yyyy")), searchTextLowerCase)
            );
        };
    }
    private static Specification<Blog> hasCategory(String name) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(name)) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("blogCategory").get("name")), ("%" + name + "%").toLowerCase());
        };
    }
    private static Specification<Blog> hasBlogCategorys(List<BlogCategory> blogCates) {
        return (root, query, cb) -> {
            if (blogCates.isEmpty()) {
                return cb.conjunction();
            }
            return root.get(Blog_.blogCategory).in(blogCates);
        };
    }

    public static Specification<Blog> buildQuery(@NotNull SearchBlogDTO searchDTO,List<BlogCategory> blogCates) {
        return hasApproveStatus(searchDTO.getStatus()).and(hasCategory(searchDTO.getCategoryName())).and(hasTitle(searchDTO.getTitle())).and(hasBlogCategorys(blogCates));
    }
}
