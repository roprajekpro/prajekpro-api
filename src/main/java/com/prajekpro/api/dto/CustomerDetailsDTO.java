package com.prajekpro.api.dto;

import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CustomerDetailsDTO {
    private String userId;
    private String userName;
    private String emailId;
    private String contactNo;
    private Float latitude;
    private Float longitude;
    private String location;
    private Integer totalBookedAppointments;
    private boolean contactVerified;
    private boolean isEmailVerified = false;

    public CustomerDetailsDTO(Users customer, int bookedAppointments) {
        this.userId = customer.getUserId();
        this.userName = customer.getFullName();
        this.emailId = customer.getEmailId();
        this.contactNo = customer.getCntcNo();
        this.latitude = customer.getLocLatitude();
        this.longitude = customer.getLocLongitude();
        this.location = customer.getLocationText();
        this.contactVerified = customer.getIsContactVerified();
        this.isEmailVerified = customer.getIsEmailVerified();
        this.totalBookedAppointments = bookedAppointments;
    }
}
