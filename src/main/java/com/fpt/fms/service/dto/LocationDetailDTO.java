package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fpt.fms.domain.PlantFormat;
import java.io.Serializable;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonProperty("name_area")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("number_of_beds")
    private Integer numberOfBeds;

    @JsonProperty("plant_format")
    private PlantFormat plantFormat;

    @JsonProperty("description")
    private String description;

    @JsonProperty("area")
    private Integer area;

    @JsonProperty("status")
    private Boolean status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date createdDate;

    private LocationDTO locationDTO;

    private ArrayList<CropPlanDTO> cropPlanDTO;
    private ArrayList<String> beds;
}
