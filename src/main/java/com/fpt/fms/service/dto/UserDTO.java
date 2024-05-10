package com.fpt.fms.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.fms.domain.Authority;
import com.fpt.fms.domain.FarmRole;
import com.fpt.fms.domain.User;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * A DTO representing a user, with only the public attributes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String fullName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    private String phoneNumber;

    private String idCard;

    private String imageUrl;

    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private Date createdDate;

    private String createdBy;
    private String farmRole;
    private String authorities;
    private String CreateByRole;

    private boolean workStatus;
    private FarmDTO farmDTO;
    private boolean activated;

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
        firstName = user.getFirstName();
        fullName = user.getFullName();
        lastName = user.getLastName();
        this.imageUrl = user.getImageUrl();
        this.idCard = user.getIdCard();
        this.farmRole = user.getFarmRole() == null ? null : user.getFarmRole().name();
        this.createdDate = Date.from(user.getCreatedDate());
        this.authorities = user.getAuthorities().toString();
        this.activated = user.isActivated();
        this.workStatus = user.isWorkStatus();
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getCreateByRole() {
        return CreateByRole;
    }

    public void setCreateByRole(String createByRole) {
        CreateByRole = createByRole;
    }

    public FarmDTO getFarmDTO() {
        return farmDTO;
    }

    public void setFarmDTO(FarmDTO farmDTO) {
        this.farmDTO = farmDTO;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getFarmRole() {
        return farmRole;
    }

    public void setFarmRole(String farmRole) {
        this.farmRole = farmRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return (
            "UserDTO{" +
            "id=" +
            id +
            ", firstName='" +
            firstName +
            '\'' +
            ", lastName='" +
            lastName +
            '\'' +
            ", fullName='" +
            fullName +
            '\'' +
            ", email='" +
            email +
            '\'' +
            ", phoneNumber='" +
            phoneNumber +
            '\'' +
            ", idCard='" +
            idCard +
            '\'' +
            ", imageUrl='" +
            imageUrl +
            '\'' +
            ", createdDate=" +
            createdDate +
            '}'
        );
    }
}
