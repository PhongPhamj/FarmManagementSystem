package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.fms.domain.GrowthStage;
import com.fpt.fms.domain.HarvestUnit;
import com.fpt.fms.domain.Session;
import com.fpt.fms.domain.StartMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CropPlanDTO {

    private Long id;

    private Boolean status;

    private Boolean isDone;

    private Date dateDone;

    private PlantDTO plantDTO;

    private Long plantId;

    private String namePlant;

    private Long locationDetailId;

    private List<HarvestPlanDTO> harvestPlanDTOS;

    private LocationDetailDTO locationDetailDTO;

    private StartMethod startMethod;

    private GrowthStage growthStage;

    private HarvestUnit harvestUnit;

    private Integer expectedAmount;

    private String bed;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date sowDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dateToHarvest;

    private Integer totalHarvestAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date germinationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date seedlingDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date flowerDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date ripeningDate;

    private Session session;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date fromDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date toDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date completeDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date seedStarted;

    private Integer germinationAmount;

    private Integer seedlingAmount;

    private Integer sowAmount;

    private Integer flowerAmount;

    private Integer ripeningAmount;

    private Integer completeAmount;

    private Integer plantSpace;

    private Integer rowSpace;

    private Integer plantDepth;

    public HarvestUnit getHarvestUnit() {
        return harvestUnit;
    }

    public void setHarvestUnit(String harvestUnit) {
        try {
            this.harvestUnit = HarvestUnit.valueOf(harvestUnit);
        }catch (Exception e){
            this.harvestUnit = null;
        }

    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = Date.from(createdDate);
    }

    public StartMethod getStartMethod() {
        return startMethod;
    }

    public void setStartMethod(String startMethod) {
        this.startMethod = StartMethod.valueOf(startMethod);
    }

    public GrowthStage getGrowthStage() {
        return growthStage;
    }

    public void setGrowthStage(String growthStage) {
        this.growthStage = GrowthStage.valueOf(growthStage);
    }
}
