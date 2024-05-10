package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fpt.fms.domain.MultiMedia;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultiMediaDTO {
    private static final long serialVersionUID = 1L;

    @NonNull
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @Size(max = 1000)
    private String name;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date createdDate;

    public MultiMediaDTO(MultiMedia u) {
        this.id = u.getId();
        this.name = u.getName();
        this.imageUrl = u.getImageUrl();
        this.createdDate = Date.from(u.getCreatedDate());
    }

}
