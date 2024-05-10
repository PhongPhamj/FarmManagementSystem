package com.fpt.fms.repository.spec;

import com.fpt.fms.domain.*;
import com.fpt.fms.service.search.SearchLocationDetailDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.constraints.NotNull;
import java.util.List;

public class LocationSpectificationBuilder {
    private static Specification<LocationDetail> hasPlantFormat(String plantFormat) {
        return (root, query, cb) -> {
            if (plantFormat != null) {
                PlantFormat statusEnum = PlantFormat.fromString(plantFormat); // Use a method to map the string to enum
                if (statusEnum != null) {
                    return cb.equal(root.get(LocationDetail_.plantFormat), statusEnum);
                }
            }
            return cb.conjunction();
        };
    }
    private static Specification<LocationDetail> hasApproveStatus(String status) {
        return (root, query, cb) -> {
            if (status != null && !status.isEmpty() ) {
                boolean statusBoolean = Boolean.parseBoolean(status);
                return cb.equal(root.get(LocationDetail_.status), statusBoolean);
            }
            return cb.conjunction();
        };
    }
    private static Specification<LocationDetail> hasName(String name) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(name)) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get(LocationDetail_.name)), ("%" + name + "%").toLowerCase());
        };
    }
    private static Specification<LocationDetail> hasLocations(List<Location> locations) {
        return (root, query, cb) -> {
            if (locations.isEmpty()) {
                return cb.conjunction();
            }
            return root.get(LocationDetail_.location).in(locations);
        };
    }

    public static Specification<LocationDetail> buildQuery(@NotNull SearchLocationDetailDTO searchLocationDetailDTO,List<Location> locations) {
        return hasPlantFormat(searchLocationDetailDTO.getPlanFormat())
            .and(hasApproveStatus(searchLocationDetailDTO.getStatus()))
            .and(hasName(searchLocationDetailDTO.getNameLocation()))
            .and(hasLocations(locations));
    }
}
