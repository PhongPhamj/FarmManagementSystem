package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.fms.domain.HarvestUnit;
import com.fpt.fms.domain.StartMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlantDetailDTO {

    private Long id;

    private StartMethod startMethod;

    private Integer rateGermination;

    private Integer lossRate;

    private Integer dayToEmerge;

    private Integer dayToMature;

    private Integer dayToHarvest;

    private Integer plantSpace;

    private Integer rowSpace;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date createdDate;

    private Integer plantDepth;

    private String description;

    private HarvestUnit harvestUnit;

    private PlantDTO plantDTO;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = Date.from(createdDate);
    }

    public HarvestUnit getHarvestUnit() {
        return harvestUnit;
    }

    public void setHarvestUnit(String harvestUnit) {
        this.harvestUnit = HarvestUnit.valueOf(harvestUnit);
    }

    public StartMethod getStartMethod() {
        return startMethod;
    }

    public void setStartMethod(String startMethod) {
        this.startMethod = StartMethod.valueOf(startMethod);
    }
}
