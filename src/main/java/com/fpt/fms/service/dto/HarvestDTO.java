package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.fms.domain.Plant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HarvestDTO {

    private Long id;

    private PlantDTO plantDTO;

    private Long plantId;

    private Integer LossRate;

    private Date harvestDate;

    private Date amountDayMature;

    private Date seedlingDate;
}
