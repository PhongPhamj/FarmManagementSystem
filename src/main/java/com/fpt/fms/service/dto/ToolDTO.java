package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolDTO {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("source")
    private String source;

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("tool_category_id")
    private String toolCategory;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("created_date")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("last_modified_date")
    private Date lastModifiedDate;

    @JsonProperty("create_by")
    private String CreateBy;

    @JsonProperty("last_modified_by")
    private String lastModifiedBy;

}
