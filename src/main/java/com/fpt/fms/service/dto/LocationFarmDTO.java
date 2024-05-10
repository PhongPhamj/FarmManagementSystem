package com.fpt.fms.service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationFarmDTO {

    private LocationDTO locationDTO;
    private LocationDetailDTO locationDetailDTO;
}
