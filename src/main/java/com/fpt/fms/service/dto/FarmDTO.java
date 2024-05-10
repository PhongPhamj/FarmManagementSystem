package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fpt.fms.domain.ApproveStatus;
import com.fpt.fms.domain.Farm;
import com.fpt.fms.domain.FarmRole;
import com.fpt.fms.domain.User;
import com.fpt.fms.fileUtils.ObjectUtil;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FarmDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @Size(max = 255, message = "Tên trang trại thành không được dài quá 255 kí tự")
    private String name;

    @Size(max = 50)
    @Length(max = 255, message = "Họ thành không được dài quá 255 kí tự")
    private String firstName;

    @Size(max = 50)
    @Length(max = 255, message = "Tên thành không được dài quá 255 kí tự")
    private String lastName;


    @JsonProperty("Employees")
    private List<UserDTO> Employees;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("user_id")
    private Long userId;

    @Email
    @JsonProperty("email")
    private String email;

    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date createdDate;

    @JsonProperty("status")
    private ApproveStatus status;

    private User user;

    public FarmDTO(Farm farm) {
        this.id = farm.getId();
        this.name = farm.getName();
        this.email = farm.getUser().getEmail();
        this.userId = farm.getUser().getId();
        this.firstName = farm.getUser().getFirstName();
        this.lastName = farm.getUser().getLastName();
        this.owner = ObjectUtil.toFullName(farm.getUser().getFirstName(), farm.getUser().getLastName());
        this.status = farm.getStatus();
        this.createdDate = Date.from(farm.getCreatedDate());
    }

}
