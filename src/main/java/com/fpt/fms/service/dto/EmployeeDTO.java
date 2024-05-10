package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.fms.domain.FarmRole;
import com.fpt.fms.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class EmployeeDTO {

    private Long id;


    @Size(max = 50)
    @Length(max = 255, message = "Họ thành không được dài quá 255 kí tự")
    private String firstName;

    @Size(max = 50)
    @Length(max = 255, message = "Tên thành không được dài quá 255 kí tự")
    private String lastName;

    @Size(max = 100)
    @Length(max = 100, message = "NickName thành không được dài quá 255 kí tự")
    private String fullName;
    @Email
    @Size(min = 5, max = 254)
    private String email;
    @Size(min = 10, max = 10)
    private String phoneNumber;

    private boolean workStatus;

    @Size(max = 50)
    private String idCard;

    private boolean activated;



    private FarmRole farmrole;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date createdDate;
    private String createdBy;

    public EmployeeDTO() {
    }

    public EmployeeDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.idCard = user.getIdCard();
        this.activated = user.isActivated();
        this.farmrole = user.getFarmRole();
        this.workStatus = user.isWorkStatus();
        createdDate = Date.from(user.getCreatedDate());
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setFarmrole(FarmRole farmrole) {
        this.farmrole = farmrole;
    }
    // Generate fullName based on firstName and lastName
}
