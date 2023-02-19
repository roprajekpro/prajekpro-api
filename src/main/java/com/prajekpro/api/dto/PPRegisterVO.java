package com.prajekpro.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prajekpro.api.enums.Source;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PPRegisterVO {

    @NotBlank
    private String firstName;
    private String lastName;
    @NotBlank
    private String emailId;
    private boolean isEmailVerified = false;
    @NotBlank
    private String contactNo;
    @JsonProperty("contactVerified")
    private boolean contactVerified;
    private String landlineNo;
    private String password;
    private String googleHandlePassword;
    private String facebookHandlePassword;
    private String address;
    private Float latitude;
    private Float longitude;
    private List<String> roles;
    private String userId;
    private Integer activeStatus;
    private Integer availabilityStatus;
    private String location;
    private Long proId;
    private Source source;
    @JsonProperty(value = "isVatRegistered")
    private boolean vatRegistered;
    private String vatNo;
    private float experienceInYears;
    private String aboutText;

    @JsonProperty("tncAccepted")
    private boolean tncAccepted;

    public PPRegisterVO(GoogleHandleResponseDTO request) {
        this.firstName = request.getDisplayName();
        this.emailId = request.getEmail();
        this.isEmailVerified = true;
        this.contactNo = request.getContactNo();
        this.contactVerified = false;
        this.googleHandlePassword = request.getUserId();
        this.tncAccepted = true;
    }

    public PPRegisterVO(Users users) {
        this.firstName = users.getFirstNm();
        this.lastName = users.getLastNm();
        this.emailId = users.getEmailId();
        this.contactNo = users.getCntcNo();
        this.location = users.getLocationText();
        this.latitude = users.getLocLatitude();
        this.longitude = users.getLocLongitude();
        this.userId = users.getUserId();
        log.debug("user Active Status = {} ", users.getActiveStatus());
        this.activeStatus = users.getActiveStatus();
        this.contactVerified = users.getIsContactVerified();
        this.isEmailVerified = users.getIsEmailVerified();
    }

    public PPRegisterVO(FacebookLoginDTO request) {
        this.firstName = request.getName();
        this.emailId = request.getEmail();
        this.isEmailVerified = true;
        this.contactNo = request.getContactNo();
        this.contactVerified = false;
        this.facebookHandlePassword = request.getId();
        this.tncAccepted = true;
    }

}
