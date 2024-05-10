package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApproveStatusDTO {

    private int id;
    private String name;

    public ApproveStatusDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
