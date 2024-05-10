package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.*;
import com.fpt.fms.service.search.SearchRankDTO;
import com.fpt.fms.service.search.SearchTaskDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.groups.Group;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpectificationBuilder {

    private static Specification<Task> hasName(String name) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(name)) {
                return cb.conjunction();
            }
            String searchTextLowerCase = "%" + name.toLowerCase().trim() + "%";
            return cb.or(
                cb.like(cb.lower(root.get(Task_.title)), searchTextLowerCase),
                cb.like(cb.lower(root.get(Task_.description)), searchTextLowerCase),
                cb.like(cb.lower(root.get(Task_.user).get(User_.fullName)), searchTextLowerCase),
                cb.like(cb.function("TO_CHAR", String.class, root.get(Task_.DUE_DATE), cb.literal("dd/mm/yyyy")), searchTextLowerCase)
            );
        };
    }

    private static Specification<Task> hasUsers(List<User> users) {
        return (root, query, cb) -> {
            if (users.isEmpty()) {
                return cb.conjunction();
            }
            return root.get(Task_.user).in(users);
        };
    }

    private static Specification<Task> hasPlants(List<Plant> plants) {
        return (root, query, cb) -> {
            if (plants.isEmpty()) {
                return cb.conjunction();
            }
            return root.get(Task_.plant).in(plants);
        };
    }

    public static Specification<Task> buildQuery(@NotNull SearchTaskDTO searchDTO, List<User> users, List<Plant> plants) {
        return hasName(searchDTO.getTaskName()).and(hasUsers(users)).and(hasPlants(plants));
    }
}
