package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fpt.fms.domain.HarvestUnit;
import com.fpt.fms.domain.PlantCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlantDTO {

    private Long id;

    private String year;

    @NotBlank
    private String name;

    private String provider;

    private Boolean status;

    private HarvestUnit harvestUnit;

    private LocationDTO locationDTO;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date createdDate;

    @NotBlank
    private PlantCategory type;


    private String source;

    private List<HarvestPlanDTO> harvestPlanDTOS;

    private List<LocationDetailDTO> locationDetailDTOS;

    @JsonProperty("plantDetail")
    private PlantDetailDTO plantDetailDTO;

    public PlantCategory getType() {
        return type;
    }

    public void setType(String type) {
        this.type = PlantCategory.valueOf(type);
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = Date.from(createdDate);
    }
}
