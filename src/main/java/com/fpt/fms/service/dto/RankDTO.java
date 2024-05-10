package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fpt.fms.domain.Rank;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RankDTO {

    @JsonProperty("id")
    private Long id;


    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("rank_detail")
    private String rankDetail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("created_date")
    private Date createdDate;

    public RankDTO(Rank rank) {
        this.id = rank.getId();
        this.name = rank.getName();
        this.description = rank.getDescription();
        this.status = rank.getStatus();
        this.rankDetail = rank.getRankDetail();
        this.createdDate = Date.from(rank.getCreatedDate());
    }
}
